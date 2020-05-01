package com.example.robi.budgetize.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.robi.budgetize.ApplicationObj;
import com.example.robi.budgetize.R;
import com.example.robi.budgetize.backend.services.DownloadBankImagesService;
import com.example.robi.budgetize.backend.viewmodels.ServicesHandlerViewModel;
import com.example.robi.budgetize.backend.viewmodels.factories.ServicesHandlerViewModelFactory;
import com.example.robi.budgetize.data.remotedatabase.entities.Bank;
import com.example.robi.budgetize.ui.adapters.gridlistview.AvailableBank;
import com.example.robi.budgetize.ui.adapters.gridlistview.AvailableBanksAdapter;

import java.util.ArrayList;
import java.util.List;

public class AvailableBanksActivity extends AppCompatActivity {

    private ListView listview;
    private ArrayList<AvailableBank> dataList;
    private AvailableBanksAdapter listadapter;
    private View headerView;
    private View footerView;

    public static ArrayList<Bank> banks = new ArrayList<Bank>();

    private ServicesHandlerViewModel servicesHandlerViewModel;
    private Observer<List<Bank>> bankListObserver;

    private final int MAX_CARDS = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        servicesHandlerViewModel = new ViewModelProvider(this,
                new ServicesHandlerViewModelFactory((ApplicationObj)this.getApplication()))
                .get(ServicesHandlerViewModel.class);
        banks.addAll(servicesHandlerViewModel.getAllBanksFromUI());
        listview = new ListView(this);
        listview.setDivider(null);
        setContentView(listview);
        //banks.addAll(((ApplicationObj) getApplicationContext()).banks);
    }

    @Override
    public void onResume() {
        super.onResume();
        bankListObserver = banks -> {
            AvailableBanksActivity.banks.clear();
            AvailableBanksActivity.banks.addAll(banks);
//            addBanks();
//            listadapter = new AvailableBanksAdapter(getApplicationContext(), this,
//                    MAX_CARDS, (ArrayList<Bank>) banks);
//            listadapter.addItemsInGrid(dataList);
//            addHeaderFooters();
//            listview.setAdapter(listadapter);
        };
        servicesHandlerViewModel.mObservableBanks.observe(this,bankListObserver);

        buildscreen();
    }

    private void buildscreen() {
        addBanks();
        listadapter = new AvailableBanksAdapter(getApplicationContext(), this,
                MAX_CARDS, (ArrayList<Bank>) banks);
        listadapter.addItemsInGrid(dataList);
        addHeaderFooters();
        listview.setAdapter(listadapter);
    }

    @Override
    public void onPause(){
        super.onPause();

        //listadapter = null;
        listview.removeHeaderView(headerView);
        listview.removeFooterView(footerView);

        servicesHandlerViewModel.mObservableBanks.removeObserver(bankListObserver);//detach the observer
    }

    private void addHeaderFooters() {
        final int cardSpacing = listadapter.getCardSpacing();

        // Header View
        headerView = getHeaderFooterView("HEADER", cardSpacing);
        headerView.setPadding(cardSpacing, cardSpacing, cardSpacing, 0);
        listview.addHeaderView(headerView);

        // Footer View
        footerView = getHeaderFooterView("FOOTER", cardSpacing);
        footerView.setPadding(cardSpacing, 0, cardSpacing, cardSpacing);
        listview.addFooterView(footerView);
    }

    private View getHeaderFooterView(String text, int cardSpacing) {
        // New footer/header view.
        View view = listadapter.getLayoutInflater().inflate(
                R.layout.simple_header_footer_layout, null);

        // Header-Footer card sizing.
        View headerFooterView = view.findViewById(R.id.card_main_parent);
        int headerFooterWidth = listadapter.getDeviceWidth()
                - (2 * cardSpacing); // Left-right so *2
        int headerFooterHeight = listadapter.getCardWidth(MAX_CARDS);
        headerFooterView.getLayoutParams().width = headerFooterWidth;
        headerFooterView.getLayoutParams().height = headerFooterHeight;

        // Setting text value
        ((TextView) view.findViewById(R.id.name)).setText(text);
        return view;
    }

    //TODO: Implement to get supported bank list from server!!!
    private void addBanks() {
        dataList = new ArrayList<AvailableBank>();
        // Add data
        for (int i = 0; i < banks.size(); i++) {
            dataList.add(new AvailableBank(banks.get(i).getFull_name() != null ? banks.get(i).getFull_name() : "null", i, banks.get(i).getId()));
        }
    }

    private void syncAllImagesService() {
        Intent serviceIntent = new Intent(this, DownloadBankImagesService.class);
        serviceIntent.putExtra("syncImages", true);
        startService(serviceIntent);
    }

}

