package cn.vfinance.demo;

import android.text.TextUtils;
import android.widget.ImageView;

import com.vfinworks.vfsdk.SDKManager;
import com.vfinworks.vfsdk.activity.core.channel.BaseChannel;
import com.vfinworks.vfsdk.alipay.AliPay;
import com.vfinworks.vfsdk.alipay.PayResult;
import com.vfinworks.vfsdk.context.BaseAcquireContext;
import com.vfinworks.vfsdk.enumtype.VFCallBackEnum;
import com.vfinworks.vfsdk.http.RequestParams;
import com.vfinworks.vfsdk.model.ChannelModel;
import com.vfinworks.vfsdk.model.VFSDKResultModel;

import org.json.JSONObject;

/**
 * Created by tangyijian on 2017/4/25.
 */

public class NewChannel extends BaseChannel {

    /**
     * 调用appserver 支付接口请求后的回调
     * @param status 回调成功的状态
     * @param payResult 回调后的返回结果参数
     * @param jsonObject 回调返回的结果json对象
     */
    @Override
    protected void goPay(String status, String payResult, JSONObject jsonObject) {
        doAliPay(status, payResult, jsonObject,mPaymentContext);
    }

    /**
     * 调用appserver 充值接口请求后的回调
     * @param status 回调成功的状态
     * @param stringResult 回调后的返回结果参数
     * @param json 回调返回的结果json对象
     */
    @Override
    protected void goRecharge(String status, String stringResult, JSONObject json) {
        doAliPay(status, stringResult, json,mRechargeContext);
    }


    /**
     * 调用appserver 转账接口请求后的回调
     * @param status 回调成功的状态
     * @param stringResult 回调后的返回结果参数
     * @param json 回调返回的结果json对象
     */
    @Override
    protected void goTransferToAccount(String status, String stringResult, JSONObject json) {
        doAliPay(status,stringResult,json,mTransferContext);
    }

    /**
     * 渠道显示的名称
     * @param channel
     * @return
     */
    @Override
    public String getName(ChannelModel channel) {
        return "支付宝支付";
    }

    /**
     * 返回渠道
     * @return
     * 参数是query_channel 返回的inst_code和pay_mode
     *
     * 如下测试用例:将("WXPAY10101","NETBANK")作为alipay的渠道
     */
    @Override
    public ChannelModel getChannelModel() {
//        return new ChannelModel("ALIPAY10401","NETBANK");
//        return new ChannelModel("WXPAY10101","NETBANK");
        return new ChannelModel("TESTBANK10110","QUICKPAY");
    }

    /**
     * 返回渠道的图标
     * @param imageview
     */
    @Override
    public void setDrawableIcon(ImageView imageview) {
        imageview.setImageResource(R.drawable.vf_zfb_icon);
    }

    //自定义支付推进:去调用ali的sdk
    private void doAliPay(String status, String payResult, JSONObject jsonObject, final BaseAcquireContext baseContext) {
        if (!TextUtils.isEmpty(payResult)) {
            //调用alipay sdk
            AliPay alipay = new AliPay(mContext, new AliPay.PayResultListener() {
                @Override
                public void onSuccess() {
                    //发送支付成功消息
                    channelResponseHandler.OnSuccess(baseContext.getInnerOrderNo());
                }
                @Override
                public void onFailed(PayResult payResult) {
                    //发送支付失败消息,关闭页面
                    mContext.finishAll();
                    if (SDKManager.getInstance().getCallbackHandler() != null) {
                        VFSDKResultModel result = new VFSDKResultModel();
                        result.setResultCode(VFCallBackEnum.PROCESS.getCode());
                        result.setMessage(payResult.getMemo());
                        baseContext.sendMessage(result);
                    }
                }

                @Override
                public void onProcess() {
                    //发送支付处理中消息,关闭页面
                    mContext.finishAll();
                    if (SDKManager.getInstance().getCallbackHandler() != null) {
                        VFSDKResultModel result = new VFSDKResultModel();
                        result.setResultCode(VFCallBackEnum.PROCESS.getCode());
                        result.setMessage("支付处理中");
                        baseContext.sendMessage(result);
                    }
                }

            });
            alipay.pay(payResult);
        } else {
            mContext.showShortToast("支付宝订单为空！");
        }
    }

    /**
     * 调用appserver 支付和下单接口时的请求参数
     * @param reqParam 参数数组
     * @param amount 这次请求的金额
     */
    @Override
    protected void initRequestParams(RequestParams reqParam,String amount) {
        //需要添加 pay_method字段的数据
        String strPayMethod = "[{\"pay_channel\":\"02\",\"amount\":" + amount + ",\"memo\":\"WXPAY,C,DC\"}]";
        reqParam.putData("pay_method", strPayMethod);
    }

    /**
     * 调用appserver 充值接口时的请求参数
     * @param reqParam 参数数组
     * @param amount 这次请求的金额
     */
    @Override
    protected void initRechargeRequestParams(RequestParams reqParam, String amount) {
        //需要添加 pay_method字段的数据 注意比上面少个[]
        String strPayMethod = "{\"pay_channel\":\"02\",\"amount\":" + amount + ",\"memo\":\"ALIPAY,C,DC\"}";
        reqParam.putData("pay_method", strPayMethod);
    }
}
