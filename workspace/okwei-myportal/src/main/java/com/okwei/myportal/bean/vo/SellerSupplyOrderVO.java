package com.okwei.myportal.bean.vo;

import java.util.List;
 

public class SellerSupplyOrderVO extends BaseOrder{
    
    /**
     * 卖家名称
     */
    private String supplyerName;
    /**
     * 买家姓名
     */
    private String buyerName;
    /**
     * 订单时间
     */
    private String createTimeStr;
    /**
     * 订单状态名称
     */
    private String orderStateName;
    
    /**
     * 订单产品列表
     */
    private List<ProductOrderVO> proList;
    
    public List<ProductOrderVO> getProList() {
            return proList;
    }
    public void setProList(List<ProductOrderVO> proList) {
            this.proList = proList;
    }
    
    public String getOrderStateName() {
            return orderStateName;
    }
    public void setOrderStateName(String orderStateName) {
            this.orderStateName = orderStateName;
    }
    public String getBuyerName() {
            return buyerName;
    }
    public void setBuyerName(String buyerName) {
            this.buyerName = buyerName;
    }

    public String getSupplyerName() {
            return supplyerName;
    }
    public void setSupplyerName(String supplyerName) {
            this.supplyerName = supplyerName;
    }
    public String getCreateTimeStr() {
            return createTimeStr;
    }
    public void setCreateTimeStr(String createTimeStr) {
            this.createTimeStr = createTimeStr;
    }

}
