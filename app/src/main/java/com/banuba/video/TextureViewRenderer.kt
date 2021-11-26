package com.banuba.video

import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.os.Looper
import android.util.AttributeSet
import android.view.TextureView
import org.webrtc.*
import org.webrtc.RendererCommon.*

/**
 *
 * Most of code was copied from [org.webrtc.SurfaceViewRenderer]
 * But replace the surface view to the texture view,
 * and removed the func: [org.webrtc.SurfaceViewRenderer.updateSurfaceSize]
 * because we don't need to set size by manually when using the texture view
 */
class TextureViewRenderer : TextureView, VideoSink, RendererEvents {

    private val resourceName = getResourceName()
    private val videoLayoutMeasure = VideoLayoutMeasure()
    private val eglRenderer: TextureEglRenderer
    private var rendererEvents: RendererEvents? = null
    private var rotatedFrameWidth = 0
    private var rotatedFrameHeight = 0

    constructor(context: Context) : super(context) {
        eglRenderer = TextureEglRenderer(resourceName)
        surfaceTextureListener = eglRenderer
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        eglRenderer = TextureEglRenderer(resourceName)
        surfaceTextureListener = eglRenderer
    }

    fun init(
        sharedContext: EglBase.Context?,
        rendererEvents: RendererEvents?,
        configAttributes: IntArray? = EglBase.CONFIG_PLAIN,
        drawer: GlDrawer? = GlRectDrawer()
    ) {
        ThreadUtils.checkIsOnMainThread()
        this.rendererEvents = rendererEvents
        rotatedFrameWidth = 0
        rotatedFrameHeight = 0
        eglRenderer.init(sharedContext, this, configAttributes, drawer)
    }

    fun clearRendererEvents() {
        this.rendererEvents = null
    }

    fun setRendererEvents(rendererEvents: RendererEvents?) {
        this.rendererEvents = rendererEvents
    }

    fun release() {
        eglRenderer.release()
    }

    fun addFrameListener(
        listener: EglRenderer.FrameListener?,
        scale: Float,
        drawerParam: GlDrawer?
    ) {
        eglRenderer.addFrameListener(listener, scale, drawerParam)
    }

    fun addFrameListener(listener: EglRenderer.FrameListener?, scale: Float) {
        eglRenderer.addFrameListener(listener, scale)
    }

    fun removeFrameListener(listener: EglRenderer.FrameListener?) {
        eglRenderer.removeFrameListener(listener)
    }

    fun setMirror(mirror: Boolean) {
        eglRenderer.setMirror(mirror)
    }

    fun setScalingType(scalingType: ScalingType?) {
        ThreadUtils.checkIsOnMainThread()
        videoLayoutMeasure.setScalingType(scalingType)
        requestLayout()
    }

    fun setScalingType(
        scalingTypeMatchOrientation: ScalingType?,
        scalingTypeMismatchOrientation: ScalingType?
    ) {
        ThreadUtils.checkIsOnMainThread()
        videoLayoutMeasure.setScalingType(
            scalingTypeMatchOrientation,
            scalingTypeMismatchOrientation
        )
        requestLayout()
    }

    fun setFpsReduction(fps: Float) {
        eglRenderer.setFpsReduction(fps)
    }

    fun disableFpsReduction() {
        eglRenderer.disableFpsReduction()
    }

    fun pauseVideo() {
        eglRenderer.pauseVideo()
    }

    fun resetLayoutAspectRatio() {
        eglRenderer.setLayoutAspectRatio(0f)
    }

    fun reset() {
        eglRenderer.reset()
        postOrRun(Runnable {
            rotatedFrameWidth = 0
            rotatedFrameHeight = 0
            requestLayout()
        })
    }

    override fun onFrame(frame: VideoFrame) {
        eglRenderer.onFrame(frame)
    }

    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        ThreadUtils.checkIsOnMainThread()
        if ((MeasureSpec.getMode(widthSpec) == MeasureSpec.AT_MOST && rotatedFrameWidth == 0) ||
            MeasureSpec.getMode(heightSpec) == MeasureSpec.AT_MOST && rotatedFrameHeight == 0
        ) {
            setMeasuredDimension(0, 0)
        } else {
            val size = videoLayoutMeasure.measure(
                widthSpec,
                heightSpec,
                rotatedFrameWidth,
                rotatedFrameHeight
            )
            setMeasuredDimension(size.x, size.y)
        }
        logD("onMeasure(). New size: ${measuredWidth}x${measuredHeight}")
    }

    override fun onLayout(
        changed: Boolean,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int
    ) {
        ThreadUtils.checkIsOnMainThread()
        if ((bottom - top) == 0) {
            resetLayoutAspectRatio()
        } else {
            eglRenderer.setLayoutAspectRatio((right - left) / (bottom - top).toFloat())
        }
    }

    private fun getResourceName(): String {
        return try {
            this.resources.getResourceEntryName(this.id)
        } catch (var2: Resources.NotFoundException) {
            ""
        }
    }

    fun clearImage() {
        eglRenderer.clearImage()
    }

    override fun onFirstFrameRendered() {
        rendererEvents?.onFirstFrameRendered()
    }

    override fun onFrameResolutionChanged(
        videoWidth: Int,
        videoHeight: Int,
        rotation: Int
    ) {
        rendererEvents?.onFrameResolutionChanged(videoWidth, videoHeight, rotation)
        val rotatedWidth = if (rotation != 0 && rotation != 180) videoHeight else videoWidth
        val rotatedHeight = if (rotation != 0 && rotation != 180) videoWidth else videoHeight
        postOrRun(Runnable {
            rotatedFrameWidth = rotatedWidth
            rotatedFrameHeight = rotatedHeight
            requestLayout()
        })
    }

    private fun postOrRun(r: Runnable) {
        if (Thread.currentThread() === Looper.getMainLooper().thread) {
            r.run()
        } else {
            post(r)
        }
    }

    private fun logD(string: String) {
        Logging.d(TAG, "$resourceName: $string")
    }

    companion object {
        private const val TAG = "TextureViewRenderer"
    }
}