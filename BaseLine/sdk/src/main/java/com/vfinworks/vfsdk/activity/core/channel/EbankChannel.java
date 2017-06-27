package com.vfinworks.vfsdk.activity.core.channel;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.vfinworks.vfsdk.activity.BaseActivity;
import com.vfinworks.vfsdk.activity.core.VFWebViewActivity;
import com.vfinworks.vfsdk.http.RequestParams;
import com.vfinworks.vfsdk.model.ChannelModel;
import com.vfinworks.vfsdk.model.QpayNewBankCardModel;

import org.json.JSONObject;

/**
 * Created by tangyijian on 2016/5/13.
 */
//@ChannelAnnotation(inst_code = "UPOP",pay_mode = "NETBANK")
public class EbankChannel extends BaseChannel {
    public EbankChannel(){
        super();
    }
    public EbankChannel(BaseActivity context, ChannelResponseHandler
            channelResponseHandler, boolean isNewCard, QpayNewBankCardModel newBank, String bankCardId) {
        super.setChannelPara(context, channelResponseHandler, isNewCard, newBank, bankCardId);
    }

    @Override
    protected void goPay(String status, String payResult, JSONObject jsonObject) {
        doStartWeb(status, payResult, jsonObject);
    }

    @Override
    protected void goRecharge(String status, String stringResult, JSONObject json) {
        doStartWeb(status,stringResult,json);
    }

    @Override
    protected void goTransferToAccount(String status, String stringResult, JSONObject json) {
        doStartWeb(status,stringResult,json);
    }

    @Override
    public String getName(ChannelModel channel) {
        return "银联卡支付";
    }

    @Override
    public ChannelModel getChannelModel() {
        return null;
    }

    @Override
    public void setDrawableIcon(ImageView imageview) {
        return;
    }

    private void doStartWeb(String status, String payResult, JSONObject jsonObject) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html;charset=utf-8\"/><title> </title></head><script language=\"JavaScript\" type=\"text/javascript\">")
                .append("function postForm()\n" +
                        "\t\t{\n" +
                        "\t\t\tdocument.getElementById('frmBankID').submit();\n" +
                        "\t\t}")
                .append("</script><body>")
                .append(jsonObject.optString("pay_result"))
                .append("</body>")
                .append("</html>\n");
        Intent intent = new Intent(mContext, VFWebViewActivity.class);
        intent.putExtra("content", sb.toString());
        Bundle bundle = new Bundle();
        bundle.putSerializable("context", mPaymentContext);
        intent.putExtras(bundle);
        mContext.startActivity(intent);
    }


    @Override
    protected void initRequestParams(RequestParams reqParam,String amount) {
        String strPayMethod = "[{\"pay_channel\":\"02\",\"amount\":" + amount
                + ",\"memo\":\"TESTBANK,C,DC\"}]";
        reqParam.putData("pay_method", strPayMethod);
        reqParam.putData("access_channel", "WEB");
    }

    @Override
    protected void initRechargeRequestParams(RequestParams reqParam, String amount) {
        String strPayMethod = "{\"pay_channel\":\"02\",\"amount\":" + amount
                + ",\"memo\":\"TESTBANK,C,DC\"}";
        reqParam.putData("pay_method", strPayMethod);
        reqParam.putData("access_channel", "WEB");
    }
}
