package com.okwei.dao.shop;

import java.util.List;

import com.okwei.bean.domain.PProducts;
import com.okwei.bean.domain.PShopClass;
import com.okwei.bean.domain.UAttention;
import com.okwei.bean.domain.UPlatformSupplyerImg;
import com.okwei.bean.domain.UWeiSeller;
import com.okwei.dao.IBaseDAO;

public interface IBasicShopMgtDAO extends IBaseDAO {

	List<PShopClass> getSubShopClass(Integer parentId);

	List<PShopClass> getShopClass(Integer parentId, Long weiId);

	List<UPlatformSupplyerImg> getImgs(Long weiId);

	String getIndustry(Long weiId);

	boolean getIsHaveShopName(long weiID, String shopClassName, Short level, Integer sid);

	// 二级店铺分类下的产品数量
	long productCountBySid(long weiId, long classId);

	long productCountBySid(Long weiId, Long[] scids);

	UAttention getUAttention(Long weiID, Long weiNo);

	UWeiSeller getUWeiSeller(Long weiNo);

	String getNickNameById(Long weiId);

	String getShopImageByWeiId(Long weiNo);

	String getImageById(Long weiId);

	String getBusContentById(Long weiId);

	/**
	 * 
	 * @return
	 */
	List<PProducts> findDbrandProducts(Long weiNo);

}
