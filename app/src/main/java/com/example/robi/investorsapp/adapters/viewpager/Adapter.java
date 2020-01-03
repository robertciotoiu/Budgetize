package com.example.robi.investorsapp.adapters.viewpager;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.robi.investorsapp.R;
import com.example.robi.investorsapp.database.wallet.Wallet;

import java.util.List;

public class Adapter extends PagerAdapter {

    private List<Wallet> wallets;
    private LayoutInflater layoutInflater;
    private Context context;
    private List<View> views;

    public Adapter(List<Wallet> wallets, Context context)
    {
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

//    public void setViews(int position)
//    {
//        views.
//    }


    @Override
    public int getCount() {
        return wallets.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view.equals(o);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position)
    {
        layoutInflater = LayoutInflater.from(context);

        View view = inflatePages(container,position);
        configureView(view,position);

        container.addView(view,position);

        return view;
    }

    private View inflatePages(ViewGroup container, int position) {
        int ie = wallets.get(position).getIE();
        View view = null;
        Button b = null;
        if(ie>0)
        {
            view = layoutInflater.inflate(R.layout.page_positive,container,false);
            b = view.findViewById(R.id.ie_button);
            b.setText("+"+ ie +"$");
        }else if(ie<0)
        {
            view = layoutInflater.inflate(R.layout.page_negative,container,false);
            b = view.findViewById(R.id.ie_button);
            b.setText(ie +"$");
        }
        else
        {
            view = layoutInflater.inflate(R.layout.page_neutral,container,false);
            b = view.findViewById(R.id.ie_button);
            b.setText(ie +"$");
        }
        return view;
    }

    private void configureView(View view, int position)
    {
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


    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }
}
