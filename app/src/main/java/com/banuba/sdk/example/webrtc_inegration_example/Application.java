package com.banuba.sdk.example.webrtc_inegration_example;

import static java.util.Objects.requireNonNull;

import com.banuba.sdk.example.common.BanubaClientToken;
import com.banuba.sdk.manager.BanubaSdkManager;

public class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        BanubaSdkManager.initialize(requireNonNull(getApplicationContext()), BanubaClientToken.KEY);
    }

    @Override
    public void onTerminate() {
        BanubaSdkManager.deinitialize();
        super.onTerminate();
    }
}
