package com.vfinworks.vfsdk.activity.core;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.vfinworks.vfsdk.R;
import com.vfinworks.vfsdk.SDKManager;
import com.vfinworks.vfsdk.activity.BaseActivity;
import com.vfinworks.vfsdk.activity.PeopleInfo.CreateGesturePwdActivity;
import com.vfinworks.vfsdk.activity.PeopleInfo.GestureSettingActivity;
import com.vfinworks.vfsdk.activity.PeopleInfo.UnlockGesturePwdActivity;
import com.vfinworks.vfsdk.common.Config;
import com.vfinworks.vfsdk.common.SharedPreferenceUtil;
import com.vfinworks.vfsdk.context.BaseContext;
import com.vfinworks.vfsdk.enumtype.VFCallBackEnum;
import com.vfinworks.vfsdk.model.VFSDKResultModel;
import com.vfinworks.vfsdk.view.SlipButton;

/**
 * 安全设置界面
 */
public class SetSecurityActivity extends BaseActivity implements View.OnClickListener{
    public final int CHECK_GESTURE_PWD_REQUEST_CODE = 800;
    public int CHECK_GESTURE_PWD_WHEN_MODIFY_REQUEST_CODE = 801;
    private String token;

    private RelativeLayout lyCloseGesture,lyModifyGesture,lyModifyLoginPassword,lyModifyPayPassword,lyGetBackPassword;
    private SlipButton mSlipButton;

