package com.vfinworks.vfsdk.activity.PeopleInfo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.vfinworks.vfsdk.R;
import com.vfinworks.vfsdk.activity.BaseActivity;
import com.vfinworks.vfsdk.common.SharedPreferenceUtil;

/**
 * 密码设置
 * Created by xiaoshengke on 2016/9/1.
 */
public class PasswordSettingActivity extends BaseActivity implements View.OnClickListener {

    private RelativeLayout rlPayReset;
    private RelativeLayout rlLoginReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_setting, FLAG_TITLE_LINEARLAYOUT);
        getTitlebarView().setTitle("密码设置");
        bindViews();
    }

    private void bindViews() {
        rlPayReset = (RelativeLayout) findViewById(R.id.rl_pay_reset);
        rlLoginReset = (RelativeLayout) findViewById(R.id.rl_login_reset);

        rlPayReset.setOnClickListener(this);
        rlLoginReset.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == rlPayReset){
            startActivity(new Intent(this,ResetPayPwdActivity.class));
        }else if(v == rlLoginReset){
            SharedPreferenceUtil.getInstance().setBooleanDataIntoSP("forget",false);
            startActivity(new Intent(this,ResetLoginActivity.class));
        }
    }
}
