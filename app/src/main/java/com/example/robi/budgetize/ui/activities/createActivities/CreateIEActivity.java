package com.example.robi.budgetize.ui.activities.createActivities;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.Observer;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ViewModelProvider;

import com.example.robi.budgetize.R;
import com.example.robi.budgetize.data.localdatabase.entities.CategoryObject;
import com.example.robi.budgetize.data.localdatabase.entities.IEObject;
import com.example.robi.budgetize.ui.activities.MainActivity;
import com.example.robi.budgetize.viewmodels.MainActivityViewModel;

import java.util.ArrayList;
import java.util.List;

public class CreateIEActivity extends AppCompatActivity implements LifecycleObserver {
    int walletID = 0;
    Spinner spinner;
    private MainActivityViewModel mainActivityViewModel;
    private Observer<List<CategoryObject>> categoryListObsever;
    private List<CategoryObject> categoryObjects = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_ie);
        mainActivityViewModel =  new ViewModelProvider(this
                , ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication()))
                .get(MainActivityViewModel.class);

        Bundle bundle = getIntent().getExtras();


        if(bundle.get("wallet")!=null)
        {
            walletID = (int) bundle.get("wallet");
            spinner = init_spinner();
            init_listeners();
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void handleOnResume(){
        categoryListObsever = categoryObjects::addAll;

        mainActivityViewModel.getAllCategoriesOfAWallet(walletID).observe(this,categoryListObsever);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void handleOnPause(){
        mainActivityViewModel.getAllCategoriesOfAWallet(walletID).removeObserver(categoryListObsever);
    }

    public void init_listeners() {
        Button add_button = (Button) this.findViewById(R.id.create_ie_add_button);

        add_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                createIE();
            }
        });

        //MainOAuthActivity.wallets.add(new Wallet();
    }

    private void createIE() {
        EditText IEName = (EditText) this.findViewById(R.id.create_ie_name);
        EditText IEAmount = (EditText) this.findViewById(R.id.create_ie_amount);
        //EditText IECategory = (EditText) this.findViewById(R.id.create_ie_category);

        Switch IEType = (Switch) this.findViewById(R.id.create_ie_switch);

        String ieName = IEName.getText().toString();
        double ieAmount = 0.0;
        String ieCategory = spinner.getSelectedItem().toString();
        int ieType = 0;
        if(IEType.isChecked()==true)
        {
            ieType = 1;
        }

        try{
            ieAmount = Double.parseDouble(IEAmount.getText().toString());
            long currentWalletID = MainActivity.wallets.get(walletID).getId();

            IEObject ieObject = new IEObject(currentWalletID, ieName, ieAmount, ieCategory, ieType);

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

    private Spinner init_spinner() {

        final Spinner spinner = (Spinner) findViewById(R.id.create_ie_category);
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this,R.layout.spinner_item,getCategories()){
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
        long wallet_id = MainActivity.wallets.get(walletID).getId();
        List<CategoryObject> categoryObjects = mainActivityViewModel.getAllCategoriesOfAWallet(wallet_id).getValue();
        String[] categories = new String[categoryObjects.size()+1];
        categories[0] = "Select Category";
        int i=1;
        for(CategoryObject c:categoryObjects)
        {
            categories[i] = c.getName();
            i++;
        }
        return categories;
    }


}
