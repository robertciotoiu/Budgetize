package com.example.robi.budgetize.backend.viewmodels;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.robi.budgetize.ApplicationObj;
import com.example.robi.budgetize.backend.services.DoOAuthService;
import com.example.robi.budgetize.data.DataRepository;
import com.example.robi.budgetize.data.localdatabase.entities.AccountTransaction;
import com.example.robi.budgetize.data.localdatabase.entities.BankAccount;
import com.example.robi.budgetize.data.localdatabase.entities.CategoryObject;
import com.example.robi.budgetize.data.localdatabase.entities.IEObject;
import com.example.robi.budgetize.data.localdatabase.entities.LinkedBank;
import com.example.robi.budgetize.data.localdatabase.entities.WalletLinkedBankAccounts;
import com.example.robi.budgetize.data.localdatabase.enums.TransactionOccurrenceEnum;
import com.example.robi.budgetize.data.remotedatabase.remote.OBPlib.OBPRestClient;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    //TODO: delete this! DB debug purposes
    public DataRepository getRepo(){
        return repository;
    }


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
                autoTxnSync(transactionList);
            }
        });
    }

    public void appFirstStartLogicInit(){
        // Get all the linked banks from the local database
        linkedBanksList = this.getAllLinkedBanks();
        // Start observing changes on linked_banks table
        linkedBanksList.observeForever(linkedBanks -> {
            for (LinkedBank linkedBank : linkedBanks) {
                // For every linked bank, we retrieve the consumer and provider
                // in order to be able to request from the linked bank
                // new user's transactions
                if (!OBPRestClient.consumers.containsKey(linkedBank.getId())) {
                    OBPRestClient.retrieveConsumerAndProvider(linkedBank.getId());
                }
                // We retrieve from OBP API, new user's accounts
                // from the already linked bank accounts
                // and we insert them into the database
                this.getAccounts(linkedBank.getId());
            }
        });
        // We retrieve the bank accounts from the database
        bankAccounts = this.getAllBankAccounts();
        // Start observing changes on bank_accounts table
        bankAccounts.observeForever(accounts -> {
            // Refresh list of bank accounts
            accountList.clear();
            accountList.addAll(accounts);
            // Get all the new transactions for each bank account
            // and we insert them into the database
                for (BankAccount bankAccount : accountList) {
                    getTransactions(bankAccount.getInternal_bank_id(), bankAccount.getBank_id(), bankAccount.getId());
                }
        });
    }

    public void doOBPOAuth() {
        Intent serviceIntent = new Intent(applicationObj.getApplicationContext(), DoOAuthService.class);
        serviceIntent.putExtra("doLogin", true);
        applicationObj.startService(serviceIntent);
    }

    //TODO: also here we need to remove all transactions from a bank account
    public void unlinkBankAccount(long bankID){
        int deleteTransactionsStatus = 0;
        String deleteTransactions = "";
        OBPRestClient.clearAccessToken(bankID);
        String obpBankIDToRemove = "";
        for(BankAccount bankAccount:accountList){
            if(bankAccount.getInternal_bank_id()==bankID){
                deleteTransactionsStatus = deleteAllTransactionsFromABankAccount(bankAccount.getId());
                deleteTransactions = deleteTransactions +","+deleteTransactionsStatus;
                obpBankIDToRemove = bankAccount.getBank_id();
//                break;
            }
        }
        Log.d("deleteAllBankAccountsTransactionsFromALinkedBank ",deleteTransactions+" Transactions DELETED FROM DB!");
        if(!obpBankIDToRemove.contentEquals("")) {
            //firstly delete all transactions

            int deleteAccountsStatus = deleteAllBankAccountsFromALinkedBank(obpBankIDToRemove);
            Log.d("deleteAllBankAccountsFromALinkedBank ",deleteAccountsStatus+" BANKACCOUNTS DELETED FROM DB!");
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


    @SuppressWarnings("WeakerAccess")
    //Operations with bank transactions:
    public void getTransactions(long bankID, String obpBankID, String accountID){
        repository.getTransactions(bankID, obpBankID, accountID, repository);
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

    public LiveData<List<BankAccount>> getBankAccountsFromALinkedBank(long internal_bank_id) {
        return repository.getBankAccountsFromALinkedBank(internal_bank_id);
    }

    public int deleteBankAccount(String id) {
        return repository.deleteBankAccount(id);
    }

    public int deleteAllBankAccountsFromALinkedBank(String bank_id){
        return repository.deleteAllBankAccountsFromALinkedBank(bank_id);
    }

    //Operations with AccountTransactions

    public LiveData<List<AccountTransaction>> getAllTransactions() {
        return repository.getAllTransactions();
    }

    public LiveData<List<AccountTransaction>> getAllAccountTransactions(String bank_account_id) {
        return repository.getAllTransactionsFromABankAccount(bank_account_id);
    }

    public int deleteAllTransactionsFromABankAccount(String bank_account_id){
        return repository.deleteAllTransactionsFromABankAccount(bank_account_id);
    }

    //Operations with WalletLinkedBankAccounts
    public long insertWalletLinkedBankAccount(WalletLinkedBankAccounts walletLinkedBankAccounts) {
        return repository.insertWalletLinkedBankAccount(walletLinkedBankAccounts);
    }

    public long getNrOfLinkedBankFromWallet(long wallet_id, long linked_banked){
        return repository.getNrOfLinkedBankFromWallet(wallet_id,linked_banked);
    }

    public WalletLinkedBankAccounts getLinkedAccountFromWallet(long wallet_id, String bank_account_id){
        return repository.getLinkedAccountFromWallet(wallet_id,bank_account_id);
    }

    //test
    public List<BankAccount> getBankAccountsFromALinkedBankNOTLIVEDATA(long bank_id){
        return repository.getBankAccountsFromALinkedBankNOTLIVEDATA(bank_id);
    }

    public boolean isSyncLinkedBank(long wallet_id, LinkedBank linkedBank){
        return repository.getNrOfLinkedBankFromWallet(wallet_id, linkedBank.getId()) == repository.getNrOfAccountsFromLinkedBank(linkedBank.getId());
    }

    public boolean isSyncLinkedBank(long wallet_id, long linkBankID){
        return repository.getNrOfLinkedBankFromWallet(wallet_id, linkBankID) == repository.getNrOfAccountsFromLinkedBank(linkBankID);
    }

    public boolean isSyncBankAccount(long wallet_id, BankAccount bankAccount){
        return getLinkedAccountFromWallet(wallet_id, bankAccount.getId()) != null;
    }

    //logic
    public boolean syncBankAccount(long wallet_id, BankAccount bankAccount) {
        WalletLinkedBankAccounts walletLinkedBankAccounts = getLinkedAccountFromWallet(wallet_id, bankAccount.getId());
        if(walletLinkedBankAccounts==null){
            //TODO: then sync it
            long status = insertWalletLinkedBankAccount(new WalletLinkedBankAccounts(wallet_id, bankAccount.getInternal_bank_id(), bankAccount.getId()));
            if(status >0) {
                startSyncTransactionsBankAcc(wallet_id, bankAccount);
                return true;
            }
            else
                return false;
        }else{
            return true;
        }
    }

    private void startSyncTransactionsBankAcc(long wallet_id, BankAccount bankAccount) {
        findOrCategoryAndSyncTxns(wallet_id, bankAccount);
    }

    private void findOrCategoryAndSyncTxns(long wallet_id, BankAccount bankAccount) {
        long category_id = 0;
        long status = 0;
        if((category_id = repository.getCategoryID(wallet_id,bankAccount.getId()))!=0){
            status = 1;
        }else {
            CategoryObject categoryObject = new CategoryObject(bankAccount.getLabel(), bankAccount.getAccount_type(), wallet_id, bankAccount.getId());
            category_id = categoryObject.getCategory_id();
            status = repository.addCategory(categoryObject);//MainActivity.myDatabase.categoryDao().addCategory(categoryObject);
        }
        if(status>0) syncTxns(wallet_id, bankAccount, category_id);
    }

    private void syncTxns(long wallet_id, BankAccount bankAccount, long category_id) {
        List<IEObject> ieObjects = new ArrayList<>();
        List<AccountTransaction> transactions = repository.getAllTransactionsFromABankAccountNOTLIVEDATA(bankAccount.getId());
        for(AccountTransaction transaction:transactions){
            ieObjects.add(new IEObject(wallet_id, transaction.getDescription(),transaction.getTxn_value(), category_id, 1, transaction.getCompleted(), TransactionOccurrenceEnum.Never.toString(), transaction.getId(),transaction.getTxn_currency()));
        }
        repository.insertAllIEObjects(ieObjects);
    }

    public boolean syncLinkedBankAccounts(long wallet_id, LinkedBank linkedBank) {
        //TODO: getListOfBankAccounts from this linkedBank
        //TODO: insert all BankAccounts which are not in the wallet_linked_bankaccounts table
        List<BankAccount> bankAccounts = repository.getBankAccountsFromALinkedBankNOTLIVEDATA(linkedBank.getId());
        for (BankAccount bankAccount : bankAccounts) {
            syncBankAccount(wallet_id, bankAccount);
        }
        return true;
    }

    //autoTxnSync
    public void autoTxnSync(List<AccountTransaction> transactionList){
        Set<String> bankAccountIDs = new HashSet<String>();
        for(AccountTransaction accountTransaction:transactionList){
            bankAccountIDs.add(accountTransaction.getBank_account_id());
        }

        Set<WalletLinkedBankAccounts> walletLinkedBankAccounts = new HashSet<>();

        for(String bankAccountID:bankAccountIDs){
            walletLinkedBankAccounts.retainAll(repository.getLinkedAccountFromBankAccount(bankAccountID));
        }

        for(WalletLinkedBankAccounts walletLinkedBankAccount:walletLinkedBankAccounts){
            findOrCategoryAndSyncTxns(walletLinkedBankAccount.getWallet_id(), repository.getBankAccount(walletLinkedBankAccount.getBank_account_id()));
        }
//        watchAccountTransaction();
//        onChanged();
        //add for all from wallet_linked_bank_accounts with wallet_id and bank_account_id;
    }
}
