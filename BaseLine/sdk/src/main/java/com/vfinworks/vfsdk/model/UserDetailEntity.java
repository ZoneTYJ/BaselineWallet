package com.vfinworks.vfsdk.model;

import java.io.Serializable;

/**
 * Created by xiaoshengke on 2016/4/5.
 */
public class UserDetailEntity implements Serializable{

    /**
     * _input_charset : utf-8
     * avaliable_balance : 5003.40
     * frozen_balance : 0.00
     * is_success : T
     * memo :
     * mobile_star : 139****0001
     * nick_name : 13900000001
     * real_name_status : checkPass
     */

    private String _input_charset;
    private String avaliable_balance;
    private String frozen_balance;
    private String is_success;
    private String memo;
    private String mobile_star;
    private String nick_name;
    private String real_name_status;
    private String freezeStatus;   //0正常1止出2止入3全部禁止

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

    public String getFreezeStatus() {
        return freezeStatus;
    }

    public void setFreezeStatus(String freezeStatus) {
        this.freezeStatus = freezeStatus;
    }

    public boolean isTurnOut(){
        if(freezeStatus==null){
            return true;
        }
        if(freezeStatus.equals("1") || freezeStatus.equals("3"))
            return false;
        return true;
    }

    public boolean isTurnIn(){
        if(freezeStatus==null){
            return true;
        }
        if(freezeStatus.equals("2") || freezeStatus.equals("3"))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "UserDetailEntity{" +
                "_input_charset='" + _input_charset + '\'' +
                ", avaliable_balance='" + avaliable_balance + '\'' +
                ", frozen_balance='" + frozen_balance + '\'' +
                ", is_success='" + is_success + '\'' +
                ", memo='" + memo + '\'' +
                ", mobile_star='" + mobile_star + '\'' +
                ", nick_name='" + nick_name + '\'' +
                ", real_name_status='" + real_name_status + '\'' +
                '}';
    }
}
