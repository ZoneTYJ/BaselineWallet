package com.vfinworks.vfsdk.activity.PeopleInfo;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.vfinworks.vfsdk.R;
import com.vfinworks.vfsdk.SDKManager;
import com.vfinworks.vfsdk.activity.BaseActivity;
import com.vfinworks.vfsdk.common.Config;
import com.vfinworks.vfsdk.common.HttpRequsetUri;
import com.vfinworks.vfsdk.common.SharedPreferenceUtil;
import com.vfinworks.vfsdk.http.HttpUtils;
import com.vfinworks.vfsdk.http.RequestParams;
import com.vfinworks.vfsdk.http.VFinResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 设置
 * Created by xiaoshengke on 2016/9/1.
 */
public class SettingActivity extends BaseActivity implements View.OnClickListener {

    private RelativeLayout rlPwdSet;
    private RelativeLayout rlSecuritySet;
    private Button btnLoginOut;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting, FLAG_TITLE_LINEARLAYOUT);
        getTitlebarView().setTitle("设置");
        bindViews();
    }

    private void bindViews() {
        rlPwdSet = (RelativeLayout) findViewById(R.id.rl_pwd_set);
        rlSecuritySet = (RelativeLayout) findViewById(R.id.rl_security_set);
        btnLoginOut = (Button) findViewById(R.id.btn_login_out);
        rlPwdSet.setOnClickListener(this);
        rlSecuritySet.setOnClickListener(this);
        btnLoginOut.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v == rlPwdSet){
            startActivity(new Intent(this,PasswordSettingActivity.class));
        }else if(v == rlSecuritySet){
//            startActivity(new Intent(this,SetSecurityActivity.class));
            startActivity(new Intent(this,SecuritySettingActivity.class));
        }else if(v == btnLoginOut){
            RequestParams rp = new RequestParams();
            rp.putData("server","logout_member");
            rp.putData("token", SDKManager.token);
            SharedPreferenceUtil.getInstance().setStringDataIntoSP("token","");
            HttpUtils.getInstance(this).excuteHttpRequest(HttpRequsetUri.getInstance().getMemberDoUri(),
                    rp, new VFinResponseHandler() {
                        @Override
                        public void onSuccess(Object responseBean, String responseString) {
                            JSONObject jsonObject;
                            try {
                                jsonObject = new JSONObject(responseString);
                                String logout_status = jsonObject.optString("logout_status");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            SDKManager.token = null;
                            go2Login();
                        }

                        @Override
                        public void onError(String statusCode, String errorMsg) {
                            go2Login();
                        }
                    },this);
            SharedPreferenceUtil.getInstance().setBooleanDataIntoSP(GestureSettingActivity.GESTURE_SWITCH,false);
            SharedPreferenceUtil.getInstance().setBooleanDataIntoSP(GestureSettingActivity.GESTURE_SHOW, false);
            SharedPreferenceUtil.getInstance().setStringDataIntoSP(GestureSettingActivity.GESTURE,"");
        }
    }

    public void go2Login(){
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(this, Config.LOGIN_CLASS));
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
