package com.vfinworks.vfsdk.activity.PeopleInfo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.vfinworks.vfsdk.R;
import com.vfinworks.vfsdk.activity.BaseActivity;

/**
 * 安全设置
 * Created by xiaoshengke on 2016/9/9.
 */
public class SecuritySettingActivity extends BaseActivity implements View.OnClickListener {
    private RelativeLayout rl_gesture_set;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_setting,FLAG_TITLE_LINEARLAYOUT);
        getTitlebarView().setTitle("安全设置");
        rl_gesture_set = (RelativeLayout) findViewById(R.id.rl_gesture_set);

        rl_gesture_set.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        startActivity(new Intent(this,GestureSettingActivity.class));
    }
}
