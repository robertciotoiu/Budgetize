package com.example.robi.budgetize.backend.viewmodels.factories;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.robi.budgetize.ApplicationObj;
import com.example.robi.budgetize.backend.viewmodels.MainActivityViewModel;

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
