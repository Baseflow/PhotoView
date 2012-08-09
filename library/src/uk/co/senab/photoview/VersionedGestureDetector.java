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

import android.content.Context;
import android.os.Build;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.VelocityTracker;

public abstract class VersionedGestureDetector {
	private static final String TAG = "VersionedGestureDetector";

	OnGestureListener mListener;

	public static VersionedGestureDetector newInstance(Context context, OnGestureListener listener) {
		final int sdkVersion = Build.VERSION.SDK_INT;
		VersionedGestureDetector detector = null;
		if (sdkVersion < Build.VERSION_CODES.ECLAIR) {
			detector = new CupcakeDetector();
		} else if (sdkVersion < Build.VERSION_CODES.FROYO) {
			detector = new EclairDetector();
		} else {
			detector = new FroyoDetector(context);
		}

		detector.mListener = listener;

		return detector;
	}

	public abstract boolean onTouchEvent(MotionEvent ev);

	public static interface OnGestureListener {
		public void onDrag(float dx, float dy);

		public void onFling(float startX, float startY, float velocityX, float velocityY);

		public void onScale(float scaleFactor, float focusX, float focusY);
	}

	private static class CupcakeDetector extends VersionedGestureDetector {

		float mLastTouchX;
		float mLastTouchY;

		private VelocityTracker mVelocityTracker;

		float getActiveX(MotionEvent ev) {
			return ev.getX();
		}

		float getActiveY(MotionEvent ev) {
			return ev.getY();
		}

		@Override
		public boolean onTouchEvent(MotionEvent ev) {
			if (null == mVelocityTracker) {
				mVelocityTracker = VelocityTracker.obtain();
			}
			mVelocityTracker.addMovement(ev);

			switch (ev.getAction()) {
				case MotionEvent.ACTION_DOWN: {
					mLastTouchX = getActiveX(ev);
					mLastTouchY = getActiveY(ev);
					break;
				}
				case MotionEvent.ACTION_MOVE: {
					final float x = getActiveX(ev);
					final float y = getActiveY(ev);

					mListener.onDrag(x - mLastTouchX, y - mLastTouchY);

					mLastTouchX = x;
					mLastTouchY = y;
					break;
				}

				case MotionEvent.ACTION_CANCEL:
				case MotionEvent.ACTION_UP: {
					// Compute velocity for with the unit as 1000ms
					mVelocityTracker.computeCurrentVelocity(1000);

					// Call listener
					mListener.onFling(mLastTouchX, mLastTouchY, -mVelocityTracker.getXVelocity(),
							-mVelocityTracker.getYVelocity());

					// Recycle Velocity Tracker
					mVelocityTracker.recycle();
					mVelocityTracker = null;
					break;
				}
			}
			return true;
		}
	}

	private static class EclairDetector extends CupcakeDetector {
		private static final int INVALID_POINTER_ID = -1;
		private int mActivePointerId = INVALID_POINTER_ID;
		private int mActivePointerIndex = 0;

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

	private static class FroyoDetector extends EclairDetector {
		private ScaleGestureDetector mDetector;

		public FroyoDetector(Context context) {
			mDetector = new ScaleGestureDetector(context, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
				@Override
				public boolean onScale(ScaleGestureDetector detector) {
					mListener.onScale(detector.getScaleFactor(), detector.getFocusX(), detector.getFocusY());
					return true;
				}
			});
		}

		@Override
		public boolean onTouchEvent(MotionEvent ev) {
			mDetector.onTouchEvent(ev);
			return super.onTouchEvent(ev);
		}
	}
}