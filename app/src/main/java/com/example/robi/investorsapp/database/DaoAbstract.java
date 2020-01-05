package com.example.robi.investorsapp.database;


import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.robi.investorsapp.database.category.CategoryDao;
import com.example.robi.investorsapp.database.category.CategoryObject;
import com.example.robi.investorsapp.database.ie.IEObject;
import com.example.robi.investorsapp.database.ie.IEObjectDao;
import com.example.robi.investorsapp.database.wallet.Wallet;
import com.example.robi.investorsapp.database.wallet.WalletDao;

@Database(entities = {Wallet.class, IEObject.class, CategoryObject.class},version = 1)//specify the tables(wallets,incomes,expenses,etc) and versions
public abstract class DaoAbstract extends RoomDatabase {
    public abstract WalletDao walletDao();

    public abstract IEObjectDao ieoDao();

    public abstract CategoryDao categoryDao();

}
