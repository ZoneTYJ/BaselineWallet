package com.vfinworks.vfsdk.activity.core.base;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.vfinworks.vfsdk.R;
import com.vfinworks.vfsdk.SDKManager;
import com.vfinworks.vfsdk.activity.BaseActivity;
import com.vfinworks.vfsdk.activity.PeopleInfo.DescriptionActivity;
import com.vfinworks.vfsdk.activity.core.channel.ChannelMaps;
import com.vfinworks.vfsdk.activity.core.channel.QpayChannel;
import com.vfinworks.vfsdk.common.BankCardUtlis;
import com.vfinworks.vfsdk.common.Config;
import com.vfinworks.vfsdk.common.HttpRequsetUri;
import com.vfinworks.vfsdk.common.NoDoubleClickListener;
import com.vfinworks.vfsdk.common.Utils;
import com.vfinworks.vfsdk.context.BaseContext;
import com.vfinworks.vfsdk.context.PaymentContext;
import com.vfinworks.vfsdk.context.RechargeContext;
import com.vfinworks.vfsdk.context.TransferContext;
import com.vfinworks.vfsdk.enumtype.VFCardTypeEnum;
import com.vfinworks.vfsdk.http.HttpUtils;
import com.vfinworks.vfsdk.http.RequestParams;
import com.vfinworks.vfsdk.http.VFinResponseHandler;
import com.vfinworks.vfsdk.model.QpayNewBankCardModel;
import com.vfinworks.vfsdk.model.RealNameInfoModel;

/**
 * 绑定银行卡后输入信息
 */
public class AddQpayInputInfoBaseActivity extends BaseActivity implements OnClickListener{
	//身份证信息
	private EditText etPhone;
	private Button btnConfirm;
	private TextView tvBank;
	

	private static final int  SELECT_BANK_CODE = 100;
	protected String bankNo;
	protected String bankName;
	protected BaseContext basecontext;
	protected QpayNewBankCardModel mBank;
	protected int currentRechargeType;
	protected String mCertNo;
	protected String mRealname;
	protected TextView tv_agreement;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.add_qpay_bank_info_activity,FLAG_TITLE_LINEARLAYOUT);
		basecontext = (BaseContext) getIntent().getExtras().getSerializable("basecontext");
		bankNo =getIntent().getExtras().getString("bankNo");
        super.onCreate(savedInstanceState);
        this.getTitlebarView().setTitle("绑定银行卡");
        this.getTitlebarView().initLeft(new OnClickListener() {
			@Override
			public void onClick(View v) {
				backOnClick();
			}
		});
        
    }

	@Override
	public void initWidget() {
		currentRechargeType=VFCardTypeEnum.QPAY.getCode();
		etPhone = (EditText)findViewById(R.id.et_phone);
		btnConfirm = (Button)findViewById(R.id.btn_confirm);
		btnConfirm.setOnClickListener(new NoDoubleClickListener() {
			@Override
			protected void onNoDoubleClick(View view) {
				btnConfirmClick();
			}
		});
		tvBank = (TextView)findViewById(R.id.tv_bank);
		tv_agreement=(TextView)findViewById(R.id.tv_agreement);
		tv_agreement.setOnClickListener(this);
		bankName=BankCardUtlis.getNameOfBank(bankNo);
		tvBank.setText(bankName);
		netCertification();
	}

	private void netCertification() {
		RequestParams reqParam=new RequestParams();
		reqParam.putData("service", "query_certification");
		reqParam.putData("token", SDKManager.token);
		showProgress();
		HttpUtils.getInstance(this).excuteHttpRequest(HttpRequsetUri.getInstance().getMemberDoUri(),
				reqParam, new VFinResponseHandler<RealNameInfoModel>(RealNameInfoModel.class) {

					@Override
					public void onSuccess(RealNameInfoModel responseBean, String responseString) {
						hideProgress();
						mRealname = responseBean.getRealName();
						mCertNo = responseBean.getCertNo();
					}

					@Override
					public void onError(String statusCode, String errorMsg) {
						hideProgress();
						showShortToast(errorMsg);
					}

				}, this);

	}

	@Override
	public void onClick(View arg0) {
		if(arg0==tv_agreement){
			Intent intent = new Intent(this,DescriptionActivity.class);
			intent.putExtra("title","快捷支付协议");
			intent.putExtra("url", Config.getInstance().staticResourceDir+"/pact_payment.html");
			startActivity(intent);
		}
	}

	protected  void ConfirmNext(){
		QpayChannel qpayChannel= (QpayChannel)ChannelMaps.getInstance().getChannel(QpayChannel.getChannel());
		qpayChannel.setChannelPara(this, null, true, mBank, null);
		if(basecontext instanceof RechargeContext){
			qpayChannel.doRecharge((RechargeContext) basecontext);
		}else if(basecontext instanceof PaymentContext){
			qpayChannel.doPay((PaymentContext) basecontext);
		}else if(basecontext instanceof TransferContext){
			qpayChannel.doTranferToAccount((TransferContext) basecontext);
		}
	}

	@Override
	protected void onDestroy() {
//		if(ChannelMaps.getInstance().getChannel(QpayChannel.getChannel()) != null)
//			ChannelMaps.getInstance().getChannel(QpayChannel.getChannel()).clear();
		ChannelMaps.getInstance().clear();
		super.onDestroy();
	}
	
	private void btnConfirmClick() {
		if(checkParams()) {
			mBank = new QpayNewBankCardModel();
			mBank.setName(mRealname);
			mBank.setPersonId(mCertNo);
			mBank.setPhone(etPhone.getText().toString());
			mBank.setCardNo(bankNo);
			mBank.setBankcode(BankCardUtlis.getBankCodeOfName(bankName));
			mBank.setBankName(bankName);
			ConfirmNext();
		}
	}



	private boolean checkParams() {
		String strPhone = etPhone.getText().toString();
		if(TextUtils.isEmpty(strPhone) == true) {
			this.showShortToast("请输入银行预留电话");
			return false;
		}
		if(Utils.getInstance().isMobile(strPhone) == false) {
			this.showShortToast("请输入正确的电话号码");
			return false;
		}
		if(TextUtils.isEmpty(mRealname) || TextUtils.isEmpty(mCertNo)){
			showShortToast("请实名认证");
			return false;
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

}
