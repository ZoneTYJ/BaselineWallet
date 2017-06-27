package com.vfinworks.vfsdk.common;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by xiaoshengke on 2016/9/5.
 */
public class ViewUtil {
    public static void viewEnableStatusChange(EditText view, final Button next){
        view.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() > 0){
                    next.setEnabled(true);
                }else{
                    next.setEnabled(false);
                }
            }
        });
    }
}
