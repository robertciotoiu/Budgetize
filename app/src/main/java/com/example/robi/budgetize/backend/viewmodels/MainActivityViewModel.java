package com.example.robi.budgetize.backend.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.robi.budgetize.data.DataRepository;
import com.example.robi.budgetize.data.localdatabase.entities.CategoryObject;
import com.example.robi.budgetize.data.localdatabase.entities.IEObject;
import com.example.robi.budgetize.data.localdatabase.entities.Wallet;
import com.example.robi.budgetize.data.remotedatabase.entities.Currency;
import com.example.robi.budgetize.data.remotedatabase.remote.rest.utils.HttpUtils;
import com.google.gson.Gson;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

public class MainActivityViewModel extends AndroidViewModel implements DataRepository.OnDataChangedRepositoryListener {

    private final DataRepository repository;
    private final MutableLiveData<List<Wallet>> mObservableWallets = new MutableLiveData<>();
    private final MutableLiveData<List<CategoryObject>> mObservableCategories = new MutableLiveData<>();
    private final MutableLiveData<List<IEObject>> mObservableIEs = new MutableLiveData<>();

    private String walletBaseCurrency = "";
    private HashSet<String> txnsCurrencies = new HashSet<>();
    private Currency currency;
    public static boolean loginStatus = false;

    public int lastWalletPosition = 0;
    public boolean firstAnimation = true;


    public MainActivityViewModel(@NonNull Application application, DataRepository repository) {
        super(application);
        this.repository = repository;
        repository.addListener(this);
        //mObservableWallets = repository.getWallets();
    }

    @NonNull
    @Override
    public <T extends Application> T getApplication() {
        return super.getApplication();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }

    @Override
    public void onWalletDataChanged(List<Wallet> walletList) {
        //TODO modify data here before "sending" it to the UI
        mObservableWallets.postValue(walletList);
    }

    @Override
    public void onCategoryDataChanged(List<CategoryObject> categoryObjects) {
        //TODO modify data here before "sending" it to the UI
        mObservableCategories.postValue(categoryObjects);
//        mObservableWallets.postValue(repository.getAllWallets().getValue());
    }

    @Override
    public void onIEDataChanged(List<IEObject> ieObjects) {
        //TODO modify data here before "sending" it to the UI
        mObservableIEs.postValue(ieObjects);
//        mObservableWallets.postValue(repository.getAllWallets().getValue());
//        mObservableCategories.postValue(repository.getAllCategories().getValue());
    }

    public boolean checkIfLinkedBankExists() {
        if (repository.noLinkedBanks() > 0)
            return true;
        else
            return false;
    }

    public long updateCurrency(long wallet_id, String currency) {
        return repository.updateCurrency(wallet_id, currency);
    }

    public LiveData<List<Wallet>> getAllWallets() {
        return repository.getAllWallets();
    }

    public void deleteWallet(Wallet wallet) {
        repository.deleteWallet(wallet);
    }

    public LiveData<List<CategoryObject>> getAllCategories() {
        return repository.getAllCategories();
    }

    public long addCategory(CategoryObject categoryObject) {
        return repository.addCategory(categoryObject);
    }

    public LiveData<CategoryObject> getCategoryByID(long category_id) {
        return repository.getCategoryByID(category_id);
    }

    public long addWallet(Wallet wallet) {
        return repository.addWallet(wallet);
    }

    public LiveData<Wallet> getWalletById(long status) {
        return repository.getWalletById(status);
    }

    public long addIEObject(IEObject ieObject) {
        return repository.addIEObject(ieObject);
    }

    public LiveData<IEObject> getIEObject(long ieID) {
        return repository.getIEObject(ieID);
    }

    public LiveData<List<CategoryObject>> getAllCategoriesOfAWallet(long wallet_id) {
        return repository.getAllCategoriesOfAWallet(wallet_id);
    }

    public void deleteCategory(long category_id) {
        repository.deleteCategory(category_id);
    }

    public void deleteIE(long ieID) {
        repository.deleteIE(ieID);
    }

    public double getCategoryIESUM(long category_id) {
        return repository.getCategoryIESUM(category_id);
    }

