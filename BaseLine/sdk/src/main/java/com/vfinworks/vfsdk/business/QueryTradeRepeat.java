package com.vfinworks.vfsdk.business;

import android.os.Handler;

import com.vfinworks.vfsdk.activity.BaseActivity;
import com.vfinworks.vfsdk.enumtype.PayStateEnum;
import com.vfinworks.vfsdk.http.RequestParams;

/**
 * Created by tangyijian on 2017/1/4.
 */
public class QueryTradeRepeat {
    public static final int MAX_WAIT=2000;
    private int maxCount;
    private BaseActivity mContext;
    private QueryTrade mQueryTrade;

    private QueryTradeHandler mQueryTradeHandler;
    private Handler mHandler;
    private Runnable queryRunnable = new Runnable() {
        @Override
        public void run() {
            startQueryTrade();
        }
    };

    public QueryTradeRepeat(BaseActivity context, int repeatTimes, String outNo, String token){
        mContext=context;
        maxCount=repeatTimes;
        mHandler= new Handler();
        mQueryTrade = new QueryTrade(mContext,outNo,token);
    }

    public void setHandler(QueryTradeHandler queryTradeHandler){
        mQueryTradeHandler = queryTradeHandler;
    }

    private void startQueryTradeRunnable(){
        mHandler.postDelayed(queryRunnable, MAX_WAIT);
    }

    public void startQueryTrade(){
        mQueryTrade.doQueryTrade(new QueryTrade.QueryTradeResponseHandler() {
            @Override
            public void onSuccess(int count, String result) {
                if(PayStateEnum.isWait(result)){
                    if (count < maxCount) {
                        startQueryTradeRunnable();
                    } else {
                        if(mQueryTradeHandler!=null){
                            mQueryTradeHandler.callBackRequery();
                        }
                    }
                }else {
                    if(PayStateEnum.isFinish(result)){
                        if(mQueryTradeHandler!=null) {
                            mQueryTradeHandler.callBackSuccess();
                        }
                    }else {
                        if(mQueryTradeHandler!=null) {
                            mQueryTradeHandler.callBackFault();
                        }
                    }
                }
            }

            @Override
            public void onError(int count, String result) {
                mContext.hideProgress();
                mContext.showShortToast(result);
            }

            @Override
            public void putData(RequestParams reqParam) {
                if(mQueryTradeHandler!=null) {
                    mQueryTradeHandler.putData(reqParam);
                }
            }
        });
    }

    public void handlerDestory(){
        mHandler.removeCallbacks(queryRunnable);
    }

    public interface QueryTradeHandler{
        void callBackSuccess();
        void callBackFault();
        void callBackRequery();
        void putData(RequestParams reqParam);
    }
}
