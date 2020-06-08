package com.example.robi.budgetize.data.remotedatabase.entities.account;

import java.util.List;

public class Account {
    public String id;
    public String label;
    public String bank_id;
    public String account_type;
    public List<AccountRouting> account_routings;
    public List<View> views;

    public Account(String id, String label, String bank_id, String account_type, List<AccountRouting> account_routings, List<View> views) {
        this.id = id;
        this.label = label;
        this.bank_id = bank_id;
        this.account_type = account_type;
        this.account_routings = account_routings;
        this.views = views;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getBank_id() {
        return bank_id;
    }

    public void setBank_id(String bank_id) {
        this.bank_id = bank_id;
    }

    public String getAccount_type() {
        return account_type;
    }

    public void setAccount_type(String account_type) {
        this.account_type = account_type;
    }

    public List<AccountRouting> getAccount_routings() {
        return account_routings;
    }

    public void setAccount_routings(List<AccountRouting> account_routing) {
        this.account_routings = account_routing;
    }

    public List<View> getViews() {
        return views;
    }

    public void setViews(List<View> views) {
        this.views = views;
    }

    @Override
    public String toString() {
        return "Account{" +
                "bank_account_id='" + id + '\'' +
                ", label='" + label + '\'' +
                ", obp_bank_id='" + bank_id + '\'' +
                ", account_type='" + account_type + '\'' +
                ", AccountRouting=" + account_routings +
                ", views=" + views +
                '}';
    }
}
