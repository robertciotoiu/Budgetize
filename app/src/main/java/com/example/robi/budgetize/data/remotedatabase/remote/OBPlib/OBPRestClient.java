package com.example.robi.budgetize.data.remotedatabase.remote.OBPlib;

import android.app.Activity;
import android.app.Service;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.example.robi.budgetize.ApplicationObj;
import com.example.robi.budgetize.R;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OBPRestClient {


    private static final String REQUEST_TOKEN_URL = calcFullPath("/oauth/initiate");
    private static final String ACCESS_TOKEN_URL = calcFullPath("/oauth/token");
    private static final String AUTHORIZE_WEBSITE_URL = calcFullPath("/oauth/authorize");

    private static final String PREF_FILE = "OBP_API_PREFS";
    private static final String BANK_ID = "BANK_ID";
    private static final String CONSUMER_TOKEN = "CONSUMER_TOKEN";
    private static final String CONSUMER_SECRET = "CONSUMER_SECRET";
    private static final String PREF_NOT_SET = "";

    private static final String LOG_TAG = OBPRestClient.class.getClass().getName();

    private static OAuthConsumer consumer = new CommonsHttpOAuthConsumer(OBPBankProvider.OBP_AUTH_KEY, OBPBankProvider.OBP_SECRET_KEY);

    public static HashMap<Long, OAuthConsumer> consumers = new HashMap<>();

    private static OAuthProvider provider = new CommonsHttpOAuthProvider(REQUEST_TOKEN_URL, ACCESS_TOKEN_URL, AUTHORIZE_WEBSITE_URL);

    public static HashMap<Long, OAuthProvider> providers = new HashMap<>();

    //Util methods
    public static void retrieveConsumerAndProvider(long bankID) {
        addConsumer(bankID);
        addProvider(bankID);
        setAccessTokenFromSharedPrefs(bankID);
    }

    private static void addConsumer(long bankID) {
        //This can be modified to add consumer from other bankProvider(ex: BNPPARIBAS,etc.). Here we have now hardcoded the OBP.
        consumers.put(bankID, new CommonsHttpOAuthConsumer(OBPBankProvider.OBP_AUTH_KEY, OBPBankProvider.OBP_SECRET_KEY));

    }

    private static void addProvider(long bankID) {
        //This can be modified to add consumer from other bankProvider(ex: BNPPARIBAS,etc.). Here we have now hardcoded the OBP.
        providers.put(bankID, new CommonsHttpOAuthProvider(REQUEST_TOKEN_URL, ACCESS_TOKEN_URL, AUTHORIZE_WEBSITE_URL));
    }

    private static OAuthConsumer getConsumer(long bankID) {
        return consumers.get(bankID);
    }

    private static OAuthProvider getProvider(long bankID) {
        return providers.get(bankID);
    }

    private static String calcFullPath(String relativePath) {
        return OBPBankProvider.BASE_URL_OBP_SANDBOX + relativePath;
    }

    public static String getAuthoriseAppUrl(long bankID)
            throws OAuthMessageSignerException, OAuthNotAuthorizedException,
            OAuthExpectationFailedException, OAuthCommunicationException {

        String customProtocol = ApplicationObj.getAppContext().getResources().getString(
                R.string.customAppProtocol);

        addConsumer(bankID);

        addProvider(bankID);

        return getProvider(bankID).retrieveRequestToken(getConsumer(bankID), customProtocol + "://redirect");
    }

    /**
     * @return true if the access token was loaded and set from shared
     * preferences
     */
    public static boolean setAccessTokenFromSharedPrefs(long bankID) {
        SharedPreferences settings = ApplicationObj.getAppContext()
                .getSharedPreferences(PREF_FILE, 0);
        Long bankIdent = settings.getLong(BANK_ID + bankID, bankID);
        String token = settings.getString(CONSUMER_TOKEN + bankID, PREF_NOT_SET);
        String secret = settings.getString(CONSUMER_SECRET + bankID, PREF_NOT_SET);

        boolean exists = !bankIdent.equals(PREF_NOT_SET) && !token.equals(PREF_NOT_SET)
                && !secret.equals(PREF_NOT_SET);
        // set it if it exists
        if (exists)
            getConsumer(bankID).setTokenWithSecret(token, secret);
        return exists;
    }
//
//    public static boolean clearAccessToken(long bankID) {
//        Editor editor = ApplicationObj.getAppContext().getSharedPreferences(PREF_FILE, 0).edit();
//        editor.putLong(BANK_ID, bankID);
//        editor.putString(CONSUMER_TOKEN, PREF_NOT_SET);
//        editor.putString(CONSUMER_SECRET, PREF_NOT_SET);
//        consumer.setTokenWithSecret("", "");
//        return editor.commit();
//    }

    public static boolean getAndSetAccessToken(long bankID, String verifyCode) {
        try {
            getProvider(bankID).retrieveAccessToken(getConsumer(bankID), verifyCode);
            String token = getConsumer(bankID).getToken();
            String secret = getConsumer(bankID).getTokenSecret();
            if (token != null && secret != null) {
                Editor editor = ApplicationObj.getAppContext().getSharedPreferences(PREF_FILE,
                        Activity.MODE_PRIVATE).edit();
                editor.putLong(BANK_ID + bankID, bankID);
                editor.putString(CONSUMER_TOKEN + bankID, token);
                editor.putString(CONSUMER_SECRET + bankID, secret);
                return editor.commit();
            } else
                return false;
        } catch (OAuthMessageSignerException e) {
            Log.w(LOG_TAG, Log.getStackTraceString(e));
        } catch (OAuthNotAuthorizedException e) {
            Log.w(LOG_TAG, Log.getStackTraceString(e));
        } catch (OAuthExpectationFailedException e) {
            Log.w(LOG_TAG, Log.getStackTraceString(e));
        } catch (OAuthCommunicationException e) {
            Log.w(LOG_TAG, Log.getStackTraceString(e));
        }
        return false;
    }

    public static boolean clearAccessToken(long bankID) {
        Editor editor = ApplicationObj.getAppContext().getSharedPreferences(PREF_FILE, 0).edit();
        editor.remove(BANK_ID + bankID);
        editor.putString(CONSUMER_TOKEN + bankID, PREF_NOT_SET);
        editor.putString(CONSUMER_SECRET + bankID, PREF_NOT_SET);
        //getConsumer(bankID).setTokenWithSecret("", ""); HERE WE SHOULD REMOVE CONSUMER FROM HASHMAP
        consumers.remove(bankID);
        providers.remove(bankID);
        return editor.commit();
    }

    //UNUSED
    public static boolean getAndSetAccessToken(Service service, String verifyCode) {
        try {
            provider.retrieveAccessToken(consumer, verifyCode);
            String token = consumer.getToken();
            String secret = consumer.getTokenSecret();
            if (token != null && secret != null) {
                Editor editor = service.getSharedPreferences(PREF_FILE,
                        Activity.MODE_PRIVATE).edit();
                editor.putString(CONSUMER_TOKEN, token);
                editor.putString(CONSUMER_SECRET, secret);
                return editor.commit();
            } else
                return false;
        } catch (OAuthMessageSignerException e) {
            Log.w(LOG_TAG, Log.getStackTraceString(e));
        } catch (OAuthNotAuthorizedException e) {
            Log.w(LOG_TAG, Log.getStackTraceString(e));
        } catch (OAuthExpectationFailedException e) {
            Log.w(LOG_TAG, Log.getStackTraceString(e));
        } catch (OAuthCommunicationException e) {
            Log.w(LOG_TAG, Log.getStackTraceString(e));
        }
        return false;
    }

    public static JSONObject getOAuthedJson(String urlString, long bankID) throws ExpiredAccessTokenException, ObpApiCallFailedException {
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet request = new HttpGet(urlString);
            getConsumer(bankID).sign(request);
            org.apache.http.HttpResponse response = httpclient.execute(request);
            HttpEntity entity = response.getEntity();

            int statusCode = response.getStatusLine().getStatusCode();

            switch (statusCode) {
                case 200:
                case 201:
                    return parseJsonResponse(entity);
                case 401:
                    try {
                        JSONObject responseJson = parseJsonResponse(entity);
                        if (responseJson.optString("error").contains(
                                getConsumer(bankID).getToken())) {
                            // We have an expired access token (probably?)
                            throw new ExpiredAccessTokenException();
                        } else {
                            // It wasn't (probably?) an expired token error
                            Log.w(LOG_TAG, responseJson.toString());
                            throw new ObpApiCallFailedException();
                        }
                    } catch (JSONException e) {
                        // Api response wasn't json -> unexpected
                        Log.w(LOG_TAG, Log.getStackTraceString(e));
                        throw new ObpApiCallFailedException();
                    }
                default:
                    throw new ObpApiCallFailedException();
            }
        } catch (MalformedURLException e) {
            Log.w(LOG_TAG, Log.getStackTraceString(e));
            throw new ObpApiCallFailedException();
        } catch (OAuthMessageSignerException e) {
            Log.w(LOG_TAG, Log.getStackTraceString(e));
            throw new ObpApiCallFailedException();
        } catch (OAuthExpectationFailedException e) {
            Log.w(LOG_TAG, Log.getStackTraceString(e));
            throw new ObpApiCallFailedException();
        } catch (OAuthCommunicationException e) {
            Log.w(LOG_TAG, Log.getStackTraceString(e));
            throw new ObpApiCallFailedException();
        } catch (ClientProtocolException e) {
            Log.w(LOG_TAG, Log.getStackTraceString(e));
            throw new ObpApiCallFailedException();
        } catch (IOException e) {
            Log.w(LOG_TAG, Log.getStackTraceString(e));
            throw new ObpApiCallFailedException();
        } catch (JSONException e) {
            Log.w(LOG_TAG, Log.getStackTraceString(e));
            throw new ObpApiCallFailedException();
        } catch (ObpApiCallFailedException e) {
            Log.w(LOG_TAG, Log.getStackTraceString(e));
            throw new ObpApiCallFailedException();
        }

    }

    private static JSONObject parseJsonResponse(HttpEntity entity) throws ParseException, JSONException, IOException {
        return new JSONObject(EntityUtils.toString(entity));
    }

    public static JSONObject getBanksJson() {
        return new BanksProvider().doInBackground();
    }

