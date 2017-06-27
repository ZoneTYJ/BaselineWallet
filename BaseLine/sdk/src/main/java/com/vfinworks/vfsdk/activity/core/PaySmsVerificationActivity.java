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
import com.vfinworks.vfsdk.activity.core.channel.BaseChannel;
import com.vfinworks.vfsdk.activity.core.channel.ChannelMaps;
import com.vfinworks.vfsdk.business.QueryTrade;
import com.vfinworks.vfsdk.business.QueryTradeRepeat;
import com.vfinworks.vfsdk.common.HttpRequsetUri;
import com.vfinworks.vfsdk.common.NoDoubleClickListener;
import com.vfinworks.vfsdk.common.Utils;
import com.vfinworks.vfsdk.context.BaseAcquireContext;
import com.vfinworks.vfsdk.context.BaseContext;
import com.vfinworks.vfsdk.context.PaymentContext;
import com.vfinworks.vfsdk.context.RechargeContext;
import com.vfinworks.vfsdk.context.TransferContext;
import com.vfinworks.vfsdk.enumtype.PayResponseStateEnum;
import com.vfinworks.vfsdk.enumtype.TradeTypeEnum;
import com.vfinworks.vfsdk.enumtype.VFCallBackEnum;
import com.vfinworks.vfsdk.http.HttpUtils;
import com.vfinworks.vfsdk.http.RequestParams;
import com.vfinworks.vfsdk.http.VFinResponseHandler;
import com.vfinworks.vfsdk.model.ChannelModel;
import com.vfinworks.vfsdk.model.QpayNewBankCardModel;
import com.vfinworks.vfsdk.model.VFSDKResultModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

/**      
 *  短信验证
 */
public class PaySmsVerificationActivity extends BaseActivity  implements OnClickListener{

	private TextView tvPhoneLable; 
	private EditText etCode;
	private Button btnGetCode;
	private Button btnSubmit;
	  
	private int mResidueTime = 60;
	private Timer mTimer;
	
	private ChannelModel currentChannelModel;
	private boolean isNewCard;
	private String newBankId;
	private String bankCardId;
	private QpayNewBankCardModel newBank = null;

	private QueryTradeRepeat mQueryTradeRepeat;
	private BaseContext baseContext;
	@Override
	protected void onCreate(Bundle savedInstanceState) { 
		setContentView(R.layout.activity_sms_verification,FLAG_TITLE_LINEARLAYOUT);
		Intent intent=this.getIntent();
		baseContext = (BaseContext)intent.getExtras().getSerializable("context");
		currentChannelModel = (ChannelModel) intent.getSerializableExtra("currentChannelModel");
		newBankId = intent.getExtras().getString("newBankId");
		isNewCard = intent.getExtras().getBoolean("isNewCard");
    	if(isNewCard) {
    		newBank = (QpayNewBankCardModel)intent.getExtras().getSerializable("newBank");
    	}else{
			bankCardId =  intent.getExtras().getString("bankCardId");
    	}
    	
		super.onCreate(savedInstanceState);
		this.getTitlebarView().setTitle("短信验证");
        this.getTitlebarView().initLeft(new OnClickListener() {
			@Override
			public void onClick(View v) {
				backOnClick();
			}
		});
	}
	
	/**
	 * 初始化控件
	 */
	@Override
	public void initWidget() { 
		tvPhoneLable = (TextView) findViewById(R.id.tv_phone_lable);
		etCode = (EditText) findViewById(R.id.et_code);
		btnGetCode = (Button) findViewById(R.id.btn_get_code); 
		btnSubmit = (Button) findViewById(R.id.btn_submit);
		initEdit();
		btnGetCode.setOnClickListener(new NoDoubleClickListener() {
			@Override
			protected void onNoDoubleClick(View view) {
				getNewCode();
			}
		});
		btnSubmit.setOnClickListener(new NoDoubleClickListener() {
			@Override
			protected void onNoDoubleClick(View view) {
				submitClick();
			}
		});
//		getBindPhone();
		requestCode();
	}
	
	private void initEdit() {
		etCode.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				int textLength = etCode.getText().length();
				btnSubmit.setEnabled(textLength > 0);
			}

