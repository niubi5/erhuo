package com.geminno.erhuo.entity;


public class UserMarket {
	private int userId;
	private int marketId;
	private String focTime;

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getMarketId() {
		return marketId;
	}

	public void setMarketId(int marketId) {
		this.marketId = marketId;
	}

	public String getFocTime() {
		return focTime;
	}

	public void setFocTime(String focTime) {
		this.focTime = focTime;
	}
}
