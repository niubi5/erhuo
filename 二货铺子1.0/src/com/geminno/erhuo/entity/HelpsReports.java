package com.geminno.erhuo.entity;

import java.util.Date;

public class HelpsReports {
	private int id;
	private int helpId;
	private String brief;
	private Date repTime;
	private int state;
	
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
