package com.example.robi.budgetize.ui.adapters.viewpager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.PagerAdapter;

import com.example.robi.budgetize.R;
import com.example.robi.budgetize.backend.APIs.viewpagerforbankaccounts.CardItem;
import com.example.robi.budgetize.backend.viewmodels.BankAccountViewModel;
import com.example.robi.budgetize.data.remotedatabase.remote.oauth1.lib.OBPRestClient;

import java.util.ArrayList;
import java.util.List;


public class LinkBanksCardPagerAdapter extends PagerAdapter implements LinkedBanksCardAdapter {

    private List<CardView> mViews;
    private List<CardItem> mData;
    private float mBaseElevation;
    private Context activityContext;

    //OBP ModelView
    private BankAccountViewModel bankAccountViewModel;

    public LinkBanksCardPagerAdapter(Context activityContext, BankAccountViewModel bankAccountViewModel) {
        mData = new ArrayList<>();
        mViews = new ArrayList<>();
        this.activityContext = activityContext;
        this.bankAccountViewModel = bankAccountViewModel;
    }

    public List<CardItem> getAllCardData(){
        return mData;
    }

    public void addCardItem(CardItem item) {
        mViews.add(null);
        mData.add(item);
    }

    public boolean existCardItem(String selectedBank) {
        for (CardItem cardItem : mData) {
            if (cardItem.getTitle() != null && cardItem.getTitle().equals(selectedBank)) {
                //Log.d("Msg","TEXTUL ESTE: "+selectedBank +"SI " + cardItem.getTitle());
                return true;
            }
        }
        return false;
    }

    public float getBaseElevation() {
        return mBaseElevation;
    }

    public SparseArray<String> getBankNameAndDataPosition() {
        SparseArray<String> bankNames = new SparseArray<String>();
        for (int i = 0; i < mData.size(); i++) {
            if (mData.get(i).getTitle() != null) {
                bankNames.put(i, mData.get(i).getTitle());
            }
        }
        return bankNames;
    }

    @Override
    public CardView getCardViewAt(int position) {
        return mViews.get(position);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    private void createDialogClickListener(long bankID) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE: {
                        try {
                            OBPRestClient.clearAccessToken(bankID);
                            bankAccountViewModel.deleteLinkedBank(bankID);
                            Toast.makeText(activityContext, "Bank Account Deleted", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.out.println(e.getMessage());
                            Toast toast = Toast.makeText(activityContext, "Unable to delete the Bank Account", Toast.LENGTH_SHORT);
                            TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                            v.setTextColor(Color.RED);
                            toast.show();
                        }


                        //Yes button clicked
                        break;
                    }

                    case DialogInterface.BUTTON_NEGATIVE: {
                        //No button clicked
                        break;
                    }
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(activityContext);
        builder.setMessage("Are you sure you want do DELETE permanently this Bank Account?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View view = LayoutInflater.from(container.getContext())
                .inflate(R.layout.bankaccount_adapter, container, false);
        container.addView(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                if (mData.get(position).getTitle() != null) {
                    //TODO: request to the server asking for auth to the corresponding bank. Have to send an object which contains bank name.(mData.get(position) + the position
                    String requestPath = "/authStatus";
                    HttpUrl.Builder httpBuilder = HttpUrl.parse(AppConstants.SERVER_URL + requestPath).newBuilder();
                    httpBuilder.addQueryParameter("BankName", mData.get(position).getTitle());

                    Request request = new Request.Builder()
                            .url(httpBuilder.build())
                            .build();

                    HttpUtils.getClient().newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {

                            Log.d("Error: ", "exception at POST" + e.toString());
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(Call call, final Response response) throws IOException {
                            if (!response.isSuccessful()) {
                                throw new IOException("Unexpected code " + response);
                            } else {
                                Log.d("RESPONSE RECEIVED: ", response.body().string());
                                // do something wih the result
                            }
                        }
                    });

                }
                 */
                //TODO: make a query in LinkedBankDao to retrieve directly the banklogo(aka OBP BANK ID)
                bankAccountViewModel.getAccounts(mData.get(position).getBankID());//, bankAccountViewModel.getLinkedBankOBPID(mData.get(position).getBankID()));
            }
        });
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                createDialogClickListener(mData.get(position).getBankID());
                return true;
            }
        });

        Button button = (Button) view.findViewById(R.id.bank_account_sync_button);
        ImageView icnView = (ImageView) view.findViewById(R.id.bank_link_img_status);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent myIntent = new Intent(activityContext, OAuthActivity.class);
//                activityContext.startActivity(myIntent);
                if (bankAccountViewModel.getLinkedBankStatus(mData.get(position).getBankID()).contentEquals("LINKED")) {
                    bankAccountViewModel.unlinkBankAccount(mData.get(position).getBankID());
                    updateUICard_BankLinked(button, icnView, position);
//                    servicesHandlerViewModel.getAccounts();
                } else {
//                    need to do the OAUTH
                    BankAccountViewModel.lastClickedBankID = mData.get(position).getBankID();
                    bankAccountViewModel.doOBPOAuth();//move this to account linking(inside of linkedbankaccount
                }
            }
        });
        updateUICard_BankLinked(button, icnView, position);

        bind(mData.get(position), view);
        CardView cardView = (CardView) view.findViewById(R.id.cardView);

        if (mBaseElevation == 0) {
            mBaseElevation = cardView.getCardElevation();
        }

        cardView.setMaxCardElevation(mBaseElevation * MAX_ELEVATION_FACTOR);
        mViews.set(position, cardView);
        return view;
    }

    private void updateUICard_BankLinked(Button button, ImageView icnView, int position) {
        if(bankAccountViewModel.getLinkedBankStatus(mData.get(position).getBankID()).contentEquals("LINKED")) {
            button.setText("Press To UNLink\nBank Account");
            icnView.setImageResource(R.drawable.ic_bookmark_linked_24dp);
            long bankID = mData.get(position).getBankID();

//            if(!OBPRestClient.consumers.containsKey(mData.get(position).getBankID())){
//                OBPRestClient.retrieveConsumer(bankID);
//            }
//            if(!OBPRestClient.providers.containsKey(mData.get(position).getBankID())) {
//                OBPRestClient.retrieveProvider(bankID);
//            }
        }else{
            button.setText("Press To Link\nBank Account");
            icnView.setImageResource(R.drawable.ic_bookmark_unlinked_24dp);
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        mViews.set(position, null);
    }

    private void bind(CardItem item, View view) {
        TextView titleTextView = (TextView) view.findViewById(R.id.titleTextView);
        TextView contentTextView = (TextView) view.findViewById(R.id.contentTextView);
        titleTextView.setText(item.getTitle());
        contentTextView.setText(item.getText());
    }

    //COMMON FUNCTIONS FOR THIS ACTIVITY
    public void startActivity(Class targetClass) {
        Intent myIntent = new Intent(activityContext, targetClass);
        activityContext.startActivity(myIntent);
        //activityContext.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}
