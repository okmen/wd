package com.okwei.bean.vo.order;

public class OrderModelReturn {

	    //订单号
		private String orderNo;
		//订单总价
		private Double totalPrice;
		//订单状态: 对应数据库字段状态 0 未支付 1已支付 其他暂时不管
		private int orderState;
		//订单类型
		private int orderType;
		//是否落地店首单
		private int firstShop;
		//获取代金券金额
		private Double coinAmount;
		//收货地址model
		private BAddressVO address;
		
		private int isHaveTicket;//是否获取优惠券
		private Double ticketAmount;//获取优惠券金额
		private Double ticketCount;
		
		public Double getTicketCount() {
			return ticketCount;
		}
		public void setTicketCount(Double ticketCount) {
			this.ticketCount = ticketCount;
		}
		public String getOrderNo() {
			return orderNo;
		}
		public void setOrderNo(String orderNo) {
			this.orderNo = orderNo;
		}
		public Double getTotalPrice() {
			return totalPrice;
		}
		public void setTotalPrice(Double totalPrice) {
			this.totalPrice = totalPrice;
		}
		public int getOrderState() {
			return orderState;
		}
		public void setOrderState(int orderState) {
			this.orderState = orderState;
		}
		
		
		public int getOrderType() {
			return orderType;
		}
		public void setOrderType(int orderType) {
			this.orderType = orderType;
		}
		public int getFirstShop() {
			return firstShop;
		}
		public void setFirstShop(int firstShop) {
			this.firstShop = firstShop;
		}
		public Double getCoinAmount() {
			return coinAmount;
		}
		public void setCoinAmount(Double coinAmount) {
			this.coinAmount = coinAmount;
		}
		public BAddressVO getAddress() {
			return address;
		}
		public void setAddress(BAddressVO address) {
			this.address = address;
		}
		public int getIsHaveTicket() {
			return isHaveTicket;
		}
		public void setIsHaveTicket(int isHaveTicket) {
			this.isHaveTicket = isHaveTicket;
		}
		public Double getTicketAmount() {
			return ticketAmount;
		}
		public void setTicketAmount(Double ticketAmount) {
			this.ticketAmount = ticketAmount;
		}
		
		
}
