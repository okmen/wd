package com.okwei.service.impl.order;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.okwei.bean.domain.AActProductExp;
import com.okwei.bean.domain.AActivity;
import com.okwei.bean.domain.AActivityProducts;
import com.okwei.bean.domain.DAgentApply;
import com.okwei.bean.domain.DAgentInfo;
import com.okwei.bean.domain.DAgentRelation;
import com.okwei.bean.domain.DBrandSupplier;
import com.okwei.bean.domain.DBrands;
import com.okwei.bean.domain.DBrandsExt;
import com.okwei.bean.domain.DCastellans;
import com.okwei.bean.domain.OOrderAddr;
import com.okwei.bean.domain.OPayOrder;
import com.okwei.bean.domain.OPayOrderExtend;
import com.okwei.bean.domain.OPayOrderLog;
import com.okwei.bean.domain.OPayReward;
import com.okwei.bean.domain.OProductOrder;
import com.okwei.bean.domain.OSupplyerOrder;
import com.okwei.bean.domain.PAgentStock;
import com.okwei.bean.domain.PProductStyles;
import com.okwei.bean.domain.PProducts;
import com.okwei.bean.domain.TBatchMarket;
import com.okwei.bean.domain.TTasteSummer;
import com.okwei.bean.domain.UBatchPort;
import com.okwei.bean.domain.UBatchSupplyer;
import com.okwei.bean.domain.UDemandProduct;
import com.okwei.bean.domain.UPlatformServiceOrder;
import com.okwei.bean.domain.UProductAgent;
import com.okwei.bean.domain.UProductShop;
import com.okwei.bean.domain.UPushMessage;
import com.okwei.bean.domain.URepayOrderLog;
import com.okwei.bean.domain.USupplierFollowMsg;
import com.okwei.bean.domain.USupplyChannel;
import com.okwei.bean.domain.USupplyDemand;
import com.okwei.bean.domain.USupplyer;
import com.okwei.bean.domain.UTradingDetails;
import com.okwei.bean.domain.UUserAssist;
import com.okwei.bean.domain.UVerifier;
import com.okwei.bean.domain.UVerifierFollowMsg;
import com.okwei.bean.domain.UWallet;
import com.okwei.bean.domain.UWalletDetails;
import com.okwei.bean.domain.UWeiCoinLog;
import com.okwei.bean.domain.UWeiDianCoinLog;
import com.okwei.bean.domain.UWeiSeller;
import com.okwei.bean.domain.UYunSupplier;
import com.okwei.bean.domain.UYunVerifier;
import com.okwei.bean.enums.AmountStatusEnum;
import com.okwei.bean.enums.AmountTypeEnum;
import com.okwei.bean.enums.BYunSupplierServiceTypeEnum;
import com.okwei.bean.enums.IsPayReward;
import com.okwei.bean.enums.LtwoTypeEnum;
import com.okwei.bean.enums.OrderFromTypeEnum;
import com.okwei.bean.enums.OrderStatusEnum;
import com.okwei.bean.enums.OrderTypeEnum;
import com.okwei.bean.enums.PayTypeEnum;
import com.okwei.bean.enums.PayedRewardStatuus;
import com.okwei.bean.enums.RzyjType;
import com.okwei.bean.enums.ShoppingCarSourceEnum;
import com.okwei.bean.enums.SupplierStatusEnum;
import com.okwei.bean.enums.SupplierTypeEnum;
import com.okwei.bean.enums.SupplyChannelTypeEnum;
import com.okwei.bean.enums.SupplyOrderType;
import com.okwei.bean.enums.UserAmountType;
import com.okwei.bean.enums.UserAmountStatus;
import com.okwei.bean.enums.UserIdentityType;
import com.okwei.bean.enums.VerfierStatusEnum;
import com.okwei.bean.enums.VerifierTypeEnum;
import com.okwei.bean.enums.VerifyCodeType;
import com.okwei.bean.enums.WalletMainTypeEnum;
import com.okwei.bean.enums.WeiCoinLogStateEnum;
import com.okwei.bean.enums.YunVerifyTypeEnum;
import com.okwei.bean.enums.agent.AgentStatus;
import com.okwei.bean.enums.agent.CastellanType;
import com.okwei.bean.vo.BResultMsg;
import com.okwei.bean.vo.order.BAmountSettings;
import com.okwei.bean.vo.order.BOrderTypePower;
import com.okwei.bean.vo.order.BSendSMSVO;
import com.okwei.bean.vo.order.PayOrderInfoVO;
import com.okwei.dao.IBaseDAO;
import com.okwei.dao.agent.IDAgentMgtDao;
import com.okwei.dao.order.IBasicOrdersDao;
import com.okwei.dao.user.IUUserAssistMgtDAO;
import com.okwei.service.IBaseCommonService;
import com.okwei.service.IRegionService;
import com.okwei.service.agent.IDAgentService;
import com.okwei.service.impl.BaseService;
import com.okwei.service.order.IBasicPayService;
import com.okwei.service.product.IBasicProductService;
import com.okwei.service.user.IAgentService;
import com.okwei.util.AppSettingUtil;
import com.okwei.util.BitOperation;
import com.okwei.util.DateUtils;
import com.okwei.util.GenerateOrderNum;
import com.okwei.util.ObjectUtil;
import com.okwei.util.ParseHelper;
import com.okwei.util.RedisUtil;
import com.okwei.util.SendSMSByMobile;

@Service
@Transactional
public class BasicPayService extends BaseService implements IBasicPayService {
	@Autowired
	private IBaseDAO baseDAO;
	@Autowired
	private IBasicOrdersDao ordersDao;
	@Autowired
	private IBaseCommonService commonService;
	@Autowired
	private IAgentService agentService;
	@Autowired
	private IBasicProductService productSerice;
	@Autowired
	private IRegionService regionService;
	@Autowired
	private IDAgentMgtDao agentDao;

	Log logger = LogFactory.getLog(this.getClass());

