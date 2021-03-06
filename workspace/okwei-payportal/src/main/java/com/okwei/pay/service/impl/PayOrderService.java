package com.okwei.pay.service.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import javassist.expr.NewArray;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.okwei.bean.domain.OPayOrder;
import com.okwei.bean.domain.OPayOrderExtend;
import com.okwei.bean.domain.OPayOrderLog;
import com.okwei.bean.domain.OPayOrderRefund;
import com.okwei.bean.domain.OProductOrder;
import com.okwei.bean.domain.OSupplyerOrder;
import com.okwei.bean.domain.PProductAssist;
import com.okwei.bean.domain.PProducts;
import com.okwei.bean.domain.TBatchMarket;
import com.okwei.bean.domain.TSmsverification;
import com.okwei.bean.domain.TVerificationId;
import com.okwei.bean.domain.UBankCard;
import com.okwei.bean.domain.UBatchPort;
import com.okwei.bean.domain.UBatchSupplyer;
import com.okwei.bean.domain.UDemandProduct;
import com.okwei.bean.domain.UPushMessage;
import com.okwei.bean.domain.USupplierFollowMsg;
import com.okwei.bean.domain.USupplyChannel;
import com.okwei.bean.domain.USupplyer;
import com.okwei.bean.domain.UTradingDetails;
import com.okwei.bean.domain.UVerifier;
import com.okwei.bean.domain.UVerifierFollowMsg;
import com.okwei.bean.domain.UWallet;
import com.okwei.bean.domain.UWalletDetails;
import com.okwei.bean.domain.UWeiSeller;
import com.okwei.bean.domain.UYunSupplier;
import com.okwei.bean.domain.UYunVerifier;
import com.okwei.bean.enums.AmountStatusEnum;
import com.okwei.bean.enums.AmountTypeEnum;
import com.okwei.bean.enums.MobileBindEnum;
import com.okwei.bean.enums.OrderStatusEnum;
import com.okwei.bean.enums.OrderTypeEnum;
import com.okwei.bean.enums.PayTypeEnum;
import com.okwei.bean.enums.SupplierStatusEnum;
import com.okwei.bean.enums.SupplierTypeEnum;
import com.okwei.bean.enums.UserIdentityType;
import com.okwei.bean.enums.WalletMainTypeEnum;
import com.okwei.bean.enums.YunVerifyTypeEnum;
import com.okwei.bean.vo.BResultMsg;
import com.okwei.bean.vo.LoginUser;
import com.okwei.common.JsonUtil;
import com.okwei.dao.impl.BaseDAO;
import com.okwei.pay.bean.enums.BatchPortStatusEnum;
import com.okwei.pay.bean.enums.BatchSupplyerInTypeEnum;
import com.okwei.pay.bean.enums.FollowTypeEnum;
import com.okwei.pay.bean.enums.PayResultStateEnum;
import com.okwei.pay.bean.enums.ProductOrderTypeEnum;
import com.okwei.pay.bean.enums.RzyjTypeEnum;
import com.okwei.pay.bean.enums.VerifierIdentityEnum;
import com.okwei.pay.bean.enums.VerifyCodeTypeEnum;
import com.okwei.pay.bean.enums.YunSupplierServiceTypeEnum;
import com.okwei.pay.bean.enums.YunSupplierStatusEnum;
import com.okwei.pay.bean.util.SendSMSUtil;
import com.okwei.pay.bean.vo.ConfigSetting;
import com.okwei.pay.bean.vo.OrderTypePower;
import com.okwei.pay.bean.vo.PayResult;
import com.okwei.pay.bean.vo.ResultMsg;
import com.okwei.pay.bean.vo.SendSMSVO;
import com.okwei.pay.dao.IPayOrderDAO;
import com.okwei.pay.service.IPayOrderService;
import com.okwei.service.impl.BaseService;
import com.okwei.service.order.IBasicPayService;
import com.okwei.util.AppSettingUtil;
import com.okwei.util.BitOperation;
import com.okwei.util.DateUtils;
import com.okwei.util.MD5Encrypt;
import com.okwei.util.ObjectUtil;
import com.okwei.util.RedisUtil;

@Service
public class PayOrderService extends BaseService implements IPayOrderService {
    @Autowired
    public IPayOrderDAO payOrderDAO;
    
    @Autowired 
    public IBasicPayService payService;

    private Log logger = LogFactory.getLog(this.getClass());
    
    /**
     * 不适用代金券 
     * @param payOrder
     */
    public void UP_CoinPayAmountDel(OPayOrder payOrder){
		OPayOrderExtend extend = payOrderDAO.get(OPayOrderExtend.class, payOrder.getPayOrderId());
		if(extend!=null){
			extend.setCoinAmount(0d);
			payOrderDAO.update(extend);
		}
	}
    public double UP_getCoinPayAmount(OPayOrder payOrder){
		double payCoin=0;
		if(payOrder!=null){
			double coinTotal=0;
			//是否可以用代金券 
			boolean useCash=false;
			UWallet wallet=super.getById(UWallet.class, payOrder.getWeiId());
			if(wallet==null||wallet.getTotalCoin()==null||wallet.getTotalCoin().doubleValue()<=0){
				return 0;
			}else {
				coinTotal=wallet.getTotalCoin();
			}
			//能用的代金券大小
			double canUseCoin=0;
			if(payOrder.getTypeState()!=null&&payOrder.getTypeState()==Short.parseShort(OrderTypeEnum.Jinhuo.toString())){
				List<OSupplyerOrder> supplyerOrders=find_SupplyerOrderByOrderID(payOrder.getPayOrderId());
				if(supplyerOrders!=null&&supplyerOrders.size()>0){
					for (OSupplyerOrder ss : supplyerOrders) {
						List<OProductOrder> productOrders=find_OProductOrderBySupplyOrderIds(new String[]{ss.getSupplierOrderId()});
						if(productOrders!=null&&productOrders.size()>0){
							int identity= isShop(payOrder.getWeiId(), productOrders.get(0).getProductId());
							if(!BitOperation.isIdentity(identity, UserIdentityType.Agent)){
								canUseCoin+= ss.getTotalPrice()*0.1;
								useCash=true;
							}
						}	
					}
				}
			}
			if(useCash){
				if(coinTotal>=canUseCoin)
					payCoin= canUseCoin;
				else {
					payCoin= coinTotal;
				}
				OPayOrderExtend extend = super.getById(OPayOrderExtend.class, payOrder.getPayOrderId());
				if (extend != null) {
					extend.setCoinAmount(payCoin);
					super.update(extend);
				} else {
					extend = new OPayOrderExtend();
					extend.setOpayOrderId(payOrder.getPayOrderId());
					extend.setCoinAmount(payCoin);
					super.add(extend);
				}
			}
		}
		return payCoin;
	}
    
	public List<OProductOrder> find_OProductOrderBySupplyOrderIds(String[] supplyOrderIds){
		String hql_sup = " from OProductOrder o where o.supplierOrderId in(:supplyOrderIds) ";
		Map<String, Object> map=new HashMap<String, Object>();
		map.put("supplyOrderIds", supplyOrderIds);
		return payOrderDAO.findByMap(hql_sup, map);
	}
    
