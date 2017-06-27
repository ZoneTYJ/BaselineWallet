package com.vfinworks.vfsdk.activity.core;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.vfinworks.vfsdk.R;

/**
 * @Description:gridview的Adapter
 * @author http://blog.csdn.net/finddreams
 */
public class MyGridAdapter extends BaseAdapter {
	private Context mContext;

	/*public String[] img_text = { "转账", "余额宝", "手机充值", "信用卡还款", "淘宝电影", "彩票",
			"当面付", "亲密付", "机票", };
	public int[] imgs = { R.drawable.app_transfer, R.drawable.app_fund,
			R.drawable.app_phonecharge, R.drawable.app_creditcard,
			R.drawable.app_movie, R.drawable.app_lottery,
			R.drawable.app_facepay, R.drawable.app_close, R.drawable.app_plane };*/

	public String[] img_text = { "充值", "提现", "转账", "付款", "收款","付款码","扫码收"};
	public int[] imgs = { R.drawable.vf_recharge_icon, R.drawable.vf_withdraw_icon,
			R.drawable.vf_transfer_icon, R.drawable.vf_pay_icon,
			R.drawable.vf_receive_icon, R.drawable.vf_pay_icon,
			R.drawable.vf_receive_icon};


	public MyGridAdapter(Context mContext) {
		super();
		this.mContext = mContext;
	}

	@Override
	public int getCount() {
		return img_text.length;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.grid_item, parent, false);
		}
		TextView tv = BaseViewHolder.get(convertView, R.id.tv_item);
		ImageView iv = BaseViewHolder.get(convertView, R.id.iv_item);
		//iv.setBackgroundResource(imgs[position]);
		iv.setImageResource(imgs[position]);

		tv.setText(img_text[position]);
		return convertView;
	}


	/*public void onItemClick(int position)
	{
		switch (position) {
			case 0:
				break;
			case 1:
				break;
			default:

				break;
		}
	}*/
}
