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
import com.vfinworks.vfsdk.context.BaseContext;
import com.vfinworks.vfsdk.http.HttpUtils;
import com.vfinworks.vfsdk.http.RequestParams;
import com.vfinworks.vfsdk.http.VFinResponseHandler;

/**
 * 修改昵称
 * Created by xiaoshengke on 2016/8/18.
 */
public class NicknameActivity extends BaseActivity {
    private EditText et_nick_name;
    private String nickname;
    private BaseContext baseContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nickname,FLAG_TITLE_LINEARLAYOUT);
        getTitlebarView().setTitle("昵称");
        getTitlebarView().initRight("保存", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(et_nick_name.getText().toString())){
                    showShortToast("请输入1-20个字的昵称");
                }else
                    saveData();
            }
        });
        initView();
    }

    private void initView() {
        nickname = getIntent().getStringExtra("name");
        baseContext = (BaseContext) getIntent().getSerializableExtra("context");

        et_nick_name = (EditText) findViewById(R.id.et_nick_name);
        et_nick_name.setText(nickname);
        et_nick_name.setSelection(nickname.length());

        et_nick_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void saveData() {
        RequestParams reqParam = new RequestParams();
        reqParam.putData("service", "update_memberName");
        reqParam.putData("token", SDKManager.token);
        reqParam.putData("member_name", et_nick_name.getText().toString().trim());
        HttpUtils.getInstance(this).excuteHttpRequest(HttpRequsetUri.getInstance().getMemberDoUri(), reqParam, new VFinResponseHandler(){

            @Override
            public void onSuccess(Object responseBean, String responseString) {
                showShortToast("修改成功");
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
