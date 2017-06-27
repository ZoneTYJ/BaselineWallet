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
import com.vfinworks.vfsdk.model.BranchInfoModel;
import com.vfinworks.vfsdk.model.BranchListInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class SelectBranchActivity extends SearchListBaseActivity {
	
	private BranchListInfo listBranchData;
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
        getBranchLst();
    }

	@Override
	protected void doItemClick(String name) {
		BranchInfoModel branchInfoModel=listBranchData.getBankModel(name);
		if(branchInfoModel!=null){
			addNewBankModel.setBranchInfoModel(branchInfoModel);
			addNewBankModel.setType(AddNewBankModel.BRANCH_TYPE);
			Intent intent = new Intent();
			intent.putExtra("addNewBankModel", addNewBankModel);
			setResult(RESULT_OK, intent);
			finishActivity();
		}else {
			showShortToast("没有找到对应的银行");
		}
	}

	private void getBranchLst() {
		showProgress();
		RequestParams reqParam = new RequestParams();
		reqParam.putData("service", "query_branch");
		reqParam.putData("token", SDKManager.token);
		reqParam.putData("city_id", String.valueOf(addNewBankModel.getCityInfoModel().getCityId()));
		reqParam.putData("Bank_code",addNewBankModel.getBankModel().getBankCode());
		HttpUtils.getInstance(this).excuteHttpRequest(HttpRequsetUri.getInstance().getMemberDoUri(), reqParam, new VFinResponseHandler<BranchListInfo>(BranchListInfo.class){

			@Override
			public void onSuccess(BranchListInfo responseBean, String responseString) {
				hideProgress();
				listBranchData = responseBean;
				if(listBranchData.getNameStringList().size()==0){
					showShortToast("没有查到相关分支行");
				}
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
