package com.vfinworks.vfsdk.entertransition;

import android.graphics.Bitmap;

/**
 * Created by tangyijian on 2017/4/5.
 */

public class ImageBitmap {
    private static ImageBitmap mInstance;
    private Bitmap mBitmap;

    public static synchronized ImageBitmap getInstance() {
        if (mInstance == null) {
            mInstance = new ImageBitmap();
        }
        return mInstance;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
    }
}
