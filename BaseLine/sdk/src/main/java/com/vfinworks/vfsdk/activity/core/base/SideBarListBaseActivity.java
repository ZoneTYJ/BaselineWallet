package com.vfinworks.vfsdk.activity.core.base;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import com.vfinworks.vfsdk.R;
import com.vfinworks.vfsdk.activity.BaseActivity;
import com.vfinworks.vfsdk.view.sidebarlist.PinYinBean;
import com.vfinworks.vfsdk.view.sidebarlist.PinyinComparator;
import com.vfinworks.vfsdk.view.sidebarlist.PinyinUtils;
import com.vfinworks.vfsdk.view.sidebarlist.SideBar;
import com.vfinworks.vfsdk.view.sidebarlist.SortAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SideBarListBaseActivity extends BaseActivity {

	protected ListView listView;
	protected SortAdapter sortadapter;
	private List<? extends PinYinBean> data;
	private SideBar sidebar;
	private TextView dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact_main,FLAG_TITLE_LINEARLAYOUT);
		init();
	}

	
	private List<? extends PinYinBean> getData(List<? extends PinYinBean> data) {
		for (int i = 0; i < data.size(); i++) {
			PinYinBean person =data.get(i);
			String pinyin = PinyinUtils.getPingYin(person.getName());
			String Fpinyin = pinyin.substring(0, 1).toUpperCase();
			person.setPinYin(pinyin);
			// 正则表达式，判断首字母是否是英文字母
			if (Fpinyin.matches("[A-Z]")) {
				person.setFirstPinYin(Fpinyin);
			} else {
				person.setFirstPinYin("#");
			}
		}
		return data;

	}

	private void init() {
		sidebar = (SideBar) findViewById(R.id.sidebar);
		listView = (ListView) findViewById(R.id.listview);
		dialog = (TextView) findViewById(R.id.dialog);
		sidebar.setTextView(dialog);
		// 设置字母导航触摸监听
		sidebar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

			@Override
			public void onTouchingLetterChanged(String s) {
				// 该字母首次出现的位置
				int position = sortadapter.getPositionForSelection(s.charAt(0));
				if (position != -1) {
					listView.setSelection(position);
				}
			}
		});
		data=getData(new ArrayList<PinYinBean>());
		// 数据在放在adapter之前需要排序
		Collections.sort(data, new PinyinComparator());
		sortadapter = new SortAdapter(this, data);
		listView.setAdapter(sortadapter);
	}

	protected void setDatas(List<? extends PinYinBean> list){
		data=getData(list);
		Collections.sort(data, new PinyinComparator());
		sortadapter.setData(data);
		sidebar.clearDraw();
	}

}
