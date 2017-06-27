package com.vfinworks.vfsdk.activity.core;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vfinworks.vfsdk.R;
import com.vfinworks.vfsdk.SDKManager;
import com.vfinworks.vfsdk.activity.BaseActivity;
import com.vfinworks.vfsdk.business.GetLimit;
import com.vfinworks.vfsdk.common.BankCardUtlis;
import com.vfinworks.vfsdk.common.HttpRequsetUri;
import com.vfinworks.vfsdk.common.LocalDataUtils;
import com.vfinworks.vfsdk.common.NoDoubleClickListener;
import com.vfinworks.vfsdk.common.SHA256Encrypt;
import com.vfinworks.vfsdk.common.SharedPreferenceUtil;
import com.vfinworks.vfsdk.common.Utils;
import com.vfinworks.vfsdk.context.LimitContext;
import com.vfinworks.vfsdk.context.WithdrawContext;
import com.vfinworks.vfsdk.http.HttpUtils;
import com.vfinworks.vfsdk.http.RequestParams;
import com.vfinworks.vfsdk.http.VFinResponseHandler;
import com.vfinworks.vfsdk.model.BankCardModel;
import com.vfinworks.vfsdk.model.LimitModel;
import com.vfinworks.vfsdk.model.UserDetailEntity;
import com.vfinworks.vfsdk.view.paypwd.PayPwdDialog;
import com.vfinworks.vfsdk.view.paypwd.PayPwdView;
import com.vfinworks.vfsdk.view.paypwd.VFEncryptData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * 提现
 */
public class WithdrawActivity extends BaseActivity implements OnClickListener {
    /**
     * 银行卡信息
     */
    private RelativeLayout layoutSelectBank;
    private RelativeLayout lyBankInfo;
    private LinearLayout lyNoBankInfo;
    private TextView tvBankName;
    private TextView tvCardNumber;
    /**
     * 提现金额
     */
    private EditText etMoney;
    private Button btnWithdraw;

    private static final int SELECT_WITHDRAW_BANK = 100;

    private TextView tvAvailableMoney;
    private BaseActivity mContext = this;

