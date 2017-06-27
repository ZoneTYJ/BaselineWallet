package com.vfinworks.vfsdk.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.vfinworks.vfsdk.R;
import com.vfinworks.vfsdk.common.L;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseListAdapter<T> extends BaseAdapter {
    /**
     * 处于下拉刷新时,加载更多行隐藏
     */
    public static final int STATE_PULL_REFRESH= -2;
    /**
     * 加载更多行隐藏
     */
    public static final int STATE_MORE_DISMISS = -1;
    /**
     * 空数据，代表暂无内容
     */
    public static final int STATE_EMPTY_ITEM = 0;
    /**
     * 有更多数据
     */
    public static final int STATE_LOAD_MORE = 1;
    /**
     * 无更多数据
     */
    public static final int STATE_NO_MORE = 2;
    /**
     * 无网络
     */
    public static final int STATE_NO_NETWORK = 3;
    /**
     * 后台响应错误
     */
    public static final int STATE_RESPONSE_ERROR = 5;

    protected int state = STATE_MORE_DISMISS;

    protected int _loadmoreText;
    protected int _loadFinishText;
    protected int _noDateText;
    protected int _noNetworkText;
    protected int mScreenWidth;

    private LayoutInflater mInflater;

    public BasePage mPage;

    protected LayoutInflater getLayoutInflater(Context context) {
        if (mInflater == null) {
            mInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        return mInflater;
    }

    public void setScreenWidth(int width) {
        mScreenWidth = width;
    }

    public void setState(int state) {
        this.state = state;
    }

    /**
     * 是否下拉刷新
     * @return
     */
    public boolean isPullRefresh(){
        return state == STATE_PULL_REFRESH;
    }

    public int getState() {
        return this.state;
    }

    protected ArrayList<T> mDatas = new ArrayList<T>();

    public BaseListAdapter() {
        _loadmoreText = R.string.loading;
        _loadFinishText = R.string.loading_no_more;
        _noDateText = R.string.error_view_no_data;
        _noNetworkText = R.string.no_network;
    }

    @Override
    public int getCount() {
        switch (getState()) {
            case STATE_EMPTY_ITEM:
                return getDataSizePlus1();
            case STATE_NO_NETWORK:
            case STATE_RESPONSE_ERROR:
            case STATE_LOAD_MORE:
                return getDataSizePlus1();
            case STATE_NO_MORE:
                return getDataSizePlus1();
            default:
                break;
        }
        return getDataSize();
    }

    public int getDataSizePlus1(){
        if(hasFooterView()){
            return getDataSize() + 1;
        }
        return getDataSize();
    }

    public int getDataSize() {
        return mDatas.size();
    }

    @Override
    public T getItem(int position) {
        if (mDatas.size() > position) {
            return mDatas.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setData(ArrayList<T> data) {
        mDatas = data;
        notifyDataSetChanged();
    }

    public ArrayList<T> getData() {
        return mDatas == null ? (mDatas = new ArrayList<T>()) : mDatas;
    }

    public void addData(List<T> data) {
        if (data == null || data.isEmpty()) {
            if(mDatas.isEmpty()){
                setState(STATE_EMPTY_ITEM);
            }else{
                setState(STATE_NO_MORE);
            }
        }else{
            mDatas.addAll(data);
            if(mPage.hasMoreData()){
                setState(STATE_LOAD_MORE);
            }else{
                setState(STATE_NO_MORE);
            }
            mPage.turnPage();
        }
        notifyDataSetChanged();
    }

//    public void addData(List<T> data) {
//        if (mDatas != null && data != null && !data.isEmpty()) {
//            mDatas.addAll(data);
//        }
//        notifyDataSetChanged();
//    }

    public void addItem(T obj) {
        if (mDatas != null) {
            mDatas.add(obj);
        }
        notifyDataSetChanged();
    }

    public void addItem(int pos, T obj) {
        if (mDatas != null) {
            mDatas.add(pos, obj);
        }
        notifyDataSetChanged();
    }

    public void removeItem(Object obj) {
        mDatas.remove(obj);
        notifyDataSetChanged();
    }

    public void clear() {
        mDatas.clear();
//        notifyDataSetChanged();
    }

    public void setLoadmoreText(int loadmoreText) {
        _loadmoreText = loadmoreText;
    }

    public void setLoadFinishText(int loadFinishText) {
        _loadFinishText = loadFinishText;
    }

    public void setNoDataText(int noDataText) {
        _noDateText = noDataText;
    }

    protected boolean loadMoreHasBg() {
        return true;
    }

    @Override
    public int getViewTypeCount() {
        if(hasFooterView())
            return 2;
        else
            return super.getViewTypeCount();
    }

    @Override
    public int getItemViewType(int position) {
        if(hasFooterView()){
            if(position < getCount() - 1)
                return 0;
            else
                return 1;
        }else
            return super.getItemViewType(position);

    }

    @SuppressWarnings("deprecation")
    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (position == getCount() - 1&&hasFooterView()) {// 最后一条
            if (getState() == STATE_LOAD_MORE || getState() == STATE_NO_MORE
                    || state == STATE_EMPTY_ITEM || state == STATE_MORE_DISMISS
                    || state == STATE_PULL_REFRESH || state == STATE_NO_NETWORK
                    || getState() == STATE_RESPONSE_ERROR) {
                this.mFooterView = (LinearLayout) LayoutInflater.from(
                        parent.getContext()).inflate(R.layout.list_cell_footer,
                        null);
                this.mFooterView.setOnClickListener(footClickListener);
                this.mFooterView.setClickable(false);
                if (!loadMoreHasBg()) {
                    mFooterView.setBackgroundDrawable(null);
                }
                ProgressBar progress = (ProgressBar) mFooterView
                        .findViewById(R.id.progressbar);
                TextView text = (TextView) mFooterView.findViewById(R.id.text);
                switch (getState()) {
                case STATE_LOAD_MORE:
                    setFooterViewLoading();
                    if(loadMoreListener != null){
                        loadMoreListener.loadMore();
                    }else{
                        L.e("loadMoreListener","null");
                    }
                    break;
                case STATE_NO_MORE:
                    mFooterView.setVisibility(View.VISIBLE);
                    progress.setVisibility(View.GONE);
                    text.setVisibility(View.VISIBLE);
                    text.setText(_loadFinishText);
                    break;
                case STATE_EMPTY_ITEM:
                    progress.setVisibility(View.GONE);
                    mFooterView.setVisibility(View.VISIBLE);
                    text.setText(_noDateText);
                    break;
                case STATE_RESPONSE_ERROR:
                    this.mFooterView.setClickable(true);
                    mFooterView.setVisibility(View.VISIBLE);
                    progress.setVisibility(View.GONE);
                    text.setVisibility(View.VISIBLE);
                    text.setText("加载出错了");
                    break;
                case STATE_NO_NETWORK:
                    this.mFooterView.setClickable(true);
                    mFooterView.setVisibility(View.VISIBLE);
                    progress.setVisibility(View.GONE);
                    text.setVisibility(View.VISIBLE);
                    text.setText(_noNetworkText);
                    break;
                case STATE_MORE_DISMISS:
                case STATE_PULL_REFRESH:
                default:
                    progress.setVisibility(View.GONE);
                    mFooterView.setVisibility(View.GONE);
                    text.setVisibility(View.GONE);
                    break;
                }
                return mFooterView;
            }
        }
        if (position < 0) {
            position = 0; // 若列表没有数据，是没有footview/headview的
        }
        return getRealView(position, convertView, parent);
    }

    protected abstract View getRealView(int position, View convertView, ViewGroup parent);


    private LinearLayout mFooterView;

    protected boolean hasFooterView(){
        return true;
    }

    public View getFooterView() {
        return this.mFooterView;
    }

    public void setFooterViewLoading(String loadMsg) {
        ProgressBar progress = (ProgressBar) mFooterView
                .findViewById(R.id.progressbar);
        TextView text = (TextView) mFooterView.findViewById(R.id.text);
        mFooterView.setVisibility(View.VISIBLE);
        progress.setVisibility(View.VISIBLE);
        text.setVisibility(View.VISIBLE);
        if (TextUtils.isEmpty(loadMsg)) {
            text.setText(_loadmoreText);
        } else {
            text.setText(loadMsg);
        }
    }

    public void setFooterViewLoading() {
        setFooterViewLoading("");
    }

    public void setFooterViewText(String msg) {
        ProgressBar progress = (ProgressBar) mFooterView
                .findViewById(R.id.progressbar);
        TextView text = (TextView) mFooterView.findViewById(R.id.text);
        mFooterView.setVisibility(View.VISIBLE);
        progress.setVisibility(View.GONE);
        text.setVisibility(View.VISIBLE);
        text.setText(msg);
    }



    protected void setText(TextView textView, String text, boolean needGone) {
        if (text == null || TextUtils.isEmpty(text)) {
            if (needGone) {
                textView.setVisibility(View.GONE);
            }
        } else {
            textView.setText(text);
        }
    }

    protected void setText(TextView textView, String text) {
        setText(textView, text, false);
    }

    private View.OnClickListener footClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(loadMoreListener != null){
                loadMoreListener.loadMore();
            }
        }
    };

    private LoadMoreListener loadMoreListener;

    public void setLoadMoreListener(LoadMoreListener loadMoreListener) {
        this.loadMoreListener = loadMoreListener;
    }

    public interface LoadMoreListener{
        void loadMore();
    }
}
