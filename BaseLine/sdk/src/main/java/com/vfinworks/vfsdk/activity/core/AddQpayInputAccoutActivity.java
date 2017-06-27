package com.vfinworks.vfsdk.activity.core;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.vfinworks.vfsdk.R;
import com.vfinworks.vfsdk.activity.BaseActivity;
import com.vfinworks.vfsdk.activity.core.base.AddQpayInputInfoBaseActivity;
import com.vfinworks.vfsdk.common.BankCardUtlis;
import com.vfinworks.vfsdk.common.Const;
import com.vfinworks.vfsdk.context.BaseContext;
import com.vfinworks.vfsdk.context.PaymentContext;

/**
 * 绑定银行卡
 */
public class AddQpayInputAccoutActivity extends BaseActivity implements OnClickListener {
    private EditText etAccount;
    private Button btnNext;
    private BaseContext mBaseContext;
    private static final int ADD_QPAY_INFO = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.add_qpay_bank_account_activity, FLAG_TITLE_LINEARLAYOUT);
        super.onCreate(savedInstanceState);
        this.getTitlebarView().setTitle("绑定银行卡");
        this.getTitlebarView().initLeft(new OnClickListener() {
            @Override
            public void onClick(View v) {
                backOnClick();
            }
        });
        mBaseContext= (BaseContext) getIntent().getExtras().getSerializable("basecontext");
    }

    @Override
    public void initWidget() {
        etAccount = (EditText) findViewById(R.id.et_account);
        btnNext = (Button) findViewById(R.id.btn_next);
        btnNext.setOnClickListener(this);
        initEdit();
    }

    private void initEdit() {
        etAccount.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnNext.setEnabled(isNextEnable());
            }

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {}
        });
    }

    private boolean isNextEnable() {
        String strCard = etAccount.getText().toString().replace(" ", "");
        if (strCard.length() < Const.MINIMUM_CARD_LENGTH
                || strCard.length() > Const.MAXIMUM_CARD_LENGTH) {
            return false;
        }

        return true;
    }

    @Override
    public void onClick(View arg0) {
        if (arg0.getId() == R.id.btn_next) {
            btnNextClick();
        }
    }

    private void btnNextClick() {
        String bankNo = etAccount.getText().toString().replace(" ", "");
        if (checkParams(bankNo)) {
            Intent intent = new Intent();
            intent.putExtra("bankNo", bankNo);
            intent.putExtra("basecontext", mBaseContext);
            intent.setClass(this, AddQpayInputInfoBaseActivity.class);
            startActivity(intent);
        }
    }

    private boolean checkParams(String bankNo) {
        String bankName = BankCardUtlis.getNameOfBank(bankNo);
        if (bankName.equals("没有记录的卡号")) {
            showShortToast("请输入正确银行卡号");
            return false;
        }
        if (!(mBaseContext instanceof PaymentContext) && bankName.contains("信用卡")) {
            showShortToast("不能使用信用卡充值");
            return false;
        }
        return true;
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
