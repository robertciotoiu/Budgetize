package com.example.robi.budgetize.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.robi.budgetize.ApplicationObj;

public class MainActivityViewModelFactory implements ViewModelProvider.Factory {
    private ApplicationObj mAp;

    public MainActivityViewModelFactory(ApplicationObj application) {
        this.mAp = application;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new MainActivityViewModel(mAp, mAp.getRepository());
    }
}
