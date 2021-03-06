package com.okwei.myportal.service;

import java.util.List; 

import com.okwei.bean.domain.USupplyDemand; 
import com.okwei.bean.dto.user.ApplyAgentDTO;
import com.okwei.bean.vo.ReturnModel;
import com.okwei.myportal.bean.vo.AgentPayVO;
import com.okwei.myportal.bean.vo.DemandAreaVO;

public interface IAgentApplyService
{
    // 获取招商需求列表
    List<USupplyDemand> getDemandList(long supplyID);
    // 根据招商需求获取代理的城市
    List<DemandAreaVO> getDemandAreaList(int demandID);  
    //平台号/品牌号添加代理商
    ReturnModel insertAgentBySupplyID(long supplyID,ApplyAgentDTO dto);
    /**
     * 获取支付金额
     * @param applyID
     * @return
     */
    AgentPayVO getAgentPayVO(Long supplyID,Integer applyID); 
}
