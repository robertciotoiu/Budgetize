package com.example.robi.investorsapp.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import com.example.robi.investorsapp.R;
import com.example.robi.investorsapp.database.wallet.Wallet;

public class CreateWalletActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_wallet);

        init_listeners();
    }



    public void init_listeners(){
        Button add_button = (Button) this.findViewById(R.id.add_button);


        wallets.add(new Wallet(700,700));
    }

}
