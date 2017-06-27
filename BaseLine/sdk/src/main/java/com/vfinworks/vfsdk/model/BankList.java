package com.vfinworks.vfsdk.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public	class BankList {
    private List<BankModel> bank_list = new ArrayList<BankModel>();
		private HashMap<String,BankModel> mMap;

		public ArrayList<String> getNameStringList(){
			ArrayList<String> list=new ArrayList();
			if(mMap==null){
				mMap=new HashMap<>();
			}else {
				mMap.clear();
			}
			for(BankModel bean:bank_list){
				String name=bean.getBankName();
				list.add(name);
				mMap.put(name,bean);
			}
			return list;
		}

		public BankModel getBankModel(String key){
			if(mMap!=null){
				return mMap.get(key);
			}
			return null;
		}
	}