package com.okwei.service.shoppingcart;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.okwei.bean.dto.shoppingcart.ShopCarDTO;
import com.okwei.bean.vo.LoginUser;
import com.okwei.bean.vo.ReturnModel;
import com.okwei.bean.vo.shoppingcart.BalanceReturnModel;
import com.okwei.bean.vo.shoppingcart.BasicProductStyleParamModel;
import com.okwei.bean.vo.shoppingcart.PProductStyleKVVO;
import com.okwei.bean.vo.shoppingcart.BasicProductStyleParam;
import com.okwei.bean.vo.shoppingcart.ShoppingCar;
import com.okwei.bean.vo.shoppingcart.ShoppingCarList;
import com.okwei.bean.vo.shoppingcart.Style;
import com.okwei.bean.vo.shoppingcart.WholesalePrice;
import com.okwei.bean.domain.AActSupplier;
import com.okwei.bean.domain.PClassProducts;
import com.okwei.bean.domain.PProductBatchPrice;
import com.okwei.bean.domain.PProductSellKey;
import com.okwei.bean.domain.PProductSellValue;
import com.okwei.bean.domain.PProductStyleKv;
import com.okwei.bean.domain.PProductStyles;
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
import com.okwei.service.IBaseService;


/**
 * 购物车
 * @author Administrator
 *
 */
