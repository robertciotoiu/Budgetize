package com.example.robi.investorsapp;

import android.animation.ArgbEvaluator;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.content.res.AppCompatResources;
import android.util.TypedValue;
import android.view.View;
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

        wallets = new ArrayList<>();
        wallets.add(new Wallet(2000,1000));
        wallets.add(new Wallet(780,1000));
        //wallets.add(new Wallet(280,45));
        wallets.add(new Wallet(700,700));
        //wallets.add(new Wallet(445,230));
        //wallets.add(new Wallet(700,700));

        adapter = new Adapter(wallets, this);

        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);
        viewPager.setPadding(0,0,0,0);

        Integer[] colors_temp = {
                getResources().getColor(R.color.colorAccent),
                getResources().getColor(R.color.colorPrimary),
                getResources().getColor(R.color.colorPrimaryDark)};

        colors = colors_temp;

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
                if(i<(adapter.getCount()-1)&&i<(colors.length-1))
                {
                    viewPager.setBackgroundColor(
                            (Integer)argbEvaluator.evaluate(
                                    v,
                                    colors[i],
                                    colors[i+1]
                            )
                    );
                }
                else
                {
                    viewPager.setBackgroundColor(colors[colors.length-1]);
                }
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
