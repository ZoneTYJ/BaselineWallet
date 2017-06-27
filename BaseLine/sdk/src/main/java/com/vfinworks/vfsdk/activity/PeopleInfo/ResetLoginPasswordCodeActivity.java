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
import com.vfinworks.vfsdk.common.NoDoubleClickListener;
import com.vfinworks.vfsdk.common.StringReplaceUtil;
import com.vfinworks.vfsdk.common.ViewUtil;
import com.vfinworks.vfsdk.http.HttpUtils;
import com.vfinworks.vfsdk.http.RequestParams;
import com.vfinworks.vfsdk.http.VFinResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 重置登录密码之短信验证码
 * Created by xiaoshengke on 2016/9/2.
 */
public class ResetLoginPasswordCodeActivity extends BaseActivity implements View.OnClickListener {
    private TextView tv_piv_phone;
    private EditText et_piv_code;
    private TextView tv_get_code;
    private Button btn_next;
    private String phone;
    private String token;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_id_verify,FLAG_TITLE_LINEARLAYOUT);
        phone = getIntent().getStringExtra("phone");
        token = SDKManager.token;
        getTitlebarView().setTitle("重置登录密码");
        bindViews();
        sendSmsClick();
    }

    private void bindViews() {
        tv_piv_phone = (TextView) findViewById(R.id.tv_piv_phone);
        et_piv_code = (EditText) findViewById(R.id.et_piv_code);
        tv_get_code = (TextView) findViewById(R.id.tv_get_code);
        btn_next = (Button) findViewById(R.id.btn_next);

        tv_piv_phone.setText("请输入手机号"+ StringReplaceUtil.getStarString(phone,3,7)+"收到的验证码");

        btn_next.setOnClickListener(nextListener);
        tv_get_code.setOnClickListener(this);

        ViewUtil.viewEnableStatusChange(et_piv_code,btn_next);
    }

    private NoDoubleClickListener nextListener = new NoDoubleClickListener() {
        @Override
        protected void onNoDoubleClick(View view) {
            if(TextUtils.isEmpty(et_piv_code.getText().toString().trim())){
                showShortToast("请输入验证码");
            } else if (et_piv_code.getText().toString().trim().length() != 6) {
                showShortToast("验证码位数为6位");
            } else {
                verifyCode();
            }
        }
    };

    @Override
    public void onClick(View v) {
        if(v == tv_get_code){
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
        reqParam.putData("template", "REFI_LOGIN_SMS");
        reqParam.putData("msg_code", et_piv_code.getText().toString().trim());
        reqParam.putData("token", "anonymous");
        HttpUtils.getInstance(this).excuteHttpRequest(HttpRequsetUri.getInstance().getMemberDoUri(), reqParam, new VFinResponseHandler() {

            @Override
            public void onSuccess(Object responseBean, String responseString) {
                hideProgress();
                try {
                    JSONObject jsn = new JSONObject(responseString);
                    if(!jsn.isNull("operateToken")){
                        Intent intent = new Intent(ResetLoginPasswordCodeActivity.this, ResetLoginPasswordNumberActivity.class);  //方法1
                        intent.putExtra("phone",phone);
//                        intent.putExtra("code",et_piv_code.getText().toString().trim());
                        intent.putExtra("operateToken",jsn.optString("operateToken"));
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
        reqParam.putData("template", "REFI_LOGIN_SMS");
        reqParam.putData("token", "anonymous");

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
