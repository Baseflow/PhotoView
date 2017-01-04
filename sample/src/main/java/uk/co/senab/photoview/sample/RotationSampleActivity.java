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
    
    private enum MenuItem {
        RotateRightTo10(0), RotateLeftTo10(1), RotateAutomatic(2), ResetTo0(3), ResetTo90(4), ResetTo180(5), ResetTo270(6);
        private final int value;
        public int value() { return value; }
        MenuItem(int value) {
            this.value = value;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, MenuItem.RotateRightTo10.value(), Menu.NONE, "Rotate 10° Right");
        menu.add(Menu.NONE, MenuItem.RotateLeftTo10.value(), Menu.NONE, "Rotate 10° Left");
        menu.add(Menu.NONE, MenuItem.RotateAutomatic.value(), Menu.NONE, "Toggle automatic rotation");
        menu.add(Menu.NONE, MenuItem.ResetTo0.value(), Menu.NONE, "Reset to 0");
        menu.add(Menu.NONE, MenuItem.ResetTo90.value(), Menu.NONE, "Reset to 90");
        menu.add(Menu.NONE, MenuItem.ResetTo180.value(), Menu.NONE, "Reset to 180");
        menu.add(Menu.NONE, MenuItem.ResetTo270.value(), Menu.NONE, "Reset to 270");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MenuItem.RotateRightTo10.value():
                photo.setRotationBy(10);
                return true;
            case MenuItem.RotateLeftTo10.value():
                photo.setRotationBy(-10);
                return true;
            case MenuItem.RotateAutomatic.value():
                toggleRotation();
                return true;
            case MenuItem.ResetTo0.value():
                photo.setRotationTo(0);
                return true;
            case MenuItem.ResetTo90.value():
                photo.setRotationTo(90);
                return true;
            case MenuItem.ResetTo180.value():
                photo.setRotationTo(180);
                return true;
            case MenuItem.ResetTo270.value():
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
