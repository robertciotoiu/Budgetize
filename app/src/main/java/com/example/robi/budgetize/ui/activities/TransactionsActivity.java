package com.example.robi.budgetize.ui.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.robi.budgetize.ApplicationObj;
import com.example.robi.budgetize.R;
import com.example.robi.budgetize.ui.activities.createActivities.CreateTransactionActivity;
import com.example.robi.budgetize.ui.modifiedthirdpartylibraries.diegodobelo.androidexpandingviewlibrary.ExpandingItem;
import com.example.robi.budgetize.ui.modifiedthirdpartylibraries.diegodobelo.androidexpandingviewlibrary.ExpandingList;
import com.example.robi.budgetize.backend.viewmodels.MainActivityViewModel;
import com.example.robi.budgetize.backend.viewmodels.factories.MainActivityViewModelFactory;
import com.example.robi.budgetize.data.localdatabase.entities.CategoryObject;
import com.example.robi.budgetize.data.localdatabase.entities.IEObject;
import com.example.robi.budgetize.data.localdatabase.entities.Wallet;
import com.example.robi.budgetize.ui.activities.createActivities.CreateCategoryActivity;
import com.google.gson.Gson;
import com.mynameismidori.currencypicker.CurrencyPicker;
import com.mynameismidori.currencypicker.CurrencyPickerListener;
import com.mynameismidori.currencypicker.ExtendedCurrency;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionButton;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionHelper;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionLayout;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RFACLabelItem;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RapidFloatingActionContentLabelList;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

//TODO: display at the bottom all the IEs without a category
public class TransactionsActivity extends AppCompatActivity implements RapidFloatingActionContentLabelList.OnRapidFloatingActionContentLabelListListener {
    private ExpandingList mExpandingList = null;
    long walletID = 0;
    boolean firstStart = true;
    boolean firstStartOrphane = true;
    private Wallet wallet;
    private RapidFloatingActionLayout rfaLayout;
    private RapidFloatingActionButton rfaBtn;
    private RapidFloatingActionHelper rfabHelper;
    private ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);

    private MainActivityViewModel mainActivityViewModel;

    private static List<CategoryObject> categoryObjectsList = new ArrayList<CategoryObject>();
    private Observer<List<CategoryObject>> categoryListObsever;

    private static List<IEObject> orphanIEs = new ArrayList<IEObject>();
    private Observer<List<IEObject>> orphanIEsObserver;

    private static List<IEObject> ieObjects = new ArrayList<IEObject>();
