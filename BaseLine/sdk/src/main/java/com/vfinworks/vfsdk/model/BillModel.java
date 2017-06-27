package com.vfinworks.vfsdk.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by xiaoshengke on 2016/4/8.
 */
public class BillModel implements Serializable {
    private String pay_status;

    public String getPay_status() {
        return pay_status;
    }

    public void setPay_status(String pay_status) {
        this.pay_status = pay_status;
    }

    /**
     * _input_charset : UTF-8
     * is_success : T
     * trade_info_list : [{"amount":"0.01","inner_trade_no":"101146002263251617468","outer_trade_no":"9198075","payee":"100006718849","payee_name":"唐寅","trade_desc":"牛奶: null","trade_status":"WAIT_PAY","trade_time":"20160407175032"},{"amount":"0.01","inner_trade_no":"101146001613590317461","outer_trade_no":"7415503","payee":"100006718849","payee_name":"唐寅","trade_desc":"牛奶: null","trade_status":"WAIT_PAY","trade_time":"20160407160216"},{"amount":"0.01","inner_trade_no":"101146001548892617459","outer_trade_no":"9328652","payee":"100006718849","payee_name":"唐寅","trade_desc":"牛奶: null","trade_status":"WAIT_PAY","trade_time":"20160407155129"},{"amount":"8.00","inner_trade_no":"101146001545950717457","outer_trade_no":"035130251603671544893","payee":"100006718849","payee_name":"唐寅","trade_desc":"牛奶: null","trade_status":"TRADE_FINISHED","trade_time":"20160407155112"},{"amount":"8.00","inner_trade_no":"101146001072298417444","outer_trade_no":"800717922123930052094","payee":"100006718849","payee_name":"唐寅","trade_desc":"牛奶: null","trade_status":"TRADE_FINISHED","trade_time":"20160407143215"},{"amount":"8.00","inner_trade_no":"101145992464183317390","outer_trade_no":"828681807562437694557","payee":"100006718849","payee_name":"唐寅","trade_desc":"牛奶: null","trade_status":"WAIT_PAY","trade_time":"20160406143721"},{"amount":"8.00","inner_trade_no":"101145992453897317389","outer_trade_no":"765136168065388214889","payee":"100006718849","payee_name":"唐寅","trade_desc":"牛奶: null","trade_status":"WAIT_PAY","trade_time":"20160406143539"},{"amount":"8.00","inner_trade_no":"101145992450492217388","outer_trade_no":"269092571676657349629","payee":"100006718849","payee_name":"唐寅","trade_desc":"牛奶: null","trade_status":"WAIT_PAY","trade_time":"20160406143505"},{"amount":"8.00","inner_trade_no":"101145992443674517387","outer_trade_no":"368416832799429406834","payee":"100006718849","payee_name":"唐寅","trade_desc":"牛奶: null","trade_status":"WAIT_PAY","trade_time":"20160406143356"},{"amount":"8.00","inner_trade_no":"101145992432331617386","outer_trade_no":"558383709079123247941","payee":"100006718849","payee_name":"唐寅","trade_desc":"牛奶: null","trade_status":"WAIT_PAY","trade_time":"20160406143203"},{"amount":"8.00","inner_trade_no":"101145992385486917380","outer_trade_no":"902718921700882463929","payee":"100006718849","payee_name":"唐寅","trade_desc":"牛奶: null","trade_status":"WAIT_PAY","trade_time":"20160406142415"},{"amount":"8.00","inner_trade_no":"101145992349507317379","outer_trade_no":"419398882608927069908","payee":"100006718849","payee_name":"唐寅","trade_desc":"牛奶: null","trade_status":"WAIT_PAY","trade_time":"20160406141815"},{"amount":"8.00","inner_trade_no":"101145992342275717378","outer_trade_no":"518712876728799126413","payee":"100006718849","payee_name":"唐寅","trade_desc":"牛奶: null","trade_status":"WAIT_PAY","trade_time":"20160406141702"},{"amount":"8.00","inner_trade_no":"101145992340719917376","outer_trade_no":"314548465844674972364","payee":"100006718849","payee_name":"唐寅","trade_desc":"牛奶: null","trade_status":"WAIT_PAY","trade_time":"20160406141647"},{"amount":"8.00","inner_trade_no":"101145992327324617375","outer_trade_no":"823981304586028894091","payee":"100006718849","payee_name":"唐寅","trade_desc":"牛奶: null","trade_status":"WAIT_PAY","trade_time":"20160406141433"},{"amount":"8.00","inner_trade_no":"101145992326166117374","outer_trade_no":"103600917366108015873","payee":"100006718849","payee_name":"唐寅","trade_desc":"牛奶: null","trade_status":"WAIT_PAY","trade_time":"20160406141421"},{"amount":"8.00","inner_trade_no":"101145992310980417373","outer_trade_no":"722307062929980111138","payee":"100006718849","payee_name":"唐寅","trade_desc":"牛奶: null","trade_status":"WAIT_PAY","trade_time":"20160406141149"},{"amount":"8.00","inner_trade_no":"101145992302051017372","outer_trade_no":"140694628101139689993","payee":"100006718849","payee_name":"唐寅","trade_desc":"牛奶: null","trade_status":"WAIT_PAY","trade_time":"20160406141020"},{"amount":"8.00","inner_trade_no":"101145992291285517371","outer_trade_no":"500556965643885134694","payee":"100006718849","payee_name":"唐寅","trade_desc":"牛奶: null","trade_status":"WAIT_PAY","trade_time":"20160406140832"},{"amount":"8.00","inner_trade_no":"101145992288993317370","outer_trade_no":"440895178602591677908","payee":"100006718849","payee_name":"唐寅","trade_desc":"牛奶: null","trade_status":"WAIT_PAY","trade_time":"20160406140810"}]
     * trade_type : INSTANT
     */

