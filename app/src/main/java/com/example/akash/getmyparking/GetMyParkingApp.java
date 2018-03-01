package com.example.akash.getmyparking;

import android.app.Application;

import com.cloudinary.android.MediaManager;

import java.util.HashMap;
import java.util.Map;


public class GetMyParkingApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Map config = new HashMap();
        config.put("cloud_name", "akashnits");
        MediaManager.init(this, config);
    }
}
