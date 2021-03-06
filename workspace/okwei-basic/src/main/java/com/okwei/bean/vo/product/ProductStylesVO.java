package com.okwei.bean.vo.product;

import java.util.List;

import com.okwei.bean.vo.order.ProductStyleKVVO;

public class ProductStylesVO {
	
	/**
	 * 款式ID
	 */
	private Long stylesId;
	/**
	 * 商品ID
	 */
	private Long productId;
	/**
	 * 零售价
	 */
	private Double retailPrice;
	/**
	 * 价格
	 */
	private Double price;
	/**
	 * 佣金
	 */
	private Double conmision;
	/**
	 * 数量
	 */
	private Integer count;
	/**
	 * 款式图片
	 */
	private String defaultImg;
	/**
	 * 商家编码
	 */
	private String bussinessNo;
	
	/**
	 * 代理价
	 */
	private Double proxyPrice;
	/**
	 * 落地价
	 */
	private Double floorPrice;
	/**
	 * 供货价
	 */
	
	private Double supplyPrice;
	
	/**
	 * 款式属性键值对列表
	 */
	private List<ProductStyleKVVO> styleKVList;
	
	public Double getSupplyPrice() {
		return supplyPrice;
	}
	public void setSupplyPrice(Double supplyPrice) {
		this.supplyPrice = supplyPrice;
	}
	public Long getStylesId() {
		return stylesId;
	}
	public void setStylesId(Long stylesId) {
		this.stylesId = stylesId;
	}
	public Long getProductId() {
		return productId;
	}
	public void setProductId(Long productId) {
		this.productId = productId;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public Double getConmision() {
		return conmision;
	}
	public void setConmision(Double conmision) {
		this.conmision = conmision;
	}
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}
	public String getDefaultImg() {
		return defaultImg;
	}
	public void setDefaultImg(String defaultImg) {
		this.defaultImg = defaultImg;
	}
	public String getBussinessNo() {
		return bussinessNo;
	}
	public void setBussinessNo(String bussinessNo) {
		this.bussinessNo = bussinessNo;
	}
	
	public List<ProductStyleKVVO> getStyleKVList() {
		return styleKVList;
	}
	public void setStyleKVList(List<ProductStyleKVVO> styleKVList) {
		this.styleKVList = styleKVList;
	}
	public Double getProxyPrice() {
		return proxyPrice;
	}
	public void setProxyPrice(Double proxyPrice) {
		this.proxyPrice = proxyPrice;
	}
	public Double getFloorPrice() {
		return floorPrice;
	}
	public void setFloorPrice(Double floorPrice) {
		this.floorPrice = floorPrice;
	}
	public Double getRetailPrice() {
		return retailPrice;
	}
	public void setRetailPrice(Double retailPrice) {
		this.retailPrice = retailPrice;
	}
}
