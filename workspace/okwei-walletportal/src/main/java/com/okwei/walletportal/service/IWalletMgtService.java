package com.okwei.walletportal.service;

import java.util.List;

import com.okwei.bean.domain.OPayOrder;
import com.okwei.bean.domain.UBankCard;
import com.okwei.bean.domain.UCancleOrderAmoutDetail;
import com.okwei.bean.domain.UTradingDetails;
import com.okwei.bean.domain.UTuizhu;
import com.okwei.bean.domain.UWalletDetails;
import com.okwei.common.Limit;
import com.okwei.common.PageResult;
import com.okwei.service.IBaseService;
import com.okwei.walletportal.bean.vo.AccountsVO;
import com.okwei.walletportal.bean.vo.SettleAccountDetailVO;

public interface IWalletMgtService extends IBaseService {

	AccountsVO getAccountInfo(Long weiId);

	List<UBankCard> getBankCards(Long weiId);

	/**
	 * 
	 * @Title: isExistCard 
	 * @Description: 判断是否存在该卡
	 * @param @param weiId
	 * @param @param banckCard
	 * @param @return
	 * @return boolean
	 * @throws
	 */
	boolean isExistCard(Long weiId, String bankCard);

	/**
	 * 获得当前微店主钱包的收支明细
	 * 
	 * @param userId
	 * @param limit
	 * @param detailType
	 * @return
	 */
	PageResult<UWalletDetails> getMyWalletDetailPage(Long userId, Limit limit, int detailType, String fromDate, String toDate);

	/**
	 * 获得当前微店主钱包的结算明细
	 * 
	 * @param userId
	 * @param limit
	 * @param statusType
	 * @param fromDate
	 * @param toDate
	 * @return
	 */
	PageResult<UTradingDetails> getMyWalletSettleAccountPage(Long userId, Limit limit, int statusType, String fromDate, String toDate);

	/**
	 * 获得当前微店主钱包的提现记录
	 * 
	 * @param userId
	 * @param limit
	 * @param statusType
	 * @param fromDate
	 * @param toDate
	 * @return
	 */
	PageResult<UCancleOrderAmoutDetail> getMyWalletWithdrawRecordPage(Long userId, Limit limit, String fromDate, String toDate);

	SettleAccountDetailVO getSettleAccountDetail(UTradingDetails tradeDetail);

	UTradingDetails getTradeDetails(Long detailId, Long weiId);
	/**
	 * 获取退驻申请记录
	 * @param weiId
	 * @param type
	 * @return
	 */
	UTuizhu getTuizhuRecord(long weiId,Short type) throws Exception;

	OPayOrder getPayOrderByCondition(long weiID, Short orderType, Short orderStatus);

}
