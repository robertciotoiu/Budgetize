package com.example.robi.investorsapp.activities;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
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
    TextView introText;
    ImageView introArrow;
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
        refresh_wallets();
    }

    private void init_screen()
    {
        init_introScreen();
        init_viewPager();
        init_listeners();
    }

    private void init_introScreen() {
        introText = (TextView) this.findViewById(R.id.intro_text);
        introArrow = (ImageView) this.findViewById(R.id.intro_arrow_imgview);
    }


    private void init_viewPager()
    {
        adapter = new Adapter(wallets, this);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);
        viewPager.setPadding(0,0,0,0);

//        Integer[] colors_temp = {
//                getResources().getColor(R.color.colorAccent),
//                getResources().getColor(R.color.colorPrimary),
//                getResources().getColor(R.color.colorPrimaryDark)};
//
//        colors = colors_temp;
        //adapter = new Adapter(wallets, this);

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

    private void init_listeners()
    {
        FloatingActionButton addWalletFab = (FloatingActionButton) this.findViewById(R.id.add_wallet);
        addWalletFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, CreateWalletActivity.class);
                //myIntent.putExtra("key", value); //Optional parameters
                MainActivity.this.startActivity(myIntent);
            }
        });

        FloatingActionButton removeWalletFab = (FloatingActionButton) this.findViewById(R.id.remove_wallet);
        removeWalletFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                createDialogClickListener();
            }
        });
    }

    private void refresh_wallets()
    {
        wallets.clear();
        wallets.addAll(myDatabase.walletDao().getAllWallets());
        viewPager.setAdapter(adapter);

        if(wallets.isEmpty())
        {
            introText.setVisibility(View.VISIBLE);
            introArrow.setVisibility(View.VISIBLE);

            viewPager.setVisibility(View.INVISIBLE);
        }
        else
        {
            introText.setVisibility(View.INVISIBLE);
            introArrow.setVisibility(View.INVISIBLE);

            viewPager.setVisibility(View.VISIBLE);
        }

    }

    private void createDialogClickListener() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE: {
                        try{
                            myDatabase.walletDao().deleteWallet(wallets.get(viewPager.getCurrentItem()));
                            refresh_wallets();
                            Toast.makeText(MainActivity.this, "Wallet Deleted", Toast.LENGTH_SHORT).show();
                        }
                        catch(Exception e)
                        {
                            e.printStackTrace();
                            System.out.println(e.getMessage());
                            Toast toast = Toast.makeText(MainActivity.this, "Unable to delete the Wallet", Toast.LENGTH_SHORT);
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
        builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

}
