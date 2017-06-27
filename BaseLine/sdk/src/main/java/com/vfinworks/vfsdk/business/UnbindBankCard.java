package com.vfinworks.vfsdk.business;

import com.vfinworks.vfsdk.SDKManager;
import com.vfinworks.vfsdk.activity.BaseActivity;
import com.vfinworks.vfsdk.common.HttpRequsetUri;
import com.vfinworks.vfsdk.common.LocalDataUtils;
import com.vfinworks.vfsdk.context.UnbindBankCardContext;
import com.vfinworks.vfsdk.enumtype.VFCallBackEnum;
import com.vfinworks.vfsdk.http.HttpUtils;
import com.vfinworks.vfsdk.http.RequestParams;
import com.vfinworks.vfsdk.http.VFinResponseHandler;
import com.vfinworks.vfsdk.model.VFSDKResultModel;

public class UnbindBankCard {
	private BaseActivity mContext;
	private UnbindBankCardContext unbindBankCardContext;
	public UnbindBankCard(BaseActivity context,UnbindBankCardContext baseContext) {
		mContext = context;
		unbindBankCardContext = baseContext;
	}
	
	public void doUnbind() {
		RequestParams reqParam = new RequestParams();
		reqParam.putData("service", "unbind_bank_card");
		reqParam.putData("token", SDKManager.token);
		reqParam.putData("bank_account_id", unbindBankCardContext.getBankCardId());
		
		HttpUtils.getInstance(mContext).excuteHttpRequest(HttpRequsetUri.getInstance().getMemberDoUri(), reqParam, new VFinResponseHandler(){

			@Override
			public void onSuccess(Object responseBean, String responseString) {
				if(SDKManager.getInstance().getCallbackHandler() != null) {
					String lastPayBankId = LocalDataUtils.getInstance().getLastPayBank(unbindBankCardContext.getMobile()+"withdraw_bank_id");
					if(unbindBankCardContext.getBankCardId().equalsIgnoreCase(lastPayBankId)) {
						LocalDataUtils.getInstance().removeLastPayBankId(unbindBankCardContext.getMobile());
					}
					VFSDKResultModel result = new VFSDKResultModel();
					result.setResultCode(VFCallBackEnum.OK.getCode());
					result.setJsonData(responseString);
					unbindBankCardContext.sendMessage(result);
					mContext.finish();
				}
			}

			@Override
			public void onError(String statusCode, String errorMsg) {
				if(SDKManager.getInstance().getCallbackHandler() != null) {
					VFSDKResultModel result = new VFSDKResultModel();
					result.setResultCode(statusCode);
					result.setMessage(errorMsg);
					unbindBankCardContext.sendMessage(result);
				}
			}
    		
    	}, this);
	}
}
