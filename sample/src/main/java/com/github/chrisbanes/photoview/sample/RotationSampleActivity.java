/*
 Copyright 2011, 2012 Chris Banes.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package com.github.chrisbanes.photoview.sample;

import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;

import com.github.chrisbanes.photoview.PhotoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class RotationSampleActivity extends AppCompatActivity {

    private PhotoView photo;
    private final Handler handler = new Handler();
    private boolean rotating = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rotation_sample);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.rotation);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_rotate_10_right:
                        photo.setRotationBy(10);
                        return true;
                    case R.id.action_rotate_10_left:
                        photo.setRotationBy(-10);
                        return true;
                    case R.id.action_toggle_automatic_rotation:
                        toggleRotation();
                        return true;
                    case R.id.action_reset_to_0:
                        photo.setRotationTo(0);
                        return true;
                    case R.id.action_reset_to_90:
                        photo.setRotationTo(90);
                        return true;
                    case R.id.action_reset_to_180:
                        photo.setRotationTo(180);
                        return true;
                    case R.id.action_reset_to_270:
                        photo.setRotationTo(270);
                        return true;
                }
                return false;
            }
        });
        photo = findViewById(R.id.iv_photo);
        photo.setImageResource(R.drawable.wallpaper);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacksAndMessages(null);
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
