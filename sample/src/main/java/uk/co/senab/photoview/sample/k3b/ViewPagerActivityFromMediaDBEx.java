package uk.co.senab.photoview.sample.k3b;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;

import uk.co.senab.photoview.sample.ViewPagerActivityFromMediaDB;

/**
 * This is nearly the same as {@link uk.co.senab.photoview.sample.ViewPagerActivityFromMediaDB}
 * that uses a customized {@link PhotoViewEx} to load higher quality
 * images on demand.
 *
 * Created by k3b on 15.07.2016.
 */
public class ViewPagerActivityFromMediaDBEx extends ViewPagerActivityFromMediaDB {
    /** This should code should normally be part of the adapter.
     * {@link uk.co.senab.photoview.sample.ViewPagerActivityFromMediaDB.SampleCursorBasedPagerAdapter}.
     * It has been moved to the activity so it can be modified  through Activity inheritance.
     *
     * This code demonstrates how to use a customized {@link PhotoViewEx} to load higher quality
     * images on demand. */
    @NonNull
    @Override
    protected PhotoViewEx createAndLoadPhotoView(long imageID, String imageFilePathFullResolution) {
        int resolutionKind = MediaStore.Images.Thumbnails.MINI_KIND;

        final BitmapFactory.Options options = new BitmapFactory.Options();
        final ContentResolver contentResolver = this.getContentResolver();
        // first display with low resolution which is much faster for swiping
        Bitmap thumbnail = MediaStore.Images.Thumbnails.getThumbnail(
                contentResolver,
                imageID,
                resolutionKind,
                options);

        PhotoViewEx photoView = new PhotoViewEx(this);

        // Now just add PhotoView to ViewPager and return it
        photoView.setImageBitmap(thumbnail);

        Log.d(LOG_TAG, "loaded thumbnail of " + imageID +
                " for " + imageFilePathFullResolution +
                " into " + photoView);

        // this image will be loaded when zooming starts
        photoView.setImageReloadFile(new File(imageFilePathFullResolution));
        return photoView;
    }
}
