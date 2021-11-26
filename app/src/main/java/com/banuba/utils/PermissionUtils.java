// Developed by Banuba Development
// http://www.banuba.com
package com.banuba.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;

import static android.Manifest.permission.RECORD_AUDIO;

@TargetApi(Build.VERSION_CODES.M)
public final class PermissionUtils {
    private PermissionUtils() {
    }

    public static boolean
    isPermissionGrantedWithMCheck(@NonNull String permission, Context context) {
        // noinspection SimplifiableIfStatement
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return isPermissionGranted(context, permission);
        } else {
            return true;
        }
    }

    public static boolean isCameraPermissionGranted(Context context) {
        return isPermissionGranted(context, Manifest.permission.CAMERA);
    }

    public static boolean
    isPermissionGranted(@NonNull Context context, @NonNull String permission) {
        return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }


    public static boolean isMicrophoneDenied(@NonNull Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (isPermissionGranted(activity, Manifest.permission.CAMERA)) {
                return !isMicrophonePermissionGranted(activity)
                    && !activity.shouldShowRequestPermissionRationale(RECORD_AUDIO);
            }
        }
        return false;
    }

    public static boolean isMicrophonePermissionGranted(Context context) {
        return isPermissionGrantedWithMCheck(RECORD_AUDIO, context);
    }
}
