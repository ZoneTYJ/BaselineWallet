package com.vfinworks.vfsdk.activity.PeopleInfo;

import android.os.Bundle;
import android.webkit.WebView;

import com.vfinworks.vfsdk.R;
import com.vfinworks.vfsdk.activity.BaseActivity;

/**
 * 说明
 * Created by xiaoshengke on 2016/10/8.
 */
public class DescriptionActivity extends BaseActivity {
    private String title;
    private String url;
    private WebView wv_desc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description,FLAG_TITLE_LINEARLAYOUT);

        url = getIntent().getStringExtra("url");
        title = getIntent().getStringExtra("title");
        getTitlebarView().setTitle(title);

        wv_desc = (WebView) findViewById(R.id.wv_desc);
        wv_desc.loadUrl(url);
    }
}
