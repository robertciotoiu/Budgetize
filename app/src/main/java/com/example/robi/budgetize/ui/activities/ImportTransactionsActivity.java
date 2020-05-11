package com.example.robi.budgetize.ui.activities;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.robi.budgetize.ApplicationObj;
import com.example.robi.budgetize.R;
import com.example.robi.budgetize.backend.APIs.expandingviewAPI.ExpandingItem;
import com.example.robi.budgetize.backend.APIs.expandingviewAPI.ExpandingList;
import com.example.robi.budgetize.backend.viewmodels.BankAccountViewModel;
import com.example.robi.budgetize.backend.viewmodels.factories.BankAccountViewModelFactory;
import com.example.robi.budgetize.data.localdatabase.entities.AccountTransaction;
import com.example.robi.budgetize.data.localdatabase.entities.BankAccount;
import com.example.robi.budgetize.data.localdatabase.entities.LinkedBank;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ImportTransactionsActivity extends AppCompatActivity {
    private ExpandingList mExpandingList = null;
    private static BankAccountViewModel bankAccountViewModel;
    private static List<LinkedBank> linkedBanks = new ArrayList<>();
    private static HashMap<String, List<BankAccount>> bankAccounts = new HashMap<>();
    private List<AccountTransaction> accountTransactions = new ArrayList<>();

    MediatorLiveData liveDataMerger = new MediatorLiveData<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_transactions);
        mExpandingList = findViewById(R.id.expanding_list_main_transactions);

        bankAccountViewModel = new ViewModelProvider(this,
                new BankAccountViewModelFactory((ApplicationObj) this.getApplication()))
                .get(BankAccountViewModel.class);

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
        HashMap<String,LiveData<List<BankAccount>>> bankAccountsMap = new HashMap<>();
        linkedBanksList.observe(this, linkedBanks -> {
            ImportTransactionsActivity.linkedBanks.clear();
            //also stop observe and clear observers!
            for(LiveData<List<BankAccount>> bankAccount:bankAccountsMap.values()){
                bankAccount.removeObservers(this);
            }
            bankAccountsMap.clear();

            for(LinkedBank linkedBank:linkedBanks){
                if(linkedBank.getLink_status().contentEquals("LINKED")){
                    ImportTransactionsActivity.linkedBanks.add(linkedBank);
                    //For each linkedbank, we add an observer of bankAccounts on that linked bank.
                    LiveData<List<BankAccount>> bankAccountsList = bankAccountViewModel.getBankAccountsFromALinkedBank(linkedBank.getObp_bank_id());
                    bankAccountsList.observe(ImportTransactionsActivity.this, new Observer<List<BankAccount>>() {
                        @Override
                        public void onChanged(List<BankAccount> bankAccounts) {
                            ImportTransactionsActivity.bankAccounts.put(linkedBank.getObp_bank_id(),bankAccounts);
                        }
                    });
                    //TODO: solve issues from here
                    //TODO: there seems to be no bankAccounts in db, I suppose somewhere those are deletedu
                    bankAccountsMap.put(linkedBank.getObp_bank_id(),bankAccountsList);

                    ImportTransactionsActivity.bankAccounts.put(linkedBank.getObp_bank_id(),bankAccountViewModel.getBankAccountsFromALinkedBankNOTLIVEDATA(linkedBank.getObp_bank_id()));

//                    List<BankAccount> bankAccountList = new ArrayList<>();
//                    bankAccountList.addAll(bankAccountViewModel.getBankAccountsFromALinkedBank(linkedBank.getObp_bank_id()).getValue());
//                    bankAccounts.put(linkedBank.getObp_bank_id(),bankAccountList);
                    addItem(linkedBank);
                }
            }
        });

//        LiveData<List<BankAccount>> bankAccountsList = bankAccountViewModel.getAllBankAccounts();
//        bankAccountsList.observe(this,bankAccounts -> {
//            this.bankAccounts.clear();
//            for(BankAccount bankAccount:bankAccounts){
//                List<BankAccount> newBankAccountList;
//                if((newBankAccountList = this.bankAccounts.get(bankAccount.getBank_id()))==null){
//                    newBankAccountList = new ArrayList<>();
//                    newBankAccountList.add(bankAccount);
//                    this.bankAccounts.put(bankAccount.getBank_id(),newBankAccountList);
//                }else{
//                    newBankAccountList.add(bankAccount);
//                    this.bankAccounts.replace(bankAccount.getBank_id(),newBankAccountList);
//                }
//            }
//            for (LinkedBank linkedBank : this.linkedBanks) {
//                addItem(linkedBank);
//            }
//        });

