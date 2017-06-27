package com.vfinworks.vfsdk.activity.PeopleInfo;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import com.vfinworks.vfsdk.R;
import com.vfinworks.vfsdk.activity.BaseActivity;
import com.vfinworks.vfsdk.common.Config;
import com.vfinworks.vfsdk.common.SharedPreferenceUtil;

/**
 * 重置成功
 * Created by xiaoshengke on 2016/9/7.
 */
public class ResetSuccessActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_success,FLAG_TITLE_LINEARLAYOUT);
        initTitleBar();
    }

    private void initTitleBar() {
        getTitlebarView().setLeftVisible(false);
        getTitlebarView().setTitle("重置成功");
        getTitlebarView().initRight("完成", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(SharedPreferenceUtil.getInstance().getBooleanValueFromSP("forget")) {
                    Intent intent = new Intent();
                    intent.setComponent(new ComponentName(ResetSuccessActivity.this, Config.LOGIN_CLASS));
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(ResetSuccessActivity.this, UserInfoActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK)
            return true;
        return super.onKeyDown(keyCode, event);
    }
}
