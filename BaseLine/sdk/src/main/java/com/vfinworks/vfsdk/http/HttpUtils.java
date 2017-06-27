package com.vfinworks.vfsdk.http;

import android.content.Context;
import android.text.TextUtils;

import com.vfinworks.vfsdk.Utils.RSA;
import com.vfinworks.vfsdk.common.SharedPreferenceUtil;
import com.vfinworks.vfsdk.db.KeyDatabaseHelper;
import com.vfinworks.vfsdk.model.KeyModel;

import net.sqlcipher.database.SQLiteDatabase;

import org.apaches.commons.codec.binary.Base64;

public class HttpUtils {
	private static HttpUtils mInstance;
	private Context mContext;
    public static String refreshToken;

    private HttpUtils(Context context) {
    	mContext = context.getApplicationContext();
    }

    public static synchronized HttpUtils getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new HttpUtils(context);
        }
        if(refreshToken == null){
            SQLiteDatabase.loadLibs(context);
            String temp = SharedPreferenceUtil.getInstance().getStringValueFromSP("refresh_token");
            if(!TextUtils.isEmpty(temp)) {
                try {
                    KeyModel keyModel = KeyDatabaseHelper.getInstance(context).queryKey();
                    refreshToken = new String(RSA.decryptByPrivateKey(Base64.decodeBase64(temp
                            .getBytes()), keyModel.privateKey));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else{
                refreshToken = "temp";
            }
        }
        return mInstance;
    }

    public void saveToken(String token,String refresh_token){
        KeyModel keyModel = KeyDatabaseHelper.getInstance(mContext).queryKey();
        try {
            SharedPreferenceUtil.getInstance().setStringDataIntoSP("token", new String(Base64
                    .encodeBase64(RSA.encryptByPublicKey(token.getBytes(), keyModel.publicKey)
                    )));
            SharedPreferenceUtil.getInstance().setStringDataIntoSP("refresh_token", new String(Base64
                    .encodeBase64(RSA.encryptByPublicKey(refresh_token.getBytes(), keyModel.publicKey)
                    )));
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.refreshToken=refresh_token;
    }
    
    /*
     * method:POST or GET
     * reqParam:请求参数
     * responseHandler：请求结果回调
     * requestTag：request标识，用于cancel用
     */
    public void excuteHttpRequest(int method,String uri,final RequestParams reqParam,final VFinResponseHandler responseHandler,Object requestTag) {
    	VFinStringRequest request = new VFinStringRequest(mContext);
    	request.setRequestTag(requestTag);
    	request.excuteHttp(method, uri, reqParam, responseHandler);
    }
    /*
     * reqParam:请求参数,请求为Post方式
     * responseHandler：请求结果回调
     * requestTag：request标识，用于cancel用
     */
    public void excuteHttpRequest(String uri,final RequestParams reqParam,final VFinResponseHandler responseHandler,Object requestTag) {
    	VFinStringRequest request = new VFinStringRequest(mContext);
    	request.setRequestTag(requestTag);
    	request.excuteHttp(uri, reqParam, responseHandler);
    }
    /*
     * 取消请求
     * requestTag:请求标识
     */
    public void cancelHttp(Object requestTag) {
    	VolleyManager.getInstance(mContext).getRequestQueue().cancelAll(requestTag);
    }
}
