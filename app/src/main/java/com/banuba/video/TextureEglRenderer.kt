 package com.banuba.video

import android.graphics.SurfaceTexture
import android.util.Log
import android.view.TextureView.SurfaceTextureListener
import org.webrtc.EglBase
import org.webrtc.EglRenderer
import org.webrtc.RendererCommon.GlDrawer
import org.webrtc.RendererCommon.RendererEvents
import org.webrtc.ThreadUtils
import org.webrtc.VideoFrame
import java.util.*
import java.util.concurrent.CountDownLatch


 /**
 *
 * Most of code was copied from [org.webrtc.SurfaceEglRenderer]
 * But replace the surface view to the texture view
 * Add reset method to reset the size when we reuse the eglRender
 */
class TextureEglRenderer(name: String?) : EglRenderer(name), SurfaceTextureListener {

    private var rendererEvents: RendererEvents? = null
    private val layoutLock = Any()
    private var isRenderingPaused = false
    private var isFirstFrameRendered = false
    var rotatedFrameWidth = 0
    var rotatedFrameHeight = 0
    private var frameRotation = 0

    fun init(
        sharedContext: EglBase.Context?,
        rendererEvents: RendererEvents?,
        configAttributes: IntArray?,
        drawer: GlDrawer?
    ) {
        ThreadUtils.checkIsOnMainThread()
        this.rendererEvents = rendererEvents
        synchronized(this.layoutLock) {
            isFirstFrameRendered = false
            rotatedFrameWidth = 0
            rotatedFrameHeight = 0
            frameRotation = 0
        }
        super.init(sharedContext, configAttributes, drawer)
    }

    fun reset() {
        isFirstFrameRendered = false
        rotatedFrameWidth = 0
        rotatedFrameHeight = 0
        frameRotation = 0
    }

    override fun init(
        sharedContext: EglBase.Context?,
        configAttributes: IntArray,
        drawer: GlDrawer
    ) {
        this.init(sharedContext, null, configAttributes, drawer)
    }

    override fun setFpsReduction(fps: Float) {
        synchronized(this.layoutLock) { isRenderingPaused = fps == 0.0f }
        super.setFpsReduction(fps)
    }

    override fun disableFpsReduction() {
        synchronized(this.layoutLock) { isRenderingPaused = false }
        super.disableFpsReduction()
    }

    override fun pauseVideo() {
        synchronized(this.layoutLock) { isRenderingPaused = true }
        super.pauseVideo()
    }

    override fun onFrame(frame: VideoFrame) {
        updateFrameDimensionsAndReportEvents(frame)
        super.onFrame(frame)
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
        ThreadUtils.checkIsOnMainThread()
        logD("onSurfaceTextureSizeChanged: surface: $surface size: ${width}x${height}")
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
        logD("onSurfaceTextureUpdated")
    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
        ThreadUtils.checkIsOnMainThread()
        logD("onSurfaceTextureDestroyed: surface: $surface")
        val completionLatch = CountDownLatch(1)
        Objects.requireNonNull(completionLatch)
        releaseEglSurface { completionLatch.countDown() }
        ThreadUtils.awaitUninterruptibly(completionLatch)
        return true
    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
        ThreadUtils.checkIsOnMainThread()
        logD("onSurfaceTextureAvailable: surface: $surface size: ${width}x${height}")
        this.createEglSurface(surface)
    }

    private fun updateFrameDimensionsAndReportEvents(frame: VideoFrame) {
        synchronized(this.layoutLock) {
            if (!isRenderingPaused) {
                if (!isFirstFrameRendered) {
                    isFirstFrameRendered = true
                    logD("Reporting first rendered frame.")
                    rendererEvents?.onFirstFrameRendered()
                }
                if (rotatedFrameWidth != frame.rotatedWidth || rotatedFrameHeight != frame.rotatedHeight || frameRotation != frame.rotation) {
                    logD("Reporting frame resolution changed to ${frame.buffer.width}x${frame.buffer.height} with rotation ${frame.rotation}")
                    rendererEvents?.onFrameResolutionChanged(
                        frame.buffer.width,
                        frame.buffer.height,
                        frame.rotation
                    )
                    rotatedFrameWidth = frame.rotatedWidth
                    rotatedFrameHeight = frame.rotatedHeight
                    frameRotation = frame.rotation
                }
            }
        }
    }

    private fun logD(string: String) {
        Log.d(TAG, "$name: $string")
    }

    companion object {
        private const val TAG = "TextureEglRenderer"
    }
}
