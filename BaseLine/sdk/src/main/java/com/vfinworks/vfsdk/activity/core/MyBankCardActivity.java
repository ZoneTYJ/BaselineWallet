package com.vfinworks.vfsdk.activity.core;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.vfinworks.vfsdk.R;
import com.vfinworks.vfsdk.SDKManager;
import com.vfinworks.vfsdk.activity.BaseActivity;
import com.vfinworks.vfsdk.common.HttpRequsetUri;
import com.vfinworks.vfsdk.common.Utils;
import com.vfinworks.vfsdk.context.AddWithdrawCardContext;
import com.vfinworks.vfsdk.context.QueryMyBankCardContext;
import com.vfinworks.vfsdk.enumtype.VFCallBackEnum;
import com.vfinworks.vfsdk.http.HttpUtils;
import com.vfinworks.vfsdk.http.RequestParams;
import com.vfinworks.vfsdk.http.VFinResponseHandler;
import com.vfinworks.vfsdk.model.BankCardListModel;
import com.vfinworks.vfsdk.model.BankCardModel;
import com.vfinworks.vfsdk.model.VFSDKResultModel;

import java.util.ArrayList;


public class MyBankCardActivity extends BaseActivity {
	private Context mContext=this;

	private QueryMyBankCardContext mybankCardContext;
	private ArrayList<BankCardModel> listCardData = new ArrayList<BankCardModel>();
	private BankCardInfoAdapter listAdapter;
	private Handler olderHandler = null;
	
