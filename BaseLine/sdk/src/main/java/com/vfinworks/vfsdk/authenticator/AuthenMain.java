package com.vfinworks.vfsdk.authenticator;

import android.content.Context;

import java.math.BigInteger;

/**
 * Created by tangyijian on 2017/1/18.
 */
public class AuthenMain {
    private static final BigInteger MULTI=new BigInteger("20198765432");
    private Context mContext;
    private static AuthenMain mAuthenMain;
    private final TotpClock mTotpClock;
    private final OtpProvider mOtpProvider;

    public AuthenMain(Context context){
        mContext=context;
        mTotpClock = new TotpClock(mContext);
        //		AccountDb accountDb=new AccountDb(mConext);
        mOtpProvider = new OtpProvider(null,mTotpClock);
    }

    public String getCurrentCode(String secret){
      String code="";
        try {
            code=mOtpProvider.computeToTpPin(secret,null);
            return code;
        } catch (OtpSourceException e) {
            return code;
        }
    }

    public String getStringQRCode(String dig,String phone){
        return new BigInteger(dig).multiply(MULTI).add(new BigInteger(phone)).toString();
    }

    public String[] decodeQRCode(String qrCodeStr){
        BigInteger qrCode=new BigInteger(qrCodeStr);
        String phone=qrCode.remainder(MULTI).toString();
        String code=qrCode.divide(MULTI).toString();
        return new String[]{phone,code};
    }

}
