package com.okwei.service.impl.order;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.okwei.bean.domain.DBrandSupplier;
import com.okwei.bean.domain.OOrderFlow;
import com.okwei.bean.domain.OPayOrder;
import com.okwei.bean.domain.OPayOrderLog;
import com.okwei.bean.domain.OProductOrder;
import com.okwei.bean.domain.OSupplyerOrder;
import com.okwei.bean.domain.PProductStyles;
import com.okwei.bean.domain.UPushMessage;
import com.okwei.bean.enums.OrderStatusEnum;
import com.okwei.bean.enums.PushMessageType;
import com.okwei.bean.enums.UserIdentityType;
import com.okwei.bean.vo.ReturnModel;
import com.okwei.bean.vo.ReturnStatus;
import com.okwei.bean.vo.order.UnitPrice;
import com.okwei.dao.IBaseDAO;
import com.okwei.dao.agent.IDAgentMgtDao;
import com.okwei.dao.order.IBasicOrdersDao;
import com.okwei.service.impl.BaseService;
import com.okwei.service.order.IBaseOrderMgtService;
import com.okwei.util.BitOperation;
import com.okwei.util.GenerateOrderNum;
import com.okwei.util.ObjectUtil;
import com.okwei.util.SendPushMessage;

@Service
public class BaseOrderMgtService extends BaseService implements IBaseOrderMgtService{
	@Autowired
	private IBaseDAO baseDAO;
	@Autowired
	private IDAgentMgtDao agentDao;
	@Autowired
	private IBasicOrdersDao ordersDao;
	
