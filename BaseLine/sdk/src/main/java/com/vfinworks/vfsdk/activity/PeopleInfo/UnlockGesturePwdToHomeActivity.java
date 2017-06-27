package com.vfinworks.vfsdk.activity.PeopleInfo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.vfinworks.vfsdk.R;
import com.vfinworks.vfsdk.SDKManager;
import com.vfinworks.vfsdk.Utils.RSA;
import com.vfinworks.vfsdk.activity.BaseActivity;
import com.vfinworks.vfsdk.activity.core.WalletActivity;
import com.vfinworks.vfsdk.activity.login.LoginActivity;
import com.vfinworks.vfsdk.common.Config;
import com.vfinworks.vfsdk.common.HttpRequsetUri;
import com.vfinworks.vfsdk.common.L;
import com.vfinworks.vfsdk.common.SHA256Encrypt;
import com.vfinworks.vfsdk.common.SharedPreferenceUtil;
import com.vfinworks.vfsdk.db.KeyDatabaseHelper;
import com.vfinworks.vfsdk.http.HttpUtils;
import com.vfinworks.vfsdk.http.RequestParams;
import com.vfinworks.vfsdk.http.VFinResponseHandler;
import com.vfinworks.vfsdk.model.KeyModel;
import com.vfinworks.vfsdk.view.GestureTipDialog;
import com.vfinworks.vfsdk.view.LockPatternUtils;
import com.vfinworks.vfsdk.view.LockPatternView;

import org.apaches.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


/**
 * @package com.netfinworks.wallet.activity
 * @description: 手势密码解锁界面
 * @version v1.0
 * @author JackieCheng
 * @email xiaming5368@163.com  
 * @date 2014-9-11 下午4:43:40  
 */
public class UnlockGesturePwdToHomeActivity extends BaseActivity implements View.OnClickListener {
	public static final int UNLOCK_FAILURE = 10000;
	private LockPatternView mLockPatternView; // 九宫格解锁界面
	private int mFailedPatternAttemptsSinceLastTimeout = 0;
	private CountDownTimer mCountdownTimer = null;
	private Handler mHandler = new Handler();
	private TextView mHeadTextView;
	private Animation mShakeAnim;

	/**忘记手势密码*/
	private TextView tvForgetPwd;
	/**切换其他账号*/
	private TextView tvSwitchAccount;

	private String mMobile;

	/**只是作为验证手势密码*/
	private boolean mCheckGesturePwd = false;

	private String pwd;

