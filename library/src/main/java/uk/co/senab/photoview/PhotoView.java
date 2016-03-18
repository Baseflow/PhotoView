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
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.widget.ImageView;

import uk.co.senab.photoview.PhotoViewAttacher.OnMatrixChangedListener;
import uk.co.senab.photoview.PhotoViewAttacher.OnPhotoTapListener;
import uk.co.senab.photoview.PhotoViewAttacher.OnViewTapListener;

public class PhotoView extends ImageView implements IPhotoView {

    public enum CustomScaleType {
        MATRIX,
        FIT_XY,
        FIT_START,
        FIT_CENTER,
        FIT_END,
        CENTER,
        CENTER_CROP,
        CENTER_INSIDE,

        //Custom
        TOP_LEFT_CROP,
        TOP_CENTER_CROP,
        TOP_RIGHT_CROP,
        BOTTOM_LEFT_CROP,
        BOTTOM_CENTER_CROP,
        BOTTOM_RIGHT_CROP,
        CENTER_LEFT_CROP,
        CENTER_RIGHT_CROP,
        MATCH_WIDTH,
        MATCH_HEIGHT;

	    public ScaleType toScaleType() {
		    switch (this) {
			    case FIT_XY:
				    return ScaleType.FIT_XY;
			    case FIT_START:
				    return ScaleType.FIT_START;
			    case FIT_CENTER:
				    return ScaleType.FIT_CENTER;
			    case FIT_END:
				    return ScaleType.FIT_END;
			    case CENTER:
				    return ScaleType.CENTER;
			    case CENTER_CROP:
				    return ScaleType.CENTER_CROP;
			    case CENTER_INSIDE:
				    return ScaleType.CENTER_INSIDE;
			    case MATRIX:
			    default:
				    return ScaleType.MATRIX;
		    }
	    }
    }

    private CustomScaleType customScaleType;
	private boolean initZoomAtMax = false;

    private PhotoViewAttacher mAttacher;

    private CustomScaleType mPendingScaleType;

    public PhotoView(Context context) {
        this(context, null);
    }

    public PhotoView(Context context, AttributeSet attr) {
        this(context, attr, 0);
    }

    public PhotoView(Context context, AttributeSet attr, int defStyle) {
        super(context, attr, defStyle);
        super.setScaleType(ScaleType.MATRIX);
        init(attr);
    }

    protected void init(AttributeSet attr) {
        if (null == mAttacher || null == mAttacher.getImageView()) {
            mAttacher = new PhotoViewAttacher(this);
        }

        if (null != mPendingScaleType) {
	        setCustomScaleType(mPendingScaleType);
            mPendingScaleType = null;
        }

	    if(attr != null)
		    initCustomScaleType(attr);
    }

	private void initCustomScaleType(AttributeSet attr) {
		TypedArray a = getContext().getTheme().obtainStyledAttributes(attr, R.styleable.PhotoView, 0, 0);

		initZoomAtMax = a.getBoolean(R.styleable.PhotoView_initZoomAtMax, false);

		switch (a.getInt(R.styleable.PhotoView_scaleType, 0)) {
			case 100:
				customScaleType = CustomScaleType.TOP_LEFT_CROP;
				break;
			case 101:
				customScaleType = CustomScaleType.TOP_CENTER_CROP;
				break;
			case 102:
				customScaleType = CustomScaleType.TOP_RIGHT_CROP;
				break;
			case 103:
				customScaleType = CustomScaleType.BOTTOM_LEFT_CROP;
				break;
			case 104:
				customScaleType = CustomScaleType.BOTTOM_CENTER_CROP;
				break;
			case 105:
				customScaleType = CustomScaleType.BOTTOM_RIGHT_CROP;
				break;
			case 106:
				customScaleType = CustomScaleType.CENTER_LEFT_CROP;
				break;
			case 107:
				customScaleType = CustomScaleType.CENTER_RIGHT_CROP;
				break;
            case 108:
                customScaleType = CustomScaleType.MATCH_WIDTH;
                break;
            case 109:
                customScaleType = CustomScaleType.MATCH_HEIGHT;
                break;
			default: {
				switch (getScaleType()) {
					case CENTER:
						customScaleType = CustomScaleType.CENTER;
						break;
					case CENTER_CROP:
						customScaleType = CustomScaleType.CENTER_CROP;
						break;
					case CENTER_INSIDE:
						customScaleType = CustomScaleType.CENTER_INSIDE;
						break;
					case FIT_CENTER:
						customScaleType = CustomScaleType.FIT_CENTER;
						break;
					case FIT_END:
						customScaleType = CustomScaleType.FIT_END;
						break;
					case FIT_START:
						customScaleType = CustomScaleType.FIT_START;
						break;
					case FIT_XY:
						customScaleType = CustomScaleType.FIT_XY;
						break;
					case MATRIX:
						customScaleType = CustomScaleType.MATRIX;
						break;
				}
			}
		}

		mAttacher.setCustomScaleType(customScaleType);
	}

