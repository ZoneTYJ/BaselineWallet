package com.vfinworks.vfsdk.activity.PeopleInfo;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.vfinworks.vfsdk.R;
import com.vfinworks.vfsdk.SDKManager;
import com.vfinworks.vfsdk.activity.BaseActivity;
import com.vfinworks.vfsdk.activity.login.LoginActivity;
import com.vfinworks.vfsdk.common.Config;
import com.vfinworks.vfsdk.common.HttpRequsetUri;
import com.vfinworks.vfsdk.common.SharedPreferenceUtil;
import com.vfinworks.vfsdk.common.Utils;
import com.vfinworks.vfsdk.common.ViewUtil;
import com.vfinworks.vfsdk.http.HttpUtils;
import com.vfinworks.vfsdk.http.RequestParams;
import com.vfinworks.vfsdk.http.VFinResponseHandler;
import com.vfinworks.vfsdk.model.CertificationModel;
import com.vfinworks.vfsdk.view.RealConfirmDialog;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 重置登录密码之身份证
 * Created by xiaoshengke on 2016/9/2.
 */
public class ResetLoginPasswordNumberActivity extends BaseActivity implements View.OnClickListener {
    private TextView tv_name;
    private EditText et_number;
    private Button btn_next;
    private CertificationModel certificationModel;
    private String phone,code,operateToken;
    private RealConfirmDialog realConfirmDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_number_verify,FLAG_TITLE_LINEARLAYOUT);
        phone = getIntent().getStringExtra("phone");
        code = getIntent().getStringExtra("code");
        operateToken = getIntent().getStringExtra("operateToken");
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
                if(!SharedPreferenceUtil.getInstance().getBooleanValueFromSP("forget")){
                    Intent intent = new Intent(ResetLoginPasswordNumberActivity.this,PasswordSettingActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }else{
                    Intent intent = new Intent();
                    try {
                        intent.setClass(ResetLoginPasswordNumberActivity.this, Class.forName(Config.LOGIN_CLASS));
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
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
        RequestParams reqParam = new RequestParams();
        reqParam.putData("service", "query_name");
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
        reqParam.putData("operate_token", operateToken);
        reqParam.putData("real_name", tv_name.getText().toString().trim());
        reqParam.putData("cert_no", et_number.getText().toString().trim());
        reqParam.putData("token", "anonymous");
        HttpUtils.getInstance(this).excuteHttpRequest(HttpRequsetUri.getInstance().getMemberDoUri(), reqParam, new VFinResponseHandler(){

            @Override
            public void onSuccess(Object responseBean, String responseString) {
                hideProgress();
                try {
                    JSONObject jsn = new JSONObject(responseString);
                    if(!jsn.isNull("status")){
                        if(jsn.optBoolean("status")){
                            Intent intent = new Intent(ResetLoginPasswordNumberActivity.this,SetLoginPasswordActivity.class);
                            intent.putExtra("phone",phone);
                            intent.putExtra("code",code);
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
//        if(!number.equals(certificationModel.getCert_no())){
//            showShortToast("证件号输入有误，请重新输入");
//        }else{
//            Intent intent = new Intent(this,SetLoginPasswordActivity.class);
//            intent.putExtra("phone",phone);
//            intent.putExtra("code",code);
//            startActivity(intent);
//        }
    }


}
