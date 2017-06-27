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
import android.widget.ImageView;
import android.widget.TextView;

import com.vfinworks.vfsdk.R;
import com.vfinworks.vfsdk.SDKManager;
import com.vfinworks.vfsdk.activity.BaseActivity;
import com.vfinworks.vfsdk.business.GetLimit;
import com.vfinworks.vfsdk.common.HttpRequsetUri;
import com.vfinworks.vfsdk.common.NoDoubleClickListener;
import com.vfinworks.vfsdk.common.SHA256Encrypt;
import com.vfinworks.vfsdk.common.Utils;
import com.vfinworks.vfsdk.context.LimitContext;
import com.vfinworks.vfsdk.context.TransferContext;
import com.vfinworks.vfsdk.context.TransferToCardContext;
import com.vfinworks.vfsdk.model.UserDetailEntity;
import com.vfinworks.vfsdk.http.HttpUtils;
import com.vfinworks.vfsdk.http.RequestParams;
import com.vfinworks.vfsdk.http.VFinResponseHandler;
import com.vfinworks.vfsdk.model.LimitModel;
import com.vfinworks.vfsdk.view.paypwd.PayPwdDialog;
import com.vfinworks.vfsdk.view.paypwd.PayPwdView;
import com.vfinworks.vfsdk.view.paypwd.VFEncryptData;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.List;


public class TransferToCardInputInfoActivity extends BaseActivity implements OnClickListener{
	private BaseActivity mContext=this;
	private EditText etMoney;
	private Button btnConfirm;
	private EditText etRemark;

