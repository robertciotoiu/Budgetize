package com.example.robi.investorsapp.database.category;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "categories", primaryKeys = {"category_name","wallet_id"})
public class CategoryObject {

    public CategoryObject(String name, String description, long wallet_id)
    {
        this.name = name;
        this.description = description;
        this.wallet_id = wallet_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getWallet_id() {
        return wallet_id;
    }

    public void setWallet_id(long wallet_id) {
        this.wallet_id = wallet_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @NonNull
    @ColumnInfo(name = "category_name")
    private String name;

    @ColumnInfo(name = "wallet_id")
    private long wallet_id;

    @ColumnInfo(name = "description")
    private String description;

}
