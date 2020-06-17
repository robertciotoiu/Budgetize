package com.example.robi.budgetize.data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.robi.budgetize.data.localdatabase.LocalRoomDatabase;
import com.example.robi.budgetize.data.localdatabase.dao.AccountTransactionDao;
import com.example.robi.budgetize.data.localdatabase.dao.BankAccountDao;
import com.example.robi.budgetize.data.localdatabase.dao.CategoryDao;
import com.example.robi.budgetize.data.localdatabase.dao.IEObjectDao;
import com.example.robi.budgetize.data.localdatabase.dao.LinkedBankDao;
import com.example.robi.budgetize.data.localdatabase.dao.WalletDao;
import com.example.robi.budgetize.data.localdatabase.dao.WalletLinkedBankAccountsDao;
import com.example.robi.budgetize.data.localdatabase.entities.AccountTransaction;
import com.example.robi.budgetize.data.localdatabase.entities.BankAccount;
import com.example.robi.budgetize.data.localdatabase.entities.CategoryObject;
import com.example.robi.budgetize.data.localdatabase.entities.IEObject;
import com.example.robi.budgetize.data.localdatabase.entities.LinkedBank;
import com.example.robi.budgetize.data.localdatabase.entities.Wallet;
import com.example.robi.budgetize.data.localdatabase.entities.WalletLinkedBankAccounts;
import com.example.robi.budgetize.data.remotedatabase.entities.bank.Bank;
import com.example.robi.budgetize.data.remotedatabase.remote.OBPRetroClass;

import java.util.List;

public class DataRepository implements WalletDao, CategoryDao, IEObjectDao, LinkedBankDao, BankAccountDao, AccountTransactionDao, WalletLinkedBankAccountsDao {

    private static DataRepository sInstance;

    private final LocalRoomDatabase mDatabase;
    private MediatorLiveData<List<Wallet>> ObservableWallet;
    private MediatorLiveData<List<CategoryObject>> ObservableCategory;
    private MediatorLiveData<List<IEObject>> ObservableIE;
    private MediatorLiveData<List<LinkedBank>> ObservableLinkedBanks;
    //TODO: MediatorLiveData for IE and Category

    //TODO: delete this! DB debug purposes
    public LocalRoomDatabase getDB() {
        return mDatabase;
    }

    //RetroClasses uses to communicate with APIs
    private OBPRetroClass obpRetroClass = new OBPRetroClass();

    private OnDataChangedRepositoryListener listener;
//    private OnBankDataChanged bankDataListener;

    private DataRepository(final LocalRoomDatabase database) {
        mDatabase = database;
        ObservableWallet = new MediatorLiveData<>();
        ObservableCategory = new MediatorLiveData<>();
        ObservableIE = new MediatorLiveData<>();
        ObservableLinkedBanks = new MediatorLiveData<>();

        ObservableWallet.addSource(mDatabase.walletDao().getAllWallets(),
                wallets -> {
                    if (mDatabase.getDatabaseCreated().getValue() != null) {
                        if (listener != null)
                            listener.onWalletDataChanged(wallets);
//                        ObservableWallet.postValue(wallets);
                    }
                });

        ObservableCategory.addSource(mDatabase.categoryDao().getAllCategories(),
                categories -> {
                    if (mDatabase.getDatabaseCreated().getValue() != null) {
                        if (listener != null)
                            listener.onCategoryDataChanged(categories);
                        //ObservableCategory.postValue(categories);
                    }
                });

        ObservableIE.addSource(mDatabase.ieoDao().getAllIE(),
                ieObjects -> {
                    if (mDatabase.getDatabaseCreated().getValue() != null)
                        if (listener != null)
                            listener.onIEDataChanged(ieObjects);
                });

        ObservableLinkedBanks.addSource(mDatabase.linkedBankDao().getAllLinkedBanks(),
                linkedBanks -> {
                    if (mDatabase.getDatabaseCreated().getValue() != null) {
                        ObservableLinkedBanks.setValue(linkedBanks);
                    }
                });
    }

    public static DataRepository getInstance(final LocalRoomDatabase database) {
        if (sInstance == null) {
            synchronized (DataRepository.class) {
                if (sInstance == null) {
                    sInstance = new DataRepository(database);
                }
            }
        }
        return sInstance;
    }

    //WALLETDAO

    @Override
    public void insertAllWallets(List<Wallet> wallets) {
        mDatabase.walletDao().insertAllWallets(wallets);
    }

    @Override
    public long addWallet(Wallet wallet) {
        return mDatabase.walletDao().addWallet(wallet);
    }

    @Override
    public String getWalletsCurrency(long id){
        return mDatabase.walletDao().getWalletsCurrency(id);
    }

    @Override
    public long updateCurrency(long wallet_id, String currency) {
        return mDatabase.walletDao().updateCurrency(wallet_id, currency);
    }

