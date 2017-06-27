package com.vfinworks.vfsdk.common;

import java.lang.reflect.Field;


public class HttpRequsetUri {
	private static HttpRequsetUri mInstance;
	
    private HttpRequsetUri() {

    }

    public static synchronized HttpRequsetUri getInstance() {
        if (mInstance == null) {
            mInstance = new HttpRequsetUri();
        }
        return mInstance;
    }
    
    //配置http server 地址
    private String ENV = "TEST_SERVER_LOC";
//    private String TEST_SERVER_LOC = "http://10.65.215.36:9519/appserver"; // gzjt
    private static String TEST_SERVER_LOC = "http://base.vfinance.cn/appserver";
//    private static String TEST_SERVER_LOC = "http://func115.vfinance.cn/appserver"; //jinxiang
//    private static String TEST_SERVER_LOC = "http://t.vfinance.cn/appserver";
    //开发本机
//    private static String TEST_SERVER_LOC = "http://10.5.20.4:8080/appserver-web-core";// tyj
//    private static String TEST_SERVER_LOC = "http://10.5.20.14:8080/appserver-web-core";

    private String DEV_SERVER_LOC = "http://tpay.dingpay.cn/mwallet";
    private String PRO_SERVER_LOC = "http://tpay.dingpay.cn/mwallet";

    public String getHttpServer() {
    	String strAddress = TEST_SERVER_LOC;
    	try {
			Field field = this.getClass().getDeclaredField(ENV);
			field.setAccessible(true);
			strAddress = (String) field.get(this);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	return strAddress;
    }
    
    //member do
    private String MemberDoUri = getHttpServer() + "/gateway/member.do";

    public String getMemberDoUri() {
        return MemberDoUri;
    }
    //acquirer do
    private String AcquirerDoUri = getHttpServer() + "/gateway/acquirer.do";

    public String getAcquirerDoUri() {
        return AcquirerDoUri;
    }

    public void updateDoUri(String url){
        TEST_SERVER_LOC = url;
        MemberDoUri = TEST_SERVER_LOC + "/gateway/member.do";
        AcquirerDoUri = TEST_SERVER_LOC + "/gateway/acquirer.do";
    }
}