	public List<OSupplyerOrder> find_SupplyerOrderByOrderID(String payId) {
		String hql_sup = " from OSupplyerOrder o where o.payOrderId=? ";
		Object[] b_sup = new Object[1];
		b_sup[0] = payId;
		return payOrderDAO.find(hql_sup, b_sup);
	}
    /**
     * 订单信息校验
     * 
     * @param orderNo
     *            支付订单号
     * @param payAmout
     *            支付金额
     * @param payType
     *            支付类型
     * @param openID
     *            第三方支付订单信息
     * @param wxWalltOrderNo
     *            第三方支付订单号
     * @return
     */
	public PayResult OrderPaymentSuccessTwo(String orderNo,double payAmout,PayTypeEnum payType,OPayOrder payOrder2)
	{
		PayResult result = new PayResult();
		result.setState(PayResultStateEnum.Success);
		result.setOrderNo(orderNo);// 设置返回的订单号
		
		DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		logger.error("支付回写  订单号:" + orderNo + "时间:" + format.format(new Date()));
		// ***** 防止并发 *****//
		String redisOrder = RedisUtil.getString(orderNo+"_lock");// 获取订单号的缓存
		if (ObjectUtil.isEmpty(redisOrder)) {// 判断缓存是否存在
		    RedisUtil.setString(orderNo+"_lock", orderNo, 300);// 缓存不存在，添加缓存
		} else {
		    result.setMessage("该订单正在处理中...");
		    result.setState(PayResultStateEnum.TryAgain);
		    return result;
		}
		

		// ***** 判断订单金额的异常情况 *****//
		if (payAmout < 0.01) {
		    result.setMessage("付款金额异常");
		    result.setState(PayResultStateEnum.Failure);
		    return result;
		}
		OPayOrder payOrder =null;
		if(payOrder2!=null){
			payOrder=payOrder2;
		}
		else{
			payOrder=payOrderDAO.getOPayOrder(orderNo);// 获取支付订单实体
		}
		if (payOrder == null) {
		    result.setMessage("订单不存在！");
		    result.setState(PayResultStateEnum.Failure);
		    return result;
		}

		// 判断是否是未支付的订单
		if (!OrderStatusEnum.Nopay.toString().equals(payOrder.getState().toString())
			&& !OrderStatusEnum.Tovoided.toString().equals(payOrder.getState().toString())) {
		    result.setMessage("该订单已经支付成功！不能重复处理！");
		    result.setState(PayResultStateEnum.Failure);
		    return result;
		}
		
		//回写支付记录表
		payOrderDAO.editPayOrderLog(orderNo, payAmout);

		// 订单的类型
		String orderTypeStr = payOrder.getTypeState().toString();	
		if (OrderTypeEnum.Pt.toString().equals(orderTypeStr) 
				|| OrderTypeEnum.BatchOrder.toString().equals(orderTypeStr) 
				|| OrderTypeEnum.BatchWholesale.toString().equals(orderTypeStr) 
				|| OrderTypeEnum.BookHeadOrder.toString().equals(orderTypeStr) 
				|| OrderTypeEnum.BookTailOrder.toString().equals(orderTypeStr) 
				|| OrderTypeEnum.BookFullOrder.toString().equals(orderTypeStr) 
//				|| OrderTypeEnum.Puhuo.toString().equals(orderTypeStr) 
//				|| OrderTypeEnum.PuhuoFull.toString().equals(orderTypeStr) 
//				|| OrderTypeEnum.PuhuoHeader.toString().equals(orderTypeStr) 
//				|| OrderTypeEnum.PuhuoTail.toString().equals(orderTypeStr)  
				|| OrderTypeEnum.Jinhuo.toString().equals(orderTypeStr) 
				|| OrderTypeEnum.RetailAgent.toString().equals(orderTypeStr)
				|| OrderTypeEnum.HalfTaste.toString().equals(orderTypeStr)
				) {
			// 获取该订单下所有供应商订单
			List<OSupplyerOrder> supplyerOrderList = getSupplyerOrderList(payOrder);
			//标识 是否需要退款
			boolean isRefund = false;		
			//供应商订单数据异常
			if(supplyerOrderList ==null || supplyerOrderList.size() <1){
				result.setMessage("供应商订单数据异常！");
				isRefund = true;
			}
			//供应商订单 金额与支付订单 金额不一致 预订单不能做该校验
			if(!OrderTypeEnum.BookHeadOrder.toString().equals(orderTypeStr) 
					&& !OrderTypeEnum.BookTailOrder.toString().equals(orderTypeStr) 
					&& !OrderTypeEnum.BookFullOrder.toString().equals(orderTypeStr))
			{
				double totalAmout =0;
				for (OSupplyerOrder oSupplyerOrder : supplyerOrderList) {
					totalAmout += oSupplyerOrder.getTotalPrice() + oSupplyerOrder.getPostage();
				}
				if(payOrder.getTotalPrice() != totalAmout){
					result.setMessage("供应商订单金额与支付订单金额数据异常！");
					isRefund = true;
				}
			}
			//支付退款
			if(isRefund){
				if(payType != PayTypeEnum.WeiWallet && payType !=PayTypeEnum.WeiCoinPay && payType!=PayTypeEnum.WalletCoinPay){
					OPayOrderRefund refund = new OPayOrderRefund();
					refund.setCreateTime(new Date());
					refund.setPayAmout(payAmout);
					refund.setPayOrderId(orderNo);
					refund.setPayType(Short.parseShort(payType.toString()));
					refund.setState((short) 0);
					refund.setWeiId(payOrder.getWeiId());
					refund.setRemarks(result.getMessage());
					payOrderDAO.saveOrUpdate(refund);
				}
			    
			    result.setState(PayResultStateEnum.Failure);
			    return result;
			}		
		}
		
		
		//订单金额校验
		if (payOrder.getTotalPrice() < 0.01) {
		    result.setMessage("该订单金额异常！");
		    result.setState(PayResultStateEnum.Failure);
		    return result;
		}

		// ***** 订单金额验证 *****//
		if (OrderTypeEnum.BatchRzy.toString().equals(payOrder.getTypeState().toString())) {
		    String batchPortServerFee = AppSettingUtil.getSingleValue("BatchPortServerFee");
		    if (payOrder.getTotalPrice() != Double.parseDouble(batchPortServerFee)) {
			result.setMessage("批发号供应商成为认证点服务金额不正确！");
			result.setState(PayResultStateEnum.Failure);
			return result;

		    }
		} else if (OrderTypeEnum.BatchGys.toString().equals(payOrder.getTypeState().toString()) || OrderTypeEnum.BatchPortNoServiceFee.toString().equals(payOrder.getTypeState().toString())) {
		    String batchSupplyerBond = AppSettingUtil.getSingleValue("BatchSupplyerBond");
		    if (payOrder.getTotalPrice() != Double.parseDouble(batchSupplyerBond)) {
			result.setMessage("批发号供应商进驻保证金金额不正确！");
			result.setState(PayResultStateEnum.Failure);
			return result;

		    }
		} else if (OrderTypeEnum.GysAndVerifier.toString().equals(payOrder.getTypeState().toString())) {
		    String gysAndVerifierServerFee = AppSettingUtil.getSingleValue("GysAndVerifierServerFee");
		    if (payOrder.getTotalPrice() != Double.parseDouble(gysAndVerifierServerFee)) {
			result.setMessage("批发号供应商进驻升级认证点服务套餐金额不正确！");
			result.setState(PayResultStateEnum.Failure);
			return result;

		    }
		} else if (OrderTypeEnum.YunRzy.toString().equals(payOrder.getTypeState().toString())) {
		    if (payOrder.getTotalPrice() != ConfigSetting.yunVerifierBond) {
			result.setMessage("升级认证员套餐金额不正确！");
			result.setState(PayResultStateEnum.Failure);
			return result;
		    }
		} else if (OrderTypeEnum.YunGysNoServiceFee.toString().equals(payOrder.getTypeState().toString())) {
		    if (payOrder.getTotalPrice() != ConfigSetting.yunSupplyerBond) {
			result.setMessage("工厂号保证金金额不正确！");
			result.setState(PayResultStateEnum.Failure);
			return result;
		    }
		}

		// ***** 判断是否使用微钱包支付 并且金额是否充足 *****//
		double walletmoney = payOrder.getWalletmoney() == null ? 0 : payOrder.getWalletmoney();
		if ((walletmoney * -1) > 0) {
			result.setState(PayResultStateEnum.Failure);
			result.setMessage("支出金额不能小于0！");
			return result;
		}
		double walletweicoin=payOrder.getWeiDianCoin() == null ? 0:payOrder.getWeiDianCoin();
		if(walletweicoin * -1 > 0)
		{
			result.setState(PayResultStateEnum.Failure);
			result.setMessage("支出微金币不能小于0！");
			return result;
		}
		if (walletmoney > 0 || walletweicoin>0) {
		    if (payOrder.getWeiId() < 1) {
			result.setState(PayResultStateEnum.Failure);
			result.setMessage("微钱包微金币支付必须登录！");
			return result;
		    }
		    double avaiAmout = payOrderDAO.getUserAmountAvailable(payOrder.getWeiId());// 获取用户可用微钱包余额
		    UWallet w=payOrderDAO.getUWallet(payOrder.getWeiId());
		    double avaiAmoutweicoin=w.getWeiDianCoin()==null ? 0D:w.getWeiDianCoin();
		    if (avaiAmout < walletmoney || avaiAmoutweicoin < walletweicoin) {// 如果可用金额 不足以支付该订单微店钱包部分
				if (payAmout >= payOrder.getTotalPrice()) {// 判断是否是全额支付,
									   // 更新微钱包支付为0
				    payOrder.setWalletmoney(0D);// 更新微钱包部分为0
				    payOrder.setWeiDianCoin(0D);
				} else {// 支付不能成功！ 付款不足
					// DecimalFormat df = new DecimalFormat("#.00");
				    result.setState(PayResultStateEnum.Failure);
				    result.setMessage("付款不足，微店钱包部分余额不足 可用：" + String.format("%.2f", avaiAmout) + "订单：" + String.format("%.2f", walletmoney));
				    return result;
				}
		    }
		}
		
		
		// 3.判断银行卡支付金额是否正确
		double needAmout = payOrder.getTotalPrice() - walletmoney - walletweicoin;// 银行卡支付的金额
		//使用优惠券支付的金额
		OPayOrderExtend extend= super.getById(OPayOrderExtend.class, payOrder.getPayOrderId());
		if(extend!=null&&extend.getCoinAmount()!=null){
			needAmout=needAmout-extend.getCoinAmount();
		}
		// 误差不能超过0.001分钱 支付不成功 付款不足
		if ((payAmout - needAmout) < -0.00001) {
		    result.setState(PayResultStateEnum.Failure);
		    result.setMessage("付款不足！需付金额：" + String.format("%.2f", needAmout) + "实付金额：" + String.format("%.2f", payAmout));
		    return result;
		}
		// ***** 插入第三方支付订单信息 可以不管它 *****//
	/*	if (payType == payType.WxPay && !ObjectUtil.isEmpty(wxWalltOrderNo)) {
		    OPayRecords records = new OPayRecords();
		    records.setCreateTime(new Date());
		    records.setPayOrderId(payOrder.getPayOrderId());
		    records.setRowOne(openID);
		    records.setRowTwo(wxWalltOrderNo);
		    records.setRowThree(payOrder.getWeiId().toString());
		    payOrderDAO.insertOPayRecords(records);
		}*/
		// ***** 订单数据处理*****//
		//result = orderDataProcessing(payOrder);
		return result;
	}
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public PayResult OrderPaymentSuccess(String orderNo, double payAmout,PayTypeEnum payType) {
    	return OrderPaymentSuccessTwo(orderNo,payAmout,payType,null);
    }

    /**
     * 订单数据处理
     * 
     * @param payOrder
     * @return
     */
    @Override
    @Transactional
    public PayResult orderDataProcessing(String orderNo,PayTypeEnum payType) {
	PayResult result = new PayResult();
	result.setState(PayResultStateEnum.Failure);
	
	try {
		
		OPayOrder order = payOrderDAO.getOPayOrder(orderNo);// 获取支付订单实体
	    if (order == null || ObjectUtil.isEmpty(order.getPayOrderId())) {
		throw new RuntimeException("供应商订单处理参数异常");
	    }
	    payService.editOrderDataProcess(order, payType);
//	    // *****微钱包支付部分处理*****//
//	    if (order.getWalletmoney() > 0) {
//		// 扣除微钱包支付部分
//		UWalletDetails details = new UWalletDetails();
//		details.setAmount(order.getWalletmoney() * -1);
//		details.setCreateTime(new Date());
//		details.setPayOrder(order.getPayOrderId());
//		details.setMainType(Short.parseShort(WalletMainTypeEnum.outcome.toString()));
//		details.setWeiId(order.getWeiId());
//		details.setType(Short.parseShort(AmountTypeEnum.gouwu.toString()));
//
//		// *****radis 钱包锁*****//
//		// *****radis 钱包锁 结束*****//
//		UWallet wallet = payOrderDAO.getUWallet(order.getWeiId());
//		if (wallet != null) {
//		    double balance = wallet.getBalance() == null ? 0 : wallet.getBalance();
//		    wallet.setBalance(balance + details.getAmount());// 更新钱包表
//		} else {
//		    wallet = new UWallet();
//		    wallet.setBalance(details.getAmount());
//		    wallet.setAccountIng(0D);
//		    wallet.setAccountNot(0D);
//		    wallet.setWeiId(order.getWeiId());
//		    wallet.setCreateTime(new Date());
//		    payOrderDAO.insertUWallet(wallet);
//		}
//		details.setRestAmount(wallet.getBalance());
//		payOrderDAO.insertUWalletDetails(details);
//	    }
//	    // 2.修改支付订单状态
//	    order.setState(Short.parseShort(OrderStatusEnum.Payed.toString()));// 设置状态为已支付
//	    order.setPayTime(new Date());
//	    order.setPayType(Short.parseShort(payType.toString()));
//	    if(!payOrderDAO.updateOPayOrder(order)){
//	     throw new RuntimeException("修改支付订单状态失败");
//	    }
//
//	    // 3. 该支付订单 是否需要处理供应商订单
//	    String orderTypeStr = order.getTypeState().toString();// 订单的类型
//	    // 云商通订单，零售订单，批发号批发订单，预订订单首款，预订订单尾款，预订订单全款
//	    if (OrderTypeEnum.Pt.toString().equals(orderTypeStr) || OrderTypeEnum.BatchOrder.toString().equals(orderTypeStr) || OrderTypeEnum.BatchWholesale.toString().equals(orderTypeStr) || OrderTypeEnum.BookHeadOrder.toString().equals(orderTypeStr) || OrderTypeEnum.BookTailOrder.toString().equals(orderTypeStr) || OrderTypeEnum.BookFullOrder.toString().equals(orderTypeStr)) {
//		// 供应商订单处理 货款 抽成 佣金 分配
//		SupplyOrderPaySuccess(order);
//	    } else if (OrderTypeEnum.BatchRzy.toString().equals(orderTypeStr)) {// 批发号认证点
//		PayBatchRzy(order, true);
//	    } else if (OrderTypeEnum.BatchGys.toString().equals(orderTypeStr)) {// 批发号供应商
//		PayBatchGys(order, true);
//	    } else if (OrderTypeEnum.GysAndVerifier.toString().equals(orderTypeStr)) {// 批发号供应商和认证点
//		PayBatchRzy(order, true);
//		PayBatchGys(order, true);
//	    } else if (OrderTypeEnum.BatchPortNoServiceFee.toString().equals(orderTypeStr)) {// 批发号认证点重新进驻
//											     // 不需要要服务费
//											     // 不分配进驻奖励
//		PayBatchRzy(order, false);
//		PayBatchGys(order, false);
//	    } else if (OrderTypeEnum.ChongZhi.toString().equals(orderTypeStr)) {// 充值
//		PayChongZhi(order);
//	    } else if (OrderTypeEnum.YunGys.toString().equals(orderTypeStr)) {// 工厂号供应商进驻
//		PayYunSupply(order, true);
//	    } else if (OrderTypeEnum.YunGysNoServiceFee.toString().equals(orderTypeStr)) {// 工厂号
//											  // 重新进驻
//											  // 不需要服务费
//											  // 不分配进驻奖励
//											  // 不更新进驻时间
//		PayYunSupply(order, false);
//	    } else if (OrderTypeEnum.YunRzy.toString().equals(orderTypeStr)) {// 工厂号认证员保证金
//		PayYunVerify(order);
//	    }
//	    result.setState(PayResultStateEnum.Success);
//	    result.setMessage("订单号：" + order.getPayOrderId() + "支付成功！");
//	    // payOrderDAO.updateOPayOrder(order);// 修改支付订单数据

	} catch (RuntimeException e) {
	    result.setState(PayResultStateEnum.TryAgain);
	    result.setMessage(e.toString());	    
	    // 日记
	    logger.error(e.toString());
	    
	    throw new RuntimeException(e);
	}
	// 订单处理完成 过期redis锁
	RedisUtil.delete(orderNo);
	return result;
    }

