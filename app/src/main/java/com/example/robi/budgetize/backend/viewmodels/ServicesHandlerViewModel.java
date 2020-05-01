package com.example.robi.budgetize.backend.viewmodels;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.robi.budgetize.ApplicationObj;
import com.example.robi.budgetize.DataRepository;
import com.example.robi.budgetize.backend.services.DoOAuthService;
import com.example.robi.budgetize.backend.services.DownloadBankImagesService;
import com.example.robi.budgetize.backend.services.RetrieveBanksService;
import com.example.robi.budgetize.backend.viewmodels.helpers.BankImagesDownloader;
import com.example.robi.budgetize.data.remotedatabase.entities.Bank;
import com.example.robi.budgetize.data.remotedatabase.remote.OBPRetroClass;

import java.util.ArrayList;
import java.util.List;

public class ServicesHandlerViewModel extends AndroidViewModel {
    //App objs
    ApplicationObj applicationObj;

    //OBP
    public boolean obpOAuthOK = false;
    private boolean onFirstCreation = false;

    //LiveData
    MutableLiveData<Boolean> showBankAccountNeedActions = new MutableLiveData<Boolean>();
    public static final MutableLiveData<List<Bank>> mObservableBanks = new MutableLiveData<>();

    //RetroClasses uses to communicate with APIs
    private OBPRetroClass obpRetroClass = new OBPRetroClass();

    //Utilitary class
    private BankImagesDownloader bankImagesDownloader = new BankImagesDownloader();

    private static List<Bank> banksList = new ArrayList<>();

    //Service messages handler
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
                    getAllBanks();
//                } else if (serviceResponse.contentEquals("BanksAdded")) {
//                    Log.d("GetBanks successfull", "!");
//                    //Run this code only at start of the app
//
//                } else if (serviceResponse.contentEquals("BanksNOTAdded")) {
//                    Log.d("GetBanks NOT successfull", "!");
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

        mObservableBanks.observeForever(new Observer<List<Bank>>() {
            @Override
            public void onChanged(List<Bank> banks) {
                banksList.clear();
                banksList.addAll(banks);
                if (!onFirstCreation) {
                    syncAllImages();
                }
            }
        });
    }


    //methods available to UI
    //Refactored: this is triggering the bank account linking
    public void startServices() {
        //1.Check if Auth available
        checkOBPOAuthStatus();
    }

    public void doOBPOAuth() {
        Intent serviceIntent = new Intent(applicationObj.getApplicationContext(), DoOAuthService.class);
        serviceIntent.putExtra("doLogin", true);
        applicationObj.startService(serviceIntent);
    }

    public List<Bank> getAllBanksFromUI(){
        return banksList;
    }



    //1st Service
    private void checkOBPOAuthStatus() {
        Intent serviceIntent = new Intent(applicationObj.getApplicationContext(), DoOAuthService.class);
        serviceIntent.putExtra("checkStatus", true);
        applicationObj.startService(serviceIntent);
    }

    //2nd "service"
    private void getAllBanks() {
        obpRetroClass.getAllBanks(mObservableBanks);
    }

    //3rd "service"
    private void syncAllImages(){
        bankImagesDownloader.doSync(banksList);
    }

    //    2nd Service
    private void getAllBanksService() {
        if (obpOAuthOK) {
            //if it is available we can download banks
            Intent serviceIntent = new Intent(applicationObj.getApplicationContext(), RetrieveBanksService.class);
            serviceIntent.putExtra("getAllBanks", true);
            applicationObj.startService(serviceIntent);
        }
    }

    //3nd Service
    private void syncAllImagesService() {
        Intent serviceIntent = new Intent(applicationObj.getApplicationContext(), DownloadBankImagesService.class);
        serviceIntent.putExtra("syncImages", true);
        applicationObj.startService(serviceIntent);
    }

}
