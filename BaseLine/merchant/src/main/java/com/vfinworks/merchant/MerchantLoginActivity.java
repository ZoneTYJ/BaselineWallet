package com.vfinworks.merchant;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.vfinworks.vfsdk.SDKManager;
import com.vfinworks.vfsdk.common.HttpRequsetUri;
import com.vfinworks.vfsdk.common.SharedPreferenceUtil;
import com.vfinworks.vfsdk.common.Utils;
import com.vfinworks.vfsdk.http.HttpUtils;
import com.vfinworks.vfsdk.http.RequestParams;
import com.vfinworks.vfsdk.http.VFinResponseHandler;
import com.vfinworks.vfsdk.model.MemberModel;

/**
 * Created by xiaoshengke on 2017/4/10.
 */

public class MerchantLoginActivity extends com.vfinworks.vfsdk.activity.login.BaseActivity implements View.OnClickListener {
    private EditText et_account;
    private EditText et_password;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_login);
        Utils.getInstance().init(this.getApplicationContext());
        SharedPreferenceUtil.getInstance().init(this.getApplicationContext());
        findViewById(R.id.btn_done).setOnClickListener(this);
        et_account = (EditText) findViewById(R.id.et_account);
        et_password = (EditText) findViewById(R.id.et_password);
    }

    @Override
    public void onClick(View v) {
        if(TextUtils.isEmpty(et_account.getText().toString().trim())){
            showShortToast("请输入邮箱账号");
            return;
        }
        if(TextUtils.isEmpty(et_password.getText().toString().trim())){
            showShortToast("请输入密码");
            return;
        }
		showProgress();
        RequestParams reqParam = new RequestParams();
        reqParam.putData("service", "login_operator");
        reqParam.putData("name", et_account.getText().toString().trim());
        reqParam.putData("password", et_password.getText().toString().trim());
        HttpUtils.getInstance(this).excuteHttpRequest(HttpRequsetUri.getInstance()
                        .getMemberDoUri(),
                reqParam, new VFinResponseHandler<MemberModel>(MemberModel.class) {
                    @Override
                    public void onSuccess(MemberModel responseBean, String responseString) {
                        hideProgress();
                        SDKManager.token = responseBean.token;
                        go2Scan();
                    }

                    @Override
                    public void onError(String statusCode, String errorMsg) {
                        hideProgress();
                        showShortToast(errorMsg);
                    }
                }, this);
    }

    private void go2Scan() {
//        SDKManager.getInstance().QRScanGathering(this,"ceshi@po.com",null,null);
        SDKManager.getInstance().QRCodeGathering(this,"ceshi@po.com",null);
    }
}
