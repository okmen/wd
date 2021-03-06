package com.okwei.walletportal.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.okwei.bean.domain.UPublicBanks;
import com.okwei.bean.vo.LoginUser;
import com.okwei.service.impl.BaseService;
import com.okwei.walletportal.bean.enums.PublicBanksStatusEnum;
import com.okwei.walletportal.dao.ICompanyAccountDAO;
import com.okwei.walletportal.service.ICompanyAccountService;

 	

@Service
public class CompanyAccountService extends BaseService implements ICompanyAccountService {
	@Autowired
	private ICompanyAccountDAO companyBankCardDAO;

	@Override
	public UPublicBanks getCompanyAccount(long weiId) throws Exception {
		return companyBankCardDAO.getCompanyAccountByWeiId(weiId);
	}

	@Override
	public String addOrUpdateCompanyAccount(UPublicBanks bc,LoginUser user,boolean flag)
			throws Exception {
		String msg = "操作失败";
		if (user.getYunS() == 1) {
			bc.setIsYunSupplier((short)1);
		} else {
			bc.setIsYunSupplier((short)0);
		}
		if (user.getBatchS() == 1) {
			bc.setIsBatchSupplier((short)1);
		} else {
			bc.setIsBatchSupplier((short)0);
		}
		bc.setWeiId(user.getWeiID());
		bc.setCreateTime(new Date());
		//修改
		if (flag) {
			companyBankCardDAO.update(bc);
			msg = "修改成功";
		} else {
			bc.setState(Short.valueOf(PublicBanksStatusEnum.Applying.toString()));
			companyBankCardDAO.save(bc);
			msg = "添加成功";
		}
		return msg;
	}

	@Override
	public boolean getPublicBanksPassCount(long weiId) throws Exception {
		String hql = "select count(*) from UPublicBanksLog where pid in (select pid from UPublicBanks where weiId=?) and state=?";
		Long count =  companyBankCardDAO.count(hql, weiId,Short.valueOf(PublicBanksStatusEnum.Pass.toString()));
		if (count != null && count > 0) {
			return true;
		}
		return false;
	}
	
	
}
