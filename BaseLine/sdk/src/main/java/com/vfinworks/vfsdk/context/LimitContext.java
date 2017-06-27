package com.vfinworks.vfsdk.context;

/**
 * Created by tangyijian on 2016/9/28.
 */
public class LimitContext extends BaseContext {

    private String transfer_Type;
    private String payChannel;//01 转账14  提现

    public LimitContext() {}

    ;

    public LimitContext(BaseContext context) {
        super(context);
        if (context instanceof RechargeContext) {
            payChannel = "01";
            transfer_Type = "normal";
        } else if (context instanceof TransferContext) {
            TransferContext transferContext = (TransferContext) context;
            payChannel = "01";
            if (transferContext.getMethod().equals("card")) {
                transfer_Type = "paymenttocard";
            } else {
                transfer_Type = "normal";
            }
        } else if (context instanceof WithdrawContext) {
            payChannel = "14";
            transfer_Type = "withdraw";
        }
    }

    public String getTransfer_Type() {
        return transfer_Type;
    }

    public void setTransfer_Type(String transfer_Type) {
        this.transfer_Type = transfer_Type;
    }

    public String getPayChannel() {
        return payChannel;
    }

    public void setPayChannel(String payChannel) {
        this.payChannel = payChannel;
    }
}
