package com.okwei.myportal.service;

import com.okwei.bean.vo.activity.AActivityProductsResult;
import com.okwei.bean.vo.activity.AActivityResult;
import com.okwei.common.PageResult;

public interface IActivityService {

	/**
	 * 限时抢购活动列表
	 * @param weiid
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	public PageResult<AActivityResult> find_Activitylist(Long weiid,int pageIndex,int pageSize);
	/**
	 * 产品通过数量
	 * @param actId
	 * @param sellerId
	 * @return
	 */
	public long count_actProductPass(Long actId, Long sellerId);
	/**
	 * 
	 * @param actId
	 * @param sellerId
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	public 	PageResult<AActivityProductsResult> find_ApplyProductListBySellerID(Long actId, Long sellerId,int pageIndex,int pageSize) ;
	/**
	 * 查找活动Id
	 * @return
	 */
	public Long getgetActCelebration();
}
