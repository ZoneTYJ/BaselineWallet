package com.vfinworks.vfsdk.business;

import android.content.Context;
import android.text.TextUtils;

import com.vfinworks.vfsdk.SDKManager;
import com.vfinworks.vfsdk.common.HttpRequsetUri;
import com.vfinworks.vfsdk.context.BaseContext;
import com.vfinworks.vfsdk.context.RealNameContext;
import com.vfinworks.vfsdk.enumtype.VFCallBackEnum;
import com.vfinworks.vfsdk.http.HttpUtils;
import com.vfinworks.vfsdk.http.RequestParams;
import com.vfinworks.vfsdk.http.VFinResponseHandler;
import com.vfinworks.vfsdk.model.RealNameInfoModel;
import com.vfinworks.vfsdk.model.VFSDKResultModel;

public class RealName {
	private Context mContext;
	private BaseContext mBaseContext;
	public RealName(Context context,BaseContext baseContext) {
		mContext = context;
		mBaseContext = baseContext;
	}
	
	public void doQuery() {
		RequestParams reqParam = new RequestParams();
		reqParam.putData("token", SDKManager.token);
		reqParam.putData("service", "query_certification");
		HttpUtils.getInstance(mContext).excuteHttpRequest(HttpRequsetUri.getInstance().getMemberDoUri(), reqParam, new VFinResponseHandler<RealNameInfoModel>(RealNameInfoModel.class){

			@Override
			public void onSuccess(RealNameInfoModel responseBean, String responseString) {

				if(SDKManager.getInstance().getCallbackHandler() != null) {

					VFSDKResultModel result = new VFSDKResultModel();

					String realName = responseBean.getRealName();
					if (TextUtils.isEmpty(realName)) {
						result.setResultCode(VFCallBackEnum.ERROR_CODE_NULL.getCode());
					}else{
						result.setResultCode(VFCallBackEnum.OK.getCode());
					}
					result.setJsonData(responseString);
					mBaseContext.sendMessage(result);
				}
			}

			@Override
			public void onError(String statusCode, String errorMsg) {
				if(SDKManager.getInstance().getCallbackHandler() != null) {
					VFSDKResultModel result = new VFSDKResultModel();
					result.setResultCode(statusCode);
					result.setMessage(errorMsg);
					mBaseContext.sendMessage(result);
				}
			}

		}, this);
	}	
	
	public void doRealName() {
		RequestParams reqParam = new RequestParams();
		reqParam.putData("token", SDKManager.token);
		reqParam.putData("service", "do_certification");
		RealNameContext realNameContext = (RealNameContext)mBaseContext;
		reqParam.putData("request_no", realNameContext.getRequestNo());
		reqParam.putData("cert_no", realNameContext.getCertNo());
		reqParam.putData("real_name", realNameContext.getRealName());
		reqParam.putData("auth_mode", realNameContext.getAuthMode());
		reqParam.putData("bank_no", realNameContext.getBankNo());
		reqParam.putData("mobile", realNameContext.getMobile());
		reqParam.putData("cvv2", realNameContext.getCvv2());
		reqParam.putData("endDate", realNameContext.getEndDate());
		HttpUtils.getInstance(mContext).excuteHttpRequest(HttpRequsetUri.getInstance().getMemberDoUri(), reqParam, new VFinResponseHandler(){
			@Override
			public void onSuccess(Object responseBean, String responseString) {
				if(SDKManager.getInstance().getCallbackHandler() != null) {
					VFSDKResultModel result = new VFSDKResultModel();
					result.setResultCode(VFCallBackEnum.OK.getCode());
					result.setJsonData(responseString);
					mBaseContext.sendMessage(result);
				}
			}

			@Override
			public void onError(String statusCode, String errorMsg) {
				if(SDKManager.getInstance().getCallbackHandler() != null) {
					VFSDKResultModel result = new VFSDKResultModel();
					result.setResultCode(statusCode);
					result.setMessage(errorMsg);
					mBaseContext.sendMessage(result);
				}
			}

		}, this);
	}
}
