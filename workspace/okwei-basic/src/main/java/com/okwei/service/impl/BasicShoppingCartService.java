package com.okwei.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.okwei.bean.domain.AActProductsShowTime;
import com.okwei.bean.domain.AActShowProducts;
import com.okwei.bean.domain.AActivityProducts;
import com.okwei.bean.domain.DAgentInfo;
import com.okwei.bean.domain.DBrands;
import com.okwei.bean.domain.DCastellans;
import com.okwei.bean.domain.OOrderAddr;
import com.okwei.bean.domain.OPayOrder;
import com.okwei.bean.domain.OPayOrderLog;
import com.okwei.bean.domain.OProductOrder;
import com.okwei.bean.domain.OSupplyerOrder;
import com.okwei.bean.domain.PClassProducts;
import com.okwei.bean.domain.PPostAgeDetails;
import com.okwei.bean.domain.PProductBatchPrice;
import com.okwei.bean.domain.PProductSellKey;
import com.okwei.bean.domain.PProductSellValue;
import com.okwei.bean.domain.PProductStyleKv;
import com.okwei.bean.domain.PProductStyles;
import com.okwei.bean.domain.PProducts;
import com.okwei.bean.domain.PShevleBatchPrice;
import com.okwei.bean.domain.SMainData;
import com.okwei.bean.domain.TShoppingCar;
import com.okwei.bean.domain.UCustomerAddr;
import com.okwei.bean.domain.UDemandProduct;
import com.okwei.bean.domain.UProductAgent;
import com.okwei.bean.domain.UProductShop;
import com.okwei.bean.domain.USupplyDemand;
import com.okwei.bean.domain.USupplyer;
import com.okwei.bean.domain.UTicketUseLog;
import com.okwei.bean.domain.UUserAssist;
import com.okwei.bean.domain.UWallet;
import com.okwei.bean.domain.UWeiSeller;
import com.okwei.bean.enums.ActProductVerState;
import com.okwei.bean.enums.FromTypeEnum;
import com.okwei.bean.enums.OrderDel;
import com.okwei.bean.enums.OrderStatusEnum;
import com.okwei.bean.enums.OrderTypeEnum;
import com.okwei.bean.enums.ProductStatusEnum;
import com.okwei.bean.enums.ShoppingCarSourceEnum;
import com.okwei.bean.enums.ShoppingCartTypeEnum;
import com.okwei.bean.enums.SupplierTypeEnum;
import com.okwei.bean.enums.SupplyOrderType;
import com.okwei.bean.enums.UTicketUseTypeEnum;
import com.okwei.bean.enums.UserIdentityType;
import com.okwei.bean.enums.VerifyCodeType;
import com.okwei.bean.vo.LimitCountVO;
import com.okwei.bean.vo.activity.ActivityModel;
import com.okwei.bean.vo.order.BAddressVO;
import com.okwei.bean.vo.order.BKuaiDi;
import com.okwei.bean.vo.order.BReturnOdertInfo;
import com.okwei.bean.vo.order.BSendSMSVO;
import com.okwei.bean.vo.order.BShoppingCartProductVO;
import com.okwei.bean.vo.order.BShoppingCartVO;
import com.okwei.common.CommonMethod;
import com.okwei.dao.IBaseDAO;
import com.okwei.dao.agent.IDAgentMgtDao;
import com.okwei.dao.product.IBaseProductDao;
import com.okwei.dao.user.IUUserAssistMgtDAO;
import com.okwei.service.IBasicShoppingCartService;
import com.okwei.service.activity.IBaseActivityService;
import com.okwei.service.agent.IDAgentService;
import com.okwei.service.order.IBasicPayService;
import com.okwei.util.AppSettingUtil;
import com.okwei.util.BitOperation;
import com.okwei.util.DateUtils;
import com.okwei.util.GenerateOrderNum;
import com.okwei.util.ImgDomain;
import com.okwei.util.ObjectUtil;
import com.okwei.util.ParseHelper;
import com.okwei.util.RedisUtil;
import com.okwei.util.SendSMSByMobile;

@Component("BasicShoppingCartService")
public class BasicShoppingCartService extends BaseService implements IBasicShoppingCartService {
	@Autowired
	private IBaseDAO baseDAO;
	@Autowired
	private IBasicPayService payService;
	@Autowired
	private IBaseProductDao productDao;
	@Autowired
	private IBaseActivityService activityService;
	@Autowired
	private IUUserAssistMgtDAO userAssistMgtDAO;

	@Autowired
	private IDAgentService agentService;
	@Autowired
	private IDAgentMgtDao agentDao;
	
	/**
	 * 订单拆单操作
	 * 
	 * @param sellerlist
	 * @param address
	 * @param weiid
	 * @return
	 */
	private List<BShoppingCartVO> cartProcess(List<BShoppingCartVO> sellerlist, BAddressVO address, long weiid) {
		if (sellerlist == null || sellerlist.size() <= 0)
			return null;
		List<BShoppingCartVO> resultList = new ArrayList<BShoppingCartVO>();
		for (BShoppingCartVO mo : sellerlist) {// 拆单操作
			if (mo == null || mo.getProductList() == null || mo.getProductList().size() <= 0) {
				continue;
			}
			mo.setBuyerIdentity(0); 
			boolean isNomal = true;// 是否为 普通供应商（非平台号体系）

			// 验证卖家是否为平台号（如果购物车发货人存落地店，此处要判断身份)
			UUserAssist seAssist = baseDAO.get(UUserAssist.class, mo.getSupplierId());
			if (seAssist != null && seAssist.getIdentity() != null) {
				if (BitOperation.verIdentity(seAssist.getIdentity(), UserIdentityType.PlatformSupplier) || BitOperation.verIdentity(seAssist.getIdentity(), UserIdentityType.BrandSupplier)) {
					isNomal = false;
				}
			}
			// 当发货人是 平台号、品牌号体系时，
			// 如果产品的发货人不一致，生成不同的OSupplyOrder
			if (!isNomal) {// 平台号、品牌号
				Map<String, List<BShoppingCartProductVO>> sellerweiids = new HashMap<String, List<BShoppingCartProductVO>>();
				for (BShoppingCartProductVO pp : mo.getProductList()) {
					String sellerId = getSellerWeiid(weiid, pp.getProNum(), address.getProvince(), address.getCity(), address.getDistrict());
					if (!sellerweiids.containsKey(sellerId)) {
						List<BShoppingCartProductVO> subList = new ArrayList<BShoppingCartProductVO>();
						subList.add(pp);
						sellerweiids.put(sellerId, subList);
					} else {
						sellerweiids.get(sellerId).add(pp);
					}
				}
				for (Map.Entry<String, List<BShoppingCartProductVO>> entry : sellerweiids.entrySet()) {
					String weiNoStr = entry.getKey();
					BShoppingCartVO mo2 = new BShoppingCartVO();
					mo2.setSupplierId(ParseHelper.toLong(weiNoStr)); 
					mo2.setLogisticList(mo.getLogisticList()); 
					mo2.setSupplierName(mo.getSupplierName());
					if (ParseHelper.toLong(weiNoStr) <= 0) {
						mo2.setSubNo(weiNoStr);// 子账号
					}
					mo2.setProductList(entry.getValue());
					mo2.setCurrentLogisticId(mo.getCurrentLogisticId());
					mo2.setIsPlatform(1);
					mo2.setBuyType(mo.getBuyType());
					mo2.setDemandId(mo.getDemandId());
					mo2.setSource(mo.getSource()); 
					mo2.setBuyerIdentity(0); 
					mo2.setMessage(mo.getMessage()); 
					mo2.setReferrerId(mo.getReferrerId()); 
					if (entry.getValue() != null && entry.getValue().size() > 0) {// 判断买家是否
																					// 是产品的代理商、落地店
						int usertype = getUserType(weiid, entry.getValue().get(0).getProNum());
						mo2.setBuyerIdentity(usertype);
					}
					resultList.add(mo2);
				}
			} else {
				if(mo.getSource()!=null&&mo.getSource()==Short.parseShort(ShoppingCarSourceEnum.share.toString())){
					//如果是分销区的产品（根据分销人 拆单）
					Map<String,List<BShoppingCartProductVO>> map=new HashMap<String, List<BShoppingCartProductVO>>();
					for (BShoppingCartProductVO pp : mo.getProductList()) {
						String keyName=mo.getSupplierId()+"_"+pp.getShareOne();
						if(map.containsKey(keyName)){
							List<BShoppingCartProductVO> list=map.get(keyName);
							list.add(pp);
							map.put(keyName, list);
						}else {
							List<BShoppingCartProductVO> list=new ArrayList<BShoppingCartProductVO>();
							list.add(pp);
							map.put(keyName, list);
						}
					}
					for (List<BShoppingCartProductVO> ll : map.values()) {
						BShoppingCartVO cartVO=new BShoppingCartVO();
						cartVO.setSupplierId(mo.getSupplierId()); 
						cartVO.setLogisticList(mo.getLogisticList()); 
						cartVO.setSupplierName(mo.getSupplierName());
						cartVO.setCurrentLogisticId(mo.getCurrentLogisticId());
						cartVO.setIsPlatform(mo.getIsPlatform());
						cartVO.setBuyType(mo.getBuyType());
						cartVO.setDemandId(mo.getDemandId());
						cartVO.setSource(mo.getSource()); 
						cartVO.setBuyerIdentity(mo.getBuyerIdentity()); 
						cartVO.setMessage(mo.getMessage()); 
						cartVO.setReferrerId(mo.getReferrerId()); 
						cartVO.setProductList(ll);
						resultList.add(cartVO);
					}
				}else {
					resultList.add(mo);
				}
			}
		}/*-------结束拆单操作---------*/
		return resultList;
	}

