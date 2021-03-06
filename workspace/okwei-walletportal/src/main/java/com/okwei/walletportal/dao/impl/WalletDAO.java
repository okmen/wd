package com.okwei.walletportal.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.okwei.bean.domain.UBankCard;
import com.okwei.dao.impl.BaseDAO;
import com.okwei.walletportal.dao.IWalletDAO;

@Repository
public class WalletDAO extends BaseDAO implements IWalletDAO {

	@Override
	public List<UBankCard> getBandCardByWeiId(Long weiId) {
		String hql = "from UBankCard where WeiID = ? ";
		return super.find(hql, weiId);
	}

	@Override
	public UBankCard getByCardId(Long weiId, String bankCard) {
		String hql = "from UBankCard where WeiID = ? and banckCard = ? ";
		List<UBankCard> list = super.find(hql, weiId, bankCard);
		return list != null && list.size() > 0 ? list.get(0) : null;
	}
}
