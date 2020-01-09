package com.example.robi.investorsapp.activities.createActivities;

import androidx.appcompat.app.AppCompatActivity;

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

import com.example.robi.investorsapp.R;
import com.example.robi.investorsapp.activities.MainActivity;
import com.example.robi.investorsapp.database.category.CategoryObject;
import com.example.robi.investorsapp.database.ie.IEObject;

import java.util.List;

import static com.example.robi.investorsapp.activities.MainActivity.myDatabase;

public class CreateIEActivity extends AppCompatActivity {
    int wallet_position = 0;
    Spinner spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_ie);
        Bundle bundle = getIntent().getExtras();

        if(bundle.get("wallet")!=null)
        {
            wallet_position = (int) bundle.get("wallet");
        }
        spinner = init_spinner();
        init_listeners();
    }


    public void init_listeners() {
        Button add_button = (Button) this.findViewById(R.id.create_ie_add_button);

        add_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                createIE();
            }
        });

        //MainActivity.wallets.add(new Wallet();
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
            long currentWalletID = MainActivity.wallets.get(wallet_position).getId();

            IEObject ieObject = new IEObject(currentWalletID, ieName, ieAmount, ieCategory, ieType);

            long status = myDatabase.ieoDao().addIEObject(ieObject);

            if(myDatabase.walletDao().getWalletById(status)!=null) {
                Toast.makeText(this, "IE added successfully", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(this, "IE failed to be added", Toast.LENGTH_SHORT).show();
            }

            this.finish(); //closes this activity and return to MainActivity.java
        }
        catch(Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            Toast.makeText(this, "Invalid data", Toast.LENGTH_SHORT).show();
        }
    }

    private String[] getCategories()
    {
        long wallet_id = MainActivity.wallets.get(wallet_position).getId();
        List<CategoryObject> categoryObjects = MainActivity.myDatabase.categoryDao().getAllCategories(wallet_id);
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

    private Spinner init_spinner()
    {

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


}
