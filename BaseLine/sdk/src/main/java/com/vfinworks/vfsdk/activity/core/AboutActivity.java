package com.vfinworks.vfsdk.activity.core;

import android.os.Bundle;
import android.view.View;

import com.vfinworks.vfsdk.R;
import com.vfinworks.vfsdk.activity.BaseActivity;

/**
 * 关于
 * Created by xiaoshengke on 2016/4/6.
 */
public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_about,FLAG_TITLE_LINEARLAYOUT);
        setIsPush(false);
        super.onCreate(savedInstanceState);
        getTitlebarView().initLeft("", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getTitlebarView().setTitle("关于");
    }
}
