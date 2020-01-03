package com.example.robi.investorsapp.database.ie;

import androidx.room.Dao;
import androidx.room.Insert;

@Dao
public interface IEObjectDao {

    @Insert
    public void addIEObject(IEObject ieObject);
}
