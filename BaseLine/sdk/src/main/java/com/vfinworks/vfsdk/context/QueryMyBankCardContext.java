package com.vfinworks.vfsdk.context;

import com.vfinworks.vfsdk.enumtype.VFQueryBankTypeEnum;

public class QueryMyBankCardContext extends BaseContext {
    private static final long serialVersionUID = 6423866860287065326L;

    private VFQueryBankTypeEnum queryType;
    private String bankcardId;
    private String bankName;
    private String cardNo;
    private String cardDes;
    private boolean qpayFlag;
    private int bankIconRes;

    public VFQueryBankTypeEnum getQueryType() {
        return queryType;
    }

    public void setQueryType(VFQueryBankTypeEnum queryType) {
        this.queryType = queryType;
    }

    public String getBankcardId() {
        return bankcardId;
    }

    public void setBankcardId(String bankcardId) {
        this.bankcardId = bankcardId;
    }

    public int getBankIconRes() {
        return bankIconRes;
    }

    public void setBankIconRes(int bankIconRes) {
        this.bankIconRes = bankIconRes;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getCardDes() {
        return cardDes;
    }

    public void setCardDes(String cardDes) {
        this.cardDes = cardDes;
    }

    public boolean isQpayFlag() {
        return qpayFlag;
    }

    public void setQpayFlag(boolean qpayFlag) {
        this.qpayFlag = qpayFlag;
    }
}
