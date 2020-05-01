package com.example.robi.budgetize.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.robi.budgetize.R;
import com.example.robi.budgetize.ui.adapters.viewpager.LinkBanksCardFragmentPagerAdapter;
import com.example.robi.budgetize.backend.APIs.viewpagerforbankaccounts.CardItem;
import com.example.robi.budgetize.ui.adapters.viewpager.LinkBanksCardPagerAdapter;
import com.example.robi.budgetize.ui.adapters.viewpager.LinkBanksShadowTransformer;

import java.util.ArrayList;

public class LinkedBankAccounts extends AppCompatActivity implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener {
    private final int SELECTED_BANK = 0;

    private SparseArray<String> currentBankStatus;

    private ViewPager mViewPager;

    private LinkBanksCardPagerAdapter mCardAdapter;
    private LinkBanksShadowTransformer mCardShadowTransformer;
    private LinkBanksCardFragmentPagerAdapter mFragmentCardAdapter;
    private LinkBanksShadowTransformer mFragmentCardShadowTransformer;

    private boolean mShowingFragments = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linked_bank_accounts);
        init_viewpager();//here we add the linked banks accounts
        init_listeners();
        checkBankAccountsStatus();
    }

    private void checkBankAccountsStatus() {
        ArrayList<String> bankNames = new ArrayList<String>();
        currentBankStatus = mCardAdapter.getBankNameAndDataPosition();
        bankNames.clear();
        bankNames.addAll(getBankNames(currentBankStatus));

        getBankNames(currentBankStatus);
        //requestStatusAccounts();
    }

    private ArrayList<String> getBankNames(SparseArray<String> currentBankStatus) {
        ArrayList<String> bankNames = new ArrayList<String>();
        for (int i = 0; i < currentBankStatus.size(); i++) {
            int key = currentBankStatus.keyAt(i);
            // get the object by the key.
            bankNames.add(currentBankStatus.valueAt(key));
        }
        return bankNames;
    }

/*
    private void requestStatusAccounts() {
        String requestPath = "/authStatus";
        HttpUrl.Builder httpBuilder = HttpUrl.parse(AppConstants.SERVER_URL+requestPath).newBuilder();
        httpBuilder.addQueryParameter("BankName",mData.get(position).getTitle());

        Request request = new Request.Builder()
                .url(httpBuilder.build())
                .build();

        HttpUtils.getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                Log.d("Error: ", "exception at POST" + e.toString());
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                } else {
                    Log.d("RESPONSE RECEIVED: ", response.body().string());
                    // do something wih the result
                }
            }
        });

    }
*/

    private void init_listeners() {
        //Link new bank account button
        Button linkNewBankAccountButton = (Button) this.findViewById(R.id.link_new_bank_account_button);
        linkNewBankAccountButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(AvailableBanksActivity.class);
            }
        });

    }

    public LinkedBankAccounts getActivityContext() {
        return this;
    }

    public void startActivity(Class targetClass) {
        Intent myIntent = new Intent(this, targetClass);
        this.startActivityForResult(myIntent, SELECTED_BANK);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECTED_BANK) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.

                String selectedBank = data.getStringExtra("BANK_NAME");
                //Log.d("Msg","TEXTUL ESTE: "+selectedBank);
                addNewCard(selectedBank);
                // Do something with the contact here (bigger example below)
            }
        }
    }

    private void addNewCard(String selectedBank) {
        if (!mCardAdapter.existCardItem(selectedBank)) {
            mCardAdapter.addCardItem(new CardItem(selectedBank, selectedBank));
            mViewPager.setAdapter(mCardAdapter);
        } else {
            Toast.makeText(this,
                    "ERROR: Bank already added", Toast.LENGTH_LONG)
                    .show();
        }
    }

    private void init_viewpager() {
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        //mButton = (Button) findViewById(R.id.cardTypeBtn);
        //((CheckBox) findViewById(R.id.checkBox)).setOnCheckedChangeListener(this);
        //mButton.setOnClickListener(this);

        //TODO: we should get this data from server!(list of available banks)
        mCardAdapter = new LinkBanksCardPagerAdapter(this);
//        mCardAdapter.addCardItem(new CardItem(R.string.ing_title, R.string.ing_description));
//        mCardAdapter.addCardItem(new CardItem(R.string.bt_title, R.string.bt_description));
//        mCardAdapter.addCardItem(new CardItem(R.string.santander_title, R.string.santander_description));
//        mCardAdapter.addCardItem(new CardItem(R.string.jpmc_title, R.string.jpmc_description));
//        mCardAdapter.addCardItem(new CardItem(R.string.citi_title, R.string.citi_description));

        //here add all the linked banks
        mCardAdapter.addCardItem(new CardItem("Citibank", "Citibank"));
        mCardAdapter.addCardItem(new CardItem("ING", "ING"));
        mCardAdapter.addCardItem(new CardItem("Banca Transilvania", "Banca Transilvania"));
        mCardAdapter.addCardItem(new CardItem("Santander", "Santander"));
        mCardAdapter.addCardItem(new CardItem("JP Morgan Chase", "JP Morgan Chase"));

        mFragmentCardAdapter = new LinkBanksCardFragmentPagerAdapter(getSupportFragmentManager(),
                dpToPixels(2, this));

        mCardShadowTransformer = new LinkBanksShadowTransformer(mViewPager, mCardAdapter);
        mFragmentCardShadowTransformer = new LinkBanksShadowTransformer(mViewPager, mFragmentCardAdapter);

        mViewPager.setAdapter(mCardAdapter);
        mViewPager.setPageTransformer(false, mCardShadowTransformer);
        mViewPager.setOffscreenPageLimit(3);
    }
    /*
    @Override
    public void onClick(View view) {
        if (!mShowingFragments) {
            mButton.setText("Views");
            mViewPager.setAdapter(mFragmentCardAdapter);
            mViewPager.setPageTransformer(false, mFragmentCardShadowTransformer);
        } else {
            mButton.setText("Fragments");
            mViewPager.setAdapter(mCardAdapter);
            mViewPager.setPageTransformer(false, mCardShadowTransformer);
        }

        mShowingFragments = !mShowingFragments;
    }
     */

    public static float dpToPixels(int dp, Context context) {
        return dp * (context.getResources().getDisplayMetrics().density);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        mCardShadowTransformer.enableScaling(b);
        mFragmentCardShadowTransformer.enableScaling(b);
    }

    @Override
    public void onClick(View v) {

    }
}
