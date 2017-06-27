package com.vfinworks.vfsdk.activity.PeopleInfo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.vfinworks.vfsdk.R;
import com.vfinworks.vfsdk.SDKManager;
import com.vfinworks.vfsdk.activity.BaseActivity;
import com.vfinworks.vfsdk.activity.assistant.SetPaymentPwdActivity;
import com.vfinworks.vfsdk.activity.login.LoginActivity;
import com.vfinworks.vfsdk.common.HttpRequsetUri;
import com.vfinworks.vfsdk.common.SharedPreferenceUtil;
import com.vfinworks.vfsdk.common.Utils;
import com.vfinworks.vfsdk.common.ViewUtil;
import com.vfinworks.vfsdk.context.BaseContext;
import com.vfinworks.vfsdk.enumtype.PaymentPwdEnum;
import com.vfinworks.vfsdk.enumtype.VFCallBackEnum;
import com.vfinworks.vfsdk.http.HttpUtils;
import com.vfinworks.vfsdk.http.RequestParams;
import com.vfinworks.vfsdk.http.VFinResponseHandler;
import com.vfinworks.vfsdk.model.CertificationModel;
import com.vfinworks.vfsdk.model.VFSDKResultModel;
import com.vfinworks.vfsdk.view.RealConfirmDialog;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 身份验证之身份证号码
 * Created by xiaoshengke on 2016/9/2.
 */
public class PayNumberVerifyActivity extends BaseActivity implements View.OnClickListener {
    private TextView tv_name;
    private EditText et_number;
    private Button btn_next;
    private CertificationModel certificationModel;
    private RealConfirmDialog realConfirmDialog;
    private String phone,code,operateToken,token;
    private BaseContext baseContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setIsPush(false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_number_verify, FLAG_TITLE_LINEARLAYOUT);
        baseContext = (BaseContext) getIntent().getSerializableExtra("context");
        phone = SharedPreferenceUtil.getInstance().getStringValueFromSP(LoginActivity.ACCOUNT);
        code = getIntent().getStringExtra("code");
        operateToken = getIntent().getStringExtra("operateToken");
        token=getToken();
        getTitlebarView().setTitle("身份验证");
        bindViews();
        initDialog();
        getData();
    }

    private void initDialog() {
        realConfirmDialog = new RealConfirmDialog(this);
        realConfirmDialog.setMessage("确定要退出本次密码找回吗？");
        realConfirmDialog.setRightBtnText("退出");
        realConfirmDialog.setSureClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                realConfirmDialog.dismiss();
                Intent intent = new Intent(PayNumberVerifyActivity.this,SafeCheckActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        getTitlebarView().initLeft(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                realConfirmDialog.show();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            realConfirmDialog.show();
            return true;
        }else
            return super.onKeyDown(keyCode, event);
    }

    private void getData() {
        showProgress();
//        RequestParams reqParam1 = new RequestParams();
//        reqParam1.putData("service", "query_certification");
//        reqParam1.putData("token", SDKManager.token);
//        HttpUtils.getInstance(this).excuteHttpRequest(HttpRequsetUri.getInstance().getMemberDoUri(), reqParam1, new VFinResponseHandler<CertificationModel>(CertificationModel.class){
//
//            @Override
//            public void onSuccess(CertificationModel responseBean, String responseString) {
//                hideProgress();
//                certificationModel = responseBean;
//                tv_name.setText(certificationModel.getReal_name());
//            }
//
//            @Override
//            public void onError(String statusCode, String errorMsg) {
//                hideProgress();
//                showShortToast(errorMsg);
//            }
//
//        }, this);

        RequestParams reqParam = new RequestParams();
        reqParam.putData("service", "query_name");
        reqParam.putData("token", SDKManager.token);
        reqParam.putData("identity_no", SharedPreferenceUtil.getInstance().getStringValueFromSP(LoginActivity.ACCOUNT));
        HttpUtils.getInstance(this).excuteHttpRequest(HttpRequsetUri.getInstance().getMemberDoUri(), reqParam, new VFinResponseHandler(){

            @Override
            public void onSuccess(Object responseBean, String responseString) {
                hideProgress();
                try {
                    JSONObject jsn = new JSONObject(responseString);
                    String realName = "";
                    if(jsn.has("realName")){
                        realName = jsn.optString("realName");
                    }
                    tv_name.setText(realName);
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

    private void submitData() {
        showProgress();
        RequestParams reqParam = new RequestParams();
        reqParam.putData("service", "verify_certification");
        reqParam.putData("mobile_no", phone);
        reqParam.putData("real_name", tv_name.getText().toString().trim());
        reqParam.putData("token",SDKManager.token);
        reqParam.putData("cert_no", et_number.getText().toString().trim());
        HttpUtils.getInstance(this).excuteHttpRequest(HttpRequsetUri.getInstance().getMemberDoUri(), reqParam, new VFinResponseHandler(){

            @Override
            public void onSuccess(Object responseBean, String responseString) {
                hideProgress();
                try {
                    JSONObject jsn = new JSONObject(responseString);
                    if(!jsn.isNull("status")){
                        if(jsn.optBoolean("status")){
//                            SharedPreferenceUtil.getInstance().setStringDataIntoSP("code",code);
                            Intent intent = new Intent();
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("context", baseContext);
                            bundle.putSerializable("type", PaymentPwdEnum.MODIFY_NEW_PASSWORD);
                            intent.putExtras(bundle);
                            intent.setClass(PayNumberVerifyActivity.this,SetPaymentPwdActivity.class);
                            startActivity(intent);
                        }else{
                            showShortToast("验证未通过，请填写正确的信息");
                        }
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

    private void bindViews() {
        tv_name = (TextView) findViewById(R.id.tv_name);
        et_number = (EditText) findViewById(R.id.et_number);
        btn_next = (Button) findViewById(R.id.btn_next);

        btn_next.setOnClickListener(this);

        ViewUtil.viewEnableStatusChange(et_number,btn_next);
    }


    @Override
    public void onClick(View v) {
        String name = tv_name.getText().toString().trim();
        if(TextUtils.isEmpty(name)){
            showShortToast("请输入姓名");
            return;
        }
        String number = et_number.getText().toString().trim();
        if(TextUtils.isEmpty(number)){
            showShortToast("请输入身份证号码");
            return;
        }
        String validateDesc = Utils.getInstance().IDCardValidate(number);
        if(!TextUtils.isEmpty(validateDesc)){
            showShortToast(validateDesc);
            return;
        }
        submitData();
    }

    private int setPaypwdcallbackMessageID = 1;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == setPaypwdcallbackMessageID) {
                handleSetPayPwdCallback((VFSDKResultModel) msg.obj);
            }
        }

        ;
    };

    private void handleSetPayPwdCallback(VFSDKResultModel resultModel) {
        if (resultModel.getResultCode().equalsIgnoreCase(VFCallBackEnum.OK.getCode()) == true) {
            Toast.makeText(this, "支付密码设置成功！", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this,SafeCheckActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

}
