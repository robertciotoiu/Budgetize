package com.example.robi.budgetize.data.remotedatabase.entities.transaction;

public class Holder {
    private String name;
    private String is_alias;

    public Holder(String name, String is_alias) {
        this.name = name;
        this.is_alias = is_alias;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIs_alias() {
        return is_alias;
    }

    public void setIs_alias(String is_alias) {
        this.is_alias = is_alias;
    }

    @Override
    public String toString() {
        return "Holder{" +
                "name='" + name + '\'' +
                ", is_alias='" + is_alias + '\'' +
                '}';
    }
}
