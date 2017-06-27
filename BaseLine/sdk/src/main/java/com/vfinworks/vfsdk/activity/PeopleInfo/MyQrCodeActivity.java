package com.vfinworks.vfsdk.activity.PeopleInfo;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import com.google.zxing.WriterException;
import com.vfinworks.vfsdk.R;
import com.vfinworks.vfsdk.activity.BaseActivity;
import com.vfinworks.vfsdk.common.QrCodeUtils;

/**
 * 我的二维码
 * Created by xiaoshengke on 2016/8/18.
 */
public class MyQrCodeActivity extends BaseActivity {
    private ImageView ivMycode;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_qrcode,FLAG_TITLE_LINEARLAYOUT);
        getTitlebarView().setTitle("我的二维码");
        ivMycode = (ImageView) findViewById(R.id.iv_mycode);
        init();
    }

    private void init() {
        name = getIntent().getStringExtra("name");
        ivMycode.post(new Runnable() {
            @Override
            public void run() {
                try {
                    Bitmap bitmap = QrCodeUtils.Create2DCode(name);
                    ivMycode.setImageBitmap(bitmap);
                } catch (WriterException e) {
                    e.printStackTrace();
                }
            }
        });


    }
}