	public ReturnModel updatePostUnitPrice(long WeiID, String param) {
		ReturnModel Retatus = new ReturnModel();
		OProductOrder ProductOrder = new OProductOrder();
		double Postage = 0.0;
		String hql = "";
		String ProductOrderID = "-1";
		
		List<OSupplyerOrder> SupplyerOrderList = new ArrayList<OSupplyerOrder>();
		List<OProductOrder> ProductOrderList = new ArrayList<OProductOrder>();
		Double AllTotalPrice = (double) 0;
		double SupplyerTPrice = 0.0;
		String supplierOrderId = "";
		
		OSupplyerOrder SupplyerOrder = new OSupplyerOrder();
		List<UnitPrice> PList = new ArrayList<UnitPrice>();
		double allCommision = 0.0;
		JSONObject obj;
		try {
			obj = JSONObject.fromObject(param);
		} catch (Exception e) {
			Retatus.setStatu(ReturnStatus.ParamError);
			Retatus.setStatusreson("json串错误！");
			return Retatus;
		}
		if (obj == null) {
			Retatus.setStatu(ReturnStatus.ParamError);
			Retatus.setStatusreson("json串错误！转换结果为null");
			return Retatus;
		}
		/**
		 * 是否修改邮费
		 */
		String PState = String.valueOf(obj.get("pState"));
		/**
		 * 供应商订单
		 */
		supplierOrderId = String.valueOf( obj.get("supplierOrderId"));
		/**
		 * 需要修改的单价
		 */
		try {
			String svalue = obj.getString("PList");
			JSONArray arrayvalue = new JSONArray().fromObject(svalue);
			for (int j = 0; j < arrayvalue.size(); j++) // 遍历value
			{
				JSONObject jovalue = (JSONObject) arrayvalue.get(j);
				UnitPrice UP = new UnitPrice();
				String pid = jovalue.getString("productOrderId");
				Double pprice = jovalue.getDouble("price");
				if (pid == null || "".equals(pid)) {
					Retatus.setStatu(ReturnStatus.ParamError);
					Retatus.setStatusreson("产品订单不能为空!");
					return Retatus;
				}
				if (pprice == null) {
					Retatus.setStatu(ReturnStatus.ParamError);
					Retatus.setStatusreson("产品价格不能为空!");
					return Retatus;
				}
				UP.setPrice(pprice);
				UP.setProductOrderID(pid);
				PList.add(UP);
			}
		} catch (Exception e) {
			Retatus.setStatu(ReturnStatus.ParamError);
			Retatus.setStatusreson("json串单价list错误");
			return Retatus;
		}
		if (PList.size() > 0) {
			for (int i = 0; i < PList.size(); i++) {
				UnitPrice item = PList.get(i);
				if (item.getPrice() == null) {
					Retatus.setStatu(ReturnStatus.ParamError);
					Retatus.setStatusreson("单价不能为空!");
					return Retatus;
				}
				if (item.getPrice() <= 0) {
					Retatus.setStatu(ReturnStatus.ParamError);
					Retatus.setStatusreson("单价不能小于0!");
					return Retatus;
				}
				if (item.getProductOrderID() == null || item.getProductOrderID().equals("")) {
					Retatus.setStatu(ReturnStatus.ParamError);
					Retatus.setStatusreson("产品订单不能为空!");
					return Retatus;
				}
				ProductOrderID = item.getProductOrderID();
				hql = "from OProductOrder p where p.productOrderId = ? and p.supplyeriId=?";
				Object[] b1 = new Object[2];
				b1[0] = ProductOrderID;
				b1[1] = WeiID;
				ProductOrder = baseDAO.getNotUniqueResultByHql(hql, b1);
				if (ProductOrder != null) {
					if (ProductOrder.getSupplyeriId() == null || "".equals(ProductOrder.getSupplyeriId())) {
						Retatus.setStatu(ReturnStatus.DataError);
						Retatus.setStatusreson("产品的供应商ID不能为空！");
						return Retatus;
					}
					if (ProductOrder.getSupplierOrderId() == null || "".equals(ProductOrder.getSupplierOrderId())) {
						Retatus.setStatu(ReturnStatus.DataError);
						Retatus.setStatusreson("产品的供应商订单不能为空！");
						return Retatus;
					}
					if (ProductOrder.getSupplyeriId() != WeiID) {
						Retatus.setStatu(ReturnStatus.ParamError);
						Retatus.setStatusreson("当前登录不是该卖家，不可以修改价格！");
						return Retatus;
					} else {
						if (!ProductOrder.getSupplierOrderId().equals(supplierOrderId)) {
							Retatus.setStatu(ReturnStatus.ParamError);
							Retatus.setStatusreson("供应商订单不配配");
							return Retatus;
						}
					}
				} else {
					Retatus.setStatu(ReturnStatus.ParamError);
					Retatus.setStatusreson("你没有产品订单！");
					return Retatus;
				}
			}
			SupplyerOrder = baseDAO.getUniqueResultByHql("from OSupplyerOrder p where p.supplierOrderId = ? and p.supplyerId=?", supplierOrderId,WeiID);
			if (SupplyerOrder == null) {
				Retatus.setStatu(ReturnStatus.ParamError);
				Retatus.setStatusreson("没有供应商订单");
				return Retatus;
			}
			// 判断支付订单是否存在
			if (SupplyerOrder.getPayOrderId() == null || SupplyerOrder.getPayOrderId().equals("")) {
				Retatus.setStatu(ReturnStatus.ParamError);
				Retatus.setStatusreson("没有该支付订单");
				return Retatus;
			}
			if (!SupplyerOrder.getSupplyerId().equals(WeiID)) {
				Retatus.setStatu(ReturnStatus.ParamError);
				Retatus.setStatusreson("该支付订单不是你的不可以修改！");
				return Retatus;
			}
			short sOState = -1;
			sOState = SupplyerOrder.getState() != null ? SupplyerOrder.getState() : -1;
			if (sOState != Short.parseShort(OrderStatusEnum.Nopay.toString())) {
				Retatus.setStatu(ReturnStatus.ParamError);
				Retatus.setStatusreson("该订单必须是未支付订单！");
				return Retatus;
			}
			/**
			 * 自定义邮费
			 */
			if (PState.equals("1")) {
				/**
				 * 运费
				 */
				try {
					Postage = obj.getDouble("postage");
				} catch (Exception e) {
					Retatus.setStatu(ReturnStatus.ParamError);
					Retatus.setStatusreson("json串postage错误");
					return Retatus;
				}
			}
			else {
				/**
				 * 免邮费
				 */
				Postage = 0.0;
			}
			if (Postage < 0) {
				Retatus.setStatu(ReturnStatus.ParamError);
				Retatus.setStatusreson("邮费不能小于0");
				return Retatus;
			}
			// 查询原始供应商的产品列表
			ProductOrderList = baseDAO.find("from OProductOrder p where p.supplierOrderId = ? and p.supplyeriId=?", supplierOrderId,WeiID);
			if (ProductOrderList == null || ProductOrderList.size() == 0) {
				Retatus.setStatu(ReturnStatus.ParamError);
				Retatus.setStatusreson("供应商的产品订单为空！");
				return Retatus;
			}
			for (int i = 0; i < PList.size(); i++) {
				UnitPrice ModifyPriceitem = PList.get(i);
				for (int j = 0; j < ProductOrderList.size(); j++) {
					OProductOrder OProductOrderItem = ProductOrderList.get(j);
					if (ModifyPriceitem.getProductOrderID().equals(OProductOrderItem.getProductOrderId())) {
						OProductOrderItem.setPrice(ModifyPriceitem.getPrice());
						ProductOrderList.set(j, OProductOrderItem);
					}
				}
			}
			// 计算供应商的总价 和佣金
			for (OProductOrder PTprice : ProductOrderList) {
				if (PTprice.getCount() == null) {
					Retatus.setStatu(ReturnStatus.ParamError);
					Retatus.setStatusreson("产品订单个数为空！");
					return Retatus;
				}
				if (PTprice.getPrice() == null) {
					Retatus.setStatu(ReturnStatus.ParamError);
					Retatus.setStatusreson("产品订单单价为空！");
					return Retatus;
				}
				SupplyerTPrice += (PTprice.getPrice() * PTprice.getCount());
				double commision = PTprice.getCommision() != null ? PTprice.getCommision() : 0.0;
				allCommision += (commision * PTprice.getCount());
			}
			// 2015-4-17加
			// 总价不能小于佣金
			if (SupplyerTPrice <= allCommision) {
				Retatus.setStatu(ReturnStatus.DataError);
				Retatus.setStatusreson("产品总价不能小于产品总佣金");
				return Retatus;
			}
			// 查询支付订单
			hql = "from OSupplyerOrder p where p.payOrderId =?";
			Object[] b4 = new Object[1];
			b4[0] = SupplyerOrder.getPayOrderId();
			SupplyerOrderList = baseDAO.find(hql, b4);
			if (SupplyerOrderList == null || SupplyerOrderList.size() == 0) {
				Retatus.setStatu(ReturnStatus.ParamError);
				Retatus.setStatusreson("没有支付订单");
				return Retatus;
			}
			String SupplyOrderIDs = "";
			// 计算支付订单的总价
			for (OSupplyerOrder SupplyerOrderTPrice : SupplyerOrderList) {
				SupplyOrderIDs += SupplyerOrderTPrice.getSupplierOrderId() + ",";
				if (SupplyerOrderTPrice.getSupplierOrderId() == null) {
					Retatus.setStatu(ReturnStatus.DataError);
					Retatus.setStatusreson("OSupplyerOrder表里面的供应商的订单号为空值！");
					return Retatus;
				}
				// 不是同个供应商订单时
				if (!SupplyerOrderTPrice.getSupplierOrderId().equals(supplierOrderId)) {
					if (SupplyerOrderTPrice.getTotalPrice() == null) {
						Retatus.setStatu(ReturnStatus.DataError);
						Retatus.setStatusreson("OSupplyerOrder表里面的总价为null！");
						return Retatus;
					}
					if (SupplyerOrderTPrice.getPostage() == null) {
						Retatus.setStatu(ReturnStatus.DataError);
						Retatus.setStatusreson("OSupplyerOrder表里面的运费为null！");
						return Retatus;
					}
					AllTotalPrice += (SupplyerOrderTPrice.getTotalPrice() + SupplyerOrderTPrice.getPostage());
				}
			}
			// 支付总价，其它供应商订单加该供应商订单总价
			AllTotalPrice += (SupplyerTPrice + Postage);
		
			for (OProductOrder Uitem : ProductOrderList) {
				OProductOrder o = baseDAO.get(OProductOrder.class, Uitem.getProductOrderId());
				o.setPrice(Uitem.getPrice());
				baseDAO.update(o);
			}
			// 重新生成新的支付订单
			OPayOrder oldPayOrder = baseDAO.get(OPayOrder.class, SupplyerOrder.getPayOrderId());
			oldPayOrder.setState(Short.valueOf(OrderStatusEnum.Tovoided.toString()));
			baseDAO.update(oldPayOrder); 
			String payorderid = GenerateOrderNum.getInstance().GenerateOrder();
			if (oldPayOrder != null) {
				OPayOrder opo = new OPayOrder();// 订单主记录
				opo.setPayOrderId(payorderid);// 生成订单号
				opo.setBigOrderId(payorderid);// 组合订单号
				opo.setOrderTime(new Date());// 下单时间
				opo.setWeiId(oldPayOrder.getWeiId());// 买家微店号
				opo.setOrderDate(new Date());// 提高效率时间
				opo.setState(Short.parseShort(OrderStatusEnum.Nopay.toString()));// 等待付款
				opo.setOrderFrom(oldPayOrder.getOrderFrom());
				opo.setTypeState(oldPayOrder.getTypeState());
				opo.setTotalPrice(AllTotalPrice); 
				opo.setSupplierOrder(oldPayOrder.getSupplierOrder());
				baseDAO.save(opo);
				
				if (StringUtils.isNotEmpty(SupplyOrderIDs)) {
					SupplyOrderIDs = SupplyOrderIDs.substring(0, SupplyOrderIDs.length() - 1);
				}
			
				// 记录支付快照
				OPayOrderLog log = new OPayOrderLog();
				log.setCreateTime(new Date());
				log.setWeiId(oldPayOrder.getWeiId());
				log.setPayOrderId(payorderid);
				log.setSupplyOrderIds(SupplyOrderIDs);
				log.setState(Short.valueOf(OrderStatusEnum.Nopay.toString()));
				log.setTotalAmout(AllTotalPrice);
				baseDAO.save(log);

				// 修改产品订单
				String[] suIds = new String[SupplyerOrderList.size()];
				for (int i = 0; i < SupplyerOrderList.size(); i++) {
					suIds[i] = SupplyerOrderList.get(i).getSupplierOrderId();
				}
				String o_pro = " update OProductOrder p set p.payOrderId=:payId where p.supplierOrderId in(:supIds)";
				Map<String, Object> map_proMap = new HashMap<String, Object>();
				map_proMap.put("payId", payorderid);
				map_proMap.put("supIds", suIds);
				baseDAO.executeHqlByMap(o_pro, map_proMap);
			}
			// 供应商订单号重新指定
			hql = "update OSupplyerOrder w set w.payOrderId=? where w.payOrderId =?";
			Object[] bb = new Object[2];
			bb[0] = payorderid;
			bb[1] = SupplyerOrder.getPayOrderId();
			baseDAO.executeHql(hql, bb);

			// 更新供应商的总价
			hql = "update OSupplyerOrder w set w.totalPrice =?, w.postage=? where w.supplierOrderId =?";
			Object[] b9 = new Object[3];
			b9[0] = SupplyerTPrice;
			b9[1] = Postage;
			b9[2] = supplierOrderId;
			baseDAO.executeHql(hql, b9);
			// 插入流水记录
			OOrderFlow OrderFlow = new OOrderFlow();
			// 供应商ID
			OrderFlow.setSupplierOrderId(supplierOrderId);
			OrderFlow.setOperateContent("修改订单单价");
			Date operateTime = new Date();
			OrderFlow.setOperateTime(operateTime);
			OrderFlow.setWeiId(WeiID);
			baseDAO.save(OrderFlow); // 保存实体
			Retatus.setStatu(ReturnStatus.Success);
			Retatus.setStatusreson("修改单价成功！");
			// 2015-4-23消息推送
			UPushMessage PushMessage = new UPushMessage();
			PushMessage.setPushWeiId(WeiID);// 推送消息人
			PushMessage.setReciptWeiId(SupplyerOrder.getBuyerID()); // 接受消息人
			PushMessage.setPushContent("您有一笔订单已改价");
			PushMessage.setMsgType(Short.parseShort(PushMessageType.buyorder.toString()));
			PushMessage.setCreateTime(new Date());
			PushMessage.setObjectId(supplierOrderId);
			// 订单类型
			String orderType = SupplyerOrder.getOrderType() != null ? SupplyerOrder.getOrderType().toString() : "-11";
			PushMessage.setExtra(orderType);
			insertSendPushMsg(PushMessage); // 保存推送消息
			return Retatus;
		} else {
			Retatus.setStatu(ReturnStatus.ParamError);
			Retatus.setStatusreson("没有修改的产品订单");
			return Retatus;
		}
	}
	
