package com.vfinworks.vfsdk.activity;

/**
 * 固定总数
 * Created by xiaoshengke on 2016/7/28.
 */
public class SecurePage extends BasePage {
    /**
     * 总条数
     */
    protected int mTotalCount = -1;
    /**
     * 总页数
     */
    protected int mPageTotal;

    /**
     * 设置总条数,并根据总条数计算总页数
     * @param totalCount
     */
    public void setTotalCount(int totalCount) {
        if(mTotalCount < 0) {
            this.mTotalCount = totalCount;
            mPageTotal = mTotalCount % mPageSize > 0 ? mTotalCount / mPageSize + 1 : mTotalCount / mPageSize;
        }
    }

    @Override
    public boolean hasMoreData() {
        if(mPageIndex < mPageTotal){
            return true;
        }
        return false;
    }

    @Override
    public boolean hasData() {
        return mTotalCount > 0;
    }

    @Override
    public void reset() {
        super.reset();
        mTotalCount = -1;
    }
}
