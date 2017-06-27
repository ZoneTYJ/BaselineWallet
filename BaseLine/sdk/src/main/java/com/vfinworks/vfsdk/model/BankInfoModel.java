package com.vfinworks.vfsdk.model;

import android.os.Parcel;
import android.os.Parcelable;
 
/**
 * Created by cheng on 15/1/01 银行信息Model
 */
public class BankInfoModel implements Parcelable{

	// 提现银行卡列表
	private String bankId;
	private String bankName;
	private String branchId;
	private String branchName;
	private String cardNo; 

	public String getBankId() {
		return bankId;
	}

	public void setBankId(String bankId) {
		this.bankId = bankId;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getBranchId() {
		return branchId;
	}

	public void setBranchId(String branchId) {
		this.branchId = branchId;
	}

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	@Override
	public int describeContents() { 
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) { 
		dest.writeString(this.bankId);
		dest.writeString(this.bankName);
		dest.writeString(this.branchId);
		dest.writeString(this.branchName);
		dest.writeString(this.cardNo);
	}
	
	public static Creator<BankInfoModel> CREATOR = new Creator<BankInfoModel>() {

		@Override
		public BankInfoModel createFromParcel(Parcel source) { 
			BankInfoModel bankInfo = new BankInfoModel();
			bankInfo.bankId = source.readString();
			bankInfo.bankName = source.readString();
			bankInfo.branchId = source.readString();
			bankInfo.branchName = source.readString();
			bankInfo.cardNo = source.readString();
			return bankInfo;
		}

		@Override
		public BankInfoModel[] newArray(int size) { 
			return new BankInfoModel[size];
		}
	};

	@Override
	public String toString() {
		return "BankInfo [bankId=" + bankId + ", bankNmae=" + bankName
				+ ", branchId=" + branchId + ", branchName=" + branchName
				+ ", cardNo=" + cardNo + "]";
	}

}