	@Override
	public BReturnOdertInfo savePlaceOrder(List<BShoppingCartVO> sellerlist, long weiid, BAddressVO address, FromTypeEnum orderFrom) {
		BReturnOdertInfo result = new BReturnOdertInfo();
		if (sellerlist == null || sellerlist.size() <= 0 || address == null || address.getAddressId() == null) {
			result.setState(-1);
			result.setMsg("参数有误");
			return result;
		}
		UCustomerAddr addr = baseDAO.get(UCustomerAddr.class, ParseHelper.toInt(address.getAddressId()));
		if (addr == null || addr.getWeiId() != weiid) {
			result.setState(-1);
			result.setMsg("收货地址有误");
			return result;
		}
		address.setProvince(addr.getProvince());
		address.setCity(addr.getCity()); 
		address.setDistrict(addr.getDistrict());
		// 生成支付订单号
		String payOrderId = GenerateOrderNum.getInstance().GenerateOrder();
		String supplyOrderIdsString = "";// 支付快照 ，支付单对应的供应商订单号
		double totalPrice = 0, totalYoufei = 0;// totalCommission=0;//总价，总邮费
		int orderType = 0;
		Date nowTime = new Date();
		//订单拆单操作
		sellerlist = cartProcess(sellerlist, address, weiid);
		if (sellerlist == null || sellerlist.size() <= 0) {
			result.setState(-1);
			result.setMsg("产品列表不能为空");
			return result;
		}
		// ------生成订单收货地址----------
		OOrderAddr orderAddr = new OOrderAddr();
		orderAddr.setCaddrId(ParseHelper.toInt(address.getAddressId()));
		orderAddr.setProvince(address.getProvince());
		orderAddr.setCity(address.getCity());
		orderAddr.setDistrict(address.getDistrict());
		orderAddr.setDetailAddr(addr.getDetailAddr());
		orderAddr.setMobilePhone(addr.getMobilePhone());
		orderAddr.setWeiId(addr.getWeiId());
		orderAddr.setReceiverName(addr.getReceiverName());
		orderAddr.setCreateTime(nowTime);
		orderAddr.setQq(addr.getQq());
		if(!ObjectUtil.isEmpty( address.getIdCard())){
			orderAddr.setIdCard(address.getIdCard());
		} 
		baseDAO.save(orderAddr);
		// 供应商订单下单操作
		for (BShoppingCartVO mo : sellerlist) {
			if (mo.getProductList() == null || mo.getProductList().size() <= 0) {
				continue;
			}
			if(mo.getBuyType()==null){
				result.setState(-1);
				result.setMsg("订单类型有误413！");
				return result;
			}
			String supplyOrderId = GenerateOrderNum.getInstance().GenerateOrder();
			OSupplyerOrder supplyOrder = new OSupplyerOrder();
			supplyOrder.setAddrId(orderAddr.getOrderAddrId());// ParseHelper.toLong(address.getAddressId())
			supplyOrder.setSupplierOrderId(supplyOrderId);
			supplyOrder.setPayOrderId(payOrderId);
			supplyOrder.setOrderTime(nowTime);
			supplyOrder.setOrderDate(nowTime);
			supplyOrder.setSupplyerId(mo.getSupplierId()); 
			supplyOrder.setSellerWeiId(mo.getProductList().get(0).getBuyShopId()); //卖家（可能是供应商、可能是代理、可能是店铺）
			supplyOrder.setMessage(mo.getMessage());
			supplyOrder.setBuyerID(weiid);
			supplyOrder.setSellerDel(Short.parseShort(com.okwei.bean.enums.OrderDel.NoDel.toString()));
			supplyOrder.setBuyerDel(Short.parseShort(com.okwei.bean.enums.OrderDel.NoDel.toString()));
			supplyOrder.setOrderFrom(Short.parseShort(orderFrom.toString()));
			supplyOrder.setState(Short.parseShort(OrderStatusEnum.Nopay.toString()));// 等待付款			
			supplyOrder.setIsActivity((short)0);//默认不参加活动
			
			if(mo.getReferrerId()!=null&&!"".equals(mo.getReferrerId())){//来源微店号（落地区的产品、首单）
				supplyOrder.setVerWeiId(ParseHelper.toLong(mo.getReferrerId())); 
			}else {
				supplyOrder.setVerWeiId(11111l); 
				supplyOrder.getVerWeiId();
			}
			supplyOrder.setSourse(mo.getSource()); 
			supplyOrder.setMessage(mo.getMessage());
			
			/*--------2016-7-8  分销（代理）区的订单---------================================-----*/
			DBrands brand= agentService.getBrands(mo.getSupplierId());
			if(brand!=null&&mo.getSource()!=null&&mo.getSource()==Integer.parseInt(ShoppingCarSourceEnum.share.toString())){
				//代理区 的订单
				orderType=Integer.parseInt(SupplyOrderType.RetailAgent.toString()); 
				supplyOrder.setOrderType(orderType);
				
			}/*----===================================================-*/
			// TODO 验证买家下单的权限
			// 1、是否有铺货权限 ，2、是否有批发权限。。。
			else if (mo.getBuyType() == Short.parseShort(ShoppingCartTypeEnum.Puhuo.toString())) { // 铺货单类型
				if (mo.getBuyerIdentity() <= 0) {
					result.setState(-1);
					result.setMsg("您还不是该产品的落地店或者代理商，没有铺货权限！");
					return result;
				}
				orderType = Integer.parseInt(OrderTypeEnum.Puhuo.toString());
				supplyOrder.setState(Short.parseShort(OrderStatusEnum.WaitSure.toString()));// 等待确认

			} else if (mo.getBuyType() == Short.parseShort(ShoppingCartTypeEnum.Retail.toString())) {// 零售订单
				long sellerweiid = mo.getSupplierId();
				if (mo.getIsPlatform() > 0) {// plater != null
					orderType = Integer.parseInt(OrderTypeEnum.RetailPTH.toString());
				} else {
					USupplyer yunSupplier = baseDAO.get(USupplyer.class, sellerweiid);
					if (yunSupplier != null) {
						if (BitOperation.isSupply(yunSupplier.getType(), SupplierTypeEnum.YunSupplier)) {
							orderType = Integer.parseInt(OrderTypeEnum.Pt.toString());
						} else if (BitOperation.isSupply(yunSupplier.getType(), SupplierTypeEnum.BatchSupplier)) {
							orderType = Integer.parseInt(OrderTypeEnum.BatchOrder.toString());
						} else {
							UUserAssist assist=baseDAO.get(UUserAssist.class, sellerweiid);
							if(assist!=null&&assist.getIdentity()!=null&&(BitOperation.isIdentity(assist.getIdentity(), UserIdentityType.Agent)||BitOperation.isIdentity(assist.getIdentity(), UserIdentityType.Ground))){
								orderType = Integer.parseInt(OrderTypeEnum.Pt.toString());
							}else {
								result.setState(-1);
								result.setMsg("订单类型有误403");
								return result;
							}
						}
					}else {
						UUserAssist assist=baseDAO.get(UUserAssist.class, sellerweiid);
						if(assist!=null&&assist.getIdentity()!=null&&(BitOperation.isIdentity(assist.getIdentity(), UserIdentityType.Agent)||BitOperation.isIdentity(assist.getIdentity(), UserIdentityType.Ground)||BitOperation.isIdentity(assist.getIdentity(), UserIdentityType.AgentBrandSupplier))){
							orderType = Integer.parseInt(OrderTypeEnum.Pt.toString());
						}else {
							result.setState(-1);
							result.setMsg("订单类型有误403");
							return result;
						}
					}
				}
			} else if (mo.getBuyType() == Short.parseShort(ShoppingCartTypeEnum.Wholesale.toString())) {// 批发订单
				orderType = Integer.parseInt(OrderTypeEnum.BatchWholesale.toString());
			} else if (mo.getBuyType() == Short.parseShort(ShoppingCartTypeEnum.Jinhuo.toString())) {// 进货单
				orderType = Integer.parseInt(OrderTypeEnum.Jinhuo.toString());
				if (mo.getBuyerIdentity() <= 0) {
					//  非落地店、代理商 订单有要求
					if (mo.getDemandId()==null||"".equals(mo.getDemandId())) {
						result.setState(-1);
						result.setMsg("数据有误407");
						return result;
					}
					USupplyDemand demandMod = baseDAO.get(USupplyDemand.class, ParseHelper.toInt(mo.getDemandId()));
					if (demandMod != null && demandMod.getOrderAmout() != null && demandMod.getOrderAmout() > 0) {
						double pricePro = 0d;
						for (BShoppingCartProductVO pro : mo.getProductList()) {
							pricePro += pro.getProductPrice() * pro.getCount();
						}
						if (pricePro < demandMod.getOrderAmout()) {
							result.setState(-1);
							result.setMsg("订单金额需满" + demandMod.getOrderAmout() + "元才能在落地区下单。");
							return result;
						}
					}else {
						result.setState(-1);
						result.setMsg("订单类型有误202。");
						return result;
					}
				}
			} else {
				result.setState(-1);
				result.setMsg("订单类型有误");
				return result;
			}
			supplyOrder.setOrderType(orderType);// 设置供应商订单类型

			if(mo.getLogisticList()==null||mo.getLogisticList().size()<=0){
				mo= getCartModel(mo, address);
			}
			
			Double youfei=0d;
			if(mo.getLogisticList()!=null&&mo.getLogisticList().size()>0){
				if(mo.getCurrentLogisticId()==null||"0".equals(mo.getCurrentLogisticId())){
					mo.setCurrentLogisticId("1"); 
				}
				if(mo.getLogisticList().size()==1){
					youfei=mo.getLogisticList().get(0).getLogisticPrice();
				}else {
					for (BKuaiDi  kk: mo.getLogisticList()) {
						if(kk.getLogisticId().equals(mo.getCurrentLogisticId())){
							youfei=kk.getLogisticPrice().doubleValue();
							continue;
						}
					}
				}
				
			}else {
				result.setState(-1);
				result.setMsg("运费信息有误444！");
				return result;
			}
			double priceTotal = 0;// 供应商产品总价
			double commissionTotal = 0;
			supplyOrder.setPostage(youfei);// 设置邮费
			for (BShoppingCartProductVO pp : mo.getProductList()) {
				//购物车购买
				long scidTemp=ParseHelper.toLong( pp.getScid());
				if (scidTemp> 0) {
					TShoppingCar car = baseDAO.get(TShoppingCar.class,scidTemp);
					if (car == null) {
						result.setState(-1);
						result.setMsg("您的购物车没有此商品410！");
						return result;
					}
					pp.setSharePageId(car.getShareID());
					pp.setShareOne(car.getSharerWeiID());
					pp.setSharePageProducer(car.getMakerWeiID()); 
					baseDAO.delete(car);
				}
				
				/*-------------单个产品的订单操作（OProductOrder）-----------------------*/
				PProducts product = baseDAO.get(PProducts.class, pp.getProNum());
				if (product != null && product.getState() != null && product.getState() == Short.parseShort(ProductStatusEnum.Showing.toString()) && pp.getCount() > 0 && ParseHelper.toInt(pp.getProductStyleId()) > 0) {
					PProductStyles styles = baseDAO.get(PProductStyles.class, ParseHelper.toLong( pp.getProductStyleId()));
					if (styles != null) {
						String productOrderId = GenerateOrderNum.getInstance().GenerateOrder();
						OProductOrder productOrder = new OProductOrder();
						productOrder.setSupplierOrderId(supplyOrderId);
						if (mo.getBuyType() != Integer.parseInt(ShoppingCartTypeEnum.Presell.toString())) {
							productOrder.setPayOrderId(payOrderId);
						}
						productOrder.setProductOrderId(productOrderId);
						productOrder.setSupplyeriId(mo.getSupplierId());
						productOrder.setCreateTime(nowTime);
						productOrder.setProductId(pp.getProNum());
						productOrder.setStyleId(ParseHelper.toLong(pp.getProductStyleId()));
						productOrder.setStyleDes(getProductStyleName(pp.getProNum(), ParseHelper.toLong(pp.getProductStyleId())));
						productOrder.setBuyerId(weiid);// 买家
						
						//分享页id /分享制作人  /分享人-------------------------------------
						productOrder.setShareID(pp.getSharePageId());
						productOrder.setMakerWeiId(pp.getSharePageProducer());//分享制作人
						if(pp.getSharePageProducer()==null)
						{
							if(productOrder.getShareID()!=null&&productOrder.getShareID()>0){
								SMainData sm=baseDAO.get(SMainData.class, pp.getSharePageId());
								if(sm!=null){
									productOrder.setMakerWeiId(sm.getWeiId()); //制作人
								}
							}else
							{
								productOrder.setMakerWeiId(111L);
							}
						}
						
						productOrder.setShareWeiId(pp.getShareOne());
						if(null!=pp.getShareOne() && pp.getShareOne()>0)
						{
							UWeiSeller shareSeller = baseDAO.get(UWeiSeller.class, pp.getShareOne());
							if (shareSeller != null && shareSeller.getSponsorWeiId() != null){
								productOrder.setSharerUpWeiId(shareSeller.getSponsorWeiId());
							}else{
								productOrder.setSharerUpWeiId(111L);
							}
						}
						
						// 成交微店
						long shopWeiid = pp.getBuyShopId()==null?0:pp.getBuyShopId();
						/*---------===================================================--------*/
						if(brand!=null&&mo.getSource()!=null&&mo.getSource()==3){//品牌号订单（分销区订单）
							long agentWeiid=mo.getSupplierId();
							DAgentInfo agentRef= agentDao.getDAgentInfo(pp.getShareOne(),  brand.getBrandId());
							if(agentRef!=null){
								agentWeiid=pp.getShareOne();
							}
							supplyOrder.setSellerWeiId(agentWeiid);//成交微店（城主、代理商）
						}
						//-------------------------------===============================================
						boolean isOkShopWeiid = false;
						if (shopWeiid > 0) {
							UWeiSeller shopSeller = baseDAO.get(UWeiSeller.class, shopWeiid);
							if (shopSeller != null && shopSeller.getSponsorWeiId() != null) {
								isOkShopWeiid = true;
								productOrder.setShopWeiID(shopWeiid);
								productOrder.setSellerWeiid(shopWeiid);
								productOrder.setSellerUpweiid(shopSeller.getSponsorWeiId());
							}
						}
						if (!isOkShopWeiid) {
							productOrder.setShopWeiID(weiid);
							productOrder.setSellerWeiid((long) 111);
							productOrder.setSellerUpweiid((long) 111);
						}
						productOrder.setState(Short.parseShort(OrderStatusEnum.Nopay.toString()));// 默认待付款
						productOrder.setProdcutTitle(product.getProductTitle());
						productOrder.setProductImg(ImgDomain.GetFullImgUrl(product.getDefaultImg()));
						productOrder.setCount(pp.getCount());
						productOrder.setClassId(product.getClassId());
						productOrder.setProductMinTitle(product.getProductMinTitle());
						productOrder.setSellerDel(Short.parseShort(OrderDel.NoDel.toString()));
						productOrder.setBuyerDel(Short.parseShort(OrderDel.NoDel.toString()));
						// 订单类型
						productOrder.setOrderType((short) orderType);
						if (mo.getBuyType() == Short.parseShort(ShoppingCartTypeEnum.Wholesale.toString())) {
							List<PProductBatchPrice> ppbplist = getBatchPricess(mo.getSupplierId(), pp.getProNum());// 获取所有的批发价格
							if (ppbplist != null && ppbplist.size() > 0) {
								int countPr=0;//批发数量
								for (BShoppingCartProductVO dd : mo.getProductList()) {
									if(dd.getProNum().longValue()==pp.getProNum().longValue()){
										countPr+=dd.getCount();
									}
								}
								Double jiageDouble = getshoppcartpricebycount(countPr, ppbplist);// 初始化批发价
								if (jiageDouble > 0) {
									priceTotal += jiageDouble * pp.getCount();
									productOrder.setPrice(jiageDouble);
									productOrder.setSourcePrice(jiageDouble);
									productOrder.setCommision(0d);
								} else {
									result.setState(-1);
									result.setMsg("抱歉，可能未达到批发数量。");
									return result;
								}
							} else {
								result.setState(-1);
								result.setMsg("抱歉，下单有误。408");
								return result;
							}
						}
						// 铺货价 /进货价
						else if (mo.getBuyType() == Short.parseShort(ShoppingCartTypeEnum.Puhuo.toString()) || mo.getBuyType() == Short.parseShort(ShoppingCartTypeEnum.Jinhuo.toString())) {
							// 是否是代理商 ，如果是 拿代理价，
							// 是否是落地店，如果是 拿落地价
							// 拿 零售价
							if(mo.getSource() !=null && mo.getSource()==Short.parseShort( ShoppingCarSourceEnum.Landi.toString())){
								if(styles.getLandPrice()==null||styles.getLandPrice()<=0){
									result.setState(-1);
									result.setMsg("该产品没有落地价");
									return result;
								}
								priceTotal += styles.getLandPrice().doubleValue() * pp.getCount();
								productOrder.setPrice(styles.getLandPrice());
								productOrder.setSourcePrice(styles.getLandPrice());
							}
							else if(mo.getSource() !=null &&  mo.getSource()== Short.parseShort( ShoppingCarSourceEnum.agency.toString())){
								if(mo.getBuyerIdentity() == 2){
									if(styles.getAgencyPrice()==null||styles.getAgencyPrice()<=0){
										result.setState(-1);
										result.setMsg("该产品没有代理价");
										return result;
									}
									priceTotal += styles.getAgencyPrice() * pp.getCount();
									productOrder.setPrice(styles.getAgencyPrice());
									productOrder.setSourcePrice(styles.getAgencyPrice());
								}else {
									result.setState(-1);
									result.setMsg("抱歉，你还不是该产品的代理商。");
									return result;
								}
							}
							productOrder.setCommision(0d);
						} else {// 零售
							double pri=styles.getPrice();
							if (product.getbType() != null && product.getbType() == 1) {
								productOrder.setOrderType((short) 4);// 零售,赠品区
							}
							if(orderType==Integer.parseInt(SupplyOrderType.RetailAgent.toString())){
								//如果是品牌代理商品，是否可以拿到代理价
								pri= agentService.getProductPriceByWeiid(weiid, styles.getStylesId());
							}
							/*----------产品是否在限时抢购中----------------*/
							AActProductsShowTime act=activityService.getAActProductsShowTime(product.getProductId(), true);
                            boolean isGoing=false;//是否正在抢购的活动
                            if(act!=null){
                            	AActShowProducts proShow=baseDAO.get(AActShowProducts.class, act.getProActId());
                            	AActivityProducts actProducts=baseDAO.get(AActivityProducts.class, act.getProActId());	
                            	if(proShow!=null&&actProducts!=null&&actProducts.getState()==Short.parseShort(ActProductVerState.Ok.toString())){
                            		productOrder.setIsActivity((short)1);
        							supplyOrder.setIsActivity((short)1);
        							supplyOrder.setActivityId(actProducts.getActId());
        							if(product.getPublishType()==null||product.getPublishType()<=0) {
        								productOrder.setPrice(actProducts.getPrice()); 
            							productOrder.setSourcePrice(actProducts.getPrice());
            							productOrder.setCommision(actProducts.getCommission());
            							priceTotal += actProducts.getPrice() * pp.getCount();
        								commissionTotal += actProducts.getCommission() * pp.getCount();
        								productOrder.setProActId(actProducts.getProActId());
            							isGoing=true; 
            							if(proShow.getStockCount()==null||proShow.getStockCount().intValue()<pp.getCount().intValue()){
            								result.setState(-1);
            								result.setMsg("不好意思，产品已抢完！");
            								return result;
            							}else {//锁库存
            								proShow.setStockCount(proShow.getStockCount()-pp.getCount());
            								baseDAO.update(proShow); 
            								String keyName="BuyLimitCount_"+weiid+"_"+act.getActPid()+"_"+act.getProductId();
            								Integer lv= (Integer)RedisUtil.getObject(keyName);
            		                		Integer counts=(lv==null ? 0 :lv);
            		            		    counts=counts+pp.getCount();
            								RedisUtil.setObject(keyName, counts, 21600);//缓存6小时
    									}
        							}
                            	}
                            }
                            /*-------------------------------------------*/
							if(!isGoing){//产品不在活动中
								priceTotal += pri * pp.getCount();
								commissionTotal += styles.getConmision() * pp.getCount();
								productOrder.setPrice(pri);
								productOrder.setSourcePrice(pri);
								productOrder.setCommision(styles.getConmision());
							}
						}
						// 插入
						baseDAO.save(productOrder);
					
					} else {
						result.setState(-1);
						result.setMsg("数据有误406");
						return result;
					}
				}
			}

			supplyOrder.setTotalPrice(priceTotal);
			supplyOrder.setCommision(commissionTotal);
			baseDAO.save(supplyOrder);
			supplyOrderIdsString += supplyOrderId + ",";

			totalPrice += priceTotal + youfei;
			totalYoufei += youfei;
		}/*---供应商下单操作完毕---------------*/

		// 生成支付订单--------------------------
		if (orderType != 0 && orderType != Integer.parseInt(OrderTypeEnum.Puhuo.toString())) {
			OPayOrder payOrder = new OPayOrder();
			payOrder.setPayOrderId(payOrderId);
			payOrder.setWeiId(weiid);
			payOrder.setTotalPrice(totalPrice);
			payOrder.setTypeState((short) orderType);
			payOrder.setOrderDate(nowTime);
			payOrder.setOrderTime(nowTime);
			payOrder.setOrderFrom(Short.parseShort(orderFrom.toString()));
			payOrder.setState(Short.parseShort(OrderStatusEnum.Nopay.toString()));
			baseDAO.save(payOrder);

			saveOrderLog(weiid, payOrderId, supplyOrderIdsString, totalPrice);

			result.setOrderno(payOrderId);
			result.setAlltotal(totalPrice);
			result.setAllpostage(totalYoufei);
		}

		/*----------------------*/
		result.setState(1);
		result.setMsg("提交成功！");
		return result;
	}

