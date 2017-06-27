package com.vfinworks.vfsdk.activity.login;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.vfinworks.vfsdk.common.ResourceUtil;

/**
 * Created by xiaoshengke on 2016/4/12.
 */
public class ReqConfirmDialog extends Dialog {
    private Context context;
    private TextView tv_ls_sure;
    private TextView tv_ls_cancel;
    private TextView tv_ls_title;
    private TextView tv_cd_desc;
    private String title;
    private String desc;

    public ReqConfirmDialog(Context context) {
        this(context, 0);
    }

    public ReqConfirmDialog(Context context, int themeResId) {
        super(context, android.R.style.Theme_Holo_Dialog_NoActionBar);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ResourceUtil.getLayoutResource(context, "dialog_reg_confirm"));
        bindViews();
        initListener();
    }

    private void initListener() {
        tv_ls_cancel.setOnClickListener(cancelListener);
        if(sureListener != null)
            tv_ls_sure.setOnClickListener(sureListener);
        else
            throw new RuntimeException("ReqConfirmDialog的确定按钮点击事件监听不能为空");
    }

    public void setTitle(String title) {
        this.title = title;
        if(tv_ls_title != null){
            tv_ls_title.setText(title);
        }
    }

    public void setDesc(String desc){
        this.desc = desc;
        if(tv_cd_desc != null){
            tv_cd_desc.setText(desc);
        }
    }

    private void bindViews() {
        tv_ls_sure = (TextView) findViewById(ResourceUtil.getIdResource(context,"tv_true"));
        tv_ls_cancel = (TextView) findViewById(ResourceUtil.getIdResource(context,"tv_cancel"));
        tv_ls_title = (TextView) findViewById(ResourceUtil.getIdResource(context,"tv_cd_title"));
        tv_cd_desc = (TextView) findViewById(ResourceUtil.getIdResource(context,"tv_cd_desc"));
        if(!TextUtils.isEmpty(title)){
            tv_ls_title.setText(title);
        }
        if(!TextUtils.isEmpty(desc)){
            tv_cd_desc.setText(desc);
        }
    }

    public void setSureClickListener(View.OnClickListener sureClickListener){
        sureListener = sureClickListener;
    }

    private View.OnClickListener cancelListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dismiss();
        }
    };
    private View.OnClickListener sureListener;

}
