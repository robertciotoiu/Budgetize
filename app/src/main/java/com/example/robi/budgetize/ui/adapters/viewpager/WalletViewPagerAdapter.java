package com.example.robi.budgetize.ui.adapters.viewpager;


import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.viewpager.widget.PagerAdapter;

import com.example.robi.budgetize.R;
import com.example.robi.budgetize.backend.viewmodels.MainActivityViewModel;
import com.example.robi.budgetize.data.localdatabase.entities.Wallet;
import com.example.robi.budgetize.ui.activities.TransactionsActivity;
import com.google.gson.Gson;
import com.mynameismidori.currencypicker.ExtendedCurrency;

import java.util.List;

public class WalletViewPagerAdapter extends PagerAdapter {

    private List<Wallet> wallets;
    private LayoutInflater layoutInflater;
    private Context context;
    private MainActivityViewModel mainActivityViewModel;
    private Observer<Double> ieListObsever;
    private List<View> views;
    int lastWalletPosition;

    @Override
    public int getItemPosition(Object object) {
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
        container.removeView((View) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = LayoutInflater.from(context);

        View view = inflatePages(container, position);
        configureView(view, position);

        container.addView(view, position);

        return view;
    }

    public WalletViewPagerAdapter(List<Wallet> wallets, Context context, MainActivityViewModel mainActivityViewModel) {
        this.wallets = wallets;
        this.context = context;
        this.mainActivityViewModel = mainActivityViewModel;

    }

    public Wallet getCurrentWallet(int position) {
        return wallets.get(position);
    }

    public View getView(int position) {
        return views.get(position);
    }

    private View inflatePages(ViewGroup container, int position) {//verifica sa nu fie null walletul
        //int ie = wallets.get(position).getIE();
        try {
            //TODO: apply mvvm
            double ie = mainActivityViewModel.getIE(wallets.get(position).getId());
            double financialStatus = wallets.get(position).getFinancialStatus();
            double financialGoal = wallets.get(position).getFinancialGoal();
            int percentage = (int) ((financialStatus / financialGoal) * 100);
            Wallet wallet = wallets.get(position);
            View view = null;
            Button b = null;
            TextView walletName = null;
            ProgressBar pb = null;

            String currency = wallet.getCurrency();
            ExtendedCurrency currencyCode = ExtendedCurrency.getCurrencyByISO(currency);
            String currencySymbol = currencyCode.getSymbol();

            if (ie > 0) {
                view = layoutInflater.inflate(R.layout.page_positive, container, false);
                //views.add(view);
//                if(views.get(position)!=null)
//                {
//                    views.add(position,view);
//                }
//                else
//                {
//                    views.remove(position);
//                    views.add(position,view);
//                }
                walletName = (TextView) view.findViewById(R.id.wallet_name);
                walletName.setText(wallet.getName());
                pb = (ProgressBar) view.findViewById(R.id.progressBar);
                pb.setProgress(0, true);

                b = (Button) view.findViewById(R.id.ie_button);
                b.setText("+" + ie + currencySymbol);

                init_pb_listener(view, position);
            } else if (ie < 0) {
                view = layoutInflater.inflate(R.layout.page_negative, container, false);
                walletName = (TextView) view.findViewById(R.id.wallet_name);
                walletName.setText(wallet.getName());
                pb = (ProgressBar) view.findViewById(R.id.progressBar);
                pb.setProgress(0, true);
                b = (Button) view.findViewById(R.id.ie_button);
                b.setText(ie + currencySymbol);

                init_pb_listener(view, position);
            } else {
                view = layoutInflater.inflate(R.layout.page_neutral, container, false);
                walletName = (TextView) view.findViewById(R.id.wallet_name);
                walletName.setText(wallet.getName());
                pb = (ProgressBar) view.findViewById(R.id.progressBar);
                pb.setProgress(0, true);
                //ObjectAnimator progressAnimator = ObjectAnimator.ofInt(pb, "progress", 10000, 0);
                b = (Button) view.findViewById(R.id.ie_button);
                b.setText(ie + currencySymbol);

                init_pb_listener(view, position);
            }
            return view;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return null;
    }

    private void init_pb_listener(View view, final int position) {
        final int p = position;
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        progressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson gson = new Gson();
                String walletAsString = gson.toJson(wallets.get(p));
                mainActivityViewModel.lastWalletPosition = position;
                Intent myIntent = new Intent(context, TransactionsActivity.class);
                myIntent.putExtra("wallet", walletAsString); //Optional parameters
                mainActivityViewModel.lastWalletPosition = position;
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

    @Override
    public void registerDataSetObserver(@NonNull DataSetObserver observer) {
        super.registerDataSetObserver(observer);
    }


}