    /**
     * 工厂号 认证员支付保证金成功处理
     * 
     * @param order
     */
    public void PayYunVerify(OPayOrder order) {
	// 1.获取认证员信息
	UYunVerifier verifier = payOrderDAO.getYunVerifier(order.getWeiId());
	if (verifier != null && verifier.getType() != Short.parseShort(YunVerifyTypeEnum.zsVerify.toString())) {
	    // 2.修改云商认证员表 身份 保证金 初始化 启动平均分配认证员标示(随机分配)
	    int verAllotCount = 0;
	    int supAllotCount = 0;
	    UYunVerifier yv = payOrderDAO.getYunVerifierByCount();
	    if (yv != null) {
		verAllotCount = yv.getVerAllotCount() == null ? 0 : yv.getVerAllotCount();
		supAllotCount = yv.getSupAllotCount() == null ? 0 : yv.getSupAllotCount();
	    }
	    verifier.setType(Short.parseShort(YunVerifyTypeEnum.zsVerify.toString()));
	    verifier.setBond(ConfigSetting.yunVerifierBond);
	    verifier.setInTime(new Date());
	    verifier.setVerAllotCount(verAllotCount);
	    verifier.setSupAllotCount(supAllotCount);
	    verifier.setIsActive((short) 1);
	    // 3.修改认证员表身份
	    UVerifier uv = payOrderDAO.getUVerifier(order.getWeiId());
	    if (uv != null && !IsAnIdentity(uv.getType() == null ? 0 : uv.getType(), Short.parseShort(VerifierIdentityEnum.Percent.toString()))) {
		int identity = (uv.getType() == null ? 0 : uv.getType()) + Short.parseShort(VerifierIdentityEnum.Percent.toString());
		uv.setType((short) identity);
	    }
	}
    }

    /**
     * 工厂号 供应商订进驻支付成功处理
     * 
     * @param order
     */
    public void PayYunSupply(OPayOrder order, Boolean isAllocateReward) throws RuntimeException {
	// 1.获取该供应商信息
	UYunSupplier supplier = payOrderDAO.getYunSupplier(order.getWeiId());
	if (supplier != null && supplier.getStatus() != Short.parseShort(YunSupplierStatusEnum.JinZhu.toString())) {// 1*
	    // 2.更新云商供应商表状态 保证金 进驻时间 等
	    supplier.setStatus(Short.parseShort(YunSupplierStatusEnum.JinZhu.toString()));
	    supplier.setBond(ConfigSetting.yunSupplyerBond);
	    if (isAllocateReward) // 如果分配奖励 才更新进驻时间
	    {
		supplier.setPayTime(new Date());
	    }
	    // 3.更新供应商表状态
	    USupplyer su = payOrderDAO.getSupplyer(order.getWeiId());
	    if (su != null && !IsAnIdentity(su.getType() == null ? 0 : su.getType(), Short.parseShort(SupplierTypeEnum.YunSupplier.toString()))) {
		int identity = su.getType() == null ? 0 : su.getType() + Short.parseShort(SupplierTypeEnum.YunSupplier.toString());
		su.setType((short) identity);
	    }
	    // 4.分配金额
	    double socureAmout = 0;
	    double followAmout = 0;
	    if (supplier.getServiceType() == Short.parseShort(YunSupplierServiceTypeEnum.OneYear.toString())) {
		socureAmout = ConfigSetting.yunSupplyerOneYearSocure;
		followAmout = ConfigSetting.yunSupplyerOneYearFollow;
	    } else if (supplier.getServiceType() == Short.parseShort(YunSupplierServiceTypeEnum.ThreeYear.toString())) {
		socureAmout = ConfigSetting.yunSupplyerThreeYearSocure;
		followAmout = ConfigSetting.yunSupplyerThreeYearFollow;
	    }
	    if (isAllocateReward) {
		List<UTradingDetails> amountList = new ArrayList<UTradingDetails>();
		// 来源佣金
		if (supplier.getSourceReciever() > 0 && socureAmout > 0) {
		    UTradingDetails td = new UTradingDetails();
		    td.setAmount(socureAmout);
		    td.setCreateTime(new Date());
		    td.setLtwoType(Short.parseShort(RzyjTypeEnum.SourceAmount.toString()));
		    td.setOrderNo(order.getPayOrderId());
		    td.setSupplyOrder(order.getWeiId().toString());
		    td.setState(Short.parseShort(AmountStatusEnum.weiFangKuan.toString()));
		    td.setType(Short.parseShort(AmountTypeEnum.rzYj.toString()));
		    td.setWeiId(supplier.getSourceReciever());// 上交给老大
		    td.setFromWeiID(supplier.getSourceWeiId());// 金额来源微店
		    amountList.add(td);
		}
		// 跟进佣金
		if (supplier.getRzReciever() > 0 && followAmout > 0) {
		    UTradingDetails td = new UTradingDetails();
		    td.setAmount(followAmout);
		    td.setCreateTime(new Date());
		    td.setLtwoType(Short.parseShort(RzyjTypeEnum.FollowAmout.toString()));
		    td.setOrderNo(order.getPayOrderId());
		    td.setSupplyOrder(order.getWeiId().toString());
		    td.setState(Short.parseShort(AmountStatusEnum.weiFangKuan.toString()));
		    td.setType(Short.parseShort(AmountTypeEnum.rzYj.toString()));
		    td.setWeiId(supplier.getRzReciever());// 上交老大
		    td.setFromWeiID(supplier.getRzWeiId());// 金额来源
		    amountList.add(td);
		}
		// 统一添加用户金额
		InsertAmoutDeatils(amountList);
		// 3.修改供应商商品的Type
		payOrderDAO.updatePayedEditType(order.getWeiId(), Short.parseShort(SupplierTypeEnum.YunSupplier.toString()));
		// 4.修改上架表中 供应商上架自己的商品的状态
		payOrderDAO.updatePayedEditType(order.getWeiId());
	    }
	}
    }

    /**
     * 充值订单支付
     * 
     * @param order
     */
    public void PayChongZhi(OPayOrder order) {
	UWalletDetails details = new UWalletDetails();
	details.setAmount(order.getTotalPrice());
	details.setCreateTime(new Date());
	details.setMainType(Short.parseShort(WalletMainTypeEnum.income.toString()));
	details.setPayOrder(order.getPayOrderId());
	details.setType(Short.parseShort(AmountTypeEnum.chongzhi.toString()));
	details.setWeiId(order.getWeiId());
	UWallet wallet = payOrderDAO.getUWallet(order.getWeiId());
	if (wallet != null) {
	    wallet.setBalance(wallet.getBalance() == null ? 0 : wallet.getBalance() + details.getAmount());
	} else {
	    wallet = new UWallet();
	    wallet.setBalance(details.getAmount());
	    wallet.setAccountIng(0d);
	    wallet.setAccountNot(0d);
	    wallet.setWeiId(details.getWeiId());
	    wallet.setCreateTime(new Date());
	    payOrderDAO.insertUWallet(wallet);
	}
	details.setRestAmount(wallet.getBalance());
	payOrderDAO.insertUWalletDetails(details);
    }

    /**
     * 批发号供应商进驻支付成功处理
     * 
     * @param order
     */
    public void PayBatchGys(OPayOrder order, boolean isNewJoin) {
	UBatchSupplyer bsupply = payOrderDAO.getUBatchSupplyer(order.getWeiId());
	if (bsupply != null) {
	    // 1.修改批发号供应商表状态 保证金
	    bsupply.setStatus(Short.parseShort(SupplierStatusEnum.PayIn.toString()));
	    bsupply.setInTime(new Date());
	    bsupply.setValidBegDate(new Date());
	    bsupply.setValidEndDate(DateUtils.addDate(new Date(), 365));
	    bsupply.setBond(ConfigSetting.batchSupplyerBond);
	    bsupply.setServiceFee(ConfigSetting.batchSupplyerServiceFee);
	    USupplierFollowMsg msg = new USupplierFollowMsg();// ****
	    msg.setCreateTime(new Date());
	    msg.setContent("供应商升级认证点缴费");
	    msg.setOperaterId(order.getWeiId());
	    msg.setWeiId(order.getWeiId());
	    msg.setRecordType(Integer.parseInt(FollowTypeEnum.PayIn.toString()));
	    // 2.更新供应商表的身份类型
	    USupplyer supplyer = payOrderDAO.getSupplyer(order.getWeiId());
	    if (supplyer != null && !IsAnIdentity(supplyer.getType() == null ? 0 : supplyer.getType(), Short.parseShort(SupplierTypeEnum.BatchSupplier.toString()))) {
		int identity = (supplyer.getType() == null ? 0 : supplyer.getType()) + Short.parseShort(SupplierTypeEnum.BatchSupplier.toString());
		supplyer.setType((short) identity);
	    }
	    if (isNewJoin) {
		// 3.修改供应商商品的Type
		List<PProducts> list = payOrderDAO.getListByProducts(order.getWeiId());
		if (list != null && list.size() > 0) {
		    for (PProducts p : list) {
			int identity = (p.getSupperType() == null ? 0 : p.getSupperType()) + Short.parseShort(SupplierTypeEnum.BatchSupplier.toString());
			p.setSupperType((short) identity);
			payOrderDAO.updateProducts(p);
		    }
		}
		// 4.修改上架表中 供应商上架自己的商品的状态
		payOrderDAO.updateClassProduct(order.getWeiId());
		// 消息推送给认证员
		UPushMessage umsg1 = new UPushMessage();
		umsg1.setReciptWeiId(bsupply.getVerifier());
		umsg1.setPushContent("供应商申请：*" + order.getWeiId().toString() + "*已缴纳服务费，正式成为供应商");
		umsg1.setMsgType((short) 40);
		umsg1.setObjectId(order.getPayOrderId());
		umsg1.setExtra(order.getTypeState().toString());
		umsg1.setCreateTime(new Date());
		umsg1.setPushWeiId(111L);
		payOrderDAO.insertSendPushMsg(umsg1);
	    }
	    // 消息推送给付款人
	    UPushMessage umsg = new UPushMessage();
	    umsg.setReciptWeiId(order.getWeiId());
	    umsg.setPushContent("恭喜你！缴费成功，您已成为批发号供应商！");
	    umsg.setMsgType((short) 100);
	    umsg.setObjectId(order.getPayOrderId());
	    umsg.setExtra(order.getTypeState().toString());
	    umsg.setCreateTime(new Date());
	    umsg.setPushWeiId(111L);
	    payOrderDAO.insertSendPushMsg(umsg);
	}
    }

