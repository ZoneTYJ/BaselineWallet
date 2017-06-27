package com.vfinworks.vfsdk.activity.core;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.vfinworks.vfsdk.R;
import com.vfinworks.vfsdk.activity.BaseActivity;

/**
 * Created by xiaoshengke on 2016/4/14.
 */
public class VFWebViewActivity extends BaseActivity {
    private WebView webView;
    private String htmlContent;
    private Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vfwebview, FLAG_TITLE_LINEARLAYOUT);
        this.getTitlebarView().setTitle("支付");
        getTitlebarView().initRight("关闭", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backOnClick();
            }
        });
        getTitlebarView().setLeftVisible(false);

        webView = (WebView) findViewById(R.id.wv_vfweb);

        htmlContent = getIntent().getStringExtra("content");
        initWebView();
        webView.loadData(htmlContent, "text/html", "UTF-8");
    }

    @Override
    public void onBackPressed() {
        backOnClick();
    }

    private void backOnClick() {
        finishAll();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        webView.postDelayed(new Runnable() {
            @Override
            public void run() {
                webView.loadUrl("javascript:postForm()");
            }
        },500);
    }

    private void initWebView() {
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
            }
        });

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if("http://func86fcw.vfinance.cn/page/".equals(url)){
//                    queryServiceResultForTen();
                    return true;
                }else
                    view.loadUrl(url);
                return super.shouldOverrideUrlLoading(view, url);
            }

        });

        webView.requestFocusFromTouch();//如果webView中需要用户手动输入用户名、密码或其他，则webview必须设置支持获取手势焦点。

        WebSettings webSettings = webView.getSettings();
        //打开页面时， 自适应屏幕
        webSettings.setUseWideViewPort(true);//设置此属性，可任意比例缩放
        webSettings.setLoadWithOverviewMode(true);

        //设置js可用
        webSettings.setJavaScriptEnabled(true);
        //页面支持缩放
        webSettings.setBuiltInZoomControls(true);
        webSettings.setSupportZoom(true);
    }

//    private void queryServiceResultForTen() {
//        showProgress();
//        handler.post(queryRunnable);
//    }

//    private Runnable queryRunnable = new Runnable() {
//        private int count;
//        @Override
//        public void run() {
//            if(count++ < 10) {
//                RequestParams requestParams = new RequestParams();
//                requestParams.putData("service", "query_trade");
//                requestParams.putData("token", rechargeContext.getToken());
//                requestParams.putData("trade_type", "DEPOSIT");
//                requestParams.putData("outer_trade_no", rechargeContext.getOutTradeNumber());
//                HttpUtils.getInstance(VFWebViewActivity.this).excuteHttpRequest(HttpRequsetUri.getInstance().getAcquirerDoUri(), requestParams, new VFinResponseHandler() {
//                    @Override
//                    public void onSuccess(Object responseBean, String responseString) {
//                        count = 11;
//                        hideProgress();
//                        Intent intent = new Intent(WalletActivity.ACTION_UPDATE);
//                        intent.putExtra(WalletActivity.MONEY_CHANGE, rechargeContext.getAmount());
//                        LocalBroadcastManager.getInstance(VFWebViewActivity.this).sendBroadcast(intent);
//                        showShortToast("充值成功");
//                        finishAll();
//                    }
//
//                    @Override
//                    public void onError(String statusCode, String errorMsg) {
//                        if(count == 10) {
//                            hideProgress();
//                            showShortToast(errorMsg);
//                        }
//                    }
//                }, this);
//                handler.postDelayed(this, 3000);
//            }
//        }
//    };


}
