package com.okwei.bean.vo.product;

import java.io.Serializable;

public class ProductInfo implements Serializable{

	private static final long serialVersionUID = 1L;
    private Long productId;
    private String productName;
    private String productPicture;
    private Double storePrice;
    private Double retailPrice;
    private Double agentPrice;
    private int ldPriceVisibility;
    private int dlPriceVisibility;
    private int currentUserIsAgent;
    private int currentUserIsStore;
    private int stockCount;
    private int saleCount;
    private Long supWeiID;
    private Integer demandId;
    private String companyName;
    private Double orderAmount;
    private Double displayPrice;
    private Long shelveId;
    private Long sellerWid;
    private Double commission;
    private String shopClass;
    private int type;
    private Integer sort;
    private Short activity;
    public Double getDisplayPrice() {
		return displayPrice;
	}

	public void setDisplayPrice(Double displayPrice) {
		this.displayPrice = displayPrice;
	}

	public Long getProductId() {
	return productId;
    }

    public void setProductId(Long productId) {
	this.productId = productId;
    }

    public String getProductName() {
	return productName;
    }

    public void setProductName(String productName) {
	this.productName = productName;
    }

    public String getProductPicture() {
	return productPicture;
    }

    public void setProductPicture(String productPicture) {
	this.productPicture = productPicture;
    }

    public Double getStorePrice() {
	return storePrice;
    }

    public void setStorePrice(Double storePrice) {
	this.storePrice = storePrice;
    }

    public Double getRetailPrice() {
	return retailPrice;
    }

    public void setRetailPrice(Double retailPrice) {
	this.retailPrice = retailPrice;
    }

    public Double getAgentPrice() {
	return agentPrice;
    }

    public void setAgentPrice(Double agentPrice) {
	this.agentPrice = agentPrice;
    }

    public int getLdPriceVisibility() {
	return ldPriceVisibility;
    }

    public void setLdPriceVisibility(int ldPriceVisibility) {
	this.ldPriceVisibility = ldPriceVisibility;
    }

    public int getDlPriceVisibility() {
	return dlPriceVisibility;
    }

    public void setDlPriceVisibility(int dlPriceVisibility) {
	this.dlPriceVisibility = dlPriceVisibility;
    }

    public int getCurrentUserIsAgent() {
	return currentUserIsAgent;
    }

    public void setCurrentUserIsAgent(int currentUserIsAgent) {
	this.currentUserIsAgent = currentUserIsAgent;
    }

    public int getCurrentUserIsStore() {
	return currentUserIsStore;
    }

    public void setCurrentUserIsStore(int currentUserIsStore) {
	this.currentUserIsStore = currentUserIsStore;
    }

    public int getStockCount() {
	return stockCount;
    }

    public void setStockCount(int stockCount) {
	this.stockCount = stockCount;
    }

    public int getSaleCount() {
	return saleCount;
    }

    public void setSaleCount(int saleCount) {
	this.saleCount = saleCount;
    }

    public Long getSupWeiID() {
        return supWeiID;
    }

    public void setSupWeiID(Long supWeiID) {
        this.supWeiID = supWeiID;
    }

    public Integer getDemandId() {
        return demandId;
    }

    public void setDemandId(Integer demandId) {
        this.demandId = demandId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Double getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(Double orderAmount) {
        this.orderAmount = orderAmount;
    }

    public Long getShelveId() {
        return shelveId;
    }

    public void setShelveId(Long shelveId) {
        this.shelveId = shelveId;
    }

    public Long getSellerWid() {
        return sellerWid;
    }

    public void setSellerWid(Long sellerWid) {
        this.sellerWid = sellerWid;
    }

    public Double getCommission() {
        return commission;
    }

    public void setCommission(Double commission) {
        this.commission = commission;
    }

    public String getShopClass() {
        return shopClass;
    }

    public void setShopClass(String shopClass) {
        this.shopClass = shopClass;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
    public Integer getSort() {
		return sort;
	}
    public void setSort(Integer sort) {
		this.sort = sort;
	}
   public Short getActivity() {
	   return activity;
   }
   public void setActivity(Short activity) {
	   this.activity = activity;
   }
}
