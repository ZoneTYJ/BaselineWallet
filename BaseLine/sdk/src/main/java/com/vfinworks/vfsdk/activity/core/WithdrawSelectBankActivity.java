package com.vfinworks.vfsdk.activity.core;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vfinworks.vfsdk.R;
import com.vfinworks.vfsdk.SDKManager;
import com.vfinworks.vfsdk.activity.BaseActivity;
import com.vfinworks.vfsdk.common.HttpRequsetUri;
import com.vfinworks.vfsdk.common.SharedPreferenceUtil;
import com.vfinworks.vfsdk.common.Utils;
import com.vfinworks.vfsdk.context.AddWithdrawCardContext;
import com.vfinworks.vfsdk.context.WithdrawContext;
import com.vfinworks.vfsdk.enumtype.QueryBankListTypeEnum;
import com.vfinworks.vfsdk.enumtype.VFCallBackEnum;
import com.vfinworks.vfsdk.http.HttpUtils;
import com.vfinworks.vfsdk.http.RequestParams;
import com.vfinworks.vfsdk.http.VFinResponseHandler;
import com.vfinworks.vfsdk.model.BankCardListModel;
import com.vfinworks.vfsdk.model.BankCardModel;
import com.vfinworks.vfsdk.model.VFSDKResultModel;
import com.vfinworks.vfsdk.view.MaxHeightListView;

import java.util.ArrayList;
import java.util.List;

/**
 * 选择提现银行卡
 */
public class WithdrawSelectBankActivity extends BaseActivity implements OnClickListener{
	private String token = "";
	private List<BankCardModel> listCardData = new ArrayList<BankCardModel>();
	private BankCardInfoAdapter listAdapter;
	private MaxHeightListView listView;
	private Handler olderHandler = null;
	private WithdrawContext withdrawContext;
	private int addWithDrawCardMessageID = 1;
	private Button btnAdd;
	
