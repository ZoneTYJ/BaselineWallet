package com.vfinworks.vfsdk.zxing.activity;

import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.transition.ChangeImageTransform;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.vfinworks.vfsdk.R;
import com.vfinworks.vfsdk.SDKManager;
import com.vfinworks.vfsdk.activity.BaseActivity;
import com.vfinworks.vfsdk.activity.core.TradeResultActivity;
import com.vfinworks.vfsdk.authenticator.AuthenMain;
import com.vfinworks.vfsdk.authenticator.Base32String;
import com.vfinworks.vfsdk.common.HttpRequsetUri;
import com.vfinworks.vfsdk.common.Utils;
import com.vfinworks.vfsdk.context.PaymentContext;
import com.vfinworks.vfsdk.entertransition.EnterTransitionActivity;
import com.vfinworks.vfsdk.entertransition.EnterTransitionActivityLollipop;
import com.vfinworks.vfsdk.entertransition.ImageBitmap;
import com.vfinworks.vfsdk.model.BillModel;
import com.vfinworks.vfsdk.http.HttpUtils;
import com.vfinworks.vfsdk.http.RequestParams;
import com.vfinworks.vfsdk.http.VFinResponseHandler;
import com.vfinworks.vfsdk.zxing.encoding.EncodingHandler;

import org.json.JSONObject;

/**
 * Created by tangyijian  付款码生成
 * 订单支付生成二维码 付款
 */
public class ActivityPaymentQRCodeGenerate extends BaseActivity implements OnClickListener{
    private ImageView imQrcode;
    private ImageView imCode39;

    private Handler mScheduleHandler;
    private Runnable mScheduleRunnable;
    private int SCHEDULE_TIME = 60 * 1000;

    //轮询查询扫描结果
    private Handler mQueryScheduleHandler;
    private Runnable mQueryScheduleRunnable;
    private int QUERY_SCAN_SCHEDULE_TIME = 20 * 1000;

