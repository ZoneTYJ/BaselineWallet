package com.vfinworks.vfsdk.activity.core;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.vfinworks.vfsdk.R;
import com.vfinworks.vfsdk.SDKManager;
import com.vfinworks.vfsdk.activity.BaseActivity;
import com.vfinworks.vfsdk.common.HttpRequsetUri;
import com.vfinworks.vfsdk.common.NoDoubleClickListener;
import com.vfinworks.vfsdk.common.Utils;
import com.vfinworks.vfsdk.context.TransferContext;
import com.vfinworks.vfsdk.http.HttpUtils;
import com.vfinworks.vfsdk.http.RequestParams;
import com.vfinworks.vfsdk.http.VFinResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;


public class TransferAccountInputAccountActivity extends BaseActivity implements OnClickListener{
	private EditText etAccount;
	private Button btnNext;

	private TransferContext transferContext;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.transfer_account_input_account_activity,FLAG_TITLE_LINEARLAYOUT);
        transferContext = (TransferContext) this.getIntent().getExtras().getSerializable("context");
        super.onCreate(savedInstanceState);
        this.getTitlebarView().setTitle("转账到户");
        this.getTitlebarView().initLeft(new OnClickListener() {
			@Override
			public void onClick(View v) {
				backOnClick();
			}
		});
        
    }

	@Override
	public void initWidget() {
		etAccount = (EditText)findViewById(R.id.et_account);
		btnNext = (Button)findViewById(R.id.btn_next);
		btnNext.setOnClickListener(new NoDoubleClickListener() {
			@Override
			protected void onNoDoubleClick(View view) {
				if(checkMobile()){
					queryAccountInfo();
				}
			}
		});
		initEdit();
	}
	
	private void initEdit() {
		etAccount.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				btnNext.setEnabled(etAccount.getText().length() > 0);
			}

			@Override
			public void afterTextChanged(Editable s) {
				String charText = s.toString();
				// 第一位不允许输入空格
				if (charText != null && charText.startsWith(" ")) {
					s.delete(0, 1);
					return;
				}
				// 最后一位不允许输入空格
				if (charText != null && charText.endsWith(" ")) {
					s.delete(charText.length() - 1, charText.length());
					return;
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
										  int arg2, int arg3) {}
		}); 
	}
	
	@Override
	public void onClick(View arg0) {

	}

	private boolean checkMobile() {
		String mobile=etAccount.getText().toString().trim();
		if(!Utils.getInstance().isMobile(mobile) && !Utils.checkEmail(mobile)){
			showShortToast("账号输入有误，请重新输入");
			return false;
		}
		if(transferContext.getMobile().equals(mobile)){
			showShortToast("不能转给自己,请重新输入");
			return false;
		}
		return true;
	}

	private void queryAccountInfo(){
		showProgress();
		RequestParams reqParam = new RequestParams();
		reqParam.putData("service", "query_name");
		reqParam.putData("token", SDKManager.token);
		reqParam.putData("identity_no", etAccount.getText().toString().trim());
		HttpUtils.getInstance(this).excuteHttpRequest(HttpRequsetUri.getInstance().getMemberDoUri(), reqParam, new VFinResponseHandler(){

			@Override
			public void onSuccess(Object responseBean, String responseString) {
				hideProgress();
				try {
					JSONObject jsn = new JSONObject(responseString);
					String nickname = "";
					String realName = "";
					if(jsn.has("memberName")){
						nickname = jsn.optString("memberName");
					}
					if(jsn.has("realName")){
						realName = jsn.optString("realName");
					}
					if(jsn.has("status")){
						if("not_certification".equals(jsn.optString("status"))){
							showShortToast("收款人未实名认证");
						}else{
							btnNextClick(nickname,realName);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}

			@Override
			public void onError(String statusCode, String errorMsg) {
				hideProgress();
				if(errorMsg.equals("查询不到该会员")){
					errorMsg="收款人不存在";
				}
				showShortToast(errorMsg);
			}

		}, this);
	}
	
	private void btnNextClick(String nickname,String realName) {
		String number=etAccount.getText().toString().trim();
		Intent intent = new Intent();
    	Bundle bundle = new Bundle();
    	bundle.putSerializable("context", transferContext);
    	bundle.putString("account_number",number);
		if(!TextUtils.isEmpty(nickname)) {
			bundle.putString("nickname",nickname);
		}
		if(!TextUtils.isEmpty(realName)) {
			bundle.putString("realName",realName);
		}
    	intent.putExtras(bundle);
        intent.setClass(this,TransferAccountInputInfoActivity.class);
        this.startActivity(intent);
	}
	
	private void backOnClick() {
		this.finishActivity();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) { 
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			backOnClick();
		}  
		return super.onKeyDown(keyCode, event);
	}
}
