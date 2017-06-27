package com.vfinworks.vfsdk.activity.assistant;
 
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.vfinworks.vfsdk.R;
import com.vfinworks.vfsdk.SDKManager;
import com.vfinworks.vfsdk.activity.BaseActivity;
import com.vfinworks.vfsdk.activity.PeopleInfo.SafeCheckActivity;
import com.vfinworks.vfsdk.common.HttpRequsetUri;
import com.vfinworks.vfsdk.common.SHA256Encrypt;
import com.vfinworks.vfsdk.context.BaseContext;
import com.vfinworks.vfsdk.enumtype.PaymentPwdEnum;
import com.vfinworks.vfsdk.enumtype.VFCallBackEnum;
import com.vfinworks.vfsdk.http.HttpUtils;
import com.vfinworks.vfsdk.http.RequestParams;
import com.vfinworks.vfsdk.http.VFinResponseHandler;
import com.vfinworks.vfsdk.model.VFSDKResultModel;
import com.vfinworks.vfsdk.view.paypwd.VFEncryptData;
import com.vfinworks.vfsdk.view.paypwd.VFKeyBoardView;
import com.vfinworks.vfsdk.view.paypwd.VFKeyBoardView.OnKeyBoardClickedListener;
import com.vfinworks.vfsdk.view.paypwd.VFPasswordView;

import org.json.JSONException;
import org.json.JSONObject;




public class SetPaymentPwdActivity extends BaseActivity implements OnClickListener{
	public boolean isForgetPwd;

	private VFPasswordView mPassView;
	private VFKeyBoardView mKeyBoardView;

	private VFEncryptData mVerifyResult;

	private VFEncryptData mFirstResult;
	private VFEncryptData mSecondResult;
	private Button btnNext;
	private TextView tvLabel;
	private boolean isClick = false;
	private TranslateAnimation mShowAnima, mHideAnima;
	private int PASSWORD_LENGTH = 6; 
	private boolean isFirst = true;


	
	private BaseContext baseContext;

	private PaymentPwdEnum pwdEnum;
	
	public static int SetPasswordCode = 500; 
	public final static int SET_PAYPWD_CANCEL_CODE = 101;

	public void setPwdEnum(PaymentPwdEnum pwdEnum) {
		this.pwdEnum = pwdEnum;
	}
	public PaymentPwdEnum getPwdEnum() {
		return this.pwdEnum;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) { 
		setContentView(R.layout.payment_pwd_set,FLAG_TITLE_LINEARLAYOUT);
		baseContext = (BaseContext) this.getIntent().getExtras().getSerializable("context");
		pwdEnum =  (PaymentPwdEnum) this.getIntent().getExtras().getSerializable("type");

		super.onCreate(savedInstanceState);

		if(pwdEnum == PaymentPwdEnum.SETPASSWORD) {

			this.getTitlebarView().setTitle("设置支付密码");
		}else if(pwdEnum == PaymentPwdEnum.MODIFY_NEW_PASSWORD){
			this.getTitlebarView().setTitle("重置支付密码");
			isForgetPwd = true;
		}
		else
		{
			this.getTitlebarView().setTitle("修改支付密码");
			((TextView)findViewById(R.id.tv_hint_label)).setText("请输入旧的6位手机支付密码");
		}
        this.getTitlebarView().initLeft(new OnClickListener() {
			@Override
			public void onClick(View v) {
				backOnClick();
			}
		});
	}
	
