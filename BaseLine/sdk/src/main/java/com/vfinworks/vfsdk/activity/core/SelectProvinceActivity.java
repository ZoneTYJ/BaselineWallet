package com.vfinworks.vfsdk.activity.core;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;

import com.vfinworks.vfsdk.SDKManager;
import com.vfinworks.vfsdk.activity.core.base.SideBarListBaseActivity;
import com.vfinworks.vfsdk.common.HttpRequsetUri;
import com.vfinworks.vfsdk.http.HttpUtils;
import com.vfinworks.vfsdk.http.RequestParams;
import com.vfinworks.vfsdk.http.VFinResponseHandler;
import com.vfinworks.vfsdk.model.AddNewBankModel;
import com.vfinworks.vfsdk.model.ProvinceInfoModel;
import com.vfinworks.vfsdk.model.ProvinceListInfo;


public class SelectProvinceActivity extends SideBarListBaseActivity {
    private AddNewBankModel addNewBankModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addNewBankModel = (AddNewBankModel) getIntent().getSerializableExtra("addNewBankModel");
        this.getTitlebarView().setTitle("选择省市");
        this.getTitlebarView().initLeft(new OnClickListener() {
            @Override
            public void onClick(View v) {
                backOnClick();
            }
        });
        getProvinceAndBankLst();
        initData();
    }

    private void initData() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                addNewBankModel.setProvinceInfoModel( ((ProvinceInfoModel)sortadapter.getItem(position)));
                addNewBankModel.setType(AddNewBankModel.PROVINCE_TYPE);
                Intent intent = new Intent();
                intent.putExtra("addNewBankModel",addNewBankModel);
                setResult(RESULT_OK, intent);
                finishActivity();
            }
        });
    }

    private void getProvinceAndBankLst() {
        this.showProgress();
        RequestParams reqParam = new RequestParams();
        reqParam.putData("service", "query_province");
        reqParam.putData("token", SDKManager.token);
        HttpUtils.getInstance(this).excuteHttpRequest(HttpRequsetUri.getInstance().getMemberDoUri(),
                reqParam, new VFinResponseHandler<ProvinceListInfo>(ProvinceListInfo.class) {

            @Override
            public void onSuccess(ProvinceListInfo responseBean, String responseString) {
                hideProgress();
                responseBean.setPYNames();
                setDatas(responseBean.getProvince());
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
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            backOnClick();
        }
        return super.onKeyDown(keyCode, event);
    }

}
