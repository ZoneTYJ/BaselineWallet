package com.vfinworks.vfsdk.model;

/**
 * Created by tangyijian on 2016/8/17.
 */
public class PaySelectModel {
    public int mType;
    private BankCardModel mBankCardModel;
    private ChannelModel mChannelModel;

    public PaySelectModel(int type,BankCardModel BankCardModel){
        mBankCardModel=BankCardModel;
        mType=type;
    }
    public PaySelectModel(int type,ChannelModel channelModel){
        mChannelModel=channelModel;
        mType=type;
    }

    public BankCardModel getBankCardModel() {
        return mBankCardModel;
    }

    public void setBankCardModel(BankCardModel bankCardModel) {
        mBankCardModel = bankCardModel;
    }

    public ChannelModel getChannelModel() {
        return mChannelModel;
    }

    public void setChannelModel(ChannelModel channelModel) {
        mChannelModel = channelModel;
    }
}
