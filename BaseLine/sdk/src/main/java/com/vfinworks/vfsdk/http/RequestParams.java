package com.vfinworks.vfsdk.http;

import com.vfinworks.vfsdk.common.Config;
import com.vfinworks.vfsdk.context.BaseContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class RequestParams {
	protected ConcurrentHashMap<String, String> data = new ConcurrentHashMap<String, String>();
	protected ConcurrentHashMap<String, String> securityData = new ConcurrentHashMap<String, String>();
	
	public RequestParams() {
		initParams();
	}
	
	public RequestParams(BaseContext baseContext) {
		initParams();
		this.putData("sign", baseContext.getSign());
		this.putData("sign_type", baseContext.getSignType());
		//如果baseContext deviceId用于二维码支付时候用
		if(baseContext.getDeviceId()!=null){
			this.putData("device_id", baseContext.getDeviceId());
		}
		if(baseContext.getAppExtension()!=null) {
			this.putData("app_extension", baseContext.getAppExtension());
		}
	}

	private void initParams() {
		//设置公共参数
		this.putData("version", Config.getInstance().VERSION);
		this.putData("partner_id", Config.getInstance().PARTNER_ID);
		this.putData("app_id",Config.getInstance().APP_ID);
		this.putData("_input_charset", "utf-8");
		this.putData("device_id", Config.getInstance().getDeviceId());
		this.putData("access_channel", "SDK");
		this.putData("memo", "");
	}

    public void putData(String key, String value) {
    	if(value == null)
    		value = "";
    	data.put(key, value);
    }
    
    public void putSecurityData(String key, String value) {
    	securityData.put(key, value);
    }

    /*
     *  得到所有的参数
     */
    public Map<String,String> getParams() {
        //排序等操作
    	
        return data;
    }
}
