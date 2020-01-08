package com.example.robi.investorsapp.activities.createActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.example.robi.investorsapp.R;
import com.example.robi.investorsapp.activities.MainActivity;
import com.example.robi.investorsapp.database.ie.IEObject;

public class CreateIEActivity extends AppCompatActivity {
    int wallet_position = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_ie);
        Bundle bundle = getIntent().getExtras();

        if(bundle.get("wallet")!=null)
        {
            wallet_position = (int) bundle.get("wallet");
        }
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
        EditText IECategory = (EditText) this.findViewById(R.id.create_ie_category);
        Switch IEType = (Switch) this.findViewById(R.id.create_ie_switch);

        String ieName = IEName.getText().toString();
        double ieAmount = 0.0;
        String ieCategory = IECategory.getText().toString();
        int ieType = 0;
        if(IEType.isChecked()==true)
        {
            ieType = 1;
        }

        try{
            ieAmount = Double.parseDouble(IEAmount.getText().toString());
            long currentWalletID = MainActivity.wallets.get(wallet_position).getId();

            IEObject ieObject = new IEObject(currentWalletID, ieName, ieAmount, ieCategory, ieType);

            long status = MainActivity.myDatabase.ieoDao().addIEObject(ieObject);

            if(MainActivity.myDatabase.walletDao().getWalletById(status)!=null) {
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
}
