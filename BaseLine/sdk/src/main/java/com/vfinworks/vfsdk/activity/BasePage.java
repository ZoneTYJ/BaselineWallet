package com.vfinworks.vfsdk.activity;

/**
 * Created by xiaoshengke on 2016/7/28.
 */
public abstract class BasePage {
    public int mPageIndex = 1;
    /**
     * 默认为20条
     */
    public int mPageSize = 20;

    /**
     * 是否有更多数据，true为有。当前页判断后再翻页
     * @return
     */
    public abstract boolean hasMoreData();

    /**
     * 是否有数据
     * @return
     *  true为有数据
     */
    public abstract boolean hasData();

    /**
     * 翻页
     */
    public void turnPage(){
        mPageIndex++;
    }

    /**
     * 重置
     */
    public void reset(){
        mPageIndex = 1;
    }



}
