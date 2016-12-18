package uk.co.senab.photoview;

import android.util.Log;

/**
 * A bit smaller than a log
 */
class Stick {

    public static void log(String message) {
        if (BuildConfig.DEBUG) {
            Log.d("PhotoView", message);
        }
    }
}
