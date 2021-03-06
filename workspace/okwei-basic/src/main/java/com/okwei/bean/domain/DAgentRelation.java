package com.okwei.bean.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * DAgentRelation entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "D_AgentRelation")
public class DAgentRelation implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8738170878859531495L;
	// Fields

	private Long weiId;
	private Integer brandId;
	private Long superWeiId;
	private Integer myLevel;
	private Long weiIdsec;
	private Long weiIdone;

	// Constructors

	/** default constructor */
	public DAgentRelation() {
	}

	/** minimal constructor */
	public DAgentRelation(Long weiId) {
		this.weiId = weiId;
	}

	/** full constructor */
	public DAgentRelation(Long weiId, Long superWeiId, Integer myLevel,
			Long weiIdsec, Long weiIdone) {
		this.weiId = weiId;
		this.superWeiId = superWeiId;
		this.myLevel = myLevel;
		this.weiIdsec = weiIdsec;
		this.weiIdone = weiIdone;
	}

	// Property accessors
	@Id
	@Column(name = "WeiID", unique = true, nullable = false)
	public Long getWeiId() {
		return this.weiId;
	}

	public void setWeiId(Long weiId) {
		this.weiId = weiId;
	}
	
	@Column(name = "BrandID", unique = true, nullable = false)
	public Integer getBrandId() {
		return this.brandId;
	}

	public void setBrandId(Integer brandId) {
		this.brandId = brandId;
	}

	@Column(name = "SuperWeiID")
	public Long getSuperWeiId() {
		return this.superWeiId;
	}

	public void setSuperWeiId(Long superWeiId) {
		this.superWeiId = superWeiId;
	}

	@Column(name = "MyLevel")
	public Integer getMyLevel() {
		return this.myLevel;
	}

	public void setMyLevel(Integer myLevel) {
		this.myLevel = myLevel;
	}

	@Column(name = "WeiIDSec")
	public Long getWeiIdsec() {
		return this.weiIdsec;
	}

	public void setWeiIdsec(Long weiIdsec) {
		this.weiIdsec = weiIdsec;
	}

	@Column(name = "WeiIDOne")
	public Long getWeiIdone() {
		return this.weiIdone;
	}

	public void setWeiIdone(Long weiIdone) {
		this.weiIdone = weiIdone;
	}

}