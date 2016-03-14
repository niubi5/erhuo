package com.geminno.erhuo.entity;

import java.util.Date;

public class GoodsReports {
	private int id;
	private int goodId;
	private String brief;
	private Date repTime;
	private int state;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getGoodId() {
		return goodId;
	}
	public void setGoodId(int goodId) {
		this.goodId = goodId;
	}
	public String getBrief() {
		return brief;
	}
	public void setBrief(String brief) {
		this.brief = brief;
	}
	public Date getRepTime() {
		return repTime;
	}
	public void setRepTime(Date repTime) {
		this.repTime = repTime;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
}