//    //HSBC API
//    public static JSONObject getAtms(long bankID) throws ExpiredAccessTokenException,
//            ObpApiCallFailedException {
//        //return getOAuthedJson(BASE_URL + "/obp/v1.2/banks");
//        return getOAuthedJson(OBPBankProvider.BASE_URL_OBP_SANDBOX + "/obp/v3.1.0/banks/hsbc.01.hk.hsbc/atms", bankID);
//    }

    public static JSONObject getAccountsAtAllBanks(long bankID) throws ExpiredAccessTokenException,
            ObpApiCallFailedException {
        //return getOAuthedJson(BASE_URL + "/obp/v1.2/banks");
        //return getOAuthedJson(BASE_URL_OBP_SANDBOX + "/obp/v3.1.0/banks/hsbc.01.hk.hsbc/accounts");
        //return getOAuthedJson(OBPBankProvider.BASE_URL_OBP_SANDBOX + "/obp/v1.2.1/accounts/private",bankID);
        return getOAuthedJson(OBPBankProvider.BASE_URL_OBP_SANDBOX + "/obp/v4.0.0/my/accounts", bankID);
    }

//    //HSBC API
//    public static JSONObject getAccounts(long bankID, String obpBankID) throws ExpiredAccessTokenException,
//            ObpApiCallFailedException {
//        //return getOAuthedJson(BASE_URL + "/obp/v1.2/banks");
//        //return getOAuthedJson(BASE_URL_OBP_SANDBOX + "/obp/v3.1.0/banks/hsbc.01.hk.hsbc/accounts");
//        return getOAuthedJson(OBPBankProvider.BASE_URL_OBP_SANDBOX + "/obp/v1.2/banks/"+obpBankID+"/accounts",bankID);
//    }


    public static JSONObject getTransactions(long bankID, String obpBankID, String accountID)
            throws ExpiredAccessTokenException, ObpApiCallFailedException {
        return getOAuthedJson(OBPBankProvider.BASE_URL_OBP_SANDBOX
                + "/obp/v4.0.0/my/banks/" + obpBankID + "/accounts/" + accountID + "/transactions", bankID);
    }

    //synch method to get bank names;
    private static class BanksProvider {
        protected JSONObject doInBackground() {
            try {
                OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder()
                        .url(OBPBankProvider.BASE_URL_OBP_SANDBOX + "/obp/v3.1.0/banks")
                        .build();
                Call call = client.newCall(request);
                Response response = call.execute();
                return onPostExecute(response.body().string());
            } catch (Exception e) {
                onPostExecute(e.toString());
                Log.e("TokenIDtoServer", "Error sending ID token to backend.", e);
            }
            return null;
        }

        protected JSONObject onPostExecute(String response) {
            // TODO: check this.exception
            // TODO: do something with the feed
            JSONObject responseJson = null;
            try {
                responseJson = new JSONObject(response);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return responseJson;
        }
    }

}
