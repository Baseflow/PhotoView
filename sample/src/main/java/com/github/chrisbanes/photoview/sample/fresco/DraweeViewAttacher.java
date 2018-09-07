package com.github.chrisbanes.photoview.sample.fresco;

import android.graphics.Matrix;
import android.graphics.RectF;

import com.facebook.drawee.view.GenericDraweeView;
import com.github.chrisbanes.photoview.PhotoViewAttacher;

/*******************************************************************************
 * Description: Extend PhotoViewAttacher to support Fresco.
 *
 * Author: Freeman
 *
 * Date: 2018/9/7
 *******************************************************************************/
public class DraweeViewAttacher extends PhotoViewAttacher {

	private GenericDraweeView imageView;

	public DraweeViewAttacher(GenericDraweeView image) {
		super(image);
		this.imageView = image;
	}

	@Override
	protected RectF getDisplayRect(Matrix matrix) {
		RectF displayRect = new RectF();
		imageView.getHierarchy().getActualImageBounds(displayRect);
		matrix.mapRect(displayRect);
		return displayRect;
	}
}