    @Override
    public LiveData<Wallet> getWalletById(long id) {
        return mDatabase.walletDao().getWalletById(id);
    }

    @Override
    public LiveData<List<Wallet>> getAllWallets() {
        return mDatabase.walletDao().getAllWallets();
    }

    @Override
    public double getIE(long wallet_id) {
        return mDatabase.walletDao().getIE(wallet_id);
    }

    @Override
    public String getLatestDate() {
        return mDatabase.walletDao().getLatestDate();
    }

    @Override
    public void financialStatusUpdate(long wallet_id, String date) {
        mDatabase.walletDao().financialStatusUpdate(wallet_id, date);
    }

    @Override
    public void deleteWallet(Wallet wallet) {
        mDatabase.walletDao().deleteWallet(wallet);

        //other cleaning
        //deleteAllWalletLinkedAccounts(wallet.getId());
    }

    //CATEGORYDAO

    @Override
    public void insertAllCategories(List<CategoryObject> categoryDaos) {
        mDatabase.categoryDao().insertAllCategories(categoryDaos);
    }

    @Override
    public long addCategory(CategoryObject category) {
        return mDatabase.categoryDao().addCategory(category);
    }

    @Override
    public LiveData<CategoryObject> getCategoryByID(long category_id) {
        return mDatabase.categoryDao().getCategoryByID(category_id);
    }

    @Override
    public long getCategoryID(long wallet_id, String bank_account_id) {
        return mDatabase.categoryDao().getCategoryID(wallet_id, bank_account_id);
    }

    @Override
    public LiveData<List<CategoryObject>> getAllCategoriesOfAWallet(long wallet_id) {
        return mDatabase.categoryDao().getAllCategoriesOfAWallet(wallet_id);
    }

    @Override
    public LiveData<List<CategoryObject>> getAllCategories() {
        return mDatabase.categoryDao().getAllCategories();
    }

    @Override
    public void deleteCategory(long category_id) {
        if (category_id != 0) {//category_id is 0 when IE doesn't have a category. So we don't want all independent IEs to be deleted at deletion of only one
            deleteAllIEofACategory(category_id);
        }
        mDatabase.categoryDao().deleteCategory(category_id);
    }

    //IEDAO

    @Override
    public double getCategoryIESUM(long category_id) {
        return mDatabase.categoryDao().getCategoryIESUM(category_id);
    }

    @Override
    public List<IEObject> getCategorysIE(long category_id) {
        return mDatabase.categoryDao().getCategorysIE(category_id);
    }

    @Override
    public List<IEObject> getAllIEsFromWallet(long wallet_id){
        return mDatabase.ieoDao().getAllIEsFromWallet(wallet_id);
    }

    @Override
    public LiveData<List<IEObject>> getAllIEofAWalletWithoutCategoriesAssigned(long walletID) {
        return mDatabase.ieoDao().getAllIEofAWalletWithoutCategoriesAssigned(walletID);
    }

    @Override
    public void insertAllIEObjects(List<IEObject> ieObjects) {
        mDatabase.ieoDao().insertAllIEObjects(ieObjects);
    }

    @Override
    public long addIEObject(IEObject ieObject) {
        return mDatabase.ieoDao().addIEObject(ieObject);
    }

    @Override
    public LiveData<IEObject> getIEObject(long ieID) {
        return mDatabase.ieoDao().getIEObject(ieID);
    }

    @Override
    public LiveData<List<IEObject>> getIESpecificList(long category_id) {
        return mDatabase.ieoDao().getIESpecificList(category_id);
    }

    @Override
    public LiveData<List<IEObject>> getAllIE() {
        return mDatabase.ieoDao().getAllIE();
    }

    @Override
    public void deleteIE(long ieID) {
        mDatabase.ieoDao().deleteIE(ieID);
    }

    @Override
    public void deleteAllIEofACategory(long category_id) {
        mDatabase.ieoDao().deleteAllIEofACategory(category_id);
    }

    @Override
    public List<String> getAllIEsCurrenciesFromWallet(long wallet_id){
        return mDatabase.ieoDao().getAllIEsCurrenciesFromWallet(wallet_id);
    }

    //LINKEDBANKDAO

    @Override
    public long addLinkedBank(LinkedBank linkedBank) {
        return mDatabase.linkedBankDao().addLinkedBank(linkedBank);
    }

    @Override
    public LiveData<List<LinkedBank>> getAllLinkedBanks() {
        return mDatabase.linkedBankDao().getAllLinkedBanks();
    }

    @Override
    public LiveData<LinkedBank> getLinkedBank(long id) {
        return mDatabase.linkedBankDao().getLinkedBank(id);
    }

    @Override
    public String getLinkedBankStatus(long id) {
        return mDatabase.linkedBankDao().getLinkedBankStatus(id);
    }

