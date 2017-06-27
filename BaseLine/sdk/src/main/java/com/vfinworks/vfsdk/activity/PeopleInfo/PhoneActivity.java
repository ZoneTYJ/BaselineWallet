package com.vfinworks.vfsdk.activity.PeopleInfo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vfinworks.vfsdk.R;
import com.vfinworks.vfsdk.SDKManager;
import com.vfinworks.vfsdk.activity.BaseActivity;
import com.vfinworks.vfsdk.common.StringReplaceUtil;
import com.vfinworks.vfsdk.context.BaseContext;
import com.vfinworks.vfsdk.enumtype.VFCallBackEnum;
import com.vfinworks.vfsdk.model.VFSDKResultModel;

/**
 * 手机号
 * Created by xiaoshengke on 2016/11/4.
 */
public class PhoneActivity extends BaseActivity {
    private RelativeLayout rl_phone;
    private TextView tv_phone;
    private Button btn_change_phone;
    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone,FLAG_TITLE_LINEARLAYOUT);
        phone = getIntent().getStringExtra("phone");
        getTitlebarView().setTitle("手机号");
        bindViews();
    }

    private void bindViews() {

        rl_phone = (RelativeLayout) findViewById(R.id.rl_phone);
        tv_phone = (TextView) findViewById(R.id.tv_phone);
        btn_change_phone = (Button) findViewById(R.id.btn_change_phone);

        tv_phone.setText(StringReplaceUtil.getStarString(phone, 3, 7));

        btn_change_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //                startActivity(new Intent(PhoneActivity.this,ChangePhoneActivity
                // .class));
                changePhone();
            }
        });
    }

    private void changePhone() {
        BaseContext baseContext = new BaseContext();
        baseContext.setToken(SDKManager.token);
        SDKManager.getInstance().ChangeMobile(this,baseContext,mHandler);
    }

    @Override
    protected void onDestroy() {
        SDKManager.getInstance().clear();
        super.onDestroy();
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            VFSDKResultModel resultModel = (VFSDKResultModel)msg.obj;
            if(resultModel.getResultCode().equalsIgnoreCase(VFCallBackEnum.OK.getCode()) == true) {
                finish();
            }
        }
    };
}
