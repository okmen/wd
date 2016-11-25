package com.okwei.bean.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * OSupplyerOrder entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "O_SupplyerOrder")
public class OSupplyerOrder extends BaseEntity {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String supplierOrderId;
	private String payOrderId;
	private Integer orderType;
	private Long addrId;
	private Long supplyerId;
	private Double totalPrice;
	private Double sourcePrice;
	private Double commision;
	private Double postage;
	private String dComanpyNo;
	private String deliveryCompany;
	private String deliveryOrder;
	private Short state;
	private Short sellerDel;
	private Short buyerDel;
	private Date orderTime;
	private Date payTime;
	private Date sendTime;
	private Date reciptTime;
	private String subNo;
	private Long verWeiId;
	private Integer sourse;
	private Short isActivity;
	private Long activityId;
	private Long sellerWeiId;
	
	@Column(name = "BackStatu")
	private Short BackStatu;

	@Column(name = "SellerDel")
	public Short getSellerDel() {
		return sellerDel;
	}

	public void setSellerDel(Short sellerDel) {
		this.sellerDel = sellerDel;
	}

	@Column(name = "BuyerDel")
	public Short getBuyerDel() {
		return buyerDel;
	}

	public void setBuyerDel(Short buyerDel) {
		this.buyerDel = buyerDel;
	}

	private Short orderFrom;
	private Short isChagePrice;

	@Column(name = "IsChangePrice")
	public Short getIsChagePrice() {
		return isChagePrice;
	}

	public void setIsChagePrice(Short isChagePrice) {
		this.isChagePrice = isChagePrice;
	}

	@Column(name = "BalanceTime")
	public Date getBalanceTime() {
		return balanceTime;
	}

	public void setBalanceTime(Date balanceTime) {
		this.balanceTime = balanceTime;
	}

	private Date balanceTime;
	private Date orderDate;
	private String message;
	private String proInstId;
	private Long buyerID;

	// Constructors

	/** default constructor */
	public OSupplyerOrder() {
	}

	/** minimal constructor */
	public OSupplyerOrder(Date sendTime) {
		this.sendTime = sendTime;
	}

	// Property accessors
	@Id
	@Column(name = "SupplierOrderID", unique = true, nullable = false, length = 32)
	public String getSupplierOrderId() {
		return this.supplierOrderId;
	}

	public void setSupplierOrderId(String supplierOrderId) {
		this.supplierOrderId = supplierOrderId;
	}

	@Column(name = "PayOrderID", length = 32)
	public String getPayOrderId() {
		return this.payOrderId;
	}

	public void setPayOrderId(String payOrderId) {
		this.payOrderId = payOrderId;
	}

	@Column(name = "OrderType")
	public Integer getOrderType() {
		return this.orderType;
	}

	public void setOrderType(Integer orderType) {
		this.orderType = orderType;
	}
	
	@Column(name = "Sourse")
	public Integer getSourse() {
		return this.sourse;
	}

	public void setSourse(Integer sourse) {
		this.sourse = sourse;
	}

	@Column(name = "AddrID")
	public Long getAddrId() {
		return this.addrId;
	}

	public void setAddrId(Long addrId) {
		this.addrId = addrId;
	}

	@Column(name = "SupplyerID")
	public Long getSupplyerId() {
		return this.supplyerId;
	}

	public void setSupplyerId(Long supplyerId) {
		this.supplyerId = supplyerId;
	}
	
	@Column(name = "SellerWeiID")
	public Long getSellerWeiId() {
		return this.sellerWeiId;
	}

	public void setSellerWeiId(Long sellerweiid) {
		this.sellerWeiId = sellerweiid;
	}

	@Column(name = "TotalPrice", precision = 18)
	public Double getTotalPrice() {
		return this.totalPrice;
	}

	public void setTotalPrice(Double totalPrice) {
		this.totalPrice = totalPrice;
	}

