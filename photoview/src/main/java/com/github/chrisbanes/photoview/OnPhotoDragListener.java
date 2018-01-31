package com.github.chrisbanes.photoview;

import android.widget.ImageView;

/**
 * A callback to be invoked when the Photo drag continues.
 */
public interface OnPhotoDragListener {

    /**
     * A callback to receive where the user continues dragging on a photo. You will only receive
     * a callback if the user continues dragging on the actual photo, dragging into  'whitespace'
     * will be ignored.
     *
     * @param view  ImageView the user started dragging.
     * @param x     where the user last dragged from the left of the Drawable, as percentage of the
     *              Drawable width.
     * @param y     where the user last dragged from the top of the Drawable, as percentage of the
     *              Drawable height.
     *
     * @param state FIRST on the first report of this drag -- this is where the user first touched
     *              CONTINUED on subsequent drag reports
     *              LAST on the final drag report for this drag event
     */
    void onPhotoDrag(ImageView view, float x, float y, DragPhase state);
}
