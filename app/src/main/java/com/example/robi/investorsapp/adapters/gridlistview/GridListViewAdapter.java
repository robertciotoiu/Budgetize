package com.example.robi.investorsapp.adapters.gridlistview;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.birin.gridlistviewadapters.Card;
import com.birin.gridlistviewadapters.ListGridAdapter;
import com.birin.gridlistviewadapters.dataholders.CardDataHolder;
import com.birin.gridlistviewadapters.utils.ChildViewsClickHandler;
import com.example.robi.investorsapp.APIs.oauth1.activity.OAuthActivity;
import com.example.robi.investorsapp.APIs.oauth1.lib.ExpiredAccessTokenException;
import com.example.robi.investorsapp.APIs.oauth1.lib.OBPRestClient;
import com.example.robi.investorsapp.APIs.oauth1.lib.ObpApiCallFailedException;
import com.example.robi.investorsapp.R;
import com.example.robi.investorsapp.activities.LinkedBankAccounts;
import com.example.robi.investorsapp.rest.model.Bank;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GridListViewAdapter extends Activity {

    private ListView listview;
    private ArrayList<Item> dataList;
    private SimplestDemoAdadpter listadapter;
    private ArrayList<Bank> banks;


    private final int MAX_CARDS = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setBankAccountsFromServer();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        listview = new ListView(this);
        listview.setDivider(null);
        setContentView(listview);
        addBanks();
        listadapter = new SimplestDemoAdadpter(getApplicationContext(), this,
                MAX_CARDS);
        listadapter.addItemsInGrid(dataList);
        addHeaderFooters();
        listview.setAdapter(listadapter);
    }

    @SuppressLint("StaticFieldLeak")
    private void setBankAccountsFromServer() throws ExecutionException, InterruptedException {

        banks = new ArrayList<Bank>();
        banks.addAll(new GetAllBanksClass().execute().get());

        //OLD WAY
//        new AsyncTask<Void, Void, String>() {
//            @Override
//            protected String doInBackground(Void... voids ) {
//            try
//            {
//                String response = HttpUtils.requestBankNames(AppConstants.SERVER_URL, "/RequestBankAccountStatus");
//                Log.d("RequestBankAccountStatus: ", response);
//                return response;
//            } catch(IOException e) {
//                Log.d("Error at RequestBankAccountStatus", e.toString());
//                e.printStackTrace();
//                return e.toString();
//            }
//            }
//        }.execute();
    }

    private class GetAllBanksClass extends AsyncTask<Void, Void, ArrayList<Bank>> {

        public ArrayList<Bank> banks = new ArrayList<Bank>();

        @Override
        /**
         * @return A String containing the json representing the available banks, or an error message
         */
        protected ArrayList<Bank> doInBackground(Void... params) {
            try {
                JSONObject banksJson = OBPRestClient.getBanksJson();
                Gson gson = new Gson();
                JSONArray banksJsonJSONArray = banksJson.getJSONArray("banks");
                for (int i = 0; i < banksJsonJSONArray.length(); i++) {
                    banks.add(gson.fromJson(banksJsonJSONArray.getJSONObject(i).toString(), Bank.class));
                    Log.d("BANK:", banks.get(i).toString());
                }
                return banks;
            } catch (ExpiredAccessTokenException e) {
                // login again / re-authenticate
                redoOAuth();
                return null;
            } catch (ObpApiCallFailedException e) {
                return null;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        public void getBanks(ArrayList<Bank> banks) {
            this.banks = banks;
        }

        @Override
        protected void onPostExecute(ArrayList<Bank> banks) {
            super.onPostExecute(banks);
        }
    }

    public ArrayList<Bank> getAllBanks() {
        return banks;
    }

    private void redoOAuth() {
        OBPRestClient.clearAccessToken(this);
        Intent oauthActivity = new Intent(this, OAuthActivity.class);
        startActivity(oauthActivity);
    }

    private void addHeaderFooters() {
        final int cardSpacing = listadapter.getCardSpacing();

        // Header View
        View headerView = getHeaderFooterView("HEADER", cardSpacing);
        headerView.setPadding(cardSpacing, cardSpacing, cardSpacing, 0);
        listview.addHeaderView(headerView);

        // Footer View
        View footerView = getHeaderFooterView("FOOTER", cardSpacing);
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
        dataList = new ArrayList<Item>();
        // Generate some dummy data.
        for (int i = 0; i < banks.size(); i++) {
            dataList.add(new Item("", i, banks.get(i).getLogo()));
        }
    }

}

// Data class to be used for ListGridAdapter demo.

class Item {
    private int position;
    private String bankName;
    private String bankImg;

    protected Item(String bankName, int position, String bankImg) {
        this.bankImg = bankImg;
        this.bankName = bankName;
        this.position = position;
    }

    public String getBankImg() {
        return bankImg;
    }

    public void setBankImg(String bankImg) {
        this.bankImg = bankImg;
    }

    public String getPositionText() {
        return Integer.toString(position);
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }
}

// Card View Holder class, these should hold all child-view references of a
// given card so that this references can be Re-used to update views (Without
// having to call findViewById each time on list scroll.)

class ViewHolder {
    //TextView bankImgLink;
}

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

class SimplestDemoAdadpter extends ListGridAdapter<Item, ViewHolder> {
    private final int SELECTED_BANK = 0;
    Context activityContext;
    int position = 0;

    public SimplestDemoAdadpter(Context context, Context activityContext, int totalCardsInRow) {
        super(context, totalCardsInRow);
        this.activityContext = activityContext;
    }

    @Override
    protected Card<ViewHolder> getNewCard(int cardwidth) {
        // Create card through XML (can be created programmatically as well.)
        View cardView = getLayoutInflater().inflate(
                R.layout.simple_card_layout, null);
        cardView.setMinimumHeight(cardwidth);
        // Now create card view holder.
        ViewHolder viewHolder = new ViewHolder();
        //viewHolder.bankImgLink = (TextView) cardView.findViewById(R.id.name);
        Bitmap bankLogo = null;
        try {
            //bankLogo = new DownloadImageTask().execute(((GridListViewAdapter)this.activityContext).getAllBanks().get(position++).getLogo()).get();
            bankLogo = new DownloadImageTask().execute("http://goo.gl/oaDMo9").get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //Log.d("LOGO",viewHolder.bankImgLink.getText().toString());
        cardView.setBackground(new BitmapDrawable(activityContext.getResources(),bankLogo));

        return new Card<ViewHolder>(cardView, viewHolder);
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... links) {
//            String urldisplay = links[0];
//            Bitmap mIcon11 = null;
//            try {
//                InputStream in = new java.net.URL(urldisplay).openStream();
//                mIcon11 = BitmapFactory.decodeStream(in);
//            } catch (Exception e) {
//                Log.e("Error", e.getMessage());
//                e.printStackTrace();
//            }
//            return mIcon11;
            boolean retry = true;
            int nrOfRetries = 0;
            Bitmap bitmap = null;
            while(retry)
            try {
                URL url = new URL("http://goo.gl/oaDMo9");
                InputStream in = url.openConnection().getInputStream();
                BufferedInputStream bis = new BufferedInputStream(in,1024*8);
                ByteArrayOutputStream out = new ByteArrayOutputStream();

                int len=0;
                byte[] buffer = new byte[1024];
                while((len = bis.read(buffer)) != -1){
                    out.write(buffer, 0, len);
                }
                out.close();
                bis.close();

                byte[] data = out.toByteArray();
                bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                if(bitmap!=null || nrOfRetries == 1){
                    retry = false;
                }
                else{
                    nrOfRetries++;
                }
            }
            catch (IOException e) {
                if(nrOfRetries==1) {
                    retry = false;
                } else {
                    nrOfRetries++;
                }
                e.printStackTrace();
            }
            return bitmap;
        }
    }

    @Override
    protected void setCardView(CardDataHolder<Item> cardDataHolder,
                               ViewHolder cardViewHolder) {
//        Item item = cardDataHolder.getData();
//        cardViewHolder.bankImgLink.setText(item.getBankImg());
    }

    @Override
    protected void onCardClicked(Item cardData) {
        startActivity(LinkedBankAccounts.class, cardData);

        Toast.makeText(getContext(),
                "Card click " + cardData.getPositionText(), Toast.LENGTH_LONG)
                .show();
    }

    private void startActivity(Class targetClass, Item cardData) {
        Intent myIntent = new Intent();
        myIntent.putExtra("BANK_NAME", cardData.getBankName());
        ((GridListViewAdapter) activityContext).setResult(Activity.RESULT_OK, myIntent);
        ((GridListViewAdapter) activityContext).finish();
    }

    private final int TEXT_VIEW_CLICK_ID = 0;

    @Override
    protected void registerChildrenViewClickEvents(ViewHolder cardViewHolder,
                                                   ChildViewsClickHandler childViewsClickHandler) {
//        childViewsClickHandler.registerChildViewForClickEvent(
//                cardViewHolder.bankImgLink, TEXT_VIEW_CLICK_ID);
    }


    //Disabled
    @Override
    protected void onChildViewClicked(View clickedChildView, Item cardData,
                                      int eventId) {

//        if (eventId == TEXT_VIEW_CLICK_ID) {
//            Toast.makeText(getContext(),
//                    "TextView click " + cardData.getPositionText(),
//                    Toast.LENGTH_LONG).show();
//        }
    }

    // OPTIONAL SETUP

    @Override
    public int getCardSpacing() {
        return (2 * super.getCardSpacing());
    }

    @Override
    protected void setRowView(View rowView, int arg1) {
        rowView.setBackgroundColor(getContext().getResources().getColor(
                R.color.positiveBackgroundColor));
    }

}

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