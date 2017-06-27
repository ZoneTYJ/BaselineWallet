package com.vfinworks.vfsdk.activity.PeopleInfo;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.vfinworks.vfsdk.R;
import com.vfinworks.vfsdk.SDKManager;
import com.vfinworks.vfsdk.activity.BaseActivity;
import com.vfinworks.vfsdk.common.HttpRequsetUri;
import com.vfinworks.vfsdk.common.SharedPreferenceUtil;
import com.vfinworks.vfsdk.common.StringReplaceUtil;
import com.vfinworks.vfsdk.common.ViewUtil;
import com.vfinworks.vfsdk.context.BaseContext;
import com.vfinworks.vfsdk.http.HttpUtils;
import com.vfinworks.vfsdk.http.RequestParams;
import com.vfinworks.vfsdk.http.VFinResponseHandler;

/**
 * 重置支付密码之身份验证
 * Created by xiaoshengke on 2016/9/2.
 */
public class PayIDVerifyActivity extends BaseActivity implements View.OnClickListener {
    private TextView tv_piv_phone;
    private EditText et_piv_code;
    private TextView tv_get_code;
    private Button btn_next;
    private String phone;
    private String token;
    private BaseContext baseContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_id_verify,FLAG_TITLE_LINEARLAYOUT);
        baseContext = (BaseContext) getIntent().getSerializableExtra("context");
        phone = SharedPreferenceUtil.getInstance().getStringValueFromSP("account");
        token = SDKManager.token;
        getTitlebarView().setTitle("身份验证");
        bindViews();
        sendSmsClick();
    }

    private void bindViews() {
        tv_piv_phone = (TextView) findViewById(R.id.tv_piv_phone);
        et_piv_code = (EditText) findViewById(R.id.et_piv_code);
        tv_get_code = (TextView) findViewById(R.id.tv_get_code);
        btn_next = (Button) findViewById(R.id.btn_next);

        tv_piv_phone.setText("请输入手机号"+ StringReplaceUtil.getStarString(SharedPreferenceUtil.getInstance().getStringValueFromSP("account"),3,7)+"收到的验证码");

        btn_next.setOnClickListener(this);
        tv_get_code.setOnClickListener(this);

        ViewUtil.viewEnableStatusChange(et_piv_code,btn_next);
    }


    @Override
    public void onClick(View v) {
        if(v == btn_next){
            if(TextUtils.isEmpty(et_piv_code.getText().toString().trim())){
                showShortToast("请输入验证码");
            } else if (et_piv_code.getText().toString().trim().length() != 6) {
                showShortToast("验证码位数为6位");
            } else {
                verifyCode();
            }
        }else if(v == tv_get_code){
            sendSmsClick();
        }
    }

    /**
     * 验证短信
     */
    private void verifyCode() {
        showProgress();
        RequestParams reqParam = new RequestParams();
        reqParam.putData("service", "verify_msg");
        reqParam.putData("mobile", phone);
        reqParam.putData("template", "SET_PAYPASSWD");
        reqParam.putData("msg_code", et_piv_code.getText().toString().trim());
        reqParam.putData("token", SDKManager.token);
        HttpUtils.getInstance(this).excuteHttpRequest(HttpRequsetUri.getInstance().getMemberDoUri(), reqParam, new VFinResponseHandler() {

            @Override
            public void onSuccess(Object responseBean, String responseString) {
                hideProgress();
                Intent intent = new Intent(PayIDVerifyActivity.this, PayNumberVerifyActivity.class);  //方法1
                intent.putExtra("context",baseContext);
                startActivity(intent);
            }

            @Override
            public void onError(String statusCode, String errorMsg) {
                hideProgress();
                showShortToast(errorMsg);
            }
        }, this);
    }

    /**
     * 发送短信
     */
    public void sendSmsClick() {
        showProgress();
        RequestParams reqParam = new RequestParams();
        reqParam.putData("service", "send_msg");
        reqParam.putData("mobile", phone);
        reqParam.putData("template", "SET_PAYPASSWD");
        reqParam.putData("token", SDKManager.token);

        HttpUtils.getInstance(this).excuteHttpRequest(HttpRequsetUri.getInstance().getMemberDoUri(), reqParam, new VFinResponseHandler() {
            @Override
            public void onSuccess(Object responseBean, String responseString) {
                // TODO Auto-generated method stub
                hideProgress();
                tv_get_code.setEnabled(false);
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

    @Override
    protected void onDestroy() {
        timer.cancel();
        super.onDestroy();
    }

    private CountDownTimer timer = new CountDownTimer(60000, 1000) {

        @Override
        public void onTick(long millisUntilFinished) {
            tv_get_code.setText((millisUntilFinished / 1000) + "秒后可重发");
        }

        @Override
        public void onFinish() {
            tv_get_code.setEnabled(true);
            tv_get_code.setText("重发验证码");
        }
    };
}
