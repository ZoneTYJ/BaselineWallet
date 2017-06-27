package com.vfinworks.vfsdk.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Toast;

import com.vfinworks.vfsdk.R;
import com.vfinworks.vfsdk.SDKManager;
import com.vfinworks.vfsdk.Utils.RSA;
import com.vfinworks.vfsdk.common.L;
import com.vfinworks.vfsdk.common.SharedPreferenceUtil;
import com.vfinworks.vfsdk.db.KeyDatabaseHelper;
import com.vfinworks.vfsdk.http.HttpUtils;
import com.vfinworks.vfsdk.model.KeyModel;
import com.vfinworks.vfsdk.view.LoadingDialog;
import com.vfinworks.vfsdk.view.activitytitle.TitleHelper;
import com.vfinworks.vfsdk.view.activitytitle.TitlebarView;

import org.apaches.commons.codec.binary.Base64;

/**
 * Created by zhoudequan  Activity基类
 */
public abstract class BaseActivity extends FragmentActivity {
	/**
	 * 以线性布局方式添加title
	 */
	public static final int FLAG_TITLE_LINEARLAYOUT = TitleHelper.FLAG_TITLE_LINEARLAYOUT;
	/**
	 * 以相对布局方式添加title
	 */
	public static final int FLAG_TITLE_RELATIVELAYOUT = TitleHelper.FLAG_TITLE_RELATIVELAYOUT;
	
	private boolean isPush = true;
	protected Dialog mLoadingDialog;

	private Intent resultIntent;

	/**
	 * 初始化
	 */
	public  void initWidget()
	{};
	public void setIsPush(boolean isPushToStack)
	{
		isPush = isPushToStack;

	}
	
	public void finishActivity() {
		ActivityStackManager.getInstance().popActivity();
		overridePendingTransition(R.anim.vf_sdk_tran_pre_in, R.anim.vf_sdk_tran_pre_out); 
	}
	
	public void finishAll() {
		ActivityStackManager.getInstance().popAllActivity();
		overridePendingTransition(R.anim.vf_sdk_tran_pre_in, R.anim.vf_sdk_tran_pre_out); 
	}
	
	public void finishJumpTo(Class clazz) {
		ActivityStackManager.getInstance().popActivityExceptOne(clazz);
		overridePendingTransition(R.anim.vf_sdk_tran_pre_in, R.anim.vf_sdk_tran_pre_out); 
	}
	
	@Override
	public void startActivity(Intent intent) {
		super.startActivity(intent);
		overridePendingTransition(R.anim.vf_sdk_tran_next_in, R.anim.vf_sdk_tran_next_out);
		
	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		super.startActivityForResult(intent, requestCode);
		overridePendingTransition(R.anim.vf_sdk_tran_next_in, R.anim.vf_sdk_tran_next_out);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(isPush == true) {
			ActivityStackManager.getInstance().pushActivity(this);
		}
		mLoadingDialog = new LoadingDialog(this);
		initWidget();
	}

	@Override
	protected void onResume(){  
		super.onResume();
		if(SDKManager.token == null){
			SDKManager.token = getToken();
		}
	}

	@Override
	protected void onDestroy() {
		hideProgress();
		HttpUtils.getInstance(this).cancelHttp(this);
		ActivityStackManager.getInstance().removeActivity(this);
		super.onDestroy();
	}

	@Override
	protected void onStop() {
		super.onStop();
		hideProgress();
	}

	public void showProgress(){
		if(!mLoadingDialog.isShowing())
			mLoadingDialog.show();
	}
	
	public void hideProgress(){
		if(mLoadingDialog.isShowing())
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
	
	/*
	 * 带上title bar添加view
	 * flag：添加title bar 的方式：FLAG_TITLE_LINEARLAYOUT或者 FLAG_TITLE_RELATIVELAYOUT
	 */
	public void setContentView(View view, int flags) {
		TitleHelper helper = new TitleHelper(this);
		setContentView(helper.addTitleBar(view, obtainTitlebarView(), flags));
	}

	public void setContentView(int layoutResID, int flags) {
		setContentView(getLayoutInflater().inflate(layoutResID, null, false), flags);
	}
	
	private View obtainTitlebarView() {
		TitlebarView titleBarView = new TitlebarView(this);
		titleBarView.initLeft(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finishActivity();
			}
		});
		return titleBarView;
	}
	
	/**
	 * 得到标题栏view(如果添加了标题栏view的话)
	 * @return 
	 */
	public TitlebarView getTitlebarView() {
		TitlebarView titleBarView = (TitlebarView)findViewById(TitleHelper.DEFAULT_TITLE_VIEW_ID);
		return titleBarView;
	}

	public String getToken(){
//		return SharedPreferenceUtil.getInstance().getStringValueFromSP("token");
		KeyModel keyModel = KeyDatabaseHelper.getInstance(this).queryKey();
		String tempToken =  SharedPreferenceUtil.getInstance().getStringValueFromSP("token");
		try {
			return new String(RSA.decryptByPrivateKey(Base64.decodeBase64(tempToken.getBytes()),keyModel.privateKey));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	protected Intent getResultIntent() {
		return resultIntent;
	}

	protected void setResultIntent(Intent resultIntent) {
		this.resultIntent = resultIntent;
	}

	protected void onIntentForResult(Intent intent){

	}
}
