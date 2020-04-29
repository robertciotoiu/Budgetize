package com.example.robi.budgetize.backend.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.robi.budgetize.DataRepository;
import com.example.robi.budgetize.data.remotedatabase.entities.Bank;

import java.util.List;

public class BankAccountViewModel extends AndroidViewModel implements DataRepository.OnBankDataChanged {
    private final MutableLiveData<List<Bank>> mObservableBanks = new MutableLiveData<>();
    private final DataRepository repository;

    public BankAccountViewModel(@NonNull Application application, DataRepository repository) {
        super(application);
        this.repository = repository;
        repository.addListener(this);
    }

    @Override
    public void onBankDataChanged(List<Bank> bankList) {

    }
}
