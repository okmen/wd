package com.okwei.service.impl.shoppingcart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.okwei.bean.domain.AActProductsShowTime;
import com.okwei.bean.domain.AActShowProducts;
import com.okwei.bean.domain.AActSupplier;
import com.okwei.bean.domain.AActivity;
import com.okwei.bean.domain.AActivityProducts;
import com.okwei.bean.domain.PBrandShevle;
import com.okwei.bean.domain.PClassProducts;
import com.okwei.bean.domain.PProductBatchPrice;
import com.okwei.bean.domain.PProductSellKey;
import com.okwei.bean.domain.PProductSellValue;
import com.okwei.bean.domain.PProductStyleKv;
import com.okwei.bean.domain.PProductStyles;
import com.okwei.bean.domain.PProducts;
import com.okwei.bean.domain.PShevleBatchPrice;
import com.okwei.bean.domain.TShoppingCar;
import com.okwei.bean.domain.UBatchSupplyer;
import com.okwei.bean.domain.UBrandSupplyer;
import com.okwei.bean.domain.UDemandProduct;
import com.okwei.bean.domain.UPlatformSupplyer;
import com.okwei.bean.domain.UShopInfo;
import com.okwei.bean.domain.USupplyChannel;
import com.okwei.bean.domain.USupplyDemand;
import com.okwei.bean.domain.USupplyer;
import com.okwei.bean.domain.UWeiSeller;
import com.okwei.bean.domain.UYunSupplier;
import com.okwei.bean.dto.shoppingcart.ShopCarDTO;
import com.okwei.bean.enums.ActProductVerState;
import com.okwei.bean.enums.BrandSupplyerStateEnum;
import com.okwei.bean.enums.PlatformSupplyerStateEnum;
import com.okwei.bean.enums.ProductShelveStatu;
import com.okwei.bean.enums.ProductStatusEnum;
import com.okwei.bean.enums.ProductTagType;
import com.okwei.bean.enums.ShoppingCarSourceEnum;
import com.okwei.bean.enums.ShoppingCartTypeEnum;
import com.okwei.bean.enums.SupplierStatusEnum;
import com.okwei.bean.enums.SupplyChannelTypeEnum;
import com.okwei.bean.vo.LoginUser;
import com.okwei.bean.vo.ReturnModel;
import com.okwei.bean.vo.ReturnStatus;
import com.okwei.bean.vo.shoppingcart.BalanceReturnModel;
import com.okwei.bean.vo.shoppingcart.BalanceVO;
import com.okwei.bean.vo.shoppingcart.BasicProductStyleParam;
import com.okwei.bean.vo.shoppingcart.BasicProductStyleParamModel;
import com.okwei.bean.vo.shoppingcart.PProductStyleKVVO;
import com.okwei.bean.vo.shoppingcart.ShoppingCar;
import com.okwei.bean.vo.shoppingcart.ShoppingCarList;
import com.okwei.bean.vo.shoppingcart.Style;
import com.okwei.bean.vo.shoppingcart.WholesalePrice;
import com.okwei.common.CommonMethod;
import com.okwei.common.JsonUtil;
import com.okwei.dao.impl.BaseDAO;
import com.okwei.dao.product.IProductSearchDao;
import com.okwei.dao.shoppingcart.IBasicPClassProductsMgtDAO;
import com.okwei.dao.shoppingcart.IBasicPProductSellKeyMgtDAO;
import com.okwei.dao.shoppingcart.IBasicPProductSellValueMgtDAO;
import com.okwei.dao.shoppingcart.IBasicPProductStyleKvMgtDAO;
import com.okwei.dao.shoppingcart.IBasicPProductStylesMgtDAO;
import com.okwei.dao.shoppingcart.IBasicPProductsMgtDAO;
import com.okwei.dao.shoppingcart.IBasicPShevleBatchPriceMgtDAO;
import com.okwei.dao.shoppingcart.IBasicTShoppingCarMgtDAO;
import com.okwei.dao.shoppingcart.IBasicUBatchSupplyerMgtDAO;
import com.okwei.dao.shoppingcart.IBasicUBrandSupplyerMgtDAO;
import com.okwei.dao.shoppingcart.IBasicUDemandProductMgtDAO;
import com.okwei.dao.shoppingcart.IBasicUPlatformSupplyerMgtDAO;
import com.okwei.dao.shoppingcart.IBasicUProductAgentMgtDAO;
import com.okwei.dao.shoppingcart.IBasicUProductShopMgtDAO;
import com.okwei.dao.shoppingcart.IBasicUShopInfoMgtDAO;
import com.okwei.dao.shoppingcart.IBasicUSupplyChannelMgtDAO;
import com.okwei.dao.shoppingcart.IBasicUSupplyDemandMgtDAO;
import com.okwei.dao.shoppingcart.IBasicUSupplyerMgtDAO;
import com.okwei.dao.shoppingcart.IBasicUUserAssistMgtDAO;
import com.okwei.dao.shoppingcart.IBasicUWeiSellerMgtDAO;
import com.okwei.dao.shoppingcart.IBasicUYunSupplierMgtDAO;
import com.okwei.service.activity.IBaseActivityService;
import com.okwei.service.agent.IDAgentService;
import com.okwei.service.impl.BaseService;
import com.okwei.service.impl.user.AgentService;
import com.okwei.service.shoppingcart.IBaseCartNewService;
import com.okwei.service.shoppingcart.IBasicShoppingCartMgtService;
import com.okwei.util.BitOperation;
import com.okwei.util.DateUtils;
import com.okwei.util.ImgDomain;
import com.okwei.util.ParseHelper;
import com.okwei.util.RedisUtil;

@Service
public class BasicShoppingCartMgtService extends BaseService implements IBasicShoppingCartMgtService {
	@Autowired
	private IBasicPProductsMgtDAO iPProductsMgtDAO;
	@Autowired
	private IBasicPClassProductsMgtDAO iBasicPClassProductsMgtDAO;
	@Autowired
	private IBasicUSupplyerMgtDAO iBasicUSupplyerMgtDAO;
	@Autowired
	private IBasicPProductStylesMgtDAO iBasicPProductStylesMgtDAO;
	@Autowired
	private IBasicPShevleBatchPriceMgtDAO iBasicPShevleBatchPriceMgtDAO;
	@Autowired
	private IBasicTShoppingCarMgtDAO iBasicTShoppingCarMgtDAO;
	@Autowired
	private IBasicUProductAgentMgtDAO iBasicUProductAgentMgtDAO;
	@Autowired
	private IBasicUDemandProductMgtDAO iUDemandProductMgtDAO;
	@Autowired
	private IBasicUPlatformSupplyerMgtDAO iBasicUPlatformSupplyerMgtDAO;
	@Autowired
	private IBasicUShopInfoMgtDAO iBasicUShopInfoMgtDAO;
	@Autowired
	private IBasicUYunSupplierMgtDAO iBasicUYunSupplierMgtDAO;
	@Autowired
	private IBasicUWeiSellerMgtDAO iBasicUWeiSellerMgtDAO;
	@Autowired
	private IBasicUDemandProductMgtDAO iBasicUDemandProductMgtDAO;
	@Autowired
	private IBasicUSupplyDemandMgtDAO iBasicUSupplyDemandMgtDAO;
	@Autowired
	private IBasicUProductShopMgtDAO iBasicUProductShopMgtDAO;
	@Autowired
	private IBasicPProductsMgtDAO iBasicPProductsMgtDAO;
	@Autowired
	private IBasicUUserAssistMgtDAO iBasicUUserAssistMgtDAO;
	@Autowired
	private IBasicUBrandSupplyerMgtDAO iBasicUBrandSupplyerMgtDAO;
	@Autowired
	private IBasicUSupplyChannelMgtDAO iBasicUSupplyChannelMgtDAO;
	@Autowired
	private IBasicUBatchSupplyerMgtDAO iBasicUBatchSupplyerMgtDAO;
	@Autowired
	private IBasicPProductStyleKvMgtDAO iBasicPProductStyleKvMgtDAO;
	@Autowired
	private IBasicPProductSellKeyMgtDAO iBasicPProductSellKeyMgtDAO;
	@Autowired
	private IBasicPProductSellValueMgtDAO iBasicPProductSellValueMgtDAO;
	@Autowired
	private BaseDAO baseDAO;

	@Autowired
	private IBaseActivityService activityService;
	@Autowired
	private IBaseCartNewService cartNewService;

	@Autowired
	private IProductSearchDao searchDao;
	@Autowired
	private IDAgentService agentService;
	
	/**
	 * 添加购物车
	 */
	@Override
	public ReturnModel addShoppingCart(String json, long weiId, LoginUser user, Long sharePageProducer, Long shareOne, Long shareID) {
		// TODO Auto-generated method stub
		// 获取需要添加到购物车的商品
		ReturnModel returnModel = new ReturnModel();
		returnModel = getShopCarDTO(json, weiId);
		ShopCarDTO shopCarDTO = new ShopCarDTO();
		if (returnModel.getStatu().equals(ReturnStatus.Success)) {
			shopCarDTO = (ShopCarDTO) returnModel.getBasemodle();
			if (sharePageProducer <= 0) // 没有传制作人，不是从分享页进来，取成交微店作为制作人
			{
				shopCarDTO.setMakerWeiID(shopCarDTO.getShopWeiID() == null ? 111L : shopCarDTO.getShopWeiID());
			} else {
				shopCarDTO.setMakerWeiID(sharePageProducer);
			}
			if (shareOne <= 0)// 没有传分享人 ，则算微店网
			{
				shopCarDTO.setSharerWeiID(111L);
			} else {
				shopCarDTO.setSharerWeiID(shareOne);
			}
			shopCarDTO.setShareID(shareID);
			returnModel.setBasemodle(null);
		} else {
			return returnModel;
		}
		// 查询有没有这个产品
		PProducts pProducts = iPProductsMgtDAO.getPProducts(shopCarDTO.getProNum());
		if (pProducts == null || pProducts.getState() != Long.parseLong(String.valueOf(ProductStatusEnum.Showing))) {
			returnModel.setStatu(ReturnStatus.DataError);
			returnModel.setStatusreson(shopCarDTO.getProNum() + "没有这个产品，或这个产品不是展示中");
			return returnModel;
		}
		// 判断是否为0 若为0则取产品的供应商id
		if (shopCarDTO.getSupplierWeiId() == null||shopCarDTO.getSupplierWeiId()==0) {
			shopCarDTO.setSupplierWeiId(pProducts.getSupplierWeiId());
		}
		
		// 产品分类id
		if (pProducts.getClassId() == null) {
			returnModel.setStatu(ReturnStatus.DataError);
			returnModel.setStatusreson(shopCarDTO.getProNum() + "这个产品的分类id为空");
			return returnModel;
		}
		// 产品价格
		if (pProducts.getDefaultPrice() == null) {
			returnModel.setStatu(ReturnStatus.DataError);
			returnModel.setStatusreson(shopCarDTO.getProNum() + "这个产品的价格为空!");
			return returnModel;
		}
		// 查询有没有上架这个产品
//		PClassProducts pClassProducts = iBasicPClassProductsMgtDAO.judageProductIsRacking(pProducts.getSupplierWeiId(), shopCarDTO.getProNum(), Short.parseShort(String.valueOf(ProductShelveStatu.OnShelf)));
//		// 供应商没有上架这个产品或者发货人为空
//		if (pClassProducts == null || pClassProducts.getSendweiId() == null) {
//			returnModel.setStatu(ReturnStatus.DataError);
//			returnModel.setStatusreson(shopCarDTO.getProNum() + "这个产品没有给供应商上架");
//			return returnModel;
//		}
		// 查询款式
		PProductStyles pProductStyles = iBasicPProductStylesMgtDAO.getPProductStyles(shopCarDTO.getStyleId());
		if (pProductStyles == null) {
			returnModel.setStatu(ReturnStatus.DataError);
			returnModel.setStatusreson(shopCarDTO.getStyleId() + "这个款式商品不存在");
			return returnModel;
		}
		return saveShoppingCart(shopCarDTO, pProducts.getSupplierWeiId(), pProducts.getProductTitle() != null ? pProducts.getProductTitle() : "", pProducts.getDefaultImg() != null ? pProducts.getDefaultImg() : "", pProducts.getClassId(), pProducts.getTag(), pProducts.getProductId(),
				pProducts.getDefaultPrice(), user);
	}

	@Override
	public boolean isSaleProduct(long weiId) {
		// TODO Auto-generated method stub
		USupplyer uSupplyer = iBasicUSupplyerMgtDAO.getUSupplyer(weiId);
		if (uSupplyer != null && uSupplyer.getType() != null && uSupplyer.getType() > 0) {
			return true;
		}
		return false;
	}

	@Override
	public ReturnModel saveShoppingCart(ShopCarDTO shopCarDTO, long supplierweiId, String productTitle, String defaultImg, int classId, Short tag, long productId, double price, LoginUser user) {
		// TODO Auto-generated method stub
		ReturnModel returnModel = new ReturnModel();
		// 订单类型
		switch (shopCarDTO.getBuyType()) {
		// 零售订单
		case 1:
			returnModel = addRetail(tag, shopCarDTO, user, productId, supplierweiId, productTitle, defaultImg, classId);
			break;
		// 预订单
		case 2:
			returnModel.setStatu(ReturnStatus.DataError);
			returnModel.setStatusreson(productId + "不可以添加预订单");
			break;
		// 批发订单
		case 3:
			returnModel = addWholesale(tag, shopCarDTO, user, supplierweiId, productId, productTitle, defaultImg, classId);
			break;
		// 进货单
		case 4:
			if (judgePlatformAndBrand(user.getWeiID())) {
				returnModel.setStatu(ReturnStatus.DataError);
				returnModel.setStatusreson("平台号和品牌号不可以购买进货单!");
				return returnModel;
			}
			returnModel = addPurchases(user, shopCarDTO, productId, supplierweiId, productTitle, defaultImg, classId);
			break;
		// 铺货单价格
		case 5:
			if (judgePlatformAndBrand(user.getWeiID())) {
				returnModel.setStatu(ReturnStatus.DataError);
				returnModel.setStatusreson("平台号和品牌号不可以购买铺货单!");
				return returnModel;
			}
			returnModel = addDistirbution(tag, user, shopCarDTO, productId, supplierweiId, productTitle, defaultImg, classId);
			break;
		default:
			break;
		}
		return returnModel;
	}

	@Override
	public List<PProductBatchPrice> getBatchPricess(long supplierWeiId, long productId, short status) {
		// TODO Auto-generated method stub

		// 查找上架表
		PClassProducts pClassProducts = iBasicPClassProductsMgtDAO.judageProductIsRacking(supplierWeiId, productId, status);
		if (pClassProducts != null && pClassProducts.getId() != null) {
			List<PShevleBatchPrice> pShevleBatchPriceList = iBasicPShevleBatchPriceMgtDAO.getPShevleBatchPriceById(pClassProducts.getId());
			if (pShevleBatchPriceList == null || pShevleBatchPriceList.size() == 0) {
				return new ArrayList<PProductBatchPrice>();
			}
			List<PProductBatchPrice> ppplist = new ArrayList<PProductBatchPrice>();

			for (PShevleBatchPrice price : pShevleBatchPriceList) {
				PProductBatchPrice ppp = new PProductBatchPrice();
				ppp.setBid(price.getSbid());
				ppp.setCount(price.getCount());
				ppp.setPirce(price.getPrice());
				ppp.setProductId(productId);
				ppplist.add(ppp);
			}
			return ppplist;
		}
		return new ArrayList<PProductBatchPrice>();
	}