	public ReturnModel updateOrderProductPrice(long WeiID, String param){
		ReturnModel Retatus=new ReturnModel();
		JSONObject obj;
		try {
			obj = JSONObject.fromObject(param);
		} catch (Exception e) {
			Retatus.setStatu(ReturnStatus.ParamError);
			Retatus.setStatusreson("json串错误！");
			return Retatus;
		}
		if (obj == null) {
			Retatus.setStatu(ReturnStatus.ParamError);
			Retatus.setStatusreson("json串错误！转换结果为null");
			return Retatus;
		}
		/**
		 * 供应商订单
		 */
		String supplierOrderId = String.valueOf(obj.get("supplierOrderId"));
		List<OProductOrder> prolist=new ArrayList<OProductOrder>();
		/**
		 * 需要修改的单价
		 */
		try {
			String svalue = obj.getString("PList");
			JSONArray arrayvalue = new JSONArray().fromObject(svalue);
			for (int j = 0; j < arrayvalue.size(); j++) // 遍历value
			{
				JSONObject jovalue = (JSONObject) arrayvalue.get(j);
				OProductOrder UP = new OProductOrder();
				String pid = jovalue.getString("productOrderId");
				Double pprice = jovalue.getDouble("price");
				if (ObjectUtil.isEmpty(pid)) {
					Retatus.setStatu(ReturnStatus.ParamError);
					Retatus.setStatusreson("产品订单不能为空!");
					return Retatus;
				}
				if (pprice == null) { 
					Retatus.setStatu(ReturnStatus.ParamError);
					Retatus.setStatusreson("产品价格不能为空!");
					return Retatus;
				}
				UP.setPrice(pprice);
				UP.setProductOrderId(pid);
				prolist.add(UP);
			}
		} catch (Exception e) {
			Retatus.setStatu(ReturnStatus.ParamError);
			Retatus.setStatusreson("json串单价list错误");
			return Retatus;
		}
		return editOrderProductPrice(WeiID,supplierOrderId,prolist);
	}
	