	public BResultMsg editOrderDataProcess(OPayOrder payOrder, PayTypeEnum paytype) {
		BResultMsg result = new BResultMsg();
		try {
			if (payOrder == null || ObjectUtil.isEmpty(payOrder.getPayOrderId())) {
				throw new RuntimeException("供应商订单处理参数异常");
			}
			if (null != payOrder.getState() && payOrder.getState().shortValue() == Short.parseShort(OrderStatusEnum.Payed.toString())) {
				result.setState(-1);
				result.setMsg("该订单已经支付成功！不能重复处理！");
				return result;
			}
			UWallet wallet = baseDAO.get(UWallet.class, payOrder.getWeiId());
			// 现金券部分
			OPayOrderExtend payExtend = super.getById(OPayOrderExtend.class, payOrder.getPayOrderId());
			if (payExtend != null && payExtend.getCoinAmount() != null) {
				if (wallet == null || wallet.getTotalCoin() == null || wallet.getTotalCoin().doubleValue() < payExtend.getCoinAmount().doubleValue()) {
					result.setState(-1);
					result.setMsg("您的现金券不足！");
					return result;
				} else {
					wallet.setTotalCoin(wallet.getTotalCoin() - payExtend.getCoinAmount());
					// baseDAO.update(wallet);
				}
			}
			//钱包支付处理
			if (payOrder.getWalletmoney() != null && payOrder.getWalletmoney() > 0) {
				if (wallet == null || wallet.getBalance() == null || wallet.getBalance() < payOrder.getWalletmoney()) {
					result.setState(-1);
					result.setMsg("钱包可用金额不足！");
					return result;
				}
				UWalletDetails deductAmout = new UWalletDetails();
				deductAmout.setWeiId(payOrder.getWeiId());
				deductAmout.setAmount(payOrder.getWalletmoney() * (-1));
				deductAmout.setRestAmount(getRestAmount(payOrder.getPayOrderId(), payOrder.getWeiId()) - payOrder.getWalletmoney());
				deductAmout.setCreateTime(new Date());
				deductAmout.setPayOrder(payOrder.getPayOrderId());
				deductAmout.setMainType((short) -1);
				if (payOrder.getTypeState() == Short.parseShort(OrderTypeEnum.ChouDian.toString())) {
					deductAmout.setType(Short.parseShort(UserAmountType.choudian.toString()));
				} else if (payOrder.getTypeState() == Short.parseShort(OrderTypeEnum.Reward.toString())) {
					deductAmout.setType(Short.parseShort(UserAmountType.reward.toString()));
				} else {
					deductAmout.setType(Short.parseShort(UserAmountType.gouwu.toString()));
				}
				baseDAO.save(deductAmout);
				UP_redis_UserWallet(payOrder.getPayOrderId(), payOrder.getWeiId(), payOrder.getWalletmoney() * (-1), 0, 1);
			}

			// 微店币支付 微金币不能退 所以直接扣除
			if (payOrder.getWeiDianCoin() != null && payOrder.getWeiDianCoin() > 0) {
				if (wallet == null || wallet.getWeiDianCoin() == null || wallet.getWeiDianCoin() < payOrder.getWeiDianCoin()) {
					result.setState(-1);
					result.setMsg("微金币可用金额不足！");
					return result;
				}
				UWeiDianCoinLog wc = new UWeiDianCoinLog();
				wc.setBalanceAmount(wallet.getWeiDianCoin() - payOrder.getWeiDianCoin());
				wc.setConsumeAmount(-payOrder.getWeiDianCoin());
				wc.setCreateTime(new Date());
				wc.setRemark("支付单号为" + payOrder.getPayOrderId() + "的购物消费");
				wc.setPayOrderId(payOrder.getPayOrderId());
				wc.setType((short) 2);// 支出
				wc.setWeiId(wallet.getWeiId());
				baseDAO.save(wc);
				UP_redis_UserWalletTwo(payOrder.getPayOrderId(), payOrder.getWeiId(), 0, 0, 1, payOrder.getWeiDianCoin() * (-1));
			}
			payOrder.setPayType(Short.parseShort(paytype.toString()));
			payOrder.setState(Short.parseShort(OrderStatusEnum.Payed.toString()));
			payOrder.setPayTime(new Date());
			baseDAO.update(payOrder);

			/* 订单类型 */
			OrderTypeEnum typeState = null;
			for (OrderTypeEnum tt : OrderTypeEnum.values()) {
				if (Short.parseShort(tt.toString()) == payOrder.getTypeState())
					typeState = tt;
			}
			if (typeState == null) {
				throw new RuntimeException("供应商订单类型有误");
			}
			switch (typeState) {
			case Pt:
			case BatchWholesale:
			case BookHeadOrder:
			case BookTailOrder:
			case BookFullOrder:
			case BatchOrder:
			case Jinhuo:
			case RetailPTH:
			case Puhuo:
			case PuhuoFull:
			case PuhuoHeader:
			case PuhuoTail:
			case RetailAgent:
			case HalfTaste:
				editSupplyOrderPaySuccessNew(payOrder);
				break;
			case BatchRzy:// 批发号成为认证点
				editPayBatchRzy(payOrder, true);
				break;
			case BatchGys:
				editBatchGys(payOrder, true);
				break;
			case GysAndVerifier:
				editPayBatchRzy(payOrder, true);
				editBatchGys(payOrder, true);
				break;
			case BatchPortNoServiceFee:
				editPayBatchRzy(payOrder, false);
				editBatchGys(payOrder, false);
				break;
			case ChongZhi:
				editChongZhi(payOrder);
				break;
			case YunGys:
				editYunSupply(payOrder, true);
				break;
			case YunGysNoServiceFee:
				editYunSupply(payOrder, false);
				break;
			case YunRzy:// 认证员保证金
				editYunVerify(payOrder);
				break;
			case Reward:// TODO 悬赏订单处理
				updateXuanShang(payOrder.getPayOrderId());
				break;
			case ChouDian:// TODO 抽点订单处理
				updateChouDian(payOrder.getPayOrderId());
				break;
			case AgentPayment:// TODO 品牌代理缴费处理
				editAgentInfo(payOrder);
				break;
			default:
				logger.error("订单类型有误 orderNo:" + payOrder.getPayOrderId());
				UP_redis_UserWallet(payOrder.getPayOrderId(), 0, 0, 0, 2);
				throw new RuntimeException("订单类型有误");
			}
			// 统计钱包金额、分配
			UP_redis_UserWallet(payOrder.getPayOrderId(), 0, 0, 0, 3);
			// 清除当前单的缓存记录
			UP_redis_UserWallet(payOrder.getPayOrderId(), 0, 0, 0, 2);
			// 清除订单锁
			ordersDao.removeOrderlock(payOrder.getPayOrderId());
			result.setState(1);
			result.setMsg("订单号：" + payOrder.getPayOrderId() + "支付成功！");

			DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			logger.error("支付成功  订单号:" + payOrder.getPayOrderId() + "时间:" + format.format(new Date()));
		} catch (Exception e) {
			result.setState(-2);
			result.setMsg(e.getMessage());
			logger.error("app钱包支付错误：" + e);
			try {
				UP_redis_UserWallet(payOrder.getPayOrderId(), 0, 0, 0, 2);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			throw new RuntimeException(e);
		}
		return result;
	}

	public List<OSupplyerOrder> getSupplyerOrderList(OPayOrder order) {
		// 获取该订单下所有供应商订单
		List<OSupplyerOrder> supplyerOrderList = new ArrayList<OSupplyerOrder>();
		// 预订订单首款，预订订单尾款，预订订单全款
		if (order.getTypeState() != null
				&& (order.getTypeState() == Short.parseShort(OrderTypeEnum.BookHeadOrder.toString()) || Short.parseShort(OrderTypeEnum.BookTailOrder.toString()) == order.getTypeState() || Short.parseShort(OrderTypeEnum.BookFullOrder.toString()) == order.getTypeState()
						|| Short.parseShort(OrderTypeEnum.PuhuoFull.toString()) == order.getTypeState() || Short.parseShort(OrderTypeEnum.PuhuoHeader.toString()) == order.getTypeState() || Short.parseShort(OrderTypeEnum.PuhuoTail.toString()) == order.getTypeState())) {
			supplyerOrderList = getSupplyerOrderBySupplyOrderId(order.getSupplierOrder());
		} else {
			supplyerOrderList = ordersDao.find_SupplyerOrderByOrderID(order.getPayOrderId());
		}
		return supplyerOrderList;
	}

	/**
	 * 现金券处理
	 * 
	 * @param weiid
	 * @param extend
	 * @param supplyOrders
	 * @throws Exception
	 */
	public void editOrderCoinProcess(long weiid, OPayOrderExtend extend, List<OSupplyerOrder> supplyOrders) throws Exception {
		if (extend == null || supplyOrders == null || supplyOrders.size() <= 0)
			return;
		List<OSupplyerOrder> supplyOrderlist = new ArrayList<OSupplyerOrder>();
		double priceTotal = 0d;

		for (OSupplyerOrder ss : supplyOrders) {
			if (ss.getOrderType() == Short.parseShort(OrderTypeEnum.Jinhuo.toString())) {
				supplyOrderlist.add(ss);
				priceTotal += ss.getTotalPrice();
			}
		}
		if (supplyOrderlist == null || supplyOrderlist.size() <= 0)
			return;
		String[] orderIds = new String[supplyOrderlist.size()];
		for (int i = 0; i < supplyOrderlist.size(); i++) {
			orderIds[i] = supplyOrderlist.get(i).getSupplierOrderId();
		}
		Date nowtime = new Date();
		List<OProductOrder> productOrders = ordersDao.find_ProductOrderBySupOrderIds(orderIds);
		if (productOrders != null && productOrders.size() > 0) {
			for (OProductOrder pp : productOrders) {
				double coinTemp = extend.getCoinAmount() * ((pp.getPrice() * pp.getCount()) / priceTotal);
				UWeiCoinLog weiCoinLog = new UWeiCoinLog();
				weiCoinLog.setSupplyOrderId(pp.getSupplierOrderId());
				weiCoinLog.setProductOrderId(pp.getProductOrderId());
				weiCoinLog.setCreateTime(nowtime);
				weiCoinLog.setUseType(2);// 支出
				weiCoinLog.setState(Integer.parseInt(WeiCoinLogStateEnum.valid.toString()));
				weiCoinLog.setDeleted(0);
				weiCoinLog.setWeiId(weiid);
				weiCoinLog.setCoinAmount(-1 * coinTemp);
				baseDAO.save(weiCoinLog);
			}
		}

	}

	/**
	 * 处理购买订单
	 * 
	 * @param order
	 * @throws Exception
	 */
	public void editSupplyOrderPaySuccess(OPayOrder order) throws Exception {
		if (order == null || null == order.getPayOrderId() || "".equals(order.getPayOrderId())) {
			throw new RuntimeException("订单处理参数异常");// 供应商订单处理参数异常
		}
		List<BSendSMSVO> sendList = new ArrayList<BSendSMSVO>();
		// 获取该订单下所有供应商订单
		Date nowtime = new Date();
		List<OSupplyerOrder> supplyOrderList = getSupplyerOrderList(order);
		// 供应商订单号 数组
		String[] s_oid = null;
		if (supplyOrderList != null) {
			s_oid = new String[supplyOrderList.size()];
			for (int i = 0; i < supplyOrderList.size(); i++)
				s_oid[i] = supplyOrderList.get(i).getSupplierOrderId();
		} else {
			throw new RuntimeException("供应商订单处理参数异常");
		}
		// 现金券处理
		OPayOrderExtend payExtend = super.getById(OPayOrderExtend.class, order.getPayOrderId());
		if (payExtend != null && payExtend.getCoinAmount() != null) {
			editOrderCoinProcess(order.getWeiId(), payExtend, supplyOrderList);
		}
		// 获取所有订单详细
		List<OProductOrder> orderInfoList = ordersDao.find_ProductOrderBySupOrderIds(s_oid);

		if (supplyOrderList != null && supplyOrderList.size() > 0) {
			for (OSupplyerOrder ss : supplyOrderList) {
				if (ss.getState() != null && ss.getState() == Short.parseShort(OrderStatusEnum.Payed.toString())) {
					throw new RuntimeException("订单已支付");
				}
				Map<Short, OrderTypeEnum> mapType = new HashMap<Short, OrderTypeEnum>();
				for (OrderTypeEnum tt : OrderTypeEnum.values()) {
					mapType.put(Short.parseShort(tt.toString()), tt);
				}
				Map<Short, OrderStatusEnum> mapState = new HashMap<Short, OrderStatusEnum>();
				for (OrderStatusEnum tt : OrderStatusEnum.values()) {
					mapState.put(Short.parseShort(tt.toString()), tt);
				}
				Short supplyOrderType = Short.parseShort(String.valueOf(ss.getOrderType()));
				if (order.getTypeState() == Short.parseShort(OrderTypeEnum.BookHeadOrder.toString()) || order.getTypeState() == Short.parseShort(OrderTypeEnum.BookTailOrder.toString()) || order.getTypeState() == Short.parseShort(OrderTypeEnum.BookFullOrder.toString())
						|| order.getTypeState() == Short.parseShort(OrderTypeEnum.PuhuoFull.toString()) || order.getTypeState() == Short.parseShort(OrderTypeEnum.PuhuoHeader.toString()) || order.getTypeState() == Short.parseShort(OrderTypeEnum.PuhuoTail.toString())) {
					supplyOrderType = order.getTypeState();
				}
				BOrderTypePower power = new BOrderTypePower(mapType.get(supplyOrderType), mapState.get(ss.getState()));
				// 修改供应商订单详情状态
				ss.setState(Short.parseShort(power.status.toString()));
				ss.setPayTime(nowtime);
				baseDAO.update(ss);

				// 是否抽成 工厂号供应商不抽成
				boolean isCut = true;
				UUserAssist userAss = baseDAO.get(UUserAssist.class, ss.getSupplyerId());// 工厂号不分配抽成
				if (userAss != null && userAss.getIdentity() != null && BitOperation.isIdentity(userAss.getIdentity(), UserIdentityType.yunSupplier)) {
					isCut = false;
				} else {
					USupplyer supplyer = baseDAO.get(USupplyer.class, ss.getSupplyerId());
					if (supplyer != null && supplyer.getType() != null && BitOperation.isIdentity(supplyer.getType(), UserIdentityType.yunSupplier)) {
						isCut = false;
					}
				}
				// 交易额 货款部分 + 邮费部分
				double totalAmount = ParseHelper.getDoubleDefValue(ss.getTotalPrice()) + ParseHelper.getDoubleDefValue(ss.getPostage());
				// a 是否分配货款
				if (power.isAllocateHuoKuan) {
					UTradingDetails supplyAmountD = new UTradingDetails();
					supplyAmountD.setAmount(totalAmount);
					supplyAmountD.setCreateTime(nowtime);
					supplyAmountD.setLtwoType((short) 0);// Short.parseShort(HuoKuanType.Product.toString())
					supplyAmountD.setOrderNo(ss.getPayOrderId());
					supplyAmountD.setSupplyOrder(ss.getSupplierOrderId());
					supplyAmountD.setState(Short.parseShort(UserAmountStatus.weiFangKuan.toString()));
					supplyAmountD.setType(Short.parseShort(UserAmountType.supplyPrice.toString()));
					supplyAmountD.setWeiId(ss.getSupplyerId());
					if (power.isDeductComminss) {
						supplyAmountD.setAmount(supplyAmountD.getAmount() - ParseHelper.getDoubleDefValue(ss.getCommision()));
					}
					if (power.isDeductCut && isCut) {
						supplyAmountD.setAmount(supplyAmountD.getAmount() * BAmountSettings.batchHuoKuan);
					}
					supplyAmountD.setSurplusAmout(supplyAmountD.getAmount());
					baseDAO.save(supplyAmountD);
					// 钱包改版
					UP_redis_UserWallet(order.getPayOrderId(), ss.getSupplyerId(), 0, supplyAmountD.getAmount(), 1);
					// 插入待发 短信
					String mobile_ss = ordersDao.getUserMobile(ss.getSupplyerId());
					if (ParseHelper.isMobile(mobile_ss)) {
						String[] param = new String[] { mobile_ss, String.format("%.2f", supplyAmountD.getAmount()) };
						BSendSMSVO ssvo = new BSendSMSVO();
						ssvo.setParam(param);
						ssvo.setWeiid(ss.getSupplyerId());
						ssvo.setVerify(VerifyCodeType.SupplyDeliver);
						sendList.add(ssvo);
					}
				}
				// b.是否分配抽成 TODO 改版（2015-7-3 新加市场管理员提成情况）
				if (power.isAllocateCut && isCut) {
					// 获取供应商信息
					UBatchSupplyer supplyer = baseDAO.get(UBatchSupplyer.class, ss.getSupplyerId());
					long marketWeiid = 111;// /市场管理员
					long marketVerWeiid = 111;// 市场管理员的认证员
					if (supplyer != null) {
						int bmid = supplyer.getBmid() == null ? 0 : supplyer.getBmid();
						if (bmid > 0) {
							TBatchMarket market = baseDAO.get(TBatchMarket.class, bmid);
							if (market != null && market.getIsAllIn() != null && market.getIsAllIn() > 0) {// 判断市场是否整体入驻
								if (market.getMarketWeiId() != null && market.getMarketWeiId() > 0) {
									marketWeiid = market.getMarketWeiId();
								}
								if (market.getMarketVerWeiId() != null && market.getMarketVerWeiId() > 0) {
									marketVerWeiid = market.getMarketVerWeiId();
								}
							}
						}
					}
					// 市场管理员 提成 1%
					UTradingDetails market = new UTradingDetails();
					market.setAmount(totalAmount * BAmountSettings.batchMarketWei);
					market.setCreateTime(nowtime);
					market.setLtwoType(Short.parseShort(RzyjType.BookCut.toString()));
					market.setOrderNo(ss.getPayOrderId());
					market.setSupplyOrder(ss.getSupplierOrderId());
					market.setState(Short.parseShort(UserAmountStatus.weiFangKuan.toString()));
					market.setType(Short.parseShort(UserAmountType.rzYj.toString()));
					market.setWeiId(marketWeiid);
					market.setSurplusAmout(market.getAmount());
					baseDAO.save(market);
					// TODO 钱包改版
					UP_redis_UserWallet(order.getPayOrderId(), market.getWeiId(), 0, market.getAmount(), 1);
					// }
					// 市场牵线人 提成 0.2%
					UTradingDetails marketver = new UTradingDetails();
					marketver.setAmount(totalAmount * BAmountSettings.batchMarketVer);
					marketver.setCreateTime(nowtime);
					marketver.setLtwoType(Short.parseShort(RzyjType.BookCut.toString()));
					marketver.setOrderNo(ss.getPayOrderId());
					marketver.setSupplyOrder(ss.getSupplierOrderId());
					marketver.setState(Short.parseShort(UserAmountStatus.weiFangKuan.toString()));
					marketver.setType(Short.parseShort(UserAmountType.rzYj.toString()));
					marketver.setWeiId(marketVerWeiid);
					marketver.setSurplusAmout(marketver.getAmount());
					baseDAO.save(marketver);
					// TODO 钱包改版
					UP_redis_UserWallet(order.getPayOrderId(), marketver.getWeiId(), 0, marketver.getAmount(), 1);

					// 认证点抽成
					UTradingDetails vPort = new UTradingDetails();
					vPort.setAmount(totalAmount * BAmountSettings.batchVerifiPort);
					vPort.setCreateTime(nowtime);
					vPort.setLtwoType(Short.parseShort(RzyjType.BookCut.toString()));
					vPort.setOrderNo(ss.getPayOrderId());
					vPort.setSupplyOrder(ss.getSupplierOrderId());
					vPort.setState(Short.parseShort(UserAmountStatus.weiFangKuan.toString()));
					vPort.setType(Short.parseShort(UserAmountType.rzYj.toString()));
					vPort.setWeiId((long) 111);// verifierPortID
					vPort.setFromWeiID((long) 111);
					if (supplyer != null) {
						if (supplyer.getPortVerifer() != null && supplyer.getPortVerifer() > 0) {
							vPort.setWeiId(supplyer.getPortVerifer());
						}
						if (supplyer.getVerifier() != null) {
							vPort.setFromWeiID(supplyer.getVerifier());
						}
					}

					vPort.setSurplusAmout(vPort.getAmount());
					baseDAO.save(vPort);
					UP_redis_UserWallet(order.getPayOrderId(), vPort.getWeiId(), 0, vPort.getAmount(), 1);

					// 代理商微店号（默认为 微店网收取0.002的抽成）
					UTradingDetails vUser = new UTradingDetails();
					vUser.setAmount(totalAmount * BAmountSettings.batchVerifier);
					vUser.setCreateTime(nowtime);
					vUser.setLtwoType(Short.parseShort(RzyjType.BookCut.toString()));
					vUser.setOrderNo(ss.getPayOrderId());
					vUser.setSupplyOrder(ss.getSupplierOrderId());
					vUser.setState(Short.parseShort(UserAmountStatus.weiFangKuan.toString()));
					vUser.setType(Short.parseShort(UserAmountType.rzYj.toString()));

					vUser.setWeiId((long) 111);// verifierID
					if (supplyer != null && supplyer.getCompanyWeiId() != null && supplyer.getCompanyWeiId() > 0) {
						vUser.setWeiId(supplyer.getCompanyWeiId());
					}
					vUser.setSurplusAmout(vUser.getAmount());
					baseDAO.save(vUser);
					// TODO 钱包改版
					UP_redis_UserWallet(order.getPayOrderId(), vUser.getWeiId(), 0, vUser.getAmount(), 1);

					// 供应商抽成 代理商抽成
					UTradingDetails uBoss = new UTradingDetails();
					uBoss.setAmount(totalAmount * BAmountSettings.batchVerifierAgent);
					uBoss.setCreateTime(nowtime);
					uBoss.setLtwoType(Short.parseShort(RzyjType.BookCut.toString()));
					uBoss.setOrderNo(ss.getPayOrderId());
					uBoss.setSupplyOrder(ss.getSupplierOrderId());
					uBoss.setState(Short.parseShort(UserAmountStatus.weiFangKuan.toString()));
					uBoss.setType(Short.parseShort(UserAmountType.rzYj.toString()));
					uBoss.setWeiId((long) 111);// proxyWeiid
					if (supplyer != null && supplyer.getAgentSupplier() != null && supplyer.getAgentSupplier() > 0) {
						uBoss.setWeiId(supplyer.getAgentSupplier());
					}
					uBoss.setSurplusAmout(uBoss.getAmount());
					baseDAO.save(uBoss);
					// TODO 钱包改版
					UP_redis_UserWallet(order.getPayOrderId(), uBoss.getWeiId(), 0, uBoss.getAmount(), 1);

					// 自己公司（微店网）的抽成
					UTradingDetails weiSystem = new UTradingDetails();
					weiSystem.setAmount(totalAmount * BAmountSettings.batchOkWei);
					weiSystem.setCreateTime(nowtime);
					weiSystem.setLtwoType(Short.parseShort(RzyjType.BookCut.toString()));
					weiSystem.setOrderNo(ss.getPayOrderId());
					weiSystem.setSupplyOrder(ss.getSupplierOrderId());
					weiSystem.setState(Short.parseShort(UserAmountStatus.weiFangKuan.toString()));
					weiSystem.setType(Short.parseShort(UserAmountType.rzYj.toString()));
					weiSystem.setWeiId((long) 111);
					weiSystem.setSurplusAmout(weiSystem.getAmount());
					baseDAO.save(weiSystem);
					// 钱包改版
					UP_redis_UserWallet(order.getPayOrderId(), weiSystem.getWeiId(), 0, weiSystem.getAmount(), 1);

				}

				// 3. ----- 货款部分处理 按单个商品进行结算佣金 货款------
				List<OProductOrder> supplyOIList = new ArrayList<OProductOrder>();
				if (orderInfoList != null && orderInfoList.size() > 0) {
					for (OProductOrder oo : orderInfoList) {
						if (ss.getSupplierOrderId().equals(oo.getSupplierOrderId()))
							supplyOIList.add(oo);
					}
				}

				if (supplyOIList != null && supplyOIList.size() > 0) {
					// 遍历该供应商的每一件商品进行分配
					for (OProductOrder product : supplyOIList) {
						// 如果是赠品 不分配佣金货款 抽成
						if (product.getOrderType() == 3) {
							continue;
						}
						PProducts pro = baseDAO.get(PProducts.class, product.getProductId());
						// 代理商、落地店 库存处理
						if (ss.getOrderType().equals(Short.parseShort(OrderTypeEnum.RetailPTH.toString())) || ss.getOrderType().equals(Short.parseShort(OrderTypeEnum.Jinhuo.toString())) || ss.getOrderType().equals(Short.parseShort(OrderTypeEnum.Puhuo.toString()))) {
							if (pro != null) {
								if (!pro.getSupplierWeiId().equals(ss.getSupplyerId())) {
									String hql_stock = " from PAgentStock p where p.weiId=? and p.productId=?";
									PAgentStock stock = baseDAO.getNotUniqueResultByHql(hql_stock, ss.getSupplyerId(), product.getProductId());
									if (stock != null) {
										if (stock.getStockCount() != null && stock.getStockCount() > product.getCount().intValue()) {
											stock.setStockCount(stock.getStockCount() - product.getCount());
											baseDAO.update(stock);
										} else {
											stock.setStockCount(0);
											baseDAO.update(stock);
										}
									} else {
										stock = new PAgentStock();
										stock.setWeiId(ss.getSupplyerId());
										stock.setProductId(product.getProductId());
										baseDAO.save(stock);
									}
								}
							}
						} else {// 普通购买的库存处理
							if (pro != null && pro.getCount() != null && pro.getCount().intValue() > product.getCount().intValue()) {
								pro.setCount(pro.getCount() - product.getCount());
								baseDAO.update(pro);
							} else if (pro != null) {
								pro.setCount(0);
								baseDAO.update(pro);
							}
						}
						// 如果是活动商品，回写活动数据 add by tan 2016-05-17
						if (product.getIsActivity() == 1) {
							AActProductExp ae = baseDAO.get(AActProductExp.class, product.getProActId());
							if (ae != null) {
								try {
									baseDAO.executeHql(" update AActProductExp a set a.sellerCount=a.sellerCount+?,a.sellerAmount=a.sellerAmount+? where  a.proActId=?", product.getCount(), product.getCount() * product.getPrice(), product.getProActId());
								} catch (Exception ex) {

								}
							} else {
								ae = new AActProductExp();
								ae.setProActId(product.getProActId());
								ae.setProductId(product.getProductId());
								ae.setSellerAmount(product.getCount() * product.getPrice());
								ae.setSellerCount(product.getCount());
								baseDAO.save(ae);
							}
						}

						// 是否分配佣金
						// 佣金分配需要改动 制作人20%分享人50% 上级30%
						if (power.isAllocateComminss) {
							// 所有可分配佣金
							double totalComminss = ParseHelper.getDoubleDefValue(product.getCommision()) * product.getCount();
							if (power.isDeductCut) {
								totalComminss = totalComminss * BAmountSettings.batchHuoKuan;
							}
							// 改动佣金分配规则 modify by tan 20160121
							Long maker = product.getMakerWeiId();
							Long sharerup = product.getSharerUpWeiId();
							Long sharer = product.getShareWeiId();
							// 如何获取不到分配的信息，佣金归微店网
							if (maker == null) {
								maker = 111L;
							}
							if (sharerup == null) {
								sharerup = 111L;
							}
							if (sharer == null) {
								sharer = 111L;
							}
							// 制作人20%
							UTradingDetails uMaker = new UTradingDetails();
							double makerpercent = ParseHelper.toDouble(AppSettingUtil.getSingleValue("Maker"));
							uMaker.setAmount(totalComminss * makerpercent);
							uMaker.setCreateTime(nowtime);
							uMaker.setLtwoType((short) 0);
							uMaker.setOrderNo(ss.getPayOrderId());
							uMaker.setSupplyOrder(product.getSupplierOrderId());
							uMaker.setProductOrder(product.getProductOrderId());
							uMaker.setState(Short.parseShort(UserAmountStatus.weiFangKuan.toString()));
							uMaker.setType(Short.parseShort(UserAmountType.orderYj.toString()));
							uMaker.setWeiId(maker);
							uMaker.setSurplusAmout(uMaker.getAmount());
							baseDAO.save(uMaker);
							// TODO 钱包改版
							UP_redis_UserWallet(order.getPayOrderId(), uMaker.getWeiId(), 0, uMaker.getAmount(), 1);
							// 分享人上级 30%
							UTradingDetails uSharerUp = new UTradingDetails();
							double shareruppercent = ParseHelper.toDouble(AppSettingUtil.getSingleValue("SharerUp"));
							uSharerUp.setAmount(totalComminss * shareruppercent);
							uSharerUp.setCreateTime(nowtime);
							uSharerUp.setLtwoType((short) 0);
							uSharerUp.setOrderNo(ss.getPayOrderId());
							uSharerUp.setSupplyOrder(product.getSupplierOrderId());
							uSharerUp.setProductOrder(product.getProductOrderId());
							uSharerUp.setState(Short.parseShort(UserAmountStatus.weiFangKuan.toString()));
							uSharerUp.setType(Short.parseShort(UserAmountType.orderYj.toString()));
							uSharerUp.setWeiId(sharerup);
							uSharerUp.setSurplusAmout(uSharerUp.getAmount());
							baseDAO.save(uSharerUp);
							// TODO 钱包改版uSharerUp
							UP_redis_UserWallet(order.getPayOrderId(), uSharerUp.getWeiId(), 0, uSharerUp.getAmount(), 1);

							// 分享人50%
							UTradingDetails uSharer = new UTradingDetails();
							double sharerpercent = ParseHelper.toDouble(AppSettingUtil.getSingleValue("Sharer"));
							uSharer.setAmount(totalComminss * sharerpercent);
							uSharer.setCreateTime(nowtime);
							uSharer.setLtwoType((short) 0);
							uSharer.setOrderNo(ss.getPayOrderId());
							uSharer.setSupplyOrder(product.getSupplierOrderId());
							uSharer.setProductOrder(product.getProductOrderId());
							uSharer.setState(Short.parseShort(UserAmountStatus.weiFangKuan.toString()));
							uSharer.setType(Short.parseShort(UserAmountType.orderYj.toString()));
							uSharer.setWeiId(sharer);
							uSharer.setSurplusAmout(uSharer.getAmount());
							baseDAO.save(uSharer);
							// TODO 钱包改版
							UP_redis_UserWallet(order.getPayOrderId(), uSharer.getWeiId(), 0, uSharer.getAmount(), 1);
						}
					}
				}
				// 如果是进货单，判断是否是首次进货（满 多少 成为落地店）
				if (supplyOrderType == Short.parseShort(OrderTypeEnum.Jinhuo.toString())) {
					saveProductShop(order.getWeiId(), ss.getSupplierOrderId());
				}
				// 消息推送给供应商
				insertPushMsg(ss.getSupplyerId(), 32, ss.getSupplierOrderId(), "销售订单：您有一笔订单已支付，请尽快发货!");

			}
			// 短信通知卖家 支付成功！
			String buyerMobile = ordersDao.getUserMobile(order.getWeiId());
			if (ParseHelper.isMobile(buyerMobile)) {
				String[] param = new String[] { buyerMobile, order.getPayOrderId() };
				BSendSMSVO ssvo = new BSendSMSVO();
				ssvo.setParam(param);
				ssvo.setWeiid(order.getWeiId());
				ssvo.setVerify(VerifyCodeType.SendWeiDeliver);
				sendList.add(ssvo);
			}
			// 批量处理发送短信
			if (sendList != null && sendList.size() > 0) {
				SendSMSByMobile smsUtil = new SendSMSByMobile();
				for (BSendSMSVO vo : sendList) {
					smsUtil.sendSMS(vo.getParam(), vo.getVerify(), vo.getWeiid());
				}
			}
		} else {
			throw new Exception("订单信息异常：orderNo=" + order.getPayOrderId());
		}

	}

	/**
	 * 获取供应商订单列表（根据供应商id）
	 * 
	 * @param supplyOrderId
	 * @return
	 */
	private List<OSupplyerOrder> getSupplyerOrderBySupplyOrderId(String supplyOrderId) {
		String hql_sup = " from OSupplyerOrder o where o.supplierOrderId=?  ";
		Object[] b_sup = new Object[1];
		b_sup[0] = supplyOrderId;
		List<OSupplyerOrder> supplyOrderList = baseDAO.find(hql_sup, b_sup);
		return supplyOrderList;
	}

	/**
	 * 获取可用金额
	 * 
	 * @param orderno
	 * @param weiid
	 * @return
	 */
	public double getRestAmount(String orderno, long weiid) {
		String keyName = "Wallet_" + orderno + "_" + weiid;
		double amountTemp = ParseHelper.toDouble(String.valueOf(RedisUtil.getObject(keyName)));
		UWallet uWallet = baseDAO.get(UWallet.class, weiid);
		if (uWallet != null) {
			return ParseHelper.getDoubleDefValue(uWallet.getBalance()) + amountTemp;
		}
		return 0;
	}

	/**
	 * 
	 * @param orderNo
	 *            (payOrderId)
	 * @param weiid
	 * @param amount
	 * @param itype
	 *            (1累加，2支付失败清除缓存 ，3统计)
	 */
	public void UP_redis_UserWallet(String orderNo, long weiid, double amount, double amountNot, int itype) throws Exception {
		UP_redis_UserWalletTwo(orderNo, weiid, amount, amountNot, itype, 0);
	}

	public void UP_redis_UserWalletTwo(String orderNo, long weiid, double amount, double amountNot, int itype, double weicoin) throws Exception {
		if ("".equals(orderNo) || null == orderNo)
			return;
		String keyName = "Wallet_" + orderNo + "_" + weiid;
		String keyNameNot = "Wallet_Not_" + orderNo + "_" + weiid;
		String key_order = "Wallet_all_" + orderNo;
		String key_weicoin = "WeiCoin_" + orderNo + "_" + weiid;
		List<Map<String, Object>> maplist = (List<Map<String, Object>>) RedisUtil.getObject(key_order);
		switch (itype) {
		case 1:
			if (weiid <= 0)
				return;
			double amountTemp = ParseHelper.toDouble(String.valueOf(RedisUtil.getObject(keyName)));
			double amountTempNot = ParseHelper.toDouble(String.valueOf(RedisUtil.getObject(keyNameNot)));
			double coinamout = ParseHelper.toDouble(String.valueOf(RedisUtil.getObject(key_weicoin) == null ? 0.0 : RedisUtil.getObject(key_weicoin)));
			amountTemp = amountTemp + amount;
			amountTempNot = amountTempNot + amountNot;
			coinamout = coinamout + weicoin;
			RedisUtil.setObject(keyName, amountTemp, 120);
			RedisUtil.setObject(keyNameNot, amountTempNot, 120);
			RedisUtil.setObject(key_weicoin, coinamout, 120);
			boolean ishave = false;
			if (maplist != null && maplist.size() > 0) {
				for (Map<String, Object> map : maplist) {
					long mapWid = ParseHelper.toLong(String.valueOf(map.get("weiid")));
					if (mapWid > 0 && mapWid == weiid) {
						map.put("amount", amountTemp);
						map.put("amountNot", amountTempNot);
						map.put("weicoin", coinamout);
						ishave = true;
					}
				}
			} else {
				maplist = new ArrayList<Map<String, Object>>();
			}
			if (!ishave) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("weiid", weiid);
				map.put("amount", amountTemp);
				map.put("amountNot", amountTempNot);
				map.put("weicoin", coinamout);
				maplist.add(map);
			}
			RedisUtil.setObject(key_order, maplist, 120);
			break;
		case 2:
			if (maplist != null && maplist.size() > 0) {
				for (Map<String, Object> map : maplist) {
					long mapWid = ParseHelper.toLong(String.valueOf(map.get("weiid")));
					if (mapWid > 0) {
						RedisUtil.delete("Wallet_" + orderNo + "_" + mapWid);
						RedisUtil.delete("Wallet_Not_" + orderNo + "_" + mapWid);
						RedisUtil.delete("WeiCoin_" + orderNo + "_" + mapWid);
					}
				}
			}
			RedisUtil.delete(key_order);
			break;
		case 3:
			Date nowtime = new Date();
			if (maplist != null && maplist.size() > 0) {
				OPayOrder order = baseDAO.get(OPayOrder.class, orderNo);
				for (Map<String, Object> map : maplist) {
					long mapWid = ParseHelper.toLong(String.valueOf(map.get("weiid")));
					if (mapWid > 0) {
						double amountA = ParseHelper.toDouble(String.valueOf(map.get("amount")));
						double amountANot = ParseHelper.toDouble(String.valueOf(map.get("amountNot")));
						double weicoinamout = ParseHelper.toDouble(String.valueOf(map.get("weicoin") == null ? 0.0 : map.get("weicoin")));
						UWallet mapUWallet = baseDAO.get(UWallet.class, mapWid);
						if (mapUWallet != null) {
							if (order != null && (order.getTypeState() == Short.parseShort(OrderTypeEnum.Reward.toString()) || order.getTypeState() == Short.parseShort(OrderTypeEnum.ChouDian.toString()))) {
								mapUWallet.setBalance(mapUWallet.getBalance() + amountA);
								mapUWallet.setWeiDianCoin(mapUWallet.getWeiDianCoin() == null ? 0.0 : mapUWallet.getWeiDianCoin() + weicoinamout);
							} else {
								mapUWallet.setBalance(mapUWallet.getBalance() + amountA);
								mapUWallet.setAccountNot(mapUWallet.getAccountNot() + amountANot);
								mapUWallet.setWeiDianCoin(mapUWallet.getWeiDianCoin() == null ? 0.0 : mapUWallet.getWeiDianCoin() + weicoinamout);
							}
							baseDAO.update(mapUWallet);
						} else {
							mapUWallet = new UWallet();
							mapUWallet.setWeiId(mapWid);
							mapUWallet.setBalance(amountA);
							mapUWallet.setAccountNot(amountANot);
							mapUWallet.setCreateTime(nowtime);
							mapUWallet.setWeiDianCoin(weicoinamout);
							baseDAO.save(mapUWallet);
						}
					}
				}
			}
			break;
		default:
			break;
		}
	}

