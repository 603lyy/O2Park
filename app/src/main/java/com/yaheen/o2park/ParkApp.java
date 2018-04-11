package com.yaheen.o2park;

import android.app.Application;

import com.iflytek.cloud.SpeechUtility;

public class ParkApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SpeechUtility.createUtility(getApplicationContext(), "appid=5acd6bec");
    }

}
