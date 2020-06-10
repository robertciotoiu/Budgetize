package com.example.robi.budgetize.data.remotedatabase.remote;

import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.robi.budgetize.data.DataRepository;
import com.example.robi.budgetize.data.localdatabase.entities.AccountTransaction;
import com.example.robi.budgetize.data.localdatabase.entities.BankAccount;
import com.example.robi.budgetize.data.remotedatabase.entities.account.Account;
import com.example.robi.budgetize.data.remotedatabase.entities.bank.Bank;
import com.example.robi.budgetize.data.remotedatabase.entities.transaction.Holder;
import com.example.robi.budgetize.data.remotedatabase.entities.transaction.Transaction;
import com.example.robi.budgetize.data.remotedatabase.remote.OBPlib.ExpiredAccessTokenException;
import com.example.robi.budgetize.data.remotedatabase.remote.OBPlib.OBPRestClient;
import com.example.robi.budgetize.data.remotedatabase.remote.OBPlib.ObpApiCallFailedException;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OBPRetroClass {

    public static void getAllAvailableBanks(MutableLiveData<List<Bank>> mObservableBanks) {
        /**
         * @return A String containing the json representing the available banks, or an error message
         */
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    ArrayList<Bank> bankArrayList = new ArrayList<>();
                    JSONObject banksJson = OBPRestClient.getBanksJson();
                    Gson gson = new Gson();
                    JSONArray banksJsonJSONArray = banksJson.getJSONArray("banks");
                    for (int i = 0; i < banksJsonJSONArray.length(); i++) {
                        bankArrayList.add(gson.fromJson(banksJsonJSONArray.getJSONObject(i).toString(), Bank.class));
                        Log.d("BANK:", bankArrayList.get(i).toString());
                    }
                    if (bankArrayList.size() != 0) {
                        mObservableBanks.postValue(bankArrayList);
                        Log.d("OBPRetroClass.getAllBanks(): ", "BanksAdded");
                    } else {
                        Log.d("OBPRetroClass.getAllBanks(): ", "BanksNOTAdded");//handle this
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    public static void getAllAccounts(long bankID, DataRepository repository) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    ArrayList<Account> accountsArrayList = new ArrayList<>();
                    JSONObject accountsJson = OBPRestClient.getAccountsAtAllBanks(bankID);
                    Gson gson = new Gson();
                    Log.d("ACCOUNTS: ", accountsJson.toString());

                    JSONArray accountsJsonJSONArray = accountsJson.getJSONArray("accounts");
                    for (int i = 0; i < accountsJsonJSONArray.length(); i++) {
                        accountsArrayList.add(gson.fromJson(accountsJsonJSONArray.getJSONObject(i).toString(), Account.class));
                        Log.d("Account:", accountsArrayList.get(i).toString());
                    }
                    if (accountsArrayList.size() != 0) {
                        //MAP OBJECT FROM ACCOUNT TO BANKACCOUNT BEFORE POSTVALUE!!!
                        List<BankAccount> bankAccounts = new ArrayList<>();
                        for (Account account : accountsArrayList) {
                            bankAccounts.add(new BankAccount(account.getId(), account.bank_id, bankID, account.label, account.account_type));
                        }
                        long[] status = repository.insertAllBankAccounts(bankAccounts);
                        String composeStatus = "";
                        for (int i = 0; i < status.length; i++) {
                            composeStatus = composeStatus.concat(status[i] + ", ");
                        }
                        Log.d("OBPRetroClass: ", "INSERTED BANKACCOUNTS TO DB: " + composeStatus);

                        //mObservableAccounts.postValue(bankAccounts);
                        Log.d("OBPRetroClass.getAllAccounts(): ", "AccountsAdded");
                    } else {
                        Log.d("OBPRetroClass.getAllAccounts(): ", "AccountsNOTAdded");//handle this
                    }

                } catch (ExpiredAccessTokenException e) {
                    // Clear access token
                    OBPRestClient.clearAccessToken(bankID);
                } catch (ObpApiCallFailedException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    public static void getAllTransactions(long bankID, String obpBankID, String accountID, DataRepository repository) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    // Declare the objects we will use
                    // Transaction is the object defined by OBP
                    ArrayList<Transaction> transactionsArrayList = new ArrayList<>();
                    // AccountTransaction is our transaction object we use internally
                    List<AccountTransaction> accountTransactions = new ArrayList<>();
                    Gson gson = new Gson();
                    long[] status;
                    StringBuilder thisHolders;
                    //Retrieve transactions from the Open Bank Project API
                    // We receive a JSONOBject which packs all the transactions
                    JSONObject transactionsJson = OBPRestClient.getTransactions(bankID, obpBankID, accountID);
                    // We need to parse the array and get the data we need from each transaction
                    JSONArray transactionsJsonJSONArray = transactionsJson.getJSONArray("transactions");
                    for (int i = 0; i < transactionsJsonJSONArray.length(); i++) {
                        // We convert the JSON transaction to the Transaction Object and add it to transactionArrayList
                        transactionsArrayList.add
                                (gson.fromJson(transactionsJsonJSONArray.getJSONObject(i).toString(),
                                        Transaction.class));
                    }
                    // We check if any transaction has been retrieved from OBP API
                    if (transactionsArrayList.size() != 0) {
                        // We transform the each Transaction object to our AccountTransaction object
                        for (Transaction transaction : transactionsArrayList) {
                            thisHolders = new StringBuilder();
                            for (Holder holder : transaction.getThis_account().getHolders()) {
                                thisHolders.append(",").append(holder.getName());
                            }
                            thisHolders = new StringBuilder(thisHolders.substring(0, thisHolders.length() - 2));
                            // We create the AccounTransaction object and add it to the ArrayList
                            accountTransactions.add(
                                    new AccountTransaction(
                                            transaction.getId(),
                                            transaction.getThis_account().getId(),
                                            thisHolders.toString(),
                                            transaction.getOther_account().getId(),
                                            transaction.getOther_account().getHolder().getName(),
                                            transaction.getDetails().getType(),
                                            transaction.getDetails().getDescription(),
                                            transaction.getDetails().getPosted(),
                                            transaction.getDetails().getCompleted(),
                                            transaction.getDetails().getNew_balance().getAmount(),
                                            transaction.getDetails().getNew_balance().getCurrency(),
                                            transaction.getDetails().getValue().getAmount(),
                                            transaction.getDetails().getValue().getCurrency()
                                    )
                            );
                        }
                        // after we converted all the objects, we insert the list of AccountTransaction objects
                        // into our local Room Database
                        status = repository.insertAllAccountTransactions(accountTransactions);
                        // we compose a report status for debug purposes
                        String composeStatus = "";
                        for (long l : status) {
                            composeStatus = composeStatus.concat(l + ", ");
                        }
                        Log.d("OBPRetroClass: ", "INSERTED TRANSACTIONS TO DB: " + composeStatus);
                        //mObservableAccounts.postValue(bankAccounts);
                        Log.d("OBPRetroClass.getAllTransactions(): ", "TransactionsAdded");
                    } else {
                        Log.d("OBPRetroClass.getAllTransactions(): ", "TransactionsNOTAdded");//handle this
                    }
                } catch (ExpiredAccessTokenException e) {
                    // Clear access token
                    OBPRestClient.clearAccessToken(bankID);
                } catch (ObpApiCallFailedException | JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

}
