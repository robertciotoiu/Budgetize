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
    @Query("SELECT c.category_name, c.wallet_id, c.description from wallets w, categories c WHERE w.id = :wallet_id AND category_name= :category_name")
    public LiveData<CategoryObject> getCategoryByName(long wallet_id, String category_name);

    //GET ALL CATEGORIES OF A WALLET
    @Query("SELECT c.category_name, c.wallet_id, c.description from categories c WHERE wallet_id=:wallet_id")
    public LiveData<List<CategoryObject>> getAllCategoriesOfAWallet(long wallet_id);

    //GET ALL CATEGORIES
    @Query("SELECT c.category_name, c.wallet_id, c.description from categories c")
    public LiveData<List<CategoryObject>> getAllCategories();

    //GET IE CALCULATED FOR A CATEGORY
    @Query("SELECT (SELECT DISTINCT COALESCE(SUM(ie.amount),0) AS category_ie FROM incomes_expenses ie WHERE ie.wallet_id = :wallet_id AND ie.category = :category_name AND ie.type=0) - (SELECT DISTINCT COALESCE(SUM(ie.amount),0) AS category_ie FROM incomes_expenses ie WHERE ie.wallet_id = :wallet_id AND ie.category = :category_name AND ie.type=1) AS Difference")
    public double getCategoryIESUM(long wallet_id,String category_name);

    //
    @Query("SELECT DISTINCT ie.id, ie.wallet_id, ie.amount, ie.name,  ie.category,  ie.type FROM wallets w, incomes_expenses ie, categories cat WHERE ie.wallet_id = :wallet_id AND ie.category = :category_name")
    //@Query("SELECT * from incomes_expenses")
    public List<IEObject> getCategorysIE(long wallet_id, String category_name);

    //DELETE A CATEGORY
    @Query("DELETE FROM categories where wallet_id = :wallet_id AND category_name=:categoryName")
    public void deleteCategory(long wallet_id, String categoryName);
}