//    private Observer<List<IEObject>> ieListObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iediegodobelo);
        mExpandingList = findViewById(R.id.expanding_list_main);
        Bundle bundle = getIntent().getExtras();
        if (bundle.get("wallet") != null) {
            Gson gson = new Gson();
            String walletAsString = (String) bundle.get("wallet");
            this.wallet = gson.fromJson(walletAsString, Wallet.class);
            walletID = wallet.getId();
        }
        //TODO: solve issue when enter in wallet there is no category/ie!
    }

    @Override
    public void onResume() {
        super.onResume();
        mainActivityViewModel = new ViewModelProvider(this
                , new MainActivityViewModelFactory((ApplicationObj) this.getApplication()))
                .get(MainActivityViewModel.class);
        init();
    }

    private void init() {
        init_floatingPointButton();
        //Observers
        categoryListObsever = new Observer<List<CategoryObject>>() {
            @Override
            public void onChanged(List<CategoryObject> categoryObjects) {
                TransactionsActivity.categoryObjectsList.clear();
                TransactionsActivity.categoryObjectsList.addAll(categoryObjects);
                if (firstStart) {
                    populateLists();
                    firstStart = false;
                }
            }
        };//categoryObjects::addAll;
        mainActivityViewModel.getAllCategoriesOfAWallet(walletID).observe(this, categoryListObsever);

//        ieListObserver = new Observer<List<IEObject>>() {
//            @Override
//            public void onChanged(List<IEObject> ieObjects) {
//                TransactionsActivity.ieObjects.clear();
//                TransactionsActivity.ieObjects.addAll(ieObjects);
//            }
//        };
//        mainActivityViewModel.getAllIE().observe(this,ieListObserver);

        orphanIEsObserver = new Observer<List<IEObject>>() {
            @Override
            public void onChanged(List<IEObject> orphanIEs) {
                TransactionsActivity.orphanIEs.clear();
                TransactionsActivity.orphanIEs.addAll(orphanIEs);
                if (firstStartOrphane) {
                    addOrphaneCategories();
                    firstStartOrphane = false;
                }
                //addOrphaneCategories();
            }
        };
        mainActivityViewModel.getAllIEofAWalletWithoutCategoriesAssigned(walletID).observe(TransactionsActivity.this, orphanIEsObserver);
    }

    @Override
    public void onPause() {
        super.onPause();
        firstStart = true;
        firstStartOrphane = true;
        depopulateLists();
        mainActivityViewModel.getAllIEofAWalletWithoutCategoriesAssigned(walletID).removeObserver(orphanIEsObserver);
        mainActivityViewModel.getAllCategories().removeObserver(categoryListObsever);
    }

    private void init_floatingPointButton() {
        RapidFloatingActionContentLabelList rfaContent = new RapidFloatingActionContentLabelList(this);
        rfaContent.setOnRapidFloatingActionContentLabelListListener(this);
        List<RFACLabelItem> items = new ArrayList<>();

        rfaLayout = this.findViewById(R.id.activity_main_rfal);
        rfaBtn = this.findViewById(R.id.activity_main_rfab);

        items.add(new RFACLabelItem<Integer>()
                .setLabel("Create new:")
                // .setResId(R.mipmap.ico_test_d)
                .setIconNormalColor(0xffd84315)
                .setIconPressedColor(0xffbf360c)
                .setWrapper(0)
        );
        items.add(new RFACLabelItem<Integer>()
                .setLabel("Category")
                .setResId(R.drawable.category_icon_test)
                .setIconNormalColor(0xff056f00)
                .setIconPressedColor(0xff0d5302)
                .setLabelColor(0xff056f00)
                .setWrapper(1)
        );
        items.add(new RFACLabelItem<Integer>()
                .setLabel("I/E")
                .setResId(R.drawable.income_expense_test_icon)
                .setIconNormalColor(0xff056f00)
                .setIconPressedColor(0xff0d5302)
                .setLabelColor(0xff056f00)
                .setWrapper(2)
        );
        items.add(new RFACLabelItem<Integer>()
                .setLabel("Import transactions")
                .setResId(R.drawable.import_transactions)
                //.setIconNormalColor(0xff056f00)
                .setIconNormalColor(0xffcccccc)
                .setIconPressedColor(0xff0d5302)
                .setLabelColor(0xff056f00)
                //.setLabelColor(0x000000)
                .setWrapper(2)
        );
        rfaContent
                .setItems(items)
                //.setIconShadowRadius(ABTextUtil.dip2px(this, 5))
                .setIconShadowColor(0xff888888)
        //.setIconShadowDy(ABTextUtil.dip2px(this, 5))
        ;
        rfabHelper = new RapidFloatingActionHelper(
                this,
                rfaLayout,
                rfaBtn,
                rfaContent
        ).build();
    }

    @Override
    public void onRFACItemLabelClick(int position, RFACLabelItem item) {
        //Toast.makeText(this, "clicked label: " + position, Toast.LENGTH_SHORT).show();
        rfabHelper.toggleContent();
    }

    @Override
    public void onRFACItemIconClick(int position, RFACLabelItem item) {
        Gson gson = new Gson();
        String walletAsString = gson.toJson(wallet);
        //Toast.makeText(this, "clicked icon: " + position, Toast.LENGTH_SHORT).show();
        if (position == 3) {
            Intent myIntent = new Intent(TransactionsActivity.this, ImportTransactionsActivity.class);
            myIntent.putExtra("wallet", walletAsString); //Optional parameters
            TransactionsActivity.this.startActivity(myIntent);
        } else if (position == 2) {
            //launch create IE activity
            Intent myIntent = new Intent(TransactionsActivity.this, CreateTransactionActivity.class);
            myIntent.putExtra("wallet", walletAsString); //Optional parameters
            TransactionsActivity.this.startActivity(myIntent);
        } else if (position == 1) {
            //launch create Category activity
            Intent myIntent = new Intent(TransactionsActivity.this, CreateCategoryActivity.class);
            myIntent.putExtra("wallet", walletAsString); //Optional parameters
            TransactionsActivity.this.startActivity(myIntent);
        }
        rfabHelper.toggleContent();
    }

