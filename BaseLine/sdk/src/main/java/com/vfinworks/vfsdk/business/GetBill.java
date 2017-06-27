package com.vfinworks.vfsdk.business;

import android.content.Context;

import com.vfinworks.vfsdk.SDKManager;
import com.vfinworks.vfsdk.common.HttpRequsetUri;
import com.vfinworks.vfsdk.context.BillContext;
import com.vfinworks.vfsdk.enumtype.VFCallBackEnum;
import com.vfinworks.vfsdk.http.HttpUtils;
import com.vfinworks.vfsdk.http.RequestParams;
import com.vfinworks.vfsdk.http.VFinResponseHandler;
import com.vfinworks.vfsdk.model.VFSDKResultModel;

/**
 * Created by xiaoshengke on 2016/4/8.
 */
public class GetBill {
    private Context mContext;
    private BillContext billContext;
    public GetBill(Context context,BillContext billContext) {
        mContext = context;
        this.billContext = billContext;
    }

    /**
     * 获取账单列表
     *  tradeType
     *  交易类型，传字符串：INSTANT表示即时到账，ENSURE表示担保，TRANSFER表示转账，REFUND表示退款，DEPOSIT表示充值，WITHDRAW表示提现
     *  startTime
     *  开始时间，一共14位,格式为：年[4位]月[2位]日[2位]时[2位]分[2位]秒[2位]，例：20140617020101
     *  endTime
     *  结束时间，格式与开始时间一样
     *  pageNo
     *  页码
     *  pageSize
     *  每页大小
     */
    public void getBillList() {
        RequestParams reqParam = new RequestParams(billContext);
        reqParam.putData("service", "query_trade_list");
        reqParam.putData("token", SDKManager.token);
        reqParam.putData("trade_type", billContext.getTradeType());
        reqParam.putData("start_time", billContext.getStartTime());
        reqParam.putData("end_time", billContext.getEndTime());
        reqParam.putData("page_no", billContext.getPageNo()+"");
        reqParam.putData("page_size", billContext.getPageSize() + "");
        HttpUtils.getInstance(mContext).excuteHttpRequest(HttpRequsetUri.getInstance().getAcquirerDoUri(), reqParam,
                new VFinResponseHandler() {
                    @Override
                    public void onSuccess(Object responseBean, String responseString) {
                        if(SDKManager.getInstance().getCallbackHandler() != null) {
                            VFSDKResultModel result = new VFSDKResultModel();
                            result.setResultCode(VFCallBackEnum.OK.getCode());
                            result.setJsonData(responseString);
                            billContext.sendMessage(result);
                        }
                    }

                    @Override
                    public void onError(String statusCode, String errorMsg) {
                        if(SDKManager.getInstance().getCallbackHandler() != null) {
                            VFSDKResultModel result = new VFSDKResultModel();
                            result.setResultCode(statusCode);
                            result.setMessage(errorMsg);
                            billContext.sendMessage(result);
                        }
                    }
                }, this);
    }

}
