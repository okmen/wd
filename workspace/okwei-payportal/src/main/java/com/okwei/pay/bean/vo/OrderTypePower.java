package com.okwei.pay.bean.vo;

import com.okwei.bean.enums.OrderStatusEnum;
import com.okwei.bean.enums.OrderTypeEnum;

/**
 * 商品订单权限分配
 * 
 * @author Administrator
 *
 */
public class OrderTypePower {

    public OrderTypePower(String orderType, Short orderStatus) {
	if (OrderTypeEnum.Pt.toString().equals(orderType)) {
	    // 微店零售 分配货款 分配佣金
	    setAllocateHuoKuan(true);
	    setDeductComminss(true);
	    setAllocateComminss(true);
	} else if (OrderTypeEnum.BatchOrder.toString().equals(orderType)) {
	    // 批发号零售订单 全部都要
	    setAllocateHuoKuan(true);
	    setDeductComminss(true);
	    setAllocateComminss(true);
	    setDeductCut(true);
	    setAllocateCut(true);
	} else if (OrderTypeEnum.BatchWholesale.toString().equals(orderType)) {
	    // 批发号批发订单 分配货款 扣除抽成 分抽成
	    setAllocateHuoKuan(true);
	    setDeductCut(true);
	    setAllocateCut(true);
	} else if (OrderTypeEnum.BookFullOrder.toString().equals(orderType)) {
	    // 批发号预定全额订单 分配货款 扣抽成 分抽成
	    setAllocateHuoKuan(true);
	    setDeductCut(true);
	    setAllocateCut(true);
	} else if (OrderTypeEnum.BookHeadOrder.toString().equals(orderType)) {
	    // 批发号预定订单首款 分配抽成
	    setAllocateCut(true);
	    setStatus(OrderStatusEnum.PayedDeposit.toString());
	} else if (OrderTypeEnum.BookTailOrder.toString().equals(orderType)) {
	    // 批发号预定订单尾款 分配货款 扣除抽成
	    setAllocateHuoKuan(true);
	    setDeductCut(true);
	    // 如果是收货后付尾款 状态为已支付尾款
	    if (orderStatus == Short.parseShort(OrderStatusEnum.Accepted.toString())) {
		setStatus(OrderStatusEnum.Completed.toString());
	    }
	}
	else{//工厂号的批发，预定订单，只分配货款
		this.allocateHuoKuan = true; 
	}
    }

    /**
     * 订单状态
     */
    private String status = OrderStatusEnum.Payed.toString();
    /**
     * 是否分配货款
     */
    private boolean allocateHuoKuan = false;
    /**
     * 是否扣除抽成
     */
    private boolean deductCut = false;
    /**
     * 是否扣除佣金
     */
    private boolean deductComminss = false;
    /**
     * 是否分配抽成
     */
    private boolean allocateCut = false;
    /**
     * 是否分配佣金
     */
    private boolean allocateComminss = false;

    public String getStatus() {
	return status;
    }

    public void setStatus(String status) {
	this.status = status;
    }

    public boolean isAllocateHuoKuan() {
	return allocateHuoKuan;
    }

    public void setAllocateHuoKuan(boolean allocateHuoKuan) {
	this.allocateHuoKuan = allocateHuoKuan;
    }

    public boolean isDeductCut() {
	return deductCut;
    }

    public void setDeductCut(boolean deductCut) {
	this.deductCut = deductCut;
    }

    public boolean isDeductComminss() {
	return deductComminss;
    }

    public void setDeductComminss(boolean deductComminss) {
	this.deductComminss = deductComminss;
    }

    public boolean isAllocateCut() {
	return allocateCut;
    }

    public void setAllocateCut(boolean allocateCut) {
	this.allocateCut = allocateCut;
    }

    public boolean isAllocateComminss() {
	return allocateComminss;
    }

    public void setAllocateComminss(boolean allocateComminss) {
	this.allocateComminss = allocateComminss;
    }
}
