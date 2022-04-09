package com.example.robi.budgetize.data.remotedatabase.remote.rest.utils;

import java.io.IOException;
import java.util.Objects;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpUtils {
    private static final OkHttpClient client = new OkHttpClient();

    public static String get(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return Objects.requireNonNull(response.body()).string();
        }
    }

    public static String post(String url, String value) throws IOException {
        HttpUrl.Builder httpBuilder = Objects.requireNonNull(HttpUrl.parse(url)).newBuilder();
        httpBuilder.addQueryParameter("BankName",value);

        Request request = new Request.Builder()
                .url(httpBuilder.build())
                .build();
        try (Response response = client.newCall(request).execute()) {
            return Objects.requireNonNull(response.body()).string();
        }
    }
    public static OkHttpClient getClient() {
        return client;
    }

    /*
    Application specific requests are implemented here and called from whatever part of this app
     */
    public static String requestBankNames(String url, String mapping) throws IOException {
        Request request = new Request.Builder()
                .url(url+mapping)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }
}