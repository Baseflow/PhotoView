package uk.co.senab.photoview.sample;

import uk.co.senab.photoview.PhotoView;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

public class MainActivity extends Activity {
	
	private PhotoView mPhotoView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mPhotoView = (PhotoView) findViewById(R.id.pv_photo);
        
        Drawable bitmap = getResources().getDrawable(R.drawable.wallpaper);
        mPhotoView.setImageDrawable(bitmap);
        mPhotoView.setZoomable(true);
    }

    

    
}
