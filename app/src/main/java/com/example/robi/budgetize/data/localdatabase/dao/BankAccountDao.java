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
     long[] insertAllBankAccounts(List<BankAccount> bankAccounts);

    @Insert
     long addBankAccount(BankAccount bankAccount);

    @Query("SELECT * FROM bank_accounts")
     LiveData<List<BankAccount>> getAllBankAccounts();

    @Query("SELECT * FROM bank_accounts WHERE id = :bank_account_id")
     BankAccount getBankAccount(String bank_account_id);

    @Query("SELECT COUNT(*) FROM bank_accounts WHERE internal_bank_id=:internal_bank_id")
     long getNrOfAccountsFromLinkedBank(long internal_bank_id);

    @Query("SELECT * FROM bank_accounts WHERE internal_bank_id=:internal_bank_id")
     LiveData<List<BankAccount>> getBankAccountsFromALinkedBank(long internal_bank_id);

    @Query("SELECT * FROM bank_accounts WHERE internal_bank_id=:internal_bank_id")
     List<BankAccount> getBankAccountsFromALinkedBankNOTLIVEDATA(long internal_bank_id);

    @Query("DELETE FROM bank_accounts WHERE id=:id")
     int deleteBankAccount(String id);

    @Query("DELETE FROM bank_accounts WHERE bank_id=:bank_id")
     int deleteAllBankAccountsFromALinkedBank(String bank_id);
}
