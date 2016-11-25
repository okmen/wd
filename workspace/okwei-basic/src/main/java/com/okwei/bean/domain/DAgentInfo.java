package com.okwei.bean.domain;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * DAgentInfo entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "D_AgentInfo")
public class DAgentInfo implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4804058284436471634L;
	// Fields

	private Long agentId;
	private Long weiId;
	private Integer brandId;
	private String name;
	private String contactPhone;
	private Long superWeiid;
	private Integer province;
	private Integer city;
	private Integer district;
	private String qq;
	private Integer type;
	private Double costs;
	private Date createTime;
	private Integer status;
	private Integer level;

	// Constructors

	/** default constructor */
	public DAgentInfo() {
	}

	/** minimal constructor */
	public DAgentInfo(Long agentId) {
		this.agentId = agentId;
	}

	/** full constructor */
	public DAgentInfo(Long agentId, Long weiId, Integer brandId, String name,
			String contactPhone, Long superWeiid, String qq, Integer type,
			Double costs, Date createTime, Integer status) {
		this.agentId = agentId;
		this.weiId = weiId;
		this.brandId = brandId;
		this.name = name;
		this.contactPhone = contactPhone;
		this.superWeiid = superWeiid;
		this.qq = qq;
		this.type = type;
		this.costs = costs;
		this.createTime = createTime;
		this.status = status;
	}

	// Property accessors
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "AgentID", unique = true, nullable = false)
	public Long getAgentId() {
		return this.agentId;
	}

	public void setAgentId(Long agentId) {
		this.agentId = agentId;
	}

	@Column(name = "WeiID")
	public Long getWeiId() {
		return this.weiId;
	}

	public void setWeiId(Long weiId) {
		this.weiId = weiId;
	}

	@Column(name = "BrandID")
	public Integer getBrandId() {
		return this.brandId;
	}

	public void setBrandId(Integer brandId) {
		this.brandId = brandId;
	}

	@Column(name = "Name", length = 64)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "ContactPhone", length = 64)
	public String getContactPhone() {
		return this.contactPhone;
	}

	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}

	@Column(name = "SuperWeiid")
	public Long getSuperWeiid() {
		return this.superWeiid;
	}

	public void setSuperWeiid(Long superWeiid) {
		this.superWeiid = superWeiid;
	}

	@Column(name = "QQ", length = 64)
	public String getQq() {
		return this.qq;
	}

	public void setQq(String qq) {
		this.qq = qq;
	}

	@Column(name = "Type")
	public Integer getType() {
		return this.type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	@Column(name = "Costs", precision = 18)
	public Double getCosts() {
		return this.costs;
	}

	public void setCosts(Double costs) {
		this.costs = costs;
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
	
	@Column(name = "Province")
	public Integer getProvince() {
		return this.province;
	}

	public void setProvince(Integer province) {
		this.province = province;
	}

	@Column(name = "City")
	public Integer getCity() {
		return this.city;
	}

	public void setCity(Integer city) {
		this.city = city;
	}

	@Column(name = "District")
	public Integer getDistrict() {
		return this.district;
	}

	public void setDistrict(Integer district) {
		this.district = district;
	}
	
	@Column(name = "Level")
	public Integer getLevel() {
		return this.level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

}