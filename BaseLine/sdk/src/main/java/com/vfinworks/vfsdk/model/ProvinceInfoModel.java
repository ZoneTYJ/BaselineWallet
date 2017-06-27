package com.vfinworks.vfsdk.model;

import com.vfinworks.vfsdk.view.sidebarlist.PinYinBean;

public class ProvinceInfoModel extends PinYinBean{

	private int provId;
	private String provName;
	private String provShortName; 

	public int getProvId() {
		return provId;
	}

	public void setProvId(int provId) {
		this.provId = provId;
	}

	public String getProvName() {
		return provName;
	}

	public void setProvName(String provName) {
		this.provName = provName;
	}

	public String getProvShortName() {
		return provShortName;
	}

	public void setProvShortName(String provShortName) {
		this.provShortName = provShortName;
	}

}
