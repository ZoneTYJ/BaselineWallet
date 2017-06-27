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
import com.vfinworks.vfsdk.common.Utils;
import com.vfinworks.vfsdk.context.PlaceOrderContext;
import com.vfinworks.vfsdk.enumtype.VFCallBackEnum;
import com.vfinworks.vfsdk.model.VFSDKResultModel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PlaceOrderActivity extends BaseActivity implements OnClickListener{
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
	private Button btnOrder;
	
	private int placeOrdercallbackMessageID = 1;
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if(msg.what == placeOrdercallbackMessageID) {
				handlePlaceOrderCallback((VFSDKResultModel)msg.obj);
			}
		};
	};
	
	private void handlePlaceOrderCallback(VFSDKResultModel resultModel) {
		PlaceOrderActivity.this.hideProgress();
		if(resultModel.getResultCode().equalsIgnoreCase(VFCallBackEnum.OK.getCode()) == true) {
			Toast.makeText(PlaceOrderActivity.this, resultModel.getJsonData(), Toast.LENGTH_LONG).show();
			Intent intent = new Intent();
			intent.putExtra("orderNum", orderNum);
			intent.putExtra("orderAmount", orderAmount);
			this.setResult(RESULT_OK,intent);
			this.finish();
		}else{
			Toast.makeText(PlaceOrderActivity.this, resultModel.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.setIsPush(false);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_place_order);
		token = this.getIntent().getExtras().getString("token");
		
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
		btnOrder = (Button) findViewById(R.id.btn_order);
		btnOrder.setOnClickListener(this);
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
			PlaceOrderContext placeOrderContext = new PlaceOrderContext();
			placeOrderContext.setToken(token);
			placeOrderContext.setCallBackMessageId(placeOrdercallbackMessageID);
			String tradeNo = Utils.getOnlyValue();
			placeOrderContext.setRequestNo(tradeNo);
			orderNum = tradeNo;
			
			BigDecimal bdTotalAmount = new BigDecimal("0.00");
			BigDecimal bdPrice = new BigDecimal(etPrice.getText().toString());
			BigDecimal bdQuantity = new BigDecimal(etQuantity.getText().toString());
			bdTotalAmount = bdPrice.multiply(bdQuantity);
			bdTotalAmount=bdTotalAmount.setScale(2, BigDecimal.ROUND_HALF_UP);
			
			orderAmount = bdTotalAmount.toString();
			
			if(tradeTypeDataList.get(tradeTypeSpinner.getSelectedItemPosition()).equalsIgnoreCase
					("TRADE_ENSURE")) {
				placeOrderContext.generateEnsureTradeList(tradeNo,etSubject.getText().toString(),bdTotalAmount.toString(),etEnsureAmount.getText().toString(),
						etSeller.getText().toString(),sellerTypeDataList.get(sellerTypeSpinner.getSelectedItemPosition()),etPrice.getText().toString(),
						etQuantity.getText().toString(),"notifyUrl","appExtension");
			}else{
				placeOrderContext.generateInstantTradeList(tradeNo,etSubject.getText().toString(),bdTotalAmount.toString(),
						etSeller.getText().toString(),sellerTypeDataList.get(sellerTypeSpinner.getSelectedItemPosition()),etPrice.getText().toString(),
						etQuantity.getText().toString(),"notifyUrl","appExtension");
			}
			placeOrderContext.setSignFlag(true);
			this.showProgress();
			SDKManager.getInstance().PlaceOrder(this,placeOrderContext,mHandler);
		}
	}

	@Override
	protected void onDestroy() {
		SDKManager.getInstance().clear();
		super.onDestroy();
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
