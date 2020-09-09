/*
 Copyright 2011, 2012 Chris Banes.
 <p/>
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 <p/>
 http://www.apache.org/licenses/LICENSE-2.0
 <p/>
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package com.github.chrisbanes.photoview

import android.content.Context
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.OnScaleGestureListener
import android.view.VelocityTracker
import android.view.ViewConfiguration

/**
 * Does a whole lot of gesture detecting.
 */
internal class CustomGestureDetector() {
    private var mActivePointerId = INVALID_POINTER_ID
    private var mActivePointerIndex = 0
    private var mDetector: ScaleGestureDetector? = null
    private var mVelocityTracker: VelocityTracker? = null
    var isDragging = false
        private set
    private var mLastTouchX = 0f
    private var mLastTouchY = 0f
    private var mTouchSlop: Float = 0.0f
    private var mMinimumVelocity: Float = 0.0f
    private lateinit var mListener: OnGestureListener
    private fun getActiveX(ev: MotionEvent): Float {
        return try {
            ev.getX(mActivePointerIndex)
        } catch (e: Exception) {
            ev.x
        }
    }

    private fun getActiveY(ev: MotionEvent): Float {
        return try {
            ev.getY(mActivePointerIndex)
        } catch (e: Exception) {
            ev.y
        }
    }

    val isScaling: Boolean
        get() = mDetector!!.isInProgress

    fun onTouchEvent(ev: MotionEvent): Boolean {
        return try {
            mDetector!!.onTouchEvent(ev)
            processTouchEvent(ev)
        } catch (e: IllegalArgumentException) {
            // Fix for support lib bug, happening when onDestroy is called
            true
        }
    }

    private fun processTouchEvent(ev: MotionEvent): Boolean {
        val action = ev.action
        when (action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                mActivePointerId = ev.getPointerId(0)
                mVelocityTracker = VelocityTracker.obtain()
                if (null != mVelocityTracker) {
                    mVelocityTracker!!.addMovement(ev)
                }
                mLastTouchX = getActiveX(ev)
                mLastTouchY = getActiveY(ev)
                isDragging = false
            }
            MotionEvent.ACTION_MOVE -> {
                val x = getActiveX(ev)
                val y = getActiveY(ev)
                val dx = x - mLastTouchX
                val dy = y - mLastTouchY
                if (!isDragging) {
                    // Use Pythagoras to see if drag length is larger than
                    // touch slop
                    isDragging = Math.sqrt(dx * dx + (dy * dy).toDouble()) >= mTouchSlop
                }
                if (isDragging) {
                    mListener.onDrag(dx, dy)
                    mLastTouchX = x
                    mLastTouchY = y
                    if (null != mVelocityTracker) {
                        mVelocityTracker!!.addMovement(ev)
                    }
                }
            }
            MotionEvent.ACTION_CANCEL -> {
                mActivePointerId = INVALID_POINTER_ID
                // Recycle Velocity Tracker
                if (null != mVelocityTracker) {
                    mVelocityTracker!!.recycle()
                    mVelocityTracker = null
                }
            }
            MotionEvent.ACTION_UP -> {
                mActivePointerId = INVALID_POINTER_ID
                if (isDragging) {
                    if (null != mVelocityTracker) {
                        mLastTouchX = getActiveX(ev)
                        mLastTouchY = getActiveY(ev)

                        // Compute velocity within the last 1000ms
                        mVelocityTracker!!.addMovement(ev)
                        mVelocityTracker!!.computeCurrentVelocity(1000)
                        val vX = mVelocityTracker!!.xVelocity
                        val vY = mVelocityTracker!!
                                .yVelocity

                        // If the velocity is greater than minVelocity, call
                        // listener
                        if (Math.max(Math.abs(vX), Math.abs(vY)) >= mMinimumVelocity) {
                            mListener.onFling(mLastTouchX, mLastTouchY, -vX,
                                    -vY)
                        }
                    }
                }

                // Recycle Velocity Tracker
                if (null != mVelocityTracker) {
                    mVelocityTracker!!.recycle()
                    mVelocityTracker = null
                }
            }
            MotionEvent.ACTION_POINTER_UP -> {
                val pointerIndex = Util.getPointerIndex(ev.action)
                val pointerId = ev.getPointerId(pointerIndex)
                if (pointerId == mActivePointerId) {
                    // This was our active pointer going up. Choose a new
                    // active pointer and adjust accordingly.
                    val newPointerIndex = if (pointerIndex == 0) 1 else 0
                    mActivePointerId = ev.getPointerId(newPointerIndex)
                    mLastTouchX = ev.getX(newPointerIndex)
                    mLastTouchY = ev.getY(newPointerIndex)
                }
            }
        }
        mActivePointerIndex = ev
                .findPointerIndex(if (mActivePointerId != INVALID_POINTER_ID) mActivePointerId else 0)
        return true
    }

    companion object {
        private const val INVALID_POINTER_ID = -1
    }

    constructor(context: Context?, listener: OnGestureListener) : this() {
        val configuration = ViewConfiguration.get(context)
        mMinimumVelocity = configuration.scaledMinimumFlingVelocity.toFloat()
        mTouchSlop = configuration.scaledTouchSlop.toFloat()
        mListener = listener
        val mScaleListener: OnScaleGestureListener = object : OnScaleGestureListener {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                val scaleFactor = detector.scaleFactor
                if (java.lang.Float.isNaN(scaleFactor) || java.lang.Float.isInfinite(scaleFactor)) return false
                if (scaleFactor >= 0) {
                    mListener.onScale(scaleFactor,
                            detector.focusX, detector.focusY)
                }
                return true
            }

            override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
                return true
            }

            override fun onScaleEnd(detector: ScaleGestureDetector) {
                // NO-OP
            }
        }
        mDetector = ScaleGestureDetector(context, mScaleListener)
    }
}
