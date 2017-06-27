package com.vfinworks.vfsdk.activity;

/**
 * 根据响应数据的条数来判断
 * Created by xiaoshengke on 2016/7/29.
 */
public class StepPage extends BasePage {
    /**
     * 响应数据中的条数
     */
    protected int responseCount;

    /**
     * 设置响应数据条数，每次网络请求都得调用该方法
     * @param responseCount
     */
    public void setResponseCount(int responseCount) {
        this.responseCount = responseCount;
    }

    @Override
    public boolean hasMoreData() {
        if(responseCount == mPageSize){
            return true;
        }else {
            return false;
        }
    }

    @Override
    public boolean hasData() {
        if(mPageIndex > 1){
            return true;
        }else{
            if(responseCount > 0){
                return true;
            }else{
                return false;
            }
        }
    }

    @Override
    public void reset() {
        super.reset();
        responseCount = 0;
    }
}
