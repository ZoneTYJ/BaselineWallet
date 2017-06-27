package com.vfinworks.vfsdk.activity.PeopleInfo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.vfinworks.vfsdk.R;
import com.vfinworks.vfsdk.SDKManager;
import com.vfinworks.vfsdk.activity.BaseActivity;
import com.vfinworks.vfsdk.common.HttpRequsetUri;
import com.vfinworks.vfsdk.common.StringReplaceUtil;
import com.vfinworks.vfsdk.http.HttpUtils;
import com.vfinworks.vfsdk.http.RequestParams;
import com.vfinworks.vfsdk.http.VFinResponseHandler;
import com.vfinworks.vfsdk.model.CertificationModel;

/**
 * 实名信息
 * Created by xiaoshengke on 2016/8/18.
 */
public class AutonymActivity extends BaseActivity {
    private TextView tv_autonym_name;
    private TextView tv_autonym_number;
    private TextView tv_autonym_desc;
    private ImageView iv_label;
    private Button tv_authenticate;
    private String state;
    private CertificationModel certificationModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autonym,FLAG_TITLE_LINEARLAYOUT);
        getTitlebarView().setTitle("身份信息");
        state = getIntent().getStringExtra("real_state");
        bindViews();
        getData();
    }

    private void bindViews() {
        tv_autonym_name = (TextView) findViewById(R.id.tv_autonym_name);
        tv_autonym_number = (TextView) findViewById(R.id.tv_autonym_number);
        tv_autonym_desc = (TextView) findViewById(R.id.tv_autonym_desc);
        iv_label = (ImageView) findViewById(R.id.iv_label);
        tv_authenticate = (Button) findViewById(R.id.tv_authenticate);

        if("notFound".equals(state) || "checkPass".equals(state)){
            tv_autonym_desc.setText("您的认证信息不完整，请上传身份证照片");
            tv_authenticate.setVisibility(View.VISIBLE);
        }else if("auditPass".equals(state) || "init".equals(state)){
            tv_autonym_desc.setText("身份信息已提交，预计1-3个工作日完成审核");
            tv_authenticate.setVisibility(View.GONE);
        }else if("auditReject".equals(state)){
            tv_autonym_desc.setText("初审拒绝");
            tv_authenticate.setVisibility(View.VISIBLE);
        }else if("dubboCheckPass".equals(state)){
            tv_authenticate.setVisibility(View.GONE);
            iv_label.setImageResource(R.drawable.vf_ture);
            tv_autonym_desc.setText("身份信息已完善");
            tv_autonym_desc.setTextColor(0xff01ba9b);
        }else if("checkReject".equals(state)){
            tv_autonym_desc.setText("复审实名认证被拒绝");
            tv_authenticate.setVisibility(View.VISIBLE);
        }

        tv_authenticate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AutonymActivity.this,CertificationActivity.class);
                intent.putExtra("data",certificationModel);
                startActivity(intent);
            }
        });
    }

    private void getData() {
        showProgress();
        RequestParams reqParam = new RequestParams();
        reqParam.putData("service", "query_certification");
        reqParam.putData("token", SDKManager.token);
        HttpUtils.getInstance(this).excuteHttpRequest(HttpRequsetUri.getInstance().getMemberDoUri(), reqParam, new VFinResponseHandler<CertificationModel>(CertificationModel.class){

            @Override
            public void onSuccess(CertificationModel responseBean, String responseString) {
                hideProgress();
                certificationModel = responseBean;
                tv_autonym_name.setText(responseBean.getReal_name());
                tv_autonym_number.setText(StringReplaceUtil.getStarString(certificationModel.getCert_no(),4,certificationModel.getCert_no().length()-4));
            }

            @Override
            public void onError(String statusCode, String errorMsg) {
                hideProgress();
                showShortToast(errorMsg);
            }

        }, this);
    }

}
