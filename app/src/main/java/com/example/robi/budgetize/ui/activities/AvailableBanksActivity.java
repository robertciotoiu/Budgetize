package com.example.robi.budgetize.ui.activities;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.robi.budgetize.ApplicationObj;
import com.example.robi.budgetize.R;
import com.example.robi.budgetize.backend.viewmodels.ServicesHandlerViewModel;
import com.example.robi.budgetize.backend.viewmodels.factories.ServicesHandlerViewModelFactory;
import com.example.robi.budgetize.backend.viewmodels.helpers.ImageDownloader;
import com.example.robi.budgetize.data.remotedatabase.entities.bank.Bank;
import com.example.robi.budgetize.ui.gridlistview.AvailableBank;
import com.example.robi.budgetize.ui.gridlistview.AvailableBanksAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AvailableBanksActivity extends AppCompatActivity {

    public static ArrayList<Bank> banks = new ArrayList<>();
    private final int MAX_CARDS = 2;
    private ListView listview;
    private ArrayList<AvailableBank> dataList;
    private AvailableBanksAdapter listadapter;
    private View headerView;
    private View footerView;
    private ServicesHandlerViewModel servicesHandlerViewModel;
    private Observer<List<Bank>> bankListObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        servicesHandlerViewModel = new ViewModelProvider(this,
                new ServicesHandlerViewModelFactory((ApplicationObj) this.getApplication()))
                .get(ServicesHandlerViewModel.class);
        //TODO: watch this as it may cause problems. in the past it was without new ArrayList<...
        banks.addAll(servicesHandlerViewModel.getAllBanksFromUI());
        listview = new ListView(this);
        listview.setDivider(null);
        setContentView(listview);
    }

    @Override
    public void onResume() {
        super.onResume();
        bankListObserver = banks -> {
            AvailableBanksActivity.banks.clear();
            AvailableBanksActivity.banks.addAll(banks);
        };
        servicesHandlerViewModel.mObservableAvailableBanks.observe(this, bankListObserver);

        buildscreen();
    }

    private void buildscreen() {
        addBanks();
        listadapter = new AvailableBanksAdapter(getApplicationContext(), this,
                MAX_CARDS, banks);
        listadapter.addItemsInGrid(dataList);
        addHeaderFooters();
        listview.setAdapter(listadapter);
    }

    @Override
    public void onPause() {
        super.onPause();
        //detach the observer
        servicesHandlerViewModel.mObservableAvailableBanks.removeObserver(bankListObserver);
    }

    private void addHeaderFooters() {
        final int cardSpacing = listadapter.getCardSpacing();

        // Header View
        headerView = getHeaderFooterView(cardSpacing);
        headerView.setPadding(0, 0, 0, 0);
        listview.addHeaderView(headerView);
        listview.setFitsSystemWindows(true);
    }

    private View getHeaderFooterView(int cardSpacing) {
        // New footer/header view.
        View view = listadapter.getLayoutInflater().inflate(
                R.layout.simple_header_footer_layout, listview, false);

        // Header-Footer card sizing.
        View headerFooterView = view.findViewById(R.id.card_main_parent);
        headerFooterView.getLayoutParams().width = listadapter.getDeviceWidth()
                - (2 * cardSpacing);
        headerFooterView.getLayoutParams().height = 300;

        // Setting text value
        ((TextView) view.findViewById(R.id.name)).setText("Available Banks");
        return view;
    }

    //TODO: Implement to get supported bank list from server!!!
    private void addBanks() {
        File fileLocation;
        if (isExternalStorageWritable()) {
            fileLocation = new File(Environment.getExternalStorageDirectory()
                    + "/Android/data/"
                    + ApplicationObj.getAppContext().getPackageName()
                    + "/BankIcons");
        } else {
            fileLocation = new File(Environment.getExternalStorageDirectory() +
                    "/Android/data/"
                    + ApplicationObj.getAppContext().getPackageName()
                    + "/BankIcons");
        }
        String bankFullName;
        dataList = new ArrayList<>();
        // Add data
        for (int i = 0; i < banks.size(); i++) {
            bankFullName = banks.get(i).getFull_name();
            if (ImageDownloader.readFromDisk(new File(fileLocation.getPath() + File.separator + banks.get(i).getId() + ".png")) != null)
                dataList.add(new AvailableBank(bankFullName != null ? bankFullName : "null", i, banks.get(i).getId()));
        }
    }

    private boolean isExternalStorageWritable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }
}