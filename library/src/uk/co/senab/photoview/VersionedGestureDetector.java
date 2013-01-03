package uk.co.senab.photoview;

/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;

public abstract class VersionedGestureDetector {
	static final String LOG_TAG = "VersionedGestureDetector";
	OnGestureListener mListener;

	public static VersionedGestureDetector newInstance(Context context, OnGestureListener listener) {
		final int sdkVersion = Build.VERSION.SDK_INT;
		VersionedGestureDetector detector = null;

		if (sdkVersion < Build.VERSION_CODES.ECLAIR) {
			detector = new CupcakeDetector(context);
		} else if (sdkVersion < Build.VERSION_CODES.FROYO) {
			detector = new EclairDetector(context);
		} else {
			detector = new FroyoDetector(context);
		}

		detector.mListener = listener;

		return detector;
	}

	public abstract boolean onTouchEvent(MotionEvent ev);

	public abstract boolean isScaling();

	public static interface OnGestureListener {
		public void onDrag(float dx, float dy);

		public void onFling(float startX, float startY, float velocityX, float velocityY);

		public void onScale(float scaleFactor, float focusX, float focusY);
	}

	private static class CupcakeDetector extends VersionedGestureDetector {

		float mLastTouchX;
		float mLastTouchY;
		final float mTouchSlop;
		final float mMinimumVelocity;

		public CupcakeDetector(Context context) {
			final ViewConfiguration configuration = ViewConfiguration.get(context);
			mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
			mTouchSlop = configuration.getScaledTouchSlop();
		}

		private VelocityTracker mVelocityTracker;
		private boolean mIsDragging;

		float getActiveX(MotionEvent ev) {
			return ev.getX();
		}

		float getActiveY(MotionEvent ev) {
			return ev.getY();
		}

		public boolean isScaling() {
			return false;
		}

		@Override
		public boolean onTouchEvent(MotionEvent ev) {
			switch (ev.getAction()) {
				case MotionEvent.ACTION_DOWN: {
					mVelocityTracker = VelocityTracker.obtain();
					mVelocityTracker.addMovement(ev);

					mLastTouchX = getActiveX(ev);
					mLastTouchY = getActiveY(ev);
					mIsDragging = false;
					break;
				}

				case MotionEvent.ACTION_MOVE: {
					final float x = getActiveX(ev);
					final float y = getActiveY(ev);
					final float dx = x - mLastTouchX, dy = y - mLastTouchY;

					if (!mIsDragging) {
						// Use Pythagoras to see if drag length is larger than
						// touch slop
						mIsDragging = FloatMath.sqrt((dx * dx) + (dy * dy)) >= mTouchSlop;
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
					// Recycle Velocity Tracker
					if (null != mVelocityTracker) {
						mVelocityTracker.recycle();
						mVelocityTracker = null;
					}
					break;
				}

				case MotionEvent.ACTION_UP: {
					if (mIsDragging) {
						if (null != mVelocityTracker) {
							mLastTouchX = getActiveX(ev);
							mLastTouchY = getActiveY(ev);

							// Compute velocity within the last 1000ms
							mVelocityTracker.addMovement(ev);
							mVelocityTracker.computeCurrentVelocity(1000);

							final float vX = mVelocityTracker.getXVelocity(), vY = mVelocityTracker.getYVelocity();

							// If the velocity is greater than minVelocity, call
							// listener
							if (Math.max(Math.abs(vX), Math.abs(vY)) >= mMinimumVelocity) {
								mListener.onFling(mLastTouchX, mLastTouchY, -vX, -vY);
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
			}

			return true;
		}
	}

	@TargetApi(5)
	private static class EclairDetector extends CupcakeDetector {
		private static final int INVALID_POINTER_ID = -1;
		private int mActivePointerId = INVALID_POINTER_ID;
		private int mActivePointerIndex = 0;

		public EclairDetector(Context context) {
			super(context);
		}

		@Override
		float getActiveX(MotionEvent ev) {
			try {
				return ev.getX(mActivePointerIndex);
			} catch (Exception e) {
				return ev.getX();
			}
		}

		@Override
		float getActiveY(MotionEvent ev) {
			try {
				return ev.getY(mActivePointerIndex);
			} catch (Exception e) {
				return ev.getY();
			}
		}

		@Override
		public boolean onTouchEvent(MotionEvent ev) {
			final int action = ev.getAction();
			switch (action & MotionEvent.ACTION_MASK) {
				case MotionEvent.ACTION_DOWN:
					mActivePointerId = ev.getPointerId(0);
					break;
				case MotionEvent.ACTION_CANCEL:
				case MotionEvent.ACTION_UP:
					mActivePointerId = INVALID_POINTER_ID;
					break;
				case MotionEvent.ACTION_POINTER_UP:
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

			mActivePointerIndex = ev.findPointerIndex(mActivePointerId != INVALID_POINTER_ID ? mActivePointerId : 0);
			return super.onTouchEvent(ev);
		}
	}

	@TargetApi(8)
	private static class FroyoDetector extends EclairDetector {

		private final ScaleGestureDetector mDetector;

		// Needs to be an inner class so that we don't hit
		// VerifyError's on API 4.
		private final OnScaleGestureListener mScaleListener = new OnScaleGestureListener() {

			@Override
			public boolean onScale(ScaleGestureDetector detector) {
				mListener.onScale(detector.getScaleFactor(), detector.getFocusX(), detector.getFocusY());
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

		public FroyoDetector(Context context) {
			super(context);
			mDetector = new ScaleGestureDetector(context, mScaleListener);
		}

		@Override
		public boolean isScaling() {
			return mDetector.isInProgress();
		}

		@Override
		public boolean onTouchEvent(MotionEvent ev) {
			mDetector.onTouchEvent(ev);
			return super.onTouchEvent(ev);
		}

	}
}