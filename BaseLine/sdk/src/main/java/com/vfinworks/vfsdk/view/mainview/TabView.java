package com.vfinworks.vfsdk.view.mainview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.vfinworks.vfsdk.R;
//import com.netfinworks.wallet.R;

/**
 * 底部导航栏按钮
 * @author tong
 *
 */
public class TabView extends LinearLayout implements OnClickListener {

	/**我的钱包*/
	private LinearLayout lay_MyWallet;
	private ImageView img_MyWallet;
	private TextView tv_MyWallet;
	/**交易明细*/
	private LinearLayout lay_TransactionDetails;
	private ImageView img_TransactionDetails;
	private TextView tv_TransactionDetails;
	/**金融超市*/
	private LinearLayout lay_FinanceSupermarket;
	private ImageView img_FinanceSupermarket;
	private TextView tv_FinanceSupermarket;
	/**生活服务*/
	private LinearLayout lay_LifeService;
	private ImageView img_LifeService;
	private TextView tv_LifeService;
	/**更多*/
	private LinearLayout lay_More;
	private ImageView img_More;
	private TextView tv_More;

	private OnCheckedChangeListener mChangeListener;
	private int mCurrentIndex;

	public TabView(Context context, AttributeSet attrs) {
		super(context, attrs);	
		
		View.inflate(context, R.layout.view_tabbar_view, this);
		findViewById(R.id.layout_tab_my_wallet).setOnClickListener(this);
		//findViewById(R.id.layout_tab_transaction_details).setOnClickListener(this);
		//findViewById(R.id.layout_tab_finance_supermarket).setOnClickListener(this);
		//findViewById(R.id.layout_tab_life_service).setOnClickListener(this);
		findViewById(R.id.layout_tab_more).setOnClickListener(this);

		lay_MyWallet = (LinearLayout) findViewById(R.id.layout_tab_my_wallet);
		/*lay_TransactionDetails = (LinearLayout) findViewById(R.id.layout_tab_transaction_details);
		lay_FinanceSupermarket = (LinearLayout) findViewById(R.id.layout_tab_finance_supermarket);
		lay_LifeService = (LinearLayout) findViewById(R.id.layout_tab_life_service);*/
		lay_More = (LinearLayout) findViewById(R.id.layout_tab_more);
		
		img_MyWallet = (ImageView) findViewById(R.id.iv_my_wallet);
		/*img_TransactionDetails = (ImageView) findViewById(R.id.iv_transaction_details);
		img_FinanceSupermarket = (ImageView) findViewById(R.id.iv_finance_supermarket);
		img_LifeService = (ImageView) findViewById(R.id.iv_life_service);*/
		img_More = (ImageView) findViewById(R.id.iv_more);

		tv_MyWallet = (TextView) findViewById(R.id.tv_my_wallet);
		/*tv_TransactionDetails = (TextView) findViewById(R.id.tv_transaction_details);
		tv_FinanceSupermarket = (TextView) findViewById(R.id.tv_finance_supermarket);
		tv_LifeService = (TextView) findViewById(R.id.tv_life_service);*/
		tv_More = (TextView) findViewById(R.id.tv_more);
	}

	public void setCheckedItem(int index) {
		mCurrentIndex = index;
		
		lay_MyWallet.setBackgroundColor(index == 0 ? getResources().getColor(R.color.tab_layout_pressed_bg_color) : 0);
		/*lay_TransactionDetails.setBackgroundColor(index == 1 ? getResources().getColor(R.color.tab_layout_pressed_bg_color) : 0);
		lay_FinanceSupermarket.setBackgroundColor(index == 2 ? getResources().getColor(R.color.tab_layout_pressed_bg_color) : 0);
		lay_LifeService.setBackgroundColor(index == 3 ? getResources().getColor(R.color.tab_layout_pressed_bg_color) : 0);
		*/
		lay_More.setBackgroundColor(index == 1 ? getResources().getColor(R.color.tab_layout_pressed_bg_color) : 0);
		
		img_MyWallet.setSelected(index == 0 ? true : false);




		/*img_TransactionDetails.setSelected(index == 1 ? true : false);
		img_FinanceSupermarket.setSelected(index == 2 ? true : false);
		img_LifeService.setSelected(index == 3 ? true : false);
		*/
		img_More.setSelected(index == 1 ? true : false);

		//tv_MyWallet.setSelected(index == 0 ? true : false);
		/*tv_TransactionDetails.setSelected(index == 1 ? true : false);
		tv_FinanceSupermarket.setSelected(index == 2 ? true : false);
		tv_LifeService.setSelected(index == 3 ? true : false);
		*/
		//tv_More.setSelected(index == 1 ? true : false);

		if(index == 0) {
			img_MyWallet.setImageResource(R.drawable.vf_tab_home_checked);
			img_More.setImageResource(R.drawable.vf_tab_my);
		}
		else
		{
			img_MyWallet.setImageResource(R.drawable.vf_tab_home);
			img_More.setImageResource(R.drawable.vf_tab_my_checked);
		}

	}

	public void setOnCheckedChangeListener(OnCheckedChangeListener changeListener) {
		this.mChangeListener = changeListener;
	}

	public interface OnCheckedChangeListener {
		void onCheckedChanged(int index);
	}

	@Override
	public void onClick(View v) {
		int index = v.getId() == R.id.layout_tab_my_wallet   ? 0 :
			/*v.getId() == R.id.layout_tab_transaction_details   ? 1 :
				v.getId() == R.id.layout_tab_finance_supermarket ? 2 : */
					v.getId() == R.id.layout_tab_more ? 1 : 2;
		if (index == mCurrentIndex)
			return;
		setCheckedItem(index);
		if (mChangeListener != null) {
			mChangeListener.onCheckedChanged(index);
		}
	}
}