	private String selectedBankId;

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if(msg.what == addWithDrawCardMessageID) {
				handleAddWithDrawCardCallback((VFSDKResultModel)msg.obj);
			}/*else if(msg.what == unbindBankCardMessageID) {
				handleUnbindBankCardCallback((VFSDKResultModel)msg.obj);
			}*/
		};
	};

	private void handleAddWithDrawCardCallback(VFSDKResultModel resultModel) {
		SDKManager.getInstance().setCallbackHandle(olderHandler);
		if(resultModel.getResultCode().equalsIgnoreCase(VFCallBackEnum.OK.getCode()) == true) {
			getBankCardLst();
		}
	}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.withdraw_card_select_activity,FLAG_TITLE_LINEARLAYOUT);
        withdrawContext = (WithdrawContext) this.getIntent().getExtras().getSerializable("context");
        super.onCreate(savedInstanceState);
        this.getTitlebarView().setTitle("选择提现银行卡");
        this.getTitlebarView().initLeft(new OnClickListener() {
			@Override
			public void onClick(View v) {
				backOnClick();
			}
		});
        selectedBankId = SharedPreferenceUtil.getInstance().getStringValueFromSP(withdrawContext.getMobile()+"withdraw_bank_id");
        getBankCardLst();
    }

	@Override
	public void initWidget() {
		btnAdd = (Button) this.findViewById(R.id.btn_add_bank);
		btnAdd.setOnClickListener(this);
		listView = (MaxHeightListView) this.findViewById(R.id.listview_card);
		listView.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,long id) {
				Intent intent = new Intent();
				intent.putExtra("select_bank", listAdapter.getItem(position)); 
				setResult(RESULT_OK, intent);
				WithdrawSelectBankActivity.this.finishActivity();
			}
			
		});
		listAdapter = new BankCardInfoAdapter();
		listView.setAdapter(listAdapter);
		listAdapter.notifyDataSetChanged();
		setLstViewMaxHeight();
	}

	private void setLstViewMaxHeight() {
		ViewTreeObserver vto = btnAdd.getViewTreeObserver();   
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() { 
		    @Override   
		    public void onGlobalLayout() {
		    	btnAdd.getViewTreeObserver().removeGlobalOnLayoutListener(this); 
		    	btnAdd.getHeight();
		    	btnAdd.getWidth();
		    	DisplayMetrics dm =getResources().getDisplayMetrics();  
		    	int w_screen = dm.widthPixels;  
		    	int h_screen = dm.heightPixels;  
		    	//btnAdd.getm
		    	listView.setListViewHeight(h_screen - btnAdd.getHeight() - btnAdd.getHeight()- btnAdd.getHeight());
		    }   
		});
	}
	
	@Override
	public void onClick(View arg0) {
		if(arg0.getId() == R.id.btn_add_bank) {
			btnAddWithdrawCardClick();
		}
	}
	
	private void btnAddWithdrawCardClick() {
		SDKManager.getInstance().setCallbackHandle(mHandler);

		AddWithdrawCardContext baseContext = new AddWithdrawCardContext();
		//注意这里的addWithDrawCardMessageID一定要设
		baseContext.setCallBackMessageId(addWithDrawCardMessageID);
		baseContext.setExternal(false);
    	baseContext.setToken(withdrawContext.getToken());
    	Intent intent = new Intent();
    	Bundle bundle = new Bundle();
    	bundle.putSerializable("context", baseContext);
    	intent.putExtras(bundle);
        intent.setClass(this,AddWithdrawCardActivity.class);
        this.startActivity(intent);

		//SDKManager.getInstance().VFAddWithdrawBankCard(this, baseContext, mHandler);
	}
	
	private void getBankCardLst() {
		this.showProgress();
		RequestParams reqParam = new RequestParams();
		reqParam.putData("service", "query_bank_list");
		reqParam.putData("token", SDKManager.token);
		reqParam.putData("pay_attribute", QueryBankListTypeEnum.NORMAL.getCode());
		HttpUtils.getInstance(this).excuteHttpRequest(HttpRequsetUri.getInstance().getMemberDoUri(), reqParam, new VFinResponseHandler<BankCardListModel>(BankCardListModel.class){

			@Override
			public void onSuccess(BankCardListModel responseBean, String responseString) {
				WithdrawSelectBankActivity.this.hideProgress();
				listCardData = (List<BankCardModel>) responseBean.getCardList().clone();
				listAdapter.notifyDataSetChanged();
			}

			@Override
			public void onError(String statusCode, String errorMsg) {
				WithdrawSelectBankActivity.this.hideProgress();
				WithdrawSelectBankActivity.this.showShortToast(errorMsg);
			}
    		
    	}, this);
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
				convertView = LinearLayout.inflate(WithdrawSelectBankActivity.this, R.layout.withdraw_select_bank_list_item, null);
				holder = new ViewHolder();
				holder.iv_icon= (ImageView) convertView.findViewById(R.id.iv_icon);
				holder.tvBankName = (TextView)convertView.findViewById(R.id.tv_bank_name);
				holder.tvBankAccount = (TextView)convertView.findViewById(R.id.tv_account);
				holder.btnSelect = (ImageView)convertView.findViewById(R.id.btn_select);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			BankCardModel bankCard = this.getItem(position);
			holder.tvBankName.setText(bankCard.getBankName());
			holder.tvBankAccount.setText(bankCard.getCardNo());
			holder.iv_icon.setImageResource(Utils.getInstance().getBankDrawableIcon(bankCard.getBank_code()));
			if(TextUtils.isEmpty(selectedBankId) == false && 
					selectedBankId.equalsIgnoreCase(bankCard.getBankcardId())) {
				holder.btnSelect.setVisibility(View.VISIBLE);
			}else{
				holder.btnSelect.setVisibility(View.GONE);
			}
			return convertView;
		}
		
	}
	
	class ViewHolder {
		ImageView iv_icon;
		TextView tvBankName;
		TextView tvBankAccount;
		ImageView btnSelect;
	}
}