    private int modifyPaypwdcallbackMessageID = 1;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if(msg.what == modifyPaypwdcallbackMessageID) {
                handleSetPayPwdCallback((VFSDKResultModel)msg.obj);
            }
        };
    };

    private void handleSetPayPwdCallback(VFSDKResultModel resultModel) {
        if(resultModel.getResultCode().equalsIgnoreCase(VFCallBackEnum.OK.getCode()) == true) {
            Toast.makeText(this, "支付密码修改成功！", Toast.LENGTH_LONG).show();
            //startHomeActivity();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_set_security);
        super.onCreate(savedInstanceState);

        token = SDKManager.token;
        Log.d("SetSecurityActivity","token is "+token);

       // return view;
    }

    @Override
    public void initWidget() {
        /*ListView listView = (ListView) this.findViewById(R.id.listview);
        listAdapter = new ListAdapter();
        listView.setAdapter(listAdapter);*/



        lyCloseGesture = (RelativeLayout) this.findViewById(R.id.ly_close_gesture);
        lyModifyGesture = (RelativeLayout) this.findViewById(R.id.ly_modify_gesture);
        lyModifyLoginPassword = (RelativeLayout) this.findViewById(R.id.ly_modify_login_password);
        lyModifyPayPassword = (RelativeLayout) this.findViewById(R.id.ly_modify_pay_password);
        lyGetBackPassword = (RelativeLayout) this.findViewById(R.id.ly_get_back_password);
        mSlipButton = (SlipButton) this.findViewById(R.id.split_button);


        lyCloseGesture.setOnClickListener(this);
        lyModifyGesture.setOnClickListener(this);
        lyModifyLoginPassword.setOnClickListener(this);
        lyModifyPayPassword.setOnClickListener(this);
        lyGetBackPassword.setOnClickListener(this);
        if(SharedPreferenceUtil.getInstance().getBooleanValueFromSP("gesture_error",false)){
//            mSlipButton.setEnabled(false);
        }
        mSlipButton.setOnChangedListener(new SlipButton.OnChangedListener() {
            @Override
            public void OnChanged(boolean CheckState) {

                if(CheckState == true) {
                    startToGesturePwdActivity();
                }

                //关掉手势密码开关，需要验证旧手势密码，手势密码正确才能更新
                if(CheckState == false) {
                    checkGesturePwd(CHECK_GESTURE_PWD_REQUEST_CODE);
                }
            }
        });
    }

    /*
	 * 验证手势密码
	 */
    private void checkGesturePwd(int requestCode) {
        Intent intent = new Intent(this, UnlockGesturePwdActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean("check_gesture_password", true);
        intent.putExtras(bundle);
        startActivityForResult(intent, requestCode);
    }

    /**
     * 去设置手势密码
     */
    private void startToGesturePwdActivity() {
        Intent intent = new Intent(this, CreateGesturePwdActivity.class);
        intent.putExtra("flag", "");
        startActivity(intent);
    }

    /**
     * 是否开启了手势密码
     */
    private boolean isSetGesturePwd() {
        return SharedPreferenceUtil.getInstance().getBooleanValueFromSP(GestureSettingActivity.GESTURE_SWITCH, false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isSetGesturePwd()){
            mSlipButton.setCheck(true);
        }else{
            mSlipButton.setCheck(false);
        }
    }

    @Override
    public void onClick(View arg0) {
        if(arg0.getId() == R.id.ly_close_gesture) {

            Log.d("TAG", "ly_close_gesture is clicked");


        }else if(arg0.getId() == R.id.ly_modify_gesture) {
            //transferCardClick();
            Log.d("TAG", "ly_modify_gesture is clicked");
            if(!TextUtils.isEmpty(SharedPreferenceUtil.getInstance().getStringValueFromSP(GestureSettingActivity.GESTURE))) {
                checkGesturePwd(CHECK_GESTURE_PWD_WHEN_MODIFY_REQUEST_CODE);
            }else{
                showShortToast("请设置手势密码");
            }
        }
        else if(arg0.getId() == R.id.ly_modify_login_password)
        {
            Log.d("TAG", "ly_modify_login_password is clicked");




        }
        else if(arg0.getId() == R.id.ly_modify_pay_password)
        {
            Log.d("TAG", "ly_modify_pay_password is clicked");
            /*Intent setpayintent = new Intent(this, SetPaymentPwdActivity.class);  //方法1
            setpayintent.putExtra("type", strmobile);
            startActivity(setsecurityintent);


            Bundle bundle = new Bundle();
            bundle.putSerializable("context", baseContext);
            bundle.putSerializable("type", PaymentPwdEnum.SETPASSWORD);
            intent.putExtras(bundle);
            intent.setClass(context, SetPaymentPwdActivity.class);
            startActivityWithAnim(context, intent);
           */

            SDKManager.getInstance().setCallbackHandle(mHandler);
            BaseContext baseContext = new BaseContext();
            baseContext.setCallBackMessageId(modifyPaypwdcallbackMessageID);
            baseContext.setToken(token);
            SDKManager.getInstance().ModifyPayPassword(this, baseContext, mHandler);

            Log.d("TAG", "ly_my_service is clicked");
        }
        else if(arg0.getId() == R.id.ly_get_back_password)
        {
            Log.d("TAG", "ly_get_back_password is clicked");
        }
    }

    @Override
    protected void onDestroy() {
        SDKManager.getInstance().clear();
        super.onDestroy();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == CHECK_GESTURE_PWD_REQUEST_CODE) {
            if(resultCode == 10000) {
                SharedPreferenceUtil.getInstance().setBooleanDataIntoSP(GestureSettingActivity.GESTURE_SWITCH,false);
                SharedPreferenceUtil.getInstance().setStringDataIntoSP(GestureSettingActivity.GESTURE,"");
                showShortToast("手势密码已失效，请重新登录设置！");
                go2Login();
                finish();
            }else if(resultCode == RESULT_OK){
                if(data == null || data.getBooleanExtra("isCancel", false) == false) {//关闭
                    SharedPreferenceUtil.getInstance().setBooleanDataIntoSP(GestureSettingActivity.GESTURE_SWITCH, false);
                    mSlipButton.setCheck(false);
                }
            }
        }else if(requestCode == CHECK_GESTURE_PWD_WHEN_MODIFY_REQUEST_CODE) {
            if(resultCode == 10000) {
                SharedPreferenceUtil.getInstance().setBooleanDataIntoSP(GestureSettingActivity.GESTURE_SWITCH,false);
                SharedPreferenceUtil.getInstance().setStringDataIntoSP(GestureSettingActivity.GESTURE,"");
                showShortToast("手势密码已失效，请重新登录设置！");
                go2Login();
                finish();
            }else if(resultCode == RESULT_OK){

                //修改流程
                startToGesturePwdActivity();
            }
        }
    }

    private void go2Login(){
        try {

            Class clazz = Class.forName(Config.LOGIN_CLASS);
            Intent intent = new Intent();
            intent.setClass(this,clazz);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


}
