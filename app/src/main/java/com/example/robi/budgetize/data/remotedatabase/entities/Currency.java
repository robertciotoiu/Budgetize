package com.example.robi.budgetize.data.remotedatabase.entities;

import java.util.HashMap;

public class Currency {
    private HashMap<String,Double> rates;
    private String base;
    private String date;

    public Currency(HashMap<String, Double> rates, String base, String date) {
        this.rates = rates;
        this.base = base;
        this.date = date;
    }

    public HashMap<String, Double> getRates() {
        return rates;
    }

    public void setRates(HashMap<String, Double> rates) {
        this.rates = rates;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Currency{" +
                "rates=" + rates +
                ", base='" + base + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
