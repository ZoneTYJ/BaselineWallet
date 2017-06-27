package com.vfinworks.vfsdk.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.vfinworks.vfsdk.R;
import com.vfinworks.vfsdk.common.Utils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by xiaoshengke on 2015/8/21.
 */
public class PicDialog extends AlertDialog {
    private Context context;
    private TextView pd_capture,pd_album,pd_cancel;
    private int naviHeight;
    public PicDialog(Context context) {
        super(context,R.style.diaolog_full_screen);
        this.context = context;
        naviHeight = Utils.getInstance().getNavigationBarHeight((Activity) context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        int height = (int) (Utils.getInstance().dip2px(getContext(), 150));
        int width = getContext().getResources().getDisplayMetrics().widthPixels;
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = width;
        lp.height = height;
        lp.x = 0;
        lp.y = getContext().getResources().getDisplayMetrics().heightPixels - height - naviHeight;
//        lp.gravity = Gravity.BOTTOM;
        onWindowAttributesChanged(lp);
        window.setWindowAnimations(R.style.main_menu_animstyle);
        setContentView(R.layout.dialog_pic);

        pd_capture = (TextView) findViewById(R.id.pd_capture);
        pd_album = (TextView) findViewById(R.id.pd_album);
        pd_cancel = (TextView) findViewById(R.id.pd_cancel);

        pd_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        pd_capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                listener.capturePic();
            }
        });
        pd_album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                listener.selectPic();
            }
        });
    }


    private ButtonsClickListener listener;

    public void setListener(ButtonsClickListener listener) {
        this.listener = listener;
    }

    public interface ButtonsClickListener{
        public void capturePic();
        public void selectPic();
    }
}
