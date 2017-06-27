package com.vfinworks.vfsdk.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class BankCardListModel{
	@SerializedName("card_list")
	private ArrayList<BankCardModel> cardList = new ArrayList<BankCardModel>();

	public ArrayList<BankCardModel> getCardList() {
		return cardList;
	}

	public void setCardList(ArrayList cardList) {
		this.cardList = cardList;
	}
}
