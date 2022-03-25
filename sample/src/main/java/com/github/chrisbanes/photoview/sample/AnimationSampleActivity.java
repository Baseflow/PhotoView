package com.github.chrisbanes.photoview.sample;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.github.chrisbanes.photoview.PhotoView;

import pl.droidsonroids.gif.GifDrawable;

public class AnimationSampleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation_sample);
        PhotoView pvGif = findViewById(R.id.pvGif);
        findViewById(R.id.btnSwitchPic).setOnClickListener(v -> {
            if (pvGif.getTag().equals("pic_dog")) {
                pvGif.setImageResource(R.mipmap.pic_cat);
                pvGif.setTag("pic_cat");
            } else {
                pvGif.setImageResource(R.mipmap.pic_dog);
                pvGif.setTag("pic_dog");
            }
        });
        findViewById(R.id.btnHalfSpeed).setOnClickListener(v -> ((GifDrawable) pvGif.getDrawable()).setSpeed(0.5f));
        findViewById(R.id.btnNormalSpeed).setOnClickListener(v -> ((GifDrawable) pvGif.getDrawable()).setSpeed(1));
        findViewById(R.id.btnDoubleSpeed).setOnClickListener(v -> ((GifDrawable) pvGif.getDrawable()).setSpeed(2));
    }
}