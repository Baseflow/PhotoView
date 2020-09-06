package com.github.chrisbanes.photoview

import android.widget.ImageView

/**
 * Callback when the user tapped outside of the photo
 */
interface OnOutsidePhotoTapListener {
    /**
     * The outside of the photo has been tapped
     */
    fun onOutsidePhotoTap(imageView: ImageView?)
}