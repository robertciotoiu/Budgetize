package com.example.robi.budgetize.backend.viewmodels.factories;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.robi.budgetize.ApplicationObj;
import com.example.robi.budgetize.backend.viewmodels.ServicesHandlerViewModel;

public class ServicesHandlerViewModelFactory implements ViewModelProvider.Factory {
    private ApplicationObj mAp;

    public ServicesHandlerViewModelFactory(ApplicationObj application) {
        this.mAp = application;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new ServicesHandlerViewModel(mAp, mAp.getRepository());
    }
}
