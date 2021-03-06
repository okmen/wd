package com.okwei.supplyportal.service;

import com.okwei.bean.domain.TOrderBackTotal;

public interface IConfirmRefundService
{

    /**
     * 确认退款
     * 
     * @param sellerID
     *            卖家微店号
     * @param refundID
     *            退款ID
     * @return
     */
    public void refundPenny(long sellerID,TOrderBackTotal refOrder);
}
