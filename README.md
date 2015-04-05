# PhotoView
***

## Deprecation notice

Library has currently no active maintainers, if you're interested, contact Chris Banes (repository owner)

If you have project built on top of this library, you can still submit pull-requests with fixes, for others to see, but you shall expect no response from former maintainers or Chris himself

Version **1.2.4** is the last version officially released (as of 2nd April 2015)

***
Branch **Dev**: [![Build Status](https://travis-ci.org/chrisbanes/PhotoView.png?branch=dev)](https://travis-ci.org/chrisbanes/PhotoView)  
Branch **Master**: [![Build Status](https://travis-ci.org/chrisbanes/PhotoView.png?branch=master)](https://travis-ci.org/chrisbanes/PhotoView)

![PhotoView](https://raw.github.com/chrisbanes/PhotoView/master/header_graphic.png)

PhotoView aims to help produce an easily usable implementation of a zooming Android ImageView. It is currently being used in [photup](https://play.google.com/store/apps/details?id=uk.co.senab.photup).

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
repositories {
  mavenCentral()
}

dependencies {
  compile 'com.github.chrisbanes.photoview:library:1.2.4'
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

## Pull Requests / Contribution
Development happens in **dev** branch of this repository, and Pull Requests should be filled against that branch.
Any Pull Request against **master** will be rejected

## Changelog
Please see the new [Changelog](https://github.com/chrisbanes/PhotoView/wiki/Changelog) page to see what's recently changed.

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
