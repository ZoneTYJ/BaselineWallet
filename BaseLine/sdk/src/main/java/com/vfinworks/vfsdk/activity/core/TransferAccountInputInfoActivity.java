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
import android.widget.TextView;

import com.vfinworks.vfsdk.R;
import com.vfinworks.vfsdk.SDKManager;
import com.vfinworks.vfsdk.activity.BaseActivity;
import com.vfinworks.vfsdk.business.GetLimit;
import com.vfinworks.vfsdk.common.HttpRequsetUri;
import com.vfinworks.vfsdk.common.NoDoubleClickListener;
import com.vfinworks.vfsdk.context.LimitContext;
import com.vfinworks.vfsdk.context.TransferContext;
import com.vfinworks.vfsdk.model.UserDetailEntity;
import com.vfinworks.vfsdk.http.HttpUtils;
import com.vfinworks.vfsdk.http.RequestParams;
import com.vfinworks.vfsdk.http.VFinResponseHandler;
import com.vfinworks.vfsdk.model.LimitModel;

import java.math.BigDecimal;
import java.util.List;


public class TransferAccountInputInfoActivity extends BaseActivity implements OnClickListener {
    private EditText etMoney;
    private Button btnConfirm;
    private EditText etRemark;

    private TransferContext transferContext;
    private String accountNum;
    private String nickname, realName;
    private BaseActivity mContext = this;
    private List<LimitModel> list;
    private boolean turnOutFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.transfer_account_input_info_activity, FLAG_TITLE_LINEARLAYOUT);
        transferContext = (TransferContext) this.getIntent().getExtras().getSerializable("context");
        accountNum = this.getIntent().getStringExtra("account_number");
        nickname = this.getIntent().getStringExtra("nickname");
        realName = this.getIntent().getStringExtra("realName");
        super.onCreate(savedInstanceState);
        this.getTitlebarView().setTitle("转账到户");
        this.getTitlebarView().initLeft(new OnClickListener() {
            @Override
            public void onClick(View v) {
                backOnClick();
            }
        });

    }

    @Override
    public void initWidget() {
        etMoney = (EditText) findViewById(R.id.et_money);
        btnConfirm = (Button) findViewById(R.id.btn_comfirm);
        btnConfirm.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View view) {
                btnConfirmClick();
            }
        });
        TextView tvAccountId = (TextView) findViewById(R.id.tv_account_id);
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < accountNum.length(); i++) {
            if (i < 3 || i >= accountNum.length() - 4) {
                sb.append(accountNum.charAt(i));
            } else {
                sb.append('*');
            }
        }
        tvAccountId.setText(sb.toString());
        TextView tvAccountName = (TextView) findViewById(R.id.tv_account_name);
        tvAccountName.setText(nickname + "(" + realName + ")");
        etRemark = (EditText) findViewById(R.id.et_remark);
        initEdit();
        netWorkGetLimit();
        queryMember();
    }

    private void initEdit() {
        etMoney.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnConfirm.setEnabled(etMoney.getText().length() > 0);
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
                                          int arg2, int arg3) {}
        });
    }


    private void netWorkGetLimit() {
        LimitContext limitContext = new LimitContext(transferContext);
        GetLimit getLimit = new GetLimit(mContext, limitContext);
        showProgress();
        getLimit.doLimit(new GetLimit.LimitResponseHandler() {
            @Override
            public void onSuccess(List<LimitModel> results) {
                String text = "";
                if (results != null) {
                    list = results;
                    StringBuffer sb=new StringBuffer();
                    for (LimitModel bean : results) {
                        if (bean.getRangType().equals(LimitModel.SINGLE)) {
                            sb.append("每笔限额" + bean.getTotalLimitedValue() + "元，");
                        } else if (bean.getLimitedType().equals(LimitModel.TIMES) && bean
                                .getTimeRangeType().equals(LimitModel.DAY)) {
//                            sb.append( "本日还可转出" + bean.getLimitedValue() + "次，");
                        }else if(bean.getLimitedType().equals(LimitModel.QUOTA)){
                            if(bean.getTimeRangeType().equals(LimitModel.DAY)){
//                                sb.append( "本日还可转出" + bean.getLimitedValue() + "元，");
                            }
                        }
                    }
                    text = sb.replace(sb.length()-1,sb.length(),"。").toString();
                }
                etMoney.setHint(text);
                hideProgress();
            }

            @Override
            public void onError(String statusCode, String errorMsg) {
                showShortToast(errorMsg);
                hideProgress();
            }
        });
    }

    @Override
    public void onClick(View arg0) {

    }

    private void btnConfirmClick() {
        if (checkParams()) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putSerializable("context", transferContext);
            bundle.putString("account_number", accountNum);
            bundle.putString("nickname", nickname);
            bundle.putString("amount", etMoney.getText().toString());
            bundle.putString("remark", etRemark.getText().toString());
            intent.putExtras(bundle);
//            intent.setClass(this, TransferAccountConfirmActivityOld.class);
            intent.setClass(this, TransferAccountConfirmActivity.class);
            this.startActivity(intent);
        }
    }

    private boolean checkParams() {
        if(!turnOutFlag){
            showShortToast("该账户已经冻结");
            return false;
        }
        if (TextUtils.isEmpty(etMoney.getText().toString())) {
            this.showShortToast("请输入转账金额！");
            return false;
        }
        String strMoney = TextUtils.isEmpty(etMoney.getText().toString()) ? "0.00" : etMoney.getText().toString();
        BigDecimal bdMoney = new BigDecimal(strMoney);
        etMoney.setText(bdMoney + "");
        etMoney.setSelection((bdMoney + "").length());
        if (bdMoney.compareTo(new BigDecimal("0.00")) == 0) {
            this.showShortToast("您输入的转账金额不对！");
            return false;
        } else if (list != null) {
            for (LimitModel bean : list) {
                String msg = bean.checkLimit(bdMoney.doubleValue());
                if (msg != null) {
                    showShortToast(msg);
                    return false;
                }
            }
        }
        return true;
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
                        turnOutFlag =responseBean.isTurnOut();
                    }

                    @Override
                    public void onError(String statusCode, String errorMsg) {
                        hideProgress();
                        mContext.showShortToast(errorMsg);
                    }
                }, this);

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
}
