package cn.vfinance.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.vfinworks.vfsdk.SDKManager;
import com.vfinworks.vfsdk.common.Config;
import com.vfinworks.vfsdk.common.HttpRequsetUri;
import com.vfinworks.vfsdk.common.L;
import com.vfinworks.vfsdk.common.PermissionHelper;
import com.vfinworks.vfsdk.common.Utils;
import com.vfinworks.vfsdk.context.AddWithdrawCardContext;
import com.vfinworks.vfsdk.context.BaseContext;
import com.vfinworks.vfsdk.context.PaymentContext;
import com.vfinworks.vfsdk.context.PlaceOrderContext;
import com.vfinworks.vfsdk.context.RechargeContext;
import com.vfinworks.vfsdk.context.TransferContext;
import com.vfinworks.vfsdk.context.WithdrawContext;
import com.vfinworks.vfsdk.enumtype.VFCallBackEnum;
import com.vfinworks.vfsdk.enumtype.VFQueryBankTypeEnum;
import com.vfinworks.vfsdk.model.VFSDKResultModel;


public class MainActivity extends Activity implements OnClickListener{
	private static String TAG = "MainActivity";
	PermissionHelper mPermissionHelper;
//	private String token = "09dcea73ebe9ee8959fa95023e398122";
	private String token = "netfinworks36d2482c292444798b663271f7163844";
	private String phone = "13100000006";

	//密钥
	private final String key="MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAO/6rPCvyCC+IMalLzTy3cVBz" +
			"/+wamCFNiq9qKEilEBDTttP7Rd/GAS51lsfCrsISbg5td/w25" +
			"+wulDfuMbjjlW9Afh0p7Jscmbo1skqIOIUPYfVQEL687B0EmJufMlljfu52b2efVAyWZF9QBG1vx" +
			"/AJz1EVyfskMaYVqPiTesZAgMBAAECgYEAtVnkk0bjoArOTg" +
			"/KquLWQRlJDFrPKP3CP25wHsU4749t6kJuU5FSH1Ao81d0Dn9m5neGQCOOdRFi23cV9gdFKYMhwPE6" +
			"+nTAloxI3vb8K9NNMe0zcFksva9c9bUaMGH2p40szMoOpO6TrSHO9Hx4GJ6UfsUUqkFFlN76XprwE" +
			"+ECQQD9rXwfbr9GKh9QMNvnwo9xxyVl4kI88iq0X6G4qVXo1Tv6/DBDJNkX1mbXKFYL5NOW1waZzR+Z" +
			"/XcKWAmUT8J9AkEA8i0WT" +
			"/ieNsF3IuFvrIYG4WUadbUqObcYP4Y7Vt836zggRbu0qvYiqAv92Leruaq3ZN1khxp6gZKl" +
			"/OJHXc5xzQJACqr1AU1i9cxnrLOhS8m+xoYdaH9vUajNavBqmJ1mY3g0IYXhcbFm/72gbYPgundQ" +
			"/pLkUCt0HMGv89tn67i+8QJBALV6UgkVnsIbkkKCOyRGv2syT3S7kOv1J" +
			"+eamGcOGSJcSdrXwZiHoArcCZrYcIhOxOWB/m47ymfE1Dw" +
			"/+QjzxlUCQCmnGFUO9zN862mKYjEkjDN65n1IUB9Fmc1msHkIZAQaQknmxmCIOHC75u4W0PGRyVzq8KkxpNBq62ICl7xmsPM=";
	
	private static final int PLACE_ORDER_REQUSET = 101;
	
	private String orderNum;
	private String orderAmount;
	private String mobile ="00000000";