	/**
     * @deprecated use {@link #setRotationTo(float)}
     */
    @Override
    public void setPhotoViewRotation(float rotationDegree) {
        mAttacher.setRotationTo(rotationDegree);
    }

    @Override
    public void setRotationTo(float rotationDegree) {
        mAttacher.setRotationTo(rotationDegree);
    }

    @Override
    public void setRotationBy(float rotationDegree) {
        mAttacher.setRotationBy(rotationDegree);
    }

    @Override
    public boolean canZoom() {
        return mAttacher.canZoom();
    }

    @Override
    public RectF getDisplayRect() {
        return mAttacher.getDisplayRect();
    }

    @Override
    public Matrix getDisplayMatrix() {
        return mAttacher.getDisplayMatrix();
    }

    @Override
    public boolean setDisplayMatrix(Matrix finalRectangle) {
        return mAttacher.setDisplayMatrix(finalRectangle);
    }

    @Override
    @Deprecated
    public float getMinScale() {
        return getMinimumScale();
    }

    @Override
    public float getMinimumScale() {
        return mAttacher.getMinimumScale();
    }

    @Override
    @Deprecated
    public float getMidScale() {
        return getMediumScale();
    }

    @Override
    public float getMediumScale() {
        return mAttacher.getMediumScale();
    }

    @Override
    @Deprecated
    public float getMaxScale() {
        return getMaximumScale();
    }

    @Override
    public float getMaximumScale() {
        return mAttacher.getMaximumScale();
    }

    @Override
    public float getScale() {
        return mAttacher.getScale();
    }

    @Override
    public ScaleType getScaleType() {
        return mAttacher.getScaleType();
    }

	public CustomScaleType getCustomScaleType() {
		return customScaleType;
	}

	public boolean initZoomAtMax() {
		return initZoomAtMax;
	}

    @Override
    public void setAllowParentInterceptOnEdge(boolean allow) {
        mAttacher.setAllowParentInterceptOnEdge(allow);
    }

    @Override
    @Deprecated
    public void setMinScale(float minScale) {
        setMinimumScale(minScale);
    }

    @Override
    public void setMinimumScale(float minimumScale) {
        mAttacher.setMinimumScale(minimumScale);
    }

    @Override
    @Deprecated
    public void setMidScale(float midScale) {
        setMediumScale(midScale);
    }

    @Override
    public void setMediumScale(float mediumScale) {
        mAttacher.setMediumScale(mediumScale);
    }

    @Override
    @Deprecated
    public void setMaxScale(float maxScale) {
        setMaximumScale(maxScale);
    }

    @Override
    public void setMaximumScale(float maximumScale) {
        mAttacher.setMaximumScale(maximumScale);
    }

    @Override
    public void setScaleLevels(float minimumScale, float mediumScale, float maximumScale) {
        mAttacher.setScaleLevels(minimumScale, mediumScale, maximumScale);
    }

