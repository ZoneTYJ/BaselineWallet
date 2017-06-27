package com.vfinworks.vfsdk.view.activitytitle;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * 为activity生成标题
 *
 */
public class TitleHelper {

	//以线性布局方式添加title
	public static final int FLAG_TITLE_LINEARLAYOUT = 0x0003;

	//以相对布局方式添加title
	public static final int FLAG_TITLE_RELATIVELAYOUT = 0x000c;

	//不添加title
	public static final int FLAG_NO_TITLE = 0x0;

	//生成的titlebar view的id
	public static final int DEFAULT_TITLE_VIEW_ID = Integer.MAX_VALUE;
	public static final int DEFAULT_CONTENT_VIEW_ID = Integer.MAX_VALUE -1;

	private static final String TAG = "TitleHelper";

	private Context mContext;

	public TitleHelper(Context context) {
		if (context == null)
			throw new IllegalArgumentException("context can not be null");
		this.mContext = context;
	}

	/**
	 * 通过资源文件产生一个带有标题的view，标题View的类型为TitlebarView
	 * @param mContext
	 * @param child
	 * @return
	 */
	public View addTitleBar(int layoutID,View titleView,int flag) {
		return addTitleBar(View.inflate(mContext, layoutID, null),titleView,flag);
	}

	/**
	 * 添加指定类型的标题栏view,id为DEFAULT_TITLE_VIEW_ID
	 * @param child
	 * @param flags 添加状态机
	 * @return
	 */
	public View addTitleBar(View child,View titleView,int flags) {
		long start = System.currentTimeMillis();
		ViewGroup root = null;
		setContentViewId(child);
		if ((flags & FLAG_TITLE_LINEARLAYOUT) != 0) {
			if (child instanceof LinearLayout && ((LinearLayout)child).getOrientation() == LinearLayout.VERTICAL) {
				root = (ViewGroup) child;
			} else {
				root = new LinearLayout(mContext);
				((LinearLayout)root).setOrientation(LinearLayout.VERTICAL);
			}
		} else if ((flags & FLAG_TITLE_RELATIVELAYOUT) != 0) {
			if (child instanceof RelativeLayout) {
				root = (ViewGroup) child;
			} else {
				root = new RelativeLayout(mContext);
			}
			root = new RelativeLayout(mContext);
		} else {
			return child;
		}

		//View titleBar = generateTitleBar();
		if (titleView == null)
			throw new RuntimeException("titleView is null !!!");
		titleView.setId(DEFAULT_TITLE_VIEW_ID);
		
		if (root != child && child.getBackground() != null) {
			root.setBackgroundDrawable(child.getBackground());
			child.setBackgroundDrawable(null);
		}
		if ((flags & FLAG_TITLE_LINEARLAYOUT) != 0) {
			if (root != child) {
				root.addView(child,new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			}
			root.addView(titleView,0,new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		} else if ((flags & FLAG_TITLE_RELATIVELAYOUT) != 0) {
			if (root != child) {
				root.addView(child,new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			}
			root.addView(titleView,new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		}
		long dtime = System.currentTimeMillis() - start;
		Log.d(TAG, "Builder titlebar success, use " + dtime);
		return root;
	}

	private int setContentViewId(View view) {
		if (view.getId() == View.NO_ID) {
			view.setId(DEFAULT_CONTENT_VIEW_ID);
		}
		return view.getId();
	}
}
