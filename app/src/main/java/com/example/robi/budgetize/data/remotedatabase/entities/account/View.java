package com.example.robi.budgetize.data.remotedatabase.entities.account;

public class View {
    String id;
    String short_name;
    String is_public;

    public View(String id, String short_name, String is_public) {
        this.id = id;
        this.short_name = short_name;
        this.is_public = is_public;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getShort_name() {
        return short_name;
    }

    public void setShort_name(String short_name) {
        this.short_name = short_name;
    }

    public String getIs_public() {
        return is_public;
    }

    public void setIs_public(String is_public) {
        this.is_public = is_public;
    }


    @Override
    public String toString() {
        return "View{" +
                "id='" + id + '\'' +
                ", short_name='" + short_name + '\'' +
                ", is_public='" + is_public + '\'' +
                '}';
    }
}
