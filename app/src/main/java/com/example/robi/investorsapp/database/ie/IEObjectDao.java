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

    @Query("SELECT DISTINCT ie.id, ie.wallet_id, ie.amount, ie.name,  ie.category,  ie.type FROM wallets w, incomes_expenses ie, categories cat WHERE ie.wallet_id = :wallet_id AND ie.category = :category_name")
    //@Query("SELECT * from incomes_expenses")
    public List<IEObject> getCategorysIE(long wallet_id, String category_name);

    @Query("DELETE FROM incomes_expenses WHERE wallet_id=:walletID AND id=:ieID")
    public void deleteWallet(long walletID, long ieID);
}
