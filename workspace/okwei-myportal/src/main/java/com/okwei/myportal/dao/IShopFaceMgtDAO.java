package com.okwei.myportal.dao;

import com.okwei.bean.domain.PcUserAdnotice;
import com.okwei.common.Limit;
import com.okwei.common.PageResult;
import com.okwei.dao.IBaseDAO;
import com.okwei.myportal.bean.dto.ShopFaceDTO;

public interface IShopFaceMgtDAO extends IBaseDAO {

	PageResult<PcUserAdnotice> getShopFaceImgs(ShopFaceDTO dto, Limit limit);

	Integer getOpenCount(Long weiId);

}
