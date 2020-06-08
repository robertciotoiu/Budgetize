/**
 * Copyright 2014-present Biraj Patel
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.example.robi.budgetize.ui.modifiedthirdpartylibraries.birajpatel.gridlistviewadapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Toast;

import com.birin.gridlistviewadapters.Card;
import com.birin.gridlistviewadapters.ListGridAdapter;
import com.birin.gridlistviewadapters.dataholders.CardDataHolder;
import com.birin.gridlistviewadapters.utils.ChildViewsClickHandler;
import com.example.robi.budgetize.R;
import com.example.robi.budgetize.ApplicationObj;
import com.example.robi.budgetize.backend.viewmodels.helpers.ImageDownloader;
import com.example.robi.budgetize.data.remotedatabase.entities.bank.Bank;
import com.example.robi.budgetize.ui.activities.AvailableBanksActivity;
import com.example.robi.budgetize.ui.activities.LinkedBankAccountsActivity;
import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class AvailableBanksAdapter extends ListGridAdapter<AvailableBank, AvailableBankViewHolder> {
    File fileLocation;
    private final int SELECTED_BANK = 0;
    Context activityContext;
    ArrayList<Bank> banks;

    public AvailableBanksAdapter(Context context, Context activityContext, int totalCardsInRow, ArrayList<Bank> banks) {
        super(context, totalCardsInRow);
        this.activityContext = activityContext;
        this.banks = banks;
        if(isExternalStorageWritable()) {
            fileLocation = new File(Environment.getExternalStorageDirectory()
                    + "/Android/data/"
                    + ApplicationObj.getAppContext().getPackageName()
                    + "/BankIcons");
        }else{
            fileLocation = new File(Environment.getExternalStorageDirectory()+
                    "/Android/data/"
                    + ApplicationObj.getAppContext().getPackageName()
                    + "/BankIcons");}
    }

    private boolean isExternalStorageWritable() {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED;
    }

    //TODO: now we have the banks here, just we need to retrieve files from the folder and add them here.
    @Override
    protected Card<AvailableBankViewHolder> getNewCard(int cardwidth) {
        int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 64, activityContext.getResources().getDisplayMetrics());
        // Create card through XML (can be created programmatically as well.)
        View cardView = getLayoutInflater().inflate(
                R.layout.simple_card_layout, null);
        AvailableBankViewHolder viewHolder = new AvailableBankViewHolder();
        viewHolder.bankLogo = cardView.findViewById(R.id.card_bank_img);
        viewHolder.bankName = cardView.findViewById(R.id.card_bank_textview);
        return new Card<AvailableBankViewHolder>(cardView, viewHolder);
    }

    @Override
    protected void setCardView(CardDataHolder<AvailableBank> cardDataHolder,
                               AvailableBankViewHolder cardViewHolder) {
        AvailableBank item = cardDataHolder.getData();
        Bitmap bankIcon = ImageDownloader.readFromDisk(new File(fileLocation.getPath()+File.separator+item.getBankImg()+".png"));
        if(bankIcon!=null) {
            cardViewHolder.bankLogo.setImageBitmap(bankIcon);
            cardViewHolder.bankName.setText(item.getBankName());
        }
    }

    //TODO: Implement the functionality when a bank is selected
    @Override
    protected void onCardClicked(AvailableBank cardData) {
        startActivity(LinkedBankAccountsActivity.class, cardData);
        Toast.makeText(getContext(),
                "Card click " + cardData.getPositionText(), Toast.LENGTH_LONG)
                .show();
    }

    private void startActivity(Class targetClass, AvailableBank cardData) {
        Intent myIntent = new Intent();
        for(Bank bank:banks){
            if(bank.getId().contentEquals(cardData.getBankImg())){
                Gson gson = new Gson();
                String bankAsJSON = gson.toJson(bank);
                myIntent.putExtra("BANK", bankAsJSON);
                ((AvailableBanksActivity) activityContext).setResult(Activity.RESULT_OK, myIntent);
                ((AvailableBanksActivity) activityContext).finish();
                break;
            }
        }
    }

    private final int TEXT_VIEW_CLICK_ID = 0;

    //Not used anymore as we don't use inner childs
    @Override
    protected void registerChildrenViewClickEvents(AvailableBankViewHolder cardViewHolder,
                                                   ChildViewsClickHandler childViewsClickHandler) {
//        childViewsClickHandler.registerChildViewForClickEvent(
//                cardViewHolder.bankImgLink, TEXT_VIEW_CLICK_ID);
    }

    //Not used anymore as we don't use inner childs
    @Override
    protected void onChildViewClicked(View clickedChildView, AvailableBank cardData,
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
        rowView.setBackgroundColor(0x000000);//getContext().getResources().getColor(R.color.positiveBackgroundColor));
    }

    //Not used anymore
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... imageUrls) {
            String imageUrl = imageUrls[0];
            return downloadImageViaDefaultHttpConnection(imageUrl);
        }


        private Bitmap downloadImageViaDefaultHttpConnection(String imageUrl) {
            Bitmap imageBitmap = null;
            try {
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(imageUrl);

                HttpResponse httpResponse = httpClient.execute(httpGet);
                InputStream imgStream = httpResponse.getEntity().getContent();
                imageBitmap = BitmapFactory.decodeStream(imgStream);

            } catch (ClientProtocolException e) {
                Log.d("Eroare download imagine: ", e.toString());
            } catch (IOException e) {
                Log.d("Eroare download imagine: ", e.toString());
            }

            return imageBitmap;

        }

        private Bitmap downloadImageViaHttpUrlConnection(String imageUrl) {
            Bitmap bitmap = null;
            try {
                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(input);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return bitmap;
        }

        private Bitmap handleImageResult(Bitmap downloadedBitmap) {
            if (downloadedBitmap != null) {
                return downloadedBitmap;
            }
            return null;
        }
    }

}
