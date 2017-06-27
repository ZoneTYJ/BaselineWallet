package com.vfinworks.vfsdk.wxpay;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler{
	
	private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";
	
    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	api = WXAPIFactory.createWXAPI(this, null);
        api.handleIntent(getIntent(), this);
    }

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
        api.handleIntent(intent, this);
	}

	/**
	 * 请求到第三方应用时，会回调到该方法
	 *
	 * @param baseReq
	 */
	@Override
	public void onReq(BaseReq baseReq) {
	}

	/**
	 * 微信的请求处理后的响应结果，会回调到该方法
	 *
	 * @param baseResp
	 */
	@Override
	public void onResp(BaseResp baseResp) {
		Log.i("VFWechatPaymentActivity", "onPayFinish, result code = " + baseResp.errCode);
		String result = "FAIL";
		boolean isSuccess;
		String detailInfo = "";
		if (baseResp.errCode == 0) {
			detailInfo="用户支付已成功";
			isSuccess=true;
//			Message.obtain(WXHandler.getInstance(), WXHandler.OK).sendToTarget();
		} else {
			switch (baseResp.errCode) {
				case -5:
					detailInfo = detailInfo + "不支持错误";
					isSuccess=false;
					break;
				case -4:
					detailInfo = detailInfo + "发送被拒绝";
					isSuccess=false;
					break;
				case -3:
					detailInfo = detailInfo + "发送失败";
					isSuccess=false;
					break;
				case -2:
					result = "CANCEL";
					detailInfo = detailInfo + "微信支付取消";
					isSuccess=false;
					break;
				case -1:
					detailInfo = detailInfo + "一般错误，微信Debug版本常见错误，请查看维金技术FAQ: https://www" +
							".vfnetwork.cn/page/devdoc/doclistindex?doclistCode=INTRO";
					isSuccess=false;
					break;
				default:
					detailInfo = detailInfo + "支付失败";
					isSuccess=false;
			}
		}
		Toast.makeText(this,detailInfo,Toast.LENGTH_SHORT).show();
		Message msg=WXHandler.getInstance().obtainMessage();
		msg.obj=isSuccess;
		msg.what=WXHandler.OK;
		WXHandler.getInstance().sendMessage(msg);
		finish();
	}
}