	/**
	 * 插入支付快照
	 * 
	 * @param weino
	 * @param payOrderId
	 * @param supplyOrderIds
	 * @param totalPrice
	 */
	public void saveOrderLog(long weino, String payOrderId, String supplyOrderIds, double totalPrice) {

		if (!ObjectUtil.isEmpty(supplyOrderIds) && supplyOrderIds.endsWith(",")) {
			supplyOrderIds = supplyOrderIds.substring(0, supplyOrderIds.length() - 1);
		}
		// 记录支付快照
		OPayOrderLog log = new OPayOrderLog();
		log.setCreateTime(new Date());
		log.setWeiId(weino);
		log.setPayOrderId(payOrderId);
		log.setSupplyOrderIds(supplyOrderIds);
		log.setState(Short.valueOf(OrderStatusEnum.Nopay.toString()));
		log.setTotalAmout(totalPrice);
		baseDAO.save(log);
	}

	/**
	 * 获取供应商的批发价格
	 * 
	 * @param dianNo
	 * @param proId
	 * @return
	 */
	public List<PProductBatchPrice> getBatchPricess(Long dianNo, Long proId) {
		String hql = " from PClassProducts p where p.weiId=? and p.productId=? and p.state=1";
		PClassProducts pclassPro = baseDAO.getUniqueResultByHql(hql, dianNo, proId);// 获取这个上架信息
		if (pclassPro != null) {
			String hql2 = " from PShevleBatchPrice p where p.id=?  order by p.count";
			List<PShevleBatchPrice> psplist = baseDAO.find(hql2, pclassPro.getId());
			if (psplist == null) {
				return null;
			}
			List<PProductBatchPrice> ppplist = new ArrayList<PProductBatchPrice>();
			for (PShevleBatchPrice price : psplist) {
				PProductBatchPrice ppp = new PProductBatchPrice();
				ppp.setBid(price.getSbid());
				ppp.setCount(price.getCount());
				ppp.setPirce(price.getPrice());
				ppp.setProductId(proId);
				ppplist.add(ppp);
			}
			return ppplist;
		} else {
			return null;
		}
	}

