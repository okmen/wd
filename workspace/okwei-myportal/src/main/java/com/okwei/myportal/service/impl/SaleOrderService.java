package com.okwei.myportal.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.okwei.common.Limit;
import com.okwei.common.PageResult;
import com.okwei.myportal.bean.dto.DistributionOrderDTO;
import com.okwei.myportal.bean.vo.SaleOrderVO;
import com.okwei.myportal.dao.IDistributionOrderDAO;
import com.okwei.myportal.dao.impl.DistributionOrderDAO;
import com.okwei.myportal.service.IDistributionOrderService;
import com.okwei.myportal.service.ISaleOrderService;
import com.okwei.util.ObjectUtil;

@Service
public class SaleOrderService implements ISaleOrderService
{
    @Autowired
    IDistributionOrderDAO distributionOrderDAO;

    @Override
    public PageResult<SaleOrderVO> getSaleOrderVOList(long weiid,Limit limit,String title,String supName,Date start,Date end)
    {
        if(ObjectUtil.isEmpty(title) && ObjectUtil.isEmpty(supName) && start == null && end == null)
        {
            return distributionOrderDAO.getDistributionOrders(weiid,null,limit);
        }
        DistributionOrderDTO dto = new DistributionOrderDTO();
        if(!ObjectUtil.isEmpty(title))
        {
            dto.setProductTitle(title);
        }
        if(!ObjectUtil.isEmpty(supName))
        {
            dto.setCompanyName(supName);
        }
        if(start != null)
        {
            dto.setBeginDate(start);
        }
        if(end != null)
        {
            dto.setEndDate(end);
        }
        return distributionOrderDAO.getDistributionOrders(weiid,dto,limit);
    }

}
