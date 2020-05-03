package com.example.robi.budgetize.data.remotedatabase.remote;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.robi.budgetize.data.remotedatabase.entities.Bank;
import com.example.robi.budgetize.data.remotedatabase.remote.oauth1.lib.ExpiredAccessTokenException;
import com.example.robi.budgetize.data.remotedatabase.remote.oauth1.lib.OBPRestClient;
import com.example.robi.budgetize.data.remotedatabase.remote.oauth1.lib.ObpApiCallFailedException;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OBPRetroClass {
    public OBPRetroClass(){

    }

    @SuppressLint("StaticFieldLeak")
    public void getAllAvailableBanks(MutableLiveData<List<Bank>> mObservableBanks){
        /**
         * @return A String containing the json representing the available banks, or an error message
         */
            new AsyncTask<Void, Void, Void>() {
                @SuppressLint("StaticFieldLeak")
                @Override
                protected Void doInBackground(Void... voids) {
                    try {
                        ArrayList<Bank> bankArrayList = new ArrayList<>();
                        JSONObject banksJson = OBPRestClient.getBanksJson();
                        Gson gson = new Gson();
                        JSONArray banksJsonJSONArray = banksJson.getJSONArray("banks");
                        for (int i = 0; i < banksJsonJSONArray.length(); i++) {
                            bankArrayList.add(gson.fromJson(banksJsonJSONArray.getJSONObject(i).toString(), Bank.class));
                            Log.d("BANK:", bankArrayList.get(i).toString());
                        }
                        if (bankArrayList.size()!=0) {
                            //((ApplicationObj) getApplicationContext()).banks.addAll(bankArrayList);
                            //servicesHandlerViewModel.mObservableBanks.postValue(banks);
                            mObservableBanks.postValue(bankArrayList);
                            Log.d("OBPRetroClass.getAllBanks(): ","BanksAdded");
                            //sendMessage("BanksAdded");//handle this
                        } else {
                            Log.d("OBPRetroClass.getAllBanks(): ","BanksNOTAdded");//handle this
                            //sendMessage("BanksNOTAdded");//handle this
                        }

                    } catch (ExpiredAccessTokenException e) {
                        // login again / re-authenticate
//                        redoOAuth(); check if this was really working(I suppose it was not)
                    } catch (ObpApiCallFailedException | JSONException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    public void getAllAccounts(long bankID, String obpBankID){
        new AsyncTask<Void, Void, Void>() {
            @SuppressLint("StaticFieldLeak")
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    ArrayList<Bank> bankArrayList = new ArrayList<>();
//                    JSONObject banksJson = OBPRestClient.getAccounts(bankID, obpBankID);
                    JSONObject banksJson = OBPRestClient.getAccountsAtAllBanks(bankID);
                    Gson gson = new Gson();
                    Log.d("ACCOUNTS: ",banksJson.toString());
//                    JSONArray banksJsonJSONArray = banksJson.getJSONArray("banks");
//                    for (int i = 0; i < banksJsonJSONArray.length(); i++) {
//                        bankArrayList.add(gson.fromJson(banksJsonJSONArray.getJSONObject(i).toString(), Bank.class));
//                        Log.d("BANK:", bankArrayList.get(i).toString());
//                    }
//                    if (bankArrayList.size()!=0) {
//                        //((ApplicationObj) getApplicationContext()).banks.addAll(bankArrayList);
//                        //servicesHandlerViewModel.mObservableBanks.postValue(banks);
//                        mObservableBanks.postValue(bankArrayList);
//                        Log.d("OBPRetroClass.getAllBanks(): ","BanksAdded");
//                        //sendMessage("BanksAdded");//handle this
//                    } else {
//                        Log.d("OBPRetroClass.getAllBanks(): ","BanksNOTAdded");//handle this
//                        //sendMessage("BanksNOTAdded");//handle this
//                    }

                } catch (ExpiredAccessTokenException e) {
                    // login again / re-authenticate
//                        redoOAuth(); check if this was really working(I suppose it was not)
                } catch (ObpApiCallFailedException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

}
