package com.example.robi.investorsapp.database.ie;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.robi.investorsapp.database.wallet.Wallet;

@Dao
public interface IEObjectDao {

    @Insert
    public void addIEObject(IEObject ieObject);

    @Query("DELETE FROM incomes_expenses WHERE wallet_id=:walletID AND id=:ieID")
    public void deleteWallet(long walletID, long ieID);
}
