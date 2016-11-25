package com.okwei.service.impl.product;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.okwei.bean.domain.DBrandsInfo;
import com.okwei.bean.domain.PBrand;
import com.okwei.bean.domain.PBrandShevle;
import com.okwei.bean.domain.PClassProducts;
import com.okwei.bean.domain.PParamKey;
import com.okwei.bean.domain.PParamValues;
import com.okwei.bean.domain.PPostAgeModel;
import com.okwei.bean.domain.PProductAssist;
import com.okwei.bean.domain.PProductBatchPrice;
import com.okwei.bean.domain.PProductClass;
import com.okwei.bean.domain.PProductClassTemp;
import com.okwei.bean.domain.PProductImg;
import com.okwei.bean.domain.PProductKeyWords;
import com.okwei.bean.domain.PProductParamKv;
import com.okwei.bean.domain.PProductRecordMsg;
import com.okwei.bean.domain.PProductRelation;
import com.okwei.bean.domain.PProductSellKey;
import com.okwei.bean.domain.PProductSellParamKey;
import com.okwei.bean.domain.PProductSellParamModel;
import com.okwei.bean.domain.PProductSellParamValues;
import com.okwei.bean.domain.PProductSellValue;
import com.okwei.bean.domain.PProductStyleKv;
import com.okwei.bean.domain.PProductStyles;
import com.okwei.bean.domain.PProductSup;
import com.okwei.bean.domain.PProducts;
import com.okwei.bean.domain.PPushShelves;
import com.okwei.bean.domain.PShelverCount;
import com.okwei.bean.domain.PShevleBatchPrice;
import com.okwei.bean.domain.PShopClass;
import com.okwei.bean.domain.TProductIndex;
import com.okwei.bean.domain.UBatchSupplyer;
import com.okwei.bean.domain.UDemandProduct;
import com.okwei.bean.domain.UOwnerMessage;
import com.okwei.bean.domain.UPlatformSupplyer;
import com.okwei.bean.domain.USupplyDemand;
import com.okwei.bean.domain.USupplyer;
import com.okwei.bean.domain.UUserAssist;
import com.okwei.bean.domain.UYunSupplier;
import com.okwei.bean.enums.AgentStatusEnum;
import com.okwei.bean.enums.ApplyAgentStatusEnum;
import com.okwei.bean.enums.MsgType;
import com.okwei.bean.enums.OrderFrom;
import com.okwei.bean.enums.ProductBType;
import com.okwei.bean.enums.ProductPublishTypeEnum;
import com.okwei.bean.enums.ProductShelveStatu;
import com.okwei.bean.enums.ProductStatusEnum;
import com.okwei.bean.enums.ProductSubStatusEnum;
import com.okwei.bean.enums.PubProductTypeEnum;
import com.okwei.bean.enums.PushTypeEnum;
import com.okwei.bean.enums.ShelveType;
import com.okwei.bean.enums.SupplierStatusEnum;
import com.okwei.bean.enums.SupplyChannelTypeEnum;
import com.okwei.bean.vo.KeyValue;
import com.okwei.bean.vo.PushShelvesContentVO;
import com.okwei.bean.vo.ResultMsg;
import com.okwei.bean.vo.ReturnModel;
import com.okwei.bean.vo.ReturnStatus;
import com.okwei.bean.vo.UserInfo;
import com.okwei.bean.vo.order.ProductSellValueVO;
import com.okwei.bean.vo.order.ProductStyleKVVO;
import com.okwei.bean.vo.product.AttributeValueVO;
import com.okwei.bean.vo.product.BatchPriceVO;
import com.okwei.bean.vo.product.P_PrductsClass;
import com.okwei.bean.vo.product.P_PrductsClassList;
import com.okwei.bean.vo.product.PostAgeModelVO;
import com.okwei.bean.vo.product.ProductAttributeVO;
import com.okwei.bean.vo.product.ProductEditInfo;
import com.okwei.bean.vo.product.ProductParam;
import com.okwei.bean.vo.product.ProductParamVO;
import com.okwei.bean.vo.product.ProductSellKeyVO;
import com.okwei.bean.vo.product.ProductSellParam;
import com.okwei.bean.vo.product.ProductStylesVO;
import com.okwei.common.CommonMethod;
import com.okwei.common.Limit;
import com.okwei.common.PageResult;
import com.okwei.dao.IBaseDAO;
import com.okwei.dao.product.IBaseProductDao;
import com.okwei.service.impl.BaseService;
import com.okwei.service.product.IBasicProductService;
import com.okwei.util.BitOperation;
import com.okwei.util.ImgDomain;
import com.okwei.util.ParseHelper;
import com.okwei.util.SellAttrMapUtil;
import com.okwei.util.StringHelp;
import com.okwei.util.StringReverse;

@Service
public class BasicProductService extends BaseService implements IBasicProductService {
	
	@Autowired
	private IBaseDAO baseDAO;
	@Autowired
	private IBaseProductDao baseProductDao;
	
	@Override
	public boolean isSaleProduct(Long weiNo) {
		if(weiNo == null)
			return false;
		USupplyer us = baseDAO.get(USupplyer.class, weiNo); //userDao.get(suhql, gys);
		if (us != null && us.getType()!=null&& us.getType() >0) {
			return true;
		}
		return false;
	}
	
	public <T> boolean IsListEqual(List<T> ListA, List<T> ListB) {
		if (ListA.size() != ListB.size())
			return false;
		for (T item : ListA) {
			if (!ListB.contains(item))
				return false;
		}
		return true;
	}
	
	public boolean isNoPayShop(Long weiNo) {
		if(weiNo == null)
			return false;
		boolean b=false;
		UYunSupplier ysup = baseDAO.get(UYunSupplier.class, weiNo);
		if (ysup != null) {
			if(ysup.getStatus()!=null && ysup.getStatus()==3)
			{
				b= true;
			}
		}
		UBatchSupplyer bsup = baseDAO.get(UBatchSupplyer.class, weiNo);
		if (bsup != null) {
			if(bsup.getStatus()!=null && bsup.getStatus()==3)
			{
				b= true;
			}
		}
		return b;
	}
	
	public UUserAssist updateUserAssist(Long weiid)
	{
		//通过微店号查询辅助表
		String hql=" from UUserAssist p where p.weiId=?";
		Object[] b=new Object[1];
		b[0]=weiid;
		UUserAssist userassit = baseDAO.getUniqueResultByHql(hql, b);
		//如果改微店还没上架，就想辅助表添加改用户使用上架功能时的个人排序
		hql = " from PShelverCount";
		PShelverCount psc = baseDAO.getUniqueResultByHql(hql,null);
		if(psc == null)
		{
			psc = new PShelverCount();
			psc.setCount(0L);
		}
		if(userassit==null){
			UUserAssist ua = new UUserAssist();
			ua.setWeiId(weiid);
			ua.setWeiIdSort(psc.getCount()+1);
			baseDAO.save(ua);
		} else {
			userassit.setWeiId(weiid);
			userassit.setWeiIdSort(psc.getCount()+1);
			baseDAO.update(userassit);
		}
		psc.setCount(psc.getCount()+1);
		//更新记录表，数量加1
		baseDAO.saveOrUpdate(psc);
		return userassit;
	}
	
	public void setShelveMyself(PProducts pro, List<PProductBatchPrice> libp) {

		String hql = "from PClassProducts p where p.weiId=? and p.productId=? and p.supplierweiId=?";
		Object[] b = new Object[3];
		b[0] = pro.getSupplierWeiId();
		b[1] = pro.getProductId();
		b[2] = pro.getSupplierWeiId();
		PClassProducts pcp = baseDAO.getUniqueResultByHql(hql, b);
		if (pcp == null)
			pcp = new PClassProducts();
		pcp.setProductId(pro.getProductId());
		pcp.setClassId(Long.parseLong(pro.getSid().toString()));
		pcp.setIsSendMyself((short) 1);
		pcp.setSort((short) -1);
		pcp.setReason("");
		pcp.setState((short) 1);
		pcp.setCreateTime(new Date());
		pcp.setWeiId(pro.getSupplierWeiId());
		pcp.setSupplierweiId(pro.getSupplierWeiId());
		pcp.setShelvweiId(pro.getSupplierWeiId());
		pcp.setSendweiId(pro.getSupplierWeiId());
		UUserAssist userassist = updateUserAssist(pro.getSupplierWeiId());
		pcp.setWeiIdsort(userassist.getWeiIdSort());
		if (pro.getSupperType() != null && pro.getSupperType() > 0)
			pcp.setType((short) 1);
		else {
			if (isNoPayShop(pro.getSupplierWeiId()))// 如果不是正式商家发布产品的时候为3
			{
				pcp.setType((short) 3);
			} else {
				pcp.setType((short) 2);// 普通微店主发布为2
			}
		}
		baseDAO.saveOrUpdate(pcp);
		long pcpid = pcp.getId();
		String dbhql = " delete from PShevleBatchPrice p where p.id=?";
		Object[] bb = new Object[1];
		bb[0] = pcpid;
		baseDAO.executeHql(dbhql, bb);// 删除批发表
		if (libp != null && libp.size() > 0) {
			for (int i = 0; i < libp.size(); i++) {
				PProductBatchPrice pbp = libp.get(i);
				PShevleBatchPrice batchprice = new PShevleBatchPrice();
				batchprice.setWeiIdsort(userassist.getWeiIdSort());
				batchprice.setId(pcpid);
				batchprice.setCount(pbp.getCount() == null ? 0 : pbp.getCount());
				batchprice.setPrice(pbp.getPirce() == null ? 0.00 : pbp.getPirce());
				baseDAO.save(batchprice);
			}
		}
		return;
	}
	
	public void setShelveMyself(PProducts pro) {

		String hql = "from PClassProducts p where p.weiId=? and p.productId=? and p.supplierweiId=?";
		Object[] b = new Object[3];
		b[0] = pro.getSupplierWeiId();
		b[1] = pro.getProductId();
		b[2] = pro.getSupplierWeiId();
		PClassProducts pcp = baseDAO.getUniqueResultByHql(hql, b);
		if (pcp == null)
			pcp = new PClassProducts();
		pcp.setProductId(pro.getProductId());
		pcp.setClassId(Long.parseLong(pro.getSid().toString()));
		pcp.setIsSendMyself((short) 1);
		pcp.setSort((short) -1);
		pcp.setReason("");
		pcp.setState((short) 1);
		pcp.setCreateTime(new Date());
		pcp.setWeiId(pro.getSupplierWeiId());
		pcp.setSupplierweiId(pro.getSupplierWeiId());
		pcp.setShelvweiId(pro.getSupplierWeiId());
		pcp.setSendweiId(pro.getSupplierWeiId());
		UUserAssist userassist = updateUserAssist(pro.getSupplierWeiId());
		pcp.setWeiIdsort(userassist.getWeiIdSort());
		pcp.setType((short) 1);
		baseDAO.saveOrUpdate(pcp);
		
		if(pro.getPublishType().shortValue()==1) //分销区
		{
			DBrandsInfo bi= baseDAO.get(DBrandsInfo.class, pro.getSupplierWeiId());
			int brandid=0;
			if(bi !=null && bi.getBrandId()!=null )
			{
				brandid=bi.getBrandId();
			}
			//上架分销区
			PBrandShevle pb= baseDAO.getUniqueResultByHql(" from PBrandShevle p where p.productId=?", pro.getProductId());
			if(pb==null)
			{
				pb=new PBrandShevle();					
			}
			pb.setBrandId(brandid);
			pb.setClassId(pro.getSid());
			pb.setCreateTime(new Date());
			pb.setProductId(pro.getProductId());
			pb.setSupplyerId(pro.getSupplierWeiId());
			pb.setSystemClassID(pro.getClassId());
			pb.setType(ProductShelveStatu.OnShelf.toShort());
			baseDAO.saveOrUpdate(pb);
			
		}
	}
	
	/**
	 * 校验规则
	 * @param product
	 * @param userinfo
	 * @return
	 * @throws Exception
	 */
	public ReturnModel checkProduct(ProductParam product, UserInfo userinfo) throws Exception {
		ReturnModel rq = new ReturnModel();
		rq.setStatu(ReturnStatus.ParamError);
		if(StringHelp.IsNullOrEmpty(product.getType())){
			rq.setStatusreson("参数错误");
			return rq;
		}
		if(!"0".equals(product.getType()) && !"1".equals(product.getType()) && !"2".equals(product.getType())){
			rq.setStatusreson("操作参数错误");
			return rq;
		}
		if("2".equals(product.getType()) && product.getAuditStatus() == null){
			rq.setStatusreson("审核参数错误");
			return rq;
		}
		if(StringHelp.IsNullOrEmpty(product.getProductTitle())){
			rq.setStatusreson("标题不能为空");
			return rq;
		}
		if(StringHelp.IsNullOrEmpty(product.getDefaultImg())){
			rq.setStatusreson("商品图片不能为空");
			return rq;
		}
		if(product.getInventory() == null || product.getInventory() < 0){
			rq.setStatusreson("库存不能为空且要大于零");
			return rq;
		}
		//非子供应商
		if (!Integer.valueOf(PubProductTypeEnum.SubAccount.toString()).equals(userinfo.getPubProductType()) ) {
			if(product.getTypeNo() == null || product.getTypeNo() < 0){
				rq.setStatusreson("请选择分类");
				return rq;
			}
			if(product.getPostFeeId() == null){
				rq.setStatusreson("邮费模板为必选");
				return rq;
			}
			if(product.getPrice() == null || product.getPrice() < 0){
				rq.setStatusreson("零售价不能为空且要大于零");
				return rq;
			}
			if(product.getCustomTypeNo() == null || product.getCustomTypeNo() < 0){
				rq.setStatusreson("店铺分类为必选");
				return rq;
			}
		}
		
		//普通微店主、代理商、落地店
		if (Integer.valueOf(PubProductTypeEnum.Ordinary.toString()).equals(userinfo.getPubProductType())){
			// 判断是否大于10个产品
			String judgeString = "select count(*) from  PProducts p where p.supplierWeiId=? and p.state=1";
			Object[] job = new Object[1];
			job[0] = userinfo.getWeiId();
			long lj = baseDAO.count(judgeString, job);
			if (lj >= 10 && "1".equals(product.getType())) {
				if (lj > 10) {
					rq.setStatu(ReturnStatus.DataError);
					rq.setStatusreson("不是正式的商家，最多只能发布10个产品");
					return rq;
				}
				if ("".equals(product.getProductId())) {
					rq.setStatu(ReturnStatus.DataError);
					rq.setStatusreson("不是正式的商家，最多只能发布10个产品");
					return rq;
				}
			}
		}
	
		//平台号品牌号
		if (Integer.valueOf(PubProductTypeEnum.Platform.toString()).equals(userinfo.getPubProductType()) 
				|| Integer.valueOf(PubProductTypeEnum.Brand.toString()).equals(userinfo.getPubProductType())) {
			if(StringHelp.IsNullOrEmpty(product.getAgentPrice())){
				rq.setStatusreson("代理价不能为空");
				return rq;
			}
			if(StringHelp.IsNullOrEmpty(product.getStorePrice())){
				rq.setStatusreson("落地价不能为空");
				return rq;
			}
		}
		rq.setStatu(ReturnStatus.Success);
		rq.setStatusreson("操作成功");
		return rq;
	}

