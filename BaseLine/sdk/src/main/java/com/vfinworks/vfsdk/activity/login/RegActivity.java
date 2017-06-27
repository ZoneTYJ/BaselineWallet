package com.vfinworks.vfsdk.activity.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.vfinworks.vfsdk.R;
import com.vfinworks.vfsdk.common.HttpRequsetUri;
import com.vfinworks.vfsdk.common.NoDoubleClickListener;
import com.vfinworks.vfsdk.common.Validator;
import com.vfinworks.vfsdk.http.HttpUtils;
import com.vfinworks.vfsdk.http.RequestParams;
import com.vfinworks.vfsdk.http.VFinResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

public class RegActivity extends BaseActivity implements View.OnClickListener {
    private EditText tvMobile;
    private EditText tv_reg_pwd;
    private static final String TAG = RegActivity.class.getSimpleName();
    private ReqConfirmDialog dialog;
    public static WeakReference<Activity> regActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);
        regActivity = new WeakReference<Activity>(this);
        dialog = new ReqConfirmDialog(this);
        dialog.setSureClickListener(this);
        ((LinearLayout) findViewById(R.id.layout_left)).setOnClickListener(this);
        tvMobile = (EditText) findViewById(R.id.et_reg_mobile_edit);
        tv_reg_pwd = (EditText) findViewById(R.id.tv_reg_pwd);
        findViewById(R.id.btn_reg_button).setOnClickListener(regListener);
    }

    private NoDoubleClickListener regListener = new NoDoubleClickListener() {
        @Override
        protected void onNoDoubleClick(View view) {
            if(TextUtils.isEmpty(tv_reg_pwd.getText().toString().trim())) {
                showShortToast("密码不能为空");
                return;
            }

            if (TextUtils.isEmpty(tvMobile.getText().toString().trim())) {
                showShortToast("手机号不能为空");
            }
            if(!Validator.isMobile(tvMobile.getText().toString().trim())){
                showShortToast("手机号输入有误，请重新输入");
                return;
            }
            if(!Validator.isLoginPasswordLegal(tv_reg_pwd.getText().toString().trim())){
                showShortToast("登录密码必须是6~20位数字、字母、符号的组合!");
                return;
            }
            checkPhoneExist();
        }
    };

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.layout_left)
            backOnClick();
        else if (v.getId() == R.id.tv_true) {
            dialog.dismiss();
            Intent regVeryfyintent = new Intent(RegActivity.this, RegVerifyActivity.class);  //方法1
            regVeryfyintent.putExtra("mobile", tvMobile.getText().toString().trim());
            regVeryfyintent.putExtra("password", tv_reg_pwd.getText().toString().trim());
            startActivity(regVeryfyintent);
        }

    }

    private void backOnClick() {
        this.finish();
    }

    private void checkPhoneExist(){
        showProgress();
        RequestParams reqParam = new RequestParams();
        final String strmobile = tvMobile.getText().toString().trim();
        reqParam.putData("service", "query_member_integrated_info");
        reqParam.putData("memberIdentity", strmobile);
        HttpUtils.getInstance(this).excuteHttpRequest(HttpRequsetUri.getInstance().getMemberDoUri(), reqParam, new VFinResponseHandler() {

            @Override
            public void onSuccess(Object responseBean, String responseString) {
                // TODO Auto-generated method stub
                hideProgress();
                try {
                    JSONObject jsonObject = new JSONObject(responseString);
                    if(!jsonObject.isNull("isMemberExists")){
                        int exist = jsonObject.optInt("isMemberExists");
                        if(exist == 1){
                            showShortToast("手机号已存在，请重新输入");
                        }else{
                            dialog.setDesc("我们将发验证码短信到这个手机号码:\n"+tvMobile.getText().toString().trim());
                            dialog.show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String statusCode, String errorMsg) {
                // TODO Auto-generated method stub
                hideProgress();
                showShortToast(errorMsg);
            }
        }, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        regActivity = null;
    }
}