    private String _input_charset;
    private String is_success;
    private String trade_type;
    /**
     * amount : 0.01
     * inner_trade_no : 101146002263251617468
     * outer_trade_no : 9198075
     * payee : 100006718849
     * payee_name : 唐寅
     * trade_desc : 牛奶: null
     * trade_status : WAIT_PAY
     * trade_time : 20160407175032
     */

    private List<TradeInfoListBean> trade_info_list;
    private TradeInfoListBean trade_info;

    public String get_input_charset() {
        return _input_charset;
    }

    public void set_input_charset(String _input_charset) {
        this._input_charset = _input_charset;
    }

    public String getIs_success() {
        return is_success;
    }

    public void setIs_success(String is_success) {
        this.is_success = is_success;
    }

    public String getTrade_type() {
        return trade_type;
    }

    public void setTrade_type(String trade_type) {
        this.trade_type = trade_type;
    }

    public List<TradeInfoListBean> getTrade_info_list() {
        return trade_info_list;
    }

    public void setTrade_info_list(List<TradeInfoListBean> trade_info_list) {
        this.trade_info_list = trade_info_list;
    }

    public TradeInfoListBean getTrade_info() {
        return trade_info;
    }

    public void setTrade_info(TradeInfoListBean trade_info) {
        this.trade_info = trade_info;
    }

    public static class TradeInfoListBean implements Serializable{
        private String amount;
        private String inner_trade_no;
        private String outer_trade_no;
        private String payer;
        private String payer_name;
        private String payee;
        private String payee_name;
        private String trade_desc;
        private String trade_status;
        private String trade_time;
        private String memo;
        private boolean payee_is;
        private String payee_auth_name;

        private String stateDesc;

        public boolean isSection;
        public String section_desc;
        public boolean hide_line;
        public String trade_type;

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getInner_trade_no() {
            return inner_trade_no;
        }

        public void setInner_trade_no(String inner_trade_no) {
            this.inner_trade_no = inner_trade_no;
        }

        public String getOuter_trade_no() {
            return outer_trade_no;
        }

        public void setOuter_trade_no(String outer_trade_no) {
            this.outer_trade_no = outer_trade_no;
        }

        public String getPayee() {
            return payee;
        }

        public void setPayee(String payee) {
            this.payee = payee;
        }

        public String getPayee_name() {
            return payee_name;
        }

        public void setPayee_name(String payee_name) {
            this.payee_name = payee_name;
        }

        public String getTrade_desc() {
            return trade_desc;
        }

        public void setTrade_desc(String trade_desc) {
            this.trade_desc = trade_desc;
        }

        public String getTrade_status() {
            return trade_status;
        }

        public void setTrade_status(String trade_status) {
            this.trade_status = trade_status;
        }

        public String getTrade_time() {
            return trade_time;
        }

        public void setTrade_time(String trade_time) {
            this.trade_time = trade_time;
        }

        public String getStateDesc() {
            return stateDesc;
        }

        public void setStateDesc(String stateDesc) {
            this.stateDesc = stateDesc;
        }

        public String getMemo() {
            return memo;
        }

        public void setMemo(String memo) {
            this.memo = memo;
        }

        public void setPayee_is(boolean payee_is) {
            this.payee_is = payee_is;
        }

        public String getPayee_auth_name() {
            return payee_auth_name;
        }

        public void setPayee_auth_name(String payee_auth_name) {
            this.payee_auth_name = payee_auth_name;
        }

        public boolean isPayee_is() {
            return payee_is;
        }

        public String getPayer() {
            return payer;
        }

        public void setPayer(String payer) {
            this.payer = payer;
        }

        public String getPayer_name() {
            return payer_name;
        }

        public void setPayer_name(String payer_name) {
            this.payer_name = payer_name;
        }
    }
}
