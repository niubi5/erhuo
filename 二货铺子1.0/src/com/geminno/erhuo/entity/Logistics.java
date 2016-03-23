package com.geminno.erhuo.entity;

/**
 * 物流信息
 * @author Administrator
 *
 */
public class Logistics {
	private Integer imageId;
	private String verify;

	public Logistics(Integer imageId, String verify) {
		super();
		this.imageId = imageId;
		this.verify = verify;
	}

	public Integer getImageId() {
		return imageId;
	}

	public void setImageId(Integer imageId) {
		this.imageId = imageId;
	}

	public String getVerify() {
		return verify;
	}

	public void setVerify(String verify) {
		this.verify = verify;
	}

	

}
