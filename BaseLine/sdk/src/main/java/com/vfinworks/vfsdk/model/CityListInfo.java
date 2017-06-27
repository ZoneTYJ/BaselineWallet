package com.vfinworks.vfsdk.model;

import java.util.ArrayList;
import java.util.List;

public	class CityListInfo {
    private List<CityInfoModel> city_list = new ArrayList<CityInfoModel>();

	public List<CityInfoModel> getCity() {
		return city_list;
	}

	public void setPYNames() {
		for (CityInfoModel bean : city_list) {
			bean.setName(bean.getCityName());
		}
	}

}