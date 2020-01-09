package com.example.robi.investorsapp.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.drawable.DrawableCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.robi.investorsapp.R;
import com.example.robi.investorsapp.activities.createActivities.CreateCategoryActivity;
import com.example.robi.investorsapp.activities.createActivities.CreateIEActivity;
import com.example.robi.investorsapp.database.category.CategoryObject;
import com.example.robi.investorsapp.database.ie.IEObject;
import com.example.robi.investorsapp.database.wallet.Wallet;
import com.example.robi.investorsapp.expandingview.ExpandingItem;
import com.example.robi.investorsapp.expandingview.ExpandingList;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionButton;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionHelper;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionLayout;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RFACLabelItem;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RapidFloatingActionContentLabelList;

import java.util.ArrayList;
import java.util.List;

import static com.example.robi.investorsapp.activities.MainActivity.wallets;

public class IEActivityDiegodobelo extends AppCompatActivity implements RapidFloatingActionContentLabelList.OnRapidFloatingActionContentLabelListListener {

    private ExpandingList mExpandingList  = null;
    int wallet_position = 0;
    private RapidFloatingActionLayout rfaLayout;
    private RapidFloatingActionButton rfaBtn;
    private RapidFloatingActionHelper rfabHelper;

    private Wallet wallet;
    private List<CategoryObject> categoryObjectsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iediegodobelo);
        Bundle bundle = getIntent().getExtras();
        if(bundle.get("wallet")!=null)
        {
            wallet_position = (int) bundle.get("wallet");
        }
        init();
        populateLists();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mExpandingList.removeAllViews();
        init();
        populateLists();
    }

    private void init() {
        init_floatingPointButton();
        wallet = wallets.get(wallet_position);
        categoryObjectsList = buildCategoryList();
        mExpandingList = findViewById(R.id.expanding_list_main);
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
        //Toast.makeText(this, "clicked icon: " + position, Toast.LENGTH_SHORT).show();
        if(position==2)
        {
            //launch create IE activity
            Intent myIntent = new Intent(IEActivityDiegodobelo.this, CreateIEActivity.class);
            myIntent.putExtra("wallet", wallet_position); //Optional parameters
            IEActivityDiegodobelo.this.startActivity(myIntent);
        }
        else if(position==1)
        {
            //launch create Category activity
            Intent myIntent = new Intent(IEActivityDiegodobelo.this, CreateCategoryActivity.class);
            myIntent.putExtra("wallet", wallet_position); //Optional parameters
            IEActivityDiegodobelo.this.startActivity(myIntent);
        }
        rfabHelper.toggleContent();
    }

    private List<CategoryObject> buildCategoryList() {
        return MainActivity.myDatabase.categoryDao().getAllCategories(wallet.getId());
    }

    private void populateLists() {
        ExpandingItem first_item = mExpandingList.createNewItem(R.layout.first_ie_item);
        if(first_item!=null)
        {
            ((TextView)first_item.findViewById(R.id.wallet_name_ie_screen)).setText(wallet.getName());
        }
        for(CategoryObject categoryObject:categoryObjectsList) {
            addItem(categoryObject);
        }
    }

    private void addItem(final CategoryObject categoryObject) {
        double ie_value = MainActivity.myDatabase.categoryDao().getCategoryIESUM(categoryObject.getWallet_id(),categoryObject.getName());
        final ExpandingItem item = getCorrectItem(ie_value);
        if(item!=null)
        {
            //setAllColors(ie_value);
            item.setIndicatorColor(Color.WHITE);
            //item.setIndicatorColorRes(getIconIndicatorColor(ie_value));//R.color.positiveBackgroundColor);
            item.setIndicatorIcon(getIcon(ie_value));//R.drawable.house_icon);
            List<IEObject> ieObjectList = MainActivity.myDatabase.categoryDao().getCategorysIE(wallet.getId(),categoryObject.getName());

            //1. ImageView category_icon
            ((TextView)item.findViewById(R.id.text_view_value_white)).setText(String.valueOf(ie_value));
            ((TextView)item.findViewById(R.id.text_view_category_name_white)).setText(categoryObject.getName());
            ((TextView)item.findViewById(R.id.text_view_description_white)).setText(categoryObject.getDescription());


            item.createSubItems(ieObjectList.size());
            for(int i = 0 ;i<item.getSubItemsCount();i++)
            {
                //Let's get the created sub item by its index
                final View view = item.getSubItemView(i);

//                if(i== item.getSubItemsCount()-1) {
//                    RelativeLayout layout = view.findViewById(R.id.layout_subitem);
//                    ViewGroup.LayoutParams params = layout.getLayoutParams();
//                    params.height = 300;
//                    view.setLayoutParams(params);
//                }
                //Let's set some values in
                configureSubItem(item, view, ieObjectList.get(i),categoryObject);
            }

//            item.findViewById(R.id.add_more_sub_items).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    //launch create Category activity
//                    Intent myIntent = new Intent(IEActivityDiegodobelo.this, CreateIEActivity.class);
//                    myIntent.putExtra("wallet", wallet_position); //Optional parameters
//                    IEActivityDiegodobelo.this.startActivity(myIntent);
//                }
//            });

            item.findViewById(R.id.remove_item).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeItemClickListener(categoryObject.getName(), item);
                }
            });
        }
    }

    private void removeItemClickListener(final String categoryName,final ExpandingItem expandingItem) {
        final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE: {
                        try {
                            MainActivity.myDatabase.categoryDao().deleteCategory(wallet.getId(),categoryName);
                            refresh_wallet();
                            Toast.makeText(IEActivityDiegodobelo.this, "Category Deleted", Toast.LENGTH_SHORT).show();
                            mExpandingList.removeItem(expandingItem);
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

            private void refresh_wallet() {

            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want do DELETE permanently this Category?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    private void setAllColors(double ie) {
        RelativeLayout lLayout = (RelativeLayout) findViewById(R.id.expanding_item_id);
        TextView value = (TextView) findViewById(R.id.text_view_value);
        TextView description = (TextView) findViewById(R.id.text_view_category_name);
        TextView name = (TextView) findViewById(R.id.text_view_category_name);

        if(ie>0)
        {
            lLayout.setBackgroundColor(getResources().getColor(R.color.positiveBackgroundColor));
            value.setTextColor(getResources().getColor(R.color.positiveBackgroundColor));
            description.setTextColor(getResources().getColor(R.color.positiveBackgroundColor));
            name.setTextColor(getResources().getColor(R.color.positiveBackgroundColor));
        }
        else if(ie<0)
        {
            lLayout.setBackgroundColor(getResources().getColor(R.color.negativeBackgroundColor));
            value.setTextColor(getResources().getColor(R.color.negativeBackgroundColor));
            description.setTextColor(getResources().getColor(R.color.negativeBackgroundColor));
            name.setTextColor(getResources().getColor(R.color.negativeBackgroundColor));
        }
        else
        {
            lLayout.setBackgroundColor(getResources().getColor(R.color.neutralBackgroundColor));
            value.setTextColor(getResources().getColor(R.color.neutralBackgroundColor));
            description.setTextColor(getResources().getColor(R.color.neutralBackgroundColor));
            name.setTextColor(getResources().getColor(R.color.neutralBackgroundColor));
        }
    }

    private ExpandingItem getCorrectItem(double ie_value) {
        if(ie_value>0)
        {
            return mExpandingList.createNewItem(R.layout.expanding_layout_positive);
        }else if(ie_value<0)
        {
            return mExpandingList.createNewItem(R.layout.expanding_layout_negative);
        }
        else
        {
            return mExpandingList.createNewItem(R.layout.expanding_layout_neutral);
        }

    }


    private Drawable getIcon(double ie) {
        //have to return icon id
        Drawable unwrappedDrawable = AppCompatResources.getDrawable(this, R.drawable.house_icon);
        Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
        if(ie>0)
        {
            DrawableCompat.setTint(wrappedDrawable, getResources().getColor(R.color.positiveBackgroundColor));
        }
        else if(ie<0)
        {
            DrawableCompat.setTint(wrappedDrawable, getResources().getColor(R.color.negativeBackgroundColor));
        }
        else
        {
            DrawableCompat.setTint(wrappedDrawable, getResources().getColor(R.color.neutralBackgroundColor));
        }

        return wrappedDrawable;
    }

    private int getIconIndicatorColor(double ie) {
        //have to return icon's indicator color
        if(ie>0)
        {
           return R.color.positiveBackgroundColor;
        }
        else if(ie<0)
        {
            return R.color.negativeBackgroundColor;
        }
        else
        {
            return R.color.neutralBackgroundColor;
        }
    }

    private void configureSubItem(final ExpandingItem item, final View view, final IEObject ieObject, final CategoryObject categoryObject) {
        //subItemViewHolder.ieIcon.setText(ieObject.getSubItemTitle()); //to be implemented
        if(ieObject.type==1)
        {
            ((TextView) view.findViewById(R.id.text_view_ie_value_white)).setText("-"+String.valueOf(ieObject.amount));
        }else{
            ((TextView) view.findViewById(R.id.text_view_ie_value_white)).setText(String.valueOf(ieObject.amount));
        }
        ((TextView) view.findViewById(R.id.text_view_title_white)).setText(String.valueOf(ieObject.name));
        //((TextView) view.findViewById(R.id.text_view_ie_description_white)).setText("TO BE IMPLEMENTED");
        view.findViewById(R.id.remove_sub_item_white).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeSubItemClickListener(ieObject.getId(),item,view,categoryObject);
            }
        });
    }

    private void removeSubItemClickListener(final long ieID, final ExpandingItem expandingItem, final View view,final CategoryObject categoryObject) {
        final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE: {
                        try {
                            MainActivity.myDatabase.ieoDao().deleteIE(wallet.getId(),ieID);
                            Toast.makeText(IEActivityDiegodobelo.this, "I/E Deleted", Toast.LENGTH_SHORT).show();
                            expandingItem.removeSubItem(view);
                            refresh_category(expandingItem,view,categoryObject);
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
            private void refresh_category(ExpandingItem expandingItem, View view, CategoryObject categoryObject) {
                double ieValue = MainActivity.myDatabase.categoryDao().getCategoryIESUM(wallet.getId(), categoryObject.getName());
                expandingItem.setIndicatorIcon(getIcon(ieValue));

                if(ieValue>0)
                {
                    expandingItem.setBackgroundResource(R.drawable.item_shape_positive);
                }else if(ieValue<0)
                {
                    expandingItem.setBackgroundResource(R.drawable.item_shape_negative);
                }
                else
                {
                    expandingItem.setBackgroundResource(R.drawable.item_shape_neutral);
                }
                TextView value = (TextView)expandingItem.findViewById(R.id.text_view_value_white);
                value.setText(String.valueOf(ieValue));
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want do DELETE permanently this I/E?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

}
