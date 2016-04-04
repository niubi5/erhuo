package com.geminno.erhuo.entity;

import java.io.Serializable;


public class Donates implements Serializable{
	private int id;
	private int helpId;
	private int userId;
	private String title;
	private String brief;
	private String donTime;
	private String logisticsCom;
	private String logisticsNum;

	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getBrief() {
		return brief;
	}

	public void setBrief(String brief) {
		this.brief = brief;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getHelpId() {
		return helpId;
	}

	public void setHelpId(int helpId) {
		this.helpId = helpId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getDonTime() {
		return donTime;
	}

	public void setDonTime(String donTime) {
		this.donTime = donTime;
	}

	public String getLogisticsCom() {
		return logisticsCom;
	}

	public void setLogisticsCom(String logisticsCom) {
		this.logisticsCom = logisticsCom;
	}

	public String getLogisticsNum() {
		return logisticsNum;
	}

	public void setLogisticsNum(String logisticsNum) {
		this.logisticsNum = logisticsNum;
	}
}
