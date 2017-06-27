package com.vfinworks.vfsdk.common;

import android.bluetooth.BluetoothAdapter;
import android.text.TextUtils;

public class Config {
    public static final String LOGIN_CLASS = "com.vfinworks.vfsdk.activity.login.LoginActivity";
	private static Config mInstance;
	//APP service 版本
	public String VERSION = "1.0";
	//合作者ID
	public static String PARTNER_ID = "188888888888";
    public static String staticResourceDir = "http://base.vfinance.cn/static/resources/help";
    public static String APP_ID = "f1";
	//设备id
	private String DEVICE_ID = null;
    //密钥
    private String key="MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAO/6rPCvyCC+IMalLzTy3cVBz" +
            "/+wamCFNiq9qKEilEBDTttP7Rd/GAS51lsfCrsISbg5td/w25" +
            "+wulDfuMbjjlW9Afh0p7Jscmbo1skqIOIUPYfVQEL687B0EmJufMlljfu52b2efVAyWZF9QBG1vx" +
            "/AJz1EVyfskMaYVqPiTesZAgMBAAECgYEAtVnkk0bjoArOTg" +
            "/KquLWQRlJDFrPKP3CP25wHsU4749t6kJuU5FSH1Ao81d0Dn9m5neGQCOOdRFi23cV9gdFKYMhwPE6" +
            "+nTAloxI3vb8K9NNMe0zcFksva9c9bUaMGH2p40szMoOpO6TrSHO9Hx4GJ6UfsUUqkFFlN76XprwE" +
            "+ECQQD9rXwfbr9GKh9QMNvnwo9xxyVl4kI88iq0X6G4qVXo1Tv6/DBDJNkX1mbXKFYL5NOW1waZzR+Z" +
            "/XcKWAmUT8J9AkEA8i0WT" +
            "/ieNsF3IuFvrIYG4WUadbUqObcYP4Y7Vt836zggRbu0qvYiqAv92Leruaq3ZN1khxp6gZKl" +
            "/OJHXc5xzQJACqr1AU1i9cxnrLOhS8m+xoYdaH9vUajNavBqmJ1mY3g0IYXhcbFm/72gbYPgundQ" +
            "/pLkUCt0HMGv89tn67i+8QJBALV6UgkVnsIbkkKCOyRGv2syT3S7kOv1J" +
            "+eamGcOGSJcSdrXwZiHoArcCZrYcIhOxOWB/m47ymfE1Dw" +
            "/+QjzxlUCQCmnGFUO9zN862mKYjEkjDN65n1IUB9Fmc1msHkIZAQaQknmxmCIOHC75u4W0PGRyVzq8KkxpNBq62ICl7xmsPM=";

    private Config() {
    }

    public static synchronized Config getInstance() {
        if (mInstance == null) {
            mInstance = new Config();
        }
        return mInstance;
    }
    
    public String getDeviceId() {
    	if(TextUtils.isEmpty(DEVICE_ID) == true) {
    		DEVICE_ID = Utils.getInstance().getPhoneDeviceId();
    	}
    	return DEVICE_ID;
    }

    public String getDeviceName(){
        BluetoothAdapter mBluetoothAdapter= BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter==null){
            return "";
        }else {
            return mBluetoothAdapter.getName();
        }
    }

    public String getKey() {
        return key;
    }
}
