package com.banuba.video;

import org.webrtc.EglBase;

public class EglBaseProvider {

    private volatile EglBase eglBase = null;

    private EglBaseProvider() {
    }

    public static void setEglBase(EglBase eglBase) {

        InstanceHolder.sInstance.eglBase = eglBase;
    }

    public static EglBase getEglBase() {
        return InstanceHolder.sInstance.eglBase;
    }

    public static EglBase.Context getEglBaseContext() {
        if (getEglBase() != null) {
            return getEglBase().getEglBaseContext();
        }

        return null;
    }

    private static class InstanceHolder {
        private static EglBaseProvider sInstance = new EglBaseProvider();
    }
}
