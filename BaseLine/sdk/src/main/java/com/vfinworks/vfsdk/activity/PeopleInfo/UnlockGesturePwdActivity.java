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
import com.vfinworks.vfsdk.activity.BaseActivity;
import com.vfinworks.vfsdk.activity.core.WalletActivity;
import com.vfinworks.vfsdk.common.Config;
import com.vfinworks.vfsdk.common.L;
import com.vfinworks.vfsdk.common.SHA256Encrypt;
import com.vfinworks.vfsdk.common.SharedPreferenceUtil;
import com.vfinworks.vfsdk.view.ForgetConfirmDialog;
import com.vfinworks.vfsdk.view.LockPatternUtils;
import com.vfinworks.vfsdk.view.LockPatternView;

import java.util.List;


/**    
 * @package com.netfinworks.wallet.activity 
 * @description: 手势密码解锁界面
 * @version v1.0
 * @author JackieCheng
 * @email xiaming5368@163.com  
 * @date 2014-9-11 下午4:43:40  
 */
public class UnlockGesturePwdActivity extends BaseActivity implements View.OnClickListener {
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

	private ForgetConfirmDialog forgetConfirmDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_unlock_gesture_pwd,FLAG_TITLE_LINEARLAYOUT);
		getTitlebarView().setTitle("验证密码");
		forgetConfirmDialog = new ForgetConfirmDialog(this);
		forgetConfirmDialog.setSureClickListener(this);
		mCheckGesturePwd = this.getIntent().getBooleanExtra("check_gesture_password", false);

		mLockPatternView = (LockPatternView) this
				.findViewById(R.id.gesturepwd_unlock_lockview);
		mLockPatternView.setOnPatternListener(mChooseNewLockPatternListener);
		mLockPatternView.setTactileFeedbackEnabled(false);
		mHeadTextView = (TextView) findViewById(R.id.gesturepwd_unlock_text);
		mShakeAnim = AnimationUtils.loadAnimation(this, R.anim.vf_shake_x);
		tvForgetPwd = (TextView) findViewById(R.id.gesturepwd_unlock_forget);
		tvSwitchAccount = (TextView) findViewById(R.id.gesturepwd_switch_account);

		tvForgetPwd.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				forgetConfirmDialog.show();
			}
		});
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
			intent.putExtra("close_gesture",true);
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
					String token = getToken();
					if(!TextUtils.isEmpty(token)) {
						Intent walletintent = new Intent(UnlockGesturePwdActivity.this, WalletActivity.class);
						walletintent.putExtra("token", SDKManager.token);
						startActivity(walletintent);
					}else{
						go2Login();
					}
					finish();
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
					SharedPreferenceUtil.getInstance().setBooleanDataIntoSP(GestureSettingActivity.GESTURE_SHOW, false);
					SharedPreferenceUtil.getInstance().setStringDataIntoSP(GestureSettingActivity.GESTURE,"");
					showShortToast("手势密码已失效，请重新设置！");
//					showShortToast("手势密码已失效，请重新登录设置！");

					go2Login();
					finish();
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
		forgetConfirmDialog.dismiss();
		go2Login();
	}
}
