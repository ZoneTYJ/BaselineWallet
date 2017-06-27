package com.vfinworks.vfsdk.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vfinworks.vfsdk.R;
import com.vfinworks.vfsdk.SDKManager;
import com.vfinworks.vfsdk.Utils.RSA;
import com.vfinworks.vfsdk.activity.BaseActivity;
import com.vfinworks.vfsdk.common.Config;
import com.vfinworks.vfsdk.common.HttpRequsetUri;
import com.vfinworks.vfsdk.common.NoDoubleClickListener;
import com.vfinworks.vfsdk.common.SharedPreferenceUtil;
import com.vfinworks.vfsdk.db.KeyDatabaseHelper;
import com.vfinworks.vfsdk.http.HttpUtils;
import com.vfinworks.vfsdk.http.RequestParams;
import com.vfinworks.vfsdk.http.VFinResponseHandler;
import com.vfinworks.vfsdk.model.KeyModel;

import org.apaches.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;

public class RegVerifyActivity extends BaseActivity implements View.OnClickListener {
    private EditText etVeryfyCode;
    private String strMobile,strPwd;
    //private static final String TAG = RegVerifyActivity.class.getSimpleName();
    private static final String TAG = "abc";

    private TextView btnResend;
    private boolean singleClick = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_verify);

        strMobile = this.getIntent().getExtras().getString("mobile");
        strPwd = this.getIntent().getExtras().getString("password");


        etVeryfyCode = (EditText) findViewById(R.id.et_verifycode);

        ((LinearLayout) findViewById(R.id.layout_left)).setOnClickListener(this);
        btnResend = (TextView) findViewById(R.id.btn_resend);
        btnResend.setOnClickListener(resendListener);
        findViewById(R.id.tv_nextstep).setOnClickListener(nextListener);
        sendSmsRequest();
    }

    private NoDoubleClickListener nextListener = new NoDoubleClickListener() {
        @Override
        protected void onNoDoubleClick(View view) {
            regVerifyClick();
        }
    };

    private NoDoubleClickListener resendListener = new NoDoubleClickListener() {
        @Override
        protected void onNoDoubleClick(View view) {
            sendSmsRequest();
        }
    };

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.layout_left)
            backOnClick();
    }

    private void sendSmsRequest() {
        showProgress();
        RequestParams reqParam = new RequestParams();
        reqParam.putData("service", "send_msg");
        reqParam.putData("mobile", strMobile);
        reqParam.putData("template", "REGISTER_MOBILE");
        reqParam.putData("token", "anonymous");

        HttpUtils.getInstance(this).excuteHttpRequest(HttpRequsetUri.getInstance().getMemberDoUri(), reqParam, new VFinResponseHandler() {

            @Override
            public void onSuccess(Object responseBean, String responseString) {
                // TODO Auto-generated method stub
                hideProgress();
                btnResend.setEnabled(false);
                timer.start();
            }

            @Override
            public void onError(String statusCode, String errorMsg) {
                // TODO Auto-generated method stub
                hideProgress();
                showShortToast(errorMsg);
            }

        }, this);
    }

    private void backOnClick() {
        this.finish();
    }

    public void showShortToast(String text) {
        showToast(text, Toast.LENGTH_SHORT);
    }


    public void regVerifyClick() {
        if (TextUtils.isEmpty(etVeryfyCode.getText().toString().trim()))
        {
            showShortToast("验证码不能为空!");
        } else {
            if(!singleClick) {
                singleClick = true;
                showProgress();
                RequestParams reqParam = new RequestParams();
                reqParam.putData("service", "register_member");
                reqParam.putData("mobile", strMobile);
                reqParam.putData("nick_name", strMobile);
                reqParam.putData("login_pwd", strPwd);
                reqParam.putData("msg_code", etVeryfyCode.getText().toString().trim());
                reqParam.putData("token", "anonymous");
                HttpUtils.getInstance(this).excuteHttpRequest(HttpRequsetUri.getInstance().getMemberDoUri(), reqParam, new VFinResponseHandler() {
                    @Override
                    public void onSuccess(Object responseBean, String responseString) {
                        hideProgress();
                        singleClick = false;
                        doLogin();
                    }

                    @Override
                    public void onError(String statusCode, String errorMsg) {
                        // TODO Auto-generated method stub
                        hideProgress();
                        showShortToast(errorMsg);
                        singleClick = false;
                    }

                }, this);
            }
        }
    }


    private void doLogin() {


            RequestParams reqParam = new RequestParams();
            reqParam.putData("service", "login_member");
            reqParam.putData("mobile", strMobile);
            reqParam.putData("login_pwd", strPwd);
            reqParam.putData("device_id", Config.getInstance().getDeviceId());
            HttpUtils.getInstance(this).excuteHttpRequest(HttpRequsetUri.getInstance()
                    .getMemberDoUri(), reqParam, new VFinResponseHandler() {
                @Override
                public void onSuccess(Object responseBean, String responseString) {
                    hideProgress();
                    Log.d(TAG, responseString);
                    SharedPreferenceUtil.getInstance().setStringDataIntoSP(LoginActivity.ACCOUNT, strMobile);
                    savePwd();
                    JSONObject json;
                    try {
                        json = new JSONObject(responseString);
                        String refresh_token = json.getString("refresh_token");
                        String token = json.getString("token");
                        saveToken(token, refresh_token);
                        int payPwdFlag = json.getInt("pay_pwd_status");
                        SharedPreferenceUtil.getInstance().setIntDataIntoSP(LoginActivity.PAY_PWD_SET, payPwdFlag);
                        startActivity(new Intent(RegVerifyActivity.this, FillInfoActivity.class));

                        finish();
                        RegActivity.regActivity.get().finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        showShortToast(e.getMessage());
                    }
                }

                @Override
                public void onError(String statusCode, String errorMsg) {
                    hideProgress();
                    showShortToast(errorMsg);

                }

            }, this);
    }

    private void saveToken(String token,String refresh_token) {
        HttpUtils.getInstance(this).saveToken(token,refresh_token);
        SDKManager.token = token;
    }

    private void savePwd() {
        KeyModel keyModel = KeyDatabaseHelper.getInstance(this).queryKey();
        try {
            SharedPreferenceUtil.getInstance().setStringDataIntoSP("pwd", new String(Base64.encodeBase64(RSA.encryptByPublicKey(strPwd.getBytes(),keyModel.publicKey))));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private CountDownTimer timer = new CountDownTimer(60000, 1000) {

        @Override
        public void onTick(long millisUntilFinished) {
            btnResend.setText((millisUntilFinished / 1000) + "秒后重发");
        }

        @Override
        public void onFinish() {
            btnResend.setEnabled(true);
            btnResend.setText("重发验证码");
        }
    };
}
