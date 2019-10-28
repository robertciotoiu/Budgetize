package com.example.robi.investorsapp;

import android.animation.ArgbEvaluator;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.content.res.AppCompatResources;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import com.example.robi.investorsapp.viewpager.Adapter;
import com.example.robi.investorsapp.viewpager.Wallet;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    ViewPager viewPager;
    Adapter adapter;
    Integer[] colors = null;
    ArgbEvaluator argbEvaluator = new ArgbEvaluator();
    List<Wallet> wallets;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        init_screen();





    }


    private void init_screen()
    {
        init_wallets();
        init_viewPager();
    }

    private void init_wallets()
    {
        wallets = new ArrayList<>();//here we should import the existing wallets from memory/database
        //Warning: I think that the wallet needs to contain also design data
        wallets.add(new Wallet(2000,1000));//test data
        wallets.add(new Wallet(780,1000));//test data
        wallets.add(new Wallet(700,700));//test data
    }

    private void init_viewPager()
    {
        adapter = new Adapter(wallets, this);

        viewPager = findViewById(R.id.viewPager);
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


//        viewPager.OnAdapterChangeListener(new ViewPager.OnAdapterChangeListener() {
//            void onAdapterChanged(@NonNull ViewPager var1, @Nullable PagerAdapter var2, @Nullable PagerAdapter var3)
//            {
//
//            }
//
//        });

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
                //aici e pe masura ce se schimba pagina
                //aici pot sa pun sa se incarce cercul cu bani
//                Adapter a = (Adapter) viewPager.getAdapter();
//                int ie = ((Integer) a.getCurrentWallet(i).getIE()).intValue();
            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
//        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int i, float v, int i1) {
//                if(i<(adapter.getCount()-1)&&i<(colors.length-1))
//                {
//                    viewPager.setBackgroundColor(
//                            (Integer)argbEvaluator.evaluate(
//                                    v,
//                                    colors[i],
//                                    colors[i+1]
//                            )
//                    );
//                }
//                else
//                {
//                    viewPager.setBackgroundColor(colors[colors.length-1]);
//                }
//            }
//
//            @Override
//            public void onPageSelected(int i) {
//
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int i) {
//
//            }
//        });
    }

}