    private WithdrawContext withdrawContext;
    private ImageView iv_bank_icon;
    private TextView tv_amount_limit;
    private double inputMoney;
    private String avaliable_balance;
    private boolean turnOutFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.withdraw_activity, FLAG_TITLE_LINEARLAYOUT);
        withdrawContext = (WithdrawContext) this.getIntent().getExtras().getSerializable("context");
        super.onCreate(savedInstanceState);
        this.getTitlebarView().setTitle("提现");
        this.getTitlebarView().initLeft(new OnClickListener() {
            @Override
            public void onClick(View v) {
                backOnClick();
            }
        });

    }

    @Override
    public void initWidget() {
        tv_amount_limit = (TextView) findViewById(R.id.tv_amount_limit);
        layoutSelectBank = (RelativeLayout) findViewById(R.id.layout_select_bank);
        layoutSelectBank.setOnClickListener(this);
        lyBankInfo = (RelativeLayout) findViewById(R.id.ly_bank);
        lyNoBankInfo = (LinearLayout) findViewById(R.id.ly_no_select_bank);
        iv_bank_icon = (ImageView) findViewById(R.id.iv_bank_icon);
        tvBankName = (TextView) findViewById(R.id.bank_name);
        tvCardNumber = (TextView) findViewById(R.id.bank_account);
        tvAvailableMoney = (TextView) findViewById(R.id.tv_available_amount);
        String newMessageInfo = "可提现金额：<font color='red'><b>" + withdrawContext
                .getAvailableAmount() + "</b></font>元";
        tvAvailableMoney.setText(Html.fromHtml(newMessageInfo));
        etMoney = (EditText) findViewById(R.id.et_money);
        btnWithdraw = (Button) findViewById(R.id.btn_withdraw);
        btnWithdraw.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View view) {
                withdrawBtnClick();
            }
        });
        initEdit();
        showBankInfo();
        netWorkGetLimit(false);
        getUserAmount();
    }


    private void getUserAmount(){
        RequestParams reqParam = new RequestParams();
        reqParam.putData("service", "query_member");
        reqParam.putData("token", SDKManager.token);
        HttpUtils.getInstance(mContext).excuteHttpRequest(HttpRequsetUri.getInstance()
                        .getMemberDoUri(),
                reqParam, new VFinResponseHandler<UserDetailEntity>(UserDetailEntity.class) {
                    @Override
                    public void onSuccess(UserDetailEntity responseBean, String responseString) {
                        avaliable_balance = responseBean.getAvaliable_balance();
                        turnOutFlag = responseBean.isTurnOut();
                    }

                    @Override
                    public void onError(String statusCode, String errorMsg) {
                        hideProgress();
                        showShortToast(errorMsg);
                    }
                }, this);
    }

    private void netWorkGetLimit(final boolean checkFlag) {
        LimitContext limitContext = new LimitContext(withdrawContext);
        GetLimit getLimit = new GetLimit(mContext, limitContext);
        showProgress();
        getLimit.doLimit(new GetLimit.LimitResponseHandler() {
            @Override
            public void onSuccess(List<LimitModel> results) {
                String text = "";
                if (results != null) {
                    StringBuffer sb = new StringBuffer();
                    for (LimitModel bean : results) {
                        if (bean.getRangType().equals(LimitModel.SINGLE)) {
                            sb.append("每笔限额" + bean.getTotalLimitedValue() + "元，");
                        } else if (bean.getLimitedType().equals(LimitModel.TIMES) && bean
                                .getTimeRangeType().equals(LimitModel.DAY)) {
                            sb.append("本日还可转出" + bean.getLimitedValue() + "次，");
                        } else if (bean.getLimitedType().equals(LimitModel.QUOTA)) {
                            if (bean.getTimeRangeType().equals(LimitModel.DAY)) {
                                sb.append("本日还可转出" + bean.getLimitedValue() + "元，");
                            }
                        }
                        if (checkFlag) { //做check校验
                            if (!turnOutFlag) {
                                showShortToast("该账户冻结");
                                return;
                            }
                            String msg = bean.checkLimit(inputMoney);
                            if (msg != null) {
                                onError("", msg);
                                return;
                            }
                        }
                    }
                    text = sb.replace(sb.length() - 1, sb.length(), "。").toString();
                }
                tv_amount_limit.setText(text);
                hideProgress();
                if (checkFlag) { //校验成功后做提现操作
                    showPwdDialog();
                }
            }

            @Override
            public void onError(String statusCode, String errorMsg) {
                showShortToast(errorMsg);
                hideProgress();
            }
        });
    }

    private void showBankInfo() {
        String bankId = LocalDataUtils.getInstance().getLastPayBank(withdrawContext.getMobile() + "withdraw_bank_id");
        if (TextUtils.isEmpty(bankId)) {
            lyBankInfo.setVisibility(View.GONE);
            lyNoBankInfo.setVisibility(View.VISIBLE);
        } else {
            lyBankInfo.setVisibility(View.VISIBLE);
            lyNoBankInfo.setVisibility(View.GONE);
            String bankname = LocalDataUtils.getInstance().getLastPayBank
                    (withdrawContext.getMobile() + "withdraw_bank_name");
            tvBankName.setText(bankname);
            tvCardNumber.setText(SharedPreferenceUtil.getInstance().getStringValueFromSP
                    (withdrawContext.getMobile() + "withdraw_bank_number"));
            iv_bank_icon.setImageResource(Utils.getInstance().getBankDrawableIcon(BankCardUtlis
                    .getBankCodeOfName(bankname)));
        }
    }

    private void initEdit() {
        etMoney.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnWithdraw.setEnabled(etMoney.getText().length() > 0);
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
                if((charText != null && charText.endsWith(" ")) ||(charText.length()>12)) {
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
                                          int arg2, int arg3) {
            }
        });
    }

    @Override
    public void onClick(View arg0) {
        if (arg0.getId() == R.id.layout_select_bank) {
            selectBankClick();
        }
    }

    private void withdrawBtnClick() {
        if (checkParams()) {
            netWorkGetLimit(true);
        }
    }

    private void showPwdDialog() {
        final PayPwdDialog mDialog = new PayPwdDialog(this, withdrawContext);
        mDialog.setPayMoney(etMoney.getText().toString());
        mDialog.setOnStatusChangeListener(new PayPwdView.OnStatusChangeListener() {
            @Override
            public void onInputComplete(VFEncryptData result) {
                checkPwd(withdrawContext.getToken(),result.getCiphertext());
                mDialog.dismiss();
            }

            @Override
            public void onUserCanel() {
                mDialog.dismiss();
            }

        });
        mDialog.show();
    }

    private void withdrawPro() {
        showProgress();
        RequestParams reqParam = new RequestParams();
        reqParam.putData("service", "do_withdraw");
        reqParam.putData("token", SDKManager.token);
        reqParam.putData("outer_trade_no", withdrawContext.getOutTradeNumber());
        reqParam.putData("bank_card_id", SharedPreferenceUtil.getInstance().getStringValueFromSP
                (withdrawContext.getMobile()+"withdraw_bank_id"));
        reqParam.putData("amount", etMoney.getText().toString());
//        reqParam.putData("pay_pwd", SHA256Encrypt.bin2hex(payPwd));
        reqParam.putData("notify_url", withdrawContext.getNotifyUrl());
        HttpUtils.getInstance(this).excuteHttpRequest(HttpRequsetUri.getInstance().getAcquirerDoUri(),
                reqParam, new VFinResponseHandler() {

                    @Override
                    public void onSuccess(Object responseBean, String responseString) {
                        hideProgress();
                        //WithdrawActivity.this.showLongToast("提现成功！");
                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("context", withdrawContext);
                        intent.putExtras(bundle);
                        intent.putExtra(WalletActivity.MONEY_CHANGE, "-" + etMoney.getText()
                                .toString()
                                .trim());
                        intent.putExtra("statue",TradeResultActivity.SUCCESS);
                        intent.setClass(WithdrawActivity.this, TradeResultActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onError(String statusCode, String errorMsg) {
                        hideProgress();
                        showShortToast(errorMsg);
                    }

                }, this);
    }

    private boolean checkParams() {
        if (TextUtils.isEmpty(etMoney.getText().toString().trim())) {
            showShortToast("请输入金额");
            return false;
        }
        String bankId = LocalDataUtils.getInstance().getLastPayBank(withdrawContext.getMobile() + "withdraw_bank_id");
        if (TextUtils.isEmpty(bankId)) {
            showShortToast("请选择银行卡");
            return false;
        }
        try {
            inputMoney = Double.parseDouble(etMoney.getText().toString().trim());
            double overage= Double.parseDouble(avaliable_balance);
            etMoney.setText(inputMoney + "");
            etMoney.setSelection((inputMoney+"").length());
            if(inputMoney==0){
                showShortToast("输入金额必须大于0");
                return false;
            }
            if(inputMoney>overage){
                showShortToast("余额不足");
                return false;
            }
        } catch (Exception e) {
            showShortToast(e.getMessage());
            return false;
        }
        return true;
    }

    private void selectBankClick() {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("context", withdrawContext);
        intent.putExtras(bundle);
        intent.setClass(this, WithdrawSelectBankActivity.class);
        startActivityForResult(intent, SELECT_WITHDRAW_BANK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == SELECT_WITHDRAW_BANK) {
            BankCardModel withdrawBankCard = (BankCardModel) data.getExtras().getParcelable
                    ("select_bank");
            LocalDataUtils.getInstance().setLastPayBank(withdrawContext.getMobile() +
                    "withdraw_bank_id", withdrawBankCard.getBankcardId());
            LocalDataUtils.getInstance().setLastPayBank(withdrawContext.getMobile() +
                    "withdraw_bank_name", withdrawBankCard.getBankName());
            LocalDataUtils.getInstance().setLastPayBank(withdrawContext.getMobile() +
                    "withdraw_bank_number", withdrawBankCard.getCardNo());
            showBankInfo();
        }
    }

    private void backOnClick() {
        this.finishAll();
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
        reqParam.putData("out_trade_no", withdrawContext.getOutTradeNumber());
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
                                withdrawPro();
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