	private PProducts getProducts(long proid,ProductParam product, UserInfo userinfo,String reqSource) throws Exception{
		PProducts pro = null;
		short tag = (short) 4;
		if (proid > 0) {
			boolean isDrop = false;//是否需要下架产品
			pro = baseDAO.get(PProducts.class, proid);
			pro.setUpdateTime(new Date());
			if (Integer.valueOf(PubProductTypeEnum.Supplyer.toString()).equals(userinfo.getPubProductType())) {//供应商
				// 判断价格是否改变
				String bhql = " from PProductBatchPrice p where p.productId=?";
				Object[] bpob = new Object[1];
				bpob[0] = proid;
				List<PProductBatchPrice> libps = baseDAO.find(bhql, bpob);
				if (libps != null && libps.size() > 0) {
					// 判断是否有批发价
					List<Map<Integer, Double>> lold = new ArrayList<Map<Integer, Double>>();
					if (product.getBatchPrice() != null && product.getBatchPrice().size() > 0) {
						for (BatchPriceVO batchPriceVO : product.getBatchPrice()) {
							Map<Integer, Double> map = new HashMap<Integer, Double>();
							map.put(batchPriceVO.getNum(), batchPriceVO.getPrice());
							lold.add(map);
						}
					}
					List<Map<Integer, Double>> lnew = new ArrayList<Map<Integer, Double>>();
					for (int i = 0; i < libps.size(); i++) {
						PProductBatchPrice pbp = libps.get(i);
						Map<Integer, Double> map = new HashMap<Integer, Double>();
						Double batchprice = pbp.getPirce();
						Integer count = pbp.getCount();
						map.put(count, batchprice);
						lnew.add(map);
					}
					// 判断是否一致
					if (!IsListEqual(lold, lnew)) {
						isDrop = true;
					}
				}
			}
			//商品分类、标题、默认图片修改了则需要审核
			if (pro.getProductTitle().equals(product.getProductTitle())
					&& pro.getClassId() == product.getTypeNo()
					&& pro.getDefaultImg().equals(ImgDomain.ReplitImgDoMain(product.getDefaultImg()))
					&& Short.valueOf(ProductStatusEnum.Showing.toString()).equals(pro.getState())) {
				pro.setVerStatus((short) 1);// 审核状态要改为1
			} else {
				pro.setVerStatus((short) 0);// 审核状态要改为0
			}
			if (isDrop || (Short.valueOf(ProductStatusEnum.Showing.toString()).equals(pro.getState()) && "0".equals(product.getType()))) {
				/** 更新操作,涉及到批发号供应商，如果价格改动，或者商品由正式发布变为草稿 则批发号供应商上架过的都要下架*/
				// modify by tan 只要涉及到批发价格变化 则所有上架过的都下架。
				Object[] dob = new Object[1];
				dob[0] = proid;
				String dbatchString = "update PClassProducts b set b.state=0 where b.productId=? ";
				baseDAO.executeHql(dbatchString, dob);
			}
		} else {
			pro = new PProducts();
			pro.setCreateTime(new Date());
			pro.setUpdateTime(new Date());
			pro.setVerStatus((short) 0);// 审核状态要改为0
		}
		
		int bmid = 0;
		UBatchSupplyer batchSupplyer = baseDAO.get(UBatchSupplyer.class,userinfo.getWeiId());
		if (batchSupplyer != null
				&& batchSupplyer.getStatus() != null
				&& (batchSupplyer.getStatus().shortValue() == Short.parseShort(SupplierStatusEnum.Pass.toString()) 
				|| batchSupplyer.getStatus().shortValue() == Short.parseShort(SupplierStatusEnum.PayIn.toString()))) {
			bmid = batchSupplyer.getBmid();
		}
		
		pro.setBmid(bmid);// 市场id
		pro.setMid(product.getmID());//商品属性模板ID
		if (!StringHelp.IsNullOrEmpty(product.getProductTitle())) {
			pro.setProductTitle(product.getProductTitle());// 标题
		}
		if (!StringHelp.IsNullOrEmpty(product.getProductTitleMin())) {
			pro.setProductMinTitle(product.getProductTitleMin());// 副标题
		}
		if (!StringHelp.IsNullOrEmpty(product.getDescription())) {
			pro.setAppdes(product.getDescription());// app描述
		}
		if (!StringHelp.IsNullOrEmpty(product.getPcdes())) {
			pro.setPcdes(product.getPcdes());// pc描述
		}
		if (product.getTypeNo() != null && product.getTypeNo() > 0) {
			pro.setClassId(product.getTypeNo());// 分类id
		}
		if (!StringHelp.IsNullOrEmpty(product.getDefaultImg())) {
			pro.setDefaultImg(ImgDomain.ReplitImgDoMain(product.getDefaultImg()));// 默认图片
		}
		// 品牌
		if (!StringHelp.IsNullOrEmpty(product.getBrandId())) {
			pro.setBrandId(Integer.valueOf(product.getBrandId()));
		}
		if (product.getType() != null) {// 草稿or正式发布
			if ("1".equals(product.getType())) {
				pro.setState(Short.valueOf(ProductStatusEnum.Showing.toString()));
			} else if ("0".equals(product.getType())) {
				pro.setState(Short.valueOf(ProductStatusEnum.OutLine.toString()));
				
			}
		}
		pro.setBrandId(ParseHelper.toInt(product.getBrandId(), -1));
		if (Integer.valueOf(PubProductTypeEnum.Supplyer.toString()).equals(userinfo.getPubProductType())
				|| Short.valueOf("1").equals(userinfo.getPthdls())
				|| Short.valueOf("1").equals(userinfo.getPthldd()))
			pro.setSupperType((short) 5);// 商家类型
		else
			pro.setSupperType((short) 1);// 商家类型

		pro.setSupplierWeiId(userinfo.getWeiId());// 供应商微店号
		pro.setSid(product.getCustomTypeNo()!=null ? product.getCustomTypeNo() : -1);// 店铺分类id
		pro.setSort((short) 0);
		pro.setbType(Short.parseShort(ProductBType.normal.toString()));// 当前业务（0：正常展示；1：赠品；2：促销活动等）
		pro.setCount(product.getInventory());// 产品数量
		//商品表 默认 价格佣金数量
		if(product.getStyleList() !=null && product.getStyleList().size()>0){
			//款式 价格 数量 
			int totalCount =0;
			pro.setDefaultConmision(product.getStyleList().get(0).getConmision());
			pro.setDefaultPrice(product.getStyleList().get(0).getPrice());
			for (ProductStylesVO styleItem : product.getStyleList()) {
				//设置商品默认展示的 金额 佣金 数量
				if(styleItem.getConmision()< product.getCommission()){
					pro.setDefaultConmision(styleItem.getConmision());
				}
				if(styleItem.getPrice()< product.getPrice()){
					pro.setDefaultPrice(styleItem.getPrice());
				}					
				totalCount += (styleItem.getCount() == null?0: (int)styleItem.getCount());				
			}
			pro.setCount(totalCount);
		} else {
			// 价格
			Double price = 0.0;
			Double conmision = 0.0;
			if (product.getPrice() != null) {
				price = product.getPrice();
			}
			if (product.getCommission() != null) {
				conmision = product.getCommission();
			}
			pro.setDefaultPrice(price + conmision);
			pro.setDefaultConmision(product.getCommission());
			
		}
		//原价
		if (!StringHelp.IsNullOrEmpty(product.getDisplayPrice())) {
			pro.setOriginalPrice(Double.valueOf(product.getDisplayPrice()));
		}
		// 批发价格
		if (product.getBatchPrice() != null && product.getBatchPrice().size() > 0) {
			BatchPriceVO batchPriceVO = product.getBatchPrice().get(0);
			pro.setBatchPrice(batchPriceVO.getPrice());
			tag = (short) (tag + (short) 1);

		}
		// 预售价格
		/*if (!StringHelp.IsNullOrEmpty(product.getBookPrice())
				&& product.getBookPrice().length() > 3) {
			JSONObject obj = new JSONObject().fromObject(product.getBookPrice());
			Double bookprice = obj.getDouble("BookPrice");
			pro.setBookPrice(bookprice);
			tag = (short) (tag + (short) 2);
		}*/
		// 邮费模板
		if (product.getPostFeeId() != null && "-1".equals(product.getPostFeeId()) && product.getPostFeeId() > 0)// 自定义费用
		{
			Double dfee = Double.parseDouble(product.getCustomPostFee());
			pro.setFreightId(-1);
			pro.setDefPostFee(dfee);
		} else {
			pro.setFreightId(product.getPostFeeId());
		}
		// 分类属性
		String sClassType = "";
		String hql = " from PProductClass p where p.classId=?";
		Object[] b = new Object[1];
		b[0] = product.getTypeNo();
		PProductClass p = baseDAO.getUniqueResultByHql(hql, b);
		while (p != null) {
			sClassType += "-" + p.getClassId();
			b[0] = p.getParentId();
			p = baseDAO.getUniqueResultByHql(hql, b);
			continue;
		}
		pro.setClassPath(StringReverse.swapWords(sClassType));
		pro.setTag(tag);
		
		if (proid > 0) // 为更新操作 sproid != null && !"".equals(sproid)
		{
			String dhql = " delete from PProductStyles p where p.productId=?";
			b[0] = proid;
			baseDAO.executeHql(dhql, b);// 删除款色表
			dhql = "delete from PProductStyleKv p where p.productId=?";
			baseDAO.executeHql(dhql, b);// 删除款式属性KV
			dhql = "delete from PProductSellKey p where p.productId=?";
			baseDAO.executeHql(dhql, b);// 删除销售属性Key
			dhql = "delete from PProductSellValue p where p.productId=?";
			baseDAO.executeHql(dhql, b);// 删除销售属性Value
			dhql="delete from PProductKeyWords p where p.productId =?";
			baseDAO.executeHql(dhql, b);//关键字
			dhql="delete from PProductParamKv p where p.productId =?";
			baseDAO.executeHql(dhql, b);//删除商品参数
			// 批发价格
			String dbhql = " delete from PProductBatchPrice p where p.productId=?";
			b[0] = proid;
			baseDAO.executeHql(dbhql, b);// 删除批发表
		}
		return pro;
	}
	private PProducts getDistribiterProducts(long proid,ProductParam product, UserInfo userinfo,String reqSource) throws Exception{
		PProducts pro = null;
		short tag = (short) 4;
		if (proid > 0) {
			boolean isDrop = false;//是否需要下架产品
			pro = baseDAO.get(PProducts.class, proid);
			pro.setUpdateTime(new Date());			
			//商品分类、标题、默认图片修改了则需要审核
			if (pro.getProductTitle().equals(product.getProductTitle())
					&& pro.getClassId() == product.getTypeNo()
					&& pro.getDefaultImg().equals(ImgDomain.ReplitImgDoMain(product.getDefaultImg()))
					&& Short.valueOf(ProductStatusEnum.Showing.toString()).equals(pro.getState())) {
			} else {
				pro.setVerStatus((short) 0);// 审核状态要改为0
			}
			if (isDrop || (Short.valueOf(ProductStatusEnum.Showing.toString()).equals(pro.getState()) && "0".equals(product.getType()))) {
				/** 更新操作,涉及到批发号供应商，如果价格改动，或者商品由正式发布变为草稿 则批发号供应商上架过的都要下架*/
				// modify by tan 只要涉及到批发价格变化 则所有上架过的都下架。
				Object[] dob = new Object[1];
				dob[0] = proid;
				String dbatchString = "update PClassProducts b set b.state=0 where b.productId=? ";
				baseDAO.executeHql(dbatchString, dob);
				String dbatchStr="update PBrandShevle b set b.type=0 where b.productId=?";
				baseDAO.executeHql(dbatchStr, dob);
			}
		} else {
			pro = new PProducts();
			pro.setCreateTime(new Date());
			pro.setUpdateTime(new Date());
			pro.setVerStatus((short) 0);// 审核状态要改为0
		}
		
		int bmid = 0;
		pro.setPublishType(ProductPublishTypeEnum.Agent.toShort());
		pro.setBmid(bmid);// 市场id
		pro.setMid(product.getmID());//商品属性模板ID
		if (!StringHelp.IsNullOrEmpty(product.getProductTitle())) {
			pro.setProductTitle(product.getProductTitle());// 标题
		}
		if (!StringHelp.IsNullOrEmpty(product.getProductTitleMin())) {
			pro.setProductMinTitle(product.getProductTitleMin());// 副标题
		}
		if (!StringHelp.IsNullOrEmpty(product.getDescription())) {
			pro.setAppdes(product.getDescription());// app描述
		}
		if (!StringHelp.IsNullOrEmpty(product.getPcdes())) {
			pro.setPcdes(product.getPcdes());// pc描述
		}
		if (product.getTypeNo() != null && product.getTypeNo() > 0) {
			pro.setClassId(product.getTypeNo());// 分类id
		}
		if (!StringHelp.IsNullOrEmpty(product.getDefaultImg())) {
			pro.setDefaultImg(ImgDomain.ReplitImgDoMain(product.getDefaultImg()));// 默认图片
		}
		// 品牌
		if (!StringHelp.IsNullOrEmpty(product.getBrandId())) {
			pro.setBrandId(Integer.valueOf(product.getBrandId()));
		}
		if (product.getType() != null) {// 草稿or正式发布
			if ("1".equals(product.getType())) {
				pro.setState(Short.valueOf(ProductStatusEnum.Showing.toString()));
			} else if ("0".equals(product.getType())) {
				pro.setState(Short.valueOf(ProductStatusEnum.OutLine.toString()));
				
			}
		}
		pro.setBrandId(ParseHelper.toInt(product.getBrandId(), -1));	
		//不能判断为5必须
		if (Integer.valueOf(PubProductTypeEnum.Ordinary.toString()).equals(userinfo.getPubProductType()))
		{
			pro.setSupperType((short) 1);// 商家类型
		}
		else
		{
			pro.setSupperType((short) 5);// 商家类型
		}

		pro.setSupplierWeiId(userinfo.getWeiId());// 供应商微店号
		pro.setSid(product.getCustomTypeNo()!=null ? product.getCustomTypeNo() : -1);// 店铺分类id
		pro.setSort((short) 0);
		pro.setbType(Short.parseShort(ProductBType.normal.toString()));// 当前业务（0：正常展示；1：赠品；2：促销活动等）
		pro.setCount(product.getInventory());// 产品数量
		pro.setDefaultConmision(0.0);
		pro.setSupplyPrice(Double.parseDouble(product.getCostPrice()));//供货价
		
		//商品表 默认 价格佣金数量
		if(product.getStyleList() !=null && product.getStyleList().size()>0){
			//款式 价格 数量 
			int totalCount =0;
			pro.setDefaultPrice(product.getStyleList().get(0).getPrice());
			for (ProductStylesVO styleItem : product.getStyleList()) {
				//设置商品默认展示的 金额 佣金 数量
				if(styleItem.getPrice()< product.getPrice()){
					pro.setDefaultPrice(styleItem.getPrice());
				}					
				totalCount += (styleItem.getCount() == null?0: (int)styleItem.getCount());				
			}
			pro.setCount(totalCount);
		} else {
			// 价格
			Double price = 0.0;
			if (product.getPrice() != null) {
				price = product.getPrice();
			}
			
			pro.setDefaultPrice(price);
			
		}
		//设置几个价格 城主、副城主、代理 供货价P1、城主价X1、副城主价X2、代理价X3、零售价P2，
		//X1=（P2-P1）*10%+P1，X2=（P2-P1）*10%+X1，X3=（P2-P1）*15%+X2。
		Double d=pro.getDefaultPrice()-pro.getSupplyPrice();
		pro.setDukePrice(d*0.1+pro.getSupplyPrice());
		pro.setDeputyPrice(d*0.1+pro.getDukePrice());
		pro.setAgentPrice(d*0.15+pro.getDeputyPrice());
		//原价
		if (!StringHelp.IsNullOrEmpty(product.getDisplayPrice())) {
			pro.setOriginalPrice(Double.valueOf(product.getDisplayPrice()));
		}
		
		// 预售价格
		/*if (!StringHelp.IsNullOrEmpty(product.getBookPrice())
				&& product.getBookPrice().length() > 3) {
			JSONObject obj = new JSONObject().fromObject(product.getBookPrice());
			Double bookprice = obj.getDouble("BookPrice");
			pro.setBookPrice(bookprice);
			tag = (short) (tag + (short) 2);
		}*/
		// 邮费模板
		if (product.getPostFeeId() != null && "-1".equals(product.getPostFeeId()) && product.getPostFeeId() > 0)// 自定义费用
		{
			Double dfee = Double.parseDouble(product.getCustomPostFee());
			pro.setFreightId(-1);
			pro.setDefPostFee(dfee);
		} else {
			pro.setFreightId(product.getPostFeeId());
		}
		// 分类属性
		String sClassType = "";
		String hql = " from PProductClass p where p.classId=?";
		Object[] b = new Object[1];
		b[0] = product.getTypeNo();
		PProductClass p = baseDAO.getUniqueResultByHql(hql, b);
		while (p != null) {
			sClassType += "-" + p.getClassId();
			b[0] = p.getParentId();
			p = baseDAO.getUniqueResultByHql(hql, b);
			continue;
		}
		pro.setClassPath(StringReverse.swapWords(sClassType));
		pro.setTag(tag);
		
		if (proid > 0) // 为更新操作 sproid != null && !"".equals(sproid)
		{
			String dhql = " delete from PProductStyles p where p.productId=?";
			b[0] = proid;
			baseDAO.executeHql(dhql, b);// 删除款色表
			dhql = "delete from PProductStyleKv p where p.productId=?";
			baseDAO.executeHql(dhql, b);// 删除款式属性KV
			dhql = "delete from PProductSellKey p where p.productId=?";
			baseDAO.executeHql(dhql, b);// 删除销售属性Key
			dhql = "delete from PProductSellValue p where p.productId=?";
			baseDAO.executeHql(dhql, b);// 删除销售属性Value
			dhql="delete from PProductKeyWords p where p.productId =?";
			baseDAO.executeHql(dhql, b);//关键字
			dhql="delete from PProductParamKv p where p.productId =?";
			baseDAO.executeHql(dhql, b);//删除商品参数
			
		}
		return pro;
	}
	/**
	 * 平台号获取产品
	 * @param proid 子产品编号或正式产品编号
	 * @param product
	 * @param userinfo
	 * @return
	 * @throws Exception
	 */
	private PProducts getPlatformProducts(long proid,ProductParam product, UserInfo userinfo,String reqSource) throws Exception{
		PProducts pro = null;
		boolean isEdit = false;
		short tag = (short) 4;
		if (proid > 0) {
			pro = baseDAO.get(PProducts.class, proid);
			if (pro != null) {
				proid = pro.getProductId();
				isEdit = true;
				if (Short.valueOf(ProductStatusEnum.Showing.toString()).equals(pro.getState()) && "0".equals(product.getType())) {
					/** 更新操作,涉及到批发号供应商，如果价格改动，或者商品由正式发布变为草稿 则批发号供应商上架过的都要下架*/
					// modify by tan 只要涉及到批发价格变化 则所有上架过的都下架。
					Object[] dob = new Object[1];
					dob[0] = proid;
					String dbatchString = "update PClassProducts b set b.state=0 where b.productId=? ";
					baseDAO.executeHql(dbatchString, dob);
				}
				//商品分类、标题、默认图片修改了则需要审核
				if (pro.getProductTitle().equals(product.getProductTitle())
						&& pro.getClassId() == product.getTypeNo()
						&& pro.getDefaultImg().equals(ImgDomain.ReplitImgDoMain(product.getDefaultImg()))
						&& Short.valueOf(ProductStatusEnum.Showing.toString()).equals(pro.getState())) {
					pro.setVerStatus((short) 1);// 审核状态要改为1
				} else {
					pro.setVerStatus((short) 0);// 审核状态要改为0
				}
			}
		}
		if (isEdit) {
			pro.setUpdateTime(new Date());
		} else {
			pro = new PProducts();
			pro.setCreateTime(new Date());
			pro.setUpdateTime(new Date());
			pro.setVerStatus((short) 0);// 审核状态要改为0
		}
		pro.setBmid(0);// 市场id
		pro.setMid(product.getmID());//商品属性模板ID
		pro.setSupplierWeiId(userinfo.getWeiId());
		if (!StringHelp.IsNullOrEmpty(product.getProductTitle())) {
			pro.setProductTitle(product.getProductTitle());// 标题
		}
		if (!StringHelp.IsNullOrEmpty(product.getProductTitleMin())) {
			pro.setProductMinTitle(product.getProductTitleMin());// 副标题
		}
		if (!StringHelp.IsNullOrEmpty(product.getDescription())) {
			pro.setAppdes(product.getDescription());// app描述
		}
		if (!StringHelp.IsNullOrEmpty(product.getPcdes())) {
			pro.setPcdes(product.getPcdes());// pc描述
		}
		if (product.getTypeNo() != null) {
			pro.setClassId(product.getTypeNo());// 分类id
		}
		if (!StringHelp.IsNullOrEmpty(product.getDefaultImg())) {
			pro.setDefaultImg(ImgDomain.ReplitImgDoMain(product.getDefaultImg()));// 默认图片
		}
		// 库存
		if (product.getInventory() != null) {
			pro.setCount(product.getInventory());
		}
		// 品牌
		if (!StringHelp.IsNullOrEmpty(product.getBrandId())) {
			pro.setBrandId(Integer.valueOf(product.getBrandId()));
		}
		if (product.getType() != null) {// 草稿or正式发布
			if ("1".equals(product.getType()) 
					|| ("2".equals(product.getType()) 
							&& Short.valueOf(ProductSubStatusEnum.Pass.toString()).equals(product.getAuditStatus())) ) {
				pro.setState(Short.valueOf(ProductStatusEnum.Showing.toString()));
			} else if ("0".equals(product.getType())) {
				pro.setState(Short.valueOf(ProductStatusEnum.OutLine.toString()));
			}
		}
		pro.setBrandId(ParseHelper.toInt(product.getBrandId(), -1));
		
		
		if (Integer.valueOf(PubProductTypeEnum.Ordinary.toString()).equals(userinfo.getPubProductType()))
		{
			pro.setSupperType((short) 1);// 商家类型
		}
		else
		{
			pro.setSupperType((short) 5);// 商家类型
		}
		

		pro.setSupplierWeiId(userinfo.getWeiId());// 供应商微店号
		pro.setSid(product.getCustomTypeNo() != null ? product.getCustomTypeNo():-1);// 店铺分类id
		pro.setSort((short) 0);
		pro.setbType(Short.parseShort(ProductBType.normal.toString()));// 当前业务（0：正常展示；1：赠品；2：促销活动等）
		//商品表 默认 价格佣金数量
		if(product.getStyleList() !=null && product.getStyleList().size()>0){
			//款式 价格 数量 
			int totalCount =0;
			pro.setDefaultConmision(product.getStyleList().get(0).getConmision());
			pro.setDefaultPrice(product.getStyleList().get(0).getPrice());
			for (ProductStylesVO styleItem : product.getStyleList()) {
				//设置商品默认展示的 金额 佣金 数量
				if(styleItem.getConmision()< product.getCommission()){
					pro.setDefaultConmision(styleItem.getConmision());
				}
				if(styleItem.getPrice()< product.getPrice()){
					pro.setDefaultPrice(styleItem.getPrice());
				}					
				totalCount += (styleItem.getCount() == null?0: (int)styleItem.getCount());				
			}
			pro.setCount(totalCount);
		} else {
			// 价格
			Double price = 0.0;
			Double conmision = 0.0;
			if (product.getPrice() != null) {
				price = product.getPrice();
			}
			if (product.getCommission() != null) {
				conmision = product.getCommission();
			}
			pro.setDefaultPrice(price + conmision);
			pro.setDefaultConmision(product.getCommission());
		}
		//原价
		if (!StringHelp.IsNullOrEmpty(product.getDisplayPrice())) {
			pro.setOriginalPrice(Double.valueOf(product.getDisplayPrice()));
		}
		// 邮费模板
		if (product.getPostFeeId() != null && "-1".equals(product.getPostFeeId()) && product.getPostFeeId()> 0)// 自定义费用
		{
			Double dfee = Double.parseDouble(product.getCustomPostFee());
			pro.setFreightId(-1);
			pro.setDefPostFee(dfee);
		} else {
			pro.setFreightId(product.getPostFeeId());
		}
		// 分类属性
		String sClassType = "";
		String hql = " from PProductClass p where p.classId=?";
		Object[] b = new Object[1];
		b[0] = product.getTypeNo();
		PProductClass p = baseDAO.getUniqueResultByHql(hql, b);
		while (p != null) {
			sClassType += "-" + p.getClassId();
			b[0] = p.getParentId();
			p = baseDAO.getUniqueResultByHql(hql, b);
			continue;
		}
		pro.setClassPath(StringReverse.swapWords(sClassType));
		if (product.getDemandId() != null && product.getDemandId()  > 0) {
			tag = (short) (tag + (short) 8);
		}
		pro.setTag(tag);
		
		if (isEdit) // 为更新操作 sproid != null && !"".equals(sproid)
		{
			String dhql = " delete from PProductStyles p where p.productId=?";
			b[0] = proid;
			baseDAO.executeHql(dhql, b);// 删除款色表
			dhql = "delete from PProductStyleKv p where p.productId=?";
			baseDAO.executeHql(dhql, b);// 删除款式属性KV
			dhql = "delete from PProductSellKey p where p.productId=?";
			baseDAO.executeHql(dhql, b);// 删除销售属性Key
			dhql = "delete from PProductSellValue p where p.productId=?";
			baseDAO.executeHql(dhql, b);// 删除销售属性Value
			dhql="delete from PProductKeyWords p where p.productId =?";
			baseDAO.executeHql(dhql, b);//关键字
			dhql="delete from PProductParamKv p where p.productId =?";
			baseDAO.executeHql(dhql, b);//删除商品参数
		}
		return pro;
	}
	
