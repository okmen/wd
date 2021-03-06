package com.okwei.myportal.bean.vo;

import java.util.List;

public class SupplyOrderVO extends BaseOrder
{
	 /**
     * 支付金额信息
     */
    private List<String> payContent;
    /**
     * 供应商订单号
     */
    private String supplierOrderID;

    /**
     * 订单详细
     */
    private List<ProductOrderVO> pOrderVOs;
    /**
     *  购买者身份，1-微店主  2-落地店 3-代理商  4-供应商
     */
    private String identityId;
    /**
     * 卖家名称
     */
    private String supplyerName;
    /**
     * 买家姓名
     */
    private String buyerName;
    /**
     * 买家id
     */
    private Long buyerId;
    /**
     * 身份状态标识
     */
    private int type;
    /**
     * 订单时间
     */
    private String createTimeStr;
    /**
     * 订单状态名称
     */
    private String orderStateName;
    /**
     * 订单类型
     */
    private int orderType;
    /**
     * 订单产品列表
     */
    private List<ProductOrderVO> proList;
  
    /**
     * 0全额、1百分比、2预付金额
     */
    private int bookType;
    /**
     * 支付类型（0发货前付尾款 1发货后付尾款）
     */
    private int paymentType;
     
   
	public int getBookType() {
		return bookType;
	}

	public void setBookType(int bookType) {
		this.bookType = bookType;
	}

	public int getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(int paymentType) {
		this.paymentType = paymentType;
	}

	public List<String> getPayContent() {
		return payContent;
	}

	public void setPayContent(List<String> payContent) {
		this.payContent = payContent;
	}

	public String getIdentityId() {
		return identityId;
	}

	public void setIdentityId(String identityId) {
		this.identityId = identityId;
	}

	public int getOrderType() {
		return orderType;
	}

	public void setOrderType(int orderType) {
		this.orderType = orderType;
	}

	public String getSupplyerName()
    {
        return supplyerName;
    }

    public void setSupplyerName(String supplyerName)
    {
        this.supplyerName = supplyerName;
    }

    public String getBuyerName()
    {
        return buyerName;
    }

    public void setBuyerName(String buyerName)
    {
        this.buyerName = buyerName;
    }

    public String getCreateTimeStr()
    {
        return createTimeStr;
    }

    public void setCreateTimeStr(String createTimeStr)
    {
        this.createTimeStr = createTimeStr;
    }

    public String getOrderStateName()
    {
        return orderStateName;
    }

    public void setOrderStateName(String orderStateName)
    {
        this.orderStateName = orderStateName;
    }

    public List<ProductOrderVO> getProList()
    {
        return proList;
    }

    public void setProList(List<ProductOrderVO> proList)
    {
        this.proList = proList;
    }

    public String getSupplierOrderID()
    {
        return supplierOrderID;
    }

    public void setSupplierOrderID(String supplierOrderID)
    {
        this.supplierOrderID = supplierOrderID;
    }

    public List<ProductOrderVO> getpOrderVOs()
    {
        return pOrderVOs;
    }

    public void setpOrderVOs(List<ProductOrderVO> pOrderVOs)
    {
        this.pOrderVOs = pOrderVOs;
    }

	/**
	 * @return the buyerId
	 */
	public Long getBuyerId() {
		return buyerId;
	}

	/**
	 * @param buyerId the buyerId to set
	 */
	public void setBuyerId(Long buyerId) {
		this.buyerId = buyerId;
	}

	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(int type) {
		this.type = type;
	}
    
}