    /**
     * 批发号供应商成为认证点支付成功处理
     * 
     * @param order
     */
    public void PayBatchRzy(OPayOrder order, Boolean isAllocateReward) {
	// 1.获取该认证点信息
	UBatchPort port = payOrderDAO.getBatchPort(order.getWeiId());
	if (port != null) {
	    // 2.更新批发号认证点表状态
	    port.setStatus(Short.parseShort(BatchPortStatusEnum.PayIn.toString()));
	    port.setInTime(new Date());
	    UVerifierFollowMsg msg = new UVerifierFollowMsg();
	    msg.setCreateTime(new Date());
	    msg.setOperateContent("供应商进驻保证金缴费");
	    msg.setOperateMan(order.getWeiId());
	    msg.setRecordType(Short.parseShort(FollowTypeEnum.PayIn.toString()));
	    payOrderDAO.insertUVerifierFollowMsg(msg);// 流水记录
	    // 3.更新认证员表身份类型
	    UVerifier uv = payOrderDAO.getUVerifier(order.getWeiId());
	    if (uv != null && !IsAnIdentity(uv.getType() == null ? 0 : uv.getType(), Short.parseShort(VerifierIdentityEnum.BatchVerifierPort.toString()))) {
		int identity = (uv.getType() == null ? 0 : uv.getType()) + Short.parseShort(VerifierIdentityEnum.BatchVerifierPort.toString());
		uv.setType((short) identity);
	    }
	    if (isAllocateReward) {
		List<UTradingDetails> amountList = new ArrayList<UTradingDetails>();
		// （2015-09-11修改）4.分配佣金700给审核人
		if (port.getCompanyWeiId() != null && port.getCompanyWeiId() > 0) {
		    UTradingDetails td = new UTradingDetails();
		    td.setAmount(ConfigSetting.batchGys);
		    td.setCreateTime(new Date());
		    td.setLtwoType(Short.parseShort(RzyjTypeEnum.BatchUpRenZheng.toString()));
		    td.setOrderNo(order.getPayOrderId());
		    td.setSupplyOrder(order.getWeiId().toString());
		    td.setState(Short.parseShort(AmountStatusEnum.weiFangKuan.toString()));
		    td.setType(Short.parseShort(AmountTypeEnum.rzYj.toString()));
		    td.setWeiId(port.getCompanyWeiId());// 款项默认是自己的
		    td.setFromWeiID(port.getUBWeiId());// 来源是该认证员
		    // payOrderDAO.insertUTradingDatails(td);
		    amountList.add(td);
		}
		// （2015-09-11修改）5.分配佣金300，给上级
		if (port.getSupperReceiver() != null && port.getSupperReceiver() > 0) {
		    UTradingDetails td = new UTradingDetails();
		    td.setAmount(ConfigSetting.batchUpGys);
		    td.setCreateTime(new Date());
		    td.setLtwoType(Short.parseShort(RzyjTypeEnum.BatchUpRenZheng.toString()));
		    td.setOrderNo(order.getPayOrderId());
		    td.setSupplyOrder(order.getWeiId().toString());
		    td.setState(Short.parseShort(AmountStatusEnum.weiFangKuan.toString()));
		    td.setType(Short.parseShort(AmountTypeEnum.rzYj.toString()));
		    td.setWeiId(port.getSupperReceiver());// 款项默认是自己的
		    td.setFromWeiID(port.getSupperVerifier());// 来源是该认证员
		    // payOrderDAO.insertUTradingDatails(td);
		    amountList.add(td);
		}

		// 统一处理所有金额记录
		InsertAmoutDeatils(amountList);
		// // 4.分配佣金1000 给认证员
		// UTradingDetails td = new UTradingDetails();
		// td.setAmount(ConfigSetting.batchGys);
		// td.setCreateTime(new Date());
		// td.setLtwoType(Short.parseShort(RzyjTypeEnum.BatchUpRenZheng.toString()));
		// td.setOrderNo(order.getPayOrderId());
		// td.setSupplyOrder(order.getWeiId().toString());
		// td.setType(Short.parseShort(AmountStatusEnum.weiFangKuan.toString()));
		// // td.setWeiId(port);/款项直接给老大的微钱包
		// td.setFromWeiID(port.getUBWeiId());// 来源是该认证员
		// payOrderDAO.insertUTradingDatails(td);
		// 消息推送给付款人
		UPushMessage umsg1 = new UPushMessage();
		umsg1.setReciptWeiId(port.getUBWeiId());
		umsg1.setPushContent("认证点申请：*" + order.getWeiId().toString() + "*已缴纳服务费，正式成为认证点，并进入您的认证团队");
		umsg1.setMsgType((short) 42);
		umsg1.setObjectId(order.getPayOrderId());
		umsg1.setExtra(order.getTypeState().toString());
		umsg1.setCreateTime(new Date());
		umsg1.setPushWeiId(111L);
		payOrderDAO.insertSendPushMsg(umsg1);
	    }
	    // 消息推送给付款人
	    UPushMessage umsg = new UPushMessage();
	    umsg.setReciptWeiId(order.getWeiId());
	    umsg.setPushContent("恭喜你！缴费成功，您已成为认证服务点！");
	    umsg.setMsgType((short) 100);
	    umsg.setObjectId(order.getPayOrderId());
	    umsg.setExtra(order.getTypeState().toString());
	    umsg.setCreateTime(new Date());
	    umsg.setPushWeiId(111L);
	    payOrderDAO.insertSendPushMsg(umsg);

	}
    }

    /**
     * 判断该用户是否是某个身份
     * 
     * @param code
     * @param verenum
     * @return
     */
    public boolean IsAnIdentity(short code, short verenum) {
	return (code & verenum) >= 1;
    }

