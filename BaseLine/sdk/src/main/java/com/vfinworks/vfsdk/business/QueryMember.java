package com.vfinworks.vfsdk.business;

import android.content.Context;

import com.vfinworks.vfsdk.SDKManager;
import com.vfinworks.vfsdk.common.HttpRequsetUri;
import com.vfinworks.vfsdk.context.BaseContext;
import com.vfinworks.vfsdk.enumtype.VFCallBackEnum;
import com.vfinworks.vfsdk.http.HttpUtils;
import com.vfinworks.vfsdk.http.RequestParams;
import com.vfinworks.vfsdk.http.VFinResponseHandler;
import com.vfinworks.vfsdk.model.VFSDKResultModel;

/**
 * Created by xiaoshengke on 2017/2/9.
 */

public class QueryMember {
    private Context mContext;
    private BaseContext mBaseContext;
    public QueryMember(Context context, BaseContext baseContext) {
        mContext = context;
        mBaseContext = baseContext;
    }

    public void getMemberInfo(){
        RequestParams reqParam = new RequestParams(mBaseContext);
        reqParam.putData("service", "query_member");
        reqParam.putData("token", SDKManager.token);
        HttpUtils.getInstance(mContext).excuteHttpRequest(HttpRequsetUri.getInstance().getMemberDoUri(), reqParam,
                new VFinResponseHandler() {
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
