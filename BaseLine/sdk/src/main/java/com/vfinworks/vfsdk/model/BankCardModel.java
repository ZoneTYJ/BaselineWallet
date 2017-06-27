package com.vfinworks.vfsdk.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;

import com.google.gson.annotations.SerializedName;
import com.vfinworks.vfsdk.common.Utils;

public class BankCardModel implements Parcelable{
	public static String QPAY="qpay";
	public static String NORMAL="normal";
	//银行卡Id
	@SerializedName("bank_card_id")
	private String bankcardId;
	//卡号
	@SerializedName("card_no")
	private String cardNo;
	//绑定手机号
	@SerializedName("mobile")
	private String mobileMask;
	//银行名称
	@SerializedName("bank_name")
	private String bankName;
	//卡类型 :DEBIT, CREDIT
	@SerializedName("card_type")
	private String cardType;
	//C,B
	@SerializedName("card_attribute")
	private String cardAttribute;
	//状态(0失效  1正常 2锁定)
	@SerializedName("card_status")
	private String cardStatus;
	//pay_attribute支付类型 qpay快捷 normal
	@SerializedName("pay_attribute")
	private String pay_attribute;
	//银行卡编码
	private String bank_code;
	
	public BankCardModel(){
	}
	
	public BankCardModel(Parcel source){
		bankcardId = source.readString();
		cardNo = source.readString();
		mobileMask = source.readString();
		bankName = source.readString();
		cardType = source.readString();
		cardAttribute = source.readString();
		cardStatus = source.readString();
		bank_code=source.readString();
		pay_attribute=source.readString();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(bankcardId);
		dest.writeString(cardNo);
		dest.writeString(mobileMask);
		dest.writeString(bankName);
		dest.writeString(cardType);
		dest.writeString(cardAttribute);
		dest.writeString(cardStatus);
		dest.writeString(bank_code);
		dest.writeString(pay_attribute);
	}
	
	//Interface that must be implemented and provided as a public CREATOR field that generates instances of your Parcelable class from a Parcel. 
	public final static Parcelable.Creator<BankCardModel> CREATOR = new Parcelable.Creator<BankCardModel>() {

		@Override
		public BankCardModel createFromParcel(Parcel source) {
			return new BankCardModel(source);
		}

		@Override
		public BankCardModel[] newArray(int size) {
			return new BankCardModel[size];
		}
	};
	
	public String getCardNo() {
		return cardNo;
	}
	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}
	public String getMobileMask() {
		return mobileMask;
	}
	public void setMobileMask(String mobileMask) {
		this.mobileMask = mobileMask;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getBankcardId() {
		return bankcardId;
	}
	public void setBankcardId(String bankcardId) {
		this.bankcardId = bankcardId;
	}
	public String getCardType() {
		return cardType;
	}
	public void setCardType(String cardType) {
		this.cardType = cardType;
	}
	public String getCardAttribute() {
		return cardAttribute;
	}
	public void setCardAttribute(String cardAttribute) {
		this.cardAttribute = cardAttribute;
	}
	public String getCardStatus() {
		return cardStatus;
	}
	public void setCardStatus(String cardStatus) {
		this.cardStatus = cardStatus;
	}

	public String getBank_code() {
		return bank_code;
	}

	public void setBank_code(String bank_code) {
		this.bank_code = bank_code;
	}

	public String getPay_attribute() {
		return pay_attribute;
	}

	public void setPay_attribute(String pay_attribute) {
		this.pay_attribute = pay_attribute;
	}

	@Override
	public int describeContents() {
		return 0;
	}


	public void setDrawableIcon(ImageView imageView) {
		int i=Utils.getInstance().getBankDrawableIcon(bank_code);
		if(i!=-1) {
			imageView.setImageResource(i);
		}
//		com.vfinworks.vfsdk.Utils.getInstance().getBankDrawableIcon(bank_code,imageView);
	}
}
