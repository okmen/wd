package com.okwei.service.impl.product;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.okwei.bean.domain.DBrandSupplier;
import com.okwei.bean.domain.DBrands;
import com.okwei.bean.domain.PAgentStock;
import com.okwei.bean.domain.PBrand;
import com.okwei.bean.domain.PClassProducts;
import com.okwei.bean.domain.PPriceShow;
import com.okwei.bean.domain.PProductAssist;
import com.okwei.bean.domain.PProductBatchPrice;
import com.okwei.bean.domain.PProducts;
import com.okwei.bean.domain.PPushShelves;
import com.okwei.bean.domain.PShevleBatchPrice;
import com.okwei.bean.domain.PShopClass;
import com.okwei.bean.domain.TProductIndex;
import com.okwei.bean.domain.UAttention;
import com.okwei.bean.domain.UChildrenUser;
import com.okwei.bean.domain.UDemandProduct;
import com.okwei.bean.domain.USupplyer;
import com.okwei.bean.domain.UUserAssist;
import com.okwei.bean.dto.ProductDTO;
import com.okwei.bean.enums.AgentStatusEnum;
import com.okwei.bean.enums.ProductShelveStatu;
import com.okwei.bean.enums.ProductStatusEnum;
import com.okwei.bean.enums.ProductSubStatusEnum;
import com.okwei.bean.enums.PushTypeEnum;
import com.okwei.bean.enums.ScoreEnum;
import com.okwei.bean.enums.ScoreType;
import com.okwei.bean.enums.ShelveType;
import com.okwei.bean.enums.SupplyChannelTypeEnum;
import com.okwei.bean.enums.UserIdentityType;
import com.okwei.bean.vo.LoginUser;
import com.okwei.bean.vo.ProductMgtVO;
import com.okwei.bean.vo.PushShelvesContentVO;
import com.okwei.bean.vo.ReturnModel;
import com.okwei.bean.vo.ReturnStatus;
import com.okwei.bean.vo.product.BatchPriceM;
import com.okwei.bean.vo.product.BrandAgentPrice;
import com.okwei.bean.vo.product.ProductInfo;
import com.okwei.bean.vo.product.ProductOnShelveModel;
import com.okwei.bean.vo.product.ShelveProduct;
import com.okwei.bean.vo.product.ShelveProductParam;
import com.okwei.common.CommonMethod;
import com.okwei.common.Limit;
import com.okwei.common.PageResult;
import com.okwei.cons.UserPowerConstants;
import com.okwei.dao.agent.IDAgentMgtDao;
import com.okwei.dao.product.IBaseProductDao;
import com.okwei.dao.product.IBasicProductMgtDAO;
import com.okwei.service.IBaseCommonService;
import com.okwei.service.IScoreManagerService;
import com.okwei.service.agent.IDAgentService;
import com.okwei.service.impl.BaseService;
import com.okwei.service.product.IBasicProductMgtService;
import com.okwei.service.product.IHouseService;
import com.okwei.util.BitOperation;
import com.okwei.util.ImgDomain;
import com.okwei.util.ObjectUtil;
import com.okwei.util.RedisUtil;

@Service
public class BasicProductMgtServiceImpl extends BaseService implements IBasicProductMgtService {

	@Autowired
	private IBasicProductMgtDAO productMgtDAO;
	@Autowired
	private IScoreManagerService scoreManagerService;
	@Autowired
	private IHouseService IHouseService;
	@Autowired
	private IDAgentService idAgentService;
	@Autowired
	private IDAgentMgtDao agentDao;
	@Autowired
	private IBaseProductDao productDao;
	@Autowired
	private IBaseCommonService commonService;

	private static Log logger = LogFactory.getLog(BasicProductMgtServiceImpl.class);

