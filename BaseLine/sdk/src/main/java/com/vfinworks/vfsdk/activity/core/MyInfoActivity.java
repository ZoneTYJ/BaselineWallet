package com.vfinworks.vfsdk.activity.core;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.vfinworks.vfsdk.R;
import com.vfinworks.vfsdk.SDKManager;
import com.vfinworks.vfsdk.activity.BaseActivity;
import com.vfinworks.vfsdk.common.HttpRequsetUri;
import com.vfinworks.vfsdk.common.SharedPreferenceUtil;
import com.vfinworks.vfsdk.http.HttpUtils;
import com.vfinworks.vfsdk.http.RequestParams;
import com.vfinworks.vfsdk.http.VFinResponseHandler;
import com.vfinworks.vfsdk.model.UserInfoModel;

import org.json.JSONException;
import org.json.JSONObject;

public class MyInfoActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = MyInfoActivity.class.getSimpleName();

    private RelativeLayout lyAccountDetail,lyMy2Dcode,lyMyService,lySetup,lyAbout;
    private Button tvExitBotton;
    private BaseActivity mContext=this;
    private UserInfoModel mUserInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_info);

        lyAccountDetail = (RelativeLayout)findViewById(R.id.ly_accountdetail);
        lyMy2Dcode = (RelativeLayout)findViewById(R.id.ly_my_2Dcode);
        lySetup = (RelativeLayout)findViewById(R.id.ly_setup);
        lyMyService = (RelativeLayout)findViewById(R.id.ly_my_service);
        lyAbout = (RelativeLayout)findViewById(R.id.ly_about);
        tvExitBotton = (Button) findViewById(R.id.tv_exit_botton);

        mUserInfo= SDKManager.getInstance().getUserInfoModel();
        if(mUserInfo==null){
            showShortToast("用户数据获取失败");
            return;
        }

        lyAccountDetail.setOnClickListener(this);
        lyMy2Dcode.setOnClickListener(this);
        lyMyService.setOnClickListener(this);
        lySetup.setOnClickListener(this);
        lyAbout.setOnClickListener(this);
        tvExitBotton.setOnClickListener(this);
    }

    @Override
    public void onClick(View arg0) {
        if(arg0.getId() == R.id.ly_accountdetail) {
            Log.d("TAG", "ly_accountdetail is clicked");
        }else if(arg0.getId() == R.id.ly_my_2Dcode) {
            //transferCardClick();
            Log.d("TAG", "ly_my_2Dcode is clicked");
        }
        else if(arg0.getId() == R.id.ly_setup)
        {
            Log.d("TAG", "ly_setup is clicked");

            //paymentContext.setToken(((HomeActivity) getActivity()).getToken());

            Intent setsecurityintent = new Intent(mContext, SetSecurityActivity.class);  //方法1
            //setsecurityintent.putExtra("mobile", strmobile);
            setsecurityintent.putExtra("token", mUserInfo.getToken());
            Log.d("MyInfoActivity", "token is" + mUserInfo.getToken());
            startActivity(setsecurityintent);
        }
        else if(arg0.getId() == R.id.ly_my_service)
        {
            Log.d("TAG", "ly_my_service is clicked");
        }
        else if(arg0.getId() == R.id.ly_about)
        {
            Log.d("TAG", "ly_about is clicked");
//            startActivity(new Intent(getActivity(),AboutActivity.class));
        }
        else if(arg0 == tvExitBotton){
            SharedPreferenceUtil.getInstance().setStringDataIntoSP("token","");
            RequestParams rp = new RequestParams();
            rp.putData("server","logout_member");
            rp.putData("token", SDKManager.token);
            HttpUtils.getInstance(mContext).excuteHttpRequest(HttpRequsetUri.getInstance().getMemberDoUri(),
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
                            go2Login();
                        }

                        @Override
                        public void onError(String statusCode, String errorMsg) {
                            go2Login();
                        }
                    },this);
        }
    }

    public void go2Login(){
        finishAll();//跳出sdk
    }
}
