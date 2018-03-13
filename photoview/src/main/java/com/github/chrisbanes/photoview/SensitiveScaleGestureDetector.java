package com.github.chrisbanes.photoview;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;

/**
 * The original ScaleGestureDetector is calling onScaleEnd when the distance between fingers are
 * smaller than a certain value, but this value is not adjustable.
 *
 * This custom class is attempting to solve this problem.
 *
 * But...magic? I modified nothing except removing "InputEventConsistencyVerifier" which I cannot
 * access, and it starts to recognize my pinch even when fingers are close...
 */

public class SensitiveScaleGestureDetector {

    private static final String TAG = "SensitiveScaleGestureDetector";

    public interface OnScaleGestureListener {

        public boolean onScale(SensitiveScaleGestureDetector detector);

        public boolean onScaleBegin(SensitiveScaleGestureDetector detector);

        public void onScaleEnd(SensitiveScaleGestureDetector detector);
    }

    public static class SimpleOnScaleGestureListener implements OnScaleGestureListener {
        public boolean onScale(SensitiveScaleGestureDetector detector) {
            return false;
        }
        public boolean onScaleBegin(SensitiveScaleGestureDetector detector) {
            return true;
        }
        public void onScaleEnd(SensitiveScaleGestureDetector detector) {
            // Intentionally empty
        }
    }

    private final Context mContext;
    private final OnScaleGestureListener mListener;
    private float mFocusX;
    private float mFocusY;
    private float mCurrSpan;
    private float mPrevSpan;
    private float mCurrSpanX;
    private float mCurrSpanY;
    private float mPrevSpanX;
    private float mPrevSpanY;
    private long mCurrTime;
    private long mPrevTime;
    private boolean mInProgress;

    public SensitiveScaleGestureDetector(Context context, OnScaleGestureListener listener) {
        mContext = context;
        mListener = listener;
    }