	@Override
	public void initWidget() {  		
		mPassView = (VFPasswordView) findViewById(R.id.pay_password_view);
		mKeyBoardView = (VFKeyBoardView) findViewById(R.id.keyboard_view);
		btnNext = (Button) findViewById(R.id.button_next);
		tvLabel = (TextView)findViewById(R.id.tv_hint_label);
		mShowAnima = new TranslateAnimation(0, 0, 400, 0);
		mShowAnima.setDuration(300);  
		mHideAnima = new TranslateAnimation(0, 0, 0, 400);
		mHideAnima.setDuration(300);  
		
		mPassView.setOnClickListener(this);	
		btnNext.setOnClickListener(this);
		mKeyBoardView.setOnKeyBoardClickedListener(new OnKeyBoardClickedListener() {

			@Override
			public void onValueChanged(VFEncryptData result) {
				if (pwdEnum == PaymentPwdEnum.SETPASSWORD) {
					if (isFirst) {
						mFirstResult = result;
					} else {
						mSecondResult = result;
					}
				} else  {
					if (pwdEnum == PaymentPwdEnum.MODIFY_VERIFY_PASSWORD) {

						mVerifyResult = result;
					} else {
						if (isFirst) {
							mFirstResult = result;
						} else {
							mSecondResult = result;
						}
					}
				}

				mPassView.setStarCount(result.getLength());
				btnNext.setEnabled(result.getLength() == PASSWORD_LENGTH);
			}

	});
	}
	
	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.pay_password_view) {
			if(isClick) {
				setKeyBoardViewAnima(isClick);
				isClick = false;
			} else {
				setKeyBoardViewAnima(isClick);
				isClick = true;
			}
		} else if(v.getId() == R.id.button_next) {
			actionNext();
		}
	}
	 
	private void actionNext() {
		if(pwdEnum == PaymentPwdEnum.SETPASSWORD)	//如果是设置支付密码
		{
			if (isFirst) {
				tvLabel.setText("再次确认支付密码");
				mKeyBoardView.reset();
				mPassView.setStarCount(0);
				btnNext.setEnabled(false);
				isFirst = false;
			} else {
				if (mFirstResult != null && mSecondResult != null) {
					if (!mFirstResult.getCiphertext().equals(mSecondResult.getCiphertext())) {
						showShortToast("支付密码不一致，请重新输入");
						mKeyBoardView.reset();
						mPassView.setStarCount(0);
					} else {
						setPayPassword(mSecondResult);
					}
				} else {
					showShortToast("请输入密码!");
				}
			}
		}
		else
		{
			if(pwdEnum == PaymentPwdEnum.MODIFY_VERIFY_PASSWORD) {//如果是修改支付密码
				VerifyPassword(mVerifyResult);
			}
			else			//如果是设置新密码PaymentPwdEnum.MODIFY_NEW_PASSWORD
			{
				if (isFirst) {
					tvLabel.setText("再次确认支付密码");
					mKeyBoardView.reset();
					mPassView.setStarCount(0);
					btnNext.setEnabled(false);
					isFirst = false;
				} else {
					if (mFirstResult != null && mSecondResult != null) {
						if (!mFirstResult.getCiphertext().equals(mSecondResult.getCiphertext())) {
							showShortToast("支付密码不一致，请重新输入");
							mKeyBoardView.reset();
							mPassView.setStarCount(0);
						} else {
							setPayPassword(mSecondResult);
						}
					} else {
						showShortToast("请输入密码!");
					}
				}
			}
		}
	}


	private void VerifyPassword(VFEncryptData payPassword) {

		this.showProgress();
		RequestParams reqParam = new RequestParams();
		reqParam.putData("service", "verify_paypwd");
		reqParam.putData("token", SDKManager.token);
		reqParam.putData("pay_pwd", SHA256Encrypt.bin2hex(payPassword.getCiphertext()));

		HttpUtils.getInstance(this).excuteHttpRequest(HttpRequsetUri.getInstance().getMemberDoUri(), reqParam, new VFinResponseHandler(){

			@Override
			public void onSuccess(Object responseBean, String responseString) {
				hideProgress();
				JSONObject json;
				try {
					json = new JSONObject(responseString);
					String isSuccess = json.getString("is_success");
					if(isSuccess.equalsIgnoreCase("T") == true) {
						pwdEnum = PaymentPwdEnum.MODIFY_NEW_PASSWORD;
						mKeyBoardView.reset();
						mPassView.setStarCount(0);
						btnNext.setEnabled(false);
						getTitlebarView().setTitle("重置支付密码");
						((TextView)findViewById(R.id.tv_hint_label)).setText("请设置6位数字支付密码");
					}else{
						mKeyBoardView.reset();
						mPassView.setStarCount(0);
						showShortToast("支付密码验证失败！");
					}
				} catch (JSONException e) {
					e.printStackTrace();
					showShortToast(e.getMessage());
				}
			}

			@Override
			public void onError(String statusCode, String errorMsg) {
				hideProgress();
				showShortToast(errorMsg);
			}

		}, this);
	}

	private void setPayPassword(VFEncryptData payPassword) {
		this.showProgress();
		RequestParams reqParam = new RequestParams();
		reqParam.putData("service", "set_paypwd");
		reqParam.putData("token", SDKManager.token);
		reqParam.putData("pay_pwd", SHA256Encrypt.bin2hex(payPassword.getCiphertext()));

		HttpUtils.getInstance(this).excuteHttpRequest(HttpRequsetUri.getInstance().getMemberDoUri(), reqParam, new VFinResponseHandler(){

			@Override
			public void onSuccess(Object responseBean, String responseString) {
				hideProgress();
				JSONObject json;
				try {
					json = new JSONObject(responseString);
					String isSuccess = json.getString("is_success");
					if(isSuccess.equalsIgnoreCase("T") == true) {
						if(SDKManager.getInstance().getCallbackHandler() != null) {
							VFSDKResultModel result = new VFSDKResultModel();
							result.setResultCode(VFCallBackEnum.OK.getCode());
							result.setJsonData(responseString);
							baseContext.sendMessage(result);

						}
						if(isForgetPwd){
							Intent intent = new Intent(SetPaymentPwdActivity.this,SafeCheckActivity.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(intent);
						}
						finishActivity();
					}else{
						showShortToast("支付密码设置失败！");
					}
				} catch (JSONException e) {
					e.printStackTrace();
					showShortToast(e.getMessage());
				}
			}

			@Override
			public void onError(String statusCode, String errorMsg) {
				hideProgress();
				showShortToast(errorMsg);
			}
    		
    	}, this);
	}
	 
	private void setKeyBoardViewAnima(boolean isShow) {
		if(isShow) {
			mKeyBoardView.setVisibility(View.VISIBLE);
			mKeyBoardView.startAnimation(mShowAnima);
		} else {
			mKeyBoardView.startAnimation(mHideAnima);
			mHideAnima.setAnimationListener(new AnimationListener() {
				
				@Override
				public void onAnimationStart(Animation animation) { 
					
				}
				
				@Override
				public void onAnimationRepeat(Animation animation) { 
					
				}
				
				@Override
				public void onAnimationEnd(Animation animation) { 
					mKeyBoardView.setVisibility(View.GONE);
				}
			});
		}
	}
	 
	private void backOnClick() {
//		this.finishAll();
		finish();
	}

	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) { 
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			backOnClick();
		}  
		return super.onKeyDown(keyCode, event);
	}
} 
