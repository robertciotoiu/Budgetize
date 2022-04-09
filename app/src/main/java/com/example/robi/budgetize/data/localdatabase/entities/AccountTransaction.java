package com.example.robi.budgetize.data.localdatabase.entities;

import static androidx.room.ForeignKey.CASCADE;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "account_transactions",foreignKeys = {
        @ForeignKey(onDelete = CASCADE, entity = BankAccount.class, parentColumns = "id", childColumns = "bank_account_id")
}
        , indices = {
        @Index("bank_account_id")
}
)
public class AccountTransaction {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private String id;

    @ColumnInfo(name = "bank_account_id")
    private String bank_account_id;

    @ColumnInfo(name = "bank_account_name")
    private String bank_account_name;

    @ColumnInfo(name = "other_account_id")
    private String other_account_id;//to which or from who is the txn

    @ColumnInfo(name = "other_account_name")
    private String other_account_name;

    @ColumnInfo(name = "type")
    private String type;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "posted")
    private String posted;//date

    @ColumnInfo(name = "completed")
    private String completed;//date

    @ColumnInfo(name = "new_balance_amount")
    private double new_balance_amount;

    @ColumnInfo(name = "new_balance_currency")
    private String new_balance_currency;

    @ColumnInfo(name = "txn_value")
    private double txn_value;

    @ColumnInfo(name = "txn_currency")
    private String txn_currency;
    //etc.


    public AccountTransaction(@NonNull String id, String bank_account_id, String bank_account_name,
                              String other_account_id, String other_account_name,
                              String type, String description, String posted,
                              String completed,
                              double new_balance_amount, String new_balance_currency,
                              double txn_value, String txn_currency) {
        this.id = id;
        this.bank_account_id = bank_account_id;
        this.bank_account_name = bank_account_name;
        this.other_account_id = other_account_id;
        this.other_account_name = other_account_name;
        this.type = type;
        this.description = description;
        this.posted = posted;
        this.completed = completed;
        this.new_balance_amount = new_balance_amount;
        this.new_balance_currency = new_balance_currency;
        this.txn_value = txn_value;
        this.txn_currency = txn_currency;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getBank_account_id() {
        return bank_account_id;
    }

    public void setBank_account_id(String bank_account_id) {
        this.bank_account_id = bank_account_id;
    }

    public String getBank_account_name() {
        return bank_account_name;
    }

    public void setBank_account_name(String bank_account_name) {
        this.bank_account_name = bank_account_name;
    }

    public String getOther_account_id() {
        return other_account_id;
    }

    public void setOther_account_id(String other_account_id) {
        this.other_account_id = other_account_id;
    }

    public String getOther_account_name() {
        return other_account_name;
    }

    public void setOther_account_name(String other_account_name) {
        this.other_account_name = other_account_name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPosted() {
        return posted;
    }

    public void setPosted(String posted) {
        this.posted = posted;
    }

    public String getCompleted() {
        return completed;
    }

    public void setCompleted(String completed) {
        this.completed = completed;
    }

    public double getNew_balance_amount() {
        return new_balance_amount;
    }

    public void setNew_balance_amount(double new_balance_amount) {
        this.new_balance_amount = new_balance_amount;
    }

    public String getNew_balance_currency() {
        return new_balance_currency;
    }

    public void setNew_balance_currency(String new_balance_currency) {
        this.new_balance_currency = new_balance_currency;
    }

    public double getTxn_value() {
        return txn_value;
    }

    public void setTxn_value(double txn_value) {
        this.txn_value = txn_value;
    }

    public String getTxn_currency() {
        return txn_currency;
    }

    public void setTxn_currency(String txn_currency) {
        this.txn_currency = txn_currency;
    }

    @Override
    public String toString() {
        return "AccountTransaction{" +
                "id='" + id + '\'' +
                ", bank_account_id='" + bank_account_id + '\'' +
                ", bank_account_name='" + bank_account_name + '\'' +
                ", other_account_id='" + other_account_id + '\'' +
                ", other_account_name='" + other_account_name + '\'' +
                ", type='" + type + '\'' +
                ", description='" + description + '\'' +
                ", posted='" + posted + '\'' +
                ", completed='" + completed + '\'' +
                ", new_balance_amount=" + new_balance_amount +
                ", new_balance_currency='" + new_balance_currency + '\'' +
                ", txn_value=" + txn_value +
                ", txn_currency='" + txn_currency + '\'' +
                '}';
    }
}
