package com.vfinworks.vfsdk.zxing.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.WriterException;
import com.vfinworks.vfsdk.R;
import com.vfinworks.vfsdk.activity.BaseActivity;
import com.vfinworks.vfsdk.zxing.encoding.EncodingHandler;

/**
 * Created by zhoudequan  收款
 */
public class ActivityGathering extends BaseActivity implements OnClickListener{
  
	public final static int SET_AMOUNT_CODE = 100;
	
	private ImageView imQrcode;
	private TextView tvQrcodeDes;
	private TextView tvAmount;
	private TextView tvReason;
	private TextView tvSetAmount;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) { 
		setContentView(R.layout.activity_gathering);
		super.onCreate(savedInstanceState);
	}
 
	@Override
	public void initWidget() { 
		imQrcode = (ImageView)this.findViewById(R.id.im_qrcode);
		tvQrcodeDes = (TextView)this.findViewById(R.id.tv_qrcode_des);
		tvSetAmount = (TextView)this.findViewById(R.id.tv_set_amount);
		tvSetAmount.setOnClickListener(this);
		tvAmount = (TextView)this.findViewById(R.id.tv_amount);
		tvReason = (TextView)this.findViewById(R.id.tv_reason);
		updateUI("","");
	}
	
	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.tv_set_amount) {
			setAmountClick();
		}
	}
	
	private void setAmountClick() {
		String strSetAmount = tvSetAmount.getText().toString();
		if(strSetAmount.equalsIgnoreCase(this.getResources().getString(R.string.gathering_clean_amount))) {
			updateUI("","");
		}else {
			Intent intent = new Intent(this,ActivityGatheringSetAmount.class);
			this.startActivityForResult(intent, SET_AMOUNT_CODE);
			overridePendingTransition(R.anim.vf_sdk_tran_next_in,R.anim.vf_sdk_tran_next_out);
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
		String strAmount = bundle.getString("amount");
		String strReason = bundle.getString("reason");
		updateUI(strAmount,strReason);
	}
	
	private void updateUI(String strAmount,String strReason) {
		if(TextUtils.isEmpty(strAmount) == true) {
			//imQrcode.setBackgroundResource(R.color.vf_sdk_gray);
			imQrcode.setImageBitmap(null);
			tvAmount.setVisibility(View.GONE);
			tvReason.setVisibility(View.GONE);
			tvSetAmount.setText(R.string.gathering_set_amount);
			tvQrcodeDes.setVisibility(View.VISIBLE);
		}else{
			tvAmount.setText("¥" + strAmount);
			tvAmount.setVisibility(View.VISIBLE);
			if(TextUtils.isEmpty(strReason) == true) {
				tvReason.setVisibility(View.GONE);
			}else{
				tvReason.setText(strReason);
				tvReason.setVisibility(View.VISIBLE);
			}
			tvSetAmount.setText(R.string.gathering_clean_amount);
			Bitmap bitMap = getQRCode(strAmount,strReason);
			if(bitMap != null) {
				Drawable drawable =new BitmapDrawable(bitMap);

				//imQrcode.setBackground(drawable);
				imQrcode.setImageBitmap(bitMap);
				tvQrcodeDes.setVisibility(View.GONE);
			}
		}
	}
	
	private Bitmap getQRCode(String strAmount,String strReason) {
		String strContent = "amount:"+strAmount + ";reason:"+strReason;
		try {
			return EncodingHandler.createQRCode(strContent, 1600);
		} catch (WriterException e) {
			e.printStackTrace();
			this.showShortToast("生成二维码失败，请重新生成！");
			return null;
		}
	}
	
	/**
	 * 返回
	 * @param v
	 */
	public void ActionBack(View v) {
		this.finish();
		overridePendingTransition(R.anim.vf_sdk_tran_pre_in, R.anim.vf_sdk_tran_pre_out); 
	}
	
}