	/**
	 * 获取支付订单OPayOrder
	 * 
	 * @param payOrderId
	 * @param needLastOne
	 *            是否要求获取最新一条支付单
	 * @return
	 */
	// public OPayOrder getOPayOrder(String payOrderId, boolean needLastOne) {
	// if (needLastOne) {
	// OPayOrderLog log = ordersDao.get_OPayOrderLog(payOrderId);
	// if (log != null) {
	// return baseDAO.get(OPayOrder.class, log.getPayOrderId());
	// } else {
	// OPayOrder order = baseDAO.get(OPayOrder.class, payOrderId);
	// if (order != null)
	// return order;
	// else {
	// OSupplyerOrder oSup = baseDAO.get(OSupplyerOrder.class, payOrderId);
	// if (oSup != null) {
	// OPayOrder payInfo = new OPayOrder();
	// payInfo.setPayOrderId(oSup.getSupplierOrderId());
	// payInfo.setSupplierOrder(oSup.getSupplierOrderId());
	// payInfo.setWeiId(oSup.getBuyerID());
	// payInfo.setTotalPrice(oSup.getTotalPrice() +
	// ParseHelper.getDoubleDefValue(oSup.getPostage()));
	// payInfo.setOrderFrom(oSup.getOrderFrom());
	// payInfo.setOrderTime(oSup.getOrderTime());
	// payInfo.setOrderDate(oSup.getOrderDate());
	// payInfo.setState(oSup.getState());
	// payInfo.setTypeState(Short.parseShort(oSup.getOrderType().toString()));
	// baseDAO.save(payInfo);
	// // 老订单改为已过期
	// OPayOrder oldOrder = baseDAO.get(OPayOrder.class, oSup.getPayOrderId());
	// if (oldOrder != null) {
	// oldOrder.setState(Short.valueOf(OrderStatusEnum.Tovoided.toString()));
	// baseDAO.update(oldOrder);
	// }
	//
	// oSup.setPayOrderId(oSup.getSupplierOrderId());
	// baseDAO.update(oSup);
	// order = payInfo;
	// String[] supids = new String[1];
	// supids[0] = oSup.getSupplierOrderId();
	// List<OProductOrder> productslist =
	// ordersDao.find_ProductOrderBySupOrderIds(supids);
	// if (productslist != null && productslist.size() > 0) {
	// for (OProductOrder oProductOrder : productslist) {
	// oProductOrder.setPayOrderId(oSup.getSupplierOrderId());
	// baseDAO.update(oProductOrder);
	// }
	// }
	// // 记录支付快照
	// OPayOrderLog oLog = new OPayOrderLog();
	// oLog.setCreateTime(new Date());
	// oLog.setWeiId(oSup.getBuyerID());
	// oLog.setPayOrderId(oSup.getSupplierOrderId());
	// oLog.setSupplyOrderIds(oSup.getSupplierOrderId());
	// oLog.setState(Short.valueOf(OrderStatusEnum.Nopay.toString()));
	// oLog.setTotalAmout(payInfo.getTotalPrice());
	// baseDAO.save(oLog);
	// }
	// }
	// return order;
	// }
	//
	// } else {
	// return baseDAO.get(OPayOrder.class, payOrderId);
	// }
	// }
	/**
	 * 获取支付订单OPayOrder
	 * 
	 * @param payOrderId
	 * @param needLastOne
	 *            是否要求获取最新一条支付单
	 * @return
	 */
	public OPayOrder getOPayOrder(String payOrderId, boolean needLastOne) {
		if (needLastOne) {
			OPayOrder order = ordersDao.get_OPayOrderLastOne(payOrderId);
			if (order != null)
				return order;
			else {
				OSupplyerOrder oSup = baseDAO.get(OSupplyerOrder.class, payOrderId);
				if (oSup != null) {
					OPayOrder payInfo = new OPayOrder();
					payInfo.setPayOrderId(oSup.getSupplierOrderId());
					payInfo.setSupplierOrder(oSup.getSupplierOrderId());
					payInfo.setWeiId(oSup.getBuyerID());
					payInfo.setTotalPrice(oSup.getTotalPrice() + ParseHelper.getDoubleDefValue(oSup.getPostage()));
					payInfo.setOrderFrom(oSup.getOrderFrom());
					payInfo.setOrderTime(oSup.getOrderTime());
					payInfo.setOrderDate(oSup.getOrderDate());
					payInfo.setState(oSup.getState());
					payInfo.setTypeState(Short.parseShort(oSup.getOrderType().toString()));
					baseDAO.save(payInfo);
					// 老订单改为已过期
					OPayOrder oldOrder = baseDAO.get(OPayOrder.class, oSup.getPayOrderId());
					if (oldOrder != null) {
						oldOrder.setState(Short.valueOf(OrderStatusEnum.Tovoided.toString()));
						baseDAO.update(oldOrder);
					}
					oSup.setPayOrderId(oSup.getSupplierOrderId());
					baseDAO.update(oSup);
					order = payInfo;
					String[] supids = new String[1];
					supids[0] = oSup.getSupplierOrderId();
					List<OProductOrder> productslist = ordersDao.find_ProductOrderBySupOrderIds(supids);
					if (productslist != null && productslist.size() > 0) {
						for (OProductOrder oProductOrder : productslist) {
							oProductOrder.setPayOrderId(oSup.getSupplierOrderId());
							baseDAO.update(oProductOrder);
						}
					}
					// 记录支付快照
					OPayOrderLog oLog = new OPayOrderLog();
					oLog.setCreateTime(new Date());
					oLog.setWeiId(oSup.getBuyerID());
					oLog.setPayOrderId(oSup.getSupplierOrderId());
					oLog.setSupplyOrderIds(oSup.getSupplierOrderId());
					oLog.setState(Short.valueOf(OrderStatusEnum.Nopay.toString()));
					oLog.setTotalAmout(payInfo.getTotalPrice());
					baseDAO.save(oLog);
				}
			}
			return order;

		} else {
			return baseDAO.get(OPayOrder.class, payOrderId);
		}
	}

	/**
	 * 批发号认证点进驻
	 * 
	 * @param order
	 * @throws Exception
	 */
	private void editPayBatchRzy(OPayOrder order, boolean isAllocateReward) throws Exception {
		// 1、获取该认证点信息
		UBatchPort port = baseDAO.get(UBatchPort.class, order.getWeiId());
		if (port != null) {
			// 2、更新批发号认证点表状态
			port.setStatus(Short.parseShort(VerfierStatusEnum.PayIn.toString()));
			port.setInTime(new Date());
			baseDAO.update(port);

			UVerifierFollowMsg msg = new UVerifierFollowMsg();
			msg.setCreateTime(new Date());
			msg.setOperateContent("供应商进驻保证金缴费");
			msg.setOperateMan(order.getWeiId());
			msg.setRecordType(Short.parseShort(SupplierStatusEnum.PayIn.toString()));
			baseDAO.save(msg);
			int ivalue = 0;
			// 3、更新认证员表身份类型
			UVerifier verifier = baseDAO.get(UVerifier.class, order.getWeiId());// getUVerifier(order.getWeiId());

			if (verifier != null) {
				if (!BitOperation.isVerifier(ParseHelper.toShort(String.valueOf(verifier.getType())), VerifierTypeEnum.batchverifierport)) {
					ivalue = ParseHelper.toInt(VerifierTypeEnum.batchverifierport.toString()) + ParseHelper.toShort(String.valueOf(verifier.getType()));
					verifier.setType((short) ivalue);
					baseDAO.update(verifier);

					UUserAssist assist = baseDAO.get(UUserAssist.class, order.getWeiId());
					if (assist != null && ivalue > 0) {
						assist.setIdentity(assist.getIdentity().intValue() + ParseHelper.toInt(VerifierTypeEnum.batchverifierport.toString()));
						baseDAO.update(assist);
					} else {
						assist = new UUserAssist();
						assist.setWeiId(order.getWeiId());
						assist.setIdentity(ParseHelper.toInt(VerifierTypeEnum.batchverifierport.toString()));
						baseDAO.save(assist);
					}
				}
			}

			if (isAllocateReward) {
				// 4.分配佣金700 给审核人
				if (port.getCompanyWeiId() != null && port.getCompanyWeiId() > 0) {
					UTradingDetails ver = new UTradingDetails();
					ver.setAmount(BAmountSettings.batchGys);
					ver.setCreateTime(new Date());
					ver.setLtwoType(Short.parseShort(RzyjType.BatchUpRenZheng.toString()));
					ver.setOrderNo(order.getPayOrderId());
					ver.setSupplyOrder(String.valueOf(order.getWeiId()));
					ver.setState(Short.parseShort(UserAmountStatus.weiFangKuan.toString()));
					ver.setType(Short.parseShort(UserAmountType.rzYj.toString()));
					ver.setWeiId(port.getCompanyWeiId());
					ver.setFromWeiID(port.getUBWeiId());
					baseDAO.save(ver);
					UP_redis_UserWallet(order.getPayOrderId(), ver.getWeiId(), 0, ver.getAmount(), 1);

				}
				// 分配佣金300 给上级
				if (port.getSupperReceiver() != null && port.getSupperReceiver() > 0) {
					UTradingDetails ver = new UTradingDetails();
					ver.setAmount(BAmountSettings.batchUpGys);
					ver.setCreateTime(new Date());
					ver.setLtwoType(Short.parseShort(RzyjType.BatchUpRenZheng.toString()));
					ver.setOrderNo(order.getPayOrderId());
					ver.setSupplyOrder(String.valueOf(order.getWeiId()));
					ver.setState(Short.parseShort(UserAmountStatus.weiFangKuan.toString()));
					ver.setType(Short.parseShort(UserAmountType.rzYj.toString()));
					ver.setWeiId(port.getSupperReceiver());
					ver.setFromWeiID(port.getSupperVerifier());
					baseDAO.save(ver);
					UP_redis_UserWallet(order.getPayOrderId(), ver.getWeiId(), 0, ver.getAmount(), 1);

				}
				insertPushMsg(order.getWeiId(), 100, order.getPayOrderId(), "恭喜你！缴费成功，您已成为认证服务点！");

			}

			insertPushMsg(port.getUBWeiId(), 42, order.getPayOrderId(), "认证点申请：*" + order.getWeiId().toString() + "*已缴纳服务费，正式成为认证点，并进入您的认证团队");
		}
	}

	/**
	 * 批发号供应商进驻支付成功处理
	 * 
	 * @param order
	 */
	public void editBatchGys(OPayOrder order, boolean isNewJoin) throws Exception {
		UBatchSupplyer bsupply = baseDAO.get(UBatchSupplyer.class, order.getWeiId());
		if (bsupply != null) {
			// 1.修改批发号供应商表状态 保证金
			bsupply.setStatus(Short.parseShort(SupplierStatusEnum.PayIn.toString()));
			bsupply.setInTime(new Date());
			bsupply.setValidBegDate(new Date());
			bsupply.setValidEndDate(DateUtils.addDate(new Date(), 365));
			bsupply.setBond(BAmountSettings.batchSupplyerBond);
			bsupply.setServiceFee(BAmountSettings.batchSupplyerServiceFee);
			USupplierFollowMsg msg = new USupplierFollowMsg();// ****
			msg.setCreateTime(new Date());
			msg.setContent("供应商升级认证点缴费");
			msg.setOperaterId(order.getWeiId());
			msg.setWeiId(order.getWeiId());
			msg.setRecordType(Integer.parseInt(SupplierStatusEnum.PayIn.toString()));
			int identity = 0;
			// 2.更新供应商表的身份类型
			USupplyer supplyer = baseDAO.get(USupplyer.class, order.getWeiId());
			if (supplyer != null && !BitOperation.isSupply(supplyer.getType(), SupplierTypeEnum.BatchSupplier)) {
				identity = (supplyer.getType() == null ? 0 : supplyer.getType()) + Integer.parseInt(SupplierTypeEnum.BatchSupplier.toString());
				supplyer.setType((short) identity);
				baseDAO.update(supplyer);

				UUserAssist assist = baseDAO.get(UUserAssist.class, order.getWeiId());
				if (assist != null && identity > 0) {
					assist.setIdentity((assist.getIdentity() == null ? 0 : assist.getIdentity()) + Integer.parseInt(UserIdentityType.batchSupplier.toString()));
					baseDAO.update(assist);
				} else {
					assist = new UUserAssist();
					assist.setWeiId(order.getWeiId());
					assist.setIdentity(Integer.parseInt(UserIdentityType.batchSupplier.toString()));
					baseDAO.save(assist);
				}
			}

			if (isNewJoin) {
				// 3.修改供应商商品的Type
				String hql_pp = " update PProducts p set p.supperType=? where p.supplierWeiId=?";
				baseDAO.executeHql(hql_pp, (short) identity, order.getWeiId());
				// 4.修改上架表中 供应商上架自己的商品的状态
				String hql_cl = "update PClassProducts p set p.type=? where p.supplierweiId=?";
				baseDAO.executeHql(hql_cl, Short.parseShort("1"), order.getWeiId());

				// 消息推送给认证员
				insertPushMsg(bsupply.getVerifier(), 40, order.getPayOrderId(), "供应商申请：*" + order.getWeiId().toString() + "*已缴纳服务费，正式成为供应商");
			}
			// 消息推送给付款人
			insertPushMsg(order.getWeiId(), 100, order.getPayOrderId(), "恭喜你！缴费成功，您已成为批发号供应商！");

		}
	}

	/**
	 * 充值订单支付
	 * 
	 * @param order
	 */
	public void editChongZhi(OPayOrder order) throws Exception {
		UWalletDetails details = new UWalletDetails();
		details.setAmount(order.getTotalPrice());
		details.setCreateTime(new Date());
		details.setMainType(Short.parseShort(WalletMainTypeEnum.income.toString()));
		details.setPayOrder(order.getPayOrderId());
		details.setType(Short.parseShort(AmountTypeEnum.chongzhi.toString()));
		details.setWeiId(order.getWeiId());
		UWallet wallet = baseDAO.get(UWallet.class, order.getWeiId());
		if (wallet != null) {
			wallet.setBalance(wallet.getBalance() == null ? 0 : wallet.getBalance() + details.getAmount());
			baseDAO.update(wallet);
		} else {
			wallet = new UWallet();
			wallet.setBalance(details.getAmount());
			wallet.setAccountIng(0d);
			wallet.setAccountNot(0d);
			wallet.setWeiId(details.getWeiId());
			wallet.setCreateTime(new Date());
			baseDAO.save(wallet);
		}
		details.setRestAmount(wallet.getBalance());
		baseDAO.save(details);
	}