    /**
     * 支付成功处理供应商订单
     * 
     * @param order
     */
    public void SupplyOrderPaySuccess(OPayOrder order) throws RuntimeException {
	List<SendSMSVO> sendList = new ArrayList<SendSMSVO>();// 发送短信列表
	// 供应商订单处理 货款 抽成 佣金 分配（支付成功处理供应商订单）
	if (order == null || ObjectUtil.isEmpty(order.getPayOrderId())) {
	    throw new RuntimeException("供应商订单处理参数异常");
	}
	String orderTypeStr = order.getTypeState().toString();// 订单的类型
	// 获取该订单下所有供应商订单
	List<OSupplyerOrder> supplyerOrderList = getSupplyerOrderList(order);
	// 判断供应商列表是否有数据
	if (supplyerOrderList == null || supplyerOrderList.size() <= 0) {
	    throw new RuntimeException("获取供应商订单数据异常");
	}
	// 如果是预订单尾款 必须验证 是否为已支付首款状态
	if (OrderTypeEnum.BookTailOrder.toString().equals(orderTypeStr)) {
	    for (OSupplyerOrder sup : supplyerOrderList) {
		// 如果有不没支付定金，没有收货的订单
		if (!OrderStatusEnum.PayedDeposit.toString().equals(sup.getState().toString()) && !OrderStatusEnum.Accepted.toString().equals(sup.getState().toString())) {
		    throw new RuntimeException("预订单必须先支付首款！");
		}
	    }
	}
	List<String> supplyOrderNo = new ArrayList<String>();
	for (OSupplyerOrder sup : supplyerOrderList) {
	    supplyOrderNo.add(sup.getSupplierOrderId());
	}
	if (supplyOrderNo.size() < 1) {
	    throw new RuntimeException("获取供应商订单数据异常.");
	}
	// 获取所有订单详细
	List<OProductOrder> orderInfoList = payOrderDAO.getProductList(supplyOrderNo);
	if (orderInfoList != null && orderInfoList.size() > 0) {
	    // 所有需要添加的金额的集合
	    List<UTradingDetails> amountList = new ArrayList<UTradingDetails>();
	    // 遍历供应商订单
	    for (OSupplyerOrder oso : supplyerOrderList) {
		// 当前订单类型
		String supplyOrderType = oso.getOrderType().toString();
		if (OrderTypeEnum.BookHeadOrder.toString().equals(orderTypeStr) || OrderTypeEnum.BookTailOrder.toString().equals(orderTypeStr) || OrderTypeEnum.BookFullOrder.toString().equals(orderTypeStr)) {
		    supplyOrderType = order.getTypeState().toString();
		} else {// 如果是其他订单 订单类型读取 供应商订单类型
		    supplyOrderType = oso.getOrderType().toString();
		}
		// 获取该订单详细处理方法权限
		OrderTypePower power = new OrderTypePower(supplyOrderType, oso.getState());
		// 修改供应商订单状态
		oso.setState(Short.parseShort(power.getStatus()));
		oso.setPayTime(new Date());
		payOrderDAO.updateOSupplyerOrder(oso);
		// 获取该供应商的信息
		UBatchSupplyer supplyer = payOrderDAO.getUBatchSupplyer(oso.getSupplyerId());
		// ***** 按供应商订单结算 货款 抽成 *****//
		// 交易额 货款部分 + 邮费部分
		double totalAmout = (oso.getTotalPrice() == null ? 0 : oso.getTotalPrice()) + (oso.getPostage() == null ? 0 : oso.getPostage());
		// 是否分配货款
		if (power.isAllocateHuoKuan()) {
		    // 分配货款
		    UTradingDetails supplyAmout = new UTradingDetails();
		    supplyAmout.setAmount(totalAmout);
		    supplyAmout.setCreateTime(new Date());
		    supplyAmout.setLtwoType((short) 0);
		    supplyAmout.setOrderNo(oso.getPayOrderId());
		    supplyAmout.setSupplyOrder(oso.getSupplierOrderId());
		    supplyAmout.setState(Short.parseShort(AmountStatusEnum.weiFangKuan.toString()));
		    supplyAmout.setType(Short.parseShort(AmountTypeEnum.supplyPrice.toString()));
		    supplyAmout.setWeiId(oso.getSupplyerId());
		    // 如果要扣除佣金 先佣金 后抽成
		    if (power.isDeductComminss()) {
			supplyAmout.setAmount((supplyAmout.getAmount() == null ? 0 : supplyAmout.getAmount()) - (oso.getCommision() == null ? 0 : oso.getCommision()));
		    }
		    // 如果要扣除抽成
		    if (power.isDeductCut()) {
			supplyAmout.setAmount(supplyAmout.getAmount() * ConfigSetting.batchHuoKuan);
		    }
		    // 同步可用金额
		    supplyAmout.setSurplusAmout(supplyAmout.getAmount());
		    amountList.add(supplyAmout);
		    // 短信通知 供应商
		    String supplyMobile = payOrderDAO.GetUserBindMobile(oso.getSupplyerId());
		    if (!ObjectUtil.isEmpty(supplyMobile)) {
			String[] param = new String[] { supplyMobile, String.format("%.2f", totalAmout) };
			SendSMSVO ssvo = new SendSMSVO();
			ssvo.setParam(param);
			ssvo.setWeiid(oso.getSupplyerId());
			ssvo.setVerify(VerifyCodeTypeEnum.SupplyDeliver);
			sendList.add(ssvo);
			// SendSMSByMobile.sendSMS(supplyMobile,
			// "您刚有一个消费者成功购买了产品，总价为：" + String.format("%.2f",
			// totalAmout) +
			// "元，请及时登录后台及时发货，以免影响消费者购买体验。下载微店app，手机也能发货了 http://app.okwei.com [微店网]");
		    }
		}
		// 是否分配抽成
		if (power.isAllocateCut()) {
		    // 批发号订单抽成分配
		    if (supplyer != null) {
			// 如果是整体入驻 牵线人 和 签约人 抽成
			UTradingDetails marketWeiAmount = new UTradingDetails();
			marketWeiAmount.setAmount(totalAmout * ConfigSetting.batchMarketWei);
			marketWeiAmount.setCreateTime(new Date());
			marketWeiAmount.setLtwoType(Short.parseShort(RzyjTypeEnum.BookCut.toString()));
			marketWeiAmount.setOrderNo(oso.getPayOrderId());
			marketWeiAmount.setSupplyOrder(oso.getSupplierOrderId());
			marketWeiAmount.setState(Short.parseShort(AmountStatusEnum.weiFangKuan.toString()));
			marketWeiAmount.setType(Short.parseShort(AmountTypeEnum.rzYj.toString()));
			marketWeiAmount.setWeiId(111L);

			UTradingDetails marketVerAmount = new UTradingDetails();
			marketVerAmount.setAmount(totalAmout * ConfigSetting.batchMarketVer);
			marketVerAmount.setCreateTime(new Date());
			marketVerAmount.setLtwoType(Short.parseShort(RzyjTypeEnum.BookCut.toString()));
			marketVerAmount.setOrderNo(oso.getPayOrderId());
			marketVerAmount.setSupplyOrder(oso.getSupplierOrderId());
			marketVerAmount.setState(Short.parseShort(AmountStatusEnum.weiFangKuan.toString()));
			marketVerAmount.setType(Short.parseShort(AmountTypeEnum.rzYj.toString()));
			marketVerAmount.setWeiId(111L);

			// 判断供应商 是否为整体入驻的供应商
			if (supplyer.getInType() != null && supplyer.getInType() == Short.parseShort(BatchSupplyerInTypeEnum.AllIn.toString())) {
			    TBatchMarket bm = payOrderDAO.getBatchMarket(supplyer.getBmid() == null ? 0 : supplyer.getBmid());
			    if (bm != null) {
				if (bm.getMarketWeiId() != null && bm.getMarketWeiId() > 0) {
				    marketWeiAmount.setWeiId(bm.getMarketWeiId());
				}
				if (bm.getMarketVerWeiId() != null && bm.getMarketVerWeiId() > 0) {
				    marketVerAmount.setWeiId(bm.getMarketVerWeiId());
				}
			    }
			}

			marketWeiAmount.setSurplusAmout(marketWeiAmount.getAmount());
			amountList.add(marketWeiAmount);
			marketVerAmount.setSurplusAmout(marketVerAmount.getAmount());
			amountList.add(marketVerAmount);
			// 认证点抽成
			UTradingDetails amoutPort = new UTradingDetails();
			amoutPort.setAmount(totalAmout * ConfigSetting.batchVerifiPort);
			amoutPort.setCreateTime(new Date());
			amoutPort.setLtwoType(Short.parseShort(RzyjTypeEnum.BookCut.toString()));
			amoutPort.setOrderNo(oso.getPayOrderId());
			amoutPort.setSupplyOrder(oso.getSupplierOrderId());
			amoutPort.setState(Short.parseShort(AmountStatusEnum.weiFangKuan.toString()));
			amoutPort.setType(Short.parseShort(AmountTypeEnum.rzYj.toString()));
			amoutPort.setWeiId(111L);
			amoutPort.setFromWeiID(supplyer.getVerifier());
			if (supplyer.getPortVerifer() != null && supplyer.getPortVerifer() > 0) {
			    amoutPort.setWeiId(supplyer.getPortVerifer());
			}
			amoutPort.setSurplusAmout(totalAmout * ConfigSetting.batchVerifiPort);
			amountList.add(amoutPort);
			// 认证员抽成
			UTradingDetails amoutVerifier = new UTradingDetails();
			amoutVerifier.setAmount(totalAmout * ConfigSetting.batchVerifier);
			amoutVerifier.setCreateTime(new Date());
			amoutVerifier.setLtwoType(Short.parseShort(RzyjTypeEnum.BookCut.toString()));
			amoutVerifier.setOrderNo(oso.getPayOrderId());
			amoutVerifier.setSupplyOrder(oso.getSupplierOrderId());
			amoutVerifier.setState(Short.parseShort(AmountStatusEnum.weiFangKuan.toString()));
			amoutVerifier.setType(Short.parseShort(AmountTypeEnum.rzYj.toString()));
			amoutVerifier.setWeiId(111L);
			amoutVerifier.setFromWeiID(supplyer.getUpVerifier());
			if (supplyer.getCompanyWeiId() != null && supplyer.getCompanyWeiId() > 0) {
			    amoutVerifier.setWeiId(supplyer.getCompanyWeiId());
			}
			amoutVerifier.setSurplusAmout(totalAmout * ConfigSetting.batchVerifier);
			amountList.add(amoutVerifier);
			// 代理商抽成
			UTradingDetails amoutAgent = new UTradingDetails();
			amoutAgent.setAmount(totalAmout * ConfigSetting.batchVerifierAgent);
			amoutAgent.setCreateTime(new Date());
			amoutAgent.setLtwoType(Short.parseShort(RzyjTypeEnum.BookCut.toString()));
			amoutAgent.setOrderNo(oso.getPayOrderId());
			amoutAgent.setSupplyOrder(oso.getSupplierOrderId());
			amoutAgent.setState(Short.parseShort(AmountStatusEnum.weiFangKuan.toString()));
			amoutAgent.setType(Short.parseShort(AmountTypeEnum.rzYj.toString()));
			amoutAgent.setWeiId(111L);// 默认没有代理商 由公司微店号1 领取
			// 如果存在代理商
			if (supplyer.getAgentSupplier() != null && supplyer.getAgentSupplier() > 0) {
			    amoutAgent.setWeiId(supplyer.getAgentSupplier());
			}
			amoutAgent.setSurplusAmout(totalAmout * ConfigSetting.batchVerifierAgent);
			amountList.add(amoutAgent);
			// 公司自己的抽成
			UTradingDetails amountOkwei = new UTradingDetails();
			amountOkwei.setAmount(totalAmout * ConfigSetting.batchOkWei);
			amountOkwei.setCreateTime(new Date());
			amountOkwei.setLtwoType(Short.parseShort(RzyjTypeEnum.BookCut.toString()));
			amountOkwei.setOrderNo(oso.getPayOrderId());
			amountOkwei.setSupplyOrder(oso.getSupplierOrderId());
			amountOkwei.setState(Short.parseShort(AmountStatusEnum.weiFangKuan.toString()));
			amountOkwei.setType(Short.parseShort(AmountTypeEnum.rzYj.toString()));
			amountOkwei.setWeiId(111L);
			amountOkwei.setSurplusAmout(totalAmout * ConfigSetting.batchOkWei);
			amountList.add(amountOkwei);
		    }
		}
		// 处理商品订单 库存 佣金 按单个商品结算
		double allComminssSum = 0d;// 总分配的佣金 总额
		// 查找出该供应商的订单详细
		List<OProductOrder> supplyOIList = new ArrayList<OProductOrder>();
		for (OProductOrder opo : orderInfoList) {
		    if (oso.getSupplierOrderId().equals(opo.getSupplierOrderId())) {
			supplyOIList.add(opo);
		    }
		}
		if (supplyOIList != null && supplyOIList.size() > 0) {
		    // 遍历该供应商的每一件商品进行分配
		    for (OProductOrder product : supplyOIList) {
			// 库存处理
			payOrderDAO.updateProductStyleCount(product.getStyleId() == null ? 0 : product.getStyleId(), product.getCount() == null ? 0 : product.getCount());
			// 如果是赠品 不分配佣金货款 抽成
			if (product.getOrderType() != null && product.getOrderType() == Short.parseShort(ProductOrderTypeEnum.Gifi.toString())) {
			    continue;
			}
			// 是否分配佣金
			if (power.isAllocateComminss()) {
			    // 佣金分配
			    double totalComminss = product.getCommision() * product.getCount(); // 所有可分配佣金
			    // 如果佣金大于等于 货款 则不分配 丢出异常
			    allComminssSum += totalComminss;
			    if (totalComminss >= totalAmout) {
				throw new RuntimeException("商品佣金大于订单成交额");
			    }
			    // 是否扣除抽成
			    if (power.isDeductCut()) {
				totalComminss = totalComminss * ConfigSetting.batchHuoKuan;
			    }
			    // 卖家分配 0.7
			    if (product.getSellerWeiid() != null && product.getSellerWeiid() > 0) {
				UTradingDetails amoutSeller = new UTradingDetails();
				amoutSeller.setAmount(totalComminss * ConfigSetting.yunSeller);
				amoutSeller.setCreateTime(new Date());
				amoutSeller.setLtwoType((short) 0);
				amoutSeller.setOrderNo(product.getPayOrderId());
				amoutSeller.setSupplyOrder(product.getSupplierOrderId());
				amoutSeller.setProductOrder(product.getProductOrderId());
				amoutSeller.setState(Short.parseShort(AmountStatusEnum.weiFangKuan.toString()));
				amoutSeller.setType(Short.parseShort(AmountTypeEnum.orderYj.toString()));
				amoutSeller.setWeiId(product.getSellerWeiid());
				amoutSeller.setSurplusAmout(totalComminss * ConfigSetting.yunSeller);
				amountList.add(amoutSeller);
			    }
			    // 成交微店上级 0.3
			    if (product.getSellerUpweiid() != null && product.getSellerUpweiid() > 0) {
				UTradingDetails amoutUpSeller = new UTradingDetails();
				amoutUpSeller.setAmount(totalComminss * ConfigSetting.yunUpSeller);
				amoutUpSeller.setCreateTime(new Date());
				amoutUpSeller.setLtwoType((short) 0);
				amoutUpSeller.setOrderNo(product.getPayOrderId());
				amoutUpSeller.setSupplyOrder(product.getSupplierOrderId());
				amoutUpSeller.setProductOrder(product.getProductOrderId());
				amoutUpSeller.setState(Short.parseShort(AmountStatusEnum.weiFangKuan.toString()));
				amoutUpSeller.setType(Short.parseShort(AmountTypeEnum.orderYj.toString()));
				amoutUpSeller.setWeiId(product.getSellerUpweiid());
				amoutUpSeller.setSurplusAmout(totalComminss * ConfigSetting.yunUpSeller);
				amountList.add(amoutUpSeller);
			    }
			}
		    }
		    // 如果分配佣金 才发送佣金短信
		    if (power.isAllocateComminss()) {
			// 统计有多少个成交微店 上级微店 且有多少佣金
			List<OProductOrder> sellAmoutList = new ArrayList<OProductOrder>();
			ArrayList<Long> weiNoList = new ArrayList<Long>();
			for (OProductOrder p : supplyOIList) {
			    weiNoList.add(p.getSellerWeiid());
			}
			weiNoList = RidRepeatByLong(weiNoList);
			for (Long sweiNo : weiNoList) {
			    OProductOrder tempopo = new OProductOrder();
			    tempopo.setCommision(0d);
			    for (OProductOrder po : supplyOIList) {
				if (po.getSellerWeiid() != null && po.getSellerWeiid() == sweiNo) {
				    tempopo.setSellerWeiid(po.getSellerWeiid());
				    tempopo.setSellerUpweiid(po.getSellerUpweiid());
				    double pocount = po.getCount() == null ? 0 : po.getCount();
				    double pocommision = po.getCommision() == null ? 0 : po.getCommision();
				    tempopo.setCommision((pocount * pocommision) + tempopo.getCommision());
				}
			    }
			    sellAmoutList.add(tempopo);
			}
			for (OProductOrder item : sellAmoutList) {
			    if (power.isDeductCut()) {
				item.setCommision(item.getCommision() * ConfigSetting.batchHuoKuan);
			    }
			    String sellmobile = payOrderDAO.GetUserBindMobile(item.getSellerWeiid());
			    if (!ObjectUtil.isEmpty(sellmobile)) {
				String sellcomminss = String.format("%.2f", (item.getCommision() * ConfigSetting.yunSeller));
				String[] param = new String[] { sellmobile, item.getSellerWeiid().toString(), sellcomminss };
				SendSMSVO ssvo = new SendSMSVO();
				ssvo.setParam(param);
				ssvo.setWeiid(item.getSellerWeiid());
				ssvo.setVerify(VerifyCodeTypeEnum.SendSellerWeiid);
				sendList.add(ssvo);
				// SendSMSByMobile.sendSMS(sellmobile,
				// "您的微店" + item.getSellerWeiid() +
				// "成交了一个订单，您获得了" + sellcomminss +
				// "元佣金。加强推广，佣金不断。加油！下载微店app，推广更方便 http://app.okwei.com [微店网]");
			    }
			    String sellUpmobile = payOrderDAO.GetUserBindMobile(item.getSellerUpweiid());
			    if (!ObjectUtil.isEmpty(sellUpmobile)) {
				String sellcomminss = String.format("%.2f", (item.getCommision() * ConfigSetting.yunUpSeller));
				String[] param = new String[] { sellUpmobile, item.getSellerWeiid().toString(), sellcomminss };
				SendSMSVO ssvo = new SendSMSVO();
				ssvo.setParam(param);
				ssvo.setWeiid(item.getSellerWeiid());
				ssvo.setVerify(VerifyCodeTypeEnum.SendSellerUpWeiid);
				sendList.add(ssvo);
				// SendSMSByMobile.sendSMS(sellUpmobile,
				// "您的分销商（微店号：" + item.getSellerWeiid() +
				// "）成交一个订单，Ta为您赚得佣金" + sellcomminss +
				// "元（占总佣金的30%）。加强推广，分销商越多，佣金越多！下载微店app，推广更方便 http://app.okwei.com [微店网]");
			    }
			}
		    }
		    // 根据商品ID分组 统计商品数量 更新到商品辅助表
		    List<OProductOrder> productCountList = new ArrayList<OProductOrder>();
		    ArrayList<Long> proIds = new ArrayList<Long>();
		    for (OProductOrder p : supplyOIList) {
			proIds.add(p.getProductId());
		    }
		    proIds = RidRepeatByLong(proIds);
		    for (long productId : proIds) {
			int count = 0;
			for (OProductOrder p : supplyOIList) {
			    if (productId == p.getProductId()) {
				count += p.getCount() == null ? 0 : p.getCount();
			    }
			}
			PProductAssist pa = payOrderDAO.getPProductAssist(productId);
			if (pa != null) {
			    pa.setTotalCount(pa.getTotalCount() == null ? 0 : pa.getTotalCount() + count);
			} else {
			    PProducts pp = payOrderDAO.getProducts(productId);
			    if (pp != null) {
				pa = new PProductAssist();
				pa.setProductId(productId);
				pa.setClassId(pp.getClassId());
				pa.setSupplierId(pp.getSupplierWeiId());
				pa.setTotalCount(count);
			    }
			    payOrderDAO.insertPProductAssist(pa);
			}
		    }
		}
		// 如果总佣金 大于 总成交额 异常
		if (allComminssSum >= totalAmout) {
		    throw new RuntimeException("订单佣金大于订单成交金额！");
		}
		// 消息推送给供应商
		UPushMessage msg = new UPushMessage();
		msg.setReciptWeiId(oso.getSupplyerId());
		msg.setPushContent("销售订单：您有一笔订单采购商已支付，请尽快发货！");
		msg.setMsgType((short) 32);
		msg.setObjectId(oso.getSupplierOrderId());
		msg.setExtra(supplyOrderType);
		msg.setCreateTime(new Date());
		msg.setPushWeiId(111L);
		payOrderDAO.insertSendPushMsg(msg);
	    }
	    // 统一处理所有金额记录
	    InsertAmoutDeatils(amountList);
	}
	// 短信通知卖家 支付成功！
	String buyerMobile = payOrderDAO.GetUserBindMobile(order.getWeiId());
	if (!ObjectUtil.isEmpty(buyerMobile)) {
	    String[] param = new String[] { buyerMobile, order.getPayOrderId() };
	    SendSMSVO ssvo = new SendSMSVO();
	    ssvo.setParam(param);
	    ssvo.setWeiid(order.getWeiId());
	    ssvo.setVerify(VerifyCodeTypeEnum.SendWeiDeliver);
	    sendList.add(ssvo);
	    // SendSMSByMobile.sendSMS(buyerMobile, "亲爱的微粉，您的订单" +
	    // order.getPayOrderId() +
	    // "已经付款成功，供应商马上给您发货！下载微店app。订单信息早知道 http://app.okwei.com，感谢购买,祝您生活愉快！[微店网]");
	}
	// 批量处理发送短信
	if (sendList != null && sendList.size() > 0) {
	    SendSMSUtil smsUtil = new SendSMSUtil();
	    for (SendSMSVO vo : sendList) {
		smsUtil.sendSMS(vo.getParam(), vo.getVerify(), vo.getWeiid());
	    }
	}
    }

