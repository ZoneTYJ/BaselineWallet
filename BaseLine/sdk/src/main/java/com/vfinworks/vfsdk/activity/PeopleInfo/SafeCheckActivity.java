package com.vfinworks.vfsdk.activity.PeopleInfo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.vfinworks.vfsdk.R;
import com.vfinworks.vfsdk.activity.BaseActivity;
import com.vfinworks.vfsdk.common.L;
import com.vfinworks.vfsdk.context.BaseContext;

import java.lang.ref.WeakReference;

/**
 * 安全校验
 * Created by xiaoshengke on 2016/9/5.
 */
public class SafeCheckActivity extends BaseActivity implements View.OnClickListener {
    public static WeakReference<BaseActivity> safeActivity;
    private RelativeLayout rl_identity;
    private BaseContext baseContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safe_check,FLAG_TITLE_LINEARLAYOUT);
        safeActivity = new WeakReference<BaseActivity>(this);
        baseContext = (BaseContext) getIntent().getExtras().getSerializable("context");
        getTitlebarView().setTitle("安全校验");
        rl_identity = (RelativeLayout) findViewById(R.id.rl_identity);

        rl_identity.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this,PayIDVerifyActivity.class);
        intent.putExtra("context",baseContext);
        startActivity(intent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        finishActivity();
    }
}
