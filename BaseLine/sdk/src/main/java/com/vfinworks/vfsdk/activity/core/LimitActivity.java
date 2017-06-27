package com.vfinworks.vfsdk.activity.core;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vfinworks.vfsdk.R;
import com.vfinworks.vfsdk.SDKManager;
import com.vfinworks.vfsdk.activity.BaseActivity;
import com.vfinworks.vfsdk.common.HttpRequsetUri;
import com.vfinworks.vfsdk.context.LimitContext;
import com.vfinworks.vfsdk.http.HttpUtils;
import com.vfinworks.vfsdk.http.RequestParams;
import com.vfinworks.vfsdk.http.VFinResponseHandler;
import com.vfinworks.vfsdk.model.LimitModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by tangyijian on 2016/9/28.
 */
public class LimitActivity extends BaseActivity {
    LimitContext limitContext;
    private TextView tv_limit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_limit, FLAG_TITLE_LINEARLAYOUT);
        limitContext = (LimitContext) this.getIntent().getSerializableExtra("context");
        super.onCreate(savedInstanceState);
        this.getTitlebarView().setTitle("限额说明");
        this.getTitlebarView().initLeft(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backOnClick();
            }
        });
        initView();
        networkQueryLflt();
    }

    private void initView() {
        tv_limit=(TextView)findViewById(R.id.tv_limit);
    }

    private void networkQueryLflt() {
        RequestParams reqParam = new RequestParams();
        reqParam.putData("service", "query_lflt");
        reqParam.putData("token", SDKManager.token);
        reqParam.putData("transfer_Type", limitContext.getTransfer_Type());
        reqParam.putData("payChannel", limitContext.getPayChannel());
        HttpUtils.getInstance(this).excuteHttpRequest(HttpRequsetUri.getInstance().getMemberDoUri(),
                reqParam, new VFinResponseHandler() {

                    @Override
                    public void onSuccess(Object responseBean, String responseString) {
                        hideProgress();
                        JSONObject json;
                        try {
                            json = new JSONObject(responseString);
                            if (!json.isNull("result")) {
                                String result = json.getString("result");
                                List<LimitModel> results = new Gson().fromJson(result, new
                                        TypeToken<List<LimitModel>>() {
                                        }.getType());
                                updateText(results);
                            } else {
                                updateText(null);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            showShortToast(e.getMessage());
                        }
                    }

                    @Override
                    public void onError(String statusCode, String errorMsg) {
                        hideProgress();
                        showShortToast(errorMsg);
                    }
                }, this);
    }

    private void updateText(List<LimitModel> results) {
        String text;
        if (results != null) {
            String a = "", b = "";
            for (LimitModel bean : results) {
                if(bean.getLimitedType().equals(LimitModel.QUOTA)){
                    if (bean.getRangType().equals(LimitModel.SINGLE)) {
                        a = "单笔限额" + bean.getTotalLimitedValue() + "元";
                    } else if ( bean.getTimeRangeType().equals(LimitModel.DAY)) {
                        b = "，本日限额" + bean.getTotalLimitedValue() + "元。";
                    }
                }
            }
            text = a + b;
        }else {
            text="没有限制";
        }
        tv_limit.setText(text);
    }

    private void backOnClick() {
        finishActivity();
    }
}
