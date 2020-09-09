package com.github.chrisbanes.photoview

/**
 * Interface definition for a callback to be invoked when the photo is experiencing a drag event
 */
interface OnViewDragListener {
    /**
     * Callback for when the photo is experiencing a drag event. This cannot be invoked when the
     * user is scaling.
     *
     * @param dx The change of the coordinates in the x-direction
     * @param dy The change of the coordinates in the y-direction
     */
    fun onDrag(dx: Float, dy: Float)
}