	private  static int addWithDrawCardMessageID = 1;
	public static int unbindBankCardMessageID = 2;
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if(msg.what == addWithDrawCardMessageID) {
				handleAddWithDrawCardCallback((VFSDKResultModel)msg.obj);
			}else if(msg.what == unbindBankCardMessageID) {
				handleUnbindBankCardCallback((VFSDKResultModel)msg.obj);
			}
		};
	};
	
	private void handleUnbindBankCardCallback(VFSDKResultModel resultModel) {
		this.hideProgress();
		SDKManager.getInstance().setCallbackHandle(olderHandler);
		if(resultModel.getResultCode().equalsIgnoreCase(VFCallBackEnum.OK.getCode())) {
			getBankCardLst();
			this.showShortToast("删除成功");
		}else{
			this.showShortToast(resultModel.getMessage());
		}
	}
	
	private void handleAddWithDrawCardCallback(VFSDKResultModel resultModel) {
		SDKManager.getInstance().setCallbackHandle(olderHandler);
		if(resultModel.getResultCode().equalsIgnoreCase(VFCallBackEnum.OK.getCode())) {
			getBankCardLst();
		}
	}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.my_bank_card_activity,FLAG_TITLE_LINEARLAYOUT);
        super.onCreate(savedInstanceState);
        mybankCardContext = (QueryMyBankCardContext) this.getIntent().getExtras().getSerializable("context");
        this.getTitlebarView().setTitle("我的银行卡");
        this.getTitlebarView().initLeft(new OnClickListener() {
			@Override
			public void onClick(View v) {
				backOnClick();
			}
		});
        this.getTitlebarView().initRight(R.drawable.vf_btn_add_bankcart_selector, new OnClickListener() {
			@Override
			public void onClick(View v) {
				addBankCardClick();
			}
		});
        getBankCardLst();
    }

	@Override
	public void initWidget() {
		ListView listView = (ListView) this.findViewById(R.id.listview_card);
		listAdapter = new BankCardInfoAdapter();
		listView.setAdapter(listAdapter);
		listAdapter.notifyDataSetChanged();
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				BankCardModel bean=listCardData.get(position);
				mybankCardContext.setBankcardId(bean.getBankcardId());
				mybankCardContext.setCardNo(bean.getCardNo());
				mybankCardContext.setCardDes(bean.getCardType().equalsIgnoreCase("CREDIT") ? "信用卡"
						: "储蓄卡");
				mybankCardContext.setBankName(bean.getBankName());
				mybankCardContext.setQpayFlag(bean.getPay_attribute().equals("QPAY"));
				mybankCardContext.setBankIconRes(Utils.getInstance().getBankDrawableIcon(bean.getBank_code()));
				Intent intent=new Intent(mContext,MyBankCardDetailActivity.class);
				intent.putExtra("context",mybankCardContext);
				startActivity(intent);
				olderHandler = SDKManager.getInstance().getCallbackHandler();
				SDKManager.getInstance().setCallbackHandle(mHandler);
			}
		});
	}
	
	private void getBankCardLst() {
		this.showProgress();
		RequestParams reqParam = new RequestParams();
		reqParam.putData("service", "query_bank_list");
		reqParam.putData("token", SDKManager.token);
		reqParam.putData("pay_attribute", mybankCardContext.getQueryType().getCode());
		HttpUtils.getInstance(this).excuteHttpRequest(HttpRequsetUri.getInstance().getMemberDoUri(), reqParam, new VFinResponseHandler<BankCardListModel>(BankCardListModel.class){

			@Override
			public void onSuccess(BankCardListModel responseBean, String responseString) {
				MyBankCardActivity.this.hideProgress();
				listCardData = (ArrayList<BankCardModel>) responseBean.getCardList().clone();
				listAdapter.notifyDataSetChanged();
			}

			@Override
			public void onError(String statusCode, String errorMsg) {
				MyBankCardActivity.this.hideProgress();
				MyBankCardActivity.this.showShortToast(errorMsg);
			}
    		
    	}, this);
	}
	
	private void backOnClick() {
		this.finishAll();
	}
	
	private void addBankCardClick() {
		AddWithdrawCardContext baseContext = new AddWithdrawCardContext();
		baseContext.setCallBackMessageId(addWithDrawCardMessageID);
		baseContext.setExternal(false);
    	baseContext.setToken(mybankCardContext.getToken());
    	olderHandler = SDKManager.getInstance().getCallbackHandler();
    	SDKManager.getInstance().AddWithdrawBankCard(this, baseContext, mHandler);
    	/*Intent intent = new Intent();
    	Bundle bundle = new Bundle();
    	bundle.putSerializable("context", baseContext);
    	intent.putExtras(bundle);
        intent.setClass(this,AddWithdrawCardActivity.class);
        this.startActivity(intent);*/
	}

	@Override
	protected void onDestroy() {
		SDKManager.getInstance().clear();
		super.onDestroy();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) { 
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			backOnClick();
		}  
		return super.onKeyDown(keyCode, event);
	}
	
	class BankCardInfoAdapter extends BaseAdapter {
		
		private BankCardInfoAdapter() {
			
		}
		
		@Override
		public int getCount() { 
			return listCardData.size();
			
		}

		@Override
		public BankCardModel getItem(int position) { 
			return listCardData.get(position);
		}

		@Override
		public long getItemId(int position) { 
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if(convertView == null) {
				convertView = LinearLayout.inflate(MyBankCardActivity.this, R.layout.list_item_my_bank_card, null);
				holder = new ViewHolder();
				holder.tvBankName = (TextView)convertView.findViewById(R.id.tv_bank_name);
				holder.tvCardDes = (TextView)convertView.findViewById(R.id.tv_bank_des);
				holder.tvUnbind = (TextView)convertView.findViewById(R.id.tv_bank_unbind);
				holder.tvCardNo = (TextView)convertView.findViewById(R.id.tv_bank_num);
				holder.IvQpayFlag = (ImageView)convertView.findViewById(R.id.iv_qpay_flag);
				holder.ivBankIcon = (ImageView)convertView.findViewById(R.id.iv_bank_icon);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			final BankCardModel bank = this.getItem(position);
			holder.tvBankName.setText(bank.getBankName());
			holder.tvCardNo.setText("**** **** **** "+bank.getCardNo().substring(bank.getCardNo().length() - 4));
			String strCardType = "储蓄卡";
			if(bank.getCardType().equalsIgnoreCase("CREDIT") == true) {
				strCardType = "信用卡";
			}
//			holder.tvCardDes.setText(strCardType + "|" + "提现卡");
			holder.tvCardDes.setText(strCardType);
//			holder.tvUnbind.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View arg0) {
//					unbindBankCardClick(bank.getBankcardId());
//				}
//			});
			bank.setDrawableIcon(holder.ivBankIcon);
			if(bank.getPay_attribute().equals("QPAY")){
				holder.IvQpayFlag.setVisibility(View.VISIBLE);
			}else{
				holder.IvQpayFlag.setVisibility(View.GONE);
			}
			return convertView;
		}
	}
	
	class ViewHolder {
		TextView tvBankName;
		TextView tvCardDes;
		TextView tvUnbind;
		TextView tvCardNo;
		ImageView IvQpayFlag;
		ImageView ivBankIcon;
	}
}
