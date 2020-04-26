package com.example.robi.budgetize.ui.activities.createActivities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.robi.budgetize.ApplicationObj;
import com.example.robi.budgetize.R;
import com.example.robi.budgetize.backend.viewmodels.MainActivityViewModelFactory;
import com.example.robi.budgetize.data.localdatabase.entities.CategoryObject;
import com.example.robi.budgetize.data.localdatabase.entities.Wallet;
import com.example.robi.budgetize.ui.activities.MainActivity;
import com.example.robi.budgetize.backend.viewmodels.MainActivityViewModel;
import com.google.gson.Gson;

import java.util.List;

public class CreateCategoryActivity extends AppCompatActivity {
    long walletID = 0;
    Wallet wallet;
    MainActivityViewModel mainActivityViewModel;
    private Observer<List<Wallet>> walletListObsever;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_category);
        mainActivityViewModel =  new ViewModelProvider(this
                , new MainActivityViewModelFactory((ApplicationObj) this.getApplication()))
                .get(MainActivityViewModel.class);
        Bundle bundle = getIntent().getExtras();
        if(bundle.get("wallet")!=null){
            Gson gson = new Gson();
            String walletAsString = (String) bundle.get("wallet");
            this.wallet = gson.fromJson(walletAsString, Wallet.class);
            walletID = wallet.getId();
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
            long currentWalletID = wallet.getId();
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
