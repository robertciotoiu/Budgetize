package com.example.robi.budgetize.ui.activities.createActivities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
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
import com.mynameismidori.currencypicker.CurrencyPickerListener;

import java.lang.ref.WeakReference;

public class CreateWalletActivity extends AppCompatActivity {
    private MainActivityViewModel mainActivityViewModel;
    TextInputLayout walletNameLayout;
    TextInputLayout financialGoalLayout;
    TextInputLayout financialStatusLayout;

    TextInputEditText walletNameInputEdit;
    TextInputEditText financialGoalInputEdit;
    TextInputEditText financialStatusInputEdit;

    ImageView financialStatusImageView;
    TextView currencyTextView;
//    ImageView financialGoalImageView;

    String financialStatusCurrency = "RON";
//    String financialGoalCurrency = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivityViewModel = new ViewModelProvider(this
                , new MainActivityViewModelFactory((ApplicationObj) this.getApplication()))
                .get(MainActivityViewModel.class);
        //setAnimation();
        setContentView(R.layout.activity_create_wallet);
        init_listeners();
    }

    public void init_listeners() {
        walletNameLayout = this.findViewById(R.id.wallet_name_text_input_layout);
        financialGoalLayout = this.findViewById(R.id.financial_status_text_input_layout);
        financialStatusLayout = this.findViewById(R.id.financial_goal_text_input_layout);

        walletNameInputEdit = this.findViewById(R.id.wallet_name_edit_text);
        financialGoalInputEdit = this.findViewById(R.id.financial_goal_edit_text);
        financialStatusInputEdit = this.findViewById(R.id.financial_status_edit_text);

        financialStatusImageView = this.findViewById(R.id.currency_financial_status_imageview_create);
        currencyTextView = this.findViewById(R.id.currency_textview);
//        financialGoalImageView = this.findViewById(R.id.currency_financial_goal_imageview_create);

        Button add_button = (Button) this.findViewById(R.id.add_button);

        add_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                createWallet();
            }
        });

        financialStatusImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CurrencyPicker picker = CurrencyPicker.newInstance("Select Currency");  // dialog title
                picker.setListener(new CurrencyPickerListener() {
                    @Override
                    public void onSelectCurrency(String name, String code, String symbol, int flagDrawableResID) {
                        // Implement your code here
//                        doSelectCurrencyLogic(first_item, code, flagDrawableResID, picker);
//                        updateAllAmounts();
                        financialStatusImageView.setImageDrawable(getDrawable(flagDrawableResID));
                        currencyTextView.setText(code);
                        financialStatusCurrency = code;
                        picker.dismiss();
                    }
                });
                picker.show(getSupportFragmentManager(), "CURRENCY_PICKER");
            }
        });

//
        currencyTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CurrencyPicker picker = CurrencyPicker.newInstance("Select Currency");  // dialog title
                picker.setListener(new CurrencyPickerListener() {
                    @Override
                    public void onSelectCurrency(String name, String code, String symbol, int flagDrawableResID) {
                        // Implement your code here
//                        doSelectCurrencyLogic(first_item, code, flagDrawableResID, picker);
//                        updateAllAmounts();
                        financialStatusImageView.setImageDrawable(getDrawable(flagDrawableResID));
                        currencyTextView.setText(code);
                        financialStatusCurrency = code;
                        picker.dismiss();
                    }
                });
                picker.show(getSupportFragmentManager(), "CURRENCY_PICKER");
            }
        });

        //MainOAuthActivity.wallets.add(new Wallet();
    }

    private void createWallet() {
        String walletName = walletNameInputEdit.getText().toString();

        double financialGoal = 0;

        double financialStatus = 0;

        try {
            financialGoal = Double.parseDouble(financialGoalInputEdit.getText().toString());

            financialStatus = Double.parseDouble(financialStatusInputEdit.getText().toString());

            Wallet wallet = new Wallet(walletName, financialStatus, financialGoal, financialStatusCurrency);

            Long status = mainActivityViewModel.addWallet(wallet);

            try {
                if (mainActivityViewModel.getWalletById(status) != null) {
//                    CategoryObject categoryObject = new CategoryObject();
                    Toast.makeText(this, "Wallet added successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Wallet failed to be added", Toast.LENGTH_SHORT).show();
                }
                this.finish(); //closes this activity and return to MainOAuthActivity.java
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

    private class AddWalletAsyncTask extends AsyncTask<Void, Void, Long> {
        //prevent leak!
        private WeakReference<Activity> weakActivity;
        MainActivityViewModel mainActivityViewModel;
        Wallet wallet;

        public AddWalletAsyncTask(Activity activity, MainActivityViewModel mainActivityViewModel, Wallet wallet) {
            this.weakActivity = new WeakReference<>(activity);
            this.mainActivityViewModel = mainActivityViewModel;
            this.wallet = wallet;
        }

        @SuppressLint("WrongThread")
        @Override
        protected Long doInBackground(Void... voids) {
            onPostExecute(mainActivityViewModel.addWallet(wallet));
            return mainActivityViewModel.addWallet(wallet);
        }

        @Override
        protected void onPostExecute(Long status) {
            try {
                Activity activity = weakActivity.get();
                if (activity == null) {
                    return;
                }

                if (mainActivityViewModel.getWalletById(status) != null) {
                    activity.runOnUiThread(() -> {
                        Toast.makeText(activity, "Wallet added successfully", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    activity.runOnUiThread(() -> {
                        Toast.makeText(activity, "Wallet failed to be added", Toast.LENGTH_SHORT).show();
                    });
                }
                activity.finish(); //closes this activity and return to MainOAuthActivity.java
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
