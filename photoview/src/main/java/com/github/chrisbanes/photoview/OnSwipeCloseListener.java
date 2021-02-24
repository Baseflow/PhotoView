package com.github.chrisbanes.photoview;

/**
 * A callback to be invoked when swiped up or down
 */
public interface OnSwipeCloseListener {
    /**
     * A callback while swiping
     * @param delta Amount of movement
     */
    void onProgress(float delta);

    /**
     * A callback when the amount of movement exceeds the threshold
     */
    void onFinish();

    /**
     * A callback if the threshold is not exceeded when release user finger
     */
    void onCancel();
}