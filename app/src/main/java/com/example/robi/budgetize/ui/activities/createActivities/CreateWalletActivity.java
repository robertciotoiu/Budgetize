package com.example.robi.budgetize.ui.activities.createActivities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.robi.budgetize.ApplicationObj;
import com.example.robi.budgetize.R;
import com.example.robi.budgetize.backend.viewmodels.MainActivityViewModel;
import com.example.robi.budgetize.backend.viewmodels.factories.MainActivityViewModelFactory;
import com.example.robi.budgetize.data.localdatabase.entities.Wallet;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mynameismidori.currencypicker.CurrencyPicker;

public class CreateWalletActivity extends AppCompatActivity {
    TextInputLayout walletNameLayout;
    TextInputLayout financialGoalLayout;
    TextInputLayout financialStatusLayout;
    TextInputEditText walletNameInputEdit;
    TextInputEditText financialGoalInputEdit;
    TextInputEditText financialStatusInputEdit;
    ImageView financialStatusImageView;
    TextView currencyTextView;
    String financialStatusCurrency = "RON";
    private MainActivityViewModel mainActivityViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivityViewModel = new ViewModelProvider(this
                , new MainActivityViewModelFactory((ApplicationObj) this.getApplication()))
                .get(MainActivityViewModel.class);
        setContentView(R.layout.activity_create_wallet);
        init_listeners();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void init_listeners() {
        walletNameLayout = this.findViewById(R.id.wallet_name_text_input_layout);
        financialGoalLayout = this.findViewById(R.id.financial_status_text_input_layout);
        financialStatusLayout = this.findViewById(R.id.financial_goal_text_input_layout);

        walletNameInputEdit = this.findViewById(R.id.wallet_name_edit_text);
        financialGoalInputEdit = this.findViewById(R.id.financial_goal_edit_text);
        financialStatusInputEdit = this.findViewById(R.id.financial_status_edit_text);

        financialStatusImageView = this.findViewById(R.id.currency_financial_status_imageview_create);
        currencyTextView = this.findViewById(R.id.currency_textview);

        Button add_button = (Button) this.findViewById(R.id.add_button);
        add_button.setOnClickListener(v -> createWallet());

        financialStatusImageView.setOnClickListener(v -> {
            CurrencyPicker picker = CurrencyPicker.newInstance("Select Currency");  // dialog title
            picker.setListener((name, code, symbol, flagDrawableResID) -> {
                financialStatusImageView.setImageDrawable(getDrawable(flagDrawableResID));
                currencyTextView.setText(code);
                financialStatusCurrency = code;
                picker.dismiss();
            });
            picker.show(getSupportFragmentManager(), "CURRENCY_PICKER");
        });

        currencyTextView.setOnClickListener(v -> {
            CurrencyPicker picker = CurrencyPicker.newInstance("Select Currency");  // dialog title
            picker.setListener((name, code, symbol, flagDrawableResID) -> {
                financialStatusImageView.setImageDrawable(getDrawable(flagDrawableResID));
                currencyTextView.setText(code);
                financialStatusCurrency = code;
                picker.dismiss();
            });
            picker.show(getSupportFragmentManager(), "CURRENCY_PICKER");
        });
    }

    private void createWallet() {
        String walletName = walletNameInputEdit.getText() == null ? "" : walletNameInputEdit.getText().toString();
        double financialGoal;
        double financialStatus;

        try {
            financialGoal = Double.parseDouble(financialGoalInputEdit.getText() == null ? "" : financialGoalInputEdit.getText().toString());
            financialStatus = Double.parseDouble(financialStatusInputEdit.getText() == null ? "" : financialStatusInputEdit.getText().toString());
            Wallet wallet = new Wallet(walletName, financialStatus, financialGoal, financialStatusCurrency);
            long status = mainActivityViewModel.addWallet(wallet);

            try {
                if (mainActivityViewModel.getWalletById(status) != null) {
                    Toast.makeText(this, "Wallet added successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Wallet failed to be added", Toast.LENGTH_SHORT).show();
                }
                //closes this activity and return to MainOAuthActivity.java
                this.finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
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