	/**
	 * 计算 批发价格
	 * 
	 * @param count
	 *            批发数量
	 * @param pplist
	 * @return
	 */
	public Double getshoppcartpricebycount(int count, List<PProductBatchPrice> pplist) {
		Collections.sort(pplist, new Comparator<PProductBatchPrice>() {
			public int compare(PProductBatchPrice arg0, PProductBatchPrice arg1) {
				return arg0.getCount().compareTo(arg1.getCount());
			}
		});
		Double proPrice = 0.0;// 梯度数量
		for (PProductBatchPrice p : pplist) {
			if (count >= p.getCount()) {
				proPrice = p.getPirce();
			} else {
				return proPrice;
			}
		}
		return proPrice;
	}

	/**
	 * 获取供应商订单所需邮费
	 * 
	 * @param pur
	 * @param weiNo
	 * @param cartType
	 * @param uca
	 * @param logisticsId
	 * @return
	 */
	public double getFare(BShoppingCartVO pur, long weiNo, BAddressVO uca, String logisticsId) {
		List<Long> proIds = new ArrayList<Long>();// 卖家产品
		if (pur != null && pur.getProductList() != null && pur.getProductList().size() > 0) {
			pur.getProductList().forEach((pp) -> {
				proIds.add(pp.getProNum());
			});
		}
		List<PProducts> ppList = productDao.findProductlistByIds(proIds, Short.parseShort(ProductStatusEnum.Showing.toString())); // find_Productlist(proIds);

		Double youfei = 0.0;// 初始化快递总金额
		for (PProducts ppt : ppList) {
			int procount = 0;// 初始化总件数
			if (isZengpin(ppt)) {
				for (BShoppingCartProductVO bsc : pur.getProductList()) {// 遍历相加购买件数
					if (bsc.getProNum() == ppt.getProductId()) {// 如果这件商品跟购买的商品一致
						procount += bsc.getCount();// 相同的物品，件数相加
					}
				}
				youfei += procount * 10;// 总件数*10
			} else if (ppt.getFreightId() <= 0) {// 如果这件商品是自定义邮费的话
				for (BShoppingCartProductVO bsc : pur.getProductList()) {// 遍历相加购买件数
					if (bsc.getProNum() == ppt.getProductId()) {// 如果这件商品跟购买的商品一致
						procount += bsc.getCount();// 相同的物品，件数相加
					}
				}
				youfei += ppt.getDefPostFee() + ((procount - 1) * ppt.getDefPostFee()); // 计算自定义邮费（首费+续件*首费）
			} else {
				String hql2 = "from PPostAgeDetails p where p.courierType=? and p.freightId=?";// 查询邮费模板

				List<PPostAgeDetails> mlist = baseDAO.find(hql2, ParseHelper.toShort(logisticsId), ppt.getFreightId());
				List<BShoppingCartProductVO> proList = new ArrayList<BShoppingCartProductVO>();
				for (BShoppingCartProductVO bb : pur.getProductList()) {
					if (bb.getProNum() == ppt.getProductId())
						proList.add(bb);
				}
				youfei += getFareByFreightID(mlist, proList, ppt, uca);
			}
		}
		return youfei;
	}

