package com.banuba.video.processor;

import android.content.Context;
import android.database.ContentObserver;
import android.graphics.ImageFormat;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.util.Size;

import androidx.annotation.NonNull;

import com.banuba.sdk.effect_player.ConsistencyMode;
import com.banuba.sdk.effect_player.EffectPlayer;
import com.banuba.sdk.effect_player.EffectPlayerConfiguration;
import com.banuba.sdk.effect_player.NnMode;
import com.banuba.sdk.manager.BanubaSdkManager;
import com.banuba.sdk.offscreen.BufferAllocator;
import com.banuba.sdk.offscreen.ImageDebugUtils;
import com.banuba.sdk.offscreen.ImageProcessResult;
import com.banuba.sdk.offscreen.OffscreenEffectPlayer;
import com.banuba.sdk.offscreen.OffscreenEffectPlayerConfig;
import com.banuba.sdk.offscreen.OffscreenSimpleConfig;
import com.banuba.sdk.recognizer.FaceSearchMode;
import com.banuba.sdk.types.FullImageData;
import com.banuba.sdk.internal.utils.OrientationHelper;

import org.webrtc.JavaI420Buffer;
import org.webrtc.JniCommon;
import org.webrtc.VideoFrame;
import org.webrtc.VideoSink;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

public class BanubaProcessor implements IVideoFrameProcessor {
//    private OffscreenEffectPlayerConfig mEffectPlayerConfig;
    private OffscreenEffectPlayer mEffectPlayer;
    private VideoSink mVideoSink;
    private boolean mProcessorEnabled = false;
    private Handler mHandler;
    private final BuffersQueue mBuffersQueue = new BuffersQueue();
    public static final String KEY = <#Place your token here#>

    private static final int framesDivider = 50;
    private Context mContext;
    private int mFrameNumber = 0;
    private int mLastFrameRotation = 0;
    final private boolean debugSaveFrames = false;

    @Override
    public void onCaptureCreate(Context context, Handler handler, int width, int height) {
        // Uncomment if external resources` archive is used.
        //BanubaSdkManager.initialize(context, KEY, context.getFilesDir().toString() + "/banuba/bnb-resources/bnb-resources");
        this.mContext = context;
        this.mHandler = handler;

        BanubaSdkManager.initialize(context, KEY);

        EffectPlayerConfiguration ep_config = new EffectPlayerConfiguration(
                width, height,
                NnMode.ENABLE,
                FaceSearchMode.MEDIUM,
                false,
                false
        );
        EffectPlayer ext_ep = Objects.requireNonNull(EffectPlayer.create(ep_config));
        ext_ep.setRenderConsistencyMode(ConsistencyMode.ASYNCHRONOUS_CONSISTENT);

        OffscreenSimpleConfig oep_config = OffscreenSimpleConfig.newBuilder(mBuffersQueue)
                .setDebugSaveFrames(debugSaveFrames).setDebugSaveFramesDivider(framesDivider)
                .build();
        mEffectPlayer = new OffscreenEffectPlayer(context, ext_ep, new Size(width, height), oep_config);
/*
        mEffectPlayerConfig = OffscreenEffectPlayerConfig.newBuilder(new Size(width, height), mBuffersQueue)
            .setDebugSaveFrames(debugSaveFrames).setDebugSaveFramesDivider(framesDivider)
            .setFaceSearchMode(FaceSearchMode.MEDIUM)
            .build();
        mEffectPlayer = new OffscreenEffectPlayer(context, mEffectPlayerConfig, KEY);
 */
        mEffectPlayer.setImageProcessListener(oepImageResult -> {
            if (mVideoSink != null && oepImageResult.getOrientation().getRotationAngle() == mLastFrameRotation) {
                VideoFrame videoFrame = convertOEPImageResult2VideoFrame(oepImageResult);
                mVideoSink.onFrame(videoFrame);
            }
        }, mHandler);
    }

    @Override
    public void onCaptureStarted() {
    }

    @Override
    public void onCaptureStopped() {
    }

    @Override
    public void onCaptureDestroy() {
        mEffectPlayer.release();
    }

