package com.example.robi.investorsapp.activities;


import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.room.Room;
import androidx.viewpager.widget.ViewPager;

import com.example.robi.investorsapp.R;
import com.example.robi.investorsapp.activities.createActivities.CreateWalletActivity;
import com.example.robi.investorsapp.adapters.viewpager.WalletViewPagerAdapter;
import com.example.robi.investorsapp.localdatabase.DaoAbstract;
import com.example.robi.investorsapp.localdatabase.entities.wallet.Wallet;
import com.example.robi.investorsapp.services.DoOAuthService;
import com.example.robi.investorsapp.services.DownloadBankImagesService;
import com.example.robi.investorsapp.services.RetrieveBanksService;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    //google sign in
    GoogleSignInAccount account = null;
    GoogleSignInClient mGoogleSignInClient = null;

    //OBP
    boolean obpOAuthOK = false;

    ViewPager viewPager;
    WalletViewPagerAdapter adapter;
    TextView introText;
    ImageView introArrow;
    Integer[] colors = null;
    public static List<Wallet> wallets = new ArrayList<Wallet>();
    public static DaoAbstract myDatabase;
    public static int lastWalletPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(mMessageReceiver,
                        new IntentFilter("my-integer"));
        init_login();
        getLocalDB();
        doJobs();
        init_screen();
        askForPermissions();
        startServices();
    }
    final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    private void askForPermissions() {
            // Here, thisActivity is the current activity
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                // Permission is not granted
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                } else {
                    // No explanation needed; request the permission
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            } else {
                // Permission has already been granted
            }
        }

    @Override
    protected void onPause() {
        // Unregister since the activity is not visible
        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(mMessageReceiver);
        super.onPause();
    }

    // Handling messages from Services
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            deactivateLoader();
            // Extract data included in the Intent
            //int yourInteger = intent.getIntExtra("message",-1/*default value*/);
            String serviceResponse = intent.getStringExtra("message");

            if(serviceResponse!=null){
                if(serviceResponse.contentEquals("needAuth")) {
                    showBankAccountNeedActions();
                }else if(serviceResponse.contentEquals("Authorized")){
                    Log.d("Authorized successfull","!");
                    obpOAuthOK = true;
                    getAllBanksService();
                }else if(serviceResponse.contentEquals("BanksAdded")) {
                    Log.d("GetBanks successfull","!");
                    syncAllImagesService();
                }else if(serviceResponse.contentEquals("BanksNOTAdded")) {
                    Log.d("GetBanks NOT successfull","!");
                }else if(serviceResponse.contentEquals("syncImagesCompleted")){
                    Log.d("syncImages Completed","!");
                }
            }else{
                Log.d("Programming error in "+this.getClass().getName()+" ","EMPTY RESPONSE FROM "+intent.getClass().getName());
            }
        }
    };

    private void showBankAccountNeedActions() {
        ImageButton bankAccounts = (ImageButton) this.findViewById(R.id.bank_accounts_button);
        bankAccounts.setImageResource(R.drawable.account_action_to_take);
    }

    //ORDER OF SERVICES:

    //1st Service
    private void startServices() {
        //1.Check if Auth available
        checkOBPOAuthStatus();
        // and save/delete image logos
    }

    //2nd Service
    private void getAllBanksService() {
        if(obpOAuthOK){
            //if it is available we can download banks
            Intent serviceIntent = new Intent(this, RetrieveBanksService.class);
            serviceIntent.putExtra("getAllBanks",true);
            startService(serviceIntent);
        }
    }

    //3nd Service
    private void syncAllImagesService() {
        Intent serviceIntent = new Intent(this, DownloadBankImagesService.class);
        serviceIntent.putExtra("syncImages",true);
        startService(serviceIntent);
    }

    private void checkOBPOAuthStatus() {
        Intent serviceIntent = new Intent(this, DoOAuthService.class);
        serviceIntent.putExtra("checkStatus",true);
        startService(serviceIntent);
    }

    private void doOBPOAuth() {
        Intent serviceIntent = new Intent(this, DoOAuthService.class);
        serviceIntent.putExtra("doLogin",true);
        activateLoader();
        startService(serviceIntent);
    }

    private void activateLoader(){
        ProgressBar accLoader = (ProgressBar) findViewById(R.id.progressAccountLoad);
        accLoader.setVisibility(View.VISIBLE);
    }
    private void deactivateLoader(){
        ProgressBar accLoader = (ProgressBar) findViewById(R.id.progressAccountLoad);
        accLoader.setVisibility(View.GONE);
    }

    private void init_login() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        account = GoogleSignIn.getLastSignedInAccount(this);
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        updateUI(account);
    }

    private boolean isLoggedIn() {
        if (account != null)
            return true;
        else
            return false;
    }

    private boolean isSynchedToCloud() {
        /*
         * Compare local data with data from cloud to see if it's sync
         * if sync return true
         * if desync return false
         * */
        return false;
    }

    private void getLocalDB() {
        myDatabase = Room.databaseBuilder(getApplicationContext(), DaoAbstract.class, "walletDB").allowMainThreadQueries().build();
    }

    private void doJobs() {
        try {
            doDateJob();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void doDateJob() throws ParseException {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM");//dd/MM/yyyy
        Date date1 = new Date();
        String strDate = sdfDate.format(date1);
        String strdate2 = myDatabase.walletDao().getLatestDate();


        if (strdate2 != null) {


            date1 = sdfDate.parse(strDate);

            Date date2 = sdfDate.parse(strdate2);

            if (date1.compareTo(date2) > 0) {
                List<Wallet> wallets = myDatabase.walletDao().getAllWallets();

                for (Wallet w : wallets) {
                    myDatabase.walletDao().financialStatusUpdate(w.getId(), sdfDate.format(date1));
                }
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh_wallets();
    }

    private void init_screen() {
        init_introScreen();
        init_viewPager();
        init_listeners();
    }

    private void init_introScreen() {
        introText = (TextView) this.findViewById(R.id.intro_text);
        introArrow = (ImageView) this.findViewById(R.id.intro_arrow_imgview);
    }


    private void init_viewPager() {
        adapter = new WalletViewPagerAdapter(wallets, this);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);
        viewPager.setPadding(0, 0, 0, 0);
        viewPager.setOffscreenPageLimit(20);

//        Integer[] colors_temp = {
//                getResources().getColor(R.color.colorAccent),
//                getResources().getColor(R.color.colorPrimary),
//                getResources().getColor(R.color.colorPrimaryDark)};
//
//        colors = colors_temp;
        //adapter = new WalletViewPagerAdapter(wallets, this);

        refresh_tabs();

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });

    }

    private void refresh_tabs() {
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabDots);
        tabLayout.setupWithViewPager(viewPager, true);

        for (int i = 0; i < tabLayout.getTabCount(); i++) {


            View tab = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(i);
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) tab.getLayoutParams();
            p.setMargins(0, 0, 20, 0);
            tab.requestLayout();
        }
    }

    private void init_listeners() {
        //ADD WALLET FAB
        final FloatingActionButton addWalletFab = (FloatingActionButton) this.findViewById(R.id.add_wallet);
        addWalletFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                startActivity(CreateWalletActivity.class);
            }
        });

        //REMOVE WALLET FAB
        final FloatingActionButton removeWalletFab = (FloatingActionButton) this.findViewById(R.id.remove_wallet);
        removeWalletFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                createDialogClickListener();
            }
        });

        //CLOUD BUTTON
        ImageButton cloudButton = (ImageButton) this.findViewById(R.id.cloud_image_button);
        cloudButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.cloud_image_button:
                        signIn();
                        break;
                    // ...
                }
            }
        });

        //SETTINGS BUTTON
        ImageButton settingsButton = (ImageButton) this.findViewById(R.id.settings_button);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final View overlay_view = (View) MainActivity.this.findViewById(R.id.overlay_view);
                final View overlay = (View) MainActivity.this.findViewById(R.id.overlay);
                RelativeLayout mainScreenLayout = (RelativeLayout) MainActivity.this.findViewById(R.id.main_screen_layout);
                mainScreenLayout.setClickable(false);
                overlay_view.setAlpha(0f);
                overlay_view.setVisibility(View.VISIBLE);
                // Animate the content view to 100% opacity, and clear any animation
                // listener set on the view.
