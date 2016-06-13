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

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.log.LogManager;

public class RotationSampleActivity extends AppCompatActivity {

    private PhotoView photo;
    private final Handler handler = new Handler();
    private boolean rotating = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        photo = new PhotoView(this);
        photo.setImageResource(R.drawable.wallpaper);
        setContentView(photo);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.rotate_menue, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem debug = menu.findItem(R.id.logging_enabled);
        debug.setChecked(LogManager.isDebugEnabled());

        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_Rotate_10_Right:
                photo.setRotationBy(10);
                return true;
            case R.id.menu_Rotate_10_Left:
                photo.setRotationBy(-10);
                return true;
            case R.id.menu_Toggle_automatic_rotation:
                toggleRotation();
                return true;
            case R.id.menu_Reset_to_0:
                photo.setRotationTo(0);
                return true;
            case R.id.menu_Reset_to_90:
                photo.setRotationTo(90);
                return true;
            case R.id.menu_Reset_to_180:
                photo.setRotationTo(180);
                return true;
            case R.id.menu_Reset_to_270:
                photo.setRotationTo(270);
                return true;
            case R.id.logging_enabled: {
                LogManager.setDebugEnabled(!LogManager.isDebugEnabled());
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void toggleRotation() {
        if (rotating) {
            handler.removeCallbacksAndMessages(null);
        } else {
            rotateLoop();
        }
        rotating = !rotating;
    }

    private void rotateLoop() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                photo.setRotationBy(1);
                rotateLoop();
            }
        }, 15);
    }

}
