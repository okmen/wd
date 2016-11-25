package com.okwei.bean.enums;
/**
 * 供应商订单类型
 * @author Administrator
 *
 */
public enum SupplyOrderType {
	/**
	 * 零售 订单（云商通）
	 * */
	Pt(1),
	/**
	 * 零售 订单(批发号)
	 * */
	BatchOrder(8),
	/**
	 * 批发订单（ 批发号）
	 * */
	BatchWholesale(9),
	/**
	 * 预订单
	 * */
	BookOrder(12),
    /*------------------------------------------------*/
    /**
     * 进货单
     */
    Jinhuo(19),
    /**
     * 铺货单
     */
    Puhuo(20),
    /**
     * 零售 订单（平台号）
     */
    RetailPTH(24),
    
    /*----------------*/
    /**
     * 代理区订单（零售）
     */
    RetailAgent(27),
    /**
     * 兑换订单
     */
    Convert(28)
    ;
	private final int step;

	private SupplyOrderType(int step) {

		this.step = step;
	}

	@Override
	public String toString() {
		return String.valueOf(this.step);
	}
}
