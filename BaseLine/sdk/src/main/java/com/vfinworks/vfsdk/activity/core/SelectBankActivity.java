package com.vfinworks.vfsdk.activity.core;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;

import com.vfinworks.vfsdk.SDKManager;
import com.vfinworks.vfsdk.activity.core.base.SearchListBaseActivity;
import com.vfinworks.vfsdk.common.HttpRequsetUri;
import com.vfinworks.vfsdk.http.HttpUtils;
import com.vfinworks.vfsdk.http.RequestParams;
import com.vfinworks.vfsdk.http.VFinResponseHandler;
import com.vfinworks.vfsdk.model.AddNewBankModel;
import com.vfinworks.vfsdk.model.BankList;
import com.vfinworks.vfsdk.model.BankModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class SelectBankActivity extends SearchListBaseActivity {
	private BankList listBankData = new BankList();
	private AddNewBankModel addNewBankModel;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addNewBankModel = (AddNewBankModel) getIntent().getSerializableExtra("addNewBankModel");
        this.getTitlebarView().setTitle("选择银行");
        this.getTitlebarView().initLeft(new OnClickListener() {
			@Override
			public void onClick(View v) {
				backOnClick();
			}
		});
		layout_search_text.setVisibility(View.GONE);
		getBankList();
    }

	@Override
	protected void doItemClick(String name) {
		BankModel bankModel=listBankData.getBankModel(name);
		if(bankModel!=null){
			addNewBankModel.setBankModel(bankModel);
			addNewBankModel.setType(AddNewBankModel.BANK_TYPE);
			Intent intent = new Intent();
			intent.putExtra("addNewBankModel", addNewBankModel);
			setResult(RESULT_OK, intent);
			finishActivity();
		}else {
			showShortToast("没有找到对应的银行");
		}
	}

	private void getBankList() {
		RequestParams reqParam = new RequestParams();
		reqParam.putData("service", "query_bank");
		reqParam.putData("token", SDKManager.token);
		reqParam.putData("Prov_id", String.valueOf(addNewBankModel.getProvinceInfoModel().getProvId()));
		reqParam.putData("city_id", String.valueOf(addNewBankModel.getCityInfoModel().getCityId()));
		reqParam.putData("type","O");
		HttpUtils.getInstance(this).excuteHttpRequest(HttpRequsetUri.getInstance().getMemberDoUri(), reqParam, new VFinResponseHandler<BankList>(BankList.class){

			@Override
			public void onSuccess(BankList responseBean, String responseString) {
				hideProgress();
				listBankData=responseBean;
				setDatas(responseBean.getNameStringList());
			}

			@Override
			public void onError(String statusCode, String errorMsg) {
				hideProgress();
				showShortToast(errorMsg);
			}

		}, this);
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

}
