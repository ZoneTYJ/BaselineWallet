package com.vfinworks.vfsdk.model;

import java.io.Serializable;

/**
 * Created by cheng on 15/1/01 分行Model
 */
public class BranchInfoModel implements Serializable {

	private int branchId;
	private String branchNo;
	private String branchName;
	private String branchShortName;

	public int getId() {
		return branchId;
	}

	public void setId(int id) {
		this.branchId = id;
	}

	public String getNo() {
		return branchNo;
	}

	public void setNo(String no) {
		this.branchNo = no;
	}

	public String getName() {
		return branchName;
	}

	public void setName(String name) {
		this.branchName = name;
	}

	public String getsName() {
		return branchShortName;
	}

	public void setsName(String sName) {
		this.branchShortName = sName;
	}

}