	@Override
	public PageResult<ProductMgtVO> getProducts(ProductDTO dto, Limit limit, LoginUser user) throws Exception {
		if (dto.getFromSite() == 1) {// 访问来自app,由于app同用一个shopClassId传一级或二级店铺分类id
			// 由于前端可能输入的店铺分类是一级或二级的，当一级时，需要转换成二级范围进行过滤
 			if (ObjectUtil.isNotEmpty(dto.getShopClassId()) && dto.getShopClassId() > 0) {
				PShopClass ps = super.getById(PShopClass.class, dto.getShopClassId());
				if (null != ps && ps.getLevel() == 1) {
					List<PShopClass> subs = productMgtDAO.getSubShopClassByid(ps.getSid());
					if (null != subs && subs.size() > 0) {
						for (PShopClass sub : subs) {
							dto.getShopClassIds().add(sub.getSid());
						}
					} else {
						dto.getShopClassIds().add(dto.getShopClassId());
					}
				} else {
					dto.getShopClassIds().add(dto.getShopClassId());
				}
			}
		}

		PageResult<ProductMgtVO> result = productMgtDAO.getProducts(dto, limit);
		if (null != result && null != result.getList()) {
			for (ProductMgtVO vo : result.getList()) {
				// 落地店不需要代理价
				if (dto.isLDD()) {
					vo.setAgentPrice(null);
					vo.setIsHasAgentPrice((short) 0);
				}
				// 店铺分类
				if (dto.getStatus() == ProductStatusEnum.Showing || dto.getStatus() == ProductStatusEnum.Drop) {
					if (ObjectUtil.isNotEmpty(vo.getClassId())) {
						PShopClass ps = super.getById(PShopClass.class, Integer.parseInt(vo.getClassId().toString()));
						if (null != ps) {
							if (ps.getParetId() != null && ps.getParetId() > 0) {
								PShopClass ps_parent = super.getById(PShopClass.class, ps.getParetId());
								if (null != ps_parent) {
									vo.setsName(ps_parent.getSname() + "/" + ps.getSname()); // 有二级店铺分类
								}
							} else {
								vo.setsName(ps.getSname()); // 只有一级店铺分类
							}
						}
					}
					if (user != null) {
						// 招商需求
						UDemandProduct dp = productMgtDAO.getUniqueResultByHql("from UDemandProduct where productId=?", new Object[] { Long.parseLong(vo.getProductId().toString()) });
						PProducts prod = productMgtDAO.get(PProducts.class, Long.parseLong(vo.getProductId().toString()));
						// 判断落地代理价格是否可见
						if (dp != null && prod != null) {
							ProductInfo info = new ProductInfo();
							if (IHouseService.checkUserIsAgentOrStore(prod.getSupplierWeiId(), user.getWeiID(), dp.getDemandId(), Short.valueOf(SupplyChannelTypeEnum.Agent.toString()), Short.valueOf(AgentStatusEnum.Normal.toString()))) {
								info.setCurrentUserIsAgent(1);
							}
							if (IHouseService.checkUserIsAgentOrStore(prod.getSupplierWeiId(), user.getWeiID(), dp.getDemandId(), Short.valueOf(SupplyChannelTypeEnum.ground.toString()), Short.valueOf(AgentStatusEnum.Normal.toString()))) {
								info.setCurrentUserIsStore(1);
							}
							// 代理价可见判断
							PPriceShow priceShow = productMgtDAO.get(PPriceShow.class, prod.getSupplierWeiId());
							info = IHouseService.getPriceShow(user, priceShow, info);
							if (info != null) {
								vo.setIsHasAgentPrice((short) info.getDlPriceVisibility());
								vo.setIsHasStorePrice((short) info.getLdPriceVisibility());
							}
							// 增加原价 add by 阿甘 2016.1.26
							vo.setDisplayPrice(CommonMethod.getInstance().getDisplayPrice(prod.getDefaultPrice(), prod.getOriginalPrice(), prod.getPercent()));
						}
					} else {
						vo.setIsHasAgentPrice((short) 0);
						vo.setIsHasStorePrice((short) 0);
					}
				} else {
					if (ObjectUtil.isNotEmpty(vo.getSid())) {
						PShopClass ps = super.getById(PShopClass.class, vo.getSid());
						if (null != ps) {
							if (ps.getParetId() != null && ps.getParetId() > 0) {
								PShopClass ps_parent = super.getById(PShopClass.class, ps.getParetId());
								if (null != ps_parent) {
									vo.setsName(ps_parent.getSname() + "/" + ps.getSname()); // 有二级店铺分类
								}
							} else {
								vo.setsName(ps.getSname()); // 只有一级店铺分类
							}
						}
					}
				}
				// 品牌名称
				if (ObjectUtil.isNotEmpty(vo.getBrandId())) {
					PBrand pb = super.getById(PBrand.class, vo.getBrandId());
					if (null != pb) {
						vo.setBrandName(pb.getBrandName());
					}
				}
				// 供应商名称
				if (ObjectUtil.isNotEmpty(vo.getSupplierWeiId())) {
					USupplyer supplyer = super.getById(USupplyer.class, Long.parseLong(vo.getSupplierWeiId().toString()));
					if (null != supplyer) {
						vo.setSupplierName(supplyer.getCompanyName()); // 供应商名字
					}
				}
				// 类型
				if ((null != vo.getType() && (vo.getType() == 1 || vo.getType() == 2 || vo.getType() == 3)) || dto.getStatus() == ProductStatusEnum.OutLine || dto.getStatus() == ProductStatusEnum.Audit) {// 1、2、3均为自营,申请中/待审核的列表均为自营
					vo.setType((short) 1);
				}
				if (user != null && user.getWeiID() > 0) {
					int i = idAgentService.verBrandSupplier(user.getWeiID());
					if (i == 1 || i == 0 || i == 2) {
						vo.setType((short) 1);
					} else if (i == -1 && vo.getPublishType() != null && vo.getPublishType() == 1) {
						vo.setType((short) 7);
					}
				}
				// 产品状态
				if (vo.getStatus() == null) {
					vo.setStatus(Short.parseShort(dto.getStatus().toString()));
				} else {// 产品状态（1上架中，4已下架，3草稿，6审核中(仅针对平台号子供应商)，7审核不通过(仅针对平台号子供应商)）
					if (vo.getStatus() == 1) {// P_ProductSup的state:1/2对应于
												// ProductStatusEnum类的6/7
						vo.setStatus((short) 6);
						vo.setStatusIntro("申请中");
					} else if (vo.getStatus() == 2) {
						vo.setStatus((short) 7);
						vo.setStatusIntro("不通过(" + vo.getStatusIntro() + ")");
					}
				}
				// 只有销售中，已下架的 才显示库存,销售量
				if (dto.getStatus() == ProductStatusEnum.Showing || dto.getStatus() == ProductStatusEnum.Drop) {
					if (dto.isDLS() || dto.isLDD()) {// 当前用户为代理商或落地店时
						// 从PAgentStock表获取
						PAgentStock pa = productMgtDAO.getPAgentStock(Long.parseLong(vo.getProductId().toString()), Long.parseLong(dto.getWeiId()));
						if (null != pa) {
							vo.setStockCount(pa.getStockCount());
							vo.setSelledCount(pa.getSelledCount());
						}
					} else {
						PProductAssist p = super.getById(PProductAssist.class, Long.parseLong(vo.getProductId().toString()));
						if (null != p) {
							vo.setSelledCount(p.getTotalCount());
						}
					}

					if (!dto.isPTZ()) {// 并且非子供应商时，从P_ShevleBatchPrice表获取批发价区间
						List<PShevleBatchPrice> list = productMgtDAO.getMyBatchPrice(Long.parseLong(vo.getSjId().toString()));
						if (null != list) {
							if (list.size() > 1) {
								String batchPrice = list.get(0).getPrice() + "-" + list.get(list.size() - 1).getPrice();
								vo.setBatchPrice(batchPrice);
							} else if (list.size() == 1) {
								vo.setBatchPrice(list.get(0).getPrice().toString());
							}
						}

					}
				} else if (dto.getStatus() == ProductStatusEnum.OutLine && dto.isPTZ()) {// 草稿箱，非子供应商，从P_ProductBatchPrice表获取批发价区间
					List<PProductBatchPrice> list = productMgtDAO.getBatchPrice(Long.parseLong(vo.getProductId().toString()));
					if (null != list) {
						if (list.size() > 1) {
							String batchPrice = list.get(0).getPirce() + "-" + list.get(list.size() - 1).getPirce();
							vo.setBatchPrice(batchPrice);
						} else if (list.size() == 1) {
							vo.setBatchPrice(list.get(0).getPirce().toString());
						}
					}
				}
			}
		}
		return result;
	}

