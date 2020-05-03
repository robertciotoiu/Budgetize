package com.example.robi.budgetize.backend.viewmodels;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.robi.budgetize.ApplicationObj;
import com.example.robi.budgetize.DataRepository;
import com.example.robi.budgetize.backend.services.DoOAuthService;
import com.example.robi.budgetize.data.localdatabase.entities.LinkedBank;
import com.example.robi.budgetize.data.remotedatabase.entities.Bank;
import com.example.robi.budgetize.data.remotedatabase.remote.oauth1.lib.OBPRestClient;

import java.util.List;

public class BankAccountViewModel extends AndroidViewModel implements DataRepository.OnBankDataChanged {
    //private final MutableLiveData<List<Bank>> mObservableBanks = new MutableLiveData<>();
    private final ApplicationObj applicationObj;
    private final LiveData<List<LinkedBank>> mObservableLinkedBanks;
    private final DataRepository repository;
    public static long lastClickedBankID=0;

    public BankAccountViewModel(@NonNull ApplicationObj application, DataRepository repository) {
        super(application);
        this.applicationObj = application;
        this.repository = repository;
        repository.addListener(this);
        mObservableLinkedBanks = repository.getLinkedBanksObserver();
    }

    public void doOBPOAuth() {
        Intent serviceIntent = new Intent(applicationObj.getApplicationContext(), DoOAuthService.class);
        serviceIntent.putExtra("doLogin", true);
        applicationObj.startService(serviceIntent);
    }

    public void unlinkBankAccount(long bankID){
        OBPRestClient.clearAccessToken(bankID);
        updateLinkStatus(bankID,"UNLINKED");
        //obpOAuthOK = false;
    }

    @Override
    public void onBankDataChanged(List<Bank> bankList) {}

    public LiveData<List<LinkedBank>> linkedBankObserver() {
        return mObservableLinkedBanks;
    }

    public void getAccounts(long bankID, String obpBankID){
        repository.getAccounts(bankID, obpBankID);
    }

    //Operations with LinkedBanks
    public long addLinkedBank(LinkedBank linkedBank) {
        return repository.addLinkedBank(linkedBank);
    }

    public LiveData<List<LinkedBank>> getAllLinkedBanks(){
        return repository.getAllLinkedBanks();
    }

    public LiveData<LinkedBank> getLinkedBank(long id){
        return repository.getLinkedBank(id);
    }

    public String getLinkedBankStatus(long id){
        return repository.getLinkedBankStatus(id);
    }

    public String getLinkedBankOBPID(long id){
        return repository.getLinkedBankOBPID(id);
    }

    public long updateLinkStatus(long id, String link_status){
        return repository.updateLinkStatus(id,link_status);
    }

    public int deleteLinkedBank(long id){
        return repository.deleteLinkedBank(id);
    }
}
