package com.example.robi.budgetize.data.remotedatabase.entities.transaction;

import com.example.robi.budgetize.data.remotedatabase.entities.AccountRouting;
import com.example.robi.budgetize.data.remotedatabase.entities.bank.BankRouting;

import java.util.List;

public class OtherAccount {
    private String id;
    private Holder holder;
    private BankRouting bank_routing;
    private List<AccountRouting> account_routings;

    public OtherAccount(String id, Holder holder, BankRouting bank_routing, List<AccountRouting> account_routings) {
        this.id = id;
        this.holder = holder;
        this.bank_routing = bank_routing;
        this.account_routings = account_routings;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Holder getHolder() {
        return holder;
    }

    public void setHolder(Holder holder) {
        this.holder = holder;
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

    @Override
    public String toString() {
        return "OtherAccount{" +
                "id='" + id + '\'' +
                ", holder='" + holder + '\'' +
                ", bank_routing=" + bank_routing +
                ", account_routing=" + account_routings +
                '}';
    }
}
