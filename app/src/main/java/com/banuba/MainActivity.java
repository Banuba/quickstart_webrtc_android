package com.banuba;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.banuba.fragments.CameraFragment;
import com.banuba.fragments.NoCameraFragment;
import com.banuba.utils.PermissionUtils;

import org.webrtc.Camera2Capturer;
import org.webrtc.Camera2Enumerator;
import org.webrtc.CapturerObserver;
import org.webrtc.EglBase;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.VideoFrame;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    static final int REQUEST_CAMERA_PERMISSION = 1234;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);
        checkPermissionAndTakeAction();
    }

    private void checkPermissionAndTakeAction() {
        final boolean hasCameraPermission = getCameraPermission();
        if (hasCameraPermission) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, getCameraFragment())
                    .commitAllowingStateLoss();
        } else {
            requestCameraPermission();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void requestCameraPermission() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            showNoCameraFragment();
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length != 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                showNoCameraFragment();
            } else {
                showCameraFragment();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void showCameraFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, getCameraFragment())
                .commitAllowingStateLoss();
    }

    protected Fragment getCameraFragment() {
        return CameraFragment.newInstance();
    }

    private void showNoCameraFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, NoCameraFragment.newInstance())
                .commitAllowingStateLoss();
    }

    private boolean getCameraPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }
}
