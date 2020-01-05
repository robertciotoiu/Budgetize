package com.example.robi.investorsapp.activities;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.robi.investorsapp.R;
import com.example.robi.investorsapp.database.wallet.Wallet;

public class CreateWalletActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_wallet);

        init_listeners();
    }


    public void init_listeners() {
        Button add_button = (Button) this.findViewById(R.id.add_button);

        add_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                createWallet();
            }
        });

        //MainActivity.wallets.add(new Wallet();
    }

    private void createWallet() {
        EditText WalletName = (EditText) this.findViewById(R.id.walletName);
        EditText FinancialGoal = (EditText) this.findViewById(R.id.financialGoal);
        EditText FinancialStatus = (EditText) this.findViewById(R.id.financialStatus);

        String walletName = WalletName.getText().toString();

        double financialGoal = 0;

        double financialStatus = 0;

        try{
            financialGoal = Double.parseDouble(FinancialGoal.getText().toString());

            financialStatus = Double.parseDouble(FinancialStatus.getText().toString());

            Wallet wallet = new Wallet(walletName, financialStatus , financialGoal);

            long status = MainActivity.myDatabase.walletDao().addWallet(wallet);

            if(MainActivity.myDatabase.walletDao().getWalletById(status)!=null) {
                Toast.makeText(this, "Wallet added successfully", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(this, "Wallet failed to be added", Toast.LENGTH_SHORT).show();
            }

            this.finish(); //closes this activity and return to MainActivity.java
        }
        catch(Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            Toast.makeText(this, "Invalid Financial Goal", Toast.LENGTH_SHORT).show();
        }
    }
}
