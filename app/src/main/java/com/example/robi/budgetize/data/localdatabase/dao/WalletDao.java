package com.example.robi.budgetize.data.localdatabase.dao;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.robi.budgetize.data.localdatabase.entities.Wallet;

import java.util.List;

@Dao
public interface WalletDao {
    //ADD LIST OF WALLETS
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllWallets(List<Wallet> wallets);

    //ADD WALLET
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public long addWallet(Wallet wallet);

    //GET WALLET BY ID
    @Query("SELECT * from wallets WHERE id= :id")
    public LiveData<Wallet> getWalletById(long id);

    //GET ALL WALLETS
    @Query("SELECT * from wallets")
    public LiveData<List<Wallet>> getAllWallets();

    //Get Wallet's currency
    @Query("SELECT currency from wallets where id=:id")
    public String getWalletsCurrency(long id);

    //GET CALCULATED IE OF A WALLET
    @Query("SELECT (SELECT COALESCE(SUM(ie.amount),0) AS wallet_ie FROM wallets w, incomes_expenses ie where w.id = ie.wallet_id and w.id = :wallet_id and ie.type=0)" +
            "- (SELECT COALESCE(SUM(ie.amount),0) AS wallet_ie FROM wallets w, incomes_expenses ie where w.id = ie.wallet_id and w.id = :wallet_id AND ie.type=1) AS Difference")
    public double getIE(long wallet_id);

    //?
    @Query("SELECT latest_income_update FROM wallets LIMIT 1")
    public String getLatestDate();

    @Query("UPDATE wallets SET currency = :currency where id = :wallet_id")
    public long updateCurrency(long wallet_id, String currency);

    //UPDATE THE FINANCIAL STATUS OF A WALLET
    @Query("UPDATE wallets set financial_status = financial_status+(SELECT (SELECT COALESCE(SUM(ie.amount),0) AS wallet_ie FROM wallets w, incomes_expenses ie where w.id = ie.wallet_id and w.id = :wallet_id and ie.type=0) - (SELECT COALESCE(SUM(ie.amount),0) AS wallet_ie FROM wallets w, incomes_expenses ie where w.id = ie.wallet_id and w.id = :wallet_id AND ie.type=1) AS Difference), latest_income_update = :date ")
    public void financialStatusUpdate(long wallet_id, String date);

    //DELETE A WALLET
    @Delete
    public void deleteWallet(Wallet wallet);

}
