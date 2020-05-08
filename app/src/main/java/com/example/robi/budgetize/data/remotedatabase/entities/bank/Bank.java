package com.example.robi.budgetize.data.remotedatabase.entities.bank;

public class Bank {
    private String id;
    private String short_name;
    private String full_name;
    private String logo;
    private String website;
    private BankRouting bank_routing;

    public Bank(String id, String short_name, String full_name, String logo, String website, BankRouting bank_routing) {
        this.id = id;
        this.short_name = short_name;
        this.full_name = full_name;
        this.logo = logo;
        this.website = website;
        this.bank_routing = bank_routing;
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

    public BankRouting getBank_routing() {
        return bank_routing;
    }

    public void setBank_routing(BankRouting bank_routing) {
        this.bank_routing = bank_routing;
    }

    @Override
    public String toString() {
        return "Bank{" +
                "id='" + id + '\'' +
                ", short_name='" + short_name + '\'' +
                ", full_name='" + full_name + '\'' +
                ", logo='" + logo + '\'' +
                ", website='" + website + '\'' +
                ", bank_routing=" + bank_routing +
                '}';
    }
}
