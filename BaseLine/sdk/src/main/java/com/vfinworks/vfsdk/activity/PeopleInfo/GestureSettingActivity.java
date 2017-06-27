package com.vfinworks.vfsdk.activity.PeopleInfo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vfinworks.vfsdk.R;
import com.vfinworks.vfsdk.activity.BaseActivity;
import com.vfinworks.vfsdk.common.Config;
import com.vfinworks.vfsdk.common.SharedPreferenceUtil;
import com.vfinworks.vfsdk.view.SlipButton;

/**
 * 手势设置
 * Created by xiaoshengke on 2016/9/18.
 */
public class GestureSettingActivity extends BaseActivity {
    public final int CHECK_GESTURE_PWD_REQUEST_CODE = 800;
    public int CHECK_GESTURE_PWD_WHEN_MODIFY_REQUEST_CODE = 801;
    
    public static final String GESTURE = "vf_gesture";
    public static final String GESTURE_SWITCH = "vf_gesture_switch";
    public static final String GESTURE_SHOW = "vf_gesture_show";

    private com.vfinworks.vfsdk.view.SlipButton sb_gesture_switch;
    private LinearLayout ll_gesture_container;
    private com.vfinworks.vfsdk.view.SlipButton sb_gesture_show;
    private TextView tv_modify_gesture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gesture_setting,FLAG_TITLE_LINEARLAYOUT);
        getTitlebarView().setTitle("手势设置");
        bindViews();
        initListener();
    }

    private void bindViews() {
        sb_gesture_switch = (com.vfinworks.vfsdk.view.SlipButton) findViewById(R.id.sb_gesture_switch);
        ll_gesture_container = (LinearLayout) findViewById(R.id.ll_gesture_container);
        sb_gesture_show = (com.vfinworks.vfsdk.view.SlipButton) findViewById(R.id.sb_gesture_show);
        tv_modify_gesture = (TextView) findViewById(R.id.tv_modify_gesture);
    }

    private void initListener() {
        //手势开关
        sb_gesture_switch.setOnChangedListener(new SlipButton.OnChangedListener() {
            @Override
            public void OnChanged(boolean CheckState) {
                if(CheckState){
                    startToGesturePwdActivity();
                }else{
                    checkGesturePwd(CHECK_GESTURE_PWD_REQUEST_CODE);
                }

            }
        });
        //显示手势轨迹
        sb_gesture_show.setOnChangedListener(new SlipButton.OnChangedListener() {
            @Override
            public void OnChanged(boolean CheckState) {
                if(CheckState){
                    SharedPreferenceUtil.getInstance().setBooleanDataIntoSP(GESTURE_SHOW,true);
                }else{
                    SharedPreferenceUtil.getInstance().setBooleanDataIntoSP(GESTURE_SHOW,false);
                }
            }
        });
        //修改手势密码
        tv_modify_gesture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isSetGesturePwd()) {
                    checkGesturePwd(CHECK_GESTURE_PWD_WHEN_MODIFY_REQUEST_CODE);
                }else{
                    showShortToast("请设置手势密码");
                }
            }
        });
    }

    /**
     * 去设置手势密码
     */
    private void startToGesturePwdActivity() {
        Intent intent = new Intent(this, CreateGesturePwdActivity.class);
        intent.putExtra("flag", "");
        startActivity(intent);
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

    @Override
    protected void onResume() {
        super.onResume();
        if(isSetGesturePwd()){
            sb_gesture_switch.setCheck(true);
            ll_gesture_container.setVisibility(View.VISIBLE);
        }else{
            sb_gesture_switch.setCheck(false);
            ll_gesture_container.setVisibility(View.GONE);
        }
        if(isShowGesturePwd()){
            sb_gesture_show.setCheck(true);
        }else{
            sb_gesture_show.setCheck(false);
        }
    }

    /**
     * 是否开启了手势密码
     */
    private boolean isSetGesturePwd() {
        return SharedPreferenceUtil.getInstance().getBooleanValueFromSP(GESTURE_SWITCH, false);
    }

    /**
     * 是否显示手势轨迹
     */
    private boolean isShowGesturePwd() {
        return SharedPreferenceUtil.getInstance().getBooleanValueFromSP(GESTURE_SHOW, false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == CHECK_GESTURE_PWD_REQUEST_CODE) {
            if(resultCode == UnlockGesturePwdActivity.UNLOCK_FAILURE) {
                SharedPreferenceUtil.getInstance().setBooleanDataIntoSP(GESTURE_SWITCH,false);
                SharedPreferenceUtil.getInstance().setBooleanDataIntoSP(GESTURE_SHOW, false);
                SharedPreferenceUtil.getInstance().setStringDataIntoSP(GESTURE,"");
                showShortToast("手势密码已失效，请重新登录设置！");
                go2Login();
                finish();
            }else if(resultCode == RESULT_OK){
                if(data == null || data.getBooleanExtra("isCancel", false) == false) {//关闭
                    SharedPreferenceUtil.getInstance().setBooleanDataIntoSP(GESTURE_SWITCH, false);
                    SharedPreferenceUtil.getInstance().setBooleanDataIntoSP(GESTURE_SHOW, false);
                    SharedPreferenceUtil.getInstance().setStringDataIntoSP(GESTURE,"");
                    sb_gesture_switch.setCheck(false);
                }
            }
        }else if(requestCode == CHECK_GESTURE_PWD_WHEN_MODIFY_REQUEST_CODE) {
            if(resultCode == UnlockGesturePwdActivity.UNLOCK_FAILURE) {
                SharedPreferenceUtil.getInstance().setBooleanDataIntoSP(GESTURE_SWITCH,false);
                SharedPreferenceUtil.getInstance().setBooleanDataIntoSP(GESTURE_SHOW, false);
                SharedPreferenceUtil.getInstance().setStringDataIntoSP(GESTURE,"");
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
