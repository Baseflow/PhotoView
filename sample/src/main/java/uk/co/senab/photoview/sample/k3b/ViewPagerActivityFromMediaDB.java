package uk.co.senab.photoview.sample.k3b;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;

import uk.co.senab.photoview.sample.HackyViewPager;
import uk.co.senab.photoview.sample.R;

/** activity demonstrating swipable ViewPager from media-db.
 *
 * Simplified version of de.k3b.android.androFotoFinder.imagedetail.ImagePagerAdapterFromCursor
 * from https://github.com/k3b/APhotoManager/
 *
 * Created by k3b on 13.06.2016.
 */
public class ViewPagerActivityFromMediaDB extends AppCompatActivity {
    public static final String LOG_TAG = "ViewPagerAct-MediaDB";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager);
        ViewPager mViewPager = (HackyViewPager) findViewById(R.id.view_pager);
        setContentView(mViewPager);

        // this is a demo. todo use try catch for error handling. do in seperate non-gui thread
        Cursor cursor = MediaStore.Images.Media.query(getContentResolver(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI,new String[] {}
                , null, null, MediaStore.Images.Media.DATE_TAKEN + " DESC");

        final SampleCursorBasedPagerAdapter adapter = new SampleCursorBasedPagerAdapter(this, cursor);
        mViewPager.setAdapter(adapter);
    }

    static class SampleCursorBasedPagerAdapter extends PagerAdapter {
        private final Activity mActivity;
        private Cursor mCursor = null; // the content of the page
        SampleCursorBasedPagerAdapter(Activity activity, Cursor cursor) {
            mActivity = activity;
            mCursor = cursor;
        }

        /**
         * Implementation for PagerAdapter:
         * Return the number of views available.
         */
        @Override
        public int getCount() {
            int result = 0;
            if (this.mCursor != null) {
                result = this.mCursor.getCount();
            }

            return result;
        }


        /**
         * Implementation for PagerAdapter:
         * Determines whether a page View is associated with a specific key object
         * as returned by {@link #instantiateItem(ViewGroup, int)}. This method is
         * required for a PagerAdapter to function properly.
         *
         * @param view Page View to check for association with <code>object</code>
         * @param object Object to check for association with <code>view</code>
         * @return true if <code>view</code> is associated with the key object <code>object</code>
         */
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            PhotoViewEx photoView = new PhotoViewEx(container.getContext());

            // Now just add PhotoView to ViewPager and return it
            container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            int resolutionKind = MediaStore.Images.Thumbnails.MINI_KIND;


            final BitmapFactory.Options options = new BitmapFactory.Options();
            final ContentResolver contentResolver = photoView.getContext().getContentResolver();

            // this is a demo. todo use try catch for error handling
            this.mCursor.moveToPosition(position);

            long imageID = mCursor.getLong(mCursor.getColumnIndex(MediaStore.Images.Media._ID));
            String imageFilePathFullResolution = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));

            Log.d(LOG_TAG, "loading " + imageID +
                    ":" + imageFilePathFullResolution +
                    "");
            // first display with low resolution which is much faster for swiping
            Bitmap thumbnail = MediaStore.Images.Thumbnails.getThumbnail(
                contentResolver,
                imageID,
                resolutionKind,
                options);

            photoView.setImageBitmap(thumbnail);
            photoView.setMaximumScale(20);
            photoView.setMediumScale(5);

            // this image will be loaded when zooming starts
            photoView.setImageReloadFile(new File(imageFilePathFullResolution));

            return photoView;
        }

        /**
         * Implementation for PagerAdapter:
         * Called to inform the adapter of which item is currently considered to
         * be the "primary", that is the one show to the user as the current page.
         *
         * @param container The containing View from which the page will be removed.
         * @param position The page position that is now the primary.
         * @param object The same object that was returned by
         * {@link #instantiateItem(View, int)}.
         */
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            // this is a demo. todo use try catch for error handling
            this.mCursor.moveToPosition(position);

            String imageFilePathFullResolution = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));
            mActivity.setTitle(imageFilePathFullResolution);
        }


        /**
         * Implementation for PagerAdapter:
         * Remove a page for the given position.  The adapter is responsible
         * for removing the view from its container, although it only must ensure
         * this is done by the time it returns from {@link #finishUpdate(ViewGroup)}.
         *
         * @param container The containing View from which the page will be removed.
         * @param position The page position to be removed.
         * @param object The same object that was returned by
         * {@link #instantiateItem(View, int)}.
         */
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }


    }
}
