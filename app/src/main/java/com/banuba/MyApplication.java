package com.banuba;

import android.app.Application;

import com.banuba.video.EglBaseProvider;

import org.webrtc.EglBase;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        System.loadLibrary("jingle_peerconnection_so");
        if (EglBaseProvider.getEglBase() == null) {
            EglBaseProvider.setEglBase(EglBase.create());
        }
    }
}