	public BKuaiDi getBKuaiDi(int type, double amount) {
		if(amount<0)
			return null;
		BKuaiDi mo = new BKuaiDi();
		mo.setLogisticPrice(amount);
		mo.setLogisticId(String.valueOf(type));
		switch (type) {
		case 1:
			mo.setLogisticName("快递");
			break;
		case 2:
			mo.setLogisticName("EMS");
			break;
		case 3:
			mo.setLogisticName("平邮");
			break;
		case 4:
			mo.setLogisticName("物流");
			break;
		default:
			return null;
		}
		return mo;
	}

	/**
	 * 获取购物模板
	 * 
	 * @param pur
	 * @param weiNo
	 * @param uca
	 * @param logisticsId
	 * @return
	 */
	public BShoppingCartVO getCartModel(BShoppingCartVO pur, BAddressVO address) {
		if (address == null) {
			return pur;
		}
		UCustomerAddr addr = baseDAO.get(UCustomerAddr.class, ParseHelper.toInt(address.getAddressId()));
		if (addr == null) {
			return pur;
		}
		address.setProvince(addr.getProvince());
		address.setCity(addr.getCity());
		address.setDistrict(addr.getDistrict());
		List<Long> proIds = new ArrayList<Long>();// 卖家产品
		if (pur != null && pur.getProductList() != null && pur.getProductList().size() > 0) {
			pur.getProductList().forEach((pp) -> {
				proIds.add(pp.getProNum());
			});
		}
		List<PProducts> ppList = productDao.findProductlistByIds(proIds, Short.parseShort(ProductStatusEnum.Showing.toString()));// find_Productlist(proIds);
		boolean haveKD = true;
		boolean haveEMS = true;
		boolean havePY = true;
		boolean havaWL=true;
		double youfei_kd = 0;
		double youfei_ems = 0;
		double youfei_py = 0;
		double youfei_wl=0;
		Map<Long, List<BKuaiDi>> map = new HashMap<Long, List<BKuaiDi>>();
		for (PProducts ppt : ppList) {
			if (!map.containsKey(ppt.getProductId())) {
				map.put(ppt.getProductId(), new ArrayList<BKuaiDi>());
			}
			if (isZengpin(ppt)) {
				int procount = 0;// 初始化总件数
				for (BShoppingCartProductVO bsc : pur.getProductList()) {// 遍历相加购买件数
					if (bsc.getProNum().longValue() == ppt.getProductId().longValue()) {// 如果这件商品跟购买的商品一致
						{
							procount += bsc.getCount();// 相同的物品，件数相加
						    bsc.setDisplayPrice(CommonMethod.getInstance().getDisplayPrice(ppt.getDefaultPrice(), ppt.getOriginalPrice(), ppt.getPercent()));
						}
					}
				}
				double yf = procount * 10;// 总件数*10
				BKuaiDi item=getBKuaiDi(1, yf);
				if(item!=null){
					map.get(ppt.getProductId()).add(item);
				}
				
			} else if (ppt.getFreightId() <= 0) {// 如果这件商品是自定义邮费的话
				int procount = 0;// 初始化总件数
				for (BShoppingCartProductVO bsc : pur.getProductList()) {// 遍历相加购买件数
					if (bsc.getProNum().equals(ppt.getProductId())) {// 如果这件商品跟购买的商品一致
						{
							procount += bsc.getCount();// 相同的物品，件数相加
							bsc.setDisplayPrice(CommonMethod.getInstance().getDisplayPrice(ppt.getDefaultPrice(), ppt.getOriginalPrice(), ppt.getPercent()));
						}
					}
				}
				double yf = procount * ppt.getDefPostFee(); // 计算自定义邮费（首费+续件*首费）

				BKuaiDi item=getBKuaiDi(1, yf);
				if(item!=null){
					map.get(ppt.getProductId()).add(item);
				}

			} else {
				String hql2 = "from PPostAgeDetails p where  p.freightId=? order by p.status desc";// 查询邮费模板
				List<PPostAgeDetails> mlist = baseDAO.find(hql2, ppt.getFreightId());
				if (mlist != null && mlist.size() > 0) {
					List<BShoppingCartProductVO> proSub = new ArrayList<BShoppingCartProductVO>();
					for (BShoppingCartProductVO bb : pur.getProductList()) {
						if (bb.getProNum().longValue() == ppt.getProductId().longValue())
							{
							   proSub.add(bb);
							   bb.setDisplayPrice(CommonMethod.getInstance().getDisplayPrice(ppt.getDefaultPrice(), ppt.getOriginalPrice(), ppt.getPercent()));
							}
					}
					for (PPostAgeDetails post : mlist) {
						double yf = getFareByPostId(post, proSub, ppt, address);
						if(yf>=0){
							BKuaiDi item=getBKuaiDi(post.getCourierType(), yf);
							if(item!=null){
								List<BKuaiDi> kdlist= map.get(ppt.getProductId());
								boolean isCanHave=false;
								if(kdlist!=null&&kdlist.size()>0){
									for (BKuaiDi bb : kdlist) {
										if(!bb.getLogisticId().equals(item.getLogisticId())){
											isCanHave=true;
										}
									}
								}else {
									isCanHave=true;
								}
								if(isCanHave)
									map.get(ppt.getProductId()).add(item);
							}
						}
					}
				}
			}
		}
		for (Long proId : map.keySet()) {
			boolean isKD = false;
			boolean isEMS = false;
			boolean isPY = false;
			boolean isWL=false;
			for (BKuaiDi kk : map.get(proId)) {
				if (ParseHelper.toInt( kk.getLogisticId()) == 1) {
					isKD = true;
					youfei_kd += kk.getLogisticPrice();
				}
				if (ParseHelper.toInt( kk.getLogisticId())  == 2) {
					isEMS = true;
					youfei_ems += kk.getLogisticPrice();
				}
				if (ParseHelper.toInt( kk.getLogisticId()) == 3) {
					isPY = true;
					youfei_py += kk.getLogisticPrice();
				}
				if (ParseHelper.toInt( kk.getLogisticId()) == 4) {
					isWL = true;
					youfei_wl += kk.getLogisticPrice();
				}
			}
			if (!isKD)
				haveKD = false;
			if (!isEMS)
				haveEMS = false;
			if (!isPY)
				havePY = false;
			if(!isWL)
				havaWL=false;
		}
		List<BKuaiDi> kdlistDis = new ArrayList<BKuaiDi>();
		if (haveKD) {
			kdlistDis.add(getBKuaiDi(1, youfei_kd));
		}
		if (haveEMS) {
			kdlistDis.add(getBKuaiDi(2, youfei_ems));
		}
		if (havePY) {
			kdlistDis.add(getBKuaiDi(3, youfei_py));
		}
		if(havaWL){
			kdlistDis.add(getBKuaiDi(4, youfei_wl));
		}
		pur.setLogisticList(kdlistDis);
		return pur;
	}

