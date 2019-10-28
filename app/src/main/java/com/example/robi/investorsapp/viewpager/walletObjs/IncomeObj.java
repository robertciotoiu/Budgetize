package com.example.robi.investorsapp.viewpager.walletObjs;

public class IncomeObj implements Obj {

    public int amount;
    public String name;
    public String category;
    public int icon[];

    public IncomeObj(int amount, String name, String category)
    {
        this.name = name;
        this.amount = amount;
        this.category = category;
        //icon

        //add incomeObj to the choosen category
    }

}