public interface IBasicShoppingCartMgtService extends IBaseService {
/**
 * 添加购物车
 * @return
 */
  ReturnModel addShoppingCart(String json,long weiId,LoginUser user,Long sharePageProducer,Long shareOne,Long shareID);
/**
 * 判断是否可以销售产品
 * @param weiId
 * @return
 */
  boolean isSaleProduct(long weiId);
  /**
   * 保存购物车
   * @param shopCarDTO
   * @param supplierweiId
   * @param productTitles
   * @param defaultImg
   * @param classId
   * @param tag
   * @return
   */
  ReturnModel saveShoppingCart(ShopCarDTO shopCarDTO,long supplierweiId,String productTitles,
  String defaultImg,int classId,Short tag,long productId,double price,LoginUser user); 
 /**
  * 查询所有批发号的价格
  * @return
  */
  List<PProductBatchPrice> getBatchPricess(long supplierWeiId,long productId,short status);
  /**
   * 
   * @param count
   * @param pplist
   * @return
   */
  public Double getshoppcartpricebycount(int count, List<PProductBatchPrice> pplist);
  /**
   * 保存购物车
   */
  long saveShoppingCart(long weiId,long sellerWeiId,long supplierWeiId,long shopWeiID,
  String productTitle,String defaultImg,int classId,long productId,double price,int count,
  long styleId,String property1,short buyType,short status,Date time,short source,Long makerWeiID,Long sharerWeiID,Long sharerID);
  /**
   * 返回代理价
   */
  ReturnModel getAngetPrice(int demandId,long productId,long styleId);
  /**
   * 进货单价格
   * @param styleId
   * @param productId
   * @param weiId
   * @param areaType
   * @return
   */
  ReturnModel getPurchasesPrice(long styleId,long productId,long weiId,short areaType);
  /**
   * 铺货单价格
   * @param styleId
   * @param productId
   * @param weiId
   * @param areaType
   * @return
   */
  ReturnModel getListTheShopPrice(long styleId,long productId,long weiId,short areaType);
  /**
   * 接收添加购物车参数
   * @param json
   */
  ReturnModel getShopCarDTO(String json,long weiId);
  /**
   * 查询购物车列表
   * @return
   */
 ReturnModel getShoppingCartList(long weiId);
 /**
  * 去掉重复的微店号
  * @param list
  * @return
  */
 List<Long> getNoDuplicateList(List<Long> list);
 /**
  * 查询购物车列表s
  * @param weiIdList
  * @param weiId
  * @return
  */
 ReturnModel getShoppingCartList(List<Long> styleIdList, List<Long>shopWeiIdList,List<Long>weiIdList,long weiId,List<TShoppingCar> tShoppingCarList,List<Long>stockAndDistirbutionWeiIDList
		 ,List<Long> productIdList,boolean wholesale,List<Long>wholesaleWeiIdList);
 /**
  * 组合招商需求关系列表
  * @param weiIdList
  * @return
  */
 List<UDemandProduct> getUDemandProductList(List<Long>weiIdList);
 /**
  * 招商需求id
  * @param productId
  * @param supplierWeiId
  * @return
  */
 int getDemandId(long productId,List<UDemandProduct> uDemandProductList);
 /**
  * 首单金额
  * @param demandId
  * @return
  */
 double getOrderAmout(int demandId,List<USupplyDemand> uSupplyDemandList);
 /**
  * 是否首单
  * @param demandId
  * @param weiId
  * @return
  */
 short getIsFirstOrder(List<USupplyChannel> shopUSupplyChannelList,int demandId);
 /**
  * 返回产品对象
  * @param tShoppingCar
  * @return
  */
 ShoppingCarList getProduct(TShoppingCar tShoppingCar);
 /**
  * 返回款式对象
  * @param tShoppingCar
  * @return
  */
 Style getStyle(TShoppingCar tShoppingCar,List<PProductStyleKv> pProductStyleKVList,List<PProductSellKey> pProductSellKeyList,List<PProductSellValue> pProductSellValueList);
 /**
  * 更新购物车状态
  * @param weiId
  * @return
  */
 ReturnModel updateTShoppingCarState(long weiId);
 /**
  * 得到批发价list
  * @param id
  * @return
  */
 List<WholesalePrice> getWholesalePriceList(long id,List<PShevleBatchPrice> pShevleBatchPriceList);
 /**
  * 删除购物车
  * @param weiId
  * @param scid
  * @return
  */
 ReturnModel delTShoppingCar(long weiId,long scid);
 /**
  * 修改购物车
  * @param weiId
  * @param scid
  * @param sellerWeiId
  * @return
  */
 ReturnModel updateTShoppingCar(long weiId,long scid,long sellerWeiId,int count);
 /**
  * 零售添加
  * @return
  */
 ReturnModel addRetail(Short tag,ShopCarDTO shopCarDTO,LoginUser user,long productId,
 long supplierweiId,String productTitle,String defaultImg,int classId);
 /**
  * 添加批发单到购物车
  */
  ReturnModel addWholesale(Short tag,ShopCarDTO shopCarDTO,LoginUser user,long supplierweiId,
	long productId,String productTitle,String defaultImg,int classId);
  /**
   * 添加进货单到购物车
   * @return
   */
  ReturnModel addPurchases(LoginUser user,ShopCarDTO shopCarDTO,long productId,
			long supplierweiId,String productTitle,String defaultImg,int classId);
  /**
   * 添加铺货单
   * @param user
   * @param shopCarDTO
   * @param productId
   * @param supplierweiId
   * @param productTitle
   * @param classId
   * @param defaultImg
   * @return
   */
	ReturnModel addDistirbution(Short tag,LoginUser user,ShopCarDTO shopCarDTO,
	long productId,long supplierweiId,String productTitle,String defaultImg,int classId);
    /**
     * 进货单和铺货单判断供应商
     * @return
     */
	boolean judgePlatformAndBrand(long supplierweiId);
	/**
	 * 组合供应商名称
	 * @param uPlatformSupplyerList
	 * @param uBrandSupplyerList
	 * @param uShopInfoList
	 * @param uYunSupplierList
	 * @param USupplyerList
	 * @param uWeiSellerList
	 * @return
	 */
	String getName(List<UPlatformSupplyer> uPlatformSupplyerList,List<UBrandSupplyer> uBrandSupplyerList, 
			List<UShopInfo> uShopInfoList ,List<UYunSupplier> uYunSupplierList,List<USupplyer> USupplyerList,
			List<UWeiSeller> uWeiSellerList ,long weiId,List<UBatchSupplyer> uBatchSupplyerList);
	/**
	 * 是否是批发购物车订单
	 * @return
	 */
	List<WholesalePrice> getWholesalePriceList(short buyType,List<PClassProducts> pClassProductsList,
			long supplierWeiId,long productId,List<PShevleBatchPrice> pShevleBatchPriceList,Map<String,Long> wholesaleLink);
	/**
	 * 组合一个购物车单
	 * @return
	 */
	ShoppingCar createShoppingCartItem(List<PProductStyleKv> pProductStyleKVList,List<PProductSellKey> pProductSellKeyList,List<PProductSellValue> pProductSellValueList,long shopWeiId,List<UPlatformSupplyer> shopUPlatformSupplyerList,List<UBrandSupplyer>shopUBrandSupplyerList,
			List<UShopInfo> shopUShopInfoList,List<UYunSupplier> shopUYunSupplierList,List<USupplyer> shopUSupplyerList,
			List<UWeiSeller> shopUWeiSellerList,List<UBatchSupplyer> shopUBatchSupplyerList,
			long supplierWeiId,List<UPlatformSupplyer> uPlatformSupplyerList,
			List<UBrandSupplyer> uBrandSupplyerList,List<UShopInfo> uShopInfoList,List<UYunSupplier> uYunSupplierList,
			List<USupplyer> uSupplyerList,List<UWeiSeller> uWeiSellerList,List<UBatchSupplyer> uBatchSupplyerList,
			short buyType,TShoppingCar item,long productId,List<USupplyDemand> uSupplyDemandList,List<UDemandProduct> uDemandProductList,
			List<USupplyChannel> shopUSupplyChannelList,List<PClassProducts> pClassProductsList,List<PShevleBatchPrice> pShevleBatchPriceList,
			Map<String,Long> wholesaleLink );
	/**
	 * 添加产品牌货款式
	 * @param tempProductList
	 * @param productId
	 * @param shopItem
	 * @param productStyleMap
	 * @param item
	 * @param supProductMap
	 * @param supType
	 * @param buyType
	 * @param pClassProductsList
	 * @param supplierWeiId
	 * @param pShevleBatchPriceList
	 * @param wholesaleLink
	 * @return
	 */
	public ShoppingCar addProductOrStyle(List<PProductStyleKv> pProductStyleKVList,List<PProductSellKey> pProductSellKeyList,List<PProductSellValue> pProductSellValueList,List<Long> tempProductList,long productId,ShoppingCar shopItem,
			Map<Long,List<Long>> productStyleMap,TShoppingCar item,Map<String,List<Long>> supProductMap,String supType,
			short buyType,List<PClassProducts> pClassProductsList,long supplierWeiId,
			List<PShevleBatchPrice> pShevleBatchPriceList,Map<String,Long> wholesaleLink);
	
