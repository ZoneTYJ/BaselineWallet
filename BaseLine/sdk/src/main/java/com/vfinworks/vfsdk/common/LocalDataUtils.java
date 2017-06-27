package com.vfinworks.vfsdk.common;

public class LocalDataUtils {
	private String TAG = this.getClass().getName();
	private static LocalDataUtils mInstance;
	
    private LocalDataUtils() {
    }
    
    public static synchronized LocalDataUtils getInstance() {
        if (mInstance == null) {
            mInstance = new LocalDataUtils();
        }
        return mInstance;
    }
	

    public String getLastPayBank(String data) {
        return SharedPreferenceUtil.getInstance().getStringValueFromSP(data);
    }
    public void setLastPayBank(String data,String value) {
        SharedPreferenceUtil.getInstance().setStringDataIntoSP(data,value);
    }

    public void removeLastPayBankId(String memberId) {
        SharedPreferenceUtil.getInstance().setStringDataIntoSP(memberId+"withdraw_bank_id", "");
        SharedPreferenceUtil.getInstance().setStringDataIntoSP(memberId+"withdraw_bank_name", "");
        SharedPreferenceUtil.getInstance().setStringDataIntoSP(memberId+"withdraw_bank_number", "");
    }
}
