package com.example.robi.investorsapp.activities.createActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.example.robi.investorsapp.rest.utils.AppConstants;
import com.example.robi.investorsapp.R;
import com.example.robi.investorsapp.rest.utils.HttpUtils;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;

public class HandleLinkedBankAccount extends AppCompatActivity {
//TODO: for this activity we should have a ViewModel to hold the bank names and request them only one time
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handle_linked_bank_account);
        // ATTENTION: This was auto-generated to handle app links.
        handleIntent();
        getBankAccountsFromServer();
    }

    private void getBankAccountsFromServer() {
        Log.d("INTRU AICI:","AM INTRAT PE AICI!!!");
        try {
            String response = HttpUtils.requestBankNames(AppConstants.SERVER_URL,"/RequestBankAccountStatus");
            Log.d("RequestBankAccountStatus: ", response);
        } catch (IOException e) {
            Log.d("Error at RequestBankAccountStatus",e.toString());
            e.printStackTrace();
        }
    }

    @Override
    protected void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent();
    }

    private void handleIntent() {
        Intent appLinkIntent = getIntent();
        String appLinkAction = appLinkIntent.getAction();
        Uri appLinkData = appLinkIntent.getData();
        if(appLinkData != null)
        {
            String linkedBankAccountResponse = appLinkData.getLastPathSegment();
            System.out.println(linkedBankAccountResponse);
        }
    }
}
