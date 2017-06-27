package com.vfinworks.vfsdk.activity.core;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.vfinworks.vfsdk.R;
import com.vfinworks.vfsdk.SDKManager;
import com.vfinworks.vfsdk.activity.BaseActivity;
import com.vfinworks.vfsdk.common.Const;
import com.vfinworks.vfsdk.common.HttpRequsetUri;
import com.vfinworks.vfsdk.common.NoDoubleClickListener;
import com.vfinworks.vfsdk.context.AddWithdrawCardContext;
import com.vfinworks.vfsdk.enumtype.VFCallBackEnum;
import com.vfinworks.vfsdk.http.HttpUtils;
import com.vfinworks.vfsdk.http.RequestParams;
import com.vfinworks.vfsdk.http.VFinResponseHandler;
import com.vfinworks.vfsdk.model.AddNewBankModel;
import com.vfinworks.vfsdk.model.RealNameInfoModel;
import com.vfinworks.vfsdk.model.VFSDKResultModel;
import com.vfinworks.vfsdk.view.AddnewBankCardView;

/**
 * 添加提现银行卡
 */
public class AddWithdrawCardActivity extends BaseActivity implements OnClickListener {

    private static final String TAG = AddWithdrawCardActivity.class.getSimpleName();

    private TextView etRealName;
    /**
     * 是否自动提现
     */
    private String realName = "";

    private AddWithdrawCardContext addWithdrawCardContext;

    private AddnewBankCardView layout_cardview;
    private AddNewBankModel addNewBankModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.add_withdraw_bank_withdraw, FLAG_TITLE_LINEARLAYOUT);
        addWithdrawCardContext = (AddWithdrawCardContext) this.getIntent().getExtras()
                .getSerializable("context");
        super.onCreate(savedInstanceState);
        this.getTitlebarView().setTitle("添加提现银行卡");
        this.getTitlebarView().initLeft(new OnClickListener() {
            @Override
            public void onClick(View v) {
                backOnClick();
            }
        });
    }

    @Override
    public void initWidget() {
        layout_cardview = (AddnewBankCardView) findViewById(R.id.layout_cardview);
        layout_cardview.setToken(addWithdrawCardContext.getToken());
        etRealName = (TextView) findViewById(R.id.tv_real_name);
        Button btnFinish = (Button) findViewById(R.id.btn_done);
        btnFinish.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View view) {
                finishBtnClick();
            }
        });
        getUserNameByToken();
    }

    private void getUserNameByToken() {
        showProgress();
        RequestParams reqParam = new RequestParams();
        reqParam.putData("token", SDKManager.token);
        reqParam.putData("service", "query_certification");
        HttpUtils.getInstance(this).excuteHttpRequest(HttpRequsetUri.getInstance().getMemberDoUri(),
                reqParam, new VFinResponseHandler<RealNameInfoModel>(RealNameInfoModel.class) {

                    @Override
                    public void onSuccess(RealNameInfoModel responseBean, String responseString) {
                        hideProgress();
                        realName = responseBean.getRealName();
                        if (TextUtils.isEmpty(realName)) {
                            setErrorCallBack("用户未实名认证");
                        }
                        etRealName.setText(realName);
                    }

                    @Override
                    public void onError(String statusCode, String errorMsg) {
                        hideProgress();
                        setErrorCallBack(errorMsg);
                    }

                }, this);

    }


    @Override
    public void onClick(View arg0) {

    }

    private void finishBtnClick() {
        if (checkTextViewIsEmpty()) {
            showProgress();
            RequestParams reqParam = new RequestParams();
            reqParam.putData("token", SDKManager.token);
            reqParam.putData("service", "create_bank_card");
            reqParam.putData("bank_code", addNewBankModel.getBankModel().getBankCode());
            reqParam.putData("bank_name", addNewBankModel.getBankModel().getBankName());
            reqParam.putData("bank_branch", addNewBankModel.getBranchInfoModel().getsName());
            //reqParam.putSecurityData("bank_account_no", etAccount.getText().toString().trim()
            // .replace(" ", ""));
            //reqParam.putSecurityData("account_name", realName);
            reqParam.putData("bank_account_no", layout_cardview.getCardNumber());
            reqParam.putData("account_name", realName);
            reqParam.putData("province", addNewBankModel.getProvinceInfoModel().getProvName());
            reqParam.putData("city", addNewBankModel.getCityInfoModel().getCityName());
            reqParam.putData("card_type", "DEBIT");//储蓄卡
            reqParam.putData("card_attribute", "C");//待确定
            reqParam.putData("is_auto", "F");
            HttpUtils.getInstance(this).excuteHttpRequest(HttpRequsetUri.getInstance()
                    .getMemberDoUri(), reqParam, new VFinResponseHandler() {

                @Override
                public void onSuccess(Object responseBean, String responseString) {
                    hideProgress();
                    //listCityData = responseBean.getCity();
                    //listAdapter.notifyDataSetChanged();
                    if (addWithdrawCardContext.isExternal()) {
                        AddWithdrawCardActivity.this.finishAll();
                    } else {
                        AddWithdrawCardActivity.this.finishActivity();
                    }
                    if (SDKManager.getInstance().getCallbackHandler() != null) {
                        VFSDKResultModel result = new VFSDKResultModel();
                        result.setResultCode(VFCallBackEnum.OK.getCode());
                        result.setMessage("添加提现银行卡成功！");
                        addWithdrawCardContext.sendMessage(result);
                    }
                }

                @Override
                public void onError(String statusCode, String errorMsg) {
                    hideProgress();
                    setErrorCallBack(errorMsg);
                }

            }, this);
        }
    }

    /**
     * 检查用户是否输入了信息
     */
    private boolean checkTextViewIsEmpty() {
        String cardNumber = layout_cardview.getCardNumber();
        if (addNewBankModel == null) {
            showShortToast("请选择省市");
            return false;
        }
        if (addNewBankModel.getType() == AddNewBankModel.PROVINCE_TYPE) {
            showShortToast("请选择城市");
            return false;
        }
        if (addNewBankModel.getType() == AddNewBankModel.CITY_TYPE) {
            showShortToast("请选择银行");
            return false;
        }
        if (addNewBankModel.getType() != AddNewBankModel.BRANCH_TYPE) {
            showShortToast("请选择支行");
            return false;
        }
        if (TextUtils.isEmpty(cardNumber)) {
            showShortToast("请输入银行卡号!");
            return false;
        }

        if (cardNumber.length() < Const.MINIMUM_CARD_LENGTH
                || cardNumber.length() > Const.MAXIMUM_CARD_LENGTH) {
            showShortToast("请输入正确的银行卡号!");
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AddnewBankCardView.BACK_CODE) {
            if (data != null) {
                addNewBankModel = (AddNewBankModel) data.getSerializableExtra("addNewBankModel");
                layout_cardview.setActivityBack(addNewBankModel);
            }
        }
    }

    private void backOnClick() {
        if (addWithdrawCardContext.isExternal() == true) {
            this.finishAll();
        } else {
            this.finishActivity();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            backOnClick();
        }
        return super.onKeyDown(keyCode, event);
    }


    private void setErrorCallBack(String errorMessage) {
        showShortToast(errorMessage);
        if (SDKManager.getInstance().getCallbackHandler() != null) {
            VFSDKResultModel result = new VFSDKResultModel();
            result.setResultCode(VFCallBackEnum.ERROR_CODE_BUSINESS.getCode());
            result.setMessage(errorMessage);
            addWithdrawCardContext.sendMessage(result);
        }
    }
}
