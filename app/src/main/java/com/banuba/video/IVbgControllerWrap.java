package com.banuba.video;

import android.util.Log;

import androidx.annotation.NonNull;

import com.banuba.sdk.effect_player.EffectActivatedListener;
import com.banuba.sdk.offscreen.OffscreenEffectPlayer;
import com.banuba.video.processor.BanubaProcessor;

public class IVbgControllerWrap extends IVbgController implements EffectActivatedListener {
    private VirtualBackgroundConfiguration mVbConfig;
    private static final String TAG = "IVbgControllerWrap";
    private String mCurrentEffect;
    private OffscreenEffectPlayer mOffscreenEffectPlayer;

    private IVbgControllerWrap() {
        mVbConfig = new VirtualBackgroundConfiguration(false, 0, false, null);
        mCurrentEffect = "";
    }

    private static class IVbgControllerWrapHolder {
        public static IVbgControllerWrap IVbgControllerWrapInstance = new IVbgControllerWrap();
    }

    public static IVbgControllerWrap getInstance() {
        return IVbgControllerWrapHolder.IVbgControllerWrapInstance;
    }

    public VirtualBackgroundConfiguration getVbConfig() {
        return mVbConfig;
    }

    private void callJsMethodSafe(String method, String parameters) {
        Log.d(TAG, "callJsMethodSafe(" + method + ", " + parameters + ")");
        OffscreenEffectPlayer effectPlayer = getOffScreenEffectPlayer();
        if (null == effectPlayer) {
            Log.e(TAG, "null == effectPlayer");
            return;
        }

        effectPlayer.callJsMethod(method, parameters);
    }

    @Override
    public void setEffect(String name) {
        getBanubaProcessor().setProcessorEnabled(true);
        loadEffectAndRotateBg(name);
    }

    private void loadEffectAndRotateBg(String name) {
        Log.d(TAG, "loadEffectAndRotateBg " + name);

        OffscreenEffectPlayer effectPlayer = getOffScreenEffectPlayer();
        if (null == effectPlayer) {
            Log.d(TAG, "null == effectPlayer");
            return;
        }

        effectPlayer.loadEffect(name);

        mCurrentEffect = name;
    }

    @Override
    public void resetEffect() {
        OffscreenEffectPlayer effectPlayer = getOffScreenEffectPlayer();
        if (null == effectPlayer) {
            Log.d(TAG, "null == effectPlayer");
            return;
        }
        getBanubaProcessor().setProcessorEnabled(false);
        Log.d(TAG, "effectPlayer.unloadEffect()");
        mCurrentEffect = "";
        effectPlayer.unloadEffect();
    }

    @Override
    public String currentEffectName() {
        Log.d(TAG, "currentEffectName" + mCurrentEffect);
        return mCurrentEffect;
    }

    @Override
    public void enableVirtualBackground(boolean enabled) {
        if (enabled) {
            callJsMethodSafe("initBackground", "true");
        } else {
            callJsMethodSafe("deleteBackground", "true");
        }
    }

    @Override
    public void setVirtualBackground(String name) {
        callJsMethodSafe("setBackgroundTexture", name);
    }

    @Override
    public void enableBlurBackground(boolean enabled) {
        if (enabled) {
            callJsMethodSafe("initBlurBackground", "true");
        } else {
            callJsMethodSafe("deleteBlurBackground", "true");
        }
    }

    @Override
    public void setBlurStrength(int value) {
        String strValue = "" + value;
        callJsMethodSafe("setBlurRadius", strValue);
    }

    @Override
    public void enableLipsColor(boolean enabled) {
        if (enabled) {
            callJsMethodSafe("initLipsColoring", "true");
        } else {
            callJsMethodSafe("deleteLipsColoring", "true");
        }
    }

    @Override
    public void setLipsColor(String rgba) {
        callJsMethodSafe("initLipsColoring", rgba);
    }

    @Override
    public void enableEyesColor(boolean enabled) {
        if (enabled) {
            callJsMethodSafe("initEyesColoring", "true");
        } else {
            callJsMethodSafe("deleteEyesColoring", "true");
        }
    }

    @Override
    public void setEyesColor(String rgba) {
        callJsMethodSafe("setEyesColor", rgba);
    }

    @Override
    public void enableHairColor(boolean enabled) {
        if (enabled) {
            callJsMethodSafe("initHairColoring", "true");
        } else {
            callJsMethodSafe("deleteHairColoring", "true");
        }
    }

    @Override
    public void setHairColor(String rgba) {
        callJsMethodSafe("setHairColor", rgba);
    }

    @Override
    public void onDataUpdate(String json) {
        callJsMethodSafe("onDataUpdate", json);
    }

    @Override
    public void initComponents() {
    }

    @Override
    public void onEffectActivated(@NonNull String url) {
        Log.d(TAG, "--> effect " + url + " activated");
    }

    public OffscreenEffectPlayer getOffScreenEffectPlayer() {
        return null;
    }

    public BanubaProcessor getBanubaProcessor() {
        return null;
    }
}
