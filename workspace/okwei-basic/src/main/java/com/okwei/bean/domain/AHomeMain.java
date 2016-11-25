package com.okwei.bean.domain;

import static javax.persistence.GenerationType.IDENTITY;

import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * AHomeMain entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "A_HomeMain")
public class AHomeMain implements java.io.Serializable {

	// Fields

	private Integer homeId;
	private Integer classId;
	private String className;
	private Integer position;
	private String categoryName;
	private String lead;
	private String homeImage;
	private String bannerImage;
	private Short state;
	private Date createTime;
	private Integer type;
	 

	// Constructors

	/** default constructor */
	public AHomeMain() {
	}

	/** full constructor */
	public AHomeMain(Integer classId, Integer position, String categoryName,
			String lead, String homeImage, String bannerImage, Short state,
			Timestamp createTime) {
		this.classId = classId;
		this.position = position;
		this.categoryName = categoryName;
		this.lead = lead;
		this.homeImage = homeImage;
		this.bannerImage = bannerImage;
		this.state = state;
		this.createTime = createTime;
	}

	// Property accessors
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "HomeID", unique = true, nullable = false)
	public Integer getHomeId() {
		return this.homeId;
	}

	public void setHomeId(Integer homeId) {
		this.homeId = homeId;
	}

	@Column(name = "ClassID")
	public Integer getClassId() {
		return this.classId;
	}

	public void setClassId(Integer classId) {
		this.classId = classId;
	}
	
	@Column(name = "ClassName", length = 16)
	public String getClassName() {
		return this.className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	@Column(name = "position")
	public Integer getPosition() {
		return this.position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	@Column(name = "CategoryName", length = 32)
	public String getCategoryName() {
		return this.categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	@Column(name = "Lead", length = 512)
	public String getLead() {
		return this.lead;
	}

	public void setLead(String lead) {
		this.lead = lead;
	}

	@Column(name = "HomeImage", length = 256)
	public String getHomeImage() {
		return this.homeImage;
	}

	public void setHomeImage(String homeImage) {
		this.homeImage = homeImage;
	}

	@Column(name = "BannerImage", length = 256)
	public String getBannerImage() {
		return this.bannerImage;
	}

	public void setBannerImage(String bannerImage) {
		this.bannerImage = bannerImage;
	}

	@Column(name = "State")
	public Short getState() {
		return this.state;
	}

	public void setState(Short state) {
		this.state = state;
	}

	@Column(name = "CreateTime", length = 0)
	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	@Column(name = "Type")
	public Integer getType() {
		return this.type;
	}

	public void setType(Integer type) {
		if(type==null)
			type=0;
		this.type = type;
	}

}