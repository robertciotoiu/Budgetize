package com.example.robi.investorsapp.viewpager;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.text.Layout;
import android.text.style.BackgroundColorSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.robi.investorsapp.MainActivity;
import com.example.robi.investorsapp.R;

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

        View view = layoutInflater.inflate(R.layout.page,container,false);

        Button b = view.findViewById(R.id.ie_button);

        setBackground(view, position);
        //view.setBackgroundColor(0xFFD81B60);


        //int income = wallets.get(position).getIncome();
        //int expenses= wallets.get(position).getExpenses();
        int ie = wallets.get(position).getIE();
        createCircle(b,ie);

        container.addView(view,position);

        return view;
    }

    private void createCircle(Button b, int ie)
    {

        int balance = ie;
        String s_balance = Integer.toString(ie);
        int ie_button_size = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, b.getResources().getDisplayMetrics()));

        StateListDrawable drawable = (StateListDrawable)b.getBackground();
        DrawableContainer.DrawableContainerState dcs = (DrawableContainer.DrawableContainerState)drawable.getConstantState();
        Drawable[] drawableItems = dcs.getChildren();
        GradientDrawable gradientDrawableChecked = (GradientDrawable)drawableItems[0]; // item 1

        if(balance>0)
        {
            s_balance = "+" + s_balance + "$";
            b.setText(s_balance);
            b.setTextColor(context.getResources().getColor(R.color.positiveMoneyColor));
            gradientDrawableChecked.setStroke(ie_button_size,context.getResources().getColor(R.color.positiveCircleColor));

        }
        else if(balance <0)
        {
            s_balance = s_balance + "$";
            b.setText(s_balance );
            b.setTextColor(context.getResources().getColor(R.color.negativeMoneyColor));
            gradientDrawableChecked.setStroke(ie_button_size,context.getResources().getColor(R.color.negativeCircleColor));


        }
        else
        {
            s_balance = s_balance + "$";
            b.setText(s_balance);
            b.setTextColor(context.getResources().getColor(R.color.neutralMoneyColor));
            gradientDrawableChecked.setStroke(ie_button_size,context.getResources().getColor(R.color.neutralCircleColor));
        }
    }

    private void setBackground(View v,  int position)
    {
        if(wallets.get(position).getIE()>0)
        {
            v.setBackgroundColor(context.getResources().getColor(R.color.positiveBackgroundColor));
        }
        else
        if(wallets.get(position).getIE()<0)
        {
            v.setBackgroundColor(context.getResources().getColor(R.color.negativeBackgroundColor));
        }
        else
        {
            v.setBackgroundColor(context.getResources().getColor(R.color.neutralBackgroundColor));
        }
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }
}
