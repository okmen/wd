package com.okwei.service.agent;

import java.util.List;

import com.okwei.bean.domain.DBrandAgentInfo;
import com.okwei.bean.dto.TasteApplyDTO;
import com.okwei.bean.vo.AgentProductVO;
import com.okwei.bean.vo.AgentVO;
import com.okwei.bean.vo.ReturnModel;


public interface IAgentBrandService {

	/**
	 * 获取数据 0独立分销号 1联合分销号
	 * @param type
	 * @return
	 */
	public List<AgentVO> getAgentList(int type) ;
	
	//获取数据 0独立分销号 1联合分销号
	public List<AgentVO> getAgentList(int type,Long weiId);
	
	
	public List<AgentProductVO> getTopAgentList();


	/**create by zlp at 2016/10/11
	 * 查询代理的制度说明
	 * @param brandId
	 * @return
	 */
	DBrandAgentInfo getDBrandAgentInfo(Integer brandId);


	/**
	 * @author zlp created at 2016/10/14
	 * 查询代理区各个分类的产品集合
	 * @param productIds
	 * @param weiid
	 * @param brandid
	 * @return
	 */
	List<AgentProductVO> getAgentProductByPids(List<Long> productIds,
			Long weiid, Integer brandid,String type);


	
	//获取已经领取试用装人数
	
	public int getTasteCount();
	//判断是否已经领取过试用品
	public boolean checkTaste(long weiid,short type);


	/**
	 * 保存试吃申请资料
	 * @author zlp created by 2016/10/17
	 * @param ta
	 * @return
	 */
	ReturnModel saveTasteApply(TasteApplyDTO ta);
	
}
