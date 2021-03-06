package com.okwei.myportal.bean.vo;

import java.util.List;

public class BrandListVO {
	/**
	 * 品牌ID
	 */
	private int brandID;
	/**
	 * 品牌LOGO
	 */
	private String brandLOGO;
	/**
	 * 品牌名称
	 */
	private String brandName;
	/**
	 * 分类列表
	 */
	private List<BrandClassParentVO> cfbVO;
	/**
	 * 品牌状态
	 */
	private int state;
	/**
	 * 状态名称
	 */
	private String stateName;

	public int getBrandID() {
		return brandID;
	}

	public void setBrandID(int brandID) {
		this.brandID = brandID;
	}

	public String getBrandLOGO() {
		return brandLOGO;
	}

	public void setBrandLOGO(String brandLOGO) {
		this.brandLOGO = brandLOGO;
	}

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getStateName() {
		return stateName;
	}

	public void setStateName(String stateName) {
		this.stateName = stateName;
	}

	public List<BrandClassParentVO> getCfbVO() {
		return cfbVO;
	}

	public void setCfbVO(List<BrandClassParentVO> cfbVO) {
		this.cfbVO = cfbVO;
	}
	
}
