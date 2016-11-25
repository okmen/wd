package com.okwei.dao.impl.wallet;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.okwei.bean.domain.UTicketUseLog;
import com.okwei.bean.domain.UWallet;
import com.okwei.bean.domain.UWalletDetails;
import com.okwei.bean.domain.UWeiCoinLog;
import com.okwei.bean.enums.UTicketUseTypeEnum;
import com.okwei.bean.enums.UserAmountType;
import com.okwei.common.Limit;
import com.okwei.common.PageResult;
import com.okwei.dao.impl.BaseDAO;
import com.okwei.dao.wallet.IBasicWalletDao;
import com.okwei.util.DateUtils;

@Repository
public class BasicWalletDaoImpl extends BaseDAO implements IBasicWalletDao {

	@Override
	public Map<String, Object> get_CountCashCoupon(Long weiId) {
		Map<String, Object> map=new HashMap<String, Object>();
		Map<String, Object> params = new HashMap<String, Object>();
/*		 //收入的现金劵个数
		String hql = "select count(coinLogId) from UWeiCoinLog where weiId=:weiId and useType= 1 ";
		params.put("weiId", weiId);
		List<Object> findByMap = super.findByMap(hql, params);
		if (findByMap!=null&&findByMap.size()>0) {
			Object object = findByMap.get(0);
			map.put("shouRu", object);
		}
		//支出的现金劵个数
		hql = "select count(coinLogId) from UWeiCoinLog where weiId=:weiId and useType= 2 ";
		params = new HashMap<String, Object>();
		params.put("weiId", weiId);
		findByMap = super.findByMap(hql, params);
		if (findByMap!=null&&findByMap.size()>0) {
			Object object = findByMap.get(0);
			map.put("zhiChu", object);
		}
		//已过期的现金劵个数
		hql = "select count(coinLogId) from UWeiCoinLog where weiId=:weiId and  state= 2 ";
		params = new HashMap<String, Object>();
		params.put("weiId", weiId);
		findByMap = super.findByMap(hql, params);
		if (findByMap!=null&&findByMap.size()>0) {
			Object object = findByMap.get(0);
			map.put("guoQi", object);
		} 
		
		//已使用的现金劵金额
		hql = "select sum(coinAmount) from UWeiCoinLog where weiId=:weiId and useType= 2 ";
		params = new HashMap<String, Object>();
		params.put("weiId", weiId);
		findByMap = super.findByMap(hql, params);
		if (findByMap!=null&&findByMap.size()>0) {
			Object object = findByMap.get(0);
			map.put("shiYongJingE", object);
		}
		 */
		
		//可使用的现金劵
		String hql = "select totalCoin from UWallet where weiId=:weiId";
		params = new HashMap<String, Object>();
		params.put("weiId", weiId);
		List<Object> findByMap = super.findByMap(hql, params);
		if (findByMap!=null&&findByMap.size()>0) {
			Object object = findByMap.get(0);
			if (object!=null) {
				map.put("weiShiYongJingE", object);
			}else{ 
				map.put("weiShiYongJingE", 0);
			}
		}
		
		//收入  的记录条数
		 hql = "select count(coinLogId) from UWeiCoinLog where weiId=:weiId and useType= 1 and state= 1 and deleted !=1";
		 params = new HashMap<String, Object>();
		 params.put("weiId", weiId);
		 long countByMap = super.countByMap(hql, params);
		 map.put("shouRu", countByMap);
	 
		//支出  的记录条数 
		 hql = "select count(coinLogId) from UWeiCoinLog where weiId=:weiId and useType= 2  and state= 1 and deleted !=1";
		 params = new HashMap<String, Object>();
		 params.put("weiId", weiId);
		 countByMap = super.countByMap(hql, params);
		 map.put("zhiChu", countByMap);
		 
		//过期  的记录条数
		 hql = "select count(coinLogId) from UWeiCoinLog where weiId=:weiId and state= 2 and deleted !=1";
		 params = new HashMap<String, Object>();
		 params.put("weiId", weiId);
		 countByMap =  super.countByMap(hql, params);
		 map.put("guoQi", countByMap);
		 
		return map;
	}

	
	@Override
	public PageResult<UWeiCoinLog> find_CashCoupon(Long weiId, Limit limit,int detailType) {
		String hql = "from UWeiCoinLog where weiId=:weiId  ";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("weiId", weiId);
		switch (detailType) {
		case 1:// 1收入 
			hql+=" and useType= 1 and state= 1 and deleted !=1";
			break;
		case 2://2支出
			hql+=" and useType= 2 and state= 1 and deleted !=1";
			break;
		case 3://3已過期
			hql+=" and state= 2 and deleted !=1";
			break;
		default:
			break;
		}  
		 return super.findPageResultByMap(hql, limit, params);
	}
	
	
	
	
	
	
	public PageResult<UWalletDetails> getMyWalletDetailPage(Long userId,
			Limit limit, int detailType, String fromDate, String toDate) {
		String hql = "from UWalletDetails where 1=1 ";
		Map<String, Object> params = new HashMap<String, Object>();
		if (userId!=null) {
			hql += " and  weiId=:weiId ";
			params.put("weiId", userId);
		}
		// 根据日期进行筛选
		if (fromDate != null && !fromDate.equals("")) {
			fromDate = fromDate+" 00:00:00";
			Date beginTime = DateUtils.format(fromDate, "yyyy-MM-dd HH:mm:ss"); 
			hql += " and createTime>=:fromdate";
			params.put("fromdate",beginTime);
		}
		
		if (toDate != null && !toDate.equals("")) {
			toDate = toDate+ " 23:59:59"; 
			Date endTime = DateUtils.format(toDate, "yyyy-MM-dd HH:mm:ss");
			hql += " and createTime<=:todate";
			params.put("todate",endTime); 
		}
		if (detailType == 1) { // 1表示收入
			hql += " and type in ("
					+ Short.parseShort(UserAmountType.chongzhi.toString())+ ","
					+ Short.parseShort(UserAmountType.classComminsion.toString()) + ","
					+ Short.parseShort(UserAmountType.orderYj.toString()) + ","
					+ Short.parseShort(UserAmountType.rzYj.toString()) + ","
					+ Short.parseShort(UserAmountType.supplyPrice.toString())+ ","
					+ Short.parseShort(UserAmountType.repay.toString())+ ","
					+ Short.parseShort(UserAmountType.reward.toString())+ ","
					+ Short.parseShort(UserAmountType.buyerPrice.toString())+ ")";
		}
		if (detailType == 2) { // 2表示支出
			hql += " and type in ("
					+ Short.parseShort(UserAmountType.tixian.toString()) + ","
					+ Short.parseShort(UserAmountType.monthtax.toString())+ ","
					+ Short.parseShort(UserAmountType.gouwu.toString())+ ","
					+ Short.parseShort(UserAmountType.choudian.toString())+ ","
					+ Short.parseShort(UserAmountType.refund.toString())+ ")";
		}
		if (detailType == 3) { // 3表示返佣
			hql += " and type in ("
					+ Short.parseShort(UserAmountType.repay.toString())+ ")";
		}
		// 根据交易时间进行降序
		hql += " order by createTime desc";
		return super.findPageResultByMap(hql, limit, params);
	}


	public double get_UTicketInfo(Long weiid, String payOrderid){
		String hql=" from UTicketUseLog a where a.orderId=? and a.weiId=? and a.type=?";
		List<UTicketUseLog> list=super.find(hql, payOrderid,weiid,Integer.parseInt(UTicketUseTypeEnum.shouru.toString()));
		double amount=0d;
		if(list!=null&&list.size()>0){
			for (UTicketUseLog uu : list) {
				amount+=uu.getAmount();
			}
		}
		return amount;
	}




}