    public void InsertAmoutDeatils(List<UTradingDetails> modelList) throws RuntimeException {
	if (modelList == null || modelList.size() < 1) {
	    return;
	}
	// 根据用户处理 一共有多少个用户需要添加金额
	ArrayList<Long> weiIds = new ArrayList<Long>();
	for (UTradingDetails td : modelList) {
	    weiIds.add(td.getWeiId());
	}
	weiIds = RidRepeatByLong(weiIds);
	if (weiIds.size() > 0) {
	    for (Long weiid : weiIds) {
		if (weiid < 1) {
		    continue;
		}
		boolean isAdd = false; // 自己的钱包是否需要新增		
		// 获取该用户钱包
		UWallet wallet = payOrderDAO.getUWallet(weiid);
		if (wallet == null) {
		    wallet = new UWallet();
		    wallet.setBalance(0D);
		    wallet.setAccountIng(0D);
		    wallet.setAccountNot(0D);
		    wallet.setCreateTime(new Date());
		    wallet.setWeiId(weiid);
		    isAdd = true;
		}

		// 提取该用户的所有要添加的金额记录
		List<UTradingDetails> userAmoutList = new ArrayList<UTradingDetails>();
		for (UTradingDetails td : modelList) {
		    if (weiid.equals(td.getWeiId())) {
			userAmoutList.add(td);
		    }
		}
		if (userAmoutList != null && userAmoutList.size() > 0) {
		    // 遍历该用户的金额 统计微钱包变更 插入数据库
		    for (UTradingDetails item : userAmoutList) {
			// 正负金额 1分钱 过滤掉
			if (item.getAmount() < 0.01 && item.getAmount() > -0.01) {
			    continue;
			}
	
			wallet.setAccountNot((wallet.getAccountNot() == null ? 0 : wallet.getAccountNot()) + item.getAmount());

			if (!payOrderDAO.insertUTradingDatails(item)) {
			    throw new RuntimeException("添加用户金额失败/参数错误");
			}
		    }
		}
		// 自己的钱包是否加钱了 更新或插入
		// 更新 或 添加 用户微钱包数据

	    if (isAdd) {
			if (!payOrderDAO.insertUWallet(wallet)) {
			    throw new RuntimeException("更新用户微钱包数据提交失败");
			}
    	} else {
			if (!payOrderDAO.updateUWallet(wallet)) {
			    throw new RuntimeException("更新用户微钱包数据提交失败");
			}
	    }
	    }
	}
    }

    public ArrayList<Long> RidRepeatByLong(ArrayList<Long> list) {
	return new ArrayList<Long>(new LinkedHashSet<Long>(list));
    }

    @Override
    public OPayOrder getPayOrder(String orderNo) {
	return payOrderDAO.getOPayOrder(orderNo);
    }

    @Override
    public UWallet getWallet(long weiid) {
	UWallet wallet = payOrderDAO.getUWallet(weiid);
	return wallet;
    }

    @Override
    public UWeiSeller getWeiSeller(long weiid) {
	return payOrderDAO.getWeiSeller(weiid);
    }

    @Override
    public boolean insertTVerificationId(TVerificationId tv) {
	return payOrderDAO.insertTVerificationId(tv);
    }

    @Override
    public PayResult verifilyOrder(String orderNo, long weiid,boolean isGoBank) {
	PayResult result = new PayResult();
	result.setOrderNo(orderNo);// 设置返回的订单号
	if (ObjectUtil.isEmpty(orderNo)) {
	    result.setMessage("支付的订单号不能为空");
	    result.setState(PayResultStateEnum.Failure);
	    result.setType(1);
	    return result;
	}

	OPayOrder payOrder = payOrderDAO.getOPayOrder(orderNo);// 获取支付订单实体
	if (payOrder == null) {
	    result.setMessage("订单不存在！");
	    result.setState(PayResultStateEnum.Failure);
	    result.setType(2);
	    return result;
	}
	if (payOrder.getWeiId() != weiid) {
	    result.setMessage("不能支付别人的订单");
	    result.setState(PayResultStateEnum.Failure);
	    result.setType(3);
	    return result;
	}
	if (payOrder.getTotalPrice() < 0.01) {
	    result.setMessage("该订单金额异常！");
	    result.setState(PayResultStateEnum.Failure);
	    result.setType(4);
	    return result;
	}
	// 判断是否是未支付的订单
	if (!OrderStatusEnum.Nopay.toString().equals(payOrder.getState().toString()) &&
			!OrderStatusEnum.Tovoided.toString().equals(payOrder.getState().toString()) ) {
	    result.setMessage("该订单已经支付成功！不能重复处理！");
	    result.setState(PayResultStateEnum.Failure);
	    result.setType(5);
	    return result;
	}
	
	// ***** 订单金额验证 *****//
	if (OrderTypeEnum.BatchRzy.toString().equals(payOrder.getTypeState().toString())) {
	    String batchPortServerFee = AppSettingUtil.getSingleValue("BatchPortServerFee");
	    if (payOrder.getTotalPrice() != Double.parseDouble(batchPortServerFee)) {
		result.setMessage("批发号供应商进驻保证金金额不正确！");
		result.setState(PayResultStateEnum.Failure);
		result.setType(6);
		return result;

	    }
	} else if (OrderTypeEnum.BatchGys.toString().equals(payOrder.getTypeState().toString())) {
	    String batchSupplyerBond = AppSettingUtil.getSingleValue("BatchSupplyerBond");
	    if (payOrder.getTotalPrice() != Double.parseDouble(batchSupplyerBond)) {
		result.setMessage("批发号供应商成为认证点服务金额不正确！");
		result.setState(PayResultStateEnum.Failure);
		result.setType(7);
		return result;

	    }
	} else if (OrderTypeEnum.GysAndVerifier.toString().equals(payOrder.getTypeState().toString())) {
	    String gysAndVerifierServerFee = AppSettingUtil.getSingleValue("GysAndVerifierServerFee");
	    if (payOrder.getTotalPrice() != Double.parseDouble(gysAndVerifierServerFee)) {
		result.setMessage("批发号供应商进驻升级认证点服务套餐金额不正确！");
		result.setState(PayResultStateEnum.Failure);
		result.setType(8);
		return result;

	    }
	}
	//如果是调起支付前 校验订单 锁定供应商订单
	if(isGoBank){
		if(payOrder.getState().equals(Short.parseShort(OrderStatusEnum.Tovoided.toString()))){
			result.setMessage("该订单已过期！");
			result.setState(PayResultStateEnum.Failure);
			result.setType(0);
			return result;
		}
		
		//校验供应商订单
		String orderTypeStr = payOrder.getTypeState().toString();
		if (OrderTypeEnum.Pt.toString().equals(orderTypeStr) 
				|| OrderTypeEnum.BatchOrder.toString().equals(orderTypeStr) 
				|| OrderTypeEnum.BatchWholesale.toString().equals(orderTypeStr) 
				|| OrderTypeEnum.BookHeadOrder.toString().equals(orderTypeStr) 
				|| OrderTypeEnum.BookTailOrder.toString().equals(orderTypeStr) 
				|| OrderTypeEnum.BookFullOrder.toString().equals(orderTypeStr)) {
			// 获取该订单下所有供应商订单
			List<OSupplyerOrder> supplyerOrderList =getSupplyerOrderList(payOrder);
			//供应商订单数据异常
			if(supplyerOrderList ==null || supplyerOrderList.size() <1){
				result.setMessage("供应商订单数据异常,请重新支付！");
				result.setState(PayResultStateEnum.Failure);
				result.setType(0);
				return result;
			}
			//供应商订单 金额与支付订单 金额不一致
			double totalAmout =0;
			for (OSupplyerOrder supplyerOrder : supplyerOrderList) {
				totalAmout += supplyerOrder.getTotalPrice() + supplyerOrder.getPostage();
				}
			if(payOrder.getTotalPrice() != totalAmout){
				result.setMessage("该支付订单金额与供应商订单金额不一致,请重新支付！");
				result.setState(PayResultStateEnum.Failure);
				result.setType(0);
				return result;
			}	
		}
		DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		logger.error("支付调起 订单号:" + payOrder.getPayOrderId() + "时间:" + format.format(new Date()));
		lockSupplyOrder(payOrder);
	}
	
	double resultPrice = payOrder.getTotalPrice() - (payOrder.getWalletmoney() == null ? 0 : payOrder.getWalletmoney()) -(payOrder.getWeiDianCoin() == null ? 0:payOrder.getWeiDianCoin());
	
	//是否使用代金券
	OPayOrderExtend extend=super.getById(OPayOrderExtend.class, payOrder.getPayOrderId());
	if(extend!=null&&extend.getCoinAmount()!=null&&extend.getCoinAmount()>0){
		resultPrice=resultPrice-extend.getCoinAmount();
	}
	
	result.setMessage(String.valueOf(new java.math.BigDecimal(Double.toString(resultPrice)).setScale(2,java.math.BigDecimal.ROUND_HALF_UP).doubleValue())); // 订单金额
	result.setState(PayResultStateEnum.Success);
	result.setType(0);
	return result;
    }

