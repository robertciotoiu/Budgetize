package com.example.robi.budgetize.ui.activities;


import android.Manifest;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.example.robi.budgetize.ApplicationObj;
import com.example.robi.budgetize.R;
import com.example.robi.budgetize.backend.viewmodels.BankAccountViewModel;
import com.example.robi.budgetize.backend.viewmodels.MainActivityViewModel;
import com.example.robi.budgetize.backend.viewmodels.ServicesHandlerViewModel;
import com.example.robi.budgetize.backend.viewmodels.factories.BankAccountViewModelFactory;
import com.example.robi.budgetize.backend.viewmodels.factories.MainActivityViewModelFactory;
import com.example.robi.budgetize.backend.viewmodels.factories.ServicesHandlerViewModelFactory;
import com.example.robi.budgetize.data.localdatabase.LocalRoomDatabase;
import com.example.robi.budgetize.data.localdatabase.entities.Wallet;
import com.example.robi.budgetize.ui.activities.createActivities.CreateWalletActivity;
import com.example.robi.budgetize.ui.adapters.viewpager.WalletViewPagerAdapter;
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
import com.wajahatkarim3.roomexplorer.RoomExplorer;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

//Strong features of the application:

/**
 * sync bank accounts and import transactions
 * sync data to google account
 * display reports of expenses
 * personal advisor to reduce unnecessarly payments.
 * revolutionary design elements
 */