	@Override
	public PageResult<ProductMgtVO> find_myProducts(Long weiid, Integer systemClassId, Integer shopClassID, int pageIndex, int pageSie) {

		PageResult<PProducts> pageResult = idAgentService.find_Productlist(weiid, shopClassID, shopClassID, pageIndex, pageSie);
//		List<PProductClass> classlist = productDao.find_PProductClassAll();
		if(pageResult!=null&&pageResult.getList()!=null&&pageResult.getList().size()>0){
			List<ProductMgtVO> proList=new ArrayList<ProductMgtVO>();
			for (PProducts pp : pageResult.getList()) {
				ProductMgtVO vo=new ProductMgtVO();
				vo.setProductId(BigInteger.valueOf(pp.getProductId()) ); 
				vo.setProductTitle(pp.getProductTitle());
				vo.setDefaultImg(ImgDomain.GetFullImgUrl(pp.getDefaultImg()));
				vo.setDefaultPrice(BigDecimal.valueOf(pp.getDefaultPrice()==null?0d:pp.getDefaultPrice()));
				vo.setOriginalPrice(BigDecimal.valueOf(pp.getOriginalPrice()==null?0d:pp.getOriginalPrice()));
				vo.setClassId(BigInteger.valueOf(pp.getClassId()));
				vo.setSupplierWeiId(BigInteger.valueOf(pp.getSupplierWeiId()));
				vo.setType((short)7);
				vo.setStatus(pp.getState()); 
//				PProductAssist productAssist=productDao.get(PProductAssist.class, pp.getProductId());
				vo.setStockCount(pp.getCount());
				vo.setSupplierName(commonService.getShopNameByWeiId(pp.getSupplierWeiId()));
				DBrandSupplier ss=productDao.get(DBrandSupplier.class, pp.getSupplierWeiId());
				if(ss!=null){
					DBrands brand=productDao.get(DBrands.class, ss.getBrandId());
					vo.setBrandId(ss.getBrandId());
					vo.setBrandName(brand==null?"":brand.getBrandName());
					
					BrandAgentPrice agentPrice=new BrandAgentPrice();
					int identity= agentDao.getUserIdentityForBrand(weiid, ss.getBrandId());
					if(BitOperation.isIdentity(identity, UserIdentityType.AgentDuke)){
						agentPrice.setDukePrice(pp.getDukePrice());
					}else if (BitOperation.isIdentity(identity, UserIdentityType.AgentDeputyDuke)) {
						agentPrice.setDeputyPrice(pp.getDeputyPrice());
					}else {
						agentPrice.setAgentPrice(pp.getAgentPrice());
					}
//					agentPrice.setSupplyPrice(pp.getSupplyPrice());
				
					vo.setBrandAgentPrice(agentPrice);
				}
				proList.add(vo);
			}
			return new PageResult<ProductMgtVO>(pageResult.getTotalCount(),Limit.buildLimit(pageIndex, pageSie),proList); 
		}
		return new PageResult<ProductMgtVO>();
	}

	@Override
	public List<PShopClass> getSubShopClassByid(Integer parentId) {
		return productMgtDAO.getSubShopClassByid(parentId);
	}

	@Override
	public boolean topProduct(Long productId, Integer shopClassId, LoginUser user) {
		Short max = productMgtDAO.getMaxSort(shopClassId, user.getWeiID());
		if (null != max) {
			List<Long> ids = new ArrayList<Long>();
			ids.add(productId);
			List<PClassProducts> list = productMgtDAO.getClassProducts(ids, user.getWeiID());
			if (null != list && list.size() > 0) {
				max++;
				PClassProducts pp = list.get(0);
				pp.setSort(max);
			}
		}
		return true;
	}

	@Override
	public boolean raiseProduct(String[] productIds, LoginUser user) {
		List<Long> ids = new ArrayList<Long>();
		for (int i = 0; i < productIds.length; i++) {
			ids.add(Long.valueOf(productIds[i]));
		}
		List<PClassProducts> list = productMgtDAO.getClassProducts(ids, user.getWeiID());
		if (null != list) {
			for (PClassProducts p : list) {
				PProducts product = super.getById(PProducts.class, p.getProductId());
				// 设置P_ClassProducts的State为上架状态
				// 如果是自营 直接上架 其他则判断源头是否为上架状态
				if (null != p.getType() && (p.getType() == ShelveType.Other.getNo())) {
					if (product.getState() != Short.parseShort(ProductStatusEnum.Showing.toString())) {
						continue;
					}
				}
				p.setState(Short.parseShort(ProductShelveStatu.OnShelf.toString()));

				// 自营的产品
				if (null != p.getType() && (p.getType() == ShelveType.Self.getNo()) || p.getType() == ShelveType.NoSale.getNo() || p.getType() == ShelveType.NoPay.getNo()) {
					// 更新 pproducts表的state为上架状态
					if (null != product) {
						product.setState(Short.parseShort(ProductStatusEnum.Showing.toString()));
						product.setUpdateTime(new Date());
					}

					if (p.getType() == ShelveType.Self.getNo()) {
						// 更新产品索引
						TProductIndex pi = new TProductIndex();
						pi.setProductId(p.getProductId());
						pi.setStatus((short) 0);
						pi.setType((short) 0);// 0新增1更新2删除
						productMgtDAO.save(pi);
					}
				}
			}
		}
		return true;
	}