			@Override
			public void afterTextChanged(Editable s) {
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
										  int arg2, int arg3) {
			}
		}); 
	}
	
	private void getBindPhone() {
		RequestParams reqParam = new RequestParams();
		reqParam.putData("service", "query_member");
		reqParam.putData("token", SDKManager.token);
		HttpUtils.getInstance(this).excuteHttpRequest(HttpRequsetUri.getInstance().getMemberDoUri(),
				reqParam, new VFinResponseHandler() {

					@Override
					public void onSuccess(Object responseBean, String responseString) {
						JSONObject json;
						try {
							json = new JSONObject(responseString);
							String phone = null;
							if (json.isNull("mobile_star") == false) {
								phone = json.getString("mobile_star");
							}
							if (!TextUtils.isEmpty(phone)) {
								tvPhoneLable.setText("请输入手机" +
										phone + "收到的短信验证码");
							} else {
								tvPhoneLable.setText("请输入手机收到的短信验证码");
							}
						} catch (JSONException e) {
							e.printStackTrace();
							showShortToast(e.getMessage());
						}
					}

					@Override
					public void onError(String statusCode, String errorMsg) {
						hideProgress();
						showShortToast(errorMsg);
					}

				}, this);
	}
	
	@Override
	public void onClick(View v) {
	}
	
	private void submitClick() {
		String strMessageCode = etCode.getText().toString().trim();
		if (strMessageCode.length() <= 3) { 
			showShortToast("请输入正确的验证码!");
			return;
		}
		payAdvance(strMessageCode);
	}
	
	private void payAdvance(String messageCode) {
		this.showProgress();
		String strInnerOrderNo = "";
		if(baseContext instanceof RechargeContext) {
			strInnerOrderNo = ((RechargeContext) baseContext).getInnerOrderNo();
		}else if(baseContext instanceof PaymentContext) {
			strInnerOrderNo = ((PaymentContext) baseContext).getInnerOrderNo();
		}else if(baseContext instanceof TransferContext){
			strInnerOrderNo = ((TransferContext) baseContext).getInnerOrderNo();
		}
		creatQueryTrade(((BaseAcquireContext) baseContext).getOutTradeNumber());

		RequestParams reqParam = new RequestParams();
		reqParam.putData("service", "advance_pay");
		reqParam.putData("token", SDKManager.token);
		reqParam.putData("inner_payment_no", strInnerOrderNo);
		reqParam.putData("msg_code", messageCode);
		HttpUtils.getInstance(this).excuteHttpRequest(HttpRequsetUri.getInstance().getAcquirerDoUri(),
				reqParam, new VFinResponseHandler() {

					@Override
					public void onSuccess(Object responseBean, String responseString) {
						JSONObject json;
						try {
							json = new JSONObject(responseString);
							String isSuccess = json.getString("is_success");
							String pay_status = "";
							if (json.has("pay_status")) {
								pay_status = json.optString("pay_status");
							}
							if (isSuccess.equalsIgnoreCase("T")
									&& (pay_status.equals(PayResponseStateEnum.S.toString())
									|| pay_status.equals(PayResponseStateEnum.P.toString()))) {
								mQueryTradeRepeat.startQueryTrade();
							} else {
								showShortToast("验证码错误,请重新获取");
								hideProgress();
							}
						} catch (JSONException e) {
							e.printStackTrace();
							showShortToast(e.getMessage());
						}
					}

					@Override
					public void onError(String statusCode, String errorMsg) {
						hideProgress();
						showShortToast(errorMsg);
					}

				}, this);
	}
	
	private void getNewCode() {
		etCode.setText(""); 
		resendSms();
		requestCode();
	}
	
	/**
	 * 计时验证码
	 */
	private void requestCode() {
		btnGetCode.setEnabled(false);
		btnSubmit.setEnabled(false);
		mTimer = new Timer();
		mTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				mResidueTime--;
				if (mResidueTime == 0) {
					mResidueTime = 60;
					mTimer.cancel();
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							btnGetCode.setEnabled(true);
							btnGetCode.setText("重新获取");
						}
					});
					return;
				}
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						btnGetCode.setText(mResidueTime + "秒后重发");
					}
				});
			}
		}, 0, 1000);
	}

	@Override
	protected void onStop() {
		super.onStop();
		mTimer.cancel();
	}

	/**
	 * 短信重发
	 */
	private void resendSms() {
		BaseChannel baseChannel = getCurrentPayment();
		if(baseChannel==null){
			showShortToast("渠道不存在");
			return;
		}
		baseChannel.setDoCallBackHandler(new BaseChannel.DoCallBackHandler() {
			@Override
			public void goPay(Object responseBean, String responseString) {
				hideProgress();
				String orderNo = setPara(responseString);
				if (orderNo != null) {
					if(baseContext instanceof PaymentContext) {
						((PaymentContext) baseContext).setInnerOrderNo(orderNo);
					}else if(baseContext instanceof TransferContext){
						((TransferContext) baseContext).setInnerOrderNo(orderNo);
					}
				}
			}

			@Override
			public void goRecharge(Object responseBean, String responseString) {
				hideProgress();
				String orderNo = setPara(responseString);
				if (orderNo != null) {
					((RechargeContext) baseContext).setInnerOrderNo(orderNo);
				}
			}

			@Override
			public void goTransferToAccount(Object responseBean, String responseString) {
				hideProgress();
				String orderNo = setPara(responseString);
				if (orderNo != null) {
					((TransferContext) baseContext).setInnerOrderNo(orderNo);
				}
			}
		});
		if(baseContext instanceof RechargeContext ) {
			baseChannel.parentDoRecharge((RechargeContext) baseContext);
		}else if(baseContext instanceof PaymentContext) {
			baseChannel.parentDopay((PaymentContext) baseContext);
		}else if(baseContext instanceof TransferContext) {
			TransferContext transferContext=(TransferContext) baseContext;
			PaymentContext paymentContext=new PaymentContext(baseContext);
			paymentContext.setSignFlag(true);
			paymentContext.setRequestNo(Utils.getRandom());
			paymentContext.setOrderNums(transferContext.getOutTradeNumber());
			paymentContext.setOrderAmount(transferContext.getAmount());
			paymentContext.setOrder_trade_type(TradeTypeEnum.TRANSFER.toString());
			baseChannel.parentDopay(paymentContext);
		}
	}

	private String setPara(String responseString) {
		JSONObject json;
		try {
			json = new JSONObject(responseString);
			String isSuccess = json.getString("is_success");
			if(isSuccess.equalsIgnoreCase("T")) {
				if(!json.isNull("newBankId")) {
					newBankId = json.getString("bank_account_id");
				}
				if(!json.isNull("inner_payment_no")) {
					return json.getString("inner_payment_no");
				}
			}else{
				showShortToast("重新发送失败");
			}
		} catch (JSONException e) {
			e.printStackTrace();
			showShortToast(e.getMessage());
		}
		return null;
	}

	private BaseChannel getCurrentPayment() {
		BaseChannel channel = ChannelMaps.getInstance().getChannel(currentChannelModel);
		if(channel==null){
			return null;
		}
		channel.setChannelPara(this, null, isNewCard, newBank, bankCardId);
		channel.setAmount(currentChannelModel.amount);
		return channel;
	}

	private void backOnClick() {
		this.finishActivity();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) { 
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			backOnClick();
		}  
		return super.onKeyDown(keyCode, event);
	}


	private void creatQueryTrade(String outNo){
		mQueryTradeRepeat = new QueryTradeRepeat(this, QueryTrade.MAX_COUNT,outNo,baseContext.getToken());
		mQueryTradeRepeat.setHandler(new QueryTradeRepeat.QueryTradeHandler() {
			@Override
			public void callBackSuccess() {
				hideProgress();
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putSerializable("context", baseContext);
				bundle.putString("statue", TradeResultActivity.SUCCESS);
				intent.putExtras(bundle);
				intent.setClass(PaySmsVerificationActivity.this, TradeResultActivity.class);
				finishAll();
				startActivity(intent);
			}

			@Override
			public void callBackFault() {
				callFault();
			}

			@Override
			public void callBackRequery() {
				hideProgress();
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putSerializable("context", baseContext);
				bundle.putString("statue",TradeResultActivity.PROCESS);
				intent.putExtras(bundle);
				intent.setClass(PaySmsVerificationActivity.this, TradeResultActivity.class);
				finishAll();
				startActivity(intent);
			}

			@Override
			public void putData(RequestParams reqParam) {
				String type = "";
				if(baseContext instanceof RechargeContext) {
					type=TradeTypeEnum.DEPOSIT.toString();
				}else if(baseContext instanceof PaymentContext) {
					PaymentContext paymentContext= (PaymentContext) baseContext;
					if(TextUtils.isEmpty(paymentContext.getOrder_trade_type())) {
						type = TradeTypeEnum.ALL.toString();
					}else {
						type=paymentContext.getOrder_trade_type();
					}
				}else if(baseContext instanceof TransferContext){
					type=TradeTypeEnum.TRANSFER.toString();
				}
				reqParam.putData("trade_type", type);
			}
		});
	}


	private void callFault(){
		hideProgress();
		if (SDKManager.getInstance().getCallbackHandler() != null) {
			VFSDKResultModel result = new VFSDKResultModel();
			result.setResultCode(VFCallBackEnum.ERROR_CODE_BUSINESS
					.getCode());
			result.setMessage("支付失败");
			baseContext.sendMessage(result);
		}
		finishAll();
	}
}
