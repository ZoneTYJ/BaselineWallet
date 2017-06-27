package com.vfinworks.vfsdk.activity.login;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.os.SystemClock;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.vfinworks.vfsdk.R;
import com.vfinworks.vfsdk.SDKManager;
import com.vfinworks.vfsdk.Utils.RSA;
import com.vfinworks.vfsdk.activity.BaseActivity;
import com.vfinworks.vfsdk.activity.PeopleInfo.ForgetLoginPasswordActivity;
import com.vfinworks.vfsdk.activity.PeopleInfo.GestureSettingActivity;
import com.vfinworks.vfsdk.activity.core.WalletActivity;
import com.vfinworks.vfsdk.common.BPUtil;
import com.vfinworks.vfsdk.common.Config;
import com.vfinworks.vfsdk.common.HttpRequsetUri;
import com.vfinworks.vfsdk.common.L;
import com.vfinworks.vfsdk.common.SharedPreferenceUtil;
import com.vfinworks.vfsdk.common.Validator;
import com.vfinworks.vfsdk.context.BaseContext;
import com.vfinworks.vfsdk.db.KeyDatabaseHelper;
import com.vfinworks.vfsdk.enumtype.VFCallBackEnum;
import com.vfinworks.vfsdk.http.HttpUtils;
import com.vfinworks.vfsdk.http.RequestParams;
import com.vfinworks.vfsdk.http.VolleyManager;
import com.vfinworks.vfsdk.http.volley.AuthFailureError;
import com.vfinworks.vfsdk.http.volley.DefaultRetryPolicy;
import com.vfinworks.vfsdk.http.volley.NetworkError;
import com.vfinworks.vfsdk.http.volley.NoConnectionError;
import com.vfinworks.vfsdk.http.volley.Request;
import com.vfinworks.vfsdk.http.volley.Response;
import com.vfinworks.vfsdk.http.volley.ServerError;
import com.vfinworks.vfsdk.http.volley.TimeoutError;
import com.vfinworks.vfsdk.http.volley.VolleyError;
import com.vfinworks.vfsdk.http.volley.toolbox.StringRequest;
import com.vfinworks.vfsdk.model.KeyModel;
import com.vfinworks.vfsdk.model.TokenModel;
import com.vfinworks.vfsdk.model.VFSDKResultModel;

import org.apaches.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import pub.devrel.easypermissions.EasyPermissions;



public class LoginActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks, View.OnClickListener {
    public static final String ACCOUNT = "account";
    public static final String REAL_STATE = "real_state";
    public static final String PAY_PWD_SET = "pay_pwd_set";
    private EditText tvUserName;
    private TextView tvForgetPwd;
    private EditText tvPwd;
    private EditText etCode;
    private RelativeLayout rl_code;
    private ImageView iv_code;
    private static final String TAG = LoginActivity.class.getSimpleName();

    private String token,refresh_token;
    private String password;

    private String avaliableBalance = "0.00";

