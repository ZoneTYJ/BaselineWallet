package com.vfinworks.vfsdk.context;

/**
 * Created by tangyijian on 2017/3/28.
 */

public class PlaceOrderPayContext extends PlaceOrderContext {
   private String qrcode;//二维码
   private String number_pwd;//二维码令牌
   private String seller;//买家手机号
   private String buyer;//卖家手机号
   private String productName;//商品名称

    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }

    public String getNumber_pwd() {
        return number_pwd;
    }

    public void setNumber_pwd(String number_pwd) {
        this.number_pwd = number_pwd;
    }

    public String getSeller() {
        return seller;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }

    public String getBuyer() {
        return buyer;
    }

    public void setBuyer(String buyer) {
        this.buyer = buyer;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

}