    @Override
    public OPayOrder getPayOrderUpWallet(String orderNo) {
	if (ObjectUtil.isEmpty(orderNo)) {
	    return null;
	}
	OPayOrder order = payOrderDAO.getOPayOrder(orderNo);
	if (order != null)
	    order.setWalletmoney(0D);
	return order;
    }

    @Override
    public boolean updateOPayOrder(OPayOrder order) {
	return payOrderDAO.updateOPayOrder(order);
    }

    @Override
    public List<UBankCard> getBankCardList(long weiid) {
	// List<UBankCard> list = payOrderDAO.getBankCardList(weiid);
	// List<UPublicBanks> pbList = payOrderDAO.getPublicBanks(weiid);
	// //銀行卡和對公賬戶都為空
	// if((list==null || list.size() < 1) && (pbList == null ||
	// pbList.size() < 1)){
	// return null;
	// }
	// //如果银行卡为空
	// if(list ==null || list.size()<1){
	// list = new ArrayList<UBankCard>();
	// }
	// //如果对公账户不为空
	// if(pbList!=null &&pbList.size() > 0){
	// for(UPublicBanks pb : pbList){
	// UBankCard bc = new UBankCard();
	// bc.setBanckName("对公账户  " + pb.getBanckName());//账户名称
	// bc.setId(pb.getPid().longValue());//账户主键
	// bc.setBanckCard(pb.getBanckNo());//账号号码
	// list.add(bc);
	// }
	// }
	// else {
	// return list;
	// }

	return payOrderDAO.getBankCardList(weiid);
    }

    @Override
    public UBankCard getBankCard(long cardId) {
	return payOrderDAO.getBankCard(cardId);
    }

    @Override
    public boolean insertTSmsverification(TSmsverification ts) {
	return payOrderDAO.insertTSmsverification(ts);
    }

    @Override
    public boolean insertPayOrder(OPayOrder order) {

	return payOrderDAO.insertPayOrder(order);
    }

    @Override
    public String verifySMS(String paypassword, String code, String orderNo, LoginUser user,String paytype) {
	ResultMsg result = new ResultMsg();
	OPayOrder payOrder = payOrderDAO.getOPayOrder(orderNo);
	UWallet wallet = payOrderDAO.getUWallet(user.getWeiID());
	UWeiSeller seller = payOrderDAO.getWeiSeller(user.getWeiID());
	// 1.先判断用户是否实名认证
	if (wallet == null || wallet.getStatus() == null || wallet.getStatus() != 1) {// 0或空：未实名认证；1：已实名认证
	    result.setMsg("用户未进行实名认证");
	    result.setStatus(-2);
	    return JsonUtil.objectToJson(result);// 用户未进行实名认证
	}
	// 2.判断支付订单是否有问题
	if (payOrder == null || user.getWeiID().longValue() != payOrder.getWeiId().longValue()) {
	    result.setStatus(-8);
	    result.setMsg("订单不存在/不是您下的订单");
	    return JsonUtil.objectToJson(result);
	}
	// 3.微钱包金额是否合法
	if (paytype.contains("1")&&(wallet.getBalance() == null || wallet.getBalance() < 0.01)) {
	    result.setStatus(-12);
	    result.setMsg("微钱余额不足");
	    return JsonUtil.objectToJson(result);
	}
	// 4.微金币金额是否合法
	if (paytype.contains("2")&&(wallet.getWeiDianCoin() == null || wallet.getWeiDianCoin() < 0.01)) {
	    result.setStatus(-12);
	    result.setMsg("微金币余额不足");
	    return JsonUtil.objectToJson(result);
	}
	if(paytype.contains("2") && (payOrder.getTypeState()==Short.parseShort(OrderTypeEnum.BatchGys.toString())
			|| payOrder.getTypeState()==Short.parseShort(OrderTypeEnum.BatchPortNoServiceFee.toString())
			|| payOrder.getTypeState()==Short.parseShort(OrderTypeEnum.BatchRzy.toString())
			|| payOrder.getTypeState()==Short.parseShort(OrderTypeEnum.ChongZhi.toString())
			|| payOrder.getTypeState()==Short.parseShort(OrderTypeEnum.DaoHang.toString())
			|| payOrder.getTypeState()==Short.parseShort(OrderTypeEnum.GysAndVerifier.toString())
			|| payOrder.getTypeState()==Short.parseShort(OrderTypeEnum.Reward.toString())
			|| payOrder.getTypeState()==Short.parseShort(OrderTypeEnum.YunGys.toString())
			|| payOrder.getTypeState()==Short.parseShort(OrderTypeEnum.YunGysNoServiceFee.toString())
			|| payOrder.getTypeState()==Short.parseShort(OrderTypeEnum.YunRzy.toString()))){
		result.setStatus(-13);
	    result.setMsg("微金币只能用于购物！");
	    return JsonUtil.objectToJson(result);
	}
	Double walletAmout = 0D;// 默认微钱包扣款为0
	Double weicoinAmout=0D;//默认微金币扣款为0
	//先取总价出来
	Double totalprice=payOrder.getTotalPrice();
	if(paytype.equals("12")) //先选择了微钱包支付后选择微金币支付
	{
		if (wallet.getBalance() >= totalprice) {
		    walletAmout = totalprice;// 如果微钱包的余额大于需要支付的订单金额，支付的微钱包为订单金额总价
		} else {
		    walletAmout = wallet.getBalance();// 微钱包金额不足于支付订单是，微钱包有多少支付多少
		    if(wallet.getWeiDianCoin()>=(totalprice-walletAmout))
		    {
		    	weicoinAmout=totalprice-walletAmout;
		    }
		    else
		    {
		    	weicoinAmout=wallet.getWeiDianCoin();
		    }
		}
		
	}
	if(paytype.equals("21")) //先选择了微金币支付后选择微钱包支付
	{
		if(wallet.getWeiDianCoin()>=totalprice)
		{
			weicoinAmout=totalprice;
		} else {
			weicoinAmout=wallet.getWeiDianCoin();
			if(wallet.getBalance()>=(totalprice-weicoinAmout)){
				walletAmout=totalprice-weicoinAmout;
			}else {
				walletAmout=wallet.getBalance();
			}
		}
		
	}
	if(paytype.equals("1") || paytype.equals("11")) //选择了微钱包支付
	{
		if (wallet.getBalance() >= totalprice) {
		    walletAmout = totalprice;// 如果微钱包的余额大于需要支付的订单金额，支付的微钱包为订单金额总价
		} else {
		    walletAmout = wallet.getBalance();// 微钱包金额不足于支付订单是，微钱包有多少支付多少
		}
	}
	if(paytype.equals("2") || paytype.equals("22")) //选择了微金币支付
	{
		if(wallet.getWeiDianCoin()>=totalprice)
			weicoinAmout=totalprice;
		else
			weicoinAmout=wallet.getWeiDianCoin();
	}
//	if (wallet.getBalance() >= payOrder.getTotalPrice()) {
//	    walletAmout = payOrder.getTotalPrice();// 如果微钱包的余额大于需要支付的订单金额，支付的微钱包为订单金额总价
//	} else {
//	    walletAmout = wallet.getBalance();// 微钱包金额不足于支付订单是，微钱包有多少支付多少
//	}
	boolean isyzphone = false;// 是否要验证手机验证码
	if (walletAmout < 0.01 && weicoinAmout<0.01) {
	    result.setStatus(-11);
	    result.setMsg("扣除微钱包或微金币金额有误");
	    return JsonUtil.objectToJson(result);
	}
	if (walletAmout != null && walletAmout >= 200) {
	    isyzphone = true;// 需要验证手机验证码
	}
	if (ObjectUtil.isEmpty(paypassword)) {
	    result.setMsg("支付密码不能为空");
	    result.setStatus(-1);
	    return JsonUtil.objectToJson(result);
	}
	Object objectPassCount = RedisUtil.getObject("pay_pass_count_code_" + String.valueOf(user.getWeiID()));// 尝试的次数
	int passCount = Integer.parseInt(objectPassCount == null ? "0" : objectPassCount.toString());
	if (passCount > 30) {
	    RedisUtil.setObject("pay_pass_count_code_" + String.valueOf(user.getWeiID()), passCount + 1, 600);// 尝试的次数
	    result.setMsg("支付密码验证太过多，请十分钟之后再试");
	    result.setStatus(-13);
	    return JsonUtil.objectToJson(result);
	}
	paypassword = MD5Encrypt.encrypt(paypassword);// 加密
	if (!paypassword.equals(wallet.getPayPassword())) {
	    result.setMsg("支付密码错误");
	    result.setStatus(-3);
	    RedisUtil.setObject("pay_pass_count_code_" + String.valueOf(user.getWeiID()), passCount + 1, 600);// 尝试的次数
	    return JsonUtil.objectToJson(result);// 支付密码错误/验证码错误******（次数问题）
	}
	RedisUtil.delete("pay_pass_count_code_" + String.valueOf(user.getWeiID()));// 删除错误此时
	// 1.验证手机验证码
	if (isyzphone) {
		    // 1.1验证码是否为空
		    if (ObjectUtil.isEmpty(code)) {
			result.setMsg("手机验证码不能为空");
			result.setStatus(-9);
			return JsonUtil.objectToJson(result);// 手机验证码不能为空
		    }
		    // 1.3是否绑定手机号码
		    if (seller == null || seller.getMobileIsBind() != Short.parseShort(MobileBindEnum.Bind.toString())) {
			result.setMsg("用户没有绑定手机号码");
			result.setStatus(-4);
			return JsonUtil.objectToJson(result);// 用户没有绑定手机号码
		    }
		    // 1.4验证码尝试次数
		    Object objectBCount = RedisUtil.getObject("pay_count_code_" + String.valueOf(user.getWeiID()));// 尝试的次数
		    int bcount = 0;
		    if (objectBCount != null) {
			bcount = Integer.parseInt(objectBCount.toString());// 获取尝试次数
			if (bcount >= 5) {
			    RedisUtil.delete("pay_send_code_" + String.valueOf(user.getWeiID()));// 删除当前验证码
			    result.setMsg("请求频繁，稍后再试");
			    result.setStatus(-6);
			    return JsonUtil.objectToJson(result);// 请求频繁，稍后再试
			}
			bcount++;
		    }
		    // 1.5验证手机验证码
		    Object objectCode = RedisUtil.getObject("pay_send_code_" + String.valueOf(user.getWeiID()));// 获取验证码
		    if (objectCode == null) {
			result.setMsg("验证码过期，请重新发送");
			result.setStatus(-5);
			return JsonUtil.objectToJson(result);// 验证码过期，请重新发送
		    }
		    RedisUtil.setObject("pay_count_code_" + String.valueOf(user.getWeiID()), bcount, 300);// 设置次数,五分钟
		    if (code.equals(objectCode.toString())) {// 验证成功
				RedisUtil.delete("pay_send_code_" + String.valueOf(user.getWeiID()));// 删除当前验证码
				RedisUtil.delete("pay_count_code_" + String.valueOf(user.getWeiID()));// 删除当前验证码次数
				payOrder.setWalletmoney(walletAmout);// 设置微钱包支付的金额
				payOrder.setWeiDianCoin(weicoinAmout);//设置微金币支付的金额
	        } else {
	                result.setMsg("验证码错误");
	                result.setStatus(-7);
	                return JsonUtil.objectToJson(result);// 验证码错误
	            }
        }
        payOrder.setWalletmoney(walletAmout);
        payOrder.setWeiDianCoin(weicoinAmout);//设置微金币支付的金额
        if (updateOPayOrder(payOrder)) {
            result.setMsg("成功");
            result.setStatus(1);
	        if (payOrder.getTotalPrice() == walletAmout && walletAmout>0) { //钱包支付
	        	
	        	PayResult payResult = OrderPaymentSuccessTwo(orderNo,payOrder.getTotalPrice(),PayTypeEnum.WeiWallet,payOrder);
				if(payResult.getState() ==PayResultStateEnum.Success)
				{
//					payResult = orderDataProcessing(orderNo, PayTypeEnum.WeiWallet);
					BResultMsg resultMsg = payService.editOrderDataProcess(payOrder, PayTypeEnum.WeiWallet);
					result.setStatus(result.getStatus());
					result.setMsg(resultMsg.getMsg()); 
				}		
				logger.error("微钱包" + payResult.getOrderNo() + "|" + payResult.getMessage());      	
	            //PayResult pr = OrderPaymentSuccess(payOrder, payOrder.getTotalPrice(), PayTypeEnum.WeiWallet,"","");
	            if (payResult.getState() != PayResultStateEnum.Success) {
		            result.setMsg(payResult.getMessage());//处理失败
		            result.setStatus(-10);
	            }
	        }
	        else if (payOrder.getTotalPrice() == weicoinAmout && weicoinAmout>0) { //微金币支付
	        	
	        	PayResult payResult = OrderPaymentSuccessTwo(orderNo,payOrder.getTotalPrice(),PayTypeEnum.WeiCoinPay,payOrder);
				if(payResult.getState() ==PayResultStateEnum.Success)
				{
//					payResult = orderDataProcessing(orderNo, PayTypeEnum.WeiWallet);
					BResultMsg resultMsg = payService.editOrderDataProcess(payOrder, PayTypeEnum.WeiCoinPay);
					result.setStatus(result.getStatus());
					result.setMsg(resultMsg.getMsg()); 
				}		
				logger.error("微金币" + payResult.getOrderNo() + "|" + payResult.getMessage());      	
	            //PayResult pr = OrderPaymentSuccess(payOrder, payOrder.getTotalPrice(), PayTypeEnum.WeiWallet,"","");
	            if (payResult.getState() != PayResultStateEnum.Success) {
		            result.setMsg(payResult.getMessage());//处理失败
		            result.setStatus(-10);
	            }
	        }
	        else if(payOrder.getTotalPrice() == (weicoinAmout+walletAmout)) //微金币和微钱包混合支付
	        {
	        	PayResult payResult = OrderPaymentSuccessTwo(orderNo,payOrder.getTotalPrice(),PayTypeEnum.WalletCoinPay,payOrder);
				if(payResult.getState() ==PayResultStateEnum.Success)
				{
//					payResult = orderDataProcessing(orderNo, PayTypeEnum.WeiWallet);
					BResultMsg resultMsg = payService.editOrderDataProcess(payOrder, PayTypeEnum.WalletCoinPay);
					result.setStatus(result.getStatus());
					result.setMsg(resultMsg.getMsg()); 
				}		
				logger.error("微金币" + payResult.getOrderNo() + "|" + payResult.getMessage());      	
	            //PayResult pr = OrderPaymentSuccess(payOrder, payOrder.getTotalPrice(), PayTypeEnum.WeiWallet,"","");
	            if (payResult.getState() != PayResultStateEnum.Success) {
		            result.setMsg(payResult.getMessage());//处理失败
		            result.setStatus(-10);
	            }
	        }
        }
        return JsonUtil.objectToJson(result); 

    }