public class MainActivity extends AppCompatActivity {
    ThreadPoolExecutor localDBThreads = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);

    final int WRITE_EXTERNAL_STORAGE_PERMISSION = 1;
    public static List<Wallet> wallets = new ArrayList<Wallet>();
    //public static int lastWalletPosition = 0;
    TabLayout tabLayout;

    //MVVM
    private MainActivityViewModel mainActivityViewModel;
    private BankAccountViewModel bankAccountViewModel;
    private ServicesHandlerViewModel servicesHandlerViewModel;
    private Observer<List<Wallet>> walletListObsever;

    //My Server
    private String serverBaseLink = "ADD_HERE_BACKEND_SERVER_IP";//ACASA
    public String sessionID = null;

    //Google Sign In
    GoogleSignInAccount account = null;
    GoogleSignInClient mGoogleSignInClient = null;
    public String googleSignInTokenID = null;

    //Wallets Viewpager UI
    ViewPager viewPager;
    WalletViewPagerAdapter adapter;
    TextView introText;
    ImageView introArrow;

    // Experimental results variables
    long startTimeonCreateonResumeFlows = 0;
    long endTimeonCreateonResumeFlows = 0;

    long startTimeGoogleSignInFlow = 0;
    long endTimeGoogleSignInFlow = 0;

    //Biometric app authentication
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login")
            .setSubtitle("Log in using your biometric credential")
            .setNegativeButtonText("Use account password")
            .build();

    @SuppressLint("ShowToast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        startTimeonCreateonResumeFlows = System.currentTimeMillis();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Initialize the ViewModels
        initViewModels();
        // Request permissions
        askForPermissions();
        // Start the biometric authentication and restrict the access to the application
        try {
            appAuthentication();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG);
        }
        // Download get all new transactions from the linked bank accounts
        doLogicOnFirstAppStart();
        // Check if the user is already logged in with the Google Account
        // if yes, try to get a session ID from the backend server
        // in order to be able to use the Special Features
        initLogin();
        // Initialize all buttons listeners
        initListeners();
        // Initialize elements for empty screen case
        initIntroScreen();
        // Retrieve from exchangeratesAPI all currency rates from today
        MainActivityViewModel.initDummyCurrencies();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Initialize the ViewPager
        initViewPager();
        // Initialize the wallet list observer
        // At every Wallet change in the database,
        // we refresh the wallet list and the viewpager
        walletListObsever = wallets -> {
            MainActivity.wallets.clear();
            MainActivity.wallets.addAll(wallets);
            adapter = new WalletViewPagerAdapter(wallets, MainActivity.this, mainActivityViewModel);
            adapter.notifyDataSetChanged();
            viewPager.setAdapter(adapter);
            viewPager.setCurrentItem(mainActivityViewModel.lastWalletPosition);
            mainActivityViewModel.lastWalletPosition = 0;
            tabLayout.setupWithViewPager(viewPager, true);
            MainActivity.this.refresh_tabs();
            // Activate/Deactivate intro screen
            // based on the number of wallets
            if (wallets.size() == 0) {
                activateDeactivateIntroScreen(true);
            } else {
                activateDeactivateIntroScreen(false);
                refresh_wallets();
                // Animate the first page's progress bar
                if(!MainActivityViewModel.firstStart) {
                    animate_firstPagePb();
                }
            }
            // animate the progress bar from the first page
            //animate_firstPagePb();
        };
        // we register the wallet list observer
        mainActivityViewModel.getAllWallets().observe(this, walletListObsever);
        endTimeonCreateonResumeFlows = System.currentTimeMillis();
        long seconds = TimeUnit.MILLISECONDS.toSeconds(endTimeonCreateonResumeFlows - startTimeonCreateonResumeFlows);
        Log.d(" Experimental Results:", "Processing time of onCreate and onResume methods: " + (endTimeonCreateonResumeFlows - startTimeonCreateonResumeFlows));
    }

    // Method which can catch Key events
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                // Check if the event is related to the volume down button
            case KeyEvent.KEYCODE_VOLUME_DOWN: {
                // Check if the caught event is indicating the press action,
                // not the release action.
                if (event.getAction() != KeyEvent.ACTION_DOWN) {
                    // Start the activity of Room Explorer,
                    // giving as parameter also the class of our Room Database
                    // and the name of our application database
                    RoomExplorer.show(this,
                            LocalRoomDatabase.class,
                            "budgetize-db");
                    return true;
                }
            }
            default:
                return super.dispatchKeyEvent(event);
        }
    }

    private void initViewModels() {
        mainActivityViewModel = new ViewModelProvider(this
                , new MainActivityViewModelFactory((ApplicationObj) this.getApplication()))
                .get(MainActivityViewModel.class);

        bankAccountViewModel = new ViewModelProvider(this,
                new BankAccountViewModelFactory((ApplicationObj) this.getApplication()))
                .get(BankAccountViewModel.class);

        servicesHandlerViewModel = new ViewModelProvider(this,
                new ServicesHandlerViewModelFactory((ApplicationObj) this.getApplication()))
                .get(ServicesHandlerViewModel.class);
        // Get a list of all available banks
        servicesHandlerViewModel.getAllAvailableBanks();
    }

    public void doLogicOnFirstAppStart() {
        tabLayout = (TabLayout) findViewById(R.id.tabDots);
        bankAccountViewModel.appFirstStartLogicInit();
    }

    @Override
    public void onStop() {
        super.onStop();
        wallets = new ArrayList<Wallet>();
    }

    @Override
    public void onPause() {
        super.onPause();
        mainActivityViewModel.getAllWallets().removeObserver(walletListObsever);
    }

    //Biometric app Authentication
    private void appAuthentication() throws Exception {
        BiometricManager biometricManager = BiometricManager.from(this);
        // Check if the device contains any biometric hardware available
        // or if there is any biometric login registered to the device
        switch (biometricManager.canAuthenticate()) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                Log.d("MY_APP_TAG", "App can authenticate using biometrics.");
                biometricLogin();
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Log.e("MY_APP_TAG", "No biometric features available on this device.");
                throw new Exception("No biometrics available");
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Log.e("MY_APP_TAG", "Biometric features are currently unavailable.");
                throw new Exception("No biometrics available");
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                Log.e("MY_APP_TAG", "The user hasn't associated " +
                        "any biometric credentials with their account.");
                throw new Exception("No biometrics available");
        }
    }

    private void biometricLogin() {
        // There we prepare the biometric dialog
        // informing the user about the steps which he have to perform
        executor = ContextCompat.getMainExecutor(this);
        // We register a callback to make the application react
        // to a successful or unsuccessful validation
        biometricPrompt = new BiometricPrompt(MainActivity.this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                // We close the dialog and we inform the User that
                // the authentication has failed
                Toast.makeText(getApplicationContext(),
                        "Authentication error: " + errString, Toast.LENGTH_SHORT)
                        .show();
                biometricPrompt.cancelAuthentication();
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(getApplicationContext(),
                        "Authentication succeeded!", Toast.LENGTH_SHORT).show();
                authUpdateUI(true);
                animate_firstPagePb();
                MainActivityViewModel.firstStart=false;
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "Authentication failed",
                        Toast.LENGTH_SHORT)
                        .show();
                biometricPrompt.cancelAuthentication();
            }
        });
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
        // We check to see if we have permission to write on external storage
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // If we don't have the permission, we need to ask the user for it
            // This code will pop-up a request message to the user's screen
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    WRITE_EXTERNAL_STORAGE_PERMISSION);
        } else {
            servicesHandlerViewModel.syncAllImages();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NotNull String[] permissions,
                                           @NotNull int[] grantResults) {
        if (requestCode == WRITE_EXTERNAL_STORAGE_PERMISSION) {
            // Parse all the request permissions results
            for (int i = 0, len = permissions.length; i < len; i++) {
                String permission = permissions[i];
                // User denied the access permission
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    boolean showRationale = shouldShowRequestPermissionRationale(permission);
                    if (!showRationale) {
                    } else if (Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(permission)) {
                        // Here we show a new message to the user
                        // explaining why it is important to allow WRITE_EXTERNAL_STORAGE permission
                        showRationaleExtDev();
                    }
                } else {
                    servicesHandlerViewModel.syncAllImages();
                }
            }
        }
    }

    private void showRationaleExtDev() {
        // Create new onClickListener for Yes and No buttons
        DialogInterface.OnClickListener dialogClickListener =
                (dialog, which) -> {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE: {
                            // Yes button clicked
                            // Do nothing
                            break;
                        }
                        case DialogInterface.BUTTON_NEGATIVE: {
                            // No button clicked
                            // We display the ask permission again to the user
                            askForPermissions();
                            break;
                        }
                    }
                };
        // Build the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Set the message, the onClick listeners and show the dialog
        builder.setMessage(getString(R.string.permission_denied_write_external_storage))
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    private void showBankAccountNeedActions() {
        ImageButton bankAccounts = (ImageButton) this.findViewById(R.id.bank_accounts_button);
        bankAccounts.setImageResource(R.drawable.account_action_to_take);
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
    private void initListeners() {
        // Biometric auth BUTTON
        Button biometricLoginButton = this.findViewById(R.id.biometric_LOGIN);
        biometricLoginButton.setOnClickListener(view -> {
            try {
                appAuthentication();
            } catch (Exception e) {
                e.printStackTrace();
            }
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
                            createSignOutDialog();
                        }
                    }
                    break;
                }
            }
        });

