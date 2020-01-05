package com.example.robi.investorsapp.adapters.viewpager;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.robi.investorsapp.R;
import com.example.robi.investorsapp.activities.IEActivity;
import com.example.robi.investorsapp.activities.MainActivity;
import com.example.robi.investorsapp.database.wallet.Wallet;

import org.w3c.dom.Text;

import java.util.List;

public class Adapter extends PagerAdapter {

    private List<Wallet> wallets;
    private LayoutInflater layoutInflater;
    private Context context;
    private List<View> views;

    @Override
    public int getItemPosition(Object object)
    {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return wallets.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view.equals(o);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = LayoutInflater.from(context);

        View view = inflatePages(container,position);
        configureView(view,position);

        container.addView(view,position);

        return view;
    }

    public Adapter(List<Wallet> wallets, Context context) {
        this.wallets = wallets;
        this.context = context;
    }

    public Wallet getCurrentWallet(int position)
    {
        return wallets.get(position);
    }

    public View getView(int position)
    {
        return views.get(position);
    }

    private View inflatePages(ViewGroup container, int position) {//verifica sa nu fie null walletul
        //int ie = wallets.get(position).getIE();
        try {
            double ie = MainActivity.myDatabase.walletDao().getIE(wallets.get(position).getId());
            double financialStatus = wallets.get(position).getFinancialStatus();
            double financialGoal = wallets.get(position).getFinancialGoal();
            int percentage = (int)((financialStatus/financialGoal)*100);
            Wallet wallet = wallets.get(position);
            View view = null;
            Button b = null;
            TextView walletName = null;
            ProgressBar pb = null;


            if (ie > 0) {
                view = layoutInflater.inflate(R.layout.page_positive, container, false);
                walletName = (TextView) view.findViewById(R.id.wallet_name);
                walletName.setText(wallet.getName());
                pb = (ProgressBar) view.findViewById(R.id.progressBar);
                pb.setProgress(percentage,true);
                b = (Button) view.findViewById(R.id.ie_button);
                b.setText("+" + ie + "$");

                init_pb_listener(view,position);
            } else if (ie < 0) {
                view = layoutInflater.inflate(R.layout.page_negative, container, false);
                walletName = (TextView) view.findViewById(R.id.wallet_name);
                walletName.setText(wallet.getName());
                b = (Button) view.findViewById(R.id.ie_button);
                b.setText(ie + "$");

                init_pb_listener(view,position);
            } else {
                view = layoutInflater.inflate(R.layout.page_neutral, container, false);
                walletName = (TextView) view.findViewById(R.id.wallet_name);
                walletName.setText(wallet.getName());
                b = (Button) view.findViewById(R.id.ie_button);
                b.setText(ie + "$");

                init_pb_listener(view,position);
            }
            return view;
        }catch(Exception e)
        {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return null;
    }

    private void init_pb_listener(View view, int position) {
        final int p = position;
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        progressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent myIntent = new Intent(context, IEActivity.class);
                myIntent.putExtra("wallet", p); //Optional parameters
                context.startActivity(myIntent);
            }
        });
    }

    private void configureView(View view, int position) {
//        int ie = wallets.get(position).getIE();
//        Button b = view.findViewById(R.id.ie_button);
//        b.getBackground().setAlpha(0);
//
//        if(ie>0) {
//            b.setText("+"+ ie +"$");
//        }
//        else
//        {
//            b.setText(ie +"$");
//        }
    }


}
