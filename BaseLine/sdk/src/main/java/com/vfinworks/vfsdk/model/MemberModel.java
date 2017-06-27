package com.vfinworks.vfsdk.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by xiaoshengke on 2016/8/18.
 */
public class MemberModel implements Serializable{

    /**
     * _input_charset : utf-8
     * avaliable_balance : 0.00
     * common_operation : ["{\"operation_name\":\"rechage\"}","{\"operation_name\":\"transfer\"}","{\"operation_name\":\"withdraw\"}","{\"operation_name\":\"transactionlist\"}"]
     * frozen_balance : 0.00
     * is_success : T
     * memo :
     * mobile_star : 13633333333
     * mycard_redenvelopes : ["{\"operation_name\":\"coupons\"}","{\"operation_name\":\"RedEnvelopes\"}"]
     * nick_name : cannot be null
     * real_name_status : notFound
     */

    private String _input_charset;
    private String avaliable_balance;
    private String frozen_balance;
    private String is_success;
    private String memo;
    private String mobile_star;
    private String nick_name;
    private String real_name_status;
    private List<String> common_operation;
    private List<String> mycard_redenvelopes;
    private String pay_pwd_status;
    public String cardNumber;
    public String token;

    public String get_input_charset() {
        return _input_charset;
    }

    public void set_input_charset(String _input_charset) {
        this._input_charset = _input_charset;
    }

    public String getAvaliable_balance() {
        return avaliable_balance;
    }

    public void setAvaliable_balance(String avaliable_balance) {
        this.avaliable_balance = avaliable_balance;
    }

    public String getFrozen_balance() {
        return frozen_balance;
    }

    public void setFrozen_balance(String frozen_balance) {
        this.frozen_balance = frozen_balance;
    }

    public String getIs_success() {
        return is_success;
    }

    public void setIs_success(String is_success) {
        this.is_success = is_success;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getMobile_star() {
        return mobile_star;
    }

    public void setMobile_star(String mobile_star) {
        this.mobile_star = mobile_star;
    }

    public String getNick_name() {
        return nick_name;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
    }

    public String getReal_name_status() {
        return real_name_status;
    }

    public void setReal_name_status(String real_name_status) {
        this.real_name_status = real_name_status;
    }

    public List<String> getCommon_operation() {
        return common_operation;
    }

    public void setCommon_operation(List<String> common_operation) {
        this.common_operation = common_operation;
    }

    public List<String> getMycard_redenvelopes() {
        return mycard_redenvelopes;
    }

    public void setMycard_redenvelopes(List<String> mycard_redenvelopes) {
        this.mycard_redenvelopes = mycard_redenvelopes;
    }

    public String getPay_pwd_status() {
        return pay_pwd_status;
    }

    public void setPay_pwd_status(String pay_pwd_status) {
        this.pay_pwd_status = pay_pwd_status;
    }
}
