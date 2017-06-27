package com.vfinworks.vfsdk.activity.PeopleInfo;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.vfinworks.vfsdk.R;
import com.vfinworks.vfsdk.SDKManager;
import com.vfinworks.vfsdk.activity.BaseActivity;
import com.vfinworks.vfsdk.common.HttpRequsetUri;
import com.vfinworks.vfsdk.http.HttpUtils;
import com.vfinworks.vfsdk.http.RequestParams;
import com.vfinworks.vfsdk.http.VFinResponseHandler;

/**
 * Created by xiaoshengke on 2016/10/8.
 */
public class AdviseActivity extends BaseActivity {
    private EditText et_advise;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advise,FLAG_TITLE_LINEARLAYOUT);
        getTitlebarView().setTitle("反馈");
        getTitlebarView().initRight("提交", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkInput()){
                    saveData();
                }
            }
        });

        et_advise = (EditText) findViewById(R.id.et_advise);

        et_advise.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                L.e("before",start+"=="+after+"=="+count);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                L.e(start+"=="+before+"=="+count);
                if(start+count == 300){
                    showShortToast("反馈内容最多允许输入300字符");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private boolean checkInput() {
        if(TextUtils.isEmpty(et_advise.getText().toString().trim())){
            showShortToast("反馈内容不能为空");
            return false;
        }
        return true;
    }

    private void saveData() {
        showProgress();
        RequestParams reqParam = new RequestParams();
        reqParam.putData("service", "member_feedback");
        reqParam.putData("content", et_advise.getText().toString().trim());
        reqParam.putData("token", SDKManager.token);
        HttpUtils.getInstance(this).excuteHttpRequest(HttpRequsetUri.getInstance().getMemberDoUri(), reqParam, new VFinResponseHandler() {

            @Override
            public void onSuccess(Object responseBean, String responseString) {
                hideProgress();
                showShortToast("提交成功");
                finishActivity();
            }

            @Override
            public void onError(String statusCode, String errorMsg) {
                hideProgress();
                showShortToast(errorMsg);
            }
        }, this);
    }
}
