package com.vfinworks.vfsdk.http;

import android.content.Context;

import com.vfinworks.vfsdk.http.volley.Request;
import com.vfinworks.vfsdk.http.volley.RequestQueue;
import com.vfinworks.vfsdk.http.volley.toolbox.HurlStack;
import com.vfinworks.vfsdk.http.volley.toolbox.Volley;

public class VolleyManager {
	private static VolleyManager mInstance;
	private RequestQueue mRequestQueue;
	private Context mContext;
	
    private VolleyManager(Context context) {
    	mContext = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized VolleyManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new VolleyManager(context.getApplicationContext());
        }
        return mInstance;
    }
    
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            HurlStack work = new HurlStack(null,SSLUtils.getSSLSocketFactory());
            // getApplicationContext()是关键, 它会避免
            // Activity或者BroadcastReceiver带来的缺点.
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext(),work);
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}
