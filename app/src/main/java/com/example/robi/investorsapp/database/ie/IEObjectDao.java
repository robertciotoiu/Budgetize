package com.example.robi.investorsapp.database.ie;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.robi.investorsapp.database.wallet.Wallet;

import java.util.List;

@Dao
public interface IEObjectDao {

    @Insert
    public long addIEObject(IEObject ieObject);

    @Query("SELECT * FROM incomes_expenses where category=:category")
    public List<IEObject> getIESpecificList(String category);//get all ie objects from a given category

    @Query("DELETE FROM incomes_expenses WHERE wallet_id=:walletID AND id=:ieID")
    public void deleteWallet(long walletID, long ieID);
}
