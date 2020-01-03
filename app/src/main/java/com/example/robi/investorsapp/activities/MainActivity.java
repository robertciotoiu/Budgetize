package com.example.robi.investorsapp.activities;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import androidx.viewpager.widget.ViewPager;

import com.example.robi.investorsapp.R;
import com.example.robi.investorsapp.adapters.viewpager.Adapter;
import com.example.robi.investorsapp.database.DaoAbstract;
import com.example.robi.investorsapp.database.wallet.Wallet;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    ViewPager viewPager;
    Adapter adapter;
    Integer[] colors = null;
    public static List<Wallet>  wallets = new ArrayList<Wallet>();
    public static DaoAbstract myDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        myDatabase = Room.databaseBuilder(getApplicationContext(),DaoAbstract.class,"walletDB").allowMainThreadQueries().build();
        init_screen();

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        getWallets();

    }


    private void init_screen()
    {
        init_viewPager();
        init_listeners();
    }

    private void init_listeners()
    {
        FloatingActionButton myFab = (FloatingActionButton) this.findViewById(R.id.add_wallet);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, CreateWalletActivity.class);
                //myIntent.putExtra("key", value); //Optional parameters
                MainActivity.this.startActivity(myIntent);
            }
        });
    }

    private void getWallets()
    {
        wallets.clear();
        wallets.addAll(myDatabase.walletDao().getAllWallets());
    }

    private void init_viewPager()
    {
        adapter = new Adapter(wallets, this);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);
        viewPager.setPadding(0,0,0,0);

        Integer[] colors_temp = {
                getResources().getColor(R.color.colorAccent),
                getResources().getColor(R.color.colorPrimary),
                getResources().getColor(R.color.colorPrimaryDark)};

        colors = colors_temp;
        adapter = new Adapter(wallets, this);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabDots);
        tabLayout.setupWithViewPager(viewPager, true);

        for(int i=0; i < tabLayout.getTabCount(); i++) {


            View tab = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(i);
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) tab.getLayoutParams();
            p.setMargins(0, 0, 20, 0);
            tab.requestLayout();
        }


        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

    }

}
