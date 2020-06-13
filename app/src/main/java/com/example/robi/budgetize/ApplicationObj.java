package com.example.robi.budgetize;

import android.app.Application;
import android.content.Context;

import androidx.annotation.Nullable;

import com.example.robi.budgetize.data.DataRepository;
import com.example.robi.budgetize.data.localdatabase.LocalRoomDatabase;
import com.example.robi.budgetize.data.remotedatabase.entities.bank.Bank;
import com.maltaisn.icondialog.pack.IconPack;
import com.maltaisn.icondialog.pack.IconPackLoader;
import com.maltaisn.iconpack.defaultpack.IconPackDefault;

import java.util.ArrayList;

public class ApplicationObj extends Application {
    private static Context context;
    public ArrayList<Bank> banks = new ArrayList<Bank>();
    private AppExecutors mAppExecutors;

    @Nullable
    private IconPack iconPack;

    public void onCreate() {
        super.onCreate();
        ApplicationObj.context = getApplicationContext();
        mAppExecutors = new AppExecutors();
        // Load the icon pack on application start.
        loadIconPack();
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

    @Nullable
    public IconPack getIconPack() {
        return iconPack != null ? iconPack : loadIconPack();
    }

    private IconPack loadIconPack() {
        // Create an icon pack loader with application context.
        IconPackLoader loader = new IconPackLoader(this);

        // Create an icon pack and load all drawables.
        iconPack = IconPackDefault.createDefaultIconPack(loader);
        iconPack.loadDrawables(loader.getDrawableLoader());

        return iconPack;
    }
}
