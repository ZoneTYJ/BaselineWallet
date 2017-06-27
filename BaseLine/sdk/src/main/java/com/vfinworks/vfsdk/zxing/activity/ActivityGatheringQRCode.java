package com.vfinworks.vfsdk.zxing.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.vfinworks.vfsdk.R;
import com.vfinworks.vfsdk.SDKManager;
import com.vfinworks.vfsdk.activity.BaseActivity;
import com.vfinworks.vfsdk.authenticator.AuthenMain;
import com.vfinworks.vfsdk.authenticator.Base32String;
import com.vfinworks.vfsdk.common.Utils;
import com.vfinworks.vfsdk.zxing.encoding.EncodingHandler;

import org.json.JSONObject;

import cn.qqtheme.framework.util.LogUtils;

/**
 * Created by tyj  收款
 * 生成二维码收款
 */
public class ActivityGatheringQRCode extends BaseActivity implements OnClickListener {
	public final static int SET_AMOUNT_CODE = 100;
	private BaseActivity mConext = this;

	private ImageView imQrcode;
	private TextView tvQrcodeDes;
	private TextView tvAmount;
	private TextView tvReason;
	private TextView tvSetAmount;
	private String mPhone;
	private AuthenMain mAuthenMain;


	private Handler mScheduleHandler;
	private Runnable mScheduleRunnable;
	private int SCHEDULE_TIME = 60 * 1000;
	private String mStrAmount="";
	private String mStrReason="";

	boolean isTotp =false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_gathering, FLAG_TITLE_LINEARLAYOUT);
		super.onCreate(savedInstanceState);
		getTitlebarView().setTitle("收款");
	}

	@Override
	public void initWidget() {
		mPhone = getIntent().getStringExtra("phone");
		imQrcode = (ImageView) this.findViewById(R.id.im_qrcode);
		tvQrcodeDes = (TextView) this.findViewById(R.id.tv_qrcode_des);
		tvSetAmount = (TextView) this.findViewById(R.id.tv_set_amount);
		tvSetAmount.setOnClickListener(this);
		tvAmount = (TextView) this.findViewById(R.id.tv_amount);
		tvReason = (TextView) this.findViewById(R.id.tv_reason);

		mScheduleHandler = new Handler();
		mScheduleRunnable = new Runnable() {
			@Override
			public void run() {
				updateUI();
			}
		};
		initAuthenicator();
	}

	private void initAuthenicator() {
		mAuthenMain = new AuthenMain(mConext);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.tv_set_amount) {
			setAmountClick();
		}
	}

	private void setAmountClick() {
		String strSetAmount = tvSetAmount.getText().toString();
		if (strSetAmount.equalsIgnoreCase(this.getResources().getString(R.string
				.gathering_clean_amount))) {
			mStrAmount="";
			mStrReason="";
			updateUI();
		} else {
			Intent intent = new Intent(this, ActivityGatheringSetAmount.class);
			this.startActivityForResult(intent, SET_AMOUNT_CODE);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			case SET_AMOUNT_CODE:
				if (resultCode == this.RESULT_OK) {
					Bundle bundle = data.getExtras();
					getQRcodePro(bundle);
				}
				break;
		}
	}

	private void getQRcodePro(Bundle bundle) {
		mStrAmount = bundle.getString("amount");
		mStrReason = bundle.getString("reason");
		updateUI();
	}

	private void updateUI() {
		mScheduleHandler.removeCallbacks(mScheduleRunnable);
		mScheduleHandler.postDelayed(mScheduleRunnable, SCHEDULE_TIME);
		if (TextUtils.isEmpty(mStrAmount)) {
			//imQrcode.setBackgroundResource(R.color.vf_sdk_gray);
			imQrcode.setImageBitmap(null);
			tvAmount.setVisibility(View.GONE);
			tvReason.setVisibility(View.GONE);
			tvSetAmount.setText(R.string.gathering_set_amount);
			tvQrcodeDes.setVisibility(View.VISIBLE);
		} else {
			tvAmount.setText("金额: ¥" + mStrAmount);
			tvAmount.setVisibility(View.VISIBLE);
			if (TextUtils.isEmpty(mStrReason)) {
				tvReason.setVisibility(View.GONE);
			} else {
				tvReason.setText("备注: " + mStrReason);
				tvReason.setVisibility(View.VISIBLE);
			}
			tvSetAmount.setText(R.string.gathering_clean_amount);
		}

		Bitmap bitMap = getQRCode(mStrAmount, mStrReason);
		if (bitMap != null) {
			//				Drawable drawable =new BitmapDrawable(bitMap);
			//				imQrcode.setBackground(drawable);
			imQrcode.setImageBitmap(bitMap);
			tvQrcodeDes.setVisibility(View.GONE);
		}
	}

	private Bitmap getQRCode(String strAmount, String strReason) {
		JSONObject object = new JSONObject();
		String StringQRCode="";
		if(Utils.checkEmail(mPhone)){
			StringQRCode=mPhone;
		}else if(mAuthenMain!=null){
			String token= SDKManager.token;
			String code;
			if (isTotp) {
				code= mAuthenMain.getCurrentCode(Base32String.encode(token.getBytes()));
			} else {
				code = "000002";
			}
			StringQRCode=mAuthenMain.getStringQRCode(code,mPhone);
		}
		try {
			object.put("code",StringQRCode);
			object.put("amount", strAmount);
			object.put("memo", strReason+" ");
			//object.toString()长度为53时候 二维码会异常
			showShortToast(object.toString()+"count="+object.toString().length());
			return EncodingHandler.createQRCode(object.toString(), 800);
		} catch (Exception e) {
			e.printStackTrace();
			LogUtils.debug(e.getMessage());
			this.showShortToast("生成二维码失败，请重新生成！" + e.getMessage());
			return null;
		}
	}

	/**
	 * 返回
	 *
	 * @param v
	 */
	public void ActionBack(View v) {
		finishActivity();
	}

	@Override
	protected void onResume() {
		super.onResume();
		updateUI();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mScheduleHandler.removeCallbacks(mScheduleRunnable);
	}
}
