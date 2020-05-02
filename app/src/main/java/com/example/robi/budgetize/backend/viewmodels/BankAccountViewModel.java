package com.example.robi.budgetize.backend.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.robi.budgetize.DataRepository;
import com.example.robi.budgetize.data.localdatabase.entities.LinkedBank;
import com.example.robi.budgetize.data.remotedatabase.entities.Bank;

import java.util.List;

public class BankAccountViewModel extends AndroidViewModel implements DataRepository.OnBankDataChanged {
    //private final MutableLiveData<List<Bank>> mObservableBanks = new MutableLiveData<>();
    private final LiveData<List<LinkedBank>> mObservableLinkedBanks;
    private final DataRepository repository;

    public BankAccountViewModel(@NonNull Application application, DataRepository repository) {
        super(application);
        this.repository = repository;
        repository.addListener(this);
        mObservableLinkedBanks = repository.getLinkedBanksObserver();
    }

    @Override
    public void onBankDataChanged(List<Bank> bankList) {}

    public LiveData<List<LinkedBank>> linkedBankObserver() {
        return mObservableLinkedBanks;
    }

    public void getAccounts(){
        repository.getAccounts();
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

    public int deleteLinkedBank(long id){
        return repository.deleteLinkedBank(id);
    }
}