    @Override
    public String getLinkedBankOBPID(long id) {
        return mDatabase.linkedBankDao().getLinkedBankOBPID(id);
    }

    @Override
    public long updateLinkStatus(long id, String link_status) {
        long status = mDatabase.linkedBankDao().updateLinkStatus(id, link_status);
        if (status > 0 && link_status == "UNLINKED") {
            //TODO: remove all txns from all wallets, and all bank accounts from
            //TODO: I think it is done automatically using foreign keys and cascade on delete, if not do the above todo.
        }
        return status;
    }

    @Override
    public int deleteLinkedBank(long id) {
        return mDatabase.linkedBankDao().deleteLinkedBank(id);
    }

    //BankAccountDAO


    @Override
    public long[] insertAllBankAccounts(List<BankAccount> bankAccounts) {
        return mDatabase.bankAccountDao().insertAllBankAccounts(bankAccounts);
    }

    @Override
    public long addBankAccount(BankAccount bankAccount) {
        return mDatabase.bankAccountDao().addBankAccount(bankAccount);
    }

    @Override
    public BankAccount getBankAccount(String bank_account_id) {
        return mDatabase.bankAccountDao().getBankAccount(bank_account_id);
    }

    @Override
    public LiveData<List<BankAccount>> getAllBankAccounts() {
        return mDatabase.bankAccountDao().getAllBankAccounts();
    }

    @Override
    public long getNrOfAccountsFromLinkedBank(long internal_bank_id) {
        return mDatabase.bankAccountDao().getNrOfAccountsFromLinkedBank(internal_bank_id);
    }

    @Override
    public LiveData<List<BankAccount>> getBankAccountsFromALinkedBank(long internal_bank_id) {
        return mDatabase.bankAccountDao().getBankAccountsFromALinkedBank(internal_bank_id);
    }

    @Override
    public int deleteBankAccount(String id) {
        return mDatabase.bankAccountDao().deleteBankAccount(id);
    }

    @Override
    public int deleteAllBankAccountsFromALinkedBank(String bank_id) {
        return mDatabase.bankAccountDao().deleteAllBankAccountsFromALinkedBank(bank_id);
    }

    //AccountTransactionDao


    @Override
    public long[] insertAllAccountTransactions(List<AccountTransaction> accountTransactions) {
        return mDatabase.accountTransactionDao().insertAllAccountTransactions(accountTransactions);
    }

    @Override
    public long addAccountTransaction(AccountTransaction accountTransactions) {
        return mDatabase.accountTransactionDao().addAccountTransaction(accountTransactions);
    }

    @Override
    public LiveData<List<AccountTransaction>> getAllTransactions() {
        return mDatabase.accountTransactionDao().getAllTransactions();
    }

    @Override
    public LiveData<List<AccountTransaction>> getAllTransactionsFromABankAccount(String bank_account_id) {
        return mDatabase.accountTransactionDao().getAllTransactionsFromABankAccount(bank_account_id);
    }

    @Override
    public List<AccountTransaction> getAllTransactionsFromABankAccountNOTLIVEDATA(String bank_account_id) {
        return mDatabase.accountTransactionDao().getAllTransactionsFromABankAccountNOTLIVEDATA(bank_account_id);
    }

    @Override
    public int deleteTransaction(String id) {
        return mDatabase.accountTransactionDao().deleteTransaction(id);
    }

    @Override
    public int deleteAllTransactionsFromABankAccount(String bank_account_id) {
        return mDatabase.accountTransactionDao().deleteAllTransactionsFromABankAccount(bank_account_id);
    }

    @Override
    public List<BankAccount> getBankAccountsFromALinkedBankNOTLIVEDATA(long bank_id) {
        return mDatabase.bankAccountDao().getBankAccountsFromALinkedBankNOTLIVEDATA(bank_id);
    }

    //WalletLinkedBankAccountsDao

    @Override
    public long[] insertListOfWalletLinkedBankAccount(List<WalletLinkedBankAccounts> walletLinkedBankAccounts) {
        long[] results = mDatabase.walletLinkedBankAccountsDao().insertListOfWalletLinkedBankAccount(walletLinkedBankAccounts);
        //TODO: also we need to add all bank accountS txns correspondent to that accounts in wallet. Txns should be converted to IEs and added to a bank account converted to a category.
        for (WalletLinkedBankAccounts walletLinkedBankAccount : walletLinkedBankAccounts) {
            walletLinkedBankAccount.getBank_account_id();
            //CategoryObject categoryObject = new CategoryObject();
        }
        return results;
    }

    @Override
    public long insertWalletLinkedBankAccount(WalletLinkedBankAccounts walletLinkedBankAccounts) {
        long result = mDatabase.walletLinkedBankAccountsDao().insertWalletLinkedBankAccount(walletLinkedBankAccounts);
        //TODO: also we need to add all bank account txns correspondent to that accounts in wallet.
        return result;
    }

