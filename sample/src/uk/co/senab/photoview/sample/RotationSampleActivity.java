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

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;

import uk.co.senab.photoview.PhotoView;

public class RotationSampleActivity extends Activity {

    private PhotoView photo;
    private float currentRotation = 0;
    private Handler handler = new Handler();
    private boolean rotating = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        photo = new PhotoView(this);
        photo.setImageResource(R.drawable.wallpaper);
        setContentView(photo);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, 0, Menu.NONE, "Rotate 10° Right");
        menu.add(Menu.NONE, 1, Menu.NONE, "Rotate 10° Left");
        menu.add(Menu.NONE, 2, Menu.NONE, "Toggle automatic rotation");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                currentRotation += 10;
                photo.setPhotoViewRotation(currentRotation);
                return true;
            case 1:
                currentRotation -= 10;
                photo.setPhotoViewRotation(currentRotation);
                return true;
            case 2:
                toggleRotation();
                return true;
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
                currentRotation += 10;
                photo.setPhotoViewRotation(currentRotation);
                rotateLoop();
            }
        }, 1000);
    }

}