	private PProducts getBrandProducts(long proid,ProductParam product, UserInfo userinfo,String reqSource) throws Exception{
		PProducts pro = null;
		short tag = (short) 4;
		if (proid > 0) {
			pro =baseDAO.get(PProducts.class, proid);
			pro.setUpdateTime(new Date());
			if (Short.valueOf(ProductStatusEnum.Showing.toString()).equals(pro.getState()) && "0".equals(product.getType())) {
				/** 更新操作,涉及到批发号供应商，如果价格改动，或者商品由正式发布变为草稿 则批发号供应商上架过的都要下架*/
				// modify by tan 只要涉及到批发价格变化 则所有上架过的都下架。
				Object[] dob = new Object[1];
				dob[0] = proid;
				String dbatchString = "update PClassProducts b set b.state=0 where b.productId=? ";
				baseDAO.executeHql(dbatchString, dob);
			}
			//商品分类、标题、默认图片修改了则需要审核
			if (pro.getProductTitle().equals(product.getProductTitle())
					&& pro.getClassId() == product.getTypeNo()
					&& pro.getDefaultImg().equals(ImgDomain.ReplitImgDoMain(product.getDefaultImg()))
					&& Short.valueOf(ProductStatusEnum.Showing.toString()).equals(pro.getState())) {
				pro.setVerStatus((short) 1);// 审核状态要改为1
			} else {
				pro.setVerStatus((short) 0);// 审核状态要改为0
			}
		} else {
			pro = new PProducts();
			pro.setCreateTime(new Date());
			pro.setUpdateTime(new Date());
			pro.setVerStatus((short) 0);// 审核状态要改为0
		}
		pro.setBmid(0);// 市场id
		pro.setMid(product.getmID());//商品属性模板ID
		pro.setSupplierWeiId(userinfo.getWeiId());
		if (!StringHelp.IsNullOrEmpty(product.getProductTitle())) {
			pro.setProductTitle(product.getProductTitle());// 标题
		}
		if (!StringHelp.IsNullOrEmpty(product.getProductTitleMin())) {
			pro.setProductMinTitle(product.getProductTitleMin());// 副标题
		}
		if (!StringHelp.IsNullOrEmpty(product.getDescription())) {
			pro.setAppdes(product.getDescription());// app描述
		}
		if (!StringHelp.IsNullOrEmpty(product.getPcdes())) {
			pro.setPcdes(product.getPcdes());// pc描述
		}
		if (product.getTypeNo() != null) {
			pro.setClassId(product.getTypeNo());// 分类id
		}
		if (!StringHelp.IsNullOrEmpty(product.getDefaultImg())) {
			pro.setDefaultImg(ImgDomain.ReplitImgDoMain(product.getDefaultImg()));// 默认图片
		}
		// 品牌
		if (!StringHelp.IsNullOrEmpty(product.getBrandId())) {
			pro.setBrandId(Integer.valueOf(product.getBrandId()));
		}
		// 库存
		if (product.getInventory() != null) {
			pro.setCount(Integer.valueOf(product.getInventory()));
		}
		if (product.getType() != null) {// 草稿or正式发布
			if ("1".equals(product.getType())) {
				pro.setState(Short.valueOf(ProductStatusEnum.Showing.toString()));
			} else if ("0".equals(product.getType())) {
				pro.setState(Short.valueOf(ProductStatusEnum.OutLine.toString()));
				
			}
		}
		pro.setBrandId(ParseHelper.toInt(product.getBrandId(), -1));
		
		
		if (Integer.valueOf(PubProductTypeEnum.Ordinary.toString()).equals(userinfo.getPubProductType()))
		{
			pro.setSupperType((short) 1);// 商家类型
		}
		else
		{
			pro.setSupperType((short) 5);// 商家类型
		}

		pro.setSupplierWeiId(userinfo.getWeiId());// 供应商微店号
		pro.setSid(product.getCustomTypeNo() != null ? product.getCustomTypeNo() : -1);// 店铺分类id
		pro.setSort((short) 0);
		pro.setbType(Short.parseShort(ProductBType.normal.toString()));// 当前业务（0：正常展示；1：赠品；2：促销活动等）
		pro.setCount(product.getInventory());// 产品数量
		//商品表 默认 价格佣金数量
		if(product.getStyleList() !=null && product.getStyleList().size()>0){
			//款式 价格 数量 
			int totalCount =0;
			pro.setDefaultConmision(product.getStyleList().get(0).getConmision());
			pro.setDefaultPrice(product.getStyleList().get(0).getPrice());
			for (ProductStylesVO styleItem : product.getStyleList()) {
				//设置商品默认展示的 金额 佣金 数量
				if(styleItem.getConmision()< product.getCommission()){
					pro.setDefaultConmision(styleItem.getConmision());
				}
				if(styleItem.getPrice()< product.getPrice()){
					pro.setDefaultPrice(styleItem.getPrice());
				}					
				totalCount += (styleItem.getCount() == null?0: (int)styleItem.getCount());				
			}
			pro.setCount(totalCount);
		} else {
			// 价格
			Double price = 0.0;
			Double conmision = 0.0;
			if (product.getPrice() != null) {
				price = product.getPrice();
			}
			if (product.getCommission() != null) {
				conmision = product.getCommission();
			}
			pro.setDefaultPrice(price + conmision);
			pro.setDefaultConmision(product.getCommission());
			
		}
		//原价
		if (!StringHelp.IsNullOrEmpty(product.getDisplayPrice())) {
			pro.setOriginalPrice(Double.valueOf(product.getDisplayPrice()));
		}
		// 邮费模板
		if (product.getPostFeeId() != null && "-1".equals(product.getPostFeeId()) && product.getPostFeeId() > 0)// 自定义费用
		{
			Double dfee = Double.parseDouble(product.getCustomPostFee());
			pro.setFreightId(-1);
			pro.setDefPostFee(dfee);
		} else {
			pro.setFreightId(product.getPostFeeId());
		}
		// 分类属性
		String sClassType = "";
		String hql = " from PProductClass p where p.classId=?";
		Object[] b = new Object[1];
		b[0] = product.getTypeNo();
		PProductClass p = baseDAO.getUniqueResultByHql(hql, b);
		while (p != null) {
			sClassType += "-" + p.getClassId();
			b[0] = p.getParentId();
			p = baseDAO.getUniqueResultByHql(hql, b);
			continue;
		}
		if (sClassType.indexOf("-") == 0) {
			sClassType = sClassType.substring(1);
		}
		pro.setClassPath(StringReverse.swapWords(sClassType));
		if (product.getDemandId() != null && product.getDemandId() > 0) {
			tag = (short) (tag + (short) 8);
		}
		pro.setTag(tag);
		
		if (proid > 0) // 为更新操作 sproid != null && !"".equals(sproid)
		{
			//删除商品款式
			String dhql = " delete from PProductStyles p where p.productId=?";
			b[0] = proid;
			baseDAO.executeHql(dhql, b);// 删除款色表
			dhql = "delete from PProductStyleKv p where p.productId=?";
			baseDAO.executeHql(dhql, b);// 删除款式属性KV
			dhql = "delete from PProductSellKey p where p.productId=?";
			baseDAO.executeHql(dhql, b);// 删除销售属性Key
			dhql = "delete from PProductSellValue p where p.productId=?";
			baseDAO.executeHql(dhql, b);// 删除销售属性Value
			dhql="delete from PProductKeyWords p where p.productId =?";
			baseDAO.executeHql(dhql, b);//关键字
			dhql="delete from PProductParamKv p where p.productId =?";
			baseDAO.executeHql(dhql, b);//删除商品参数
		}
		return pro;
	}
	