    @Override
    // setImageBitmap calls through to this method
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        if (null != mAttacher) {
            mAttacher.update();
        }
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        if (null != mAttacher) {
            mAttacher.update();
        }
    }

    @Override
    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
        if (null != mAttacher) {
            mAttacher.update();
        }
    }

    @Override
    public void setOnMatrixChangeListener(OnMatrixChangedListener listener) {
        mAttacher.setOnMatrixChangeListener(listener);
    }

    @Override
    public void setOnLongClickListener(OnLongClickListener l) {
        mAttacher.setOnLongClickListener(l);
    }

    @Override
    public void setOnPhotoTapListener(OnPhotoTapListener listener) {
        mAttacher.setOnPhotoTapListener(listener);
    }

    @Override
    public OnPhotoTapListener getOnPhotoTapListener() {
        return mAttacher.getOnPhotoTapListener();
    }

    @Override
    public void setOnViewTapListener(OnViewTapListener listener) {
        mAttacher.setOnViewTapListener(listener);
    }

    @Override
    public OnViewTapListener getOnViewTapListener() {
        return mAttacher.getOnViewTapListener();
    }

    @Override
    public void setScale(float scale) {
        mAttacher.setScale(scale);
    }

    @Override
    public void setScale(float scale, boolean animate) {
        mAttacher.setScale(scale, animate);
    }

    @Override
    public void setScale(float scale, float focalX, float focalY, boolean animate) {
        mAttacher.setScale(scale, focalX, focalY, animate);
    }

    @Override
    public void setScaleType(ScaleType scaleType) {
        if (null != mAttacher) {
            mAttacher.setScaleType(scaleType);
        } else {
	        switch (scaleType) {
		        case CENTER:
			        mPendingScaleType = CustomScaleType.CENTER;
			        break;
		        case CENTER_CROP:
			        mPendingScaleType = CustomScaleType.CENTER_CROP;
			        break;
		        case CENTER_INSIDE:
			        mPendingScaleType = CustomScaleType.CENTER_INSIDE;
			        break;
		        case FIT_CENTER:
			        mPendingScaleType = CustomScaleType.FIT_CENTER;
			        break;
		        case FIT_END:
			        mPendingScaleType = CustomScaleType.FIT_END;
			        break;
		        case FIT_START:
			        mPendingScaleType = CustomScaleType.FIT_START;
			        break;
		        case FIT_XY:
			        mPendingScaleType = CustomScaleType.FIT_XY;
			        break;
		        case MATRIX:
			        mPendingScaleType = CustomScaleType.MATRIX;
			        break;
	        }
        }
    }

	public void setCustomScaleType(CustomScaleType scaleType) {
		if (null != mAttacher) {
			mAttacher.setCustomScaleType(scaleType);
		} else {
			mPendingScaleType = scaleType;
		}
	}

    @Override
    public void setZoomable(boolean zoomable) {
        mAttacher.setZoomable(zoomable);
    }

    @Override
    public Bitmap getVisibleRectangleBitmap() {
        return mAttacher.getVisibleRectangleBitmap();
    }

    @Override
    public void setZoomTransitionDuration(int milliseconds) {
        mAttacher.setZoomTransitionDuration(milliseconds);
    }

    @Override
    public IPhotoView getIPhotoViewImplementation() {
        return mAttacher;
    }

    @Override
    public void setOnDoubleTapListener(GestureDetector.OnDoubleTapListener newOnDoubleTapListener) {
        mAttacher.setOnDoubleTapListener(newOnDoubleTapListener);
    }

    @Override
    public void setOnScaleChangeListener(PhotoViewAttacher.OnScaleChangeListener onScaleChangeListener) {
        mAttacher.setOnScaleChangeListener(onScaleChangeListener);
    }

    @Override
    protected void onDetachedFromWindow() {
        mAttacher.cleanup();
        super.onDetachedFromWindow();
    }

    @Override
    protected void onAttachedToWindow() {
        init(null);
        super.onAttachedToWindow();
    }
}