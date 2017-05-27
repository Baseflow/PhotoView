package com.github.chrisbanes.photoview;

/**
 * Interface definition for a callback to be invoked when the photo is experiencing a drag event
 */
public interface OnViewDragListener {

    /**
     * Callback for when the photo is experiencing a drag event. This cannot be invoked when the
     * user is scaling.
     *
     * @param dx
     * @param dy
     */
    void onDrag(float dx, float dy);
}
