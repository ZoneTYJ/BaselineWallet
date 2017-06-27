package com.vfinworks.vfsdk.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class RealNameInfoModel implements Parcelable{
	//证件类型
	@SerializedName("cert_type")
	private String certType;
	
	//证件号码
	@SerializedName("cert_no")
	private String certNo;
	
	//真实姓名
	@SerializedName("real_name")
	private String realName;
	
	//状态
	@SerializedName("status")
	private String status;
	
	public RealNameInfoModel(Parcel source){
		certType = source.readString();
		certNo = source.readString();
		realName = source.readString();
		status = source.readString();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(certType);
		dest.writeString(certNo);
		dest.writeString(realName);
		dest.writeString(status);
	}
	
	//Interface that must be implemented and provided as a public CREATOR field that generates instances of your Parcelable class from a Parcel. 
	public final static Parcelable.Creator<BankModel> CREATOR = new Parcelable.Creator<BankModel>() {

		@Override
		public BankModel createFromParcel(Parcel source) {
			return new BankModel(source);
		}

		@Override
		public BankModel[] newArray(int size) {
			return new BankModel[size];
		}
	};
	
	@Override
	public int describeContents() {
		return 0;
	}

	public String getCertType() {
		return certType;
	}

	public void setCertType(String certType) {
		this.certType = certType;
	}

	public String getCertNo() {
		return certNo;
	}

	public void setCertNo(String certNo) {
		this.certNo = certNo;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
