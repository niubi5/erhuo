package com.geminno.erhuo.entity;

import java.util.Date;

/**
 * 捐赠信息
 * 
 * @author Administrator
 * 
 */

public class Donation {
	/**
	 * 用户头像
	 */
	private Integer userHeadImage;
	/**
	 * 用户名
	 */
	private String userName;
	/**
	 * 发布时间
	 */
	private String time;
	/**
	 * 发布图片
	 */
	private Integer image;
	/**
	 * 发布内容
	 */
	private String detail;
	/**
	 * 请求捐赠地址图片
	 */
	private Integer addressImage;
	/**
	 * 请求捐赠地址
	 */
	private String address;
	/**
	 * 按钮
	 */
	private Integer button;

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public Integer getButton() {
		return button;
	}

	public void setButton(Integer button) {
		this.button = button;
	}

	public Donation(Integer userHeadImage, String userName, String time,
			Integer image, String detail, Integer addressImage, String address,
			Integer button) {
		super();
		this.userHeadImage = userHeadImage;
		this.userName = userName;
		this.time = time;
		this.image = image;
		this.detail = detail;
		this.addressImage = addressImage;
		this.address = address;
		this.button = button;
	}

	public Donation(Integer userHeadImage, String userName, String time,
			Integer image, Integer addressImage, String address) {
		super();
		this.userHeadImage = userHeadImage;
		this.userName = userName;
		this.time = time;
		this.image = image;
		this.addressImage = addressImage;
		this.address = address;
	}

	public Integer getUserHeadImage() {
		return userHeadImage;
	}

	public void setUserHeadImage(Integer userHeadImage) {
		this.userHeadImage = userHeadImage;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public Integer getImage() {
		return image;
	}

	public void setImage(Integer image) {
		this.image = image;
	}

	public Integer getAddressImage() {
		return addressImage;
	}

	public void setAddressImage(Integer addressImage) {
		this.addressImage = addressImage;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

}
