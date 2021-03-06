package com.okwei.supplyportal.bean.vo;

import java.util.Date;

public class ShopClassVO {
	/**
	 * 店铺分类ID
	 */
	private Integer sid;
	/**
	 * 店铺分类名称
	 */
	private long weiid;
	/**
	 * 店铺分类名称
	 */
	private String sname;
	/**
	 * 状态
	 */
	private Short state;
	/**
	 * 排序
	 */
	private Short sort;
	/**
	 * 类型
	 */
	private Short type;

	public Integer getSid() {
		return sid;
	}

	public void setSid(Integer sid) {
		this.sid = sid;
	}

	public long getWeiid() {
		return weiid;
	}

	public void setWeiid(long weiid) {
		this.weiid = weiid;
	}

	public String getSname() {
		return sname;
	}

	public void setSname(String sname) {
		this.sname = sname;
	}

	public Short getState() {
		return state;
	}

	public void setState(Short state) {
		this.state = state;
	}

	public Short getSort() {
		return sort;
	}

	public void setSort(Short sort) {
		this.sort = sort;
	}

	public Short getType() {
		return type;
	}

	public void setType(Short type) {
		this.type = type;
	}
}
