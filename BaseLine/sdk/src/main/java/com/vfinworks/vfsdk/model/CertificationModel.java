package com.vfinworks.vfsdk.model;

import java.io.Serializable;

/**
 * Created by xiaoshengke on 2016/8/19.
 */
public class CertificationModel implements Serializable{

    /**
     * _input_charset : utf-8
     * cert_no : 432503198911225951
     * cert_type : 1
     * is_success : T
     * memo :
     * real_name : f*
     * status : notFound
     */

    private String _input_charset;
    private String cert_no;
    private String cert_type;
    private String is_success;
    private String memo;
    private String real_name;
    private String status;

    public String get_input_charset() {
        return _input_charset;
    }

    public void set_input_charset(String _input_charset) {
        this._input_charset = _input_charset;
    }

    public String getCert_no() {
        return cert_no;
    }

    public void setCert_no(String cert_no) {
        this.cert_no = cert_no;
    }

    public String getCert_type() {
        return cert_type;
    }

    public void setCert_type(String cert_type) {
        this.cert_type = cert_type;
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

    public String getReal_name() {
        return real_name;
    }

    public void setReal_name(String real_name) {
        this.real_name = real_name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
