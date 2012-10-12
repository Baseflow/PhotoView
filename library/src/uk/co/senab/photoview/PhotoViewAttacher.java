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
package uk.co.senab.photoview;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class PhotoViewAttacher implements View.OnTouchListener, VersionedGestureDetector.OnGestureListener,
		GestureDetector.OnDoubleTapListener, OnGlobalLayoutListener {

	static final boolean DEBUG = false;
	static final String LOG_TAG = "PhotoViewAttacher";

	/**
	 * Interface definition for a callback to be invoked when the internal
	 * Matrix has changed for this View.
	 * 
	 * @author Chris Banes
	 */
	public static interface OnMatrixChangedListener {
		/**
		 * Callback for when the Matrix displaying the Drawable has changed.
		 * This could be because the View's bounds have changed, or the user has
		 * zoomed.
		 * 
		 * @param rect
		 *            - Rectangle displaying the Drawable's new bounds.
		 */
		void onMatrixChanged(RectF rect);
	}

	/**
	 * Interface definition for a callback to be invoked when the Photo is
	 * tapped with a single tap.
	 * 
	 * @author Chris Banes
	 */
	public static interface OnPhotoTapListener {

		/**
		 * A callback to receive where the user taps on a photo. You will only
		 * receive a callback if the user taps on the actual photo, tapping on
		 * 'whitespace' will be ignored.
		 * 
		 * @param view
		 *            - View the user tapped
		 * @param x
		 *            - where the user tapped from the left, as percentage of
		 *            the Drawable width.
		 * @param y
		 *            - where the user tapped from the top, as percentage of the
		 *            Drawable height.
		 */
		void onPhotoTap(View view, float x, float y);
	}

	private class FlingRunnable implements Runnable {

		private final ScrollerProxy mScroller;
		private int mCurrentX, mCurrentY;

		public FlingRunnable() {
			Context context = mImageView.getContext();
			mScroller = ScrollerProxy.getScroller(context);
		}

		public void fling(int velocityX, int velocityY) {
			final int viewHeight = mImageView.getHeight();
			final int viewWidth = mImageView.getWidth();
			final RectF rect = getDisplayRect();

			final int minX, maxX, minY, maxY;

			final int startX = Math.round(-rect.left);
			if (viewWidth < rect.width()) {
				minX = 0;
				maxX = Math.round(rect.width() - viewWidth);
			} else {
				minX = maxX = startX;
			}

			final int startY = Math.round(-rect.top);
			if (viewHeight < rect.height()) {
				minY = 0;
				maxY = Math.round(rect.height() - viewHeight);
			} else {
				minY = maxY = startY;
			}

			mCurrentX = startX;
			mCurrentY = startY;

			if (DEBUG) {
				Log.d(LOG_TAG, "fling. StartX:" + startX + " StartY:" + startY + " MaxX:" + maxX + " MaxY:" + maxY);
			}
			mScroller.fling(startX, startY, velocityX, velocityY, minX, maxX, minY, maxY, 0, 0);
		}

		@Override
		public void run() {
			if (mScroller.computeScrollOffset()) {

				final int newX = mScroller.getCurrX();
				final int newY = mScroller.getCurrY();

				mSuppMatrix.postTranslate(mCurrentX - newX, mCurrentY - newY);
				setImageViewMatrix(getDisplayMatrix());

				mCurrentX = newX;
				mCurrentY = newY;

				if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
					SDK16.postOnAnimation(mImageView, this);
				} else {
					mImageView.postDelayed(this, 10);
				}
			}
		}

		public void cancelFling() {
			if (DEBUG) {
				Log.d(LOG_TAG, "Cancel Fling");
			}
			mScroller.forceFinished(true);
		}
	}

	private class AnimatedZoomRunnable implements Runnable {

		// These are 'postScale' values, means they're compounded each iteration
		static final float ANIMATION_SCALE_PER_ITERATION_IN = 1.07f;
		static final float ANIMATION_SCALE_PER_ITERATION_OUT = 0.93f;

		private final float mFocalX, mFocalY;
		private final float mTargetZoom;
		private final float mDeltaScale;

		public AnimatedZoomRunnable(final float currentZoom, final float targetZoom, final float focalX,
				final float focalY) {
			mTargetZoom = targetZoom;
			mFocalX = focalX;
			mFocalY = focalY;

			if (currentZoom < targetZoom) {
				mDeltaScale = ANIMATION_SCALE_PER_ITERATION_IN;
			} else {
				mDeltaScale = ANIMATION_SCALE_PER_ITERATION_OUT;
			}
		}

		public void run() {
			mSuppMatrix.postScale(mDeltaScale, mDeltaScale, mFocalX, mFocalY);
			centerAndDisplayMatrix();

			final float currentScale = getScale();

			if ((mDeltaScale > 1f && currentScale < mTargetZoom) || (mDeltaScale < 1f && mTargetZoom < currentScale)) {
				// We haven't hit our target scale yet, so post ourselves again
				postSelf();

			} else {
				// We've scaled past our target zoom, so calculate the
				// necessary scale so we're back at target zoom
				final float delta = mTargetZoom / currentScale;
				mSuppMatrix.postScale(delta, delta, mFocalX, mFocalY);
				centerAndDisplayMatrix();
			}
		}

		private void postSelf() {
			if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
				SDK16.postOnAnimation(mImageView, this);
			} else {
				mImageView.postDelayed(this, 10);
			}
		}
	}

	static final int EDGE_NONE = -1;
	static final int EDGE_LEFT = 0;
	static final int EDGE_RIGHT = 1;
	static final int EDGE_BOTH = 2;

	private static final float MAX_ZOOM = 3.0f;
	private static final float MID_ZOOM = 1.75f;
	private static final float MIN_ZOOM = 1.0f;

	// Gesture Detectors
	private GestureDetector mGestureDetector;
	private VersionedGestureDetector mScaleDragDetector;

	private final Matrix mBaseMatrix = new Matrix();
	private final Matrix mDrawMatrix = new Matrix();
	private final Matrix mSuppMatrix = new Matrix();

	// Listeners
	private OnMatrixChangedListener mMatrixChangeListener;
	private OnPhotoTapListener mPhotoTapListener;

	// Saves us allocating a new float[] when getValue is called
	private final float[] mMatrixValues = new float[9];

	private int mScrollEdge = EDGE_BOTH;
	private boolean mZoomEnabled;

	private FlingRunnable mCurrentFlingRunnable;

	private final ImageView mImageView;

	private int mIvTop, mIvRight, mIvBottom, mIvLeft;

	public PhotoViewAttacher(ImageView imageView) {
		mImageView = imageView;
		mImageView.setOnTouchListener(this);
		mImageView.getViewTreeObserver().addOnGlobalLayoutListener(this);

		// Create Gesture Detectors...
		mScaleDragDetector = VersionedGestureDetector.newInstance(mImageView.getContext(), this);
		mGestureDetector = new GestureDetector(mImageView.getContext(), new GestureDetector.SimpleOnGestureListener());
		mGestureDetector.setOnDoubleTapListener(this);

		// Make sure we using MATRIX Scale Type
		mImageView.setScaleType(ScaleType.MATRIX);

		// Finally, update the UI so that we're zoomable
		setZoomable(true);
	}

	/**
	 * Returns true if the PhotoView is set to allow zooming of Photos.
	 * 
	 * @return true if the PhotoView allows zooming.
	 */
	public boolean canZoom() {
		return mZoomEnabled;
	}

	/**
	 * Gets the Display Rectangle of the currently displayed Drawable. The
	 * Rectangle is relative to this View and includes all scaling and
	 * translations.
	 * 
	 * @return - RectF of Displayed Drawable
	 */
	public RectF getDisplayRect() {
		checkMatrixBounds();
		return getDisplayRect(getDisplayMatrix());
	}

	/**
	 * Returns the current scale value
	 * 
	 * @return float - current scale value
	 */
	public float getScale() {
		return getValue(mSuppMatrix, Matrix.MSCALE_X);
	}

	public final boolean onDoubleTap(MotionEvent ev) {
		try {
			float scale = getScale();
			float x = ev.getX();
			float y = ev.getY();

			if (scale < MID_ZOOM) {
				zoomTo(MID_ZOOM, x, y);
			} else if (scale >= MID_ZOOM && scale < MAX_ZOOM) {
				zoomTo(MAX_ZOOM, x, y);
			} else {
				zoomTo(MIN_ZOOM, x, y);
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			// Can sometimes happen when getX() and getY() is called
		}

		return true;
	}

	public final boolean onDoubleTapEvent(MotionEvent e) {
		// Wait for the confirmed onDoubleTap() instead
		return false;
	}

	public final void onDrag(float dx, float dy) {
		if (DEBUG) {
			Log.d(LOG_TAG, String.format("onDrag: dx: %.2f. dy: %.2f", dx, dy));
		}

		mSuppMatrix.postTranslate(dx, dy);
		centerAndDisplayMatrix();

		/**
		 * Here we decide whether to let the ImageView's parent to start taking
		 * over the touch event.
		 * 
		 * We never want the parent to take over if we're scaling. We then check
		 * the edge we're on, and the direction of the scroll (i.e. if we're
		 * pulling against the edge, aka 'overscrolling', let the parent take
		 * over).
		 */
		if (!mScaleDragDetector.isScaling()) {
			if (mScrollEdge == EDGE_BOTH || (mScrollEdge == EDGE_LEFT && dx >= 1f)
					|| (mScrollEdge == EDGE_RIGHT && dx <= -1f)) {
				mImageView.getParent().requestDisallowInterceptTouchEvent(false);
			}
		}
	}

	@Override
	public final void onFling(float startX, float startY, float velocityX, float velocityY) {
		if (DEBUG) {
			Log.d(LOG_TAG, "onFling. sX: " + startX + " sY: " + startY + " Vx: " + velocityX + " Vy: " + velocityY);
		}

		mCurrentFlingRunnable = new FlingRunnable();
		mCurrentFlingRunnable.fling((int) velocityX, (int) velocityY);
		mImageView.post(mCurrentFlingRunnable);
	}

	@Override
	public final void onGlobalLayout() {
		if (mZoomEnabled) {

			final int top = mImageView.getTop();
			final int right = mImageView.getRight();
			final int bottom = mImageView.getBottom();
			final int left = mImageView.getLeft();

			/**
			 * We need to check whether the ImageView's bounds have changed.
			 * This would be easier if we targeted API 11+ as we could just use
			 * View.OnLayoutChangeListener. Instead we have to replicate the
			 * work, keeping track of the ImageView's bounds and then checking
			 * if the values change.
			 */
			if (top != mIvTop || bottom != mIvBottom || left != mIvLeft || right != mIvRight) {
				// Update our base matrix, as the bounds have changed
				updateBaseMatrix(mImageView.getDrawable());

				// Update values as something has changed
				mIvTop = top;
				mIvRight = right;
				mIvBottom = bottom;
				mIvLeft = left;
			}
		}
	}

	public final void onScale(float scaleFactor, float focusX, float focusY) {
		if (DEBUG) {
			Log.d(LOG_TAG, String.format("onScale: scale: %.2f. fX: %.2f. fY: %.2f", scaleFactor, focusX, focusY));
		}

		if (getScale() < MAX_ZOOM || scaleFactor < 1f) {
			mSuppMatrix.postScale(scaleFactor, scaleFactor, focusX, focusY);
			centerAndDisplayMatrix();
		}
	}

	public final boolean onSingleTapConfirmed(MotionEvent e) {
		if (null != mPhotoTapListener) {
			final RectF displayRect = getDisplayRect();

			if (null != displayRect) {
				final float x = e.getX(), y = e.getY();

				// Check to see if the user tapped on the photo
				if (displayRect.contains(x, y)) {

					float xResult = (x - displayRect.left) / displayRect.width();
					float yResult = (y - displayRect.top) / displayRect.height();

					mPhotoTapListener.onPhotoTap(mImageView, xResult, yResult);
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public final boolean onTouch(View v, MotionEvent ev) {
		if (mZoomEnabled) {

			switch (ev.getAction()) {
				case MotionEvent.ACTION_DOWN:
					// First, disable the Parent from intercepting the touch
					// event
					v.getParent().requestDisallowInterceptTouchEvent(true);

					// If we're flinging, and the user presses down, cancel
					// fling
					cancelFling();
					break;

				case MotionEvent.ACTION_CANCEL:
				case MotionEvent.ACTION_UP:
					// If the user has zoomed less than MIN_ZOOM, zoom back
					// to 1.0f
					if (getScale() < MIN_ZOOM) {
						v.post(new AnimatedZoomRunnable(getScale(), MIN_ZOOM, 0f, 0f));
						return true;
					}
					break;
			}

			// Check to see if the user double tapped
			if (null != mGestureDetector && mGestureDetector.onTouchEvent(ev)) {
				return true;
			}

			// Finally, try the Scale/Drag detector
			if (null != mScaleDragDetector && mScaleDragDetector.onTouchEvent(ev)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Register a callback to be invoked when the Matrix has changed for this
	 * View. An example would be the user panning or scaling the Photo.
	 * 
	 * @param listener
	 *            - Listener to be registered.
	 */
	public void setOnMatrixChangeListener(OnMatrixChangedListener listener) {
		mMatrixChangeListener = listener;
	}

	/**
	 * Register a callback to be invoked when the Photo displayed by this View
	 * is tapped with a single tap.
	 * 
	 * @param listener
	 *            - Listener to be registered.
	 */
	public void setOnPhotoTapListener(OnPhotoTapListener listener) {
		mPhotoTapListener = listener;
	}

	/**
	 * Allows you to enable/disable the zoom functionality on the ImageView.
	 * When disable the ImageView reverts to using the FIT_CENTER matrix.
	 * 
	 * @param zoomable
	 *            - Whether the zoom functionality is enabled.
	 */
	public final void setZoomable(boolean zoomable) {
		mZoomEnabled = zoomable;
		update();
	}

	public final void update() {
		if (mZoomEnabled) {
			// Make sure we using MATRIX Scale Type
			mImageView.setScaleType(ScaleType.MATRIX);

			// Update the base matrix using the current drawable
			updateBaseMatrix(mImageView.getDrawable());
		} else {
			// Reset the Matrix...
			resetMatrix();
		}
	}

	/**
	 * Zooms to the specified scale, around the focal point given.
	 * 
	 * @param scale
	 *            - Scale to zoom to
	 * @param focalX
	 *            - X Focus Point
	 * @param focalY
	 *            - Y Focus Point
	 */
	public void zoomTo(float scale, float focalX, float focalY) {
		mImageView.post(new AnimatedZoomRunnable(getScale(), scale, focalX, focalY));
	}

	protected Matrix getDisplayMatrix() {
		mDrawMatrix.set(mBaseMatrix);
		mDrawMatrix.postConcat(mSuppMatrix);
		return mDrawMatrix;
	}

	private void cancelFling() {
		if (null != mCurrentFlingRunnable) {
			mCurrentFlingRunnable.cancelFling();
			mCurrentFlingRunnable = null;
		}
	}

	/**
	 * Helper method that simply centers the Matrix, and then displays the
	 * result
	 */
	private void centerAndDisplayMatrix() {
		checkMatrixBounds();
		setImageViewMatrix(getDisplayMatrix());
	}

	private void checkMatrixBounds() {
		Drawable d = mImageView.getDrawable();
		if (null == d) {
			return;
		}

		RectF rect = new RectF(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
		getDisplayMatrix().mapRect(rect);

		final float height = rect.height(), width = rect.width();
		float deltaX = 0, deltaY = 0;

		final int viewHeight = mImageView.getHeight();
		if (height < viewHeight) {
			deltaY = (viewHeight - height) / 2 - rect.top;
		} else if (rect.top > 0) {
			deltaY = -rect.top;
		} else if (rect.bottom < viewHeight) {
			deltaY = viewHeight - rect.bottom;
		}

		final int viewWidth = mImageView.getWidth();
		if (width < viewWidth) {
			deltaX = (viewWidth - width) / 2 - rect.left;
			mScrollEdge = EDGE_BOTH;
		} else if (rect.left > 0) {
			mScrollEdge = EDGE_LEFT;
			deltaX = -rect.left;
		} else if (rect.right < viewWidth) {
			deltaX = viewWidth - rect.right;
			mScrollEdge = EDGE_RIGHT;
		} else {
			mScrollEdge = EDGE_NONE;
		}

		mSuppMatrix.postTranslate(deltaX, deltaY);
	}

	/**
	 * Helper method that maps the supplied Matrix to the current Drawable
	 * 
	 * @param Matrix
	 *            - Matrix to map Drawable against
	 * @return RectF - Displayed Rectangle
	 */
	private RectF getDisplayRect(Matrix matrix) {
		Drawable d = mImageView.getDrawable();
		if (null != d) {
			RectF rect = new RectF(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
			matrix.mapRect(rect);
			return rect;
		}
		return null;
	}

	/**
	 * Helper method that 'unpacks' a Matrix and returns the required value
	 * 
	 * @param matrix
	 *            - Matrix to unpack
	 * @param whichValue
	 *            - Which value from Matrix.M* to return
	 * @return float - returned value
	 */
	private float getValue(Matrix matrix, int whichValue) {
		matrix.getValues(mMatrixValues);
		return mMatrixValues[whichValue];
	}

	/**
	 * Resets the Matrix back to FIT_CENTER, and then displays it.s
	 */
	private void resetMatrix() {
		mSuppMatrix.reset();
		setImageViewMatrix(getDisplayMatrix());
		mScrollEdge = EDGE_BOTH;
	}

	private void setImageViewMatrix(Matrix matrix) {
		mImageView.setImageMatrix(matrix);

		// Call MatrixChangedListener if needed
		if (null != mMatrixChangeListener) {
			RectF displayRect = getDisplayRect(matrix);
			if (null != displayRect) {
				mMatrixChangeListener.onMatrixChanged(displayRect);
			}
		}
	}

	/**
	 * Calculate Matrix for FIT_CENTER
	 * 
	 * @param d
	 *            - Drawable being displayed
	 */
	private void updateBaseMatrix(Drawable d) {
		if (null == d) {
			return;
		}

		float viewWidth = mImageView.getWidth();
		float viewHeight = mImageView.getHeight();
		int dWidth = d.getIntrinsicWidth();
		int dHeight = d.getIntrinsicHeight();

		mBaseMatrix.reset();

		float widthScale = viewWidth / dWidth;
		float heightScale = viewHeight / dHeight;
		float scale = Math.min(widthScale, heightScale);

		mBaseMatrix.postScale(scale, scale);
		mBaseMatrix.postTranslate((viewWidth - dWidth * scale) / 2F, (viewHeight - dHeight * scale) / 2F);

		resetMatrix();
	}

}
