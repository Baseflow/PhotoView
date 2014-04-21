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
        menu.add(Menu.NONE, 0, Menu.NONE, "Rotate 10° Right");
        menu.add(Menu.NONE, 1, Menu.NONE, "Rotate 10° Left");
        menu.add(Menu.NONE, 2, Menu.NONE, "Toggle automatic rotation");
        menu.add(Menu.NONE, 3, Menu.NONE, "Reset to 0");
        menu.add(Menu.NONE, 4, Menu.NONE, "Reset to 90");
        menu.add(Menu.NONE, 5, Menu.NONE, "Reset to 180");
        menu.add(Menu.NONE, 6, Menu.NONE, "Reset to 270");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                photo.setRotationBy(10);
                return true;
            case 1:
                photo.setRotationBy(-10);
                return true;
            case 2:
                toggleRotation();
                return true;
            case 3:
                photo.setRotationTo(0);
                return true;
            case 4:
                photo.setRotationTo(90);
                return true;
            case 5:
                photo.setRotationTo(180);
                return true;
            case 6:
                photo.setRotationTo(270);
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
                photo.setRotationBy(1);
                rotateLoop();
            }
        }, 15);
    }

}
