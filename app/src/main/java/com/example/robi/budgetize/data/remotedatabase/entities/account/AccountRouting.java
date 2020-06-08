package com.example.robi.budgetize.data.remotedatabase.entities.account;

public class AccountRouting {
    String scheme;
    String address;

    public AccountRouting(String scheme, String address) {
        this.scheme = scheme;
        this.address = address;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "AccountRouting{" +
                "scheme='" + scheme + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
