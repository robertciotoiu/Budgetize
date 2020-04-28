package com.example.robi.budgetize.backend.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.robi.budgetize.ApplicationObj;

public class BankAccountViewModelFactory implements ViewModelProvider.Factory {
    private ApplicationObj mAp;

    public BankAccountViewModelFactory(ApplicationObj application) {
        this.mAp = application;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new BankAccountViewModel(mAp, mAp.getRepository());
    }
}
