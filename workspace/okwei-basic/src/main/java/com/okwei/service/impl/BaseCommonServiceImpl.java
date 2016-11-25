package com.okwei.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.okwei.bean.domain.DBrandSupplier;
import com.okwei.bean.domain.DBrandsInfo;
import com.okwei.bean.domain.UBatchSupplyer;
import com.okwei.bean.domain.UPushMessage;
import com.okwei.bean.domain.UShopInfo;
import com.okwei.bean.domain.USupplyer;
import com.okwei.bean.domain.UWeiSeller;
import com.okwei.bean.enums.SupplierTypeEnum;
import com.okwei.common.SendPushMessage;
import com.okwei.dao.impl.BaseDAO;
import com.okwei.service.IBaseCommonService;
import com.okwei.util.BitOperation;
import com.okwei.util.ImgDomain;
import com.okwei.util.ObjectUtil;
@Service("baseCommonService")
public class BaseCommonServiceImpl extends BaseDAO implements IBaseCommonService {

	public boolean insertSendPushMsg(UPushMessage msg) {
		//加载发送规则
		//发送push
		boolean b=new SendPushMessage().SendMessage(msg);
		if(!b)
			return false;
		//回写数据库
		super.save(msg);
		return true;
	}
	
	

	public String getShopNameByWeiId(Long weiNo) {
		if (weiNo == null) {
			return "";
		}
		UShopInfo shopInfo = super.get(UShopInfo.class, weiNo);
		if (shopInfo != null && shopInfo.getShopName() != null && !"".equals(shopInfo.getShopName()))
			return shopInfo.getShopName();
		USupplyer supplyer = super.get(USupplyer.class, weiNo);
		if (supplyer != null && supplyer.getType() != null) {
			short valueSupply = supplyer.getType();
			if (BitOperation.isSupply(valueSupply, SupplierTypeEnum.YunSupplier)) {
				if (supplyer.getCompanyName() != null && !"".equals(supplyer.getCompanyName())) {
					return supplyer.getCompanyName();
				}
			}
			if (BitOperation.isSupply(valueSupply, SupplierTypeEnum.BatchSupplier)) {
				UBatchSupplyer bsup = super.get(UBatchSupplyer.class, weiNo);
				if (bsup != null && bsup.getShopName() != null && !"".equals(bsup.getShopName())) {
					return bsup.getShopName();
				}
			}
		}
		UWeiSeller seller = super.get(UWeiSeller.class, weiNo);
		if(seller!=null&&seller.getWeiName()!=null&&!"".equals(seller.getWeiId()))
			return seller.getWeiName();
		return "";
	}
	
	public String getContactPhone(Long weiNo){
		USupplyer supplyer=super.get(USupplyer.class, weiNo);
		if(supplyer!=null&&!ObjectUtil.isEmpty(supplyer.getMobilePhone())){
			return supplyer.getMobilePhone();
		}
		DBrandsInfo brandSupplier=super.get(DBrandsInfo.class, weiNo);
		if(brandSupplier!=null&&!ObjectUtil.isEmpty(brandSupplier.getContactPhone())){
			return brandSupplier.getContactPhone();
		}
		UWeiSeller seller=super.get(UWeiSeller.class, weiNo);
		if(seller!=null){
			return seller.getMobilePhone();
		}
		return "";
	}
	
	public String getQQ(Long weiNo){
		USupplyer supplyer=super.get(USupplyer.class, weiNo);
		if (supplyer != null) {
			if (supplyer.getServiceQQ() != null && supplyer.getServiceQQ().length() > 0) {
				String[] qqStr = supplyer.getServiceQQ().split("\\|");
				return qqStr[0];
			}
		}
		DBrandsInfo brandSupplier=super.get(DBrandsInfo.class, weiNo);
		if(brandSupplier!=null&&!ObjectUtil.isEmpty(brandSupplier.getQq())){
			return brandSupplier.getQq();
		}
		UWeiSeller seller=super.get(UWeiSeller.class, weiNo);
		if(seller!=null){
			return seller.getQq();
		}
		return "";
	}
	
	public String getShopImageByWeiId(Long weiNo) {
		if (weiNo == null) {
			return "";
		}
		UShopInfo shopInfo = super.get(UShopInfo.class, weiNo);
		if (shopInfo != null && shopInfo.getShopImg() != null && !"".equals(shopInfo.getShopImg()))
			return ImgDomain.GetFullImgUrl(shopInfo.getShopImg(),24);
		USupplyer supplyer = super.get(USupplyer.class, weiNo);
		if (supplyer != null && supplyer.getType() != null) {
			short valueSupply = supplyer.getType();
			if (BitOperation.isSupply(valueSupply, SupplierTypeEnum.BatchSupplier)) {
				UBatchSupplyer bsup = super.get(UBatchSupplyer.class, weiNo);
				if (bsup != null && bsup.getShopPic() != null && !"".equals(bsup.getShopPic())) {
					return ImgDomain.GetFullImgUrl(bsup.getShopPic(),24);
				}
			}
			if (supplyer.getSupplierLogo() != null && !"".equals(supplyer.getSupplierLogo())) {
				return ImgDomain.GetFullImgUrl(supplyer.getSupplierLogo(),24);
			}
		}
		UWeiSeller seller = super.get(UWeiSeller.class, weiNo);
		if(seller!=null&&seller.getImages()!=null&&!"".equals(seller.getImages()))
			return ImgDomain.GetFullImgUrl(seller.getImages(),24);
		return "";
	}
	
	public String getBusContentByWeiId(Long weiNo) {
		if (weiNo == null) {
			return "";
		}
		UShopInfo shopInfo = super.get(UShopInfo.class, weiNo);
		if (shopInfo != null && shopInfo.getShopBusContent() != null && !"".equals(shopInfo.getShopBusContent()))
			return shopInfo.getShopBusContent();
		USupplyer supplyer = super.get(USupplyer.class, weiNo);
		if(supplyer!=null && !ObjectUtil.isEmpty(supplyer.getBusContent())){
			return  supplyer.getBusContent();
		}
		UBatchSupplyer bsup = super.get(UBatchSupplyer.class, weiNo);
		if (bsup != null && bsup.getBusContent() != null && !"".equals(bsup.getBusContent())) {
			return bsup.getBusContent();
		}
		return "";
	}
	
	public List<UShopInfo> find_shopinfolist(List<Long> weiids){
		Map<String , Object> map=new HashMap<String, Object>();
		map.put("weiids", weiids);
		return super.findByMap("from UShopInfo u where u.weiId in(:weiids)", map);
	}
	
	public String getShopName(List<UShopInfo> shoplist,Long weiid){
		for (UShopInfo uu : shoplist) {
			if(uu.getWeiId().longValue()==weiid){
				return uu.getShopName();
			}
		}
		return super.get(UWeiSeller.class, weiid).getWeiName();
	}
	
	public String getShopImg(List<UShopInfo> shoplist,Long weiid){
		for (UShopInfo uu : shoplist) {
			if(uu.getWeiId().longValue()==weiid){
				return uu.getShopImg();
			}
		}
		return super.get(UWeiSeller.class, weiid).getImages();
	}
	
	public List<UWeiSeller> find_Userlist(List<Long> weiids){
		Map<String , Object> map=new HashMap<String, Object>();
		map.put("weiids", weiids);
		return super.findByMap("from UWeiSeller u where u.weiId in(:weiids)", map);
	}
}
