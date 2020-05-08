package com.example.robi.budgetize.backend.viewmodels;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.robi.budgetize.ApplicationObj;
import com.example.robi.budgetize.DataRepository;
import com.example.robi.budgetize.backend.services.DoOAuthService;
import com.example.robi.budgetize.data.localdatabase.entities.AccountTransaction;
import com.example.robi.budgetize.data.localdatabase.entities.BankAccount;
import com.example.robi.budgetize.data.localdatabase.entities.LinkedBank;
import com.example.robi.budgetize.data.remotedatabase.remote.oauth1.lib.OBPRestClient;

import java.util.ArrayList;
import java.util.List;

public class BankAccountViewModel extends AndroidViewModel{// implements DataRepository.OnBankDataChanged {
    //private final MutableLiveData<List<Bank>> mObservableBanks = new MutableLiveData<>();
    private final ApplicationObj applicationObj;
//    private final LiveData<List<LinkedBank>> mObservableLinkedBanks;
    private final DataRepository repository;
    public static final MutableLiveData<List<BankAccount>> mObservableBankAccounts = new MutableLiveData<>();
    public static final MutableLiveData<List<AccountTransaction>> mObservableTransactions = new MutableLiveData<>();
    public static long lastClickedBankID=0;

    public static List<BankAccount> accountList = new ArrayList<>();
    public static List<AccountTransaction> transactionList = new ArrayList<>();


    //test
    LiveData<List<LinkedBank>> linkedBanksList;//moved here from appFirstStartLogicInit()
    LiveData<List<BankAccount>> bankAccounts;

    public BankAccountViewModel(@NonNull ApplicationObj application, DataRepository repository) {
        super(application);
        this.applicationObj = application;
        this.repository = repository;
//        repository.addListener(this);
//        mObservableLinkedBanks = repository.getLinkedBanksObserver();
        mObservableTransactions.observeForever(new Observer<List<AccountTransaction>>() {
            @Override
            public void onChanged(List<AccountTransaction> transactions) {
                transactionList.clear();
                transactionList.addAll(transactions);
            }
        });
    }

    public void appFirstStartLogicInit(){
        linkedBanksList = this.getAllLinkedBanks();
        linkedBanksList.observeForever(linkedBanks -> {
            for (LinkedBank linkedBank : linkedBanks) {
                //also get all accounts from linked bank accounts
                this.getAccounts(linkedBank.getId());
                if (!OBPRestClient.consumers.containsKey(linkedBank.getId())) {
                    OBPRestClient.retrieveConsumer(linkedBank.getId());
                }
                if (!OBPRestClient.providers.containsKey(linkedBank.getId())) {
                    OBPRestClient.retrieveProvider(linkedBank.getId());
                }
            }
        });

        bankAccounts = this.getAllBankAccounts();
        bankAccounts.observeForever(new Observer<List<BankAccount>>() {
            @Override
            public void onChanged(List<BankAccount> accounts) {
                accountList.clear();
                accountList.addAll(accounts);
                Log.d("ACCOUNTSCHANGED: ","BANKACCOUNTVIEWMODEL");
                //sync with database


                //retrieve transactions only at 3 minutes or restart of the app
//                if(System.currentTimeMillis()-ApplicationObj.lastTransactionsDownload>=60000*3) {
                    for (BankAccount bankAccount : accountList) {
                        getTransactions(bankAccount.getInternal_bank_id(), bankAccount.getBank_id(), bankAccount.getId());
                    }
//                    ApplicationObj.lastTransactionsDownload = System.currentTimeMillis();
//                }
            }
        });
    }

    public void doOBPOAuth() {
        Intent serviceIntent = new Intent(applicationObj.getApplicationContext(), DoOAuthService.class);
        serviceIntent.putExtra("doLogin", true);
        applicationObj.startService(serviceIntent);
    }

    public void unlinkBankAccount(long bankID){
        OBPRestClient.clearAccessToken(bankID);
        String obpBankIDToRemove = "";
        for(BankAccount bankAccount:accountList){
            if(bankAccount.getInternal_bank_id()==bankID){
                obpBankIDToRemove = bankAccount.getId();
                break;
            }
        }
        if(!obpBankIDToRemove.contentEquals("")) {
            deleteAllBankAccountsFromALinkedBank(obpBankIDToRemove);
            Log.d("deleteAllBankAccountsFromALinkedBank","BANKACCOUNTS DELETED FROM DB!");
        }else{
            Log.d("deleteAllBankAccountsFromALinkedBank","bank was not found for delete");
        }
        updateLinkStatus(bankID,"UNLINKED");
        //obpOAuthOK = false;
    }

//    @Override
//    public void onBankDataChanged(List<Bank> bankList) {}

//    public LiveData<List<LinkedBank>> linkedBankObserver() {
//        return mObservableLinkedBanks;
//    }

    //operations with bank accounts:
    public void getAccounts(long bankID){
        repository.getAccounts(bankID, repository);
    }

    //Operations with bank transactions:
    public void getTransactions(long bankID, String obpBankID, String accountID){
        repository.getTransactions(bankID, obpBankID, accountID, mObservableTransactions);
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

    //Operations with BankAccounts
    public long addBankAccount(BankAccount bankAccount) {
        return repository.addBankAccount(bankAccount);
    }

    public LiveData<List<BankAccount>> getAllBankAccounts() {
        return repository.getAllBankAccounts();
    }

    public LiveData<List<BankAccount>> getBankAccountsFromALinkedBank(String bank_id) {
        return repository.getBankAccountsFromALinkedBank(bank_id);
    }

    public int deleteBankAccount(String id) {
        return repository.deleteBankAccount(id);
    }

    public int deleteAllBankAccountsFromALinkedBank(String bank_id){
        return repository.deleteAllBankAccountsFromALinkedBank(bank_id);
    }
}
