package com.vfinworks.vfsdk.entertransition;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.widget.ImageView;

import com.vfinworks.vfsdk.R;

/**
 * Android 5.0开始提供原生的实现，这里全部用Java代码实现，没有写xml。
 *
 * 旋转操作时候 需要先确定转换前的坐标，然后再转换
 * 更多说明可参考：https://developer.android.com/training/material/animations.html#Transitions
 */
public class EnterTransitionActivityLollipop extends Activity {

    public static final String SHARED_ELEMENT_KEY = "SHARED_ELEMENT_KEY";
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entertransition); // layout代码和4.x是一样的
        imageView = (ImageView) findViewById(R.id.imageDetail);
        initImageEnterTransition();
    }

    private void initImageEnterTransition() {
        imageView.setVisibility(View.VISIBLE);
        String imageTransitionName = getIntent().getStringExtra(SHARED_ELEMENT_KEY);
        ViewCompat.setTransitionName(imageView, imageTransitionName);

        View mainContainer = findViewById(R.id.activityContanierDetail);
        mainContainer.setAlpha(1.0f);
        Bitmap bitmap=ImageBitmap.getInstance().getBitmap();
        imageView.setImageBitmap(bitmap);
    }

}
