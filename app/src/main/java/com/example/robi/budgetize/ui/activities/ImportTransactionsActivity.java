package com.example.robi.budgetize.ui.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.robi.budgetize.ApplicationObj;
import com.example.robi.budgetize.R;
import com.example.robi.budgetize.ui.thirdPartyLibraries.ExpandingItem;
import com.example.robi.budgetize.ui.thirdPartyLibraries.ExpandingList;
import com.example.robi.budgetize.backend.viewmodels.BankAccountViewModel;
import com.example.robi.budgetize.backend.viewmodels.factories.BankAccountViewModelFactory;
import com.example.robi.budgetize.data.localdatabase.entities.BankAccount;
import com.example.robi.budgetize.data.localdatabase.entities.LinkedBank;
import com.example.robi.budgetize.data.localdatabase.entities.Wallet;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ImportTransactionsActivity extends AppCompatActivity {
    private ExpandingList mExpandingList = null;
    private static BankAccountViewModel bankAccountViewModel;
    private static List<LinkedBank> linkedBanks = new ArrayList<>();
    private static HashMap<String, List<BankAccount>> bankAccounts = new HashMap<>();
    private static Wallet wallet;
    private static long walletID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_transactions);
        mExpandingList = findViewById(R.id.expanding_list_main_transactions);

        bankAccountViewModel = new ViewModelProvider(this,
                new BankAccountViewModelFactory((ApplicationObj) this.getApplication()))
                .get(BankAccountViewModel.class);

        Bundle bundle = getIntent().getExtras();
        if (bundle.get("wallet") != null) {
            Gson gson = new Gson();
            String walletAsString = (String) bundle.get("wallet");
            this.wallet = gson.fromJson(walletAsString, Wallet.class);
            walletID = wallet.getId();
        }


        populateLists();
    }

    private void populateLists() {
        //TODO: We have the issue that obp_bank_id is not correct. It is just for example, we need to save multiple banks like: inv.01, etc. for a linked account!
        //TODO: One OBP account may be linked to multiple banks! We need to take all those banks and get accounts from each and save with account reference
        //TODO: Those needs to be under the "imaginary" selected bank(which is just a reference for the linked obp account).
        //TODO: So the current bank is that we have no link between the account and the linked bank and we must have!ex(in db we have obp bank id: inv.01 at linkedbank and obp bank id: x-bank-ofx at bankaccount
        //Display Wallet Name:
//        ExpandingItem first_item = mExpandingList.createNewItem(R.layout.first_ie_item);
//        if (first_item != null) {
//            ((TextView) first_item.findViewById(R.id.wallet_name_ie_screen)).setText(wallet.getName());//TODO
//        }
        LiveData<List<LinkedBank>> linkedBanksList = bankAccountViewModel.getAllLinkedBanks();
        HashMap<String, LiveData<List<BankAccount>>> bankAccountsMap = new HashMap<>();

        linkedBanksList.observe(this, linkedBanks -> {
            ImportTransactionsActivity.linkedBanks.clear();
            //also stop observe and clear observers!
            for (LiveData<List<BankAccount>> bankAccount : bankAccountsMap.values()) {
                bankAccount.removeObservers(this);
            }
            bankAccountsMap.clear();
            depopulateLists();

            for (LinkedBank linkedBank : linkedBanks) {
                if (linkedBank.getLink_status().contentEquals("LINKED")) {
                    ImportTransactionsActivity.linkedBanks.add(linkedBank);
                    //For each linkedbank, we add an observer of bankAccounts on that linked bank.
                    LiveData<List<BankAccount>> bankAccountsList = bankAccountViewModel.getBankAccountsFromALinkedBank(linkedBank.getId());//wrong, must get bank accounts from linked banked with linkedbank.getID();
                    bankAccountsList.observe(ImportTransactionsActivity.this, new Observer<List<BankAccount>>() {
                        @Override
                        public void onChanged(List<BankAccount> bankAccounts) {

                            for (int i = 0; i < mExpandingList.getItemsCount(); i++) {
                                if (mExpandingList.getItemByIndex(i) != null) {
                                    ExpandingItem item = mExpandingList.getItemByIndex(i);
                                    if (item.itemIDHolder.contentEquals(linkedBank.getObp_bank_id())) {
                                        item.removeAllSubItems();
                                        item.createSubItems(bankAccounts.size());
                                        for (int j = 0; j < item.getSubItemsCount(); j++) {
                                            //Let's get the created sub item by its index
                                            final View view = item.getSubItemView(j);
                                            configureSubItem(item, view, bankAccounts.get(j));
                                        }
                                        ImportTransactionsActivity.bankAccounts.put(linkedBank.getObp_bank_id(), bankAccounts);
                                    }
                                }
//                if(i== item.getSubItemsCount()-1) {
//                    RelativeLayout layout = view.findViewById(R.id.layout_subitem);
//                    ViewGroup.LayoutParams params = layout.getLayoutParams();
//                    params.height = 300;
//                    view.setLayoutParams(params);
//                }
                                //Let's set some values in

                            }
                        }
                    });
                    //TODO: solve issues from here
                    //TODO: there seems to be no bankAccounts in db, I suppose somewhere those are deletedu
                    bankAccountsMap.put(linkedBank.getObp_bank_id(), bankAccountsList);
                    addItem(linkedBank);
                }
            }
        });
        //Add Categories and IEs
        for (LinkedBank linkedBank : linkedBanks) {
            addItem(linkedBank);
        }
    }

    private void depopulateLists() {
        mExpandingList.removeAllViews();
    }

    private void addItem(final LinkedBank linkedBank) {
        final ExpandingItem item = mExpandingList.createNewItem(R.layout.expanding_layout_transaction);
        if (item != null) {
            item.itemIDHolder = linkedBank.getObp_bank_id();
            item.setIndicatorColor(Color.TRANSPARENT);
            if (bankAccountViewModel.isSyncLinkedBank(walletID, linkedBank)) {
                item.setIndicatorIcon(AppCompatResources.getDrawable(this, R.drawable.transactions_synched));
            } else {
                item.setIndicatorIcon(AppCompatResources.getDrawable(this, R.drawable.synch_transactions));
            }
            item.setIndicatorSize(25, 25, this);
            item.setIndicatorOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (bankAccountViewModel.isSyncLinkedBank(walletID, linkedBank)) {
                        //TODO: implement here dialog asking user for desync
                    } else {
                        doItemSync(item, linkedBank);
                    }
                }
            });
            //1. ImageView category_icon
            ((TextView) item.findViewById(R.id.text_view_value_transaction)).setText("");
            ((TextView) item.findViewById(R.id.text_view_category_name_transaction)).setText(linkedBank.getFull_name());
            ((TextView) item.findViewById(R.id.text_view_description_transaction)).setText(linkedBank.getShort_name());
