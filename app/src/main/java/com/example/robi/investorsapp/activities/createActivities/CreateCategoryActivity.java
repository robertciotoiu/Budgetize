package com.example.robi.investorsapp.activities.createActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.robi.investorsapp.R;
import com.example.robi.investorsapp.activities.MainActivity;
import com.example.robi.investorsapp.database.category.CategoryObject;

public class CreateCategoryActivity extends AppCompatActivity {
    int wallet_position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_category);
        Bundle bundle = getIntent().getExtras();

        if(bundle.get("wallet")!=null)
        {
            wallet_position = (int) bundle.get("wallet");
        }

        init_listeners();
    }


    public void init_listeners() {
        Button add_button = (Button) this.findViewById(R.id.add_button_category);

        add_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                createCategory();
            }
        });

        //MainActivity.wallets.add(new Wallet();
    }

    private void createCategory() {
        EditText CategoryName = (EditText) this.findViewById(R.id.category_name_category);
        EditText CategoryDescription = (EditText) this.findViewById(R.id.category_description_category);

        String categoryName = CategoryName.getText().toString();
        String categoryDescription = CategoryDescription.getText().toString();

        try{
            long currentWalletID = MainActivity.wallets.get(wallet_position).getId();
            CategoryObject categoryObject = new CategoryObject(categoryName,categoryDescription,currentWalletID);

            long status = MainActivity.myDatabase.categoryDao().addCategory(categoryObject);

            if(MainActivity.myDatabase.walletDao().getWalletById(status)!=null) {
                Toast.makeText(this, "Category added successfully", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(this, "Category failed to be added", Toast.LENGTH_SHORT).show();
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