//         SIGNOUT BUTTON
//        ImageButton analyticsButton = (ImageButton) this.findViewById(R.id.analyticsButton);
//        analyticsButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                signOut();
//            }
//        });

        //SETTINGS BUTTON
        ImageButton settingsButton = (ImageButton) this.findViewById(R.id.settings_button);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //hide other layouts
//                RelativeLayout mainScreenLayout = (RelativeLayout) MainActivity.this.findViewById(R.id.main_screen_layout);
//                RelativeLayout overlayLayout = (RelativeLayout) MainActivity.this.findViewById(R.id.overlay_layout);
//                overlayLayout.bringToFront();
//                overlayLayout.setTranslationZ(1);
                //mainScreenLayout.setVisibility(View.GONE);

                //final View overlay_view = (View) MainActivity.this.findViewById(R.id.overlay_view);
                //final View overlay = (View) MainActivity.this.findViewById(R.id.overlay);
                //RelativeLayout mainScreenLayout = (RelativeLayout) MainActivity.this.findViewById(R.id.main_screen_layout);
                //mainScreenLayout.setClickable(false);
                //overlay_view.setAlpha(0f);
                //overlay_view.setVisibility(View.VISIBLE);
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
                //addWalletFab.setAlpha(0.6F);
                //removeWalletFab.setAlpha(0.6F);
            }
        });

        //BANK ACCOUNTS BUTTON
        ImageButton bankAccounts = (ImageButton) this.findViewById(R.id.bank_accounts_button);
        bankAccounts.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(LinkedBankAccountsActivity.class);
            }
        });
    }

    //Database sync
    private void doSynch() {
        //

    }

    private void initIntroScreen() {
        introText = (TextView) this.findViewById(R.id.intro_text);
        introArrow = (ImageView) this.findViewById(R.id.intro_arrow_imgview);
    }

    private void activateDeactivateIntroScreen(boolean shouldActivate) {
        if (shouldActivate) {
            introText.setVisibility(View.VISIBLE);
            introArrow.setVisibility(View.VISIBLE);
        } else {
            introText.setVisibility(View.GONE);
            introArrow.setVisibility(View.GONE);
        }
    }

    private void initViewPager() {
        // Initialize the adapter with all the wallets
        adapter = new WalletViewPagerAdapter(wallets, this, mainActivityViewModel);
        // Initialize the viewpager
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        // Set the adapter to the Viewpager
        viewPager.setAdapter(adapter);
        viewPager.setPadding(0, 0, 0, 0);
        viewPager.setOffscreenPageLimit(20);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                //When a page is selected, we load the data on it and we animate the progress bar
                ProgressBar pb = new ProgressBar(MainActivity.this);
                for (View v : viewPager.getTouchables())
                    if (v instanceof ProgressBar) {
                        pb = (ProgressBar) v;
                    }
                double financialStatus = wallets.get(i).getFinancialStatus();
                double financialGoal = wallets.get(i).getFinancialGoal();
                int percentage = (int) ((financialStatus / financialGoal) * 100);
                ObjectAnimator animation = ObjectAnimator.ofInt(pb, "progress", percentage);
                animation.setDuration(percentage * 16);//for 50%: 800 is great except small ones.
                animation.setInterpolator(new AccelerateDecelerateInterpolator());//new FastOutSlowInInterpolator());
                animation.start();
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
    }

    private void initLogin() {
        //Build the GoogleSignInOptions
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        // We check if there if the User already Signed-in with Google
        if ((account = GoogleSignIn.getLastSignedInAccount(this)) != null) {
            googleSignInTokenID = account.getIdToken();
            // We found google account linked to the application
            // Validate on server to obtain session ID.
            // (In order to perform validated requests to Backend server)
            Thread validateUserThread = new Thread(this::serverValidateUser);
            validateUserThread.start();
        }
    }

    private void animate_firstPagePb() {
        ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int arg0) { }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) { }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    default: {
                        {
                            ProgressBar pb = new ProgressBar(MainActivity.this);
                            for (View v : viewPager.getTouchables())
                                if (v instanceof ProgressBar) {
                                    pb = (ProgressBar) v;
                                }
                            double financialStatus = wallets.get(position).getFinancialStatus();
                            double financialGoal = wallets.get(position).getFinancialGoal();
                            int percentage = (int) ((financialStatus / financialGoal) * 100);
                            ObjectAnimator animation = ObjectAnimator.ofInt(pb, "progress", percentage);
                            animation.setDuration(percentage * 16);//for 50%: 800 is great except small ones.
                            animation.setInterpolator(new AccelerateDecelerateInterpolator());//new FastOutSlowInInterpolator());
                            animation.start();
                            break;
                        }
                    }
                }
            }
        };

        viewPager.setOnPageChangeListener(pageChangeListener);
        // do this in a runnable to make sure the viewPager's views are already instantiated before triggering the onPageSelected call
        viewPager.post(new Runnable()
        {
            @Override
            public void run()
            {
                pageChangeListener.onPageSelected(viewPager.getCurrentItem());
            }
        });
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
            startTimeGoogleSignInFlow = System.currentTimeMillis();
            account = completedTask.getResult(ApiException.class);
            googleSignInTokenID = account.getIdToken();
            //we send to validate user on our server and to obtain SessionID
            Thread validateUserThread = new Thread(this::serverValidateUser);
            validateUserThread.start();
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
                cloud.setImageResource(R.drawable.synchedwhitecloud);
                Log.d("TOKENID: ", account.getIdToken());
            } else {
                ImageButton cloud = (ImageButton) this.findViewById(R.id.cloud_image_button);
                cloud.setImageResource(R.drawable.tosyncwhitecloud);
                Log.d("TOKENID: ", account.getIdToken());
            }