    public List<IEObject> getCategorysIE(long category_id) {
        return repository.getCategorysIE(category_id);
    }

    public LiveData<List<IEObject>> getAllIEofAWalletWithoutCategoriesAssigned(long walletID) {
        return repository.getAllIEofAWalletWithoutCategoriesAssigned(walletID);
    }

    public LiveData<List<IEObject>> getAllIE() {
        return repository.getAllIE();
    }

    public double getIE(long wallet_id) {
        return repository.getIE(wallet_id);
    }


    public List<IEObject> applyFilter(long category_id, String timeFrameFilter, String frequencyFilter, String currencyFilter, List<IEObject> orphanIEs) throws ParseException {
        List<IEObject> ieObjectList = new ArrayList<>();
        if (category_id != 0) {
            ieObjectList = getCategorysIE(category_id);
        } else {
            ieObjectList = new ArrayList<>(orphanIEs);
        }
        final Calendar c = Calendar.getInstance();
        SimpleDateFormat sdfDaily = new SimpleDateFormat("yyyy-MM-dd");
        Date dateFilterDaily = sdfDaily.parse(timeFrameFilter);
        Date dateTxnDaily;

        SimpleDateFormat sdfMonthly = new SimpleDateFormat("yyyy-MM");
        Date dateFilterMonthly = sdfDaily.parse(timeFrameFilter);
        Date dateTxnMonthly;

        SimpleDateFormat sdfYearly = new SimpleDateFormat("yyyy");
        Date dateFilterYearly = sdfDaily.parse(timeFrameFilter);
        Date dateTxnYearly;

        c.setTime(sdfMonthly.parse(timeFrameFilter));

        if (ieObjectList != null) {
            for (IEObject ieObject : ieObjectList) {
                dateTxnDaily = sdfDaily.parse(ieObject.date);
                dateTxnMonthly = sdfMonthly.parse(ieObject.date);
                dateTxnYearly = sdfYearly.parse(ieObject.date);
                if (frequencyFilter != null && frequencyFilter != null) {
                    if (frequencyFilter.contentEquals("daily")) {
                        if (ieObject.occurrence.contentEquals("Every Day")) {
                            if (dateFilterDaily.compareTo(dateTxnDaily) < 0) {
                                // not in the timeFrame - remove
                                ieObjectList.remove(ieObject);
                            }
                        } else if (ieObject.occurrence.contentEquals("Every Month")) {
                            if (dateFilterMonthly.compareTo(dateTxnMonthly) < 0) {
                                // not in the timeFrame - remove
                                ieObjectList.remove(ieObject);
                            } else {
                                YearMonth yearMonthObject = YearMonth.of(c.get(Calendar.YEAR), dateFilterMonthly.getMonth() + 1);
                                int daysInMonth = yearMonthObject.lengthOfMonth();
                                ieObject.amount = ieObject.amount / daysInMonth;
                            }
                        } else if (ieObject.occurrence.contentEquals("Every Year")) {
                            if (dateFilterYearly.compareTo(dateTxnDaily) < 0) {
                                // not in the timeFrame - remove
                                ieObjectList.remove(ieObject);
                            } else {
                                YearMonth yearMonthObject = YearMonth.of(c.get(Calendar.YEAR), dateFilterMonthly.getMonth());
                                int daysInYear = yearMonthObject.lengthOfYear(); //29
                                ieObject.amount = ieObject.amount / daysInYear;
                            }
                        } else if (ieObject.occurrence.contentEquals("Never")) {
                            if (dateFilterDaily.compareTo(dateTxnDaily) != 0) {
                                // not in the timeFrame - remove
                                ieObjectList.remove(ieObject);
                            }
                        }
                    } else if (frequencyFilter.contentEquals("monthly")) {
                        dateFilterMonthly = sdfMonthly.parse(timeFrameFilter);
                        if (ieObject.occurrence.contentEquals("Every Day")) {
                            // cate zile din luna txn a fost facuta
                            if (dateFilterMonthly.compareTo(dateTxnMonthly) < 0) {
                                // not in the timeFrame - remove
                                ieObjectList.remove(ieObject);
                            } else if (dateFilterMonthly.compareTo(dateTxnMonthly) == 0) {
                                //doar zilele cu txn
                                YearMonth yearMonthObject = YearMonth.of(c.get(Calendar.YEAR), dateFilterMonthly.getMonth() + 1);
                                int daysInMonth = yearMonthObject.lengthOfMonth();
                                int txnDays = (daysInMonth - dateTxnDaily.getDate()) + 1;
                                ieObject.amount = ieObject.amount * txnDays;
                            } else if (dateFilterMonthly.compareTo(dateTxnMonthly) > 0) {
                                YearMonth yearMonthObject = YearMonth.of(c.get(Calendar.YEAR), dateFilterMonthly.getMonth() + 1);
                                int daysInMonth = yearMonthObject.lengthOfMonth();
                                ieObject.amount = ieObject.amount * daysInMonth;
                            }
                        } else if (ieObject.occurrence.contentEquals("Every Month")) {
                            if (dateFilterMonthly.compareTo(dateTxnMonthly) < 0) {
                                // not in the timeFrame - remove
                                ieObjectList.remove(ieObject);
                            }
                        } else if (ieObject.occurrence.contentEquals("Every Year")) {
                            if (dateFilterYearly.compareTo(dateTxnMonthly) < 0) {
                                ieObjectList.remove(ieObject);
                            } else {
                                ieObject.amount = ieObject.amount / 12;
                            }
//                            else{
//
//                            }
                        } else if (ieObject.occurrence.contentEquals("Never")) {
                            if (dateFilterMonthly.compareTo(dateTxnMonthly) != 0) {
                                ieObjectList.remove(ieObject);
                            } else {
                                YearMonth yearMonthObject = YearMonth.of(c.get(Calendar.YEAR), dateFilterMonthly.getMonth() + 1);
                                int daysInMonth = yearMonthObject.lengthOfMonth();
                                ieObject.amount = ieObject.amount / daysInMonth;
                            }
                        }
                    } else if (frequencyFilter.contentEquals("yearly")) {// YEARLY
                        if (ieObject.occurrence.contentEquals("Every Day")) {//TXN
                            if (dateFilterYearly.compareTo(dateTxnYearly) < 0) {
                                // not in the timeFrame - remove
                                ieObjectList.remove(ieObject);
                            } else if (dateFilterYearly.compareTo(dateTxnYearly) == 0) {
                                // starting month
                                YearMonth yearMonthObject = YearMonth.of(c.get(Calendar.YEAR), dateTxnMonthly.getMonth() + 1);
                                int daysInMonth = yearMonthObject.lengthOfMonth();
                                int txnDays = (daysInMonth - dateTxnDaily.getDate()) + 1;
                                ieObject.amount = ieObject.amount * txnDays;

                                // rest of the months
                                for (int month = dateTxnMonthly.getMonth() + 1; month <= 11; month++) {
                                    yearMonthObject = YearMonth.of(c.get(Calendar.YEAR), month + 1);
                                    daysInMonth = yearMonthObject.lengthOfMonth();
                                    ieObject.amount = ieObject.amount * daysInMonth;
                                }
                            } else if (dateFilterYearly.compareTo(dateTxnYearly) > 0) {
                                YearMonth yearMonthObject = YearMonth.of(c.get(Calendar.YEAR), dateFilterMonthly.getMonth() + 1);
                                int daysInYear = yearMonthObject.lengthOfYear();
                                ieObject.amount = ieObject.amount * daysInYear;
                            }
                        } else if (ieObject.occurrence.contentEquals("Every Month")) {
                            //TODO: aici am ramas
                            if (dateFilterYearly.compareTo(dateTxnYearly) < 0) {
                                ieObjectList.remove(ieObject);
                            } else if (dateFilterYearly.compareTo(dateTxnYearly) == 0) {
                                ieObject.amount = ieObject.amount * (12 - dateTxnMonthly.getMonth());
                            } else if (dateFilterYearly.compareTo(dateTxnYearly) > 0) {
                                ieObject.amount = ieObject.amount * 12;
                            }
                        } else if (ieObject.occurrence.contentEquals("Every Year")) {
                            if (dateFilterYearly.compareTo(dateTxnYearly) < 0) {
                                // not in the timeFrame - remove
                                ieObjectList.remove(ieObject);
                            }
                        } else if (ieObject.occurrence.contentEquals("Never")) {
                            dateFilterYearly = sdfYearly.parse(timeFrameFilter);
                            if (dateFilterYearly.compareTo(dateTxnYearly) != 0) {
                                ieObjectList.remove(ieObject);
                            } else {
                                YearMonth yearMonthObject = YearMonth.of(c.get(Calendar.YEAR), dateFilterMonthly.getMonth());
                                int daysInYear = yearMonthObject.lengthOfYear();
                                ieObject.amount = ieObject.amount / daysInYear;
                            }
                        }
                    }
                }
                ieObject.amount = convertToWalletCurrency(ieObject.amount, ieObject.currency, currencyFilter);
            }
        }

        // Do we have any txns?

        //do we have a set timeFrameFilter?
//        if (timeFrameFilter != null) {
//
//            for (IEObject ieObject : ieObjectList) {
//
//                if (ieObject.occurrence.contentEquals("Never")) {
//
//                } else {
//                    if (dateFilter.compareTo(dateTxn) > 0) {
//                        ieObjectList.remove(ieObject);
//                    }
//                }
//                    else if (ieObject.occurrence.contentEquals("Every Day")) {
//
//                    } else if (ieObject.occurrence.contentEquals("Every 2 Days")) {
//
//                    } else if (ieObject.occurrence.contentEquals("Every Working Day")) {
//
//                    } else if (ieObject.occurrence.contentEquals("Every Week")) {
//
//                    } else if (ieObject.occurrence.contentEquals("Every 2 Weeks")) {
//
//                    } else if (ieObject.occurrence.contentEquals("Every Month")) {
//
//                    } else if (ieObject.occurrence.contentEquals("Every 2 Months")) {
//
//                    } else if (ieObject.occurrence.contentEquals("Every 3 Months")) {
//
//                    } else if (ieObject.occurrence.contentEquals("Every 6 Months")) {
//
//                    } else if (ieObject.occurrence.contentEquals("Every Year")) {
//
//                    }
//            }
//        }
        for (IEObject ieObject : ieObjectList) {
            ieObject.amount = round(ieObject.amount, 2);
        }
        return ieObjectList;
    }

