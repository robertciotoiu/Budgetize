package com.example.robi.budgetize.data.localdatabase.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "categories", foreignKeys = {
        @ForeignKey(onDelete = CASCADE, entity = Wallet.class,
                parentColumns = "id", childColumns = "wallet_id"),

        @ForeignKey(onDelete = CASCADE , entity = BankAccount.class,
        parentColumns = "id", childColumns = "bank_account_id")
}
        , indices = {
        @Index("wallet_id"),
        @Index("bank_account_id")
})
public class CategoryObject {

    public CategoryObject(String name, String description, long wallet_id, String bank_account_id) {
        this.category_id = System.nanoTime();
        this.name = name;
        this.description = description;
        this.wallet_id = wallet_id;
        this.bank_account_id = bank_account_id;
    }

    public long getCategory_id() {
        return category_id;
    }

    public void setCategory_id(long category_id) {
        this.category_id = category_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getWallet_id() {
        return wallet_id;
    }

    public void setWallet_id(long wallet_id) {
        this.wallet_id = wallet_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBank_account_id() {
        return bank_account_id;
    }

    public void setBank_account_id(String bank_account_id) {
        this.bank_account_id = bank_account_id;
    }

    @PrimaryKey
    @ColumnInfo(name = "category_id")
    private long category_id;

    @ColumnInfo(name = "wallet_id")
    private long wallet_id;

    @NonNull
    @ColumnInfo(name = "category_name")
    private String name;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "bank_account_id")
    private String bank_account_id;

}