	@Override
	public Double getshoppcartpricebycount(int count, List<PProductBatchPrice> pplist) {
		// TODO Auto-generated method stub
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

	@Override
	public long saveShoppingCart(long weiId, long sellerWeiId, long supplierWeiId, long shopWeiID, String productTitle, String defaultImg, int classId, long productId, double price, int count, long styleId, String property1, short buyType, short status, Date time, short source, Long makerWeiID,
			Long sharerWeiID, Long sharerID) {
		// TODO Auto-generated method stub
		TShoppingCar tShoppingCar = new TShoppingCar();
		// 买家微店号
		tShoppingCar.setWeiId(weiId);
		// 成交微店号
		tShoppingCar.setSellerWeiId(sellerWeiId);
		// 供应商微店号
		tShoppingCar.setSupplierWeiId(supplierWeiId);
		//
		tShoppingCar.setShopWeiID(shopWeiID);
		// 商品标题
		tShoppingCar.setProTitle(productTitle);
		// 商品图片
		tShoppingCar.setImage(ImgDomain.GetFullImgUrl(defaultImg));
		// 分类ID
		tShoppingCar.setClassId(classId);
		// 产品id
		tShoppingCar.setProNum(productId);
		// 价格
		tShoppingCar.setPrice(price);
		// 数量
		tShoppingCar.setCount(count);
		// 款式id
		tShoppingCar.setStyleId(styleId);
		// 属性
		tShoppingCar.setProperty1(property1);
		// BuyType
		tShoppingCar.setBuyType(buyType);
		// 制作人
		tShoppingCar.setMakerWeiID(makerWeiID);
		// 分享人
		tShoppingCar.setSharerWeiID(sharerWeiID);
		// 分享ID
		tShoppingCar.setShareID(sharerID);
		// 状态
		tShoppingCar.setStatus((short)1);
		tShoppingCar.setCreateTime(new Date());
		tShoppingCar.setSource(source);

		return (long) iBasicTShoppingCarMgtDAO.saveTShoppingCar(tShoppingCar); // 新添加数据
	}

	@Override
	public ReturnModel getAngetPrice(int demandId, long productId, long styleId) {
		// TODO Auto-generated method stub
		// 查找招商需求与产品关系表
		ReturnModel returnModel = new ReturnModel();
		UDemandProduct uDemandProduct = iUDemandProductMgtDAO.getUDemandProduct(demandId, productId);
		if (uDemandProduct == null) {
			returnModel.setStatu(ReturnStatus.DataError);
			returnModel.setStatusreson("招商需求关系表为空!");
			return returnModel;
		}
		// 查找款式表
		PProductStyles pProductStyles = iBasicPProductStylesMgtDAO.getPProductStyles(styleId, productId);
		if (pProductStyles == null || pProductStyles.getAgencyPrice() == null) {
			returnModel.setStatu(ReturnStatus.DataError);
			returnModel.setStatusreson(productId + "产品" + styleId + "款式" + "招商需求Id为空!");
			return returnModel;
		}
		returnModel.setBasemodle(pProductStyles.getAgencyPrice());
		returnModel.setStatu(ReturnStatus.Success);
		return returnModel;
	}

	/**
	 * 获取进货单价格
	 */
	@Override
	public ReturnModel getPurchasesPrice(long styleId, long productId, long weiId, short areaType) {
		// TODO Auto-generated method stub
		ReturnModel returnModel = new ReturnModel();
		double price = 0.0;
		// 查询产品款式表
		PProductStyles pProductStyles = iBasicPProductStylesMgtDAO.getPProductStyles(styleId, productId);
		switch (areaType) {
		// 代理区
		case 1:
			// 招商需求与产品关系表
			UDemandProduct uDemandProduct = iBasicUDemandProductMgtDAO.getUDemandProductByProductId(productId);
			if (uDemandProduct == null) {
				returnModel.setStatu(ReturnStatus.DataError);
				returnModel.setStatusreson(productId + "这个产品的需求关系表为空!");
				return returnModel;
			}
			List<UDemandProduct> uDemandProductList = new ArrayList<UDemandProduct>();
			uDemandProductList.add(uDemandProduct);
			int demandId = getDemandId(productId, uDemandProductList);
			// 只有代理商才可以添加代理区的产品到购物车
			List<Long> weiIdList = new ArrayList<Long>();
			weiIdList.add(weiId);
			List<USupplyChannel> agentList = iBasicUSupplyChannelMgtDAO.getUSupplyChannelList(weiIdList, (short) 1, (short) 1);
			if (isAgentOrLandingByDemandId(agentList, demandId) == false) {
				returnModel.setStatu(ReturnStatus.DataError);
				returnModel.setStatusreson("您不是代理商不可以购买代理区的进货单!");
				return returnModel;
			}
			if (pProductStyles == null || pProductStyles.getAgencyPrice() == null) {

				returnModel.setStatu(ReturnStatus.DataError);
				returnModel.setStatusreson(styleId + "这个款式没有或代理价为空!");
				return returnModel;
			}
			// 拿代理价
			price = pProductStyles.getAgencyPrice();
			break;
		// 落地区
		case 2:
			// 查询产品款式表
			if (pProductStyles == null || pProductStyles.getLandPrice() == null) {
				returnModel.setStatu(ReturnStatus.DataError);
				returnModel.setStatusreson(styleId + "这个款式没有或落地区为空!");
				return returnModel;
			}
			// 拿落地价
			price = pProductStyles.getLandPrice();
			break;
		default:
			returnModel.setStatu(ReturnStatus.DataError);
			returnModel.setStatusreson("进货单的区必须是代理区和落地区!");
			break;
		}
		if (price <= 0) {
			returnModel.setStatu(ReturnStatus.DataError);
			returnModel.setStatusreson("进货单的价格必须大于0!");
			return returnModel;
		}
		returnModel.setBasemodle(price);
		returnModel.setStatu(ReturnStatus.Success);
		return returnModel;
	}

	/**
	 * 铺货单价格
	 */
	@Override
	public ReturnModel getListTheShopPrice(long styleId, long productId, long weiId, short areaType) {
		// TODO Auto-generated method stub
		ReturnModel returnModel = new ReturnModel();
		double price = 0.0;
		// 查找款式表
		PProductStyles pProductStyles = iBasicPProductStylesMgtDAO.getPProductStyles(styleId, productId);
		// 哪个区拿哪个价格(代理区代理价，落地去落地价)
		switch (areaType) {
		// 代理区
		case 1:
			// 查找招商需求与产品关系表
			if (pProductStyles == null || pProductStyles.getAgencyPrice() == null || pProductStyles.getAgencyPrice() <= 0) {
				returnModel.setStatu(ReturnStatus.DataError);
				returnModel.setStatusreson(styleId + "款式空,或代理价为空或小于等于0!");
				return returnModel;
			}
			price = pProductStyles.getAgencyPrice();
			returnModel.setStatu(ReturnStatus.Success);
			returnModel.setBasemodle(price);
			break;
		// 落地区
		case 2:
			// 判断落地店
			if (pProductStyles == null || pProductStyles.getLandPrice() == null || pProductStyles.getLandPrice() <= 0) {
				returnModel.setStatu(ReturnStatus.DataError);
				returnModel.setStatusreson(styleId + "款式空,或落地价为空或小于等于0!");
				return returnModel;
			}
			price = pProductStyles.getLandPrice();
			returnModel.setStatu(ReturnStatus.Success);
			returnModel.setBasemodle(price);
			break;
		default:
			returnModel.setStatu(ReturnStatus.DataError);
			returnModel.setStatusreson("铺货单必须是代理区和落地区");
			break;
		}
		return returnModel;
	}

	@Override
	public ReturnModel getShopCarDTO(String json, long weiId) {
		// TODO Auto-generated method stub
		ReturnModel returnModel = new ReturnModel();
		try {
			ShopCarDTO shopCarDTO = new ShopCarDTO();
			JSONObject obj;
			obj = JSONObject.fromObject(json);
			// 卖家微店号
			shopCarDTO.setSupplierWeiId(obj.getLong("supplierID"));
			if (shopCarDTO.getSupplierWeiId() < 0) {
				returnModel.setStatu(ReturnStatus.ParamError);
				returnModel.setStatusreson("参数supplierID错误");
				return returnModel;
			}
			// sellerWeiId(成交微店号)
			shopCarDTO.setSellerWeiId(obj.getLong("sellerWeiId"));
			if (shopCarDTO.getSellerWeiId() <= 0) {
				// returnModel.setStatu(ReturnStatus.ParamError);
				// returnModel.setStatusreson("参数sellerWeiId错误");
				// return returnModel;
				shopCarDTO.setSellerWeiId(weiId);
			}
			// proNum(产品id)
			shopCarDTO.setProNum(obj.getLong("proNum"));
			if (shopCarDTO.getProNum() <= 0) {
				returnModel.setStatu(ReturnStatus.ParamError);
				returnModel.setStatusreson("参数proNum错误");
				return returnModel;
			}
			// count(产品数量)
			shopCarDTO.setCount(obj.getInt("count"));
			if (shopCarDTO.getCount() <= 0) {
				returnModel.setStatu(ReturnStatus.ParamError);
				returnModel.setStatusreson("参数count错误");
				return returnModel;
			}
			// price(价格)
			// shopCarDTO.setPrice(obj.getDouble("price"));
			// if (shopCarDTO.getPrice() <= 0) {
			// returnModel.setStatu(ReturnStatus.ParamError);
			// returnModel.setStatusreson("参数price错误");
			// return returnModel;
			// }
			// image(图片)
			shopCarDTO.setImage(obj.getString("image"));
			// if (shopCarDTO.getImage() == null
			// || "null".equals(shopCarDTO.getImage())
			// || "".equals(shopCarDTO.getImage())) {
			// returnModel.setStatu(ReturnStatus.ParamError);
			// returnModel.setStatusreson("参数image错误");
			// return returnModel;
			// }
			// proTitle
			shopCarDTO.setProTitle(obj.getString("proTitle"));
			if (shopCarDTO.getProTitle() == null || "".equals(shopCarDTO.getProTitle()) || "null".equals(shopCarDTO.getProTitle())) {
				returnModel.setStatu(ReturnStatus.ParamError);
				returnModel.setStatusreson("参数proTitle错误");
				return returnModel;
			}
			// property
			shopCarDTO.setProperty1(obj.getString("property"));
			String property = shopCarDTO.getProperty1().replace("-1", "默认");
			shopCarDTO.setProperty1(property);
			// if(shopCarDTO.getProperty1() == null ||
			// "-1".equals(shopCarDTO.getProperty1()) ||
			// "null".equals(shopCarDTO.getProperty1()))
			// {
			// shopCarDTO.setProperty1("默认");
			// }
			// if (shopCarDTO.getProperty1() == null
			// || "".equals(shopCarDTO.getProperty1())
			// || "null".equals(shopCarDTO.getProperty1())) {
			// returnModel.setStatu(ReturnStatus.ParamError);
			// returnModel.setStatusreson("参数property错误");
			// return returnModel;
			// }

			// if (shopCarDTO.getProperty1() == null
			// || "".equals(shopCarDTO.getProperty1())
			// || "null".equals(shopCarDTO.getProperty1())) {
			// returnModel.setStatu(ReturnStatus.ParamError);
			// returnModel.setStatusreson("参数property错误");
			// return returnModel;
			// }
			// 订单类型
			shopCarDTO.setBuyType((short) obj.getInt("buyType"));
			if (shopCarDTO.getBuyType() <= 0) {
				returnModel.setStatu(ReturnStatus.ParamError);
				returnModel.setStatusreson("参数BuyType错误");
				return returnModel;
			}
			// styleId（款式id）
			shopCarDTO.setStyleId(obj.getLong("styleId"));
			if (shopCarDTO.getStyleId() <= 0) {
				returnModel.setStatu(ReturnStatus.ParamError);
				returnModel.setStatusreson("参数styleId错误");
				return returnModel;
			}
			// source(区)
			shopCarDTO.setSource((short) obj.getInt("source"));
			if (shopCarDTO.getSource() < 0 || shopCarDTO.getSource() > 2) {
				returnModel.setStatu(ReturnStatus.ParamError);
				returnModel.setStatusreson("参数source错误");
				return returnModel;
			}
			/*
			 * //制作人 try {
			 * shopCarDTO.setMakerWeiID(obj.getLong("sharePageProducer")); }
			 * catch(Exception ex) { shopCarDTO.setMakerWeiID(null); } //分享人 try
			 * { shopCarDTO.setSharerWeiID((long)obj.getLong("shareOne")); }
			 * catch(Exception ex) { shopCarDTO.setSharerWeiID(null); }
			 */

			returnModel.setStatu(ReturnStatus.Success);
			returnModel.setBasemodle(shopCarDTO);
		} catch (Exception ex) {
			returnModel.setStatu(ReturnStatus.SystemError);
			returnModel.setBasemodle(ex);
			returnModel.setStatusreson(ex.getMessage());
		}
		return returnModel;
	}

	@Override
	public ReturnModel getShoppingCartList(long weiId) {
		return cartNewService.find_ShoppingCartList(weiId);
//		ReturnModel returnModel = new ReturnModel();
//		List<PClassProducts> pClassProductsList = new ArrayList<PClassProducts>();
//		// 购物车列表
//		List<TShoppingCar> tShoppingCarList = iBasicTShoppingCarMgtDAO.getTShoppingCarList(weiId);
//		if (tShoppingCarList == null || tShoppingCarList.size() == 0) {
//			returnModel.setBasemodle(new ArrayList<ShoppingCar>());
//			returnModel.setStatu(ReturnStatus.Success);
//			return returnModel;
//		}
//		// 供应商微店号List
//		List<Long> supplierWeiIdList = new ArrayList<Long>();
//		// 产品Id List
//		List<Long> productIdList = new ArrayList<Long>();
//		// 产品款式Id list
//		List<Long> styleIdList = new ArrayList<Long>();
//		if (tShoppingCarList != null && tShoppingCarList.size() > 0) {
//			for (TShoppingCar item : tShoppingCarList) {
//				supplierWeiIdList.add(item.getSupplierWeiId() != null ? item.getSupplierWeiId() : -1);
//				productIdList.add(item.getProNum() != null ? item.getProNum() : -1);
//				styleIdList.add(item.getStyleId() != null ? item.getStyleId() : -1);
//			}
//			//
//			// 上架表
//			pClassProductsList = iBasicPClassProductsMgtDAO.getPClassProductsList(supplierWeiIdList, productIdList);
//			// 产品列表
//			List<PProducts> pProductsList = iBasicPProductsMgtDAO.getPProductsList(productIdList);
//			// 查找款式列表
//			List<PProductStyles> pProductStylesList = iBasicPProductStylesMgtDAO.getPProductStyles(styleIdList);
//			for (TShoppingCar item : tShoppingCarList) {
//				// 供应商微店号
//				long supplierWeiId = item.getSupplierWeiId() != null ? item.getSupplierWeiId() : -11;
//				// 产品id
//				long productId = item.getProNum() != null ? item.getProNum() : -12;
//				// 款式id
//				long styleId = item.getStyleId() != null ? item.getStyleId() : -13;
//				// 该产品是否在上架表
//				boolean isPClassProducts = false;
//				// 该产品是否在产品表
//				boolean isProduct = false;
//				for (PClassProducts pClassProducts : pClassProductsList) {
//					long pSupplierWeiId = pClassProducts.getSupplierweiId() != null ? pClassProducts.getSupplierweiId() : -21;
//					long pProductId = pClassProducts.getProductId() != null ? pClassProducts.getProductId() : -31;
//					short status = pClassProducts.getState() != null ? pClassProducts.getState() : -41;
//					// 供应商微店号和产品id相等
//					if (supplierWeiId == pSupplierWeiId && productId == pProductId && status == Short.parseShort(String.valueOf(ProductShelveStatu.OnShelf))) {
//						isPClassProducts = true;
//						break;
//					}
//				}
//
//				/*----------产品是否在限时抢购中----------------*/
//				AActProductsShowTime act = activityService.getAActProductsShowTime(productId, true);
//				if (act != null) {
//					AActivityProducts actProducts = baseDAO.get(AActivityProducts.class, act.getProActId());
//					if (actProducts != null && actProducts.getState() == Short.parseShort(ActProductVerState.Ok.toString())) {
//						String keyName = "BuyLimitCount_" + weiId + "_" + act.getActPid() + "_" + act.getProductId();
//						Integer lv = (Integer) RedisUtil.getObject(keyName);
//						int counts = (lv == null ? 0 : lv);
//						if (counts >= 5) {
//							item.setCount(0);
//							// iBasicTShoppingCarMgtDAO.updateTShoppingCar(item.getScid(),
//							// 0);
//						} else {
//							int count = item.getCount();
//
//							if (count + counts > 5) {
//								// iBasicTShoppingCarMgtDAO.updateTShoppingCar(item.getScid(),
//								// 5-counts);
//								item.setCount(5 - counts);
//							} else {
//								// iBasicTShoppingCarMgtDAO.updateTShoppingCar(item.getScid(),
//								// count);
//								item.setCount(count);
//							}
//						}
//
//					}
//				}
//
//				// 如果在上架表找不到修改购物车状态
//				if (isPClassProducts == false) {
//					// 修改购物车状态无效
//					// iBasicTShoppingCarMgtDAO.updateTShoppingCarStatus(item.getScid(),
//					// (short) 0);
//					item.setStatus((short) 0);
//				}
//				for (PProducts pProducts : pProductsList) {
//					// 主键不可能为空
//					long pProductId = pProducts.getProductId();
//					short state = pProducts.getState() != null ? pProducts.getState() : -11;
//					if (pProductId == productId && (state == Short.parseShort(String.valueOf(ProductStatusEnum.Showing)))) {
//						isProduct = true;
//						break;
//					}
//				}
//				//
//				if (isProduct == false) {
//					// 修改购物车状态无效
//					// iBasicTShoppingCarMgtDAO.updateTShoppingCarStatus(item.getScid(),
//					// (short) 0);
//					item.setStatus((short) 0);
//				}
//				boolean haveStyle = false;
//				for (PProductStyles pProductStyles : pProductStylesList) {
//					// 款式id不相等（主键不可能为空）
//					if (styleId == pProductStyles.getStylesId()) {
//						haveStyle = true;
//						break;
//					}
//				}
//				if (haveStyle == false) {
//					// 修改购物车状态无效(找不到这个款式)
//					// iBasicTShoppingCarMgtDAO.updateTShoppingCarStatus(item.getScid(),
//					// (short) 0);
//					item.setStatus((short) 0);
//				}
//
//				baseDAO.update(item);
//			}
//		}
//
//		// 进货购物类型和铺货购物类型的供应商微店号list
//		List<Long> stockAndDistirbutionWeiIDList = new ArrayList<Long>();
//		// 成交微店号list
//		List<Long> shopWeiIdList = new ArrayList<Long>();
//		// 批发单
//		boolean wholesale = false;
//		// 批发单微店号
//		List<Long> wholesaleWeiIdList = new ArrayList<Long>();
//		// // 循环添加供应商微店号
//		// for (TShoppingCar item : tShoppingCarList) {
//		// // 供应商微店号list
//		// supplierWeiIdList.add(item.getSupplierWeiId() != null ? item
//		// .getSupplierWeiId() : -1);
//		// // 店铺微店号list
//		// shopWeiIdList.add(item.getSellerWeiId() != null ? item
//		// .getSellerWeiId() : -1);
//		// // 购物车类型
//		// short buyType = item.getBuyType() != null ? item.getBuyType() : -11;
//		// // 进货和铺货类型的供应商的微店号
//		// if (buyType == Short.parseShort(String
//		// .valueOf(ShoppingCartTypeEnum.Jinhuo))
//		// || buyType == Short.parseShort(String
//		// .valueOf(ShoppingCartTypeEnum.Puhuo))) {
//		// stockAndDistirbutionWeiIDList
//		// .add(item.getSupplierWeiId() != null ? item
//		// .getSupplierWeiId() : -1);
//		// }
//		// if (buyType == Short.parseShort(String
//		// .valueOf(ShoppingCartTypeEnum.Wholesale))) {
//		// // 是否有批发单
//		// wholesale = true;
//		// // 批发单供应商微店号
//		// wholesaleWeiIdList.add(item.getSupplierWeiId() != null ? item
//		// .getSupplierWeiId() : -1);
//		// }
//		// productIdList.add(item.getProNum() != null ? item.getProNum() : -1);
//		// // 款式id
//		// styleIdList.add(item.getStyleId() != null ? item.getStyleId() : -1);
//		// }
//		// 去除重复所有供应商微店号
//		supplierWeiIdList = getNoDuplicateList(supplierWeiIdList);
//		// 进货和铺货类型的供应商的微店号去掉重复值
//		stockAndDistirbutionWeiIDList = getNoDuplicateList(stockAndDistirbutionWeiIDList);
//		// 批发单供应商微店号去掉重复值
//		wholesaleWeiIdList = getNoDuplicateList(wholesaleWeiIdList);
//		// 产品id去掉重复
//		productIdList = getNoDuplicateList(productIdList);
//		return getShoppingCartList(styleIdList, shopWeiIdList, supplierWeiIdList, weiId, tShoppingCarList, stockAndDistirbutionWeiIDList, productIdList, wholesale, wholesaleWeiIdList);
	}

	@Override
	public List<Long> getNoDuplicateList(List<Long> list) {
		// TODO Auto-generated method stub
		HashSet h = new HashSet(list);
		list.clear();
		list.addAll(h);
		return list;
	}

	@Override
	public ReturnModel getShoppingCartList(List<Long> styleIdList, List<Long> shopWeiIdList, List<Long> weiIdList, long weiId, List<TShoppingCar> tShoppingCarList, List<Long> stockAndDistirbutionWeiIDList, List<Long> productIdList, boolean wholesale, List<Long> wholesaleWeiIdList) {
		// TODO Auto-generated method stub
		ReturnModel returnModel = new ReturnModel();
		// List<TShoppingCar> tShoppingCarSecondList = tShoppingCarList;
		// 最终返回对象
		List<ShoppingCar> resultList = new ArrayList<ShoppingCar>();
		// 查找平台号供应商(进货单和铺货的weiIdList)
		List<UPlatformSupplyer> uPlatformSupplyerList = iBasicUPlatformSupplyerMgtDAO.getUPlatformSupplyerList(stockAndDistirbutionWeiIDList);
		// 查找品牌号(进货单和铺货的weiIdList)
		List<UBrandSupplyer> uBrandSupplyerList = iBasicUBrandSupplyerMgtDAO.getUBrandSupplyerList(stockAndDistirbutionWeiIDList);
		// 查找店铺名
		List<UShopInfo> uShopInfoList = iBasicUShopInfoMgtDAO.getBasicUShopInfoList(weiIdList);
		short supplierStatus = Short.parseShort(String.valueOf(SupplierStatusEnum.PayIn));
		// 查找云商通供应商表
		List<UYunSupplier> uYunSupplierList = iBasicUYunSupplierMgtDAO.getUYunSupplierList(weiIdList, supplierStatus);
		// 查找供应商信息表
		List<USupplyer> uSupplyerList = iBasicUSupplyerMgtDAO.getUSupplyerList(weiIdList);
		// 查找批发号供应商列表
		List<UBatchSupplyer> uBatchSupplyerList = iBasicUBatchSupplyerMgtDAO.getBasicUBatchSupplyerList(weiIdList, supplierStatus);
		// 查找微店用户表
		List<UWeiSeller> uWeiSellerList = iBasicUWeiSellerMgtDAO.getUWeiSellerList(weiIdList);
		// 成交微店号名称查询list
		List<UPlatformSupplyer> shopUPlatformSupplyerList = iBasicUPlatformSupplyerMgtDAO.getUPlatformSupplyerList(shopWeiIdList);
		List<UBrandSupplyer> shopUBrandSupplyerList = iBasicUBrandSupplyerMgtDAO.getUBrandSupplyerList(shopWeiIdList);
		List<UShopInfo> shopUShopInfoList = iBasicUShopInfoMgtDAO.getBasicUShopInfoList(shopWeiIdList);
		List<UYunSupplier> shopUYunSupplierList = iBasicUYunSupplierMgtDAO.getUYunSupplierList(shopWeiIdList, supplierStatus);
		List<USupplyer> shopUSupplyerList = iBasicUSupplyerMgtDAO.getUSupplyerList(shopWeiIdList);
		List<UBatchSupplyer> shopUBatchSupplyerList = iBasicUBatchSupplyerMgtDAO.getBasicUBatchSupplyerList(shopWeiIdList, supplierStatus);
		List<UWeiSeller> shopUWeiSellerList = iBasicUWeiSellerMgtDAO.getUWeiSellerList(shopWeiIdList);
		// 招商需求关系表(进货单和铺货的weiIdList)
		List<USupplyDemand> uSupplyDemandList = iBasicUSupplyDemandMgtDAO.getUSupplyDemandList(stockAndDistirbutionWeiIDList);
		// 招商需求产品关系表
		List<UDemandProduct> uDemandProductList = iBasicUDemandProductMgtDAO.getUDemandProductListByProductId(productIdList);
		// 落地店list
		// USupplyChannel shopUSupplyChannel =
		// iBasicUSupplyChannelMgtDAO.getUSupplyChannel(weiId, (short)2,
		// (short)1);
		List<USupplyChannel> shopUSupplyChannelList = iBasicUSupplyChannelMgtDAO.getUSupplyChannelLsitByWeiIdAndChannelTypeAndStatus(weiId, (short) 2, (short) 1);
		// 代理商
		// List<USupplyChannel> agentUSupplyChannelList =
		// iBasicUSupplyChannelMgtDAO
		// .getUSupplyChannelLsitByWeiIdAndChannelTypeAndStatus(weiId,
		// (short) 1, (short) 1);
		// ....款式开始
		// 款式kvlist
		List<PProductStyleKv> pProductStyleKVList = iBasicPProductStyleKvMgtDAO.getPProductStyleKvByProductIdAndStyleId(styleIdList, productIdList);
		// 款式keyList
		List<PProductSellKey> pProductSellKeyList = iBasicPProductSellKeyMgtDAO.getPProductSellKeyByProductId(productIdList);
		// 取出attrivalueId
		List<Long> attributeIdList = new ArrayList<Long>();
		if (pProductSellKeyList != null && pProductSellKeyList.size() > 0) {
			for (PProductSellKey pProductSellKey : pProductSellKeyList) {
				// 主键不需要判断是否为空
				attributeIdList.add(pProductSellKey.getAttributeId());
			}
		}
		// 款式value
		List<PProductSellValue> pProductSellValueList = iBasicPProductSellValueMgtDAO.getPProductSellValueByProductIdAndAttributeId(productIdList, attributeIdList);
		// ....款式结束
		// 上架表list
		List<PClassProducts> pClassProductsList = new ArrayList<PClassProducts>();
		// 批发价的链接map
		Map<String, Long> wholesaleLink = new HashMap<String, Long>();
		List<PShevleBatchPrice> pShevleBatchPriceList = new ArrayList<PShevleBatchPrice>();
		// 判断有没有批发单
		if (wholesale == true) {
			List<Long> idList = new ArrayList<Long>();
			// 查询上架表
			pClassProductsList = iBasicPClassProductsMgtDAO.getPClassProductsList(wholesaleWeiIdList, productIdList);
			if (pClassProductsList != null && pClassProductsList.size() > 0) {
				for (PClassProducts pClassProducts : pClassProductsList) {
					// 产品上架id主键(不用判断是否为空)
					idList.add(pClassProducts.getId());
					String key = (pClassProducts.getSupplierweiId() != null ? String.valueOf(pClassProducts.getSupplierweiId()) : "-11") + String.valueOf(pClassProducts.getProductId());
					wholesaleLink.put(key, pClassProducts.getId());
				}
				// 上架批发价格表List(idList是主键的list不肯能为空值)
				pShevleBatchPriceList = iBasicPShevleBatchPriceMgtDAO.getPShevleBatchPrice(idList);
			}
		}
		// 购物车对象键值对
		Map<String, List<Long>> supProductMap = new HashMap<String, List<Long>>();
		// 供应商和购物车单类型
		List<String> supplier = new ArrayList<String>();
		// 供应商和购物车单类型和
		// 产品键值对
		Map<Long, List<Long>> productStyleMap = new HashMap<Long, List<Long>>();
		// 进货单需求id的键值对
		// 循环购物车列表
		short jinhuoEnum = Short.parseShort(String.valueOf(ShoppingCartTypeEnum.Jinhuo));
		// 循环购物车列表
		for (int i = 0; i < tShoppingCarList.size(); i++) {
			TShoppingCar item = tShoppingCarList.get(i);
			// 购物车类型
			short buyType = item.getBuyType() != null ? item.getBuyType() : -11;
			// 供应商微店号
			long supplierWeiId = item.getSupplierWeiId() != null ? item.getSupplierWeiId() : -12;
			// 成交微店号
			long shopWeiId = item.getShopWeiID() != null ? item.getShopWeiID() : -18;
			// 产品编号
			long productId = item.getProNum() != null ? item.getProNum() : -13;
			String supType = String.valueOf(supplierWeiId + "_" + buyType);
			// 判断同个供应商的同个订单
			if (!listContains(supplier, supType)) {
				// 如果是进货单
				if (buyType == jinhuoEnum) {
					// 招商需求id
					int demandId = getDemandId(productId, uDemandProductList);
					// 键值
					supType = String.valueOf(supType + "_" + demandId);
				}
				// 添加同个供应商的同个订单
				// 添加同个供应商的同个订单
				supplier.add(supType);
				// 产品Id list
				List<Long> productList = new ArrayList<Long>();
				productList.add(productId);
				// 添加产品id List
				supProductMap.put(supType, productList);
				// 款式id List
				List<Long> styleList = new ArrayList<Long>();
				styleList.add(item.getStyleId());
				productStyleMap.put(productId, styleList);
				// 以下就是添加一个TShoppingCar 实体
				// ***
				resultList.add(createShoppingCartItem(pProductStyleKVList, pProductSellKeyList, pProductSellValueList, shopWeiId, shopUPlatformSupplyerList, shopUBrandSupplyerList, shopUShopInfoList, shopUYunSupplierList, shopUSupplyerList, shopUWeiSellerList, shopUBatchSupplyerList, supplierWeiId,
						uPlatformSupplyerList, uBrandSupplyerList, uShopInfoList, uYunSupplierList, uSupplyerList, uWeiSellerList, uBatchSupplyerList, buyType, item, productId, uSupplyDemandList, uDemandProductList, shopUSupplyChannelList, pClassProductsList, pShevleBatchPriceList, wholesaleLink));
			} else {
				// 如果是进货单
				if (buyType == jinhuoEnum) {
					// 招商需求id
					int demandId = getDemandId(productId, uDemandProductList);
					// 键值
					supType = String.valueOf(supType + "_" + demandId);
					// 供应商购物类型相等，判断产品的需求id相等
					if (supplier.contains(supType)) {
						// 找出对应购物车订单实体
						ShoppingCar shopItem = resultList.get(supplier.indexOf(supType));// 找到供应商订单
						// 找到产品idList
						List<Long> tempProductList = supProductMap.get(supType);
						// 判断有没有相同的产品
						// 同个供应商，是进货单或铺货单
						// 添加产品或款式
						shopItem = addProductOrStyle(pProductStyleKVList, pProductSellKeyList, pProductSellValueList, tempProductList, productId, shopItem, productStyleMap, item, supProductMap, supType, buyType, pClassProductsList, supplierWeiId, pShevleBatchPriceList, wholesaleLink);
						// 给购物车订单列表重置对象
						resultList.set(supplier.indexOf(supType), shopItem);
					} else {
						// 添加同个供应商的同个订单
						supplier.add(supType);
						// 产品Id list
						List<Long> productList = new ArrayList<Long>();
						productList.add(productId);
						// 添加产品id List
						supProductMap.put(supType, productList);
						// 款式id List
						List<Long> styleList = new ArrayList<Long>();
						styleList.add(item.getStyleId());
						productStyleMap.put(productId, styleList);
						// 以下就是添加一个TShoppingCar 实体
						// ***
						resultList.add(createShoppingCartItem(pProductStyleKVList, pProductSellKeyList, pProductSellValueList, shopWeiId, shopUPlatformSupplyerList, shopUBrandSupplyerList, shopUShopInfoList, shopUYunSupplierList, shopUSupplyerList, shopUWeiSellerList, shopUBatchSupplyerList,
								supplierWeiId, uPlatformSupplyerList, uBrandSupplyerList, uShopInfoList, uYunSupplierList, uSupplyerList, uWeiSellerList, uBatchSupplyerList, buyType, item, productId, uSupplyDemandList, uDemandProductList, shopUSupplyChannelList, pClassProductsList,
								pShevleBatchPriceList, wholesaleLink));
					}
				} else {
					// 找出对应购物车订单实体
					ShoppingCar shopItem = resultList.get(supplier.indexOf(supType));// 找到供应商订单
					// 找到产品idList
					List<Long> tempProductList = supProductMap.get(supType);
					// 判断有没有相同的产品
					// 同个供应商，是进货单或铺货单
					// 添加产品或款式
					shopItem = addProductOrStyle(pProductStyleKVList, pProductSellKeyList, pProductSellValueList, tempProductList, productId, shopItem, productStyleMap, item, supProductMap, supType, buyType, pClassProductsList, supplierWeiId, pShevleBatchPriceList, wholesaleLink);
					// 给购物车订单列表重置对象
					resultList.set(supplier.indexOf(supType), shopItem);
				}
			}
		}
		returnModel.setStatu(ReturnStatus.Success);
		returnModel.setBasemodle(resultList);
		return returnModel;
	}

	@Override
	public List<UDemandProduct> getUDemandProductList(List<Long> weiIdList) {
		// TODO Auto-generated method stub
		// List<Object[]> objectList = iBasicUDemandProductMgtDAO
		// .getUDemandProductListForUSupplyDemandWeiId(weiIdList);
		List<Object[]> objectList = new ArrayList<Object[]>();
		List<UDemandProduct> uDemandProductList = new ArrayList<UDemandProduct>();
		if (objectList != null && objectList.size() > 0) {
			for (int i = 0; i < objectList.size(); i++) {
				Object[] item = objectList.get(i);
				UDemandProduct uDemandProduct = new UDemandProduct();
				// 主键不用判断是否为空
				uDemandProduct.setDpid((int) item[0]);
				// 招商需求id
				uDemandProduct.setDemandId(item[1] != null ? (int) item[1] : -1);
				// 产品id
				uDemandProduct.setProductId(item[2] != null ? Long.parseLong(String.valueOf(item[2])) : -1);
				// 创建时间
				uDemandProduct.setCreateTime(item[2] != null ? DateUtils.parseDate(String.valueOf(item[3])) : null);
				uDemandProductList.add(uDemandProduct);
			}
		}
		return uDemandProductList;
	}

	@Override
	public int getDemandId(long productId, List<UDemandProduct> uDemandProductList) {
		// TODO Auto-generated method stub
		for (UDemandProduct item : uDemandProductList) { // 产品id
			long secondProductId = item.getProductId() != null ? item.getProductId() : -11;
			if (productId == secondProductId) {
				return item.getDemandId() != null ? item.getDemandId() : -1;
			}
		}
		return -1;
	}

	@Override
	public double getOrderAmout(int demandId, List<USupplyDemand> uSupplyDemandList) {
		// TODO Auto-generated method stub
		for (USupplyDemand item : uSupplyDemandList) {
			// 招商需求id
			if (demandId == item.getDemandId()) {
				return item.getOrderAmout() != null ? item.getOrderAmout() : 0.0;
			}
		}
		return 0.0;
	}

	@Override
	public short getIsFirstOrder(List<USupplyChannel> shopUSupplyChannelList, int demandId) {
		// TODO Auto-generated method stub
		short result = 1;
		if (shopUSupplyChannelList == null || shopUSupplyChannelList.size() < 1) {
			return result;
		}
		// 判断是否是这个产品的落地店
		for (USupplyChannel item : shopUSupplyChannelList) {
			int secondDemandId = item.getDemandId() != null ? item.getDemandId() : -11;
			if (secondDemandId == demandId) {
				return 0;
			}
		}
		return result;
	}

	/**
	 * 返回产品
	 */
	@Override
	public ShoppingCarList getProduct(TShoppingCar tShoppingCar) {
		// TODO Auto-generated method stub
		ShoppingCarList product = new ShoppingCarList();
		// 产品编号
		product.setProNum(tShoppingCar.getProNum());
		return product;
	}

	@Override
	public Style getStyle(TShoppingCar tShoppingCar, List<PProductStyleKv> pProductStyleKVList, List<PProductSellKey> pProductSellKeyList, List<PProductSellValue> pProductSellValueList) {
		// TODO Auto-generated method stub
		Style model = new Style();
		// 是否启用
		model.setStatus(tShoppingCar.getStatus() != null ? tShoppingCar.getStatus() : 0);
		// 主键
		model.setScid(tShoppingCar.getScid() != null ? tShoppingCar.getScid() : -1);
		// 款式id
		model.setStyleId(tShoppingCar.getStyleId() != null ? tShoppingCar.getStyleId() : -1);
		// modify by tan 20160512 增加活动价格判断

		AActProductsShowTime ast = activityService.getAActProductsShowTime(tShoppingCar.getProNum(), true);

		if (ast != null) // 在展示时间端内
		{
			model.setIsActivity((short) 1);// 在活动时间内
			AActShowProducts aps = baseDAO.get(AActShowProducts.class, ast.getProActId());
			if (aps != null)// 产品有效
			{
				int tcount = tShoppingCar.getCount() != null ? tShoppingCar.getCount() : 0;
				model.setActivityPrice(aps.getPrice());
				int count = aps.getStockCount() == null ? 0 : aps.getStockCount();
				if (count <= tcount) {
					model.setCount(count);
				} else {
					model.setCount(tcount);
				}
			} else {
				model.setIsActivity((short) 0);
				// 数量
				model.setCount(tShoppingCar.getCount() != null ? tShoppingCar.getCount() : 0);

			}
			// 价格
			model.setPrice(tShoppingCar.getPrice() != null ? tShoppingCar.getPrice() : 0.0);
		} else {
			model.setIsActivity((short) 0);
			// 数量
			model.setCount(tShoppingCar.getCount() != null ? tShoppingCar.getCount() : 0);
			// 价格
			model.setPrice(tShoppingCar.getPrice() != null ? tShoppingCar.getPrice() : 0.0);
		}
		// 属性
		model.setProperty(tShoppingCar.getProperty1() != null ? tShoppingCar.getProperty1() : "");
		// 图片
		model.setImage(tShoppingCar.getImage() != null ? ImgDomain.GetFullImgUrl(tShoppingCar.getImage()) : "");
		// 产品标题
		model.setProTitle(tShoppingCar.getProTitle() != null ? tShoppingCar.getProTitle() : "");
		// 成交微店号
		model.setTradeWeiId(tShoppingCar.getSellerWeiId() != null ? tShoppingCar.getSellerWeiId() : -1);
		// 设置keyvlaue值
		model.setpProductStyleKVVOList(getPProductStyleKVVOList(pProductStyleKVList, pProductSellKeyList, pProductSellValueList, tShoppingCar.getProNum() != null ? tShoppingCar.getProNum() : -1, model.getStyleId()));
		String property = model.getProperty().replace("-1", "默认");
		model.setProperty(property);
		// 区
		model.setSource(tShoppingCar.getSource() != null ? tShoppingCar.getSource() : 0);

		model.setShareOne(tShoppingCar.getSharerWeiID() != null ? tShoppingCar.getSharerWeiID() : 0L);

		model.setSharePageProducer(tShoppingCar.getMakerWeiID() != null ? tShoppingCar.getMakerWeiID() : 0L);
		model.setSharePageId(tShoppingCar.getShareID() != null ? tShoppingCar.getShareID() : 0L);

		PProducts product = iBasicTShoppingCarMgtDAO.getProductByID(tShoppingCar.getProNum());
		if (product != null) {
			model.setDisplayPrice(CommonMethod.getInstance().getDisplayPrice(product.getDefaultPrice(), product.getOriginalPrice(), product.getPercent()));
		}

		return model;
	}

	@Override
	public ReturnModel updateTShoppingCarState(long weiId) {
		// TODO Auto-generated method stub
		ReturnModel returnModel = new ReturnModel();
		List<PClassProducts> pClassProductsList = new ArrayList<PClassProducts>();
		// 购物车列表
		List<TShoppingCar> tShoppingCarList = iBasicTShoppingCarMgtDAO.getTShoppingCarList(weiId);
		// 供应商微店号List
		List<Long> supplierWeiIdList = new ArrayList<Long>();
		// 产品Id List
		List<Long> productIdList = new ArrayList<Long>();
		// 产品款式Id list
		List<Long> styleIdList = new ArrayList<Long>();
		if (tShoppingCarList != null && tShoppingCarList.size() > 0) {
			for (TShoppingCar item : tShoppingCarList) {
				supplierWeiIdList.add(item.getSupplierWeiId() != null ? item.getSupplierWeiId() : -1);
				productIdList.add(item.getProNum() != null ? item.getProNum() : -1);
				styleIdList.add(item.getStyleId() != null ? item.getStyleId() : -1);
			}
			//
			// 上架表
			pClassProductsList = iBasicPClassProductsMgtDAO.getPClassProductsList(supplierWeiIdList, productIdList);
			
			List<PBrandShevle> pBrandShevleList =iBasicPClassProductsMgtDAO.getPBrandShevleList(productIdList);
			// 产品列表
			List<PProducts> pProductsList = iBasicPProductsMgtDAO.getPProductsList(productIdList);
			// 查找款式列表
			List<PProductStyles> pProductStylesList = iBasicPProductStylesMgtDAO.getPProductStyles(styleIdList);

			// 查找代理商List
			// List<USupplyChannel> uSupplyChannelList =
			// iBasicUSupplyChannelMgtDAO
			// .getUSupplyChannelLsitByWeiIdAndChannelTypeAndStatus(weiId,
			// (short) 1, (short) 1);
			// List<Integer> demandIdList = new ArrayList<Integer>();
			// // 找出招商需求id
			// if (uSupplyChannelList != null && uSupplyChannelList.size() > 0)
			// {
			// for (USupplyChannel uSupplyChannel : uSupplyChannelList) {
			// demandIdList
			// .add(uSupplyChannel.getDemandId() != null ? uSupplyChannel
			// .getDemandId() : -11);
			// }
			// }
			// 招商需求与产品关系表
			// List<UDemandProduct> uDemandProductList =
			// iBasicUDemandProductMgtDAO
			// .getUDemandProductListByProductIdAndDemandId(productIdList,
			// demandIdList);
			for (TShoppingCar item : tShoppingCarList) {
				// 供应商微店号
				long supplierWeiId = item.getSupplierWeiId() != null ? item.getSupplierWeiId() : -11;
				// 产品id
				long productId = item.getProNum() != null ? item.getProNum() : -12;
				// 款式id
				long styleId = item.getStyleId() != null ? item.getStyleId() : -13;
				// 该产品是否在上架表
				boolean isPClassProducts = false;
				// 该产品是否在产品表
				boolean isProduct = false;
				for (PClassProducts pClassProducts : pClassProductsList) {
					long pSupplierWeiId = pClassProducts.getSupplierweiId() != null ? pClassProducts.getSupplierweiId() : -21;
					long pProductId = pClassProducts.getProductId() != null ? pClassProducts.getProductId() : -31;
					short status = pClassProducts.getState() != null ? pClassProducts.getState() : -41;
					// 供应商微店号和产品id相等
					if (supplierWeiId == pSupplierWeiId && productId == pProductId && status == Short.parseShort(String.valueOf(ProductShelveStatu.OnShelf))) {
						isPClassProducts = true;
						break;
					}
				}
				if(!isPClassProducts)
				{
					for(PBrandShevle pb:pBrandShevleList)
					{
						if(productId == pb.getProductId().longValue() && pb.getType() == Short.parseShort(String.valueOf(ProductShelveStatu.OnShelf)))
						{
							isPClassProducts =true;
							break;
						}
					}
				}
				/*----------产品是否在限时抢购中----------------*/
				AActProductsShowTime act = activityService.getAActProductsShowTime(productId, true);
				if (act != null) {
					AActivityProducts actProducts = baseDAO.get(AActivityProducts.class, act.getProActId());
					if (actProducts != null && actProducts.getState() == Short.parseShort(ActProductVerState.Ok.toString())) {
						String keyName = "BuyLimitCount_" + weiId + "_" + act.getActPid() + "_" + act.getProductId();
						Integer lv = (Integer) RedisUtil.getObject(keyName);
						int counts = (lv == null ? 0 : lv);
						// int counts=RedisUtil.getObject(keyName)==null ? 0
						// :(int)RedisUtil.getObject(keyName);
						if (counts >= 5) {
							iBasicTShoppingCarMgtDAO.updateTShoppingCar(item.getScid(), 0);
						} else {
							int count = item.getCount();

							if (count + counts > 5) {
								iBasicTShoppingCarMgtDAO.updateTShoppingCar(item.getScid(), 5 - counts);
							} else {
								iBasicTShoppingCarMgtDAO.updateTShoppingCar(item.getScid(), count);
							}
						}

					}
				}

				// 如果在上架表找不到修改购物车状态
				if (isPClassProducts == false) {
					// 修改购物车状态无效
					iBasicTShoppingCarMgtDAO.updateTShoppingCarStatus(item.getScid(), (short) 0);
				}
				for (PProducts pProducts : pProductsList) {
					// 主键不可能为空
					long pProductId = pProducts.getProductId();
					short state = pProducts.getState() != null ? pProducts.getState() : -11;
					if (pProductId == productId && (state == Short.parseShort(String.valueOf(ProductStatusEnum.Showing)))) {
						isProduct = true;
						break;
					}
				}
				//
				if (isProduct == false) {
					// 修改购物车状态无效
					iBasicTShoppingCarMgtDAO.updateTShoppingCarStatus(item.getScid(), (short) 0);
				}
				boolean haveStyle = false;
				for (PProductStyles pProductStyles : pProductStylesList) {
					// 款式id不相等（主键不可能为空）
					if (styleId == pProductStyles.getStylesId()) {
						haveStyle = true;
						break;
					}
				}
				if (haveStyle == false) {
					// 修改购物车状态无效(找不到这个款式)
					iBasicTShoppingCarMgtDAO.updateTShoppingCarStatus(item.getScid(), (short) 0);
				}
				//
				// if (item.getBuyType() != null && item.getBuyType() == 5) {
				// // 是铺货单的情况,是否可以购买
				// boolean isUDemandProduct = false;
				// isUDemandProduct = isDemandProduct(uDemandProductList,
				// uSupplyChannelList, weiId, productId);
				// if (isUDemandProduct == false) {
				// // 修改购物车状态无效(找不到这个款式)
				// iBasicTShoppingCarMgtDAO.updateTShoppingCarStatus(
				// item.getScid(), (short) 0);
				// }
				// }
			}
		}
		returnModel.setStatu(ReturnStatus.Success);
		returnModel.setBasemodle(pClassProductsList);
		return returnModel;
	}

	@Override
	public List<WholesalePrice> getWholesalePriceList(long id, List<PShevleBatchPrice> pShevleBatchPriceList) {
		// TODO Auto-generated method stub
		List<WholesalePrice> wholesalePriceList = new ArrayList<WholesalePrice>();
		if (pShevleBatchPriceList != null && pShevleBatchPriceList.size() > 0) {
			for (PShevleBatchPrice item : pShevleBatchPriceList) {
				long pId = item.getId() != null ? item.getId() : -25;
				if (pId == id) {
					WholesalePrice model = new WholesalePrice();
					// 批发个数
					model.setCount(item.getCount() != null ? item.getCount() : 0);
					// 批发价格
					model.setPrice(item.getPrice() != null ? item.getPrice() : 0.0);
					wholesalePriceList.add(model);
				}
			}
		}
		return wholesalePriceList;
	}

	/**
	 * 删除购物车
	 */
	@Override
	public ReturnModel delTShoppingCar(long weiId, long scid) {
		// TODO Auto-generated method stub
		ReturnModel returnModel = new ReturnModel();
		iBasicTShoppingCarMgtDAO.delTShoppingCar(scid, weiId);
		returnModel.setStatu(ReturnStatus.Success);
		returnModel.setStatusreson("成功!");
		return returnModel;
	}

	/**
	 * 修改购物车
	 */
	@Override
	public ReturnModel updateTShoppingCar(long weiId, long scid, long sellerWeiId, int count) {
		// TODO Auto-generated method stub
		ReturnModel returnModel = new ReturnModel();
		// 查找购物车的数据
		TShoppingCar tShoppingCar = iBasicTShoppingCarMgtDAO.getTShoppingCar(scid, weiId, (short) 1);
		if (tShoppingCar != null) { // 购物车类型
			short buyType = tShoppingCar.getBuyType() != null ? tShoppingCar.getBuyType() : -1;
			// 是批发单
			if (buyType == Short.parseShort(String.valueOf(ShoppingCartTypeEnum.Wholesale))) {
				List<Long> scidList = new ArrayList<Long>();
				// 产品id
				long productId = tShoppingCar.getProNum() != null ? tShoppingCar.getProNum() : -1;
				// 供应商微店号
				long supplierWeiId = tShoppingCar.getSupplierWeiId() != null ? tShoppingCar.getSupplierWeiId() : -1;
				// 获取所有的批发价格*****************
				List<PProductBatchPrice> ppbplist = getBatchPricess(supplierWeiId, productId, (short) 1);
				// 判断是否有设置批发价
				if (ppbplist != null && ppbplist.size() > 0) {
					// 查询当前用户、供应商、产品、批发订单
					List<TShoppingCar> tShoppingCarList = iBasicTShoppingCarMgtDAO.getTShoppingCarList(weiId, productId, buyType, supplierWeiId);
					// 修改的款式的数量减去之前的数量
					int pCount = count - (tShoppingCar.getCount() != null ? tShoppingCar.getCount() : 0);
					// 遍历这个产品的所有批发单
					if (tShoppingCarList != null && tShoppingCarList.size() > 0) {
						for (TShoppingCar item : tShoppingCarList) {
							// 这个产品的总个数
							pCount += (item.getCount() != null ? item.getCount() : 0);
							// 主键不用判断是否为空
							scidList.add(item.getScid());
						}
						// 计算批发价
						double tradeprice = getshoppcartpricebycount(pCount, ppbplist);// 初始化批发价
						if (tradeprice <= 0) {
							returnModel.setStatu(ReturnStatus.DataError);
							returnModel.setStatusreson("购买的数量不可以批发!");
							return returnModel;
						}
						// 修改这条数据
						iBasicTShoppingCarMgtDAO.updateTShoppingCar(scid, count);
						// 修改这个产品的批发价(scidList不可能为空，它是主键，前面已经判断过了)
						iBasicTShoppingCarMgtDAO.updateTShoppingCarPrice(scidList, tradeprice);
					} else {
						returnModel.setStatu(ReturnStatus.DataError);
						returnModel.setStatusreson("修改的批发单数据有误!");
						return returnModel;
					}
				} else {
					returnModel.setStatu(ReturnStatus.DataError);
					returnModel.setStatusreson("该商品没有批发价!");
					return returnModel;
				}
			} else // (其它的订单直接修改数量)
			{
				// 判断商品是否在活动期间，活动期间每人只能购买5个
				/*----------产品是否在限时抢购中----------------*/
				AActProductsShowTime act = activityService.getAActProductsShowTime(tShoppingCar.getProNum(), true);
				if (act != null) {
					AActivityProducts actProducts = baseDAO.get(AActivityProducts.class, act.getProActId());
					if (actProducts != null && actProducts.getState() == Short.parseShort(ActProductVerState.Ok.toString())) {
						String keyName = "BuyLimitCount_" + weiId + "_" + act.getActPid() + "_" + act.getProductId();
						Integer lv = (Integer) RedisUtil.getObject(keyName);
						int counts = (lv == null ? 0 : lv);
						// int counts=RedisUtil.getObject(keyName)==null ? 0
						// :(int)RedisUtil.getObject(keyName);
						if (counts >= 5) {
							returnModel.setStatu(ReturnStatus.DataError);
							returnModel.setStatusreson("同个商品每人每次活动最多只能抢购5件！");
							return returnModel;
						}
						int oldcount = tShoppingCar.getCount();

						if (count < oldcount) // 减操作
						{
							if (count + counts > 5) {
								iBasicTShoppingCarMgtDAO.updateTShoppingCar(scid, 5 - counts);
								returnModel.setStatu(ReturnStatus.DataError);
								returnModel.setStatusreson("同个商品每人每次活动最多只能抢购5件！");
								return returnModel;
							} else {
								iBasicTShoppingCarMgtDAO.updateTShoppingCar(scid, count);
							}

						} else // 加操作
						{
							if (count + counts > 5) {
								iBasicTShoppingCarMgtDAO.updateTShoppingCar(scid, 5 - counts);
								returnModel.setStatu(ReturnStatus.DataError);
								returnModel.setStatusreson("同个商品每人每次活动最多只能抢购5件！");
								return returnModel;
							}
							iBasicTShoppingCarMgtDAO.updateTShoppingCar(scid, count);
						}

					}
				} else {
					iBasicTShoppingCarMgtDAO.updateTShoppingCar(scid, count);
				}
			}
		} else {
			returnModel.setStatu(ReturnStatus.DataError);
			returnModel.setStatusreson("该商品在购物车不存在");
			return returnModel;
		}
		returnModel.setStatu(ReturnStatus.Success);
		returnModel.setStatusreson("成功!");
		return returnModel;
	}

	@Override
	public ReturnModel addRetail(Short tag, ShopCarDTO shopCarDTO, LoginUser user, long productId, long supplierweiId, String productTitle, String defaultImg, int classId) {
		// TODO Auto-generated method stub
		ReturnModel returnModel = new ReturnModel();
		// 查找平台号
//		UPlatformSupplyer uPlatformSupplyer = iBasicUPlatformSupplyerMgtDAO.getUPlatformSupplyer(supplierweiId, Short.parseShort(String.valueOf(PlatformSupplyerStateEnum.payIn)));
//		// 查找品牌号
//		UBrandSupplyer uBrandSupplyer = iBasicUBrandSupplyerMgtDAO.getUBrandSupplyer(supplierweiId, Short.parseShort(String.valueOf(BrandSupplyerStateEnum.payIn)));
//		// 查找落地店
//		USupplyChannel shopUSupplyChannel = iBasicUSupplyChannelMgtDAO.getUSupplyChannel(supplierweiId, (short) 2, (short) 1);
//		// 查找代理商
//		USupplyChannel agentUSupplyChannel = iBasicUSupplyChannelMgtDAO.getUSupplyChannel(supplierweiId, (short) 1, (short) 1);
//		// 判断供应商身份
//		if (uPlatformSupplyer == null && uBrandSupplyer == null && shopUSupplyChannel == null && agentUSupplyChannel == null && !isSaleProduct(supplierweiId)) {
//			returnModel.setStatu(ReturnStatus.DataError);
//			returnModel.setStatusreson("商家已经退驻，不可以购买!");
//			return returnModel;
//		}
		
		// 判断是否是零售区
		if (shopCarDTO.getSource() != 0&&shopCarDTO.getSource() != 3) {
			returnModel.setStatu(ReturnStatus.DataError);
			returnModel.setStatusreson("不是零售区不可以购买零售订单");
			return returnModel;
		}
		PProducts product=baseDAO.get(PProducts.class, shopCarDTO.getProNum());
		List<TShoppingCar> cartlists=iBasicTShoppingCarMgtDAO.find_TShoppingCar(user.getWeiID(),  shopCarDTO.getStyleId());
		TShoppingCar tShoppingCar =null;
		if(product!=null&&product.getPublishType()!=null&&product.getPublishType()>0){
			shopCarDTO.setSource(Short.parseShort(ShoppingCarSourceEnum.share.toString()));
//			ActProductInfo info= searchDao.get_ProductAct(product.getProductId());
//			if(info!=null&&info.getActiveType()==1){
//				shopCarDTO.setSource(Short.parseShort(ShoppingCarSourceEnum.retail.toString()));
//			}
		}
		if(cartlists!=null&&cartlists.size()>0){
			for (TShoppingCar tt : cartlists) {
				if(tt.getBuyType().intValue()==shopCarDTO.getBuyType().intValue()
						&&tt.getSupplierWeiId().longValue()==shopCarDTO.getSupplierWeiId().longValue()
						&&tt.getSource().longValue()==shopCarDTO.getSource().longValue()
						&&tt.getSellerWeiId().equals(shopCarDTO.getSellerWeiId())){
					if(shopCarDTO.getSource()!=null&&shopCarDTO.getSource().shortValue()==Short.parseShort(ShoppingCarSourceEnum.share.toString())){
						if(shopCarDTO.getSharerWeiID()!=null&&shopCarDTO.getSharerWeiID().longValue()==tt.getSharerWeiID()){
							tShoppingCar=tt;
						}
					}else {
						tShoppingCar=tt;
					}
				}
			}
		}
		
		// 是否零售
//		TShoppingCar tShoppingCar = iBasicTShoppingCarMgtDAO.getTShoppingCar(user.getWeiID(), shopCarDTO.getStyleId(), shopCarDTO.getBuyType(), shopCarDTO.getSellerWeiId(), supplierweiId, shopCarDTO.getSource());
		if (tShoppingCar != null) {// 判断对象是否存在
			tShoppingCar.setCount(shopCarDTO.getCount() + (tShoppingCar.getCount() != null ? tShoppingCar.getCount() : 0));// 现在加以前总数
			iBasicTShoppingCarMgtDAO.updateTShoppingCar(tShoppingCar);// 修改原来数据
		} else {
			// 查询产品款式表
			PProductStyles pProductStyles = iBasicPProductStylesMgtDAO.getPProductStyles(shopCarDTO.getStyleId());
			double stylePrice = 0.0;
			// 判断款式价格是否为空
			if (pProductStyles != null && pProductStyles.getPrice() != null) {
				stylePrice =agentService.getProductPriceByWeiid(user.getWeiID(), shopCarDTO.getStyleId());
			}
			saveShoppingCart(user.getWeiID(), shopCarDTO.getSellerWeiId(), supplierweiId, shopCarDTO.getSellerWeiId(), productTitle, defaultImg, classId, productId, stylePrice, shopCarDTO.getCount(), shopCarDTO.getStyleId(), shopCarDTO.getProperty1(),
					shopCarDTO.getBuyType(), (short)1, new Date(), shopCarDTO.getSource(), shopCarDTO.getMakerWeiID(), shopCarDTO.getSharerWeiID(), shopCarDTO.getShareID()); // 新添加数据
		}
		
		returnModel.setStatu(ReturnStatus.Success);
		returnModel.setStatusreson("成功!");
		return returnModel;
	}

	@Override
	public ReturnModel addWholesale(Short tag, ShopCarDTO shopCarDTO, LoginUser user, long supplierweiId, long productId, String productTitle, String defaultImg, int classId) {
		// TODO Auto-generated method stub
		ReturnModel returnModel = new ReturnModel();
		// 查询代理商
		USupplyChannel uSupplyChannelAgent = iBasicUSupplyChannelMgtDAO.getUSupplyChannel(supplierweiId, Short.parseShort(String.valueOf(SupplyChannelTypeEnum.Agent)), (short) 1);
		// 查询落地店
		USupplyChannel uSupplyChannelLanding = iBasicUSupplyChannelMgtDAO.getUSupplyChannel(supplierweiId, Short.parseShort(String.valueOf(SupplyChannelTypeEnum.ground)), (short) 1);
		// 查询落地店
		if (!isSaleProduct(supplierweiId) && uSupplyChannelAgent == null && uSupplyChannelLanding == null) {
			returnModel.setStatu(ReturnStatus.DataError);
			returnModel.setStatusreson("商家不是供应商不可以购买批发订单");
			return returnModel;
		}
		if (tag != null && BitOperation.judageProductTag(tag, ProductTagType.Wholesale)) {
			// 判断是否是批发订单
			if (shopCarDTO.getSource() != 0) {
				returnModel.setStatu(ReturnStatus.DataError);
				returnModel.setStatusreson("不是零售区不可以购买批发订单");
				return returnModel;
			}
			// 产品是否支持批发
			List<PProductBatchPrice> ppbplist = getBatchPricess(supplierweiId, productId, Short.parseShort(String.valueOf(ProductShelveStatu.OnShelf)));// 获取所有的批发价格
			// 判断是否有设置批发价
			if (ppbplist != null && ppbplist.size() > 0) {
				// 查询购物车里面相同编号的商品
				List<TShoppingCar> tempCarList = iBasicTShoppingCarMgtDAO.getTShoppingCarList(user.getWeiID(), productId, Short.parseShort(ShoppingCartTypeEnum.Wholesale.toString()), supplierweiId);
				Integer pfcount = shopCarDTO.getCount();// 获取要购买的数量
				boolean isupdate = false;// 判断新添加
				TShoppingCar updateTSC = new TShoppingCar();// 修改的购物车数据
				for (TShoppingCar tsc : tempCarList) {// 遍历添加总数量
					pfcount += tsc.getCount();// 加上本次添加的值
					if(tsc.getBuyType()==Short.parseShort(ShoppingCartTypeEnum.Retail.toString())&&tsc.getSource()==Short.parseShort(ShoppingCarSourceEnum.agency.toString())&& tsc.getSellerWeiId().equals(shopCarDTO.getSellerWeiId())){
						isupdate = true;// 表示这个数据是修改购物车
						updateTSC = tsc;// 表示修改的数据
					}
					else if (tsc.getStyleId().equals(shopCarDTO.getStyleId()) ) {// 判断购物车的款式ID是否等新添加的ID
						isupdate = true;// 表示这个数据是修改购物车
						updateTSC = tsc;// 表示修改的数据
					}
				}
				boolean ispfcount = false;// 标志购买数量是否到达批发界限
				Double tradeCount = 0d;
				if(shopCarDTO.getShopCount()!=null){
					tradeCount = getshoppcartpricebycount(shopCarDTO.getShopCount(), ppbplist);
				}
				Double tradeprice = getshoppcartpricebycount(pfcount, ppbplist);// 初始化批发价
				if (tradeprice > 0||tradeCount>0) {
					ispfcount = true;
				}
				if (ispfcount) {
					// 已经赋值过价格，
					if (isupdate) {// 判断是否修改数据
						updateTSC.setCount(updateTSC.getCount() + shopCarDTO.getCount());// 数量相加
						updateTSC.setPrice(tradeprice);// 修改原来的价格
						iBasicTShoppingCarMgtDAO.updateTShoppingCar(updateTSC);// 修改原来数据
					} else {
						// 保存
						saveShoppingCart(user.getWeiID(), shopCarDTO.getSellerWeiId(), supplierweiId, shopCarDTO.getSellerWeiId(), productTitle, ImgDomain.GetFullImgUrl(defaultImg), classId, productId, tradeprice, shopCarDTO.getCount(), shopCarDTO.getStyleId(), shopCarDTO.getProperty1(),
								shopCarDTO.getBuyType(), (short) 1, new Date(), shopCarDTO.getSource(), shopCarDTO.getMakerWeiID(), shopCarDTO.getSharerWeiID(), shopCarDTO.getShareID()); // 新添加数据
					}
					for (TShoppingCar tsc : tempCarList) {// 遍历修改数量
						tsc.setPrice(tradeprice);// 修改原来的价格
						iBasicTShoppingCarMgtDAO.updateTShoppingCar(tsc);// 修改原来数据
					}
				} else {
					returnModel.setStatu(ReturnStatus.DataError);
					returnModel.setStatusreson("购买的数量没能批发");
					return returnModel;
				}
			} else {
				returnModel.setStatu(ReturnStatus.DataError);
				returnModel.setStatusreson(productId + "商品没有设置批发价格");
				return returnModel;
			}
		} else {
			returnModel.setStatu(ReturnStatus.DataError);
			returnModel.setStatusreson(productId + "商品不支持批发");
			return returnModel;
		}
		returnModel.setStatu(ReturnStatus.Success);
		returnModel.setBasemodle("成功!");
		return returnModel;
	}

	@Override
	public ReturnModel addPurchases(LoginUser user, ShopCarDTO shopCarDTO, long productId, long supplierweiId, String productTitle, String defaultImg, int classId) {
		// TODO Auto-generated method stub
		ReturnModel returnModel = new ReturnModel();
		if (shopCarDTO.getSource() != 1 && shopCarDTO.getSource() != 2) {
			returnModel.setStatu(ReturnStatus.DataError);
			returnModel.setStatusreson("进货单必须是代理区或落地区");
			return returnModel;
		}
		// 查询购物车
		double purchasesPrice = 0.0;
		returnModel = getPurchasesPrice(shopCarDTO.getStyleId(), productId, user.getWeiID(), shopCarDTO.getSource());
		if (returnModel.getStatu().equals(ReturnStatus.Success)) {
			purchasesPrice = (double) returnModel.getBasemodle();
		} else {
			return returnModel;
		}
		TShoppingCar temptsc = iBasicTShoppingCarMgtDAO.getTShoppingCar(user.getWeiID(), shopCarDTO.getStyleId(), shopCarDTO.getBuyType(), shopCarDTO.getSellerWeiId(), supplierweiId, shopCarDTO.getSource());
		if (temptsc != null) {// 判断对象是否存在
			temptsc.setCount(shopCarDTO.getCount() + temptsc.getCount());// 现在加以前总数
			// 修改原来数据
			iBasicTShoppingCarMgtDAO.updateTShoppingCar(temptsc);
		} else {
			// 保存
			saveShoppingCart(user.getWeiID(), shopCarDTO.getSellerWeiId(), supplierweiId, shopCarDTO.getSellerWeiId(), productTitle, ImgDomain.GetFullImgUrl(defaultImg), classId, productId, purchasesPrice, shopCarDTO.getCount(), shopCarDTO.getStyleId(), shopCarDTO.getProperty1(),
					shopCarDTO.getBuyType(), (short) 1, new Date(), shopCarDTO.getSource(), shopCarDTO.getMakerWeiID(), shopCarDTO.getSharerWeiID(), shopCarDTO.getShareID()); // 新添加数据
		}
		returnModel.setStatu(ReturnStatus.Success);
		returnModel.setStatusreson("成功!");
		return returnModel;
	}

	@Override
	public ReturnModel addDistirbution(Short tag, LoginUser user, ShopCarDTO shopCarDTO, long productId, long supplierweiId, String productTitle, String defaultImg, int classId) {
		// TODO Auto-generated method stub
		ReturnModel returnModel = new ReturnModel();
		// 判断商品是否支持铺货
		if (tag == null || !BitOperation.judageProductTag(tag, ProductTagType.Distirbution)) {
			returnModel.setStatu(ReturnStatus.DataError);
			returnModel.setStatusreson("商品不支持铺货!");
			return returnModel;
		}
		// 查询是否是代理商
		// 招商需求与产品关系表
		UDemandProduct uDemandProduct = iBasicUDemandProductMgtDAO.getUDemandProductByProductId(productId);
		if (uDemandProduct == null) {
			returnModel.setStatu(ReturnStatus.DataError);
			returnModel.setStatusreson(productId + "这个产品的需求关系表为空!");
			return returnModel;
		}
		List<UDemandProduct> uDemandProductList = new ArrayList<UDemandProduct>();
		uDemandProductList.add(uDemandProduct);
		int demandId = getDemandId(productId, uDemandProductList);
		// 只有代理商才可以添加代理区的产品到购物车
		List<Long> weiIdList = new ArrayList<Long>();
		weiIdList.add(user.getWeiID());
		List<USupplyChannel> agentList = iBasicUSupplyChannelMgtDAO.getUSupplyChannelList(weiIdList, (short) 1, (short) 1);
		// 落地店list
		List<USupplyChannel> landingList = iBasicUSupplyChannelMgtDAO.getUSupplyChannelList(weiIdList, (short) 2, (short) 1);
		if (isAgentOrLandingByDemandId(agentList, demandId) == false && isAgentOrLandingByDemandId(landingList, demandId) == false) {
			returnModel.setStatu(ReturnStatus.DataError);
			returnModel.setStatusreson("您不是这个产品的代理商，也不是这个产品的落地店，不可以购买铺货单");
			return returnModel;
		}
		// 判断是不是平台号或品牌号
		if (judgePlatformAndBrand(supplierweiId)) {
			double tradeprice = 0.0;
			TShoppingCar temptsc = iBasicTShoppingCarMgtDAO.getTShoppingCar(user.getWeiID(), shopCarDTO.getStyleId(), shopCarDTO.getBuyType(), shopCarDTO.getSellerWeiId(), supplierweiId, shopCarDTO.getSource());
			// temptsc = stemptsc;
			returnModel = getListTheShopPrice(shopCarDTO.getStyleId(), productId, user.getWeiID(), shopCarDTO.getSource());
			if (returnModel.getStatu().equals(ReturnStatus.Success)) {
				tradeprice = (double) returnModel.getBasemodle();
			} else {
				return returnModel;
			}
			if (temptsc != null) {// 判断对象是否存在
				temptsc.setCount(shopCarDTO.getCount() + (temptsc.getCount() != null ? temptsc.getCount() : 0));// 现在加以前总数
				// 修改原来数据
				iBasicTShoppingCarMgtDAO.updateTShoppingCar(temptsc);
			} else {
				// 保存// 新添加数据
				saveShoppingCart(user.getWeiID(), shopCarDTO.getSellerWeiId(), supplierweiId, shopCarDTO.getSellerWeiId(), productTitle, ImgDomain.GetFullImgUrl(defaultImg), classId, productId, tradeprice, shopCarDTO.getCount(), shopCarDTO.getStyleId(), shopCarDTO.getProperty1(),
						shopCarDTO.getBuyType(), (short) 1, new Date(), shopCarDTO.getSource(), shopCarDTO.getMakerWeiID(), shopCarDTO.getSharerWeiID(), shopCarDTO.getShareID());
			}
		} else {
			returnModel.setStatu(ReturnStatus.DataError);
			returnModel.setStatusreson("商家不是品牌号或平台号，不可购买铺货!");
			return returnModel;
		}
		returnModel.setStatu(ReturnStatus.Success);
		returnModel.setStatusreson("成功!");
		return returnModel;
	}

	@Override
	public boolean judgePlatformAndBrand(long supplierweiId) {
		// TODO Auto-generated method stub
		// 查找平台号
		UPlatformSupplyer uPlatformSupplyer = iBasicUPlatformSupplyerMgtDAO.getUPlatformSupplyer(supplierweiId, Short.parseShort(String.valueOf(PlatformSupplyerStateEnum.payIn)));
		// 查找品牌号
		UBrandSupplyer uBrandSupplyer = iBasicUBrandSupplyerMgtDAO.getUBrandSupplyer(supplierweiId, Short.parseShort(String.valueOf(BrandSupplyerStateEnum.payIn)));
		if (uPlatformSupplyer != null || uBrandSupplyer != null) {
			return true;
		}
		return false;
	}

	@Override
	public String getName(List<UPlatformSupplyer> uPlatformSupplyerList, List<UBrandSupplyer> uBrandSupplyerList, List<UShopInfo> uShopInfoList, List<UYunSupplier> uYunSupplierList, List<USupplyer> USupplyerList, List<UWeiSeller> uWeiSellerList, long weiId, List<UBatchSupplyer> uBatchSupplyerList) {
		// TODO Auto-generated method stub
		// 是否是平台号
		if (uPlatformSupplyerList != null && uPlatformSupplyerList.size() > 0) {
			for (UPlatformSupplyer item : uPlatformSupplyerList) {
				// item.getWeiId()是主键不可能为空,weiId也不可能为空
				if (item.getWeiId() == weiId) {
					return item.getSupplyName() != null ? item.getSupplyName() : "";
				}
			}
		}
		// 品牌号
		if (uBrandSupplyerList != null && uBrandSupplyerList.size() > 0) {
			for (UBrandSupplyer item : uBrandSupplyerList) {
				// 品牌号的微店号是主键，在这里不可能为空
				if (item.getWeiId() == weiId) {
					return item.getSupplyName() != null ? item.getSupplyName() : "";
				}
			}
		}
		// 店铺名称
		if (uShopInfoList != null && uShopInfoList.size() > 0)
			for (UShopInfo item : uShopInfoList) {
				// 店铺名称的微店号是主键，在这里不可能为空
				if (item.getWeiId() == weiId) {
					return item.getShopName() != null ? item.getShopName() : "";
				}
			}
		// 云商通供应商
		if (uYunSupplierList != null && uYunSupplierList.size() > 0) {
			for (UYunSupplier item : uYunSupplierList) {
				// 云商通的微店号不可能为空
				if (item.getWeiId() == weiId) {
					if (USupplyerList != null && USupplyerList.size() > 0) {
						for (USupplyer model : USupplyerList) {
							// model.weiId是主键不可能为空
							if (weiId == model.getWeiId()) {
								return model.getCompanyName() != null ? model.getCompanyName() : "";
							}
						}
					}
				}
			}
		}
		// 批发号
		if (uBatchSupplyerList != null && uBatchSupplyerList.size() > 0) {
			for (UBatchSupplyer item : uBatchSupplyerList) {
				if (item.getWeiId() == weiId) {
					return item.getShopName() != null ? item.getShopName() : "";
				}
			}
		}
		if (uWeiSellerList != null && uWeiSellerList.size() > 0) {
			for (UWeiSeller item : uWeiSellerList) {
				if (item.getWeiId() == weiId) {
					return item.getWeiName() != null ? item.getWeiName() : "";
				}
			}
		}
		return "";
	}

	@Override
	public List<WholesalePrice> getWholesalePriceList(short buyType, List<PClassProducts> pClassProductsList, long supplierWeiId, long productId, List<PShevleBatchPrice> pShevleBatchPriceList, Map<String, Long> wholesaleLink) {
		// TODO Auto-generated method stub
		// 判断是否是批发订单
		if (buyType == Short.parseShort(String.valueOf(ShoppingCartTypeEnum.Wholesale))) {
			// 赋值批发价
			if (pClassProductsList != null && pClassProductsList.size() > 0) {
				String pKey = String.valueOf(supplierWeiId) + String.valueOf(productId);
				if (wholesaleLink.containsKey(pKey)) {
					// wholesaleLink.get(pKey)得到是上架表的主键
					return getWholesalePriceList(wholesaleLink.get(pKey), pShevleBatchPriceList);
				}
			}
		}
		return null;
	}

	@Override
	public ShoppingCar createShoppingCartItem(List<PProductStyleKv> pProductStyleKVList, List<PProductSellKey> pProductSellKeyList, List<PProductSellValue> pProductSellValueList, long shopWeiId, List<UPlatformSupplyer> shopUPlatformSupplyerList, List<UBrandSupplyer> shopUBrandSupplyerList,
			List<UShopInfo> shopUShopInfoList, List<UYunSupplier> shopUYunSupplierList, List<USupplyer> shopUSupplyerList, List<UWeiSeller> shopUWeiSellerList, List<UBatchSupplyer> shopUBatchSupplyerList, long supplierWeiId, List<UPlatformSupplyer> uPlatformSupplyerList,
			List<UBrandSupplyer> uBrandSupplyerList, List<UShopInfo> uShopInfoList, List<UYunSupplier> uYunSupplierList, List<USupplyer> uSupplyerList, List<UWeiSeller> uWeiSellerList, List<UBatchSupplyer> uBatchSupplyerList, short buyType, TShoppingCar item, long productId,
			List<USupplyDemand> uSupplyDemandList, List<UDemandProduct> uDemandProductList, List<USupplyChannel> shopUSupplyChannelList, List<PClassProducts> pClassProductsList, List<PShevleBatchPrice> pShevleBatchPriceList, Map<String, Long> wholesaleLink) {
		// TODO Auto-generated method stub
		ShoppingCar shoppingCar = new ShoppingCar();
		// 供应商微店号
		shoppingCar.setSupplierWeiId(supplierWeiId);
		// 组合供应商名称(封装一个获取名称方法)
		shoppingCar.setCompanyName(getName(uPlatformSupplyerList, uBrandSupplyerList, uShopInfoList, uYunSupplierList, uSupplyerList, uWeiSellerList, supplierWeiId, uBatchSupplyerList));
		// 成交微店号名称
		shoppingCar.setShopName((getName(shopUPlatformSupplyerList, shopUBrandSupplyerList, shopUShopInfoList, shopUYunSupplierList, shopUSupplyerList, shopUWeiSellerList, shopWeiId, shopUBatchSupplyerList)));
		// 购物车类型
		shoppingCar.setBuyType(buyType);
		// 区
		shoppingCar.setSource(item.getSource() != null ? item.getSource() : 0);
		// 成交微店号
		shoppingCar.setSellerWeiId(item.getSellerWeiId() != null ? item.getSellerWeiId() : -1);
		// 如果是进货铺货单
		if (buyType == Short.parseShort(String.valueOf(String.valueOf(ShoppingCartTypeEnum.Jinhuo))) || buyType == Short.parseShort(String.valueOf(ShoppingCartTypeEnum.Puhuo))) {
			// 招商需求id
			shoppingCar.setDemandId(getDemandId(productId, uDemandProductList));
			// 只有进货单才有（买家在落地区才有可能首单成为落地店）
			if (buyType == Short.parseShort(String.valueOf(ShoppingCartTypeEnum.Jinhuo))) {
				// 首单金额
				shoppingCar.setFirstOrderAmount(getOrderAmout(shoppingCar.getDemandId(), uSupplyDemandList));
				// 是否首单(买家)
				shoppingCar.setIsFirstOrder(getIsFirstOrder(shopUSupplyChannelList, shoppingCar.getDemandId()));

			}
		}

		// 包含产品的list
		List<ShoppingCarList> productItemList = new ArrayList<ShoppingCarList>();
		ShoppingCarList product = new ShoppingCarList();
		// 产品id
		product.setProNum(item.getProNum() != null ? item.getProNum() : -1);
		// 款式list
		List<Style> styleItemList = new ArrayList<Style>();
		// 款式
		Style styleModel = getStyle(item, pProductStyleKVList, pProductSellKeyList, pProductSellValueList);
		styleItemList.add(styleModel);
		// 产品添加款式列表
		product.setStyle(styleItemList);

		// 是否批发购物车订单
		product.setWholesalePriceList(getWholesalePriceList(buyType, pClassProductsList, supplierWeiId, productId, pShevleBatchPriceList, wholesaleLink));
		// 批发价json
		try {
			product.setWholesalePriceListJson(JsonUtil.objectToJsonStr(product.getWholesalePriceList()));

		} catch (Exception ex) {
			product.setWholesalePriceListJson("[]");
		}
		// 产品列表添加产品
		product.setMakerId(item.getMakerWeiID());
		product.setShareId(item.getSharerWeiID());
		// 是否批发购物车订单
		product.setWholesalePriceList(getWholesalePriceList(buyType, pClassProductsList, supplierWeiId, productId, pShevleBatchPriceList, wholesaleLink));
		// 产品列表添加产品
		productItemList.add(product);
		shoppingCar.setShoppingCarList(productItemList);
		return shoppingCar;

	}

	@Override
	public ShoppingCar addProductOrStyle(List<PProductStyleKv> pProductStyleKVList, List<PProductSellKey> pProductSellKeyList, List<PProductSellValue> pProductSellValueList, List<Long> tempProductList, long productId, ShoppingCar shopItem, Map<Long, List<Long>> productStyleMap, TShoppingCar item,
			Map<String, List<Long>> supProductMap, String supType, short buyType, List<PClassProducts> pClassProductsList, long supplierWeiId, List<PShevleBatchPrice> pShevleBatchPriceList, Map<String, Long> wholesaleLink) {
		// TODO Auto-generated method stub
		if (tempProductList.contains(productId)) {

			// 找到产品实体
			ShoppingCarList produtItem = shopItem.getShoppingCarList().get(tempProductList.indexOf(productId));// 找到相同的产品
			// 找到产品的款式list
			List<Long> tempStyleList = productStyleMap.get(productId);
			// 如果款式相同
			if (tempStyleList.contains(item.getStyleId())) {
				// 如果是代理区或落地区，就要添加新的款式，因为进货单和铺货单在不同的区价格不同（添加新的款式
				// 款式相等区相等
				boolean equalStyle = false;
				for (Style style : produtItem.getStyle()) { // style.getSource()
															// 处理过，不可能为空
					short source = style.getSource();
					// 购物车的source
					short secondSource = item.getSource() != null ? item.getSource() : 0;
					// 款式id处理过不可能为空
					long styleId = style.getStyleId();
					if (styleId == item.getStyleId() && source == secondSource) {
						equalStyle = true;
						break;
					}
				}
				if (equalStyle) {
					// 找到相同的款式
					Style styleItem = produtItem.getStyle().get(tempStyleList.indexOf(item.getStyleId()));
					// 只是在款式哪里加上数量
					styleItem.setCount(styleItem.getCount() + (item.getCount() != null ? item.getCount() : 0));
					// 把款式重新赋值
					produtItem.getStyle().set(tempStyleList.indexOf(item.getStyleId()), styleItem);
				} else {
					// 添加款式list
					tempStyleList.add(item.getStyleId());
					productStyleMap.put(productId, tempStyleList);
					// 以下逻辑为添加 产品的一个款式实体
					produtItem.getStyle().add(getStyle(item, pProductStyleKVList, pProductSellKeyList, pProductSellValueList));
				}

			} else {
				// 添加款式list
				tempStyleList.add(item.getStyleId());
				productStyleMap.put(productId, tempStyleList);
				// 以下逻辑为添加 产品的一个款式实体
				produtItem.getStyle().add(getStyle(item, pProductStyleKVList, pProductSellKeyList, pProductSellValueList));
			}
			// 给实体重置产品对象
			shopItem.getShoppingCarList().set(tempProductList.indexOf(productId), produtItem);
		}// 没有这个产品
		else {
			tempProductList.add(productId);
			supProductMap.put(supType, tempProductList);
			List<Long> styleList = new ArrayList<Long>();
			styleList.add(item.getStyleId());
			productStyleMap.put(productId, styleList);
			// 以下的逻辑为添加一个产品实体到
			// 得到产品实体
			ShoppingCarList productItem = getProduct(item);
			List<Style> secondStyleList = new ArrayList<Style>();
			secondStyleList.add(getStyle(item, pProductStyleKVList, pProductSellKeyList, pProductSellValueList));
			// 给产品赋值款式
			productItem.setStyle(secondStyleList);
			// 如果产品有批发价负值批发价
			productItem.setWholesalePriceList(getWholesalePriceList(buyType, pClassProductsList, supplierWeiId, productId, pShevleBatchPriceList, wholesaleLink));
			// 给购物车实体的产品列表添加产品
			shopItem.getShoppingCarList().add(productItem);
		}
		return shopItem;
	}

	@Override
	public int getShoppingCartCount(long weiId, short status) {
		// TODO Auto-generated method stub
		int count = 0;
		List<Object[]> listCount = iBasicTShoppingCarMgtDAO.getTShoppingCarCountByState(weiId, status);
		if (listCount != null && listCount.size() > 0)
			;
		{
			Object n = (Object) listCount.get(0);
			count = Integer.parseInt(String.valueOf(n));
		}
		return count;
	}

	@Override
	public boolean isDemandProduct(List<UDemandProduct> uDemandProductList, List<USupplyChannel> uSupplyChannelList, long weiId, long productId) {
		// TODO Auto-generated method stub
		if (uSupplyChannelList == null || uSupplyChannelList.size() < 1 || uDemandProductList == null || uDemandProductList.size() < 1) {
			return false;
		}
		for (USupplyChannel uSupplyChannel : uSupplyChannelList) {
			long channelWeiId = uSupplyChannel.getWeiId() != null ? uSupplyChannel.getWeiId() : -16;
			// 是代理商
			if (channelWeiId == weiId) {
				// 招商需求id
				int demandId = uSupplyChannel.getDemandId() != null ? uSupplyChannel.getDemandId() : -32;
				for (UDemandProduct uDemandProduct : uDemandProductList) {
					long secondDemandId = uDemandProduct.getDemandId() != null ? uDemandProduct.getDemandId() : -64;
					long secondProductId = uDemandProduct.getProductId() != null ? uDemandProduct.getProductId() : -128;
					if (demandId == secondDemandId && productId == secondProductId) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public List<PProductStyleKVVO> getPProductStyleKVVOList(List<PProductStyleKv> pProductStyleKVList, List<PProductSellKey> pProductSellKeyList, List<PProductSellValue> pProductSellValueList, long productId, long styleId) {
		// TODO Auto-generated method stub
		// 最终返回list
		List<PProductStyleKVVO> pProductStyleKVVOList = new ArrayList<PProductStyleKVVO>();
		if (pProductStyleKVList == null || pProductStyleKVList.size() < 1) {
			return pProductStyleKVVOList;
		}
		for (PProductStyleKv item : pProductStyleKVList) {
			// 比较的产品id
			long secondProductId = item.getProductId() != null ? item.getProductId() : -21;
			// 比较多款式id
			long secondStyleId = item.getStylesId() != null ? item.getStylesId() : -31;
			if (productId == secondProductId && secondStyleId == styleId) {
				// 比较的产品款式id
				PProductStyleKVVO pProductStyleKVVO = new PProductStyleKVVO();
				// 主键不用判断是否为空
				pProductStyleKVVO.setKvid(item.getKvid());
				// 产品id
				pProductStyleKVVO.setProductId(secondProductId);
				// 款式id
				pProductStyleKVVO.setStylesId(secondStyleId);
				// attributeid
				pProductStyleKVVO.setAttributeId(item.getAttributeId() != null ? item.getAttributeId() : -22);
				// keyId
				pProductStyleKVVO.setKeyId(item.getKeyId() != null ? item.getKeyId() : -23);
				// 时间
				pProductStyleKVVO.setCreateTime(item.getCreateTime());
				pProductStyleKVVOList.add(pProductStyleKVVO);
			}
		}
		// 组合 key value
		for (int i = 0; i < pProductStyleKVVOList.size(); i++) {
			PProductStyleKVVO pProductStyleKVVO = pProductStyleKVVOList.get(i);
			// 组合key名称
			pProductStyleKVVO.setAttributeName(getAttributeName(pProductStyleKVVO.getAttributeId() != null ? pProductStyleKVVO.getAttributeId() : -1, pProductSellKeyList));
			// 组合value名称
			pProductStyleKVVO.setKeyIdName(getKeyName(pProductStyleKVVO.getKeyId() != null ? pProductStyleKVVO.getKeyId() : -1, pProductSellValueList));
			pProductStyleKVVOList.set(i, pProductStyleKVVO);
		}
		return pProductStyleKVVOList;
	}

	@Override
	public String getKeyName(long keyId, List<PProductSellValue> pProductSellValueList) {
		// TODO Auto-generated method stub
		if (pProductSellValueList == null || pProductSellValueList.size() < 1)
			return "";
		for (PProductSellValue item : pProductSellValueList) {
			// 这里是主键可以不用判断是否为空
			if (keyId == item.getKeyId()) {
				return item.getValue() != null ? item.getValue() : "";
			}
		}
		return "";
	}

	@Override
	public String getAttributeName(long attributeId, List<PProductSellKey> pProductSellKeyList) {
		// TODO Auto-generated method stub
		if (pProductSellKeyList == null || pProductSellKeyList.size() < 1) {
			return "";
		}
		for (PProductSellKey item : pProductSellKeyList) {
			if (item.getAttributeId() == attributeId) {
				return item.getAttributeName() != null ? item.getAttributeName() : "";
			}
		}
		return "";
	}

	@Override
	public BasicProductStyleParamModel getBasicProductStyleParamModel(long productId, long styleId) {
		// TODO Auto-generated method stub
		// 最终返回对象
		BasicProductStyleParamModel basicProductStyleParamModel = new BasicProductStyleParamModel();
		// 组合款式list
		List<BasicProductStyleParam> pProductStyleParamList = new ArrayList<BasicProductStyleParam>();
		// 产品kvlist
		List<PProductStyleKv> pProductStyleKVList = iBasicPProductStyleKvMgtDAO.getPProductStyleKvByProductIdAndStyleId(styleId, productId);
		// 查询P_ProductSellKey表
		List<PProductSellKey> pProductSellKeyList = iBasicPProductSellKeyMgtDAO.getPProductSellKeyByProductId(productId);
		// attributeIdList
		List<Long> attributeIdList = new ArrayList<Long>();
		for (PProductSellKey item : pProductSellKeyList) {
			// 主键不用判断是否为空
			attributeIdList.add(item.getAttributeId());
		}
		// value值
		List<PProductSellValue> pProductSellValueList = iBasicPProductSellValueMgtDAO.getPProductSellValueByProductIdAndAttributeId(productId, attributeIdList);
		for (PProductSellKey item : pProductSellKeyList) {
			BasicProductStyleParam productStyleParam = new BasicProductStyleParam();
			// key对象
			productStyleParam.setProSellKey(item);
			// value值
			productStyleParam.setProSellValue(getPProductSellValueListByAttributeId(item.getAttributeId() != null ? item.getAttributeId() : -1, pProductSellValueList));
			// 添加值
			pProductStyleParamList.add(productStyleParam);
		}
		// 选中的款式
		List<PProductStyleKVVO> pProductStyleKVVOList = getPProductStyleKVVOList(pProductStyleKVList, pProductSellKeyList, pProductSellValueList, productId, styleId);
		// 所有的款式属性
		basicProductStyleParamModel.setBasicProductStyleParamList(pProductStyleParamList);
		// 已选中的款式属性
		try {
			basicProductStyleParamModel.setJsonPProductStyleKVVOList(JsonUtil.objectToJsonStr(pProductStyleKVVOList));
		} catch (Exception ex) {
			basicProductStyleParamModel.setJsonPProductStyleKVVOList("[]");
		}
		return basicProductStyleParamModel;
	}

	@Override
	public List<PProductSellValue> getPProductSellValueListByAttributeId(long attributeId, List<PProductSellValue> pProductSellValueList) {
		// TODO Auto-generated method stub
		List<PProductSellValue> list = new ArrayList<PProductSellValue>();
		if (pProductSellValueList == null || pProductSellValueList.size() == 0) {
			return list;
		}
		for (PProductSellValue item : pProductSellValueList) {
			long secondAttributeId = item.getAttributeId() != null ? item.getAttributeId() : -21;
			if (secondAttributeId == attributeId) {
				list.add(item);
			}
		}
		return list;
	}

	@Override
	public List<PProductStyleKVVO> getPProductStyleKVVOList(long styleId, long productId) {
		// TODO Auto-generated method stub
		// ....款式开始
		// 款式kvlist
		List<PProductStyleKv> pProductStyleKVList = iBasicPProductStyleKvMgtDAO.getPProductStyleKvByProductIdAndStyleId(styleId, productId);
		// 款式keyList
		List<PProductSellKey> pProductSellKeyList = iBasicPProductSellKeyMgtDAO.getPProductSellKeyByProductId(productId);
		// 取出attrivalueId
		List<Long> attributeIdList = new ArrayList<Long>();
		if (pProductSellKeyList != null && pProductSellKeyList.size() > 0) {
			for (PProductSellKey pProductSellKey : pProductSellKeyList) {
				// 主键不需要判断是否为空
				attributeIdList.add(pProductSellKey.getAttributeId());
			}
		}
		// 款式value
		List<PProductSellValue> pProductSellValueList = iBasicPProductSellValueMgtDAO.getPProductSellValueByProductIdAndAttributeId(productId, attributeIdList);
		// ....款式结束
		return getPProductStyleKVVOList(pProductStyleKVList, pProductSellKeyList, pProductSellValueList, productId, styleId);
	}

	@Override
	public String getAppProductKeyValue(List<PProductStyleKVVO> pProductStyleKVVOList) {
		// TODO Auto-generated method stub
		if (pProductStyleKVVOList == null || pProductStyleKVVOList.size() < 1) {
			return "";
		}
		String str = "";
		for (PProductStyleKVVO item : pProductStyleKVVOList) {
			if (item.getKeyIdName() != null && !"-1".equals(item.getKeyIdName())) {
				str += item.getKeyIdName() + "/";
			} else if (item.getKeyIdName() == null || "".equals(item.getKeyIdName())) {
				str += "默认" + "/";
			} else {
				str += "默认" + "/";
			}
		}
		str = str.substring(0, str.length() - 1);
		return str;
	}

	@Override
	public BalanceReturnModel getBalanceRegular(String scids, long weiId) {
		// TODO Auto-generated method stub
		BalanceReturnModel balanceReturnModel = new BalanceReturnModel();
		String[] scidsArr = scids.split(",");
		List<Long> scidList = new ArrayList<Long>();
		for (String s : scidsArr) {
			if (StringUtils.isNotEmpty(s)) {
				scidList.add(Long.parseLong(s));
			}
		}
		BalanceVO balanceVO = new BalanceVO();
		balanceVO.setCount(0);
		balanceVO.setTotalPrice(0.0);
		// 找出所有购物车订单
		List<TShoppingCar> tShoppingCarList = iBasicTShoppingCarMgtDAO.getTShoppingCarList(weiId, scidList);
		if (tShoppingCarList == null || tShoppingCarList.size() < 1) {
			balanceReturnModel.setStatusreson("没有记录");
			balanceReturnModel.setShopCartBalanceStatus((short) 2);
			return balanceReturnModel;
		}
		// 款式idList
		List<Long> styleIdList = new ArrayList<Long>();
		// 产品idList
		List<Long> productIdList = new ArrayList<Long>();
		// 招商需求微店号list
		List<Long> stockAndDistirbutionWeiIDList = new ArrayList<Long>();
		// 是否有铺货单
		boolean isDistribution = false;
		// 铺货单枚举
		short distributionEnum = Short.parseShort(String.valueOf(ShoppingCartTypeEnum.Puhuo));
		// 进货单枚举
		short purchasesEnum = Short.parseShort(String.valueOf(ShoppingCartTypeEnum.Jinhuo));
		// 铺货单
		for (TShoppingCar item : tShoppingCarList) {
			styleIdList.add(item.getStyleId() != null ? item.getStyleId() : -1);
			productIdList.add(item.getProNum() != null ? item.getProNum() : -1);
			// 购物类型
			short buyType = item.getBuyType() != null ? item.getBuyType() : (short) -1;
			if (distributionEnum == buyType) {
				isDistribution = true;
			}
			// 进货单或铺货单
			if (purchasesEnum == buyType || buyType == distributionEnum) {
				stockAndDistirbutionWeiIDList.add(item.getSupplierWeiId() != null ? item.getSupplierWeiId() : -1);
			}
		}
		// 判断铺货单不能和别的一起结算
		for (TShoppingCar item : tShoppingCarList) {
			short buyType = item.getBuyType() != null ? item.getBuyType() : (short) -1;
			// 不全都是铺货单，提示报错
			if (isDistribution == true && buyType != distributionEnum) {
				// 铺货单不能和别的订单一起结算错误！
				balanceReturnModel.setStatusreson("铺货单不能和别的订单一起结算");
				balanceReturnModel.setShopCartBalanceStatus((short) 3);
				return balanceReturnModel;
			}
		}
		// 招商需求关系表(进货单和铺货的weiIdList)
		List<USupplyDemand> uSupplyDemandList = iBasicUSupplyDemandMgtDAO.getUSupplyDemandList(stockAndDistirbutionWeiIDList);
		// 代理商list
		// List<USupplyChannel> agentSupplierList =
		// iBasicUSupplyChannelMgtDAO.getUSupplyChannelLsitByWeiIdAndChannelTypeAndStatus(weiId,
		// (short)1, (short)2);
		// 落地店list
		List<USupplyChannel> landingSupplierList = iBasicUSupplyChannelMgtDAO.getUSupplyChannelLsitByWeiIdAndChannelTypeAndStatus(weiId, (short) 2, (short) 2);
		// 产品款式
		List<PProductStyles> pProductStylesList = iBasicPProductStylesMgtDAO.getPProductStyles(styleIdList);
		// 招商需求产品关系表
		List<UDemandProduct> uDemandProductList = iBasicUDemandProductMgtDAO.getUDemandProductListByProductId(productIdList);
		for (TShoppingCar item : tShoppingCarList) {
			// 区
			short source = item.getSource() != null ? item.getSource() : -1;
			// 款式id
			long styleId = item.getStyleId() != null ? item.getStyleId() : -1;
			// 购物车类型
			short buyType = item.getBuyType() != null ? item.getBuyType() : -1;
			// 个数
			// int shopCartCount = item.getCount() != null ? item.getCount() :
			// 0;
			switch (buyType) {
			// 1零售
			// case 1:
			// balanceVO.setTotalPrice(balanceVO.getTotalPrice() + shopCartCount
			// * getStylePrice(styleId,pProductStylesList,source));
			// 产品的个数
			// balanceVO.setCount(balanceVO.getCount() + shopCartCount);
			// break;
			// 2预定
			// case 2:
			// break;
			// 批发单
			case 3:
				// 批发单的价格在修改数量的时候验证过，这里直接读
				// balanceVO.setTotalPrice(balanceVO.getTotalPrice() +
				// shopCartCount * (item.getPrice() != null ? item.getPrice() :
				// 0.0));
				// //产品的个数
				// balanceVO.setCount(balanceVO.getCount() + shopCartCount);
				break;
			// 进货单
			case 4:
				// 1判断是否是这个需求的落地店或代理商2代理价必须是已成为代理商才可以购买3.落地价必需是落地店或产品达到首单要求
				long pProductId = item.getProNum() != null ? item.getProNum() : -1;
				// 需求id
				int demandId = getDemandId(pProductId, uDemandProductList);
				switch (source) {
				// 代理区
				case 1:
					// 取出产品的代理价,之前加入购物车的时候判断过是否是代理商
					// 是否是这个需求代理商
					boolean isAgentBuyLandingPrice = judgeAgentOrlanding(demandId, landingSupplierList);
					if (isAgentBuyLandingPrice == false) {
						balanceReturnModel.setStatusreson("您不是代理商，不可以购买进货单代理价!");
						balanceReturnModel.setShopCartBalanceStatus((short) 4);
						return balanceReturnModel;
					}
					// balanceVO.setTotalPrice(balanceVO.getTotalPrice() +
					// shopCartCount *
					// getStylePrice(styleId,pProductStylesList,source));
					break;
				// 落地区
				case 2:
					// 是否是这个需求的落地店
					boolean isLanding = judgeAgentOrlanding(demandId, landingSupplierList);
					boolean isAgent = judgeAgentOrlanding(demandId, landingSupplierList);
					// 是代理商和落地店可以购买落地价
					if (isLanding == false && isAgent == false) {
						// 否满足这需求价格的
						// 成为落地店首单金额
						double firstAmount = getOrderAmout(demandId, uSupplyDemandList);
						// 得到这个招商需求的所有产品
						List<Long> demandProductIdList = getProductIdListBydemandId(demandId, uDemandProductList);
						// 算出这个需求产品的总价格
						double allDemandPrice = getDemandProductTotalprice(demandProductIdList, tShoppingCarList, pProductStylesList);
						// 铺货单不能和别的订单一起结算错误！
						if (allDemandPrice < firstAmount || firstAmount == 0) {
							balanceReturnModel.setStatusreson("您的进货订单不满足落地店的采购标准，请补货后再提交订单!");
							balanceReturnModel.setShopCartBalanceStatus((short) 5);
							// 主键不用判断是否为空
							balanceReturnModel.setBasemodle(item.getScid());
							return balanceReturnModel;
						}
					}
					break;
				// 其它单
				default:
					break;
				}
				break;
			// 铺货单
			case 5:
				// balanceVO.setTotalPrice(balanceVO.getTotalPrice() +
				// shopCartCount*
				// getStylePrice(styleId,pProductStylesList,source));
				// balanceVO.setCount(balanceVO.getCount() + shopCartCount);
				break;
			default:
				break;
			}
		}
		balanceReturnModel.setShopCartBalanceStatus((short) 1);
		balanceReturnModel.setStatusreson("成功");
		balanceReturnModel.setBasemodle(balanceVO);
		return balanceReturnModel;
	}

	@Override
	public boolean judgeAgentOrlanding(int demandId, List<USupplyChannel> agentUSupplyChannelList) {
		// TODO Auto-generated method stub
		if (agentUSupplyChannelList == null || agentUSupplyChannelList.size() < 1) {
			return false;
		}
		for (USupplyChannel item : agentUSupplyChannelList) {
			if (item.getDemandId() != null && item.getDemandId() == demandId) {
				return true;
			}
		}
		return false;
	}

	@Override
	public double getStylePrice(long styleId, List<PProductStyles> list, short source) {
		// TODO Auto-generated method stub
		double price = 0.0;
		if (list == null || list.size() < 1) {
			return price;
		}
		for (PProductStyles item : list) {
			// 这里是主键不用判断是否为空
			if (item.getStylesId() == styleId) {
				switch (source) {
				// 代理
				case 1:
					price = item.getAgencyPrice() != null ? item.getAgencyPrice() : 0;
					break;
				// 落地区
				case 2:
					price = item.getLandPrice() != null ? item.getLandPrice() : 0.0;
					break;
				default:
					PProducts products=baseDAO.get(PProducts.class, item.getProductId());
					if(products!=null&&(products.getPublishType()==null||products.getPublishType()<=0)){
						// modify by tan 修改活动价
						AActProductsShowTime ast = activityService.getAActProductsShowTime(item.getProductId(), true);
						if (ast != null) {
							AActShowProducts aps = baseDAO.get(AActShowProducts.class, ast.getProActId());
							if (aps == null) {
								price = item.getPrice() != null ? item.getPrice() : 0.0;
							} else {
								price = aps.getPrice();
							}
						} else {
							price = item.getPrice() != null ? item.getPrice() : 0.0;
						}
					}else {
						price=item.getPrice() != null ? item.getPrice() : 0d;
					}
					break;
				}
			}
		}
		return price;
	}

	@Override
	public List<Long> getProductIdListBydemandId(long demandId, List<UDemandProduct> list) {
		// TODO Auto-generated method stub
		List<Long> resultList = new ArrayList<Long>();
		if (list == null || list.size() == 0) {
			return resultList;
		}
		for (UDemandProduct item : list) {
			int secondDemandId = item.getDemandId() != null ? item.getDemandId() : -19;
			if (secondDemandId == demandId) {
				// 添加产品id
				resultList.add(item.getProductId() != null ? item.getProductId() : -21);
			}
		}
		return resultList;
	}

	@Override
	public double getDemandProductTotalprice(List<Long> productList, List<TShoppingCar> tShoppingCarList, List<PProductStyles> pProductStylesList) {
		// TODO Auto-generated method stub
		double totalPrice = 0.0;
		if (productList == null || productList.size() < 1 || tShoppingCarList == null || tShoppingCarList.size() < 1) {
			return 0.0;
		}
		for (TShoppingCar tShoppingCar : tShoppingCarList) {
			// 产品id
			long tProductId = tShoppingCar.getProNum() != null ? tShoppingCar.getProNum() : -1;
			// 类型
			short buyType = tShoppingCar.getBuyType() != null ? tShoppingCar.getBuyType() : -1;
			// 区
			short source = tShoppingCar.getSource() != null ? tShoppingCar.getSource() : -1;
			for (long productId : productList) {

				// 是进货单，2落地区3.产品id相等
				if (tProductId == productId && buyType == Short.parseShort(String.valueOf(ShoppingCartTypeEnum.Jinhuo)) && Short.parseShort(String.valueOf(ShoppingCarSourceEnum.Landi)) == source) {
					long styleId = tShoppingCar.getStyleId() != null ? tShoppingCar.getStyleId() : -1;
					int count = tShoppingCar.getCount() != null ? tShoppingCar.getCount() : 0;
					// 这个需求的产品
					totalPrice += (getStylePrice(styleId, pProductStylesList, source) * count);
				}
			}
		}
		return totalPrice;
	}

	@Override
	public boolean listContains(List<String> list, String str) {
		// TODO Auto-generated method stub
		if (list == null || list.size() < 1) {
			return false;
		}
		for (String s : list) {
			if (s.contains(str)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isAgentOrLandingByDemandId(List<USupplyChannel> list, int demandId) {
		// TODO Auto-generated method stub
		if (list == null || list.size() < 1) {
			return false;
		}
		for (USupplyChannel item : list) {
			int secondDemandId = item.getDemandId() != null ? item.getDemandId() : -11;
			if (secondDemandId == demandId) {
				return true;
			}
		}
		return false;
	}

	@Override
	public ReturnModel getBalance(String scids, long weiId) {
		// TODO Auto-generated method stub
		ReturnModel returnModel = new ReturnModel();
		String[] scidsArr = scids.split(",");
		List<Long> scidList = new ArrayList<Long>();
		for (String s : scidsArr) {
			if (StringUtils.isNotEmpty(s)) {
				scidList.add(Long.parseLong(s));
			}
		}
		BalanceVO balanceVO = new BalanceVO();
		balanceVO.setCount(scidList.size());
		balanceVO.setTotalPrice(0.0);
		// 找出所有购物车订单
		List<TShoppingCar> tShoppingCarList = iBasicTShoppingCarMgtDAO.getTShoppingCarList(weiId, scidList);
		if (tShoppingCarList == null || tShoppingCarList.size() < 1) {
			returnModel.setStatu(ReturnStatus.Success);
			returnModel.setStatusreson("成功!");
			returnModel.setBasemodle(balanceVO);
			return returnModel;
		}
		// 款式idList
		List<Long> styleIdList = new ArrayList<Long>();
		for (TShoppingCar item : tShoppingCarList) {
			styleIdList.add(item.getStyleId() != null ? item.getStyleId() : -1);
		}
		List<PProductStyles> pProductStylesList = iBasicPProductStylesMgtDAO.getPProductStyles(styleIdList);
		for (TShoppingCar item : tShoppingCarList) {
			// 区
			short source = item.getSource() != null ? item.getSource() : -1;
			// 款式id
			long styleId = item.getStyleId() != null ? item.getStyleId() : -1;
			// 购物车类型
			short buyType = item.getBuyType() != null ? item.getBuyType() : -1;
			// 个数
			int shopCartCount = item.getCount() != null ? item.getCount() : 0;
			switch (buyType) {
			// 1零售
			case 1:
				balanceVO.setTotalPrice(balanceVO.getTotalPrice()+shopCartCount*item.getPrice()); 
//				balanceVO.setTotalPrice(balanceVO.getTotalPrice() + shopCartCount * getStylePrice(styleId, pProductStylesList, source));
				break;
			// 2预定
			case 2:
				break;
			// 批发单
			case 3:
				// 批发单的价格在修改数量的时候验证过，这里直接读
				balanceVO.setTotalPrice(balanceVO.getTotalPrice() + shopCartCount * (item.getPrice() != null ? item.getPrice() : 0.0));
				break;
			// 进货单
			case 4:
				balanceVO.setTotalPrice(balanceVO.getTotalPrice() + shopCartCount * getStylePrice(styleId, pProductStylesList, source));
				break;
			// 铺货单
			case 5:
				balanceVO.setTotalPrice(balanceVO.getTotalPrice() + shopCartCount * getStylePrice(styleId, pProductStylesList, source));
				break;
			default:
				break;
			}
		}
		returnModel.setStatu(ReturnStatus.Success);
		returnModel.setStatusreson("成功");
		returnModel.setBasemodle(balanceVO);
		return returnModel;
	}

	// 款式价格
	@Override
	public double getStylePrice(PProductStyles pProductStyles, short source) {
		// TODO Auto-generated method stub
		double price = 0.0;
		switch (source) {
		// 代理区
		case 1:
			price = pProductStyles.getAgencyPrice() != null ? pProductStyles.getAgencyPrice() : 0.0;
			break;
		// 落地区
		case 2:
			price = pProductStyles.getLandPrice() != null ? pProductStyles.getLandPrice() : 0.0;
			break;
		// 活动区
		default:
			price = pProductStyles.getPrice() != null ? pProductStyles.getPrice() : 0.0;
			break;
		}
		return price;
	}

	@Override
	public ReturnModel addShoppingCartByStyleList(String json, LoginUser user) {
		// TODO Auto-generated method stub
		ReturnModel returnModel = getShopCarStyleListJson(json, user.getWeiID());
		List<ShopCarDTO> shopCarDTOList = new ArrayList<ShopCarDTO>();
		if (returnModel.getStatu().equals(ReturnStatus.Success)) {
			shopCarDTOList = (List<ShopCarDTO>) returnModel.getBasemodle();
			returnModel.setBasemodle(null);
		} else {
			return returnModel;
		}
		if (shopCarDTOList == null || shopCarDTOList.size() < 1) {
			returnModel.setStatu(ReturnStatus.DataError);
			returnModel.setStatusreson("参数有误!");
			return returnModel;
		}
		Integer count = 0;
		for (ShopCarDTO shopdto : shopCarDTOList) {
			if(shopdto.getCount()!=null&&shopdto.getCount()>=0){
				count += shopdto.getCount();
			}
		}
		for (ShopCarDTO shopCarDTO : shopCarDTOList) {
			// 查询有没有这个产品
			PProducts pProducts = iPProductsMgtDAO.getPProducts(shopCarDTO.getProNum());
			if (pProducts == null || pProducts.getState() != Long.parseLong(String.valueOf(ProductStatusEnum.Showing))) {
				returnModel.setStatu(ReturnStatus.DataError);
				returnModel.setStatusreson(shopCarDTO.getProNum() + "没有这个产品，或这个产品不是展示中");
				return returnModel;
			}
			shopCarDTO.setShopCount(count);
			// 判断产品供应商
			if (pProducts.getSupplierWeiId() == null) {
				returnModel.setStatu(ReturnStatus.DataError);
				returnModel.setStatusreson(shopCarDTO.getProNum() + "这个产品的供应商微店号为空");
				return returnModel;
			}
			// 产品分类id
			if (pProducts.getClassId() == null) {
				returnModel.setStatu(ReturnStatus.DataError);
				returnModel.setStatusreson(shopCarDTO.getProNum() + "这个产品的分类id为空");
				return returnModel;
			}
			// 产品价格
			if (pProducts.getDefaultPrice() == null) {
				returnModel.setStatu(ReturnStatus.DataError);
				returnModel.setStatusreson(shopCarDTO.getProNum() + "这个产品的价格为空!");
				return returnModel;
			}
//			// 查询有没有上架这个产品
//			PClassProducts pClassProducts = iBasicPClassProductsMgtDAO.judageProductIsRacking(pProducts.getSupplierWeiId(), shopCarDTO.getProNum(), Short.parseShort(String.valueOf(ProductShelveStatu.OnShelf)));
//			// 供应商没有上架这个产品或者发货人为空
//			if (pClassProducts == null || pClassProducts.getSendweiId() == null) {
//				returnModel.setStatu(ReturnStatus.DataError);
//				returnModel.setStatusreson(shopCarDTO.getProNum() + "这个产品没有给供应商上架");
//				return returnModel;
//			}
			// 查询款式
			PProductStyles pProductStyles = iBasicPProductStylesMgtDAO.getPProductStyles(shopCarDTO.getStyleId());
			if (pProductStyles == null) {
				returnModel.setStatu(ReturnStatus.DataError);
				returnModel.setStatusreson(shopCarDTO.getStyleId() + "这个款式商品不存在");
				return returnModel;
			}
			returnModel = saveShoppingCart(shopCarDTO, pProducts.getSupplierWeiId(), pProducts.getProductTitle() != null ? pProducts.getProductTitle() : "", pProducts.getDefaultImg() != null ? pProducts.getDefaultImg() : "", pProducts.getClassId(), pProducts.getTag(), pProducts.getProductId(),
					pProducts.getDefaultPrice(), user);
			if(!returnModel.getStatu().equals(ReturnStatus.Success)){
				return returnModel;
			}
		}
		returnModel.setStatu(ReturnStatus.Success);
		returnModel.setStatusreson("成功!");
		return returnModel;
	}

	/**
	 * 添加购物车list
	 */
	@Override
	public ReturnModel getShopCarStyleListJson(String json, long weiId) {
		// TODO Auto-generated method stub
		// 返回对象
		ReturnModel returnModel = new ReturnModel();
		// 添加购物车list
		List<ShopCarDTO> shopCarDTOList = new ArrayList<ShopCarDTO>();
		try {
			JSONArray arrayvalue = new JSONArray().fromObject(json);
			for (int j = 0; j < arrayvalue.size(); j++) // 遍历value
			{
				JSONObject jovalue = (JSONObject) arrayvalue.get(j);
				ShopCarDTO shopCarDTO = new ShopCarDTO();
				// 卖家微店号
				shopCarDTO.setSupplierWeiId(jovalue.getLong("supplierID"));
				if (shopCarDTO.getSupplierWeiId() <= 0) {
					returnModel.setStatu(ReturnStatus.ParamError);
					returnModel.setStatusreson("参数supplierID错误");
					return returnModel;
				}
				// sellerWeiId(成交微店号)
				shopCarDTO.setSellerWeiId(jovalue.getLong("sellerWeiId"));
				if (shopCarDTO.getSellerWeiId() <= 0) {
					shopCarDTO.setSellerWeiId(weiId);
				}
				// proNum(产品id)
				shopCarDTO.setProNum(jovalue.getLong("proNum"));
				if (shopCarDTO.getProNum() <= 0) {
					returnModel.setStatu(ReturnStatus.ParamError);
					returnModel.setStatusreson("参数proNum错误");
					return returnModel;
				}
				// count(产品数量)
				shopCarDTO.setCount(jovalue.getInt("count"));
				if (shopCarDTO.getCount() <= 0) {
					returnModel.setStatu(ReturnStatus.ParamError);
					returnModel.setStatusreson("参数count错误");
					return returnModel;
				}
				// property
				shopCarDTO.setProperty1(jovalue.getString("property"));
				String property = shopCarDTO.getProperty1().replace("-1", "默认");
				shopCarDTO.setProperty1(property);
				// 订单类型
				shopCarDTO.setBuyType((short) jovalue.getInt("buyType"));
				if (shopCarDTO.getBuyType() <= 0) {
					returnModel.setStatu(ReturnStatus.ParamError);
					returnModel.setStatusreson("参数BuyType错误");
					return returnModel;
				}
				// styleId（款式id）
				shopCarDTO.setStyleId(jovalue.getLong("styleId"));
				if (shopCarDTO.getStyleId() <= 0) {
					returnModel.setStatu(ReturnStatus.ParamError);
					returnModel.setStatusreson("参数styleId错误");
					return returnModel;
				}
				// source(区)
				shopCarDTO.setSource((short) jovalue.getInt("source"));
				if (shopCarDTO.getSource() < 0 || shopCarDTO.getSource() > 2) {
					returnModel.setStatu(ReturnStatus.ParamError);
					returnModel.setStatusreson("参数source错误");
					return returnModel;
				}
				// shareId
				shopCarDTO.setMakerWeiID(ParseHelper.toLong(String.valueOf(jovalue.get("makerWeiID"))));// jovalue.getLong("makerWeiID"));
				if (shopCarDTO.getMakerWeiID() < 1) {
					shopCarDTO.setMakerWeiID(111l);
					// returnModel.setStatu(ReturnStatus.ParamError);
					// returnModel.setStatusreson("参数makeId错误");
					// return returnModel;
				}
				// shareId
				shopCarDTO.setSharerWeiID(ParseHelper.toLong(String.valueOf(jovalue.get("sharerWeiID"))));// jovalue.getLong("shareId")
				if (shopCarDTO.getSharerWeiID() < 1) {
					// returnModel.setStatu(ReturnStatus.ParamError);
					// returnModel.setStatusreson("参数shareId错误");
					// return returnModel;
					shopCarDTO.setSharerWeiID(111l);
				}
				// 分享id
				shopCarDTO.setShareID(ParseHelper.toLong(String.valueOf(jovalue.get("shareID"))));// jovalue.getLong("shareId")
				shopCarDTOList.add(shopCarDTO);
			}
			returnModel.setBasemodle(shopCarDTOList);
			returnModel.setStatu(ReturnStatus.Success);
			returnModel.setStatusreson("成功!");
		} catch (Exception ex) {
			returnModel.setBasemodle(ex);
			returnModel.setStatu(ReturnStatus.SystemError);
			returnModel.setStatusreson(String.valueOf(ex));
		}
		return returnModel;
	}

	@Override
	public int getTwoPowInteger(int value) {
		// TODO Auto-generated method stub
		int m = 0;
		int result = 0;
		while (m >= 0) {

			if (Math.pow(2, m) == value) {
				result = m;
				break;
			}
			m++;
		}
		return result;
	}

	@Override
	public List<AActSupplier> getAActSupplier(Long weiID) {
		return baseDAO.find("from AActSupplier where supplyWeiid=?", weiID);
	}

	@Override
	public short isBeb(Long actId) {
		AActivity aat = baseDAO.get(AActivity.class, actId);
		if (aat != null) {
			if (aat.getType() == 1) {
				return 1;
			}
			return 0;
		}
		return 0;
	}
}
