package com.vfinworks.vfsdk.activity.PeopleInfo;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vfinworks.vfsdk.R;
import com.vfinworks.vfsdk.SDKManager;
import com.vfinworks.vfsdk.activity.BaseActivity;
import com.vfinworks.vfsdk.common.HttpRequsetUri;
import com.vfinworks.vfsdk.common.NoDoubleClickListener;
import com.vfinworks.vfsdk.common.SharedPreferenceUtil;
import com.vfinworks.vfsdk.common.ViewUtil;
import com.vfinworks.vfsdk.context.BaseContext;
import com.vfinworks.vfsdk.enumtype.VFCallBackEnum;
import com.vfinworks.vfsdk.http.HttpUtils;
import com.vfinworks.vfsdk.http.RequestParams;
import com.vfinworks.vfsdk.http.VFinResponseHandler;
import com.vfinworks.vfsdk.model.VFSDKResultModel;

/**
 * 更改手机号码——短信
 * Created by xiaoshengke on 2016/8/18.
 */
public class ChangePhoneSmsActivity extends BaseActivity {
    private TextView tv_cs_label;
    private LinearLayout ll_layout;
    private EditText et_verifycode;
    private TextView btn_resend;
    private Button tv_next;

    private String phone;
    private String token;
    private BaseContext baseContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_sms, FLAG_TITLE_LINEARLAYOUT);
        getTitlebarView().setTitle("更换手机号码");
        phone = getIntent().getStringExtra("phone");
        token = SDKManager.token;
        baseContext = (BaseContext) getIntent().getSerializableExtra("context");
        bindViews();
//        btn_resend.setEnabled(false);
//        timer.start();
        sendSmsRequest();
    }

    private void bindViews() {
        tv_cs_label = (TextView) findViewById(R.id.tv_cs_label);
        ll_layout = (LinearLayout) findViewById(R.id.ll_layout);
        et_verifycode = (EditText) findViewById(R.id.et_verifycode);
        btn_resend = (TextView) findViewById(R.id.btn_resend);
        tv_next = (Button) findViewById(R.id.tv_next);

        btn_resend.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View view) {
                sendSmsRequest();
            }
        });
        tv_next.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View view) {
                if(TextUtils.isEmpty(et_verifycode.getText().toString().trim())){
                    showShortToast("请输入验证码");
                }else{
                    verifyCode();
                }
            }
        });
        ViewUtil.viewEnableStatusChange(et_verifycode,tv_next);
    }

    private void verifyCode() {
        showProgress();
        RequestParams reqParam = new RequestParams();
        reqParam.putData("service", "verify_msg");
        reqParam.putData("mobile", phone);
        reqParam.putData("template", "RESET_MOBILE");
        reqParam.putData("token", SDKManager.token);
        reqParam.putData("msg_code", et_verifycode.getText().toString().trim());

        HttpUtils.getInstance(this).excuteHttpRequest(HttpRequsetUri.getInstance().getMemberDoUri(), reqParam, new VFinResponseHandler() {

            @Override
            public void onSuccess(Object responseBean, String responseString) {
                // TODO Auto-generated method stub
                updateData();
            }

            @Override
            public void onError(String statusCode, String errorMsg) {
                // TODO Auto-generated method stub
                hideProgress();
                showShortToast(errorMsg);
            }

        }, this);
    }

    /**
     * 更换手机号码
     */
    private void updateData() {
        RequestParams reqParam = new RequestParams();
        reqParam.putData("service", "modify_mobile");
        reqParam.putData("identity_no", SharedPreferenceUtil.getInstance().getStringValueFromSP("account"));
        reqParam.putData("identity_type", "MOBILE");
        reqParam.putData("mobile", phone);
        reqParam.putData("token", SDKManager.token);

        HttpUtils.getInstance(this).excuteHttpRequest(HttpRequsetUri.getInstance().getMemberDoUri(), reqParam, new VFinResponseHandler() {

            @Override
            public void onSuccess(Object responseBean, String responseString) {
                // TODO Auto-generated method stub
                hideProgress();
                showShortToast("更换成功");
                SharedPreferenceUtil.getInstance().setStringDataIntoSP("account",phone);
                if(SDKManager.getInstance().getCallbackHandler() != null) {
                    VFSDKResultModel result = new VFSDKResultModel();
                    result.setResultCode(VFCallBackEnum.OK.getCode());
                    result.setJsonData(responseString);
                    BaseContext baseContext = new BaseContext();
                    baseContext.sendMessage(result);
                    SDKManager.getInstance().setCallbackHandle(null);
                }
                if(ChangePhoneActivity.changePhoneActivity != null && ChangePhoneActivity.changePhoneActivity.get() != null){
                    ChangePhoneActivity.changePhoneActivity.get().finish();
                }
                finish();
//                Intent intent = new Intent(ChangePhoneSmsActivity.this,UserInfoActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
            }

            @Override
            public void onError(String statusCode, String errorMsg) {
                hideProgress();
                showShortToast(errorMsg);
            }

        }, this);
    }


    private void sendSmsRequest() {
        showProgress();
        RequestParams reqParam = new RequestParams();
        reqParam.putData("service", "send_msg");
        reqParam.putData("mobile", phone);
        reqParam.putData("template", "RESET_MOBILE");
        reqParam.putData("token", SDKManager.token);

        HttpUtils.getInstance(this).excuteHttpRequest(HttpRequsetUri.getInstance().getMemberDoUri(), reqParam, new VFinResponseHandler() {

            @Override
            public void onSuccess(Object responseBean, String responseString) {
                // TODO Auto-generated method stub
                hideProgress();
                btn_resend.setEnabled(false);
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


    private CountDownTimer timer = new CountDownTimer(60000, 1000) {

        @Override
        public void onTick(long millisUntilFinished) {
            btn_resend.setText((millisUntilFinished / 1000) + "秒后重发");
        }

        @Override
        public void onFinish() {
            btn_resend.setEnabled(true);
            btn_resend.setText("重发验证码");
        }
    };
}
