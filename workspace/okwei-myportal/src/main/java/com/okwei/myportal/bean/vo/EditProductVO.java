package com.okwei.myportal.bean.vo;

import java.util.List;

import com.okwei.bean.vo.order.ProductBatchPriceVO;
import com.okwei.bean.vo.order.ProductImgVO;
import com.okwei.bean.vo.product.ProductParamVO;
import com.okwei.bean.vo.product.ProductSellKeyVO;
import com.okwei.bean.vo.product.ProductStylesVO;


public class EditProductVO {

	/**
	 * 产品编号
	 */
	private Long productId;
	/**
	 * 产品标题
	 */
	private String productTitle;
	/**
	 * 商品副标题
	 */
	private String productMinTitle;
	
	/**
	 * 分类ID
	 */
	private Integer classId;
	
	/**
	 * 分类信息实体
	 */
	private ProductClassVO classVO;
	/**
	 * 店铺分类ID 一级分类
	 */
	private Integer parentSid;
	/**
	 * 店铺分类ID
	 */
	private Integer sid;
	/**
	 * 产品默认图片
	 */
	private String productImg;
	/**
	 * 零售价
	 */
	private Double retailPrice;
	/**
	 * 价格 零售价+佣金
	 */
	private Double price;
	/**
	 * 佣金
	 */
	private Double commission;
	/**
	 * 供应商weiid
	 */
	private Long supplyerWeiid;
	/**
	 * 邮费模板ID
	 */
	private Integer freightId;
	/**
	 * 商品关键字
	 */
	private String keyWords;
	
	/**
	 * 商品属性模板ID
	 */
	private Integer mID;

	/**
	 * 图片列表
	 */
	private List<ProductImgVO> imgList;
	
	/**
	 * 款式列表
	 */
	private List<ProductStylesVO> styleList;
	
	/**
	 * 款式key列表
	 */
	private List<ProductSellKeyVO> sellKeyList;

	/**
	 * 预定价格
	 */
	private PPreOrderVO preOrder;
	/**
	 * 批发价格列表
	 */
	private List<ProductBatchPriceVO> batchPriceList;
	/**
	 * 商品属性列表
	 */
	private List<ProductParamVO> paramList;
	
	private String pcdes;
	private String appdes;
	private Short state;
	private Integer count;
	private Short tag;
	private Double batchPrice;
	private Double prePrice;
	/**招商需求ID*/
	private Integer demandId;
	/**
	 * 代理价
	 */
	private Double proxyPrice;
	/**
	 * 落地价
	 */
	private Double floorPrice;
	/**
	 * 成本价
	 */
	private Double defaultPrice;
	/**
	 * 建议价
	 */
	private Double advicePrice;
	
