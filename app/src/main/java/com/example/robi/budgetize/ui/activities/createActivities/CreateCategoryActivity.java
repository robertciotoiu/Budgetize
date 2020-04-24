package com.example.robi.budgetize.ui.activities.createActivities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.example.robi.budgetize.R;
import com.example.robi.budgetize.data.localdatabase.entities.CategoryObject;
import com.example.robi.budgetize.data.localdatabase.entities.Wallet;
import com.example.robi.budgetize.ui.activities.MainActivity;
import com.example.robi.budgetize.backend.viewmodels.MainActivityViewModel;

import java.util.List;

public class CreateCategoryActivity extends AppCompatActivity {
    int walletID = 0;
    MainActivityViewModel mainActivityViewModel;
    private Observer<List<Wallet>> walletListObsever;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_category);
        Bundle bundle = getIntent().getExtras();
        if(bundle.get("wallet")!=null)
        {
            walletID = (int) bundle.get("wallet");
        }
        init_listeners();
    }


    public void init_listeners() {
        Button add_button = (Button) this.findViewById(R.id.add_button_category);

        add_button.setOnClickListener(v -> createCategory());

        //MainOAuthActivity.wallets.add(new Wallet();
    }

    private void createCategory() {
        EditText CategoryName = (EditText) this.findViewById(R.id.category_name_category);
        EditText CategoryDescription = (EditText) this.findViewById(R.id.category_description_category);

        String categoryName = CategoryName.getText().toString();
        String categoryDescription = CategoryDescription.getText().toString();

        try{
            long currentWalletID = MainActivity.wallets.get(walletID).getId();
            CategoryObject categoryObject = new CategoryObject(categoryName,categoryDescription,currentWalletID);

            long status = mainActivityViewModel.addCategory(categoryObject);//MainActivity.myDatabase.categoryDao().addCategory(categoryObject);

            if(mainActivityViewModel.getCategoryByName(currentWalletID,categoryName)!=null){//MainActivity.myDatabase.walletDao().getWalletById(status)!=null) {
                Toast.makeText(this, "Category added successfully", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(this, "Category failed to be added", Toast.LENGTH_SHORT).show();
            }

            this.finish(); //closes this activity and return to MainOAuthActivity.java
        }
        catch(Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            Toast.makeText(this, "Invalid data", Toast.LENGTH_SHORT).show();
        }

    }
}