	@Override
	public boolean dropProduct(String[] productIds, LoginUser user) {
		List<Long> ids = new ArrayList<Long>();
		for (int i = 0; i < productIds.length; i++) {
			ids.add(Long.valueOf(productIds[i]));
		}
		List<PClassProducts> list = productMgtDAO.getClassProducts(ids, user.getWeiID());
		if (null != list) {
			for (PClassProducts p : list) {
				// 设置P_ClassProducts的State为下架状态
				p.setState(Short.parseShort(ProductShelveStatu.OffShelf.toString()));

				// 自营的产品
				if (null != p.getType() && (p.getType() == ShelveType.Self.getNo()) || p.getType() == ShelveType.NoSale.getNo() || p.getType() == ShelveType.NoPay.getNo()) {
					// 更新 pproducts表的state为下架状态
					PProducts product = super.getById(PProducts.class, p.getProductId());
					if (null != product) {
						product.setState(Short.parseShort(ProductStatusEnum.Drop.toString()));
						product.setUpdateTime(new Date());
					}

					if (p.getType() == ShelveType.Self.getNo()) {
						// P_ClassProducts中下架所有别人分销该产品的上架记录
						List<PClassProducts> pps = productMgtDAO.getClassProducts_other(p.getProductId(), user.getWeiID());
						if (null != pps) {
							for (PClassProducts pp : pps) {
								pp.setState(Short.parseShort(ProductShelveStatu.OffShelf.toString()));
							}
						}
						// 更新产品索引
						TProductIndex pi = new TProductIndex();
						pi.setProductId(p.getProductId());
						pi.setStatus((short) 0);
						pi.setType((short) 2);// 0新增1更新2删除
						productMgtDAO.save(pi);
					}
				}
			}
		}
		return true;
	}

	@Override
	public boolean deleteProduct(String[] productIds, LoginUser user) {
		List<Long> ids = new ArrayList<Long>();
		for (int i = 0; i < productIds.length; i++) {
			ids.add(Long.valueOf(productIds[i]));
		}
		List<PClassProducts> list = productMgtDAO.getClassProducts(ids, user.getWeiID());
		List<Long> tempList = new ArrayList<Long>();
		if (null != list) {
			for (PClassProducts p : list) {
				tempList.add(p.getProductId());
				// 设置P_ClassProducts的State为下架状态
				p.setState(Short.parseShort(ProductShelveStatu.Deleted.toString()));

				// 自营的产品
				if (null != p.getType() && (p.getType() == ShelveType.Self.getNo()) || p.getType() == ShelveType.NoSale.getNo() || p.getType() == ShelveType.NoPay.getNo()) {
					// 更新 pproducts表的state为删除状态
					PProducts product = super.getById(PProducts.class, p.getProductId());
					if (null != product) {
						product.setState(Short.parseShort(ProductStatusEnum.Deleted.toString()));
						product.setUpdateTime(new Date());
					}

					if (p.getType() == ShelveType.Self.getNo()) {
						// P_ClassProducts中下架所有别人分销该产品的上架记录
						List<PClassProducts> pps = productMgtDAO.getClassProducts_other(p.getProductId(), user.getWeiID());
						if (null != pps) {
							for (PClassProducts pp : pps) {
								pp.setState(Short.parseShort(ProductShelveStatu.Deleted.toString()));
							}
						}
						// 更新产品索引
						TProductIndex pi = new TProductIndex();
						pi.setProductId(p.getProductId());
						pi.setStatus((short) 0);
						pi.setType((short) 2);// 0新增1更新2删除
						productMgtDAO.save(pi);
					}
				}
			}
		}

		// 草稿箱的产品,传入的产品列表减去上架表存在的，即是草稿箱的
		ids.removeAll(tempList);
		if (ids.size() > 0) {
			List<PProducts> pps = productMgtDAO.getPProducts(ids, user.getWeiID());
			if (null != pps && pps.size() > 0) {
				for (PProducts pp : pps) {
					pp.setState(Short.parseShort(ProductStatusEnum.Deleted.toString()));
					pp.setUpdateTime(new Date());
				}
			}
		}
		return true;
	}

	@Override
	public boolean moveProduct(String[] productIds, Integer shopClassId, LoginUser user) {
		List<Long> ids = new ArrayList<Long>();
		for (int i = 0; i < productIds.length; i++) {
			ids.add(Long.valueOf(productIds[i]));
		}
		List<PClassProducts> list = productMgtDAO.getClassProducts(ids, user.getWeiID());
		if (null != list) {
			for (PClassProducts p : list) {
				// 设置P_ClassProducts的ClassID=shopClassId
				p.setClassId(Long.parseLong(shopClassId.toString()));
				// 自营的产品
				if (null != p.getType() && (p.getType() == ShelveType.Self.getNo()) || p.getType() == ShelveType.NoSale.getNo()) {
					// 更新 pproducts表的sid=shopClassId
					PProducts product = super.getById(PProducts.class, p.getProductId());
					if (null != product) {
						product.setSid(shopClassId);
						product.setUpdateTime(new Date());
					}
				}
			}
		}
		return true;
	}

	@Override
	public List<UChildrenUser> getChildSupplyer(Long parentId) {
		return productMgtDAO.getChildSupplyer(parentId);
	}

