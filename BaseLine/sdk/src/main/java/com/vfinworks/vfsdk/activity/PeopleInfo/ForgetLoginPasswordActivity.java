package com.vfinworks.vfsdk.activity.PeopleInfo;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.vfinworks.vfsdk.R;
import com.vfinworks.vfsdk.activity.BaseActivity;
import com.vfinworks.vfsdk.common.HttpRequsetUri;
import com.vfinworks.vfsdk.common.NoDoubleClickListener;
import com.vfinworks.vfsdk.common.SharedPreferenceUtil;
import com.vfinworks.vfsdk.common.Validator;
import com.vfinworks.vfsdk.common.ViewUtil;
import com.vfinworks.vfsdk.http.HttpUtils;
import com.vfinworks.vfsdk.http.RequestParams;
import com.vfinworks.vfsdk.http.VFinResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 忘记登录密码之输手机号
 * Created by xiaoshengke on 2016/9/5.
 */
public class ForgetLoginPasswordActivity extends BaseActivity {
    private EditText et_phone;
    private Button btn_next;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_login_password,FLAG_TITLE_LINEARLAYOUT);
        getTitlebarView().setTitle("重置登录密码");

        bindViews();
    }

    private void bindViews() {
        et_phone = (EditText) findViewById(R.id.et_phone);
        btn_next = (Button) findViewById(R.id.btn_next);

        ViewUtil.viewEnableStatusChange(et_phone,btn_next);

        btn_next.setOnClickListener(listener);
    }

    private NoDoubleClickListener listener = new NoDoubleClickListener() {
        @Override
        protected void onNoDoubleClick(View view) {
            if(TextUtils.isEmpty(et_phone.getText().toString().trim())){
                showShortToast("账户名不能为空");
                return;
            }
            if(Validator.isMobile(et_phone.getText().toString().trim())) {
                checkPhoneExist();
            }else{
                showShortToast("手机号输入有误，请重新输入");
            }
        }
    };

    private void checkPhoneExist(){
        showProgress();
        RequestParams reqParam = new RequestParams();
        final String strmobile = et_phone.getText().toString().trim();
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
                            SharedPreferenceUtil.getInstance().setBooleanDataIntoSP("forget",true);
                            Intent intent = new Intent(ForgetLoginPasswordActivity.this, ResetLoginActivity.class);
                            intent.putExtra("phone", et_phone.getText().toString().trim());
                            startActivity(intent);
                        }else{
                            showShortToast("账户名不存在，请重新输入");
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
}
