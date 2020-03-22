package com.example.robi.investorsapp.localdatabase.entities.category;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.robi.investorsapp.localdatabase.entities.ie.IEObject;

import java.util.List;

@Dao
public interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.FAIL)
    public long addCategory(CategoryObject category);

    @Query("SELECT c.category_name, c.wallet_id, c.description from wallets w, categories c WHERE w.id = :wallet_id AND category_name= :category_name")
    public CategoryObject getCategoryByName(long wallet_id, String category_name);

    @Query("SELECT c.category_name, c.wallet_id, c.description from categories c WHERE wallet_id=:wallet_id")
    public List<CategoryObject> getAllCategories(long wallet_id);

    @Query("SELECT (SELECT DISTINCT COALESCE(SUM(ie.amount),0) AS category_ie FROM incomes_expenses ie WHERE ie.wallet_id = :wallet_id AND ie.category = :category_name AND ie.type=0) - (SELECT DISTINCT COALESCE(SUM(ie.amount),0) AS category_ie FROM incomes_expenses ie WHERE ie.wallet_id = :wallet_id AND ie.category = :category_name AND ie.type=1) AS Difference")
    public double getCategoryIESUM(long wallet_id,String category_name);

    @Query("SELECT DISTINCT ie.id, ie.wallet_id, ie.amount, ie.name,  ie.category,  ie.type FROM wallets w, incomes_expenses ie, categories cat WHERE ie.wallet_id = :wallet_id AND ie.category = :category_name")
    //@Query("SELECT * from incomes_expenses")
    public List<IEObject> getCategorysIE(long wallet_id, String category_name);

    @Query("DELETE FROM categories where wallet_id = :wallet_id AND category_name=:categoryName")
    public void deleteCategory(long wallet_id, String categoryName);
}
