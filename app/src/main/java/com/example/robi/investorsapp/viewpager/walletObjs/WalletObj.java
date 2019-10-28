package com.example.robi.investorsapp.viewpager.walletObjs;

import java.util.ArrayList;
import java.util.List;

public class WalletObj implements Obj {
    //public List<ArrayList<String>> categories;

    public int balance = 0;//this should be recalculated everytime a new Obj is added;

    public List<IncomeObj> incomeObjs;

    public List<ExpenseObj> expenseObjs;

    public List<String> categories;


    public void getAllItems()
    {

    }

    public void getAllCategories()
    {

    }
}
