package com.okwei.service;

import java.util.List;

import com.okwei.bean.domain.DAdInfo;
import com.okwei.bean.domain.DBrandAgentInfo;
import com.okwei.bean.domain.DBrands;
import com.okwei.bean.dto.SantoMgtDTO;
import com.okwei.bean.dto.TrialStylesDTO;
import com.okwei.bean.vo.PictureAdModel;
import com.okwei.bean.vo.ReturnModel;
import com.okwei.bean.vo.SantoMgtVO;
import com.okwei.bean.vo.product.ProductAuditVO;
import com.okwei.common.Limit;
import com.okwei.common.PageResult;

public interface ISantoMgtService extends IBaseService{
	  /**
     * 查询品牌商管理列表
     * 
     * @param title
     * @param state
     * @return
     */
    public PageResult<SantoMgtVO> findPageSanto(SantoMgtDTO sMgtDTO,Limit limit);
    /**
     * 验证页面返回值
     * 
     * @param title
     * @param state
     * @return
     */
    ReturnModel getSantoDTO(String json);
    /**
     * 验证页面返回值
     * 
     * @param title
     * @param state
     * @return
     */
    ReturnModel getAgentoDTO(String json);
    /**
     * 增加品牌
     * 
     * @param title
     * @param state
     * @return
     */
    ReturnModel saveSantoDTO(SantoMgtVO vo);
    /**
     * 增加代理品牌
     * 
     * @param title
     * @param state
     * @return
     */
    ReturnModel saveDBrandAgentInfo(DBrandAgentInfo dbaif);
    /**
     * 增加品牌
     * 
     * @param title
     * @param state
     * @return
     */
    ReturnModel saveAgento(SantoMgtVO vo);
    /**
     * 查找
     * 
     * @param title
     * @param state
     * @return
     */
    List<DBrands> findDBrands(int stauts,int supType); 
    /**
     * 查询代理商管理列表
     * 
     * @param title
     * @param state
     * @return
     */
    public PageResult<SantoMgtVO> findPageAgent(SantoMgtDTO sMgtDTO,Limit limit);
    /**
     * 审核弹窗数据
     */
    List<ProductAuditVO> findProductAudit(Long productId);
     /**
      * 审核弹窗数据
      */
    ReturnModel saveProductAudit(List<TrialStylesDTO> dto);
    /**
	 * BA01 品牌分销首页广告
	 * yhm（2016-7-19）
	 */
	public List<PictureAdModel> findListAdModels();
	/**
	 * BA01 添加品牌分销首页广告
	 * yhm（2016-7-19）
	*/
	ReturnModel saveDAdInfo(DAdInfo dAdInfo);
}
