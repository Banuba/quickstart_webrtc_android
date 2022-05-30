package com.banuba;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

public class MainActivity extends AppCompatActivity {
    static final int REQUEST_CAMERA_PERMISSION = 1234123;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.loadLibrary("jingle_peerconnection_so");
        setContentView(R.layout.layout_main);
        checkPermissionAndTakeAction();
    }

    private void checkPermissionAndTakeAction() {
        final boolean hasCameraPermission = getCameraPermission();
        if (hasCameraPermission) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, getCameraFragment()).commitAllowingStateLoss();
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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
        getSupportFragmentManager().beginTransaction().replace(R.id.container, getCameraFragment()).commitAllowingStateLoss();
    }

    private void showNoCameraFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, new Fragment()).commitAllowingStateLoss();
    }

    protected Fragment getCameraFragment() {
        return new CameraFragment();
    }

    private boolean getCameraPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }
}
