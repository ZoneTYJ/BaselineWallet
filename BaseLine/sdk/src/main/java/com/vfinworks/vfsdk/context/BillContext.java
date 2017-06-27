package com.vfinworks.vfsdk.context;

/**
 * Created by xiaoshengke on 2016/4/8.
 */
public class BillContext extends BaseContext {
    /**
     * 交易类型，传字符串：INSTANT表示即时，ENSURE表示担保，TRANSFER表示转账，REFUND表示退款，DEPOSIT表示充值，WITHDRAW表示提现
     */
    private String tradeType;
    /**
     * 开始时间，一共14位,格式为：年[4位]月[2位]日[2位]时[2位]分[2位]秒[2位]，例：20140617020101
     */
    private String startTime;
    /**
     * 结束时间，格式与开始时间一样
     */
    private String endTime;
    /**
     * 页码
     */
    private int pageNo;
    /**
     * 每页大小
     */
    private int pageSize = 20;

    public String getTradeType() {
        return tradeType;
    }

    /**
     * 设置交易类型
     * @param tradeType
     *  字符串：INSTANT表示即时到账，ENSURE表示担保，TRANSFER表示转账，REFUND表示退款，DEPOSIT表示充值，WITHDRAW表示提现
     */
    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
    }


    public String getStartTime() {
        return startTime;
    }

    /**
     * 设置开始时间
     * @param startTime
     *  一共14位,格式为：年[4位]月[2位]日[2位]时[2位]分[2位]秒[2位]，例：20140617020101
     */
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