	private PProductSup getProductsSub(long proid,ProductParam product, UserInfo userinfo) throws Exception{
		PProductSup pro = null;
		if (proid > 0) {
			pro = baseDAO.getUniqueResultByHql("from PProductSup where productId=?", proid);
			if (pro != null) {
				pro.setUpdateTime(new Date());
			} else {
				return null;
			}
		} else {
			pro = new PProductSup();
			PProducts products = new PProducts();
			Long productId = (Long) baseDAO.save(products);
			pro.setProductId(productId);
			pro.setCreateTime(new Date());
			pro.setUpdateTime(new Date());
		}
		// 价格
		if (!StringHelp.IsNullOrEmpty(product.getCostPrice())) {
			pro.setDefaultPrice(Double.valueOf(product.getCostPrice()));
		}
		if (!StringHelp.IsNullOrEmpty(product.getSuggestPrice())) {
			pro.setAdvicePrice(Double.valueOf(product.getSuggestPrice()));
		}
		if (!StringHelp.IsNullOrEmpty(product.getProductTitle())) {
			pro.setProductTitle(product.getProductTitle());// 标题
		}
		if (!StringHelp.IsNullOrEmpty(product.getProductTitleMin())) {
			pro.setProductMinTitle(product.getProductTitleMin());// 副标题
		}
		if (!StringHelp.IsNullOrEmpty(product.getDescription())) {
			pro.setAppdes(product.getDescription());// app描述
		}
		if (!StringHelp.IsNullOrEmpty(product.getPcdes())) {
			pro.setPcdes(product.getPcdes());// pc描述
		}
		if (product.getTypeNo() != null) {
			pro.setClassId(product.getTypeNo());// 分类id
		}
		if (!StringHelp.IsNullOrEmpty(product.getDefaultImg())) {
			pro.setDefaultImg(ImgDomain.ReplitImgDoMain(product.getDefaultImg()));// 默认图片
		}
		if (!StringHelp.IsNullOrEmpty(product.getBrandId())) {
			pro.setBrandID(ParseHelper.toInt(product.getBrandId()));// 品牌
		}
		// 库存
		if (product.getInventory() != null) {
			pro.setStock(Integer.valueOf(product.getInventory()));
		}
		Short tid = Short.parseShort(product.getType()); // 草稿or正式发布
		pro.setState(tid);
		pro.setChildrenID(userinfo.getSubNo());
		pro.setWeiID(userinfo.getWeiId());//平台号微店号
		return pro;
	}
	