    @Override
    public void pushVideoFrame(VideoFrame videoFrame, boolean isFrontCamera) {
        mLastFrameRotation = videoFrame.getRotation();

        if (!mProcessorEnabled) {
            VideoFrame newVideoFrame = new VideoFrame(videoFrame.getBuffer().toI420(), videoFrame.getRotation(), videoFrame.getTimestampNs());
            mVideoSink.onFrame(newVideoFrame);
            newVideoFrame.release();
            return;
        }
        VideoFrame.I420Buffer i420Buffer = videoFrame.getBuffer().toI420();
        int width = i420Buffer.getWidth();
        int height = i420Buffer.getHeight();

        if (debugSaveFrames && mFrameNumber % framesDivider == 0) {
            ImageDebugUtils.saveImageDetailed(mContext, ImageFormat.YUV_420_888,
                    i420Buffer.getDataY(),
                    i420Buffer.getWidth(),
                    i420Buffer.getHeight(),
                    i420Buffer.getStrideY(), "camera.jpg", mFrameNumber, null);
        }
        mFrameNumber++;

        final boolean isRequireMirroring = isFrontCamera;
        int deviceOrientationAngle = OrientationHelper.getInstance(mContext).getDeviceOrientationAngle();
        if (!isFrontCamera) {
            if (deviceOrientationAngle == 0) {
                deviceOrientationAngle = 180;
            } else if (deviceOrientationAngle == 180) {
                deviceOrientationAngle = 0;
            }
        }

        final FullImageData.Orientation orientation = OrientationHelper.getOrientation(
            videoFrame.getRotation(),
            deviceOrientationAngle,
            isRequireMirroring);

        Log.d("videoFrame.getRotation", "" + videoFrame.getRotation() + "\t faceOrientation: " + orientation.getFaceOrientation() +
            "\t deviceOrientation: " + deviceOrientationAngle +
            "\t rw: " + videoFrame.getRotatedWidth() + "\t rh: " + videoFrame.getRotatedHeight());

        FullImageData fullImageData = new FullImageData(new Size(width, height), i420Buffer.getDataY(),
                i420Buffer.getDataU(), i420Buffer.getDataV(), i420Buffer.getStrideY(),
                i420Buffer.getStrideU(), i420Buffer.getStrideV(), 1, 1, 1, orientation);
        Log.d("fullImageData", "" + fullImageData.getSize().getHeight() + " " + fullImageData.getSize().getWidth());

        mEffectPlayer.processFullImageData(fullImageData, () -> mHandler.post(() -> i420Buffer.release()), videoFrame.getTimestampNs());
    }

    public VideoFrame convertOEPImageResult2VideoFrame(ImageProcessResult result) {
        int width = result.getWidth();
        int height = result.getHeight();
        int frameSize = width * height;
        int yPos = 0;
        int uPos = frameSize;
        int vPos = frameSize * 5 / 4;

        final ByteBuffer buffer = result.getBuffer();
        mBuffersQueue.retainBuffer(buffer);
        buffer.position(yPos);
        buffer.limit(uPos);
        ByteBuffer dataY = buffer.slice();
        buffer.position(uPos);
        buffer.limit(vPos);
        ByteBuffer dataU = buffer.slice();
        buffer.position(vPos);
        buffer.limit(vPos + frameSize / 4);
        ByteBuffer dataV = buffer.slice();

        JavaI420Buffer I420buffer = JavaI420Buffer.wrap(result.getWidth(), result.getHeight(),
                dataY, width, dataU, width / 2, dataV, width / 2, () -> {
                    JniCommon.nativeFreeByteBuffer(buffer);
                });

        return new VideoFrame(I420buffer, result.getOrientation().getRotationAngle(), result.getTimestamp());
    }

    @Override
    public void setSink(VideoSink videoSink) {
        mVideoSink = videoSink;
    }

    @Override
    public void callJsMethod(String method, String param) {
        mEffectPlayer.callJsMethod(method, param);
    }

    @Override
    public void loadEffect(String effectName) {
        mEffectPlayer.loadEffect(effectName);
    }

    @Override
    public void unloadEffect() {
        mEffectPlayer.unloadEffect();
    }

    @Override
    public void setProcessorEnabled(Boolean isEnabled) {
        mProcessorEnabled = isEnabled;
    }

    public OffscreenEffectPlayer getEffectPlayer() {
        return mEffectPlayer;
    }

    private boolean isAndroidOrientationFixed() {
        return Settings.System.getInt(mContext.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0) == 0;
    }

    private static class BuffersQueue implements BufferAllocator {

        private final int capacity;
        private final Queue<ByteBuffer> queue = new LinkedList<>();

        public BuffersQueue(int capacity) {
            this.capacity = capacity;
        }

        public BuffersQueue() {
            this(4);
        }

        @NonNull
        @Override
        public synchronized ByteBuffer allocateBuffer(int minimumCapacity) {

            final ByteBuffer buffer = queue.poll();
            if (buffer != null && buffer.capacity() >= minimumCapacity) {
                buffer.rewind();
                buffer.limit(buffer.capacity());
                return buffer;
            }

            return ByteBuffer.allocateDirect(minimumCapacity);
        }

        public synchronized void retainBuffer(@NonNull ByteBuffer buffer) {
            if (queue.size() < capacity) {
                queue.add(buffer);
            }
        }

    }

}