    private double convertToWalletCurrency(double amount, String fromCurrency, String toCurrency) {
        // TODO: round(x,2) should be eliminate and we should replace all double with BigDecimal
        if (!fromCurrency.contentEquals(toCurrency)) {
            if (currency.getBase().contentEquals(toCurrency)) {
                double rate = currency.getRates().get(fromCurrency) != null ? round(currency.getRates().get(fromCurrency), 2) : 0;
                if (rate == 0) {
                    return 0;
                }
                return round(amount, 2) / (rate);
            } else {
                return round(amount, 2) * (currency.getRates().get(toCurrency) != null ? round(currency.getRates().get(toCurrency), 2) : 0);
            }
        } else return amount;
    }

    private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }


    public void prepareCurrencies(long walletID) {
        walletBaseCurrency = repository.getWalletsCurrency(walletID);
        txnsCurrencies = new HashSet(repository.getAllIEsCurrenciesFromWallet(walletID));
        if (txnsCurrencies.size() != 0) {
            StringBuilder sb = new StringBuilder("");
            for (String s : txnsCurrencies) {
                sb.append(s).append(",");
            }
            sb.deleteCharAt(sb.length() - 1);
            Thread t = new Thread() {
                @Override
                public void run() {
                    try {
                        String response = HttpUtils.get("https://api.exchangeratesapi.io/latest?base=" + walletBaseCurrency + "&symbols=" + sb.toString());
                        Gson gson = new Gson();
                        currency = gson.fromJson(response, Currency.class);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            t.start();
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void addNewCurrencies() {

    }
}
