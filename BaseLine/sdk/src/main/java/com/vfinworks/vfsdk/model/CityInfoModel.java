package com.vfinworks.vfsdk.model;

import com.vfinworks.vfsdk.view.sidebarlist.PinYinBean;

/**
 * Created by cheng on 15/1/01 城市Model
 */
public class CityInfoModel extends PinYinBean{

	private int cityId;
	private String provId;
	private String cityName;
	private String cityShortName; 

	public int getCityId() {
		return cityId;
	}

	public void setCityId(int cityId) {
		this.cityId = cityId;
	}

	public String getProvId() {
		return provId;
	}

	public void setProvId(String provId) {
		this.provId = provId;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getCityShortName() {
		return cityShortName;
	}

	public void setCityShortName(String cityShortName) {
		this.cityShortName = cityShortName;
	}
}
