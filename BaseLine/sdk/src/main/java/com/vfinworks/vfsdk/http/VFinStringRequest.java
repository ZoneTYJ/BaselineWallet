package com.vfinworks.vfsdk.http;

import android.content.Context;

import com.vfinworks.vfsdk.SDKManager;
import com.vfinworks.vfsdk.common.L;
import com.vfinworks.vfsdk.context.BaseContext;
import com.vfinworks.vfsdk.http.volley.AuthFailureError;
import com.vfinworks.vfsdk.http.volley.DefaultRetryPolicy;
import com.vfinworks.vfsdk.http.volley.NetworkError;
import com.vfinworks.vfsdk.http.volley.NoConnectionError;
import com.vfinworks.vfsdk.http.volley.Request;
import com.vfinworks.vfsdk.http.volley.Response;
import com.vfinworks.vfsdk.http.volley.ServerError;
import com.vfinworks.vfsdk.http.volley.TimeoutError;
import com.vfinworks.vfsdk.http.volley.VolleyError;
import com.vfinworks.vfsdk.http.volley.toolbox.StringRequest;
import com.vfinworks.vfsdk.model.VFSDKResultModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class VFinStringRequest {
	public static final  int MAX_WAITMS=5000;
	private Context mContext;
	//用于cancel请求的时候用的标识
	private Object requestTag = null;

	public Object getRequestTag() {
		return requestTag;
	}

	public void setRequestTag(Object requestTag) {
		this.requestTag = requestTag;
	}

	public VFinStringRequest(Context context) {
		mContext = context;
	}

	public void excuteHttp(String uri,RequestParams reqParam,VFinResponseHandler responseHandler) {
		excuteHttp(Request.Method.POST,uri,reqParam,responseHandler);
	}

	public void excuteHttp(int method,String uri,final RequestParams reqParam,final VFinResponseHandler responseHandler) {
		if(method == Request.Method.POST) {
			httpPost(uri,reqParam,responseHandler);
		}else if(method == Request.Method.GET) {
			httpGet(uri,reqParam,responseHandler);
		}
	}

	private void httpPost(final String uri, final RequestParams reqParam, final VFinResponseHandler responseHandler) {
		final String url = uri;
		StringRequest sr = new StringRequest(Request.Method.POST,uri, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				L.d("HTTP-URI", url+"---service="+reqParam.data.get("service"));
				L.d("req", reqParam.getParams().toString());
				L.d("response", response);
				try {
					JSONObject jsonObj = new JSONObject(response);
					String strResult = jsonObj.getString("is_success");
					if("T".equalsIgnoreCase(strResult) == false && "true".equalsIgnoreCase(strResult) == false) {
						String strErrCode="";
						if(!jsonObj.isNull("error_code")){
							strErrCode=jsonObj.getString("error_code");
						}
						String strErrMessage = "";
						if(!jsonObj.isNull("error_message"))
							strErrMessage =  jsonObj.getString("error_message");

						if(strErrCode.equals("ILLEGAL_TOKEN")){
							if(SDKManager.getInstance().getCallbackHandler() != null){
								BaseContext baseContext = new BaseContext();
								VFSDKResultModel vfsdkResultModel = new VFSDKResultModel();
								vfsdkResultModel.setMessage("token已过期");
								vfsdkResultModel.setResultCode("-1");
								baseContext.sendMessage(vfsdkResultModel);
							}
							responseHandler.httpResponseError(strErrCode, strErrMessage);
//							RequestParams requestParams1 = new RequestParams();
//							requestParams1.putData("service", "login_gesture");
//							requestParams1.putData("refresh_token", HttpUtils.refreshToken);
//							httpPost(HttpRequsetUri.getInstance().getMemberDoUri(), requestParams1, new VFinResponseHandler() {
//								@Override
//								public void onSuccess(Object responseBean, String responseString) {
//									try {
//										JSONObject jsonObject2 = new JSONObject(responseString);
//										String token = jsonObject2.getString("token");
//										String refresh_token = jsonObject2.getString("refresh_token");
//										saveToken(token,refresh_token);
//										reqParam.putData("token",token);
//										httpPost(url,reqParam,responseHandler);
//									} catch (JSONException e) {
//										e.printStackTrace();
//									}
//								}
//
//								@Override
//								public void onError(String statusCode, String errorMsg) {
//									if("ILLEGAL_REFRESH_TOKEN".equals(statusCode)){
//										Class loginClazz = null;
//										try {
//											loginClazz = Class.forName(Config.LOGIN_CLASS);
//										} catch (ClassNotFoundException e) {
//											e.printStackTrace();
//										}
//										Intent intent = new Intent(mContext, loginClazz);
//										intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
//										mContext.startActivity(intent);
//										Toast.makeText(mContext, "refresh_token已过期,请重新登录", Toast.LENGTH_SHORT).show();
//									}else {
//										Toast.makeText(mContext, errorMsg, Toast.LENGTH_SHORT).show();
//									}
//								}
//							});
						}else{
							responseHandler.httpResponseError(strErrCode, strErrMessage);
						}
						return;
					}
				} catch (JSONException e) {
					e.printStackTrace();
					responseHandler.httpResponseError("JSON_ERROR", "json 解析异常");
					return;
				}
				responseHandler.httpResponseSuccess(response);
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				String statusCode = "SYSTEM_ERROR";
				String errMsg = "服务器内部错误";
				if(error instanceof TimeoutError) {
					statusCode = "TIMEOUT_ERROR";
					errMsg = "请求超时";
				}else if(error instanceof NoConnectionError){
					statusCode = "NO_CONNECTION";
					errMsg = "网络异常";
				}else if(error instanceof NetworkError){
					statusCode = "NETWORK_ERROR";
					errMsg = "网络异常";
				}else if(error instanceof ServerError){
					statusCode = "SERVER_ERROR";
					errMsg = "服务器错误";
				}else if(error instanceof AuthFailureError){
					statusCode = "AUTO_FAILURE_ERROR";
					errMsg = "认证失败";
				}

				responseHandler.httpResponseError(statusCode, errMsg);
			}
		}){
			//POST 参数
			@Override
			protected Map<String,String> getParams(){
				Map<String,String> params = new HashMap<String, String>();
				if(reqParam != null)
					params = reqParam.getParams();
				//params.put("service",reqParam.getParams());
				return params;
			}

			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				Map<String,String> params = new HashMap<String, String>();
				//params.put("Content-Type","application/x-www-form-urlencoded");
				return params;
			}
		};
		sr.setTag(requestTag);
		sr.setRetryPolicy(
				new DefaultRetryPolicy(
						MAX_WAITMS,//默认超时时间，应设置一个稍微大点儿的，例如本处的500000
						1,//默认最大尝试次数
						DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
				)
		);

		VolleyManager.getInstance(mContext).addToRequestQueue(sr);
	}

	private void saveToken(String token,String refresh_token) {
		HttpUtils.getInstance(mContext).saveToken(token,refresh_token);
		SDKManager.token = token;
	}

	private void httpGet(String uri,final RequestParams reqParam,final VFinResponseHandler responseHandler) {
		//get 参数拼接


		StringRequest sr = new StringRequest(uri, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				responseHandler.httpResponseSuccess(response);
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				String statusCode = "SYSTEM_ERROR";
				String errMsg = "服务器内部错误";
				if(error instanceof TimeoutError) {
					statusCode = "TIMEOUT_ERROR";
					errMsg = "请求超时";
				}else if(error instanceof NoConnectionError){
					statusCode = "NO_CONNECTION";
					errMsg = "网络未连接";
				}else if(error instanceof NetworkError){
					statusCode = "NETWORK_ERROR";
					errMsg = "网络异常";
				}else if(error instanceof ServerError){
					statusCode = "SERVER_ERROR";
					errMsg = "服务器错误";
				}else if(error instanceof AuthFailureError){
					statusCode = "AUTO_FAILURE_ERROR";
					errMsg = "认证失败";
				}

				responseHandler.httpResponseError(statusCode, errMsg);
			}
		}){
			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				Map<String,String> params = new HashMap<String, String>();
				//params.put("Content-Type","application/x-www-form-urlencoded");
				return params;
			}
		};
		sr.setTag(requestTag);
		VolleyManager.getInstance(mContext).addToRequestQueue(sr);
	}
}
