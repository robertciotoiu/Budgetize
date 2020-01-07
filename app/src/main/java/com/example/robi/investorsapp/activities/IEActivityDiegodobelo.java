package com.example.robi.investorsapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.diegodobelo.expandingview.ExpandingItem;
import com.diegodobelo.expandingview.ExpandingList;
import com.example.robi.investorsapp.R;
import com.example.robi.investorsapp.database.category.CategoryObject;
import com.example.robi.investorsapp.database.ie.IEObject;
import com.example.robi.investorsapp.database.wallet.Wallet;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionButton;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionHelper;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionLayout;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RFACLabelItem;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RapidFloatingActionContentLabelList;

import java.util.ArrayList;
import java.util.List;

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
        init();

        populateLists();

    }

    private void init() {
        Bundle bundle = getIntent().getExtras();
        if(bundle.get("wallet")!=null)
        {
            wallet_position = (int) bundle.get("wallet");
        }
        wallet = MainActivity.wallets.get(wallet_position);
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
                .setLabel("I/E")
                .setResId(R.drawable.category_icon_test)
                .setIconNormalColor(0xff056f00)
                .setIconPressedColor(0xff0d5302)
                .setLabelColor(0xff056f00)
                .setWrapper(1)
        );
        items.add(new RFACLabelItem<Integer>()
                .setLabel("Category")
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
        Toast.makeText(this, "clicked label: " + position, Toast.LENGTH_SHORT).show();
        rfabHelper.toggleContent();
    }

    @Override
    public void onRFACItemIconClick(int position, RFACLabelItem item) {
        Toast.makeText(this, "clicked icon: " + position, Toast.LENGTH_SHORT).show();
        if(position==1)
        {
            //launch create IE activity
            Intent myIntent = new Intent(IEActivityDiegodobelo.this, CreateIEActivity.class);
            myIntent.putExtra("wallet", wallet_position); //Optional parameters
            IEActivityDiegodobelo.this.startActivity(myIntent);
        }
        else if(position==2)
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
        for(CategoryObject categoryObject:categoryObjectsList) {
            addItem(categoryObject);
        }
    }

    private void addItem(CategoryObject categoryObject) {
        final ExpandingItem item = mExpandingList.createNewItem(R.layout.expanding_layout);

        if(item!=null)
        {
            item.setIndicatorColorRes(R.color.positiveBackgroundColor);
            item.setIndicatorIconRes(R.drawable.house_icon);
            List<IEObject> ieObjectList = MainActivity.myDatabase.categoryDao().getCategorysIE(wallet.getId(),categoryObject.getName());

            //1. ImageView category_icon
            ((TextView)item.findViewById(R.id.text_view_value)).setText(String.valueOf(MainActivity.myDatabase.categoryDao().getCategoryIESUM(categoryObject.getWallet_id(),categoryObject.getName())));
            ((TextView)item.findViewById(R.id.text_view_category_name)).setText(categoryObject.getName());
            ((TextView)item.findViewById(R.id.text_view_description)).setText(categoryObject.getDescription());


            item.createSubItems(ieObjectList.size());
            for(int i = 0 ;i<item.getSubItemsCount();i++)
            {
                //Let's get the created sub item by its index
                final View view = item.getSubItemView(i);

                //Let's set some values in
                configureSubItem(item, view, ieObjectList.get(i));
            }

            item.findViewById(R.id.add_more_sub_items).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //launch create Category activity
                    Intent myIntent = new Intent(IEActivityDiegodobelo.this, CreateIEActivity.class);
                    myIntent.putExtra("wallet", wallet_position); //Optional parameters
                    IEActivityDiegodobelo.this.startActivity(myIntent);
                }
            });

            item.findViewById(R.id.remove_item).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mExpandingList.removeItem(item);
                }
            });
        }
    }


    private void configureSubItem(final ExpandingItem item, final View view, IEObject ieObject) {
        //subItemViewHolder.ieIcon.setText(ieObject.getSubItemTitle()); //to be implemented
        ((TextView) view.findViewById(R.id.text_view_ie_value)).setText(String.valueOf(ieObject.amount));
        ((TextView) view.findViewById(R.id.text_view_title)).setText(String.valueOf(ieObject.name));
        ((TextView) view.findViewById(R.id.text_view_ie_description)).setText("TO BE IMPLEMENTED");
        view.findViewById(R.id.remove_sub_item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.removeSubItem(view);
            }
        });
    }
}
