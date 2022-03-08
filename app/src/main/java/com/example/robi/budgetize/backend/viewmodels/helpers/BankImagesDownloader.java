package com.example.robi.budgetize.backend.viewmodels.helpers;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.example.robi.budgetize.ApplicationObj;
import com.example.robi.budgetize.data.remotedatabase.entities.bank.Bank;

import java.io.File;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class BankImagesDownloader {
        private ThreadPoolExecutor executor =
            (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
    private File fileLocation;

    public BankImagesDownloader() {
        if (ImageDownloader.isExternalStorageWritable()) {
            fileLocation = new File(Environment.getExternalStorageDirectory() +
                    "/Android/data/"
                    + ApplicationObj.getAppContext().getPackageName()
                    + "/BankIcons");
        } else {
            fileLocation = new File(ApplicationObj.getAppContext().getFilesDir()
                    + "/BankIcons");
        }
    }

    // Experimental results
    long startTime = 0;
    long completedTasks = 0;
    long noTasks = 0;

    public void doSync(List<Bank> banks) {
        // Get the location where to save the logos
        File folder = new File(fileLocation.getPath());
        boolean success = true;
        // If the folder does not exist, create it
        if (!folder.exists()) {
            success = folder.mkdirs();
        }
        // Log for Debug purposes
        Log.d("Dir created successfully? ", success + "!");
        if (success) {
            for (int i = 0; i < banks.size(); i++) {
                File file = new File(fileLocation.getPath()
                        + File.separator + banks.get(i).getId() + ".png");
                int finalI = i;
                final ImageDownloader downloader =
                        new ImageDownloader(new ImageDownloader.OnImageLoaderListener() {
                    // Here we handle the different statuses of the bank logo download
                    @Override
                    public void onError(ImageDownloader.ImageError error) {
                        Log.d("Error code ", error.getErrorCode() + ": "
                                + error.getMessage());
                        error.printStackTrace();
                    }

                    @Override
                    public void onProgressChange(int percent) {
                    }

                    @Override
                    public void onComplete(Bitmap result) {
                        // Set the format to save the file
                        final Bitmap.CompressFormat mFormat = Bitmap.CompressFormat.PNG;
                        // Create the file on the disk
                        final File imageFileLocation = new File(fileLocation.getPath()
                                + File.separator + banks.get(finalI).getId() + ".png");
                        // Save the downloaded bitmap to the imageFileLocation
                        ImageDownloader.writeToDisk(imageFileLocation,
                                result,
                                new ImageDownloader.OnBitmapSaveListener() {
                            @Override
                            public void onBitmapSaved() {
                                // Debug purposes
                                Log.d("SAVED", "IMAGE SAVED TO LOCATION:"
                                        + imageFileLocation.getAbsolutePath());
                            }

                            @Override
                            public void onBitmapSaveError(ImageDownloader.ImageError error) {
                                // Debug purposes
                                Log.d("Error code ", error.getErrorCode() + ": " +
                                        error.getMessage());
                                error.printStackTrace();
                            }
                        }, mFormat, false);
                    }
                });
                // Check if the file if Bank's Logo is already downloaded
                if (ImageDownloader.readFromDisk(file) == null) {
                    String logoUrl = banks.get(i).getLogo();
                    // Check if Bank's Logo URL is valid
                    if (logoUrl != null && !logoUrl.contentEquals("")) {
                        // Start the DownloadTask on a new Thread
                        executor.execute(new DownloadTask(downloader, logoUrl, true));
                    }
                }
            }
        }
    }

    private class DownloadTask implements Runnable {
        ImageDownloader downloader;
        String logoUrl;
        boolean displayProgress;

        public DownloadTask(ImageDownloader downloader, String logoUrl, boolean displayProgress) {
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
