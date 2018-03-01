package com.example.akash.getmyparking;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.example.akash.getmyparking.utils.GMPSharedPrefs;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Map;


public class UploadService extends IntentService {

    private static final String TAG = UploadService.class.getSimpleName();
    private ArrayList<String> mPictureList;
    //private String mLastUploadedUrl;
    private int mCount;


    public UploadService() {
        super("UploadService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            mPictureList = intent.getStringArrayListExtra("pictureList");
        }

        if (mPictureList != null && mPictureList.size() > 0) {
           uploadSinglePhoto(mPictureList.get(mCount));
        }
    }

    private void uploadSinglePhoto(String filePath){
        MediaManager.get().upload(filePath)
                .unsigned("l0pylvdy")
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {
                        // your code here

                        Log.v(TAG, "Starting upload with request id" + requestId);
                    }

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {
                        // example code starts here
                        Double progress = (double) bytes / totalBytes;
                        // post progress to app UI (e.g. progress bar, notification)
                        // example code ends here
                    }

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        // get the url and save in shared preferences

                        String url = resultData.get("secure_url").toString();
                        Log.v(TAG, url + " is success");

                        updatePhotosPrefs(url);
                        mCount++;
                        Toast.makeText(getApplicationContext(), mCount + " Upload Successful", Toast.LENGTH_SHORT).show();
                        if(mCount < mPictureList.size()){
                            uploadSinglePhoto(mPictureList.get(mCount));
                        }else {
                            Toast.makeText(getApplicationContext(), "Refreshing... Scroll down to see", Toast.LENGTH_SHORT).show();
                            Intent intent= new Intent();
                            intent.setAction("REFRESH_ADAPTER");
                            sendBroadcast(intent);
                        }
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        // your code here

                        Log.v(TAG, error.getDescription());
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {
                        // your code here
                        Log.v(TAG, error.getDescription());

                    }
                })
                .dispatch();
    }


    private void updatePhotosPrefs(String url) {
        LinkedHashSet<String> gmpPhotosStringSet = GMPSharedPrefs.
                getGmpPhotosPrefSet(getApplicationContext());
        gmpPhotosStringSet.add(url);
        GMPSharedPrefs.setGmpPhotosPrefSet(getApplicationContext(),
                gmpPhotosStringSet);
    }


}
