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

import uk.co.senab.photoview.PhotoViewAttacher.OnMatrixChangedListener;
import uk.co.senab.photoview.PhotoViewAttacher.OnPhotoTapListener;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

@TargetApi(9)
public class PhotoView extends ImageView {

	private final PhotoViewAttacher mAttacher;

	public PhotoView(Context context) {
		super(context);
		mAttacher = new PhotoViewAttacher(this);
	}

	public PhotoView(Context context, AttributeSet attr) {
		super(context, attr);
		mAttacher = new PhotoViewAttacher(this);
	}

	/**
	 * Returns true if the PhotoView is set to allow zooming of Photos.
	 * 
	 * @return true if the PhotoView allows zooming.
	 */
	public boolean canZoom() {
		return mAttacher.canZoom();
	}

	/**
	 * Gets the Display Rectangle of the currently displayed Drawable. The
	 * Rectangle is relative to this View and includes all scaling and
	 * translations.
	 * 
	 * @return - RectF of Displayed Drawable
	 */
	public RectF getDisplayRect() {
		return mAttacher.getDisplayRect();
	}

	/**
	 * Returns the current scale value
	 * 
	 * @return float - current scale value
	 */
	public float getScale() {
		return mAttacher.getScale();
	}

	@Override
	public void setImageDrawable(Drawable drawable) {
		super.setImageDrawable(drawable);
		mAttacher.update();
	}

	/**
	 * Register a callback to be invoked when the Matrix has changed for this
	 * View. An example would be the user panning or scaling the Photo.
	 * 
	 * @param listener
	 *            - Listener to be registered.
	 */
	public void setOnMatrixChangeListener(OnMatrixChangedListener listener) {
		mAttacher.setOnMatrixChangeListener(listener);
	}

	/**
	 * Register a callback to be invoked when the Photo displayed by this View
	 * is tapped with a single tap.
	 * 
	 * @param listener
	 *            - Listener to be registered.
	 */
	public void setOnPhotoTapListener(OnPhotoTapListener listener) {
		mAttacher.setOnPhotoTapListener(listener);
	}

	/**
	 * Allows you to enable/disable the zoom functionality on the ImageView.
	 * When disable the ImageView reverts to using the FIT_CENTER matrix.
	 * 
	 * @param zoomable
	 *            - Whether the zoom functionality is enabled.
	 */
	public void setZoomable(boolean zoomable) {
		mAttacher.setZoomable(zoomable);
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
		mAttacher.zoomTo(scale, focalX, focalY);
	}

}