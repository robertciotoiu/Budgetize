package com.example.robi.budgetize.ui.activities.createActivities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.robi.budgetize.R;
import com.example.robi.budgetize.data.localdatabase.entities.Wallet;
import com.example.robi.budgetize.viewmodels.MainActivityViewModel;

public class CreateWalletActivity extends AppCompatActivity {

    private MainActivityViewModel mainActivityViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivityViewModel =  new ViewModelProvider(this
                , ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication()))
                .get(MainActivityViewModel.class);
        //setAnimation();
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

        //MainOAuthActivity.wallets.add(new Wallet();
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

            long status = mainActivityViewModel.addWallet(wallet);

            if(mainActivityViewModel.getWalletById(status)!=null) {
                Toast.makeText(this, "Wallet added successfully", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(this, "Wallet failed to be added", Toast.LENGTH_SHORT).show();
            }

            this.finish(); //closes this activity and return to MainOAuthActivity.java
        }
        catch(Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            Toast.makeText(this, "Invalid Financial Goal", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
