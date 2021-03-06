package com.okwei.bean.domain;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * AActivityProducts entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "A_ActivityProducts")
public class AActivityProducts implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = -7630372924359878101L; 
	public Long proActId;
	public Long actId;
	public Long sellerId;
	public Integer classId;
	public Long productId;
	public String productTitle;
	public String productImg;
	public Double price;
	public Double commission;
	public Double traddingPrice;
	public Integer totalCount;
	public Short state;
	public Long verifier;
	public Date verifyTime;
	public Date createTime;
	public String reason;

	// Constructors

	/** default constructor */
	public AActivityProducts() {
	}

	/** full constructor */
	public AActivityProducts(Long actId, Long sellerId, Integer classId,
			Long productId, String productTitle, String productImg, Double price,
			Double commission, Integer totalCount, Short state, Long verifier,
			Date verifyTime, Date createTime) {
		this.actId = actId;
		this.sellerId = sellerId;
		this.classId = classId;
		this.productId = productId;
		this.productTitle = productTitle;
		this.productImg = productImg;
		this.price = price;
		this.commission = commission;
		this.totalCount = totalCount;
		this.state = state;
		this.verifier = verifier;
		this.verifyTime = verifyTime;
		this.createTime = createTime;
	}

	// Property accessors
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "ProActID", unique = true, nullable = false)
	public Long getProActId() {
		return this.proActId;
	}

	public void setProActId(Long proActId) {
		this.proActId = proActId;
	}

	@Column(name = "ActID")
	public Long getActId() {
		return this.actId;
	}

	public void setActId(Long actId) {
		this.actId = actId;
	}

	@Column(name = "SellerID")
	public Long getSellerId() {
		return this.sellerId;
	}

	public void setSellerId(Long sellerId) {
		this.sellerId = sellerId;
	}

	@Column(name = "ClassID")
	public Integer getClassId() {
		return this.classId;
	}

	public void setClassId(Integer classId) {
		this.classId = classId;
	}

	@Column(name = "ProductID")
	public Long getProductId() {
		return this.productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	@Column(name = "ProductTitle", length = 256)
	public String getProductTitle() {
		return this.productTitle;
	}

	public void setProductTitle(String productTitle) {
		this.productTitle = productTitle;
	}

	@Column(name = "ProductImg", length = 128)
	public String getProductImg() {
		return this.productImg;
	}

	public void setProductImg(String productImg) {
		this.productImg = productImg;
	}

	@Column(name = "Price",  precision = 18)
	public Double getPrice() {
		return this.price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	@Column(name = "Commission", precision = 18)
	public Double getCommission() {
		return this.commission;
	}

	public void setCommission(Double commission) {
		this.commission = commission;
	}
	
	@Column(name = "TraddingPrice", precision = 18)
	public Double getTraddingPrice() {
		return this.traddingPrice;
	}

	public void setTraddingPrice(Double traddingPrice) {
		this.traddingPrice = traddingPrice;
	}

	@Column(name = "TotalCount")
	public Integer getTotalCount() {
		return this.totalCount;
	}

	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
	}

	@Column(name = "State")
	public Short getState() {
		return this.state;
	}

	public void setState(Short state) {
		this.state = state;
	}

	@Column(name = "Verifier")
	public Long getVerifier() {
		return this.verifier;
	}

	public void setVerifier(Long verifier) {
		this.verifier = verifier;
	}

	@Column(name = "VerifyTime", length = 0)
	public Date getVerifyTime() {
		return this.verifyTime;
	}

	public void setVerifyTime(Date verifyTime) {
		this.verifyTime = verifyTime;
	}

	@Column(name = "CreateTime", length = 0)
	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	@Column(name = "Reason", length = 128)
	public String getReason() {
		return this.reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}


}