	@Column(name = "Postage", precision = 18)
	public Double getPostage() {
		if (this.postage == null)
			return 0d;
		return this.postage;
	}

	public void setPostage(Double postage) {
		this.postage = postage;
	}

	@Column(name = "DeliveryCompany", length = 64)
	public String getDeliveryCompany() {
		return this.deliveryCompany;
	}

	public void setDeliveryCompany(String deliveryCompany) {
		this.deliveryCompany = deliveryCompany;
	}

	@Column(name = "DeliveryOrder", length = 32)
	public String getDeliveryOrder() {
		return this.deliveryOrder;
	}

	public void setDeliveryOrder(String deliveryOrder) {
		this.deliveryOrder = deliveryOrder;
	}

	@Column(name = "State")
	public Short getState() {
		return this.state;
	}

	public void setState(Short state) {
		this.state = state;
	}

	@Column(name = "OrderTime", length = 0)
	public Date getOrderTime() {
		return this.orderTime;
	}

	public void setOrderTime(Date orderTime) {
		this.orderTime = orderTime;
	}

	@Column(name = "SendTime", length = 0)
	public Date getSendTime() {
		return this.sendTime;
	}

	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}

	@Column(name = "OrderDate", length = 0)
	public Date getOrderDate() {
		return this.orderDate;
	}

	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}

	@Column(name = "Message", length = 512)
	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Column(name = "ProInstID", length = 50)
	public String getProInstId() {
		return this.proInstId;
	}

	public void setProInstId(String proInstId) {
		this.proInstId = proInstId;
	}

	@Column(name = "DComanpyNo")
	public String getdComanpyNo() {
		return dComanpyNo;
	}

	public void setdComanpyNo(String dComanpyNo) {
		this.dComanpyNo = dComanpyNo;
	}

	@Column(name = "BuyerID")
	public Long getBuyerID() {
		return buyerID;
	}

	public void setBuyerID(Long buyerID) {
		this.buyerID = buyerID;
	}

	@Column(name = "Commision")
	public Double getCommision() {
		if (this.commision == null)
			return 0d;
		return commision;
	}

	public void setCommision(Double commision) {
		this.commision = commision;
	}

	@Column(name = "OrderFrom")
	public Short getOrderFrom() {
		return orderFrom;
	}

	public void setOrderFrom(Short orderFrom) {
		this.orderFrom = orderFrom;
	}

	@Column(name = "PayTime")
	public Date getPayTime() {
		return payTime;
	}

	public void setPayTime(Date payTime) {
		this.payTime = payTime;
	}

	@Column(name = "ReceptTime")
	public Date getReciptTime() {
		return reciptTime;
	}

	public void setReciptTime(Date reciptTime) {
		this.reciptTime = reciptTime;
	}

	@Column(name = "SourcePrice")
	public Double getSourcePrice() {
		return sourcePrice;
	}

	public void setSourcePrice(Double sourcePrice) {
		this.sourcePrice = sourcePrice;
	}

	public Short getBackStatu() {
		return BackStatu;
	}

	public void setBackStatu(Short backStatu) {
		BackStatu = backStatu;
	}
	@Column(name = "SubNo", length = 64)
	public String getSubNo() {
		return subNo;
	}

	public void setSubNo(String subNo) {
		this.subNo = subNo;
	}
	
	@Column(name = "VerWeiId")
	public Long getVerWeiId() {
		return this.verWeiId;
	}

	public void setVerWeiId(Long verWeiId) {
		this.verWeiId = verWeiId;
	}
	
	@Column(name = "IsActivity")
	public Short getIsActivity() {
		if(isActivity==null)
			return (short)0;
		return isActivity;
	}

	public void setIsActivity(Short isActivity) {
		this.isActivity = isActivity;
	}
	
	@Column(name = "ActivityId")
	public Long getActivityId() {
		return activityId;
	}

	public void setActivityId(Long activity) {
		this.activityId = activity;
	}
}