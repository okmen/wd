package com.okwei.walletportal.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.okwei.bean.domain.OPayOrder;
import com.okwei.bean.domain.UBankCard;
import com.okwei.bean.domain.UBatchSupplyer;
import com.okwei.bean.domain.UCancleOrderAmoutDetail;
import com.okwei.bean.domain.UTradingDetails;
import com.okwei.bean.domain.UTuizhu;
import com.okwei.bean.domain.UVerifier;
import com.okwei.bean.domain.UWallet;
import com.okwei.bean.domain.UWalletDetails;
import com.okwei.bean.domain.UYunSupplier;
import com.okwei.bean.enums.VerifierTypeEnum;
import com.okwei.common.Limit;
import com.okwei.common.PageResult;
import com.okwei.dao.wallet.IBasicWalletDao;
import com.okwei.service.impl.BaseService;
import com.okwei.walletportal.bean.enums.SupplierStatusEnum;
import com.okwei.walletportal.bean.vo.AccountsVO;
import com.okwei.walletportal.bean.vo.SettleAccountDetailVO;
import com.okwei.walletportal.dao.IWalletDAO;
import com.okwei.walletportal.dao.IWalletMgtDAO;
import com.okwei.walletportal.service.IWalletMgtService;

@Service
public class WalletMgtService extends BaseService implements IWalletMgtService {

	@Autowired
	private IWalletDAO walletDAO;

	@Autowired
	private IWalletMgtDAO walletMgtDAO;
	@Autowired
	private IBasicWalletDao basicWalletDao;
	@Override
	public AccountsVO getAccountInfo(Long weiId) {
		AccountsVO vo = new AccountsVO();
		UWallet wallet = super.getById(UWallet.class, weiId);
		if (null != wallet) {
			vo.setBalance(wallet.getBalance());
			vo.setMarkup(wallet.getAccountNot());
			vo.setWithdraw(wallet.getAccountIng());
		}

		/* 保证金由以下三部分构成 */
		double deposit = (double) 0;
		UYunSupplier ys = super.getById(UYunSupplier.class, weiId);
		if (null != ys && null != ys.getBond()) {
			deposit += ys.getBond();
		}

		UBatchSupplyer bs = super.getById(UBatchSupplyer.class, weiId);
		if (null != bs && bs.getStatus() != null && (bs.getStatus() == Short.parseShort(SupplierStatusEnum.PayIn.toString()))) {
			if (bs.getBond() != null) {
				deposit += bs.getBond();
			}
		}

		UVerifier vf = super.getById(UVerifier.class, weiId);
		if (null != vf) {
			if (null != vf.getType() && ((short) vf.getType() & Short.parseShort(VerifierTypeEnum.percent.toString())) > 1) {
				deposit += 3000; // 临时
			}
		}
		vo.setDeposit(deposit);
		return vo;
	}

	@Override
	public boolean isExistCard(Long weiId, String bankCard) {
		UBankCard bc = walletDAO.getByCardId(weiId, bankCard);
		return bc != null ? true : false;
	}

	@Override
	public List<UBankCard> getBankCards(Long weiId) {
		return walletDAO.getBandCardByWeiId(weiId);
	}

	@Override
	public PageResult<UWalletDetails> getMyWalletDetailPage(Long userId, Limit limit, int detailType, String fromDate, String toDate) {
		PageResult<UWalletDetails> page = basicWalletDao.getMyWalletDetailPage(userId, limit, detailType, fromDate, toDate);
		return page;
	}

	@Override
	public PageResult<UTradingDetails> getMyWalletSettleAccountPage(Long userId, Limit limit, int statusType, String fromDate, String toDate) {
		PageResult<UTradingDetails> page = walletMgtDAO.getMyWalletSettleAccountPage(userId, limit, statusType, fromDate, toDate);
		return page;
	}

	@Override
	public PageResult<UCancleOrderAmoutDetail> getMyWalletWithdrawRecordPage(Long userId, Limit limit, String fromDate, String toDate) {
		PageResult<UCancleOrderAmoutDetail> page = walletMgtDAO.getMyWalletWithdrawRecordPage(userId, limit, fromDate, toDate);
		return page;
	}

	@Override
	public SettleAccountDetailVO getSettleAccountDetail(UTradingDetails tradeDetail) {
		return walletMgtDAO.getSettleAccountDetail(tradeDetail);
	}

	@Override
	public UTradingDetails getTradeDetails(Long detailId, Long weiId) {
		return walletMgtDAO.getTradeDetails(detailId, weiId);
	}

	@Override
	public UTuizhu getTuizhuRecord(long weiId, Short type) throws Exception{
		return walletMgtDAO.getTuizhuRecord(weiId, type);
	}

	@Override
	public OPayOrder getPayOrderByCondition(long weiID, Short orderType,
			Short orderStatus) {
		return walletMgtDAO.getPayOrderByCondition(weiID, orderType, orderStatus);
	}
}