//            ImageButton analyticsButton = (ImageButton) this.findViewById(R.id.analyticsButton);
//            analyticsButton.setVisibility(View.VISIBLE);
            MainActivityViewModel.loginStatus = true;

            ImageButton cardsButton = (ImageButton) this.findViewById(R.id.bank_accounts_button);
            cardsButton.setVisibility(View.VISIBLE);
        } else {
            ImageButton cloud = (ImageButton) this.findViewById(R.id.cloud_image_button);
            cloud.setImageResource(R.drawable.crossedwhitecloud);

//            ImageButton analyticsButton = (ImageButton) this.findViewById(R.id.analyticsButton);
//            analyticsButton.setVisibility(View.GONE);
            MainActivityViewModel.loginStatus = false;
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

    //Communication with my RESTful API
    public void serverValidateUser() {
        // We activate a progress bar loader, in order to let the user know
        // that the sign-in request is in process
        MainActivity.this.runOnUiThread(this::activateLoader);
        // We create a new ValidateUserTask and define the onTaskCompleted listener
        new ValidateUserTask(new OnTaskCompleted() {
            @Override
            public void onTaskCompleted(String responseBody) {
                // When the task has been completed, firstly we deactivate the progress bar loader
                MainActivity.this.runOnUiThread(() -> deactivateLoader());
                // Then we check the responseBody received from the server
                // in order to see if there is any exception, or a valid Session ID
                if (responseBody.toUpperCase().contains("EXCEPTION")) {
                    if (responseBody.toUpperCase().contains("CONNECTEXCEPTION")) {
                        MainActivity.this.runOnUiThread(() -> {
                            // Inform the user there is no internet connection
                            Toast.makeText(MainActivity.this,
                                    "NO INTERNET CONNECTION", Toast.LENGTH_SHORT).show();
                        });
                    } else if (responseBody.toUpperCase().contains("SOCKETTIMEOUTEXCEPTION")) {
                        MainActivity.this.runOnUiThread(() -> {
                            // Inform the user that Budgetize's server is not available
                            Toast.makeText(MainActivity.this,
                                    "SERVICE CURRENTLY UNAVAILABLE", Toast.LENGTH_SHORT).show();
                        });
                    }
                    // If any issue occurs, we logout the user also from the google sign in account
                    // disabling his access to the special features also
                    signOut();
                } else {
                    // If there is no exception in the responseBody
                    // we retrieve the sessionID and we update the UI accordingly.
                    sessionID = responseBody;
                    MainActivity.this.runOnUiThread(() -> {
                        updateUI(account);
                    });
                }
                endTimeGoogleSignInFlow = System.currentTimeMillis();
                Log.d(" Time took Sign-in Flow",
                        " " + (endTimeGoogleSignInFlow - startTimeGoogleSignInFlow));
            }
        }, googleSignInTokenID).doInBackground();
    }

    //JOBS
    /*
    TODO:reimplement this respecting mvvm
    private void doJobs() {
        try {
            doDateJob();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    */

    /**
     * TODO:reimplement this respecting mvvm
     * private void doDateJob() throws ParseException {
     * SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM");//dd/MM/yyyy
     * Date date1 = new Date();
     * String strDate = sdfDate.format(date1);
     * String strdate2 = myDatabase.walletDao().getLatestDate();
     * <p>
     * <p>
     * if (strdate2 != null) {
     * <p>
     * <p>
     * date1 = sdfDate.parse(strDate);
     * <p>
     * Date date2 = sdfDate.parse(strdate2);
     * <p>
     * if (date1.compareTo(date2) > 0) {
     * List<Wallet> wallets = myDatabase.walletDao().getAllWallets();
     * <p>
     * for (Wallet w : wallets) {
     * myDatabase.walletDao().financialStatusUpdate(w.getId(), sdfDate.format(date1));
     * }
     * }
     * <p>
     * }
     * }
     */
    //WALLET UI
    private void refresh_tabs() {
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            View tab = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(i);
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) tab.getLayoutParams();
            p.setMargins(0, 0, 10, 0);
            tab.requestLayout();
        }
    }

    //* Deprecated as we use MVVM
    private void refresh_wallets() {
        adapter = new WalletViewPagerAdapter(wallets, MainActivity.this, mainActivityViewModel);
        viewPager.setAdapter(adapter);
        //adapter.notifyDataSetChanged();
        refresh_tabs();//to keep the distance between bullets
        viewPager.setCurrentItem(mainActivityViewModel.lastWalletPosition);
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

    private void createSignOutDialog() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE: {
                        signOut();
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
        builder.setMessage("Do you want to disconnect your Google Account from Budgetize?\nThis will deactivate Import Transactions feature").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    private void createDialogClickListener() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE: {
                        try {
                            mainActivityViewModel.deleteWallet(wallets.get(viewPager.getCurrentItem()));
                            wallets.remove(viewPager.getCurrentItem());
                            mainActivityViewModel.lastWalletPosition = 0;
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
        mainActivityViewModel.lastWalletPosition = viewPager.getCurrentItem();
        Intent myIntent = new Intent(MainActivity.this, targetClass);
        MainActivity.this.startActivity(myIntent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public interface OnTaskCompleted {
        void onTaskCompleted(String responseBody);
    }

    private class ValidateUserTask {
        String googleSignInTokenID;
        OnTaskCompleted listener;

        ValidateUserTask(OnTaskCompleted listener, String googleSignInTokenID) {
            this.listener = listener;
            this.googleSignInTokenID = googleSignInTokenID;
        }

        protected String doInBackground(String... urls) {
            try {
                // We use OkHttp library to build the request
                OkHttpClient client = new OkHttpClient();
                // Create the body of the request, which contains the Google tokenID
                RequestBody formBody = new FormBody.Builder()
                        .add("tokenID", googleSignInTokenID)
                        .build();
                // We build the request by setting the url+endpoint and the CRUD operation
                Request request = new Request.Builder()
                        .url(serverBaseLink + "/validateUser")
                        .post(formBody)
                        .build();
                // We call and wait for a response
                Call call = client.newCall(request);
                Response response = call.execute();
                // We forward the response
                onPostExecute(Objects.requireNonNull(response.body()).string());
            } catch (Exception e) {
                // We catch different type of exceptions and forward them
                onPostExecute(e.toString());
                Log.e("TokenIDtoServer", "Error sending ID token to backend.", e);
            }
            return null;
        }

        void onPostExecute(String response) {
            // We use the callback
            listener.onTaskCompleted(response);
        }
    }

}
