# PhotoView

Branch **Develop**: [![Build Status](https://travis-ci.org/chrisbanes/PhotoView.png?branch=develop)](https://travis-ci.org/chrisbanes/PhotoView)
Branch **Master**: [![Build Status](https://travis-ci.org/chrisbanes/PhotoView.png?branch=master)](https://travis-ci.org/chrisbanes/PhotoView)

![PhotoView](https://raw.github.com/chrisbanes/PhotoView/master/header_graphic.png)

PhotoView aims to help produce an easily usable implementation of a zooming Android ImageView.

## Features
- Out of the box zooming, using multi-touch and double-tap.
- Scrolling, with smooth scrolling fling.
- Works perfectly when using used in a scrolling parent (such as ViewPager).
- Allows the application to be notified when the displayed Matrix has changed. Useful for when you need to update your UI based on the current zoom/scroll position.
- Allows the application to be notified when the user taps on the Photo.

## Sample Application
The sample application (the source is in the repository) has been published onto Google Play for easy access:

[![Get it on Google Play](http://www.android.com/images/brand/get_it_on_play_logo_small.png)](http://play.google.com/store/apps/details?id=uk.co.senab.photoview.sample)

## Gradle integration

Minimum code for Gradle integration, place code in your `build.gradle`

```gradle
dependencies {
  compile 'com.commit451:PhotoView:1.2.4'
}
```

## Sample Usage
There is a [sample](https://github.com/chrisbanes/PhotoView/tree/master/sample) provided which shows how to use the library in a more advanced way, but for completeness here is all that is required to get PhotoView working:

```java
ImageView mImageView;
PhotoViewAttacher mAttacher;

@Override
public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_main);
	
	// Any implementation of ImageView can be used!
	mImageView = (ImageView) findViewById(R.id.iv_photo);
	
	// Set the Drawable displayed
	Drawable bitmap = getResources().getDrawable(R.drawable.wallpaper);
	mImageView.setImageDrawable(bitmap);

	// Attach a PhotoViewAttacher, which takes care of all of the zooming functionality.
	mAttacher = new PhotoViewAttacher(mImageView);
}


// If you later call mImageView.setImageDrawable/setImageBitmap/setImageResource/etc then you just need to call
mAttacher.update();
```

## Issues With ViewGroups
There are some ViewGroups (ones that utilize onInterceptTouchEvent) that throw exceptions when a PhotoView is placed within them, most notably [ViewPager](http://developer.android.com/reference/android/support/v4/view/ViewPager.html) and [DrawerLayout](https://developer.android.com/reference/android/support/v4/widget/DrawerLayout.html). This is a framework issue that has not been resolved. In order to prevent this exception (which typically occurs when you zoom out), take a look at [HackyDrawerLayout](https://github.com/chrisbanes/PhotoView/blob/master/sample/src/main/java/uk/co/senab/photoview/sample/HackyDrawerLayout.java) and you can see the solution is to simply catch the exception. Any ViewGroup which uses onInterceptTouchEvent will also need to be extended and exceptions caught. Use the [HackyDrawerLayout](https://github.com/chrisbanes/PhotoView/blob/master/sample/src/main/java/uk/co/senab/photoview/sample/HackyDrawerLayout.java) as a template of how to do so. The basic implementation is:
```java
public class HackyProblematicViewGroup extends ProblematicViewGroup {

    public HackyProblematicViewGroup(Context context) {
        super(context);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return false;
        }
    }
}
```

## Pull Requests / Contribution
Development happens in **develop** branch of this repository, and Pull Requests should be filled against that branch.
Any Pull Request against **master** will be rejected


## License

    Copyright 2011, 2012 Chris Banes

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
