package com.example.robi.budgetize.data.localdatabase;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.robi.budgetize.AppExecutors;
import com.example.robi.budgetize.data.DataGenerator;
import com.example.robi.budgetize.data.localdatabase.dao.CategoryDao;
import com.example.robi.budgetize.data.localdatabase.entities.CategoryObject;
import com.example.robi.budgetize.data.localdatabase.entities.IEObject;
import com.example.robi.budgetize.data.localdatabase.dao.IEObjectDao;
import com.example.robi.budgetize.data.localdatabase.entities.Wallet;
import com.example.robi.budgetize.data.localdatabase.dao.WalletDao;

import java.util.List;

@Database(entities = {Wallet.class, IEObject.class, CategoryObject.class},version = 1)//specify the tables(wallets,incomes,expenses,etc) and versions
public abstract class LocalRoomDatabase extends RoomDatabase {
    @VisibleForTesting
    public static final String DATABASE_NAME = "budgetize-db";

    public abstract WalletDao walletDao();

    public abstract IEObjectDao ieoDao();

    public abstract CategoryDao categoryDao();

    private static LocalRoomDatabase sInstance;

    private final MutableLiveData<Boolean> mIsDatabaseCreated = new MutableLiveData<>();

    public static LocalRoomDatabase getInstance(final Context context, final AppExecutors executors) {
        if (sInstance == null) {
            synchronized (LocalRoomDatabase.class) {
                if (sInstance == null) {
                    sInstance = buildDatabase(context.getApplicationContext(), executors);
                    sInstance.updateDatabaseCreated(context.getApplicationContext());
                }
            }
        }
        return sInstance;
    }

    /**
     * Build the database. {@link Builder#build()} only sets up the database configuration and
     * creates a new instance of the database.
     * The SQLite database is only created when it's accessed for the first time.
     */
    private static LocalRoomDatabase buildDatabase(final Context appContext,
                                                   final AppExecutors executors) {
        return Room.databaseBuilder(appContext, LocalRoomDatabase.class, DATABASE_NAME)
                .addCallback(new Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        executors.diskIO().execute(() -> {
                            // Add a delay to simulate a long-running operation
                            //addDelay();
                            // Generate the data for pre-population
                            LocalRoomDatabase database = LocalRoomDatabase.getInstance(appContext, executors);
                            List<Wallet> wallets = DataGenerator.generateWallets();
                            List<CategoryObject> categories = DataGenerator.generateCategoriesForWallets(wallets);
                            List<IEObject> ieobjects = DataGenerator.generateIEForCategories(categories);

                            insertData(database, wallets, categories,ieobjects);
                            // notify that the database was created and it's ready to be used
                            database.setDatabaseCreated();
                        });
                    }
                })
                .allowMainThreadQueries()
                //.addMigrations(MIGRATION_1_2)
                .build();
    }

    /**
     * Check whether the database already exists and expose it via {@link #getDatabaseCreated()}
     */
    private void updateDatabaseCreated(final Context context) {
        if (context.getDatabasePath(DATABASE_NAME).exists()) {
            setDatabaseCreated();
        }
    }

    private void setDatabaseCreated(){
        mIsDatabaseCreated.postValue(true);
    }

    private static void insertData(final LocalRoomDatabase database, final List<Wallet> wallets,
                                   final List<CategoryObject> categoryDaos, final List<IEObject> ieObjects) {
        database.runInTransaction(() -> {
            database.walletDao().insertAllWallets(wallets);
            database.categoryDao().insertAllCategories(categoryDaos);
            database.ieoDao().insertAllIEObjects(ieObjects);
        });
    }

    private static void addDelay() {
        try {
            Thread.sleep(4000);
        } catch (InterruptedException ignored) {
        }
    }

    public LiveData<Boolean> getDatabaseCreated() {
        return mIsDatabaseCreated;
    }

//    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
//
//        @Override
//        public void migrate(@NonNull SupportSQLiteDatabase database) {
//            database.execSQL("CREATE VIRTUAL TABLE IF NOT EXISTS `Wallet` USING FTS4("
//                    + "`name` TEXT, `description` TEXT, content=`products`)");
//            database.execSQL("INSERT INTO productsFts (`rowid`, `name`, `description`) "
//                    + "SELECT `id`, `name`, `description` FROM products");
//
//        }
//    };
}
