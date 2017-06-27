package com.vfinworks.vfsdk.activity.PeopleInfo;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;

import com.vfinworks.vfsdk.R;
import com.vfinworks.vfsdk.activity.BaseActivity;
import com.vfinworks.vfsdk.common.SharedPreferenceUtil;

/**
 * 重置登录密码
 * Created by xiaoshengke on 2016/9/5.
 */
public class ResetLoginActivity extends BaseActivity implements View.OnClickListener {
    private RelativeLayout rl_find;
    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_login,FLAG_TITLE_LINEARLAYOUT);
        getTitlebarView().setTitle("重置登录密码");
        phone = getIntent().getStringExtra("phone");
        if(TextUtils.isEmpty(phone)){
            phone = SharedPreferenceUtil.getInstance().getStringValueFromSP("account");
        }
        rl_find = (RelativeLayout) findViewById(R.id.rl_find);

        rl_find.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
//        Intent intent = new Intent(this,SetLoginPasswordActivity.class);
        Intent intent = new Intent(this,ResetLoginPasswordCodeActivity.class);
        intent.putExtra("phone",phone);
        startActivity(intent);
//        startActivity(new Intent(this,ResetLoginPasswordCodeActivity.class));
    }
}