	@Override
	public ReturnModel editProduct(ProductParam product, UserInfo userinfo,String reqSource) throws Exception {
		ReturnModel rm = new ReturnModel();
		if (Integer.valueOf(PubProductTypeEnum.Platform.toString()).equals(userinfo.getPubProductType()) 
				&& Short.valueOf("2").equals(product.getAuditStatus()) ) {
			if(product.getProductId() == null || product.getProductId() < 0){
				rm.setStatu(ReturnStatus.ParamError);
				rm.setStatusreson("产品参数错误");
				return rm;
			}
			if(StringHelp.IsNullOrEmpty(product.getNotPassIntro())){
				rm.setStatu(ReturnStatus.ParamError);
				rm.setStatusreson("不通过原因不能为空");
				return rm;
			}
		} else {
			ReturnModel cm = checkProduct(product, userinfo);//校验数据
			if (!ReturnStatus.Success.toString().equals(cm.getStatu().toString())) {
				return cm;
			}
		}
		if (Integer.valueOf(PubProductTypeEnum.SubAccount.toString()).equals(userinfo.getPubProductType())) {
			rm = insertOrUpdateSubProd(product, userinfo,reqSource);//子供应商
		} else if(Integer.valueOf(PubProductTypeEnum.Platform.toString()).equals(userinfo.getPubProductType())){
			rm = insertOrUpdatePlatformProd(product, userinfo,reqSource);//平台号
		} else if(Integer.valueOf(PubProductTypeEnum.Brand.toString()).equals(userinfo.getPubProductType())){
			rm = insertOrUpdateBrandProd(product, userinfo,reqSource);//品牌号
		} else if(Integer.valueOf(PubProductTypeEnum.BrandSupplyer.toString()).equals(userinfo.getPubProductType())){
			rm = insertOrUpdateDistribityProd(product, userinfo,reqSource);//分銷体系
		} else if(Integer.valueOf(PubProductTypeEnum.Ordinary.toString()).equals(userinfo.getPubProductType()) 
				|| Integer.valueOf(PubProductTypeEnum.Supplyer.toString()).equals(userinfo.getPubProductType())){
			rm = insertOrUpdateProd(product, userinfo,reqSource);//微店主、工厂批发号供应商
		}
		return rm;
	}
	/**
	 * 微店主、工厂批发号供应商发布产品
	 * @param proid
	 * @param product
	 * @param userinfo
	 * @throws Exception
	 */
	private ReturnModel insertOrUpdateProd(ProductParam product, UserInfo userinfo,String reqSource) throws Exception{
		ReturnModel rm = new ReturnModel();
		rm.setStatu(ReturnStatus.Success);
		rm.setStatusreson("操作成功");
		long proid = product.getProductId();// 产品编号，不小于0 则为更新操作
		boolean isEdit = false;
		PProducts products = this.getProducts(proid, product, userinfo,reqSource);
		if (products == null) {
			rm.setStatu(ReturnStatus.DataError);
			rm.setStatusreson("数据错误");
			return rm;
		}
		products.setPublishType(ProductPublishTypeEnum.Normal.toShort());
		if (proid > 0) // 为更新操作 
		{
			isEdit = true;
			baseDAO.update(products);// 更新主表
		} else {
			proid = (Long) baseDAO.save(products);// 插入主表
		}
		//更新属性
		updateProductProperty(proid, isEdit,products.getDefaultPrice(),products.getDefaultConmision(), product, userinfo,reqSource);
		//更新批发价格
		List<PProductBatchPrice> listbatchPrice = new ArrayList<PProductBatchPrice>();
		if (product.getBatchPrice() != null && product.getBatchPrice().size() > 0) {
			for (BatchPriceVO vo : product.getBatchPrice()) {
				PProductBatchPrice pp = new PProductBatchPrice();
				pp.setPirce(vo.getPrice());
				pp.setCount(vo.getNum());
				pp.setProductId(proid);
				listbatchPrice.add(pp);
				baseDAO.save(pp);
			}
		}
		// modify by tan 2015-5-13 发布以后自动上架
		if ("1".equals(product.getType())){
			setShelveMyself(products, listbatchPrice);
			if (OrderFrom.APP.toString().equals(reqSource)
					|| OrderFrom.WAP.toString().equals(reqSource)) {
				// 保存销售属性模板
				String mod_name = "个人模板";
				rm = saveProductSellParamModel(userinfo.getWeiId(), product.getTypeNo(), mod_name, product.getSellAttr());
			}

			rm.setStatu(ReturnStatus.Success);
			rm.setStatusreson("");
			Map<String, Object> map = new HashMap<String, Object>();
			int push = getDownCount(userinfo.getWeiId());//分销商数量
			map.put("distributorCount", push);
			map.put("productid", products.getProductId());
			rm.setBasemodle(map);
		} else {
			Map<String, Object> map = new HashMap<String, Object>();
//			map.put("product", products);
			map.put("productid", proid);
			rm.setBasemodle(map);
		}
		return rm;
	}
	/**
	 * 子供应商编辑发布产品
	 * @param product
	 * @param userinfo
	 * @param reqSource
	 * @return
	 * @throws Exception
	 */
	private ReturnModel insertOrUpdateSubProd(ProductParam product, UserInfo userinfo,String reqSource) throws Exception{
		ReturnModel rm = new ReturnModel();
		rm.setStatu(ReturnStatus.SystemError);
		rm.setStatusreson("系统错误");
		long proid = product.getProductId();// 产品编号，不小于0 则为更新操作
		boolean isEdit = false;
		PProductSup products = this.getProductsSub(proid, product, userinfo);
		
		if (products == null) {
			rm.setStatu(ReturnStatus.DataError);
			rm.setStatusreson("数据错误");
			return rm;
		}
		if (proid > 0) // 为更新操作 sproid != null && !"".equals(sproid)
		{
			isEdit = true;
			baseDAO.update(products);// 更新主表
		} else {
			proid = (Long) baseDAO.save(products);// 插入主表
			
		}
		// 图片列表
		if (product.getImgs() != null && product.getImgs().length() > 0) {
			if (isEdit) {
				String d = " delete from PProductImg p where p.productId=?";
				Object[] ob = new Object[1];
				ob[0] = proid;
				baseDAO.executeHql(d, ob);
			}
			for (String url : product.getImgs().split(",")) {
				PProductImg pi = new PProductImg();
				pi.setCreateTime(new Date());
				pi.setImgPath(ImgDomain.ReplitImgDoMain(url));
				pi.setProductId(proid);
				pi.setSupplierWeiId(userinfo.getWeiId()); 
				baseDAO.save(pi);
			}
		}
		String auditMerchantWeiName = "";
		UPlatformSupplyer platformSupplyer = baseDAO.get(UPlatformSupplyer.class, userinfo.getWeiId());
		if (platformSupplyer != null) {
			auditMerchantWeiName = platformSupplyer.getSupplyName();
		}
		Map<String, Object> map = new HashMap<String, Object>();
//		map.put("product", products);
		map.put("productid", proid);
		map.put("isEdit", isEdit);
		map.put("platformWeiId", userinfo.getWeiId());
		map.put("platformWeiName", auditMerchantWeiName);
		rm.setBasemodle(map);
		rm.setStatu(ReturnStatus.Success);
		rm.setStatusreson("操作成功");
		return rm;
	}
	/**
	 * 平台号编辑发布
	 * @param product
	 * @param userinfo
	 * @param reqSource
	 * @return
	 * @throws Exception
	 */
	private ReturnModel insertOrUpdatePlatformProd(ProductParam product, UserInfo userinfo,String reqSource) throws Exception{
		ReturnModel rm = new ReturnModel();
		rm.setStatu(ReturnStatus.Success);
		rm.setStatusreson("操作成功");
		long proid = product.getProductId();// 产品编号，不小于0 则为更新操作
		boolean isEdit = false;
		boolean isAudit = false;//是否审核编辑子供应商的产品
		PProducts pro = null;
		PProductSup prosub = null;
		Long subProductId = null;
		if (proid > 0) // 为更新操作 sproid != null && !"".equals(sproid)
		{
			pro = baseDAO.get(PProducts.class, proid);
			if (pro != null) {
				isEdit = true;
			} else {
				prosub = baseDAO.getUniqueResultByHql("from PProductSup where productId=?", proid);
				if (prosub != null) {
					isAudit = true;
					subProductId = prosub.getProductId();
				} else {
					rm.setStatu(ReturnStatus.DataError);
					rm.setStatusreson("平台号子供应商已删除该产品");
					return rm;
				}
			}
		}
		if (isAudit) {
			//平台号审核子产品为不通过则返回
			//更新子产品审核状态
			prosub.setState(product.getAuditStatus());
			if (!Short.valueOf(ProductSubStatusEnum.Pass.toString()).equals(product.getAuditStatus())) {
				if (Short.valueOf(ProductSubStatusEnum.Fail.toString()).equals(product.getAuditStatus())) {
					if (StringUtils.isNotEmpty(product.getNotPassIntro())) {
						prosub.setReason(product.getNotPassIntro());
					}
				}
				baseDAO.update(prosub);
				rm.setStatu(ReturnStatus.Success);
				rm.setStatusreson("审核成功");
				return rm;
			}
			if (Short.valueOf(ProductSubStatusEnum.Fail.toString()).equals(product.getAuditStatus())) {
				if (StringUtils.isNotEmpty(product.getNotPassIntro())) {
					prosub.setReason(product.getNotPassIntro());
				}
			}
			baseDAO.update(prosub);
		}
		PProducts products = this.getPlatformProducts(proid, product, userinfo,reqSource);
		if (products == null) {
			rm.setStatu(ReturnStatus.DataError);
			rm.setStatusreson("数据错误");
			return rm;
		}
		products.setPublishType(ProductPublishTypeEnum.Normal.toShort());
		PProductRelation pr = null;
		if (isEdit) // 为更新操作 sproid != null && !"".equals(sproid)
		{
			baseDAO.update(products);// 更新主表
			if (pro != null) {
				pr = baseDAO.get(PProductRelation.class, proid);
			}
		} else {
			proid = (Long) baseDAO.save(products);// 插入主表
		}
		if (pr == null) {
			pr = new PProductRelation();
			pr.setCreateTime(new Date());
			pr.setSubProductId(subProductId);
		}
		if (prosub != null) {
			pr.setSubProductId(prosub.getProductId());
			pr.setAdvicePrice(prosub.getAdvicePrice());
			pr.setDefaultPrice(prosub.getDefaultPrice());
			pr.setSubWeiId(prosub.getChildrenID());
		}
		pr.setProductId(proid);
		if (OrderFrom.APP.toString().equals(reqSource)
				|| OrderFrom.WAP.toString().equals(reqSource)) {
			// 代理价
			if (!StringHelp.IsNullOrEmpty(product.getAgentPrice())) {
				pr.setProxyPrice(Double.valueOf(product.getAgentPrice()));
				pr.setMinProxyPrice(Double.valueOf(product.getAgentPrice()));
				pr.setMaxProxyPrice(Double.valueOf(product.getAgentPrice()));
			}
			// 落地价
			if (!StringHelp.IsNullOrEmpty(product.getStorePrice())) {
				pr.setFloorPrice(Double.valueOf(product.getStorePrice()));
				pr.setMinFloorPrice(Double.valueOf(product.getStorePrice()));
				pr.setMaxFloorPrice(Double.valueOf(product.getStorePrice()));
			}
			
		} else {
			// 代理价
			if (!StringHelp.IsNullOrEmpty(product.getAgentPrice())) {
				pr.setProxyPrice(Double.valueOf(product.getAgentPrice()));
			}
			// 落地价
			if (!StringHelp.IsNullOrEmpty(product.getStorePrice())) {
				pr.setFloorPrice(Double.valueOf(product.getStorePrice()));
			}
			Double minProxyPrice = Double.valueOf(product.getAgentPrice());
			Double maxProxyPrice = Double.valueOf(product.getAgentPrice());
			Double minFloorPrice = Double.valueOf(product.getStorePrice());
			Double maxFloorPrice = Double.valueOf(product.getStorePrice());
			if(product.getStyleList() !=null && product.getStyleList().size()>0){
				for (ProductStylesVO styleVO : product.getStyleList()) {
					if (minProxyPrice == 0) {
						minProxyPrice = styleVO.getProxyPrice();
					}
					if (maxProxyPrice == 0) {
						maxProxyPrice = styleVO.getProxyPrice();
					}
					if (minFloorPrice == 0) {
						minFloorPrice = styleVO.getFloorPrice();
					}
					if (maxFloorPrice == 0) {
						maxFloorPrice = styleVO.getFloorPrice();
					}
					if (minProxyPrice > styleVO.getProxyPrice()) {
						minProxyPrice = styleVO.getProxyPrice();
					}
					if (maxProxyPrice < styleVO.getProxyPrice()) {
						maxProxyPrice = styleVO.getProxyPrice();
					}
					if (minFloorPrice > styleVO.getFloorPrice()) {
						minFloorPrice = styleVO.getFloorPrice();
					}
					if (maxFloorPrice < styleVO.getFloorPrice()) {
						maxFloorPrice = styleVO.getFloorPrice();
					}
				}
			}
			pr.setMinProxyPrice(minProxyPrice);
			pr.setMinFloorPrice(minFloorPrice);
			pr.setMaxProxyPrice(maxProxyPrice);
			pr.setMaxFloorPrice(maxFloorPrice);
		}
		baseDAO.saveOrUpdate(pr);//保存产品辅助表
		boolean demandChange = false;
		Integer oldDemandId = null;
		//产品招商需求关联
		UDemandProduct dp =  baseDAO.getUniqueResultByHql("from UDemandProduct where productId=?", new Object[]{proid});
		if (product.getDemandId() != null && product.getDemandId() > 0) {
			if (dp != null) {
				if (!product.getDemandId().equals(dp.getDemandId())) {
					demandChange = true;
					oldDemandId = dp.getDemandId();
				}
				dp.setDemandId(product.getDemandId());
				baseDAO.update(dp);
			} else {
				dp = new UDemandProduct();
				dp.setProductId(proid);
				dp.setCreateTime(new Date());
				dp.setDemandId(product.getDemandId());
				baseDAO.save(dp);
			}
		} else {
			if (dp != null) {
				baseDAO.executeHql("delete from UDemandProduct where productId=?", proid);
			}
		}
		//更新属性
		updateProductProperty(proid, isEdit,products.getDefaultPrice(),products.getDefaultConmision(), product, userinfo,reqSource);
		// modify by tan 2015-5-13 发布以后自动上架
		if ("1".equals(product.getType()) || "2".equals(product.getType())){
			setShelveMyself(products);
			if (OrderFrom.APP.toString().equals(reqSource)
				|| OrderFrom.WAP.toString().equals(reqSource)) {
				// 保存销售属性模板
				String mod_name = "个人模板";
				rm = saveProductSellParamModel(userinfo.getWeiId(), product.getTypeNo(), mod_name, product.getSellAttr());
			}
			if (product.getDemandId() != null && product.getDemandId() > 0) {
				List<PPushShelves> list = baseProductDao.find("from PPushShelves where weiId=? and productId=? and pushType=?", new Object[]{userinfo.getWeiId(),products.getProductId(),PushTypeEnum.ShelveProduct.getNo()});
				PPushShelves pushShelves = null;
				if (list != null && list.size() > 0) {
					pushShelves =  list.get(0);
				}
				if (pushShelves == null) {
					//改上架到所有代理商、落地店
					PPushShelves push = new PPushShelves();
					push.setWeiId(userinfo.getWeiId());
					push.setPushType(PushTypeEnum.ShelveProduct.getNo());
					push.setCreateTime(new Date());
					push.setProductId(products.getProductId());
					PushShelvesContentVO content = new PushShelvesContentVO();
					content.setDemandId(product.getDemandId());
					content.setOldDemandId(oldDemandId);
					if (demandChange) {
						content.setSehlveType(1);
					} else {
						content.setSehlveType(0);
					}
					push.setContent(JSONObject.toJSONString(content));
					baseDAO.save(push);
				} else {
					if (demandChange) {
						//改上架到所有代理商、落地店
						pushShelves.setWeiId(userinfo.getWeiId());
						pushShelves.setPushType(PushTypeEnum.ShelveProduct.getNo());
						pushShelves.setCreateTime(new Date());
						pushShelves.setProductId(products.getProductId());
						PushShelvesContentVO content = new PushShelvesContentVO();
						content.setDemandId(product.getDemandId());
						content.setOldDemandId(oldDemandId);
						content.setSehlveType(1);
						pushShelves.setContent(JSONObject.toJSONString(content));
						baseDAO.update(pushShelves);
					}
				}
//				shelveToAgeStoreBydomeId(product.getDemandId(),oldDemandId, userinfo.getWeiId(), products,demandChange);
				//产品大分类 备份
				if (products.getClassId() != null && products.getClassId() > 0) {
					maintainProductClassTemp(products.getClassId());
				}
			}
			rm.setStatu(ReturnStatus.Success);
			rm.setStatusreson("");
			Map<String, Object> map = new HashMap<String, Object>();
			int agentCount = getAgentCount(userinfo.getWeiId(),Short.valueOf(ApplyAgentStatusEnum.Pass.toString()));
			int storeCount = getStoreCount(userinfo.getWeiId());
			map.put("agentCount", agentCount);
			map.put("storeCount", storeCount);
			map.put("productid", products.getProductId());
			rm.setBasemodle(map);
		} else {
			Map<String, Object> map = new HashMap<String, Object>();
//			map.put("product", products);
			map.put("productid", proid);
			rm.setBasemodle(map);
		}
		return rm;
	}
	/**
	 * 品牌号编辑发布产品
	 * @param product
	 * @param userinfo
	 * @param reqSource
	 * @return
	 * @throws Exception
	 */
	private ReturnModel insertOrUpdateBrandProd(ProductParam product, UserInfo userinfo,String reqSource) throws Exception{
		ReturnModel rm = new ReturnModel();
		rm.setStatu(ReturnStatus.Success);
		rm.setStatusreson("操作成功");
		long proid = product.getProductId();// 产品编号，不小于0 则为更新操作
		boolean isEdit = false;
		PProducts products = this.getBrandProducts(proid, product, userinfo,reqSource);
		if (products == null) {
			rm.setStatu(ReturnStatus.DataError);
			rm.setStatusreson("数据错误");
			return rm;
		}
		products.setPublishType(ProductPublishTypeEnum.Normal.toShort());
		PProductRelation pr = null;
		if (proid > 0) // 为更新操作 sproid != null && !"".equals(sproid)
		{
			isEdit = true;
			baseDAO.update(products);// 更新主表
			pr = baseDAO.get(PProductRelation.class, proid);
		} else {
			proid = (Long) baseDAO.save(products);// 插入主表
		}
		
		if (pr == null) {
			pr = new PProductRelation();
			pr.setCreateTime(new Date());
		}
		pr.setProductId(proid);
		
		if (OrderFrom.APP.toString().equals(reqSource)
				|| OrderFrom.WAP.toString().equals(reqSource)) {
			// 代理价
			if (!StringHelp.IsNullOrEmpty(product.getAgentPrice())) {
				pr.setProxyPrice(Double.valueOf(product.getAgentPrice()));
				pr.setMinProxyPrice(Double.valueOf(product.getAgentPrice()));
			}
			// 落地价
			if (!StringHelp.IsNullOrEmpty(product.getStorePrice())) {
				pr.setFloorPrice(Double.valueOf(product.getStorePrice()));
				pr.setMinFloorPrice(Double.valueOf(product.getStorePrice()));
			}
			
		} else {
			// 代理价
			if (!StringHelp.IsNullOrEmpty(product.getAgentPrice())) {
				pr.setProxyPrice(Double.valueOf(product.getAgentPrice()));
			}
			// 落地价
			if (!StringHelp.IsNullOrEmpty(product.getStorePrice())) {
				pr.setFloorPrice(Double.valueOf(product.getStorePrice()));
			}
			Double minProxyPrice = Double.valueOf(product.getAgentPrice());
			Double maxProxyPrice = Double.valueOf(product.getAgentPrice());
			Double minFloorPrice = Double.valueOf(product.getStorePrice());
			Double maxFloorPrice = Double.valueOf(product.getStorePrice());
			if(product.getStyleList() !=null && product.getStyleList().size()>0){
				for (ProductStylesVO styleVO : product.getStyleList()) {
					if (minProxyPrice == 0) {
						minProxyPrice = styleVO.getProxyPrice();
					}
					if (maxProxyPrice == 0) {
						maxProxyPrice = styleVO.getProxyPrice();
					}
					if (minFloorPrice == 0) {
						minFloorPrice = styleVO.getFloorPrice();
					}
					if (maxFloorPrice == 0) {
						maxFloorPrice = styleVO.getFloorPrice();
					}
					if (minProxyPrice > styleVO.getProxyPrice()) {
						minProxyPrice = styleVO.getProxyPrice();
					}
					if (maxProxyPrice < styleVO.getProxyPrice()) {
						maxProxyPrice = styleVO.getProxyPrice();
					}
					if (minFloorPrice > styleVO.getFloorPrice()) {
						minFloorPrice = styleVO.getFloorPrice();
					}
					if (maxFloorPrice < styleVO.getFloorPrice()) {
						maxFloorPrice = styleVO.getFloorPrice();
					}
				}
			}
			pr.setMinProxyPrice(minProxyPrice);
			pr.setMinFloorPrice(minFloorPrice);
			pr.setMaxProxyPrice(maxProxyPrice);
			pr.setMaxFloorPrice(maxFloorPrice);
		}
		baseDAO.saveOrUpdate(pr);//保存产品辅助表
		boolean demandChange = false;
		Integer oldDemandId = null;
		//产品招商需求关联
		UDemandProduct dp =  baseDAO.getUniqueResultByHql("from UDemandProduct where productId=?", new Object[]{proid});
		if (product.getDemandId() != null && product.getDemandId() > 0) {
			if (dp != null) {
				if (!product.getDemandId().equals(dp.getDemandId())) {
					demandChange = true;
					oldDemandId = dp.getDemandId();
				}
				dp.setDemandId(Integer.valueOf(product.getDemandId()));
				baseDAO.update(dp);
			} else {
				dp = new UDemandProduct();
				dp.setProductId(proid);
				dp.setCreateTime(new Date());
				dp.setDemandId(Integer.valueOf(product.getDemandId()));
				baseDAO.save(dp);
			}
		} else {
			if (dp != null) {
				baseDAO.executeHql("delete from UDemandProduct where productId=?", proid);
			}
		}
		//更新属性
		updateProductProperty(proid, isEdit,products.getDefaultPrice(),products.getDefaultConmision(), product, userinfo,reqSource);
		// modify by tan 2015-5-13 发布以后自动上架
		if ("1".equals(product.getType())){
			setShelveMyself(products);
			if (OrderFrom.APP.toString().equals(reqSource)
					|| OrderFrom.WAP.toString().equals(reqSource)) {
				// 保存销售属性模板
				String mod_name = "个人模板";
				rm = saveProductSellParamModel(userinfo.getWeiId(), product.getTypeNo(), mod_name, product.getSellAttr());
			}
			if (product.getDemandId() != null && product.getDemandId() > 0) {
				List<PPushShelves> list = baseProductDao.find("from PPushShelves where weiId=? and productId=? and pushType=?", new Object[]{userinfo.getWeiId(),products.getProductId(),PushTypeEnum.ShelveProduct.getNo()});
				PPushShelves pushShelves = null;
				if (list != null && list.size() > 0) {
					pushShelves =  list.get(0);
				}
				if (pushShelves == null) {
					//改上架到所有代理商、落地店
					PPushShelves push = new PPushShelves();
					push.setWeiId(userinfo.getWeiId());
					push.setPushType(PushTypeEnum.ShelveProduct.getNo());
					push.setCreateTime(new Date());
					push.setProductId(products.getProductId());
					PushShelvesContentVO content = new PushShelvesContentVO();
					content.setDemandId(product.getDemandId());
					content.setOldDemandId(oldDemandId);
					if (demandChange) {
						content.setSehlveType(1);
					} else {
						content.setSehlveType(0);
					}
					push.setContent(JSONObject.toJSONString(content));
					baseDAO.save(push);
				} else {
					if (demandChange) {
						//改上架到所有代理商、落地店
						pushShelves.setWeiId(userinfo.getWeiId());
						pushShelves.setPushType(PushTypeEnum.ShelveProduct.getNo());
						pushShelves.setCreateTime(new Date());
						pushShelves.setProductId(products.getProductId());
						PushShelvesContentVO content = new PushShelvesContentVO();
						content.setDemandId(product.getDemandId());
						content.setOldDemandId(oldDemandId);
						content.setSehlveType(1);
						pushShelves.setContent(JSONObject.toJSONString(content));
						baseDAO.update(pushShelves);
					}
				}
//				shelveToAgeStoreBydomeId(product.getDemandId(),oldDemandId, userinfo.getWeiId(), products,demandChange);
				//产品大分类 备份
				if (products.getClassId() != null && products.getClassId() > 0) {
					maintainProductClassTemp(products.getClassId());
				}
			}
			rm.setStatu(ReturnStatus.Success);
			rm.setStatusreson("");
			Map<String, Object> map = new HashMap<String, Object>();
			int agentCount = getAgentCount(userinfo.getWeiId(),Short.valueOf(ApplyAgentStatusEnum.Pass.toString()));
			int storeCount = getStoreCount(userinfo.getWeiId());
			map.put("agentCount", agentCount);
			map.put("storeCount", storeCount);
			map.put("productid", products.getProductId());
			rm.setBasemodle(map);
		} else {
			Map<String, Object> map = new HashMap<String, Object>();
//			map.put("product", products);
			map.put("productid", proid);
			rm.setBasemodle(map);
		}
		return rm;
	}
	
	
	/**
	 * 分销体系发布商品
	 * @param product
	 * @param userinfo
	 * @param reqSource
	 * @return
	 * @throws Exception
	 */
	private ReturnModel insertOrUpdateDistribityProd(ProductParam product, UserInfo userinfo,String reqSource) throws Exception{
		ReturnModel rm = new ReturnModel();
		rm.setStatu(ReturnStatus.Success);
		rm.setStatusreson("操作成功");
		long proid = product.getProductId();// 产品编号，不小于0 则为更新操作
		boolean isEdit = false;
		PProducts products = this.getDistribiterProducts(proid, product, userinfo,reqSource);
		if (products == null) {
			rm.setStatu(ReturnStatus.DataError);
			rm.setStatusreson("数据错误");
			return rm;
		}
		if (proid > 0) // 为更新操作 sproid != null && !"".equals(sproid)
		{
			isEdit = true;
			baseDAO.update(products);// 更新主表
		} else {
			proid = (Long) baseDAO.save(products);// 插入主表
		}
		
		//更新属性
		updateProductProperty(proid, isEdit,products.getDefaultPrice(),products.getDefaultConmision(), product, userinfo,reqSource);
		// modify by tan 2015-5-13 发布以后自动上架
		if ("1".equals(product.getType())){
			setShelveMyself(products);
			if (OrderFrom.APP.toString().equals(reqSource)
					|| OrderFrom.WAP.toString().equals(reqSource)) {
				// 保存销售属性模板
				String mod_name = "个人模板";
				rm = saveProductSellParamModel(userinfo.getWeiId(), product.getTypeNo(), mod_name, product.getSellAttr());
			}
			
			rm.setStatu(ReturnStatus.Success);
			rm.setStatusreson("");
			Map<String, Object> map = new HashMap<String, Object>();		
			map.put("productid", products.getProductId());
			rm.setBasemodle(map);
		} else {
			Map<String, Object> map = new HashMap<String, Object>();
//			map.put("product", products);
			map.put("productid", proid);
			rm.setBasemodle(map);
		}
		return rm;
	}
	/**
	 * 更新商品的属性
	 * @param proid 
	 * @param product
	 * @param userinfo
	 */
	private void updateProductProperty(long proid,boolean isEdit,Double defaultPrice,Double defaultConmision, ProductParam product,
			UserInfo userinfo,String reqSource) throws Exception {
		// 图片列表
		if (product.getImgs() != null && product.getImgs().length() > 0) {
			if (isEdit) {
				String d = " delete from PProductImg p where p.productId=?";
				Object[] ob = new Object[1];
				ob[0] = proid;
				baseDAO.executeHql(d, ob);
			}
			for (String url : product.getImgs().split(",")) {
				PProductImg pi = new PProductImg();
				pi.setCreateTime(new Date());
				pi.setImgPath(ImgDomain.ReplitImgDoMain(url));
				pi.setProductId(proid);
				pi.setSupplierWeiId(userinfo.getWeiId()); 
				baseDAO.save(pi);
			}
		}
		//关键词表
		if(product.getKeyWords() !=null && product.getKeyWords() !=""){
			String[] keyWords= product.getKeyWords().split(" ");
			for (String kw : keyWords) {
				PProductKeyWords keyWordModel = new PProductKeyWords();
				keyWordModel.setCreateTime(new Date());
				keyWordModel.setKeyWord(kw);
				keyWordModel.setProductId(proid);
				keyWordModel.setState((short)1);
				baseDAO.save(keyWordModel);
			}
		}
		//默认代理价、落地价
		Double storeprice = 0.0;
		Double agentprice = 0.0;
		Double supplyprice=0.0;
		if (Integer.valueOf(PubProductTypeEnum.Platform.toString()).equals(userinfo.getPubProductType())
				|| Integer.valueOf(PubProductTypeEnum.Brand.toString()).equals(userinfo.getPubProductType())) {
			if (!StringHelp.IsNullOrEmpty(product.getStorePrice())) {
				storeprice = Double.valueOf(product.getStorePrice());
			}
			if (!StringHelp.IsNullOrEmpty(product.getAgentPrice())) {
				agentprice = Double.valueOf(product.getAgentPrice());
			}
		}
		
		//app wap
		if (OrderFrom.APP.toString().equals(reqSource)
				|| OrderFrom.WAP.toString().equals(reqSource)) {
			// 销售属性 //没有销售属性，默认给一个 
			if (product.getSellAttr() == null || product.getSellAttr().size() <= 0) {
				List<ProductAttributeVO> attrList = new ArrayList<ProductAttributeVO>();
				ProductAttributeVO attr = new ProductAttributeVO();
				attr.setName("-1");
				List<AttributeValueVO> vals = new ArrayList<AttributeValueVO>();
				AttributeValueVO attrValue = new AttributeValueVO();
				attrValue.setValue("-1");
				vals.add(attrValue);
				attr.setVals(vals);
				attrList.add(attr);
				product.setSellAttr(attrList);
			}
			List<List<KeyValue>> listskv = new ArrayList<List<KeyValue>>();
			for (int i = 0;i < product.getSellAttr().size();i++) // 遍历 key值
			{
				ProductAttributeVO attrVO = product.getSellAttr().get(i);
				List<AttributeValueVO> vals = attrVO.getVals();
				if (vals.size() == 0) {
					continue;
				}
				List<KeyValue> lkv = new ArrayList<KeyValue>();
				PProductSellKey pkey = new PProductSellKey();
				pkey.setCreateTime(new Date());
				pkey.setProductId(proid);
				pkey.setSort((short) i);
				pkey.setAttributeName(attrVO.getName());
				Long la = (Long) baseDAO.save(pkey);
				
				for (int j = 0; j < vals.size(); j++) // 遍历value
				{
					AttributeValueVO attrVals = vals.get(j);
					PProductSellValue pv = new PProductSellValue();
					pv.setCreateTime(new Date());
					pv.setState((short) 1);
					pv.setClassId(product.getTypeNo());
					pv.setProductId(proid);
					pv.setUpdateTime(new Date());
					pv.setValue(attrVals.getValue());
					pv.setAttributeId(la);
					Long lv = (Long) baseDAO.save(pv);
					KeyValue kv = new KeyValue(la, lv);
					lkv.add(kv);
				}
				listskv.add(lkv);
			}
			List<List<KeyValue>> ls = SellAttrMapUtil.Tp(listskv);
			for (List<KeyValue> lk : ls) {
				PProductStyles ps = new PProductStyles();
				ps.setProductId(proid);
				ps.setCreateTime(new Date());
				ps.setUpdateTime(new Date());
				ps.setCount(9999);
				ps.setPrice(defaultPrice);
				ps.setConmision(defaultConmision);
				ps.setLandPrice(storeprice);
				ps.setAgencyPrice(agentprice);
				Double costprice=0.0;
				if(product.getCostPrice()!=null && !"".equals(product))
				{
					costprice=Double.parseDouble(product.getCostPrice());
				}
				ps.setSupplyPrice(costprice);
				if(Integer.valueOf(PubProductTypeEnum.BrandSupplyer.toString()).equals(userinfo.getPubProductType())) //分销区
				{
					Double d=ps.getPrice()-ps.getSupplyPrice();
					ps.setDukePrice(d*0.1+ps.getSupplyPrice());
					ps.setDeputyPrice(d*0.1+ps.getDukePrice());
					ps.setAgentPrice(d*0.15+ps.getDeputyPrice());
				}
				Long psid = (Long) baseDAO.save(ps);
				for (KeyValue kv : lk) {
					PProductStyleKv pkv = new PProductStyleKv();
					pkv.setStylesId(psid);
					pkv.setProductId(proid);
					pkv.setCreateTime(new Date());
					pkv.setAttributeId(kv.getKey());
					pkv.setKeyId(kv.getValue());
					baseDAO.save(pkv);
				}
			}

		}
		// PC
		else if (OrderFrom.PC.toString().equals(reqSource)) {
			//销售属性key value style kv
			if(product.getStyleList() !=null && product.getStyleList().size()>0){
				List<PProductSellKey> sellKeys = new ArrayList<PProductSellKey>();
				List<PProductSellValue> sellValues= new ArrayList<PProductSellValue>();
				//key
				for (ProductSellKeyVO keyVO : product.getSellKeyList()) {
					PProductSellKey keyModel = new PProductSellKey();
					keyModel.setProductId(proid);
					keyModel.setAttributeName(keyVO.getAttributeName());
					keyModel.setCreateTime(new Date());
					keyModel.setSort(keyVO.getSort());
					
					
					long attributeId = (long) baseDAO.save(keyModel);
					if(attributeId>0)
					{
						keyModel.setAttributeId(attributeId);
						sellKeys.add(keyModel);
						//value
						for (ProductSellValueVO valueVO : keyVO.getSellValueList()) {
							PProductSellValue valueModel= new PProductSellValue();
							valueModel.setAttributeId(attributeId);
							valueModel.setClassId(product.getTypeNo());
							valueModel.setCreateTime(new Date());
							valueModel.setDefaultImg(valueVO.getDefaultImg());
							valueModel.setProductId(proid);
							valueModel.setState((short)1);
							valueModel.setValue(valueVO.getValue());
							
							long keyID = (long) baseDAO.save(valueModel);
							if(keyID>0)
							{
								valueModel.setKeyId(keyID);
								sellValues.add(valueModel);
							}
						}
					}
				}
				//style
				for (ProductStylesVO styleVO : product.getStyleList()) {
					PProductStyles styleModel = new PProductStyles();
					styleModel.setBussinessNo(styleVO.getBussinessNo());
					styleModel.setConmision(styleVO.getConmision());
					styleModel.setCount(styleVO.getCount());
					styleModel.setCreateTime(new Date());
					styleModel.setDefaultImg(ImgDomain.ReplitImgDoMain(styleVO.getDefaultImg()));
					styleModel.setPrice(styleVO.getPrice());
					styleModel.setProductId(proid);
					styleModel.setAgencyPrice(styleVO.getProxyPrice());
					styleModel.setLandPrice(styleVO.getFloorPrice());
					styleModel.setSupplyPrice(styleVO.getSupplyPrice());
					//设置几个价格 城主、副城主、代理 供货价P1、城主价X1、副城主价X2、代理价X3、零售价P2，
					//X1=（P2-P1）*10%+P1，X2=（P2-P1）*10%+X1，X3=（P2-P1）*15%+X2。
					if(Integer.valueOf(PubProductTypeEnum.BrandSupplyer.toString()).equals(userinfo.getPubProductType())) //分销区
					{
						Double d=styleModel.getPrice()-styleModel.getSupplyPrice();
						styleModel.setDukePrice(d*0.1+styleModel.getSupplyPrice());
						styleModel.setDeputyPrice(d*0.1+styleModel.getDukePrice());
						styleModel.setAgentPrice(d*0.15+styleModel.getDeputyPrice());
					}
					long styleID = (long) baseDAO.save(styleModel);
					if(styleID>0){
						for (ProductStyleKVVO kvvo : styleVO.getStyleKVList()) {
							PProductStyleKv styleKv = new PProductStyleKv();
							styleKv.setStylesId(styleID);
							styleKv.setProductId(proid);
							styleKv.setCreateTime(new Date());
							
							for (PProductSellKey key : sellKeys) {
								if(key.getAttributeName().equals(kvvo.getAttrbuteName())){
									styleKv.setAttributeId(key.getAttributeId());
									break;
								}
							}
							for (PProductSellValue value : sellValues) {
								if(value.getValue().equals(kvvo.getValueName())){
									styleKv.setKeyId(value.getKeyId());
									break;
								}
							}
							baseDAO.save(styleKv);					
						}
					}			
				}		
			}else{//如果没有 款式 默认 -1 -1
				PProductSellKey  keyModel = new PProductSellKey();
				keyModel.setAttributeName("-1");
				keyModel.setCreateTime(new Date());
				keyModel.setProductId(proid);
				keyModel.setSort((short)0);
				long attributeId = (long) baseDAO.save(keyModel);
				
				PProductSellValue valueModel = new PProductSellValue();
				valueModel.setClassId(product.getTypeNo());
				valueModel.setCreateTime(new Date());
				valueModel.setProductId(proid);
				valueModel.setState((short)1);
				valueModel.setValue("-1");
				valueModel.setAttributeId(attributeId);
				long keyId=(long) baseDAO.save(valueModel);
				
				PProductStyles styleModel= new PProductStyles();
				styleModel.setConmision(product.getCommission());
				styleModel.setCount(product.getInventory());
				styleModel.setCreateTime(new Date());
				styleModel.setPrice(defaultPrice);
				styleModel.setProductId(proid);		
				styleModel.setAgencyPrice(agentprice);
				styleModel.setLandPrice(storeprice);
				Double costprice=0.0;
				if(product.getCostPrice()!=null && !"".equals(product))
				{
					costprice=Double.parseDouble(product.getCostPrice());
				}
				styleModel.setSupplyPrice(costprice);
				if(Integer.valueOf(PubProductTypeEnum.BrandSupplyer.toString()).equals(userinfo.getPubProductType())) //分销区
				{
					Double d=styleModel.getPrice()-styleModel.getSupplyPrice();
					styleModel.setDukePrice(d*0.1+styleModel.getSupplyPrice());
					styleModel.setDeputyPrice(d*0.1+styleModel.getDukePrice());
					styleModel.setAgentPrice(d*0.15+styleModel.getDeputyPrice());
				}
				long styleID = (long) baseDAO.save(styleModel);
				
				PProductStyleKv styleKVModel = new PProductStyleKv();
				styleKVModel.setAttributeId(attributeId);
				styleKVModel.setCreateTime(new Date());
				styleKVModel.setKeyId(keyId);
				styleKVModel.setProductId(proid);
				styleKVModel.setStylesId(styleID);
				baseDAO.save(styleKVModel);					
			}
			
			// 产品参数值表
			if (product.getParamList() != null
					&& product.getParamList().size() > 0) {
				for (ProductParamVO paramVO : product.getParamList()) {
					// 获取Key value 的系统模板中的ID
					PParamKey paramKey = null;
					if (paramVO.getAttributeID() != null
							&& paramVO.getAttributeID() > 0) {
						paramKey = baseDAO.get(PParamKey.class,
								paramVO.getAttributeID());
					}
					PParamValues paramValue = null;
					if (paramVO.getaVID() != null && paramVO.getaVID() > 0) {
						paramValue = baseDAO.get(PParamValues.class,
								paramVO.getaVID());
					}
					PProductParamKv paramModel = new PProductParamKv();
					if (paramKey != null) {
						paramModel.setAttributeID(paramKey.getAttributeId());
						paramModel.setSysAttributeID(paramKey
								.getSysAttributeID());
					} else {
						paramModel.setAttributeID(0);
						paramModel.setSysAttributeID(0);
					}
					if (paramValue != null) {
						paramModel.setaVID(paramValue.getAvid());
						paramModel.setSysAVID(paramValue.getSysAVID());
					} else {
						paramModel.setaVID(0);
						paramModel.setSysAVID(0);
					}
					paramModel.setCreateTime(new Date());
					paramModel.setParamName(paramVO.getParamName());
					paramModel.setParamValue(paramVO.getParamValue());
					paramModel.setProductId(proid);
					baseDAO.save(paramModel);
				}
			}
			//操作流水表
			PProductRecordMsg msg = new PProductRecordMsg();
			msg.setCreateTime(new Date());
			msg.setProductId(proid);
			msg.setWeiId(product.getSupplyerWeiid());
			if(product.getProductId()>0){
				msg.setContent("修改商品");
			}else{
				if("1".equals(product.getType())){
					msg.setContent("发布商品");
				}else{
					msg.setContent("保存草稿");
				}			
			}
			baseDAO.save(msg);
		}
		
		// modify by tan 没有交钱审核通过的商家也能在上游出现
		if (Integer.valueOf(PubProductTypeEnum.Supplyer.toString()).equals(userinfo.getPubProductType())
				|| Integer.valueOf(PubProductTypeEnum.Platform.toString()).equals(userinfo.getPubProductType())
				|| Integer.valueOf(PubProductTypeEnum.Brand.toString()).equals(userinfo.getPubProductType())
				|| isNoPayShop(userinfo.getWeiId())) // 正式商家更新业务信息索引相关表
		{
			String dhql = " delete from UOwnerMessage u where u.weiId=? and u.keyValue=?";
			Object[] dob = new Object[2];
			dob[0] = userinfo.getWeiId();
			dob[1] = proid;
			baseDAO.executeHql(dhql, dob);

			// 更新业务动态信息表
			if ("1".equals(product.getType())) {
				UOwnerMessage m = new UOwnerMessage();
				m.setCreateTime(new Date());
				m.setKeyValue(proid);
				m.setMessage("发布新品");
				m.setState((short) 0);
				m.setWeiId(userinfo.getWeiId());
				m.setType(Short.parseShort(MsgType.publishproduct.toString()));
				baseDAO.save(m);
			}

			if (userinfo.isYun()) {
				TProductIndex pi = new TProductIndex();
				pi.setProductId(proid);
				pi.setStatus((short) 0);
				if ((isEdit && "0".equals(product.getType()))) {//产品编辑为草稿
					pi.setType((short) 2);// 0新增1更新2删除
					baseDAO.save(pi);
				} else if (isEdit) {//编辑发布
					pi.setType((short) 1);// 0新增1更新2删除
					baseDAO.save(pi);
				} else {
					pi.setType((short) 0);// 0新增1更新2删除
					baseDAO.save(pi);
				}
			}
		}
		if (Integer.valueOf(PubProductTypeEnum.BrandSupplyer.toString()).equals(userinfo.getPubProductType())) // 正式商家更新业务信息索引相关表
		{			
			TProductIndex pi = new TProductIndex();
			pi.setProductId(proid);
			pi.setStatus((short) 0);
			if ((isEdit && "0".equals(product.getType()))) {//产品编辑为草稿
				pi.setType((short) 2);// 0新增1更新2删除
				baseDAO.save(pi);
			} else if (isEdit) {//编辑发布
				pi.setType((short) 1);// 0新增1更新2删除
				baseDAO.save(pi);
			} else {
				pi.setType((short) 0);// 0新增1更新2删除
				baseDAO.save(pi);
			}			
		}
		// 更新辅助表
		PProductAssist pa = baseDAO.get(PProductAssist.class, proid);
		if (pa == null) {
			pa = new PProductAssist();
			pa.setProductId(proid);
			pa.setClassId(product.getTypeNo());
			pa.setEvaluateCount(0);
			pa.setMonthCount(0);
			pa.setSupplierId(userinfo.getWeiId());
			pa.setShelvesCount(0);
			baseDAO.save(pa);
		}
	}
	

