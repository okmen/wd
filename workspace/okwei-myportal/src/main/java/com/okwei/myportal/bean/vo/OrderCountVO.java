package com.okwei.myportal.bean.vo;

public class OrderCountVO
{
    /**
     * 零售数量
     */
    private int retailCount;
    /**
     * 批发数量
     */
    private int wholesaleCount;
    /**
     * 预定数量
     */
    private int reserveCount;
    /**
     * 等待付款数量
     */
    private int waitPayCount;
    /**
     * 等待发货数量
     */
    private int waitShipmentsCount;
    /**
     * 等待收货
     */
    private int waitReceivCount;
    
    /**
     * 退款中的数量
     */
    private int refundCount;

    /**
     * 交易完成数量
     */
    private int completeCount;
    
    /**
     * 待确认的数量
     */
    private int confirmCount;
    
    public int getRetailCount()
    {
        return retailCount;
    }

    public void setRetailCount(int retailCount)
    {
        this.retailCount = retailCount;
    }

    public int getReserveCount()
    {
        return reserveCount;
    }

    public void setReserveCount(int reserveCount)
    {
        this.reserveCount = reserveCount;
    }

    public int getWholesaleCount()
    {
        return wholesaleCount;
    }

    public void setWholesaleCount(int wholesaleCount)
    {
        this.wholesaleCount = wholesaleCount;
    }

    public int getWaitPayCount()
    {
        return waitPayCount;
    }

    public void setWaitPayCount(int waitPayCount)
    {
        this.waitPayCount = waitPayCount;
    }

    public int getWaitShipmentsCount()
    {
        return waitShipmentsCount;
    }

    public void setWaitShipmentsCount(int waitShipmentsCount)
    {
        this.waitShipmentsCount = waitShipmentsCount;
    }

    public int getWaitReceivCount()
    {
        return waitReceivCount;
    }

    public void setWaitReceivCount(int waitReceivCount)
    {
        this.waitReceivCount = waitReceivCount;
    }

    public int getRefundCount() {
	return refundCount;
    }

    public void setRefundCount(int refundCount) {
	this.refundCount = refundCount;
    }

    public int getCompleteCount() {
	return completeCount;
    }

    public void setCompleteCount(int completeCount) {
	this.completeCount = completeCount;
    }

    public int getConfirmCount() {
	return confirmCount;
    }

    public void setConfirmCount(int confirmCount) {
	this.confirmCount = confirmCount;
    }

}