	public ReturnModel editOrderProductPrice(Long weiid,String supplyOrderid, List<OProductOrder> prolist){
		ReturnModel rq=new ReturnModel();
		if(prolist==null||prolist.size()<=0){
			rq.setStatu(ReturnStatus.ParamError);
			rq.setStatusreson("暂无修改");
			return rq;
		}
		OSupplyerOrder supplyerOrder=baseDAO.get(OSupplyerOrder.class, supplyOrderid);
		if(supplyerOrder!=null){
			if(supplyerOrder.getSellerWeiId()!=null&&supplyerOrder.getSellerWeiId().longValue()==weiid){
				List<OProductOrder> list=ordersDao.find_ProductOrderBySupplyOrderId(supplyOrderid);
				double backPrice=0d;
				if(list!=null&&list.size()>0){
					String payOrderId=GenerateOrderNum.getInstance().GenerateOrder();
					// 检测供应商是否存在（品牌）
					DBrandSupplier supplier= baseDAO.get(DBrandSupplier.class, list.get(0).getSupplyeriId());
					if(supplier==null){
						rq.setStatu(ReturnStatus.SystemError);
						rq.setStatusreson("非代理产品！");
						return rq;
					}
					int identity= agentDao.getUserIdentityForBrand(weiid, supplier.getBrandId());
					for (OProductOrder oo : list) {
						for (OProductOrder pp : prolist) {
							if(oo.getProductOrderId().equals(pp.getProductOrderId())){
								PProductStyles styles=baseDAO.get(PProductStyles.class, oo.getStyleId());
								if(styles.getPrice()<pp.getPrice()){
									rq.setStatu(ReturnStatus.ParamError);
									rq.setStatusreson("价格不能高于零售价");
									return rq;
								}
								else if(BitOperation.verIdentity(identity, UserIdentityType.AgentDuke)){
									if(styles.getDukePrice()>pp.getPrice()){
										rq.setStatu(ReturnStatus.ParamError);
										rq.setStatusreson("价格不能低于城主价");
										return rq;
									}
								}else if (BitOperation.verIdentity(identity, UserIdentityType.AgentDeputyDuke)) {
									if(styles.getDeputyPrice()>pp.getPrice()){
										rq.setStatu(ReturnStatus.ParamError);
										rq.setStatusreson("价格不能低于副城主价");
										return rq;
									}
								}else if (BitOperation.verIdentity(identity, UserIdentityType.AgentBrandAgent)) {
									if(styles.getAgentPrice()>pp.getPrice()){
										rq.setStatu(ReturnStatus.ParamError);
										rq.setStatusreson("价格不能低于代理价");
										return rq;
									}
								}
								backPrice+=(oo.getPrice()-pp.getPrice())*oo.getCount();
								oo.setPrice(pp.getPrice());
								baseDAO.update(oo);
							}
						}
					}
					if(backPrice!=0){
						OPayOrder payOrder=baseDAO.get(OPayOrder.class, supplyerOrder.getPayOrderId());
						supplyerOrder.setTotalPrice(supplyerOrder.getTotalPrice()-backPrice);
						if(payOrder!=null){
							payOrder.setState(Short.parseShort(OrderStatusEnum.Tovoided.toString()));
							baseDAO.update(payOrder); 
							//新生成的支付订单
							OPayOrder orderNew=new OPayOrder();
							orderNew.setPayOrderId(payOrderId);
							orderNew.setOrderDate(new Date());
							orderNew.setTypeState(payOrder.getTypeState());
							orderNew.setWeiId(payOrder.getWeiId());
							orderNew.setSellerWeiId(payOrder.getSellerWeiId()); 
							orderNew.setWalletmoney(payOrder.getWalletmoney());
							orderNew.setWeiDianCoin(payOrder.getWeiDianCoin());
							orderNew.setOtherAmout(payOrder.getOtherAmout());
							orderNew.setTotalPrice(supplyerOrder.getTotalPrice()+supplyerOrder.getPostage());
							orderNew.setState(Short.parseShort(OrderStatusEnum.Nopay.toString()));
							baseDAO.save(orderNew); 
						}else {
							payOrder=new OPayOrder();
							payOrder.setPayOrderId(payOrderId);
							payOrder.setTotalPrice(supplyerOrder.getTotalPrice()+supplyerOrder.getPostage());
							payOrder.setOrderTime(new Date());
							payOrder.setTypeState(supplyerOrder.getOrderType().shortValue());
							payOrder.setSellerWeiId(supplyerOrder.getSupplyerId());
							payOrder.setWeiId(supplyerOrder.getBuyerID());
							payOrder.setState(Short.parseShort(OrderStatusEnum.Nopay.toString())); 
							baseDAO.save(payOrder);
						}
						//订单快照
						OPayOrderLog log=new OPayOrderLog();
						log.setPayOrderId(payOrderId);
						log.setSupplyOrderIds(supplyOrderid);
						log.setTotalAmout(supplyerOrder.getTotalPrice()+supplyerOrder.getPostage());
						log.setCreateTime(new Date());
						log.setWeiId(supplyerOrder.getBuyerID()); 
						baseDAO.save(log);
						supplyerOrder.setPayOrderId(payOrderId); 
						baseDAO.update(supplyerOrder);
					}
					rq.setStatu(ReturnStatus.Success);
					rq.setStatusreson("修改成功");
				}
			}else {
				rq.setStatu(ReturnStatus.SystemError);
				rq.setStatusreson("无权限做此操作");
			}
		}else {
			rq.setStatu(ReturnStatus.SystemError);
			rq.setStatusreson("供应商订单不存在");
		}
		return rq;
	}
	
