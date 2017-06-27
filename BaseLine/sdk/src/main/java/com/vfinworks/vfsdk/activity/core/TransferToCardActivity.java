package com.vfinworks.vfsdk.activity.core;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vfinworks.vfsdk.R;
import com.vfinworks.vfsdk.SDKManager;
import com.vfinworks.vfsdk.activity.BaseActivity;
import com.vfinworks.vfsdk.business.GetLimit;
import com.vfinworks.vfsdk.common.Const;
import com.vfinworks.vfsdk.common.HttpRequsetUri;
import com.vfinworks.vfsdk.common.NoDoubleClickListener;
import com.vfinworks.vfsdk.common.SHA256Encrypt;
import com.vfinworks.vfsdk.common.Validator;
import com.vfinworks.vfsdk.context.LimitContext;
import com.vfinworks.vfsdk.context.TransferContext;
import com.vfinworks.vfsdk.model.UserDetailEntity;
import com.vfinworks.vfsdk.http.HttpUtils;
import com.vfinworks.vfsdk.http.RequestParams;
import com.vfinworks.vfsdk.http.VFinResponseHandler;
import com.vfinworks.vfsdk.model.AddNewBankModel;
import com.vfinworks.vfsdk.model.LimitModel;
import com.vfinworks.vfsdk.view.AddnewBankCardView;
import com.vfinworks.vfsdk.view.paypwd.PayPwdDialog;
import com.vfinworks.vfsdk.view.paypwd.PayPwdView;
import com.vfinworks.vfsdk.view.paypwd.VFEncryptData;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.List;

/**
 * 转账到银行卡
 */
public class TransferToCardActivity extends BaseActivity implements OnClickListener {
    private static final int GET_BANK = 100;
    private BaseActivity mContext=this;
    private TransferContext transferContext;
    private AddnewBankCardView layout_cardview;
    private EditText et_name;
    private RelativeLayout layout_real_name;
    private EditText et_money;

