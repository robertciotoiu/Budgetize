package com.example.robi.investorsapp.database.wallet;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.ArrayList;

@Entity(tableName = "wallets")
public class Wallet {

    @PrimaryKey
    private long id;

    @ColumnInfo(name="desired_sum")
    private int desiredSum;

    private int current_sum;
    //private int ArrayList<IncomeObj> incomesArr;
    //private int ArrayList<ExpenseObj> expenseArr;

    //we need a view which calculate ie

}
