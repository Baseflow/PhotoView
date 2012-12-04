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
package uk.co.senab.photoview.sample;

import uk.co.senab.photoview.PhotoViewAttacher;
import uk.co.senab.photoview.PhotoViewAttacher.OnMatrixChangedListener;
import uk.co.senab.photoview.PhotoViewAttacher.OnPhotoTapListener;
import android.app.Activity;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import android.widget.Toast;

public class SimpleSampleActivity extends Activity {

	static final String PHOTO_TAP_TOAST_STRING = "Photo Tap! X: %.2f %% Y:%.2f %%";

	private ImageView mImageView;
	private TextView mCurrMatrixTv;

	private PhotoViewAttacher mAttacher;

	private Toast mCurrentToast;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mImageView = (ImageView) findViewById(R.id.iv_photo);
		mCurrMatrixTv = (TextView) findViewById(R.id.tv_current_matrix);

		Drawable bitmap = getResources().getDrawable(R.drawable.wallpaper);
		mImageView.setImageDrawable(bitmap);

		// The MAGIC happens here!
		mAttacher = new PhotoViewAttacher(mImageView);

		// Lets attach some listeners, not required though!
		mAttacher.setOnMatrixChangeListener(new MatrixChangeListener());
		mAttacher.setOnPhotoTapListener(new PhotoTapListener());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		// Need to call clean-up
		mAttacher.cleanup();
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem zoomToggle = menu.findItem(R.id.menu_zoom_toggle);
		zoomToggle.setTitle(mAttacher.canZoom() ? R.string.menu_zoom_disable : R.string.menu_zoom_enable);

		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_zoom_toggle:
				mAttacher.setZoomable(!mAttacher.canZoom());
				return true;

			case R.id.menu_scale_fit_center:
				mAttacher.setScaleType(ScaleType.FIT_CENTER);
				return true;

			case R.id.menu_scale_fit_start:
				mAttacher.setScaleType(ScaleType.FIT_START);
				return true;

			case R.id.menu_scale_fit_end:
				mAttacher.setScaleType(ScaleType.FIT_END);
				return true;

			case R.id.menu_scale_fit_xy:
				mAttacher.setScaleType(ScaleType.FIT_XY);
				return true;

			case R.id.menu_scale_scale_center:
				mAttacher.setScaleType(ScaleType.CENTER);
				return true;

			case R.id.menu_scale_scale_center_crop:
				mAttacher.setScaleType(ScaleType.CENTER_CROP);
				return true;

			case R.id.menu_scale_scale_center_inside:
				mAttacher.setScaleType(ScaleType.CENTER_INSIDE);
				return true;
		}

		return super.onOptionsItemSelected(item);
	}

	private class PhotoTapListener implements OnPhotoTapListener {

		@Override
		public void onPhotoTap(View view, float x, float y) {
			float xPercentage = x * 100f;
			float yPercentage = y * 100f;

			if (null != mCurrentToast) {
				mCurrentToast.cancel();
			}

			mCurrentToast = Toast.makeText(SimpleSampleActivity.this,
					String.format(PHOTO_TAP_TOAST_STRING, xPercentage, yPercentage), Toast.LENGTH_SHORT);
			mCurrentToast.show();
		}
	}

	private class MatrixChangeListener implements OnMatrixChangedListener {

		@Override
		public void onMatrixChanged(RectF rect) {
			mCurrMatrixTv.setText(rect.toString());
		}
	}

}