// Data class to be used for ListGridAdapter demo.

// Card View Holder class, these should hold all child-view references of a
// given card so that this references can be Re-used to update views (Without
// having to call findViewById each time on list scroll.)

// Now that our data class & ViewHolder class are ready lets bind each data
// item to each card, for that purpose we extend
// ListGridAdapter<E,CVH>/CursorGridAdapter<CVH> & implement few easy methods.
//
// When using ListGridAdapter you need to pass your POJO in place of 'E' so that
// you can get that object back as a parameters in various methods so in this
// Example we are dealing with ArrayList<Item> so we need to
// pass 'Item' in place of 'E'
//
// CVH means CardViewHolder for our card.
//
// By passing E & CVH you bind your adapter class generically to specific
// objects so that type-cast are not needed & compile type object safety can be
// achieved, Just like using ArrayList<Integer> where only integer values only
// can be passed.

// There are 5 easy methods to be implemented in grid-adapters
//
// 1. getNewCard() here library will ask how your Card will look & child views
// you want to hold in CardViewHolder ?, return new Object of
// com.birin.gridlistviewadapters.Card<CVH> class where CVH stands for
// CardViewHolder for this Card. Card<CVH> takes two objects in constructor
// i. CardView : return new view inflating through layout or creating new View
// through Java.
// ii. CardViewHolder : ViewHolder class that should hold child views of given
// cardView, so that view-holder can be used to recycle on list scroll.
// All the card & element sizing should be done in this method.
//
// 2. setCardView() here library will tell you to fill data into your views by
// using Data & CardViewHolder.
//
// 3. onCardClicked() here library give you callback of card-click event with
// card's data.
//
// 4. registerChildrenViewClickEvents() here library will tell you to register
// children present in your CardViewHolder using ChildViewsClickHandler
// instance, registering your children will enable you to get click events in
// onChildViewClicked method, Using this is optional alternatively user can
// handle children ViewClicks in their own ways.
//
// 5. onChildViewClicked() here library will tell you to that any of registered
// child is clicked the advantage of registering is that library will provide
// data related to clicked view's row & you do not have to find your data,
// child view can be registered using ChildViewsClickHandler instance which was
// passed in registerChildrenViewClickEvents() method.
//
//

//
//
//
// Feeling comfortable with basic library usage ?? Wait !! There are more demos
// in this Sample Project like,
// 1.Usage of CursorGridAdapter for handling DB data.
// 2.Usage of PositionCalculator class to maintain correct position
// after rotation.
// 3.Handling card click events.
// 4.Handling click events of child views in a card in very easy way.
// 5.Usage of Tap to Load More functionality
// 6.Using Auto load more feature of library
// 7.Limiting Auto Load More on max items reached
// 8.Binding data through ContentProvider/CursorLoader mechanism
// 9.Supporting fixed number of data items
// 10.Best approach to handle data & AysncTasks through rotation of screen.
//
// So goto AndroidManifest & start tracking different feature's demos (All the
// demos are presented considering the OOPs concept so that it becomes very
// clear what line of code needs to be added for using various features of
// library.)
//
//
//