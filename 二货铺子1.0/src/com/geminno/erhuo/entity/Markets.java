package com.geminno.erhuo.entity;

import java.io.File;

public class Markets {
	private int id;
	private String name;
	private int logo;
	private String brief;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getLogo() {
		return logo;
	}
	public void setLogo(int logo) {
		this.logo = logo;
	}
	public String getBrief() {
		return brief;
	}
	public void setBrief(String brief) {
		this.brief = brief;
	}
}
