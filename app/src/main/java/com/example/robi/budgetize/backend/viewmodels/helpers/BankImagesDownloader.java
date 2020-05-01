package com.example.robi.budgetize.backend.viewmodels.helpers;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.example.robi.budgetize.ApplicationObj;
import com.example.robi.budgetize.data.remotedatabase.entities.Bank;

import java.io.File;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class BankImagesDownloader {
    private ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
    // Random number generator
    private final Random mGenerator = new Random();
    File fileLocation = new File(Environment.getExternalStorageDirectory()+
            "/Android/data/"
            + ApplicationObj.getAppContext().getPackageName()
            + "/BankIcons");

    public BankImagesDownloader(){
    }

    public void doSync(List<Bank> banks) {
        File folder = new File(fileLocation.getPath());
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdirs();
        }
        Log.d("SUCCES MAKING THE DIRS?", success + "!");
        for (int i = 0; i < banks.size(); i++) {
            File file = new File(fileLocation.getPath() + File.separator + banks.get(i).getId() + ".png");

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
                    /* save the image - I'm gonna use PNG */
                    final Bitmap.CompressFormat mFormat = Bitmap.CompressFormat.PNG;
                    /* don't forget to include the extension into the file name */
                    final File myImageFile = new File(fileLocation.getPath() + File.separator + banks.get(finalI).getId() + ".png");
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
                    executor.execute(new BankImagesDownloader.DownloadTask(downloader, logoUrl, true));
                    //downloader.download(logoUrl, true);
                }
            }
        }
        Log.d("BankImagesDownloader.doSync(): ","Bank images downloaded");
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