	private int type;//产品类型0-主产品1-子供应商产品
	private Integer brandId;//品牌id
	private Double displayPrice;//原价
	private Double supplyPrice;//供货价
	
	
	public Double getSupplyPrice() {
		return supplyPrice;
	}
	public void setSupplyPrice(Double supplyPrice) {
		this.supplyPrice = supplyPrice;
	}
	public Integer getBrandId() {
		return brandId;
	}
	public void setBrandId(Integer brandId) {
		this.brandId = brandId;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public Integer getDemandId() {
		return demandId;
	}
	public void setDemandId(Integer demandId) {
		this.demandId = demandId;
	}
	public Double getBatchPrice() {
		return batchPrice;
	}
	public void setBatchPrice(Double batchPrice) {
		this.batchPrice = batchPrice;
	}
	public Double getPrePrice() {
		return prePrice;
	}
	public void setPrePrice(Double prePrice) {
		this.prePrice = prePrice;
	}
	public Integer getmID() {
		return mID;
	}
	public void setmID(Integer mID) {
		this.mID = mID;
	}

	public ProductClassVO getClassVO() {
		return classVO;
	}
	public void setClassVO(ProductClassVO classVO) {
		this.classVO = classVO;
	}
	public Integer getClassId() {
		return classId;
	}
	public void setClassId(Integer classId) {
		this.classId = classId;
	}
	public Long getProductId() {
		return productId;
	}
	public void setProductId(Long productId) {
		this.productId = productId;
	}
	public String getProductTitle() {
		return productTitle;
	}
	public void setProductTitle(String productTitle) {
		this.productTitle = productTitle;
	}
	public String getProductMinTitle() {
		return productMinTitle;
	}
	public void setProductMinTitle(String productMinTitle) {
		this.productMinTitle = productMinTitle;
	}
	public String getProductImg() {
		return productImg;
	}
	public void setProductImg(String productImg) {
		this.productImg = productImg;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public Double getCommission() {
		return commission;
	}
	public void setCommission(Double commission) {
		this.commission = commission;
	}
	public Long getSupplyerWeiid() {
		return supplyerWeiid;
	}
	public void setSupplyerWeiid(Long supplyerWeiid) {
		this.supplyerWeiid = supplyerWeiid;
	}
	public Integer getSid() {
		return sid;
	}
	public void setSid(Integer sid) {
		this.sid = sid;
	}
	public Integer getFreightId() {
		return freightId;
	}
	public void setFreightId(Integer freightId) {
		this.freightId = freightId;
	}
	public String getKeyWords() {
		return keyWords;
	}
	public void setKeyWords(String keyWords) {
		this.keyWords = keyWords;
	}
	public List<ProductParamVO> getParamList() {
		return paramList;
	}
	public void setParamList(List<ProductParamVO> paramList) {
		this.paramList = paramList;
	}
	public List<ProductImgVO> getImgList() {
		return imgList;
	}
	public void setImgList(List<ProductImgVO> imgList) {
		this.imgList = imgList;
	}
	public List<ProductStylesVO> getStyleList() {
		return styleList;
	}
	public void setStyleList(List<ProductStylesVO> styleList) {
		this.styleList = styleList;
	}

	public List<ProductSellKeyVO> getSellKeyList() {
		return sellKeyList;
	}
	public void setSellKeyList(List<ProductSellKeyVO> sellKeyList) {
		this.sellKeyList = sellKeyList;
	}

	public PPreOrderVO getPreOrder() {
		return preOrder;
	}
	public void setPreOrder(PPreOrderVO preOrder) {
		this.preOrder = preOrder;
	}
	public List<ProductBatchPriceVO> getBatchPriceList() {
		return batchPriceList;
	}
	public void setBatchPriceList(List<ProductBatchPriceVO> batchPriceList) {
		this.batchPriceList = batchPriceList;
	}
	public String getAppdes() {
		return appdes;
	}
	public void setAppdes(String appdes) {
		this.appdes = appdes;
	}
	public String getPcdes() {
		return pcdes;
	}
	public void setPcdes(String pcdes) {
		this.pcdes = pcdes;
	}
	public Short getState() {
		return state;
	}
	public void setState(Short state) {
		this.state = state;
	}
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}
	public Short getTag() {
		return tag;
	}
	public void setTag(Short tag) {
		this.tag = tag;
	}
	public Double getProxyPrice() {
		return proxyPrice;
	}
	public void setProxyPrice(Double proxyPrice) {
		this.proxyPrice = proxyPrice;
	}
	public Double getFloorPrice() {
		return floorPrice;
	}
	public void setFloorPrice(Double floorPrice) {
		this.floorPrice = floorPrice;
	}
	public Double getDefaultPrice() {
		return defaultPrice;
	}
	public void setDefaultPrice(Double defaultPrice) {
		this.defaultPrice = defaultPrice;
	}
	public Double getAdvicePrice() {
		return advicePrice;
	}
	public void setAdvicePrice(Double advicePrice) {
		this.advicePrice = advicePrice;
	}
	public Integer getParentSid() {
		return parentSid;
	}
	public void setParentSid(Integer parentSid) {
		this.parentSid = parentSid;
	}
	public Double getRetailPrice() {
		return retailPrice;
	}
	public void setRetailPrice(Double retailPrice) {
		this.retailPrice = retailPrice;
	}
	public Double getDisplayPrice() {
		return displayPrice;
	}
	public void setDisplayPrice(Double displayPrice) {
		this.displayPrice = displayPrice;
	}
}