    @Override
    public long getNrOfLinkedBankFromWallet(long wallet_id, long linked_bank_id) {
        return mDatabase.walletLinkedBankAccountsDao().getNrOfLinkedBankFromWallet(wallet_id, linked_bank_id);
    }

    @Override
    public WalletLinkedBankAccounts getLinkedBanksFromWallet(long wallet_id, String bank_account_id) {
        return mDatabase.walletLinkedBankAccountsDao().getLinkedBanksFromWallet(wallet_id, bank_account_id);
    }

    @Override
    public List<WalletLinkedBankAccounts> getLinkedBankAccountsFromWallet(long wallet_id) {
        return mDatabase.walletLinkedBankAccountsDao().getLinkedBankAccountsFromWallet(wallet_id);
    }

    @Override
    public WalletLinkedBankAccounts getLinkedAccountFromWallet(long wallet_id, String bank_account_id) {
        return mDatabase.walletLinkedBankAccountsDao().getLinkedAccountFromWallet(wallet_id, bank_account_id);
    }

    @Override
    public List<WalletLinkedBankAccounts> getLinkedAccountFromBankAccount(String bank_account_id) {
        return mDatabase.walletLinkedBankAccountsDao().getLinkedAccountFromBankAccount(bank_account_id);
    }

    @Override
    public int unlinkBankAccountFromWallet(long wallet_id, String bank_account_id) {
        return mDatabase.walletLinkedBankAccountsDao().unlinkBankAccountFromWallet(wallet_id, bank_account_id);
    }

    @Override
    public int unlinkLinkedBankAccountsFromWallets(long wallet_id, String linked_bank_id) {
        return mDatabase.walletLinkedBankAccountsDao().unlinkLinkedBankAccountsFromWallets(wallet_id, linked_bank_id);
    }

    @Override
    public int deleteAllWalletLinkedAccounts(long wallet_id) {
        return mDatabase.walletLinkedBankAccountsDao().deleteAllWalletLinkedAccounts(wallet_id);
    }

    //REMOTE REST APIS
    //USED BY ServiceHandlerViewModel.java
    public void getAllAvailableBanks(MutableLiveData<List<Bank>> mObservableBanks) {
        OBPRetroClass.getAllAvailableBanks(mObservableBanks);
    }

    //REMOTE REST APIS
    //USED BY BankAccountViewModel.java

    /**
     * Get the list of products from the database and get notified when the data changes.
     */
    public void getAccounts(long bankID, DataRepository repository) {
        OBPRetroClass.getAllAccounts(bankID, repository);
    }

    public void getTransactions(long bankID, String obpBankID, String accountID,
                                DataRepository dataRepository) {
        OBPRetroClass.getAllTransactions(bankID, obpBankID, accountID, dataRepository);
    }

    public long noLinkedBanks() {
        return mDatabase.linkedBankDao().noLinkedBanks();
    }

    //Listeners
    public interface OnDataChangedRepositoryListener {
        void onWalletDataChanged(List<Wallet> walletList);

        void onCategoryDataChanged(List<CategoryObject> categoryObjects);

        void onIEDataChanged(List<IEObject> ieObjects);
    }
//
//    public interface OnBankDataChanged{
//        void onBankDataChanged(List<Bank> bankList);
//    }

//    public void addListener(OnBankDataChanged bankDataListener){ this.bankDataListener = bankDataListener;}

    public void addListener(OnDataChangedRepositoryListener listener) {
        this.listener = listener;
    }

    /**
     * Get the list of wallets from the database and get notified when the data changes.
     */
//    public long addWallet(Wallet wallet){
//        return mDatabase.walletDao().addWallet(wallet);
//    }
//
//    public LiveData<List<Wallet>> getWallets() {
//        return mDatabase.walletDao().getAllWallets();
//    }
//
//    public LiveData<Wallet> getWallet(final long walletID) {
//        return mDatabase.walletDao().getWalletById(walletID);
//    }
//
//    public void deleteWallet(Wallet wallet){
//        mDatabase.walletDao().deleteWallet(wallet);
//    }
//
//    public long addCategory(CategoryObject categoryObject){
//        return mDatabase.categoryDao().addCategory(categoryObject);
//    }
//
//    public LiveData<CategoryObject> getCategoryByName(final long walletID, final String categoryName){
//        return mDatabase.categoryDao().getCategoryByName(walletID,categoryName);
//    }


//    public LiveData<List<CommentEntity>> loadComments(final int productId) {
//        return mDatabase.commentDao().loadComments(productId);
//    }
//
//    public LiveData<List<Wallet>> searchProducts(String query) {
//        return mDatabase.productDao().searchAllProducts(query);
//    }

}