	@Override
	public String getLastPayOrderID(OPayOrder payOrder) {
		//如果是普通订单 
		String orderTypeStr = payOrder.getTypeState().toString();
		if (OrderTypeEnum.Pt.toString().equals(orderTypeStr) 
				|| OrderTypeEnum.BatchOrder.toString().equals(orderTypeStr) 
				|| OrderTypeEnum.BatchWholesale.toString().equals(orderTypeStr) 
				|| OrderTypeEnum.BookHeadOrder.toString().equals(orderTypeStr) 
				|| OrderTypeEnum.BookTailOrder.toString().equals(orderTypeStr) 
				|| OrderTypeEnum.BookFullOrder.toString().equals(orderTypeStr)) {
			OPayOrderLog orderLog = payOrderDAO.get(OPayOrderLog.class, payOrder.getPayOrderId());
			if(orderLog !=null){
				OPayOrderLog log = payOrderDAO.getLastLog(orderLog.getSupplyOrderIds());				
				if(log !=null && !log.getPayOrderId().equals(payOrder.getPayOrderId())){
					return log.getPayOrderId();
				}else{
					return null;
				}
			}
		}
		return payOrder.getPayOrderId();
	}

	@Override
	public void lockSupplyOrder(OPayOrder payOrder) {
		List<OSupplyerOrder> supplyerOrderList =getSupplyerOrderList(payOrder);
		if(supplyerOrderList !=null && supplyerOrderList.size() >0){
			for (OSupplyerOrder oSupplyerOrder : supplyerOrderList) {
				RedisUtil.setString("PayToBank_"+oSupplyerOrder.getSupplierOrderId(), oSupplyerOrder.getSupplierOrderId(), 300);
			}
		}	
	}
	
	@Override
	public void openLockSupplyOrder(OPayOrder payOrder){
		List<OSupplyerOrder> supplyerOrderList =getSupplyerOrderList(payOrder);
		if(supplyerOrderList !=null && supplyerOrderList.size() >0){
			for (OSupplyerOrder oSupplyerOrder : supplyerOrderList) {				
				RedisUtil.delete("PayToBank_"+oSupplyerOrder.getSupplierOrderId());
			}
		}
	}

	@Override
	public List<OSupplyerOrder> getSupplyerOrderList(OPayOrder payOrder) {
		String orderTypeStr = payOrder.getTypeState().toString();

		// 获取该订单下所有供应商订单
		List<OSupplyerOrder> supplyerOrderList = new ArrayList<OSupplyerOrder>();
		// 预订订单首款，预订订单尾款，预订订单全款
		if (OrderTypeEnum.BookHeadOrder.toString().equals(orderTypeStr) 
				|| OrderTypeEnum.BookTailOrder.toString().equals(orderTypeStr)
				|| OrderTypeEnum.BookFullOrder.toString().equals(orderTypeStr)) {
		    supplyerOrderList = payOrderDAO.getListBySupplyOrderID(payOrder.getSupplierOrder());
		} else {
		    supplyerOrderList = payOrderDAO.getListByPayOrderID(payOrder.getPayOrderId());
		}	
		
		return supplyerOrderList;
	}


    @Override
    public ResultMsg getIszfs(String orderNo) {
	ResultMsg result = new ResultMsg();
	result.setStatus(0);
	OPayOrder payOrder = payOrderDAO.getOPayOrder(orderNo);
	if (payOrder != null) {
	    // 判断是否支付成功
	    if (payOrder.getState().intValue() == 1) {
		result.setStatus(2);// 支付成功
	    } else {
		// 查询供应商订单是否有支付锁
		List<OSupplyerOrder> list = payOrderDAO.getListByPayOrderID(orderNo);
		if (list != null && list.size() > 0) {
		    for (OSupplyerOrder supOrder : list) {
			String keyString = RedisUtil.getString("PayToBank_" + supOrder.getSupplierOrderId());
			if (keyString != null && keyString != "") {
			    result.setStatus(1);
			    break;
			}
		    }
		}
	    }
	}
	return result;
    }
    /**
	 * 
	 * @param weiid
	 * @param productId
	 * @param type
	 * @return
	 */
	public int isShop(long weiid,long productId){
		int userIdentity = 0;
		String hqlString = " from UDemandProduct u where u.productId=? ";
		Object[] bb = new Object[1];
		bb[0] = productId;
		UDemandProduct demandMod = null;
		List<UDemandProduct> find = payOrderDAO.find(hqlString, bb);
		if (find!=null&&find.size()>0) {
			demandMod=find.get(0);
		} 
		if (demandMod != null) {
			String hql_u = " from USupplyChannel u where u.demandId=? and u.weiId=? ";
			Object[] cc = new Object[2];
			cc[0] = demandMod.getDemandId();
			cc[1] = weiid;
			List<USupplyChannel> channels = payOrderDAO.find(hql_u, cc);
			if (channels != null && channels.size() > 0) {
				for (USupplyChannel uu : channels) {
					if (uu.getChannelType() != null && uu.getChannelType() == 2) {
						if(uu.getState().shortValue()==1){
							userIdentity+=Integer.parseInt(UserIdentityType.Ground.toString());
						}
					} else if (uu.getChannelType() != null && uu.getChannelType() == 1) {
						if(uu.getState().shortValue()==1){
							userIdentity+=Integer.parseInt(UserIdentityType.Agent.toString());
						}
					}
				}
			}
		}
		return userIdentity;
	}
	@Override
	public boolean checkAppNotify(String tiket, String orderNo,Integer notifyType,PayTypeEnum payType) {
		if(tiket==null || "".equals(tiket) || orderNo==null || "".equals(orderNo) || notifyType ==null){
			return false;
		}
		logger.error("检验APP支付成功通知"+orderNo + ":"+tiket+":"+notifyType+":"+payType);
		//登录
        Object userObject = RedisUtil.getObject(tiket + "_IBS");
        //logger.error("检验APP支付成功通知获取redis");
        if(userObject == null)// 如果存在
        {
        	logger.error("检验APP支付成功通知"+tiket +"未能获取到redis");
        	return false;
        }
        //订单必须存在 且只能是订单购买人 查询
        LoginUser user = (LoginUser) userObject;
		OPayOrder payOrder = getPayOrder(orderNo);
		if(payOrder ==null || !user.getWeiID().equals(payOrder.getWeiId())){
			logger.error("检验APP支付成功通知redis中用户与订单用户不同");
			return false;
		}
		if(notifyType !=1){
			//支付解锁
			openLockSupplyOrder(payOrder);
			return false;
		}
		logger.error("检验APP支付成功通知"+orderNo + ":"+tiket +":"+user.getWeiID());
		return true;
	}
	@Override
	public Date getExpirationTime(String orderNo) {
		List<OSupplyerOrder> list=payOrderDAO.getListByPayOrderID(orderNo);
		Date date=new Date();
		boolean flag=false;
		for(OSupplyerOrder oo:list)
		{
			if(oo.getIsActivity()==1)
			{
				flag=true;
			}
			if(oo.getOrderTime().getTime()<date.getTime())
			{
				date=oo.getOrderTime();
			}
		}
		if(flag)
		{
			return DateUtils.addMinute(date, 10);
		}
		else
		{
			return null;
		}
	}

}
