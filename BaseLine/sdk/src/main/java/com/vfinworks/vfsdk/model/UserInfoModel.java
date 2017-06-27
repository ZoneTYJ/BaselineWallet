package com.vfinworks.vfsdk.model;

import java.io.Serializable;

/**
 * Created by tangyijian on 2016/8/17.
 */
public class UserInfoModel implements Serializable {
    public String token;
    public String nickname;
    public String memberId;
    public String mobileStar;
    public boolean realNameStatus;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getMobileStar() {
        return mobileStar;
    }

    public void setMobileStar(String mobileStar) {
        this.mobileStar = mobileStar;
    }

    public boolean isRealNameStatus() {
        return realNameStatus;
    }

    public void setRealNameStatus(boolean realNameStatus) {
        this.realNameStatus = realNameStatus;
    }


}
