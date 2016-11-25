package com.okwei.bean.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

import com.okwei.bean.enums.ProductStatusEnum;
import com.okwei.bean.vo.product.BrandAgentPrice;
import com.okwei.util.ImgDomain;

/**
 * 产品管理页面的VO
 * 
 * @author Administrator
 *
 */
public class ProductMgtVO implements Serializable {

	/**
	 * @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么)
	 */
	private static final long serialVersionUID = 1L;

	private BigInteger productId; // 产品编号
	private String productTitle; // 产品名称
	private String defaultImg; // 产品图片

	private Integer selledCount;// 销售量@
	private Integer stockCount;// 库存@

	private BigDecimal defaultPrice;// 缺省售价
	// private BigDecimal sellPriceMax;// 最大售价@
	// private BigDecimal sellPriceMin;// 最小售价@

	private BigDecimal defaultConmision;// 缺省佣金
	// private BigDecimal commissionMax;// 最大佣金@
	// private BigDecimal commissionMin;// 最小佣金@

	private BigDecimal bookPrice;// 预订价

	private String batchPrice;// 最低最高批发价区间
	// private BigDecimal rePriceMax;// 最大批发价@
	// private BigDecimal rePriceMin;// 最小批发价@

	private BigDecimal storePrice;// 落地价
	private BigDecimal agentPrice;// 代理商价

	private BigDecimal factoryPrice;// 出厂价
	private BigDecimal advicePrice; // 建议零售价

	private short isHasBatchPrice;// 是否有批发价
	private short isHasStorePrice;// 是否有落地价
	private short isHasAgentPrice;// 是否有代理价

	private Short status;// 产品状态（1上架中，4已下架，3草稿，6审核中(仅针对平台号子供应商)，7审核不通过(仅针对平台号子供应商)）

	private String statusIntro;// 状态描述

	/**
	 * 类型: 0表示分销；1、2、3表示自营(对应于ShelveType枚举值); -1表示页面选择全部
	 */
	private Short type;// 产品类型

	private Short publishType;//是否为分销产品
	private Integer sid;// 店铺分类
	private BigInteger classId;// 因为两个表的shopClassId字段类型不一样
	private String sName;

	private Integer brandId;// 品牌
	private String brandName;

	private BigInteger supplierWeiId;// 供应商
	private String supplierName;

	private Short tag;// 批发、预定、零售

	private BigInteger sjId;// 上架表id
	private Double displayPrice;
	
	private BigDecimal originalPrice;//原价
	
	private BrandAgentPrice brandAgentPrice;
	
	public Short getPublishType() {
		return publishType;
	}

	public void setPublishType(Short publishType) {
		this.publishType = publishType;
	}

	public BigDecimal getOriginalPrice() {
		return originalPrice;
	}

	public void setOriginalPrice(BigDecimal originalPrice) {
		this.originalPrice = originalPrice;
	}

	public BigInteger getProductId() {
		return productId;
	}

	public void setProductId(BigInteger productId) {
		this.productId = productId;
	}

	public String getProductTitle() {
		return productTitle;
	}

	public void setProductTitle(String productTitle) {
		this.productTitle = productTitle;
	}

	public String getDefaultImg() {
		return defaultImg;
	}

	public void setDefaultImg(String defaultImg) {
		this.defaultImg = ImgDomain.GetFullImgUrl(defaultImg, 24);
	}

	public Integer getSelledCount() {
		return selledCount;
	}

	public void setSelledCount(Integer selledCount) {
		this.selledCount = selledCount;
	}

	public Integer getStockCount() {
		return stockCount;
	}

	public void setStockCount(Integer stockCount) {
		this.stockCount = stockCount;
	}

	public BigDecimal getDefaultPrice() {
		return defaultPrice;
	}

	public void setDefaultPrice(BigDecimal defaultPrice) {
		this.defaultPrice = defaultPrice;
	}

	public BigDecimal getDefaultConmision() {
		return defaultConmision;
	}

	public void setDefaultConmision(BigDecimal defaultConmision) {
		this.defaultConmision = defaultConmision;
	}

	public BigDecimal getBookPrice() {
		return bookPrice;
	}

	public void setBookPrice(BigDecimal bookPrice) {
		this.bookPrice = bookPrice;
	}

	public String getBatchPrice() {
		return batchPrice;
	}

	public void setBatchPrice(String batchPrice) {
		this.batchPrice = batchPrice;
		this.isHasBatchPrice = 1;
	}

	public BigDecimal getStorePrice() {
		return storePrice;
	}

	public void setStorePrice(BigDecimal storePrice) {
		this.storePrice = storePrice;
//		this.isHasStorePrice = 1;
	}

	public BigDecimal getAgentPrice() {
		return agentPrice;
	}

	public void setAgentPrice(BigDecimal agentPrice) {
		this.agentPrice = agentPrice;
//		this.isHasAgentPrice = 1;
	}

	public short getIsHasBatchPrice() {
		return isHasBatchPrice;
	}

	public void setIsHasBatchPrice(short isHasBatchPrice) {
		this.isHasBatchPrice = isHasBatchPrice;
	}

	public short getIsHasStorePrice() {
		return isHasStorePrice;
	}

	public void setIsHasStorePrice(short isHasStorePrice) {
		this.isHasStorePrice = isHasStorePrice;
	}

	public short getIsHasAgentPrice() {
		return isHasAgentPrice;
	}

	public void setIsHasAgentPrice(short isHasAgentPrice) {
		this.isHasAgentPrice = isHasAgentPrice;
	}

	public Short getType() {
		return type;
	}

	public void setType(Short type) {
		this.type = type;
	}

	public Integer getSid() {
		return sid;
	}

	public void setSid(Integer sid) {
		this.sid = sid;
	}

	public BigInteger getClassId() {
		return classId;
	}

	public void setClassId(BigInteger classId) {
		this.classId = classId;
	}

	public String getsName() {
		return sName;
	}

	public void setsName(String sName) {
		this.sName = sName;
	}

	public Integer getBrandId() {
		return brandId;
	}

	public void setBrandId(Integer brandId) {
		this.brandId = brandId;
	}

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public BigInteger getSupplierWeiId() {
		return supplierWeiId;
	}

	public void setSupplierWeiId(BigInteger supplierWeiId) {
		this.supplierWeiId = supplierWeiId;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	public Short getTag() {
		return tag;
	}

	public void setTag(Short tag) {
		this.tag = tag;
	}

	public BigInteger getSjId() {
		return sjId;
	}

	public void setSjId(BigInteger sjId) {
		this.sjId = sjId;
	}

	public BigDecimal getFactoryPrice() {
		return factoryPrice;
	}

	public void setFactoryPrice(BigDecimal factoryPrice) {
		this.factoryPrice = factoryPrice;
	}

	public BigDecimal getAdvicePrice() {
		return advicePrice;
	}

	public void setAdvicePrice(BigDecimal advicePrice) {
		this.advicePrice = advicePrice;
	}

	public String getStatusIntro() {
		return statusIntro;
	}

	public void setStatusIntro(String statusIntro) {
		this.statusIntro = statusIntro;
	}

	public Short getStatus() {
		return status;
	}

	public void setStatus(Short status) {
		this.status = status;
	}

	public Double getDisplayPrice() {
		return displayPrice;
	}

	public void setDisplayPrice(Double displayPrice) {
		this.displayPrice = displayPrice;
	}

	public BrandAgentPrice getBrandAgentPrice() {
		return brandAgentPrice;
	}

	public void setBrandAgentPrice(BrandAgentPrice brandAgentPrice) {
		this.brandAgentPrice = brandAgentPrice;
	}

}