	private TransferContext transferContext;
	private TransferToCardContext transferToCardContext;
	private List<LimitModel> list;
	private boolean turnOutFlag;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.transfer_tocard_input_info_activity,FLAG_TITLE_LINEARLAYOUT);
        transferContext = (TransferContext) this.getIntent().getExtras().getSerializable("context");
		transferToCardContext = (TransferToCardContext) getIntent().getSerializableExtra("TransferToCardContext");
        super.onCreate(savedInstanceState);
        this.getTitlebarView().setTitle("转账到卡");
        this.getTitlebarView().initLeft(new OnClickListener() {
			@Override
			public void onClick(View v) {
				backOnClick();
			}
		});
        
    }

	@Override
	public void initWidget() {
		etMoney = (EditText)findViewById(R.id.et_money);
		btnConfirm = (Button)findViewById(R.id.btn_comfirm);
		btnConfirm.setOnClickListener(new NoDoubleClickListener() {
			@Override
			protected void onNoDoubleClick(View view) {
				finishBtnClick();
			}
		});
		TextView tv_bank_name = (TextView)findViewById(R.id.tv_bank_name);
		tv_bank_name.setText(transferToCardContext.getBank_name());
		TextView tv_card_no = (TextView)findViewById(R.id.tv_card_no);
		String no=transferToCardContext.getBank_account_no();
		StringBuffer sb=new StringBuffer();
		for(int i=0;i<no.length();i++){
			if(i<5 || i>=no.length()-4){
				sb.append(no.charAt(i));
			}else {
				sb.append('*');
			}
		}
		tv_card_no.setText(sb.toString());
		TextView tv_user_name = (TextView)findViewById(R.id.tv_user_name);
		tv_user_name.setText(transferToCardContext.getAccount_name());
		ImageView iv_icon= (ImageView) findViewById(R.id.iv_icon);
		iv_icon.setImageResource(Utils.getInstance().getBankDrawableIcon(transferToCardContext
				.getBank_code()));
		etRemark = (EditText)findViewById(R.id.et_remark);
		initEdit();
		queryMember();
		netWorkGetLimit();
	}

	private void queryMember() {
		RequestParams reqParam = new RequestParams();
		reqParam.putData("service", "query_member");
		reqParam.putData("token", SDKManager.token);
		HttpUtils.getInstance(mContext).excuteHttpRequest(HttpRequsetUri.getInstance()
						.getMemberDoUri(),
				reqParam, new VFinResponseHandler<UserDetailEntity>(UserDetailEntity.class) {
					@Override
					public void onSuccess(UserDetailEntity responseBean, String responseString) {
						hideProgress();
						turnOutFlag = responseBean.isTurnOut();
					}

					@Override
					public void onError(String statusCode, String errorMsg) {
						hideProgress();
						mContext.showShortToast(errorMsg);
					}
				}, this);

	}

	private void netWorkGetLimit() {
		LimitContext limitContext = new LimitContext(transferContext);
		GetLimit getLimit = new GetLimit(mContext, limitContext);
		showProgress();
		getLimit.doLimit(new GetLimit.LimitResponseHandler() {
			@Override
			public void onSuccess(List<LimitModel> results) {
				String text = "";
				if (results != null) {
					list = results;
					StringBuffer sb = new StringBuffer();
					for (LimitModel bean : results) {
						if (bean.getRangType().equals(LimitModel.SINGLE)) {
							sb.append("每笔限额" + bean.getTotalLimitedValue() + "元，");
						} else if (bean.getLimitedType().equals(LimitModel.TIMES) && bean
								.getTimeRangeType().equals(LimitModel.DAY)) {
							//							sb.append( "本日还可转出" + bean.getLimitedValue
							// () + "次，");
						} else if (bean.getLimitedType().equals(LimitModel.QUOTA)) {
							if (bean.getTimeRangeType().equals(LimitModel.DAY)) {
								//								sb.append( "本日还可转出" + bean
								// .getLimitedValue() + "元，");
							}
						}
					}
					text = sb.replace(sb.length() - 1, sb.length(), "。").toString();
				}
				etMoney.setHint(text);
				hideProgress();
			}

			@Override
			public void onError(String statusCode, String errorMsg) {
				showShortToast(errorMsg);
				hideProgress();
			}
		});
	}
	
	private void initEdit() {
		etMoney.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				btnConfirm.setEnabled(etMoney.getText().length() > 0);
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

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
										  int arg2, int arg3) {}
		}); 
	}
	
	@Override
	public void onClick(View arg0) {
	}

	private void finishBtnClick() {
		if(checkParams()) {
			final PayPwdDialog mDialog = new PayPwdDialog(this,transferContext);
			mDialog.setPayMoney(etMoney.getText().toString());
			mDialog.setOnStatusChangeListener(new PayPwdView.OnStatusChangeListener() {
				@Override
				public void onInputComplete(VFEncryptData result) {
					checkPwd(transferContext.getToken(), result.getCiphertext());
					mDialog.dismiss();
				}

				@Override
				public void onUserCanel() {
					mDialog.dismiss();
				}

			});
			mDialog.show();
		}
	}

	private void transfer2Card() {
		showProgress();
		RequestParams reqParam = new RequestParams();
		reqParam.putData("service", "payment_to_card");
		reqParam.putData("outer_trade_no", transferContext.getOutTradeNumber());
		reqParam.putData("amount", etMoney.getText().toString().trim());
		reqParam.putData("token", SDKManager.token);
		reqParam.putData("bank_code", transferToCardContext.getBank_code());
		reqParam.putData("bank_name", transferToCardContext.getBank_name());
		reqParam.putData("province", transferToCardContext.getProvince());
		reqParam.putData("city", transferToCardContext.getCity());
		reqParam.putData("bank_branch", transferToCardContext.getBank_branch());
		reqParam.putData("account_name",transferToCardContext.getAccount_name());
//		reqParam.putData("pay_pwd", SHA256Encrypt.bin2hex(ciphertext));
		reqParam.putData("bank_account_no",transferToCardContext.getBank_account_no());
		reqParam.putData("card_type", "DEBIT");//储蓄卡
		reqParam.putData("card_attribute", transferToCardContext.getCard_attribute());//待确定
		reqParam.putData("notify_url", transferContext.getNotifyUrl());
		HttpUtils.getInstance(this).excuteHttpRequest(HttpRequsetUri.getInstance().getAcquirerDoUri(),
				reqParam, new VFinResponseHandler() {

					@Override
					public void onSuccess(Object responseBean, String responseString) {
						hideProgress();
						Intent intent = new Intent();
						Bundle bundle = new Bundle();
						bundle.putSerializable("context", transferContext);
						intent.putExtras(bundle);
						intent.putExtra("statue",TradeResultActivity.SUCCESS);
						intent.setClass(mContext, TradeResultActivity.class);
						startActivity(intent);
					}

					@Override
					public void onError(String statusCode, String errorMsg) {
						hideProgress();
						showShortToast(errorMsg);
					}

				}, this);
	}

	private boolean checkParams() {
		if (transferToCardContext == null) {
			showShortToast("数据传输错误");
			return false;
		}
		if(!turnOutFlag){
			showShortToast("该账户已经冻结");
			return false;
		}
		if (TextUtils.isEmpty(etMoney.getText().toString())) {
			this.showShortToast("请输入转账金额！");
			return false;
		}
		String strMoney = TextUtils.isEmpty(etMoney.getText().toString()) ? "0.00" : etMoney.getText().toString();
		BigDecimal bdMoney = new BigDecimal(strMoney);
		etMoney.setText(bdMoney + "");
		etMoney.setSelection((bdMoney + "").length());
		if (bdMoney.compareTo(new BigDecimal("0.00")) == 0) {
			this.showShortToast("您输入的转账金额不对！");
			return false;
		} else if (list != null) {
			for (LimitModel bean : list) {
				String msg = bean.checkLimit(bdMoney.doubleValue());
				if (msg != null) {
					showShortToast(msg);
					return false;
				}
			}
		}
		return true;
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

	private void checkPwd(String token,String payPwd) {
		mContext.showProgress();
		RequestParams reqParam = new RequestParams();
		reqParam.putData("service", "verify_paypwd");
		reqParam.putData("token", SDKManager.token);
		reqParam.putData("out_trade_no", transferContext.getOutTradeNumber());
		reqParam.putData("pay_pwd", SHA256Encrypt.bin2hex(payPwd));

		HttpUtils.getInstance(mContext).excuteHttpRequest(HttpRequsetUri.getInstance().getMemberDoUri(),
				reqParam, new VFinResponseHandler() {

					@Override
					public void onSuccess(Object responseBean, String responseString) {
						mContext.hideProgress();
						JSONObject json;
						try {
							json = new JSONObject(responseString);
							String isSuccess = json.getString("is_success");
							//T代表验证成功
							if (isSuccess.equalsIgnoreCase("T")) {
								transfer2Card();
							}
						} catch (JSONException e) {
							e.printStackTrace();
							mContext.showShortToast(e.getMessage());
						}
					}

					@Override
					public void onError(String statusCode, String errorMsg) {
						mContext.hideProgress();
						mContext.showShortToast(errorMsg);
					}
				}, this);
	}

}
