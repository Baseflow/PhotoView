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
package com.github.chrisbanes.photoview.sample;

import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import android.widget.Toast;

import com.github.chrisbanes.photoview.OnMatrixChangedListener;
import com.github.chrisbanes.photoview.OnPhotoTapListener;
import com.github.chrisbanes.photoview.OnSingleFlingListener;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.Random;


public class SimpleSampleActivity extends AppCompatActivity {

    static final String PHOTO_TAP_TOAST_STRING = "Photo Tap! X: %.2f %% Y:%.2f %% ID: %d";
    static final String SCALE_TOAST_STRING = "Scaled to: %.2ff";
    static final String FLING_LOG_STRING = "Fling velocityX: %.2f, velocityY: %.2f";

    private PhotoView mPhotoView;
    private TextView mCurrMatrixTv;

    private Toast mCurrentToast;

    private Matrix mCurrentDisplayMatrix = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPhotoView = (PhotoView) findViewById(R.id.iv_photo);
        mCurrMatrixTv = (TextView) findViewById(R.id.tv_current_matrix);

        Drawable bitmap = ContextCompat.getDrawable(this, R.drawable.wallpaper);
        mPhotoView.setImageDrawable(bitmap);

        // Lets attach some listeners, not required though!
        mPhotoView.setOnMatrixChangeListener(new MatrixChangeListener());
        mPhotoView.setOnPhotoTapListener(new PhotoTapListener());
        mPhotoView.setOnSingleFlingListener(new SingleFlingListener());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem zoomToggle = menu.findItem(R.id.menu_zoom_toggle);
        zoomToggle.setTitle(mPhotoView.isZoomEnabled() ? R.string.menu_zoom_disable : R.string.menu_zoom_enable);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_zoom_toggle:
                mPhotoView.setZoomable(!mPhotoView.isZoomEnabled());
                return true;

            case R.id.menu_scale_fit_center:
                mPhotoView.setScaleType(ScaleType.FIT_CENTER);
                return true;

            case R.id.menu_scale_fit_start:
                mPhotoView.setScaleType(ScaleType.FIT_START);
                return true;

            case R.id.menu_scale_fit_end:
                mPhotoView.setScaleType(ScaleType.FIT_END);
                return true;

            case R.id.menu_scale_fit_xy:
                mPhotoView.setScaleType(ScaleType.FIT_XY);
                return true;

            case R.id.menu_scale_scale_center:
                mPhotoView.setScaleType(ScaleType.CENTER);
                return true;

            case R.id.menu_scale_scale_center_crop:
                mPhotoView.setScaleType(ScaleType.CENTER_CROP);
                return true;

            case R.id.menu_scale_scale_center_inside:
                mPhotoView.setScaleType(ScaleType.CENTER_INSIDE);
                return true;

            case R.id.menu_scale_random_animate:
            case R.id.menu_scale_random:
                Random r = new Random();

                float minScale = mPhotoView.getMinimumScale();
                float maxScale = mPhotoView.getMaximumScale();
                float randomScale = minScale + (r.nextFloat() * (maxScale - minScale));
                mPhotoView.setScale(randomScale, item.getItemId() == R.id.menu_scale_random_animate);

                showToast(String.format(SCALE_TOAST_STRING, randomScale));

                return true;
            case R.id.menu_matrix_restore:
                if (mCurrentDisplayMatrix == null)
                    showToast("You need to capture display matrix first");
                else
                    mPhotoView.setDisplayMatrix(mCurrentDisplayMatrix);
                return true;
            case R.id.menu_matrix_capture:
                mCurrentDisplayMatrix = new Matrix();
                mPhotoView.getDisplayMatrix(mCurrentDisplayMatrix);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class PhotoTapListener implements OnPhotoTapListener {

        @Override
        public void onPhotoTap(ImageView view, float x, float y) {
            float xPercentage = x * 100f;
            float yPercentage = y * 100f;

            showToast(String.format(PHOTO_TAP_TOAST_STRING, xPercentage, yPercentage, view == null ? 0 : view.getId()));
        }
    }

    private void showToast(CharSequence text) {
        if (mCurrentToast != null) {
            mCurrentToast.cancel();
        }

        mCurrentToast = Toast.makeText(SimpleSampleActivity.this, text, Toast.LENGTH_SHORT);
        mCurrentToast.show();
    }

    private class MatrixChangeListener implements OnMatrixChangedListener {

        @Override
        public void onMatrixChanged(RectF rect) {
            mCurrMatrixTv.setText(rect.toString());
        }
    }

    private class SingleFlingListener implements OnSingleFlingListener {

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.d("PhotoView", String.format(FLING_LOG_STRING, velocityX, velocityY));
            return true;
        }
    }
}
