package com.okwei.myportal.bean.dto;

public class BrandProveDTO {
	/**
	 * 品牌名称
	 */
	private String brandName;
	
	/***
	 * 品牌申请状态
	 */
	private Integer brandState;

	/**
	 * 分类IDs
	 */
	private Integer[] classIDs;
	/**
	 * 分类ID字符串
	 */
	private String clasString;
	
	
	public String getClasString() {
		return clasString;
	}

	public void setClasString(String clasString) {
		this.clasString = clasString;
	}

	public Integer[] getClassIDs() {
		return classIDs;
	}

	public void setClassIDs(Integer[] classIDs) {
		this.classIDs = classIDs;
	}

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public Integer getBrandState() {
		return brandState;
	}

	public void setBrandState(Integer brandState) {
		this.brandState = brandState;
	}
}
