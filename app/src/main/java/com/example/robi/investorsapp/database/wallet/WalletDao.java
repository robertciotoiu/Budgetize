package com.example.robi.investorsapp.database.wallet;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface WalletDao {

    @Insert(onConflict = OnConflictStrategy.FAIL)
    public long addWallet(Wallet wallet);

    @Query("SELECT * from wallets WHERE id= :id")
    public List<Wallet> getWalletById(long id);

    @Query("SELECT * from wallets")
    public List<Wallet> getAllWallets();

    @Query("SELECT (SELECT COALESCE(SUM(ie.amount),0) AS wallet_ie FROM wallets w, incomes_expenses ie where w.id = ie.wallet_id and w.id = :wallet_id and ie.type=0) - (SELECT COALESCE(SUM(ie.amount),0) AS wallet_ie FROM wallets w, incomes_expenses ie where w.id = ie.wallet_id and w.id = :wallet_id AND ie.type=1) AS Difference")
    public double getIE(long wallet_id);

    @Delete
    public void deleteWallet(Wallet wallet);

}
