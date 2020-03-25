package com.example.robi.investorsapp;

import android.app.Application;
import android.content.Context;

import com.example.robi.investorsapp.rest.model.Bank;

import java.util.ArrayList;

public class ApplicationObj extends Application {
    public boolean bankImagesAvailable = false;
    public long lastUpdated = 0;
    public ArrayList<Bank> banks = new ArrayList<Bank>();

    private static Context context;

    public void onCreate() {
        super.onCreate();
        ApplicationObj.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return ApplicationObj.context;
    }
}
