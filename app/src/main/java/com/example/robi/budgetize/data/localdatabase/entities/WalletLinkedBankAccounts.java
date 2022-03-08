package com.example.robi.budgetize.data.localdatabase.entities;

import static androidx.room.ForeignKey.CASCADE;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "wallet_linked_bankaccounts",foreignKeys = {
        @ForeignKey(onDelete = CASCADE, entity = Wallet.class,
                parentColumns = "id", childColumns = "wallet_id"),
        @ForeignKey(onDelete = CASCADE, entity = BankAccount.class,
                parentColumns = "id", childColumns = "bank_account_id"),
        @ForeignKey(onDelete = CASCADE, entity = LinkedBank.class
                , parentColumns = "id", childColumns = "linked_bank_id")}
        , indices = {
        @Index("wallet_id"),
        @Index("linked_bank_id"),
        @Index("bank_account_id")})
public class WalletLinkedBankAccounts {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private long id;

    @ColumnInfo(name = "wallet_id")
    private long wallet_id;

    @ColumnInfo(name = "linked_bank_id")
    private long linked_bank_id;

    @ColumnInfo(name = "bank_account_id")
    private String bank_account_id;

    public WalletLinkedBankAccounts(long wallet_id, long linked_bank_id, String bank_account_id) {
        this.id = System.nanoTime();
        this.wallet_id = wallet_id;
        this.linked_bank_id = linked_bank_id;
        this.bank_account_id = bank_account_id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getWallet_id() {
        return wallet_id;
    }

    public void setWallet_id(long wallet_id) {
        this.wallet_id = wallet_id;
    }

    public long getLinked_bank_id() {
        return linked_bank_id;
    }

    public void setLinked_bank_id(long linked_bank_id) {
        this.linked_bank_id = linked_bank_id;
    }

    public String getBank_account_id() {
        return bank_account_id;
    }

    public void setBank_account_id(String bank_account_id) {
        this.bank_account_id = bank_account_id;
    }

    @Override
    public String toString() {
        return "WalletLinkedBankAccounts{" +
                "id=" + id +
                ", wallet_id=" + wallet_id +
                ", linked_bank_id=" + linked_bank_id +
                ", bank_account_id='" + bank_account_id + '\'' +
                '}';
    }
}
