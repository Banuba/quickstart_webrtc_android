package com.banuba.fragments;


import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.banuba.sdk.internal.utils.OrientationHelper;
import com.developer.filepicker.model.DialogConfigs;
import com.developer.filepicker.model.DialogProperties;
import com.developer.filepicker.view.FilePickerDialog;
import com.banuba.CenterLayoutManager;
import com.banuba.EffectAdapter;
import com.banuba.R;
import com.banuba.utils.VideoCaptureUtils;
import com.banuba.video.CaptureFrame;
import com.banuba.video.EglBaseProvider;
import com.banuba.video.CameraEventsHandler;
import com.banuba.video.TextureViewRenderer;
import com.banuba.video.processor.EffectInfo;
import com.banuba.video.processor.EffectType;
import com.banuba.video.processor.EffectsRepo;
import com.banuba.video.processor.IVideoFrameProcessor;
import com.banuba.video.processor.BanubaProcessor;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.Camera2Capturer;
import org.webrtc.Camera2Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.CameraVideoCapturer;
import org.webrtc.CapturerObserver;
import org.webrtc.EglBase;
import org.webrtc.GlRectDrawer;
import org.webrtc.RendererCommon;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.VideoFrame;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CameraFragment extends Fragment {
    private static final String TAG = "CameraFragment";
    private SurfaceTextureHelper mSurfaceTextureHelper;
    private Camera2Capturer mCamera2Capturer;
    private TextureViewRenderer mTextureViewRenderer;
    private VideoCaptureUtils.CaptureInfo mCaptureInfo;
    private IVideoFrameProcessor mVideoFrameProcessor;
    private RecyclerView mRecycleView;
    private EffectAdapter mAdapter;
    private ImageButton mSwitchCameraBtn;
    private boolean isUseFront = true;
    private String frontCamera;
    private String rearCamera;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_camera, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        loadEffects();
    }

    private void initView(View view) {
        mTextureViewRenderer = view.findViewById(R.id.bnbView);
        mTextureViewRenderer.init(EglBaseProvider.getEglBaseContext(), null, EglBase.CONFIG_PLAIN, new GlRectDrawer());
        mTextureViewRenderer.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT, RendererCommon.ScalingType.SCALE_ASPECT_FIT);
        mTextureViewRenderer.setMirror(isUseFront);

        OrientationHelper.getInstance(requireContext()).startDeviceOrientationUpdates();

        // init RecyclerView
        mAdapter = new EffectAdapter();
        mAdapter.setOnItemClickListener(info -> {
            if (info.getType() == EffectType.Blur) {
                mTextureViewRenderer.setMirror(false);
                mVideoFrameProcessor.setProcessorEnabled(true);
                mVideoFrameProcessor.loadEffect("Beauty");
                mVideoFrameProcessor.callJsMethod("initBlurBackground", "true");
                mVideoFrameProcessor.callJsMethod("setBlurRadius", "6");
            } else if (info.getType() == EffectType.OFF) {
                mTextureViewRenderer.setMirror(isUseFront);
                mVideoFrameProcessor.setProcessorEnabled(false);
            } else if (info.getType() == EffectType.VBG) {
                mTextureViewRenderer.setMirror(false);
                mVideoFrameProcessor.setProcessorEnabled(true);
                mVideoFrameProcessor.loadEffect("Beauty");
                mVideoFrameProcessor.callJsMethod("initBackground", "true");
                mVideoFrameProcessor.callJsMethod("setBackgroundTexture", info.getFilePath());
            } else if (info.getType() == EffectType.MP4) {
                mTextureViewRenderer.setMirror(false);
                mVideoFrameProcessor.setProcessorEnabled(true);
                mVideoFrameProcessor.loadEffect("Beauty");
                mVideoFrameProcessor.callJsMethod("initBackground", "true");
                mVideoFrameProcessor.callJsMethod("setBackgroundVideo", "video.mp4");
            } else if (info.getType() == EffectType.GIF) {
                mTextureViewRenderer.setMirror(false);
                mVideoFrameProcessor.setProcessorEnabled(true);
                mVideoFrameProcessor.loadEffect("Beauty");
                mVideoFrameProcessor.callJsMethod("initBackground", "true");
                mVideoFrameProcessor.callJsMethod("setBackgroundVideo", "animated.gif");
            } else if (info.getType() == EffectType.Makeup) {
                mTextureViewRenderer.setMirror(false);
                mVideoFrameProcessor.setProcessorEnabled(true);
                mVideoFrameProcessor.loadEffect("Beauty");
                mVideoFrameProcessor.callJsMethod("setBackgroundVideo", "video.mp4");
                mVideoFrameProcessor.callJsMethod("test", ""); // test makeup
            } else if (info.getType() == EffectType.Select) {
                mTextureViewRenderer.setMirror(false);
                final List<String> supportedExtensions = Collections.unmodifiableList(Arrays.asList("mp4", "gif", "jpg", "png"));
                DialogProperties properties = new DialogProperties();

                properties.selection_mode = DialogConfigs.SINGLE_MODE;
                properties.selection_type = DialogConfigs.FILE_AND_DIR_SELECT;
                properties.root = Environment.getExternalStorageDirectory();
                properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
                properties.offset = new File(DialogConfigs.DEFAULT_DIR);
                properties.extensions = supportedExtensions.toArray(new String[0]);

                FilePickerDialog dialog = new FilePickerDialog(requireContext(), properties);
                dialog.setTitle("Select an mp4, gif or image file");

                dialog.setDialogSelectionListener(paths -> {
                    String pathToEffect = paths[0];
                    boolean isFile = pathToEffect.lastIndexOf(".") > 0;
                    if (isFile) {
                        String selectedFileExtension = pathToEffect.substring(pathToEffect.lastIndexOf(".") + 1);
                        if (supportedExtensions.contains(selectedFileExtension)) {
                            Map<String, String> values = new HashMap<>();
                            values.put("path", pathToEffect);
                            values.put("type", (selectedFileExtension.equals("mp4") || selectedFileExtension.equals("gif") ? "video" : "image"));

                            JSONObject obj = new JSONObject(values);
                            try {
                                ExifInterface exif = new ExifInterface(pathToEffect);
                                String orientation = exif.getAttribute(ExifInterface.TAG_ORIENTATION);

                                if (selectedFileExtension.equals("mp4")) {
                                    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                                    retriever.setDataSource(pathToEffect);
                                    orientation = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
                                    retriever.release();
                                }

                                int angle = 0;
                                if (orientation != null) {
                                    switch (Integer.parseInt(orientation)) {
                                        case ExifInterface.ORIENTATION_ROTATE_90:
                                        case 90:
                                            angle = 90;
                                            break;
                                        case ExifInterface.ORIENTATION_ROTATE_180:
                                        case 180:
                                            angle = 180;
                                            break;
                                        case ExifInterface.ORIENTATION_ROTATE_270:
                                        case 270:
                                            angle = 270;
                                            break;
                                    }
                                }
                                obj.put("orientation", angle);
                            } catch (IOException e) {
                                Log.e(TAG, "Exception while exif is created.", e);
                            } catch (JSONException | IllegalArgumentException e) {
                                Log.e(TAG, "Exception while orientation read.", e);
                            }

                            String json = obj.toString();
                            mVideoFrameProcessor.setProcessorEnabled(true);
                            mVideoFrameProcessor.loadEffect("Beauty");
                            mVideoFrameProcessor.callJsMethod("initBackground", "true");
                            mVideoFrameProcessor.callJsMethod("setBackgroundMedia", json);

                            Log.i(TAG, "setBackgroundMedia json: " + json);
                        }
                    }
                });

                dialog.show();
            }
        });
        CenterLayoutManager layoutManager = new CenterLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        mRecycleView = view.findViewById(R.id.recyclerView);
        mRecycleView.setAdapter(mAdapter);
        mRecycleView.setLayoutManager(layoutManager);

        CameraManager cameraManager = (CameraManager) requireContext().getSystemService(Context.CAMERA_SERVICE);
        try {
            String[] cameraIds = cameraManager.getCameraIdList();
            List<String> faceCameras = filterCameraIdFacing(cameraManager, cameraIds, CameraMetadata.LENS_FACING_FRONT);
            List<String> backCameras = filterCameraIdFacing(cameraManager, cameraIds, CameraMetadata.LENS_FACING_BACK);

            frontCamera = getFirstOrNull(faceCameras);
            rearCamera = getFirstOrNull(backCameras);

        } catch (CameraAccessException e) {
            Log.e(TAG, "Camera device has been disconnected.", e);
        }

        mSwitchCameraBtn = view.findViewById(R.id.btn_switch_camera);
        mSwitchCameraBtn.setOnClickListener(v -> {
            String switchDeviceName = isUseFront ? rearCamera : frontCamera;
            mCamera2Capturer.switchCamera(new CameraVideoCapturer.CameraSwitchHandler() {
                @Override
                public void onCameraSwitchDone(boolean isFrontCamera) {
                    isUseFront = isFrontCamera;
                    Log.d(TAG, "isUseFront:" + isUseFront + " isFrontCamera:" + isFrontCamera);
                    //mTextureViewRenderer.setMirror(isUseFront);
                }

                @Override
                public void onCameraSwitchError(String errMsg) {

                }
            }, switchDeviceName);
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initCapture();
    }

    private void loadEffects() {
        List<EffectInfo> effectInfoList = EffectsRepo.INSTANCE.loadEffects(requireContext());
        mAdapter.setEffectList(effectInfoList);
    }

    @Override
    public void onResume() {
        super.onResume();
        mCamera2Capturer.startCapture(mCaptureInfo.captureSize.getWidth(), mCaptureInfo.captureSize.getHeight(), 30);
    }

    @Override
    public void onStop() {
        super.onStop();
        mCamera2Capturer.stopCapture();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mVideoFrameProcessor.onCaptureDestroy();
    }

    private void initCapture() {
        CameraEnumerator enumerator = new Camera2Enumerator(requireActivity());
        String[] deviceNames = enumerator.getDeviceNames();
        mCaptureInfo = VideoCaptureUtils.getCaptureInformation(enumerator, CaptureFrame.PRESET1280X720);
        mCamera2Capturer = new Camera2Capturer(requireActivity(), mCaptureInfo.deviceName, new CameraEventsHandler());
        mVideoFrameProcessor = new BanubaProcessor();
        mVideoFrameProcessor.setSink(videoFrame -> mTextureViewRenderer.onFrame(videoFrame));
        mSurfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", EglBase.create().getEglBaseContext(), false);
        mCamera2Capturer.initialize(mSurfaceTextureHelper, requireActivity(), new CapturerObserver() {

            @Override
            public void onCapturerStarted(boolean b) {
                Log.v(TAG, "onCapturerStarted");
                mVideoFrameProcessor.onCaptureStarted();
            }

            @Override
            public void onCapturerStopped() {
                Log.v(TAG, "onCapturerStopped");
                mVideoFrameProcessor.onCaptureStopped();
            }

            @Override
            public void onFrameCaptured(VideoFrame videoFrame) {
                Log.v(TAG, "onFrameCaptured:" + videoFrame.getRotatedHeight() + ":" + videoFrame.getRotatedWidth());
                mVideoFrameProcessor.pushVideoFrame(videoFrame, isUseFront);
            }
        });
        mVideoFrameProcessor.onCaptureCreate(requireContext(), mSurfaceTextureHelper.getHandler(), mCaptureInfo.captureSize.getWidth(), mCaptureInfo.captureSize.getHeight());
    }

    private List<String> filterCameraIdFacing(
            @NonNull CameraManager cameraManager,
            @NonNull String[] cameraIds,
            int facing) {
        List<String> desiredCameraId = new ArrayList<>();
        try {
            for (String id : cameraIds) {
                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(id);
                Integer lensFacing = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (lensFacing == null) {
                    continue;
                }
                if (lensFacing == facing) {
                    desiredCameraId.add(id);
                }
            }
        } catch (CameraAccessException e) {
            Log.e(TAG, "Camera device has been disconnected.", e);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "CameraId does not match any known camera device.", e);
        }
        return desiredCameraId;
    }

    @Nullable
    private <T> T getFirstOrNull(@Nullable List<T> list) {
        if (list == null || list.size() == 0) {
            return null;
        } else {
            return list.get(0);
        }
    }

    public static Fragment newInstance() {
        return new CameraFragment();
    }

}
