package com.vfinworks.vfsdk.activity.core;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;

import com.vfinworks.vfsdk.R;
import com.vfinworks.vfsdk.SDKManager;
import com.vfinworks.vfsdk.common.HttpRequsetUri;
import com.vfinworks.vfsdk.http.HttpUtils;
import com.vfinworks.vfsdk.http.RequestParams;
import com.vfinworks.vfsdk.http.VFinResponseHandler;
import com.vfinworks.vfsdk.model.AddNewBankModel;
import com.vfinworks.vfsdk.model.CityInfoModel;
import com.vfinworks.vfsdk.activity.core.base.SideBarListBaseActivity;
import com.vfinworks.vfsdk.model.CityListInfo;

import java.util.ArrayList;
import java.util.List;


public class SelectCityActivity extends SideBarListBaseActivity {
	private AddNewBankModel addNewBankModel;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.select_city_activity,FLAG_TITLE_LINEARLAYOUT);
		addNewBankModel = (AddNewBankModel) getIntent().getSerializableExtra("addNewBankModel");
        super.onCreate(savedInstanceState);
        this.getTitlebarView().setTitle("选择城市");
        this.getTitlebarView().initLeft(new OnClickListener() {
			@Override
			public void onClick(View v) {
				backOnClick();
			}
		});
        getCityLst();
		initData();
    }

	private void initData() {
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				addNewBankModel.setCityInfoModel((CityInfoModel)sortadapter.getItem(position));
				addNewBankModel.setType(AddNewBankModel.CITY_TYPE);
				Intent intent = new Intent();
				intent.putExtra("addNewBankModel", addNewBankModel);
				setResult(RESULT_OK, intent);
				finishActivity();
			}
		});
	}

	private void getCityLst() {
		RequestParams reqParam = new RequestParams();
		reqParam.putData("service", "query_city");
		reqParam.putData("token", SDKManager.token);
		reqParam.putData("Prov_id", String.valueOf(addNewBankModel.getProvinceInfoModel().getProvId()));
		HttpUtils.getInstance(this).excuteHttpRequest(HttpRequsetUri.getInstance().getMemberDoUri(), reqParam, new VFinResponseHandler<CityListInfo>(CityListInfo.class){

			@Override
			public void onSuccess(CityListInfo responseBean, String responseString) {
				hideProgress();
				responseBean.setPYNames();
				setDatas(responseBean.getCity());
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

