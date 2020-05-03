package com.example.robi.budgetize.backend.services;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.robi.budgetize.R;
import com.example.robi.budgetize.backend.viewmodels.BankAccountViewModel;
import com.example.robi.budgetize.data.remotedatabase.remote.oauth1.lib.OBPRestClient;

import java.util.Random;

import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;

public class DoOAuthService extends Service {

    private final IBinder binder = (IBinder) new DoOAuthService.LocalBinder();
    // Random number generator
    private final Random mGenerator = new Random();

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        DoOAuthService getService() {
            // Return this instance of LocalService so clients can call public methods
            return DoOAuthService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        if(intent.getBooleanExtra("checkStatus",false)) {
            checkOAuth();
        }
        else if (intent.getBooleanExtra("doLogin",false)){
            doOAuth();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void checkOAuth() {
        if (!OBPRestClient.setAccessTokenFromSharedPrefs(BankAccountViewModel.lastClickedBankID))
            sendMessage("needAuth");
        else{
            sendMessage("Authorized");
        }
    }

    private void doOAuth() {
        if (!OBPRestClient.setAccessTokenFromSharedPrefs(BankAccountViewModel.lastClickedBankID))
            getNewAccessToken();
        else{
            Log.d("BLABLA","SUNT DEJA AUTH");
        }
    }

    private void getNewAccessToken() {
        new AuthorizeTask(this).execute();
    }

    private class AuthorizeTask extends AsyncTask<Void, Void, Uri> {

        private final String LOG_TAG = getClass().getName();
        private Service callingService;

        private AuthorizeTask(DoOAuthService act) {
            this.callingService = act;
        }

        @Override
        protected Uri doInBackground(Void... arg0) {
            // Get the the url the user should be directed to in order to login
            try {
                String urlString = OBPRestClient
                        .getAuthoriseAppUrl(BankAccountViewModel.lastClickedBankID);
                Uri authoriseURI = Uri.parse(urlString);
                return authoriseURI;
            } catch (OAuthMessageSignerException e) {
                Log.w(LOG_TAG, Log.getStackTraceString(e));
            } catch (OAuthNotAuthorizedException e) {
                Log.w(LOG_TAG, Log.getStackTraceString(e));
            } catch (OAuthExpectationFailedException e) {
                Log.w(LOG_TAG, Log.getStackTraceString(e));
            } catch (OAuthCommunicationException e) {
                Log.w(LOG_TAG, Log.getStackTraceString(e));
            }
            return null;
        }

        @Override
        protected void onPostExecute(Uri authUri) {
            if (authUri != null) {
                /**
                 * We have a url to send the user to, so we will open a web
                 * browser and send them to it. The url we open contains a
                 * parameter specifying the callback url, which we have set up
                 * to be handled in AuthenticateActivity.
                 */
                Intent browser = new Intent(Intent.ACTION_VIEW);
                browser.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                browser.setData(authUri);

                callingService.startActivity(browser);
            } else {
                showError();
            }

        }

        private void showError() {
            String OAUTH_ERROR = callingService.getResources().getString(
                    R.string.oauth_error);
            Toast.makeText(callingService, OAUTH_ERROR, Toast.LENGTH_LONG).show();
        }

    }

    // Send an Intent with an action named "custom-event-name". The Intent
    // sent should
    // be received by the ReceiverActivity.
    //message should contain the Activity name to assure only that activity is affected
    private void sendMessage(String message) {
        Log.d("sender", "Broadcasting message");
        Intent intent = new Intent("my-integer");
        // You can also include some extra data.
        intent.putExtra("message", message);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
