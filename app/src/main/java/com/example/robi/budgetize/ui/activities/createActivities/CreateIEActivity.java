package com.example.robi.budgetize.ui.activities.createActivities;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

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
import com.example.robi.budgetize.data.localdatabase.enums.IEOccurrenceEnum;
import com.example.robi.budgetize.data.localdatabase.entities.Wallet;
import com.google.android.gms.common.util.ArrayUtils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class CreateIEActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{
    private static ArrayList<CategoryObject> categoryObjects = new ArrayList<CategoryObject>();
    private HashMap<String,Long> categoryHashMap = new HashMap<String, Long>();
    private Wallet wallet;
    private long wallet_id = 0;
    private TextView pickedDate;
    private Spinner categorySpinner;
    private Spinner occurrenceSpinner;
    private String selectedDate;

    private MainActivityViewModel mainActivityViewModel;
    private Observer<List<CategoryObject>> categoryListObsever;
    private Observer<List<CategoryObject>> getCategoryListObsever;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_ie);
        Bundle bundle = getIntent().getExtras();
        if(bundle.get("wallet")!=null) {
            Gson gson = new Gson();
            String walletAsString = (String) bundle.get("wallet");
            this.wallet = gson.fromJson(walletAsString, Wallet.class);
            wallet_id = wallet.getId();

            pickedDate = (TextView) this.findViewById(R.id.selected_date_textView);

            //spinner = init_spinner();
            //init activity
        }
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    protected void handleOnDestroy(){
        mainActivityViewModel.getAllCategoriesOfAWallet(wallet_id).removeObserver(getCategoryListObsever);
    }

    private void init_mvvm() {
        categoryObjects = new ArrayList<CategoryObject>();
        mainActivityViewModel =  new ViewModelProvider(this
                , new MainActivityViewModelFactory((ApplicationObj) this.getApplication()))
                .get(MainActivityViewModel.class);
        getCategoryListObsever = new Observer<List<CategoryObject>>() {
            @Override
            public void onChanged(List<CategoryObject> categoryObjects) {
                CreateIEActivity.categoryObjects.addAll(categoryObjects);
                categoryHashMap.clear();
                String[] categoriesArr = getCategories();
                categorySpinner = init_spinner(R.id.create_ie_category, categoriesArr);
            }
        };
        mainActivityViewModel.getAllCategoriesOfAWallet(wallet.getId()).observe(this,getCategoryListObsever);
        String[] occArr = ArrayUtils.concat(new String[]{"Select transaction's occurrence"},getOccurrencesOptions(IEOccurrenceEnum.class));
        occurrenceSpinner = init_spinner(R.id.pick_occurence_spinner, occArr);
    }
    @Override
    public void onResume(){
        super.onResume();
        init_mvvm();
        init_listeners();
    }

//    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
//    public void handleOnResume(){
//
//        //categoryListObsever = categoryObjects::addAll;
//        //mainActivityViewModel.getAllCategoriesOfAWallet(wallet.getId()).observe(this,categoryListObsever);
//    }

    @Override
    public void onPause(){
        super.onPause();
        mainActivityViewModel.getAllCategoriesOfAWallet(wallet.getId()).removeObserver(getCategoryListObsever);
    }

    public static String[] getOccurrencesOptions(Class<? extends Enum> e) {
        return Arrays.stream(e.getEnumConstants()).map(Enum::name).toArray(String[]::new);
    }

    public void init_listeners() {
        Button add_button = (Button) this.findViewById(R.id.create_ie_add_button);

        add_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                createIE();
            }
        });

        findViewById(R.id.pick_ie_date_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        //MainOAuthActivity.wallets.add(new Wallet();
    }

    @SuppressLint("ShowToast")
    private void createIE() {
        EditText IEName = (EditText) this.findViewById(R.id.create_ie_name);
        EditText IEAmount = (EditText) this.findViewById(R.id.create_ie_amount);
        Switch IEType = (Switch) this.findViewById(R.id.create_ie_switch);
        String ieName = IEName.getText().toString();
        double ieAmount = 0.0;
        String ieCategoryName = categorySpinner.getSelectedItem().toString();
        long ieCategoryID = 0;
        int ieType = 0;
        if(IEType.isChecked()) {
            ieType = 1;
        }
        try{
            ieAmount = Double.parseDouble(IEAmount.getText().toString());
            long currentWalletID = wallet.getId();
            if(pickedDate.getText().toString().contentEquals("Press Transaction Date")){
                Toast.makeText(this,"Press on Date Icon to selected Transaction Date!",Toast.LENGTH_LONG).show();
                return;
            }
            if(occurrenceSpinner.getSelectedItem().toString().contentEquals("Select ie occurrence")){
                Toast.makeText(this,"Pick occurrence!",Toast.LENGTH_LONG).show();
                return;
            }

            if(!ieCategoryName.contentEquals("Select Category")){
                ieCategoryID = categoryHashMap.get(ieCategoryName);
            }

            IEObject ieObject = new IEObject(currentWalletID, ieName, ieAmount, ieCategoryID, ieType, pickedDate.getText().toString(), occurrenceSpinner.getSelectedItem().toString(),null,"USD");//TODO:Choosable

            long status = mainActivityViewModel.addIEObject(ieObject);

            if(mainActivityViewModel.getIEObject(status)!=null) {
                Toast.makeText(this, "IE added successfully", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(this, "IE failed to be added", Toast.LENGTH_SHORT).show();
            }

            this.finish(); //closes this activity and return to MainOAuthActivity.java
        }
        catch(Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            Toast.makeText(this, "Invalid data", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDatePickerDialog(){
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
                this,R.layout.spinner_item, values){
            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                }
                else
                {
                    return true;
                }
            }
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                }
                else {
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
                if(position > 0){
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
        long wallet_id = wallet.getId();
        //List<CategoryObject> categoryObjects = mainActivityViewModel.getAllCategoriesOfAWallet(wallet_id).getValue();
        String[] categories = new String[categoryObjects.size()+1];
        categories[0] = "Select Category";
        int i=1;
        for(CategoryObject c:categoryObjects)
        {
            categories[i] = c.getName();
            categoryHashMap.put(c.getName(),c.getCategory_id());
            i++;
        }
        return categories;
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String date = year+"-"+month+"-"+dayOfMonth;//"yyyy"+-MM-dd";
        pickedDate.setText(date);
    }
}