	@Override
	public ReturnModel onShelvesProduct(ShelveProductParam shelveProduct, LoginUser loginUser) throws Exception {
		ReturnModel rs = new ReturnModel();
		rs.setStatu(ReturnStatus.ParamError);
		rs.setStatusreson("参数有误");
		if (StringUtils.isNotEmpty(shelveProduct.getIds())) {
			Long weiId = loginUser.getWeiID();
			JSONArray productIdList = new JSONArray().fromObject(shelveProduct.getIds());
			JSONObject prodIdObj = productIdList.getJSONObject(0);// 取第一项
			Long productID = prodIdObj.getLong("proNo");
			PProducts products = productMgtDAO.get(PProducts.class, productID);
			if ((products != null && (products.getSupperType() == null ? 0 : products.getSupperType()) != 0 && products.getSupplierWeiId() != null && products.getSupplierWeiId() > 0) || (weiId.equals(products.getSupplierWeiId()))) {
				// 查找自己是否上架过这个产品
				PClassProducts db_pcp = productMgtDAO.getShevleProductByWeiidAndProductID(weiId, productID);
				Long shelveWeiid = shelveProduct.getShelveWeiid();
				Long shopclassid = shelveProduct.getShopClassId();
				short type = ShelveType.Other.getNo();
				ShelveProduct spt = new ShelveProduct();
				if (weiId.equals(products.getSupplierWeiId())) {
					if (Short.valueOf("2").equals(products.getState()) || Short.valueOf("0").equals(products.getVerStatus())) {
						rs.setStatu(ReturnStatus.DataError);
						rs.setStatusreson("该产品未通过审核，请重新编辑产品!");
						return rs;
					}
					type = ShelveType.Self.getNo();
					shelveWeiid = weiId;
					if (loginUser.judgePower(UserPowerConstants.Product_IsYun_Power)) {
						TProductIndex pi = new TProductIndex();
						pi.setProductId(productID);
						pi.setType((short) 0);
						pi.setStatus((short) 0);
						productMgtDAO.save(pi);
					}
					UDemandProduct dp = productMgtDAO.getUniqueResultByHql("from UDemandProduct where productId=?", new Object[] { productID });
					// 品牌平台号供应商上架产品需要推送到下游代理落地店
					if (dp != null && (Short.valueOf("1").equals(loginUser.getPth()) || Short.valueOf("1").equals(loginUser.getPph()))) {
						Long count = productMgtDAO.count("select count(*) from PPushShelves where weiId=? and productId=? and pushType=?", new Object[] { loginUser.getWeiID(), products.getProductId(), PushTypeEnum.ShelveProduct.getNo() });
						if (count <= 0) {
							PPushShelves push = new PPushShelves();
							push.setWeiId(loginUser.getWeiID());
							push.setPushType(PushTypeEnum.ShelveProduct.getNo());
							push.setCreateTime(new Date());
							push.setProductId(products.getProductId());
							PushShelvesContentVO content = new PushShelvesContentVO();
							content.setDemandId(dp.getDemandId());
							content.setSehlveType(0);
							push.setContent(JSONObject.fromObject(content).toString());
							productMgtDAO.save(push);
						}
					}
				} else {
					type = ShelveType.Other.getNo();
				}
				// 记录下之前的商品来源
				if (db_pcp != null) {
					// 自己修改保留产品来源
					shelveWeiid = db_pcp.getShelvweiId();
					type = db_pcp.getType();
					if (type == ShelveType.Self.getNo()) {
						shopclassid = db_pcp.getClassId();
					}
					productMgtDAO.deleteShevleProductByWeiidAndProductID(weiId, productID, db_pcp.getId());
				} else
					db_pcp = new PClassProducts();

				if (shelveWeiid != null && shelveWeiid.equals(0L))
					shelveWeiid = products.getSupplierWeiId(); // 若在新品那里上架就保存供应商的ID
				spt.setProductID(productID);// 商品id
				spt.setSupplierWeiid(products.getSupplierWeiId());// 商品供应商
				spt.setShelveWeiid(shelveWeiid);// 上架人
				spt.setClassID(shopclassid);
				spt.setIsSendMyself(type);
				spt.setShevleDes(shelveProduct.getShevleDes());
				spt.setSendWeiid(products.getSupplierWeiId());
				spt.setType(type);
				List<BatchPriceM> batchPrice = new ArrayList<BatchPriceM>();
				// 如果上架自己的产品且上架人是供应商 则可以有批发价格
				if ((Short.valueOf("1").equals(loginUser.getYunS()) || Short.valueOf("1").equals(loginUser.getBatchS())) && Short.valueOf(ShelveType.Self.getNo()).equals(spt.getType())) {
					List<PProductBatchPrice> productBatchPricelist = productMgtDAO.GetBatchPriceByID(productID);
					if (productBatchPricelist != null && productBatchPricelist.size() > 0) {
						for (PProductBatchPrice pbp : productBatchPricelist) {
							BatchPriceM bm = new BatchPriceM();
							bm.setCount(pbp.getCount() == null ? 0 : pbp.getCount());
							bm.setPrice(pbp.getPirce() == null ? 0.00 : pbp.getPirce());
							batchPrice.add(bm);
						}
					}
				}
				spt.setBatchList(batchPrice);
				// 上架产品
				UUserAssist userassist = productMgtDAO.updateUserAssist(weiId);
				// TODO 插入销量
				Long count = productMgtDAO.InsertShevleProduct(db_pcp, spt, weiId, userassist.getWeiIdSort());
				if (type == ShelveType.Self.getNo()) {
					// 产品表上架
					productMgtDAO.UP_ProductByProductID(weiId, productID, Short.parseShort(ProductStatusEnum.Showing.toString()));
				}
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("push_success_count", count);

				String indexNeedSelect = "indexList_need_" + weiId;
				RedisUtil.delete(indexNeedSelect);
				scoreManagerService.UP_ScoreAdd_process(productID, weiId, 0, ScoreType.Onshelve, ScoreEnum.onshelve);
				rs.setStatu(ReturnStatus.Success);
				rs.setStatusreson("成功！");
				rs.setBasemodle(map);
			} else {
				rs.setStatu(ReturnStatus.DataError);
				rs.setStatusreson("该产品不能上架，非供应商发布！");
			}
		}
		return rs;
	}

