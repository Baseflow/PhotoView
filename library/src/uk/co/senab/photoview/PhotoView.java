package uk.co.senab.photoview;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class PhotoView extends ImageView implements
		VersionedGestureDetector.OnGestureListener,
		GestureDetector.OnDoubleTapListener {

	static final String LOG_TAG = "PhotoView";

	static interface OnMatrixChangedListener {
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

	static interface OnPhotoTapListener {

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

	private class AnimatedZoomRunnable implements Runnable {

		static final long ANIMATION_DURATION = 200;

		private final float mFocalX, mFocalY;
		private final float mIncrementPerMs;
		private final float mStartScale;
		private final long mStartTime;

		public AnimatedZoomRunnable(final float zoomLevel, final float focalX,
				final float focalY) {
			mStartScale = getScale();
			mStartTime = System.currentTimeMillis();
			mIncrementPerMs = (zoomLevel - mStartScale) / ANIMATION_DURATION;
			mFocalX = focalX;
			mFocalY = focalY;
		}

		public void run() {
			float currentMs = Math.min(ANIMATION_DURATION,
					System.currentTimeMillis() - mStartTime);
			float target = mStartScale + (mIncrementPerMs * currentMs);

			mSuppMatrix.setScale(target, target, mFocalX, mFocalY);
			centerAndDisplayMatrix();

			if (currentMs < ANIMATION_DURATION) {
				if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
					SDK16.postOnAnimation(PhotoView.this, this);
				} else {
					postDelayed(this, 10);
				}
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
	private VersionedGestureDetector mScaleDetector;

	private final Matrix mBaseMatrix = new Matrix();
	private final Matrix mDrawMatrix = new Matrix();
	private final Matrix mSuppMatrix = new Matrix();

	// Listeners
	private OnMatrixChangedListener mMatrixChangeListener;
	private OnPhotoTapListener mPhotoTapListener;

	// Saves us allocating a new float[] when getValue is called
	private final float[] mMatrixValues = new float[9];

	private int mScrollEdge = EDGE_BOTH;
	private boolean mZoomEnabled = false;

	public PhotoView(Context context) {
		super(context);
		init(context);
	}

	public PhotoView(Context context, AttributeSet attr) {
		super(context, attr);
		init(context);
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

	public boolean onDoubleTap(MotionEvent ev) {
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

	public boolean onDoubleTapEvent(MotionEvent e) {
		// Wait for the confirmed onDoubleTap() instead
		return false;
	}

	public void onDrag(float dx, float dy) {
		mSuppMatrix.postTranslate(dx, dy);
		centerAndDisplayMatrix();
	}

	public void onScale(float scaleFactor, float focusX, float focusY) {
		mSuppMatrix.postScale(scaleFactor, scaleFactor, focusX, focusY);
		centerAndDisplayMatrix();
	}

	public boolean onSingleTapConfirmed(MotionEvent e) {
		if (null != mPhotoTapListener) {
			final RectF displayRect = getDisplayRect();

			if (null != displayRect) {
				final float x = e.getX(), y = e.getY();

				// Check to see if the user tapped on the photo
				if (displayRect.contains(x, y)) {

					float xResult = (x - displayRect.left)
							/ displayRect.width();
					float yResult = (y - displayRect.top)
							/ displayRect.height();

					mPhotoTapListener.onPhotoTap(this, xResult, yResult);
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (mZoomEnabled) {

			getParent().requestDisallowInterceptTouchEvent(true);

			if (null != mScaleDetector && mScaleDetector.onTouchEvent(ev)) {

				// If we're on an edge, then let the parent intercept. Useful
				// for ViewPagers
				if (mScrollEdge != EDGE_NONE) {
					getParent().requestDisallowInterceptTouchEvent(false);
				}

				switch (ev.getAction()) {
				case MotionEvent.ACTION_CANCEL:
				case MotionEvent.ACTION_UP:
					// If the user has zoomed less than MIN_ZOOM, zoom back to
					// 1.0f
					if (getScale() < MIN_ZOOM) {
						post(new AnimatedZoomRunnable(MIN_ZOOM, 0f, 0f));
					}
					break;
				}

				return true;
			}

			// Check to see if the user double tapped
			if (null != mGestureDetector && mGestureDetector.onTouchEvent(ev)) {
				return true;
			}

			getParent().requestDisallowInterceptTouchEvent(false);
		}

		return super.onTouchEvent(ev);
	}

	@Override
	public void setImageDrawable(Drawable drawable) {
		super.setImageDrawable(drawable);
		if (mZoomEnabled) {
			updateBaseMatrix(drawable);
		}
	}

	@Override
	public void setImageMatrix(Matrix matrix) {
		super.setImageMatrix(matrix);

		// Call MatrixChangedListener if needed
		if (null != mMatrixChangeListener) {
			RectF displayRect = getDisplayRect(matrix);
			if (null != displayRect) {
				mMatrixChangeListener.onMatrixChanged(displayRect);
			}
		}
	}

	public void setMatrixChangeListener(OnMatrixChangedListener listener) {
		mMatrixChangeListener = listener;
	}

	public void setPhotoTapListener(OnPhotoTapListener listener) {
		mPhotoTapListener = listener;
	}

	public void setZoomable(boolean zoomable) {
		mZoomEnabled = zoomable;
		if (mZoomEnabled) {
			// Make sure we using MATRIX Scale Type
			setScaleType(ScaleType.MATRIX);

			// Update the base matrix using the current drawable
			updateBaseMatrix(getDrawable());
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
		post(new AnimatedZoomRunnable(scale, focalX, focalY));
	}
	
	protected Matrix getDisplayMatrix() {
		mDrawMatrix.set(mBaseMatrix);
		mDrawMatrix.postConcat(mSuppMatrix);
		return mDrawMatrix;
	}

	@Override
	protected boolean setFrame(int l, int t, int r, int b) {
		if (mZoomEnabled && super.setFrame(l, t, r, b)) {
			updateBaseMatrix(getDrawable());
			return true;
		}
		return false;
	}

	/**
	 * Helper method that simply centers the Matrix, and then displays the
	 * result
	 */
	private void centerAndDisplayMatrix() {
		checkMatrixBounds();
		setImageMatrix(getDisplayMatrix());
	}

	private void checkMatrixBounds() {
		Drawable d = getDrawable();
		if (null == d) {
			return;
		}

		RectF rect = new RectF(0, 0, d.getIntrinsicWidth(),
				d.getIntrinsicHeight());
		getDisplayMatrix().mapRect(rect);

		final float height = rect.height(), width = rect.width();
		float deltaX = 0, deltaY = 0;

		final int viewHeight = getHeight();
		if (height < viewHeight) {
			deltaY = (viewHeight - height) / 2 - rect.top;
		} else if (rect.top > 0) {
			deltaY = -rect.top;
		} else if (rect.bottom < viewHeight) {
			deltaY = viewHeight - rect.bottom;
		}

		final int viewWidth = getWidth();
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
		Drawable d = getDrawable();
		if (null != d) {
			RectF rect = new RectF(0, 0, d.getIntrinsicWidth(),
					d.getIntrinsicHeight());
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

	private void init(Context context) {
		mScaleDetector = VersionedGestureDetector.newInstance(context, this);

		mGestureDetector = new GestureDetector(context,
				new GestureDetector.SimpleOnGestureListener());
		mGestureDetector.setOnDoubleTapListener(this);
	}

	/**
	 * Resets the Matrix back to FIT_CENTER, and then displays it.s
	 */
	private void resetMatrix() {
		mSuppMatrix.reset();
		setImageMatrix(getDisplayMatrix());
		mScrollEdge = EDGE_BOTH;
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

		float viewWidth = getWidth();
		float viewHeight = getHeight();
		int dWidth = d.getIntrinsicWidth();
		int dHeight = d.getIntrinsicHeight();

		mBaseMatrix.reset();

		float widthScale = viewWidth / dWidth;
		float heightScale = viewHeight / dHeight;
		float scale = Math.min(widthScale, heightScale);

		mBaseMatrix.postScale(scale, scale);
		mBaseMatrix.postTranslate((viewWidth - dWidth * scale) / 2F,
				(viewHeight - dHeight * scale) / 2F);

		resetMatrix();
	}
}
