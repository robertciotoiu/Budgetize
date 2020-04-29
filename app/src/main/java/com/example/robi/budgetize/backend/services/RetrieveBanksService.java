package com.example.robi.budgetize.backend.services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.robi.budgetize.ApplicationObj;
import com.example.robi.budgetize.backend.viewmodels.ServicesHandlerViewModel;
import com.example.robi.budgetize.data.remotedatabase.entities.Bank;
import com.example.robi.budgetize.data.remotedatabase.remote.oauth1.activity.OAuthActivity;
import com.example.robi.budgetize.data.remotedatabase.remote.oauth1.lib.ExpiredAccessTokenException;
import com.example.robi.budgetize.data.remotedatabase.remote.oauth1.lib.OBPRestClient;
import com.example.robi.budgetize.data.remotedatabase.remote.oauth1.lib.ObpApiCallFailedException;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RetrieveBanksService extends Service {

    public ArrayList<Bank> banks = new ArrayList<Bank>();
    ServicesHandlerViewModel servicesHandlerViewModel;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        if (intent.getBooleanExtra("getAllBanks", false)) {
            // ((ApplicationObj)getApplicationContext()).banks = new ArrayList<Bank>().addAll();
            try {
                setBanks();
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("Error retrieving banks ","in RetrieveBanksService.java");
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }
     /**
     * @return A String containing the json representing the available banks, or an error message
     */
    @SuppressLint("StaticFieldLeak")
    protected void setBanks() throws JSONException {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    JSONObject banksJson = OBPRestClient.getBanksJson();
                    Gson gson = new Gson();
                    JSONArray banksJsonJSONArray = banksJson.getJSONArray("banks");
                    for (int i = 0; i < banksJsonJSONArray.length(); i++) {
                        banks.add(gson.fromJson(banksJsonJSONArray.getJSONObject(i).toString(), Bank.class));
                        Log.d("BANK:", banks.get(i).toString());
                    }
                    if (banks != null) {
                        ((ApplicationObj) getApplicationContext()).banks.addAll(banks);
                        //servicesHandlerViewModel.mObservableBanks.postValue(banks);
                        sendMessage("BanksAdded");
                    } else {
                        sendMessage("BanksNOTAdded");
                    }

                } catch (ExpiredAccessTokenException e) {
                    // login again / re-authenticate
                    redoOAuth();
                } catch (ObpApiCallFailedException | JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
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
