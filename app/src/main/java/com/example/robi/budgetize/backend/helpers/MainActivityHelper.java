//package com.example.robi.budgetize.backend.helpers;
//
//import android.Manifest;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.content.pm.PackageManager;
//import android.os.Environment;
//import android.util.Log;
//import android.widget.ImageButton;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.biometric.BiometricManager;
//import androidx.biometric.BiometricPrompt;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//import androidx.localbroadcastmanager.content.LocalBroadcastManager;
//
//import com.example.robi.budgetize.ApplicationObj;
//import com.example.robi.budgetize.R;
//import com.example.robi.budgetize.backend.services.DoOAuthService;
//import com.example.robi.budgetize.backend.services.DownloadBankImagesService;
//import com.example.robi.budgetize.backend.services.RetrieveBanksService;
//import com.example.robi.budgetize.ui.activities.MainActivity;
//
//import org.apache.http.client.ClientProtocolException;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.concurrent.Executor;
//
//import okhttp3.Call;
//import okhttp3.FormBody;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.RequestBody;
//import okhttp3.Response;
//
//public class MainActivityHelper {
//    //Biometric app authorization
//    private Executor executor;
//    private BiometricPrompt biometricPrompt;
//    private BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
//            .setTitle("Biometric login for my app")
//            .setSubtitle("Log in using your biometric credential")
//            .setNegativeButtonText("Use account password")
//            .build();
//
//    //My Backend Server
//    private String serverBaseLink = "http://79.114.77.175:8080/";
//    public String sessionID = null;
//
//    public MainActivityHelper(){
//        init();
//    }
//
//    public init(){
//        //app authentication
//        try {
//            appAuthorization();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        setContentView(R.layout.activity_main);
//        LocalBroadcastManager.getInstance(this)
//                .registerReceiver(mMessageReceiver,
//                        new IntentFilter("my-integer"));
//        init_login();
////        getLocalDB();
////        doJobs();
//        init_screen();
//        askForPermissions();
//        startServices();
//    }
//
//    //TODO: all this logic needs to be moved in the viewmodel
//    //Services response listener:
//    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
//        // Handling messages from Services
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            deactivateLoader();
//            // Extract data included in the Intent
//            //int yourInteger = intent.getIntExtra("message",-1/*default value*/);
//            String serviceResponse = intent.getStringExtra("message");
//            if (serviceResponse != null) {
//                if (serviceResponse.contentEquals("needAuth")) {
//                    showBankAccountNeedActions();
//                } else if (serviceResponse.contentEquals("Authorized")) {
//                    Log.d("Authorized successfull", "!");
//                    obpOAuthOK = true;
//                    getAllBanksService();
//                } else if (serviceResponse.contentEquals("BanksAdded")) {
//                    Log.d("GetBanks successfull", "!");
////                    Thread synchAllImagesThread = new Thread(new Runnable() {
////                        @Override
////                        public void run() {
//                    syncAllImagesService();
////                        }
////                    });
////                    synchAllImagesThread.start();
//                } else if (serviceResponse.contentEquals("BanksNOTAdded")) {
//                    Log.d("GetBanks NOT successfull", "!");
//                } else if (serviceResponse.contentEquals("syncImagesCompleted")) {
//                    Log.d("syncImages Completed", "!");
//                }
//            } else {
//                Log.d("Programming error in " + this.getClass().getName() + " ", "EMPTY RESPONSE FROM " + intent.getClass().getName());
//            }
//        }
//    };
//
//    //Biometric app Authorization
//    private void appAuthorization() throws Exception {
//        BiometricManager biometricManager = BiometricManager.from(this);
//        switch (biometricManager.canAuthenticate()) {
//            case BiometricManager.BIOMETRIC_SUCCESS:
//                Log.d("MY_APP_TAG", "App can authenticate using biometrics.");
//                biometricLogin();
//                break;
//            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
//                Log.e("MY_APP_TAG", "No biometric features available on this device.");
//                throw new Exception("No biometrics available");//should handle with other type of authorization(passcode)
//            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
//                Log.e("MY_APP_TAG", "Biometric features are currently unavailable.");
//                throw new Exception("No biometrics available");//should handle with other type of authorization(passcode)
//            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
//                Log.e("MY_APP_TAG", "The user hasn't associated " +
//                        "any biometric credentials with their account.");
//                throw new Exception("No biometrics available");
//        }
//    }
//
//    private void biometricLogin() {
//        executor = ContextCompat.getMainExecutor(this);
//        biometricPrompt = new BiometricPrompt(MainActivity.this,
//                executor, new BiometricPrompt.AuthenticationCallback() {
//            @Override
//            public void onAuthenticationError(int errorCode,
//                                              @NonNull CharSequence errString) {
//                super.onAuthenticationError(errorCode, errString);
//                Toast.makeText(getApplicationContext(),
//                        "Authentication error: " + errString, Toast.LENGTH_SHORT)
//                        .show();
//                biometricPrompt.cancelAuthentication();
//                biometricUpdateUI(false);
//            }
//
//            @Override
//            public void onAuthenticationSucceeded(
//                    @NonNull BiometricPrompt.AuthenticationResult result) {
//                super.onAuthenticationSucceeded(result);
//                Toast.makeText(getApplicationContext(),
//                        "Authentication succeeded!", Toast.LENGTH_SHORT).show();
//                biometricUpdateUI(true);
//            }
//
//            @Override
//            public void onAuthenticationFailed() {
//                super.onAuthenticationFailed();
//                Toast.makeText(getApplicationContext(), "Authentication failed",
//                        Toast.LENGTH_SHORT)
//                        .show();
//                biometricPrompt.cancelAuthentication();
//                biometricUpdateUI(false);
//            }
//        });
//        // Prompt appears when user clicks "Log in".
//        // Consider integrating with the keystore to unlock cryptographic operations,
//        // if needed by your app.
//        biometricPrompt.authenticate(promptInfo);
//    }
//
//    private void askForPermissions() {
//        //delete all
//        File dir = new File(Environment.getExternalStorageDirectory()
//                + "/Android/data/"
//                + ApplicationObj.getAppContext().getPackageName()
//                + "/BankIcons");
////        try {
////            FileUtils.deleteDirectory(dir);
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
//        // Here, thisActivity is the current activity
//        if (ContextCompat.checkSelfPermission(this,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED) {
//
//            // Permission is not granted
//            // Should we show an explanation?
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//                // Show an explanation to the user *asynchronously* -- don't block
//                // this thread waiting for the user's response! After the user
//                // sees the explanation, try again to request the permission.
//            } else {
//                // No explanation needed; request the permission
//                ActivityCompat.requestPermissions(this,
//                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
//
//                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
//                // app-defined int constant. The callback method gets the
//                // result of the request.
//            }
//        } else {
//            // Permission has already been granted
//        }
//    }
//
//    private void showBankAccountNeedActions() {
//        ImageButton bankAccounts = (ImageButton) this.findViewById(R.id.bank_accounts_button);
//        bankAccounts.setImageResource(R.drawable.account_action_to_take);
//    }
//
//    //ORDER OF SERVICES(Related to OBP API):
//
//    //1st This start all the services
//    private void startServices() {
//        //1.Check if Auth available
//        checkOBPOAuthStatus();
//    }
//
//    //1st Service
//    private void checkOBPOAuthStatus() {
//        Intent serviceIntent = new Intent(this, DoOAuthService.class);
//        serviceIntent.putExtra("checkStatus", true);
//        startService(serviceIntent);
//    }
//
//    //2nd Service
//    private void getAllBanksService() {
//        if (obpOAuthOK) {
//            //if it is available we can download banks
//            Intent serviceIntent = new Intent(this, RetrieveBanksService.class);
//            serviceIntent.putExtra("getAllBanks", true);
//            startService(serviceIntent);
//        }
//    }
//
//    //3nd Service
//    private void syncAllImagesService() {
//        Intent serviceIntent = new Intent(this, DownloadBankImagesService.class);
//        serviceIntent.putExtra("syncImages", true);
//        startService(serviceIntent);
//    }
//
//    //Communication with my REST Server
//
//    public void serverValidateUser() {
//        new ValidateUserTask(new OnTaskCompleted() {
//            @Override
//            public void onTaskCompleted(String responseBody) {
//                if (responseBody == null) {
//                    signOut();
//                } else {
//                    sessionID = responseBody;
//                    MainActivity.this.runOnUiThread(() -> {
//                        updateUI(account);
//                        Log.d("UI thread", "I am the UI thread");
//                    });
//                }
//            }
//        },googleSignInTokenID).doInBackground();
//    }
//
//    public interface OnTaskCompleted {
//        void onTaskCompleted(String responseBody);
//    }
//
//    class ValidateUserTask {
//        String googleSignInTokenID;
//        private Exception exception;
//        OnTaskCompleted listener;
//
//        public ValidateUserTask(OnTaskCompleted listener,String googleSignInTokenID) {
//            this.listener = listener;
//            this.googleSignInTokenID = googleSignInTokenID;
//        }
//
//        protected String doInBackground(String... urls) {
//            try {
//                OkHttpClient client = new OkHttpClient();
//                RequestBody formBody = new FormBody.Builder()
//                        .add("tokenID", googleSignInTokenID)
//                        .build();
//
//                Request request = new Request.Builder()
//                        .url(serverBaseLink + "/validateUser")
//                        .post(formBody)
//                        .build();
//
//                Call call = client.newCall(request);
//                Response response = call.execute();
//                onPostExecute(response.body().string());
//            } catch (ClientProtocolException e) {
//                Log.e("TokenIDtoServer", "Error sending ID token to backend.", e);
//            } catch (IOException e) {
//                Log.e("TokenIDtoServer", "Error sending ID token to backend.", e);
//            }
//            return null;
//        }
//        protected void onPostExecute(String response) {
//            // TODO: check this.exception
//            // TODO: do something with the feed
//            listener.onTaskCompleted(response);
//        }
//    }
//
//    /**
//     * TODO:reimplement this respecting mvvm
//     private void doDateJob() throws ParseException {
//     SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM");//dd/MM/yyyy
//     Date date1 = new Date();
//     String strDate = sdfDate.format(date1);
//     String strdate2 = myDatabase.walletDao().getLatestDate();
//
//
//     if (strdate2 != null) {
//
//
//     date1 = sdfDate.parse(strDate);
//
//     Date date2 = sdfDate.parse(strdate2);
//
//     if (date1.compareTo(date2) > 0) {
//     List<Wallet> wallets = myDatabase.walletDao().getAllWallets();
//
//     for (Wallet w : wallets) {
//     myDatabase.walletDao().financialStatusUpdate(w.getId(), sdfDate.format(date1));
//     }
//     }
//
//     }
//     }
//     */
//}
