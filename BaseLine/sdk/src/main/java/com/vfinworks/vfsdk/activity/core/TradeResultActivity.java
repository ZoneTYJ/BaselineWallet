package com.vfinworks.vfsdk.activity.core;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.vfinworks.vfsdk.R;
import com.vfinworks.vfsdk.SDKManager;
import com.vfinworks.vfsdk.activity.BaseActivity;
import com.vfinworks.vfsdk.context.BaseContext;
import com.vfinworks.vfsdk.context.DealAcceptContext;
import com.vfinworks.vfsdk.context.PaymentContext;
import com.vfinworks.vfsdk.context.PlaceOrderPayContext;
import com.vfinworks.vfsdk.context.RechargeContext;
import com.vfinworks.vfsdk.context.TransferContext;
import com.vfinworks.vfsdk.context.WithdrawContext;
import com.vfinworks.vfsdk.enumtype.VFCallBackEnum;
import com.vfinworks.vfsdk.model.VFSDKResultModel;

/**
 * 提现、转账结果等的显示
 */
public class TradeResultActivity extends BaseActivity implements OnClickListener{
	public static final String SUCCESS="success";
	public static final String PROCESS="process";


	private TextView tvResult;
	private Button btnComfirm;
	private BaseContext baseContext;
	private String mStatue;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.withdraw_result_activity,FLAG_TITLE_LINEARLAYOUT);
        baseContext = (BaseContext) this.getIntent().getExtras().getSerializable("context");
		mStatue =getIntent().getStringExtra("statue");
        super.onCreate(savedInstanceState);
        showTitle();
        this.getTitlebarView().initLeft(new OnClickListener() {
			@Override
			public void onClick(View v) {
				backOnClick();
			}
		});
        
    }
    
    private void showTitle() {
		getTitlebarView().setTitle(getMessage()+"结果");
    }

	@Override
	public void initWidget() {
		tvResult = (TextView)this.findViewById(R.id.tv_result);
		showResult();
		btnComfirm = (Button)this.findViewById(R.id.btn_confirm);
		btnComfirm.setOnClickListener(this);
	}
	
	private void showResult() {
		if(mStatue.equals(SUCCESS)){
			tvResult.setText(getMessage()+"成功");
		}else if(mStatue.equals(PROCESS)){
			tvResult.setText(getMessage() + "处理中");
		}
//		Intent intent = new Intent(WalletActivity.ACTION_UPDATE);
//		intent.putExtra(WalletActivity.MONEY_CHANGE, getIntent().getStringExtra(WalletActivity.MONEY_CHANGE));
//		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
	}
	
	@Override
	public void onClick(View arg0) {
		if(arg0.getId() == R.id.btn_confirm) {
			confirmBtnClick();
		}
	}
	
	private void confirmBtnClick() {
		backOnClick();
	}
	
	private void backOnClick() {
		this.finishAll();
		if(SDKManager.getInstance().getCallbackHandler() != null) {
			VFSDKResultModel result = new VFSDKResultModel();
			result.setResultCode(VFCallBackEnum.OK.getCode());
			result.setMessage(getMessage()+"成功");
			baseContext.sendMessage(result);
		}
	}
	
	private String getMessage() {
		if(baseContext instanceof WithdrawContext) {
    		return "提现";
    	}else if(baseContext instanceof TransferContext) {
    		return "转账";
    	}else if(baseContext instanceof PaymentContext) {
			return "支付";
		}else if(baseContext instanceof RechargeContext){
			return "充值";
		}else if(baseContext instanceof PlaceOrderPayContext) {
			return "商家收款";
		}else if(baseContext instanceof DealAcceptContext) {
			return"确认收货";
		}
		return null;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) { 
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			backOnClick();
		}  
		return super.onKeyDown(keyCode, event);
	}
}
