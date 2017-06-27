package com.vfinworks.vfsdk.activity.PeopleInfo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.vfinworks.vfsdk.R;
import com.vfinworks.vfsdk.activity.BaseActivity;
import com.vfinworks.vfsdk.common.Config;

/**
 * 服务协议
 * Created by xiaoshengke on 2016/10/8.
 */
public class ProtocolActivity extends BaseActivity implements View.OnClickListener {
    private RelativeLayout rl_wallet;
    private RelativeLayout rl_pay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_protocol,FLAG_TITLE_LINEARLAYOUT);
        getTitlebarView().setTitle("服务协议");
        bindViews();
    }

    private void bindViews() {
        rl_wallet = (RelativeLayout) findViewById(R.id.rl_wallet);
        rl_pay = (RelativeLayout) findViewById(R.id.rl_pay);

        rl_wallet.setOnClickListener(this);
        rl_pay.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if(v == rl_pay){
            Intent intent = new Intent(this,DescriptionActivity.class);
            intent.putExtra("title","快捷支付协议");
            intent.putExtra("url", Config.getInstance().staticResourceDir+"/pact_payment.html");
            startActivity(intent);
        }else{
            Intent intent = new Intent(this,DescriptionActivity.class);
            intent.putExtra("title","钱包服务协议");
            intent.putExtra("url",Config.getInstance().staticResourceDir+"/pact_wallet.html");
            startActivity(intent);
        }
    }
}
