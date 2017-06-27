package com.vfinworks.vfsdk.activity.core;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.vfinworks.vfsdk.R;
import com.vfinworks.vfsdk.SDKManager;
import com.vfinworks.vfsdk.activity.BaseActivity;
import com.vfinworks.vfsdk.activity.core.dialog.MyBankCardDialog;
import com.vfinworks.vfsdk.context.QueryMyBankCardContext;
import com.vfinworks.vfsdk.context.UnbindBankCardContext;

/**
 * Created by tangyijian on 2016/9/19.
 */
public class MyBankCardDetailActivity extends BaseActivity {
    private QueryMyBankCardContext mybankCardContext;
    private MyBankCardDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_bankcard_detail, FLAG_TITLE_LINEARLAYOUT);
        super.onCreate(savedInstanceState);
        mybankCardContext = (QueryMyBankCardContext) getIntent().getSerializableExtra("context");
        this.getTitlebarView().setTitle("银行服务");
        this.getTitlebarView().initLeft(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backOnClick();
            }
        });
        this.getTitlebarView().initRight("管理", new View
                .OnClickListener() {
            @Override
            public void onClick(View v) {
                showCardDialog();
            }
        });
        initView();
        initData();
    }

    private void backOnClick() {
        finishActivity();
    }

    private void initView() {
        ImageView iv_bank_icon = (ImageView) findViewById(R.id.iv_bank_icon);
        ImageView iv_qpay_flag = (ImageView) findViewById(R.id.iv_qpay_flag);
        TextView tv_bank_name = (TextView) findViewById(R.id.tv_bank_name);
        TextView tv_bank_des = (TextView) findViewById(R.id.tv_bank_des);
        TextView tv_bank_num = (TextView) findViewById(R.id.tv_bank_num);
        tv_bank_name.setText(mybankCardContext.getBankName());
        tv_bank_des.setText(mybankCardContext.getCardDes());
        tv_bank_num.setText("尾号"+mybankCardContext.getCardNo().substring(mybankCardContext.getCardNo().length()-4));
        if(mybankCardContext.isQpayFlag()){
            iv_qpay_flag.setVisibility(View.VISIBLE);
        }else {
            iv_qpay_flag.setVisibility(View.GONE);
        }
        iv_bank_icon.setImageResource(mybankCardContext.getBankIconRes());

    }

    private void initData() {
        mDialog = new MyBankCardDialog(this);
        mDialog.setListener(new MyBankCardDialog.DiaBankClickListener() {
            @Override
            public void deleteBankCard() {
//                showShortToast("删除该银行卡");
                unbindBankCardClick(mybankCardContext.getBankcardId());
            }
        });
    }

    private void showCardDialog() {
        if (mDialog != null) {
            mDialog.show();
        }
    }

    private void unbindBankCardClick(String strBankCardId) {
        this.showProgress();
        UnbindBankCardContext baseContext = new UnbindBankCardContext();
        baseContext.setCallBackMessageId(MyBankCardActivity.unbindBankCardMessageID);
        baseContext.setToken(mybankCardContext.getToken());
        baseContext.setMobile(mybankCardContext.getMobile());
        baseContext.setBankCardId(strBankCardId);
        SDKManager.getInstance().UnbindBankCard(this, baseContext, SDKManager.getInstance().getCallbackHandler());
    }


}
