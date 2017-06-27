package com.vfinworks.vfsdk.http;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.vfinworks.vfsdk.common.Utils;

import android.text.TextUtils;

/**
 * 请求返回
 * @param <JSON_TYPE> 服务器返回的数据目标转换类型
 */
public abstract class VFinResponseHandler<JSON_TYPE> {
	/*
	 * 服务器返回的json字符串转换的JavaBean类型
	 */
	private Class<JSON_TYPE> mBeanType = null;
	
	public VFinResponseHandler() {	
		
	}

	public VFinResponseHandler(Class<JSON_TYPE> beanType) {
		this.mBeanType = beanType;
	}

	/**
	 * 成功返回到客户端
	 * @param responseBean 服务器返回的json数据转换后的JavaBean
	 * @param responseString 服务器返回信息
	 */
	public abstract void onSuccess(JSON_TYPE responseBean,String responseString);

	/**
	 * 错误返回到客户端
	 * 
	 * @param statusCode 错误码
	 * @param errorMsg  错误信息 
	 */
	public abstract void onError(String statusCode, String errorMsg);
	
	public <T> void httpResponseSuccess(T result) {
		if( result instanceof String) {
			//判断是不是成功
			//todo
			if(mBeanType != null) {
				JSON_TYPE object = Utils.getInstance().json2Object((String)result,mBeanType);
				onSuccess(object,(String)result);
			}else{
				onSuccess(null,(String)result);
			}
		}
	}
	
	public void httpResponseError(String strStatusCode,String strErorMsg) {
		onError(strStatusCode,strErorMsg);
	}
}
