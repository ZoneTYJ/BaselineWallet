package com.vfinworks.vfsdk.activity.core.base;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.vfinworks.vfsdk.R;
import com.vfinworks.vfsdk.activity.BaseActivity;

import java.util.ArrayList;
import java.util.List;

public abstract class SearchListBaseActivity extends BaseActivity {
	protected BaseActivity mContext;
	protected ListView mListView;
	private EditText et_search;

	private List<String> branchs;
	private List<String> originList;
	private BaseAdapter mAdapter;
	private String filterText;
	protected LinearLayout layout_search_text;
	private TextView tv_cancel_search;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search,FLAG_TITLE_LINEARLAYOUT);
		init();
	}

	
	private void init() {
		filterText="";
		mContext=this;
		branchs=new ArrayList<>();
		tv_cancel_search= (TextView) findViewById(R.id.tv_cancel_search);
		tv_cancel_search.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				et_search.setText("");
			}
		});
		mListView = (ListView) findViewById(R.id.listview);
		layout_search_text = (LinearLayout) findViewById(R.id.layout_search_text);
		et_search = (EditText) findViewById(R.id.et_search);
		et_search.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				filterText = s.toString().trim();
				setfilterList();
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		mAdapter = new SearchAdapter();
		mListView.setAdapter(mAdapter);

		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				doItemClick(branchs.get(position));
			}
		});
	}

	abstract protected void doItemClick(String name);

	class SearchAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return branchs.size();
		}

		@Override
		public Object getItem(int position) {
			return branchs.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Holder holder=null;
			if(convertView==null){
				convertView=View.inflate(mContext,R.layout.list_bank_item,null);
				holder=new Holder();
				holder.tv_name= (TextView) convertView.findViewById(R.id.tv_name);
				convertView.setTag(holder);
			}else {
				holder= (Holder) convertView.getTag();
			}
			holder.tv_name.setText(branchs.get(position));
			return convertView;
		}
	}

	class Holder{
		TextView tv_name;
	}


	protected void setDatas(ArrayList<String> list){
		originList=list;
		setfilterList();
	}

	private void setfilterList() {
		if(originList==null){
			return;
		}
		branchs.clear();
		for(String bean:originList){
			if(bean.contains(filterText)){
				branchs.add(bean);
			}
		}
		mAdapter.notifyDataSetChanged();
	}

}