	/**
	 * 保存销售模板
	 */
	@Override
	public ReturnModel saveProductSellParamModel(long weino, int classid, String modelName, List<ProductAttributeVO> attributeVO) {
		ReturnModel rq = new ReturnModel();
		try {
			List<ProductSellParam> paramlist = new ArrayList<ProductSellParam>();
			if (attributeVO != null && attributeVO.size() > 0) {
				for (ProductAttributeVO attribute : attributeVO) {
					ProductSellParam itemParam = new ProductSellParam();
					String nameString = attribute.getName();
					itemParam.setName(nameString);
					List<AttributeValueVO> vals = attribute.getVals();
					if (vals != null && vals.size() > 0) {
						List<PProductSellParamValues> valslist = new ArrayList<PProductSellParamValues>();
						for (AttributeValueVO value : vals) {
							PProductSellParamValues va = new PProductSellParamValues();
							va.setValue(value.getValue());
							valslist.add(va);
						}
						itemParam.setVals(valslist);
					}
					paramlist.add(itemParam);
				}
			}
			ResultMsg msgReMsg = saveProductSellParam(weino, classid, modelName, paramlist);
			if (msgReMsg.getStatus() == 1) {
				rq.setStatu(ReturnStatus.Success);
				rq.setStatusreson("保存成功");
			} else {
				rq.setStatu(ReturnStatus.SystemError);
				rq.setStatusreson(msgReMsg.getMsg());
			}
		} catch (Exception e) {
			rq.setStatu(ReturnStatus.SystemError);
			rq.setStatusreson(e.getMessage());
		}
		return rq;
	}
	
