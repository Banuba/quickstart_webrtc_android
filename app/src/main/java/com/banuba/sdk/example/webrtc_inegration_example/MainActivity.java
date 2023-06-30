package com.banuba.sdk.example.webrtc_inegration_example;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.os.Handler;
import android.util.Size;
import android.view.TextureView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.banuba.sdk.effect_player.CameraOrientation;
import com.banuba.sdk.effect_player.ConsistencyMode;
import com.banuba.sdk.effect_player.EffectPlayer;
import com.banuba.sdk.effect_player.EffectPlayerConfiguration;
import com.banuba.sdk.internal.utils.OrientationHelper;
import com.banuba.sdk.offscreen.BufferAllocator;
import com.banuba.sdk.offscreen.OEPImageFormat;
import com.banuba.sdk.offscreen.OffscreenEffectPlayer;
import com.banuba.sdk.offscreen.OffscreenSimpleConfig;
import com.banuba.sdk.types.FullImageData;

import org.webrtc.Camera2Capturer;
import org.webrtc.Camera2Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.CapturerObserver;
import org.webrtc.EglBase;
import org.webrtc.EglRenderer;
import org.webrtc.GlRectDrawer;
import org.webrtc.JavaI420Buffer;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.ThreadUtils;
import org.webrtc.VideoFrame;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;

public class MainActivity extends AppCompatActivity {
    private static final String MASK_NAME = "TrollGrandma";
    private static final int REQUEST_CAMERA_PERMISSION = 1234;
    private static final Size CAPTURE_SIZE = new Size(1280, 720);

    private final EglBase.Context eglContext = EglBase.create().getEglBaseContext();
    private Camera2Capturer mCamera2Capturer;
    private SurfaceTextureHelper mSurfaceTextureHelper;

    private OffscreenEffectPlayer mOEP;
    private final BuffersQueue mBuffersQueue = new BuffersQueue();

    private static final String TAG = "MainActivity";

