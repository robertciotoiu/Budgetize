package com.example.robi.budgetize.backend.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.robi.budgetize.backend.viewmodels.ServicesHandlerViewModel;
import com.example.robi.budgetize.data.remotedatabase.entities.Bank;
import com.example.robi.budgetize.data.remotedatabase.remote.oauth1.activity.OAuthActivity;
import com.example.robi.budgetize.data.remotedatabase.remote.oauth1.lib.OBPRestClient;

import java.util.ArrayList;

public class RetrieveBanksService extends Service {

    public ArrayList<Bank> banks = new ArrayList<Bank>();
    ServicesHandlerViewModel servicesHandlerViewModel;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        if (intent.getBooleanExtra("getAllBanks", false)) {
            // ((ApplicationObj)getApplicationContext()).banks = new ArrayList<Bank>().addAll();
        }
        return super.onStartCommand(intent, flags, startId);
    }


    private void redoOAuth() {
        OBPRestClient.clearAccessToken(this);
        Intent oauthActivity = new Intent(this, OAuthActivity.class);
        startActivity(oauthActivity);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void sendMessage(String message) {
        Log.d("sender", "Broadcasting message");
        Intent intent = new Intent("my-integer");
        // You can also include some extra data.
        intent.putExtra("message", message);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
