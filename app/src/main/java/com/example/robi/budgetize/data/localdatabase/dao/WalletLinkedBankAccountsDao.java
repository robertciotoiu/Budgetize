package com.example.robi.budgetize.data.localdatabase.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.robi.budgetize.data.localdatabase.entities.WalletLinkedBankAccounts;

import java.util.List;

@Dao
public interface WalletLinkedBankAccountsDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
     long[] insertListOfWalletLinkedBankAccount(List<WalletLinkedBankAccounts> walletLinkedBankAccounts);

    @Insert
     long insertWalletLinkedBankAccount(WalletLinkedBankAccounts walletLinkedBankAccounts);

    @Query("SELECT * FROM wallet_linked_bankaccounts where wallet_id=:wallet_id")
     List<WalletLinkedBankAccounts> getLinkedBankAccountsFromWallet(long wallet_id);

    @Query("SELECT COUNT(DISTINCT(bank_account_id)) FROM wallet_linked_bankaccounts WHERE wallet_id = :wallet_id AND linked_bank_id = :linked_bank_id")
     long getNrOfLinkedBankFromWallet(long wallet_id, long linked_bank_id);

    @Query("SELECT * FROM wallet_linked_bankaccounts WHERE wallet_id = :wallet_id AND bank_account_id = :bank_account_id")
     WalletLinkedBankAccounts getLinkedBanksFromWallet(long wallet_id, String bank_account_id);

    @Query("SELECT * FROM wallet_linked_bankaccounts WHERE wallet_id = :wallet_id AND bank_account_id = :bank_account_id")
     WalletLinkedBankAccounts getLinkedAccountFromWallet(long wallet_id, String bank_account_id);

    @Query("SELECT * FROM wallet_linked_bankaccounts WHERE bank_account_id = :bank_account_id")
     List<WalletLinkedBankAccounts> getLinkedAccountFromBankAccount(String bank_account_id);

    @Query("DELETE FROM wallet_linked_bankaccounts WHERE wallet_id = :wallet_id AND bank_account_id = :bank_account_id")
     int unlinkBankAccountFromWallet(long wallet_id, String bank_account_id);

    @Query("DELETE FROM wallet_linked_bankaccounts WHERE wallet_id = :wallet_id AND linked_bank_id = :linked_bank_id")
     int unlinkLinkedBankAccountsFromWallets(long wallet_id, String linked_bank_id);

    @Query("DELETE FROM wallet_linked_bankaccounts WHERE wallet_id = :wallet_id")
     int deleteAllWalletLinkedAccounts(long wallet_id);
}