    private boolean mShouldApply = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);
        System.loadLibrary("jingle_peerconnection_so");

        initOEP();
        initInput();
        initOutput();

        // Set custom OnTouchListener to change mask style.
        final Button showMaskButton = findViewById(R.id.showMaskButton);
        showMaskButton.setOnClickListener(v -> {
            mShouldApply = !mShouldApply;
            mOEP.loadEffect(mShouldApply ? "effects/" + MASK_NAME : "");
            showMaskButton.setText(mShouldApply ? getString(R.string.hide_mask) : getString(R.string.show_mask));
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isCameraPermissionGranted()) {
            mCamera2Capturer.startCapture(CAPTURE_SIZE.getWidth(), CAPTURE_SIZE.getHeight(), 30);
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        mCamera2Capturer.stopCapture();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        mOEP.release();
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (isCameraPermissionGranted()) {
            mCamera2Capturer.startCapture(CAPTURE_SIZE.getWidth(), CAPTURE_SIZE.getHeight(), 30);
        } else {
            finish();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private boolean isCameraPermissionGranted() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void initOEP() {
        EffectPlayerConfiguration effectPlayerConfig = EffectPlayerConfiguration.create(CAPTURE_SIZE.getWidth(), CAPTURE_SIZE.getHeight());
        EffectPlayer effectPlayer = Objects.requireNonNull(EffectPlayer.create(effectPlayerConfig));
        effectPlayer.setRenderConsistencyMode(ConsistencyMode.ASYNCHRONOUS_CONSISTENT);

        OffscreenSimpleConfig oepConfig = OffscreenSimpleConfig.newBuilder(mBuffersQueue).build();
        mOEP = new OffscreenEffectPlayer(this, effectPlayer, CAPTURE_SIZE, oepConfig);
    }

    private void initOutput() {
        final TextureView textureView = findViewById(R.id.textrueView);

        // create textrue renderer
        final EglRenderer renderer = new EglRenderer("textureView");
        renderer.init(eglContext, EglBase.CONFIG_PLAIN, new GlRectDrawer());
        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
                ThreadUtils.checkIsOnMainThread();
                renderer.createEglSurface(surface);
                renderer.setLayoutAspectRatio(((float)width) / ((float)height));
            }

            @Override
            public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {
                renderer.setLayoutAspectRatio(((float)width) / ((float)height));
            }

            @Override
            public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
                ThreadUtils.checkIsOnMainThread();
                CountDownLatch completionLatch = new CountDownLatch(1);
                renderer.releaseEglSurface(completionLatch::countDown);
                ThreadUtils.awaitUninterruptibly(completionLatch);
                return true;
            }

            @Override
            public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {
            }
        });

        // draw frames with renderer
        final Handler handler = mSurfaceTextureHelper.getHandler();

        mOEP.setImageProcessListener(oepImageResult -> {
            final JavaI420Buffer i420buffer = JavaI420Buffer.wrap(
                oepImageResult.getWidth(),
                oepImageResult.getHeight(),
                oepImageResult.getPlaneBuffer(0),
                oepImageResult.getBytesPerRowOfPlane(0),
                oepImageResult.getPlaneBuffer(1),
                oepImageResult.getBytesPerRowOfPlane(1),
                oepImageResult.getPlaneBuffer(2),
                oepImageResult.getBytesPerRowOfPlane(2),
                null);
            final VideoFrame videoFrame = new VideoFrame(i420buffer, oepImageResult.getOrientation().getRotationAngle(), oepImageResult.getTimestamp());
            renderer.onFrame(videoFrame);
            videoFrame.release();
            mBuffersQueue.retainBuffer(oepImageResult.getBuffer());
        }, handler);
    }

    @SuppressLint("RestrictedApi")
    private void initInput() {
        // capture from the Front camera
        final CameraEnumerator enumerator = new Camera2Enumerator(this);
        mCamera2Capturer = new Camera2Capturer(this, enumerator.getDeviceNames()[1], null);
        mSurfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", eglContext, false);

        // capture frames and push it to the Player
        mCamera2Capturer.initialize(mSurfaceTextureHelper, this, new CapturerObserver() {
            @Override
            public void onCapturerStarted(boolean b) {
            }

            @Override
            public void onCapturerStopped() {
            }

            @Override
            public void onFrameCaptured(VideoFrame videoFrame) {
                final FullImageData.Orientation orientation = OrientationHelper.getOrientation(videoFrame.getRotation(), 0, true);
                final VideoFrame.I420Buffer i420Buffer = videoFrame.getBuffer().toI420();
                final FullImageData fullImageData = new FullImageData(
                        new Size(i420Buffer.getWidth(), i420Buffer.getHeight()),
                        i420Buffer.getDataY(),
                        i420Buffer.getDataU(),
                        i420Buffer.getDataV(),
                        i420Buffer.getStrideY(),
                        i420Buffer.getStrideU(),
                        i420Buffer.getStrideV(),
                        1, 1, 1,
                        orientation);

                mOEP.processFullImageData(fullImageData, i420Buffer::release, OEPImageFormat.I420_BT601_VIDEO, videoFrame.getTimestampNs());
            }
        });
    }

    private static class BuffersQueue implements BufferAllocator {
        private final int capacity = 4;
        private final Queue<ByteBuffer> queue = new LinkedList<>();

        @NonNull
        @Override
        public synchronized ByteBuffer allocateBuffer(int minimumCapacity) {
            final ByteBuffer buffer = queue.poll();
            if (buffer != null && buffer.capacity() >= minimumCapacity) {
                buffer.rewind().limit();
                buffer.limit(buffer.capacity());
                return buffer;
            } else {
                return ByteBuffer.allocateDirect(minimumCapacity);
            }
        }

        public synchronized void retainBuffer(@NonNull ByteBuffer buffer) {
            if (queue.size() < capacity) {
                queue.add(buffer);
            }
        }
    }
}
