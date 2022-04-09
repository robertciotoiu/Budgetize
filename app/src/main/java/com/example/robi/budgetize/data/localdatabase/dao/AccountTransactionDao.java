package com.example.robi.budgetize.data.localdatabase.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.robi.budgetize.data.localdatabase.entities.AccountTransaction;

import java.util.List;

@Dao
public interface AccountTransactionDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long[] insertAllAccountTransactions(List<AccountTransaction> accountTransactions);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long addAccountTransaction(AccountTransaction accountTransactions);

    @Query("SELECT * FROM account_transactions")
    LiveData<List<AccountTransaction>> getAllTransactions();

    @Query("SELECT * FROM account_transactions WHERE bank_account_id=:bank_account_id")
    LiveData<List<AccountTransaction>> getAllTransactionsFromABankAccount(String bank_account_id);

    @Query("SELECT * FROM account_transactions WHERE bank_account_id=:bank_account_id")
    List<AccountTransaction> getAllTransactionsFromABankAccountNOTLIVEDATA(String bank_account_id);

    @Query("DELETE FROM account_transactions WHERE id=:id")
    int deleteTransaction(String id);

    @Query("DELETE FROM account_transactions WHERE bank_account_id=:bank_account_id")
    int deleteAllTransactionsFromABankAccount(String bank_account_id);
}
