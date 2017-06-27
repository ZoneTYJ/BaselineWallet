package com.vfinworks.vfsdk.activity.PeopleInfo;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vfinworks.vfsdk.R;
import com.vfinworks.vfsdk.activity.BaseActivity;

/**
 * 关于
 * Created by xiaoshengke on 2016/10/8.
 */
public class AboutActivity extends BaseActivity implements View.OnClickListener {
    private TextView tv_version_id;
    private RelativeLayout rl_introduction;
    private RelativeLayout rl_advise;
    private RelativeLayout rl_protocol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about,FLAG_TITLE_LINEARLAYOUT);
        getTitlebarView().setTitle("关于");
        bindViews();
    }

    private void bindViews() {
        tv_version_id = (TextView) findViewById(R.id.tv_version_id);
        rl_introduction = (RelativeLayout) findViewById(R.id.rl_introduction);
        rl_advise = (RelativeLayout) findViewById(R.id.rl_advise);
        rl_protocol = (RelativeLayout) findViewById(R.id.rl_protocol);

        PackageManager manager = this.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(this.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        tv_version_id.setText("版本号"+info.versionName);

        rl_introduction.setOnClickListener(this);
        rl_advise.setOnClickListener(this);
        rl_protocol.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if(v == rl_introduction){
            Intent intent = new Intent(this,VersionIntroActivity.class);
            startActivity(intent);
        }else if(v == rl_advise){
            startActivity(new Intent(this,AdviseActivity.class));
        }else if(v == rl_protocol){
            startActivity(new Intent(this,ProtocolActivity.class));
        }
    }
}
