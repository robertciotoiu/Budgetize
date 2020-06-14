package com.example.robi.budgetize.backend.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.robi.budgetize.data.DataRepository;
import com.example.robi.budgetize.data.localdatabase.entities.CategoryObject;
import com.example.robi.budgetize.data.localdatabase.entities.IEObject;
import com.example.robi.budgetize.data.localdatabase.entities.Wallet;

import java.util.List;

public class MainActivityViewModel extends AndroidViewModel implements DataRepository.OnDataChangedRepositoryListener {

    private final DataRepository repository;
    private final MutableLiveData<List<Wallet>> mObservableWallets = new MutableLiveData<>();
    private final MutableLiveData<List<CategoryObject>> mObservableCategories = new MutableLiveData<>();
    private final MutableLiveData<List<IEObject>> mObservableIEs = new MutableLiveData<>();
    public int lastWalletPosition = 0;
    public boolean firstAnimation = true;


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
//        mObservableWallets.postValue(repository.getAllWallets().getValue());
    }

    @Override
    public void onIEDataChanged(List<IEObject> ieObjects) {
        //TODO modify data here before "sending" it to the UI
        mObservableIEs.postValue(ieObjects);
//        mObservableWallets.postValue(repository.getAllWallets().getValue());
//        mObservableCategories.postValue(repository.getAllCategories().getValue());
    }

    public boolean checkIfLinkedBankExists(){
        if(repository.noLinkedBanks()>0)
            return true;
        else
            return false;
    }

    public long updateCurrency(long wallet_id, String currency) {
        return repository.updateCurrency(wallet_id, currency);
    }

    public LiveData<List<Wallet>> getAllWallets() {
        return repository.getAllWallets();
    }

    public void deleteWallet(Wallet wallet) {
        repository.deleteWallet(wallet);
    }

    public LiveData<List<CategoryObject>> getAllCategories() {
        return repository.getAllCategories();
    }

    public long addCategory(CategoryObject categoryObject) {
        return repository.addCategory(categoryObject);
    }

    public LiveData<CategoryObject> getCategoryByID(long category_id) {
        return repository.getCategoryByID(category_id);
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

    public void deleteCategory(long category_id) {
        repository.deleteCategory(category_id);
    }

    public void deleteIE(long ieID) {
        repository.deleteIE(ieID);
    }

    public double getCategoryIESUM(long category_id) {
        return repository.getCategoryIESUM(category_id);
    }

    public List<IEObject> getCategorysIE(long category_id) {
        return repository.getCategorysIE(category_id);
    }

    public LiveData<List<IEObject>> getAllIEofAWalletWithoutCategoriesAssigned(long walletID){
        return repository.getAllIEofAWalletWithoutCategoriesAssigned(walletID);
    }

    public LiveData<List<IEObject>> getAllIE(){
        return repository.getAllIE();
    }

    public double getIE(long wallet_id) {
        return repository.getIE(wallet_id);
    }


}