	/**
	 * 不同运费模板 计算邮费
	 * 
	 * @param mlist
	 * @param address
	 * @param logisticsId
	 * @return
	 */
	public double getFareByPostId(PPostAgeDetails ppad, List<BShoppingCartProductVO> prolist, PProducts product, BAddressVO address) {
		double youfei = 0;
		if(address==null)
			return youfei;
		int province =  address.getProvince()==null? 0: address.getProvince();
		int city = address.getCity()==null?0:address.getCity();
		int district = address.getDistrict()==null?0:address.getDistrict();
		boolean isSpecial = false;// 是否是特殊区域
		PPostAgeDetails pasmodel = null;// 例外的模板
		PPostAgeDetails defaultmodel = null;// 默认的模板

		if ((ppad.getDestination().indexOf("|" + province + "|") >= 0) || (ppad.getDestination().indexOf("|" + city + "|") >= 0) || (ppad.getDestination().indexOf("|" + district + "|") >= 0)) {
			isSpecial = true;// 确认为特殊区域
			pasmodel = ppad;// 特殊区域模板
		}
		if (ppad.getStatus() == 0) {// 邮费默认模板为0，其余为1
			defaultmodel = ppad;// 默认邮费详情模板
		}

		if (!isSpecial) {
			pasmodel = defaultmodel;
		}
		if (pasmodel == null)
			return -1;
		int productCount = 0;// 产品件数
		if (prolist != null && prolist.size() > 0) {
			for (BShoppingCartProductVO pvo : prolist) {
				if (pvo.getProNum().equals(product.getProductId()))
					productCount += pvo.getCount();
			}
		}

		int restCount = productCount - pasmodel.getFirstCount();// 排除首件还有多少件
		if (restCount > 0) {
			int mul = (int) Math.ceil((double) restCount / pasmodel.getMoreCount());
			youfei += pasmodel.getFirstpiece() + mul * pasmodel.getMorepiece();
		} else {
			youfei += pasmodel.getFirstpiece();
		}

		return youfei;
	}

	/**
	 * 不同运费模板 计算邮费
	 * 
	 * @param mlist
	 * @param address
	 * @param logisticsId
	 * @return
	 */
	public double getFareByFreightID(List<PPostAgeDetails> mlist, List<BShoppingCartProductVO> prolist, PProducts product, BAddressVO address) {
		double youfei = 0;
		int province = address.getProvince();
		int city = address.getCity();
		int district = address.getDistrict();
		boolean isSpecial = false;// 是否是特殊区域
		PPostAgeDetails pasmodel = null;// 例外的模板
		PPostAgeDetails defaultmodel = null;// 默认的模板
		if (mlist != null && mlist.size() > 0) {
			for (PPostAgeDetails ppad : mlist) {
				if ((ppad.getDestination().indexOf("|" + province + "|") >= 0) || (ppad.getDestination().indexOf("|" + city + "|") >= 0) || (ppad.getDestination().indexOf("|" + district + "|") >= 0)) {
					isSpecial = true;// 确认为特殊区域
					pasmodel = ppad;// 特殊区域模板
					break;
				}
				if (ppad.getStatus() == 0) {// 邮费默认模板为0，其余为1
					defaultmodel = ppad;// 默认邮费详情模板
				}
			}
			if (!isSpecial) {
				pasmodel = defaultmodel;
			}
			if (pasmodel == null)
				return 0;
			int productCount = 0;// 产品件数
			if (prolist != null && prolist.size() > 0) {
				for (BShoppingCartProductVO pvo : prolist) {
					if (pvo.getProNum() == product.getProductId())
						productCount += pvo.getCount();
				}
			}

			int restCount = productCount - pasmodel.getFirstCount();// 排除首件还有多少件
			if (restCount > 0) {
				int mul = (int) Math.ceil((double) restCount / pasmodel.getMoreCount());
				youfei += pasmodel.getFirstpiece() + mul * pasmodel.getMorepiece();
			} else {
				youfei += pasmodel.getFirstpiece();
			}

		}
		return youfei;
	}

