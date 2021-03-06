package com.okwei.myportal.bean.vo;

public class PostAgeModelVO {
	/**
	 * 模板ID
	 */
	private Integer freightId;
	/**
	 * 模板名称
	 */
	private String postAgeName;
	/**
	 * 供应商微店号
	 */
	private Long supplierWeiId;
	/**
	 * 模板类型
	 */
	private Short billingType;
	/**
	 * 发货时间
	 */
	private Short deliverytime;
	private Double free;
	/**
	 * 发货地址
	 */
	private String deliveryAddress;
	/**
	 * 模板状态
	 */
	private Short status;
	public Integer getFreightId() {
		return freightId;
	}
	public void setFreightId(Integer freightId) {
		this.freightId = freightId;
	}
	public String getPostAgeName() {
		return postAgeName;
	}
	public void setPostAgeName(String postAgeName) {
		this.postAgeName = postAgeName;
	}
	public Long getSupplierWeiId() {
		return supplierWeiId;
	}
	public void setSupplierWeiId(Long supplierWeiId) {
		this.supplierWeiId = supplierWeiId;
	}
	public Short getBillingType() {
		return billingType;
	}
	public void setBillingType(Short billingType) {
		this.billingType = billingType;
	}
	public Short getDeliverytime() {
		return deliverytime;
	}
	public void setDeliverytime(Short deliverytime) {
		this.deliverytime = deliverytime;
	}
	public Double getFree() {
		return free;
	}
	public void setFree(Double free) {
		this.free = free;
	}
	public String getDeliveryAddress() {
		return deliveryAddress;
	}
	public void setDeliveryAddress(String deliveryAddress) {
		this.deliveryAddress = deliveryAddress;
	}
	public Short getStatus() {
		return status;
	}
	public void setStatus(Short status) {
		this.status = status;
	}
	
}
