package com.example.robi.budgetize.data.remotedatabase.entities.transaction;

public class NewBalance {
    private String currency;
    private double amount;

    public NewBalance(String currency, double amount) {
        this.currency = currency;
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "NewBalance{" +
                "currency='" + currency + '\'' +
                ", amount=" + amount +
                '}';
    }
}
