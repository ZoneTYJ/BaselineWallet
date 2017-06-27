package com.vfinworks.vfsdk.activity.core;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.vfinworks.vfsdk.R;
import com.vfinworks.vfsdk.SDKManager;
import com.vfinworks.vfsdk.activity.BaseActivity;
import com.vfinworks.vfsdk.activity.login.LoginActivity;
import com.vfinworks.vfsdk.authenticator.AuthenMain;
import com.vfinworks.vfsdk.authenticator.Base32String;
import com.vfinworks.vfsdk.common.HttpRequsetUri;
import com.vfinworks.vfsdk.common.L;
import com.vfinworks.vfsdk.common.SharedPreferenceUtil;
import com.vfinworks.vfsdk.common.Utils;
import com.vfinworks.vfsdk.context.PlaceOrderContext;
import com.vfinworks.vfsdk.enumtype.VFCallBackEnum;
import com.vfinworks.vfsdk.enumtype.VFOrderTypeEnum;
import com.vfinworks.vfsdk.http.HttpUtils;
import com.vfinworks.vfsdk.http.RequestParams;
import com.vfinworks.vfsdk.http.VFinResponseHandler;
import com.vfinworks.vfsdk.model.VFSDKResultModel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PlaceOrderPayActivity extends BaseActivity implements OnClickListener{
	private String token;
	
	private String orderNum;
	private String orderAmount;
	
	private Spinner tradeTypeSpinner;
	private List<String> tradeTypeDataList;
	private ArrayAdapter<String> tradeTypeAdapter;
	
	private Spinner sellerTypeSpinner;
	private List<String> sellerTypeDataList;
	private ArrayAdapter<String> sellerTypeAdapter;
	
	private LinearLayout lyEnsureAmount;
	private EditText etEnsureAmount;
	private EditText etSubject;
	private EditText etPrice;
	private EditText etQuantity;
	private EditText etSeller;
	private EditText et_buyer;
	private Button btnOrder;
	private AuthenMain mAuthenMain;

	private String phone;
	private String number_pwd;
	private String qrcode;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.setIsPush(false);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_place_order_pay);
		token = this.getIntent().getExtras().getString("token");
		phone = this.getIntent().getExtras().getString("phone");
		number_pwd = this.getIntent().getExtras().getString("number_pwd");
		qrcode = this.getIntent().getExtras().getString("qrcode");
		mAuthenMain = new AuthenMain(this);
		initView();
	}
	
	private void initView() {
		tradeTypeSpinner = (Spinner) findViewById(R.id.spinner_trade_type);
	    
        //数据
		tradeTypeDataList = new ArrayList<String>();
		tradeTypeDataList.add("TRADE_INSTANT");
		tradeTypeDataList.add("TRADE_ENSURE");
        
        //适配器
		tradeTypeAdapter= new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, tradeTypeDataList);
        //设置样式
		tradeTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
		tradeTypeSpinner.setAdapter(tradeTypeAdapter);
		tradeTypeSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
				if(tradeTypeDataList.get(position).equals("TRADE_INSTANT") == true)
					lyEnsureAmount.setVisibility(View.GONE);
				else
					lyEnsureAmount.setVisibility(View.VISIBLE);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		sellerTypeSpinner = (Spinner) findViewById(R.id.spinner_seller_type);
		 //数据
		sellerTypeDataList = new ArrayList<String>();
		sellerTypeDataList.add("MOBILE");
		sellerTypeDataList.add("UID");
		sellerTypeDataList.add("MEMBER_ID");
		sellerTypeDataList.add("LOGIN_NAME");
		//适配器
		sellerTypeAdapter= new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, sellerTypeDataList);
        //设置样式
		sellerTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
		sellerTypeSpinner.setAdapter(sellerTypeAdapter);
		
		lyEnsureAmount = (LinearLayout) findViewById(R.id.ly_ensure_amount);
		LinearLayout lyBack = (LinearLayout) findViewById(R.id.layout_left);
		lyBack.setOnClickListener(this);
		etEnsureAmount = (EditText) findViewById(R.id.et_ensure_amount);
		etSubject = (EditText) findViewById(R.id.et_subject);
		etPrice = (EditText) findViewById(R.id.et_price);
		etQuantity = (EditText) findViewById(R.id.et_quantity);
		etSeller = (EditText) findViewById(R.id.et_seller);
		et_buyer = (EditText) findViewById(R.id.et_buyer);
		btnOrder = (Button) findViewById(R.id.btn_order);
		btnOrder.setOnClickListener(this);

		etSeller.setText(SharedPreferenceUtil.getInstance().getStringValueFromSP(LoginActivity.ACCOUNT));
		et_buyer.setText(phone);
	}
	
	
	@Override
	protected void onResume() {
       super.onResume();
       //showDialog();
    }

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.btn_order) {
			placeOrderClick();
		}else if(v.getId() == R.id.layout_left) {
			backOnClick();
		}
	}
	
	private void placeOrderClick() {
		if(checkOrder() == true) {
			final PlaceOrderContext placeOrderContext = new PlaceOrderContext();
			placeOrderContext.setToken(token);
			placeOrderContext.setCallBackMessageId(0);
			String tradeNo = Utils.getOnlyValue();
			placeOrderContext.setRequestNo(tradeNo);
			orderNum = tradeNo;
			
			BigDecimal bdTotalAmount = new BigDecimal("0.00");
			BigDecimal bdPrice = new BigDecimal(etPrice.getText().toString());
			BigDecimal bdQuantity = new BigDecimal(etQuantity.getText().toString());
			bdTotalAmount = bdPrice.multiply(bdQuantity);
			bdTotalAmount=bdTotalAmount.setScale(2, BigDecimal.ROUND_HALF_UP);
			
			orderAmount = bdTotalAmount.toString();
			
			String strTradeList = "[{\"out_trade_no\":\"" + tradeNo + "\",\"subject\":\"" + etSubject.getText().toString() + "\",\"total_amount\":\"" +
					bdTotalAmount.toString() + "\",";
			placeOrderContext.setTradeType(VFOrderTypeEnum.TRADE_INSTANT);
			strTradeList = strTradeList + "\"seller\":\"" + etSeller.getText().toString() + "\",\"seller_type\":\"" +
					sellerTypeDataList.get(sellerTypeSpinner.getSelectedItemPosition()) + "\",\"price\":\"" + etPrice.getText().toString() +
					"\",\"quantity\":" + etQuantity.getText().toString() + "}]";
			placeOrderContext.setTradeList(strTradeList);
			placeOrderContext.setSignFlag(true);
//			placeOrderContext.setSignFlag(false);
			/*//即时到账ISNTANT、担保交易ENSURE
			//placeOrderContext.setTradeType(VFOrderTypeEnum.TRADE_ENSURE);
			//placeOrderContext.setTradeList("[{\"out_trade_no\":\""+ tradeNo + "\",\"subject\":\"牛奶\",\"total_amount\":\"0.01\",\"ensure_amount\":\"1\",\"seller\":\"13621722085\",\"seller_type\":\"MOBILE\",\"price\":\"0.01\",\"quantity\":1}]");
			placeOrderContext.setTradeType(VFOrderTypeEnum.TRADE_INSTANT);
			placeOrderContext.setTradeList("[{\"out_trade_no\":\""+ tradeNo + "\",\"subject\":\"牛奶\",\"total_amount\":\"0.01\",\"seller\":\"13621722085\",\"seller_type\":\"MOBILE\",\"price\":\"0.01\",\"quantity\":1}]");
			//placeOrderContext.setPayMethod("online");*/
			this.showProgress();
//			SDKManager.getInstance().PlaceOrder(this,placeOrderContext,null);

			RequestParams reqParam = new RequestParams(placeOrderContext);
			reqParam.putData("service", "creat_pay_qrcode");
			reqParam.putData("token", SDKManager.token);
			reqParam.putData("request_no", placeOrderContext.getRequestNo());

			reqParam.putData("trade_list", placeOrderContext.getTradeList());
			reqParam.putData("pay_method", "[{\"pay_channel\":\"01\",\"amount\":" + bdTotalAmount.toString() + "}]");
			reqParam.putData("phone",phone);
//			String code = mAuthenMain.getCurrentCode(Base32String.encode(SDKManager.token.getBytes()));
//			String stringQRCode=mAuthenMain.getStringQRCode(number_pwd,et_buyer.getText().toString().trim());
//			L.e(Base32String.encode(SDKManager.token.getBytes()));
			reqParam.putData("number_pwd",number_pwd);
			reqParam.putData("qrcode",qrcode);
			HttpUtils.getInstance(this).excuteHttpRequest(HttpRequsetUri.getInstance()
					.getAcquirerDoUri(), reqParam, new VFinResponseHandler() {

				@Override
				public void onSuccess(Object responseBean, String responseString) {
					hideProgress();
					if (SDKManager.getInstance().getCallbackHandler() != null) {
						VFSDKResultModel result = new VFSDKResultModel();
						result.setResultCode(VFCallBackEnum.OK.getCode());
						result.setJsonData(responseString);
						placeOrderContext.sendMessage(result);
					}
				}

				@Override
				public void onError(String statusCode, String errorMsg) {
					hideProgress();
					if (SDKManager.getInstance().getCallbackHandler() != null) {
						VFSDKResultModel result = new VFSDKResultModel();
						result.setResultCode(statusCode);
						result.setMessage(errorMsg);
						placeOrderContext.sendMessage(result);
					}
				}

			}, this);
		}
	}
	
	private boolean checkOrder() {
		if(tradeTypeDataList.get(tradeTypeSpinner.getSelectedItemPosition()).equalsIgnoreCase("TRADE_ENSURE") == true) {
			if(TextUtils.isEmpty(etEnsureAmount.getText().toString()) == true) {
				Toast.makeText(this, "保证金不能为空！", Toast.LENGTH_LONG).show();
				return false;
			}
		}
		
		if(TextUtils.isEmpty(etSubject.getText().toString()) == true) {
			Toast.makeText(this, "Subject不能为空！", Toast.LENGTH_LONG).show();
			return false;
		}
		
		if(TextUtils.isEmpty(etPrice.getText().toString()) == true) {
			Toast.makeText(this, "价格不能为空！", Toast.LENGTH_LONG).show();
			return false;
		}
		
		if(TextUtils.isEmpty(etQuantity.getText().toString()) == true) {
			Toast.makeText(this, "数量不能为空！", Toast.LENGTH_LONG).show();
			return false;
		}
		if(TextUtils.isEmpty(etSeller.getText().toString()) == true) {
			Toast.makeText(this, "seller不能为空！", Toast.LENGTH_LONG).show();
			return false;
		}
		if(TextUtils.isEmpty(et_buyer.getText().toString()) == true) {
			Toast.makeText(this, "buyer不能为空！", Toast.LENGTH_LONG).show();
			return false;
		}
		
		return true;
	}
	
	private void backOnClick() {
		this.finish();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) { 
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			backOnClick();
		}  
		return super.onKeyDown(keyCode, event);
	}
}
