package com.okwei.dao.impl.shoppingcart;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.ar.ArabicAnalyzer;
import org.springframework.stereotype.Repository;

import com.okwei.bean.domain.PProducts;
import com.okwei.bean.domain.TShoppingCar;
import com.okwei.dao.shoppingcart.IBasicTShoppingCarMgtDAO;
import com.okwei.dao.impl.BaseDAO;

@Repository
public class BasicTShoppingCarMgtDAO extends BaseDAO implements IBasicTShoppingCarMgtDAO {

	@Override
	public long saveTShoppingCar(TShoppingCar tShoppingCar) {
		// TODO Auto-generated method stub
		return (long) super.save(tShoppingCar);
	}

	@Override
	public List<TShoppingCar> getTShoppingCarList(long weiId, long proNum,
			short buyType, long supplierWeiId) {
		// TODO Auto-generated method stub
		String hql = "from TShoppingCar t where t.weiId=? and t.proNum=? and t.buyType=? and t.supplierWeiId=?";// 先查询这个商品在不在购物车
		return super.find(hql, weiId,proNum,buyType,supplierWeiId);
	}

	@Override
	public void updateTShoppingCar(TShoppingCar tShoppingCar) {
		// TODO Auto-generated method stub
		super.update(tShoppingCar);
	}

	@Override
	public TShoppingCar getTShoppingCar(long weiId, long styleId,
			short buyType, long sellerWeiId, long supplierWeiId,short source) {
		// TODO Auto-generated method stub
		// 先查询这个商品在不在购物车
		String hql = "from TShoppingCar t where t.weiId=? and t.styleId=? and t.buyType=? and t.sellerWeiId=?"
		+ " and t.supplierWeiId=? and t.source = ? order by createTime desc";
		return super.getNotUniqueResultByHql(hql, weiId,styleId,buyType,sellerWeiId,supplierWeiId,source);
	}
	
	public List<TShoppingCar>  find_TShoppingCar(long weiId, long styleId) {
		// TODO Auto-generated method stub
		// 先查询这个商品在不在购物车
		String hql = " from TShoppingCar t where t.weiId=? and t.styleId=? ";
		return super.find(hql, weiId,styleId);
	}

	@Override
	public List<TShoppingCar> getTShoppingCarList(long weiId,short status) {
		// TODO Auto-generated method stub
		String hql = " from TShoppingCar t where t.weiId=? and t.status = ? order by t.createTime desc";// 查询语句
		return super.find(hql, weiId,status);
	}
	@Override
	public int updateTShoppingCarStatus(long sCID,short status) {
		// TODO Auto-generated method stub
		String hql = "update TShoppingCar set status = ? where scid = ?";
		return super.executeHql(hql,status,sCID);
	}

	@Override
	public int delTShoppingCar(long scid, long weiId) {
		// TODO Auto-generated method stub
		String hql = "delete from TShoppingCar where scid = ? and weiId = ?";
		return super.executeHql(hql, scid,weiId);
	}

	@Override
	public TShoppingCar getTShoppingCar(long scid, long weiId,short status) {
		// TODO Auto-generated method stub
		String hql = "from TShoppingCar where scid = ? and weiId = ? and status = ?"; 
		return super.getUniqueResultByHql(hql, scid,weiId,status);
	}

	@Override
	public int updateTShoppingCar(long scid, int count) {
		// TODO Auto-generated method stub
		return	super.executeHql("update TShoppingCar t set t.count = ? where t.scid  = ? ",count,scid);
	}

	@Override
	public int updateTShoppingCarPrice(List<Long> scidList,double price) {
		// TODO Auto-generated method stub
		String hql = "update TShoppingCar set price = :price  where scid in(:scidList)";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("price", price);
		map.put("scidList", scidList);
		return super.executeHqlByMap(hql, map);
	}

	@Override
	public List<TShoppingCar> getTShoppingCarList(long weiId) {
		// TODO Auto-generated method stub
		String hql = " from TShoppingCar t where t.weiId=? order by t.createTime desc";// 查询语句
		return super.find(hql, weiId);
	}

	@Override
	public List<Object[]> getTShoppingCarCountByState(long weiId,short status) {
		// TODO Auto-generated method stub
//		String sql = " select count(scid) from T_ShoppingCar where weiId = ? and status = ?";
		String sql = " select count(scid) from T_ShoppingCar where weiId = ?";
		return super.queryBySql(sql, weiId);
	}

	@Override
	public List<TShoppingCar> getTShoppingCarList(long weiId,
			List<Long> scidList) {
		// TODO Auto-generated method stub
		String hql = "from TShoppingCar  where scid in(:scidList) and weiId =:weiId ";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("scidList", scidList);
		map.put("weiId", weiId);
		return super.findByMap(hql,map);
	}

	@Override
	public PProducts getProductByID(Long productID) {
		// TODO 自动生成的方法存根
		return super.get(PProducts.class, productID);
	}
}
