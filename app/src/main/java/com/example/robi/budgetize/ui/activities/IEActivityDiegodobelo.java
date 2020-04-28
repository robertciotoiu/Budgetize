package com.example.robi.budgetize.ui.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.robi.budgetize.ApplicationObj;
import com.example.robi.budgetize.R;
import com.example.robi.budgetize.backend.APIs.expandingviewAPI.ExpandingItem;
import com.example.robi.budgetize.backend.APIs.expandingviewAPI.ExpandingList;
import com.example.robi.budgetize.backend.viewmodels.MainActivityViewModel;
import com.example.robi.budgetize.backend.viewmodels.MainActivityViewModelFactory;
import com.example.robi.budgetize.data.localdatabase.entities.CategoryObject;
import com.example.robi.budgetize.data.localdatabase.entities.IEObject;
import com.example.robi.budgetize.data.localdatabase.entities.Wallet;
import com.example.robi.budgetize.ui.activities.createActivities.CreateCategoryActivity;
import com.example.robi.budgetize.ui.activities.createActivities.CreateIEActivity;
import com.google.gson.Gson;
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
public class IEActivityDiegodobelo extends AppCompatActivity implements RapidFloatingActionContentLabelList.OnRapidFloatingActionContentLabelListListener {
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
        if(bundle.get("wallet")!=null){
            Gson gson = new Gson();
            String walletAsString = (String) bundle.get("wallet");
            this.wallet = gson.fromJson(walletAsString, Wallet.class);
            walletID = wallet.getId();
        }
        //TODO: solve issue when enter in wallet there is no category/ie!
    }

    @Override
    public void onResume(){
        super.onResume();
        mainActivityViewModel =  new ViewModelProvider(this
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
                IEActivityDiegodobelo.categoryObjectsList.clear();
                IEActivityDiegodobelo.categoryObjectsList.addAll(categoryObjects);
                if(firstStart){
                    populateLists();
                    firstStart = false;
                }
            }
        };//categoryObjects::addAll;
        mainActivityViewModel.getAllCategoriesOfAWallet(walletID).observe(this, categoryListObsever);

