package com.okwei.bean.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * AActProductsShowTime entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "A_ActProductsShowTime")
public class AActProductsShowTime implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = -8138093858858489238L;
	private Long actPid;
	private Long actId;
	private Integer classId;
	private Long proActId;
	private Long productId;
	private Date beginTime;
	private Date endTime;
	private Integer sort;

	// Constructors

	/** default constructor */
	public AActProductsShowTime() {
	}

	/** minimal constructor */
	public AActProductsShowTime(Long actPid, Long proActId) {
		this.actPid = actPid;
		this.proActId = proActId;
	}

	/** full constructor */
	public AActProductsShowTime(Long actPid, Long proActId, Long productId,
			Date beginTime, Date endTime, Integer sort) {
		this.actPid = actPid;
		this.proActId = proActId;
		this.productId = productId;
		this.beginTime = beginTime;
		this.endTime = endTime;
		this.sort = sort;
	}

	// Property accessors
	@Id
	@Column(name = "ActPID", unique = true, nullable = false)
	public Long getActPid() {
		return this.actPid;
	}

	public void setActPid(Long actPid) {
		this.actPid = actPid;
	}

	@Column(name = "ProActID", nullable = false)
	public Long getProActId() {
		return this.proActId;
	}

	public void setProActId(Long proActId) {
		this.proActId = proActId;
	}
	
	@Column(name = "ActID", nullable = false)
	public Long getActId() {
		return this.actId;
	}

	public void setActId(Long actId) {
		this.actId = actId;
	}

	@Column(name = "ProductID")
	public Long getProductId() {
		return this.productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	@Column(name = "BeginTime", length = 0)
	public Date getBeginTime() {
		return this.beginTime;
	}

	public void setBeginTime(Date beginTime) {
		this.beginTime = beginTime;
	}

	@Column(name = "EndTime", length = 0)
	public Date getEndTime() {
		return this.endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	@Column(name = "Sort")
	public Integer getSort() {
		return this.sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}


	@Column(name = "ClassID")
	public Integer getClassId() {
		return this.classId;
	}

	public void setClassId(Integer classId) {
		this.classId = classId;
	}
}