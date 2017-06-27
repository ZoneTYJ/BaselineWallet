package com.vfinworks.vfsdk.model;

import android.os.Parcel;

import java.io.Serializable;

public class BankModel implements Serializable{
	private String bankName;
	private String bankCode;
	
	
	public BankModel(Parcel source){
		bankName = source.readString();
		bankCode = source.readString();
	}

	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	
	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}
	
}
