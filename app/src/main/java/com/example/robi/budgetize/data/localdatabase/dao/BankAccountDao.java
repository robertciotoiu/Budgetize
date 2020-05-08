package com.example.robi.budgetize.data.localdatabase.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.robi.budgetize.data.localdatabase.entities.BankAccount;

import java.util.List;

@Dao
public interface BankAccountDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public long[] insertAllBankAccounts(List<BankAccount> bankAccounts);

    @Insert
    public long addBankAccount(BankAccount bankAccount);

    @Query("SELECT * FROM bank_accounts")
    public LiveData<List<BankAccount>> getAllBankAccounts();

    @Query("SELECT * FROM bank_accounts WHERE bank_id=:bank_id")
    public LiveData<List<BankAccount>> getBankAccountsFromALinkedBank(String bank_id);

    @Query("DELETE FROM bank_accounts WHERE id=:id")
    public int deleteBankAccount(String id);

    @Query("DELETE FROM bank_accounts WHERE bank_id=:bank_id")
    public int deleteAllBankAccountsFromALinkedBank(String bank_id);
}
