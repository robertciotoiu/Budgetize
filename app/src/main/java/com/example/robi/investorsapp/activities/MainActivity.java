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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.room.Room;
import androidx.viewpager.widget.ViewPager;

import com.example.robi.investorsapp.ApplicationObj;
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
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.skydoves.elasticviews.ElasticButton;

import org.apache.commons.io.FileUtils;

import org.apache.http.client.ClientProtocolException;


import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes.SIGN_IN_FAILED;


public class MainActivity extends AppCompatActivity {
    final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;

    //Services response listener:
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        // Handling messages from Services
        @Override
        public void onReceive(Context context, Intent intent) {
            deactivateLoader();
            // Extract data included in the Intent
            //int yourInteger = intent.getIntExtra("message",-1/*default value*/);
            String serviceResponse = intent.getStringExtra("message");
            if (serviceResponse != null) {
                if (serviceResponse.contentEquals("needAuth")) {
                    showBankAccountNeedActions();
                } else if (serviceResponse.contentEquals("Authorized")) {
                    Log.d("Authorized successfull", "!");
                    obpOAuthOK = true;
                    getAllBanksService();
                } else if (serviceResponse.contentEquals("BanksAdded")) {
                    Log.d("GetBanks successfull", "!");
//                    Thread synchAllImagesThread = new Thread(new Runnable() {
//                        @Override
//                        public void run() {
                    syncAllImagesService();
//                        }
//                    });
//                    synchAllImagesThread.start();
                } else if (serviceResponse.contentEquals("BanksNOTAdded")) {
                    Log.d("GetBanks NOT successfull", "!");
                } else if (serviceResponse.contentEquals("syncImagesCompleted")) {
                    Log.d("syncImages Completed", "!");
                }
            } else {
                Log.d("Programming error in " + this.getClass().getName() + " ", "EMPTY RESPONSE FROM " + intent.getClass().getName());
            }
        }
    };

    //My Server
    private String serverBaseLink = "http://79.118.0.37:8080/";
    public String sessionID = null;

    //Google Sign In
    GoogleSignInAccount account = null;
    GoogleSignInClient mGoogleSignInClient = null;
    public String googleSignInTokenID = null;

    //OBP
    boolean obpOAuthOK = false;

    //Wallets Viewpager UI
    ViewPager viewPager;
    WalletViewPagerAdapter adapter;
    TextView introText;
    ImageView introArrow;

    //Biometric app authorization
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login for my app")
            .setSubtitle("Log in using your biometric credential")
            .setNegativeButtonText("Use account password")
            .build();

    //public variables across the application
    public static DaoAbstract myDatabase;//through this variable we communicate with local room database(SQL LITE)
    public static List<Wallet> wallets = new ArrayList<Wallet>();
    public static int lastWalletPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //app authentication
        try {
            appAuthorization();
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh_wallets();
    }

    @Override
    protected void onPause() {
        // Unregister since the activity is not visible
        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(mMessageReceiver);
        super.onPause();
    }

    //Biometric app Authorization
    private void appAuthorization() throws Exception {
        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate()) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                Log.d("MY_APP_TAG", "App can authenticate using biometrics.");
                biometricLogin();
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Log.e("MY_APP_TAG", "No biometric features available on this device.");
                throw new Exception("No biometrics available");//should handle with other type of authorization(passcode)
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Log.e("MY_APP_TAG", "Biometric features are currently unavailable.");
                throw new Exception("No biometrics available");//should handle with other type of authorization(passcode)
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                Log.e("MY_APP_TAG", "The user hasn't associated " +
                        "any biometric credentials with their account.");
                throw new Exception("No biometrics available");
        }
    }

    private void biometricLogin() {
        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(MainActivity.this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(),
                        "Authentication error: " + errString, Toast.LENGTH_SHORT)
                        .show();
                biometricPrompt.cancelAuthentication();
                authUpdateUI(false);
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(getApplicationContext(),
                        "Authentication succeeded!", Toast.LENGTH_SHORT).show();
                authUpdateUI(true);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "Authentication failed",
                        Toast.LENGTH_SHORT)
                        .show();
                biometricPrompt.cancelAuthentication();
                authUpdateUI(false);
            }
        });
        // Prompt appears when user clicks "Log in".
        // Consider integrating with the keystore to unlock cryptographic operations,
        // if needed by your app.
        biometricPrompt.authenticate(promptInfo);
    }

    private void authUpdateUI(boolean status) {
        RelativeLayout screenLayout = this.findViewById(R.id.main_screen_layout);
        RelativeLayout authLayout = this.findViewById(R.id.auth_layout);
        if (status) {
            authLayout.setVisibility(View.GONE);
            screenLayout.setVisibility(View.VISIBLE);
        } else {
            screenLayout.setVisibility(View.GONE);
            authLayout.setVisibility(View.VISIBLE);
        }

    }

    private void askForPermissions() {
        //delete all
        File dir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + ApplicationObj.getAppContext().getPackageName()
                + "/BankIcons");
