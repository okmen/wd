package com.okwei.bean.enums;

/// <summary>
/// 用户金额来源类型
/// </summary>
public enum UserAmountType {
	
	 /**
	  *  订单的佣金
	  */
    orderYj(1),
    /**
     * 认证的佣金
     */
    rzYj(2),
    /**
     *  供应商货款
     */
    supplyPrice(3),
    /**
     * 买家收到退款
     */
    buyerPrice(4),
    /**
     * 分类带来的佣金
     */
    classComminsion(5),
    /**
     * 充值
     */
    chongzhi(6),
    /**
     * 提现
     */
    tixian(7),
    /**
     * 购物
     */
    gouwu(8),
    /**
     * 退款扣除
     */
    refund(9),
    /**
     * 月结扣税
     */
    monthtax(10) ,
    /**
     * 落地店返佣
     */
    repay(11),
    /**
     * 悬赏
     */
    reward(12),
    /**
     * 平台抽点
     */
    choudian(13),
    /**
     * 全返活动现金券
     */
    ticket(14)
    ;


	private final int step; 

    private UserAmountType(int step) { 

         this.step = step; 
    }
    
    @Override
    public String toString()
    {
    	return String.valueOf(this.step);         	
    }
}
