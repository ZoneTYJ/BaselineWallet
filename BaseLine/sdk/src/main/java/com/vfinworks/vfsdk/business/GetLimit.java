package com.vfinworks.vfsdk.business;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vfinworks.vfsdk.SDKManager;
import com.vfinworks.vfsdk.common.HttpRequsetUri;
import com.vfinworks.vfsdk.context.LimitContext;
import com.vfinworks.vfsdk.http.HttpUtils;
import com.vfinworks.vfsdk.http.RequestParams;
import com.vfinworks.vfsdk.http.VFinResponseHandler;
import com.vfinworks.vfsdk.model.LimitModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class GetLimit {
    private Context mContext;
    private LimitContext mLimitContext;

    public GetLimit(Context context, LimitContext limitContext) {
        mContext = context;
        mLimitContext = limitContext;
    }

    public void doLimit(final LimitResponseHandler limitResponseHandler) {
        RequestParams reqParam = new RequestParams();
        reqParam.putData("service", "query_lflt");
        reqParam.putData("token", SDKManager.token);
        reqParam.putData("transfer_Type", mLimitContext.getTransfer_Type());
        reqParam.putData("payChannel", mLimitContext.getPayChannel());
        HttpUtils.getInstance(mContext).excuteHttpRequest(HttpRequsetUri.getInstance().getMemberDoUri(),
                reqParam, new VFinResponseHandler() {
                    @Override
                    public void onSuccess(Object responseBean, String responseString) {
                        JSONObject json;
                        try {
                            json = new JSONObject(responseString);
                            if (!json.isNull("result")) {
                                String result = json.getString("result");
                                List<LimitModel> results = new Gson().fromJson(result, new
                                        TypeToken<List<LimitModel>>() {
                                        }.getType());
                                limitResponseHandler.onSuccess(results);
                            } else {
                                limitResponseHandler.onSuccess(null);
                            }
                        } catch (JSONException e) {
                            limitResponseHandler.onError("", "数据解析有误");
                        }
                    }

                    @Override
                    public void onError(String statusCode, String errorMsg) {
                        limitResponseHandler.onError(statusCode, errorMsg);
                    }
                }, mContext);

    }

    public interface LimitResponseHandler {
        public void onSuccess(List<LimitModel> results);

        public void onError(String statusCode, String errorMsg);
    }
}
