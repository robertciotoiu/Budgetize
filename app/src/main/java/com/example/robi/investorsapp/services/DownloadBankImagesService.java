package com.example.robi.investorsapp.services;
//TODO:this

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.robi.investorsapp.ApplicationObj;
import com.example.robi.investorsapp.rest.model.Bank;
import com.example.robi.investorsapp.services.utils.BasicImageDownloader;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

public class DownloadBankImagesService extends Service {
    private ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
    private final IBinder binder = (IBinder) new LocalBinder();
    // Random number generator
    private final Random mGenerator = new Random();
    File fileLocation = new File(Environment.getExternalStorageDirectory()
            + "/Android/data/"
            + ApplicationObj.getAppContext().getPackageName()
            + "/BankIcons");

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        DownloadBankImagesService getService() {
            // Return this instance of LocalService so clients can call public methods
            return DownloadBankImagesService.this;
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
        if (intent.getBooleanExtra("syncImages", false)) {
            doSync();
        }
        return super.onStartCommand(intent, flags, startId);
    }
    /*
    This service must start on onCreate MainActivity to prepare the GridListViewAdapter.java in advance!
    0.Make the OAuth
    1.Get All banks available from OBP API.
    2.Check in the img folder all the icons ids and add/remove the differences
    2.1.Add the new files: Save the filename using name = Bank.id
    2.2.Remove the photos of the ids not in the list retriev from OBP API
    3.Done.
     */

//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        // TODO Auto-generated method stub
//
//        sendMessage();
//        return super.onStartCommand(intent, flags, startId);
//    }

    // Send an Intent with an action named "custom-event-name". The Intent
    // sent should
    // be received by the ReceiverActivity.
    protected void doSync() {
        File folder = new File(fileLocation.getPath());
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdirs();
        }
        Log.d("SUCCES MAKING THE DIRS?", success + "!");
        final ArrayList<Bank> banks = new ArrayList<Bank>();
        banks.addAll(((ApplicationObj) getApplicationContext()).banks);
        for (int i = 0; i < banks.size(); i++) {
            File file = new File(fileLocation.getPath() + File.separator + banks.get(i).getId() + ".jpg");

            final int finalI = i;
            final BasicImageDownloader downloader = new BasicImageDownloader(new BasicImageDownloader.OnImageLoaderListener() {
                @Override
                public void onError(BasicImageDownloader.ImageError error) {
                    Log.d("Error code ", error.getErrorCode() + ": " + error.getMessage());
                    error.printStackTrace();
                }

                @Override
                public void onProgressChange(int percent) {
                }

                @Override
                public void onComplete(Bitmap result) {
                    /* save the image - I'm gonna use JPEG */
                    final Bitmap.CompressFormat mFormat = Bitmap.CompressFormat.JPEG;
                    /* don't forget to include the extension into the file name */
                    final File myImageFile = new File(fileLocation.getPath() + File.separator + banks.get(finalI).getId() + ".jpg");
                    BasicImageDownloader.writeToDisk(myImageFile, result, new BasicImageDownloader.OnBitmapSaveListener() {
                        @Override
                        public void onBitmapSaved() {
                            //Toast.makeText(ImageActivity.this, "Image saved as: " + myImageFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
                            Log.d("SAVED","IMAGE SAVED TO LOCATION:"+myImageFile.getAbsolutePath());
                        }

                        @Override
                        public void onBitmapSaveError(BasicImageDownloader.ImageError error) {
                            Log.d("Error code ", error.getErrorCode() + ": " +
                                    error.getMessage());
                            error.printStackTrace();
                        }
                    }, mFormat, false);
                }
            });
            if (downloader.readFromDisk(file) == null) {
                String logoUrl = banks.get(i).getLogo();
                if (logoUrl != null && !logoUrl.contentEquals("")) {
                    executor.execute(new DownloadTask(downloader, logoUrl, true));
                    //downloader.download(logoUrl, true);
                }
            }
        }
        sendMessage("syncImagesCompleted");
    }


    private void sendMessage(String message) {
        Log.d("sender", "Broadcasting message");
        Intent intent = new Intent("my-integer");
        // You can also include some extra data.
        intent.putExtra("message", message);
        LocalBroadcastManager.getInstance(DownloadBankImagesService.this).sendBroadcast(intent);
    }


    private class DownloadTask implements Runnable {
        BasicImageDownloader downloader;
        String logoUrl;
        boolean displayProgress;

        public DownloadTask(BasicImageDownloader downloader, String logoUrl, boolean displayProgress) {
            this.displayProgress = displayProgress;
            this.downloader = downloader;
            this.logoUrl = logoUrl;
        }

        @Override
        public void run() {
            downloader.download(logoUrl, displayProgress);
        }
    }
}
