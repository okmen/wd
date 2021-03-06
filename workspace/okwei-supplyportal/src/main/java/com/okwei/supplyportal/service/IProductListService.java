package com.okwei.supplyportal.service;

import java.util.List;

import com.okwei.service.IBaseService;
import com.okwei.supplyportal.bean.vo.LoginUser;
import com.okwei.supplyportal.bean.vo.ProductManageVO;

public interface IProductListService extends IBaseService{

	/**
	 * 获取产品管理页信息
	 * @return
	 */
	ProductManageVO getManageMsg(LoginUser user, Short productstate,int pagesize,int pageindex,String content);
	/**
	 * 取消推荐产品
	 * @param productid
	 * @return
	 */
	int offTop(Long productid,Long supweiid);
	/**
	 * 产品推荐置顶
	 * @param productid
	 * @return
	 */
	int onTop(Long productid,Long supweiid);
	/**
	 * 移动推荐位置
	 * @param productid
	 * @param updown
	 * @return
	 */
	int moveposition(Long productid,Short updown,Long supweiid);
	/**
	 * 批量置顶产品
	 * @param products
	 * @param supweiid
	 */
	int batchontop(String[] products,Long supweiid);
	/**
	 * 批量下架
	 * @param products
	 * @param supweiid
	 */
	int batchoffshow(String[] products,Long supweiid);
	/**
	 * 批量操作产品。上架、删除
	 * @param products
	 * @param supweiid
	 * @param optype 1 上架 -1 删除
	 * @return
	 */
	int batchoperate(String[] products,Long supweiid,Short optype);
}
