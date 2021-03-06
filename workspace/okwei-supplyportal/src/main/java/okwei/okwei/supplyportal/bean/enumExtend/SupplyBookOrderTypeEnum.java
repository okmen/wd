package okwei.okwei.supplyportal.bean.enumExtend;

/**
 * 供应商 预订单 状态
 * @author Administrator
 *
 */
public enum SupplyBookOrderTypeEnum {
	
	/**
	 * 待确认
	 */
	Waitting(1),
	/**
	 * 已确定
	 */
	Sured(2),
	/**
	 * 待发货
	 */
	DaiFahuo(3),
	/**
	 * 未付尾款
	 */
	NopayTail(4),
	/**
	 * 交易完成
	 */
	Complete(5);
	
	private final int step;

	private SupplyBookOrderTypeEnum(int step) {

			this.step = step;
	}

	@Override
	public String toString() {
		return String.valueOf(this.step);
	}
}
