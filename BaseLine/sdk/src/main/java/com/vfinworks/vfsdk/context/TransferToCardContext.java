package com.vfinworks.vfsdk.context;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by tangyijian on 2016/9/1.
 */
public class TransferToCardContext implements Serializable {
    @SerializedName("cardNo")
    private String bank_account_no;
    @SerializedName("bankCode")
    private String bank_code;
    @SerializedName("bankName")
    private String bank_name;
    @SerializedName("prov")
    private String province;
    @SerializedName("city")
    private String city;
    @SerializedName("branchName")
    private String bank_branch;
    @SerializedName("name")
    private String account_name;
//    @SerializedName("cardType")
//    private String card_type;
    @SerializedName("companyOrPersonal")
    private String card_attribute;

    public String getBank_code() {
        return bank_code;
    }

    public void setBank_code(String bank_code) {
        this.bank_code = bank_code;
    }

    public String getBank_name() {
        return bank_name;
    }

    public void setBank_name(String bank_name) {
        this.bank_name = bank_name;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getBank_branch() {
        return bank_branch;
    }

    public void setBank_branch(String bank_branch) {
        this.bank_branch = bank_branch;
    }

    public String getAccount_name() {
        return account_name;
    }

    public void setAccount_name(String account_name) {
        this.account_name = account_name;
    }

    public String getBank_account_no() {
        return bank_account_no;
    }

    public void setBank_account_no(String bank_account_no) {
        this.bank_account_no = bank_account_no;
    }

    public String getCard_attribute() {
        return card_attribute;
    }

    public void setCard_attribute(String card_attribute) {
        this.card_attribute = card_attribute;
    }

}
