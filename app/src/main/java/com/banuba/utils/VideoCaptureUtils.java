package com.banuba.utils;

import android.util.Log;
import android.util.Size;

import androidx.annotation.Nullable;

import com.banuba.video.CaptureFrame;
import org.webrtc.CameraEnumerationAndroid;
import org.webrtc.CameraEnumerator;
import org.webrtc.CameraVideoCapturer;
import org.webrtc.VideoCapturer;

import java.util.List;

public final class VideoCaptureUtils {

    private static final String TAG = "VideoCaptureUtils";


    public static class CaptureInfo {
        public String deviceName;
        public Size captureSize;
        public int fps;

        @Override
        public String toString() {
            return "CaptureInfo{" +
                    "deviceName='" + deviceName + '\'' +
                    ", captureSize=" + captureSize +
                    ", fps=" + fps +
                    '}';
        }
    }

    public static class CameraCapturerResult {
        public VideoCapturer videoCapturer;
        public CaptureInfo captureInfo;

        public CameraCapturerResult(VideoCapturer videoCapturer, CaptureInfo captureInfo) {
            this.videoCapturer = videoCapturer;
            this.captureInfo = captureInfo;
        }
    }

    public static Size toSize(CaptureFrame frame) {
        int videoW;
        int videoH;
        switch (frame) {
            case PRESET352X288:
                videoW = 352;
                videoH = 288;
                break;
            case PRESET640X480:
                videoW = 640;
                videoH = 480;
                break;
            case PRESET960X540:
                videoW = 960;
                videoH = 540;
                break;
            case PRESET1280X720:
                videoW = 1280;
                videoH = 720;
                break;
            default:
                videoW = 640;
                videoH = 480;
                break;
        }
        return new Size(videoW, videoH);
    }

    public static CaptureInfo getCaptureInformation(CameraEnumerator enumerator, CaptureFrame frame) {
        final String[] deviceNames = enumerator.getDeviceNames();
        if (deviceNames.length == 0) {
            Log.e(TAG, "Can not get any valid camera device");
            return null;
        }
        String device = deviceNames[0];

        for (String deviceName : deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                device = deviceName;
                break;
            }
        }
        int currentDiff = Integer.MAX_VALUE;
        Size dimension = toSize(frame);

        List<CameraEnumerationAndroid.CaptureFormat> formatList = enumerator.getSupportedFormats(device);
        if (formatList.isEmpty()) {
            Log.e(TAG, "Camera supports no format, invalid device");
            return null;
        }
        CameraEnumerationAndroid.CaptureFormat selectedFormat = formatList.get(0);

        for (CameraEnumerationAndroid.CaptureFormat captureFormat : formatList) {
            Log.e(TAG, "captureFormat " + captureFormat);
            if (captureFormat.width > 1280 || captureFormat.height > 720) {
                continue;
            }
            int diff = Math.abs(dimension.getWidth() - captureFormat.width) + Math.abs(dimension.getHeight() - captureFormat.height);
            if (diff < currentDiff) {
                selectedFormat = captureFormat;
                currentDiff = diff;
            }
        }
        int maxFramerate = 24;
        if (selectedFormat.framerate.min <= 24 && selectedFormat.framerate.max > 24) {
            maxFramerate = 24;
        } else {
            maxFramerate = Math.max(maxFramerate, selectedFormat.framerate.max);
        }
        if (maxFramerate > 24) {
            maxFramerate = 24;
        }

        CaptureInfo captureInfo = new CaptureInfo();
        captureInfo.deviceName = device;
        captureInfo.captureSize = new Size(selectedFormat.width, selectedFormat.height);
        captureInfo.fps = maxFramerate;
        Log.e(TAG, "captureInfo " + captureInfo.toString());
        return captureInfo;
    }

    @Nullable
    private static VideoCapturer createCameraCapturer(CameraEnumerator enumerator, CameraVideoCapturer.CameraEventsHandler handler) {
        final String[] deviceNames = enumerator.getDeviceNames();
        if (deviceNames.length == 0) {
            Log.e(TAG, "Can not get any valid camera device");
            return null;
        }
        String device = deviceNames[0];
        for (String deviceName : deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                device = deviceName;
                break;
            }
        }
        return enumerator.createCapturer(device, handler);
    }
}

