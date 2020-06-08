package com.example.robi.budgetize.data.remotedatabase.remote;

import android.annotation.SuppressLint;
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
import com.example.robi.budgetize.data.remotedatabase.remote.oauth1.lib.ExpiredAccessTokenException;
import com.example.robi.budgetize.data.remotedatabase.remote.oauth1.lib.OBPRestClient;
import com.example.robi.budgetize.data.remotedatabase.remote.oauth1.lib.ObpApiCallFailedException;
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
            @SuppressLint("StaticFieldLeak")
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
                        //((ApplicationObj) getApplicationContext()).banks.addAll(bankArrayList);
                        //servicesHandlerViewModel.mObservableBanks.postValue(banks);
                        mObservableBanks.postValue(bankArrayList);
                        Log.d("OBPRetroClass.getAllBanks(): ", "BanksAdded");
                        //sendMessage("BanksAdded");//handle this
                    } else {
                        Log.d("OBPRetroClass.getAllBanks(): ", "BanksNOTAdded");//handle this
                        //sendMessage("BanksNOTAdded");//handle this
                    }
                } catch (ExpiredAccessTokenException e) {
                    // login again / re-authenticate
//                        redoOAuth(); check if this was really working(I suppose it was not)
                } catch (ObpApiCallFailedException | JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    public static void getAllAccounts(long bankID, DataRepository repository) {
        new AsyncTask<Void, Void, Void>() {
            @SuppressLint("StaticFieldLeak")
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    ArrayList<Account> accountsArrayList = new ArrayList<>();
//                    JSONObject banksJson = OBPRestClient.getAccounts(bankID, obpBankID);
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
                        String composeStatus="";
                        for(int i = 0;i<status.length;i++) {
                            composeStatus = composeStatus.concat(status[i]+", ");
                        }
                        Log.d("OBPRetroClass: ", "INSERTED BANKACCOUNTS TO DB: " + composeStatus);

                        //mObservableAccounts.postValue(bankAccounts);
                        Log.d("OBPRetroClass.getAllAccounts(): ", "AccountsAdded");
                    } else {
                        Log.d("OBPRetroClass.getAllAccounts(): ", "AccountsNOTAdded");//handle this
                    }

                } catch (ExpiredAccessTokenException e) {
                    // login again / re-authenticate
//                        redoOAuth(); check if this was really working(I suppose it was not)
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
            @SuppressLint("StaticFieldLeak")
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    ArrayList<Transaction> transactionsArrayList = new ArrayList<>();
//                    JSONObject banksJson = OBPRestClient.getAccounts(bankID, obpBankID);
                    JSONObject transactionsJson = OBPRestClient.getTransactions(bankID, obpBankID, accountID);
                    Gson gson = new Gson();
                    Log.d("TRANSACTIONS: ", transactionsJson.toString());

                    JSONArray transactionsJsonJSONArray = transactionsJson.getJSONArray("transactions");
                    for (int i = 0; i < transactionsJsonJSONArray.length(); i++) {
                        transactionsArrayList.add(gson.fromJson(transactionsJsonJSONArray.getJSONObject(i).toString(), Transaction.class));
                        Log.d("Transaction:", transactionsArrayList.get(i).toString());
                    }

                    if (transactionsArrayList.size() != 0) {
                        //MAP OBJECT FROM ACCOUNT TO BANKACCOUNT BEFORE POSTVALUE!!!
                        List<AccountTransaction> accountTransactions = new ArrayList<>();
                        for (Transaction transaction : transactionsArrayList) {
                            String thisHolders = "";
                            for(Holder holder:transaction.getThis_account().getHolders()){
                                thisHolders = thisHolders +","+ holder.getName();
                            }
                            thisHolders = thisHolders.substring(0,thisHolders.length()-2);

//                            String otherHolders = "";
//                            for(Holder holder:transaction.getOther_account().getHolders()){
//                                otherHolders = otherHolders +","+ holder.getName();
//                            }
//                            otherHolders = otherHolders.substring(0,otherHolders.length()-2);

                            accountTransactions.add(
                                    new AccountTransaction(
                                            transaction.getId(),
                                            transaction.getThis_account().getId(),
                                            thisHolders,
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

                        long[] status = repository.insertAllAccountTransactions(accountTransactions);
                        String composeStatus="";
                        for(int i = 0;i<status.length;i++) {
                            composeStatus = composeStatus.concat(status[i]+", ");
                        }
                        Log.d("OBPRetroClass: ", "INSERTED TRANSACTIONS TO DB: " + composeStatus);

                        //mObservableAccounts.postValue(bankAccounts);
                        Log.d("OBPRetroClass.getAllTransactions(): ", "TransactionsAdded");
                    } else {
                        Log.d("OBPRetroClass.getAllTransactions(): ", "TransactionsNOTAdded");//handle this
                    }
                } catch (ExpiredAccessTokenException e) {
                    // login again / re-authenticate
//                        redoOAuth(); check if this was really working(I suppose it was not)
                } catch (ObpApiCallFailedException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

}
