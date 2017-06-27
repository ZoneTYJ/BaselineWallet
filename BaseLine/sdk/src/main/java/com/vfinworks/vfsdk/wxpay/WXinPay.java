package com.vfinworks.vfsdk.wxpay;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 微信支付类
 * Created by tangyijian on 2016/4/20.
 */
public class WXinPay {
    private String TAG = "WXinPay";
    private Context mContext;
    private IWXAPI api;
    private final PayResultListener mPayResultListener;

    public WXinPay(Context context, PayResultListener payListener) {
        mContext = context;
        mPayResultListener = payListener;
    }

    public void goWeixinPay(String htmlform) {
        try {
//            Map<String, String> map = Utils.getHTMLFormMaps(htmlform);
//            String strJson = map.get("value");
            String strJson=htmlform;
            final JSONObject json = new JSONObject(strJson);
            if (null != json) {
                Log.d(TAG, strJson);
                PayReq req = new PayReq();
                //req.appId = "wxf8b4f85f3a794e77";  // 测试用appId
                try {
                    req.appId = json.getString("appid");
                    if (api == null) {
                        api = WXAPIFactory.createWXAPI(mContext, null);
                        boolean rea = api.registerApp(req.appId);
                        Log.i("registerApp","注册:"+rea);
                    }
                    if (!api.isWXAppInstalled()) {
                        if (mPayResultListener != null) {
                            mPayResultListener.onUnstalled("没有安装微信");
                            return;
                        }
                    } else if (!api.isWXAppSupportAPI()) {
                        if (mPayResultListener != null) {
                            mPayResultListener.onUnstalled("微信版本不支持");
                            return;
                        }
                    }
                    req.partnerId = json.getString("partnerid");
                    req.prepayId = json.getString("prepayid");
                    req.nonceStr = json.getString("noncestr");
                    req.timeStamp = json.getString("timestamp");
                    req.packageValue = json.getString("package");
                    req.sign = json.getString("sign");

                    boolean rea = api.sendReq(req);
                } catch (JSONException e) {
                    Log.d(TAG, "JSON 解析异常");
                    Toast.makeText(mContext, "JSON 解析异常", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.d(TAG, "JSON 为空");
                Toast.makeText(mContext, "JSON 为空", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e1) {
            e1.printStackTrace();
            Toast.makeText(mContext, "表单数据异常", Toast.LENGTH_SHORT).show();
        }
    }

    public interface PayResultListener {
        void onUnstalled(String msg);
    }
}
