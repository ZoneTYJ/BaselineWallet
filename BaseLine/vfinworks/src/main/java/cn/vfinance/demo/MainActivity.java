package cn.vfinance.demo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.vfinworks.vfsdk.SDKManager;
import com.vfinworks.vfsdk.activity.BaseActivity;
import com.vfinworks.vfsdk.common.Config;
import com.vfinworks.vfsdk.common.HttpRequsetUri;
import com.vfinworks.vfsdk.common.Utils;
import com.vfinworks.vfsdk.context.AddWithdrawCardContext;
import com.vfinworks.vfsdk.context.PaymentContext;
import com.vfinworks.vfsdk.context.TransferContext;
import com.vfinworks.vfsdk.context.WithdrawContext;
import com.vfinworks.vfsdk.enumtype.VFCallBackEnum;
import com.vfinworks.vfsdk.enumtype.VFQueryBankTypeEnum;
import com.vfinworks.vfsdk.model.VFSDKResultModel;

public class MainActivity extends BaseActivity implements OnClickListener{
	private String token = "anonymous";
	
	private static final int PLACE_ORDER_REQUSET = 101;
	
	private String orderNum;
	private String orderAmount;
	private String mobile ="00000000";

	private int paycallbackMessageID = 1;
	private int placeOrdercallbackMessageID = 2;
	private int transferCallbackMessageID = 3;
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if(msg.what == paycallbackMessageID) {
				handlePayCallback((VFSDKResultModel)msg.obj);
			}else if(msg.what == placeOrdercallbackMessageID) {
				handlePlaceOrderCallback((VFSDKResultModel)msg.obj);
			}else if(msg.what == transferCallbackMessageID) {
				
			}
		};
	};
	
	private void handlePlaceOrderCallback(VFSDKResultModel resultModel) {
		MainActivity.this.hideProgress();
		if(resultModel.getResultCode().equalsIgnoreCase(VFCallBackEnum.OK.getCode()) == true) {
			Toast.makeText(MainActivity.this, resultModel.getJsonData(), Toast.LENGTH_LONG).show();
		}else{
			Toast.makeText(MainActivity.this, resultModel.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}
	
	private void handlePayCallback(VFSDKResultModel resultModel) {
		if(resultModel.getResultCode().equalsIgnoreCase(VFCallBackEnum.OK.getCode()) == true) {
			Toast.makeText(this, resultModel.getMessage(), Toast.LENGTH_LONG).show();
		}else{
			Toast.makeText(MainActivity.this, resultModel.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//第一次调用需要调用初始化
		SDKManager.getInstance().init(this, Config.getInstance().PARTNER_ID);
		SDKManager.getInstance().SetCommonParam(Config.getInstance().PARTNER_ID,Config.getInstance().APP_ID,
				HttpRequsetUri.getInstance().getHttpServer(),"http://base.vfinance.cn/static/resources/help");
		initView();
	}
	
	private void initView() {
		TextView tvRecharge = (TextView) this.findViewById(R.id.tv_recharge);
		tvRecharge.setOnClickListener(this);
		TextView tvWithDraw = (TextView) this.findViewById(R.id.tv_withdraw);
		tvWithDraw.setOnClickListener(this);
		TextView tvMyBankCard = (TextView) this.findViewById(R.id.tv_my_bank_card);
		tvMyBankCard.setOnClickListener(this);
		TextView tvAddBankCard = (TextView) this.findViewById(R.id.tv_add_bank_card);
		tvAddBankCard.setOnClickListener(this);
		TextView tvPay = (TextView) this.findViewById(R.id.tv_pay);
		tvPay.setOnClickListener(this);
		TextView tvPlaceOrder = (TextView) this.findViewById(R.id.tv_place_order);
		tvPlaceOrder.setOnClickListener(this);
		TextView tvTransfer = (TextView) this.findViewById(R.id.tv_transfer);
		tvTransfer.setOnClickListener(this);
		TextView tvLogin = (TextView) this.findViewById(R.id.tv_login);
		tvLogin.setOnClickListener(this);
	}
	
	
	@Override
	protected void onResume() {
       	super.onResume();
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.tv_recharge:
			rechargeClick();
			break;
		case R.id.tv_withdraw:
			withdrawClick();
			break;
		case R.id.tv_my_bank_card:
			MyBankCardClick();
			break;
		case R.id.tv_add_bank_card:
			addBankCardClick();
			break;
		case R.id.tv_pay:
			paymentClick();
			break;
		case R.id.tv_place_order:
			placeOrderClick();
			break;
		case R.id.tv_transfer:
			transferClick();
			break;
		case R.id.tv_login:
			loginClick();
			break;
		}
	}
	
	private void loginClick() {
		SDKManager.getInstance().Login(this,null);

//		Intent loginintent=new Intent(this,CertificationActivity.class);  //方法1
//		startActivity(loginintent);

//		if(SharedPreferenceUtil.getInstance().getBooleanValueFromSP("gesture_switch"))
//			startActivity(new Intent(this, UnlockGesturePwdToHomeActivity.class));
//		else
//			startActivity(new Intent(this,LoginActivity.class));

	}
	
	private void transferClick() {
		TransferContext transferContext = new TransferContext();
		transferContext.setToken(token);
		transferContext.setCallBackMessageId(transferCallbackMessageID);
		SDKManager.getInstance().Transfer(this, transferContext, mHandler);
	}
	
	private void placeOrderClick() {
		
//		Intent intent = new Intent();
//    	Bundle bundle = new Bundle();
//    	bundle.putString("token", token);
//    	intent.putExtras(bundle);
//        intent.setClass(this,PlaceOrderActivity.class);
//        this.startActivityForResult(intent,PLACE_ORDER_REQUSET);

		/*PlaceOrderContext placeOrderContext = new PlaceOrderContext();
		placeOrderContext.setToken(token);
		placeOrderContext.setCallBackMessageId(placeOrdercallbackMessageID);
		placeOrderContext.setRequestNo(String.valueOf(Math.round(Math.random()*10000000)));
		String tradeNo = String.valueOf(Math.round(Math.random()*10000000));
		orderNum = tradeNo;
		orderAmount = "0.01";
		//即时到账ISNTANT、担保交易ENSURE
		//placeOrderContext.setTradeType(VFOrderTypeEnum.TRADE_ENSURE);
		//placeOrderContext.setTradeList("[{\"out_trade_no\":\""+ tradeNo + "\",\"subject\":\"牛奶\",\"total_amount\":\"0.01\",\"ensure_amount\":\"1\",\"seller\":\"13621722085\",\"seller_type\":\"MOBILE\",\"price\":\"0.01\",\"quantity\":1}]");
		placeOrderContext.setTradeType(VFOrderTypeEnum.TRADE_INSTANT);
		placeOrderContext.setTradeList("[{\"out_trade_no\":\""+ tradeNo + "\",\"subject\":\"牛奶\",\"total_amount\":\"0.01\",\"seller\":\"13621722085\",\"seller_type\":\"MOBILE\",\"price\":\"0.01\",\"quantity\":1}]");
		//placeOrderContext.setPayMethod("online");
		this.showProgress();
		SDKManager.getInstance().PlaceOrder(this,placeOrderContext,mHandler);*/
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) { 
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_OK && requestCode == PLACE_ORDER_REQUSET) { 
			orderNum =  data.getExtras().getString("orderNum");
			orderAmount =  data.getExtras().getString("orderAmount");
		}
	}
	
	private void paymentClick() {
		PaymentContext paymentContext = new PaymentContext();
    	paymentContext.setToken(token);
    	paymentContext.setCallBackMessageId(paycallbackMessageID);
    	paymentContext.setRequestNo(Utils.getOnlyValue());
    	paymentContext.setOrderNums(orderNum);
    	paymentContext.setOrderInfo("购买地球");
    	paymentContext.setOrderAmount(orderAmount);
		SDKManager.getInstance().Payment(this, paymentContext,mHandler);
	}
	
	private void addBankCardClick() {
		AddWithdrawCardContext addWithdrawCardContext = new AddWithdrawCardContext();
		addWithdrawCardContext.setToken(token);
		SDKManager.getInstance().AddWithdrawBankCard(this,addWithdrawCardContext,null);
	}
	
	private void MyBankCardClick() {
		SDKManager.getInstance().QueryMyBankCard(this,token, mobile,VFQueryBankTypeEnum.ALL,null);
	}
	
	private void rechargeClick() {
		//SDKManager.getInstance().Recharge(this,null);
	}
	
	private void withdrawClick() {
		WithdrawContext withdrawContext = new WithdrawContext();
		withdrawContext.setToken(token);
		withdrawContext.setMobile(mobile);
		withdrawContext.setAvailableAmount("20.00");
		SDKManager.getInstance().Withdraw(this,withdrawContext,null);
	}
	
	/*private void showDialog() {
		final PayPwdDialog mDialog = new PayPwdDialog(this);
	    mDialog.setPayMoney("2");
	    mDialog.setOnStatusChangeListener(new PayPwdView.OnStatusChangeListener() {
	        @Override
	        public void onInputComplete(VFEncryptData result) {
	            mDialog.dismiss();
	        }

	        @Override
	        public void onUserCanel() {
	            mDialog.dismiss();
	        }

	    });
	    mDialog.show();
	}*/
}
