package com.vfinworks.vfsdk.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tangyijian on 2017/4/24.
 */

public class ProvinceListInfo {
    private List<ProvinceInfoModel> province_list = new ArrayList<ProvinceInfoModel>();

    public List<ProvinceInfoModel> getProvince() {
        return province_list;
    }

    public void setPYNames(){
        for (ProvinceInfoModel bean:province_list){
            bean.setName(bean.getProvName());
        }
    }
}
