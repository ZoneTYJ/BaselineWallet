package com.vfinworks.vfsdk.activity.PeopleInfo;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.vfinworks.vfsdk.R;
import com.vfinworks.vfsdk.activity.BaseActivity;
import com.vfinworks.vfsdk.common.HttpRequsetUri;
import com.vfinworks.vfsdk.common.SHA256Encrypt;
import com.vfinworks.vfsdk.common.Validator;
import com.vfinworks.vfsdk.common.ViewUtil;
import com.vfinworks.vfsdk.http.HttpUtils;
import com.vfinworks.vfsdk.http.RequestParams;
import com.vfinworks.vfsdk.http.VFinResponseHandler;

/**
 * 设置登录密码
 * Created by xiaoshengke on 2016/9/5.
 */
public class SetLoginPasswordActivity extends BaseActivity implements View.OnClickListener {
    private EditText et_login_password;
    private ImageView iv_see;
    private Button btn_next;
    private boolean isShow;
    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_login_password,FLAG_TITLE_LINEARLAYOUT);
        getTitlebarView().setTitle("重置登录密码");
        phone = getIntent().getStringExtra("phone");
        bindViews();
    }

    private void bindViews() {
        et_login_password = (EditText) findViewById(R.id.et_login_password);
        iv_see = (ImageView) findViewById(R.id.iv_see);
        btn_next = (Button) findViewById(R.id.btn_next);

        iv_see.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isShow){
                    setTextVisibility(true);
                    isShow = true;
                    iv_see.setImageResource(R.drawable.vf_eye_open);
                }else{
                    setTextVisibility(false);
                    isShow = false;
                    iv_see.setImageResource(R.drawable.vf_eye_close);
                }
            }
        });
        btn_next.setOnClickListener(this);

        ViewUtil.viewEnableStatusChange(et_login_password,btn_next);
    }

    /**
     * 设置输入的文本是否可见
     * @param isShow
     */
    public void setTextVisibility(boolean isShow){
        if(!isShow)
            et_login_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
        else
            et_login_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
    }

    @Override
    public void onClick(View v) {
        if(et_login_password.getText().toString().trim().length() < 6){
            showShortToast("密码至少为6位");
        }
        else if(!Validator.isLoginPasswordLegal(et_login_password.getText().toString().trim())){
            showShortToast("登录密码必须是6~20位数字、字母、符号的组合!");
        }else{
            updateData();
        }
    }

    private void updateData() {
        showProgress();
        RequestParams reqParam = new RequestParams();
        reqParam.putData("service", "set_loginpwd");
        reqParam.putData("identity_no", phone);
        reqParam.putData("identity_type", "MOBILE");
        reqParam.putData("login_pwd", SHA256Encrypt.bin2hex(et_login_password.getText().toString().trim()));
        reqParam.putData("token", "");
        HttpUtils.getInstance(this).excuteHttpRequest(HttpRequsetUri.getInstance().getMemberDoUri(), reqParam, new VFinResponseHandler() {

            @Override
            public void onSuccess(Object responseBean, String responseString) {
                hideProgress();
//                showShortToast("重置登录密码成功");
//                if(SharedPreferenceUtil.getInstance().getBooleanValueFromSP("forget")) {
//                    Intent intent = new Intent();
//                    intent.setComponent(new ComponentName(SetLoginPasswordActivity.this,Config.LOGIN_CLASS));
//                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(intent);
//                }else{
//                    Intent intent = new Intent(SetLoginPasswordActivity.this, UserInfoActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(intent);
//                }
                startActivity(new Intent(SetLoginPasswordActivity.this,ResetSuccessActivity.class));
            }

            @Override
            public void onError(String statusCode, String errorMsg) {
                hideProgress();
                showShortToast(errorMsg);
            }
        }, this);
    }
}
