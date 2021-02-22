package com.github.chrisbanes.photoview;

public interface OnSwipeCloseListener {
    void onProgress(float delta);
    void onFinish();
    void onCancel();
}
