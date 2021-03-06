package com.okwei.myportal.service.impl;

import org.springframework.stereotype.Service;

import net.sf.ehcache.search.impl.BaseResult;

import com.okwei.bean.domain.UBatchSupplyer;
import com.okwei.bean.domain.UBrandSupplyer;
import com.okwei.bean.domain.UPlatformSupplyer;
import com.okwei.bean.domain.USupplyer;
import com.okwei.bean.domain.UYunSupplier;
import com.okwei.myportal.bean.enums.BaseResultStateEnum;
import com.okwei.myportal.bean.vo.BaseResultVO;
import com.okwei.myportal.service.ICustomerQQService;
import com.okwei.service.impl.BaseService;

@Service
public class CustomerQQService extends BaseService implements ICustomerQQService {

	@Override
	public String[] getCustomerQQs(long weiID) {
		if(weiID <1){
			return null;
		}
		USupplyer supplyer = super.getById(USupplyer.class, weiID);
		if(supplyer !=null && supplyer.getServiceQQ() !=null && !"".equals(supplyer.getServiceQQ())){
			return supplyer.getServiceQQ().split("\\|");
		}
		
		return null;
	}

	@Override
	public BaseResultVO saveCustomerQQ(long weiID, String QQs) {
		BaseResultVO result = new BaseResultVO();
		result.setState(BaseResultStateEnum.Failure);
		result.setMessage("参数错误！");
		if(weiID <1){
			return result;
		}
		USupplyer supplyer = super.getById(USupplyer.class, weiID);
		if(supplyer !=null){
			supplyer.setServiceQQ(QQs);
			if(super.update(supplyer)){
				result.setState(BaseResultStateEnum.Success);
				//同步到各子表
				/*UPlatformSupplyer platform = super.getById(UPlatformSupplyer.class, weiID);
				if(platform !=null){
					platform.setServiceQq(QQs);
					super.update(platform);
				}
				UBrandSupplyer brand = super.getById(UBrandSupplyer.class, weiID);
				if(brand !=null){
					brand.setServiceQq(QQs);
					super.update(brand);
				}
				UYunSupplier yun = super.getById(UYunSupplier.class, weiID);
				if(yun !=null){
					yun.setServiceQq(QQs);
					super.update(yun);
				}
				UBatchSupplyer batch = super.getById(UBatchSupplyer.class, weiID);
				if(batch !=null){
					batch.setQq(QQs);
					super.update(batch);
				}*/
			}
		}else{
			result.setMessage("数据查询失败！请稍后重试！");
		}
			
		return result;
	}

}
