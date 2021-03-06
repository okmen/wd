package com.okwei.myportal.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.okwei.common.Limit;
import com.okwei.common.PageResult;
import com.okwei.myportal.bean.dto.DistributionOrderDTO;
import com.okwei.myportal.bean.vo.ProductOrderVO;
import com.okwei.myportal.bean.vo.SaleOrderVO;
import com.okwei.myportal.dao.IDistributionOrderDAO;
import com.okwei.myportal.dao.impl.DistributionOrderDAO;
import com.okwei.myportal.service.IDistributionOrderService;
import com.okwei.service.impl.BaseService;

@Service
public class DistributionOrderService extends BaseService implements IDistributionOrderService
{

    @Autowired
    IDistributionOrderDAO distributionOrderDAO;

    @Override
    public PageResult<SaleOrderVO> getMyselfSellOrder(long weiID,DistributionOrderDTO param,Limit limit)
    {

        return distributionOrderDAO.getDistributionOrders(weiID,param,limit);
    }

}
