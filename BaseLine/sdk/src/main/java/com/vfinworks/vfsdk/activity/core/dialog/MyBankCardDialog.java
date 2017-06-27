package com.vfinworks.vfsdk.activity.core.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.vfinworks.vfsdk.R;
import com.vfinworks.vfsdk.view.ForgetConfirmDialog;

/**
 * Created by tangyijian on 2016/9/20.
 */
public class MyBankCardDialog extends Dialog {
    private Context mContext;
    private DiaBankClickListener mListener;

    private TextView tv_delete;
    private TextView tv_cancel;
    private ForgetConfirmDialog forgetConfirmDialog;

    public MyBankCardDialog(Context context) {
        super(context, R.style.diaolog_bankcard);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window win = getWindow();
        win.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.FILL_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        win.setAttributes(lp);

        setContentView(R.layout.dialog_mybankcard);

        tv_delete = (TextView) findViewById(R.id.tv_delete);
        tv_cancel = (TextView) findViewById(R.id.tv_cancel);

        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        tv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgetConfirmDialog = new ForgetConfirmDialog(mContext);
                forgetConfirmDialog.setMessage("删除该银行卡？");
                forgetConfirmDialog.setRightBtnText("删除");
                forgetConfirmDialog.setSureClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.deleteBankCard();
                        dismiss();
                        forgetConfirmDialog.dismiss();
                    }
                });
                forgetConfirmDialog.show();
            }
        });
    }

    public void setListener(DiaBankClickListener listener) {
        mListener = listener;
    }

    public interface DiaBankClickListener {
        public void deleteBankCard();
    }

}
