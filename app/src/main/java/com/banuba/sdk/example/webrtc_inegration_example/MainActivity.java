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
import com.banuba.sdk.frame.FramePixelBufferFormat;
import com.banuba.sdk.input.StreamInput;
import com.banuba.sdk.output.FrameOutput;
import com.banuba.sdk.player.IDirectBufferAllocator;
import com.banuba.sdk.player.Player;
import com.banuba.sdk.player.PlayerTouchListener;
import com.banuba.sdk.types.FrameData;
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

    private Player mPlayer;
    private FrameOutput mFrameOutput;
    private final BuffersQueue mBuffersQueue = new BuffersQueue(4);

    private boolean mShouldApply = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);
        System.loadLibrary("jingle_peerconnection_so");

        mPlayer = new Player();
        initInput();
        initOutput();

        // Set custom OnTouchListener to change mask style.
        final Button showMaskButton = findViewById(R.id.showMaskButton);
        showMaskButton.setOnClickListener(v -> {
            mShouldApply = !mShouldApply;
            mPlayer.loadAsync(mShouldApply ? "effects/" + MASK_NAME : "");
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
        mPlayer.pause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mPlayer.play();
    }

    @Override
    public void onStop() {
        mCamera2Capturer.stopCapture();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        mPlayer.close();
        mFrameOutput.close();
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

    @SuppressLint("ClickableViewAccessibility")
    private void initOutput() {
        final TextureView textureView = findViewById(R.id.textrueView);

        // Set custom OnTouchListener to change mask style.
        textureView.setOnTouchListener(new PlayerTouchListener(this, mPlayer));

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
        mFrameOutput = new FrameOutput(framePixelBuffer -> handler.post(() -> {
            final JavaI420Buffer i420buffer = JavaI420Buffer.wrap(
                    framePixelBuffer.getWidth(),
                    framePixelBuffer.getHeight(),
                    framePixelBuffer.getPlane(0),
                    framePixelBuffer.getBytesPerRowOfPlane(0),
                    framePixelBuffer.getPlane(1),
                    framePixelBuffer.getBytesPerRowOfPlane(1),
                    framePixelBuffer.getPlane(2),
                    framePixelBuffer.getBytesPerRowOfPlane(2),
                    null);
            final VideoFrame videoFrame = new VideoFrame(i420buffer, 0, System.nanoTime());
            renderer.onFrame(videoFrame);
            videoFrame.release();
            mBuffersQueue.retainBuffer(framePixelBuffer.getBuffer());
        }), mBuffersQueue);
        mFrameOutput.setFormat(FramePixelBufferFormat.I420_BT601_FULL);

        // attach output to the player
        mPlayer.use(mFrameOutput);
    }

    private void initInput() {
        // the stream input will be provide frames from Camera2 to the Player
        final StreamInput streamInput = new StreamInput();

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
                final CameraOrientation cameraOrientation = CameraOrientation.values()[(videoFrame.getRotation() / 90) % 4];
                final FullImageData.Orientation orientation = new FullImageData.Orientation(cameraOrientation, true, 0);
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

                final FrameData frameData = Objects.requireNonNull(FrameData.create());
                frameData.addFullImg(fullImageData);
                i420Buffer.release();
                streamInput.push(frameData, videoFrame.getTimestampNs());
            }
        });

        // attach input to the player
        mPlayer.use(streamInput);
    }

    //
    public static class BuffersQueue implements IDirectBufferAllocator {
        private final int mCapacity;
        private final Queue<ByteBuffer> mQueue = new LinkedList<>();

        public BuffersQueue(int capacity) {
            mCapacity = capacity;
        }

        @NonNull
        public synchronized ByteBuffer allocateBuffer(int capacity) {
            final ByteBuffer buffer = mQueue.poll();
            if (buffer != null && buffer.capacity() == capacity) {
                buffer.rewind();
                return buffer;
            }
            return ByteBuffer.allocateDirect(capacity);
        }

        public synchronized void retainBuffer(@NonNull ByteBuffer buffer) {
            if (mQueue.size() < mCapacity) {
                mQueue.add(buffer);
            }
        }
    }
}
