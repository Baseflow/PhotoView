package com.github.chrisbanes.photoview;

import android.content.Context;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;

class PhotoGestureDetector {

    private static final int INVALID_POINTER_ID = -1;

    private final ScaleGestureDetector mDetector;
    private OnGestureListener mListener;
    private float mLastTouchX;
    private float mLastTouchY;
    private final float mTouchSlop;
    private final float mMinimumVelocity;
    private VelocityTracker mVelocityTracker;
    private boolean mIsDragging;
    private int mActivePointerId = INVALID_POINTER_ID;
    private int mActivePointerIndex = 0;

    public PhotoGestureDetector(Context context, OnGestureListener onGestureListener) {
        mListener = onGestureListener;
        final ViewConfiguration configuration = ViewConfiguration
                .get(context);
        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        mTouchSlop = configuration.getScaledTouchSlop();

        ScaleGestureDetector.OnScaleGestureListener mScaleListener = new ScaleGestureDetector.OnScaleGestureListener() {

            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                float scaleFactor = detector.getScaleFactor();

                if (Float.isNaN(scaleFactor) || Float.isInfinite(scaleFactor)) {
                    return false;
                }

                mListener.onScale(scaleFactor,
                        detector.getFocusX(), detector.getFocusY());
                return true;
            }

            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                return true;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
                // NO-OP
            }
        };
        mDetector = new ScaleGestureDetector(context, mScaleListener);
    }

    public boolean isScaling() {
        return mDetector.isInProgress();
    }

    public boolean isDragging() {
        return mDetector.isInProgress();
    }

    public boolean onTouchEvent(MotionEvent ev) {
        try {
            mDetector.onTouchEvent(ev);
            return processTouchEvent(ev);
        } catch (IllegalArgumentException e) {
            // Fix for support lib bug, happening when onDestroy is called
            return true;
        }
    }

    private boolean processTouchEvent(MotionEvent ev) {
        switch (ev.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
                mActivePointerId = ev.getPointerId(0);
                mVelocityTracker = VelocityTracker.obtain();
                if (null != mVelocityTracker) {
                    mVelocityTracker.addMovement(ev);
                }

                mLastTouchX = ev.getX(mActivePointerIndex);
                mLastTouchY = ev.getY(mActivePointerIndex);
                mIsDragging = false;
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                final float x = ev.getX(mActivePointerIndex);
                final float y = ev.getY(mActivePointerIndex);
                final float dx = x - mLastTouchX, dy = y - mLastTouchY;

                if (!mIsDragging) {
                    // Use Pythagoras to see if drag length is larger than
                    // touch slop
                    mIsDragging = Math.sqrt((dx * dx) + (dy * dy)) >= mTouchSlop;
                }

                if (mIsDragging) {
                    mListener.onDrag(dx, dy);
                    mLastTouchX = x;
                    mLastTouchY = y;

                    if (null != mVelocityTracker) {
                        mVelocityTracker.addMovement(ev);
                    }
                }
                break;
            }

            case MotionEvent.ACTION_CANCEL: {
                mActivePointerId = INVALID_POINTER_ID;
                // Recycle Velocity Tracker
                if (null != mVelocityTracker) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }
                break;
            }

            case MotionEvent.ACTION_UP: {
                mActivePointerId = INVALID_POINTER_ID;
                if (mIsDragging) {
                    if (null != mVelocityTracker) {
                        mLastTouchX = ev.getX();
                        mLastTouchY = ev.getY();

                        // Compute velocity within the last 1000ms
                        mVelocityTracker.addMovement(ev);
                        mVelocityTracker.computeCurrentVelocity(1000);

                        final float vX = mVelocityTracker.getXVelocity(), vY = mVelocityTracker
                                .getYVelocity();

                        // If the velocity is greater than minVelocity, call
                        // listener
                        if (Math.max(Math.abs(vX), Math.abs(vY)) >= mMinimumVelocity) {
                            mListener.onFling(mLastTouchX, mLastTouchY, -vX,
                                    -vY);
                        }
                    }
                }

                // Recycle Velocity Tracker
                if (null != mVelocityTracker) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }
                break;
            }

            case MotionEvent.ACTION_POINTER_UP:
                // Ignore deprecation, ACTION_POINTER_ID_MASK and
                // ACTION_POINTER_ID_SHIFT has same value and are deprecated
                // You can have either deprecation or lint target api warning
                final int pointerIndex = (ev.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                final int pointerId = ev.getPointerId(pointerIndex);
                if (pointerId == mActivePointerId) {
                    // This was our active pointer going up. Choose a new
                    // active pointer and adjust accordingly.
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    mActivePointerId = ev.getPointerId(newPointerIndex);
                    mLastTouchX = ev.getX(newPointerIndex);
                    mLastTouchY = ev.getY(newPointerIndex);
                }
                break;
        }
        mActivePointerIndex = ev.findPointerIndex(
                mActivePointerId != INVALID_POINTER_ID ? mActivePointerId : 0);

        return true;
    }
}
