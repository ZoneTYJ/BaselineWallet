package com.vfinworks.vfsdk.activity.core;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.vfinworks.vfsdk.R;
import com.vfinworks.vfsdk.SDKManager;
import com.vfinworks.vfsdk.activity.BaseActivity;
import com.vfinworks.vfsdk.common.HttpRequsetUri;
import com.vfinworks.vfsdk.http.HttpUtils;
import com.vfinworks.vfsdk.http.RequestParams;
import com.vfinworks.vfsdk.http.VFinResponseHandler;
import com.vfinworks.vfsdk.model.BankModel;

import java.util.ArrayList;


public class SelectQpayBankActivity extends BaseActivity {
	private String token;
	private ArrayList<BankModel> listBankData = new ArrayList<BankModel>();
	private ListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.select_qpay_bank_activity,FLAG_TITLE_LINEARLAYOUT);
        token = this.getIntent().getExtras().getString("token");
        super.onCreate(savedInstanceState);
        this.getTitlebarView().setTitle("选择银行");
        this.getTitlebarView().initLeft(new OnClickListener() {
			@Override
			public void onClick(View v) {
				backOnClick();
			}
		});
    }

	@Override
	public void initWidget() {
		ListView listView = (ListView) this.findViewById(R.id.listview);
		listAdapter = new ListAdapter();
		listView.setAdapter(listAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent();
				intent.putExtra("bank", listAdapter.getItem(position));
				setResult(RESULT_OK, intent);
				SelectQpayBankActivity.this.finishActivity();
			}
		});
		
		getBankLst();
	}
	
	private void getBankLst() {
		RequestParams reqParam = new RequestParams();
		reqParam.putData("service", "query_bank");
		reqParam.putData("token", SDKManager.token);
		reqParam.putData("type","I");
		HttpUtils.getInstance(this).excuteHttpRequest(HttpRequsetUri.getInstance().getMemberDoUri(), reqParam, new VFinResponseHandler<BankLst>(BankLst.class) {

			@Override
			public void onSuccess(BankLst responseBean, String responseString) {
				hideProgress();
				if (responseBean.getBank_list() != null) {
					listBankData = (ArrayList<BankModel>) responseBean.getBank_list().clone();
				}
				listAdapter.notifyDataSetChanged();
			}

			@Override
			public void onError(String statusCode, String errorMsg) {
				hideProgress();
				showShortToast(errorMsg);
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
	
	class BankLst {
		private ArrayList<BankModel> bank_list = new ArrayList<BankModel>();

		public ArrayList<BankModel> getBank_list() {
			return bank_list;
		}

		public void setBank_list(ArrayList<BankModel> bank_list) {
			this.bank_list = bank_list;
		}

		
	}
	
	class ListAdapter extends BaseAdapter {		 
		
		private ListAdapter() {
			
		}
		
		@Override
		public int getCount() { 
			if(listBankData == null)
				return 0;
			return listBankData.size();
			
		}

		@Override
		public BankModel getItem(int position) { 
			return listBankData.get(position);
		}

		@Override
		public long getItemId(int position) { 
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if(convertView == null) {
				convertView = LinearLayout.inflate(SelectQpayBankActivity.this, R.layout.list_qpay_bank_item, null);
				holder = new ViewHolder();
				holder.tvName = (TextView)convertView.findViewById(R.id.tv_name);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			BankModel bankInfo = getItem(position);
			holder.tvName.setText(bankInfo.getBankName()); 
			return convertView;
		}
		
	}
	
	class ViewHolder {
		TextView tvName;
	}
}
