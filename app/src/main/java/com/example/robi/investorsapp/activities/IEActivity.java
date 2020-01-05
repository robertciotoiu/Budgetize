package com.example.robi.investorsapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import com.example.robi.investorsapp.R;
import com.example.robi.investorsapp.adapters.recyclerView.IECategoryAdapter;
import com.example.robi.investorsapp.database.wallet.Wallet;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionButton;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionHelper;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionLayout;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RFACLabelItem;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RapidFloatingActionContentLabelList;

import java.util.ArrayList;
import java.util.List;

//@AILayout(R.layout.activity_main)
public class IEActivity extends AppCompatActivity implements RapidFloatingActionContentLabelList.OnRapidFloatingActionContentLabelListListener{
    int wallet_position = 0;
    //@AIView(R.id.activity_main_rfal)
    private RapidFloatingActionLayout rfaLayout;
    //@AIView(R.id.activity_main_rfab)
    private RapidFloatingActionButton rfaBtn;
    private RapidFloatingActionHelper rfabHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ie);
        Bundle bundle = getIntent().getExtras();

        if(bundle.get("wallet")!=null)
        {
            wallet_position = (int) bundle.get("wallet");
        }

        init_recyclerView(wallet_position);
        init_floatingPointButton();

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

    private void init_recyclerView(int position) {

        RecyclerView rvCategory = findViewById(R.id.recycleView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(IEActivity.this);
        IECategoryAdapter ieCategoryAdapter = new IECategoryAdapter(this,MainActivity.wallets.get(position));
        rvCategory.setAdapter(ieCategoryAdapter);
        rvCategory.setLayoutManager(layoutManager);
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
            Intent myIntent = new Intent(IEActivity.this, CreateIEActivity.class);
            myIntent.putExtra("wallet", wallet_position); //Optional parameters
            IEActivity.this.startActivity(myIntent);
        }
        else if(position==2)
        {
            //launch create Category activity
            Intent myIntent = new Intent(IEActivity.this, CreateCategoryActivity.class);
            myIntent.putExtra("wallet", wallet_position); //Optional parameters
            IEActivity.this.startActivity(myIntent);
        }
        rfabHelper.toggleContent();
    }
}
