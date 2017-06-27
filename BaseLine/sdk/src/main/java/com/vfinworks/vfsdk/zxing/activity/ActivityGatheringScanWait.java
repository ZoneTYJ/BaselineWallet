package com.vfinworks.vfsdk.zxing.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.vfinworks.vfsdk.R;
import com.vfinworks.vfsdk.SDKManager;
import com.vfinworks.vfsdk.activity.BaseActivity;
import com.vfinworks.vfsdk.common.HttpRequsetUri;
import com.vfinworks.vfsdk.common.VFShowDialog;
import com.vfinworks.vfsdk.model.BillModel;
import com.vfinworks.vfsdk.enumtype.PayStateEnum;
import com.vfinworks.vfsdk.enumtype.VFCallBackEnum;
import com.vfinworks.vfsdk.http.HttpUtils;
import com.vfinworks.vfsdk.http.RequestParams;
import com.vfinworks.vfsdk.http.VFinResponseHandler;
import com.vfinworks.vfsdk.model.VFSDKResultModel;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by tangyijian  等待付款方确认
 */
public class ActivityGatheringScanWait extends BaseActivity {
    private TextView tvAmount;
    private TextView tvTarget;

    private Handler mScheduleHandler;
    private Runnable mScheduleRunnable;
    private int SCHEDULE_TIME = 2 * 1000;
    private String mToken;
    private String mOut_trade_no;
    private String mDeviceId;
    private Activity mContext;
    private TextView tvWait;

    private boolean mPayFlag=false;
    private int callBackGatheringId=7;
    private String mNickname;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_gathering_scan_wait, FLAG_TITLE_LINEARLAYOUT);
        super.onCreate(savedInstanceState);
        this.getTitlebarView().setTitle("等待确认");
        this.getTitlebarView().initLeft(new OnClickListener() {
            @Override
            public void onClick(View v) {
                backOnClick();
            }
        });

        Bundle bundle = this.getIntent().getExtras();
        String result = bundle.getString("result");
        try {
            JSONObject object = new JSONObject(result);
            mToken = object.getString("token");
            mDeviceId = object.getString("deviceId");
            mOut_trade_no = object.getString("out_trade_no");
            mNickname = object.getString("nickname");
        } catch (JSONException e) {
            e.printStackTrace();
            this.showShortToast("订单号为有误！");
            return;
        }

        mScheduleHandler = new Handler();
        mScheduleRunnable = new Runnable() {
            @Override
            public void run() {
                queryScanResult();
            }
        };
        //第一次显示内容快些
        //mScheduleHandler.postDelayed(mScheduleRunnable, SCHEDULE_TIME);
        mScheduleHandler.post(mScheduleRunnable);
    }

    @Override
    public void initWidget() {
        tvAmount = (TextView) this.findViewById(R.id.tv_amount);
        tvTarget = (TextView) this.findViewById(R.id.tv_target);
        tvWait = (TextView) this.findViewById(R.id.tv_wait);
    }

    private void queryScanResult() {
        this.showProgress();
        RequestParams requestParams = new RequestParams();
        requestParams.putData("service", "query_trade");
        requestParams.putData("token", SDKManager.token);
        requestParams.putData("trade_type", "INSTANT");
        requestParams.putData("outer_trade_no", mOut_trade_no);
        requestParams.putData("device_id", mDeviceId);
        HttpUtils.getInstance(mContext).excuteHttpRequest(HttpRequsetUri
                .getInstance().getAcquirerDoUri(), requestParams, new VFinResponseHandler<BillModel>
                (BillModel.class) {
            @Override
            public void onSuccess(BillModel responseBean, String responseString) {
                hideProgress();
                checkTrade(responseBean.getTrade_info());
                mScheduleHandler.postDelayed(mScheduleRunnable, SCHEDULE_TIME);
            }

            @Override
            public void onError(String statusCode, String errorMsg) {
                hideProgress();
                showShortToast(errorMsg);
                mScheduleHandler.postDelayed(mScheduleRunnable, SCHEDULE_TIME);
            }
        }, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mScheduleHandler.removeCallbacks(mScheduleRunnable);
    }

    private void checkTrade(BillModel.TradeInfoListBean tradeInfoListBean) {
        mScheduleHandler.removeCallbacks(mScheduleRunnable);
        String trade_status = tradeInfoListBean.getTrade_status();
        //待支付 继续查询
        if (PayStateEnum.WAIT_PAY.toString().equals(trade_status)) {
            mPayFlag=false;
            tvTarget.setText("收款对象：" + mNickname);
            tvAmount.setText("收款金额:" + tradeInfoListBean.getAmount() + "元");
            tvWait.setText("处理中");
            mScheduleHandler.postDelayed(mScheduleRunnable, SCHEDULE_TIME);
        } else if (PayStateEnum.TRADE_FINISHED.toString().equalsIgnoreCase(trade_status)) {
            mPayFlag = true;
            mScheduleHandler.removeCallbacks(mScheduleRunnable);
            //收款成功
            tvWait.setText("收款成功");
            VFSDKResultModel resultModel = new VFSDKResultModel();
            resultModel.setResultCode(VFCallBackEnum.OK.getCode());
            resultModel.setMessage("收款成功！");
            if (SDKManager.getInstance().getCallbackHandler() != null) {
                sendMessage(resultModel,callBackGatheringId);
            }
            finishAll();
        } else {
            mPayFlag=true;
            tvWait.setText(trade_status);
            mScheduleHandler.removeCallbacks(mScheduleRunnable);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            backOnClick();
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 返回
     */
    public void backOnClick() {
        if(!mPayFlag) {
            final VFShowDialog showDialog = new VFShowDialog(this);
            showDialog.setContent("正在等待对方付款，您确定退出吗？");
            showDialog.setOKText("确定");
            showDialog.setOnCompleteListener(new VFShowDialog.OnCompleteListener() {

                @Override
                public void onComplete() {
                    showDialog.dismiss();
                    VFSDKResultModel resultModel = new VFSDKResultModel();
                    resultModel.setResultCode(VFCallBackEnum.OK.getCode());
                    resultModel.setMessage("收款结束！");
                    if (SDKManager.getInstance().getCallbackHandler() != null) {
                        sendMessage(resultModel, callBackGatheringId);
                    }
                    finishAll();
                }

                @Override
                public void onCancel() {
                    showDialog.dismiss();
                }
            });
            showDialog.show();
        }
    }

    public void sendMessage(VFSDKResultModel resultModel,int callBackMessageId) {
        if(SDKManager.getInstance().getCallbackHandler() != null) {
            Message msg = SDKManager.getInstance().getCallbackHandler().obtainMessage();
            msg.what = callBackMessageId;
            msg.obj = resultModel;
            SDKManager.getInstance().getCallbackHandler().sendMessage(msg);
        }
    }

}