	/**
	 * 保存销售属性模板
	 * 
	 * @param weino
	 * @param classid
	 * @param modelName
	 * @param keyParams
	 * @return
	 */
	public ResultMsg saveProductSellParam(long weino, int classid, String modelName, List<ProductSellParam> keyParams) throws Exception {
		ResultMsg resultModelMsg = new ResultMsg();
		if (weino <= 0 || classid <= 0 || "".equals(modelName) || modelName == null) {
			resultModelMsg.setStatus(-1);
			resultModelMsg.setMsg("参数有误");
			return resultModelMsg;
		}

		int mo_id = 0;
		String hql_mod = " from PProductSellParamModel p where p.classId=? and p.supplierWeiId=? and p.state=1 ";
		Object[] b = new Object[2];
		b[0] = classid;
		b[1] = weino;
		PProductSellParamModel cl_model = baseDAO.getUniqueResultByHql(hql_mod, b);
		if (cl_model != null && modelName!=null&& !modelName.equals(cl_model.getMname() )) {
			mo_id = cl_model.getMid();
			cl_model.setMname(modelName);
			baseDAO.update(cl_model);
		} else if (cl_model == null) {
			cl_model = new PProductSellParamModel();
			cl_model.setMname(modelName);
			cl_model.setSupplierWeiId(weino);
			cl_model.setClassId(classid);
			cl_model.setCreateTime(new Date());
			cl_model.setState((short) 1);
			baseDAO.save(cl_model);
			mo_id = cl_model.getMid();
		}
		if (mo_id > 0 && keyParams != null && keyParams.size() > 0) {
			// 删除之前的模板属性
			String hql_key_delString = " delete from PProductSellParamKey p where p.mid=:Mid";
			Map<String, Object> m = new HashMap<String, Object>();
			m.put("Mid", mo_id);
			baseDAO.executeHql(hql_key_delString, m);
			String hql_value_delString = " delete from PProductSellParamValues p where p.attributeId in (select a.attributeId from PProductSellParamKey a where a.mid=:Mid)";

			baseDAO.executeHql(hql_value_delString, m);

			for (int i = 0; i < keyParams.size(); i++) // 遍历 key值
			{
				ProductSellParam jo = keyParams.get(i);
				PProductSellParamKey pkey = new PProductSellParamKey();
				pkey.setCreateTime(new Date());
				pkey.setClassId(classid);
				pkey.setAttributeName(jo.getName());
				pkey.setMid(mo_id);
				pkey.setUpdateTime(new Date());
				pkey.setSort((short) 1);
				pkey.setIsMust((short) 1);
				int la = (int) baseDAO.save(pkey);

				if (jo.getVals() != null && jo.getVals().size() > 0) {
					for (int j = 0; j < jo.getVals().size(); j++) // 遍历value
					{

						PProductSellParamValues val_mod = new PProductSellParamValues();
						val_mod.setAttributeId(la);
						val_mod.setValue(jo.getVals().get(j).getValue());
						val_mod.setCreateTime(new Date());
						val_mod.setUpdateTime(new Date());
						val_mod.setState((short) 1);
						baseDAO.save(val_mod);
					}
				}
			}

		}
		resultModelMsg.setStatus(1);
		resultModelMsg.setMsg("操作成功");

		return resultModelMsg;
	}
	
	@Override
	public int getDownCount(Long weiNo) {
		String shql = "select count(*) from UAttentioned w where w.attTo=? and w.status in (1,2)";
		Object[] ob = new Object[1];
		ob[0] = weiNo;
		Long count = baseDAO.count(shql, ob);
		return ParseHelper.toInt(count.toString(), 0);

	}
	
	@Override
	public int getAgentCount(Long supplyId,Short state) {
		String shql = "select count(*) from UAgentApply where supplyId=? and state=?";
		Object[] ob = new Object[2];
		ob[0] = supplyId;
		ob[1] = state;
		Long count = baseDAO.count(shql, ob);
		return ParseHelper.toInt(count.toString(), 0);

	}
	
	@Override
	public int getStoreCount(Long supplyId) {
		String shql = "select count(*) from UProductShop where supplyId=?";
		Object[] ob = new Object[1];
		ob[0] = supplyId;
		Long count = baseDAO.count(shql, ob);
		return ParseHelper.toInt(count.toString(), 0);

	}

	@Override
	public boolean isEditByMobile(Long productid) {
		String sql = "select distinct Price from P_ProductStyles  where ProductID=" + String.valueOf(productid);
		List<Object[]> list = baseDAO.queryBySql(sql);
		if (list != null && list.size() > 1) {
			return false;
		}
		return true;
	}

	@Override
	public ProductEditInfo getEditProductInfoByID(Long productid, int idtype) throws Exception{
		ProductEditInfo pif = new ProductEditInfo();
		PProducts pro = baseDAO.get(PProducts.class, productid);
		if (pro == null){
			if (Integer.parseInt(PubProductTypeEnum.Platform.toString()) == idtype) {
				return getEditProductSubInfoByID(productid);
			} else {
				return null;
			}
		}
		pif.setDefaultImg(ImgDomain.GetFullImgUrl(pro.getDefaultImg(),24));// 默认图片
		pif.setDescription(pro.getAppdes());// 描述
		pif.setBrandId(pro.getBrandId());
		pif.setStatus(pro.getState());
		//增加原价 add by 阿甘 2016.1.26
		pif.setDisplayPrice(CommonMethod.getInstance().getDisplayPrice(pro.getDefaultPrice(), pro.getOriginalPrice(), pro.getPercent()));
		if (pro.getBrandId() != null && pro.getBrandId() > 0) {
			PBrand brand = baseDAO.get(PBrand.class, pro.getBrandId());
			pif.setBrandName(brand.getBrandName());
		}
		// 获取大类路径
		if (pro.getClassId() != null && pro.getClassId() > 0) {
			int m = 0;
			Map<Integer, Object> mc = new HashMap<Integer, Object>();
			PProductClass pc = baseDAO.get(PProductClass.class, pro.getClassId());
			int classid = pc.getClassId();
			mc.put(m, pc.getClassName());
			while (pc.getParentId() > 1) {
				m += 1;
				PProductClass pctemp = baseDAO.get(PProductClass.class, pc.getParentId());
				mc.put(m, pctemp.getClassName());
				pc = pctemp;
			}
			String stotal = "";
			for (int i = m; i >= 0; i--) {
				if (i == m) {
					stotal = mc.get(i).toString();
				} else {
					stotal += "->" + mc.get(i).toString();
				}
			}
			pif.setTypeNo(classid);
			pif.setTypeName(stotal);
		}
		pif.setProductId(productid);
		if (pro.getSid() != null && pro.getSid() > 0) {
			int m = 0;
			Map<Integer, Object> mc = new HashMap<Integer, Object>();
			PShopClass ps = baseDAO.get(PShopClass.class, pro.getSid());
			int sid = ps.getSid();
			mc.put(m, ps.getSname());
			while (ps.getParetId() != null && ps.getParetId() > 1) {
				m += 1;
				PShopClass pstemp = baseDAO.get(PShopClass.class, ps.getParetId());
				mc.put(m, pstemp.getSname());
				ps = pstemp;
			}
			String stotal = "";
			for (int i = m; i >= 0; i--) {
				if (i == m) {
					stotal = mc.get(i).toString();
				} else {
					stotal += "->" + mc.get(i).toString();
				}
			}
			pif.setCustomTypeNo(sid);
			pif.setCustomTypeName(stotal);
		}

		pif.setPrice(pro.getDefaultPrice() - pro.getDefaultConmision());
		pif.setCommission(pro.getDefaultConmision());
		pif.setInventory(pro.getCount());
		
		if(idtype ==  Integer.parseInt(PubProductTypeEnum.BrandSupplyer.toString()))
		{
			pif.setCostPrice(pro.getSupplyPrice()==null?0.0:pro.getSupplyPrice());
		}
		//平台号、品牌号才有代理价落地价
		if (idtype == 3 || idtype == 4) {
			PProductRelation pr = baseDAO.getUniqueResultByHql(" from PProductRelation where productId=?", new Object[]{productid});
			if (pr != null) {
				pif.setAgentPrice(pr.getProxyPrice());
				pif.setStorePrice(pr.getFloorPrice());
			}
			//招商需求
			UDemandProduct dp = baseDAO.getUniqueResultByHql(" from UDemandProduct where productId=?", new Object[]{productid});
			if (dp != null) {
				USupplyDemand sd = baseDAO.get(USupplyDemand.class, dp.getDemandId());
				Map<String, Object> mca = new HashMap<String, Object>();
				if (sd != null) {
					pif.setDemandName(sd.getTitle());
				}
				pif.setDemandId(dp.getDemandId());
			}
		}
		pif.setProductTitle(pro.getProductTitle()); // 标题
		pif.setProductTitleMin(pro.getProductMinTitle());// 子标题
		// 获取自定义邮费
		if (pro.getFreightId() != null) {
			//自定义邮费
			if (pro.getFreightId() == -1) {
				
			} 
			//
			else {
				PPostAgeModel postage = baseDAO.get(PPostAgeModel.class, pro.getFreightId());
				if (postage != null) {
					pif.setPostFeeId(postage.getFreightId());
					pif.setPostFeeName(postage.getPostAgeName());
				}
			}
		} 
		
		// 图片集
		String imgs = "";
		String hql = " from PProductImg p where p.productId=?";
		Object[] b = new Object[1];
		b[0] = productid;
		List<PProductImg> list = baseDAO.find(hql, b);
		for (int i = 0; i < list.size(); i++) {
			PProductImg pi = list.get(i);
			imgs += ImgDomain.GetFullImgUrl(pi.getImgPath(),24)+",";
		}
		if (imgs != "") {
			pif.setImgs(imgs.substring(0,imgs.length()-1));// 图片集
		}

		// 销售属性
		/*hql = " from PProductSellKey p where p.productId=?";
		List<PProductSellKey> listkey = baseDAO.find(hql, b);
		List<SellAtrrEdit> listedit = new ArrayList<SellAtrrEdit>();
		for (int i = 0; i < listkey.size(); i++) {
			SellAtrrEdit se = new SellAtrrEdit();
			PProductSellKey pk = listkey.get(i);
			// 如果是系统的默认属性模板，则不返回任何参数 modify by tan 20150420
			if ("-1".equals(pk.getAttributeName())) {
				continue;
			}
			se.setName(pk.getAttributeName());
			hql = " from PProductSellValue p where p.attributeId=?";
			b[0] = pk.getAttributeId();
			List<PProductSellValue> listpv = baseDAO.find(hql, b);
			List<Map<String, String>> lm = new ArrayList<Map<String, String>>();
			Map<String, String> mv = null;
			List<String> lj = new ArrayList<String>();// 用来后面判断是否包含在系统模板属性里面的值
			for (int j = 0; j < listpv.size(); j++) {
				PProductSellValue pv = listpv.get(j);
				mv = new HashMap<String, String>();
				mv.put("value", pv.getValue());
				mv.put("isSelect", "1");
				lm.add(mv);
				lj.add(pv.getValue());
			}
			// 加载系统模板
			hql = " from PProductSellParamModel p where p.classId=? and p.supplierWeiId=? and p.state=1";
			Object[] obn = new Object[2];
			obn[0] = pro.getClassId();
			obn[1] = 0L;// 系统默认模板
			PProductSellParamModel pspm = baseDAO.getUniqueResultByHql(hql, obn);
			if (pspm == null) {
				se.setVals(lm);
				listedit.add(se);
				continue;
			}
			hql = " from PProductSellParamKey p where p.mid =? and p.attributeName=?";
			obn[0] = pspm.getMid();
			obn[1] = pk.getAttributeName();
			PProductSellParamKey pspk = baseDAO.getUniqueResultByHql(hql, obn);
			if (pspk == null) {
				se.setVals(lm);
				listedit.add(se);
				continue;
			}
			hql = " from PProductSellParamValues p where p.attributeId=?";
			b[0] = pspk.getAttributeId();
			List<PProductSellParamValues> lpv = baseDAO.find(hql, b);
			for (PProductSellParamValues pv : lpv) {
				if (lj.contains(pv.getValue())) {
					continue;
				}
				mv = new HashMap<String, String>();
				mv.put("value", pv.getValue());
				mv.put("isSelect", "0");
				lm.add(mv);
			}
			se.setVals(lm);
			listedit.add(se);
		}
		// modify by tan 20150420 修改返回默认模板里面的值
		String sellparams = AppSettingUtil.getSingleValue("SellParam");
		String[] sellparam = sellparams.split(",");
		for (String sp : sellparam) {
			boolean bb = true;
			for (SellAtrrEdit sed : listedit) {
				if (sp.equals(sed.getName())) {
					bb = false;
					break;
				}
			}
			if (bb) {
				SellAtrrEdit se = new SellAtrrEdit();
				se.setName(sp);
				List<Map<String, String>> lt = new ArrayList<Map<String, String>>();
				se.setVals(lt);
				listedit.add(se);
			}
		}*/
		// 销售属性
//		pif.setSellAttr(listedit);
		if (idtype != 3 && idtype != 4) {//非平台号、品牌号才有批发价
			if (BitOperation.getIntegerSomeBit(pro.getTag(), 0) == 1) // 有没有批发
			{
				hql = " from PProductBatchPrice p where p.productId=? order by p.pirce desc";
				b[0] = productid;
				List<PProductBatchPrice> listbatch = baseDAO.find(hql, b);
				if (listbatch != null && listbatch.size() > 0) {
					List<BatchPriceVO> batchList = new ArrayList<BatchPriceVO>();
					for (int n = 0; n < listbatch.size(); n++) {
						PProductBatchPrice bp = listbatch.get(n);
						BatchPriceVO batch = new BatchPriceVO();
						batch.setNum(bp.getCount());
						batch.setPrice(bp.getPirce());
						batchList.add(batch);
					}
					pif.setBatchPrice(batchList);
				} 
			} 
		}
		return pif;
	}
	