    public boolean onTouchEvent(MotionEvent event) {

        final int action = event.getActionMasked();
        final boolean streamComplete = action == MotionEvent.ACTION_UP ||
                action == MotionEvent.ACTION_CANCEL;
        if (action == MotionEvent.ACTION_DOWN || streamComplete) {
            // Reset any scale in progress with the listener.
            // If it's an ACTION_DOWN we're beginning a new event stream.
            // This means the app probably didn't give us all the events. Shame on it.
            if (mInProgress) {
                mListener.onScaleEnd(this);
                mInProgress = false;
            }
            if (streamComplete) {
                return true;
            }
        }
        final boolean configChanged =
                action == MotionEvent.ACTION_POINTER_UP ||
                        action == MotionEvent.ACTION_POINTER_DOWN;
        final boolean pointerUp = action == MotionEvent.ACTION_POINTER_UP;
        final int skipIndex = pointerUp ? event.getActionIndex() : -1;
        // Determine focal point
        float sumX = 0, sumY = 0;
        final int count = event.getPointerCount();
        for (int i = 0; i < count; i++) {
            if (skipIndex == i) continue;
            sumX += event.getX(i);
            sumY += event.getY(i);
        }
        final int div = pointerUp ? count - 1 : count;
        final float focusX = sumX / div;
        final float focusY = sumY / div;
        // Determine average deviation from focal point
        float devSumX = 0, devSumY = 0;
        for (int i = 0; i < count; i++) {
            if (skipIndex == i) continue;
            devSumX += Math.abs(event.getX(i) - focusX);
            devSumY += Math.abs(event.getY(i) - focusY);
        }
        final float devX = devSumX / div;
        final float devY = devSumY / div;
        // Span is the average distance between touch points through the focal point;
        // i.e. the diameter of the circle with a radius of the average deviation from
        // the focal point.
        final float spanX = devX * 2;
        final float spanY = devY * 2;
        final float span = (float)Math.sqrt(spanX * spanX + spanY * spanY);
        Log.i("scaleGestureDetector", "" + span);
        // Dispatch begin/end events as needed.
        // If the configuration changes, notify the app to reset its current state by beginning
        // a fresh scale event stream.
        if (mInProgress && (span == 0 || configChanged)) {
            mListener.onScaleEnd(this);
            mInProgress = false;
        }
        if (configChanged) {
            mPrevSpanX = mCurrSpanX = spanX;
            mPrevSpanY = mCurrSpanY = spanY;
            mPrevSpan = mCurrSpan = span;
        }
        if (!mInProgress && span != 0) {
            mFocusX = focusX;
            mFocusY = focusY;
            mInProgress = mListener.onScaleBegin(this);
        }
        // Handle motion; focal point and span/scale factor are changing.
        if (action == MotionEvent.ACTION_MOVE) {
            mCurrSpanX = spanX;
            mCurrSpanY = spanY;
            mCurrSpan = span;
            mFocusX = focusX;
            mFocusY = focusY;
            boolean updatePrev = true;
            if (mInProgress) {
                updatePrev = mListener.onScale(this);
            }
            if (updatePrev) {
                mPrevSpanX = mCurrSpanX;
                mPrevSpanY = mCurrSpanY;
                mPrevSpan = mCurrSpan;
            }
        }
        return true;
    }
    /**
     * Returns {@code true} if a scale gesture is in progress.
     */
    public boolean isInProgress() {
        return mInProgress;
    }
    /**
     * Get the X coordinate of the current gesture's focal point.
     * If a gesture is in progress, the focal point is between
     * each of the pointers forming the gesture.
     *
     * If {@link #isInProgress()} would return false, the result of this
     * function is undefined.
     *
     * @return X coordinate of the focal point in pixels.
     */
    public float getFocusX() {
        return mFocusX;
    }
    /**
     * Get the Y coordinate of the current gesture's focal point.
     * If a gesture is in progress, the focal point is between
     * each of the pointers forming the gesture.
     *
     * If {@link #isInProgress()} would return false, the result of this
     * function is undefined.
     *
     * @return Y coordinate of the focal point in pixels.
     */
    public float getFocusY() {
        return mFocusY;
    }
    /**
     * Return the average distance between each of the pointers forming the
     * gesture in progress through the focal point.
     *
     * @return Distance between pointers in pixels.
     */
    public float getCurrentSpan() {
        return mCurrSpan;
    }
    /**
     * Return the average X distance between each of the pointers forming the
     * gesture in progress through the focal point.
     *
     * @return Distance between pointers in pixels.
     */
    public float getCurrentSpanX() {
        return mCurrSpanX;
    }
    /**
     * Return the average Y distance between each of the pointers forming the
     * gesture in progress through the focal point.
     *
     * @return Distance between pointers in pixels.
     */
    public float getCurrentSpanY() {
        return mCurrSpanY;
    }
    /**
     * Return the previous average distance between each of the pointers forming the
     * gesture in progress through the focal point.
     *
     * @return Previous distance between pointers in pixels.
     */
    public float getPreviousSpan() {
        return mPrevSpan;
    }
    /**
     * Return the previous average X distance between each of the pointers forming the
     * gesture in progress through the focal point.
     *
     * @return Previous distance between pointers in pixels.
     */
    public float getPreviousSpanX() {
        return mPrevSpanX;
    }
    /**
     * Return the previous average Y distance between each of the pointers forming the
     * gesture in progress through the focal point.
     *
     * @return Previous distance between pointers in pixels.
     */
    public float getPreviousSpanY() {
        return mPrevSpanY;
    }
    /**
     * Return the scaling factor from the previous scale event to the current
     * event. This value is defined as
     * ({@link #getCurrentSpan()} / {@link #getPreviousSpan()}).
     *
     * @return The current scaling factor.
     */
    public float getScaleFactor() {
        return mPrevSpan > 0 ? mCurrSpan / mPrevSpan : 1;
    }
    /**
     * Return the time difference in milliseconds between the previous
     * accepted scaling event and the current scaling event.
     *
     * @return Time difference since the last scaling event in milliseconds.
     */
    public long getTimeDelta() {
        return mCurrTime - mPrevTime;
    }
    /**
     * Return the event time of the current event being processed.
     *
     * @return Current event time in milliseconds.
     */
    public long getEventTime() {
        return mCurrTime;
    }

}
