package com.example.robi.budgetize.data.localdatabase.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.robi.budgetize.data.localdatabase.entities.IEObject;

import java.util.List;

@Dao
public interface IEObjectDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAllIEObjects(List<IEObject> ieObjects);

    @Insert
     long addIEObject(IEObject ieObject);

    @Query("SELECT * FROM incomes_expenses where id=:ieID")
     LiveData<IEObject> getIEObject(long ieID);

    @Query("SELECT * FROM incomes_expenses where category_id=:category_id")//not in use currently
     LiveData<List<IEObject>> getIESpecificList(long category_id);//get all ie objects from a given category

    @Query("SELECT * FROM incomes_expenses where wallet_id=:wallet_id")
     List<IEObject> getAllIEsFromWallet(long wallet_id);

    @Query("SELECT * FROM incomes_expenses")
     LiveData<List<IEObject>> getAllIE();//get all ie objects

    @Query("SELECT DISTINCT(currency) FROM incomes_expenses WHERE wallet_id=:wallet_id")
     List<String> getAllIEsCurrenciesFromWallet(long wallet_id);

    @Query("SELECT * FROM incomes_expenses WHERE wallet_id=:wallet_id AND category_id=0")
     LiveData<List<IEObject>> getAllIEofAWalletWithoutCategoriesAssigned(long wallet_id);

    @Query("DELETE FROM incomes_expenses WHERE id=:ieID")
     void deleteIE(long ieID);

    @Query("DELETE FROM incomes_expenses WHERE category_id=:category_id")
    void deleteAllIEofACategory(long category_id);
}
