package com.example.robi.investorsapp.database.wallet;


import androidx.room.Dao;
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
}
