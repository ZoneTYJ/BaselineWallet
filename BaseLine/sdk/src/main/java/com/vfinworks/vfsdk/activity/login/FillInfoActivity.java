package com.vfinworks.vfsdk.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.vfinworks.vfsdk.R;
import com.vfinworks.vfsdk.SDKManager;
import com.vfinworks.vfsdk.activity.BaseActivity;
import com.vfinworks.vfsdk.activity.core.WalletActivity;
import com.vfinworks.vfsdk.common.HttpRequsetUri;
import com.vfinworks.vfsdk.common.NoDoubleClickListener;
import com.vfinworks.vfsdk.common.SharedPreferenceUtil;
import com.vfinworks.vfsdk.common.Utils;
import com.vfinworks.vfsdk.common.Validator;
import com.vfinworks.vfsdk.context.BaseContext;
import com.vfinworks.vfsdk.enumtype.VFCallBackEnum;
import com.vfinworks.vfsdk.http.HttpUtils;
import com.vfinworks.vfsdk.http.RequestParams;
import com.vfinworks.vfsdk.http.VFinResponseHandler;
import com.vfinworks.vfsdk.model.VFSDKResultModel;

import java.util.Calendar;

import cn.qqtheme.framework.picker.DatePicker;
import cn.qqtheme.framework.picker.OptionPicker;

/**
 * 填写实名信息--注册时
 * Created by xiaoshengke on 2016/8/17.
 */
public class FillInfoActivity extends BaseActivity implements View.OnClickListener {
    public TextView tv_lab1;
    public EditText et_afi_name;
    public TextView tv_lb2;
    public TextView tv_card_type;
    public TextView tv_lab3;
    public EditText et_afi_number;
    public TextView tv_date;
    public CheckBox cb_time_live;
    public Button btn_next;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_info);
        bindViews();
    }

    private void bindViews() {
        token = SDKManager.token;
        tv_lab1 = (TextView) findViewById(R.id.tv_lab1);
        et_afi_name = (EditText) findViewById(R.id.et_afi_name);
        tv_lb2 = (TextView) findViewById(R.id.tv_lb2);
        tv_card_type = (TextView) findViewById(R.id.tv_card_type);
        tv_lab3 = (TextView) findViewById(R.id.tv_lab3);
        et_afi_number = (EditText) findViewById(R.id.et_afi_number);
        tv_date = (TextView) findViewById(R.id.tv_date);
        cb_time_live = (CheckBox) findViewById(R.id.cb_time_live);
        btn_next = (Button) findViewById(R.id.btn_next);

//        tv_card_type.setOnClickListener(this);
        tv_date.setOnClickListener(this);
        btn_next.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View view) {
                saveData();
            }
        });

        cb_time_live.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    tv_date.setTextColor(0xffcccccc);
                }else{
                    tv_date.setTextColor(0xff333333);
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        if(v == tv_date){
            Calendar now = Calendar.getInstance();
            DatePicker picker = new DatePicker(this);
            picker.setRange(now.get(Calendar.YEAR), 2050);
            picker.setAnimationStyle(R.style.Animation_CustomPopup);
            picker.setSelectedItem(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH));
            picker.setOnDatePickListener(new DatePicker.OnYearMonthDayPickListener() {
                @Override
                public void onDatePicked(String year, String month, String day) {
                    tv_date.setText(year + "-" + month + "-" + day);
                }
            });
            picker.show();
        }else if(v == tv_card_type){
            OptionPicker picker = new OptionPicker(this, new String[]{
                    "身份证", "护照", "驾驶证"
            });
            picker.setOffset(2);
            picker.setSelectedIndex(0);
            picker.setTextSize(18);
            picker.setOnOptionPickListener(new OptionPicker.OnOptionPickListener() {
                @Override
                public void onOptionPicked(int position, String option) {
                    tv_card_type.setText(option);
                }
            });
            picker.show();
        }
    }

    private void saveData(){
        String name = et_afi_name.getText().toString().trim();
        if(TextUtils.isEmpty(name)){
            showShortToast("真实姓名不能为空");
            return;
        }
        if(!Validator.isName(name)){
            showShortToast("姓名格式错误");
            return;
        }
        String number = et_afi_number.getText().toString().trim();
        if(TextUtils.isEmpty(number)){
            showShortToast("证件号不能为空");
            return;
        }
        String checkIdCard = Utils.getInstance().IDCardValidate(number);
        if(TextUtils.isEmpty(checkIdCard) == false) {
            this.showShortToast(checkIdCard);
            return;
        }
        showProgress();
        RequestParams reqParam = new RequestParams();
        reqParam.putData("token",  SDKManager.token);
        reqParam.putData("service", "do_certification");
        reqParam.putData("cert_type", "IC");
        reqParam.putData("cert_no", et_afi_number.getText().toString().trim());
        reqParam.putData("real_name", et_afi_name.getText().toString().trim());
        HttpUtils.getInstance(this).excuteHttpRequest(HttpRequsetUri.getInstance().getMemberDoUri(), reqParam, new VFinResponseHandler(){
            @Override
            public void onSuccess(Object responseBean, String responseString) {
                hideProgress();
                if(SharedPreferenceUtil.getInstance().getIntValueFromSP(LoginActivity.PAY_PWD_SET) == 0){
                    BaseContext baseContext = new BaseContext();
                    baseContext.setCallBackMessageId(setPaypwdcallbackMessageID);
                    baseContext.setToken(token);
                    SDKManager.getInstance().SetPayPassword(FillInfoActivity.this, baseContext, mHandler);
                }else{
                    startHomeActivity();
                }
            }

            @Override
            public void onError(String statusCode, String errorMsg) {
                hideProgress();
                showShortToast(errorMsg);
            }

        }, this);
    }

    @Override
    protected void onDestroy() {
        SDKManager.getInstance().clear();
        super.onDestroy();
    }

    private int setPaypwdcallbackMessageID = 1;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == setPaypwdcallbackMessageID) {
                handleSetPayPwdCallback((VFSDKResultModel) msg.obj);
            }
        }

        ;
    };

    private void handleSetPayPwdCallback(VFSDKResultModel resultModel) {
        if (resultModel.getResultCode().equalsIgnoreCase(VFCallBackEnum.OK.getCode()) == true) {
            Toast.makeText(this, "支付密码设置成功！", Toast.LENGTH_LONG).show();
            startHomeActivity();
        }
    }

    private void startHomeActivity() {
        Intent walletintent = new Intent(FillInfoActivity.this, WalletActivity.class);  //方法1
        walletintent.putExtra("token", token);
        startActivity(walletintent);
    }
}
