package uk.co.senab.photoview.sample;

import android.app.Application;

import uk.co.senab.photoview.log.LogManager;

/**
 * Created by k3b on 12.06.2016.
 */
public class PhotoViewSampleApp  extends Application {
    @Override public void onCreate() {
        super.onCreate();

        // enable full log-debugging
        // lib default is false
        LogManager.setDebugEnabled(true);
    }
}
