package com.okwei.myportal.service;

import com.okwei.common.Limit;
import com.okwei.common.PageResult;
import com.okwei.myportal.bean.dto.DistributionOrderDTO;
import com.okwei.myportal.bean.vo.ProductOrderVO;
import com.okwei.myportal.bean.vo.SaleOrderVO;
import com.okwei.myportal.bean.vo.SupplyOrderVO;

public interface IDistributionOrderService {
	
	/**
	 * 
	 * @param weiID
	 * @return
	 */
	public PageResult<SaleOrderVO> getMyselfSellOrder(long weiID,DistributionOrderDTO param,Limit limit);
}
