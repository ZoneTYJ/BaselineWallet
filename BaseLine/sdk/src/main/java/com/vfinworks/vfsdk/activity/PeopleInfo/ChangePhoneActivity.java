package com.vfinworks.vfsdk.activity.PeopleInfo;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.vfinworks.vfsdk.R;
import com.vfinworks.vfsdk.activity.BaseActivity;
import com.vfinworks.vfsdk.common.HttpRequsetUri;
import com.vfinworks.vfsdk.common.NoDoubleClickListener;
import com.vfinworks.vfsdk.common.Validator;
import com.vfinworks.vfsdk.http.HttpUtils;
import com.vfinworks.vfsdk.http.RequestParams;
import com.vfinworks.vfsdk.http.VFinResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

/**
 * 更换手机
 * Created by xiaoshengke on 2016/8/18.
 */
public class ChangePhoneActivity extends BaseActivity {
    private TextView tv_cp_label;
    private EditText et_cp_phone;
    private Button tv_next;
    public static WeakReference<BaseActivity> changePhoneActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changePhoneActivity = new WeakReference<BaseActivity>(this);
        setContentView(R.layout.activity_change_phone,FLAG_TITLE_LINEARLAYOUT);
        getTitlebarView().setTitle("更换绑定手机");
        bindViews();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(changePhoneActivity.get() != null){
            changePhoneActivity.get().finish();
            changePhoneActivity.clear();
        }
        changePhoneActivity = null;
    }

    private void bindViews() {
        tv_cp_label = (TextView) findViewById(R.id.tv_cp_label);
        et_cp_phone = (EditText) findViewById(R.id.et_cp_phone);
        tv_next = (Button) findViewById(R.id.tv_next);

        tv_next.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View view) {
                String phone = et_cp_phone.getText().toString().trim();
                if(TextUtils.isEmpty(phone)){
                    showShortToast("请输入手机号码");
                    return;
                }
                if(!Validator.isMobile(phone)){
                    showShortToast("手机号输入有误，请重新输入");
                    return;
                }
                checkPhoneExist();
            }
        });

    }

    private void checkPhoneExist(){
        showProgress();
        RequestParams reqParam = new RequestParams();
        final String strmobile = et_cp_phone.getText().toString().trim();
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
                            sendSmsRequest();
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

    private void sendSmsRequest() {
        Intent intent = new Intent(ChangePhoneActivity.this,ChangePhoneSmsActivity.class);
        intent.putExtra("phone",et_cp_phone.getText().toString().trim());
        intent.putExtra("context",getIntent().getSerializableExtra("context"));
        startActivity(intent);
    }

}