	@Override
	public ReturnModel offShelvesProduct(ShelveProductParam shelveProduct, LoginUser loginUser) throws Exception {
		ReturnModel rs = new ReturnModel();
		rs.setStatu(ReturnStatus.ParamError);
		rs.setStatusreson("参数有误");
		if (StringUtils.isNotEmpty(shelveProduct.getIds())) {
			Long weiId = loginUser.getWeiID();
			Long[] productIds = null;
			JSONArray productIdList = new JSONArray().fromObject(shelveProduct.getIds());
			if (Integer.valueOf("1").equals(shelveProduct.getIsAll())) {
				List<PClassProducts> proList = productMgtDAO.getPClassProducts(weiId, shelveProduct.getShopClassId(), Short.parseShort(ProductShelveStatu.OnShelf.toString()));
				productIds = new Long[proList.size()];
				for (int i = 0; i < proList.size(); i++) {
					productIds[i] = proList.get(i).getProductId();
				}
			} else {
				productIds = new Long[productIdList.size()];
				for (int i = 0; i < productIdList.size(); i++) {
					JSONObject prodIdObj = productIdList.getJSONObject(i);// 取第一项
					productIds[i] = prodIdObj.getLong("proNo");
				}
			}
			if (productIds != null && productIds.length > 0) {
				String strHql = " from PClassProducts p where p.weiId=:weiid and p.productId in (:productids)";
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("weiid", weiId);
				map.put("productids", productIds);
				List<PClassProducts> proLists = productMgtDAO.findByMap(strHql, map);
				for (int i = 0; i < proLists.size(); i++) {
					PClassProducts cp = proLists.get(i);
					// 自营产品下架
					if (cp.getWeiId().equals(cp.getSupplierweiId())) {
						Long count = productMgtDAO.count("select count(*) from PPushShelves where weiId=? and productId=? and pushType=?", new Object[] { cp.getWeiId(), cp.getProductId(), PushTypeEnum.ShelveProduct.getNo() });
						if (count > 0) {
							// job上架的流程关闭
							productMgtDAO.executeHql("delete from PPushShelves where weiId=? and productId=? and pushType=?", new Object[] { cp.getWeiId(), cp.getProductId(), PushTypeEnum.ShelveProduct.getNo() });
						}
						// //云端产品库删除
						if (loginUser.judgePower(UserPowerConstants.Product_IsYun_Power)) {
							TProductIndex index = new TProductIndex();
							index.setProductId(cp.getProductId());
							index.setType((short) 2);
							index.setStatus((short) 0);
							productMgtDAO.save(index);
						}

						// 对所有产品下架操作
						String hql = " update PClassProducts p set p.state=?  where p.productId =?";
						Object[] bb = new Object[2];
						bb[0] = Short.parseShort(ProductShelveStatu.OffShelf.toString());
						bb[1] = cp.getProductId();
						productMgtDAO.executeHql(hql, bb);
						// 产品表下架
						productMgtDAO.UP_ProductByProductID(weiId, cp.getProductId(), Short.parseShort(ProductStatusEnum.Drop.toString()));
					}
					// 非自营产品下架
					else {
						productMgtDAO.UP_PClassProductsByProId(weiId, cp.getProductId(), Short.parseShort(ProductShelveStatu.OffShelf.toString()), false);
					}
				}
				// 清除 UOwerMessage表的脏数据
				productMgtDAO.UP_UOwnerMessagesDelByProIds(productIds, weiId);

			}
			String indexNeedSelect = "indexList_need_" + weiId;
			RedisUtil.delete(indexNeedSelect);
			rs.setStatu(ReturnStatus.Success);
			rs.setStatusreson("成功");
		}
		return rs;
	}

	@Override
	public ReturnModel deleteShelvesProduct(ShelveProductParam shelveProduct, LoginUser loginUser) throws Exception {
		ReturnModel rs = new ReturnModel();
		rs.setStatu(ReturnStatus.ParamError);
		rs.setStatusreson("参数有误");
		if (StringUtils.isNotEmpty(shelveProduct.getIds())) {
			Long weiId = loginUser.getWeiID();
			JSONArray productIdList = new JSONArray().fromObject(shelveProduct.getIds());
			Long[] productIds = new Long[productIdList.size()];
			for (int i = 0; i < productIdList.size(); i++) {
				JSONObject prodIdObj = productIdList.getJSONObject(i);
				productIds[i] = prodIdObj.getLong("proNo");
			}
			List<PClassProducts> proList = null;
			List<PProducts> products = null;
			if (Integer.valueOf("1").equals(shelveProduct.getIsAll())) {
				if (shelveProduct.getState() != null) {
					if (shelveProduct.getState() > -1 && shelveProduct.getState() != 3) {
						proList = productMgtDAO.getPClassProducts(weiId, shelveProduct.getShopClassId(), shelveProduct.getState());
					} else if (shelveProduct.getState() == 3) {
						String hqlString = "from PProducts p where p.supplierWeiId=? and p.state=?";
						Object[] bb = new Object[2];
						bb[0] = weiId;
						bb[1] = Short.parseShort(ProductStatusEnum.OutLine.toString());
						products = productMgtDAO.find(hqlString, bb);
						if (products != null && products.size() > 0) {
							productIds = new Long[products.size()];
							for (int i = 0; i < products.size(); i++) {
								productIds[i] = products.get(i).getProductId();
							}
						}
					}
					if (proList != null && proList.size() > 0) {
						productIds = new Long[proList.size()];
						for (int i = 0; i < proList.size(); i++) {
							productIds[i] = proList.get(i).getProductId();

						}
					}
				}
			}
			if (productIds != null && productIds.length > 0) {
				// 草稿
				if (Short.valueOf(ProductStatusEnum.OutLine.toString()).equals(shelveProduct.getState())) {
					if (loginUser.getIsChildren() == 1) {
						productMgtDAO.UP_ProductSubByProductIDS(loginUser.getAccount(), productIds, Short.valueOf(ProductSubStatusEnum.Deleted.toString()));
					} else {
						productMgtDAO.UP_ProductByProductIDS(weiId, productIds, Short.valueOf(ProductStatusEnum.Deleted.toString()));
					}
				}
				// 待审核【平台号】、 申请中【平台号子供应商 】)
				else if (Short.valueOf(ProductStatusEnum.Audit.toString()).equals(shelveProduct.getState())) {
					// //平台号
					// if (Short.valueOf("1").equals(loginUser.getPth())) {
					// productMgtDAO.UP_SubProductByProductIDS(loginUser.getAccount(),
					// productIds,
					// Short.valueOf(ProductSubStatusEnum.Deleted.toString()));
					// }
					// //平台号子供应商
					// else if
					// (Short.valueOf("1").equals(loginUser.getPthSupply())) {
					// productMgtDAO.UP_SubProductByProductIDS(loginUser.getAccount(),
					// productIds,
					// Short.valueOf(ProductSubStatusEnum.Deleted.toString()));
					// }
					productMgtDAO.UP_SubProductByProductIDS(loginUser.getAccount(), productIds, Short.valueOf(ProductSubStatusEnum.Deleted.toString()));
				} else {
					String strHql = " from PClassProducts p where p.weiId=:weiid and p.productId in (:productids)";
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("weiid", weiId);
					map.put("productids", productIds);
					List<PClassProducts> proLists = productMgtDAO.findByMap(strHql, map);
					for (int i = 0; i < proLists.size(); i++) {
						PClassProducts cp = proLists.get(i);
						// 自营
						if (cp.getWeiId().equals(cp.getSupplierweiId())) {
							// //modify by tan 20150709
							TProductIndex index = new TProductIndex();
							index.setProductId(cp.getProductId());
							index.setType((short) 2);
							index.setStatus((short) 0);
							productMgtDAO.save(index);
							// 删除所有上架的
							String hql = " delete from PClassProducts p  where  p.productId=?";
							Object[] bb = new Object[1];
							bb[0] = cp.getProductId();
							productMgtDAO.executeHql(hql, bb);
							productMgtDAO.UP_ProductByProductID(weiId, cp.getProductId(), Short.parseShort(ProductStatusEnum.Deleted.toString()));
						}
						// 非自营
						else {
							// 删除 上架产品
							productMgtDAO.UP_PClassProductsByProIds(weiId, productIds, null, true);
						}
					}
				}
				// 清除 UOwerMessage表的脏数据
				productMgtDAO.UP_UOwnerMessagesDelByProIds(productIds, weiId);

				// 清楚店铺缓存
				String indexNeedSelect = "indexList_need_" + weiId;
				RedisUtil.delete(indexNeedSelect);
			}
			rs.setStatu(ReturnStatus.Success);
			rs.setStatusreson("成功");
		}
		return rs;
	}

