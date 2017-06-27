package com.vfinworks.vfsdk.activity.login;

import com.vfinworks.vfsdk.view.LoadingDialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

/**
 * Created by zhoudequan  Activity基类
 */
public abstract class BaseActivity extends FragmentActivity {
	public Dialog mLoadingDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);
		mLoadingDialog = new LoadingDialog(this);
	}
	 
	protected void onResume(){  
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	public void showProgress(){
		mLoadingDialog.show();
	}
	
	public void hideProgress(){
		mLoadingDialog.dismiss();
	}
	 
	/**
	 * 显示duration为0的Toast
	 */
	public void showShortToast(String text) {
		showToast(text, Toast.LENGTH_SHORT);
	}

	/**
	 * 显示duration为0的Toast
	 */
	public void showShortToast(int textId) {
		showShortToast(getString(textId));
	}

	/**
	 * 显示duration为1的Toast
	 */
	public void showLongToast(String text) {
		showToast(text, Toast.LENGTH_LONG);
	}

	/**
	 * 显示duration为1的Toast
	 */
	public void showLongToast(int textId) {
		showLongToast(getString(textId));
	}

	/**
	 * 显示toast
	 * @param text 文字
	 * @param duration
	 */
	public void showToast(String text,int duration) {
		Toast.makeText(this, text, duration).show();
	}
}
