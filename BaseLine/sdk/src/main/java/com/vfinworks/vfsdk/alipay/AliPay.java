package com.vfinworks.vfsdk.alipay;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.alipay.sdk.app.PayTask;

public class AliPay {
	private Context mContext;
	private final int SDK_PAY_FLAG = 1;
	private final int SDK_CHECK_FLAG = 2;
	private PayResultListener payResultListener;

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case SDK_PAY_FLAG: {
					PayResult payResult = new PayResult((String) msg.obj);
					// 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
					String resultInfo = payResult.getResult();

					String resultStatus = payResult.getResultStatus();
					// 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
					if (TextUtils.equals(resultStatus, "9000")) {
						//Toast.makeText(PayDemoActivity.this, "支付成功",Toast.LENGTH_SHORT).show();
						if (payResultListener != null)
							payResultListener.onSuccess();
					} else {
						// 判断resultStatus 为非“9000”则代表可能支付失败
						// “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
						if (TextUtils.equals(resultStatus, "8000")) {
							//Toast.makeText(PayDemoActivity.this, "支付结果确认中",Toast.LENGTH_SHORT)
							// .show();
							if (payResultListener != null)
								payResultListener.onProcess();

						} else if (TextUtils.equals(resultStatus, "6001")) {
							//用户手动取消
						} else {
							// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
							//Toast.makeText(PayDemoActivity.this, "支付失败",Toast.LENGTH_SHORT)
							// .show();
							if (payResultListener != null)
								payResultListener.onFailed(payResult);
						}
					}
					break;
				}
				case SDK_CHECK_FLAG: {
					//Toast.makeText(PayDemoActivity.this, "检查结果为：" + msg.obj,Toast.LENGTH_SHORT)
					// .show();
					break;
				}
				default:
					break;
			}
		}

		;
	};

	public AliPay(Context context, PayResultListener payListener) {
		mContext = context;
		payResultListener = payListener;
	}

	public void pay(final String orderInfo) {
		if (TextUtils.isEmpty(orderInfo)) {
			return;
		}

		Runnable payRunnable = new Runnable() {

			@Override
			public void run() {
				// 构造PayTask 对象
				PayTask alipay = new PayTask((Activity) mContext);
				// 调用支付接口，获取支付结果
				String result = alipay.pay(orderInfo, true);

				Message msg = new Message();
				msg.what = SDK_PAY_FLAG;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		};

		// 必须异步调用
		Thread payThread = new Thread(payRunnable);
		payThread.start();
	}

	public interface PayResultListener {
		void onSuccess();

		void onFailed(PayResult payResult);

		void onProcess();
	}
}
