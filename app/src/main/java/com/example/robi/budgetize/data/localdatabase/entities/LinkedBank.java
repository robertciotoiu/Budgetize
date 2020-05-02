package com.example.robi.budgetize.data.localdatabase.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "linked_banks")
public class LinkedBank {

    @PrimaryKey
    @ColumnInfo(name = "id")
    private long id;

    @ColumnInfo(name = "short_name")
    private String short_name;

    @ColumnInfo(name = "full_name")
    private String full_name;

    @ColumnInfo(name = "logo")
    private String logo;

    @ColumnInfo(name = "website")
    private String website;

    @ColumnInfo(name = "link_status")
    private String link_status;

    public LinkedBank(String short_name, String full_name, String logo, String website, String link_status) {
        this.id = System.nanoTime();
        this.short_name = short_name;
        this.full_name = full_name;
        this.logo = logo;
        this.website = website;
        this.link_status = link_status;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId(){
        return id;
    }

    public String getShort_name() {
        return short_name;
    }

    public void setShort_name(String short_name) {
        this.short_name = short_name;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getLink_status() {
        return link_status;
    }

    public void setLink_status(String link_status) {
        this.link_status = link_status;
    }
}
