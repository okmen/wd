package com.okwei.myportal.bean.vo;

import java.util.List;

public class LogisticsVO
{
    /**
     * 物流名称
     */
    private String longisticsName;
    /**
     * 物流单号
     */
    private String logisticsNo;
    
    private Long sellerWeiid;
    private Long buyerWeiid;
	/**
	 * 订单状态
	 */
	private Integer orderState;

    /**
     * 物流跟踪
     */
    private List<String> tailList;

    public String getLongisticsName()
    {
        return longisticsName;
    }

    public void setLongisticsName(String longisticsName)
    {
        this.longisticsName = longisticsName;
    }

    public String getLogisticsNo()
    {
        return logisticsNo;
    }

    public void setLogisticsNo(String logisticsNo)
    {
        this.logisticsNo = logisticsNo;
    }

    public List<String> getTailList()
    {
        return tailList;
    }

    public void setTailList(List<String> tailList)
    {
        this.tailList = tailList;
    }

	public Long getSellerWeiid() {
		return sellerWeiid;
	}

	public void setSellerWeiid(Long sellerWeiid) {
		this.sellerWeiid = sellerWeiid;
	}

	public Long getBuyerWeiid() {
		return buyerWeiid;
	}

	public void setBuyerWeiid(Long buyerWeiid) {
		this.buyerWeiid = buyerWeiid;
	}

	public Integer getOrderState() {
		return orderState;
	}

	public void setOrderState(Integer orderState) {
		this.orderState = orderState;
	}

    
}
