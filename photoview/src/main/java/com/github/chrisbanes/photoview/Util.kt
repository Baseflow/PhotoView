package com.github.chrisbanes.photoview

import android.annotation.TargetApi
import android.os.Build
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.ImageView.ScaleType

internal object Util {
    private const val SIXTY_FPS_INTERVAL = 1000 / 60

    @JvmStatic
    fun postOnAnimation(view: View, runnable: Runnable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            postOnAnimationJellyBean(view, runnable)
        } else {
            view.postDelayed(runnable, SIXTY_FPS_INTERVAL.toLong())
        }
    }

    @TargetApi(16)
    private fun postOnAnimationJellyBean(view: View, runnable: Runnable) {
        view.postOnAnimation(runnable)
    }

    @JvmStatic
    fun checkZoomLevels(
        minZoom: Float, midZoom: Float,
        maxZoom: Float
    ) {
        require(minZoom < midZoom) { "Minimum zoom has to be less than Medium zoom. Call setMinimumZoom() with a more appropriate value" }
        require(midZoom < maxZoom) { "Medium zoom has to be less than Maximum zoom. Call setMaximumZoom() with a more appropriate value" }
    }

    @JvmStatic
    fun hasDrawable(imageView: ImageView): Boolean {
        return imageView.drawable != null
    }

    @JvmStatic
    fun isSupportedScaleType(scaleType: ScaleType?): Boolean {
        if (scaleType == null) {
            return false
        }
        check(scaleType != ScaleType.MATRIX) { "Matrix scale type is not supported" }
        return true
    }

    @JvmStatic
    fun getPointerIndex(action: Int): Int {
        return action and MotionEvent.ACTION_POINTER_INDEX_MASK shr MotionEvent.ACTION_POINTER_INDEX_SHIFT
    }
}