//        try {
//            FileUtils.deleteDirectory(dir);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
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

    private void showBankAccountNeedActions() {
        ImageButton bankAccounts = (ImageButton) this.findViewById(R.id.bank_accounts_button);
        bankAccounts.setImageResource(R.drawable.account_action_to_take);
    }

    //ORDER OF SERVICES(Related to OBP API):

    //1st This start all the services
    private void startServices() {
        //1.Check if Auth available
        checkOBPOAuthStatus();
    }

    //1st Service
    private void checkOBPOAuthStatus() {
        Intent serviceIntent = new Intent(this, DoOAuthService.class);
        serviceIntent.putExtra("checkStatus", true);
        startService(serviceIntent);
    }

    //2nd Service
    private void getAllBanksService() {
        if (obpOAuthOK) {
            //if it is available we can download banks
            Intent serviceIntent = new Intent(this, RetrieveBanksService.class);
            serviceIntent.putExtra("getAllBanks", true);
            startService(serviceIntent);
        }
    }

    //3nd Service
    private void syncAllImagesService() {
        Intent serviceIntent = new Intent(this, DownloadBankImagesService.class);
        serviceIntent.putExtra("syncImages", true);
        startService(serviceIntent);
    }

    private void doOBPOAuth() {
        Intent serviceIntent = new Intent(this, DoOAuthService.class);
        serviceIntent.putExtra("doLogin", true);
        activateLoader();
        startService(serviceIntent);
    }

    private void activateLoader() {
        ProgressBar accLoader = (ProgressBar) findViewById(R.id.progressAccountLoad);
        accLoader.setVisibility(View.VISIBLE);
    }

    private void deactivateLoader() {
        ProgressBar accLoader = (ProgressBar) findViewById(R.id.progressAccountLoad);
        accLoader.setVisibility(View.GONE);
    }

    //OBP
    private void revokeOBPAccess() {

    }

    //INITIALIZERS:

    private void init_listeners() {
        //biometric auth button
        Button biometricLoginButton = this.findViewById(R.id.biometric_LOGIN);
        biometricLoginButton.setOnClickListener(view -> {
            biometricPrompt.authenticate(promptInfo);
        });

        //ADD WALLET FAB
        final ElasticButton addWalletFab = (ElasticButton) this.findViewById(R.id.add_wallet);
        addWalletFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(CreateWalletActivity.class);
            }
        });

        //REMOVE WALLET FAB
        final ElasticButton removeWalletFab = (ElasticButton) this.findViewById(R.id.remove_wallet);
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
                    case R.id.cloud_image_button: {
                        if (googleSignInTokenID == null) {
                            signIn();
                        } else {
                            //TODO: Implement synch between local database and server database
                            doSynch();
                        }
                    }
                    break;
                    case R.id.settings_button: {
                        //TODO:I'm using temporary this as Google SIGN OUT! PAY ATTENTION TO THIS
                        signOut();
                        break;
                    }
                }
            }
        });

        //signout BUTTON
        ImageButton analyticsButton = (ImageButton) this.findViewById(R.id.analyticsButton);
        analyticsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                signOut();
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
                if (obpOAuthOK) {
                    startActivity(LinkedBankAccounts.class);
                } else {
                    //need to do the OAUTH
                    doOBPOAuth();
                }
            }
        });
    }

    //Database sync

    private void doSynch() {

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

    private void init_login() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        if ((account = GoogleSignIn.getLastSignedInAccount(this)) != null) {
            googleSignInTokenID = account.getIdToken();
            updateUI(account);
        } else {
            //do nothing
        }
    }

    //GOOGLE SIGN-IN

    private void refreshIdToken() {
        // Attempt to silently refresh the GoogleSignInAccount. If the GoogleSignInAccount
        // already has a valid token this method may complete immediately.
        //
        // If the user has not previously signed in on this device or the sign-in has expired,
        // this asynchronous branch will attempt to sign in the user silently and get a valid
        // ID token. Cross-device single sign on will occur in this branch.
        mGoogleSignInClient.silentSignIn()
                .addOnCompleteListener(this, new OnCompleteListener<GoogleSignInAccount>() {
                    @Override
                    public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                        handleSignInResultWithToken(task);
                    }
                });
    }

    private void handleSignInResultWithToken(Task<GoogleSignInAccount> completedTask) {
        try {
            account = completedTask.getResult(ApiException.class);
            googleSignInTokenID = account.getIdToken();
            //we send to validate user on our server and to obtain SessionID
            Thread synchAllImagesThread = new Thread(this::serverValidateUser);
            synchAllImagesThread.start();
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.d(MainActivity.class.toString(), "signInResult:failed code=" + GoogleSignInStatusCodes.getStatusCodeString(e.getStatusCode()));
            updateUI(null);
        }
    }

    //    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
