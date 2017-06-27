package com.vfinworks.vfsdk.model;

/**
 * Created by tangyijian on 2016/9/29.
 */
public class LimitModel {
    public final static String SINGLE = "SINGLE";
    public final static String TIMES = "TIMES";
    public final static String QUOTA = "QUOTA";
    public final static String DAY = "DAY";

    private String limitedType;//QUOTA金额 TIMES次数
    private double limitedValue;
    private String rangType;  //SINGLE单笔 ACCUMULATED累计
    private String strategySourceType;
    private double totalLimitedValue;
    private String timeRangeType;//年月日

    public String getLimitedType() {
        return limitedType;
    }

    public void setLimitedType(String limitedType) {
        this.limitedType = limitedType;
    }

    public double getLimitedValue() {
        return limitedValue;
    }

    public void setLimitedValue(double limitedValue) {
        this.limitedValue = limitedValue;
    }

    public String getRangType() {
        return rangType;
    }

    public void setRangType(String rangType) {
        this.rangType = rangType;
    }

    public String getStrategySourceType() {
        return strategySourceType;
    }

    public void setStrategySourceType(String strategySourceType) {
        this.strategySourceType = strategySourceType;
    }

    public double getTotalLimitedValue() {
        return totalLimitedValue;
    }

    public void setTotalLimitedValue(double totalLimitedValue) {
        this.totalLimitedValue = totalLimitedValue;
    }

    public String checkLimit(double inputMoney) {
        if (limitedType.equals("QUOTA")) {   //金额
            if (rangType.equals("SINGLE") && inputMoney > limitedValue) {
                return "超出单笔交易限额";
            } else if (inputMoney > limitedValue) {
                return "超出" + TimeRangeTypeEnum.getByCode(timeRangeType).getMessage() + "交易限额";
            }
        } else if (limitedType.equals("TIMES")) {//次数
            if (limitedValue<=0) {
                return "超出" + TimeRangeTypeEnum.getByCode(timeRangeType).getMessage() + "交易次数";
            }
        }
        return null;
    }

    public String getTimeRangeType() {
        return timeRangeType;
    }

    public void setTimeRangeType(String timeRangeType) {
        this.timeRangeType = timeRangeType;
    }


    private enum TimeRangeTypeEnum {
        YEAR("YEAR", "年"), MONTH("MONTH", "月"), WEEK("WEEK", "周"), DAY("DAY", "日"),;

        private final String code;
        private final String message;

        TimeRangeTypeEnum(String code, String message) {
            this.code = code;
            this.message = message;
        }

        public static TimeRangeTypeEnum getByCode(String code) {
            for (TimeRangeTypeEnum timeRangeTypeEnum : TimeRangeTypeEnum.values()) {
                if (timeRangeTypeEnum.getCode().equalsIgnoreCase(code)) {
                    return timeRangeTypeEnum;
                }
            }
            return null;
        }

        public String getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }
}
