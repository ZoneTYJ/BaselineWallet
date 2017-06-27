package com.vfinworks.vfsdk.interfacemanager;

/**
 * Created by tangyijian on 2016/5/5.
 */
public class SignInterfaceManager {
    private static SignInterfaceManager signInterfaceManager;
    private SignInterface signInterface;

    public static SignInterfaceManager getInstance(){
        if(signInterfaceManager==null){
            signInterfaceManager=new SignInterfaceManager();
        }
        return signInterfaceManager;
    }

    public void setSignInterface(SignInterface signInterface){
        this.signInterface=signInterface;
    }

    public SignInterface getSignInterface() {
        return signInterface;
    }
}
