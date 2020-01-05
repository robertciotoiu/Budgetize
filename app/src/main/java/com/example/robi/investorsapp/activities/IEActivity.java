package com.example.robi.investorsapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.robi.investorsapp.R;
import com.example.robi.investorsapp.adapters.recyclerView.IECategoryAdapter;

public class IEActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ie);
        int position = 0;
        Bundle bundle = getIntent().getExtras();
        if(bundle.getString("wallet")!=null)
        {
            position = (int) bundle.get("wallet");
        }

        RecyclerView rvCategory = findViewById(R.id.recycleView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(IEActivity.this);
        IECategoryAdapter ieCategoryAdapter = new IECategoryAdapter(this,MainActivity.wallets.get(position));
        rvCategory.setAdapter(ieCategoryAdapter);
        rvCategory.setLayoutManager(layoutManager);
    }
}
