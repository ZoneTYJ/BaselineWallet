package com.vfinworks.vfsdk.activity.PeopleInfo;


import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.vfinworks.vfsdk.R;
import com.vfinworks.vfsdk.activity.BaseActivity;
import com.vfinworks.vfsdk.common.SHA256Encrypt;
import com.vfinworks.vfsdk.common.SharedPreferenceUtil;
import com.vfinworks.vfsdk.view.LockPatternIndicatorView;
import com.vfinworks.vfsdk.view.LockPatternUtils;
import com.vfinworks.vfsdk.view.LockPatternView;

import java.util.ArrayList;
import java.util.List;


/**    
 * @package com.netfinworks.wallet.activity 
 * @description: 创建手势密码界面
 * @version v1.0
 * @author JackieCheng
 * @email xiaming5368@163.com  
 * @date 2014-9-11 下午4:30:48  
 */
public class CreateGesturePwdActivity extends BaseActivity {

	private static final int ID_EMPTY_MESSAGE = -1;
	private static final String KEY_UI_STAGE = "uiStage";
	private static final String KEY_PATTERN_CHOICE = "chosenPattern";
	private LockPatternView mLockPatternView;
	private LockPatternIndicatorView indicatorView;
	protected TextView mHeaderText;
	protected List<LockPatternView.Cell> mChosenPattern = null;
	private Stage mUiStage = Stage.Introduction; 
	  
	private static final int ExcuteToConfirm = 0x01;
	private static final int ExcuteConfirmed = 0x02;
	 
	private String flag = "";
	
	private StringBuffer sbuffer = new StringBuffer();

	private Animation mShakeAnim;
	
	/**
	 * Keep track internally of where the user is in choosing a pattern.
	 */
	protected enum Stage {
		// 绘制解锁图案
		Introduction(R.string.lockpattern_recording_intro_header,
				ID_EMPTY_MESSAGE), 		
		// 至少链接4个点, 请重试
		ChoiceTooShort(
				R.string.lockpattern_recording_incorrect_too_short, 
				ID_EMPTY_MESSAGE), 
		// 图案已记录
		FirstChoiceValid(
				R.string.lockpattern_pattern_entered_header, 
				ID_EMPTY_MESSAGE), 
		// 请再次绘制解锁图案
		NeedToConfirm(
				R.string.lockpattern_need_to_confirm, ID_EMPTY_MESSAGE), 
		// 与上次输入不一致, 请重试
		ConfirmWrong(
				R.string.lockpattern_need_to_unlock_wrong, 
				ID_EMPTY_MESSAGE), 
		// 确认保存新解锁图案
		ChoiceConfirmed(
				R.string.lockpattern_pattern_confirmed_header, 
				ID_EMPTY_MESSAGE);

		/**
		 * @param headerMessage
		 *            The message displayed at the top.
		 * @param footerMessage
		 *            The footer message.
		 */
		Stage(int headerMessage, int footerMessage) {
			this.headerMessage = headerMessage; 
			this.footerMessage = footerMessage; 
		}

		final int headerMessage; 
		final int footerMessage; 
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_create_gesture_pwd, FLAG_TITLE_LINEARLAYOUT);
		super.onCreate(savedInstanceState);
		getTitlebarView().initLeft(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		getTitlebarView().setTitle("设置手势密码");
		getTitlebarView().initRight("重置", new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mLockPatternView.clearPattern();
				indicatorView.resetPatternIndicator();
				updateStage(Stage.Introduction);
			}
		});

		mShakeAnim = AnimationUtils.loadAnimation(this, R.anim.vf_shake_x);
		mLockPatternView = (LockPatternView) this
				.findViewById(R.id.gesturepwd_create_lockview);
		mHeaderText = (TextView) findViewById(R.id.gesturepwd_create_text);
		indicatorView = (LockPatternIndicatorView) findViewById(R.id.lpi_indicator);
		mLockPatternView.setOnPatternListener(mChooseNewLockPatternListener);
		mLockPatternView.setTactileFeedbackEnabled(false);