//                overlay_view.animate()
//                        .alpha(1f)
//                        .setDuration(shortAnimationDuration)
//                        .setListener(null);
//                transition.addTarget(overlay_view);
//                TransitionManager.beginDelayedTransition((ViewGroup)overlay_view.getParent(),transition);
//                overlay_view.setVisibility(View.VISIBLE);
//
//                transition.addTarget(overlay);
//                TransitionManager.beginDelayedTransition((ViewGroup)overlay.getParent(),transition);
//                overlay.setVisibility(View.VISIBLE);
                addWalletFab.setAlpha(0.6F);
                removeWalletFab.setAlpha(0.6F);
            }
        });

        //BANK ACCOUNTS BUTTON
        ImageButton bankAccounts = (ImageButton) this.findViewById(R.id.bank_accounts_button);
        bankAccounts.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(obpOAuthOK) {
                    startActivity(LinkedBankAccounts.class);
                }else{
                    //need to do the OAUTH
                    doOBPOAuth();
                }
            }
        });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 9001);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == 9001) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(MainActivity.class.toString(), "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    private void updateUI(GoogleSignInAccount account) {
        if (account != null) {
            if (isSynchedToCloud()) {
                ImageButton cloud = (ImageButton) this.findViewById(R.id.cloud_image_button);
                //cloud.setVisibility(View.GONE);
                cloud.setImageResource(R.drawable.synchedwhitecloud);
            } else {
                ImageButton cloud = (ImageButton) this.findViewById(R.id.cloud_image_button);
                //cloud.setVisibility(View.GONE);
                cloud.setImageResource(R.drawable.tosyncwhitecloud);
            }
        } else {
            ImageButton cloud = (ImageButton) this.findViewById(R.id.cloud_image_button);
            cloud.setImageResource(R.drawable.crossedwhitecloud);
        }
    }

    public void startActivity(Class targetClass) {
        lastWalletPosition = viewPager.getCurrentItem();
        Intent myIntent = new Intent(MainActivity.this, targetClass);
        MainActivity.this.startActivity(myIntent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void refresh_wallets() {
        wallets.clear();
        wallets.addAll(myDatabase.walletDao().getAllWallets());
        viewPager.setAdapter(adapter);
        refresh_tabs();//to keep the distance between bullets
        viewPager.setCurrentItem(lastWalletPosition);

        if (wallets.isEmpty()) {
            introText.setVisibility(View.VISIBLE);
            introArrow.setVisibility(View.VISIBLE);

            viewPager.setVisibility(View.INVISIBLE);
        } else {
            introText.setVisibility(View.INVISIBLE);
            introArrow.setVisibility(View.INVISIBLE);

            viewPager.setVisibility(View.VISIBLE);
        }

    }

    private void createDialogClickListener() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE: {
                        try {
                            myDatabase.walletDao().deleteWallet(wallets.get(viewPager.getCurrentItem()));
                            refresh_wallets();
                            Toast.makeText(MainActivity.this, "Wallet Deleted", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.out.println(e.getMessage());
                            Toast toast = Toast.makeText(MainActivity.this, "Unable to delete the Wallet", Toast.LENGTH_SHORT);
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

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want do DELETE permanently this Wallet?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

}
