package com.example.robi.budgetize.data;

import com.example.robi.budgetize.data.localdatabase.entities.CategoryObject;
import com.example.robi.budgetize.data.localdatabase.entities.IEObject;
import com.example.robi.budgetize.data.localdatabase.entities.Wallet;
import com.example.robi.budgetize.data.localdatabase.enums.TransactionOccurrenceEnum;

import java.util.ArrayList;
import java.util.List;

public class DataGenerator {
    public static List<Wallet> generateWallets() {
        List<Wallet> wallets = new ArrayList<Wallet>();
        //TODO: change all money fields to BigDecimal!
        Wallet w1 = new Wallet("My Wallet", 100000, 500000, "RON");
        Wallet w2 = new Wallet("Wife's Wallet", 75000, 200000, "RON");
        Wallet w3 = new Wallet("Child's Wallet", 2000, 5500, "RON");
        wallets.add(w1);
        wallets.add(w2);
        wallets.add(w3);
        return wallets;
    }

    public static List<CategoryObject> generateCategoriesForWallets(List<Wallet> wallets) {
        List<CategoryObject> categoryObjects = new ArrayList<CategoryObject>();
        for (Wallet w : wallets) {
            if (w.getName().contentEquals("My Wallet")) {
                CategoryObject co1 = new CategoryObject("Salaries", "Work+Freelancing", 259, w.getId(), null);
                CategoryObject co2 = new CategoryObject("Loans", "Bank loans", 252, w.getId(), null);
                CategoryObject co3 = new CategoryObject("Apartment", "Timisoara, Paris 14", 461, w.getId(), null);
                CategoryObject co4 = new CategoryObject("House", "Dumbravita, Ferventia 23", 470, w.getId(), null);
                CategoryObject co5 = new CategoryObject("Car", "Toyota Land Cruiser", 386, w.getId(), null);
                CategoryObject co6 = new CategoryObject("Subscriptions", "Gym, Music, etc.", 302, w.getId(), null);
                CategoryObject co7 = new CategoryObject("Food", "Food expenses", 258, w.getId(), null);
                CategoryObject co8 = new CategoryObject("Taxes", "Country taxes", 1012, w.getId(), null);
                categoryObjects.add(co1);
                categoryObjects.add(co2);
                categoryObjects.add(co3);
                categoryObjects.add(co4);
                categoryObjects.add(co5);
                categoryObjects.add(co6);
                categoryObjects.add(co7);
                categoryObjects.add(co8);
            } else if (w.getName().contentEquals("Wife's Wallet")) {
                CategoryObject co1 = new CategoryObject("Salaries", "Work+Freelancing", 259, w.getId(), null);
                CategoryObject co2 = new CategoryObject("Loans", "Bank loans", 252, w.getId(), null);
                CategoryObject co3 = new CategoryObject("Holiday House", "Gran Canaria, Puerto Rico 109", 485, w.getId(), null);
                CategoryObject co4 = new CategoryObject("Subscriptions", "Gym, Music, etc.", 302, w.getId(), null);
                CategoryObject co5 = new CategoryObject("Car", "Tesla Model 3", 387, w.getId(), null);
                CategoryObject co6 = new CategoryObject("Food", "Food expenses", 258, w.getId(), null);
                CategoryObject co7 = new CategoryObject("Taxes", "Country taxes", 1012, w.getId(), null);
                categoryObjects.add(co1);
                categoryObjects.add(co2);
                categoryObjects.add(co3);
                categoryObjects.add(co4);
                categoryObjects.add(co5);
                categoryObjects.add(co6);
            } else if (w.getName().contentEquals("Child's Wallet")) {
                CategoryObject co1 = new CategoryObject("Food", "Food expenses", 259, w.getId(), null);
                CategoryObject co2 = new CategoryObject("Entertainment", "Games, books,etc.", 259, w.getId(), null);
                CategoryObject co3 = new CategoryObject("Subscriptions", "TV, Music, etc.", 302, w.getId(), null);
                CategoryObject co4 = new CategoryObject("School", "Taxes, books", 259, w.getId(), null);
                categoryObjects.add(co1);
                categoryObjects.add(co2);
                categoryObjects.add(co3);
                categoryObjects.add(co4);
            }
        }

        return categoryObjects;
    }

