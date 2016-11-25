package com.okwei.dao.impl.shoppingcart;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.okwei.bean.domain.PBrandShevle;
import com.okwei.bean.domain.PClassProducts;
import com.okwei.dao.impl.BaseDAO;
import com.okwei.dao.shoppingcart.IBasicPClassProductsMgtDAO;

@Repository
public class BasicPClassProductsMgtDAO extends BaseDAO implements IBasicPClassProductsMgtDAO {

	@Override
	public PClassProducts judageProductIsRacking(long supplierWeiId,
			long productId, short status) {
		// TODO Auto-generated method stub
		String hql = "from PClassProducts p where p.weiId=? and p.productId=? and p.state = ?";
		return super.getNotUniqueResultByHql(hql, supplierWeiId,productId,status);
	}

	@Override
	public List<PClassProducts> getPClassProductsList(List<Long> weiIdList,
			List<Long> productList) {
		// TODO Auto-generated method stub
		if(weiIdList == null || productList == null || weiIdList.size() == 0 || productList.size() == 0)
		{
			return new ArrayList<PClassProducts>();
		}
		String hql = "from PClassProducts where weiId in (:weiIdList) and productId in(:productList)";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("weiIdList", weiIdList);
		map.put("productList", productList);
		return super.findByMap(hql,map);
	}

	@Override
	public List<PBrandShevle> getPBrandShevleList(List<Long> productList) {
		if(productList == null || productList.size() == 0)
		{
			return new ArrayList<PBrandShevle>();
		}
		String hql = "from PBrandShevle where productId in(:productList)";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("productList", productList);
		return super.findByMap(hql,map);
	}

	
}
