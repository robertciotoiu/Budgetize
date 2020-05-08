package com.example.robi.budgetize.data.remotedatabase.entities.transaction;

public class Value {
    private String currency;
    private double amount;

    public Value(String currency, double amount) {
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
        return "Value{" +
                "currency='" + currency + '\'' +
                ", amount=" + amount +
                '}';
    }
}
