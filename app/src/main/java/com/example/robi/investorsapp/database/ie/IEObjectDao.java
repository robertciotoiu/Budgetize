package com.example.robi.investorsapp.database.ie;

import android.arch.persistence.room.Insert;

public interface IEObjectDao {

    @Insert
    public void addIEObject(IEObject ieObject);
}
