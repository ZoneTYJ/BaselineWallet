package com.vfinworks.vfsdk.context;

public class DealAcceptContext extends BaseAcquireContext {
    //请求号
    private String requestNo;

    public DealAcceptContext(){}
    public DealAcceptContext(BaseContext baseContext) {
        super(baseContext);
    }

    public String getRequestNo() {
        return requestNo;
    }

    public void setRequestNo(String requestNo) {
        this.requestNo = requestNo;
    }

}
