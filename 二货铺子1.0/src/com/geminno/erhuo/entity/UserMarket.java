package com.geminno.erhuo.entity;

import java.util.Date;

public class UserMarket {
	private int userId;
	private int marketId;
	private Date focTime;
	
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
	public Date getFocTime() {
		return focTime;
	}
	public void setFocTime(Date focTime) {
		this.focTime = focTime;
	}
}
