package com.example.robi.budgetize;

import android.app.Application;
import android.content.Context;

import com.example.robi.budgetize.data.localdatabase.LocalRoomDatabase;
import com.example.robi.budgetize.backend.rest.model.Bank;

import java.util.ArrayList;

public class ApplicationObj extends Application {
    public boolean bankImagesAvailable = false;
    public long lastUpdated = 0;
    public ArrayList<Bank> banks = new ArrayList<Bank>();
    private AppExecutors mAppExecutors;

    private static Context context;

    public void onCreate() {
        super.onCreate();
        ApplicationObj.context = getApplicationContext();
        mAppExecutors = new AppExecutors();
    }

    public static Context getAppContext() {
        return ApplicationObj.context;
    }

    public LocalRoomDatabase getDatabase() {
        return LocalRoomDatabase.getInstance(this, mAppExecutors);
    }

    public DataRepository getRepository() {
        return DataRepository.getInstance(getDatabase());
    }
}

//    public DataRepository getRepository() {
//        return DataRepository.getInstance(getDatabase());
//    }

