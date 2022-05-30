package com.banuba;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.banuba.sdk.effect_player.ConsistencyMode;
import com.banuba.sdk.effect_player.EffectPlayer;
import com.banuba.sdk.effect_player.EffectPlayerConfiguration;
import com.banuba.sdk.effect_player.NnMode;
import com.banuba.sdk.internal.utils.OrientationHelper;
import com.banuba.sdk.manager.BanubaSdkManager;
import com.banuba.sdk.offscreen.BufferAllocator;
import com.banuba.sdk.offscreen.OffscreenEffectPlayer;
import com.banuba.sdk.offscreen.OffscreenSimpleConfig;
import com.banuba.sdk.recognizer.FaceSearchMode;
import com.banuba.sdk.types.FullImageData;
import com.banuba.BanubaClientToken;

import org.webrtc.Camera2Capturer;
import org.webrtc.Camera2Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.CapturerObserver;
import org.webrtc.EglBase;
import org.webrtc.EglRenderer;
import org.webrtc.GlRectDrawer;
import org.webrtc.JavaI420Buffer;
import org.webrtc.JniCommon;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.ThreadUtils;
import org.webrtc.VideoFrame;
import org.webrtc.VideoSink;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;

public class CameraFragment extends Fragment {
    private static final String TAG = "CameraFragment";

    private Camera2Capturer mCamera2Capturer;
    private boolean mUseFrontCamera = true;
    private String mFrontCameraName = null;
    private String mBackCameraName = null;
    private VideoSink mVideoSink;
    private final Size mCaptureSize = new Size(1280, 720);
    private EglRenderer mEglRenderer;
    private SurfaceTextureHelper mSurfaceTextureHelper;
    private OffscreenEffectPlayer mOEP;
    private EffectAdapter mEffectAdapter;
    private final BuffersQueue mBuffersQueue = new BuffersQueue();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_camera, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initFrontAndBackCamerasNames();
        initSwitchCamerasButton(view);
        initTextureView(view);
        initOEP();
        initOEPFramesInputFromCamera();
        initOEPFramesOutputToTextureView();
        initEffectsAdapterView(view);

        /* effects load callback */
        mEffectAdapter.setOnItemClickListener(effectName -> {
            if (effectName == "off()") {
                mOEP.unloadEffect();
            } else {
                mOEP.loadEffect(effectName);
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mCamera2Capturer.startCapture(mCaptureSize.getWidth(), mCaptureSize.getHeight(), 30);
    }

    @Override
    public void onStop() {
        super.onStop();
        mCamera2Capturer.stopCapture();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mOEP.release();
    }

    private void initFrontAndBackCamerasNames() {
        try {
            CameraManager cameraManager = (CameraManager) requireContext().getSystemService(Context.CAMERA_SERVICE);
            String[] cameraIds = cameraManager.getCameraIdList();
            for (String id : cameraIds) {
                Integer lensFacing = cameraManager.getCameraCharacteristics(id).get(CameraCharacteristics.LENS_FACING);
                if (lensFacing == CameraMetadata.LENS_FACING_FRONT && mFrontCameraName == null) {
                    mFrontCameraName = id;
                } else if (lensFacing == CameraMetadata.LENS_FACING_BACK && mBackCameraName == null) {
                    mBackCameraName = id;
                }
            }
        } catch (CameraAccessException e) {
            Log.e(TAG, "Camera device has been disconnected.", e);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "CameraId does not match any known camera device.", e);
        }
    }

    private void initSwitchCamerasButton(@NonNull View view) {
        ImageButton switchCameraBtn = view.findViewById(R.id.btn_switch_camera);
        switchCameraBtn.setOnClickListener(v -> {
            mCamera2Capturer.switchCamera(null, mUseFrontCamera ? mBackCameraName : mFrontCameraName);
            mUseFrontCamera = !mUseFrontCamera;
        });
    }

    private void initTextureView(@NonNull View view) {
        mEglRenderer = new EglRenderer("bnbView");
        mEglRenderer.init(EglBase.create().getEglBaseContext(), EglBase.CONFIG_PLAIN, new GlRectDrawer());
        TextureView textureView = view.findViewById(R.id.bnbView);
        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
                ThreadUtils.checkIsOnMainThread();
                mEglRenderer.createEglSurface(surface);
                mEglRenderer.setLayoutAspectRatio(((float)width) / ((float)height));
            }

            @Override
            public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {
                mEglRenderer.setLayoutAspectRatio(((float)width) / ((float)height));
            }

            @Override
            public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
                ThreadUtils.checkIsOnMainThread();
                CountDownLatch completionLatch = new CountDownLatch(1);
                Objects.requireNonNull(completionLatch);
                mEglRenderer.releaseEglSurface(new Runnable() {
                    @Override
                    public void run() {
                        completionLatch.countDown();
                    }
                });
                ThreadUtils.awaitUninterruptibly(completionLatch);
                return true;
            }