//		mLockPatternView.setInStealthMode(true);
		mLockPatternView.setShowTriangle(true);

		if (savedInstanceState == null) {
			updateStage(Stage.Introduction);
		} else {
			final String patternString = savedInstanceState
					.getString(KEY_PATTERN_CHOICE);
			if (patternString != null) {
				mChosenPattern = LockPatternUtils
						.stringToPattern(patternString);
			}
			updateStage(Stage.values()[savedInstanceState.getInt(KEY_UI_STAGE)]);
		}
	}




	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(KEY_UI_STAGE, mUiStage.ordinal());
		if (mChosenPattern != null) {
			outState.putString(KEY_PATTERN_CHOICE,
					LockPatternUtils.patternToString(mChosenPattern));
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
		
		}

		public void onPatternCleared() {
			mLockPatternView.removeCallbacks(mClearPatternRunnable);
		}

		public void onPatternDetected(List<LockPatternView.Cell> pattern) {
			if (pattern == null)
				return;
			if (mUiStage == Stage.NeedToConfirm
					|| mUiStage == Stage.ConfirmWrong) {
				if (mChosenPattern == null)
					throw new IllegalStateException(
							"null chosen pattern in stage 'need to confirm");
				if (mChosenPattern.equals(pattern)) {
					updateStage(Stage.ChoiceConfirmed);
				} else {
					updateStage(Stage.ConfirmWrong);
				}
			} else if (mUiStage == Stage.Introduction
					|| mUiStage == Stage.ChoiceTooShort) {
//				if (false) {
				if (pattern.size() < LockPatternUtils.MIN_LOCK_PATTERN_SIZE) {
					updateStage(Stage.ChoiceTooShort);
				} else {
					mChosenPattern = new ArrayList<LockPatternView.Cell>(
							pattern);
					indicatorView.updatePatternSelect((ArrayList<LockPatternView.Cell>) mChosenPattern);
					// 图案已记录
					updateStage(Stage.FirstChoiceValid);
				}
			} else {
				throw new IllegalStateException("Unexpected stage " + mUiStage
						+ " when " + "entering the pattern.");
			}
		}

		public void onPatternCellAdded(List<LockPatternView.Cell> pattern) {

		}
		
	};
	 
	/**
	 * 根据不同的操作状态, 提示不同的信息
	 * 
	 * @param stage
	 */
	private void updateStage(Stage stage) {
		mUiStage = stage;
		mHeaderText.setTextColor(getResources().getColor(R.color.text_9));
		// 至少链接4个点, 请重试
		if (stage == Stage.ChoiceTooShort) {
			mHeaderText.setText(getResources().getString(stage.headerMessage,
					LockPatternUtils.MIN_LOCK_PATTERN_SIZE));
			mHeaderText.setTextColor(Color.RED);
			mHeaderText.startAnimation(mShakeAnim);
		} else {
			mHeaderText.setText(stage.headerMessage);
		}

		mLockPatternView.setDisplayMode(LockPatternView.DisplayMode.Correct);

		switch (mUiStage) {
		// 绘制解锁图案
		case Introduction:
			mLockPatternView.clearPattern();
			break;
		// 至少链接4个点, 请重试
		case ChoiceTooShort:
			mLockPatternView.setDisplayMode(LockPatternView.DisplayMode.Wrong);
			postClearPatternRunnable();
			break;
		// 图案已记录
		case FirstChoiceValid:
			handler.sendEmptyMessageDelayed(ExcuteToConfirm, 500);	
			break;
		case NeedToConfirm:
			mLockPatternView.clearPattern(); 
			break;
		// 请再次绘制解锁图案
		case ConfirmWrong:
			mHeaderText.setTextColor(Color.RED);
			mHeaderText.startAnimation(mShakeAnim);
			mLockPatternView.setDisplayMode(LockPatternView.DisplayMode.Wrong);
			postClearPatternRunnable();
			break;
		// 确认保存新解锁图案
		case ChoiceConfirmed:
			handler.sendEmptyMessageDelayed(ExcuteConfirmed, 500);	
			break;
		}

	}

	// clear the wrong pattern unless they have started a new one
	// already
	private void postClearPatternRunnable() {
		mLockPatternView.removeCallbacks(mClearPatternRunnable);
		mLockPatternView.postDelayed(mClearPatternRunnable, 2000);
	}

	private Handler handler = new Handler() {

		@Override
		public void dispatchMessage(Message msg) { 
			switch(msg.what) {
			// 图案已记录
			case ExcuteToConfirm: {
				if (mUiStage != Stage.FirstChoiceValid) {
					throw new IllegalStateException("expected ui stage "
							+ Stage.FirstChoiceValid + " when button is ");
				}
				// 请再次绘制解锁图案
				updateStage(Stage.NeedToConfirm);
				break;
			}
			// 确认保存新解锁图案
			case ExcuteConfirmed: {
				if (mUiStage != Stage.ChoiceConfirmed) {
					throw new IllegalStateException("expected ui stage "
							+ Stage.ChoiceConfirmed + " when button is ");
				}
				// 保存解锁图案, 并进入下一个页面
				saveChosenPatternAndFinish();
				break;
			}
			default:break;
			}
		}		
	};
	
	private void saveChosenPatternAndFinish() {
		for(LockPatternView.Cell pattern : mChosenPattern) { 
			sbuffer.append(pattern.getRow());
			sbuffer.append(pattern.getColumn());
		}
		// 正常登录设置手势密码
		userStates();
		showShortToast("密码设置成功!");
	}
	
	private void userStates() {
		SharedPreferenceUtil.getInstance().setStringDataIntoSP(GestureSettingActivity.GESTURE, SHA256Encrypt.bin2hex(sbuffer.toString()));
		SharedPreferenceUtil.getInstance().setBooleanDataIntoSP(GestureSettingActivity.GESTURE_SWITCH,true);
		SharedPreferenceUtil.getInstance().setBooleanDataIntoSP(GestureSettingActivity.GESTURE_SHOW, true);
		finishActivity();
	}
	




}
