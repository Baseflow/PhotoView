package uk.co.senab.photoview.sample.k3b;

import android.content.Context;
import android.os.Build;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;
import uk.co.senab.photoview.log.LogManager;

/**
 * Enhanvced PhotoView with support for
 * - huge images
 * - fast load reduced image that will be replaced by full-size image on first zoom
 *
 * Simplified version of code used in de.k3b.android.androFotoFinder.imagedetail.ImagePagerAdapterFromCursor
 * from https://github.com/k3b/APhotoManager/
 *
 * Created by k3b on 14.07.2016.
 */
public class PhotoViewEx extends PhotoView {
    private PhotoViewAttacherEx mAttacher;

    public PhotoViewEx(Context context) {
        super(context);
    }

    /** Required to have my own enhanced attacher that contains the additional functionality */
    protected IPhotoViewAttacher onCreatePhotoViewAttacher(PhotoView photoView) {
        mAttacher = new PhotoViewAttacherEx(photoView);
        return mAttacher;
    }

    /** k3b 20150913 #10: Faster initial loading: initially the view is loaded with low res image.
     * on first zoom it is reloaded with this uri */
    public void setImageReloadFile(File file) {
        mAttacher.setImageReloadFile(file);
    }

    static class PhotoViewAttacherEx extends PhotoViewAttacher {
        /** k3b 20150913 #10: Faster initial loading: initially the view is loaded with low res image.
         * on first zoom it is reloaded with this uri */
        private File mImageReloadFile = null;

        /** my android 4.4 cannot process images bigger than 4096*4096. -1 means must be calculated from openGL  */
        private static int MAX_IMAGE_DIMENSION = -1; // will be set to 4096

        public PhotoViewAttacherEx(PhotoView photoView) {
            super(photoView);
        }
        /** k3b 20150913 #10: Faster initial loading: initially the view is loaded with low res image.
         * on first zoom it is reloaded with this uri
         * @param imageReloadURI*/
        public void setImageReloadFile(File imageReloadURI) {
            this.mImageReloadFile = imageReloadURI;
        }

        /** invoked by the guesture detector */
        @Override
        public void onScale(float scaleFactor, float focusX, float focusY) {
            /** k3b 20150913 #10: Faster initial loading: initially the view is loaded with low res image.
             * On first zoom it is reloaded with mImageReloadFile */
            if (mImageReloadFile != null) {
                ImageView imageView = getImageView();
                if (imageView != null) {
                    if (DEBUG) {
                        // !!!
                        LogManager.getLogger().d(
                                LOG_TAG,
                                "onScale: Reloading image from " + mImageReloadFile);
                    }
                    try {
                        if (MAX_IMAGE_DIMENSION < 0) {
                            MAX_IMAGE_DIMENSION = (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) ? 4096 : HugeImageLoader.getMaxTextureSize();
                        }
                        imageView.setImageBitmap(HugeImageLoader.loadImage(mImageReloadFile.getAbsoluteFile(), MAX_IMAGE_DIMENSION, MAX_IMAGE_DIMENSION));
                    } catch (OutOfMemoryError e) {
                        LogManager.getLogger().e(
                                LOG_TAG,
                                "onScale: Not enought memory to reloading image from " + mImageReloadFile + " failed: " + e.getMessage());
                    }

                    mImageReloadFile = null; // either success or error: do not try it again
                }
            }

            super.onScale(scaleFactor,focusX,focusY);
        }
    }
}
