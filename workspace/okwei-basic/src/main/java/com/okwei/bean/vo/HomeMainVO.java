package com.okwei.bean.vo;

import java.io.Serializable;
import java.util.Date;

public class HomeMainVO implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
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

    public Integer getHomeId() {
	return homeId;
    }

    public void setHomeId(Integer homeId) {
	this.homeId = homeId;
    }

    public Integer getClassId() {
	return classId;
    }

    public void setClassId(Integer classId) {
	this.classId = classId;
    }

    public String getClassName() {
	return className;
    }

    public void setClassName(String className) {
	this.className = className;
    }

    public Integer getPosition() {
	return position;
    }

    public void setPosition(Integer position) {
	this.position = position;
    }

    public String getCategoryName() {
	return categoryName;
    }

    public void setCategoryName(String categoryName) {
	this.categoryName = categoryName;
    }

    public String getLead() {
	return lead;
    }

    public void setLead(String lead) {
	this.lead = lead;
    }

    public String getHomeImage() {
	return homeImage;
    }

    public void setHomeImage(String homeImage) {
	this.homeImage = homeImage;
    }

    public String getBannerImage() {
	return bannerImage;
    }

    public void setBannerImage(String bannerImage) {
	this.bannerImage = bannerImage;
    }

    public Short getState() {
	return state;
    }

    public void setState(Short state) {
	this.state = state;
    }

    public Date getCreateTime() {
	return createTime;
    }

    public void setCreateTime(Date createTime) {
	this.createTime = createTime;
    }
    
    public Integer getType() {
		return type;
	}
    public void setType(Integer type) {
		this.type = type;
	}
}
