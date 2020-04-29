package com.example.robi.budgetize.backend.viewmodels;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.robi.budgetize.ApplicationObj;
import com.example.robi.budgetize.DataRepository;
import com.example.robi.budgetize.backend.services.DoOAuthService;
import com.example.robi.budgetize.backend.services.DownloadBankImagesService;
import com.example.robi.budgetize.backend.services.RetrieveBanksService;
import com.example.robi.budgetize.data.remotedatabase.entities.Bank;

import java.util.List;

public class ServicesHandlerViewModel extends AndroidViewModel {

    ApplicationObj applicationObj;

    //OBP
    public boolean obpOAuthOK = false;
    private boolean onFirstCreation = false;
    MutableLiveData<Boolean> showBankAccountNeedActions = new MutableLiveData<Boolean>();
    public final MutableLiveData<List<Bank>> mObservableBanks = new MutableLiveData<>();


    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        // Handling messages from Services
        @Override
        public void onReceive(Context context, Intent intent) {
            String serviceResponse = intent.getStringExtra("message");
            if (serviceResponse != null) {
                if (serviceResponse.contentEquals("needAuth")) {
                    showBankAccountNeedActions.postValue(true);
                } else if (serviceResponse.contentEquals("Authorized")) {
                    Log.d("Authorized successfull", "!");
                    obpOAuthOK = true;
                    getAllBanksService();
                } else if (serviceResponse.contentEquals("BanksAdded")) {
                    Log.d("GetBanks successfull", "!");
                    //Run this code only at start of the app
                    if(!onFirstCreation) {
                        syncAllImagesService();
                    }
                } else if (serviceResponse.contentEquals("BanksNOTAdded")) {
                    Log.d("GetBanks NOT successfull", "!");
                } else if (serviceResponse.contentEquals("syncImagesCompleted")) {
                    onFirstCreation = true;
                    Log.d("syncImages Completed", "!");
                }
            } else {
                Log.d("Programming error in " + this.getClass().getName() + " ", "EMPTY RESPONSE FROM " + intent.getClass().getName());
            }
        }
    };

    public ServicesHandlerViewModel(@NonNull ApplicationObj application, DataRepository repository) {
        super(application);
        this.applicationObj = application;
        LocalBroadcastManager.getInstance(ApplicationObj.getAppContext())
                .registerReceiver(mMessageReceiver,
                        new IntentFilter("my-integer"));
    }




    //ORDER OF SERVICES(Related to OBP API):
    //1st This start all the services
    public void startServices() {
        //1.Check if Auth available
        checkOBPOAuthStatus();
    }

    //1st Service
    private void checkOBPOAuthStatus() {
        Intent serviceIntent = new Intent(applicationObj.getApplicationContext(), DoOAuthService.class);
        serviceIntent.putExtra("checkStatus", true);
        applicationObj.startService(serviceIntent);
    }

    //2nd Service
    private void getAllBanksService() {
        if (obpOAuthOK) {
            //if it is available we can download banks
            Intent serviceIntent = new Intent(applicationObj.getApplicationContext(), RetrieveBanksService.class);
            serviceIntent.putExtra("getAllBanks", true);
            applicationObj.startService(serviceIntent);
        }
    }
    //2nd service mvvm manner


    //3nd Service
    private void syncAllImagesService() {
        Intent serviceIntent = new Intent(applicationObj.getApplicationContext(), DownloadBankImagesService.class);
        serviceIntent.putExtra("syncImages", true);
        applicationObj.startService(serviceIntent);
    }

    public void doOBPOAuth() {
        Intent serviceIntent = new Intent(applicationObj.getApplicationContext(), DoOAuthService.class);
        serviceIntent.putExtra("doLogin", true);
        applicationObj.startService(serviceIntent);
    }


}
