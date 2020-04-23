package com.example.robi.budgetize.backend.rest.model;

public class BankRouting {
    private String scheme;
    private String address;

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

    public BankRouting(String scheme, String address) {
        this.scheme = scheme;
        this.address = address;
    }

    @Override
    public String toString() {
        return "BankRouting{" +
                "scheme='" + scheme + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