//        try {
//            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
//            googleSignInTokenID = account.getIdToken();
//            // Signed in successfully, show authenticated UI.
//            updateUI(account);
//        } catch (ApiException e) {
//            // The ApiException status code indicates the detailed failure reason.
//            // Please refer to the GoogleSignInStatusCodes class reference for more information.
//            Log.d(MainActivity.class.toString(), "signInResult:failed code=" + GoogleSignInStatusCodes.getStatusCodeString(e.getStatusCode()));
//            updateUI(null);
//        }
//    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == 9001) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            //TODO: send ID Token to server and validate

            handleSignInResultWithToken(task);
        } else if (requestCode == 9002) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResultWithToken(task);
        }
    }

    private void updateUI(GoogleSignInAccount account) {
        if (account != null) {
            if (isSynchedToCloud()) {
                ImageButton cloud = (ImageButton) this.findViewById(R.id.cloud_image_button);
                //cloud.setVisibility(View.GONE);
                cloud.setImageResource(R.drawable.synchedwhitecloud);
                Log.d("TOKENID: ", account.getIdToken());
            } else {
                ImageButton cloud = (ImageButton) this.findViewById(R.id.cloud_image_button);
                //cloud.setVisibility(View.GONE);
                cloud.setImageResource(R.drawable.tosyncwhitecloud);
                Log.d("TOKENID: ", account.getIdToken());
            }
            ImageButton analyticsButton = (ImageButton) this.findViewById(R.id.analyticsButton);
            analyticsButton.setVisibility(View.VISIBLE);

            ImageButton cardsButton = (ImageButton) this.findViewById(R.id.bank_accounts_button);
            cardsButton.setVisibility(View.VISIBLE);
        } else {
            ImageButton cloud = (ImageButton) this.findViewById(R.id.cloud_image_button);
            cloud.setImageResource(R.drawable.crossedwhitecloud);

            ImageButton analyticsButton = (ImageButton) this.findViewById(R.id.analyticsButton);
            analyticsButton.setVisibility(View.GONE);

            ImageButton cardsButton = (ImageButton) this.findViewById(R.id.bank_accounts_button);
            cardsButton.setVisibility(View.GONE);
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 9001);
    }

    private void signOut() {
        mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                account = null;
                googleSignInTokenID = null;
                updateUI(null);
                revokeAccess();
            }
        });
        revokeOBPAccess();
    }

    private void revokeAccess() {
        mGoogleSignInClient.revokeAccess().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(null);
                    }
                });
    }

    private boolean isLoggedIn() {
        if (account != null)
            return true;
        else
            return false;
    }

    //CLOUD SYNCH

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

    //Communication with my REST Server

    public void serverValidateUser() {
        new ValidateUserTask(new OnTaskCompleted() {
            @Override
            public void onTaskCompleted(String responseBody) {
                if (responseBody == null) {
                    signOut();
                } else {
                    sessionID = responseBody;
                    MainActivity.this.runOnUiThread(() -> {
                        updateUI(account);
                        Log.d("UI thread", "I am the UI thread");
                    });
                }
            }
        },googleSignInTokenID).doInBackground();
    }

    public interface OnTaskCompleted {
        void onTaskCompleted(String responseBody);
    }

    class ValidateUserTask {
        String googleSignInTokenID;
        private Exception exception;
        OnTaskCompleted listener;

        public ValidateUserTask(OnTaskCompleted listener,String googleSignInTokenID) {
            this.listener = listener;
            this.googleSignInTokenID = googleSignInTokenID;
        }

        protected String doInBackground(String... urls) {
            try {
                OkHttpClient client = new OkHttpClient();
                RequestBody formBody = new FormBody.Builder()
                        .add("tokenID", googleSignInTokenID)
                        .build();

                Request request = new Request.Builder()
                        .url(serverBaseLink + "/validateUser")
                        .post(formBody)
                        .build();
                Call call = client.newCall(request);
                Response response = call.execute();
                onPostExecute(response.body().string());
            } catch (ClientProtocolException e) {
                Log.e("TokenIDtoServer", "Error sending ID token to backend.", e);
            } catch (IOException e) {
                Log.e("TokenIDtoServer", "Error sending ID token to backend.", e);
            }
            return null;
        }
        protected void onPostExecute(String response) {
            // TODO: check this.exception
            // TODO: do something with the feed
            listener.onTaskCompleted(response);
        }
    }

    //JOBS

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

    //WALLET UI

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

    //COMMON FUNCTIONS FOR THIS ACTIVITY
    public void startActivity(Class targetClass) {
        lastWalletPosition = viewPager.getCurrentItem();
        Intent myIntent = new Intent(MainActivity.this, targetClass);
        MainActivity.this.startActivity(myIntent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

}
