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
import com.example.robi.budgetize.backend.viewmodels.helpers.BankImagesDownloader;
import com.example.robi.budgetize.data.DataRepository;
import com.example.robi.budgetize.data.remotedatabase.entities.bank.Bank;

import java.util.ArrayList;
import java.util.List;

public class ServicesHandlerViewModel extends AndroidViewModel {
    public final MutableLiveData<List<Bank>> mObservableAvailableBanks = new
            MutableLiveData<>();
    public static List<Bank> banksList = new ArrayList<>();
    private static boolean onFirstCreation = false;
    private final DataRepository repository;
    private final BankImagesDownloader bankImagesDownloader = new BankImagesDownloader();

    public ServicesHandlerViewModel(@NonNull ApplicationObj application, DataRepository repository) {
        super(application);
        //Service messages handler
        // Handling messages from Services
        BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
            // Handling messages from Services
            @Override
            public void onReceive(Context context, Intent intent) {
                String serviceResponse = intent.getStringExtra("message");
                if (serviceResponse != null) {
                    if (serviceResponse.contentEquals("Authorized")) {
                        Log.d(this.getClass().toString(), "Authorized successful!");
                        getAllAvailableBanks();
                    } else if (serviceResponse.contentEquals("syncImagesCompleted")) {
                        onFirstCreation = true;
                        Log.d(this.getClass().toString(), "syncImages Completed!");
                    }
                } else {
                    Log.d("Programming error in " + this.getClass().getName() + " ", "EMPTY RESPONSE FROM " + intent.getClass().getName());
                }
            }
        };
        LocalBroadcastManager.getInstance(ApplicationObj.getAppContext())
                .registerReceiver(mMessageReceiver,
                        new IntentFilter("my-integer"));

        this.repository = repository;
        mObservableAvailableBanks.observeForever(banks -> {
            // Clean bank list
            banksList.clear();
            // Add all banks
            banksList.addAll(banks);
            //Test purpose message
            Log.d("BANKSCHANGED: ", "SERVICEHANDLERVIEWMODEL");
            // Perform this action only at the start of the application
            if (!onFirstCreation) {
                // Download all bank logos
                syncAllImages();
                onFirstCreation = true;
            }
        });
    }

    //1st "service"
    public void getAllAvailableBanks() {
        repository.getAllAvailableBanks(mObservableAvailableBanks);
    }

    //2nd "service"
    public void syncAllImages() {
        Thread syncImagesThread = new Thread(() -> bankImagesDownloader.doSync(banksList));
        syncImagesThread.start();
    }

    public List<Bank> getAllBanksFromUI() {
        return banksList;
    }
}