            @Override
            public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {
            }
        });
        mVideoSink = (videoFrame -> mEglRenderer.onFrame(videoFrame));
    }

    private void initOEP() {
        BanubaSdkManager.initialize(requireContext(), BanubaClientToken.KEY);
        EffectPlayerConfiguration effectPlayerConfig = new EffectPlayerConfiguration(mCaptureSize.getWidth(), mCaptureSize.getHeight(), NnMode.ENABLE, FaceSearchMode.MEDIUM, false, false);
        EffectPlayer effectPlayer = Objects.requireNonNull(EffectPlayer.create(effectPlayerConfig));
        effectPlayer.setRenderConsistencyMode(ConsistencyMode.ASYNCHRONOUS_CONSISTENT);

        OffscreenSimpleConfig oepConfig = OffscreenSimpleConfig.newBuilder(mBuffersQueue).build();
        mOEP = new OffscreenEffectPlayer(requireContext(), effectPlayer, mCaptureSize, oepConfig);
    }

    private void initOEPFramesInputFromCamera() {
        CameraEnumerator enumerator = new Camera2Enumerator(requireActivity());
        mCamera2Capturer = new Camera2Capturer(requireActivity(), enumerator.getDeviceNames()[1], null);
        mSurfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", EglBase.create().getEglBaseContext(), false);
        OrientationHelper.getInstance(requireContext()).startDeviceOrientationUpdates();

        mCamera2Capturer.initialize(mSurfaceTextureHelper, requireActivity(), new CapturerObserver() {
            @Override
            public void onCapturerStarted(boolean b) {
            }

            @Override
            public void onCapturerStopped() {
            }

            @Override
            public void onFrameCaptured(VideoFrame videoFrame) {
                final int deviceOrientationAngle = OrientationHelper.getInstance(requireContext()).getDeviceOrientationAngle();
                final FullImageData.Orientation orientation = OrientationHelper.getOrientation(videoFrame.getRotation(), deviceOrientationAngle, mUseFrontCamera);
                VideoFrame.I420Buffer i420Buffer = videoFrame.getBuffer().toI420();
                FullImageData fullImageData = new FullImageData(new Size(i420Buffer.getWidth(), i420Buffer.getHeight()), i420Buffer.getDataY(),
                        i420Buffer.getDataU(), i420Buffer.getDataV(), i420Buffer.getStrideY(),
                        i420Buffer.getStrideU(), i420Buffer.getStrideV(), 1, 1, 1, orientation);

                mOEP.processFullImageData(fullImageData, () -> mSurfaceTextureHelper.getHandler().post(() -> i420Buffer.release()), videoFrame.getTimestampNs());
            }
        });
    }

    private void initOEPFramesOutputToTextureView() {
        mOEP.setImageProcessListener(oepImageResult -> {
            if (mVideoSink != null) {
                int width = oepImageResult.getWidth();
                int height = oepImageResult.getHeight();
                int frameSize = width * height;
                int uPos = frameSize;
                int vPos = frameSize * 5 / 4;

                final ByteBuffer buffer = oepImageResult.getBuffer();
                mBuffersQueue.retainBuffer(buffer);
                buffer.position(0).limit(uPos);
                ByteBuffer dataY = buffer.slice();
                buffer.position(uPos).limit(vPos);
                ByteBuffer dataU = buffer.slice();
                buffer.position(vPos).limit(vPos + frameSize / 4);
                ByteBuffer dataV = buffer.slice();

                JavaI420Buffer I420buffer = JavaI420Buffer.wrap(width, height, dataY, width, dataU, width / 2, dataV, width / 2, () -> {
                    JniCommon.nativeFreeByteBuffer(buffer);
                });

                VideoFrame videoFrame = new VideoFrame(I420buffer, oepImageResult.getOrientation().getRotationAngle(), oepImageResult.getTimestamp());

                mVideoSink.onFrame(videoFrame);
            }
        }, mSurfaceTextureHelper.getHandler());
    }

    private void initEffectsAdapterView(@NonNull View view) {
        mEffectAdapter = new EffectAdapter();
        RecyclerView recycleView = view.findViewById(R.id.recyclerView);
        recycleView.setAdapter(mEffectAdapter);
        recycleView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));

        String pathToEffects = requireContext().getFilesDir().toString() + "/banuba/bnb-resources/effects/";
        File[] effectsDirs = new File(pathToEffects).listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return dir.isDirectory();
            }
        });

        List<String> pathsToEffectsList = new ArrayList<String>();
        pathsToEffectsList.add("off()");
        for (File effectDir : effectsDirs) {
            pathsToEffectsList.add(effectDir.toString());
        }

        mEffectAdapter.setEffectList(pathsToEffectsList);
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