	@Override
	public ReturnModel onGetShelvesProduct(ShelveProductParam shelveProduct, LoginUser loginUser) throws Exception {
		ReturnModel rs = new ReturnModel();
		rs.setStatu(ReturnStatus.ParamError);
		rs.setStatusreson("参数有误");
		if (StringUtils.isNotEmpty(shelveProduct.getIds())) {
			Long weiId = loginUser.getWeiID();
			JSONArray productIdList = new JSONArray().fromObject(shelveProduct.getIds());
			JSONObject prodIdObj = productIdList.getJSONObject(0);// 取第一项
			Long productid = prodIdObj.getLong("proNo");
			PProducts product = productMgtDAO.get(PProducts.class, productid);
			if (product != null) {
				if(product.getPublishType()!=null&&product.getPublishType()>0){
					rs.setStatu(ReturnStatus.SystemError);
					rs.setStatusreson("代理商品不支持上架！");
					return rs;
				}
				Long productOwnerId = product.getSupplierWeiId();// 供应商ID
				if (product.getState() == 1) {
					USupplyer supplyer = productMgtDAO.get(USupplyer.class, weiId);
					PClassProducts classproduct = productMgtDAO.GetShelveProductByWeiid(weiId, productid, (short) 0);// 取供应商分类
					UAttention attention = productMgtDAO.getattentionModle(weiId, productOwnerId);

					ProductOnShelveModel pnsm = new ProductOnShelveModel();
					pnsm.setProductid(product.getProductId());
					pnsm.setSupplierid(product.getSupplierWeiId());
					pnsm.setProductOwnerId(productOwnerId);
					pnsm.setProductimg(ImgDomain.GetFullImgUrl(product.getDefaultImg() == null ? "" : product.getDefaultImg()));
					pnsm.setProducttitle(product.getProductTitle() == null ? "" : product.getProductTitle());
					pnsm.setPrice(product.getDefaultPrice() == null ? 0.00 : product.getDefaultPrice());
					pnsm.setCommision(product.getDefaultConmision() == null ? 0.00 : product.getDefaultConmision());
					pnsm.setMaxbatchprice((double) 0);
					pnsm.setMinbatchprice((double) 0);
					pnsm.setIsAttentioned((short) 0);
					pnsm.setIsHaveWholesaleInfo((short) 1);

					pnsm.setOrignalPrice(product.getOriginalPrice() == null ? product.getDefaultPrice() : product.getOriginalPrice());

					// 如果产品不支持批发，设置为零
					if (BitOperation.getIntegerSomeBit(product.getTag(), 0) != 1)
						pnsm.setIsHaveWholesaleInfo((short) 0);
					// 如果本人不是批发商，设置为零
					if (supplyer == null || BitOperation.getIntegerSomeBit(supplyer.getType() == null ? 0 : supplyer.getType(), 2) != 1)
						pnsm.setIsHaveWholesaleInfo((short) 0);
					// logger.error("2-3");
					if (classproduct != null) {
						PClassProducts classProduct_S;
						if (classproduct.getShelvweiId() != null)
							classProduct_S = productMgtDAO.GetShelveProductByWeiid(classproduct.getShelvweiId(), productid, (short) 1);// 取供应商分类
						else
							classProduct_S = null;
						// logger.error("3");
						pnsm.setIsSendMyself(classproduct.getIsSendMyself());
						PShopClass shopclass = productMgtDAO.getShopClassByid(Integer.valueOf(classproduct.getClassId() + ""));
						if (shopclass != null)
							pnsm.setMyStoreCateModel(shopclass);

						if (BitOperation.getIntegerSomeBit(product.getTag(), 0) == 1) {
							// logger.error("4");
							// 自己的一套价格
							List<PShevleBatchPrice> batchpricelist = productMgtDAO.GetShevleBatchPriceByID(classproduct.getId());
							List<BatchPriceM> bpmList = new ArrayList<BatchPriceM>();
							if (batchpricelist != null && batchpricelist.size() > 0) {
								Double maxprice = batchpricelist.get(0).getPrice() == null ? 0.00 : batchpricelist.get(0).getPrice();
								Double minprice = batchpricelist.get(0).getPrice() == null ? 0.00 : batchpricelist.get(0).getPrice();
								for (PShevleBatchPrice pbp : batchpricelist) {
									Double tempprice = pbp.getPrice() == null ? 0.00 : pbp.getPrice();
									maxprice = tempprice > maxprice ? tempprice : maxprice;
									minprice = tempprice < minprice ? tempprice : minprice;
									BatchPriceM bpm = new BatchPriceM();
									bpm.setCount(pbp.getCount() == null ? 0 : pbp.getCount());
									bpm.setPrice(tempprice);
									bpmList.add(bpm);
								}

								pnsm.setBatchList(bpmList);
								pnsm.setMaxbatchprice(maxprice);
								pnsm.setMinbatchprice(minprice);

								// logger.error("5");
							}

							if (classProduct_S != null) {
								// 上级的一套价格
								List<PShevleBatchPrice> batchpricelist_S = productMgtDAO.GetShevleBatchPriceByID(classProduct_S.getId());
								List<BatchPriceM> bpmList_s = new ArrayList<BatchPriceM>();
								if (batchpricelist_S != null && batchpricelist_S.size() > 0) {
									Double maxprice_s = batchpricelist_S.get(0).getPrice() == null ? 0.00 : batchpricelist_S.get(0).getPrice();
									Double minprice_s = batchpricelist_S.get(0).getPrice() == null ? 0.00 : batchpricelist_S.get(0).getPrice();
									for (PShevleBatchPrice pbp : batchpricelist_S) {
										Double tempprice_s = pbp.getPrice() == null ? 0.00 : pbp.getPrice();
										maxprice_s = tempprice_s > maxprice_s ? tempprice_s : maxprice_s;
										minprice_s = tempprice_s < minprice_s ? tempprice_s : minprice_s;

										for (BatchPriceM bpml : bpmList) {
											if (bpml.getCount() == (pbp.getCount() == null ? 0 : pbp.getCount())) {
												bpml.setPrice_s(tempprice_s);
												break;
											}
										}

									}
									pnsm.setBatchList(bpmList);
									pnsm.setMaxbatchprice_s(maxprice_s);
									pnsm.setMinbatchprice_s(minprice_s);
									// logger.error("6");
								}

							} else {

								List<PProductBatchPrice> batchpricelist_p = productMgtDAO.GetBatchPriceByID(productid);
								if (batchpricelist_p != null && batchpricelist_p.size() > 0) {
									Double maxprice = batchpricelist_p.get(0).getPirce() == null ? 0.00 : batchpricelist_p.get(0).getPirce();
									Double minprice = batchpricelist_p.get(0).getPirce() == null ? 0.00 : batchpricelist_p.get(0).getPirce();
									List<BatchPriceM> bpmList_p = new ArrayList<BatchPriceM>();
									for (PProductBatchPrice pbp : batchpricelist_p) {
										Double tempprice = pbp.getPirce() == null ? 0.00 : pbp.getPirce();
										maxprice = tempprice > maxprice ? tempprice : maxprice;
										minprice = tempprice < minprice ? tempprice : minprice;

										for (BatchPriceM bpml : bpmList) {
											if (bpml.getCount() == (pbp.getCount() == null ? 0 : pbp.getCount())) {
												bpml.setPrice_s(tempprice);
												break;
											}
										}
									}
									pnsm.setMaxbatchprice_s(maxprice);
									pnsm.setMinbatchprice_s(minprice);
									pnsm.setBatchList(bpmList);
									// logger.error("7");
								}
							}

						}
						pnsm.setIsShelved((short) 1);
					} else {
						// modify by tan 2015-04-16 改动上架所属
						pnsm.setIsSendMyself((short) 0);
						if (BitOperation.getIntegerSomeBit(product.getTag(), 0) == 1) {
							// 如果我去上架的上架已上架 要显示他的价格
							PClassProducts classproduct_up = productMgtDAO.GetShelveProductByWeiid(productOwnerId, productid, (short) 1);// 取供应商分类
							if (classproduct_up != null) {
								List<PShevleBatchPrice> batchpricelist = productMgtDAO.GetShevleBatchPriceByID(classproduct_up.getId());
								List<BatchPriceM> bpmList = new ArrayList<BatchPriceM>();
								if (batchpricelist != null && batchpricelist.size() > 0) {
									Double maxprice = batchpricelist.get(0).getPrice() == null ? 0.00 : batchpricelist.get(0).getPrice();
									Double minprice = batchpricelist.get(0).getPrice() == null ? 0.00 : batchpricelist.get(0).getPrice();
									for (PShevleBatchPrice pbp : batchpricelist) {
										Double tempprice = pbp.getPrice() == null ? 0.00 : pbp.getPrice();
										maxprice = tempprice > maxprice ? tempprice : maxprice;
										minprice = tempprice < minprice ? tempprice : minprice;
										BatchPriceM bpm = new BatchPriceM();
										bpm.setCount(pbp.getCount() == null ? 0 : pbp.getCount());
										bpm.setPrice(tempprice);
										bpm.setPrice_s(tempprice);
										bpmList.add(bpm);
									}

									pnsm.setBatchList(bpmList);
									pnsm.setMaxbatchprice(maxprice);
									pnsm.setMinbatchprice(minprice);

									pnsm.setMaxbatchprice_s(maxprice);
									pnsm.setMinbatchprice_s(minprice);
									// logger.error("5");
								}
							} else {
								// 否则显示供应商的价格
								List<PProductBatchPrice> batchpricelist = productMgtDAO.GetBatchPriceByID(productid);
								if (batchpricelist != null && batchpricelist.size() > 0) {
									Double maxprice = batchpricelist.get(0).getPirce() == null ? 0.00 : batchpricelist.get(0).getPirce();
									Double minprice = batchpricelist.get(0).getPirce() == null ? 0.00 : batchpricelist.get(0).getPirce();
									List<BatchPriceM> bpmList = new ArrayList<BatchPriceM>();
									for (PProductBatchPrice pbp : batchpricelist) {
										Double tempprice = pbp.getPirce() == null ? 0.00 : pbp.getPirce();
										maxprice = tempprice > maxprice ? tempprice : maxprice;
										minprice = tempprice < minprice ? tempprice : minprice;
										BatchPriceM bpm = new BatchPriceM();
										bpm.setCount(pbp.getCount() == null ? 0 : pbp.getCount());
										bpm.setPrice(tempprice);
										bpm.setPrice_s(tempprice);
										bpmList.add(bpm);
									}

									pnsm.setMaxbatchprice(maxprice);
									pnsm.setMinbatchprice(minprice);
									pnsm.setBatchList(bpmList);

									pnsm.setMaxbatchprice_s(maxprice);
									pnsm.setMinbatchprice_s(minprice);

								}
							}

						}
						pnsm.setIsShelved((short) 0);
					}
					if (attention != null)
						pnsm.setIsAttentioned((short) 1);
					String indexNeedSelect = "indexList_need_" + weiId;
					RedisUtil.delete(indexNeedSelect);
					rs.setStatu(ReturnStatus.Success);
					rs.setStatusreson("成功！");
					rs.setBasemodle(pnsm);
				} else {
					rs.setStatu(ReturnStatus.DataError);
					rs.setStatusreson("该产品已被删除！");
				}
			} else {
				rs.setStatu(ReturnStatus.DataError);
				rs.setStatusreson("该产品不存在！");
			}

		}
		return rs;
	}

	@Override
	public List<String> getPictureImg(Long productId) {
		List<String> imglist = productMgtDAO.getPictureImg(productId);
		if (imglist != null)
			return imglist;
		return null;
	}

}
