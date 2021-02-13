package com.github.chrisbanes.photoview;

import android.view.MotionEvent;
import android.view.View;

public interface AdditionalOnTouchListener {

    boolean onTouch (View view, MotionEvent event);

}