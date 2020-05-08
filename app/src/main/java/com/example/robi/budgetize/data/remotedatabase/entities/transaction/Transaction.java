package com.example.robi.budgetize.data.remotedatabase.entities.transaction;

public class Transaction {
    private String id;
    private ThisAccount this_account;
    private OtherAccount other_account;
    private Details details;

    public Transaction(String id, ThisAccount this_account, OtherAccount other_account, Details details) {
        this.id = id;
        this.this_account = this_account;
        this.other_account = other_account;
        this.details = details;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ThisAccount getThis_account() {
        return this_account;
    }

    public void setThis_account(ThisAccount this_account) {
        this.this_account = this_account;
    }

    public OtherAccount getOther_account() {
        return other_account;
    }

    public void setOther_account(OtherAccount other_account) {
        this.other_account = other_account;
    }

    public Details getDetails() {
        return details;
    }

    public void setDetails(Details details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id='" + id + '\'' +
                ", this_account=" + this_account +
                ", other_account=" + other_account +
                ", details=" + details +
                '}';
    }
}
