package com.okwei.cartportal.web;

import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.okwei.bean.domain.UCustomerAddr;
import com.okwei.bean.enums.FromTypeEnum;
import com.okwei.bean.enums.ShoppingCarSourceEnum;
import com.okwei.bean.enums.ShoppingCartTypeEnum;
import com.okwei.bean.vo.LoginUser;
import com.okwei.bean.vo.ReturnStatus;
import com.okwei.bean.vo.order.BAddressVO;
import com.okwei.bean.vo.order.BReturnOdertInfo;
import com.okwei.bean.vo.order.BShoppingCartVO;
import com.okwei.cartportal.bean.vo.SettlementParam;
import com.okwei.cartportal.bean.vo.ShoppingCartOrderParam;
import com.okwei.cartportal.bean.vo.SubmitOrderResult;
import com.okwei.cartportal.service.ICartVerFiveService;
import com.okwei.cartportal.service.IShopCartService;
import com.okwei.cartportal.service.ITest;
import com.okwei.common.JsonUtil;
import com.okwei.service.IBasicShoppingCartService;
import com.okwei.service.address.IBasicAdressService;
import com.okwei.util.AppSettingUtil;
import com.okwei.util.IDCard;
import com.okwei.util.ObjectUtil;
import com.okwei.util.ParseHelper;
import com.okwei.web.base.SSOController;

@Controller
@RequestMapping(value="/shopCartMgtVerFive")
public class CartMgtVersionFiveConntroller extends SSOController{
	@Autowired
	public IBasicShoppingCartService baseCartService;
	@Autowired
	private IShopCartService shopCartService;

	@Autowired
    private IBasicAdressService basicAdressService;
	
	@Autowired
	private ICartVerFiveService cartService;
	@Autowired
	private ITest test;
	
	private final static Log logger = LogFactory.getLog(ShopCartAjaxController.class);
	
	/**
	 * 结算清单页面
	 * @param addrId
	 * @param listJson
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/preSubmitNew")
	public String forwardPreSubmitNew(String addrId, String listJson, Model model) {
		LoginUser user =getUser();// 
//		LoginUser user=getLoginUser(); //
		
		String msg="step:1";
		try {	
			
			List<SettlementParam> sellerList2= (List<SettlementParam>) JsonUtil.json2Objectlist(listJson, SettlementParam.class);
			msg+=",2";
			//商品寄送到的地址
			BAddressVO vo =null;
			// 收货地址列表
			List<BAddressVO> list = basicAdressService.getBAddressList(user.getWeiID());
			msg+=",3";
			if(list!=null&&list.size()>0){
				if (!ObjectUtil.isEmpty(addrId)) {
					for (BAddressVO ad : list) {
						if(ad.getAddressId().equals(addrId))
							vo=ad;
					}
				} 
				if (vo == null) {
					for (BAddressVO ad : list) {
						if (ad.getIsDefault() == 1) {
							vo = ad;
							if(ObjectUtil.isEmpty(addrId)){
							    addrId = ad.getAddressId();
							}
						}
					}
				}
			}
			msg+=",4";
			List<BShoppingCartVO> sellerList= cartService.getSettlementData(sellerList2, user.getWeiID(), vo);//cartService.find_BShoppingCartVOlist(sellerList, vo, user.getWeiID());
			msg+=",5";
			int storeOrder = 0;//
			Double totalPrice=0.0;//总价
			int firstOrderFull=0;
			
			int isHaiwaiUser=0;
			if(sellerList!=null&&sellerList.size()>0){
				List<Map<String, String>> usList= AppSettingUtil.getMaplist("hwgusers");
				for (BShoppingCartVO ss : sellerList) {
					/*---------海外购 -----------*/
					if(usList!=null&&usList.size()>0){
						for (Map<String, String> map : usList) {
							if(ss.getSupplierId()==ParseHelper.toLong(map.get("userid"))){//验证身份证
								isHaiwaiUser=1;
							}
						}
					}
					/*------- -----------*/
					
