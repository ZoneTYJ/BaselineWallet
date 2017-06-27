package com.vfinworks.vfsdk.activity.PeopleInfo;

import android.os.Bundle;

import com.vfinworks.vfsdk.R;
import com.vfinworks.vfsdk.activity.BaseActivity;

/**
 * 版本说明
 * Created by xiaoshengke on 2016/10/8.
 */
public class VersionIntroActivity extends BaseActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_version_intro,FLAG_TITLE_LINEARLAYOUT);
        getTitlebarView().setTitle("版本说明");
    }
}