	public ReturnModel updateModifyTotalPrice(long WeiID, String param) {
		ReturnModel Retatus = new ReturnModel();
		String hql = "";
		OSupplyerOrder SupplyerOrder = new OSupplyerOrder();
		List<OSupplyerOrder> SupplyerOrderList = new ArrayList<OSupplyerOrder>();
		Double AllTotalPrice = 0.0;
		String supplierOrderId = "";
		Double NewTotalPrice = 0.0;
		Double Postage;
		JSONObject obj;
		Double totalPrice = 0.0;
		try {
			obj = JSONObject.fromObject(param);
		} catch (Exception e) {
			Retatus.setStatu(ReturnStatus.ParamError);
			Retatus.setStatusreson("json串错误！");
			return Retatus;
		}
		if (obj == null) {
			Retatus.setStatu(ReturnStatus.ParamError);
			Retatus.setStatusreson("json串错误！");
			return Retatus;
		}
		try {
			totalPrice = obj.getDouble("totalPrice");
		} catch (Exception e) {
			Retatus.setStatu(ReturnStatus.ParamError);
			Retatus.setStatusreson("json串AllTotalPrice错误！");
			return Retatus;
		}
		try {
			supplierOrderId = obj.getString("supplierOrderId");
		} catch (Exception e) {
			Retatus.setStatu(ReturnStatus.ParamError);
			Retatus.setStatusreson("json串supplierOrderId错误！");
			return Retatus;
		}
		SupplyerOrder = baseDAO.getNotUniqueResultByHql("from OSupplyerOrder p where p.supplierOrderId = ? and p.supplyerId=?", supplierOrderId,WeiID);
		OPayOrder PayOrder = new OPayOrder();
		if (SupplyerOrder == null) {
			Retatus.setStatu(ReturnStatus.ParamError);
			Retatus.setStatusreson("没有你的供应商订单");
			return Retatus;
		}
		if (SupplyerOrder.getSupplyerId() != WeiID) {
			Retatus.setStatu(ReturnStatus.ParamError);
			Retatus.setStatusreson("当前登录不是该卖家，不可以修改运费！");
			return Retatus;
		}
		if (SupplyerOrder.getState() != Short.parseShort(OrderStatusEnum.Nopay.toString())) {
			Retatus.setStatu(ReturnStatus.ParamError);
			Retatus.setStatusreson("订单必须是未支付状态！");
			return Retatus;
		}
		if (SupplyerOrder.getPayOrderId() == null) {
			Retatus.setStatu(ReturnStatus.DataError);
			Retatus.setStatusreson("供应商的支付订单为空！");
			return Retatus;
		}
		if (SupplyerOrder.getPostage() == null) {
			Retatus.setStatu(ReturnStatus.DataError);
			Retatus.setStatusreson("供应商的邮费为null！");
			return Retatus;
		}
		Postage = SupplyerOrder.getPostage();
		if (totalPrice <= 0 || Postage < 0) {
			Retatus.setStatu(ReturnStatus.ParamError);
			Retatus.setStatusreson("总价不能小于等于0或者邮费不能小于0");
			return Retatus;
		}
		if (totalPrice > Postage) // 总价大于邮费的时候
		{
			NewTotalPrice = totalPrice - Postage;
		} else if (totalPrice == Postage) // 总价等于邮费的时候
		{
			NewTotalPrice = 0.0;
		} else if (totalPrice < Postage) // 修改的总价格小于邮费时
		{
			Postage = totalPrice;
			NewTotalPrice = 0.0;
		} else {
			Retatus.setStatu(ReturnStatus.ParamError);
			Retatus.setStatusreson("总价不能小于邮费");
			return Retatus;
		}
		double commision = SupplyerOrder.getCommision() != null ? SupplyerOrder.getCommision() : 0.0;
		if (totalPrice <= commision) {
			Retatus.setStatu(ReturnStatus.DataError);
			Retatus.setStatusreson("总价不能小于佣金");
			return Retatus;
		}
		/**
		 * 算出总价
		 */
		// 查找支付订单表是否有数据
		PayOrder = baseDAO.get(OPayOrder.class, SupplyerOrder.getPayOrderId());
		if (PayOrder == null) {
			Retatus.setStatu(ReturnStatus.DataError);
			Retatus.setStatusreson("支付订单为空！");
			return Retatus;
		}
		// 查找支付订单的所有供应商
		SupplyerOrderList = baseDAO.find("from OSupplyerOrder p where p.payOrderId = ?", SupplyerOrder.getPayOrderId());
		if (SupplyerOrderList == null || SupplyerOrderList.size() == 0) {
			Retatus.setStatu(ReturnStatus.DataError);
			Retatus.setStatusreson("供应商表列表为空！");
			return Retatus;
		}
		for (OSupplyerOrder item : SupplyerOrderList) {
			if (item.getPostage() == null) {
				Retatus.setStatu(ReturnStatus.DataError);
				Retatus.setStatusreson("供应商表订单运费为null！");
				return Retatus;
			}
			if (item.getTotalPrice() == null) {
				Retatus.setStatu(ReturnStatus.DataError);
				Retatus.setStatusreson("供应商表订单总价为null！");
				return Retatus;
			}
			// 如果等于更新的 那个供应商订单用最新的供应商总价
			if (item.getSupplierOrderId().equals(supplierOrderId)) {
				AllTotalPrice += (NewTotalPrice + Postage);
				// Allpostage +=Postage; 2014-4-19 不需要修改支付订单邮费，邮费字段已删除。
			} else {
				AllTotalPrice += (item.getTotalPrice() + item.getPostage());
				// Allpostage +=item.getPostage();
			}
		}

		// 重新生成新的支付订单
		OPayOrder oldPayOrder = baseDAO.get(OPayOrder.class, SupplyerOrder.getPayOrderId());
		oldPayOrder.setState( Short.valueOf(OrderStatusEnum.Tovoided.toString()));
		baseDAO.update(oldPayOrder);
		String payorderid = GenerateOrderNum.getInstance().GenerateOrder();
		if (oldPayOrder != null) {
			OPayOrder opo = new OPayOrder();// 订单主记录
			opo.setPayOrderId(payorderid);// 生成订单号
			opo.setBigOrderId(payorderid);// 组合订单号
			opo.setOrderTime(new Date());// 下单时间
			opo.setWeiId(oldPayOrder.getWeiId());// 买家微店号
			opo.setOrderDate(new Date());// 提高效率时间
			opo.setState(Short.parseShort(OrderStatusEnum.Nopay.toString()));// 等待付款
			opo.setTotalPrice(AllTotalPrice);
			opo.setOrderFrom(oldPayOrder.getOrderFrom()); 
			opo.setTypeState(oldPayOrder.getTypeState());
			opo.setSupplierOrder(oldPayOrder.getSupplierOrder());
			baseDAO.save(opo);
			// 记录支付快照
			OPayOrderLog log = new OPayOrderLog();
			log.setCreateTime(new Date());
			log.setWeiId(oldPayOrder.getWeiId());
			log.setPayOrderId(payorderid);
			log.setSupplyOrderIds(SupplyerOrder.getSupplierOrderId());
			log.setState(Short.valueOf(OrderStatusEnum.Nopay.toString()));
			log.setTotalAmout(AllTotalPrice);
			baseDAO.save(log);
			// 修改产品订单
			String[] suIds = new String[1];
			suIds[0] = SupplyerOrder.getSupplierOrderId();
			String o_pro = " update OProductOrder p set p.payOrderId=:payId where p.supplierOrderId in(:supIds)";
			Map<String, Object> map_proMap = new HashMap<String, Object>();
			map_proMap.put("payId", payorderid);
			map_proMap.put("supIds", suIds);
			baseDAO.executeHql(o_pro, map_proMap);
		}

		// 更新供应商订单总价
		Object[] b5 = new Object[4];
		b5[0] = NewTotalPrice;
		b5[1] = Postage;
		b5[2] = payorderid;
		b5[3] = supplierOrderId;
		baseDAO.executeHql( "update OSupplyerOrder w set w.totalPrice =?,w.postage=?,payOrderId=? where w.supplierOrderId =?", b5);

		// 插入流水记录
		OOrderFlow OrderFlow = new OOrderFlow();
		// 供应商ID
		OrderFlow.setSupplierOrderId(supplierOrderId);
		OrderFlow.setOperateContent("修改订单总价");
		Date operateTime = new Date();
		OrderFlow.setOperateTime(operateTime);
		OrderFlow.setWeiId(WeiID);
		baseDAO.save(OrderFlow); // 保存实体

		// 2015-4-23消息推送
		UPushMessage PushMessage = new UPushMessage();
		PushMessage.setPushWeiId(WeiID);// 推送消息人
		PushMessage.setReciptWeiId(SupplyerOrder.getBuyerID()); // 接受消息人
		PushMessage.setPushContent("您有一笔订单已改价");
		PushMessage.setMsgType(Short.parseShort(PushMessageType.buyorder.toString()));
		PushMessage.setCreateTime(new Date());
		PushMessage.setObjectId(supplierOrderId);
		// 订单类型
		String orderType = SupplyerOrder.getOrderType() != null ? SupplyerOrder.getOrderType().toString() : "-1";
		PushMessage.setExtra(orderType);
		insertSendPushMsg(PushMessage); // 保存推送消息
		Retatus.setStatu(ReturnStatus.Success);
		Retatus.setStatusreson("修改总价成功！");
		return Retatus;
	}

	

	public boolean insertSendPushMsg(UPushMessage msg) {
		//发送push
		boolean b=new SendPushMessage().SendMessage(msg);
		if(!b)
			return false;
		//回写数据库
		baseDAO.save(msg);
		return true;
	}
}
