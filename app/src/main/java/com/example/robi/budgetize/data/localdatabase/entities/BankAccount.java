package com.example.robi.budgetize.data.localdatabase.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "bank_accounts",foreignKeys = {
        @ForeignKey(onDelete = CASCADE, entity = LinkedBank.class, parentColumns = "id", childColumns = "internal_bank_id")
//        ,
//        @ForeignKey(onDelete = CASCADE, entity = BankAccount.class, parentColumns = "id", childColumns = "bank_account_id"),
//        @ForeignKey(onDelete = CASCADE, entity = LinkedBank.class, parentColumns = "id", childColumns = "linked_bank_id")
}
        , indices = {
        @Index("internal_bank_id")
//        ,
//        @Index("linked_bank_id"),
//        @Index("bank_account_id")
}
)
public class BankAccount {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private String id;

    @ColumnInfo(name = "bank_id")
    private String bank_id;

    @ColumnInfo(name = "internal_bank_id")
    private long internal_bank_id;

    @ColumnInfo(name = "label")
    private String label;

    @ColumnInfo(name = "account_type")
    private String account_type;

    public BankAccount(String id, String bank_id, long internal_bank_id, String label, String account_type) {
        this.id = id;
        this.bank_id = bank_id;
        this.internal_bank_id = internal_bank_id;
        this.label = label;
        this.account_type = account_type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBank_id() {
        return bank_id;
    }

    public void setBank_id(String bank_id) {
        this.bank_id = bank_id;
    }

    public long getInternal_bank_id() {
        return internal_bank_id;
    }

    public void setInternal_bank_id(long internal_bank_id) {
        this.internal_bank_id = internal_bank_id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getAccount_type() {
        return account_type;
    }

    public void setAccount_type(String account_type) {
        this.account_type = account_type;
    }

    @Override
    public String toString() {
        return "BankAccount{" +
                "id='" + id + '\'' +
                ", bank_id='" + bank_id + '\'' +
                ", label='" + label + '\'' +
                ", account_type='" + account_type + '\'' +
                '}';
    }
}