//            item.findViewById(R.id.unsync_bank).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    importItemClickListener(linkedBank, item, linkedBank);//TODO
//                }
//            });
        }
    }

    private void configureSubItem(final ExpandingItem item, final View view, final BankAccount bankAccount) {
        //subItemViewHolder.ieIcon.setText(ieObject.getSubItemTitle()); //to be implemented
        ((TextView) view.findViewById(R.id.text_view_ie_value_transaction)).setText(bankAccount.getAccount_type());
        ((TextView) view.findViewById(R.id.text_view_title_transaction)).setText(bankAccount.getLabel());

        if (bankAccountViewModel.isSyncBankAccount(walletID, bankAccount)) {
            ((ImageView) view.findViewById(R.id.remove_sub_item_transaction)).setImageDrawable(getDrawable(R.drawable.transactions_synched));
            ((ImageView) view.findViewById(R.id.remove_sub_item_transaction)).setTag("sync");
        } else {
            ((ImageView) view.findViewById(R.id.remove_sub_item_transaction)).setImageDrawable(getDrawable(R.drawable.synch_transactions));
            ((ImageView) view.findViewById(R.id.remove_sub_item_transaction)).setTag("desync");
        }

        //((TextView) view.findViewById(R.id.text_view_ie_description_white)).setText("TO BE IMPLEMENTED");
        view.findViewById(R.id.remove_sub_item_transaction).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                syncAccountTransactions(bankAccount, v);
                //importSubItemClickListener(item, view, bankAccount);
            }
        });
    }

    private void doItemSync(ExpandingItem item, LinkedBank linkedBank) {
        ObjectAnimator objectAnimator = item.startIndicatorAnimation();
        bankAccountViewModel.syncLinkedBankAccounts(walletID, linkedBank);
        updateUILinkedBankSynched(item, objectAnimator);
        updateUIAllSubItem(item, linkedBank);
    }

    public ObjectAnimator startIndicatorAnimation(View v) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(v, View.ROTATION, 0.0f, 360.0f);
        objectAnimator.setDuration(2000);
        objectAnimator.setRepeatCount(Animation.INFINITE);
        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                animation.end();
            }
        });
        objectAnimator.start();
        v.setOnClickListener((lambda) -> stopIndicatorAnimation(v, objectAnimator));
        return objectAnimator;
    }

    public void stopIndicatorAnimation(View v, ObjectAnimator objectAnimator) {
//        imageViewObjectAnimator.cancel();
        objectAnimator.cancel();
        v.clearAnimation();
//        v.setOnClickListener((lamba) -> startIndicatorAnimation(v));
    }

    private void syncAccountTransactions(BankAccount bankAccount, View view) {
        String status = (String) ((ImageView) view.findViewById(R.id.remove_sub_item_transaction)).getTag();
        if (status.contentEquals("sync")) {
            //TODO: implement here dialog asking user for desync
        } else {
            objectAnimator = startIndicatorAnimation(view);
            bankAccountViewModel.syncBankAccount(walletID, bankAccount);
            stopIndicatorAnimation(view, objectAnimator);//stop loader animation
            updateUISubItem(view, bankAccount);//set new icon
            updateParentItem(bankAccount, view);//set new icon for Parent if it's the case
        }
    }

    private void updateParentItem(BankAccount bankAccount, View view) {
        if (bankAccountViewModel.isSyncLinkedBank(walletID, bankAccount.getInternal_bank_id())) {
            int itemCount = mExpandingList.getItemsCount();
            for (int i = 0; i < itemCount; i++) {
                ExpandingItem item = mExpandingList.getItemByIndex(i);
                for (int j = 0; j < item.getSubItemsCount(); j++) {
                    if (item.getSubItemView(j).equals(view.getParent())) {
                        updateUILinkedBankSynched(item, objectAnimator);
                        break;
                    }
                }
            }
        }
        //updateAllSubItem(item, linkedBank);
    }

    private void updateUILinkedBankSynched(ExpandingItem item, ObjectAnimator objectAnimator) {
        objectAnimator.cancel();
//        item.removeIndicatorOnClickListeners(); //Don't remove because we want to be able to let user unsync bank account
        item.setIndicatorIcon(AppCompatResources.getDrawable(this, R.drawable.transactions_synched));
    }

    private void updateUISubItem(View view, BankAccount bankAccount) {
        if (bankAccountViewModel.isSyncBankAccount(walletID, bankAccount)) {
            ((ImageView) view.findViewById(R.id.remove_sub_item_transaction)).setImageDrawable(getDrawable(R.drawable.transactions_synched));
            ((ImageView) view.findViewById(R.id.remove_sub_item_transaction)).setTag("sync");
        } else {
            ((ImageView) view.findViewById(R.id.remove_sub_item_transaction)).setImageDrawable(getDrawable(R.drawable.synch_transactions));
            ((ImageView) view.findViewById(R.id.remove_sub_item_transaction)).setTag("desync");
        }
    }

    private void updateUIAllSubItem(ExpandingItem item, LinkedBank linkedBank) {
        int subItems = item.getSubItemsCount();
        for (int i = 0; i < subItems; i++) {
            View v = item.getSubItemView(i);
            ((ImageView) v.findViewById(R.id.remove_sub_item_transaction)).setImageDrawable(getDrawable(R.drawable.transactions_synched));
            ((ImageView) v.findViewById(R.id.remove_sub_item_transaction)).setTag("sync");
        }
    }

    private void importItemClickListener(Object object, final ExpandingItem expandingItem, final LinkedBank linkedBank) {
        final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE: {
                        try {
                            //TODO
//                            if (object instanceof IEObject) {
//                                mainActivityViewModel.deleteIE(((IEObject) object).getId());
//                                mExpandingList.removeView(expandingItem);
//                            } else {
//                                mainActivityViewModel.deleteCategory(((CategoryObject) object).getCategory_id());
//                                mExpandingList.removeView(expandingItem);
//                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.out.println(e.getMessage());
                            Toast toast = Toast.makeText(ImportTransactionsActivity.this, "Unable to import transactions", Toast.LENGTH_SHORT);
                            TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                            v.setTextColor(Color.RED);
                            toast.show();
                        }


                        //Yes button clicked
                        break;
                    }

                    case DialogInterface.BUTTON_NEGATIVE: {
                        //No button clicked
                        break;
                    }
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want do DELETE permanently this Category?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    private void importSubItemClickListener(final ExpandingItem expandingItem, final View view, final BankAccount bankAccount) {
        final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE: {
                        try {
                            //TODO
                            Toast.makeText(ImportTransactionsActivity.this, "I/E Deleted", Toast.LENGTH_SHORT).show();
                            expandingItem.removeSubItem(view);
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.out.println(e.getMessage());
                            Toast toast = Toast.makeText(ImportTransactionsActivity.this, "Unable to import transactions", Toast.LENGTH_SHORT);
                            TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                            v.setTextColor(Color.RED);
                            toast.show();
                        }
                        //Yes button clicked
                        break;
                    }

                    case DialogInterface.BUTTON_NEGATIVE: {
                        //No button clicked
                        break;
                    }
                }
            }
            /*
            private void refresh_category(ExpandingItem expandingItem, View view, CategoryObject categoryObject) {
                double ieValue = MainActivity.myDatabase.categoryDao().getCategoryIESUM(wallet.getId(), categoryObject.getName());
                expandingItem.setIndicatorIcon(getIcon(ieValue));

                if (ieValue > 0) {
                    expandingItem.setBackgroundResource(R.drawable.item_shape_positive);
                } else if (ieValue < 0) {
                    expandingItem.setBackgroundResource(R.drawable.item_shape_negative);
                } else {
                    expandingItem.setBackgroundResource(R.drawable.item_shape_neutral);
                }
                TextView value = (TextView) expandingItem.findViewById(R.id.text_view_value_white);
                value.setText(String.valueOf(ieValue));
            }

             */
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want do DELETE permanently this I/E?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    ObjectAnimator objectAnimator;

    private Drawable getIcon(double ie) {
        //have to return icon id
        Drawable unwrappedDrawable = AppCompatResources.getDrawable(this, R.drawable.house_icon);
        Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
        if (ie > 0) {
            DrawableCompat.setTint(wrappedDrawable, getResources().getColor(R.color.positiveBackgroundColor));
        } else if (ie < 0) {
            DrawableCompat.setTint(wrappedDrawable, getResources().getColor(R.color.negativeBackgroundColor));
        } else {
            DrawableCompat.setTint(wrappedDrawable, getResources().getColor(R.color.neutralBackgroundColor));
        }
        return wrappedDrawable;
    }
}
