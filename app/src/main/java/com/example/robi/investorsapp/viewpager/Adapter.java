package com.example.robi.investorsapp.viewpager;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.robi.investorsapp.R;

import java.util.List;

public class Adapter extends PagerAdapter {

    private List<Wallet> wallets;
    private LayoutInflater layoutInflater;
    private Context context;

    public Adapter(List<Wallet> wallets, Context context)
    {
        this.wallets = wallets;
        this.context = context;
    }

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
        View view = layoutInflater.inflate(R.layout.page,container,false);

        Button b = view.findViewById(R.id.ie_button);


        //int income = wallets.get(position).getIncome();
        //int expenses= wallets.get(position).getExpenses();
        int ie = wallets.get(position).getIE();
        b.setText(Integer.toString(ie));
        createCircle(b,ie);

        container.addView(view,position);

        return view;
    }

    private void createCircle(Button b, int ie)
    {

        int balance = ie;
        int ie_button_size = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, b.getResources().getDisplayMetrics()));

        StateListDrawable drawable = (StateListDrawable)b.getBackground();
        DrawableContainer.DrawableContainerState dcs = (DrawableContainer.DrawableContainerState)drawable.getConstantState();
        Drawable[] drawableItems = dcs.getChildren();
        GradientDrawable gradientDrawableChecked = (GradientDrawable)drawableItems[0]; // item 1
        if(balance>0)
        {
            b.setTextColor(0xFF0F7D3C);
            gradientDrawableChecked.setStroke(ie_button_size,0xFF0F7D3C);

        }
        else if(balance <0)
        {
            b.setTextColor(0xFFBE402A);
            gradientDrawableChecked.setStroke(ie_button_size,0xFFBE402A);

        }
        else
        {
            b.setTextColor(0xFFF5A11A);
            gradientDrawableChecked.setStroke(ie_button_size,0xFFF5A11A);
        }
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }
}