    public static List<IEObject> generateIEForCategories(String name, List<CategoryObject> categories, long walletID) {
        List<IEObject> ieObjects = new ArrayList<IEObject>();
        if (name.contentEquals("My Wallet")) {
            for (CategoryObject c : categories) {
                /**
                 *  this.wallet_id = wallet_id;
                 *  this.name = name;
                 *  this.amount = amount;
                 *  this.category = category;          //add incomeObj to the choosen category
                 *  this.type = type;                  //0 is income, 1 is expense
                 */
                if (c.getName().contentEquals("Salaries") && c.getWallet_id() == walletID) {
                    IEObject ie1 = new IEObject(c.getWallet_id(), "Salary", 9500, c.getCategory_id(), 0, "2015-02-01", TransactionOccurrenceEnum.EveryMonth.getOccurrence(), null, "RON");
                    IEObject ie2 = new IEObject(c.getWallet_id(), "Freelancing", 800, c.getCategory_id(), 0, "2015-02-01", TransactionOccurrenceEnum.EveryMonth.getOccurrence(), null, "EUR");
                    ieObjects.add(ie1);
                    ieObjects.add(ie2);
                } else if (c.getName().contentEquals("Loans") && c.getWallet_id() == walletID) {
                    IEObject ie1 = new IEObject(c.getWallet_id(), "Car Loan", 1500, c.getCategory_id(), 1, "2015-02-01", TransactionOccurrenceEnum.EveryMonth.getOccurrence(), null, "RON");
                    IEObject ie2 = new IEObject(c.getWallet_id(), "House Loan", 500, c.getCategory_id(), 1, "2015-02-01", TransactionOccurrenceEnum.EveryMonth.getOccurrence(), null, "EUR");
                    ieObjects.add(ie1);
                    ieObjects.add(ie2);
                } else if (c.getName().contentEquals("Apartment") && c.getWallet_id() == walletID) {
                    IEObject ie1 = new IEObject(c.getWallet_id(), "Utilities", 350, c.getCategory_id(), 1, "2018-07-01", TransactionOccurrenceEnum.EveryMonth.getOccurrence(), null, "RON");
                    IEObject ie2 = new IEObject(c.getWallet_id(), "Energy", 200, c.getCategory_id(), 1, "2018-07-01", TransactionOccurrenceEnum.EveryMonth.getOccurrence(), null, "RON");
                    IEObject ie3 = new IEObject(c.getWallet_id(), "Gas", 30, c.getCategory_id(), 1, "2018-07-01", TransactionOccurrenceEnum.EveryMonth.getOccurrence(), null, "RON");
                    IEObject ie4 = new IEObject(c.getWallet_id(), "TV+Internet", 100, c.getCategory_id(), 1, "2018-07-01", TransactionOccurrenceEnum.EveryMonth.getOccurrence(), null, "RON");
                    IEObject ie5 = new IEObject(c.getWallet_id(), "Maintenance", 500, c.getCategory_id(), 1, "2018-07-01", TransactionOccurrenceEnum.EveryYear.getOccurrence(), null, "RON");
                    IEObject ie6 = new IEObject(c.getWallet_id(), "Rent", 500, c.getCategory_id(), 0, "2018-07-01", TransactionOccurrenceEnum.EveryMonth.getOccurrence(), null, "EUR");
                    ieObjects.add(ie1);
                    ieObjects.add(ie2);
                    ieObjects.add(ie3);
                    ieObjects.add(ie4);
                    ieObjects.add(ie5);
                    ieObjects.add(ie6);
                } else if (c.getName().contentEquals("House") && c.getWallet_id() == walletID) {
                    IEObject ie1 = new IEObject(c.getWallet_id(), "Energy", 250, c.getCategory_id(), 1, "2018-07-01", TransactionOccurrenceEnum.EveryMonth.getOccurrence(), null, "RON");
                    IEObject ie2 = new IEObject(c.getWallet_id(), "Gas", 400, c.getCategory_id(), 1, "2018-07-01", TransactionOccurrenceEnum.EveryMonth.getOccurrence(), null, "RON");
                    IEObject ie3 = new IEObject(c.getWallet_id(), "TV+Internet", 100, c.getCategory_id(), 1, "2018-07-01", TransactionOccurrenceEnum.EveryMonth.getOccurrence(), null, "RON");
                    IEObject ie4 = new IEObject(c.getWallet_id(), "Water", 150, c.getCategory_id(), 1, "2018-07-01", TransactionOccurrenceEnum.EveryMonth.getOccurrence(), null, "RON");
                    IEObject ie5 = new IEObject(c.getWallet_id(), "Salubrity", 60, c.getCategory_id(), 1, "2018-07-01", TransactionOccurrenceEnum.EveryMonth.getOccurrence(), null, "RON");
                    IEObject ie6 = new IEObject(c.getWallet_id(), "Maintenance", 800, c.getCategory_id(), 1, "2018-07-01", TransactionOccurrenceEnum.EveryYear.getOccurrence(), null, "RON");
                    ieObjects.add(ie1);
                    ieObjects.add(ie2);
                    ieObjects.add(ie3);
                    ieObjects.add(ie4);
                    ieObjects.add(ie5);
                    ieObjects.add(ie6);
                } else if (c.getName().contentEquals("Car") && c.getWallet_id() == walletID) {
                    IEObject ie1 = new IEObject(c.getWallet_id(), "Fuel", 600, c.getCategory_id(), 1, "2015-03-01", TransactionOccurrenceEnum.EveryMonth.getOccurrence(), null, "RON");
                    IEObject ie2 = new IEObject(c.getWallet_id(), "Maintenance", 1300, c.getCategory_id(), 1, "2015-03-01", TransactionOccurrenceEnum.EveryYear.getOccurrence(), null, "RON");
                    IEObject ie3 = new IEObject(c.getWallet_id(), "ITP", 150, c.getCategory_id(), 1, "2015-03-01", TransactionOccurrenceEnum.EveryYear.getOccurrence(), null, "RON");
                    IEObject ie4 = new IEObject(c.getWallet_id(), "Tyres", 150, c.getCategory_id(), 1, "2015-03-01", TransactionOccurrenceEnum.EveryYear.getOccurrence(), null, "EUR");
                    IEObject ie5 = new IEObject(c.getWallet_id(), "Insurance", 1000, c.getCategory_id(), 1, "2015-03-01", TransactionOccurrenceEnum.EveryYear.getOccurrence(), null, "RON");
                    ieObjects.add(ie1);
                    ieObjects.add(ie2);
                    ieObjects.add(ie3);
                    ieObjects.add(ie4);
                    ieObjects.add(ie5);
                } else if (c.getName().contentEquals("Subscriptions") && c.getWallet_id() == walletID) {
                    IEObject ie1 = new IEObject(c.getWallet_id(), "SmartFit", 200, c.getCategory_id(), 1, "2015-02-01", TransactionOccurrenceEnum.EveryMonth.getOccurrence(), null, "RON");
                    IEObject ie2 = new IEObject(c.getWallet_id(), "Netflix", 15, c.getCategory_id(), 1, "2015-02-01", TransactionOccurrenceEnum.EveryMonth.getOccurrence(), null, "EUR");
                    IEObject ie3 = new IEObject(c.getWallet_id(), "Spotify", 4.72, c.getCategory_id(), 1, "2015-02-01", TransactionOccurrenceEnum.EveryMonth.getOccurrence(), null, "EUR");
                    IEObject ie4 = new IEObject(c.getWallet_id(), "Mobil", 100, c.getCategory_id(), 1, "2015-02-01", TransactionOccurrenceEnum.EveryMonth.getOccurrence(), null, "RON");
                    IEObject ie5 = new IEObject(c.getWallet_id(), "Tennis", 400, c.getCategory_id(), 1, "2015-02-01", TransactionOccurrenceEnum.EveryMonth.getOccurrence(), null, "RON");
                    ieObjects.add(ie1);
                    ieObjects.add(ie2);
                    ieObjects.add(ie3);
                    ieObjects.add(ie4);
                    ieObjects.add(ie5);
                } else if (c.getName().contentEquals("Food") && c.getWallet_id() == walletID) {
                    IEObject ie1 = new IEObject(c.getWallet_id(), "Groceries", 1000, c.getCategory_id(), 1, "2015-02-01", TransactionOccurrenceEnum.EveryMonth.getOccurrence(), null, "RON");
                    IEObject ie2 = new IEObject(c.getWallet_id(), "Restaurant", 35, c.getCategory_id(), 1, "2015-02-01", TransactionOccurrenceEnum.EveryDay.getOccurrence(), null, "RON");
                    ieObjects.add(ie1);
                    ieObjects.add(ie2);
                } else if (c.getName().contentEquals("Taxes") && c.getWallet_id() == walletID) {
                    IEObject ie1 = new IEObject(c.getWallet_id(), "Apartment", 180, c.getCategory_id(), 1, "2015-02-01", TransactionOccurrenceEnum.EveryYear.getOccurrence(), null, "RON");
                    IEObject ie2 = new IEObject(c.getWallet_id(), "House", 200, c.getCategory_id(), 1, "2015-02-01", TransactionOccurrenceEnum.EveryYear.getOccurrence(), null, "RON");
                    IEObject ie3 = new IEObject(c.getWallet_id(), "Car", 3000, c.getCategory_id(), 1, "2015-02-01", TransactionOccurrenceEnum.EveryYear.getOccurrence(), null, "RON");
                    ieObjects.add(ie1);
                    ieObjects.add(ie2);
                    ieObjects.add(ie3);
                }
            }
        } else if (name.contentEquals("Wife's Wallet")) {
            for (CategoryObject c : categories) {
                /**
                 *  this.wallet_id = wallet_id;
                 *  this.name = name;
                 *  this.amount = amount;
                 *  this.category = category;          //add incomeObj to the choosen category
                 *  this.type = type;                  //0 is income, 1 is expense
                 */
                if (c.getName().contentEquals("Salaries") && c.getWallet_id() == walletID) {
                    IEObject ie1 = new IEObject(c.getWallet_id(), "Salary", 12000, c.getCategory_id(), 0, "2015-02-01", TransactionOccurrenceEnum.EveryMonth.getOccurrence(), null, "RON");
                    ieObjects.add(ie1);
                } else if (c.getName().contentEquals("Loans") && c.getWallet_id() == walletID) {
                    IEObject ie1 = new IEObject(c.getWallet_id(), "Car Loan", 2000, c.getCategory_id(), 1, "2018-03-01", TransactionOccurrenceEnum.EveryMonth.getOccurrence(), null, "RON");
                    IEObject ie2 = new IEObject(c.getWallet_id(), "Holiday House Loan", 1500, c.getCategory_id(), 1, "2015-02-01", TransactionOccurrenceEnum.EveryMonth.getOccurrence(), null, "EUR");
                    ieObjects.add(ie1);
                    ieObjects.add(ie2);
                } else if (c.getName().contentEquals("Holiday House") && c.getWallet_id() == walletID) {
                    IEObject ie1 = new IEObject(c.getWallet_id(), "Energy", 150, c.getCategory_id(), 1, "2015-02-01", TransactionOccurrenceEnum.EveryMonth.getOccurrence(), null, "EUR");
                    IEObject ie2 = new IEObject(c.getWallet_id(), "TV+Internet", 80, c.getCategory_id(), 1, "2015-02-01", TransactionOccurrenceEnum.EveryMonth.getOccurrence(), null, "EUR");
                    IEObject ie3 = new IEObject(c.getWallet_id(), "Maintenance", 3000, c.getCategory_id(), 1, "2015-02-01", TransactionOccurrenceEnum.EveryYear.getOccurrence(), null, "EUR");
                    IEObject ie4 = new IEObject(c.getWallet_id(), "Water", 100, c.getCategory_id(), 1, "2015-02-01", TransactionOccurrenceEnum.EveryMonth.getOccurrence(), null, "EUR");
                    IEObject ie5 = new IEObject(c.getWallet_id(), "Rent", 45000, c.getCategory_id(), 0, "2015-02-01", TransactionOccurrenceEnum.EveryYear.getOccurrence(), null, "EUR");
                    ieObjects.add(ie1);
                    ieObjects.add(ie2);
                    ieObjects.add(ie3);
                    ieObjects.add(ie4);
                    ieObjects.add(ie5);
                } else if (c.getName().contentEquals("Car") && c.getWallet_id() == walletID) {
                    IEObject ie1 = new IEObject(c.getWallet_id(), "Energy", 100, c.getCategory_id(), 1, "2018-03-01", TransactionOccurrenceEnum.EveryYear.getOccurrence(), null, "EUR");
                    IEObject ie2 = new IEObject(c.getWallet_id(), "Tyres", 100, c.getCategory_id(), 1, "2018-03-01", TransactionOccurrenceEnum.EveryYear.getOccurrence(), null, "EUR");
                    IEObject ie3 = new IEObject(c.getWallet_id(), "Insurance", 150, c.getCategory_id(), 1, "2018-03-01", TransactionOccurrenceEnum.EveryYear.getOccurrence(), null, "EUR");
                    ieObjects.add(ie1);
                    ieObjects.add(ie2);
                    ieObjects.add(ie3);
                } else if (c.getName().contentEquals("Subscriptions") && c.getWallet_id() == walletID) {
                    IEObject ie1 = new IEObject(c.getWallet_id(), "SmartFit", 200, c.getCategory_id(), 1, "2015-02-01", TransactionOccurrenceEnum.EveryMonth.getOccurrence(), null, "RON");
                    IEObject ie2 = new IEObject(c.getWallet_id(), "Spotify", 4.99, c.getCategory_id(), 1, "2015-02-01", TransactionOccurrenceEnum.EveryMonth.getOccurrence(), null, "EUR");
                    IEObject ie3 = new IEObject(c.getWallet_id(), "Mobil", 100, c.getCategory_id(), 1, "2015-02-01", TransactionOccurrenceEnum.EveryMonth.getOccurrence(), null, "RON");
                    IEObject ie4 = new IEObject(c.getWallet_id(), "Tennis", 400, c.getCategory_id(), 1, "2015-02-01", TransactionOccurrenceEnum.EveryMonth.getOccurrence(), null, "RON");
                    ieObjects.add(ie1);
                    ieObjects.add(ie2);
                    ieObjects.add(ie3);
                    ieObjects.add(ie4);
                } else if (c.getName().contentEquals("Food") && c.getWallet_id() == walletID) {
                    IEObject ie1 = new IEObject(c.getWallet_id(), "Restaurant", 35, c.getCategory_id(), 1, "2015-02-01", TransactionOccurrenceEnum.EveryDay.getOccurrence(), null, "RON");
                    ieObjects.add(ie1);
                } else if (c.getName().contentEquals("Taxes") && c.getWallet_id() == walletID) {
                    IEObject ie1 = new IEObject(c.getWallet_id(), "Holiday House", 500, c.getCategory_id(), 1, "2015-02-01", TransactionOccurrenceEnum.EveryYear.getOccurrence(), null, "EUR");
                    ieObjects.add(ie1);
                }
            }
        } else if (name.contentEquals("Child's Wallet")) {
            for (CategoryObject c : categories) {
                /**
                 *  this.wallet_id = wallet_id;
                 *  this.name = name;
                 *  this.amount = amount;
                 *  this.category = category;          //add incomeObj to the choosen category
                 *  this.type = type;                  //0 is income, 1 is expense
                 */
                if (c.getName().contentEquals("Food") && c.getWallet_id() == walletID) {
                    IEObject ie1 = new IEObject(c.getWallet_id(), "Restaurant", 200, c.getCategory_id(), 1, "2015-02-01", TransactionOccurrenceEnum.EveryMonth.getOccurrence(), null, "RON");
                    ieObjects.add(ie1);
                } else if (c.getName().contentEquals("Entertainment") && c.getWallet_id() == walletID) {
                    IEObject ie1 = new IEObject(c.getWallet_id(), "Video Games", 100, c.getCategory_id(), 1, "2015-02-01", TransactionOccurrenceEnum.EveryMonth.getOccurrence(), null, "EUR");
                    IEObject ie2 = new IEObject(c.getWallet_id(), "Books", 50, c.getCategory_id(), 1, "2015-02-01", TransactionOccurrenceEnum.EveryMonth.getOccurrence(), null, "EUR");
                    ieObjects.add(ie1);
                    ieObjects.add(ie2);
                } else if (c.getName().contentEquals("Subscriptions") && c.getWallet_id() == walletID) {
                    IEObject ie1 = new IEObject(c.getWallet_id(), "SmartFit", 200, c.getCategory_id(), 1, "2015-02-01", TransactionOccurrenceEnum.EveryMonth.getOccurrence(), null, "RON");
                    IEObject ie2 = new IEObject(c.getWallet_id(), "Spotify", 4.99, c.getCategory_id(), 1, "2015-02-01", TransactionOccurrenceEnum.EveryMonth.getOccurrence(), null, "EUR");
                    IEObject ie3 = new IEObject(c.getWallet_id(), "Mobil", 50, c.getCategory_id(), 1, "2015-02-01", TransactionOccurrenceEnum.EveryMonth.getOccurrence(), null, "RON");
                    IEObject ie4 = new IEObject(c.getWallet_id(), "Tennis", 400, c.getCategory_id(), 1, "2015-02-01", TransactionOccurrenceEnum.EveryMonth.getOccurrence(), null, "RON");
                    ieObjects.add(ie1);
                    ieObjects.add(ie2);
                    ieObjects.add(ie3);
                    ieObjects.add(ie4);
                } else if (c.getName().contentEquals("School") && c.getWallet_id() == walletID) {
                    IEObject ie1 = new IEObject(c.getWallet_id(), "School Tax", 1000, c.getCategory_id(), 1, "2015-02-01", TransactionOccurrenceEnum.EveryYear.getOccurrence(), null, "EUR");
                    IEObject ie2 = new IEObject(c.getWallet_id(), "Books", 100, c.getCategory_id(), 1, "2015-02-01", TransactionOccurrenceEnum.EveryYear.getOccurrence(), null, "EUR");
                    IEObject ie3 = new IEObject(c.getWallet_id(), "Clothes", 700, c.getCategory_id(), 1, "2015-02-01", TransactionOccurrenceEnum.EveryYear.getOccurrence(), null, "EUR");
                    ieObjects.add(ie1);
                    ieObjects.add(ie2);
                    ieObjects.add(ie3);
                }
            }
        }
        return ieObjects;
    }
}