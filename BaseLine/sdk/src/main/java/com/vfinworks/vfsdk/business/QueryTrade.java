package com.vfinworks.vfsdk.business;

import android.content.Context;

import com.vfinworks.vfsdk.common.HttpRequsetUri;
import com.vfinworks.vfsdk.model.BillModel;
import com.vfinworks.vfsdk.http.HttpUtils;
import com.vfinworks.vfsdk.http.RequestParams;
import com.vfinworks.vfsdk.http.VFinResponseHandler;


/**
 * Created by tangyijian on 2016/12/30.
 */
public class QueryTrade {
    public static final int MAX_COUNT=3;
    private int mCount;
    private String mOutNo,mToken;
    private Context mContext;

    public QueryTrade(Context context, String outNo, String token){
        mContext=context;
        mOutNo=outNo;
        mToken=token;
        mCount=0;
    }

    public void doQueryTrade(final QueryTradeResponseHandler queryTradeResponseHandler){
        mCount++;
        RequestParams reqParam = new RequestParams();
        reqParam.putData("service", "query_trade");
        reqParam.putData("token", mToken);
        reqParam.putData("outer_trade_no", mOutNo);
        if(queryTradeResponseHandler!=null){
            queryTradeResponseHandler.putData(reqParam);
        }
        HttpUtils.getInstance(mContext).excuteHttpRequest(HttpRequsetUri.getInstance().getAcquirerDoUri(),
                reqParam, new VFinResponseHandler<BillModel>(BillModel.class) {

                    @Override
                    public void onSuccess(BillModel responseBean, String responseString) {
                        if (responseBean.getTrade_info()!=null) {
                            queryTradeResponseHandler.onSuccess(mCount,responseBean.getTrade_info().getTrade_status());
                        } else {
                            queryTradeResponseHandler.onSuccess(mCount, "requery");
                        }
                    }

                    @Override
                    public void onError(String statusCode, String errorMsg) {
                        queryTradeResponseHandler.onError(mCount,errorMsg);
                    }
                }, mContext);
    }

    public interface QueryTradeResponseHandler {
         void onSuccess(int count, String result);
         void onError(int count, String result);
         void putData(RequestParams reqParam);
    }
}