    private BaseActivity mConext = this;
    private AuthenMain mAuthenMain;
    private String mPhone;
    private String mStringQRCode;
    private String mLastQRCode;
    private TextView tv_code39;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_payment_qrcode_generate, FLAG_TITLE_LINEARLAYOUT);
        super.onCreate(savedInstanceState);
        getTitlebarView().setTitle("付款");
        getTitlebarView().initLeft(new OnClickListener() {
            @Override
            public void onClick(View v) {
                backOnClick();
            }
        });

        mScheduleHandler = new Handler();
        mScheduleRunnable = new Runnable() {
            @Override
            public void run() {
                mLastQRCode=mStringQRCode;
                updateUI();
                mScheduleHandler.postDelayed(this, SCHEDULE_TIME);
            }
        };

        initAuthenicator();
        //轮询查询扫描
        mQueryScheduleHandler = new Handler();
        mQueryScheduleRunnable = new Runnable() {
            @Override
            public void run() {
                queryScanResult();
            }
        };
    }

    private void initAuthenicator() {
        mAuthenMain = new AuthenMain(mConext);
    }

    @Override
    public void initWidget() {
        mPhone = getIntent().getStringExtra("phone");
        imQrcode = (ImageView) this.findViewById(R.id.im_qrcode);
        imCode39 = (ImageView) this.findViewById(R.id.im_code39);
        tv_code39 = (TextView) this.findViewById(R.id.tv_code39);
        mLastQRCode="";
        imQrcode.setOnClickListener(this);
        imCode39.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
        mScheduleHandler.postDelayed(mScheduleRunnable, SCHEDULE_TIME);
        mQueryScheduleHandler.postDelayed(mQueryScheduleRunnable, QUERY_SCAN_SCHEDULE_TIME);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mScheduleHandler.removeCallbacks(mScheduleRunnable);
        mQueryScheduleHandler.removeCallbacks(mQueryScheduleRunnable);
    }

    //查询订单
    private void queryScanResult() {
        this.showProgress();
        RequestParams requestParams = new RequestParams();
        requestParams.putData("service", "query_trade");
        requestParams.putData("token", SDKManager.token);
        requestParams.putData("trade_type", "QRCODE");
        requestParams.putData("qrcode", mStringQRCode);
        requestParams.putData("qrcode2", mLastQRCode);
        HttpUtils.getInstance(ActivityPaymentQRCodeGenerate.this).excuteHttpRequest(HttpRequsetUri
                .getInstance().getAcquirerDoUri(), requestParams, new VFinResponseHandler<BillModel>
                (BillModel.class) {
            @Override
            public void onSuccess(BillModel responseBean, String responseString) {
                hideProgress();
                if(responseBean.getPay_status().equals("T")) {
                    startAct();
                }else {
                    mQueryScheduleHandler.postDelayed(mQueryScheduleRunnable, QUERY_SCAN_SCHEDULE_TIME);
                }
            }

            @Override
            public void onError(String statusCode, String errorMsg) {
                hideProgress();
                showShortToast(errorMsg);
                mQueryScheduleHandler.postDelayed(mQueryScheduleRunnable, QUERY_SCAN_SCHEDULE_TIME);
            }
        }, this);
    }
    private void startAct() {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("context", new PaymentContext());
        bundle.putSerializable("statue", TradeResultActivity.SUCCESS);
        intent.putExtras(bundle);
        intent.setClass(this, TradeResultActivity.class);
        startActivity(intent);
        finishAll();
    }

    private void updateUI() {
        Bitmap bitMap = getQRCode();
        if (bitMap != null) {
            imQrcode.setImageBitmap(bitMap);
            imQrcode.setTag(bitMap);
        }
        bitMap = getCode39();
        if (bitMap != null) {
            imCode39.setImageBitmap(bitMap);
            imCode39.setTag(bitMap);
            tv_code39.setText(mStringQRCode);
            tv_code39.setVisibility(View.VISIBLE);
        }else {
            tv_code39.setVisibility(View.GONE);
        }
    }

    private Bitmap getQRCode() {
        JSONObject object = new JSONObject();
        mStringQRCode = "";
        if(mAuthenMain!=null){
            String token= SDKManager.token;
            String code = mAuthenMain.getCurrentCode(Base32String.encode(token.getBytes()));

            mStringQRCode =mAuthenMain.getStringQRCode(code,mPhone);
        }
        try {
            return  EncodingHandler.createQRCode(mStringQRCode,800);
        } catch (Exception e) {
            e.printStackTrace();
            this.showShortToast("生成二维码失败，请重新生成！");
            return null;
        }
    }
    private Bitmap getCode39() {
        try {
            return  EncodingHandler.createCode39(mStringQRCode, (int) Utils.getInstance().dip2px(this,600),
                    (int) Utils.getInstance().dip2px(this,100));
        } catch (Exception e) {
            e.printStackTrace();
            this.showShortToast("生成一维码失败，请重新生成！");
            return null;
        }
    }

    private void backOnClick() {
        this.finishAll();
    }



    private void transitionOnAndroidIceCream(ImageView imageView,Bitmap bitmap) {
        Intent intent = new Intent(this, EnterTransitionActivity.class);
        Rect rect = new Rect();
        imageView.getGlobalVisibleRect(rect);
        intent.putExtra(EnterTransitionActivity.IMAGE_ORIGIN_RECT, rect);
        intent.putExtra(EnterTransitionActivity.IMAGE_SCALE_TYPE, imageView.getScaleType());
        ImageBitmap.getInstance().setBitmap(bitmap);
        // 打开第二个界面，要屏蔽Activity的默认转场效果
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void transitionOnAndroidLoL(ImageView imageView,Bitmap bitmap) {
        // 把需要共享的元素-ImageView，传给第二个界面
        Intent intent = new Intent(this, EnterTransitionActivityLollipop.class);
        String shareElementName = "sharedImageView";
        ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(this, imageView, shareElementName);
        getWindow().setSharedElementEnterTransition(new ChangeImageTransform(this, null));
        intent.putExtra(EnterTransitionActivityLollipop.SHARED_ELEMENT_KEY, shareElementName);
        ImageBitmap.getInstance().setBitmap(bitmap);
        // 打开它
        startActivity(intent, activityOptions.toBundle());
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            backOnClick();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        if(v==imCode39 || v==imQrcode){
            ImageView imageView= (ImageView) v;
//            transitionOnAndroidIceCream(imageView, (Bitmap) imageView.getTag());
            if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.LOLLIPOP) {
                transitionOnAndroidLoL(imageView, (Bitmap) imageView.getTag());
            }
        }
    }
}