	/**
	 * 工厂号 供应商订进驻支付成功处理
	 * 
	 * @param order
	 */
	public void editYunSupply(OPayOrder order, Boolean isAllocateReward) throws Exception {
		// 1.获取该供应商信息
		UYunSupplier supplier = baseDAO.get(UYunSupplier.class, order.getWeiId());
		if (supplier != null && supplier.getStatus() != Short.parseShort(SupplierStatusEnum.PayIn.toString())) {// 1*
			// 2.更新云商供应商表状态 保证金 进驻时间 等
			supplier.setStatus(Short.parseShort(SupplierStatusEnum.PayIn.toString()));
			supplier.setBond(BAmountSettings.yunSupplyerBond);
			if (isAllocateReward) // 如果分配奖励 才更新进驻时间
			{
				supplier.setPayTime(new Date());
			}
			// 3.更新供应商表状态
			USupplyer su = baseDAO.get(USupplyer.class, order.getWeiId());
			int identity = 0;
			if (su != null && !BitOperation.isSupply(su.getType(), SupplierTypeEnum.YunSupplier)) {
				identity = su.getType() == null ? 0 : su.getType() + Integer.parseInt(SupplierTypeEnum.YunSupplier.toString());
				su.setType((short) identity);
				baseDAO.update(su);

				UUserAssist assist = baseDAO.get(UUserAssist.class, order.getWeiId());
				if (assist != null) {
					assist.setIdentity((assist.getIdentity() == null ? 0 : assist.getIdentity()) + Integer.parseInt(UserIdentityType.yunSupplier.toString()));
					baseDAO.update(assist);
				} else {
					assist = new UUserAssist();
					assist.setWeiId(order.getWeiId());
					assist.setIdentity(Integer.parseInt(UserIdentityType.yunSupplier.toString()));
					baseDAO.save(assist);
				}
			}
			// 4.分配金额
			double socureAmout = 0;
			double followAmout = 0;
			if (supplier.getServiceType() == Short.parseShort(BYunSupplierServiceTypeEnum.OneYear.toString())) {
				socureAmout = BAmountSettings.yunSupplyerOneYearSocure;
				followAmout = BAmountSettings.yunSupplyerOneYearFollow;
			} else if (supplier.getServiceType() == Short.parseShort(BYunSupplierServiceTypeEnum.ThreeYear.toString())) {
				socureAmout = BAmountSettings.yunSupplyerThreeYearSocure;
				followAmout = BAmountSettings.yunSupplyerThreeYearFollow;
			}
			DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
			Date shopDate = dateFormat1.parse("2016-08-28");
			Date now = new Date();
			if (isAllocateReward) {
				// 来源佣金
				if (supplier.getSourceReciever() > 0 && socureAmout > 0) {
					UTradingDetails td = new UTradingDetails();
					td.setAmount(socureAmout);
//					if (now.getTime() < shopDate.getTime()) // 在店庆前
//					{
//						td.setCreateTime(DateUtils.addDate(now, 183));
//					} else {
						td.setCreateTime(new Date());
//					}
					td.setLtwoType(Short.parseShort(RzyjType.SourceAmount.toString()));
					td.setOrderNo(order.getPayOrderId());
					td.setSupplyOrder(order.getWeiId().toString());
					td.setState(Short.parseShort(AmountStatusEnum.weiFangKuan.toString()));
					td.setType(Short.parseShort(AmountTypeEnum.rzYj.toString()));
					td.setWeiId(supplier.getSourceReciever());// 上交给老大
					td.setFromWeiID(supplier.getSourceWeiId());// 金额来源微店
					baseDAO.save(td);
					UP_redis_UserWallet(order.getPayOrderId(), td.getWeiId(), 0, td.getAmount(), 1);

				}
				// 跟进佣金
				if (supplier.getRzReciever() > 0 && followAmout > 0) {
					UTradingDetails td = new UTradingDetails();
					td.setAmount(followAmout);
//					if (now.getTime() < shopDate.getTime()) // 在店庆前
//					{
//						td.setCreateTime(DateUtils.addDate(now, 183));
//					} else {
						td.setCreateTime(new Date());
//					}
					td.setLtwoType(Short.parseShort(RzyjType.FollowAmout.toString()));
					td.setOrderNo(order.getPayOrderId());
					td.setSupplyOrder(order.getWeiId().toString());
					td.setState(Short.parseShort(AmountStatusEnum.weiFangKuan.toString()));
					td.setType(Short.parseShort(AmountTypeEnum.rzYj.toString()));
					td.setWeiId(supplier.getRzReciever());// 上交老大
					td.setFromWeiID(supplier.getRzWeiId());// 金额来源
					baseDAO.save(td);
					UP_redis_UserWallet(order.getPayOrderId(), td.getWeiId(), 0, td.getAmount(), 1);

				}
				// 3.修改供应商商品的Type
				String hql_pp = " update PProducts p set p.supperType=? where p.supplierWeiId=?";
				baseDAO.executeHql(hql_pp, (short) identity, order.getWeiId());
				// 4.修改上架表中 供应商上架自己的商品的状态
				String hql_cl = "update PClassProducts p set p.type=? where p.supplierweiId=?";
				baseDAO.executeHql(hql_cl, Short.parseShort("1"), order.getWeiId());

			}
		}
	}

	/**
	 * 工厂号 认证员支付保证金成功处理
	 * 
	 * @param order
	 */
	public void editYunVerify(OPayOrder order) {
		// 1.获取认证员信息
		UYunVerifier verifier = baseDAO.get(UYunVerifier.class, order.getWeiId());
		if (verifier != null && verifier.getType() != Short.parseShort(YunVerifyTypeEnum.zsVerify.toString())) {
			// 2.修改云商认证员表 身份 保证金 初始化 启动平均分配认证员标示(随机分配)
			int verAllotCount = 0;
			int supAllotCount = 0;
			String hql = "from UYunVerifier u where u.isActive=1 order by u.verAllotCount asc,supAllotCount asc";
			List<UYunVerifier> list = baseDAO.find(hql);

			UYunVerifier yv = null;// payOrderDAO.getYunVerifierByCount();
			if (list != null && list.size() > 0) {
				yv = list.get(0);
			}
			if (yv != null) {
				verAllotCount = yv.getVerAllotCount() == null ? 0 : yv.getVerAllotCount();
				supAllotCount = yv.getSupAllotCount() == null ? 0 : yv.getSupAllotCount();
			}
			verifier.setType(Short.parseShort(YunVerifyTypeEnum.zsVerify.toString()));
			verifier.setBond(BAmountSettings.yunVerifierBond);
			verifier.setInTime(new Date());
			verifier.setVerAllotCount(verAllotCount);
			verifier.setSupAllotCount(supAllotCount);
			verifier.setIsActive((short) 1);
			// 3.修改认证员表身份
			UVerifier uv = baseDAO.get(UVerifier.class, order.getWeiId());
			if (uv != null && !BitOperation.verIdentity(uv.getType(), UserIdentityType.percent)) {
				int identity = (uv.getType() == null ? 0 : uv.getType()) + Short.parseShort(UserIdentityType.percent.toString());
				uv.setType((short) identity);
				baseDAO.update(uv);

				// ------------------------------------------
				UUserAssist assist = baseDAO.get(UUserAssist.class, order.getWeiId());
				if (assist != null) {
					assist.setIdentity(assist.getIdentity().intValue() + Integer.parseInt(UserIdentityType.percent.toString()));
					baseDAO.update(assist);
				} else {
					assist = new UUserAssist();
					assist.setWeiId(order.getWeiId());
					assist.setIdentity(Integer.parseInt(UserIdentityType.percent.toString()));
					baseDAO.save(assist);
				}
			}
		}
	}

