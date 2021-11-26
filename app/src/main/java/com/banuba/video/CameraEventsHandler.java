package com.banuba.video;

import android.os.Handler;
import android.os.Looper;

import org.webrtc.CameraVideoCapturer;

public class CameraEventsHandler implements CameraVideoCapturer.CameraEventsHandler, CameraVideoCapturer.CameraSwitchHandler {

    private boolean isFrontCamera;
    private Handler mainHandler = new Handler(Looper.getMainLooper());


    @Override
    public void onCameraError(String s) {

    }

    @Override
    public void onCameraDisconnected() {

    }

    @Override
    public void onCameraFreezed(String s) {

    }

    @Override
    public void onCameraOpening(String s) {

    }

    @Override
    public void onFirstFrameAvailable() {
    }

    @Override
    public void onCameraClosed() {

    }

    @Override
    public void onCameraSwitchDone(boolean b) {
        isFrontCamera = b;
//        mainHandler.post(() ->{
//            if (mCallback != null){
//                mCallback.onCameraSwitchDone(isFrontCamera);
//            }
//        });
    }

    @Override
    public void onCameraSwitchError(String s) {
//        mainHandler.post(() ->{
//            if (mCallback != null){
//                mCallback.onCameraSwitchError(s);
//            }
//        });
    }
}
