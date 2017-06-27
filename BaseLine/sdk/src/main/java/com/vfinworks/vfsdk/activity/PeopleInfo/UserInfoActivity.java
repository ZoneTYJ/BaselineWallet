package com.vfinworks.vfsdk.activity.PeopleInfo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vfinworks.vfsdk.R;
import com.vfinworks.vfsdk.SDKManager;
import com.vfinworks.vfsdk.activity.BaseActivity;
import com.vfinworks.vfsdk.common.HttpRequsetUri;
import com.vfinworks.vfsdk.common.StringReplaceUtil;
import com.vfinworks.vfsdk.context.BaseContext;
import com.vfinworks.vfsdk.http.HttpUtils;
import com.vfinworks.vfsdk.http.RequestParams;
import com.vfinworks.vfsdk.http.VFinResponseHandler;
import com.vfinworks.vfsdk.model.MemberModel;
import com.vfinworks.vfsdk.view.activitytitle.TitleHelper;

/**
 * 我的信息
 * Created by xiaoshengke on 2016/8/18.
 */
public class UserInfoActivity extends BaseActivity implements View.OnClickListener {
    private RelativeLayout rl_name;
    private RelativeLayout rl_phone;
    private RelativeLayout rl_card;
    private RelativeLayout rl_code;
    private TextView tv_set;
    private TextView tv_about;
    private TextView tv_name;
    private TextView tv_phone;
    private TextView tv_card_info;
    private MemberModel memberModel;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info, TitleHelper.FLAG_TITLE_LINEARLAYOUT);
        getTitlebarView().setTitle("我的信息");
        token = getIntent().getStringExtra("token");
        bindViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    private void bindViews() {
        rl_name = (RelativeLayout) findViewById(R.id.rl_name);
        rl_phone = (RelativeLayout) findViewById(R.id.rl_phone);
        rl_card = (RelativeLayout) findViewById(R.id.rl_card);
        rl_code = (RelativeLayout) findViewById(R.id.rl_code);
        tv_set = (TextView) findViewById(R.id.tv_set);
        tv_about = (TextView) findViewById(R.id.tv_about);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_phone = (TextView) findViewById(R.id.tv_phone);
        tv_card_info = (TextView) findViewById(R.id.tv_card_info);

        rl_name.setOnClickListener(this);
        rl_phone.setOnClickListener(this);
        rl_card.setOnClickListener(this);
        rl_code.setOnClickListener(this);
        tv_set.setOnClickListener(this);
        tv_about.setOnClickListener(this);
    }

    private void updateUI(MemberModel responseBean) {
        memberModel = responseBean;
        tv_name.setText(responseBean.getNick_name());
        tv_phone.setText(StringReplaceUtil.getStarString(responseBean.getMobile_star(),3,7));
        if("notFound".equals(responseBean.getReal_name_status()) || "checkPass".equals(responseBean.getReal_name_status())){
            tv_card_info.setText("未实名认证");
        }else if("init".equals(responseBean.getReal_name_status())){
            tv_card_info.setText("实名认证中");
        }else if("auditPass".equals(responseBean.getReal_name_status())){
            tv_card_info.setText("初审通过");
        }else if("auditReject".equals(responseBean.getReal_name_status())){
            tv_card_info.setText("初审拒绝");
        }else if("dubboCheckPass".equals(responseBean.getReal_name_status())){
            tv_card_info.setText("已实名认证");
        }else if("checkReject".equals(responseBean.getReal_name_status())){
            tv_card_info.setText("复审实名认证被拒绝");
        }
    }

    private void getData() {
        RequestParams reqParam = new RequestParams();
        reqParam.putData("service", "query_member");
        reqParam.putData("token", SDKManager.token);
        HttpUtils.getInstance(this).excuteHttpRequest(HttpRequsetUri.getInstance().getMemberDoUri(), reqParam, new VFinResponseHandler<MemberModel>(MemberModel.class){

            @Override
            public void onSuccess(MemberModel responseBean, String responseString) {
                updateUI(responseBean);
            }

            @Override
            public void onError(String statusCode, String errorMsg) {
                hideProgress();
                showShortToast(errorMsg);
            }

        }, this);
    }

    @Override
    public void onClick(View v) {
        if(v == rl_code){
            Intent intent = new Intent(this,MyQrCodeActivity.class);
            intent.putExtra("name",memberModel.getNick_name());
            startActivity(intent);
        }else if(v == rl_name){
            BaseContext baseContext = new BaseContext();
            baseContext.setToken(token);
            SDKManager.getInstance().ModifyNickname(this,baseContext,memberModel.getNick_name(),null);
        }else if(v == rl_phone){
            Intent intent = new Intent(this,PhoneActivity.class);
            intent.putExtra("phone",memberModel.getMobile_star());
            startActivity(intent);
        }else if(v == rl_card){
            Intent intent = new Intent(this,AutonymActivity.class);
            intent.putExtra("real_state",memberModel.getReal_name_status());
            startActivity(intent);
        }else if(v == tv_set){
            startActivity(new Intent(this,SettingActivity.class));
        }else if(v == tv_about){
            startActivity(new Intent(this,AboutActivity.class));
        }
    }
}
