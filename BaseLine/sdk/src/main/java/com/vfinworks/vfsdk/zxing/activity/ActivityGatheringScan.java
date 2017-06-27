package com.vfinworks.vfsdk.zxing.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.vfinworks.vfsdk.SDKManager;
import com.vfinworks.vfsdk.activity.BaseActivity;
import com.vfinworks.vfsdk.activity.core.TransferAccountConfirmActivity;
import com.vfinworks.vfsdk.activity.core.TransferAccountInputInfoActivity;
import com.vfinworks.vfsdk.activity.core.WalletActivity;
import com.vfinworks.vfsdk.authenticator.AuthenMain;
import com.vfinworks.vfsdk.common.HttpRequsetUri;
import com.vfinworks.vfsdk.common.Utils;
import com.vfinworks.vfsdk.context.TransferContext;
import com.vfinworks.vfsdk.http.HttpUtils;
import com.vfinworks.vfsdk.http.RequestParams;
import com.vfinworks.vfsdk.http.VFinResponseHandler;
import com.vfinworks.vfsdk.view.TipsDialog;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by tangyijian  付款扫设定金额
 * 扫一扫收款
 */
public class ActivityGatheringScan extends MipcaActivityCapture {
    /**
     * 二维码扫描intent code
     */
    private BaseActivity mContext = this;

    private String mResult;
    private String phone;
    private String amount;
    private String memo;
    private String mobile;
    private String code;
    private AuthenMain mAuthenMain;
    private boolean isTotp=false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mobile = getIntent().getStringExtra("mobile");
        this.getTitlebarView().setTitle("二维码/条形码");
        this.getTitlebarView().initLeft(new OnClickListener() {
            @Override
            public void onClick(View v) {
                backOnClick();
            }
        });
        mAuthenMain = new AuthenMain(mContext);
    }

    @Override
    protected void onResultHandler(String resultString, int nType, Bitmap bitmap) {
        if (TextUtils.isEmpty(resultString)) {
            Toast.makeText(this, "Scan failed!", Toast.LENGTH_SHORT).show();
            return;
        }
        mResult = resultString;
        scanResult(mResult);

    }

    /**
     * 获取信息,下订单
     *
     * @param result
     * @return
     */
    private void scanResult(String result) {
        JSONObject object;
        try {
            object = new JSONObject(result);
            code=object.optString("code");
            // phone = object.optString("payee");
            amount = object.optString("amount").trim();
            memo = object.optString("memo").trim();
            if (TextUtils.isEmpty(code)) {
                showDiatips("该二维码/条形码不是正规的付款码");
                return;
            }
        } catch (JSONException e) {
            showDiatips("该二维码/条形码不是正规的付款码");
            return;
        }
        if(Utils.checkEmail(code)){
            phone=code;
            if (phone.equals(mobile)) {
                showDiatips("不能转账给自己");
                return;
            }
            queryAccountInfo();
        }else {
            String[] strs = mAuthenMain.decodeQRCode(code);
            phone = strs[0];
            if (phone.equals(mobile)) {
                showDiatips("不能转账给自己");
                return;
            }
            if (isTotp) {
                validCode(strs[1]);
            } else {
                queryAccountInfo();
            }
        }
    }


    private void showDiatips(String str) {
        TipsDialog tipsDialog = new TipsDialog(this);
        tipsDialog.setOnCompleteListener(new TipsDialog.OnCompleteListener() {
            @Override
            public void onCancel() {
                handler.restartPreviewAndDecode();
            }
        });
        tipsDialog.setContent(str).show();
    }


    private void validCode(String code) {
        showProgress();
        RequestParams reqParam = new RequestParams();
        reqParam.putData("service", "verify_otp");
        reqParam.putData("phone", phone);
        reqParam.putData("number_pwd", code);
        reqParam.putData("token", SDKManager.token);
        HttpUtils.getInstance(this).excuteHttpRequest(HttpRequsetUri.getInstance().getMemberDoUri(),
                reqParam, new VFinResponseHandler() {

                    @Override
                    public void onSuccess(Object responseBean, String responseString) {
                        try {
                            JSONObject jsn = new JSONObject(responseString);
                            if(jsn.optString("is_success").equals("T")){
                                queryAccountInfo();
                            }else{
                                hideProgress();
                                showShortToast("二维码校验失败");
                                backOnClick();
                            }
                        } catch (JSONException e) {
                            showShortToast("查询异常");
                            backOnClick();
                        }

                    }

                    @Override
                    public void onError(String statusCode, String errorMsg) {
                        hideProgress();
                        showShortToast(errorMsg);
                        backOnClick();
                    }

                }, this);
    }
    private void queryAccountInfo() {
        RequestParams reqParam = new RequestParams();
        reqParam.putData("service", "query_name");
        reqParam.putData("token", SDKManager.token);
        reqParam.putData("identity_no", phone);
        HttpUtils.getInstance(this).excuteHttpRequest(HttpRequsetUri.getInstance().getMemberDoUri(),
                reqParam, new VFinResponseHandler() {

                    @Override
                    public void onSuccess(Object responseBean, String responseString) {
                        hideProgress();
                        try {
                            JSONObject jsn = new JSONObject(responseString);
                            String nickname = "";
                            String realName = "";
                            if (jsn.has("memberName")) {
                                nickname = jsn.optString("memberName");
                            }
                            if (jsn.has("realName")) {
                                realName = jsn.optString("realName");
                            }
                            btnNextClick(nickname, realName);
                        } catch (JSONException e) {
                            showShortToast("查询异常");
                            backOnClick();
                        }

                    }

                    @Override
                    public void onError(String statusCode, String errorMsg) {
                        hideProgress();
                        showShortToast(errorMsg);
                        backOnClick();
                    }

                }, this);
    }


    private void btnNextClick(String nickname, String realName) {
        TransferContext transferContext = new TransferContext();
        transferContext.setMobile(mobile);
        transferContext.setToken(SDKManager.token);
        transferContext.setCallBackMessageId(WalletActivity.transferCallbackMessageID);
        transferContext.setAvailableAmount(amount);
        transferContext.setOutTradeNumber(Utils.getOnlyValue());
        transferContext.setNotifyUrl("");
        transferContext.setMethod("account");
        if (TextUtils.isEmpty(amount)) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putSerializable("context", transferContext);
            bundle.putString("account_number", phone);
            bundle.putString("nickname", nickname);
            bundle.putString("realName", realName);
            intent.putExtras(bundle);
            intent.setClass(this, TransferAccountInputInfoActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putSerializable("context", transferContext);
            bundle.putString("account_number", phone);
            bundle.putString("nickname", nickname);
            bundle.putString("amount", amount);
            bundle.putString("remark", memo);
            intent.putExtras(bundle);
            intent.setClass(this, TransferAccountConfirmActivity.class);
            startActivity(intent);
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            backOnClick();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void backOnClick() {
        this.finishAll();
    }

}
