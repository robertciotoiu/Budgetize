package com.example.robi.budgetize.data.localdatabase.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.robi.budgetize.data.localdatabase.entities.CategoryObject;
import com.example.robi.budgetize.data.localdatabase.entities.IEObject;

import java.util.List;

@Dao
public interface CategoryDao {
    //ADD LIST OF CATEGORIES
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllCategories(List<CategoryObject> categoryDaos);

    //ADD ONE CATEGORY
    @Insert(onConflict = OnConflictStrategy.FAIL)
    public long addCategory(CategoryObject category);

    //GET A CATEGORY BY ID
    @Query("SELECT * from categories c WHERE category_id= :category_id")
    public LiveData<CategoryObject> getCategoryByID(long category_id);

    //GET ALL CATEGORIES OF A WALLET
    @Query("SELECT * from categories c WHERE wallet_id=:wallet_id")
    public LiveData<List<CategoryObject>> getAllCategoriesOfAWallet(long wallet_id);

    //GET ALL CATEGORIES
    @Query("SELECT * from categories c")
    public LiveData<List<CategoryObject>> getAllCategories();

    @Query("SELECT * FROM categories where wallet_id = :wallet_id AND bank_account_id =:bank_account_id")
    public long getCategoryID(long wallet_id, String bank_account_id);

    //GET IE CALCULATED FOR A CATEGORY
    @Query("SELECT (SELECT DISTINCT COALESCE(SUM(ie.amount),0) AS category_ie FROM incomes_expenses ie WHERE category_id=:category_id AND ie.type=0) - (SELECT DISTINCT COALESCE(SUM(ie.amount),0) AS category_ie FROM incomes_expenses ie WHERE category_id=:category_id AND ie.type=1) AS Difference")
    public double getCategoryIESUM(long category_id);

    //
    @Query("SELECT DISTINCT ie.id, ie.wallet_id, ie.amount, ie.name,  ie.category_id, ie.date, ie.occurrence , ie.type, ie.txn_id FROM wallets w, incomes_expenses ie, categories cat WHERE ie.category_id = :category_id")
    //@Query("SELECT * from incomes_expenses")
    public List<IEObject> getCategorysIE(long category_id);

    //DELETE A CATEGORY
    @Query("DELETE FROM categories where category_id=:category_id;")
    public void deleteCategory(long category_id);
}