	private int rechargeCallbackMessageID = 4;
	private int withdrawCallbackMessageID = 5;
	private int placeOrderAndPayCallbackMessageID = 1;
	private int paycallbackMessageID = 6;
	private int placeOrdercallbackMessageID = 2;
	private int transferCallbackMessageID = 3;
	private int queryRealNameInfoMessageID = 7;
	private int forgetPwdMessageID = 8;
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			VFSDKResultModel resultModel = (VFSDKResultModel)msg.obj;
			if(msg.what == paycallbackMessageID) {
				handlePayCallback((VFSDKResultModel)msg.obj);
			}else if(msg.what == placeOrdercallbackMessageID) {
				handlePlaceOrderCallback((VFSDKResultModel)msg.obj);
			}else if(msg.what == transferCallbackMessageID) {
				handleCallback(resultModel);
			}else if(msg.what == rechargeCallbackMessageID) {
				handleCallback(resultModel);
			}else if(msg.what == withdrawCallbackMessageID) {
				handleCallback(resultModel);
			}else if(msg.what == placeOrderAndPayCallbackMessageID) {
				handleCallback(resultModel);
			}else if(msg.what == queryRealNameInfoMessageID) {
				handleRealNameCallback(resultModel);
			}
		};
	};

	private void handlePlaceOrderCallback(VFSDKResultModel resultModel) {
		if(resultModel.getResultCode().equalsIgnoreCase(VFCallBackEnum.OK.getCode())) {
			Toast.makeText(MainActivity.this, resultModel.getJsonData(), Toast.LENGTH_LONG).show();
		}else{
			Toast.makeText(MainActivity.this, resultModel.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}
	
	private void handlePayCallback(VFSDKResultModel resultModel) {
		if(resultModel.getResultCode().equalsIgnoreCase(VFCallBackEnum.OK.getCode())) {
			Toast.makeText(this, resultModel.getMessage(), Toast.LENGTH_LONG).show();
		}else{
			Toast.makeText(MainActivity.this, resultModel.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}

	private void handleCallback(VFSDKResultModel resultModel) {
		if(resultModel.getResultCode().equalsIgnoreCase(VFCallBackEnum.OK.getCode())) {
			Toast.makeText(this, resultModel.getMessage(), Toast.LENGTH_LONG).show();
		}else{
			if(VFCallBackEnum.ERROR_CODE_BUSINESS.equals(resultModel.getResultCode())){
				SDKManager.getInstance().close();
			}
			Toast.makeText(MainActivity.this, resultModel.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}

	//没有实名就不能做支付操作
	private void handleRealNameCallback(VFSDKResultModel resultModel) {
		if(resultModel.getResultCode().equalsIgnoreCase(VFCallBackEnum.ERROR_CODE_NULL.getCode())) {
			Toast.makeText(MainActivity.this, "请先实名认证后,再做支付操作", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		L.isDebug=true;
		//第一步调用需要调用初始化
		SDKManager.getInstance().init(this, Config.getInstance().PARTNER_ID,token,phone);
		//第二步设置一些共同的参数，之后便可调用sdk方法了
		SDKManager.getInstance().SetCommonParam(Config.getInstance().PARTNER_ID,Config.getInstance().APP_ID,
//				HttpRequsetUri.getInstance().getHttpServer()
//				"http://func115.vfinance.cn/appserver"
//				"http://58.20.52.111:58519/appserver"
				"http://10.5.20.4:8080/appserver-web-core"
				,"http://base.vfinance.cn/static/resources/help");

		// 当系统为6.0以上时，需要申请权限
		mPermissionHelper = new PermissionHelper(this);
		mPermissionHelper.setOnApplyPermissionListener(new PermissionHelper.OnApplyPermissionListener() {
			@Override
			public void onAfterApplyAllPermission() {
				Log.i(TAG, "All of requested permissions has been granted, so run app logic.");

			}
		});
		if (Build.VERSION.SDK_INT < 23) {
			// 如果系统版本低于23，直接跑应用的逻辑
			Log.d(TAG, "The api level of system is lower than 23, so run app logic directly.");

		} else {
			// 如果权限全部申请了，那就直接跑应用逻辑
			if (mPermissionHelper.isAllRequestedPermissionGranted()) {
				Log.d(TAG, "All of requested permissions has been granted, so run app logic directly.");

			} else {
				// 如果还有权限为申请，而且系统版本大于23，执行申请权限逻辑
				Log.i(TAG, "Some of requested permissions hasn't been granted, so apply permissions first.");
				mPermissionHelper.applyPermissions();
			}
		}
		initView();
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		mPermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_OK && requestCode == PLACE_ORDER_REQUSET) {
			orderNum =  data.getExtras().getString("orderNum");	//订单号
			orderAmount =  data.getExtras().getString("orderAmount");	//订单金额
		}
		else{
			mPermissionHelper.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	protected void onDestroy() {
		SDKManager.getInstance().clear();
		super.onDestroy();
	}

	private void initView() {
		TextView tvRecharge = (TextView) this.findViewById(R.id.tv_recharge);
		tvRecharge.setOnClickListener(this);
		TextView tv_init = (TextView) this.findViewById(R.id.tv_init);
		tv_init.setOnClickListener(this);
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
		TextView tv_order_and_pay = (TextView) this.findViewById(R.id.tv_order_and_pay);
		tv_order_and_pay.setOnClickListener(this);
		TextView tv_forget_pwd = (TextView) this.findViewById(R.id.tv_forget_pwd);
		tv_forget_pwd.setOnClickListener(this);
		TextView tv_add_channel = (TextView) this.findViewById(R.id.tv_add_channel);
		tv_add_channel.setOnClickListener(this);
	}
	
	
	@Override
	protected void onResume() {
       	super.onResume();
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.tv_init:
			initClick();
			break;
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
		case R.id.tv_order_and_pay:
			OrderAndPayClick();
			break;
		case R.id.tv_transfer:
			transferClick();
			break;
		case R.id.tv_login:
			loginClick();
			break;
		case R.id.tv_forget_pwd:
			forgetPwdClick();
			break;
		case R.id.tv_add_channel:
			SDKManager.getInstance().addChannel(new NewChannel());
			break;
		}
	}

	/**
	 * 忘记支付密码点击
	 */
	private void forgetPwdClick() {
		BaseContext baseContext = new BaseContext();
		baseContext.setCallBackMessageId(forgetPwdMessageID);
		baseContext.setToken(SDKManager.token);
		SDKManager.getInstance().ResetPayPassword(this, baseContext, mHandler);
	}

	/**
	 * 初始化
	 */
	private void initClick() {
		//第一步调用需要调用初始化
		SDKManager.getInstance().init(this, Config.getInstance().PARTNER_ID,token,phone);
		//第二步设置一些共同的参数，之后便可调用sdk方法了
		SDKManager.getInstance().SetCommonParam(Config.getInstance().PARTNER_ID,Config.getInstance().APP_ID,
				HttpRequsetUri.getInstance().getHttpServer(),"http://base.vfinance.cn/static/resources/help");
//		queryRealNameInfo();//是否已经实名验证
		BaseContext baseContext=new BaseContext();
		baseContext.setToken(token);
		SDKManager.getInstance().QueryMember(this,baseContext,mHandler);
	}


	/**
	 * 转账点击
	 */
	private void transferClick() {
		TransferContext transferContext = new TransferContext();
		transferContext.setMobile(mobile);		//设置手机号
		transferContext.setToken(token);		//设置token
		transferContext.setCallBackMessageId(transferCallbackMessageID);	//设置mHandler消息的id
		transferContext.setAvailableAmount("500");		//设置有效余额
		transferContext.setOutTradeNumber(Utils.getOnlyValue());	//设置外部订单号
		transferContext.setNotifyUrl("");	//设置转账完成后台通知后台的url
		SDKManager.getInstance().Transfer(this, transferContext, mHandler);
	}
	


	/**
	 * 下单并支付
	 */
	private void OrderAndPayClick() {
		PlaceOrderContext placeOrderContext = new PlaceOrderContext();
		placeOrderContext.setToken(token);
		placeOrderContext.setCallBackMessageId(placeOrderAndPayCallbackMessageID);	//设置mHandler消息的id
		String tradeNo = Utils.getOnlyValue();
		placeOrderContext.setRequestNo(tradeNo);	//设置请求号

//		//下担保交易订单，参数说明：外部订单号，商品名称，总金额，担保金额，卖家，卖家类型（MOBILE,UID,MEMBER_ID,LOGIN_NAME)，商品价格，商品数量
//		placeOrderContext.generateEnsureTradeList(tradeNo,"外星人","0.02","0.01",
//				"15700000000","MOBILE","0.02",
//				"1");

		//下即时交易订单，参数说明：外部订单号，商品名称，总金额，卖家，卖家类型（MOBILE,UID,MEMBER_ID,LOGIN_NAME)，商品价格，商品数量,异步通知地址
		placeOrderContext.generateInstantTradeList(tradeNo,"外星人","0.02",
				"15700000000","MOBILE","0.02", "1","notifyUrlCeShi","");
		placeOrderContext.setAppExtension("ceishiExtension");
		placeOrderContext.setSignFlag(true);	//设置内部加签
		//		设置外部加签
//		try {
//			placeOrderContext.setSign(RSA.sign("tradeList="+placeOrderContext.getTradeList(),key,"utf-8"));
//			placeOrderContext.setPaymentSign(RSA.sign("amount=" + placeOrderContext.getOrderAmount() + "&outer_trade_no_list=" +
//					placeOrderContext.getTradeNo(),key,"utf-8"));
//			placeOrderContext.setSignType("RSA");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		SDKManager.getInstance().OrderAndPay(this,placeOrderContext,mHandler);
	}

	/**
	 * 充值点击
	 */
	private void rechargeClick() {
		RechargeContext rechargeContext = new RechargeContext();
		rechargeContext.setToken(token);		//设置token
		rechargeContext.setCallBackMessageId(rechargeCallbackMessageID);	//设置mHandler消息的id
		rechargeContext.setOutTradeNumber(Utils.getOnlyValue());	//设置外部订单号
		rechargeContext.setMobile(mobile);				//设置手机号
		rechargeContext.setNotifyUrl("");			//设置充值完成通知后台的url
		SDKManager.getInstance().Recharge(this, rechargeContext, mHandler);
	}

	/**
	 * 提现点击
	 */
	private void withdrawClick() {
		WithdrawContext withdrawContext = new WithdrawContext();
		withdrawContext.setToken(token);		//设置token
		withdrawContext.setCallBackMessageId(withdrawCallbackMessageID);	//设置mHandler消息的id
		withdrawContext.setMobile(mobile);		//设置手机号
		withdrawContext.setAvailableAmount("500");		//设置有效的余额
		withdrawContext.setOutTradeNumber(Utils.getOnlyValue());	//设置外部订单号
		withdrawContext.setNotifyUrl("");		//设置提现完成通知后台的url
		SDKManager.getInstance().Withdraw(this, withdrawContext, null);
	}

	/**
	 * 添加提现卡
	 */
	private void addBankCardClick() {
		AddWithdrawCardContext addWithdrawCardContext = new AddWithdrawCardContext();
		addWithdrawCardContext.setToken(token);
		SDKManager.getInstance().AddWithdrawBankCard(this,addWithdrawCardContext,null);
	}

	/**
	 * 查询我的银行卡
	 */
	private void MyBankCardClick() {
		SDKManager.getInstance().QueryMyBankCard(this,token, mobile, VFQueryBankTypeEnum.ALL,null);
	}

	/**
	 * 模拟下单，仅测试
	 */
	private void placeOrderClick() {
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putString("token", token);
		intent.putExtras(bundle);
		intent.setClass(this, com.vfinworks.vfsdk.activity.core.PlaceOrderActivity.class);
		this.startActivityForResult(intent, PLACE_ORDER_REQUSET);
	}

	/**
	 * app已下单，跳转去支付
	 */
	private void paymentClick() {
		PaymentContext paymentContext = new PaymentContext();
		paymentContext.setToken(token);
		paymentContext.setCallBackMessageId(paycallbackMessageID);
		paymentContext.setRequestNo(Utils.getOnlyValue());
		paymentContext.setOrderNums(orderNum);	//订单号
		paymentContext.setOrderInfo("购买地球");
		paymentContext.setOrderAmount(orderAmount);	//订单金额
		paymentContext.setSignFlag(true);	//设置内部加签
		//设置外部加签
//		try {
//			paymentContext.setSign(RSA.sign("amount=" + paymentContext.getOrderAmount() + "&outer_trade_no_list=" +
//                    paymentContext.getOrderNums(),key,"utf-8"));
//			paymentContext.setSignType("RSA");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		SDKManager.getInstance().Payment(this, paymentContext, mHandler);
	}

	private void loginClick() {
		SDKManager.getInstance().Login(this,mHandler);
	}

	private void queryRealNameInfo() {
		BaseContext baseContext = new BaseContext();
		baseContext.setToken(token);
		baseContext.setCallBackMessageId(queryRealNameInfoMessageID);
		SDKManager.getInstance().QueryRealNameInfo(this, baseContext,
				mHandler);
	}

}