	/**
	 * 判断是否为赠品
	 * 
	 * @param p
	 * @return
	 */
	public boolean isZengpin(PProducts p) {
		if (p.getbType().toString().equals("1")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 获取用户 是否是代理商、落地店
	 * 
	 * @param weiid
	 * @param productId
	 * @return
	 */
	public int getUserType(long weiid, long productId) {
		int identity = payService.isShop(weiid, productId);
		if (BitOperation.verIdentity(identity, UserIdentityType.Agent))
			return 2;
		else if (BitOperation.verIdentity(identity, UserIdentityType.Ground)) {
			return 1;
		}
		return 0;
	}

	/**
	 * 
	 * @param weiid
	 *            买家微店号
	 * @param productid
	 *            产品id
	 * @param provice
	 *            收货地址（省）
	 * @param city
	 *            市
	 * @param areaid
	 *            区
	 * @return
	 */
	public String getSellerWeiid(long weiid, long productid, int provice, int city, int areaid) {
		PProducts products = baseDAO.get(PProducts.class, productid);
		if (products == null)
			return "0";
		String demand = " from UDemandProduct p where p.productId=?";
		UDemandProduct demandMod = baseDAO.getUniqueResultByHql(demand, productid);
		if (demandMod != null) {
			// 获取用户 对于该产品的身份标示
			int identity = payService.isShop(weiid, productid);
			if (BitOperation.verIdentity(identity, UserIdentityType.Agent)) {// 代理商
				return String.valueOf(products.getSupplierWeiId());
			} else if (BitOperation.verIdentity(identity, UserIdentityType.Ground)) {// 落地店 ,订单分给 1.代理商2 平台号
				long sellerid = getSellerWeiid(demandMod.getDemandId(), provice, city, areaid, true);
				if (sellerid <= 0)
					return String.valueOf(products.getSupplierWeiId());
				return String.valueOf(sellerid);
			} else { //普通用户， 订单分给（顺序） 1落地店，2代理商，3平台号
				long sellerid = getSellerWeiid(demandMod.getDemandId(), provice, city, areaid, false);
				if (sellerid <= 0)
					return String.valueOf(products.getSupplierWeiId());
				return String.valueOf(sellerid);
			}
		} else {
			return String.valueOf(products.getSupplierWeiId());
		}
	}

	/**
	 * 针对平台号产品，获取就近发货人（代理商、落地店）
	 * 
	 * @param demandId
	 * @param province
	 * @param city
	 * @param areaid
	 * @param isGround
	 * @return
	 */
	private long getSellerWeiid(Integer demandId, Integer province, Integer city, Integer areaid, boolean isGround) {
		String agent_hql = " from UProductAgent p  where  p.demandId=? and p.provice=? ";
		String groud_hql = " from UProductShop u where u.demandId=? and u.province=? and u.city=?";
		if (isGround) {// 落地店用户
			List<UProductAgent> agents = baseDAO.find(agent_hql, demandId, province);
			if (agents != null&&agents.size()>0) {
				return agents.get(0).getWeiId();
			} 
		} else {// 普通用户（非落地店）
			List<UProductShop> shopList = baseDAO.find(groud_hql, demandId, province, city);
			if (shopList != null && shopList.size() > 0) {
				for (UProductShop pp : shopList) {
					if (pp.getArea() != null && pp.getArea().intValue() == areaid) {
						return pp.getWeiId();
					}
				}
				return shopList.get(0).getWeiId();
			} else {// 平台没有落地店，找代理商
				List<UProductAgent> agents = baseDAO.find(agent_hql, demandId, province);
				if (agents != null&&agents.size()>0) {
					return agents.get(0).getWeiId();
				} 
			}
		}
		return 0;
	}

	/**
	 * 获取产品属性
	 * 
	 * @param proid
	 * @param styleid
	 * @return
	 */
	public String getProductStyleName(Long proid, Long styleid) {
		/* 查商品款式KV */
		String hql = " from PProductStyleKv p where p.productId=? and p.stylesId=?  order by p.attributeId asc";
		Object[] b = new Object[2];
		b[0] = proid;
		b[1] = styleid;
		List<PProductStyleKv> listkey = baseDAO.find(hql, b);// 获取商品款式所属的属性列表
		if (listkey == null || listkey.size() <= 0)// 如果列表为空，返回空
		{
			return "";
		}
		String ret = "";
		for (PProductStyleKv ppsk : listkey) {
			hql = "from PProductSellValue p where p.productId=? and p.keyId=?";
			Object[] b2 = new Object[2];
			b2[0] = ppsk.getProductId();
			b2[1] = ppsk.getKeyId();
			PProductSellValue ppsv = baseDAO.getUniqueResultByHql(hql, b2);
			hql = "from PProductSellKey p where p.productId=? and p.attributeId=?";
			b2[1] = ppsk.getAttributeId();
			PProductSellKey ppkey = baseDAO.getUniqueResultByHql(hql, b2);
			if (ppsv == null || ppkey == null) {
				continue;
			}
			if(ppkey.getAttributeName().equals("-1")){
				ret +="默认：默认|";
			}else {
				ret += ppkey.getAttributeName() + ":" + ppsv.getValue() + "|";
			}
		}
		if (!ObjectUtil.isEmpty(ret) && ret.endsWith("|")) {
			ret = ret.substring(0, ret.length() - 1);
		}
		return ret;
	}

	@Override
	public BReturnOdertInfo saveExchangeOrder(List<BShoppingCartVO> sellerlist,
			long weiid, BAddressVO address, FromTypeEnum orderFrom) {
		BReturnOdertInfo result = new BReturnOdertInfo();
		//兑换只能一个一个来
		if (sellerlist == null || sellerlist.size() <= 0  || address == null || address.getAddressId() == null) {
			result.setState(-1);
			result.setMsg("参数有误");
			return result;
		}
		UCustomerAddr addr = baseDAO.get(UCustomerAddr.class, ParseHelper.toInt(address.getAddressId()));
		if (addr == null || addr.getWeiId() != weiid) {
			result.setState(-1);
			result.setMsg("收货地址有误");
			return result;
		}
		address.setProvince(addr.getProvince());
		address.setCity(addr.getCity()); 
		address.setDistrict(addr.getDistrict());
		// 生成支付订单号
		String payOrderId = GenerateOrderNum.getInstance().GenerateOrder();
		String supplyOrderIdsString = "";// 支付快照 ，支付单对应的供应商订单号
		double totalPrice = 0, totalYoufei = 0;// totalCommission=0;//总价，总邮费
		int orderType = 0;
		Date nowTime = new Date();
		//订单拆单操作
		sellerlist = cartProcess(sellerlist, address, weiid);
		if (sellerlist == null || sellerlist.size() <= 0) {
			result.setState(-1);
			result.setMsg("产品列表不能为空");
			return result;
		}
		// ------生成订单收货地址----------
		OOrderAddr orderAddr = new OOrderAddr();
		orderAddr.setCaddrId(ParseHelper.toInt(address.getAddressId()));
		orderAddr.setProvince(address.getProvince());
		orderAddr.setCity(address.getCity());
		orderAddr.setDistrict(address.getDistrict());
		orderAddr.setDetailAddr(addr.getDetailAddr());
		orderAddr.setMobilePhone(addr.getMobilePhone());
		orderAddr.setWeiId(addr.getWeiId());
		orderAddr.setReceiverName(addr.getReceiverName());
		orderAddr.setCreateTime(nowTime);
		orderAddr.setQq(addr.getQq());
		if(!ObjectUtil.isEmpty( address.getIdCard())){
			orderAddr.setIdCard(address.getIdCard());
		} 
		baseDAO.save(orderAddr);
		// 供应商订单下单操作
		for (BShoppingCartVO mo : sellerlist) {
			if (mo.getProductList() == null || mo.getProductList().size() <= 0) {
				continue;
			}
			if(mo.getBuyType()==null){
				result.setState(-1);
				result.setMsg("订单类型有误413！");
				return result;
			}
			//判断是否兑换区的商品
			Long supplierid=mo.getSupplierId();
			Long conversupplierid=Long.parseLong(AppSettingUtil.getSingleValue("supiler")==null?"0":AppSettingUtil.getSingleValue("supiler"));
			if(supplierid.longValue()!=conversupplierid.longValue())
			{
				result.setState(-1);
				result.setMsg("不是兑换商品！");
				return result;
			}
			String supplyOrderId = GenerateOrderNum.getInstance().GenerateOrder();
			OSupplyerOrder supplyOrder = new OSupplyerOrder();
			supplyOrder.setAddrId(orderAddr.getOrderAddrId());// ParseHelper.toLong(address.getAddressId())
			supplyOrder.setSupplierOrderId(supplyOrderId);
			supplyOrder.setPayOrderId(payOrderId);
			supplyOrder.setOrderTime(nowTime);
			supplyOrder.setOrderDate(nowTime);
			supplyOrder.setSupplyerId(mo.getSupplierId()); 
			supplyOrder.setSellerWeiId(mo.getProductList().get(0).getBuyShopId()); //卖家（可能是供应商、可能是代理、可能是店铺）
			supplyOrder.setMessage(mo.getMessage());
			supplyOrder.setBuyerID(weiid);
			supplyOrder.setSellerDel(Short.parseShort(com.okwei.bean.enums.OrderDel.NoDel.toString()));
			supplyOrder.setBuyerDel(Short.parseShort(com.okwei.bean.enums.OrderDel.NoDel.toString()));
			supplyOrder.setOrderFrom(Short.parseShort(orderFrom.toString()));
			supplyOrder.setState(Short.parseShort(OrderStatusEnum.Payed.toString()));// 已付款			
			supplyOrder.setIsActivity((short)2);//全返区活动订单
			supplyOrder.setVerWeiId(1l); 
			supplyOrder.setSourse(mo.getSource()); 
			supplyOrder.setMessage(mo.getMessage());
			orderType=Integer.parseInt(SupplyOrderType.Convert.toString());	
			supplyOrder.setOrderType(orderType);// 设置供应商订单类型

			if(mo.getLogisticList()==null||mo.getLogisticList().size()<=0){
				mo= getCartModel(mo, address);
			}
			
			Double youfei=0d;
			if(mo.getLogisticList()!=null&&mo.getLogisticList().size()>0){
				if(mo.getCurrentLogisticId()==null||"0".equals(mo.getCurrentLogisticId())){
					mo.setCurrentLogisticId("1"); 
				}
				if(mo.getLogisticList().size()==1){
					youfei=mo.getLogisticList().get(0).getLogisticPrice();
				}else {
					for (BKuaiDi  kk: mo.getLogisticList()) {
						if(kk.getLogisticId().equals(mo.getCurrentLogisticId())){
							youfei=kk.getLogisticPrice().doubleValue();
							continue;
						}
					}
				}
				
			}else {
				result.setState(-1);
				result.setMsg("运费信息有误444！");
				return result;
			}
			double priceTotal = 0;// 供应商产品总价
			double commissionTotal = 0;
			supplyOrder.setPostage(youfei);// 设置邮费
			for (BShoppingCartProductVO pp : mo.getProductList()) {		
				/*-------------单个产品的订单操作（OProductOrder）-----------------------*/
				PProducts product = baseDAO.get(PProducts.class, pp.getProNum());
				if (product != null && product.getState() != null && product.getState() == Short.parseShort(ProductStatusEnum.Showing.toString()) && pp.getCount() > 0 && ParseHelper.toInt(pp.getProductStyleId()) > 0) {
					PProductStyles styles = baseDAO.get(PProductStyles.class, ParseHelper.toLong( pp.getProductStyleId()));
					if (styles != null) {
						//==================库存修改===========================
						if (styles != null && styles.getCount() != null && styles.getCount().intValue() >= pp.getCount().intValue()) {
							styles.setCount(styles.getCount() - pp.getCount());
							baseDAO.update(styles);
						} else if (styles != null) {
							result.setState(-1);
							result.setMsg("此款式库存不足！");
							return result;
						}
						if (product != null && product.getCount() != null && product.getCount().intValue() >= pp.getCount().intValue()) {
							product.setCount(product.getCount() - pp.getCount());
							baseDAO.update(product);
						} else if (product != null) {
							result.setState(-1);
							result.setMsg("库存不足！");
							return result;
						}
						//=============================================================
						
						String productOrderId = GenerateOrderNum.getInstance().GenerateOrder();
						OProductOrder productOrder = new OProductOrder();
						productOrder.setSupplierOrderId(supplyOrderId);					
						productOrder.setPayOrderId(payOrderId);
						productOrder.setProductOrderId(productOrderId);
						productOrder.setSupplyeriId(mo.getSupplierId());
						productOrder.setCreateTime(nowTime);
						productOrder.setProductId(pp.getProNum());
						productOrder.setStyleId(ParseHelper.toLong(pp.getProductStyleId()));
						productOrder.setStyleDes(getProductStyleName(pp.getProNum(), ParseHelper.toLong(pp.getProductStyleId())));
						productOrder.setBuyerId(weiid);// 买家
						
						//分享页id /分享制作人  /分享人-------------------------------------
						productOrder.setShareID(0L);					
						productOrder.setMakerWeiId(111L);//分享制作人
						productOrder.setSharerUpWeiId(111L);
						
						
						// 成交微店
						long shopWeiid = 111L;
						/*---------===================================================--------*/
						supplyOrder.setSellerWeiId(shopWeiid);//成交微店（城主、代理商）
						
						//-------------------------------===============================================
						productOrder.setShopWeiID((long) 111);
						productOrder.setSellerWeiid((long) 111);
						productOrder.setSellerUpweiid((long) 111);
						productOrder.setState(Short.parseShort(OrderStatusEnum.Payed.toString()));// 默认付款
						productOrder.setProdcutTitle(product.getProductTitle());
						productOrder.setProductImg(ImgDomain.GetFullImgUrl(product.getDefaultImg()));
						productOrder.setCount(pp.getCount());
						productOrder.setClassId(product.getClassId());
						productOrder.setProductMinTitle(product.getProductMinTitle());
						productOrder.setSellerDel(Short.parseShort(OrderDel.NoDel.toString()));
						productOrder.setBuyerDel(Short.parseShort(OrderDel.NoDel.toString()));
						// 订单类型
						productOrder.setOrderType((short) orderType);
						
						priceTotal += styles.getPrice() * pp.getCount();
						commissionTotal += styles.getConmision() * pp.getCount();
						productOrder.setPrice(styles.getPrice());
						productOrder.setSourcePrice(styles.getPrice());
							
						// 插入
						baseDAO.save(productOrder);
						
					
					} else {
						result.setState(-1);
						result.setMsg("数据有误406");
						return result;
					}
				}
			}

			supplyOrder.setTotalPrice(priceTotal);
			supplyOrder.setCommision(commissionTotal);
			baseDAO.save(supplyOrder);
			supplyOrderIdsString += supplyOrderId + ",";

			totalPrice += priceTotal + youfei;
			totalYoufei += youfei;
		}/*---供应商下单操作完毕---------------*/

		UWallet wt=baseDAO.get(UWallet.class, weiid);
		if(wt==null)
		{
			result.setState(-1);
			result.setMsg("数据有误501！");
			return result;
		}
		double totalticket=wt.getAbleTicket()==null?0.0:wt.getAbleTicket();
		if(totalticket<totalPrice)
		{
			result.setState(-1);
			result.setMsg("兑换劵数量不足！");
			return result;
		}		
		//减去钱包数量
		baseDAO.executeHql(" update UWallet a set a.ableTicket=a.ableTicket-? where a.weiId=?", totalPrice,weiid);
		//插入兑换劵消费记录
		UTicketUseLog utl=new UTicketUseLog();
		utl.setAmount(totalPrice*-1);
		utl.setCreateTime(nowTime);
		utl.setOrderId(payOrderId);
		utl.setRemark("兑换商品消费");
		utl.setType(Integer.parseInt(UTicketUseTypeEnum.zhichu.toString()));
		utl.setWeiId(weiid);
		baseDAO.save(utl);
		
		OPayOrder payOrder = new OPayOrder();
		payOrder.setPayOrderId(payOrderId);
		payOrder.setWeiId(weiid);
		payOrder.setTotalPrice(totalPrice);
		payOrder.setTypeState((short) orderType);
		payOrder.setOrderDate(nowTime);
		payOrder.setOrderTime(nowTime);
		payOrder.setOrderFrom(Short.parseShort(orderFrom.toString()));
		payOrder.setState(Short.parseShort(OrderStatusEnum.Payed.toString()));
		baseDAO.save(payOrder);
		//支付快照
		OPayOrderLog payOrderLog = new OPayOrderLog();
		payOrderLog.setPayOrderId(payOrderId);
		payOrderLog.setWeiId(weiid);
		payOrderLog.setPayAmout(totalPrice);
		payOrderLog.setTotalAmout(totalPrice);
		payOrderLog.setCreateTime(nowTime);
		payOrderLog.setPayTime(nowTime);
		payOrderLog.setRemarks("兑换区购物消费");
		payOrderLog.setState(Short.parseShort(OrderStatusEnum.Payed.toString()));
		payOrderLog.setSupplyOrderIds(supplyOrderIdsString);
		baseDAO.save(payOrderLog);

//		saveOrderLog(weiid, payOrderId, supplyOrderIdsString, totalPrice);
		//插入通知短信 待处理
		
		result.setOrderno(payOrderId);
		result.setAlltotal(totalPrice);
		result.setAllpostage(totalYoufei);
		/*----------------------*/
		result.setState(1);
		result.setMsg("提交成功！");
		return result;
	}

}