					if (Integer.valueOf("1").equals(ss.getIsFirstOrder())) {
						storeOrder = 1;
					}
					totalPrice+=ss.getTotalPrice();
					if(ss.getLogisticList()!=null&&ss.getLogisticList().size()>0){
						ss.setTotalShopYoufei(ss.getLogisticList().get(0).getLogisticPrice());
						ss.setCurrentLogisticId(ss.getLogisticList().get(0).getLogisticId()); 
					}
					if(ss.getSource()==Short.parseShort(ShoppingCarSourceEnum.Landi.toString())&& ss.getFristOrderDiffMoney()!=null&&ss.getFristOrderDiffMoney()>0){
						firstOrderFull=1;
					}
				}
			}
			msg+=",6";
			model.addAttribute("isNeedIdcard", isHaiwaiUser);//是否需要填写身份证
			model.addAttribute("firstOrderFull",firstOrderFull);
			model.addAttribute("showList", sellerList);//商品清单
			model.addAttribute("list", list);//收货地址清单
			model.addAttribute("address", vo);//寄送的地址
	        model.addAttribute("totalPrice", totalPrice);//总价格
	        model.addAttribute("addrId", addrId);//选择的收货地址
			model.addAttribute("scidJson", listJson);//用于刷新提交页面的json
			model.addAttribute("storeOrder", storeOrder);
			
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("确定下单页："+e.getMessage()+msg+"。addrId="+addrId+"json:"+listJson);
		}
		model.addAttribute("user",user);
		return "shopcart/preSubmitNew";
	}
	
	/**
	 * 微店5.0 提交订单
	 * @param addrId
	 * @param orderJson
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/orderSubmitNew")
	public String orderSubmitNew(String orderJson,  Model model,String idcard) {
		SubmitOrderResult rmolde = new SubmitOrderResult();// 返回实体
		try {
			ShoppingCartOrderParam param=(ShoppingCartOrderParam)JsonUtil.jsonStrToObject(orderJson, ShoppingCartOrderParam.class);
//			LoginUser user =  getUser(); //
			LoginUser user =getLoginUser(); //
			model.addAttribute("user",user);
			if(user==null){
				rmolde.setBaseModle("登陆异常");
	            rmolde.setStatusReson("登陆异常");// 状态描述
	            rmolde.setStatu(ReturnStatus.LoginError.toString());// 返回状态
	            model.addAttribute("rmolde",rmolde);
	            return "shopcart/orderPay";
			}
			
			if(param!=null) {
				List<BShoppingCartVO> sellerlist=param.getSupplierOrderList();
				BAddressVO address= null;
				if(param.getReceiveInfo()!=null){
					address=param.getReceiveInfo();
					if(!ObjectUtil.isEmpty(address.getAddressId()) ){
						UCustomerAddr addr = shopCartService.getById(UCustomerAddr.class, ParseHelper.toInt(address.getAddressId()));
						if (addr != null) {
							address.setProvince(addr.getProvince());
							address.setCity(addr.getCity());
							address.setDistrict(addr.getDistrict());
						}
					}
				}
				boolean isHavePuhuo=false;//是否有铺货单
				boolean isHaveCommon=false;//是否有普通购买单
				
				for (BShoppingCartVO ss : sellerlist) {
					/*-----------海外购用户id---------------*/
					List<Map<String, String>> usList= AppSettingUtil.getMaplist("hwgusers");
					if(usList!=null&&usList.size()>0){
						for (Map<String, String> map : usList) {
							if(ss.getSupplierId()==ParseHelper.toLong(map.get("userid"))){//验证身份证
								String vercodeString=new IDCard().IDCardValidate(idcard);
								if(!"".equals(vercodeString)){
									rmolde.setBaseModle("身份证号有误");
						            rmolde.setStatusReson(vercodeString);
						            rmolde.setStatu(ReturnStatus.DataError.toString());// 返回状态
						            model.addAttribute("rmolde",rmolde);
						    		return "shopcart/orderPay";
								}
								address.setIdCard(idcard);
							} 
						}
					}
					/*--------------------------*/
					
					if(ss.getBuyType()==Short.valueOf(ShoppingCartTypeEnum.Puhuo.toString())){
						isHavePuhuo=true;
					}else {
						isHaveCommon=true;
					}
				} 
				if(isHaveCommon&&isHavePuhuo){//铺货单不能与其他订单一起提交，返回  购物车页面
					return "shopcart/list";
				}
				BReturnOdertInfo mod  = baseCartService.savePlaceOrder(sellerlist, user.getWeiID(), address, FromTypeEnum.PC);
				if (mod.getState() == 1) {
					if (isHavePuhuo&&!isHaveCommon) {//铺货单页面
						rmolde.setBaseModle("铺货单成功");
                        rmolde.setStatu(ReturnStatus.Success.toString());// 返回状态
                        rmolde.setStatusReson("成功");// 状态描述
                        model.addAttribute("rmolde",rmolde);
						return "shopcart/presell";
					}else {//普通订单
						String pay = ResourceBundle.getBundle("domain").getString("paydomain");
						return "redirect:" + pay + "/pay/cashier?orderNo=" + mod.getOrderno();
					}
				}else {
					rmolde.setBaseModle(mod.getMsg());
					rmolde.setStatusReson("数据异常");// 状态描述
					rmolde.setStatu(ReturnStatus.DataError.toString());// 返回状态
				}
			}else {
				rmolde.setBaseModle(" 服务器异常");
	            rmolde.setStatusReson("服务器异常");// 状态描述
	            rmolde.setStatu(ReturnStatus.DataError.toString());// 返回状态
			}
		} catch (Exception e) {
			rmolde.setBaseModle(" 服务器异常");
            rmolde.setStatusReson("服务器异常");// 状态描述
            rmolde.setStatu(ReturnStatus.DataError.toString());// 返回状态
		}
		model.addAttribute("rmolde",rmolde);
		return "shopcart/orderPay";
	}
	
}
