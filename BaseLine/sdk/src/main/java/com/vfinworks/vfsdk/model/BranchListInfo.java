package com.vfinworks.vfsdk.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BranchListInfo {
    private List<BranchInfoModel> branch_list = new ArrayList<BranchInfoModel>();;
		private HashMap<String,BranchInfoModel> mMap;

		public ArrayList<String> getNameStringList(){
				ArrayList<String> list=new ArrayList();
			if(branch_list==null){
				return list;
			}
			if(mMap==null){
				mMap=new HashMap<>();
			}else {
				mMap.clear();
			}
			for(BranchInfoModel bean:branch_list){
				String name=bean.getsName();
				list.add(name);
				mMap.put(name,bean);
			}
			return list;
		}

		public BranchInfoModel getBankModel(String key){
			if(mMap!=null){
				return mMap.get(key);
			}
			return null;
		}
	}