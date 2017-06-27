package com.vfinworks.vfsdk.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.TextView;

import com.vfinworks.vfsdk.R;
import com.vfinworks.vfsdk.common.Utils;

/**
 * 筛选菜单
 * Created by xiaoshengke on 2015/8/21.
 */
public class FilterDialog extends AlertDialog {
    private Context context;
    private TextView pd_cancel;
    private CheckBox cb_all;
    private CheckBox cb_instant;
    private CheckBox cb_danbao;
    private CheckBox cb_tran_hu;
    private CheckBox cb_tran_ka;
    private CheckBox cb_tui;
    private CheckBox cb_chong;
    private CheckBox cb_ti;
    private com.google.android.flexbox.FlexboxLayout fbl_layout;
    private int naviHeight;//下面软的导航条的高度
    public FilterDialog(Context context) {
        super(context, R.style.diaolog_full_screen);
        this.context = context;
        naviHeight = Utils.getInstance().getNavigationBarHeight((Activity) context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        View view = getLayoutInflater().inflate(R.layout.dialog_filter,null);
        view.measure(0,0);
        int height = view.getMeasuredHeight();
        int width = getContext().getResources().getDisplayMetrics().widthPixels;
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = width;
        lp.height = height;
        lp.x = 0;
        lp.y = getContext().getResources().getDisplayMetrics().heightPixels - height - naviHeight;
//        lp.gravity = Gravity.BOTTOM;  //这样也可以
        onWindowAttributesChanged(lp);
        window.setWindowAnimations(R.style.main_menu_animstyle);
        setContentView(view);

        fbl_layout = (com.google.android.flexbox.FlexboxLayout) findViewById(R.id.fbl_layout);
        cb_all = (CheckBox) findViewById(R.id.cb_all);
        cb_instant = (CheckBox) findViewById(R.id.cb_instant);
        cb_danbao = (CheckBox) findViewById(R.id.cb_danbao);
        cb_tran_hu = (CheckBox) findViewById(R.id.cb_tran_hu);
        cb_tran_ka = (CheckBox) findViewById(R.id.cb_tran_ka);
        cb_tui = (CheckBox) findViewById(R.id.cb_tui);
        cb_chong = (CheckBox) findViewById(R.id.cb_chong);
        cb_ti = (CheckBox) findViewById(R.id.cb_ti);
        pd_cancel = (TextView) findViewById(R.id.pd_cancel);

        pd_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        cb_all.setOnClickListener(clickListener);
        cb_instant.setOnClickListener(clickListener);
        cb_danbao.setOnClickListener(clickListener);
        cb_tran_hu.setOnClickListener(clickListener);
        cb_tran_ka.setOnClickListener(clickListener);
        cb_tui.setOnClickListener(clickListener);
        cb_chong.setOnClickListener(clickListener);
        cb_ti.setOnClickListener(clickListener);

        ((CheckBox) fbl_layout.getChildAt(lastClickIndex)).setChecked(true);
    }

    private int lastClickIndex = 0;
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            CheckBox checkBox = ((CheckBox) v);
            int i = fbl_layout.indexOfChild(checkBox);
            if(i == lastClickIndex){
                dismiss();
                return;
            }else{

                ((CheckBox) fbl_layout.getChildAt(lastClickIndex)).setChecked(false);
                lastClickIndex = i;
                if(listener != null){
                    listener.selectClick(lastClickIndex);
                }
                dismiss();
            }
        }
    };

    private SelectClickListener listener;

    public void setListener(SelectClickListener listener) {
        this.listener = listener;
    }

    public interface SelectClickListener {
        public void selectClick(int index);
    }
}