	private GestureTipDialog gestureTipDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_unlock_gesture_pwd_to_home);
		super.onCreate(savedInstanceState);
		pwd = SharedPreferenceUtil.getInstance().getStringValueFromSP("pwd","");
		gestureTipDialog = new GestureTipDialog(this);
		gestureTipDialog.setOnCompleteListener(this);
		mCheckGesturePwd = this.getIntent().getBooleanExtra("check_gesture_password", false);

		mLockPatternView = (LockPatternView) this
				.findViewById(R.id.gesturepwd_unlock_lockview);
		mLockPatternView.setOnPatternListener(mChooseNewLockPatternListener);
		mLockPatternView.setTactileFeedbackEnabled(false);
		mHeadTextView = (TextView) findViewById(R.id.gesturepwd_unlock_text);
		mShakeAnim = AnimationUtils.loadAnimation(this, R.anim.vf_shake_x);
		tvForgetPwd = (TextView) findViewById(R.id.gesturepwd_unlock_forget);
		tvSwitchAccount = (TextView) findViewById(R.id.gesturepwd_switch_account);

		tvSwitchAccount.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				go2Login();
			}
		});

		if(isShowGesturePwd()){
			mLockPatternView.setInStealthMode(false);
		}else{
			mLockPatternView.setInStealthMode(true);
		}
	}

	/**
	 * 是否显示手势轨迹
	 */
	private boolean isShowGesturePwd() {
		return SharedPreferenceUtil.getInstance().getBooleanValueFromSP(GestureSettingActivity.GESTURE_SHOW, false);
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mCountdownTimer != null)
			mCountdownTimer.cancel();
	}

	private void go2Login(){
		try {
			Class clazz = Class.forName(Config.LOGIN_CLASS);
			Intent intent = new Intent();
			intent.setClass(this,clazz);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private Runnable mClearPatternRunnable = new Runnable() {
		public void run() {
			mLockPatternView.clearPattern();
		}
	};

	protected LockPatternView.OnPatternListener mChooseNewLockPatternListener = new LockPatternView.OnPatternListener() {

		public void onPatternStart() {
			mLockPatternView.removeCallbacks(mClearPatternRunnable);
			patternInProgress();
		}

		public void onPatternCleared() {
			mLockPatternView.removeCallbacks(mClearPatternRunnable);
		}

		public void onPatternDetected(List<LockPatternView.Cell> pattern) {
			StringBuffer sbuffer = new StringBuffer();
			if (pattern == null)
				return;
			for(LockPatternView.Cell patter : pattern) {
				sbuffer.append(patter.getRow());
				sbuffer.append(patter.getColumn());
			}
			L.e("解锁mobile: -->" + mMobile);
			String gesturePwd = SharedPreferenceUtil.getInstance().getStringValueFromSP(GestureSettingActivity.GESTURE);
			if (!TextUtils.isEmpty(gesturePwd) && gesturePwd.equals(SHA256Encrypt.bin2hex(sbuffer.toString()))) {
				mLockPatternView
						.setDisplayMode(LockPatternView.DisplayMode.Correct);
				if(mCheckGesturePwd == true) {
					setResult(RESULT_OK);
					finish();
					return;
				}else{
					//					doLogin();
					mHeadTextView.setText("输入完成");
					mLockPatternView.clearPattern();
					gestureLogin();
				}
			} else {
				mLockPatternView
						.setDisplayMode(LockPatternView.DisplayMode.Wrong);
				if (pattern.size() >= LockPatternUtils.MIN_PATTERN_REGISTER_FAIL) {
					mFailedPatternAttemptsSinceLastTimeout++;
					int retry = LockPatternUtils.FAILED_ATTEMPTS_BEFORE_TIMEOUT
							- mFailedPatternAttemptsSinceLastTimeout;
					if (retry >= 0) {
						if (retry == 0) {
							//showShortToast("您已5次输错密码，请30秒后再试");
							showShortToast("您已5次输错密码!");
						}
						mHeadTextView.setText("密码错误，还可以再输入" + retry + "次");
						mHeadTextView.setTextColor(Color.RED);
						mHeadTextView.startAnimation(mShakeAnim);
					}

				} else {
					showShortToast("输入长度不够，请重试");
				}

				if (mFailedPatternAttemptsSinceLastTimeout >= LockPatternUtils.FAILED_ATTEMPTS_BEFORE_TIMEOUT) {
					if(mCheckGesturePwd == true) {
						setResult(UNLOCK_FAILURE);//代表失败
						finish();
						return;
					}
					//去掉30秒后重试功能，清掉手势密码信息，调起账户登录功能
					//mHandler.postDelayed(attemptLockout, 2000);
					SharedPreferenceUtil.getInstance().setBooleanDataIntoSP(GestureSettingActivity.GESTURE_SWITCH,false);
					SharedPreferenceUtil.getInstance().setStringDataIntoSP(GestureSettingActivity.GESTURE,"");
					//					showShortToast("手势密码已失效，请重新设置！");
					//					showShortToast("手势密码已失效，请重新登录设置！");

					gestureTipDialog.show();
				} else {
					mLockPatternView.postDelayed(mClearPatternRunnable, 2000);
				}
			}
		}

		public void onPatternCellAdded(List<LockPatternView.Cell> pattern) {

		}

		private void patternInProgress() {
		}
	};

	private void gestureLogin(){
		mLockPatternView.setEnabled(false);
		KeyModel keyModel = KeyDatabaseHelper.getInstance(this).queryKey();
		String refreshToken = SharedPreferenceUtil.getInstance().getStringValueFromSP("refresh_token");
		try {
			refreshToken = new String(RSA.decryptByPrivateKey(Base64.decodeBase64(refreshToken.getBytes()),keyModel.privateKey));
		} catch (Exception e) {
			e.printStackTrace();
		}
		RequestParams reqParam2 = new RequestParams();
		reqParam2.putData("service", "login_gesture");
		reqParam2.putData("refresh_token", refreshToken);
		HttpUtils.getInstance(this).excuteHttpRequest(HttpRequsetUri.getInstance().getMemberDoUri(), reqParam2, new VFinResponseHandler(){

			@Override
			public void onSuccess(Object responseBean, String responseString) {
				mLockPatternView.setEnabled(true);
				try {
					JSONObject jsonObject = new JSONObject(responseString);
					String token = jsonObject.getString("token");
					String refresh_token = jsonObject.getString("refresh_token");
					saveToken(token,refresh_token);
					Intent walletintent = new Intent(UnlockGesturePwdToHomeActivity.this, WalletActivity.class);
					walletintent.putExtra("token", token);
					startActivity(walletintent);
					finish();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onError(String statusCode, String errorMsg) {
				mLockPatternView.setEnabled(true);
				hideProgress();
				showShortToast(errorMsg);
				if("ILLEGAL_REFRESH_TOKEN".equals(statusCode)){
					go2Login();
					finish();
				}
			}

		}, this);
	}

	private void saveToken(String token,String refresh_token) {
		HttpUtils.getInstance(this).saveToken(token,refresh_token);
		SDKManager.token = token;
	}

	private void doLogin() {
		if(TextUtils.isEmpty(pwd)){
			go2Login();
			finish();
			return;
		}
		KeyModel keyModel = KeyDatabaseHelper.getInstance(this).queryKey();
		RequestParams reqParam = new RequestParams();
		reqParam.putData("service", "login_member");
		reqParam.putData("mobile", SharedPreferenceUtil.getInstance().getStringValueFromSP(LoginActivity.ACCOUNT));
		try {
			reqParam.putData("login_pwd", new String(RSA.decryptByPrivateKey(Base64.decodeBase64(pwd.getBytes()),keyModel.privateKey)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		reqParam.putData("device_id", Config.getInstance().getDeviceId());
		HttpUtils.getInstance(this).excuteHttpRequest(HttpRequsetUri.getInstance().getMemberDoUri(), reqParam, new VFinResponseHandler(){
			@Override
			public void onSuccess(Object responseBean, String responseString) {
				hideProgress();
				JSONObject json;
				try {
					json = new JSONObject(responseString);
					String token = json.getString("token");
					SharedPreferenceUtil.getInstance().setStringDataIntoSP("token", token);
					Intent walletintent = new Intent(UnlockGesturePwdToHomeActivity.this, WalletActivity.class);
					walletintent.putExtra("token", token);
					startActivity(walletintent);
				} catch (JSONException e) {
					e.printStackTrace();
					showShortToast(e.getMessage());
				}
				finish();
			}

			@Override
			public void onError(String statusCode, String errorMsg) {
				hideProgress();
				showShortToast(errorMsg);
			}

		}, this);
	}

	Runnable attemptLockout = new Runnable() {

		@Override
		public void run() {
			mLockPatternView.clearPattern();
			mLockPatternView.setEnabled(false);
			mCountdownTimer = new CountDownTimer(
					LockPatternUtils.FAILED_ATTEMPT_TIMEOUT_MS + 1, 1000) {

				@Override
				public void onTick(long millisUntilFinished) {
					int secondsRemaining = (int) (millisUntilFinished / 1000) - 1;
					if (secondsRemaining > 0) {
						mHeadTextView.setText(secondsRemaining + " 秒后重试");
					} else {
						mHeadTextView.setText("请绘制手势密码");
						mHeadTextView.setTextColor(Color.WHITE);
					}
				}

				@Override
				public void onFinish() {
					mLockPatternView.setEnabled(true);
					mFailedPatternAttemptsSinceLastTimeout = 0;
				}
			}.start();
		}
	};

	@Override
	public void onClick(View v) {
		gestureTipDialog.dismiss();
		go2Login();
		finish();
	}
}
