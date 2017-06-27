package com.vfinworks.vfsdk.activity.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;

import com.vfinworks.vfsdk.SDKManager;
import com.vfinworks.vfsdk.Utils.RSA;
import com.vfinworks.vfsdk.activity.PeopleInfo.GestureSettingActivity;
import com.vfinworks.vfsdk.activity.PeopleInfo.UnlockGesturePwdToHomeActivity;
import com.vfinworks.vfsdk.activity.core.channel.ChannelMaps;
import com.vfinworks.vfsdk.common.Config;
import com.vfinworks.vfsdk.common.HttpRequsetUri;
import com.vfinworks.vfsdk.common.PermissionHelper;
import com.vfinworks.vfsdk.common.SharedPreferenceUtil;
import com.vfinworks.vfsdk.db.KeyDatabaseHelper;
import com.vfinworks.vfsdk.model.KeyModel;

import net.sqlcipher.database.SQLiteDatabase;

import java.util.Map;

/**
 * Created by xiaoshengke on 2016/9/26.
 */
public class LoadingActivity extends Activity {
    private static String TAG = "MainActivity";
    private PermissionHelper mPermissionHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView textView = new TextView(this);
        textView.setText("初始化...");
        textView.setGravity(Gravity.CENTER);
        setContentView(textView);
        SDKManager.getInstance().init(this, Config.getInstance().PARTNER_ID);
        SDKManager.getInstance().SetCommonParam(Config.getInstance().PARTNER_ID,Config.getInstance().APP_ID,
                HttpRequsetUri.getInstance().getHttpServer(),"http://base.vfinance.cn/static/resources/help");

        // 当系统为6.0以上时，需要申请权限
        mPermissionHelper = new PermissionHelper(this);
        mPermissionHelper.setOnApplyPermissionListener(new PermissionHelper.OnApplyPermissionListener() {
            @Override
            public void onAfterApplyAllPermission() {
                Log.i(TAG, "All of requested permissions has been granted, so run app logic.");
                checkKey2();
            }
        });
        if (Build.VERSION.SDK_INT < 23) {
            // 如果系统版本低于23，直接跑应用的逻辑
            Log.d(TAG, "The api level of system is lower than 23, so run app logic directly.");
            checkKey2();
        } else {
            // 如果权限全部申请了，那就直接跑应用逻辑
            if (mPermissionHelper.isAllRequestedPermissionGranted()) {
                Log.d(TAG, "All of requested permissions has been granted, so run app logic directly.");
                checkKey2();
            } else {
                // 如果还有权限为申请，而且系统版本大于23，执行申请权限逻辑
                Log.i(TAG, "Some of requested permissions hasn't been granted, so apply permissions first.");
				mPermissionHelper.applyPermissions();
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mPermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPermissionHelper.onActivityResult(requestCode, resultCode, data);
    }




    private void checkKey2() {
        SQLiteDatabase.loadLibs(this);
        if(SharedPreferenceUtil.getInstance().getBooleanValueFromSP("dbkey",false)){
            go2Activity();
            return;
        }
        new AsyncTask<Context,Void,Void>(){

            @Override
            protected Void doInBackground(Context... params) {
                KeyDatabaseHelper.getInstance(params[0]).init();
                try {
                    Map<String, Object> genKeyPair = RSA.genKeyPair();
                    String privateKey = RSA.getPrivateKey(genKeyPair);
                    String publicKey = RSA.getPublicKey(genKeyPair);
                    KeyModel keyModel = KeyDatabaseHelper.getInstance(params[0]).queryKey();
                    if(keyModel == null){
                        keyModel = new KeyModel();
                        keyModel.privateKey = privateKey;
                        keyModel.publicKey = publicKey;
                        keyModel.createTime = System.currentTimeMillis();
                        KeyDatabaseHelper.getInstance(params[0]).insertKey(keyModel);
                        SharedPreferenceUtil.getInstance().setBooleanDataIntoSP("dbkey",true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                go2Activity();
            }
        }.execute(this.getApplicationContext());
    }

    private void go2Activity(){
        if(SharedPreferenceUtil.getInstance().getBooleanValueFromSP(GestureSettingActivity.GESTURE_SWITCH))
            startActivity(new Intent(LoadingActivity.this, UnlockGesturePwdToHomeActivity.class));
        else
            startActivity(new Intent(LoadingActivity.this,LoginActivity.class));
        finish();
    }
}