//        liveDataMerger.addSource(linkedBanksList, value -> liveDataMerger.setValue(value));
//        liveDataMerger.addSource(bankAccountsList, value -> liveDataMerger.setValue(value));

        //Add Categories and IEs

            for (LinkedBank linkedBank : linkedBanks) {
                addItem(linkedBank);
            }
    }

    private void depopulateLists() {
        mExpandingList.removeAllViews();
    }

    private void addItem(final LinkedBank linkedBank) {
        //double ie_value = mainActivityViewModel.getCategoryIESUM(categoryObject.getCategory_id());
        //List<IEObject> ieObjectList = mainActivityViewModel.getCategorysIE(categoryObject.getCategory_id());
        //mExpandingList.createNewItem(R.layout.expanding_layout_positive);
        final ExpandingItem item = mExpandingList.createNewItem(R.layout.expanding_layout_transaction);
        //IEActivityDiegodobelo.this.runOnUiThread(() -> {
            //final ExpandingItem item = getCorrectItem(ie_value);
            if (item != null) {
                //setAllColors(ie_value);
                //item.setIndicatorColor(Color.WHITE);
                item.setIndicatorColor(Color.TRANSPARENT);
                //item.setIndicatorColorRes(getIconIndicatorColor(ie_value));//R.color.positiveBackgroundColor);
                item.setIndicatorIcon(AppCompatResources.getDrawable(this,R.drawable.linkedbank_status));//R.drawable.house_icon);
                //1. ImageView category_icon
                ((TextView) item.findViewById(R.id.text_view_value_transaction)).setText("");
                ((TextView) item.findViewById(R.id.text_view_category_name_transaction)).setText(linkedBank.getFull_name());
                ((TextView) item.findViewById(R.id.text_view_description_transaction)).setText(linkedBank.getShort_name());

//                bankAccountViewModel.getBankAccountsFromALinkedBank(linkedBank.getObp_bank_id());
                List<BankAccount> bankAccountList = bankAccounts.get(linkedBank.getObp_bank_id());
                item.createSubItems(bankAccountList.size());
                for (int i = 0; i < item.getSubItemsCount(); i++) {
                    //Let's get the created sub item by its index
                    final View view = item.getSubItemView(i);
//                if(i== item.getSubItemsCount()-1) {
//                    RelativeLayout layout = view.findViewById(R.id.layout_subitem);
//                    ViewGroup.LayoutParams params = layout.getLayoutParams();
//                    params.height = 300;
//                    view.setLayoutParams(params);
//                }
                    //Let's set some values in
                    configureSubItem(item, view, bankAccountList.get(i));
                }
//            item.findViewById(R.id.add_more_sub_items).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    //launch create Category activity
//                    Intent myIntent = new Intent(IEActivityDiegodobelo.this, CreateIEActivity.class);
//                    myIntent.putExtra("wallet", walletID); //Optional parameters
//                    IEActivityDiegodobelo.this.startActivity(myIntent);
//                }
//            });
                item.findViewById(R.id.remove_item).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        importItemClickListener(linkedBank, item,linkedBank);//TODO
                    }
                });
            }
//            Log.d("UI thread", "I am the UI thread");
//        });
    }

    private void configureSubItem(final ExpandingItem item, final View view, final BankAccount bankAccount) {
        //subItemViewHolder.ieIcon.setText(ieObject.getSubItemTitle()); //to be implemented
        ((TextView) view.findViewById(R.id.text_view_ie_value_transaction)).setText(bankAccount.getAccount_type());
        ((TextView) view.findViewById(R.id.text_view_title_transaction)).setText(bankAccount.getLabel());
        //((TextView) view.findViewById(R.id.text_view_ie_description_white)).setText("TO BE IMPLEMENTED");
        view.findViewById(R.id.remove_sub_item_transaction).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                importSubItemClickListener(item, view, bankAccount);
            }
        });
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
//                            mainActivityViewModel.deleteIE(ieID);
                            Toast.makeText(ImportTransactionsActivity.this, "I/E Deleted", Toast.LENGTH_SHORT).show();
                            expandingItem.removeSubItem(view);
//                            recalculateCategorySum(expandingItem, categoryObject);
//                            mExpandingList.removeAllViews();
//                            populateLists();

                            //mExpandingList.replaceItem(expandingItem);
                            //refresh_category(expandingItem, view, categoryObject);
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
