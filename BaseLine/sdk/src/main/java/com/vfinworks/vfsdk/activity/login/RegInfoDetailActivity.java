package com.vfinworks.vfsdk.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.vfinworks.vfsdk.R;
import com.vfinworks.vfsdk.common.HttpRequsetUri;
import com.vfinworks.vfsdk.http.HttpUtils;
import com.vfinworks.vfsdk.http.RequestParams;
import com.vfinworks.vfsdk.http.VFinResponseHandler;

public class RegInfoDetailActivity extends BaseActivity implements View.OnClickListener {

    private EditText etNickname;
    private EditText etPassword;
    private String strMobile;
    private String msgCode;
    private static final String TAG = RegInfoDetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_info_detail);

        strMobile = this.getIntent().getExtras().getString("mobile");
        msgCode = this.getIntent().getExtras().getString("msgcode");


        etNickname = (EditText) findViewById(R.id.et_nickname);
        //支付宝只有一个密码，没有密码确认
        etPassword = (EditText) findViewById(R.id.et_password);



        ((LinearLayout) findViewById(R.id.layout_left)).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        backOnClick();
    }

    private void backOnClick() {
        this.finish();
    }

    public void showShortToast(String text) {
        showToast(text, Toast.LENGTH_SHORT);
    }



    public void regDetailInfo_Click(View view) {
        if (TextUtils.isEmpty(etNickname.getText().toString().trim()))
        {
            showShortToast("请输入昵称!");
        } else  if (TextUtils.isEmpty(etPassword.getText().toString().trim()))
        {
            showShortToast("请输入昵称!");
        }{
            showProgress();
            RequestParams reqParam = new RequestParams();
            final String nickname = etNickname.getText().toString().trim();
            final String password = etPassword.getText().toString().trim();

            reqParam.putData("service", "register_member");
            reqParam.putData("mobile", strMobile);

            reqParam.putData("nick_name", nickname);
            reqParam.putData("login_pwd", password);
            reqParam.putData("msg_code", msgCode);
            reqParam.putData("token", "anonymous");


            HttpUtils.getInstance(this).excuteHttpRequest(HttpRequsetUri.getInstance().getMemberDoUri(), reqParam, new VFinResponseHandler() {


                @Override
                public void onSuccess(Object responseBean, String responseString) {
                    // TODO Auto-generated method stub
                    hideProgress();

                    Log.d(TAG, responseString);

                    Intent loginintent=new Intent(RegInfoDetailActivity.this,LoginActivity.class);  //方法1
                    startActivity(loginintent);


                    /*Intent reginfointent = new Intent(RegInfoDetailActivity.this, RegInfoDetailActivity.class);  //方法1
                    reginfointent.putExtra("mobile", strMobile);
                    startActivity(reginfointent);


                    hideProgress();
                    Log.d(TAG, responseString);


                    JSONObject json;
                    try {
                        json = new JSONObject(responseString);
                        String token = json.getString("token");
                        Log.d(TAG, token);
                        Intent walletintent=new Intent(LoginActivity.this, HomeActivity.class);  //方法1
                        walletintent.putExtra("token",token);
                        startActivity(walletintent);

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        showShortToast(e.getMessage());
                    }*/


                }





                @Override
                public void onError(String statusCode, String errorMsg) {
                    // TODO Auto-generated method stub
                    hideProgress();
                    showShortToast(errorMsg);

                    //Intent walletintent=new Intent(LoginActivity.this, HomeActivity.class);  //方法1
                    //startActivity(walletintent);
                }

            }, this);
        }
        /*String str = "---->" + count;
        textView.setText(str);
        count++;*/

    }

}
