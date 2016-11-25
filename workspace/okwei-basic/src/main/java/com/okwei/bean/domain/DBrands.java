package com.okwei.bean.domain;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * DBrands entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "D_BrandsNew")
public class DBrands implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2763669534177903755L;
	// Fields

	private Integer brandId;
	private Long weiId;
	private String brandName;
	private String logo;
	private String headName;
	private String phoneNumber;
	private String industry;
	private Date createTime;
	private Integer status;
	private Integer type;

	// Constructors

	/** default constructor */
	public DBrands() {
	}

	/** minimal constructor */
	public DBrands(Integer brandId) {
		this.brandId = brandId;
	}

	/** full constructor */
	public DBrands(Integer brandId, Long weiId, String brandName, String logo,
			String headName, String phoneNumber, String industry,
			Date createTime, Integer status) {
		this.brandId = brandId;
		this.weiId = weiId;
		this.brandName = brandName;
		this.logo = logo;
		this.headName = headName;
		this.phoneNumber = phoneNumber;
		this.industry = industry;
		this.createTime = createTime;
		this.status = status;
	}

	// Property accessors
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "BrandID", unique = true, nullable = false)
	public Integer getBrandId() {
		return this.brandId;
	}

	public void setBrandId(Integer brandId) {
		this.brandId = brandId;
	}

	@Column(name = "WeiID")
	public Long getWeiId() {
		return this.weiId;
	}

	public void setWeiId(Long weiId) {
		this.weiId = weiId;
	}

	@Column(name = "BrandName", length = 64)
	public String getBrandName() {
		return this.brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	@Column(name = "Logo", length = 128)
	public String getLogo() {
		return this.logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	@Column(name = "HeadName", length = 64)
	public String getHeadName() {
		return this.headName;
	}

	public void setHeadName(String headName) {
		this.headName = headName;
	}

	@Column(name = "PhoneNumber", length = 64)
	public String getPhoneNumber() {
		return this.phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	@Column(name = "Industry", length = 128)
	public String getIndustry() {
		return this.industry;
	}

	public void setIndustry(String industry) {
		this.industry = industry;
	}

	@Column(name = "CreateTime", length = 0)
	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Column(name = "Status")
	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
	
	@Column(name = "Type")
	public Integer getType() {
		return this.type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

}