package com.vfinworks.vfsdk.zxing.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vfinworks.vfsdk.R;
import com.vfinworks.vfsdk.activity.BaseActivity;

import java.math.BigDecimal;

/**
 * Created by zhoudequan  收款设定金额
 */
public class ActivityGatheringSetAmount extends BaseActivity implements OnClickListener{
	private TextView tvReason;
	private EditText etAmount;
	private EditText etReason;
	private RelativeLayout rlReason;
	private Button btnSubmit;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) { 
		setContentView(R.layout.activity_gathering_set_amount,FLAG_TITLE_LINEARLAYOUT);
		super.onCreate(savedInstanceState);
		this.getTitlebarView().setTitle("收款");
		this.getTitlebarView().initLeft(new OnClickListener() {
			@Override
			public void onClick(View v) {
				backOnClick();
			}
		});
	}
 
	@Override
	public void initWidget() { 
		tvReason = (TextView)this.findViewById(R.id.tv_set_reason);
		tvReason.setOnClickListener(this);
		rlReason = (RelativeLayout)this.findViewById(R.id.layout_reason);
		etAmount = (EditText)this.findViewById(R.id.et_amount);
		etReason = (EditText)this.findViewById(R.id.et_reason);
		btnSubmit = (Button)this.findViewById(R.id.btn_confirm);
		btnSubmit.setOnClickListener(this);
		tvReason.setVisibility(View.VISIBLE);
		rlReason.setVisibility(View.GONE);
		initEditTextAmount();
		reasonClick();//默认就打开
	}
	
	private void initEditTextAmount() {
		etAmount.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				btnSubmit.setEnabled(etAmount.getText().length() > 0);
			}

			@Override
			public void afterTextChanged(Editable s) {
				String charText = s.toString();
				// 第一位不允许输入空格
				if (charText != null && charText.startsWith(" ")
						|| charText.startsWith(".")) {
					s.delete(0, 1);
					return;
				}
				// 最后一位不允许输入空格 并且限制长度为12位
				if ((charText != null && charText.endsWith(" ")) || (charText.length() > 12)) {
					s.delete(charText.length() - 1, charText.length());
					return;
				}
				// 如果输入的是数字,只允许输入小数点后两位
				int posDot = charText.indexOf(".");
				if (posDot <= 0)
					return;
				if (charText.length() - posDot - 1 > 2) {
					s.delete(posDot + 3, posDot + 4);
				}
			}

		});
	}
	
	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.btn_confirm) {
			submitClick();
		}else if(v.getId() == R.id.tv_set_reason) {
			reasonClick();
		}
	}
	
	private void submitClick() {
		//检查金额输入
		String strAmount = etAmount.getText().toString();
		BigDecimal amount = new BigDecimal(strAmount);
		BigDecimal zero = new BigDecimal(0.00);
		if(amount.compareTo(zero) <= 0) {
			showShortToast("请输入正确的收款金额!");
			return;
		}
		if(amount.compareTo(new BigDecimal(20000))>0){
			showShortToast("金额不能大于2万");
			return;
		}
		strAmount=String.format("%.2f",amount.doubleValue());
		Intent resultIntent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putString("amount", strAmount);
		bundle.putString("reason", etReason.getText().toString());
		resultIntent.putExtras(bundle);
		this.setResult(RESULT_OK, resultIntent);
		finishActivity();
	}
	
	private void reasonClick() {
		tvReason.setVisibility(View.GONE);
		rlReason.setVisibility(View.VISIBLE);
	}
	
	public void backOnClick() {
		finishActivity();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			backOnClick();
		}
		return super.onKeyDown(keyCode, event);
	}
	
}
