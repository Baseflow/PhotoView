package uk.co.senab.photoview.sample;

import android.app.Application;
import android.view.Menu;
import android.view.MenuItem;

import uk.co.senab.photoview.PhotoViewAttacher;
import uk.co.senab.photoview.log.LogManager;

/**
 * Created by k3b on 12.06.2016.
 */
public class PhotoViewSampleApp  extends Application {
    @Override public void onCreate() {
        super.onCreate();

        // enable medium log-debugging
        // lib default is false
        LogManager.setDebugEnabled(true);
        PhotoViewAttacher.DEBUG = true;
    }

    /** Common menu handling for all Activities */
    public static void onPrepareOptionsMenu(Menu menu) {
        MenuItem debug = menu.findItem(R.id.logging_enabled);
        debug.setChecked(LogManager.isDebugEnabled());

        debug = menu.findItem(R.id.logging_detailed_enabled);
        debug.setChecked(PhotoViewAttacher.DEBUG);

    }

    /** Common menu handling for all Activities */
    public static boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logging_enabled: {
                boolean debugEnabled = !LogManager.isDebugEnabled();
                LogManager.setDebugEnabled(debugEnabled);
                if (!debugEnabled) PhotoViewAttacher.DEBUG = false;
                return true;
            }
            case R.id.logging_detailed_enabled: {
                PhotoViewAttacher.DEBUG = !PhotoViewAttacher.DEBUG;
                if (PhotoViewAttacher.DEBUG) LogManager.setDebugEnabled(true);
                return true;
            }
        }
        return false;
    }

}
