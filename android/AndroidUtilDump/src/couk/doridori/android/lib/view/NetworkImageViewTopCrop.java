package com.doridori.android.lib.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.AttributeSet;

import com.android.volley.toolbox.NetworkImageView;

/**
 * @author abdyer
 */
public class NetworkImageViewTopCrop extends NetworkImageView
{
    public NetworkImageViewTopCrop(Context context) {
        super(context);
    }

    public NetworkImageViewTopCrop(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NetworkImageViewTopCrop(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        if(bm != null) {
            setScaleType(ScaleType.MATRIX);
            Matrix matrix = getImageMatrix();
            float scaleFactor = getWidth()/(float)getDrawable().getIntrinsicWidth();
            matrix.setScale(scaleFactor, scaleFactor, 0, 0);
            setImageMatrix(matrix);
        }
    }
}
