package com.vfinworks.vfsdk.context;


public class UnbindBankCardContext extends BaseContext{
	private static final long serialVersionUID = 6423866860287065326L;

	private String bankCardId;

	public String getBankCardId() {
		return bankCardId;
	}

	public void setBankCardId(String bankCardId) {
		this.bankCardId = bankCardId;
	}

}
