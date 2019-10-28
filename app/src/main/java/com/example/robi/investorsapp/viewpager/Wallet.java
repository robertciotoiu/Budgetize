package com.example.robi.investorsapp.viewpager;

public class Wallet {

    private int income;
    private int expenses;
    private int current_sum;
    //private int ArrayList<IncomeObj> incomesArr;
    //private int ArrayList<ExpenseObj> expenseArr;


    public Wallet(int income, int expenses)
    {
        this.income = income;
        this.expenses = expenses;
    }

    public void setExpenses(int expenses) {
        this.expenses = expenses;
    }

    public void setIncome(int income) {
        this.income = income;
    }

    public int getExpenses() {
        return expenses;
    }

    public int getIncome() {
        return income;
    }

    public int getIE()
    {
        return income-expenses;
    }
}
