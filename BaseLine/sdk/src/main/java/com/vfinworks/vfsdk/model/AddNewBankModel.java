package com.vfinworks.vfsdk.model;

import java.io.Serializable;

/**
 * Created by tangyijian on 2016/8/29.
 */
public class AddNewBankModel implements Serializable{
    public final static int PROVINCE_TYPE=101;
    public final static int CITY_TYPE=102;
    public final static int BANK_TYPE=103;
    public final static int BRANCH_TYPE=104;

    /*省份*/
    private ProvinceInfoModel mProvinceInfoModel;
    /*城市 */
    private CityInfoModel mCityInfoModel;
    /*银行*/
    private BankModel mBankModel;
    /* 分行 */
    private BranchInfoModel mBranchInfoModel;
    /* 当前执行的类型 */
    private int type;
    /* 用户token */
    private String token;

    public ProvinceInfoModel getProvinceInfoModel() {
        return mProvinceInfoModel;
    }

    public void setProvinceInfoModel(ProvinceInfoModel provinceInfoModel) {
        mProvinceInfoModel = provinceInfoModel;
    }

    public CityInfoModel getCityInfoModel() {
        return mCityInfoModel;
    }

    public void setCityInfoModel(CityInfoModel cityInfoModel) {
        mCityInfoModel = cityInfoModel;
    }

    public BankModel getBankModel() {
        return mBankModel;
    }

    public void setBankModel(BankModel bankModel) {
        mBankModel = bankModel;
    }

    public BranchInfoModel getBranchInfoModel() {
        return mBranchInfoModel;
    }

    public void setBranchInfoModel(BranchInfoModel branchInfoModel) {
        mBranchInfoModel = branchInfoModel;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