	/**
	 * 成为落地店
	 * 
	 * @param weiid
	 * @param supplyOrderId
	 */
	public void saveProductShop(long weiid, String supplyOrderId) {
		OSupplyerOrder supplyerOrder = baseDAO.get(OSupplyerOrder.class, supplyOrderId);
		if (supplyerOrder.getSourse() != null && supplyerOrder.getSourse() == Integer.parseInt(ShoppingCarSourceEnum.Landi.toString())) {
			List<OProductOrder> productOrders = ordersDao.find_ProductOrderBySupOrderIds(new String[] { supplyOrderId });
			if (productOrders != null && productOrders.size() > 0) {
				PProducts pro = baseDAO.get(PProducts.class, productOrders.get(0).getProductId());
				if (pro == null)
					return;
				long supplyWeiid = pro.getSupplierWeiId();
				int identity = isShop(weiid, productOrders.get(0).getProductId());
				if (!BitOperation.verIdentity(identity, UserIdentityType.Ground) && supplyerOrder.getSourse() != null && supplyerOrder.getSourse() == Integer.parseInt(ShoppingCarSourceEnum.Landi.toString())) {// 落地首单
					String hql = " from UDemandProduct u where u.productId=?";
					UDemandProduct demandMod = baseDAO.getUniqueResultByHql(hql, productOrders.get(0).getProductId());
					OOrderAddr address = baseDAO.get(OOrderAddr.class, supplyerOrder.getAddrId());
					if (address != null && demandMod != null) {
						USupplyDemand supplyDemand = baseDAO.get(USupplyDemand.class, demandMod.getDemandId());
						if (supplyDemand != null) {// 满足落地店首单金额要求，成为落地店
							if (supplyDemand.getOrderAmout() != null && supplyDemand.getOrderAmout() <= supplyerOrder.getTotalPrice()) {
								// 保存用户落地店身份
								UUserAssist userAssist = baseDAO.get(UUserAssist.class, weiid);
								if (userAssist != null) {
									if (!BitOperation.verIdentity(userAssist.getIdentity() == null ? 0 : userAssist.getIdentity(), UserIdentityType.Ground)) {
										userAssist.setIdentity((userAssist.getIdentity() == null ? 0 : userAssist.getIdentity()) + Short.parseShort(UserIdentityType.Ground.toString()));
										baseDAO.update(userAssist);
									}
								} else {
									userAssist = new UUserAssist();
									userAssist.setWeiId(weiid);
									userAssist.setIdentity(Integer.parseInt(UserIdentityType.Ground.toString()));
									baseDAO.save(userAssist);
								}
								String hql_channel = " from USupplyChannel u where u.demandId=? and u.weiId=?  and u.channelType=? ";

								// TODO 落地、代理关系表
								USupplyChannel channelMod = baseDAO.getUniqueResultByHql(hql_channel, demandMod.getDemandId(), weiid, Short.parseShort(SupplyChannelTypeEnum.ground.toString())); // new
																																																	// USupplyChannel();
								if (channelMod != null) {
									channelMod.setState((short) 1);
									channelMod.setFollowVerifier(supplyerOrder.getVerWeiId());
									channelMod.setReward(supplyDemand.getShopReward());
									baseDAO.update(channelMod);
								} else {
									channelMod = new USupplyChannel();
									channelMod.setWeiId(weiid);
									channelMod.setDemandId(demandMod.getDemandId());
									channelMod.setCreateTime(new Date());
									channelMod.setReward(supplyDemand.getShopReward());
									channelMod.setChannelType(Short.parseShort(SupplyChannelTypeEnum.ground.toString()));
									channelMod.setVerifier(supplyerOrder.getVerWeiId());
									channelMod.setFollowVerifier(supplyerOrder.getVerWeiId());
									channelMod.setSupplyId(supplyWeiid);
									channelMod.setState((short) 1);
									channelMod.setPayedReward(Short.parseShort(IsPayReward.notpayed.toString()));// 未交易完成的
									baseDAO.save(channelMod);
								}

								// TODO 落地、代理关系表
								UProductShop shopMod = baseDAO.get(UProductShop.class, channelMod.getChannelId());
								if (shopMod == null) {
									shopMod = new UProductShop();
									shopMod.setWeiId(weiid);
									shopMod.setWeiName(commonService.getShopNameByWeiId(weiid));
									shopMod.setDemandId(demandMod.getDemandId());
									shopMod.setProvince(address.getProvince());
									shopMod.setCity(address.getCity());
									shopMod.setArea(address.getDistrict());
									shopMod.setAddress(address.getDetailAddr());
									String addStr = regionService.getNameByCode(address.getProvince()) + regionService.getNameByCode(address.getCity()) + regionService.getNameByCode(address.getDistrict()) + address.getDetailAddr();
									shopMod.setRegionStr(addStr);
									shopMod.setCreateTime(new Date());
									shopMod.setChannelId(channelMod.getChannelId());
									shopMod.setSupplyId(supplyWeiid);
									shopMod.setLinkMan(address.getReceiverName());
									shopMod.setPhone(address.getMobilePhone());
									baseDAO.save(shopMod);
								}

								// productDao.UP_onshelves(channelMod.getDemandId(),weiid);
								try {
									productSerice.shelveProductToAgeStore(channelMod.getDemandId(), channelMod.getSupplyId(), weiid, (short) 2, (short) 1);
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								// TODO 优惠券赠送
								for (OProductOrder pp : productOrders) {
									double amount = pp.getPrice() * pp.getCount();
									UWeiCoinLog coinLog = new UWeiCoinLog();
									coinLog.setWeiId(weiid);
									coinLog.setSupplyOrderId(supplyOrderId);
									coinLog.setProductOrderId(pp.getProductOrderId());
									coinLog.setCoinAmount(amount);
									coinLog.setAbleCoinAmount(amount);
									coinLog.setCreateTime(new Date());
									coinLog.setUseType(1);
									coinLog.setDeleted(0);
									coinLog.setState(0);
									baseDAO.save(coinLog);
								}
							}
						}

					}
				}
			}
		}

	}

	/**
	 * 消息推送
	 * 
	 * @param receptWeiid
	 * @param type
	 * @param orderno
	 * @param content
	 * @return
	 */
	public boolean insertPushMsg(long receptWeiid, int type, String orderno, String content) {
		try {
			UPushMessage msg = new UPushMessage();
			msg.setReciptWeiId(receptWeiid);
			msg.setCreateTime(new Date());
			msg.setMsgType((short) type);
			msg.setObjectId(orderno);
			msg.setPushWeiId((long) 1);
			msg.setExtra("1");
			msg.setPushContent(content);
			ordersDao.insertSendPushMsg(msg);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return true;
	}

	/**
	 * 当订单完成交易时，做如下操作 1、落地店返佣订单记录（记录表URepayOrderLog） 2、平台抽点订单记录
	 * 
	 * @param supplyOrderId
	 */
	public void insertTemp(String supplyOrderId) {
		OSupplyerOrder supplyerOrder = baseDAO.get(OSupplyerOrder.class, supplyOrderId);
		if (supplyerOrder == null || supplyerOrder.getOrderType() == null)
			return;
		int identity = 0;
		List<OProductOrder> proList = ordersDao.find_ProductOrderBySupOrderIds(new String[] { supplyOrderId });
		if (proList != null && proList.size() > 0) {
			identity = isShop(supplyerOrder.getBuyerID(), proList.get(0).getProductId());
		}
		if (supplyerOrder.getOrderType() == Integer.parseInt(OrderTypeEnum.Jinhuo.toString()) || supplyerOrder.getOrderType() == Integer.parseInt(OrderTypeEnum.Puhuo.toString())) {
			if (BitOperation.verIdentity(identity, UserIdentityType.Ground) && !BitOperation.verIdentity(identity, UserIdentityType.Agent)) {
				URepayOrderLog repayOrderLog = baseDAO.get(URepayOrderLog.class, supplyOrderId);
				if (repayOrderLog == null) {
					repayOrderLog = new URepayOrderLog();
					repayOrderLog.setSupplyOrderId(supplyOrderId);
					repayOrderLog.setWeiId(supplyerOrder.getBuyerID());
					repayOrderLog.setTotalPrice(supplyerOrder.getTotalPrice() + ParseHelper.toDouble(String.valueOf(supplyerOrder.getPostage())));
					baseDAO.save(repayOrderLog);
				}
			}

			String orderNo = GenerateOrderNum.getInstance().GenerateOrder();
			double totalPrice = supplyerOrder.getTotalPrice() + ParseHelper.toDouble(String.valueOf(supplyerOrder.getPostage()));
			// TODO 抽点 金额比例
			double payPrice = totalPrice * BAmountSettings.platformPercent;

			// TODO 如果钱包的钱足够 ，自动支付平台抽点
			// 插入抽点支付订单
			OPayOrder payInfo = new OPayOrder();
			payInfo.setPayOrderId(orderNo);
			payInfo.setWeiId(supplyerOrder.getSupplyerId());// 谁来买单，平台号支付给微店网
			payInfo.setTotalPrice(payPrice);
			payInfo.setOrderFrom(supplyerOrder.getOrderFrom());
			payInfo.setOrderTime(new Date());
			payInfo.setOrderDate(new Date());
			payInfo.setState(Short.parseShort(OrderStatusEnum.Nopay.toString()));
			payInfo.setTypeState(Short.parseShort(OrderTypeEnum.ChouDian.toString()));
			baseDAO.save(payInfo);
			// 平台服务费记录表
			UPlatformServiceOrder plat = new UPlatformServiceOrder();
			plat.setPayOrderId(orderNo);
			plat.setSupplyOrderId(supplyOrderId);
			plat.setBuyerWeiid(supplyerOrder.getBuyerID());
			plat.setSellerWeiid(supplyerOrder.getSupplyerId());
			plat.setTotalPrice(totalPrice);
			plat.setPayAmount(payPrice);
			plat.setCreateTime(new Date());
			plat.setState(Integer.parseInt(OrderStatusEnum.Nopay.toString()));
			baseDAO.save(plat);
		}
	}

	/**
	 * 
	 * 完成抽点订单支付 逻辑处理 （平台号进货单、铺货单 ，微店网抽点）
	 */
	public void updateChouDian(String payOrderId) throws Exception {
		String hql = " from UPlatformServiceOrder p where p.payOrderId=? ";
		List<UPlatformServiceOrder> platlist = baseDAO.find(hql, payOrderId);
		if (platlist != null && platlist.size() > 0) {
			for (UPlatformServiceOrder plat : platlist) {
				if (plat != null) {
					if (plat.getState() != null && plat.getState() == Integer.parseInt(OrderStatusEnum.Payed.toString()))
						throw new Exception("订单已支付");
					Date nowtime = new Date();
					plat.setState(Integer.parseInt(OrderStatusEnum.Payed.toString()));
					plat.setPayTime(nowtime);
					baseDAO.update(plat);
					// 自己公司（微店网）的抽成
					UTradingDetails weiSystem = new UTradingDetails();
					weiSystem.setAmount(plat.getPayAmount());
					weiSystem.setCreateTime(nowtime);
					// weiSystem.setLtwoType(Short.parseShort(RzyjType.choudian.toString()));
					weiSystem.setOrderNo(plat.getPayOrderId());
					weiSystem.setSupplyOrder(plat.getSupplyOrderId());
					weiSystem.setState(Short.parseShort(UserAmountStatus.yiFangKuan.toString()));
					weiSystem.setType(Short.parseShort(UserAmountType.choudian.toString()));
					weiSystem.setWeiId((long) 111);
					weiSystem.setSurplusAmout(weiSystem.getAmount());
					baseDAO.save(weiSystem);
					// 钱包改版
					// UP_redis_UserWallet(payOrderId, weiSystem.getWeiId(), 0,
					// weiSystem.getAmount(), 1);
				}
			}
		} else {
			logger.error("没有找到相应的记录UPlatformServiceOrder:payOrderId=" + payOrderId);
			throw new RuntimeException("没有找到相应的记录");
		}
	}

	/**
	 * 支付悬赏
	 * 
	 * @throws Exception
	 */
	public void updateXuanShang(String payOrderId) throws Exception {
		String hql = " from OPayReward o where o.payOrderId=? ";
		List<OPayReward> rewardlist = baseDAO.find(hql, payOrderId);
		if (rewardlist != null && rewardlist.size() > 0) {
			Map<Long, UWallet> userWalletMap = new HashMap<Long, UWallet>();
			for (OPayReward reward : rewardlist) {
				reward.setPayTime(new Date());
				baseDAO.update(reward);
				USupplyChannel channel = baseDAO.get(USupplyChannel.class, reward.getChannelId());
				if (channel != null) {
					if (channel.getPayedReward() != null && channel.getPayedReward() == Short.parseShort(OrderStatusEnum.Payed.toString())) {
						throw new Exception("悬赏已经被支付过。");
					}
					Date nowtime = new Date();
					channel.setPayedReward(Short.parseShort(OrderStatusEnum.Payed.toString()));
					baseDAO.update(channel);
					long verWeiid = 111;
					long followWid = 111;
					if (channel.getChannelType() != null && channel.getChannelType().equals(Short.parseShort(SupplyChannelTypeEnum.Agent.toString()))) {
						UProductAgent agent = baseDAO.get(UProductAgent.class, channel.getChannelId());
						if (agent != null) {
							followWid = agentService.getVerifyWeiID(channel.getVerifier(), agent.getCity());
							verWeiid = agentService.getSourceCommissionWeiIdLong(channel.getDemandId(), channel.getVerifier());
						}
					} else {
						if (channel.getVerifier() != null && channel.getVerifier() > 0) {
							UWeiSeller uSeller = baseDAO.get(UWeiSeller.class, channel.getVerifier());
							if (uSeller != null) {
								verWeiid = channel.getVerifier();
								followWid = channel.getVerifier();
							}
						}
					}
					double payAmount = channel.getReward().doubleValue();

					if (!userWalletMap.containsKey(111l)) {
						UWallet sysUserUWallet = baseDAO.get(UWallet.class, 111l);
						userWalletMap.put(111l, sysUserUWallet);
					}
					UWallet verUWallet = null;// 来源人钱包
					UWallet followUWallet = null;// 跟进人钱包
					if (verWeiid > 0) {// 来源
						if (!userWalletMap.containsKey(verWeiid)) {
							Double amount = payAmount * 0.4;
							verUWallet = baseDAO.get(UWallet.class, verWeiid);
							if (verUWallet != null) {
								amount += verUWallet.getBalance();
								verUWallet.setBalance(amount);
								userWalletMap.put(verWeiid, verUWallet);
							} else {
								verUWallet = new UWallet();
								verUWallet.setWeiId(verWeiid);
								verUWallet.setBalance(amount);
								baseDAO.save(verUWallet);
								userWalletMap.put(verWeiid, verUWallet);
							}
						} else {
							verUWallet = userWalletMap.get(verWeiid);
							verUWallet.setBalance(verUWallet.getBalance() + payAmount * 0.4);
							userWalletMap.put(verWeiid, verUWallet);
						}
					}
					if (followWid > 0) {
						if (!userWalletMap.containsKey(followWid)) {
							Double amount = payAmount * 0.4;
							followUWallet = baseDAO.get(UWallet.class, followWid);
							if (followUWallet != null) {
								amount += followUWallet.getBalance();
								followUWallet.setBalance(amount);
								userWalletMap.put(followWid, followUWallet);
							} else {
								followUWallet = new UWallet();
								followUWallet.setWeiId(followWid);
								followUWallet.setBalance(amount);
								baseDAO.save(followUWallet);
								userWalletMap.put(followWid, followUWallet);
							}
						} else {
							followUWallet = userWalletMap.get(followWid);
							followUWallet.setBalance(followUWallet.getBalance() + payAmount * 0.6);
							userWalletMap.put(followWid, followUWallet);
						}
					}

					if (verWeiid > 0) {// 来源
						UTradingDetails weiSystem = new UTradingDetails();
						weiSystem.setAmount(payAmount * 0.4);
						weiSystem.setCreateTime(nowtime);
						// weiSystem.setLtwoType(Short.parseShort(RzyjType..toString()));
						weiSystem.setOrderNo(payOrderId);
						weiSystem.setState(Short.parseShort(UserAmountStatus.yiFangKuan.toString()));
						weiSystem.setType(Short.parseShort(UserAmountType.reward.toString()));
						// weiSystem.setLtwoType(Short.parseShort(LtwoTypeEnum.Reward.toString()));
						weiSystem.setWeiId(verWeiid);
						weiSystem.setSurplusAmout(weiSystem.getAmount());
						baseDAO.save(weiSystem);

						// UP_redis_UserWallet(payOrderId, weiSystem.getWeiId(),
						// 0, weiSystem.getAmount(), 1);

						UWalletDetails details = new UWalletDetails();
						details.setAmount(weiSystem.getAmount());
						details.setCreateTime(new Date());
						details.setMainType(Short.parseShort(WalletMainTypeEnum.income.toString()));
						details.setPayOrder(payOrderId);
						details.setType(Short.parseShort(AmountTypeEnum.reward.toString()));
						details.setWeiId(weiSystem.getWeiId());
						details.setRestAmount(verUWallet.getBalance());
						baseDAO.save(details);

					}
					if (followWid > 0) {
						// 跟进人
						UTradingDetails weiSystem = new UTradingDetails();
						weiSystem.setAmount(payAmount * 0.6);
						weiSystem.setCreateTime(nowtime);
						weiSystem.setOrderNo(payOrderId);
						weiSystem.setState(Short.parseShort(UserAmountStatus.yiFangKuan.toString()));
						// weiSystem.setType(Short.parseShort(UserAmountType.reward.toString()));
						weiSystem.setType(Short.parseShort(UserAmountType.reward.toString()));
						// weiSystem.setLtwoType(Short.parseShort(LtwoTypeEnum.Reward.toString()));
						weiSystem.setWeiId(followWid);
						weiSystem.setSurplusAmout(weiSystem.getAmount());
						baseDAO.save(weiSystem);

						// UP_redis_UserWallet(payOrderId, weiSystem.getWeiId(),
						// 0, weiSystem.getAmount(), 1);

						UWalletDetails details = new UWalletDetails();
						details.setAmount(weiSystem.getAmount());
						details.setCreateTime(new Date());
						details.setMainType(Short.parseShort(WalletMainTypeEnum.income.toString()));
						details.setPayOrder(payOrderId);
						details.setType(Short.parseShort(AmountTypeEnum.reward.toString()));
						details.setWeiId(weiSystem.getWeiId());
						details.setRestAmount(followUWallet.getBalance());
						baseDAO.save(details);
					}

				} else {
					throw new Exception("未找到相应的渠道信息：" + payOrderId);
				}
			}
			for (UWallet uu : userWalletMap.values()) {
				baseDAO.saveOrUpdate(uu);
			}

		} else {
			throw new Exception("未找到相应的支付记录：" + payOrderId);
		}
	}

	/**
	 * 
	 * @param weiid
	 * @param productId
	 * @param type
	 * @return
	 */
	public int isShop(long weiid, long productId) {
		int userIdentity = 1;
		String hqlString = " from UDemandProduct u where u.productId=? ";
		UDemandProduct demandMod = baseDAO.getNotUniqueResultByHql(hqlString, productId);
		if (demandMod != null) {
			String hql_u = " from USupplyChannel u where u.demandId=? and u.weiId=? ";
			List<USupplyChannel> channels = baseDAO.find(hql_u, demandMod.getDemandId(), weiid);
			if (channels != null && channels.size() > 0) {
				for (USupplyChannel uu : channels) {
					if (uu.getChannelType() != null && uu.getChannelType().shortValue() == Short.parseShort(SupplyChannelTypeEnum.ground.toString())) {
						if (uu.getState().shortValue() == 1) {
							userIdentity += Integer.parseInt(UserIdentityType.Ground.toString());
						}
					} else if (uu.getChannelType() != null && uu.getChannelType().shortValue() == Short.parseShort(SupplyChannelTypeEnum.Agent.toString())) {
						if (uu.getState().shortValue() == 1) {
							userIdentity += Integer.parseInt(UserIdentityType.Agent.toString());
						}
					}
				}
			}
		}
		return userIdentity;
	}

	/**
	 * 获取 平台服务费订单、抽点订单 支付信息（合并支付）
	 * 
	 * @param weiid
	 * @param orderFrom
	 * @return
	 */
	public BResultMsg update_OrderPayInfo(long weiid, int orderFrom, int orderType, List<String> ids) throws Exception {
		BResultMsg result = new BResultMsg();
		result.setState(-1);

		Map<String, Object> mapParam = new HashMap<String, Object>();
		if (orderType == 1) {// 悬赏订单
			List<Integer> channelList = new ArrayList<Integer>();
			for (String id : ids) {
				channelList.add(ParseHelper.toInt(id));
			}
			String hql_channel = " from USupplyChannel u where u.channelId in (:channelids) ";
			mapParam.put("channelids", channelList);
			List<USupplyChannel> supplyChannels = baseDAO.findByMap(hql_channel, mapParam);
			if (supplyChannels != null && supplyChannels.size() > 0) {
				double totalPrice = 0;
				for (USupplyChannel cc : supplyChannels) {
					if (cc.getPayedReward() != null && cc.getPayedReward() == Short.parseShort(PayedRewardStatuus.Yes.toString())) {
						result.setMsg("不能重复支付");
						return result;
					}
					totalPrice += cc.getReward().doubleValue();

				}
				String payOrderId = update_getNewPayOrderId(weiid, totalPrice, Integer.parseInt(OrderTypeEnum.Reward.toString()), orderFrom);
				String hql_payreward = " from OPayReward r where r.channelId in (:ids) ";
				Map<String, Object> mapParam2 = new HashMap<String, Object>();
				mapParam2.put("ids", channelList);
				List<OPayReward> rewards = baseDAO.findByMap(hql_payreward, mapParam2);
				for (USupplyChannel cc : supplyChannels) {
					boolean ishave = false;// 是否已经有支付记录
					OPayReward reward = null;
					if (rewards != null && rewards.size() > 0) {
						for (OPayReward rr : rewards) {
							if (rr.getChannelId().intValue() == cc.getChannelId().intValue()) {
								ishave = true;
								reward = rr;
								continue;
							}
						}
					}
					if (ishave && reward != null) {
						reward.setPayOrderId(payOrderId);
						baseDAO.update(reward);
					} else {
						reward = new OPayReward();
						reward.setChannelId(cc.getChannelId());
						reward.setPayOrderId(payOrderId);
						baseDAO.save(reward);
					}
				}
				result.setState(1);
				result.setMsg(payOrderId);
			} else {
				result.setState(-1);
				result.setMsg("沒有找到相应的数据");
				throw new RuntimeException("沒有找到相应的数据");
			}

		} else if (orderType == 2) {// 平台服务费
			List<Long> serList = new ArrayList<Long>();
			for (String id : ids) {
				serList.add(ParseHelper.toLong(id));
			}
			String hql_Platform = " from UPlatformServiceOrder u where u.id in (:ids) ";
			mapParam.put("ids", serList);
			List<UPlatformServiceOrder> pList = baseDAO.findByMap(hql_Platform, mapParam);
			if (pList != null && pList.size() > 0) {
				double totalPrice = 0;
				for (UPlatformServiceOrder pp : pList) {
					if (pp.getState() == Integer.parseInt(OrderStatusEnum.Payed.toString())) {
						result.setState(-1);
						result.setMsg("有已支付的订单。");
						return result;
					}
					if (pp.getPayAmount() != null)
						totalPrice += pp.getPayAmount();
				}
				String payOrderId = update_getNewPayOrderId(weiid, totalPrice, Integer.parseInt(OrderTypeEnum.ChouDian.toString()), orderFrom);
				for (UPlatformServiceOrder pp : pList) {
					pp.setPayOrderId(payOrderId);
					baseDAO.update(pp);
				}
				result.setState(1);
				result.setMsg(payOrderId);
			} else {
				result.setState(-1);
				result.setMsg("沒有找到相应的数据");
				throw new RuntimeException("沒有找到相应的数据");
			}
		}

		return result;
	}

	/**
	 * 新生成支付订单（悬赏、抽点订单 合并支付用）
	 * 
	 * @param weiid
	 * @param totalPrice
	 * @param orderType
	 * @param orderFrom
	 * @return
	 * @throws Exception
	 */
	public String update_getNewPayOrderId(long weiid, double totalPrice, int orderType, int orderFrom) throws Exception {
		String payOrderId = GenerateOrderNum.getInstance().GenerateOrder();
		OPayOrder payInfo = new OPayOrder();
		payInfo.setPayOrderId(payOrderId);
		payInfo.setWeiId(weiid);
		payInfo.setTotalPrice(totalPrice);
		payInfo.setWalletmoney(0d);
		payInfo.setOrderFrom((short) orderFrom);
		payInfo.setOrderTime(new Date());
		payInfo.setOrderDate(new Date());
		payInfo.setState(Short.parseShort(OrderStatusEnum.Nopay.toString()));
		payInfo.setTypeState((short) orderType);
		baseDAO.save(payInfo);
		return payOrderId;
	}

	public double UP_getCoinPayAmount(String orderNo) {
		OPayOrder payOrder = getOPayOrder(orderNo, true);
		double payCoin = 0;
		if (payOrder != null) {
			double coinTotal = 0;
			boolean useCash = false;
			UWallet wallet = baseDAO.get(UWallet.class, payOrder.getWeiId());
			if (wallet == null || wallet.getTotalCoin() == null || wallet.getTotalCoin().doubleValue() <= 0) {
				return 0;
			} else {
				coinTotal = wallet.getTotalCoin();
			}
			double canUseCoin = 0;
			if (payOrder.getTypeState() != null && payOrder.getTypeState() == Short.parseShort(OrderTypeEnum.Jinhuo.toString())) {
				List<OSupplyerOrder> supplyerOrders = ordersDao.find_SupplyerOrderByOrderID(payOrder.getPayOrderId());
				if (supplyerOrders != null && supplyerOrders.size() > 0) {
					for (OSupplyerOrder ss : supplyerOrders) {
						List<OProductOrder> productOrders = ordersDao.find_ProductOrderBySupOrderIds(new String[] { ss.getSupplierOrderId() });
						if (productOrders != null && productOrders.size() > 0) {
							int identity = isShop(payOrder.getWeiId(), productOrders.get(0).getProductId());
							if (!BitOperation.verIdentity(identity, UserIdentityType.Agent)) {
								canUseCoin += ss.getTotalPrice() * 0.1;
								useCash = true;
							}
						}
					}
				}
			}
			if (useCash) {
				if (coinTotal >= canUseCoin)
					payCoin = canUseCoin;
				else {
					payCoin = coinTotal;
				}
				OPayOrderExtend extend = baseDAO.get(OPayOrderExtend.class, payOrder.getPayOrderId());
				if (extend != null) {
					extend.setCoinAmount(payCoin);
					baseDAO.update(extend);
				} else {
					extend = new OPayOrderExtend();
					extend.setOpayOrderId(payOrder.getPayOrderId());
					extend.setCoinAmount(payCoin);
					baseDAO.save(extend);
				}
			}
		}
		return payCoin;
	}

	public void UP_CoinPayAmountDel(String orderNo) {
		OPayOrder payOrder = getOPayOrder(orderNo, true);
		if (payOrder == null)
			return;
		OPayOrderExtend extend = baseDAO.get(OPayOrderExtend.class, payOrder.getPayOrderId());
		if (extend != null) {
			extend.setCoinAmount(0d);
			baseDAO.update(extend);
		}
	}

	public PayOrderInfoVO getPayOrderInfo(String orderId, long weiid, boolean lastOne) {
		if (ObjectUtil.isEmpty(orderId))
			return null;
		OPayOrder payOrder = getOPayOrder(orderId, lastOne);
		if (payOrder != null) {
			PayOrderInfoVO result = new PayOrderInfoVO();
			result.setOrderId(payOrder.getPayOrderId());
			result.setAmount(payOrder.getTotalPrice());

			double coinTotal = 0;
			boolean useCash = false;
			UWallet wallet = baseDAO.get(UWallet.class, weiid);
			if (wallet != null) {
				result.setOkweiCoin(wallet.getWeiDianCoin() == null ? 0.0 : wallet.getWeiDianCoin());
			} else {
				result.setOkweiCoin(0.0);
			}
			if (payOrder.getTypeState() != null && payOrder.getTypeState() != Short.parseShort(OrderTypeEnum.Puhuo.toString()) && payOrder.getTypeState() != Short.parseShort(OrderTypeEnum.PuhuoHeader.toString()) && payOrder.getTypeState() != Short.parseShort(OrderTypeEnum.PuhuoTail.toString())
					&& payOrder.getTypeState() != Short.parseShort(OrderTypeEnum.PuhuoFull.toString()) && payOrder.getTypeState() != Short.parseShort(OrderTypeEnum.Reward.toString()) && payOrder.getTypeState() != Short.parseShort(OrderTypeEnum.ChouDian.toString())
					&& payOrder.getTypeState() != Short.parseShort(OrderTypeEnum.ChongZhi.toString())) {
				if (result.getOkweiCoin().doubleValue() != 0) {
					result.setUseOkweiCoin(1);
				}
			}
			if (wallet == null || wallet.getTotalCoin() == null || wallet.getTotalCoin().doubleValue() <= 0) {
				result.setUseCash(0);
				result.setCashAmount(0d);
				return result;
			} else {
				coinTotal = wallet.getTotalCoin();
			}
			double canUseCoin = 0;
			if (payOrder.getTypeState() != null && (payOrder.getTypeState() == Short.parseShort(OrderTypeEnum.Jinhuo.toString()) || payOrder.getTypeState() == Short.parseShort(OrderTypeEnum.Pt.toString()) || payOrder.getTypeState() == Short.parseShort(OrderTypeEnum.BatchOrder.toString()))) {
				List<OSupplyerOrder> supplyerOrders = ordersDao.find_SupplyerOrderByOrderID(orderId);
				if (supplyerOrders != null && supplyerOrders.size() > 0) {
					for (OSupplyerOrder ss : supplyerOrders) {
						if (ss.getIsActivity() != null && ss.getIsActivity() > 0) {
							result.setPayTimeLimit(600);
						}
						if (payOrder.getTypeState() == Short.parseShort(OrderTypeEnum.Jinhuo.toString())) {
							List<OProductOrder> productOrders = ordersDao.find_ProductOrderBySupOrderIds(new String[] { ss.getSupplierOrderId() });
							if (productOrders != null && productOrders.size() > 0) {
								int identity = isShop(weiid, productOrders.get(0).getProductId());
								if (!BitOperation.verIdentity(identity, UserIdentityType.Agent)) {
									canUseCoin += ss.getTotalPrice() * 0.1;
									useCash = true;
								}
							}
						}
					}
				}
			}
			if (useCash) {
				result.setUseCash(1);
				if (coinTotal >= canUseCoin) {
					result.setCashAmount(canUseCoin);
				} else {
					result.setCashAmount(coinTotal);
				}
			} else {
				result.setUseCash(0);
				result.setCashAmount(0d);
			}
			// 现金券不能和 微金币同时使用 优先使用现金券
			if (result.getUseCash() == 1) {
				result.setUseOkweiCoin(0);
			}
			return result;
		}
		return null;
	}

	/*
	 * ======================订单处理新版本 2016-7-8
	 * zy==========================================================
	 */

	public void editSupplyOrderPaySuccessNew(OPayOrder order) throws Exception {
		if (order == null || null == order.getPayOrderId() || "".equals(order.getPayOrderId())) {
			throw new RuntimeException("订单处理参数异常");// 供应商订单处理参数异常
		}
		// 获取该订单下所有供应商订单
		List<OSupplyerOrder> supplyOrderList = getSupplyerOrderList(order);
		// 现金券处理
		OPayOrderExtend payExtend = super.getById(OPayOrderExtend.class, order.getPayOrderId());
		if (payExtend != null && payExtend.getCoinAmount() != null) {
			editOrderCoinProcess(order.getWeiId(), payExtend, supplyOrderList);
		}
		if (supplyOrderList != null && supplyOrderList.size() > 0) {
			for (OSupplyerOrder ss : supplyOrderList) {
//				long l=0L;
//				if(ss.getActivityId()!=null&&ss.getActivityId()>0)
//					l=ss.getActivityId();
//				//是否参加购物全返活动
//				boolean b=false;
//				if(l>0){
//					AActivity aa=baseDAO.get(AActivity.class, l);
//					if(aa!=null&&aa.getType()!=null&&aa.getType()>0){
//						b=true;
//					}
//				}
				
				if (ss.getOrderType().intValue() == Integer.parseInt(SupplyOrderType.RetailAgent.toString())) {
					editRetailAgentProcessing(order, ss);// 分销区订单处理
				}
//				else if(b){ //活动全返区处理
//					editActivityProcessing(order,ss);
//				}
				else{
					editSupplyOrderProcessing(order, ss);
				}
			}
		}
	}

	/**
	 * 全返活动区订单处理 -- 待处理
	 * 
	 * @param order
	 * @param ss
	 * @throws Exception
	 */
	public void editActivityProcessing(OPayOrder order, OSupplyerOrder ss) throws Exception {
		
		if (ss.getState() != null && ss.getState() == Short.parseShort(OrderStatusEnum.Payed.toString())) {
			throw new RuntimeException("订单已支付");
		}
		Map<Short, OrderTypeEnum> mapType = new HashMap<Short, OrderTypeEnum>();
		for (OrderTypeEnum tt : OrderTypeEnum.values()) {
			mapType.put(Short.parseShort(tt.toString()), tt);
		}
		Map<Short, OrderStatusEnum> mapState = new HashMap<Short, OrderStatusEnum>();
		for (OrderStatusEnum tt : OrderStatusEnum.values()) {
			mapState.put(Short.parseShort(tt.toString()), tt);
		}
		Short supplyOrderType = Short.parseShort(String.valueOf(ss.getOrderType()));
		
		Date nowtime = new Date();
		List<OProductOrder> orderInfoList = ordersDao.find_ProductOrderBySupplyOrderId(ss.getSupplierOrderId()); // ArrayList<OProductOrder>();
		BOrderTypePower power = new BOrderTypePower(mapType.get(supplyOrderType), mapState.get(ss.getState()));
		// 修改供应商订单详情状态
		ss.setState(Short.parseShort(power.status.toString()));
		ss.setPayTime(nowtime);
		baseDAO.update(ss);
		List<BSendSMSVO> sendList = new ArrayList<BSendSMSVO>();
		
		// 交易额 货款部分 + 邮费部分
		double totalAmount=ParseHelper.getDoubleDefValue(ss.getPostage());
		//double totalAmount = ParseHelper.getDoubleDefValue(ss.getTotalPrice()) + ParseHelper.getDoubleDefValue(ss.getPostage());
		List<AActivityProducts> listP=baseDAO.find(" from AActivityProducts p where p.actId=? and p.productId in (select o.productId from OProductOrder o where o.supplierOrderId=?) ", ss.getActivityId(),ss.getSupplierOrderId());
		if(listP!=null && listP.size()>0)
		{
			for(AActivityProducts a : listP)
			{
				for(OProductOrder oo:orderInfoList)
				{
					if(oo.getProductId().longValue()== a.getProductId().longValue())
					{
						totalAmount+=(a.getTraddingPrice()==null?0.0:a.getTraddingPrice()*oo.getCount());
					}
				}
			}
		}
		//增加全返区活动，分配现金券给购买用户 每人最多5000
		if(ss.getIsActivity()!=null && ss.getIsActivity().shortValue()==1 &&(order.getTypeState()== Short.parseShort(OrderTypeEnum.Pt.toString())||order.getTypeState()==Short.parseShort(OrderTypeEnum.BatchOrder.toString()))) //活动带来的订单
		{
			updateTicket(ss);
		}
		// a 是否分配货款	
		UTradingDetails supplyAmountD = new UTradingDetails();
		supplyAmountD.setAmount(totalAmount);
		supplyAmountD.setCreateTime(nowtime);
		supplyAmountD.setLtwoType((short) 0);// Short.parseShort(HuoKuanType.Product.toString())
		supplyAmountD.setOrderNo(ss.getPayOrderId());
		supplyAmountD.setSupplyOrder(ss.getSupplierOrderId());
		supplyAmountD.setState(Short.parseShort(UserAmountStatus.weiFangKuan.toString()));
		supplyAmountD.setType(Short.parseShort(UserAmountType.supplyPrice.toString()));
		supplyAmountD.setWeiId(ss.getSupplyerId());			
		supplyAmountD.setSurplusAmout(supplyAmountD.getAmount());
		baseDAO.save(supplyAmountD);
		// 钱包改版
		UP_redis_UserWallet(order.getPayOrderId(), ss.getSupplyerId(), 0, supplyAmountD.getAmount(), 1);
		// 插入待发 短信
		String mobile_ss = ordersDao.getUserMobile(ss.getSupplyerId());
		if (ParseHelper.isMobile(mobile_ss)) {
			String[] param = new String[] { mobile_ss, String.format("%.2f", supplyAmountD.getAmount()) };
			BSendSMSVO ssvo = new BSendSMSVO();
			ssvo.setParam(param);
			ssvo.setWeiid(ss.getSupplyerId());
			ssvo.setVerify(VerifyCodeType.SupplyDeliver);
			sendList.add(ssvo);
		}
		
		// 3. ----- 货款部分处理 按单个商品进行结算佣金 货款------
		List<OProductOrder> supplyOIList = new ArrayList<OProductOrder>();
		if (orderInfoList != null && orderInfoList.size() > 0) {
			for (OProductOrder oo : orderInfoList) {
				if (ss.getSupplierOrderId().equals(oo.getSupplierOrderId()))
					supplyOIList.add(oo);
			}
		}

		if (supplyOIList != null && supplyOIList.size() > 0) {
		// 遍历该供应商的每一件商品进行分配
		for (OProductOrder product : supplyOIList) {
			// 如果是赠品 不分配佣金货款 抽成
			if (product.getOrderType() == 3) {
				continue;
			}
			PProducts pro = baseDAO.get(PProducts.class, product.getProductId());
			// 代理商、落地店 库存处理
			if (pro != null && pro.getCount() != null && pro.getCount().intValue() > product.getCount().intValue()) {
					pro.setCount(pro.getCount() - product.getCount());
					baseDAO.update(pro);
				} else if (pro != null) {
					pro.setCount(0);
					baseDAO.update(pro);
				}
			// 如果是活动商品，回写活动数据 add by tan 2016-05-17
			if (product.getIsActivity() == 1) {
				AActProductExp ae = baseDAO.get(AActProductExp.class, product.getProActId());
				if (ae != null) {
					try {
						baseDAO.executeHql(" update AActProductExp a set a.sellerCount=a.sellerCount+?,a.sellerAmount=a.sellerAmount+? where  a.proActId=?", product.getCount(), product.getCount() * product.getPrice(), product.getProActId());
					} catch (Exception ex) {

					}
				} else {
					ae = new AActProductExp();
					ae.setProActId(product.getProActId());
					ae.setProductId(product.getProductId());
					ae.setSellerAmount(product.getCount() * product.getPrice());
					ae.setSellerCount(product.getCount());
					baseDAO.save(ae);
				}
			}
			}
		}

		
		// 消息推送给供应商
		insertPushMsg(ss.getSupplyerId(), 32, ss.getSupplierOrderId(), "销售订单：您有一笔订单已支付，请尽快发货!");

		// 短信通知卖家 支付成功！
		String buyerMobile = ordersDao.getUserMobile(order.getWeiId());
		if (ParseHelper.isMobile(buyerMobile)) {
			String[] param = new String[] { buyerMobile, order.getPayOrderId() };
			BSendSMSVO ssvo = new BSendSMSVO();
			ssvo.setParam(param);
			ssvo.setWeiid(order.getWeiId());
			ssvo.setVerify(VerifyCodeType.SendWeiDeliver);
			sendList.add(ssvo);
		}
		// 批量处理发送短信
		if (sendList != null && sendList.size() > 0) {
			SendSMSByMobile smsUtil = new SendSMSByMobile();
			for (BSendSMSVO vo : sendList) {
				smsUtil.sendSMS(vo.getParam(), vo.getVerify(), vo.getWeiid());
			}
		}
	}
	
	/**
	 * 供应商订单处理 -- 待处理
	 * 
	 * @param order
	 * @param ss
	 * @throws Exception
	 */
	public void editSupplyOrderProcessing(OPayOrder order, OSupplyerOrder ss) throws Exception {
		if (ss.getState() != null && ss.getState() == Short.parseShort(OrderStatusEnum.Payed.toString())) {
			throw new RuntimeException("订单已支付");
		}
		Map<Short, OrderTypeEnum> mapType = new HashMap<Short, OrderTypeEnum>();
		for (OrderTypeEnum tt : OrderTypeEnum.values()) {
			mapType.put(Short.parseShort(tt.toString()), tt);
		}
		Map<Short, OrderStatusEnum> mapState = new HashMap<Short, OrderStatusEnum>();
		for (OrderStatusEnum tt : OrderStatusEnum.values()) {
			mapState.put(Short.parseShort(tt.toString()), tt);
		}
		Short supplyOrderType = Short.parseShort(String.valueOf(ss.getOrderType()));
		if (order.getTypeState() == Short.parseShort(OrderTypeEnum.BookHeadOrder.toString()) || order.getTypeState() == Short.parseShort(OrderTypeEnum.BookTailOrder.toString()) || order.getTypeState() == Short.parseShort(OrderTypeEnum.BookFullOrder.toString())
				|| order.getTypeState() == Short.parseShort(OrderTypeEnum.PuhuoFull.toString()) || order.getTypeState() == Short.parseShort(OrderTypeEnum.PuhuoHeader.toString()) || order.getTypeState() == Short.parseShort(OrderTypeEnum.PuhuoTail.toString())) {
			supplyOrderType = order.getTypeState();
		}
		Date nowtime = new Date();
		List<OProductOrder> orderInfoList = ordersDao.find_ProductOrderBySupplyOrderId(ss.getSupplierOrderId()); // ArrayList<OProductOrder>();
		BOrderTypePower power = new BOrderTypePower(mapType.get(supplyOrderType), mapState.get(ss.getState()));
		// 修改供应商订单详情状态
		ss.setState(Short.parseShort(power.status.toString()));
		ss.setPayTime(nowtime);
		baseDAO.update(ss);
		List<BSendSMSVO> sendList = new ArrayList<BSendSMSVO>();
		// 是否抽成 工厂号供应商不抽成
		boolean isCut = true;
		UUserAssist userAss = baseDAO.get(UUserAssist.class, ss.getSupplyerId());// 工厂号不分配抽成
		if (userAss != null && userAss.getIdentity() != null && BitOperation.isIdentity(userAss.getIdentity(), UserIdentityType.yunSupplier)) {
			isCut = false;
		} else {
			USupplyer supplyer = baseDAO.get(USupplyer.class, ss.getSupplyerId());
			if (supplyer != null && supplyer.getType() != null && BitOperation.isIdentity(supplyer.getType(), UserIdentityType.yunSupplier)) {
				isCut = false;
			}
		}
		// 交易额 货款部分 + 邮费部分
		double totalAmount = ParseHelper.getDoubleDefValue(ss.getTotalPrice()) + ParseHelper.getDoubleDefValue(ss.getPostage());
		
		//增加全返区活动，分配现金券给购买用户 每人最多5000
		if(ss.getIsActivity()!=null && ss.getIsActivity().shortValue()==1 &&(order.getTypeState()== Short.parseShort(OrderTypeEnum.Pt.toString())||order.getTypeState()==Short.parseShort(OrderTypeEnum.BatchOrder.toString()))) //活动带来的订单
		{
			//判断是否在活动区里面。
			Long l=0L;
			if(ss.getActivityId()!=null)
				l=ss.getActivityId();
			AActivity aa= baseDAO.getUniqueResultByHql(" from AActivity a where a.type=?", 1);
			if(aa!=null)
			{
				if(aa.getActId().longValue()==l.longValue())//属于购物全返区里面的订单
				{
					UWallet wal=baseDAO.get(UWallet.class, ss.getBuyerID());
					if(wal==null)
					{
						wal=new UWallet();
						wal.setCreateTime(new Date());
						wal.setWeiId(ss.getBuyerID());
						wal.setWeiDianCoin(0.0);
						wal.setBalance(0.0);
						wal.setAccountIng(0.0);
						wal.setAccountNot(0.0);
						wal.setAbleTicket(0.0);
						wal.setUnAbleTicket(0.0);
						wal.setLimitTicket(0.0);
					}
					double ticket=wal.getLimitTicket()==null?0.0:wal.getLimitTicket();
					if(ticket<5000.0)
					{						
						Double backprice=0.0;
						if(ticket+ss.getTotalPrice().longValue()>=5000.0)
						{
							backprice=5000.0-ticket;
						}
						else
						{
							backprice=ss.getTotalPrice();
						}
						UTradingDetails back = new UTradingDetails();
						back.setAmount(backprice); //去掉邮费
						back.setCreateTime(nowtime);
						back.setLtwoType((short) 0);// Short.parseShort(HuoKuanType.Product.toString())
						back.setOrderNo(ss.getPayOrderId());
						back.setSupplyOrder(ss.getSupplierOrderId());
						back.setState(Short.parseShort(UserAmountStatus.weiFangKuan.toString()));
						back.setType(Short.parseShort(UserAmountType.ticket.toString()));
						back.setWeiId(ss.getBuyerID());	
						back.setSurplusAmout(backprice);
						baseDAO.save(back);
						// 更新钱包
						
						wal.setUnAbleTicket((wal.getUnAbleTicket()==null?0.0:wal.getUnAbleTicket())+backprice);
						wal.setLimitTicket((wal.getLimitTicket()==null?0.0:wal.getLimitTicket())+backprice);
						baseDAO.saveOrUpdate(wal);
					}
					
				}
			}
			
		}
		
		// a 是否分配货款
		if (power.isAllocateHuoKuan) {		
			
			UTradingDetails supplyAmountD = new UTradingDetails();
			supplyAmountD.setAmount(totalAmount);
			supplyAmountD.setCreateTime(nowtime);
			supplyAmountD.setLtwoType((short) 0);// Short.parseShort(HuoKuanType.Product.toString())
			supplyAmountD.setOrderNo(ss.getPayOrderId());
			supplyAmountD.setSupplyOrder(ss.getSupplierOrderId());
			supplyAmountD.setState(Short.parseShort(UserAmountStatus.weiFangKuan.toString()));
			supplyAmountD.setType(Short.parseShort(UserAmountType.supplyPrice.toString()));
			supplyAmountD.setWeiId(ss.getSupplyerId());
			if (power.isDeductComminss) {
				supplyAmountD.setAmount(supplyAmountD.getAmount() - ParseHelper.getDoubleDefValue(ss.getCommision()));
			}
			if (power.isDeductCut && isCut) {
				supplyAmountD.setAmount(supplyAmountD.getAmount() * BAmountSettings.batchHuoKuan);
			}
			supplyAmountD.setSurplusAmout(supplyAmountD.getAmount());
			baseDAO.save(supplyAmountD);
			// 钱包改版
			UP_redis_UserWallet(order.getPayOrderId(), ss.getSupplyerId(), 0, supplyAmountD.getAmount(), 1);
			// 插入待发 短信
			String mobile_ss = ordersDao.getUserMobile(ss.getSupplyerId());
			if (ParseHelper.isMobile(mobile_ss)) {
				String[] param = new String[] { mobile_ss, String.format("%.2f", supplyAmountD.getAmount()) };
				BSendSMSVO ssvo = new BSendSMSVO();
				ssvo.setParam(param);
				ssvo.setWeiid(ss.getSupplyerId());
				ssvo.setVerify(VerifyCodeType.SupplyDeliver);
				sendList.add(ssvo);
			}
			if(ss.getOrderType().intValue() == Integer.parseInt(OrderTypeEnum.HalfTaste.toString()))
			{
				//回写首次半价
				TTasteSummer tt=baseDAO.getUniqueResultByHql(" from TTasteSummer t where t.weiId=? and t.tasteType=?", ss.getBuyerID(),(short)2);
				if(tt==null)
				{
					tt=new TTasteSummer();
					tt.setTasteType((short) 2);
					tt.setWeiId(ss.getBuyerID());
					baseDAO.save(tt);
				}
			}
		}
		// b.是否分配抽成 TODO 改版（2015-7-3 新加市场管理员提成情况）
		if (power.isAllocateCut && isCut) {
			// 获取供应商信息
			UBatchSupplyer supplyer = baseDAO.get(UBatchSupplyer.class, ss.getSupplyerId());
			long marketWeiid = 111;// /市场管理员
			long marketVerWeiid = 111;// 市场管理员的认证员
			if (supplyer != null) {
				int bmid = supplyer.getBmid() == null ? 0 : supplyer.getBmid();
				if (bmid > 0) {
					TBatchMarket market = baseDAO.get(TBatchMarket.class, bmid);
					if (market != null && market.getIsAllIn() != null && market.getIsAllIn() > 0) {// 判断市场是否整体入驻
						if (market.getMarketWeiId() != null && market.getMarketWeiId() > 0) {
							marketWeiid = market.getMarketWeiId();
						}
						if (market.getMarketVerWeiId() != null && market.getMarketVerWeiId() > 0) {
							marketVerWeiid = market.getMarketVerWeiId();
						}
					}
				}
			}
			// 市场管理员 提成 1%
			UTradingDetails market = new UTradingDetails();
			market.setAmount(totalAmount * BAmountSettings.batchMarketWei);
			market.setCreateTime(nowtime);
			market.setLtwoType(Short.parseShort(RzyjType.BookCut.toString()));
			market.setOrderNo(ss.getPayOrderId());
			market.setSupplyOrder(ss.getSupplierOrderId());
			market.setState(Short.parseShort(UserAmountStatus.weiFangKuan.toString()));
			market.setType(Short.parseShort(UserAmountType.rzYj.toString()));
			market.setWeiId(marketWeiid);
			market.setSurplusAmout(market.getAmount());
			baseDAO.save(market);
			// TODO 钱包改版
			UP_redis_UserWallet(order.getPayOrderId(), market.getWeiId(), 0, market.getAmount(), 1);
			// }
			// 市场牵线人 提成 0.2%
			UTradingDetails marketver = new UTradingDetails();
			marketver.setAmount(totalAmount * BAmountSettings.batchMarketVer);
			marketver.setCreateTime(nowtime);
			marketver.setLtwoType(Short.parseShort(RzyjType.BookCut.toString()));
			marketver.setOrderNo(ss.getPayOrderId());
			marketver.setSupplyOrder(ss.getSupplierOrderId());
			marketver.setState(Short.parseShort(UserAmountStatus.weiFangKuan.toString()));
			marketver.setType(Short.parseShort(UserAmountType.rzYj.toString()));
			marketver.setWeiId(marketVerWeiid);
			marketver.setSurplusAmout(marketver.getAmount());
			baseDAO.save(marketver);
			// TODO 钱包改版
			UP_redis_UserWallet(order.getPayOrderId(), marketver.getWeiId(), 0, marketver.getAmount(), 1);

			// 认证点抽成
			UTradingDetails vPort = new UTradingDetails();
			vPort.setAmount(totalAmount * BAmountSettings.batchVerifiPort);
			vPort.setCreateTime(nowtime);
			vPort.setLtwoType(Short.parseShort(RzyjType.BookCut.toString()));
			vPort.setOrderNo(ss.getPayOrderId());
			vPort.setSupplyOrder(ss.getSupplierOrderId());
			vPort.setState(Short.parseShort(UserAmountStatus.weiFangKuan.toString()));
			vPort.setType(Short.parseShort(UserAmountType.rzYj.toString()));
			vPort.setWeiId((long) 111);// verifierPortID
			vPort.setFromWeiID((long) 111);
			if (supplyer != null) {
				if (supplyer.getPortVerifer() != null && supplyer.getPortVerifer() > 0) {
					vPort.setWeiId(supplyer.getPortVerifer());
				}
				if (supplyer.getVerifier() != null) {
					vPort.setFromWeiID(supplyer.getVerifier());
				}
			}

			vPort.setSurplusAmout(vPort.getAmount());
			baseDAO.save(vPort);
			UP_redis_UserWallet(order.getPayOrderId(), vPort.getWeiId(), 0, vPort.getAmount(), 1);

			// 代理商微店号（默认为 微店网收取0.002的抽成）
			UTradingDetails vUser = new UTradingDetails();
			vUser.setAmount(totalAmount * BAmountSettings.batchVerifier);
			vUser.setCreateTime(nowtime);
			vUser.setLtwoType(Short.parseShort(RzyjType.BookCut.toString()));
			vUser.setOrderNo(ss.getPayOrderId());
			vUser.setSupplyOrder(ss.getSupplierOrderId());
			vUser.setState(Short.parseShort(UserAmountStatus.weiFangKuan.toString()));
			vUser.setType(Short.parseShort(UserAmountType.rzYj.toString()));

			vUser.setWeiId((long) 111);// verifierID
			if (supplyer != null && supplyer.getCompanyWeiId() != null && supplyer.getCompanyWeiId() > 0) {
				vUser.setWeiId(supplyer.getCompanyWeiId());
			}
			vUser.setSurplusAmout(vUser.getAmount());
			baseDAO.save(vUser);
			// TODO 钱包改版
			UP_redis_UserWallet(order.getPayOrderId(), vUser.getWeiId(), 0, vUser.getAmount(), 1);

			// 供应商抽成 代理商抽成
			UTradingDetails uBoss = new UTradingDetails();
			uBoss.setAmount(totalAmount * BAmountSettings.batchVerifierAgent);
			uBoss.setCreateTime(nowtime);
			uBoss.setLtwoType(Short.parseShort(RzyjType.BookCut.toString()));
			uBoss.setOrderNo(ss.getPayOrderId());
			uBoss.setSupplyOrder(ss.getSupplierOrderId());
			uBoss.setState(Short.parseShort(UserAmountStatus.weiFangKuan.toString()));
			uBoss.setType(Short.parseShort(UserAmountType.rzYj.toString()));
			uBoss.setWeiId((long) 111);// proxyWeiid
			if (supplyer != null && supplyer.getAgentSupplier() != null && supplyer.getAgentSupplier() > 0) {
				uBoss.setWeiId(supplyer.getAgentSupplier());
			}
			uBoss.setSurplusAmout(uBoss.getAmount());
			baseDAO.save(uBoss);
			// TODO 钱包改版
			UP_redis_UserWallet(order.getPayOrderId(), uBoss.getWeiId(), 0, uBoss.getAmount(), 1);

			// 自己公司（微店网）的抽成
			UTradingDetails weiSystem = new UTradingDetails();
			weiSystem.setAmount(totalAmount * BAmountSettings.batchOkWei);
			weiSystem.setCreateTime(nowtime);
			weiSystem.setLtwoType(Short.parseShort(RzyjType.BookCut.toString()));
			weiSystem.setOrderNo(ss.getPayOrderId());
			weiSystem.setSupplyOrder(ss.getSupplierOrderId());
			weiSystem.setState(Short.parseShort(UserAmountStatus.weiFangKuan.toString()));
			weiSystem.setType(Short.parseShort(UserAmountType.rzYj.toString()));
			weiSystem.setWeiId((long) 111);
			weiSystem.setSurplusAmout(weiSystem.getAmount());
			baseDAO.save(weiSystem);
			// 钱包改版
			UP_redis_UserWallet(order.getPayOrderId(), weiSystem.getWeiId(), 0, weiSystem.getAmount(), 1);
		}

		// 3. ----- 货款部分处理 按单个商品进行结算佣金 货款------
		List<OProductOrder> supplyOIList = new ArrayList<OProductOrder>();
		if (orderInfoList != null && orderInfoList.size() > 0) {
			for (OProductOrder oo : orderInfoList) {
				if (ss.getSupplierOrderId().equals(oo.getSupplierOrderId()))
					supplyOIList.add(oo);
			}
		}

		if (supplyOIList != null && supplyOIList.size() > 0) {
			// 遍历该供应商的每一件商品进行分配
			for (OProductOrder product : supplyOIList) {
				// 如果是赠品 不分配佣金货款 抽成
				if (product.getOrderType() == 3) {
					continue;
				}
				PProducts pro = baseDAO.get(PProducts.class, product.getProductId());
				// 代理商、落地店 库存处理
				if (ss.getOrderType().equals(Short.parseShort(OrderTypeEnum.RetailPTH.toString())) || ss.getOrderType().equals(Short.parseShort(OrderTypeEnum.Jinhuo.toString())) || ss.getOrderType().equals(Short.parseShort(OrderTypeEnum.Puhuo.toString()))) {
					if (pro != null) {
						if (!pro.getSupplierWeiId().equals(ss.getSupplyerId())) {
							String hql_stock = " from PAgentStock p where p.weiId=? and p.productId=?";
							PAgentStock stock = baseDAO.getNotUniqueResultByHql(hql_stock, ss.getSupplyerId(), product.getProductId());
							if (stock != null) {
								if (stock.getStockCount() != null && stock.getStockCount() > product.getCount().intValue()) {
									stock.setStockCount(stock.getStockCount() - product.getCount());
									baseDAO.update(stock);
								} else {
									stock.setStockCount(0);
									baseDAO.update(stock);
								}
							} else {
								stock = new PAgentStock();
								stock.setWeiId(ss.getSupplyerId());
								stock.setProductId(product.getProductId());
								baseDAO.save(stock);
							}
						}
					}
				} else {// 普通购买的库存处理
					if (pro != null && pro.getCount() != null && pro.getCount().intValue() > product.getCount().intValue()) {
						pro.setCount(pro.getCount() - product.getCount());
						baseDAO.update(pro);
					} else if (pro != null) {
						pro.setCount(0);
						baseDAO.update(pro);
					}
				}
				// 如果是活动商品，回写活动数据 add by tan 2016-05-17
				if (product.getIsActivity() == 1) {
					AActProductExp ae = baseDAO.get(AActProductExp.class, product.getProActId());
					if (ae != null) {
						try {
							baseDAO.executeHql(" update AActProductExp a set a.sellerCount=a.sellerCount+?,a.sellerAmount=a.sellerAmount+? where  a.proActId=?", product.getCount(), product.getCount() * product.getPrice(), product.getProActId());
						} catch (Exception ex) {

						}
					} else {
						ae = new AActProductExp();
						ae.setProActId(product.getProActId());
						ae.setProductId(product.getProductId());
						ae.setSellerAmount(product.getCount() * product.getPrice());
						ae.setSellerCount(product.getCount());
						baseDAO.save(ae);
					}
				}

				// 是否分配佣金
				// 佣金分配需要改动 制作人20%分享人50% 上级30%
				if (power.isAllocateComminss) {
					// 所有可分配佣金
					double totalComminss = ParseHelper.getDoubleDefValue(product.getCommision()) * product.getCount();
					if (power.isDeductCut) {
						totalComminss = totalComminss * BAmountSettings.batchHuoKuan;
					}
					// 改动佣金分配规则 modify by tan 20160121
					Long maker = product.getMakerWeiId();
					Long sharerup = product.getSharerUpWeiId();
					Long sharer = product.getShareWeiId();
					// 如何获取不到分配的信息，佣金归微店网
					if (maker == null) {
						maker = 111L;
					}
					if (sharerup == null) {
						sharerup = 111L;
					}
					if (sharer == null) {
						sharer = 111L;
					}
					// 制作人20%
					UTradingDetails uMaker = new UTradingDetails();
					double makerpercent = ParseHelper.toDouble(AppSettingUtil.getSingleValue("Maker"));
					uMaker.setAmount(totalComminss * makerpercent);
					uMaker.setCreateTime(nowtime);
					uMaker.setLtwoType((short) 0);
					uMaker.setOrderNo(ss.getPayOrderId());
					uMaker.setSupplyOrder(product.getSupplierOrderId());
					uMaker.setProductOrder(product.getProductOrderId());
					uMaker.setState(Short.parseShort(UserAmountStatus.weiFangKuan.toString()));
					uMaker.setType(Short.parseShort(UserAmountType.orderYj.toString()));
					uMaker.setWeiId(maker);
					uMaker.setSurplusAmout(uMaker.getAmount());
					baseDAO.save(uMaker);
					// TODO 钱包改版
					UP_redis_UserWallet(order.getPayOrderId(), uMaker.getWeiId(), 0, uMaker.getAmount(), 1);
					// 分享人上级 30%
					UTradingDetails uSharerUp = new UTradingDetails();
					double shareruppercent = ParseHelper.toDouble(AppSettingUtil.getSingleValue("SharerUp"));
					uSharerUp.setAmount(totalComminss * shareruppercent);
					uSharerUp.setCreateTime(nowtime);
					uSharerUp.setLtwoType((short) 0);
					uSharerUp.setOrderNo(ss.getPayOrderId());
					uSharerUp.setSupplyOrder(product.getSupplierOrderId());
					uSharerUp.setProductOrder(product.getProductOrderId());
					uSharerUp.setState(Short.parseShort(UserAmountStatus.weiFangKuan.toString()));
					uSharerUp.setType(Short.parseShort(UserAmountType.orderYj.toString()));
					uSharerUp.setWeiId(sharerup);
					uSharerUp.setSurplusAmout(uSharerUp.getAmount());
					baseDAO.save(uSharerUp);
					// TODO 钱包改版uSharerUp
					UP_redis_UserWallet(order.getPayOrderId(), uSharerUp.getWeiId(), 0, uSharerUp.getAmount(), 1);

					// 分享人50%
					UTradingDetails uSharer = new UTradingDetails();
					double sharerpercent = ParseHelper.toDouble(AppSettingUtil.getSingleValue("Sharer"));
					uSharer.setAmount(totalComminss * sharerpercent);
					uSharer.setCreateTime(nowtime);
					uSharer.setLtwoType((short) 0);
					uSharer.setOrderNo(ss.getPayOrderId());
					uSharer.setSupplyOrder(product.getSupplierOrderId());
					uSharer.setProductOrder(product.getProductOrderId());
					uSharer.setState(Short.parseShort(UserAmountStatus.weiFangKuan.toString()));
					uSharer.setType(Short.parseShort(UserAmountType.orderYj.toString()));
					uSharer.setWeiId(sharer);
					uSharer.setSurplusAmout(uSharer.getAmount());
					baseDAO.save(uSharer);
					// TODO 钱包改版
					UP_redis_UserWallet(order.getPayOrderId(), uSharer.getWeiId(), 0, uSharer.getAmount(), 1);
				}
			}
		}
		// 如果是进货单，判断是否是首次进货（满 多少 成为落地店）
		if (supplyOrderType == Short.parseShort(OrderTypeEnum.Jinhuo.toString())) {
			saveProductShop(order.getWeiId(), ss.getSupplierOrderId());
		}
		
		// 消息推送给供应商
		insertPushMsg(ss.getSupplyerId(), 32, ss.getSupplierOrderId(), "销售订单：您有一笔订单已支付，请尽快发货!");

		// 短信通知卖家 支付成功！
		String buyerMobile = ordersDao.getUserMobile(order.getWeiId());
		if (ParseHelper.isMobile(buyerMobile)) {
			String[] param = new String[] { buyerMobile, order.getPayOrderId() };
			BSendSMSVO ssvo = new BSendSMSVO();
			ssvo.setParam(param);
			ssvo.setWeiid(order.getWeiId());
			ssvo.setVerify(VerifyCodeType.SendWeiDeliver);
			sendList.add(ssvo);
		}
		
		
		// 批量处理发送短信
		if (sendList != null && sendList.size() > 0) {
			SendSMSByMobile smsUtil = new SendSMSByMobile();
			for (BSendSMSVO vo : sendList) {
				smsUtil.sendSMS(vo.getParam(), vo.getVerify(), vo.getWeiid());
			}
		}
	}

	/**
	 * 分销区订单处理
	 * 
	 * @param order
	 * @param ss
	 * @throws Exception
	 */
//	public void editRetailAgentProcessing(OPayOrder order, OSupplyerOrder ss) throws Exception {
//		if (ss.getState() != null && ss.getState() == Short.parseShort(OrderStatusEnum.Payed.toString())) {
//			throw new RuntimeException("订单已支付");
//		}
//		Date nowtime = new Date();
//		// 订单产品列表
//		List<OProductOrder> orderInfoList = ordersDao.find_ProductOrderBySupplyOrderId(ss.getSupplierOrderId());
//		if (orderInfoList == null || orderInfoList.size() <= 0) {
//			throw new RuntimeException("订单产品有误");
//		}
//		DBrandSupplier supplier = baseDAO.get(DBrandSupplier.class, ss.getSupplyerId());
//		if (supplier == null) {
//			//UUserAssist supAssist=baseDAO.get(UUserAssist.class, ss.getSupplyerId());
//			supplier=new DBrandSupplier();
//			supplier.setBrandId(0);
////			throw new RuntimeException("订单信息有误（卖家并非品牌供应商）");
//		}
//		OOrderAddr address = baseDAO.get(OOrderAddr.class, ss.getAddrId());
//		if (address == null) {
//			throw new RuntimeException("收货地址不能为空");
//		}
//		List<BSendSMSVO> sendList = new ArrayList<BSendSMSVO>();
//		double supplyPriceTotal = 0d;
//		
//		
//		//如果是全返活动区的结算
//		if(ss.getIsActivity()!=null && ss.getIsActivity().shortValue()==1)
//		{
//			
//			double totalAmount=ParseHelper.getDoubleDefValue(ss.getPostage());
//			//double totalAmount = ParseHelper.getDoubleDefValue(ss.getTotalPrice()) + ParseHelper.getDoubleDefValue(ss.getPostage());
//			List<AActivityProducts> listP=baseDAO.find(" from AActivityProducts p where p.actId=? and p.productId in (select o.productId from OProductOrder o where o.supplierOrderId=?) ", ss.getActivityId(),ss.getSupplierOrderId());
//			if(listP!=null && listP.size()>0)
//			{
//				for(AActivityProducts a : listP)
//				{
//					for(OProductOrder oo :orderInfoList) 
//					{	
//						if(oo.getProductId().longValue()==a.getProductId().longValue())
//						{
//							totalAmount+=(a.getTraddingPrice()==null?0.0:a.getTraddingPrice()*oo.getCount());
//						}
//					}
//				}
//			}
//			//增加全返区活动，分配现金券给购买用户 每人最多5000
//			if(ss.getIsActivity()!=null && ss.getIsActivity().shortValue()==1) //活动带来的订单
//			{
//				//判断是否在活动区里面。
//				Long l=0L;
//				if(ss.getActivityId()!=null)
//					l=ss.getActivityId();
//				AActivity aa= baseDAO.getUniqueResultByHql(" from AActivity a where a.type=?", 1);
//				if(aa!=null)
//				{
//					if(aa.getActId().longValue()==l.longValue())//属于购物全返区里面的订单
//					{
//						UWallet wal=baseDAO.get(UWallet.class, ss.getBuyerID());
//						if(wal==null)
//						{
//							wal=new UWallet();
//							wal.setCreateTime(new Date());
//							wal.setWeiId(ss.getBuyerID());
//							wal.setWeiDianCoin(0.0);
//							wal.setBalance(0.0);
//							wal.setAccountIng(0.0);
//							wal.setAccountNot(0.0);
//							wal.setAbleTicket(0.0);
//							wal.setUnAbleTicket(0.0);
//							wal.setLimitTicket(0.0);
//						}
//						double ticket=wal.getLimitTicket()==null?0.0:wal.getLimitTicket();
//						if(ticket<5000.0)
//						{						
//							Double backprice=0.0;
//							if(ticket+ss.getTotalPrice().longValue()>=5000.0)
//							{
//								backprice=5000.0-ticket;
//							}
//							else
//							{
//								backprice=ss.getTotalPrice();
//							}
//							UTradingDetails back = new UTradingDetails();
//							back.setAmount(backprice); //去掉邮费
//							back.setCreateTime(nowtime);
//							back.setLtwoType((short) 0);// Short.parseShort(HuoKuanType.Product.toString())
//							back.setOrderNo(ss.getPayOrderId());
//							back.setSupplyOrder(ss.getSupplierOrderId());
//							back.setState(Short.parseShort(UserAmountStatus.weiFangKuan.toString()));
//							back.setType(Short.parseShort(UserAmountType.ticket.toString()));
//							back.setWeiId(ss.getBuyerID());	
//							back.setSurplusAmout(backprice);
//							baseDAO.save(back);
//							// 更新钱包
//							
//							wal.setUnAbleTicket((wal.getUnAbleTicket()==null?0.0:wal.getUnAbleTicket())+backprice);
//							wal.setLimitTicket((wal.getLimitTicket()==null?0.0:wal.getLimitTicket())+backprice);
//							baseDAO.saveOrUpdate(wal);
//						}
//						
//					}
//				}
//				
//			}
//			// a 是否分配货款	
//			UTradingDetails supplyAmountD = new UTradingDetails();
//			supplyAmountD.setAmount(totalAmount);
//			supplyAmountD.setCreateTime(nowtime);
//			supplyAmountD.setLtwoType((short) 0);// Short.parseShort(HuoKuanType.Product.toString())
//			supplyAmountD.setOrderNo(ss.getPayOrderId());
//			supplyAmountD.setSupplyOrder(ss.getSupplierOrderId());
//			supplyAmountD.setState(Short.parseShort(UserAmountStatus.weiFangKuan.toString()));
//			supplyAmountD.setType(Short.parseShort(UserAmountType.supplyPrice.toString()));
//			supplyAmountD.setWeiId(ss.getSupplyerId());			
//			supplyAmountD.setSurplusAmout(supplyAmountD.getAmount());
//			baseDAO.save(supplyAmountD);
//			// 钱包改版
//			UP_redis_UserWallet(order.getPayOrderId(), ss.getSupplyerId(), 0, supplyAmountD.getAmount(), 1);
//			
//			
//			for (OProductOrder op : orderInfoList) {
//				PProductStyles styles = baseDAO.get(PProductStyles.class, op.getStyleId());
//				if (styles.getSupplyPrice() == null || styles.getDukePrice() == null) {
//					throw new RuntimeException("代理商信息有误，缺少相应的价格属性");
//				}
//				// 分销者成本价---------===================================
//				double shareBaseAmount = styles.getPrice();
//				double priceOkwei= op.getPrice()*0.8- styles.getSupplyPrice();//零售价与供货价差价
//				DCastellans shareUser = agentDao.get_DCastellan(supplier.getBrandId(), op.getShareWeiId());// 分享人是城主
//				DCastellans castellan = agentDao.get_DCastellan(supplier.getBrandId(), address.getProvince(), address.getCaddrId(), address.getDistrict());
//				if (shareUser != null) {
//					if (shareUser.getPorN() == ParseHelper.toInt(CastellanType.castellan.toString())) {
//						shareBaseAmount = styles.getDukePrice();// 城主价
//					} else {
//						shareBaseAmount = styles.getDeputyPrice();// 副城主价
//					}
//				} else {
//					DAgentInfo agentInfo = agentDao.getDAgentInfo(op.getShareWeiId(), supplier.getBrandId());
//					if (agentInfo != null) {
//						shareBaseAmount = styles.getAgentPrice();// 代理商价
//					}
//				}
//				// 分销者差价（零售价与 分享人的身份（代理商、城主）相应价格的差价）
//				double amount = op.getPrice() - shareBaseAmount;
//				if (amount > 0) {// 代理商（城主/副城主）获取差价----=================================
//					priceOkwei=priceOkwei-amount;
//					//插入佣金记录
//					saveUTradingDetails(order.getPayOrderId(),ss.getSupplierOrderId(),amount,op.getShareWeiId(),Short.parseShort(UserAmountStatus.weiFangKuan.toString()),Short.parseShort(UserAmountType.orderYj.toString()),(short)0);	
//					UP_redis_UserWallet(order.getPayOrderId(), op.getShareWeiId(), 0,amount, 1);
//				}
//				if (castellan != null && !castellan.getWeiId().equals(op.getShareWeiId())) {
//					// 城主获取分销差价==================================================================
//					double amountCallen = shareBaseAmount - styles.getDukePrice();// 代理价 跟城主价的差价（分给城主）
//					if(amountCallen>0){
//						priceOkwei=priceOkwei-amountCallen;
//						//插入佣金记录
//						saveUTradingDetails(order.getPayOrderId(),ss.getSupplierOrderId(),amountCallen,castellan.getWeiId(),Short.parseShort(UserAmountStatus.weiFangKuan.toString()),Short.parseShort(UserAmountType.orderYj.toString()),(short)0);	
//						UP_redis_UserWallet(order.getPayOrderId(), castellan.getWeiId(), 0,amountCallen, 1);
//					}
//				}
//				if(priceOkwei>0){//平台获取差价
//					//插入佣金记录
//					saveUTradingDetails(order.getPayOrderId(),ss.getSupplierOrderId(),priceOkwei,111l,Short.parseShort(UserAmountStatus.weiFangKuan.toString()),Short.parseShort(UserAmountType.orderYj.toString()),(short)0);	
//					UP_redis_UserWallet(order.getPayOrderId(), 111l, 0,priceOkwei, 1);
//				}
//			}
//
//		}
//		else
//		{
//			for (OProductOrder op : orderInfoList) {
//				PProductStyles styles = baseDAO.get(PProductStyles.class, op.getStyleId());
//				if (styles.getSupplyPrice() == null || styles.getDukePrice() == null) {
//					throw new RuntimeException("代理商信息有误，缺少相应的价格属性");
//				}
//				supplyPriceTotal += styles.getSupplyPrice()*op.getCount();//供应商货款
//				// 分销者成本价---------===================================
//				double shareBaseAmount = styles.getPrice();
//				double priceOkwei= op.getPrice()- styles.getSupplyPrice();//零售价与供货价差价
//				DCastellans shareUser = agentDao.get_DCastellan(supplier.getBrandId(), op.getShareWeiId());// 分享人是城主
//				DCastellans castellan = agentDao.get_DCastellan(supplier.getBrandId(), address.getProvince(), address.getCaddrId(), address.getDistrict());
//				if (shareUser != null) {
//					if (shareUser.getPorN() == ParseHelper.toInt(CastellanType.castellan.toString())) {
//						shareBaseAmount = styles.getDukePrice();// 城主价
//					} else {
//						shareBaseAmount = styles.getDeputyPrice();// 副城主价
//					}
//				} else {
//					DAgentInfo agentInfo = agentDao.getDAgentInfo(op.getShareWeiId(), supplier.getBrandId());
//					if (agentInfo != null) {
//						shareBaseAmount = styles.getAgentPrice();// 代理商价
//					}
//				}
//				// 分销者差价（零售价与 分享人的身份（代理商、城主）相应价格的差价）
//				double amount = op.getPrice() - shareBaseAmount;
//				if (amount > 0) {// 代理商（城主/副城主）获取差价----=================================
//					priceOkwei=priceOkwei-amount;
//					//插入佣金记录
//					saveUTradingDetails(order.getPayOrderId(),ss.getSupplierOrderId(),amount,op.getShareWeiId(),Short.parseShort(UserAmountStatus.weiFangKuan.toString()),Short.parseShort(UserAmountType.orderYj.toString()),(short)0);	
//					UP_redis_UserWallet(order.getPayOrderId(), op.getShareWeiId(), 0,amount, 1);
//				}
//				if (castellan != null && !castellan.getWeiId().equals(op.getShareWeiId())) {
//					// 城主获取分销差价==================================================================
//					double amountCallen = shareBaseAmount - styles.getDukePrice();// 代理价 跟城主价的差价（分给城主）
//					if(amountCallen>0){
//						priceOkwei=priceOkwei-amountCallen;
//						//插入佣金记录
//						saveUTradingDetails(order.getPayOrderId(),ss.getSupplierOrderId(),amountCallen,castellan.getWeiId(),Short.parseShort(UserAmountStatus.weiFangKuan.toString()),Short.parseShort(UserAmountType.orderYj.toString()),(short)0);	
//						UP_redis_UserWallet(order.getPayOrderId(), castellan.getWeiId(), 0,amountCallen, 1);
//					}
//				}
//				if(priceOkwei>0){//平台获取差价
//					//插入佣金记录
//					saveUTradingDetails(order.getPayOrderId(),ss.getSupplierOrderId(),priceOkwei,111l,Short.parseShort(UserAmountStatus.weiFangKuan.toString()),Short.parseShort(UserAmountType.orderYj.toString()),(short)0);	
//					UP_redis_UserWallet(order.getPayOrderId(), 111l, 0,priceOkwei, 1);
//				}
//			}
//			supplyPriceTotal+=(ss.getPostage()==null?0d:ss.getPostage());//供应商拿到货款跟邮费
//			// 供应商获取货款===========================================================================
//			saveUTradingDetails(order.getPayOrderId(),ss.getSupplierOrderId(),supplyPriceTotal,ss.getSupplyerId(),Short.parseShort(UserAmountStatus.weiFangKuan.toString()),Short.parseShort(UserAmountType.supplyPrice.toString()),(short)0);
//			UP_redis_UserWallet(order.getPayOrderId(), ss.getSupplyerId(), 0, supplyPriceTotal, 1);
//
//		}
//		//更新供应商订单状态
//		ss.setState(Short.parseShort(OrderStatusEnum.Payed.toString()));
//		ss.setPayTime(nowtime);
//		baseDAO.update(ss);
//		// 消息推送给供应商
//		insertPushMsg(ss.getSupplyerId(), 32, ss.getSupplierOrderId(), "销售订单：您有一笔订单已支付，请尽快发货!");
//		// 短信通知卖家 支付成功！
//		String buyerMobile = ordersDao.getUserMobile(order.getWeiId());
//		if (ParseHelper.isMobile(buyerMobile)) {
//			String[] param = new String[] { buyerMobile, order.getPayOrderId() };
//			BSendSMSVO ssvo = new BSendSMSVO();
//			ssvo.setParam(param);
//			ssvo.setWeiid(order.getWeiId());
//			ssvo.setVerify(VerifyCodeType.SendWeiDeliver);
//			sendList.add(ssvo);
//		}
//		// 批量处理发送短信
//		if (sendList != null && sendList.size() > 0) {
//			SendSMSByMobile smsUtil = new SendSMSByMobile();
//			for (BSendSMSVO vo : sendList) {
//				smsUtil.sendSMS(vo.getParam(), vo.getVerify(), vo.getWeiid());
//			}
//		}
//	}
	
	/**
	 * 分销区订单处理
	 * 
	 * @param order
	 * @param ss
	 * @throws Exception
	 */
	public void editRetailAgentProcessing(OPayOrder order, OSupplyerOrder ss) throws Exception {
		if (ss.getState() != null && ss.getState() == Short.parseShort(OrderStatusEnum.Payed.toString())) {
			throw new RuntimeException("订单已支付");
		}
		Date nowtime = new Date();
		// 订单产品列表
		List<OProductOrder> orderInfoList = ordersDao.find_ProductOrderBySupplyOrderId(ss.getSupplierOrderId());
		if (orderInfoList == null || orderInfoList.size() <= 0) {
			throw new RuntimeException("订单产品有误");
		}
		DBrandSupplier supplier = baseDAO.get(DBrandSupplier.class, ss.getSupplyerId());
		if (supplier == null) {
			supplier = new DBrandSupplier();
			supplier.setBrandId(0);
			// throw new RuntimeException("订单信息有误（卖家并非品牌供应商）");
		}
		OOrderAddr address = baseDAO.get(OOrderAddr.class, ss.getAddrId());
		if (address == null) {
			throw new RuntimeException("收货地址不能为空");
		}
		List<BSendSMSVO> sendList = new ArrayList<BSendSMSVO>();
		
		// 如果是全返活动区的结算
		// 增加全返区活动，分配现金券给购买用户 每人最多5000
		if (ss.getIsActivity() != null && ss.getIsActivity().shortValue() == 1) // 活动带来的订单
		{// 分配现金券给购买用户 每人最多5000
			this.updateTicket(ss);
		}
		//供应商要拿到的货款
		double supplyPriceTotal = 0d;
		for (OProductOrder op : orderInfoList) {
			PProductStyles styles = baseDAO.get(PProductStyles.class, op.getStyleId());
			if (styles.getSupplyPrice() == null || styles.getDukePrice() == null) {
				throw new RuntimeException("代理商信息有误，缺少相应的价格属性");
			}
			supplyPriceTotal += styles.getSupplyPrice() * op.getCount();// 供应商货款
			// 分销者（分享人）成本价---------===================================
			double shareBaseAmount = styles.getPrice();
			// 零售价与供货价差价（需要分配的 利润空间）
			double priceOkwei = op.getPrice() - styles.getSupplyPrice();
			// 分享人是否为城主
			DCastellans shareUser = agentDao.get_DCastellan(supplier.getBrandId(), op.getShareWeiId());
			//该购买区域的城主
			DCastellans castellan = agentDao.get_DCastellan(supplier.getBrandId(), address.getProvince(), address.getCaddrId(), address.getDistrict());
			//分享人是否为城主，
			if (shareUser != null) {
				if (shareUser.getPorN() == ParseHelper.toInt(CastellanType.castellan.toString())) {
					shareBaseAmount = styles.getDukePrice();// 城主价
				} else {
					shareBaseAmount = styles.getDeputyPrice();// 副城主价
				}
			} else {
				DAgentInfo agentInfo = agentDao.getDAgentInfo(op.getShareWeiId(), supplier.getBrandId());
				if (agentInfo != null) {
					shareBaseAmount = styles.getAgentPrice();// 代理商价
				}
			}
			// 分销者差价（零售价与 分享人的身份（代理商、城主）相应价格的差价）
			double amount = op.getPrice() - shareBaseAmount;
			// 分销者（分享人）获取差价----=================================
			if (amount > 0) {
				priceOkwei = priceOkwei - amount;
				// 插入佣金记录
				saveUTradingDetails(order.getPayOrderId(), ss.getSupplierOrderId(), amount, op.getShareWeiId(), Short.parseShort(UserAmountStatus.weiFangKuan.toString()), Short.parseShort(UserAmountType.orderYj.toString()), (short) 0);
				UP_redis_UserWallet(order.getPayOrderId(), op.getShareWeiId(), 0, amount, 1);
			}
			//城主拿佣金 如果存在城主，且不是分享人，城主需拿佣金
			if (castellan != null && !castellan.getWeiId().equals(op.getShareWeiId())) {
				// 城主获取分销差价==================================================================
				double amountCallen = shareBaseAmount - styles.getDukePrice();
				if (amountCallen > 0) {
					priceOkwei = priceOkwei - amountCallen;
					// 插入佣金记录
					saveUTradingDetails(order.getPayOrderId(), ss.getSupplierOrderId(), amountCallen, castellan.getWeiId(), Short.parseShort(UserAmountStatus.weiFangKuan.toString()), Short.parseShort(UserAmountType.orderYj.toString()), (short) 0);
					UP_redis_UserWallet(order.getPayOrderId(), castellan.getWeiId(), 0, amountCallen, 1);
				}
			}
			// 微店网 平台获取差价
			if (priceOkwei > 0) {
				// 插入佣金记录
				saveUTradingDetails(order.getPayOrderId(), ss.getSupplierOrderId(), priceOkwei, 111l, Short.parseShort(UserAmountStatus.weiFangKuan.toString()), Short.parseShort(UserAmountType.orderYj.toString()), (short) 0);
				UP_redis_UserWallet(order.getPayOrderId(), 111l, 0, priceOkwei, 1);
			}
		}
		supplyPriceTotal += (ss.getPostage() == null ? 0d : ss.getPostage());// 供应商拿到货款跟邮费
		// 供应商获取货款===========================================================================
		saveUTradingDetails(order.getPayOrderId(), ss.getSupplierOrderId(), supplyPriceTotal, ss.getSupplyerId(), Short.parseShort(UserAmountStatus.weiFangKuan.toString()), Short.parseShort(UserAmountType.supplyPrice.toString()), (short) 0);
		UP_redis_UserWallet(order.getPayOrderId(), ss.getSupplyerId(), 0, supplyPriceTotal, 1);

		// 更新供应商订单状态
		ss.setState(Short.parseShort(OrderStatusEnum.Payed.toString()));
		ss.setPayTime(nowtime);
		baseDAO.update(ss);
		// 消息推送给供应商
		insertPushMsg(ss.getSupplyerId(), 32, ss.getSupplierOrderId(), "销售订单：您有一笔订单已支付，请尽快发货!");
		
		// 短信通知卖家 支付成功！
		String buyerMobile = ordersDao.getUserMobile(order.getWeiId());
		if (ParseHelper.isMobile(buyerMobile)) {
			String[] param = new String[] { buyerMobile, order.getPayOrderId() };
			BSendSMSVO ssvo = new BSendSMSVO();
			ssvo.setParam(param);
			ssvo.setWeiid(order.getWeiId());
			ssvo.setVerify(VerifyCodeType.SendWeiDeliver);
			sendList.add(ssvo);
		}
		// 批量处理发送短信
		if (sendList != null && sendList.size() > 0) {
			SendSMSByMobile smsUtil = new SendSMSByMobile();
			for (BSendSMSVO vo : sendList) {
				smsUtil.sendSMS(vo.getParam(), vo.getVerify(), vo.getWeiid());
			}
		}
	}
	/**
	 * 代理商支付成功，资料生效
	 * @param OPayOrder
	 */
	public void editAgentInfo(OPayOrder order) throws Exception {
		//插入代理商正式资料DAgentInfo,OPayOrder需要增加字段brandId
		DAgentApply da=baseDAO.getUniqueResultByHql(" from DAgentApply d where d.weiId=? and d.brandId=?", order.getWeiId(),Integer.parseInt(String.valueOf(order.getSupplierOrder())));
		if(da == null)
			throw new Exception();
		DBrands dn= baseDAO.get(DBrands.class, da.getBrandId());
		DAgentInfo di= new DAgentInfo();
		di.setBrandId(da.getBrandId());
		di.setCity(da.getCity());
		di.setContactPhone(da.getContactPhone());
		di.setCosts(da.getCosts());
		di.setCreateTime(new Date());
		di.setDistrict(da.getDistrict());
		di.setLevel(da.getLevel());
		di.setName(da.getName());
		di.setProvince(da.getProvince());
		di.setQq(da.getQq());
		di.setStatus(Integer.parseInt(AgentStatus.Ok.toString()));
		di.setSuperWeiid(da.getReferencer());
		di.setType(dn.getType());
		di.setWeiId(da.getWeiId());
		baseDAO.save(di);
		//DAgentInfo agentinfo=null;
		//DAgentApply agentapply=agentDao.getDAgentApply(order.getWeiId(), brandid);
		//更新或插入品牌辅助表DBrandsExt 更新品牌代理数量
		DBrandsExt de=baseDAO.get(DBrandsExt.class, da.getBrandId());
		if(de==null)
		{
			de= new DBrandsExt();
			de.setBrandId(da.getBrandId());
			de.setCreateTime(new Date());
			de.setAgentOneCount(0);
			de.setAgentTwoCount(0);
			de.setAgentThreeCount(0);
		}
		UUserAssist ua= baseDAO.get(UUserAssist.class, order.getWeiId());
		if(ua == null)
		{
			ua= new UUserAssist();
			ua.setWeiId(order.getWeiId());
			ua.setIdentity(1);
		}
		if(da.getLevel().intValue()==1)
		{
			ua.setIdentity(ua.getIdentity()+Integer.parseInt(UserIdentityType.AgentDuke.toString()));
			de.setAgentOneCount(de.getAgentOneCount()+1);
		}
		else if(da.getLevel().intValue()==2)
		{
			ua.setIdentity(ua.getIdentity()+Integer.parseInt(UserIdentityType.AgentDeputyDuke.toString()));
			de.setAgentTwoCount(de.getAgentTwoCount()+1);
		}
		else if(da.getLevel().intValue()==3)
		{
			ua.setIdentity(ua.getIdentity()+Integer.parseInt(UserIdentityType.AgentBrandAgent.toString()));
			de.setAgentThreeCount(de.getAgentThreeCount()+1);
		}
		baseDAO.saveOrUpdate(ua);
		baseDAO.saveOrUpdate(de);
		//插入代理商关系表
		//DAgentRelation agentrelation=null;
		//先更新DAgentRelation表数据
		if(da.getLevel().intValue()==1)//申请一级代理
		{
			baseDAO.executeHql(" update DAgentRelation d set d.weiIdone=? where d.brandId=? and d.weiId in (select n.weiId from DAgentInfo n where n.brandId=? and n.superWeiid=?)", da.getWeiId(),da.getBrandId(),da.getBrandId(),da.getWeiId());
		}
		else if(da.getLevel().intValue()==2)//申请一级代理
		{
			baseDAO.executeHql(" update DAgentRelation d set d.weiIdsec=? where d.brandId=? and d.weiId in (select n.weiId from DAgentInfo n where n.brandId=? and n.superWeiid=?)", da.getWeiId(),da.getBrandId(),da.getBrandId(),da.getWeiId());
		}
		DAgentRelation dr=baseDAO.getUniqueResultByHql(" from DAgentRelation d where d.weiId=? and d.brandId=?",da.getWeiId(),da.getBrandId());
		if(dr == null)
		{
			dr = new DAgentRelation();
			dr.setWeiId(da.getWeiId());
			dr.setMyLevel(da.getLevel());
			dr.setSuperWeiId(da.getReferencer());
			dr.setBrandId(da.getBrandId());
			//获取上级的上级数据
			DAgentRelation drs=baseDAO.getUniqueResultByHql(" from DAgentRelation d where d.weiId=? and d.brandId=?", da.getReferencer(),da.getBrandId());
			if(drs!=null)
			{
				dr.setWeiIdone(drs.getWeiIdone());
				dr.setWeiIdsec(dr.getWeiIdsec());
				if(drs.getMyLevel().intValue()==1)
				{
					dr.setWeiIdone(drs.getWeiId());
				}
				else if (drs.getMyLevel().intValue()==2)
				{
					dr.setWeiIdsec(drs.getWeiId());
				}
			}
		}
		dr.setMyLevel(da.getLevel());
		baseDAO.saveOrUpdate(dr);
		//反写DAgentApply的状态及agentid字段	
		da.setStatus(Integer.parseInt(AgentStatus.Ok.toString()));
		baseDAO.update(da);
	}
	/**
	 * 828活动分配优惠券
	 * @param ss
	 */
	public void updateTicket(OSupplyerOrder ss){
		// 判断是否在活动区里面。
		Long l = 0L;
		if (ss.getActivityId() != null&&ss.getActivityId()>0)
			l = ss.getActivityId();
		AActivity aa = baseDAO.get(AActivity.class, l);
		// baseDAO.getUniqueResultByHql(" from AActivity a where a.type=?",1);
		if (aa != null && aa.getType() != null && aa.getType() > 0) {
			UWallet wal = baseDAO.get(UWallet.class, ss.getBuyerID());
			if (wal == null) {
				wal = new UWallet();
				wal.setCreateTime(new Date());
				wal.setWeiId(ss.getBuyerID());
				wal.setWeiDianCoin(0.0);
				wal.setBalance(0.0);
				wal.setAccountIng(0.0);
				wal.setAccountNot(0.0);
				wal.setAbleTicket(0.0);
				wal.setUnAbleTicket(0.0);
				wal.setLimitTicket(0.0);
			}
			double ticket = wal.getLimitTicket() == null ? 0.0 : wal.getLimitTicket();
			if (ticket < 5000.0) {
				Double backprice = 0.0;
				if (ticket + ss.getTotalPrice().longValue() >= 5000.0) {
					backprice = 5000.0 - ticket;
				} else {
					backprice = ss.getTotalPrice();
				}
				UTradingDetails back = new UTradingDetails();
				back.setAmount(backprice); // 去掉邮费
				back.setCreateTime(new Date());
				back.setLtwoType((short) 0);// Short.parseShort(HuoKuanType.Product.toString())
				back.setOrderNo(ss.getPayOrderId());
				back.setSupplyOrder(ss.getSupplierOrderId());
				back.setState(Short.parseShort(UserAmountStatus.weiFangKuan.toString()));
				back.setType(Short.parseShort(UserAmountType.ticket.toString()));
				back.setWeiId(ss.getBuyerID());
				back.setSurplusAmout(backprice);
				baseDAO.save(back);
				// 更新钱包
				wal.setUnAbleTicket((wal.getUnAbleTicket() == null ? 0.0 : wal.getUnAbleTicket()) + backprice);
				wal.setLimitTicket((wal.getLimitTicket() == null ? 0.0 : wal.getLimitTicket()) + backprice);
				baseDAO.saveOrUpdate(wal);
			}

		}
	}
	
	/**
	 * 
	 * @param payOrderid
	 * @param supplyOrderid
	 * @param amount
	 * @param weiid
	 * @param state
	 * @param type
	 * @param ltwoType
	 */
	public void saveUTradingDetails(String payOrderid,String supplyOrderid, Double amount,Long weiid,Short state,Short type,Short ltwoType){
		UTradingDetails marketver = new UTradingDetails();
		marketver.setAmount(amount);
		marketver.setCreateTime(new Date());
		marketver.setLtwoType(ltwoType);
		marketver.setOrderNo(payOrderid);
		marketver.setSupplyOrder(supplyOrderid);
		marketver.setState(state);
		marketver.setType(type);
		marketver.setWeiId(weiid);
		marketver.setSurplusAmout(marketver.getAmount());
		baseDAO.save(marketver);
	}

	/*
	 * ========================订单处理新版 2016-7-8（完） ===========================================================
	 */
}
