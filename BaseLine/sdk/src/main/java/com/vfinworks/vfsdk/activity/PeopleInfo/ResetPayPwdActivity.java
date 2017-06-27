package com.vfinworks.vfsdk.activity.PeopleInfo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vfinworks.vfsdk.R;
import com.vfinworks.vfsdk.SDKManager;
import com.vfinworks.vfsdk.activity.BaseActivity;
import com.vfinworks.vfsdk.common.SharedPreferenceUtil;
import com.vfinworks.vfsdk.common.StringReplaceUtil;
import com.vfinworks.vfsdk.context.BaseContext;
import com.vfinworks.vfsdk.enumtype.VFCallBackEnum;
import com.vfinworks.vfsdk.model.VFSDKResultModel;

/**
 * 重置支付密码
 * Created by xiaoshengke on 2016/9/1.
 */
public class ResetPayPwdActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_phone_tip;
    private RelativeLayout rl_remember;
    private RelativeLayout rl_forget;

    private int modifyPaypwdcallbackMessageID = 1;
    private int setPaypwdcallbackMessageID = 2;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if(msg.what == modifyPaypwdcallbackMessageID) {
                handleSetPayPwdCallback((VFSDKResultModel)msg.obj);
            }else if (msg.what == setPaypwdcallbackMessageID) {
                handleResetSetPayPwdCallback((VFSDKResultModel) msg.obj);
            }
        };
    };

    private void handleSetPayPwdCallback(VFSDKResultModel resultModel) {
        if(resultModel.getResultCode().equalsIgnoreCase(VFCallBackEnum.OK.getCode()) == true) {
            Toast.makeText(this, "支付密码修改成功！", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this,SettingActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    private void handleResetSetPayPwdCallback(VFSDKResultModel resultModel) {
        if (resultModel.getResultCode().equalsIgnoreCase(VFCallBackEnum.OK.getCode()) == true) {
            Toast.makeText(this, "支付密码设置成功！", Toast.LENGTH_LONG).show();
//            Intent intent = new Intent(this,SafeCheckActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pay_pwd, FLAG_TITLE_LINEARLAYOUT);
        getTitlebarView().setTitle("密码设置");
        bindViews();
    }

    private void bindViews() {
        tv_phone_tip = (TextView) findViewById(R.id.tv_phone_tip);
        rl_remember = (RelativeLayout) findViewById(R.id.rl_remember);
        rl_forget = (RelativeLayout) findViewById(R.id.rl_forget);

        tv_phone_tip.setText("你正在为"+ StringReplaceUtil.getStarString(SharedPreferenceUtil.getInstance().getStringValueFromSP("account"),3,7)+"重置支付密码");

        rl_remember.setOnClickListener(this);
        rl_forget.setOnClickListener(this);
    }



    @Override
    public void onClick(View v) {
        if(v == rl_remember){
            SDKManager.getInstance().setCallbackHandle(mHandler);
            BaseContext baseContext = new BaseContext();
            baseContext.setCallBackMessageId(modifyPaypwdcallbackMessageID);
            baseContext.setToken(SDKManager.token);
            SDKManager.getInstance().ModifyPayPassword(this, baseContext, mHandler);
        }else if(v == rl_forget){
//            startActivity(new Intent(this,SafeCheckActivity.class));

            BaseContext baseContext = new BaseContext();
            baseContext.setCallBackMessageId(setPaypwdcallbackMessageID);
            baseContext.setToken(SDKManager.token);
            SDKManager.getInstance().ResetPayPassword(this, baseContext, mHandler);
        }
    }

    @Override
    protected void onDestroy() {
        SDKManager.getInstance().clear();
        super.onDestroy();
    }
}
