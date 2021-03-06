package com.okwei.myportal.bean.vo;

public class ProductSellValueVO {
	private Long keyId;
	/**
	 * 商品ID
	 */
	private Long productId;
	/**
	 * 分类属性ID
	 */
	private Long attributeId;
	/**
	 * 分类ID
	 */
	private Integer classId;
	/**
	 * 值
	 */
	private String value;
	/**
	 * 图片
	 */
	private String defaultImg;
	public Long getKeyId() {
		return keyId;
	}
	public void setKeyId(Long keyId) {
		this.keyId = keyId;
	}
	public Long getProductId() {
		return productId;
	}
	public void setProductId(Long productId) {
		this.productId = productId;
	}
	public Long getAttributeId() {
		return attributeId;
	}
	public void setAttributeId(Long attributeId) {
		this.attributeId = attributeId;
	}
	public Integer getClassId() {
		return classId;
	}
	public void setClassId(Integer classId) {
		this.classId = classId;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getDefaultImg() {
		return defaultImg;
	}
	public void setDefaultImg(String defaultImg) {
		this.defaultImg = defaultImg;
	}
}
