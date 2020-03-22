package com.example.robi.investorsapp.localdatabase;


import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.robi.investorsapp.localdatabase.entities.category.CategoryDao;
import com.example.robi.investorsapp.localdatabase.entities.category.CategoryObject;
import com.example.robi.investorsapp.localdatabase.entities.ie.IEObject;
import com.example.robi.investorsapp.localdatabase.entities.ie.IEObjectDao;
import com.example.robi.investorsapp.localdatabase.entities.wallet.Wallet;
import com.example.robi.investorsapp.localdatabase.entities.wallet.WalletDao;

@Database(entities = {Wallet.class, IEObject.class, CategoryObject.class},version = 1)//specify the tables(wallets,incomes,expenses,etc) and versions
public abstract class DaoAbstract extends RoomDatabase {
    public abstract WalletDao walletDao();

    public abstract IEObjectDao ieoDao();

    public abstract CategoryDao categoryDao();

}
