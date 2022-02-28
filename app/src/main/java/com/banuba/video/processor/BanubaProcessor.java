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

import com.banuba.sdk.effect_player.JsCallback;
import com.banuba.sdk.manager.BanubaSdkManager;
import com.banuba.sdk.offscreen.BufferAllocator;
import com.banuba.sdk.offscreen.ImageDebugUtils;
import com.banuba.sdk.offscreen.ImageProcessResult;
import com.banuba.sdk.offscreen.OffscreenEffectPlayer;
import com.banuba.sdk.offscreen.OffscreenEffectPlayerConfig;
import com.banuba.sdk.recognizer.FaceSearchMode;
import com.banuba.sdk.recognizer.UtilityManager;
import com.banuba.sdk.types.FullImageData;
import com.banuba.sdk.internal.utils.OrientationHelper;

import org.webrtc.JavaI420Buffer;
import org.webrtc.JniCommon;
import org.webrtc.VideoFrame;
import org.webrtc.VideoSink;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.Queue;

public class BanubaProcessor implements IVideoFrameProcessor {
    private OffscreenEffectPlayerConfig mEffectPlayerConfig;
    private OffscreenEffectPlayer mOffscreenEffectPlayer;
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
        BanubaSdkManager.initialize(context, KEY, context.getFilesDir().toString() + "/banuba/bnb-resources/bnb-resources");
        // Init must be called before.
        if (!UtilityManager.getBanubaSdkResourcesVersionString().equals("2.382.21")) {
            throw new Error("Invalid NN resource package version");
        }
        this.mContext = context;
        this.mHandler = handler;
        mEffectPlayerConfig = OffscreenEffectPlayerConfig.newBuilder(new Size(width, height), mBuffersQueue)
            .setDebugSaveFrames(debugSaveFrames).setDebugSaveFramesDivider(framesDivider)
            .setFaceSearchMode(FaceSearchMode.MEDIUM) // <- Perf tradeoff by CPU usage - Quality, by default is good, but consumed more CPU.
            .build();
        mOffscreenEffectPlayer = new OffscreenEffectPlayer(context, mEffectPlayerConfig, KEY);
        mOffscreenEffectPlayer.setImageProcessListener(oepImageResult -> {
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
        mOffscreenEffectPlayer.release();
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

        final boolean isRequireMirroring = true;//isFrontCamera;
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

        mOffscreenEffectPlayer.processFullImageData(fullImageData, () -> mHandler.post(() -> i420Buffer.release()), videoFrame.getTimestampNs());
    }

    public VideoFrame convertOEPImageResult2VideoFrame(ImageProcessResult result) {
        if (result.getFormat() != ImageFormat.YUV_420_888) {
            throw new RuntimeException("Unexpected image format");
        }

        int width = result.getWidth();
        int height = result.getHeight();
        int frameSize = result.getBytesPerRow() * height;
        int yPos = result.getOffsetOfPlane(0);
        int uPos = result.getOffsetOfPlane(1);
        int vPos = result.getOffsetOfPlane(2);
        // Because of YUV representation as a single buffer containing all YUV planes,
        // the U and V plane strides are equal to the Y plane stride.
        int uSize = result.getBytesPerRowOfPlane(1) * result.getHeightOfPlane(1); // frameSize / 2;
        // Since V plane rows are intermittent with U plane rows, the size of the V plane (in the common case)
        // is less than the size of the U plane by half of the corresponding width of the plane in bytes.
        int vSize =  result.getBytesPerRowOfPlane(2) * result.getHeightOfPlane(2) - result.getBytesPerRowOfPlane(2) / 2;

        final ByteBuffer buffer = result.getBuffer();
        mBuffersQueue.retainBuffer(buffer);

        buffer.position(yPos);
        buffer.limit(uPos);
        ByteBuffer dataY = buffer.slice();
        buffer.position(uPos);
        buffer.limit(uPos + uSize);
        ByteBuffer dataU = buffer.slice();
        buffer.position(vPos);
        buffer.limit(vPos + vSize);
        ByteBuffer dataV = buffer.slice();

        JavaI420Buffer I420buffer = JavaI420Buffer.wrap(result.getWidth(), result.getHeight(),
                dataY, result.getBytesPerRowOfPlane(0),
                dataU, result.getBytesPerRowOfPlane(1),
                dataV, result.getBytesPerRowOfPlane(2), () -> {
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
        mOffscreenEffectPlayer.callJsMethod(method, param);
    }

    @Override
    public void loadEffect(String effectName) {
        mOffscreenEffectPlayer.loadEffect(effectName);
    }

    @Override
    public void unloadEffect() {
        mOffscreenEffectPlayer.unloadEffect();
    }

    @Override
    public void setProcessorEnabled(Boolean isEnabled) {
        mProcessorEnabled = isEnabled;
    }

    public OffscreenEffectPlayer getEffectPlayer() {
        return mOffscreenEffectPlayer;
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