	int getShoppingCartCount(long weiId,short status);
	
	boolean isDemandProduct(List<UDemandProduct> uDemandProductList,List<USupplyChannel> uSupplyChannelList,long weiId,long productId);
	/**
	 * 产品牌款式属性
	 * @param pProductStyleKVList
	 * @param pProductSellKeyList
	 * @param pProductSellValueList
	 * @param productId
	 * @param styleId
	 * @return
	 */
	List<PProductStyleKVVO> getPProductStyleKVVOList(List<PProductStyleKv> pProductStyleKVList,List<PProductSellKey> pProductSellKeyList,List<PProductSellValue> pProductSellValueList,long productId,long styleId);
	/**
	 * 找到keyName
	 * @param keyId
	 * @return
	 */
	String getKeyName(long keyId,List<PProductSellValue> pProductSellValueList);
	/**
	 * 找到attributeName;
	 * @param attributeId
	 * @param pProductSellKeyList
	 * @return
	 */
	String getAttributeName(long attributeId,List<PProductSellKey> pProductSellKeyList);
	/**
	 * 产品款式属性
	 * @param productId
	 * @param styleId
	 * @return
	 */
	BasicProductStyleParamModel getBasicProductStyleParamModel(long productId,long styleId);

	/**
	 * 
	 * @param attributeId
	 * @return
	 */
	List<PProductSellValue> getPProductSellValueListByAttributeId(long attributeId,List<PProductSellValue> pProductSellValueList);
	/**
	 * 产品款式销售属性
	 * @param styleId
	 * @param productId
	 * @return
	 */
	List<PProductStyleKVVO> getPProductStyleKVVOList(long styleId,long productId);
	/**
	 * app产品销售属性
	 * @param pProductStyleKVVOList
	 * @return
	 */
	String getAppProductKeyValue(List<PProductStyleKVVO> pProductStyleKVVOList);
	/**
	 * 结算
	 * @return
	 */
	BalanceReturnModel getBalanceRegular(String scids,long weiId);
	/**
	 * 是否是代理商
	 * @param demandId
	 * @param agentUSupplyChannelList
	 * @return
	 */
	boolean judgeAgentOrlanding(int demandId,List<USupplyChannel> agentUSupplyChannelList);
	/**
	 * 
	 * @return
	 */
	double getStylePrice(long styleId,List<PProductStyles> list,short source); 
	//款式价格
	double getStylePrice(PProductStyles pProductStyles,short source);
	/**
	 * 用需求id找产品id
	 * @param demandId
	 * @param list
	 * @return
	 */
	List<Long> getProductIdListBydemandId(long demandId,List<UDemandProduct> list);
	/**
	 * 落地区这需求产品的总价
	 * @param productList
	 * @param tShoppingCarList
	 * @return
	 */
	double getDemandProductTotalprice(List<Long> productList,List<TShoppingCar> tShoppingCarList,List<PProductStyles> pProductStylesList);
	/**
	 * lsit字符串模糊查询
	 * @param list
	 * @param str
	 * @return
	 */
	boolean listContains(List<String> list ,String str);
	//某个需求的代理商或落地店
	boolean isAgentOrLandingByDemandId(List<USupplyChannel> list,int demandId);
	/**
	 * 
	 * @return
	 */
	ReturnModel getBalance(String scids,long weiId);
	/**
	 * 添加购物车（一次添加多个款式）
	 * @param json
	 * @param weiId
	 * @param user
	 * @return
	 */
	ReturnModel addShoppingCartByStyleList(String json,LoginUser user);
	/**
	 * 
	 * @param json
	 * @return
	 */
	ReturnModel getShopCarStyleListJson(String json,long weiId);
	/**
	 * 一个数是2的多少次方
	 * @param value
	 * @return
	 */
	int getTwoPowInteger(int value);
	/**
	 * 查找用户参加的活动id
	 * @param weiID
	 * @return
	 */
	List<AActSupplier> getAActSupplier(Long weiID);
	/**
	 * 判断它是不是828活动，是则返回1；不是返回0；
	 * @param actId
	 * @return
	 */
	short isBeb(Long actId);
}
