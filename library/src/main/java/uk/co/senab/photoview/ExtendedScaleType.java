package uk.co.senab.photoview;

import android.widget.ImageView;

public enum ExtendedScaleType {
    TOP_CENTER,
    TOP_CENTER_CROP,
    TOP_CENTER_INSIDE,
    CENTER,
    CENTER_CROP,
    CENTER_INSIDE,
    FIT_CENTER,
    FIT_END,
    FIT_START,
    FIT_XY;

    public ImageView.ScaleType toImageScaleType() {
        switch (this) {
            case TOP_CENTER: return ImageView.ScaleType.MATRIX;
            case TOP_CENTER_CROP: return ImageView.ScaleType.MATRIX;
            case TOP_CENTER_INSIDE: return ImageView.ScaleType.MATRIX;
            case CENTER: return ImageView.ScaleType.CENTER;
            case CENTER_CROP: return ImageView.ScaleType.CENTER_CROP;
            case CENTER_INSIDE: return ImageView.ScaleType.CENTER_INSIDE;
            case FIT_CENTER: return ImageView.ScaleType.FIT_CENTER;
            case FIT_END: return ImageView.ScaleType.FIT_END;
            case FIT_START: return ImageView.ScaleType.FIT_START;
            case FIT_XY: return ImageView.ScaleType.FIT_XY;
            default: return ImageView.ScaleType.MATRIX;
        }
    }

    public static ExtendedScaleType fromImageScaleType(ImageView.ScaleType scaleType) {
        switch (scaleType) {
            case CENTER: return CENTER;
            case CENTER_CROP: return CENTER_CROP;
            case CENTER_INSIDE: return CENTER_INSIDE;
            case FIT_CENTER: return FIT_CENTER;
            case FIT_END: return FIT_END;
            case FIT_START: return FIT_START;
            case FIT_XY: return FIT_XY;
            default: throw new IllegalArgumentException(scaleType.name()
                    + " is not supported in PhotoView");
        }
    }
}
