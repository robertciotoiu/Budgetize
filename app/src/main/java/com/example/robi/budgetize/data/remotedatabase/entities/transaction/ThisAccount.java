package com.example.robi.budgetize.data.remotedatabase.entities.transaction;

import com.example.robi.budgetize.data.remotedatabase.entities.account.AccountRouting;
import com.example.robi.budgetize.data.remotedatabase.entities.bank.BankRouting;

import java.util.List;

public class ThisAccount {
    private String id;
    private BankRouting bank_routing;
    private List<AccountRouting> account_routings;
    private List<Holder> holders;

    public ThisAccount(String id, BankRouting bank_routing, List<AccountRouting> account_routings, List<Holder> holders) {
        this.id = id;
        this.bank_routing = bank_routing;
        this.account_routings = account_routings;
        this.holders = holders;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public BankRouting getBank_routing() {
        return bank_routing;
    }

    public void setBank_routing(BankRouting bank_routing) {
        this.bank_routing = bank_routing;
    }

    public List<AccountRouting> getAccount_routings() {
        return account_routings;
    }

    public void setAccount_routings(List<AccountRouting> account_routings) {
        this.account_routings = account_routings;
    }

    public List<Holder> getHolders() {
        return holders;
    }

    public void setHolders(List<Holder> holders) {
        this.holders = holders;
    }

    @Override
    public String toString() {
        return "ThisAccount{" +
                "id='" + id + '\'' +
                ", bank_routing=" + bank_routing +
                ", account_routings=" + account_routings +
                ", holders=" + holders +
                '}';
    }
}