//    private LiveData<List<CategoryObject>> buildCategoryList() {
//        return mainActivityViewModel.getAllCategoriesOfAWallet(wallet.getId());
//    }

    private void populateLists() {
        //Display Wallet Name:
        ExpandingItem first_item = mExpandingList.createNewItem(R.layout.first_ie_item);
        if (first_item != null) {
            //1. Wallet name
            ((TextView) first_item.findViewById(R.id.wallet_name_ie_screen)).setText(wallet.getName());

            String currency = wallet.getCurrency();
            CurrencyPicker picker = CurrencyPicker.newInstance("Select Currency");
            ExtendedCurrency currencyCode = ExtendedCurrency.getCurrencyByISO(currency);
            ((TextView) first_item.findViewById(R.id.currency_textview)).setText(currencyCode.getCode());
            ((ImageView) first_item.findViewById(R.id.currency_imageview)).setImageDrawable(getDrawable(currencyCode.getFlag()));


            ((TextView) first_item.findViewById(R.id.currency_textview)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CurrencyPicker picker = CurrencyPicker.newInstance("Select Currency");  // dialog title
                    picker.setListener(new CurrencyPickerListener() {
                        @Override
                        public void onSelectCurrency(String name, String code, String symbol, int flagDrawableResID) {
                            // Implement your code here
                            doSelectCurrencyLogic(first_item, code, flagDrawableResID, picker);
                        }
                    });
                    picker.show(getSupportFragmentManager(), "CURRENCY_PICKER");
                }
            });

            ((ImageView) first_item.findViewById(R.id.currency_imageview)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CurrencyPicker picker = CurrencyPicker.newInstance("Select Currency");  // dialog title
                    picker.setListener(new CurrencyPickerListener() {
                        @Override
                        public void onSelectCurrency(String name, String code, String symbol, int flagDrawableResID) {
                            // Implement your code here
                            doSelectCurrencyLogic(first_item, code, flagDrawableResID, picker);
                        }
                    });
                    picker.show(getSupportFragmentManager(), "CURRENCY_PICKER");
                }
            });
        }

        //Add Categories and IEs
        for (CategoryObject categoryObject : categoryObjectsList) {
            addItem(categoryObject);
        }
    }

    private void doSelectCurrencyLogic(ExpandingItem first_item, String code, int flagDrawableResID, CurrencyPicker picker) {
        long status = mainActivityViewModel.updateCurrency(walletID, code);

        if(status>0) {
            ((TextView) first_item.findViewById(R.id.currency_textview)).setText(code);
            ((ImageView) first_item.findViewById(R.id.currency_imageview)).setImageDrawable(getDrawable(flagDrawableResID));
            picker.dismiss();
        }else{
            Toast toast = Toast.makeText(TransactionsActivity.this, "Cannot change currency! Contact support team!", Toast.LENGTH_LONG);
            TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
            v.setTextColor(Color.RED);
            toast.show();
        }
    }

    private void addOrphaneCategories() {
        Log.d("WTF:", mExpandingList.getItemsCount() + "-ITEM COUNT");
        for (IEObject orphaneIEObject : orphanIEs) {
            addOrphanItems(orphaneIEObject);
        }
    }

    private void addOrphanItems(IEObject orphaneIEObject) {
        //double ie_value = mainActivityViewModel.getCategoryIESUM(orphaneIEObject.getWallet_id(), orphaneIEObject.getName());
        //List<IEObject> ieObjectList = mainActivityViewModel.getCategorysIE(walletID, orphaneIEObject.getName());

        TransactionsActivity.this.runOnUiThread(() -> {
            final ExpandingItem item;
            if (orphaneIEObject.type == 0) {
                item = getCorrectItem(orphaneIEObject.amount);
            } else {
                item = getCorrectItem(-orphaneIEObject.amount);
            }
            if (item != null) {
                //setAllColors(ie_value);
                //item.setIndicatorColor(Color.WHITE);
                //item.setIndicatorColorRes(getIconIndicatorColor(ie_value));//R.color.positiveBackgroundColor);
                //item.setIndicatorIcon(getIcon(orphaneIEObject.amount));//R.drawable.house_icon);
                //1. ImageView category_icon
                ((TextView) item.findViewById(R.id.text_view_value_white)).setText(String.valueOf(orphaneIEObject.amount));
                ((TextView) item.findViewById(R.id.text_view_category_name_white)).setText(orphaneIEObject.getName());
                ((TextView) item.findViewById(R.id.text_view_description_white)).setText("Empty");
                item.findViewById(R.id.remove_item).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        removeItemClickListener(orphaneIEObject, item);
                    }
                });
            }
            Log.d("UI thread", "I am the UI thread");
        });
    }

    private void depopulateLists() {
        mExpandingList.removeAllViews();
    }

    private void addItem(final CategoryObject categoryObject) {
        double ie_value = mainActivityViewModel.getCategoryIESUM(categoryObject.getCategory_id());
        List<IEObject> ieObjectList = mainActivityViewModel.getCategorysIE(categoryObject.getCategory_id());

        TransactionsActivity.this.runOnUiThread(() -> {
            final ExpandingItem item = getCorrectItem(ie_value);
            if (item != null) {
                //setAllColors(ie_value);
                item.setIndicatorColor(Color.WHITE);
                item.setIndicatorSize(35, 35, this);
                //item.setIndicatorColorRes(getIconIndicatorColor(ie_value));//R.color.positiveBackgroundColor);
                item.setIndicatorIcon(getIcon(ie_value));//R.drawable.house_icon);
                //1. ImageView category_icon
                TextView valueTextView = (TextView) item.findViewById(R.id.text_view_value_white);
                valueTextView.setText(String.valueOf(ie_value));
                valueTextView.setTooltipText(String.valueOf(ie_value));

                TextView nameTextView = ((TextView) item.findViewById(R.id.text_view_category_name_white));
                nameTextView.setText(categoryObject.getName());
                nameTextView.setTooltipText(String.valueOf(categoryObject.getName()));

                TextView descriptionTextView = ((TextView) item.findViewById(R.id.text_view_description_white));
                descriptionTextView.setText(categoryObject.getDescription());
                descriptionTextView.setTooltipText(String.valueOf(categoryObject.getDescription()));

                item.createSubItems(ieObjectList.size());
                for (int i = 0; i < item.getSubItemsCount(); i++) {
                    final View view = item.getSubItemView(i);
                    configureSubItem(item, view, ieObjectList.get(i), categoryObject);
                }

                if (categoryObject.getBank_account_id() != null) {
                    //IF BANK ACCOUNT, PERSONALIZE

                    //HIDE delete button and reset position of value
                    final RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(dpToPixels(100), dpToPixels(30));
                    params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, R.id.text_view_category_name_white);
                    params.addRule(RelativeLayout.ALIGN_PARENT_END);
                    params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                    params.addRule(RelativeLayout.TEXT_ALIGNMENT_TEXT_END);
                    params.topMargin = dpToPixels(22);
                    params.leftMargin = dpToPixels(5);
                    params.rightMargin = dpToPixels(5);
                    params.width = dpToPixels(110);
                    valueTextView.setLayoutParams(params);
                    item.findViewById(R.id.remove_item).setVisibility(View.INVISIBLE);

                    item.setIndicatorIcon(personalizeIcon(R.drawable.bankaccounts, R.color.gold));
                    item.setBackground(getDrawable(R.drawable.item_shape_gold));
                } else {
                    item.findViewById(R.id.remove_item).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            removeItemClickListener(categoryObject, item);
                        }
                    });
                }
            }
            Log.d("UI thread", "I am the UI thread");
        });
    }

    public int dpToPixels(int dp) {
        return (int) (dp * (getResources().getDisplayMetrics().density));
    }

    private void removeItemClickListener(Object object, final ExpandingItem expandingItem) {
        final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE: {
                        try {
                            if (object instanceof IEObject) {
                                mainActivityViewModel.deleteIE(((IEObject) object).getId());
                                mExpandingList.removeView(expandingItem);
                            } else {
                                mainActivityViewModel.deleteCategory(((CategoryObject) object).getCategory_id());
                                mExpandingList.removeView(expandingItem);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.out.println(e.getMessage());
                            Toast toast = Toast.makeText(TransactionsActivity.this, "Unable to delete the Category", Toast.LENGTH_SHORT);
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

    //TODO: refactor and change layout programatically!
    private ExpandingItem getCorrectItem(double ie_value) {
        if (ie_value > 0) {
            return mExpandingList.createNewItem(R.layout.expanding_layout_positive);
        } else if (ie_value < 0) {
            return mExpandingList.createNewItem(R.layout.expanding_layout_negative);
        } else {
            return mExpandingList.createNewItem(R.layout.expanding_layout_neutral);
        }

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

    private Drawable personalizeIcon(int iconID, int color) {
        Drawable unwrappedDrawable = AppCompatResources.getDrawable(this, iconID).mutate();
        Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
        DrawableCompat.setTint(wrappedDrawable, getResources().getColor(color));

        return unwrappedDrawable;
    }

    private void configureSubItem(final ExpandingItem item, final View view, final IEObject ieObject, final CategoryObject categoryObject) {
        //subItemViewHolder.ieIcon.setText(ieObject.getSubItemTitle()); //to be implemented
        TextView valueTextView = ((TextView) view.findViewById(R.id.text_view_ie_value_white));
        if (ieObject.type == 1) {
            if (ieObject.amount >= 0) {
                valueTextView.setText("-" + String.valueOf(ieObject.amount));
                valueTextView.setTooltipText(String.valueOf("-"+ieObject.amount));
            } else {
                valueTextView.setText(String.valueOf(ieObject.amount));
                valueTextView.setTooltipText(String.valueOf(ieObject.amount));
            }
        } else {
            valueTextView.setText(String.valueOf(ieObject.amount));
            valueTextView.setTooltipText(String.valueOf(ieObject.amount));
        }
        TextView ieNameTextView = ((TextView) view.findViewById(R.id.text_view_title_white));
        ieNameTextView.setText(String.valueOf(ieObject.name));
        ieNameTextView.setTooltipText(String.valueOf(ieObject.name));
        //((TextView) view.findViewById(R.id.text_view_ie_description_white)).setText("TO BE IMPLEMENTED");
        if (ieObject.txn_id != null) {
            view.findViewById(R.id.remove_sub_item_white).setVisibility(View.INVISIBLE);

            final RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(dpToPixels(100), dpToPixels(20));
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, R.id.text_view_title_white);
            params.addRule(RelativeLayout.ALIGN_PARENT_END);
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            params.addRule(RelativeLayout.TEXT_ALIGNMENT_TEXT_END);
            params.leftMargin = dpToPixels(1);
            params.rightMargin = dpToPixels(10);
            valueTextView.setLayoutParams(params);
        } else {
            view.findViewById(R.id.remove_sub_item_white).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeSubItemClickListener(ieObject.getId(), item, view, categoryObject);
                }
            });
        }
    }

    private void removeSubItemClickListener(final long ieID, final ExpandingItem expandingItem, final View view, final CategoryObject categoryObject) {
        final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE: {
                        try {
                            //TODO:issue here, when we delete one IE, we must removeSubItem() and recalculate category
                            mainActivityViewModel.deleteIE(ieID);
                            Toast.makeText(TransactionsActivity.this, "I/E Deleted", Toast.LENGTH_SHORT).show();
                            expandingItem.removeSubItem(view);
                            recalculateCategorySum(expandingItem, categoryObject);
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.out.println(e.getMessage());
                            Toast toast = Toast.makeText(TransactionsActivity.this, "Unable to delete the I/E", Toast.LENGTH_SHORT);
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
        builder.setMessage("Are you sure you want do DELETE permanently this I/E?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    private void recalculateCategorySum(final ExpandingItem expandingItem, final CategoryObject categoryObject) {
        double ie_value = mainActivityViewModel.getCategoryIESUM(categoryObject.getCategory_id());
        ((TextView) expandingItem.findViewById(R.id.text_view_value_white)).setText(String.valueOf(ie_value));
        if (ie_value > 0) {
            expandingItem.setBackground(getResources().getDrawable(R.drawable.item_shape_positive));
        } else if (ie_value < 0) {
            expandingItem.setBackground(getResources().getDrawable(R.drawable.item_shape_negative));
        } else {
            expandingItem.setBackground(getResources().getDrawable(R.drawable.item_shape_neutral));
        }
        expandingItem.setIndicatorIcon(getIcon(ie_value));
    }

    private void setAllColors(double ie) {
//        RelativeLayout lLayout = (RelativeLayout) findViewById(R.id.expanding_item_id);
        TextView value = (TextView) findViewById(R.id.text_view_value);
        TextView description = (TextView) findViewById(R.id.text_view_category_name);
        TextView name = (TextView) findViewById(R.id.text_view_category_name);

        if (ie > 0) {
//            lLayout.setBackgroundColor(getResources().getColor(R.color.positiveBackgroundColor));
            value.setTextColor(getResources().getColor(R.color.positiveBackgroundColor));
            description.setTextColor(getResources().getColor(R.color.positiveBackgroundColor));
            name.setTextColor(getResources().getColor(R.color.positiveBackgroundColor));
        } else if (ie < 0) {
//            lLayout.setBackgroundColor(getResources().getColor(R.color.negativeBackgroundColor));
            value.setTextColor(getResources().getColor(R.color.negativeBackgroundColor));
            description.setTextColor(getResources().getColor(R.color.negativeBackgroundColor));
            name.setTextColor(getResources().getColor(R.color.negativeBackgroundColor));
        } else {
//            lLayout.setBackgroundColor(getResources().getColor(R.color.neutralBackgroundColor));
            value.setTextColor(getResources().getColor(R.color.neutralBackgroundColor));
            description.setTextColor(getResources().getColor(R.color.neutralBackgroundColor));
            name.setTextColor(getResources().getColor(R.color.neutralBackgroundColor));
        }
    }

    private int getIconIndicatorColor(double ie) {
        //have to return icon's indicator color
        if (ie > 0) {
            return R.color.positiveBackgroundColor;
        } else if (ie < 0) {
            return R.color.negativeBackgroundColor;
        } else {
            return R.color.neutralBackgroundColor;
        }
    }
}