//        ieListObserver = new Observer<List<IEObject>>() {
//            @Override
//            public void onChanged(List<IEObject> ieObjects) {
//                IEActivityDiegodobelo.ieObjects.clear();
//                IEActivityDiegodobelo.ieObjects.addAll(ieObjects);
//            }
//        };
//        mainActivityViewModel.getAllIE().observe(this,ieListObserver);

        orphanIEsObserver = new Observer<List<IEObject>>() {
            @Override
            public void onChanged(List<IEObject> orphanIEs) {
                IEActivityDiegodobelo.orphanIEs.clear();
                IEActivityDiegodobelo.orphanIEs.addAll(orphanIEs);
                if(firstStartOrphane){
                    addOrphaneCategories();
                    firstStartOrphane = false;
                }
                //addOrphaneCategories();
            }
        };
        mainActivityViewModel.getAllIEofAWalletWithoutCategoriesAssigned(walletID).observe(IEActivityDiegodobelo.this, orphanIEsObserver);
    }

    @Override
    public void onPause(){
        super.onPause();
        firstStart=true;
        firstStartOrphane=true;
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
        if (position == 2) {
            //launch create IE activity
            Intent myIntent = new Intent(IEActivityDiegodobelo.this, CreateIEActivity.class);
            myIntent.putExtra("wallet", walletAsString); //Optional parameters
            IEActivityDiegodobelo.this.startActivity(myIntent);
        } else if (position == 1) {
            //launch create Category activity
            Intent myIntent = new Intent(IEActivityDiegodobelo.this, CreateCategoryActivity.class);
            myIntent.putExtra("wallet", walletAsString); //Optional parameters
            IEActivityDiegodobelo.this.startActivity(myIntent);
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
            ((TextView) first_item.findViewById(R.id.wallet_name_ie_screen)).setText(wallet.getName());//TODO
        }

        //Add Categories and IEs
        for (CategoryObject categoryObject : categoryObjectsList) {
            addItem(categoryObject);
//            executor.execute(new Runnable() {
//                @Override
//                public void run() {
//
//                }
//            });
        }
    }

    private void addOrphaneCategories(){
        Log.d("WTF:",mExpandingList.getItemsCount()+"-ITEM COUNT");
        for(IEObject orphaneIEObject: orphanIEs){
            addOrphanItems(orphaneIEObject);
        }
    }

    private void addOrphanItems(IEObject orphaneIEObject) {
        //double ie_value = mainActivityViewModel.getCategoryIESUM(orphaneIEObject.getWallet_id(), orphaneIEObject.getName());
        //List<IEObject> ieObjectList = mainActivityViewModel.getCategorysIE(walletID, orphaneIEObject.getName());

        IEActivityDiegodobelo.this.runOnUiThread(() -> {
            final ExpandingItem item;
            if(orphaneIEObject.type==0) {
                item  =getCorrectItem(orphaneIEObject.amount);
            }else{
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

    private void depopulateLists(){
        mExpandingList.removeAllViews();
    }

    private void addItem(final CategoryObject categoryObject) {
        double ie_value = mainActivityViewModel.getCategoryIESUM(categoryObject.getCategory_id());
        List<IEObject> ieObjectList = mainActivityViewModel.getCategorysIE(categoryObject.getCategory_id());

        IEActivityDiegodobelo.this.runOnUiThread(() -> {
            final ExpandingItem item = getCorrectItem(ie_value);
            if (item != null) {
                //setAllColors(ie_value);
                item.setIndicatorColor(Color.WHITE);
                //item.setIndicatorColorRes(getIconIndicatorColor(ie_value));//R.color.positiveBackgroundColor);
                item.setIndicatorIcon(getIcon(ie_value));//R.drawable.house_icon);
                //1. ImageView category_icon
                ((TextView) item.findViewById(R.id.text_view_value_white)).setText(String.valueOf(ie_value));
                ((TextView) item.findViewById(R.id.text_view_category_name_white)).setText(categoryObject.getName());
                ((TextView) item.findViewById(R.id.text_view_description_white)).setText(categoryObject.getDescription());
                item.createSubItems(ieObjectList.size());
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
                    configureSubItem(item, view, ieObjectList.get(i), categoryObject);
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
                        removeItemClickListener(categoryObject, item);
                    }
                });
            }
            Log.d("UI thread", "I am the UI thread");
        });
    }

    private void removeItemClickListener(Object object, final ExpandingItem expandingItem) {
        final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE: {
                        try {
                            if(object instanceof IEObject){
                                mainActivityViewModel.deleteIE(((IEObject)object).getId());
                                mExpandingList.removeView(expandingItem);
                            }else {
                                mainActivityViewModel.deleteCategory(((CategoryObject)object).getCategory_id());
                                mExpandingList.removeView(expandingItem);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.out.println(e.getMessage());
                            Toast toast = Toast.makeText(IEActivityDiegodobelo.this, "Unable to delete the Category", Toast.LENGTH_SHORT);
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

    private void configureSubItem(final ExpandingItem item, final View view, final IEObject ieObject, final CategoryObject categoryObject) {
        //subItemViewHolder.ieIcon.setText(ieObject.getSubItemTitle()); //to be implemented
        if (ieObject.type == 1) {
            ((TextView) view.findViewById(R.id.text_view_ie_value_white)).setText("-" + String.valueOf(ieObject.amount));
        } else {
            ((TextView) view.findViewById(R.id.text_view_ie_value_white)).setText(String.valueOf(ieObject.amount));
        }
        ((TextView) view.findViewById(R.id.text_view_title_white)).setText(String.valueOf(ieObject.name));
        //((TextView) view.findViewById(R.id.text_view_ie_description_white)).setText("TO BE IMPLEMENTED");
        view.findViewById(R.id.remove_sub_item_white).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeSubItemClickListener(ieObject.getId(), item, view, categoryObject);
            }
        });
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
                            Toast.makeText(IEActivityDiegodobelo.this, "I/E Deleted", Toast.LENGTH_SHORT).show();
                            expandingItem.removeSubItem(view);
                            recalculateCategorySum(expandingItem, categoryObject);
//                            mExpandingList.removeAllViews();
//                            populateLists();

                            //mExpandingList.replaceItem(expandingItem);
                            //refresh_category(expandingItem, view, categoryObject);
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.out.println(e.getMessage());
                            Toast toast = Toast.makeText(IEActivityDiegodobelo.this, "Unable to delete the I/E", Toast.LENGTH_SHORT);
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

    private void refresh_category(ExpandingItem expandingItem, View view, CategoryObject categoryObject) {

    }

}
