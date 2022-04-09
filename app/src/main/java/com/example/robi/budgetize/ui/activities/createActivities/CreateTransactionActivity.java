package com.example.robi.budgetize.ui.activities.createActivities;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ViewModelProvider;

import com.example.robi.budgetize.ApplicationObj;
import com.example.robi.budgetize.R;
import com.example.robi.budgetize.backend.viewmodels.MainActivityViewModel;
import com.example.robi.budgetize.backend.viewmodels.factories.MainActivityViewModelFactory;
import com.example.robi.budgetize.data.localdatabase.entities.CategoryObject;
import com.example.robi.budgetize.data.localdatabase.entities.IEObject;
import com.example.robi.budgetize.data.localdatabase.entities.Wallet;
import com.example.robi.budgetize.data.localdatabase.enums.TransactionOccurrenceEnum;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.mynameismidori.currencypicker.CurrencyPicker;
import com.skydoves.elasticviews.ElasticButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class CreateTransactionActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private static final String TAG = CreateTransactionActivity.class.getName();
    private static ArrayList<CategoryObject> categoryObjects = new ArrayList<>();
    private final HashMap<String, Long> categoryHashMap = new HashMap<>();
    ElasticButton addButton;
    ImageView currencyPickerImageView;
    // New design approach using Material
    ArrayAdapter<String> categoryAdapter;
    ArrayAdapter<String> occurrenceAdapter;
    private Wallet wallet;
    private long wallet_id = 0;
    private int ieType = 0;
    private String selectedCurrency = "RON";
    //Switch
    private RadioGroup ieSwitch;
    private RadioButton ieSwitchIncome;
    private RadioButton ieSwitchExpense;
    private AutoCompleteTextView categoryDropDown;
    private AutoCompleteTextView occurrenceDropdown;
    private TextInputEditText nameInputText;
    private TextInputEditText amountInputText;
    private TextInputEditText pickedDate;
    // MVVM
    private MainActivityViewModel mainActivityViewModel;
    private Observer<List<CategoryObject>> getCategoryListObserver;

    public static String[] getOccurrencesOptions(Class<? extends TransactionOccurrenceEnum> e) {
        return Arrays.stream(e.getEnumConstants()).map(TransactionOccurrenceEnum::getOccurrence).toArray(String[]::new);
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "UseCompatLoadingForColorStateLists"})
    public void initRadioButtons() {
        ieSwitchIncome = findViewById(R.id.ie_switch_income);
        ieSwitchExpense = findViewById(R.id.ie_switch_expense);
        ieSwitch = findViewById(R.id.ie_switch);

        ieSwitchIncome.setOnClickListener(v -> {
            // Income selected, update UI
            ieSwitch.setBackgroundColor(getColor(R.color.positiveBackgroundColor));
            ieSwitchIncome.setBackground(getDrawable(R.drawable.transaction_ie_selected_shape));
            ieSwitchExpense.setBackgroundColor(getColor(R.color.positiveBackgroundColor));
            ieSwitchIncome.setTextColor(0x8A000000);
            ieSwitchExpense.setTextColor(0xffffffff);
            addButton.setBackgroundTintList(getResources().getColorStateList(R.color.positiveBackgroundColor));
            ieType = 0;
        });


        ieSwitchExpense.setOnClickListener(v -> {
            // Income selected, update UI
            ieSwitch.setBackgroundColor(getColor(R.color.negativeBackgroundColor));
            ieSwitchExpense.setBackground(getDrawable(R.drawable.transaction_ie_selected_shape));
            ieSwitchIncome.setBackgroundColor(getColor(R.color.negativeBackgroundColor));
            ieSwitchExpense.setTextColor(0x8A000000);
            ieSwitchIncome.setTextColor(0xffffffff);
            addButton.setBackgroundTintList(getResources().getColorStateList(R.color.negativeBackgroundColor));
            ieType = 1;
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.CreateTransactionActivityTheme_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_transaction);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.get("wallet") != null) {
            Gson gson = new Gson();
            String walletAsString = (String) bundle.get("wallet");
            this.wallet = gson.fromJson(walletAsString, Wallet.class);
            wallet_id = wallet.getId();
            initUIElements();
        }
    }

    private void initUIElements() {
        findViewById(R.id.create_ie_name);
        findViewById(R.id.create_ie_amount);
        findViewById(R.id.category_dropdown_textfield);
        findViewById(R.id.occurrence_dropdown_textfield);
        findViewById(R.id.pick_date_textinput);
        currencyPickerImageView = findViewById(R.id.currency_imageview_create);

        nameInputText = findViewById(R.id.name_input_text);
        amountInputText = findViewById(R.id.amount_input_text);
        pickedDate = findViewById(R.id.date_picked);

        categoryDropDown = findViewById(R.id.category_dropdown);
        occurrenceDropdown = findViewById(R.id.occurrence_dropdown);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    protected void handleOnDestroy() {
        mainActivityViewModel.getAllCategoriesOfAWallet(wallet_id).removeObserver(getCategoryListObserver);
    }

    private void init_mvvm() {
        categoryObjects = new ArrayList<>();
        mainActivityViewModel = new ViewModelProvider(this
                , new MainActivityViewModelFactory((ApplicationObj) this.getApplication()))
                .get(MainActivityViewModel.class);
        getCategoryListObserver = categoryObjects -> {
            CreateTransactionActivity.categoryObjects.addAll(categoryObjects);
            categoryHashMap.clear();
            String[] categoriesArr = getCategories();
            categoryAdapter = new ArrayAdapter<>(CreateTransactionActivity.this, R.layout.dropdown_item, categoriesArr);
            categoryDropDown.setAdapter(categoryAdapter);
        };
        mainActivityViewModel.getAllCategoriesOfAWallet(wallet.getId()).observe(this, getCategoryListObserver);
        String[] occArr = getOccurrencesOptions(TransactionOccurrenceEnum.class);
        occurrenceAdapter = new ArrayAdapter<>(CreateTransactionActivity.this, R.layout.dropdown_item, occArr);
        occurrenceDropdown.setAdapter(occurrenceAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        init_mvvm();
        init_listeners();
        initRadioButtons();
    }

    @Override
    public void onPause() {
        super.onPause();
        mainActivityViewModel.getAllCategoriesOfAWallet(wallet.getId()).removeObserver(getCategoryListObserver);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void init_listeners() {
        addButton = (ElasticButton) this.findViewById(R.id.create_ie_add_button);

        addButton.setOnClickListener(v -> createIE());

        findViewById(R.id.date_picked).setOnClickListener(v -> showDatePickerDialog());

        currencyPickerImageView.setOnClickListener(v -> {
            // dialog title
            CurrencyPicker picker = CurrencyPicker.newInstance("Select Currency");
            picker.setListener((name, code, symbol, flagDrawableResID) -> {
                currencyPickerImageView.setImageDrawable(getDrawable(flagDrawableResID));
                selectedCurrency = code;
                picker.dismiss();
            });
            picker.show(getSupportFragmentManager(), "CURRENCY_PICKER");
        });
    }

    @SuppressLint("ShowToast")
    private void createIE() {
        String ieName = nameInputText.getText() == null ? "" : nameInputText.getText().toString();
        double ieAmount;
        String ieCategoryName = categoryDropDown.getText().toString();
        long ieCategoryID = 0;
        getCategories();
        try {
            ieAmount = Double.parseDouble(amountInputText.getText() == null ? "" : amountInputText.getText().toString());
            long currentWalletID = wallet.getId();
            if (pickedDate.getText() == null) {
                Log.e(TAG, "pickedDate is null. Investigate.");
                return;
            }
            if (pickedDate.getText().toString().contentEquals("")) {
                Toast.makeText(this, "Press on Date Icon to selected Transaction Date!", Toast.LENGTH_LONG).show();
                return;
            }
            if (occurrenceDropdown.getText().toString().contentEquals("")) {
                Toast.makeText(this, "Pick occurrence!", Toast.LENGTH_LONG).show();
                return;
            }

            if (!ieCategoryName.contentEquals("")) {
                Long ieCategoryIDLong = categoryHashMap.get(ieCategoryName);
                if (ieCategoryIDLong != null)
                    ieCategoryID = ieCategoryIDLong;
            }


            IEObject ieObject = new IEObject(currentWalletID, ieName, ieAmount, ieCategoryID, ieType, pickedDate.getText().toString(), occurrenceDropdown.getText().toString(), null, selectedCurrency);//TODO:Choosable

            long status = mainActivityViewModel.addIEObject(ieObject);

            if (mainActivityViewModel.getIEObject(status) != null) {
                Toast.makeText(this, "Transaction created successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Transaction failed to be created", Toast.LENGTH_SHORT).show();
            }

            this.finish(); //closes this activity and return to MainOAuthActivity.java
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            Toast.makeText(this, "Invalid data", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }


    private Spinner init_spinner(int viewID, String[] values) {
        final Spinner spinner = (Spinner) findViewById(viewID);
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this, R.layout.spinner_item, values) {
            @Override
            public boolean isEnabled(int position) {
                // Disable the first item from Spinner
                // First item will be use for hint
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(spinnerArrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                // If user change the default selection
                // First item is disable and it is used for hint
                if (position > 0) {
                    // Notify the selected item text
                    Toast.makeText
                            (getApplicationContext(), "Selected : " + selectedItemText, Toast.LENGTH_SHORT)
                            .show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        return spinner;
    }

    private String[] getCategories() {
        String[] categories = new String[categoryObjects.size() + 1];
        categories[0] = "No Category";
        int i = 1;
        for (CategoryObject c : categoryObjects) {
            categories[i] = c.getName();
            categoryHashMap.put(c.getName(), c.getCategory_id());
            i++;
        }
        return categories;
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String date = year + "-" + (month + 1) + "-" + dayOfMonth;//"yyyy"+-MM-dd";
        pickedDate.setText(date);
    }
}
