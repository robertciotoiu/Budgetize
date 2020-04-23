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
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllIEObjects(List<IEObject> ieObjects);

    @Insert
    public long addIEObject(IEObject ieObject);

    @Query("SELECT * FROM incomes_expenses where id=:ieID")
    public LiveData<IEObject> getIEObject(long ieID);

    @Query("SELECT * FROM incomes_expenses where wallet_id=:walletID AND category=:category")
    public LiveData<List<IEObject>> getIESpecificList(long walletID, String category);//get all ie objects from a given category

    @Query("SELECT * FROM incomes_expenses")
    public LiveData<List<IEObject>> getAllIE();//get all ie objects

    @Query("DELETE FROM incomes_expenses WHERE wallet_id=:walletID AND id=:ieID")
    public void deleteIE(long walletID, long ieID);
}
