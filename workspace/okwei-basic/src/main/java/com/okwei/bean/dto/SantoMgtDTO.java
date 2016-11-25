package com.okwei.bean.dto;

import com.okwei.bean.domain.TRegional;





public class SantoMgtDTO {
	
	private Integer brandId;//品牌号
	private Long weiId;//微店号
	private String brandName;//品牌名称
	private TRegional tregional;
	private Integer province;  //省
	private Integer city;  //市
	private Integer district;  //区
	private String createTime;//入驻时间
	
	private Integer status;//城主、代理、2级代理状态
	public Long getWeiId() {
		return weiId;
	}
	public void setWeiId(Long weiId) {
		this.weiId = weiId;
	}
	public String getBrandName() {
		return brandName;
	}
	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}
	
	
	/**
	 * @return the province
	 */
	public Integer getProvince() {
		return province;
	}
	/**
	 * @param province the province to set
	 */
	public void setProvince(Integer province) {
		this.province = province;
	}
	/**
	 * @return the city
	 */
	public Integer getCity() {
		return city;
	}
	/**
	 * @param city the city to set
	 */
	public void setCity(Integer city) {
		this.city = city;
	}
	/**
	 * @return the district
	 */
	public Integer getDistrict() {
		return district;
	}
	/**
	 * @param district the district to set
	 */
	public void setDistrict(Integer district) {
		this.district = district;
	}
	/**
	 * @return the createTime
	 */
	public String getCreateTime() {
		return createTime;
	}
	/**
	 * @param createTime the createTime to set
	 */
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public Integer getBrandId() {
		return brandId;
	}
	public void setBrandId(Integer brandId) {
		this.brandId = brandId;
	}
	/**
	 * @return the tregional
	 */
	public TRegional getTregional() {
		return tregional;
	}
	/**
	 * @param tregional the tregional to set
	 */
	public void setTregional(TRegional tregional) {
		this.tregional = tregional;
	}
	/**
	 * @return the status
	 */
	public Integer getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}
	
	
}
