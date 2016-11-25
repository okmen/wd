package com.okwei.bean.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * PProducts entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "P_Products")
public class PProducts extends BaseEntity {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long productId;
	private Integer freightId;
	private Integer sid;
	private Integer classId;

	
	private Integer mid;
	
	private Integer bmid;

	private String classPath;
	private Long supplierWeiId;
	private String productTitle;
	private String productMinTitle;
	private String defaultImg;
	private String pcdes;
	private String appdes;
	private Double defaultPrice;
	private Double bookPrice;
	private Double batchPrice;
	private Double defaultConmision;
	private Integer count;
	private Short tag;
	private Short sort;
	private Short bType;
	private Short state;
	private Integer brandId;
	private Short supperType;
	private Date createTime;
	private Date updateTime;
	private Date endTime;
	private Double defPostFee;
	private Date verTime;
	private Short verStatus;
	
	private Double originalPrice;
	private Double percent;
	
	private Short publishType;//类型 普通区还是代理产品
	private Double supplyPrice; //供货价
	private Double dukePrice;//一级代理价
	private Double deputyPrice;//二级代理价
	private Double agentPrice;//三级代理价
	
	@Id
	@GeneratedValue
	@Column(name = "ProductID", unique = true, nullable = false)
	public Long getProductId() {
		return this.productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	@Column(name = "FreightID")
	public Integer getFreightId() {
		return this.freightId;
	}

	public void setFreightId(Integer freightId) {
		this.freightId = freightId;
	}

	@Column(name = "SID")
	public Integer getSid() {
		return this.sid;
	}

	public void setSid(Integer sid) {
		this.sid = sid;
	}

	@Column(name = "ClassID")
	public Integer getClassId() {
		return this.classId;
	}

	public void setClassId(Integer classId) {
		this.classId = classId;
	}

	@Column(name = "MID")
	public Integer getMid() {
		return mid;
	}

	public void setMid(Integer mid) {
		this.mid = mid;
	}

	@Column(name = "ClassPath", length = 32)
	public String getClassPath() {
		return this.classPath;
	}

	public void setClassPath(String classPath) {
		this.classPath = classPath;
	}

	@Column(name = "SupplierWeiID")
	public Long getSupplierWeiId() {
		return this.supplierWeiId;
	}

	public void setSupplierWeiId(Long supplierWeiId) {
		this.supplierWeiId = supplierWeiId;
	}

	@Column(name = "ProductTitle", length = 256)
	public String getProductTitle() {
		return this.productTitle;
	}

	public void setProductTitle(String productTitle) {
		this.productTitle = productTitle;
	}

	@Column(name = "ProductMinTitle", length = 256)
	public String getProductMinTitle() {
		return this.productMinTitle;
	}

	public void setProductMinTitle(String productMinTitle) {
		this.productMinTitle = productMinTitle;
	}

	@Column(name = "DefaultImg", length = 256)
	public String getDefaultImg() {
		return this.defaultImg;
	}

	public void setDefaultImg(String defaultImg) {
		this.defaultImg = defaultImg;
	}

	@Column(name = "PCDES", length = 65535)
	public String getPcdes() {
		return this.pcdes;
	}

	public void setPcdes(String pcdes) {
		this.pcdes = pcdes;
	}

	@Column(name = "APPDES", length = 65535)
	public String getAppdes() {
		return this.appdes;
	}

	public void setAppdes(String appdes) {
		this.appdes = appdes;
	}

	@Column(name = "DefaultPrice", precision = 18)
	public Double getDefaultPrice() {
		if (this.defaultPrice == null)
			return 0d;
		return this.defaultPrice;
	}

	public void setDefaultPrice(Double defaultPrice) {
		this.defaultPrice = defaultPrice;
	}

	@Column(name = "DefaultConmision", precision = 18)
	public Double getDefaultConmision() {
		if (this.defaultConmision == null)
			return 0d;
		return this.defaultConmision;
	}

	public void setDefaultConmision(Double defaultConmision) {
		this.defaultConmision = defaultConmision;
	}

	@Column(name = "Count")
	public Integer getCount() {
		return this.count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	@Column(name = "Tag")
	public Short getTag() {
		return this.tag;
	}

	public void setTag(Short tag) {
		this.tag = tag;
	}

	@Column(name = "State")
	public Short getState() {
		return this.state;
	}

	public void setState(Short state) {
		this.state = state;
	}

	@Column(name = "SupperType")
	public Short getSupperType() {
		return this.supperType;
	}

	public void setSupperType(Short supperType) {
		this.supperType = supperType;
	}

	@Column(name = "CreateTime", length = 0)
	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Column(name = "UpdateTime", length = 0)
	public Date getUpdateTime() {
		return this.updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	@Column(name = "EndTime", length = 0)
	public Date getEndTime() {
		return this.endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	@Column(name = "BookPrice", precision = 18)
	public Double getBookPrice() {
		if (this.bookPrice == null)
			return 0d;
		return bookPrice;
	}

	public void setBookPrice(Double bookPrice) {
		this.bookPrice = bookPrice;
	}

	@Column(name = "BatchPrice", precision = 18)
	public Double getBatchPrice() {
		if (this.batchPrice == null)
			return 0d;
		return batchPrice;
	}

	public void setBatchPrice(Double batchPrice) {
		this.batchPrice = batchPrice;
	}

	@Column(name = "BrandID")
	public Integer getBrandId() {
		return brandId;
	}

	public void setBrandId(Integer brandId) {
		this.brandId = brandId;
	}

	@Column(name = "Sort")
	public Short getSort() {
		return sort;
	}

	public void setSort(Short sort) {
		this.sort = sort;
	}

	@Column(name = "DefPostFee")
	public Double getDefPostFee() {
		if (this.defPostFee == null)
			return 0d;
		return defPostFee;
	}

	public void setDefPostFee(Double defPostFee) {
		this.defPostFee = defPostFee;
	}

	@Column(name = "BType")
	public Short getbType() {
		return bType;
	}

	public void setbType(Short bType) {
		this.bType = bType;
	}

	@Column(name="BMID")
	public Integer getBmid() {
		return bmid;
	}

	public void setBmid(Integer bmid) {
		this.bmid = bmid;
	}

    @Column(name="VerTime",length = 0)
	public Date getVerTime() {
		return verTime;
	}

	public void setVerTime(Date verTime) {
		this.verTime = verTime;
	}


	@Column(name="VerStatus")
	public Short getVerStatus() {
		return verStatus;
	}

	public void setVerStatus(Short verStatus) {
		this.verStatus = verStatus;
	}

	@Column(name = "OriginalPrice", precision = 18)
	public Double getOriginalPrice() {
		return originalPrice;
	}

	public void setOriginalPrice(Double originalPrice) {
		this.originalPrice = originalPrice;
	}

	@Column(name = "Percent", precision = 18)
	public Double getPercent() {
		return percent;
	}

	public void setPercent(Double percent) {
		this.percent = percent;
	}

	@Column(name = "SupplyPrice", precision = 18)
	public Double getSupplyPrice() {
		return supplyPrice;
	}

	public void setSupplyPrice(Double supplyPrice) {
		this.supplyPrice = supplyPrice;
	}

	@Column(name = "PublishType")
	public Short getPublishType() {
		return publishType;
	}

	public void setPublishType(Short publishType) {
		this.publishType = publishType;
	}

	@Column(name = "DukePrice")
	public Double getDukePrice() {
		return dukePrice;
	}

	public void setDukePrice(Double dukePrice) {
		this.dukePrice = dukePrice;
	}

	@Column(name = "DeputyPrice")
	public Double getDeputyPrice() {
		return deputyPrice;
	}

	public void setDeputyPrice(Double deputyPrice) {
		this.deputyPrice = deputyPrice;
	}

	@Column(name = "AgentPrice")
	public Double getAgentPrice() {
		return agentPrice;
	}

	public void setAgentPrice(Double agentPrice) {
		this.agentPrice = agentPrice;
	}
	
	

}