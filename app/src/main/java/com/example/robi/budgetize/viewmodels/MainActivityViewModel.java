package com.example.robi.budgetize.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.robi.budgetize.DataRepository;
import com.example.robi.budgetize.data.localdatabase.entities.CategoryObject;
import com.example.robi.budgetize.data.localdatabase.entities.IEObject;
import com.example.robi.budgetize.data.localdatabase.entities.Wallet;

import java.util.List;

public class MainActivityViewModel extends AndroidViewModel implements DataRepository.OnDataChangedRepositoryListener {

    private final DataRepository repository;
    private final MutableLiveData<List<Wallet>> mObservableWallets = new MutableLiveData<>();
    private final MutableLiveData<List<CategoryObject>> mObservableCategories = new MutableLiveData<>();


    public MainActivityViewModel(@NonNull Application application, DataRepository repository) {
        super(application);
        this.repository = repository;
        repository.addListener(this);
        //mObservableWallets = repository.getWallets();
    }


    @NonNull
    @Override
    public <T extends Application> T getApplication() {
        return super.getApplication();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }

    @Override
    public void onWalletDataChanged(List<Wallet> walletList) {
        //TODO modify data here before "sending" it to the UI
        mObservableWallets.postValue(walletList);
    }

    @Override
    public void onCategoryDataChanged(List<CategoryObject> categoryObjects) {
        //TODO modify data here before "sending" it to the UI
        mObservableCategories.postValue(categoryObjects);
    }

    public LiveData<List<Wallet>> getAllWallets() {
        return repository.getAllWallets();
    }

    public void deleteWallet(Wallet wallet) {
        repository.deleteWallet(wallet);
    }

    public LiveData<List<CategoryObject>> getAllCategories() {
        return mObservableCategories;
    }

    public long addCategory(CategoryObject categoryObject) {
        return repository.addCategory(categoryObject);
    }

    public LiveData<CategoryObject> getCategoryByName(long currentWalletID, String categoryName) {
        return repository.getCategoryByName(currentWalletID, categoryName);
    }

    public long addWallet(Wallet wallet) {
        return repository.addWallet(wallet);
    }

    public LiveData<Wallet> getWalletById(long status) {
        return repository.getWalletById(status);
    }

    public long addIEObject(IEObject ieObject) {
        return repository.addIEObject(ieObject);
    }

    public LiveData<IEObject> getIEObject(long ieID) {
        return repository.getIEObject(ieID);
    }

    public LiveData<List<CategoryObject>> getAllCategoriesOfAWallet(long wallet_id) {
        return repository.getAllCategoriesOfAWallet(wallet_id);
    }

    public void deleteCategory(long walletID, String categoryName) {
        repository.deleteCategory(walletID,categoryName);
    }

    public void deleteIE(long walletID, long ieID) {
        repository.deleteIE(walletID,ieID);
    }

    public double getCategoryIESUM(long wallet_id, String name) {
        return repository.getCategoryIESUM(wallet_id,name);
    }

    public List<IEObject> getCategorysIE(long walletID, String name) {
        return repository.getCategorysIE(walletID,name);
    }

}