	@Override
	public List<PPostAgeModel> getPostAgeListByWeiid(long weino)  throws Exception{
		if (weino < 0)
			return null;
		String hqlString = " from PPostAgeModel p where p.supplierWeiId=? and p.status=1 ";
		Object[] b = new Object[1];
		b[0] = weino;
		List<PPostAgeModel> list = baseDAO.find(hqlString, b);
		return list;
	}

	@Override
	public ProductEditInfo getEditProductSubInfoByID(Long productId) {
		ProductEditInfo pif = new ProductEditInfo();
		PProductSup pro = baseDAO.get(PProductSup.class, productId);
		if (pro == null)
			return null;
		pif.setProductTitle(pro.getProductTitle()); // 标题
		pif.setProductTitleMin(pro.getProductMinTitle());// 子标题
		pif.setDefaultImg(ImgDomain.GetFullImgUrl(pro.getDefaultImg(),24));// 默认图片
		pif.setDescription(pro.getAppdes());// 描述
		pif.setBrandId(pro.getBrandID());
		pif.setProductId(productId);
		if (Short.valueOf(ProductSubStatusEnum.Pass.toString()).equals(pro.getState())) {
			pif.setStatus(Short.valueOf("8"));
		} else if (Short.valueOf(ProductSubStatusEnum.Fail.toString()).equals(pro.getState())) {
			pif.setStatus(Short.valueOf("7"));
		} else if (Short.valueOf(ProductSubStatusEnum.Deleted.toString()).equals(pro.getState())) {
			pif.setStatus(Short.valueOf("5"));
		} else if (Short.valueOf(ProductSubStatusEnum.Audit.toString()).equals(pro.getState())) {
			pif.setStatus(Short.valueOf("6"));
		} else if (Short.valueOf(ProductSubStatusEnum.OutLine.toString()).equals(pro.getState())) {
			pif.setStatus(Short.valueOf("3"));
		}
		// 获取大类路径
		if (pro.getClassId() != null && pro.getClassId() > 0) {
			int m = 0;
			Map<Integer, Object> mc = new HashMap<Integer, Object>();
			PProductClass pc = baseDAO.get(PProductClass.class, pro.getClassId());
			int classid = pc.getClassId();
			mc.put(m, pc.getClassName());
			while (pc.getParentId() > 1) {
				m += 1;
				PProductClass pctemp = baseDAO.get(PProductClass.class, pc.getParentId());
				mc.put(m, pctemp.getClassName());
				pc = pctemp;
			}
			String stotal = "";
			for (int i = m; i >= 0; i--) {
				if (i == m) {
					stotal = mc.get(i).toString();
				} else {
					stotal += "," + mc.get(i).toString();
				}
			}
			pif.setTypeNo(classid);
			pif.setTypeName(stotal);
		}
		
		pif.setCostPrice(pro.getDefaultPrice());
		pif.setSuggestPrice(pro.getAdvicePrice());
		pif.setInventory(pro.getStock());
		// 图片集
		String imgs = "";
		String hql = " from PProductImg p where p.productId=?";
		Object[] b = new Object[1];
		b[0] = productId;
		List<PProductImg> list = baseDAO.find(hql, b);
		for (int i = 0; i < list.size(); i++) {
			PProductImg pi = list.get(i);
			imgs += ImgDomain.GetFullImgUrl(pi.getImgPath(),24)+",";
		}
		if (imgs != "") {
			pif.setImgs(imgs.substring(0,imgs.length()-1));// 图片集
		}
		return pif;
	}

	@Override
	public PageResult<PostAgeModelVO> getPostAgeModelPageResult(Long weiID,
			Integer pageIndex, Integer pageSize) {
		Limit limit = Limit.buildLimit(pageIndex, pageSize);
		List<PPostAgeModel> list = null;
		if (pageIndex <= 1) {
			list = baseProductDao.findPostAgeList(0L);
		}
		PageResult<PPostAgeModel> pageResult = baseProductDao.findPostAgePageResult(weiID,limit);
		if (pageResult == null) {
			return new PageResult<PostAgeModelVO>();
		}
		List<PostAgeModelVO> paList = new ArrayList<PostAgeModelVO>();
		if (list != null) {
			for (PPostAgeModel pag : list) {
				PostAgeModelVO vo = new PostAgeModelVO();
				vo.setFreightId(pag.getFreightId());
				vo.setFreightName(pag.getPostAgeName());
				vo.setTypeName("系统模板1");
				vo.setType(0);
				paList.add(vo);
			}
		}
		for (PPostAgeModel pa : pageResult.getList()) {
			PostAgeModelVO vo = new PostAgeModelVO();
			vo.setFreightId(pa.getFreightId());
			vo.setFreightName(pa.getPostAgeName());
			vo.setTypeName("");
			vo.setType(1);
			paList.add(vo);
		}
		return new PageResult<PostAgeModelVO>(pageResult.getTotalCount()+2,limit,paList);
	}
	
	/**
	 * 平台号、品牌号发布的产品上架到招商需求下的代理商、落地点
	 * @param demandId 招商需求
	 * @param oldDemandId 老的招商需求
	 * @param weiId 平台品牌号 
	 * @param productId 产品id
	 * @throws Exception
	 */
	public void shelveToAgeStoreBydomeId(Integer demandId,Integer oldDemandId,Long weiId,PProducts product,boolean sehlveType) throws Exception {
		//代理商
		Long[] agents = baseProductDao.getAgentOrStoreWeiIdsBySupId(weiId,demandId,Short.valueOf(SupplyChannelTypeEnum.Agent.toString()),Short.valueOf(AgentStatusEnum.Normal.toString()));
		//落地店
		Long[] stores = baseProductDao.getAgentOrStoreWeiIdsBySupId(weiId,demandId,Short.valueOf(SupplyChannelTypeEnum.ground.toString()),Short.valueOf(AgentStatusEnum.Normal.toString()));;
		if (agents != null && agents.length > 0) {
			baseProductDao.setShelveProduct(agents, product, ShelveType.Proxy.getNo());
		}
		if (stores != null && stores.length > 0) {
			baseProductDao.setShelveProduct(stores, product, ShelveType.floor.getNo());
		}
		//产品招商需求发生改变、下架老的需求的产品
		if (sehlveType && oldDemandId != null && oldDemandId > 0) {
			Long[] oldAgents = baseProductDao.getAgentWeiIdsBySupId(weiId,oldDemandId,Short.valueOf(ApplyAgentStatusEnum.Pass.toString()));
			Long[] oldStores = baseProductDao.getStoreWeiIdsBySupId(weiId, oldDemandId);
			if (oldAgents != null && oldAgents.length > 0) {
				baseProductDao.UPPClassProductsByCondition(oldAgents, product.getProductId(), Short.parseShort(ProductShelveStatu.OffShelf.toString()), ShelveType.Proxy.getNo());
			}
			if (oldStores != null && oldStores.length > 0) {
				baseProductDao.UPPClassProductsByCondition(oldStores, product.getProductId(), Short.parseShort(ProductShelveStatu.OffShelf.toString()), ShelveType.floor.getNo());
			}
		}
		
	}
	/**
	 * 上架、下架某个系列下的产品到某一个代理商或者落地店
	 * @param channelType 代理商或者落地店 枚举类SupplyChannelTypeEnum
	 * @param ShelveStatu上架或者 枚举类ProductShelveStatu
	 * @param demandId 系列id
	 * @param platformWid 平台号或品牌号weiid
	 * @param weiId 代理商或者落地店weiid
	 * @return
	 */
	@Override
	public void shelveProductToAgeStore(Integer demandId,Long platformWid,Long weiId,Short channelType,Short shelveStatu) throws Exception{
		if (demandId == null || weiId == null || channelType == null) {
			throw new Exception("参数错误");
		}
		List<PProducts> productList = baseProductDao.findProductsListByDemandId(demandId);
		baseProductDao.setShelveProducts(productList, channelType, shelveStatu, platformWid,weiId);
	}
	
	public void maintainProductClassTemp(Integer classid){
		if (classid != null && classid > 0) {
			PProductClassTemp temp = baseProductDao.get(PProductClassTemp.class, classid);
			if (temp == null) {
				PProductClass pcthree = baseProductDao.get(PProductClass.class, classid);
				if (pcthree != null) {
					if (Short.valueOf("3").equals(pcthree.getStep())) {
						if (baseProductDao.get(PProductClassTemp.class, pcthree.getClassId()) == null) {
							PProductClassTemp tp3 = new PProductClassTemp();
							tp3.setClassId(pcthree.getClassId());
							tp3.setClassName(pcthree.getClassName());
							tp3.setParentId(pcthree.getParentId());
							tp3.setHeadImg(pcthree.getHeadImg());
							tp3.setSeodes(pcthree.getSeodes());
							tp3.setSeokeyword(pcthree.getSeokeyword());
							tp3.setSeotitle(pcthree.getSeotitle());
							tp3.setSort(pcthree.getSort());
							tp3.setStep(pcthree.getStep());
							baseProductDao.save(tp3);
							if (pcthree.getParentId() != null) {
								PProductClass pctwo = baseProductDao.get(PProductClass.class, pcthree.getParentId());
								if (pctwo != null) {
									if (baseProductDao.get(PProductClassTemp.class, pctwo.getClassId()) == null) {
										PProductClassTemp tp2 = new PProductClassTemp();
										tp2.setClassId(pctwo.getClassId());
										tp2.setClassName(pctwo.getClassName());
										tp2.setParentId(pctwo.getParentId());
										tp2.setHeadImg(pctwo.getHeadImg());
										tp2.setSeodes(pctwo.getSeodes());
										tp2.setSeokeyword(pctwo.getSeokeyword());
										tp2.setSeotitle(pctwo.getSeotitle());
										tp2.setSort(pctwo.getSort());
										tp2.setStep(pctwo.getStep());
										baseProductDao.save(tp2);
									}
									if (pctwo.getParentId() != null) {
										PProductClass pcone = baseProductDao.get(PProductClass.class, pctwo.getParentId());
										if (pcone != null) {
											if (baseProductDao.get(PProductClassTemp.class, pcone.getClassId()) == null) {
												PProductClassTemp tp1 = new PProductClassTemp();
												tp1.setClassId(pcone.getClassId());
												tp1.setClassName(pcone.getClassName());
												tp1.setParentId(pcone.getParentId());
												tp1.setHeadImg(pcone.getHeadImg());
												tp1.setSeodes(pcone.getSeodes());
												tp1.setSeokeyword(pcone.getSeokeyword());
												tp1.setSeotitle(pcone.getSeotitle());
												tp1.setSort(pcone.getSort());
												tp1.setStep(pcone.getStep());
												baseProductDao.save(tp1);
											}
											if (pcone.getParentId() != null) {
												PProductClass pczero = baseProductDao.get(PProductClass.class, pcone.getParentId());
												if (pczero != null) {
													if (baseProductDao.get(PProductClassTemp.class, pczero.getClassId()) == null) {
														PProductClassTemp tp = new PProductClassTemp();
														tp.setClassId(pczero.getClassId());
														tp.setClassName(pczero.getClassName());
														tp.setParentId(pczero.getParentId());
														tp.setHeadImg(pczero.getHeadImg());
														tp.setSeodes(pczero.getSeodes());
														tp.setSeokeyword(pczero.getSeokeyword());
														tp.setSeotitle(pczero.getSeotitle());
														tp.setSort(pczero.getSort());
														tp.setStep(pczero.getStep());
														baseProductDao.save(tp);
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	@Override
	public List<P_PrductsClassList> findYunProductClass() throws Exception{
		// 一级分类集合
		List<PProductClassTemp> find_ProductClass = baseProductDao.find_ProductClass(null,new Short("1"));
		// 二级分类集合
		List<PProductClassTemp> find_ProductClass2 = baseProductDao.find_ProductClass(null,new Short("2"));
		List<P_PrductsClassList> listpc=new ArrayList<P_PrductsClassList>();
		if (find_ProductClass != null && find_ProductClass.size() > 0) {
			for (PProductClassTemp pProductClass : find_ProductClass) {
				P_PrductsClassList pc = new P_PrductsClassList();
				P_PrductsClass p = new P_PrductsClass();
				p.setcId(pProductClass.getClassId());
				p.setStep(pProductClass.getStep());
				p.setcName(pProductClass.getClassName());
				pc.setPrductsClass(p);
				List<P_PrductsClass> list = new ArrayList<P_PrductsClass>();
				if (find_ProductClass2 != null && find_ProductClass2.size() > 0) {
					for (PProductClassTemp pProductClass1 : find_ProductClass2) {
						if (pProductClass.getClassId().intValue() == pProductClass1.getParentId().intValue()) {
							P_PrductsClass pp = new P_PrductsClass();
							pp.setcId(pProductClass1.getClassId());
							pp.setStep(pProductClass1.getStep());
							pp.setcName(pProductClass1.getClassName());
							list.add(pp);
						}
					}
				}
				pc.setList(list);
				listpc.add(pc);
			}
		}
		return listpc;
	}

}
