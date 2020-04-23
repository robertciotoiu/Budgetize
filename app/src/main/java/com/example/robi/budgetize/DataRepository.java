package com.example.robi.budgetize;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import com.example.robi.budgetize.data.localdatabase.LocalRoomDatabase;
import com.example.robi.budgetize.data.localdatabase.dao.CategoryDao;
import com.example.robi.budgetize.data.localdatabase.dao.IEObjectDao;
import com.example.robi.budgetize.data.localdatabase.dao.WalletDao;
import com.example.robi.budgetize.data.localdatabase.entities.CategoryObject;
import com.example.robi.budgetize.data.localdatabase.entities.IEObject;
import com.example.robi.budgetize.data.localdatabase.entities.Wallet;
import java.util.List;

public class DataRepository implements WalletDao, CategoryDao, IEObjectDao {

    private static DataRepository sInstance;

    private final LocalRoomDatabase mDatabase;
    private MediatorLiveData<List<Wallet>> ObservableWallet;
    private MediatorLiveData<List<CategoryObject>> ObservableCategory;
    private MediatorLiveData<List<IEObject>> ObservableIE;
    //TODO: MediatorLiveData for IE and Category

    private OnDataChangedRepositoryListener listener;

    private DataRepository(final LocalRoomDatabase database) {
        mDatabase = database;
        ObservableWallet = new MediatorLiveData<>();
        ObservableCategory = new MediatorLiveData<>();
        ObservableIE = new MediatorLiveData<>();

        ObservableWallet.addSource(mDatabase.walletDao().getAllWallets(),
                wallets -> {
                    if (mDatabase.getDatabaseCreated().getValue() != null) {
                        if(listener != null)
                            listener.onWalletDataChanged(wallets);
//                        ObservableWallet.postValue(wallets);
                    }
                });

        ObservableCategory.addSource(mDatabase.categoryDao().getAllCategories(),
                categories -> {
                    if (mDatabase.getDatabaseCreated().getValue() != null) {
                        if(listener!=null)
                            listener.onCategoryDataChanged(categories);
                        //ObservableCategory.postValue(categories);
                    }
                });

        ObservableIE.addSource(mDatabase.ieoDao().getAllIE(),
                ieObjects -> {
                if(mDatabase.getDatabaseCreated().getValue()!=null)
                    ObservableIE.postValue(ieObjects);
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

    @Override
    public void insertAllCategories(List<CategoryObject> categoryDaos) {
        mDatabase.categoryDao().insertAllCategories(categoryDaos);
    }

    @Override
    public long addCategory(CategoryObject category) {
        return mDatabase.categoryDao().addCategory(category);
    }

    @Override
    public LiveData<CategoryObject> getCategoryByName(long wallet_id, String category_name) {
        return mDatabase.categoryDao().getCategoryByName(wallet_id,category_name);
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
    public double getCategoryIESUM(long wallet_id, String category_name) {
        return mDatabase.categoryDao().getCategoryIESUM(wallet_id,category_name);
    }

    @Override
    public List<IEObject> getCategorysIE(long wallet_id, String category_name) {
        return mDatabase.categoryDao().getCategorysIE(wallet_id,category_name);
    }

    @Override
    public void deleteCategory(long wallet_id, String categoryName) {
        mDatabase.categoryDao().deleteCategory(wallet_id,categoryName);
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
    public LiveData<List<IEObject>> getIESpecificList(long walletID, String category) {
        return mDatabase.ieoDao().getIESpecificList(walletID, category);
    }

    @Override
    public LiveData<List<IEObject>> getAllIE() {
        return mDatabase.ieoDao().getAllIE();
    }

    @Override
    public void deleteIE(long walletID, long ieID) {
        mDatabase.ieoDao().deleteIE(walletID,ieID);
    }

    @Override
    public void insertAllWallets(List<Wallet> wallets) {
        mDatabase.walletDao().insertAllWallets(wallets);
    }

    @Override
    public long addWallet(Wallet wallet) {
        return mDatabase.walletDao().addWallet(wallet);
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
        mDatabase.walletDao().financialStatusUpdate(wallet_id,date);
    }

    @Override
    public void deleteWallet(Wallet wallet) {
        mDatabase.walletDao().deleteWallet(wallet);
    }

    public interface OnDataChangedRepositoryListener{
        void onWalletDataChanged(List<Wallet> walletList);
        void onCategoryDataChanged(List<CategoryObject> categoryObjects);
    }

    public void addListener(OnDataChangedRepositoryListener listener){
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
