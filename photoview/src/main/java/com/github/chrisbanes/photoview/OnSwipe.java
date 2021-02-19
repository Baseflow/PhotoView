package com.github.chrisbanes.photoview;

import android.view.MotionEvent;
import android.view.View;

public class OnSwipe {
    public void swipe(View v, MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // Save Y coordinate position in order to drag the image
                v.setTag(ev.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                // Make the image follow the finger
                v.setTranslationY(ev.getRawY() - (Float)v.getTag());
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
    }
}
