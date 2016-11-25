package com.okwei.bean.domain;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * DAgentMembers entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "D_AgentMembers")
public class DAgentMembers implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9038591931572962823L;
	// Fields

	private Long memberId;
	private Integer brandId;
	private Long weiId;
	private Long superWeiid;
	private Date createTime;
	private Long agentId;

	// Constructors

	/** default constructor */
	public DAgentMembers() {
	}

	/** minimal constructor */
	public DAgentMembers(Long memberId) {
		this.memberId = memberId;
	}

	/** full constructor */
	public DAgentMembers(Long memberId, Integer brandId, Long weiId,
			 Date createTime, Long agentId) {
		this.memberId = memberId;
		this.brandId = brandId;
		this.weiId = weiId;
		this.createTime = createTime;
		this.agentId = agentId;
	}

	// Property accessors
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "MemberID", unique = true, nullable = false)
	public Long getMemberId() {
		return this.memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

	@Column(name = "BrandID")
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

	@Column(name = "SuperWeiid")
	public Long getSuperWeiid() {
		return superWeiid;
	}

	public void setSuperWeiid(Long superWeiid) {
		this.superWeiid = superWeiid;
	}

	@Column(name = "CreateTime", length = 0)
	public Date getCreateTime() {
		return this.createTime;
	}

	

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Column(name = "AgentID")
	public Long getAgentId() {
		return this.agentId;
	}

	public void setAgentId(Long agentId) {
		this.agentId = agentId;
	}

}