package com.okwei.service.user;

import com.okwei.bean.vo.ReturnModel;
import com.okwei.service.IBaseService;

public interface IBasicWalletService extends IBaseService {
	
	public ReturnModel getMyWeiCoinList(Long weiId,Integer pageIndex,Integer pageSize,Integer status);

	public ReturnModel getMyExchangeCoupon(Long weiId,Integer pageIndex,Integer pageSize,Integer status);
}