    private int setPaypwdcallbackMessageID = 1;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == setPaypwdcallbackMessageID) {
                handleSetPayPwdCallback((VFSDKResultModel) msg.obj);
            }
        }

        ;
    };
    private String mNick_name;
    private String verifyCode;

    private void handleSetPayPwdCallback(VFSDKResultModel resultModel) {
        if (resultModel.getResultCode().equalsIgnoreCase(VFCallBackEnum.OK.getCode()) == true) {
            Toast.makeText(this, "支付密码设置成功！", Toast.LENGTH_LONG).show();
            startWalletActivity();
        }
    }

    private boolean closeGesture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setIsPush(false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        closeGesture = getIntent().getBooleanExtra("close_gesture",false);
        tvUserName = (EditText) findViewById(R.id.tv_usernameedit);
        tvForgetPwd = (TextView) findViewById(R.id.tv_forget_pwd);
        rl_code = (RelativeLayout) findViewById(R.id.rl_code);
        iv_code = (ImageView) findViewById(R.id.iv_code);
        tvPwd = (EditText) findViewById(R.id.tv_pwdeedit);
        etCode = (EditText) findViewById(R.id.et_code);
        String account = SharedPreferenceUtil.getInstance().getStringValueFromSP(ACCOUNT);
        if (!TextUtils.isEmpty(account)) {
            tvUserName.setText(account);
        }

        tvPwd.setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if(Pattern.matches("^[\u4e00-\u9fa5]*$",source)){//过滤掉中文输入
                    return "";
                }
                int keep = 20 - (dest.length() - (dend - dstart));
                if (keep <= 0) {
                    return "";
                } else if (keep >= end - start) {
                    return null; // keep original
                } else {
                    keep += start;
                    if (Character.isHighSurrogate(source.charAt(keep - 1))) {
                        --keep;
                        if (keep == start) {
                            return "";
                        }
                    }
                    return source.subSequence(start, keep);
                }
            }
        }});

        tvForgetPwd.setOnClickListener(this);

        tvUserName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    if(!Validator.isMobile(tvUserName.getText().toString().trim())){
                        showShortToast("手机号输入有误，请重新输入");
                    }
                }
            }
        });
    }

    /**
     * 检查用户是否输入了信息
     */
    private boolean checkTextViewIsEmpty() {
        if (TextUtils.isEmpty(tvUserName.getText().toString().trim())) {
            showShortToast("账号不能为空");
            return false;
        }

        if (TextUtils.isEmpty(tvPwd.getText().toString().trim())) {
            showShortToast("密码不能为空");
            return false;
        }
        if(!Validator.isMobile(tvUserName.getText().toString().trim())){
            showShortToast("手机号输入有误，请重新输入");
            return false;
        }
        if(!Validator.isLoginPasswordLegal(tvPwd.getText().toString().trim())){
            showShortToast("登录密码必须是6~20位数字、字母、符号的组合!");
            return false;
        }
        if(rl_code.getVisibility() == View.VISIBLE){
            if(TextUtils.isEmpty(etCode.getText().toString().trim())){
                showShortToast("验证码不能为空");
                return false;
            }
            if(!verifyCode.equals(etCode.getText().toString().trim())){
                showShortToast("验证码错误，请重新输入");
                return false;
            }
        }
        return true;
    }

    public final int MIN_CLICK_DELAY_TIME = 1000;
    private long lastClickTime = 0;

    public void login_Click(View view) {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.READ_PHONE_STATE)) {
            // Have permission, do the thing!
//            doLogin();
            long currentTime = Calendar.getInstance().getTimeInMillis();
            if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
                lastClickTime = currentTime;
                if(checkTextViewIsEmpty())
                    actLogin();
            }

        } else {
            // Ask for one permission
            EasyPermissions.requestPermissions(this, "读取设备信息方便使用后续服务",
                    110, Manifest.permission.READ_PHONE_STATE);
        }
    }

    private void actLogin(){
        showProgress();
        password = tvPwd.getText().toString().trim();
        final RequestParams reqParam = new RequestParams();
        reqParam.putData("service", "login_member");
        reqParam.putData("mobile", tvUserName.getText().toString().trim());
        reqParam.putData("login_pwd", tvPwd.getText().toString().trim());
        reqParam.putData("device_id", Config.getInstance().getDeviceId());
        reqParam.putData("device_name", Config.getInstance().getDeviceName());
        if(!TextUtils.isEmpty(verifyCode))
            reqParam.putData("verify_code", verifyCode);
        StringRequest sr = new StringRequest(Request.Method.POST,HttpRequsetUri.getInstance()
                .getMemberDoUri(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideProgress();
                L.d("response", response);
//                L.e("login-response", SystemClock.currentThreadTimeMillis()+"");
                SharedPreferenceUtil.getInstance().setStringDataIntoSP(ACCOUNT, tvUserName
                            .getText().toString().trim());
                try {
                    JSONObject jsonObj = new JSONObject(response);
                    String strResult = jsonObj.getString("is_success");
                    if("T".equalsIgnoreCase(strResult) == false) {
                        String strErrCode="";
                        if(!jsonObj.isNull("error_code")){
                            strErrCode=jsonObj.getString("error_code");
                        }
                        String strErrMessage =  jsonObj.getString("error_message");
                        showShortToast(strErrMessage);
                        if(jsonObj.has("verifyCode")){
                            verifyCode = jsonObj.optString("verifyCode");
                            rl_code.setVisibility(View.VISIBLE);
                            showCode();
                        }else{
                            rl_code.setVisibility(View.GONE);
                        }
                        return;
                    }else{
                        if(closeGesture){
                            SharedPreferenceUtil.getInstance().setBooleanDataIntoSP(GestureSettingActivity.GESTURE_SWITCH,false);
                            SharedPreferenceUtil.getInstance().setBooleanDataIntoSP(GestureSettingActivity.GESTURE_SHOW, false);
                            SharedPreferenceUtil.getInstance().setStringDataIntoSP(GestureSettingActivity.GESTURE,"");
                        }
//                        savePwd();
                        rl_code.setVisibility(View.GONE);
                        token = jsonObj.getString("token");
                        refresh_token = jsonObj.getString("refresh_token");
                        saveSomething();
//                        saveToken();
                        avaliableBalance = jsonObj.getString("avaliable_balance");
                        mNick_name = jsonObj.getString("nick_name");
                        String real_name_status = jsonObj.getString("real_name_status");
                        SharedPreferenceUtil.getInstance().setStringDataIntoSP(REAL_STATE,
                                real_name_status);
                        int payPwdFlag = jsonObj.getInt("pay_pwd_status");
                        SharedPreferenceUtil.getInstance().setIntDataIntoSP(PAY_PWD_SET,
                                payPwdFlag);

                        if ("notCertifition".equals(real_name_status)) {
                            startActivity(new Intent(LoginActivity.this, FillInfoActivity.class));
                        } else {
                            guideSetPayPassword(payPwdFlag == 1 ? true : false);
                        }
                        //获取服务器时间 计算开机时间戳并且保存
                        String strTime = jsonObj.getString("time");
                        SharedPreferenceUtil.getInstance().setLongDataIntoSP(SharedPreferenceUtil
                                .TIME, Long.parseLong(strTime) - SystemClock.elapsedRealtime());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    showShortToast("JSON解析异常");
                    return;
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgress();
                String statusCode = "SYSTEM_ERROR";
                String errMsg = "服务器内部错误";
                if(error instanceof TimeoutError) {
                    statusCode = "TIMEOUT_ERROR";
                    errMsg = "请求超时";
                }else if(error instanceof NoConnectionError){
                    statusCode = "NO_CONNECTION";
                    errMsg = "网络异常";
                }else if(error instanceof NetworkError){
                    statusCode = "NETWORK_ERROR";
                    errMsg = "网络异常";
                }else if(error instanceof ServerError){
                    statusCode = "SERVER_ERROR";
                    errMsg = "服务器错误";
                }else if(error instanceof AuthFailureError){
                    statusCode = "AUTO_FAILURE_ERROR";
                    errMsg = "认证失败";
                }
                showShortToast(errMsg);
            }
        }){
            //POST 参数
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                if(reqParam != null)
                    params = reqParam.getParams();
                //params.put("service",reqParam.getParams());
                return params;
            }
        };
        sr.setRetryPolicy(
                new DefaultRetryPolicy(
                        5000,//默认超时时间，应设置一个稍微大点儿的，例如本处的500000
                        0,//默认最大尝试次数
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );

        VolleyManager.getInstance(LoginActivity.this).addToRequestQueue(sr);
    }

    @Override
    protected void onStop() {
        super.onStop();
//        L.e("login-onstop", SystemClock.currentThreadTimeMillis()+"");
    }

    private void saveSomething(){
        SDKManager.token = token;
        new Thread(new Runnable() {
            @Override
            public void run() {
                Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
//                savePwd();
                saveToken();
            }
        }).start();
    }
    private void savePwd() {
        KeyModel keyModel = KeyDatabaseHelper.getInstance(this).queryKey();
        try {
            SharedPreferenceUtil.getInstance().setStringDataIntoSP("pwd", new String(Base64.encodeBase64(RSA.encryptByPublicKey(password.getBytes(),keyModel.publicKey))));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showCode() {
        BPUtil.getInstance().setCode(verifyCode);
        iv_code.setImageBitmap(BPUtil.getInstance().createBitmap());
    }

    private void guideSetPayPassword(boolean isSetPayPwd) {
        if(SDKManager.getInstance().getCallbackHandler() != null) {
            TokenModel tokenModel = new TokenModel();
            tokenModel.token = token;
            tokenModel.isSetPayPwd = isSetPayPwd;
            String jsonString = new Gson().toJson(tokenModel);
            BaseContext baseContext = new BaseContext();
            VFSDKResultModel result = new VFSDKResultModel();
            result.setResultCode(VFCallBackEnum.OK.getCode());
            result.setJsonData(jsonString);
            baseContext.sendMessage(result);
            SDKManager.getInstance().setCallbackHandle(null);
        }
        if (isSetPayPwd == false) {
            BaseContext baseContext = new BaseContext();
            baseContext.setCallBackMessageId(setPaypwdcallbackMessageID);
            baseContext.setToken(token);
            SDKManager.getInstance().SetPayPassword(this, baseContext, mHandler);
        } else {
            startWalletActivity();
        }
    }

    private void startWalletActivity() {
        Intent walletintent = new Intent(LoginActivity.this, WalletActivity.class);  //方法1
        walletintent.putExtra("token", token);
//        walletintent.putExtra("nickname", mNick_name);
        startActivity(walletintent);
//        startActivity(new Intent(this, UserInfoActivity.class));
    }

    private void saveToken() {
        HttpUtils.getInstance(this).saveToken(token,refresh_token);
        SDKManager.token = token;
    }


    public void reg_Click(View view) {
        Intent regintent = new Intent(LoginActivity.this, RegActivity.class);
        startActivity(regintent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
//        doLogin();
        actLogin();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        finish();
    }

    @Override
    public void onClick(View v) {
        startActivity(new Intent(this, ForgetLoginPasswordActivity.class));
    }
}
