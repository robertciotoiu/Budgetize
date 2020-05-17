package com.example.robi.budgetize.data.localdatabase.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "incomes_expenses"
,foreignKeys ={
        @ForeignKey(onDelete = CASCADE, entity = AccountTransaction.class,
                parentColumns = "id",childColumns = "txn_id")}
        , indices = {
        @Index("txn_id")
})
public class IEObject {

    @PrimaryKey
    @ColumnInfo(name = "id")
    private long id;

    @ColumnInfo(name = "wallet_id")
    private long wallet_id;

    @ColumnInfo(name = "amount")
    public double amount;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "category_id")
    public long category_id;

    @ColumnInfo(name = "date")
    public String date;

    @ColumnInfo(name = "occurrence")
    public String occurrence;

    //public int icon[];
    @ColumnInfo(name="type")
    public int type;//0 means income, 1 means expense

    @ColumnInfo(name = "txn_id")
    public String txn_id;//Reference to txns from OBP like 5fb61a56-85cf-45e2-84b5-f272b08b4ac4

//    @ColumnInfo(name = "bank_account")


    public IEObject(long wallet_id,String name, double amount, long category_id, int type, String date, String occurrence, String txn_id) {
        this.id = System.nanoTime();
        this.wallet_id = wallet_id;
        this.name = name;
        this.amount = amount;
        this.category_id = category_id;          //add incomeObj to the choosen category

//        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//dd/MM/yyyy
//        Date now = new Date();
//        String strDate = sdfDate.format(date);
        this.date = date;

        this.occurrence = occurrence;

        this.type = type;

        this.txn_id = txn_id;

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

    public double getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getCategory() {
        return category_id;
    }

    public void setCategory(long category_id) {
        this.category_id = category_id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTxn_id() {
        return txn_id;
    }

    public void setTxn_id(String txn_id) {
        this.txn_id = txn_id;
    }
}