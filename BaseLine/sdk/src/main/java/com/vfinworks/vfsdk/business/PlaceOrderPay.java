package com.vfinworks.vfsdk.business;

import android.content.Context;

import com.vfinworks.vfsdk.SDKManager;
import com.vfinworks.vfsdk.common.HttpRequsetUri;
import com.vfinworks.vfsdk.context.PlaceOrderPayContext;
import com.vfinworks.vfsdk.enumtype.VFOrderTypeEnum;
import com.vfinworks.vfsdk.http.HttpUtils;
import com.vfinworks.vfsdk.http.RequestParams;
import com.vfinworks.vfsdk.http.VFinResponseHandler;
import com.vfinworks.vfsdk.view.LoadingDialog;

/**
 * 收款码
 * Created by xiaoshengke on 2016/4/18.
 */
public class PlaceOrderPay {
    private final PlaceOrderPayResponseHandler responseHandler;
    private Context mContext;
    private PlaceOrderPayContext placeOrderContext;
    private LoadingDialog lsLoadingDialog;

    public PlaceOrderPay(Context activity, PlaceOrderPayContext placeOrderContext,PlaceOrderPayResponseHandler vFinResponseHandler) {
        this.mContext = activity;
        this.placeOrderContext = placeOrderContext;
        lsLoadingDialog = new LoadingDialog(activity);
        responseHandler=vFinResponseHandler;
    }



    public void placeOrderPay(){
        lsLoadingDialog.show();

        String strTradeList = "[{\"out_trade_no\":\"" + placeOrderContext.getRequestNo() + "\",\"subject\":\"" + placeOrderContext.getProductName() + "\",\"total_amount\":\"" +
                placeOrderContext.getOrderAmount() + "\",";
        placeOrderContext.setTradeType(VFOrderTypeEnum.TRADE_INSTANT);
        strTradeList = strTradeList + "\"seller\":\"" + placeOrderContext.getSeller() + "\",\"seller_type\":\"" +
                "MOBILE" + "\",\"price\":\"" + placeOrderContext.getOrderAmount() +
                "\",\"quantity\":" + "1" + "}]";
        placeOrderContext.setTradeList(strTradeList);
        placeOrderContext.setSignFlag(true);
//			placeOrderContext.setSignFlag(false);
			/*//即时到账ISNTANT、担保交易ENSURE
			//placeOrderContext.setTradeType(VFOrderTypeEnum.TRADE_ENSURE);
			//placeOrderContext.setTradeList("[{\"out_trade_no\":\""+ tradeNo + "\",\"subject\":\"牛奶\",\"total_amount\":\"0.01\",\"ensure_amount\":\"1\",\"seller\":\"13621722085\",\"seller_type\":\"MOBILE\",\"price\":\"0.01\",\"quantity\":1}]");
			placeOrderContext.setTradeType(VFOrderTypeEnum.TRADE_INSTANT);
			placeOrderContext.setTradeList("[{\"out_trade_no\":\""+ tradeNo + "\",\"subject\":\"牛奶\",\"total_amount\":\"0.01\",\"seller\":\"13621722085\",\"seller_type\":\"MOBILE\",\"price\":\"0.01\",\"quantity\":1}]");
			//placeOrderContext.setPayMethod("online");*/
//			SDKManager.getInstance().PlaceOrder(this,placeOrderContext,null);

        RequestParams reqParam = new RequestParams(placeOrderContext);
        reqParam.putData("service", "creat_pay_qrcode");
        reqParam.putData("token", SDKManager.token);
        reqParam.putData("request_no", placeOrderContext.getRequestNo());

        reqParam.putData("trade_list", placeOrderContext.getTradeList());
        reqParam.putData("pay_method", "[{\"pay_channel\":\"01\",\"amount\":" + placeOrderContext.getOrderAmount() + "}]");
        reqParam.putData("phone",placeOrderContext.getBuyer());
//			String code = mAuthenMain.getCurrentCode(Base32String.encode(SDKManager.token.getBytes()));
//			String stringQRCode=mAuthenMain.getStringQRCode(number_pwd,et_buyer.getText().toString().trim());
//			L.e(Base32String.encode(SDKManager.token.getBytes()));
        reqParam.putData("number_pwd",placeOrderContext.getNumber_pwd());
        reqParam.putData("qrcode",placeOrderContext.getQrcode());
        HttpUtils.getInstance(mContext).excuteHttpRequest(HttpRequsetUri.getInstance()
                .getAcquirerDoUri(), reqParam, new VFinResponseHandler() {

            @Override
            public void onSuccess(Object responseBean, String responseString) {
                lsLoadingDialog.dismiss();
                if(responseHandler!=null) {
                    responseHandler.onSuccess(responseBean, responseString);
                }
            }

            @Override
            public void onError(String statusCode, String errorMsg) {
                lsLoadingDialog.dismiss();
                if(responseHandler!=null) {
                    responseHandler.onError(statusCode,errorMsg);;
                }
            }

        }, this);
    }

    public interface PlaceOrderPayResponseHandler {
        void onSuccess(Object responseBean, String responseString);
        void onError(String statusCode, String errorMsg);
    }

}