    private Button btn_done;
    private AddNewBankModel addNewBankModel;
    private TextView tv_btn_limit;
    private double inputMoney;
    private boolean turnOutFlag;
    private EditText et_remark;
    private String amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.transfer_to_card, FLAG_TITLE_LINEARLAYOUT);
        transferContext = (TransferContext) this.getIntent().getExtras().getSerializable("context");
        this.getTitlebarView().setTitle("转账到银行卡");
        this.getTitlebarView().initLeft(new OnClickListener() {
            @Override
            public void onClick(View v) {
                backOnClick();
            }
        });
        super.onCreate(savedInstanceState);
        queryMember();
    }

    private void bindViews() {
        layout_cardview = (AddnewBankCardView) findViewById(R.id.layout_cardview);
        layout_cardview.setToken(transferContext.getToken());
        et_name = (EditText) findViewById(R.id.et_name);
        et_money = (EditText) findViewById(R.id.et_money);
        et_remark = (EditText) findViewById(R.id.et_remark);
        btn_done = (Button) findViewById(R.id.btn_done);
        btn_done.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View view) {
                finishBtnClick();
            }
        });
        tv_btn_limit = (TextView) findViewById(R.id.tv_btn_limit);
        tv_btn_limit.setOnClickListener(this);

        et_money.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btn_done.setEnabled(et_money.getText().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
                String charText = s.toString();
                // 第一位不允许输入空格
                if (charText != null && charText.startsWith(" ")
                        || charText.startsWith(".")) {
                    s.delete(0, 1);
                    return;
                }
                // 最后一位不允许输入空格 并且限制长度为12位
                if ((charText != null && charText.endsWith(" ")) || (charText.length() > 12)) {
                    s.delete(charText.length() - 1, charText.length());
                    return;
                }
                // 如果输入的是数字,只允许输入小数点后两位
                int posDot = charText.indexOf(".");
                if (posDot <= 0)
                    return;
                if (charText.length() - posDot - 1 > 2) {
                    s.delete(posDot + 3, posDot + 4);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {}
        });
    }

    private void queryMember() {
        RequestParams reqParam = new RequestParams();
        reqParam.putData("service", "query_member");
        reqParam.putData("token", SDKManager.token);
        HttpUtils.getInstance(mContext).excuteHttpRequest(HttpRequsetUri.getInstance()
                        .getMemberDoUri(),
                reqParam, new VFinResponseHandler<UserDetailEntity>(UserDetailEntity.class) {
                    @Override
                    public void onSuccess(UserDetailEntity responseBean, String responseString) {
                        hideProgress();
                        turnOutFlag = responseBean.isTurnOut();
                        amount = responseBean.getAvaliable_balance();
                    }

                    @Override
                    public void onError(String statusCode, String errorMsg) {
                        hideProgress();
                        mContext.showShortToast(errorMsg);
                    }
                }, this);

    }

    @Override
    public void initWidget() {
        bindViews();
    }

    @Override
    public void onClick(View arg0) {
        if (arg0 == tv_btn_limit) {
            LimitContext limitContext = new LimitContext(transferContext);
            Intent intent = new Intent(this, LimitActivity.class);
            intent.putExtra("context", limitContext);
            startActivity(intent);
        }
    }

    private void finishBtnClick() {
        if (checkTextViewIsEmpty()) {
            LimitContext limitContext = new LimitContext(transferContext);
            GetLimit getLimit = new GetLimit(mContext, limitContext);
            showProgress();
            getLimit.doLimit(new GetLimit.LimitResponseHandler() {
                @Override
                public void onSuccess(List<LimitModel> results) {
                    if (results == null) {
                        doTransfer();
                    } else {
                        for (LimitModel bean : results) {
                            String msg = bean.checkLimit(inputMoney);
                            if (msg != null) {
                                onError("", msg);
                                return;
                            }
                        }
                        doTransfer();
                    }
                    hideProgress();
                }

                @Override
                public void onError(String statusCode, String errorMsg) {
                    showShortToast(errorMsg);
                    hideProgress();
                }
            });
        }
    }


    private void doTransfer() {
        final PayPwdDialog mDialog = new PayPwdDialog(this, transferContext);
        mDialog.setPayMoney(et_money.getText().toString());
        mDialog.setOnStatusChangeListener(new PayPwdView.OnStatusChangeListener() {
            @Override
            public void onInputComplete(VFEncryptData result) {
                checkPwd(transferContext.getToken(),result.getCiphertext());
                mDialog.dismiss();
            }

            @Override
            public void onUserCanel() {
                mDialog.dismiss();
            }

        });
        mDialog.show();
    }

    private void transfer2Card() {
        showProgress();
        RequestParams reqParam = new RequestParams();
        reqParam.putData("service", "payment_to_card");
        reqParam.putData("outer_trade_no", transferContext.getOutTradeNumber());
        reqParam.putData("amount", et_money.getText().toString().trim());
        reqParam.putData("token", SDKManager.token);
        reqParam.putData("bank_code", addNewBankModel.getBankModel().getBankCode());
        reqParam.putData("bank_name", addNewBankModel.getBankModel().getBankName());
        reqParam.putData("province", addNewBankModel.getProvinceInfoModel().getProvId() + "");
        reqParam.putData("city", addNewBankModel.getCityInfoModel().getCityId() + "");
        reqParam.putData("bank_branch", addNewBankModel.getBranchInfoModel().getId() + "");
        reqParam.putData("account_name", et_name.getText().toString().trim());
//        reqParam.putData("pay_pwd", SHA256Encrypt.bin2hex(ciphertext));
        reqParam.putData("bank_account_no", layout_cardview.getCardNumber());
        reqParam.putData("card_type", "DEBIT");//储蓄卡
        reqParam.putData("card_attribute", "C");//待确定
        reqParam.putData("notify_url", transferContext.getNotifyUrl());
        reqParam.putData("memo",et_remark.getText().toString().trim());
        HttpUtils.getInstance(this).excuteHttpRequest(HttpRequsetUri.getInstance().getAcquirerDoUri(),
                reqParam, new VFinResponseHandler() {

                    @Override
                    public void onSuccess(Object responseBean, String responseString) {
                        hideProgress();
                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("context", transferContext);
                        intent.putExtras(bundle);
                        intent.putExtra("statue",TradeResultActivity.SUCCESS);
                        intent.setClass(TransferToCardActivity.this, TradeResultActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onError(String statusCode, String errorMsg) {
                        hideProgress();
                        showShortToast(errorMsg);
                    }

                }, this);
    }

    /**
     * 检查用户是否输入了信息
     */
    private boolean checkTextViewIsEmpty() {
        if(!turnOutFlag){
            showShortToast("该账户已被冻结");
            return false;
        }
        if (addNewBankModel == null) {
            showShortToast("请选择省市");
            return false;
        }
        if(addNewBankModel.getType() == AddNewBankModel.PROVINCE_TYPE){
            showShortToast("请选择城市");
            return false;
        }
        if(addNewBankModel.getType() == AddNewBankModel.CITY_TYPE){
            showShortToast("请选择银行");
            return false;
        }
        if (addNewBankModel.getType() != AddNewBankModel.BRANCH_TYPE) {
            showShortToast("请选择支行");
            return false;
        }
        String cardNumber = layout_cardview.getCardNumber();
        String name = et_name.getText().toString().trim();
        String bank = addNewBankModel.getBankModel().getBankName().toString().trim();
        String etMoney = et_money.getText().toString().trim();

        if(!Validator.isName(name)){
            showShortToast("请输入正确的持卡人姓名");
            return false;
        }

        if (TextUtils.isEmpty(cardNumber)) {
            showShortToast("请输入银行卡号!");
            return false;
        }

        if (TextUtils.isEmpty(bank)) {
            showShortToast("请选择银行");
            return false;
        }
        if (cardNumber.length() < Const.MINIMUM_CARD_LENGTH
                || cardNumber.length() > Const.MAXIMUM_CARD_LENGTH) {
            showShortToast("请输入正确的银行卡号!");
            return false;
        }
        if (TextUtils.isEmpty(etMoney)) {
            showShortToast("请输入金额");
            return false;
        }
        try {
            inputMoney = Double.parseDouble(etMoney);
            if(inputMoney==0){
                showShortToast("请输入正确金额");
                return false;
            }
            et_money.setText(inputMoney + "");
        } catch (Exception e) {
            showShortToast(e.getMessage());
            return false;
        }
        if(new BigDecimal(amount).compareTo(new BigDecimal(inputMoney))==-1){
            showShortToast("余额不足");
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
        this.finishActivity();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            backOnClick();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void checkPwd(String token,String payPwd) {
        mContext.showProgress();
        RequestParams reqParam = new RequestParams();
        reqParam.putData("service", "verify_paypwd");
        reqParam.putData("token", SDKManager.token);
        reqParam.putData("out_trade_no", transferContext.getOutTradeNumber());
        reqParam.putData("pay_pwd", SHA256Encrypt.bin2hex(payPwd));

        HttpUtils.getInstance(mContext).excuteHttpRequest(HttpRequsetUri.getInstance().getMemberDoUri(),
                reqParam, new VFinResponseHandler() {

                    @Override
                    public void onSuccess(Object responseBean, String responseString) {
                        mContext.hideProgress();
                        JSONObject json;
                        try {
                            json = new JSONObject(responseString);
                            String isSuccess = json.getString("is_success");
                            //T代表验证成功
                            if (isSuccess.equalsIgnoreCase("T")) {
                                transfer2Card();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            mContext.showShortToast(e.getMessage());
                        }
                    }

                    @Override
                    public void onError(String statusCode, String errorMsg) {
                        mContext.hideProgress();
                        mContext.showShortToast(errorMsg);
                    }
                }, this);
    }
}
