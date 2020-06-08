package com.example.robi.budgetize.backend.viewmodels.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.robi.budgetize.ApplicationObj;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class ImageDownloader {

    private OnImageLoaderListener mImageLoaderListener;
    private Set<String> mUrlsInProgress = new HashSet<>();
    private final String TAG = this.getClass().getSimpleName();
    Context applicationContext = null;
    File fileLocation;

    public ImageDownloader(@NonNull OnImageLoaderListener listener) {
        this.mImageLoaderListener = listener;
        this.applicationContext = applicationContext;

        if(isExternalStorageWritable()) {
            fileLocation = new File(Environment.getExternalStorageDirectory()
                    + "/Android/data/"
                    + ApplicationObj.getAppContext().getPackageName()
                    + "/BankIcons");
        }else{
            fileLocation = new File(ApplicationObj.getAppContext().getFilesDir()
                    + "/BankIcons");
        }
    }

    public interface OnImageLoaderListener {
        void onError(ImageError error);
        void onProgressChange(int percent);
        void onComplete(Bitmap result);
    }
    /*
    public void getListOfBanksImgsToDownload(){
        ArrayList<Bank> banks = new ArrayList<Bank>();
        banks.addAll(((ApplicationObj) applicationContext).banks);
        for(int i = 0; i<banks.size();i++) {
            File file = new File(fileLocation.getPath()+ File.separator +banks.get(i).getId()+".jpg");
            if(readFromDisk()==null) {
                //download that icon and save it
                download(banks.get(i).getLogo(),false);
                writeToDisk(file, result, new ImageDownloader.OnBitmapSaveListener() {
                    @Override
                    public void onBitmapSaved() {
                        Log.d("Image saved as: " + file.getAbsolutePath());
                    }

                    @Override
                    public void onBitmapSaveError(ImageError error) {
                        Toast.makeText(ImageActivity.this, "Error code " + error.getErrorCode() + ": " +
                                error.getMessage(), Toast.LENGTH_LONG).show();
                        error.printStackTrace();
                    }


                }, mFormat, false);
            }
        }
    }
    */
/**
 * Old method of downloading.
 * Deprecated because of AsyncTask
    @SuppressLint("StaticFieldLeak")
    public void download(@NonNull final String imageUrl, final boolean displayProgress) {
        if (mUrlsInProgress.contains(imageUrl)) {
            Log.w(TAG, "a download for this url is already running, " +
                    "no further download will be started");
            return;
        }
        new AsyncTask<Void, Integer, Bitmap>() {

            private ImageError error;

            @Override
            protected void onPreExecute() {
                mUrlsInProgress.add(imageUrl);
                Log.d(TAG, "starting download");
            }

            @Override
            protected void onCancelled() {
                mUrlsInProgress.remove(imageUrl);
                mImageLoaderListener.onError(error);
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                mImageLoaderListener.onProgressChange(values[0]);
            }

            @Override
            protected Bitmap doInBackground(Void... params) {
                Bitmap bitmap = null;
                //HttpURLConnection connection = null;
                InputStream is = null;
                ByteArrayOutputStream out = null;
                try {
                    URL url=new URL(imageUrl);
                    HttpURLConnection.setFollowRedirects(false);
                    HttpURLConnection httpURLConnection=(HttpURLConnection) url.openConnection();
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.connect();
                    boolean isRedirect;
                    do {
                        if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_MOVED_PERM || httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP) {
                            isRedirect = true;
                            String newURL = httpURLConnection.getHeaderField("Location");
                            httpURLConnection = (HttpURLConnection) new URL(newURL).openConnection();
                        } else {
                            isRedirect = false;
                        }
                    } while (isRedirect);

                    InputStream inptStream=httpURLConnection.getInputStream();
                    bitmap = BitmapFactory.decodeStream(inptStream);
                } catch (Throwable e) {
                    if (!this.isCancelled()) {
                        error = new ImageError(e).setErrorCode(ImageError.ERROR_GENERAL_EXCEPTION);
                        this.cancel(true);
                    }
                } finally {
                }
                return bitmap;
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                if (result == null){// || result.getByteCount()==0) {
                    Log.e(TAG, "factory returned a null result");
                    mImageLoaderListener.onError(new ImageError("downloaded file could not be decoded as bitmap"+imageUrl)
                            .setErrorCode(ImageError.ERROR_DECODE_FAILED));
                } else {
                    Log.d(TAG, "download complete, " + result.getByteCount() +
                            " bytes transferred");
                    mImageLoaderListener.onComplete(result);
                }
                mUrlsInProgress.remove(imageUrl);
                System.gc();
            }
        }.execute();
    }

 */

    public void download(@NonNull final String imageUrl, final boolean displayProgress){

        Bitmap bitmap = null;
        //HttpURLConnection connection = null;
        InputStream is = null;
        ByteArrayOutputStream out = null;
        try {
            URL url=new URL(imageUrl);
            HttpURLConnection.setFollowRedirects(false);
            HttpURLConnection httpURLConnection=(HttpURLConnection) url.openConnection();
            httpURLConnection.setDoInput(true);
            httpURLConnection.connect();
            boolean isRedirect;
            do {
                if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_MOVED_PERM || httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP) {
                    isRedirect = true;
                    String newURL = httpURLConnection.getHeaderField("Location");
                    httpURLConnection = (HttpURLConnection) new URL(newURL).openConnection();
                } else {
                    isRedirect = false;
                }
            } while (isRedirect);

            InputStream inptStream=httpURLConnection.getInputStream();
            bitmap = BitmapFactory.decodeStream(inptStream);
        } catch (Throwable e) {
            e.printStackTrace();
//            if (!this.isCancelled()) {
//                error = new ImageError(e).setErrorCode(ImageError.ERROR_GENERAL_EXCEPTION);
//                this.cancel(true);
//            }
        } finally {
            if (bitmap == null){// || result.getByteCount()==0) {
                Log.e(TAG, "factory returned a null result");
                mImageLoaderListener.onError(new ImageError("downloaded file could not be decoded as bitmap"+imageUrl)
                        .setErrorCode(ImageError.ERROR_DECODE_FAILED));
            } else {
                Log.d(TAG, "download complete, " + bitmap.getByteCount() +
                        " bytes transferred");
                mImageLoaderListener.onComplete(bitmap);
            }
            mUrlsInProgress.remove(imageUrl);
            System.gc();
        }

    }

    public interface OnBitmapSaveListener {
        void onBitmapSaved();
        void onBitmapSaveError(ImageError error);
    }

    public static void writeToDisk(@NonNull final File imageFile, @NonNull final Bitmap image,
                                   @NonNull final OnBitmapSaveListener listener,
                                   @NonNull final Bitmap.CompressFormat format, boolean shouldOverwrite) {
        if (imageFile.isDirectory()) {
            listener.onBitmapSaveError(new ImageError("the specified path points to a directory, " +
                    "should be a file").setErrorCode(ImageError.ERROR_IS_DIRECTORY));
            return;
        }

        if (imageFile.exists()) {
            if (!shouldOverwrite) {
                listener.onBitmapSaveError(new ImageError("file already exists, " +
                        "write operation cancelled").setErrorCode(ImageError.ERROR_FILE_EXISTS));
                return;
            } else if (!imageFile.delete()) {
                listener.onBitmapSaveError(new ImageError("could not delete existing file, " +
                        "most likely the write permission was denied")
                        .setErrorCode(ImageError.ERROR_PERMISSION_DENIED));
                return;
            }
        }

        File parent = imageFile.getParentFile();
        if (!parent.exists() && !parent.mkdirs()) {
            listener.onBitmapSaveError(new ImageError("could not create parent directory")
                    .setErrorCode(ImageError.ERROR_PERMISSION_DENIED));
            return;
        }

        try {
            if (!imageFile.createNewFile()) {
                listener.onBitmapSaveError(new ImageError("could not create file")
                        .setErrorCode(ImageError.ERROR_PERMISSION_DENIED));
                return;
            }
        } catch (IOException e) {
            listener.onBitmapSaveError(new ImageError(e).setErrorCode(ImageError.ERROR_GENERAL_EXCEPTION));
            return;
        }

        new AsyncTask<Void, Void, Void>() {

            private ImageError error;

            @Override
            protected Void doInBackground(Void... params) {
                FileOutputStream fos = null;
                try {
                    //MediaScannerConnection.scanFile(ApplicationObj.getAppContext(), new String[]  {imageFile.getPath()} , new String[]{"image/*"}, null);
                    fos = new FileOutputStream(imageFile);
                    image.compress(format, 100, fos);
                } catch (IOException e) {
                    error = new ImageError(e).setErrorCode(ImageError.ERROR_GENERAL_EXCEPTION);
                    this.cancel(true);
                } finally {
                    if (fos != null) {
                        try {
                            fos.flush();
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return null;
            }

            @Override
            protected void onCancelled() {
                listener.onBitmapSaveError(error);
            }

            @Override
            protected void onPostExecute(Void result) {
                listener.onBitmapSaved();
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private boolean isExternalStorageWritable() {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED;
    }

    public static Bitmap readFromDisk(@NonNull File imageFile) {
        if (!imageFile.exists() || imageFile.isDirectory()) return null;
        return BitmapFactory.decodeFile(imageFile.getAbsolutePath());
    }

    public interface OnImageReadListener {
        void onImageRead(Bitmap bitmap);
        void onReadFailed();
    }

    public static void readFromDiskAsync(@NonNull File imageFile, @NonNull final OnImageReadListener listener) {
        new AsyncTask<String, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(String... params) {
                return BitmapFactory.decodeFile(params[0]);
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                if (bitmap != null)
                    listener.onImageRead(bitmap);
                else
                    listener.onReadFailed();
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, imageFile.getAbsolutePath());
    }

    /** Create a File for saving an image or video
     * @param id*/
    private  File getOutputMediaFile(String id){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        //if(Environment.getExternalStorageDirectory()!=null) {
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + ApplicationObj.getAppContext().getPackageName()
                + "/BankIcons");

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        File mediaFile;
        String mImageName=id +".png";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }

    public static final class ImageError extends Throwable {

        private int errorCode;
        public static final int ERROR_GENERAL_EXCEPTION = -1;
        public static final int ERROR_INVALID_FILE = 0;
        public static final int ERROR_DECODE_FAILED = 1;
        public static final int ERROR_FILE_EXISTS = 2;
        public static final int ERROR_PERMISSION_DENIED = 3;
        public static final int ERROR_IS_DIRECTORY = 4;


        public ImageError(@NonNull String message) {
            super(message);
        }

        public ImageError(@NonNull Throwable error) {
            super(error.getMessage(), error.getCause());
            this.setStackTrace(error.getStackTrace());
        }

        public ImageError setErrorCode(int code) {
            this.errorCode = code;
            return this;
        }

        public int getErrorCode() {
            return errorCode;
        }
    }
}