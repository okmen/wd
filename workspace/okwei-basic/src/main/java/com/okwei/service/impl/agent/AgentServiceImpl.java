package com.okwei.service.impl.agent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.okwei.bean.domain.AActShowProducts;
import com.okwei.bean.domain.DAgentInfo;
import com.okwei.bean.domain.DBrandSupplier;
import com.okwei.bean.domain.DBrands;
import com.okwei.bean.domain.OPayOrder;
import com.okwei.bean.domain.PBrandShevle;
import com.okwei.bean.domain.PProductClass;
import com.okwei.bean.domain.PProductStyles;
import com.okwei.bean.domain.PProducts;
import com.okwei.bean.domain.UUserAssist;
import com.okwei.bean.enums.OrderTypeEnum;
import com.okwei.bean.enums.ProductStatusEnum;
import com.okwei.bean.enums.UserIdentityType;
import com.okwei.bean.vo.activity.ActProductInfo;
import com.okwei.bean.vo.agent.AgentInfoExd;
import com.okwei.bean.vo.agent.BrandAgentInfo;
import com.okwei.bean.vo.product.ProductModel;
import com.okwei.common.Limit;
import com.okwei.common.PageResult;
import com.okwei.dao.IBaseDAO;
import com.okwei.dao.agent.IDAgentMgtDao;
import com.okwei.dao.product.IBaseProductDao;
import com.okwei.dao.product.IProductSearchDao;
import com.okwei.service.IBaseCommonService;
import com.okwei.service.agent.IDAgentService;
import com.okwei.service.impl.BaseService;
import com.okwei.util.BitOperation;
import com.okwei.util.DateUtils;
import com.okwei.util.ImgDomain;

@Service
public class AgentServiceImpl extends BaseService implements IDAgentService{
	@Autowired
	private IBaseProductDao productDao;
	@Autowired
	private  IProductSearchDao productSearchDao;
	@Autowired
	private IDAgentMgtDao agentDao;
	@Autowired
	private IBaseDAO baseDAO;
	@Autowired
	private IBaseCommonService commonService;
	
	
	public PageResult<PProducts> find_Productlist(Long weiid,Integer classID,Integer sysClassID, int pageIndex,int pageSize){
		DBrandSupplier supplier=baseDAO.get(DBrandSupplier.class, weiid);
		List<Integer> brandids=new ArrayList<Integer>();
		if(supplier==null){
			List<DAgentInfo> list= agentDao.find_DAgentInfos(weiid,null);
			if(list!=null&&list.size()>0){
				for (DAgentInfo dd : list) {
					brandids.add(dd.getBrandId());
				}
			}
		}else {
			brandids.add(supplier.getBrandId());	
		}
		Limit limit=Limit.buildLimit(pageIndex, pageSize);
		if(brandids!=null&&brandids.size()>0){
			StringBuilder sb=new StringBuilder();
			sb.append(" from PBrandShevle a where a.brandId in(:brandid) and  a.type=:type ");
			Map<String, Object> map=new HashMap<String, Object>();
			map.put("brandid", brandids);
			map.put("type", Short.parseShort(ProductStatusEnum.Showing.toString())); 
			if(classID!=null&&classID!=-1){
				sb.append(" and a.classId=:classid ");
		 		map.put("classid", classID);
			}
			if(sysClassID!=null&&sysClassID!=-1){
				List<Integer> list=new ArrayList<Integer>();
				list.add(sysClassID);
				List<PProductClass> a=findClassId(list);
				List<Integer> ls=new ArrayList<Integer>();

				for(PProductClass b:a){
					ls.add(b.getClassId());
				}
				sb.append(" and a.systemClassID in(:systemClassID) ");
				map.put("systemClassID", ls);
			}
			sb.append(" order by a.bid desc ");
		    PageResult<PBrandShevle> result=baseDAO.findPageResultByMap(sb.toString(), limit, map);
		    if(result!=null&&result.getList()!=null&&result.getList().size()>0){
		    	List<Long> proidsList=new ArrayList<Long>();
		    	for (PBrandShevle p : result.getList()) {
					proidsList.add(p.getProductId());
				}
		    	List<PProducts> prolist=productDao.findProductlistByIds(proidsList, Short.parseShort(ProductStatusEnum.Showing.toString()));
		    	return new PageResult<PProducts>(result.getTotalCount(), limit, prolist);
		    }
		    return new PageResult<PProducts>(result.getTotalCount(),limit,null);
		}
		return new PageResult<PProducts>(0,limit,null);
	}
	public List<PProductClass> findClassId(List<Integer> sysSid){
		if (sysSid != null && sysSid.size() > 0) {
			Map<String, Object> m = new HashMap<String, Object>();
			m.put("parentId", sysSid);
			List<PProductClass> a = baseDAO.findByMap("from PProductClass where parentId in(:parentId)", m);
			if (a != null && a.size() > 0) {
				if (a.get(0).getStep()!=null&&a.get(0).getStep() == 3) {
					return a;
				} else {
					List<Integer> b = new ArrayList<Integer>();
					for (PProductClass c : a) {
						b.add(c.getClassId());
					}
					return  findClassId(b);
				}
			}
		}
		return null;
		
	}
	public int verBrandSupplier(Long weiid){
		DBrandSupplier supplier=baseDAO.get(DBrandSupplier.class, weiid);
		if(supplier!=null){
			return supplier.getType()==null?0:supplier.getType();
		}else {
			UUserAssist assist=baseDAO.get(UUserAssist.class, weiid);
			if(assist!=null){
				if(BitOperation.verIdentity(assist.getIdentity()==null?0:assist.getIdentity(), UserIdentityType.AgentBrandAgent)
				||BitOperation.verIdentity(assist.getIdentity()==null?0:assist.getIdentity(), UserIdentityType.AgentDuke)
				||BitOperation.verIdentity(assist.getIdentity()==null?0:assist.getIdentity(), UserIdentityType.AgentDeputyDuke)
				||BitOperation.verIdentity(assist.getIdentity()==null?0:assist.getIdentity(), UserIdentityType.AgentCaptain)){
					return 3;
				}
			}
		}
		return -1;
	}
	
	public DBrands getBrands(Long weiid){
		DBrandSupplier supplier=baseDAO.get(DBrandSupplier.class, weiid);
		if(supplier!=null&&supplier.getBrandId()!=null){
			return baseDAO.get(DBrands.class, supplier.getBrandId());
		}
		return null;
	}
	
	@Override
	public DBrandSupplier getBrandSupplier(Long weiid){
		DBrandSupplier supplier=baseDAO.get(DBrandSupplier.class, weiid);
		return supplier;
	}
	
	public boolean canShelve(int useridentity){
		if(BitOperation.isIdentity(useridentity, UserIdentityType.AgentBrandAgent)
				||BitOperation.isIdentity(useridentity, UserIdentityType.AgentBrandSupplier)
				||BitOperation.isIdentity(useridentity, UserIdentityType.AgentCaptain)
				||BitOperation.isIdentity(useridentity, UserIdentityType.AgentDuke)
				||BitOperation.isIdentity(useridentity, UserIdentityType.AgentDeputyDuke)){
			return false;
		}
		return true;
	}


	@Override
	public List<DBrands> find_listDBrands(Long wid) {
		List<DBrandSupplier> supplier=baseDAO.find("from DBrandSupplier where weiId=?", wid);
		if(supplier!=null&&supplier.size()>0){
			List<DBrands> list=new ArrayList<DBrands>();
			for(DBrandSupplier db:supplier){
				DBrands brands=baseDAO.getUniqueResultByHql("from DBrands where brandId=?", db.getBrandId());//fin();
				list.add(brands);
			}
			return list;
		}
		return null;
	}

	@Override
	public Long getwei(Integer long1) {
		DBrands db=baseDAO.get(DBrands.class,long1);
		if(db!=null){
			return db.getWeiId();
		}
		return null;
	}

	@Override
	public List<DBrands> findBrandIds(Long wid) {
		List<DAgentInfo> daifo= baseDAO.find("from DAgentInfo where weiId=? order by brandId", wid);//getNotUniqueResultByHql("select superWeiid from DAgentInfo where weiId=?", wid);
		List<DBrands> list=null;
		if(daifo!=null&&daifo.size()>0){
			list=new ArrayList<DBrands>();
			for(DAgentInfo w:daifo){
				Integer brandid=w.getBrandId();
				DBrands aa=baseDAO.get(DBrands.class, brandid);
				list.add(aa);
			}
		}
		return list;
	}
	
	 @Override
	 public PageResult<ProductModel>  findAgentShopProductlist(Long weiid,Long loginedWeiid,Integer brandid,Integer classID,Integer sysClassID, int pageIndex,int pageSize){
			Limit limit=Limit.buildLimit(pageIndex, pageSize);
			StringBuilder sb=new StringBuilder();
			sb.append(" from PBrandShevle a where a.brandId=:brandid ");
			Map<String, Object> map=new HashMap<String, Object>();
			map.put("brandid", brandid);
			if(classID!=null&&classID!=-1&&classID!=0){
				sb.append(" and a.classId=:classid ");
				map.put("classid", classID);
			}
			if(sysClassID!=null&&sysClassID!=-1&&sysClassID!=0){
				List<Integer> list=new ArrayList<Integer>();
				list.add(sysClassID);
				List<PProductClass> a=findClassId(list);
				List<Integer> ls=new ArrayList<Integer>();

				for(PProductClass b:a){
					ls.add(b.getClassId());
				}
				sb.append(" and a.systemClassID in(:systemClassID) ");
				map.put("systemClassID", ls);
			}
			
			sb.append(" order by a.bid desc ");
		    PageResult<PBrandShevle> result=productDao.findPageResultByMap(sb.toString(), limit, map);
		    
		    DAgentInfo agentinfo=null;
			if(loginedWeiid!=0L){
				//判断登录人的代理身份
				agentinfo=agentDao.getDAgentInfo(loginedWeiid, brandid);
			}
		    if(result!=null&&result.getList()!=null&&result.getList().size()>0){
		    	List<Long> proidsList=new ArrayList<Long>();
		    	for (PBrandShevle p : result.getList()) {
					proidsList.add(p.getProductId());
				}
		    	List<PProducts> prolist=productDao.findProductlistByIds(proidsList, Short.parseShort(ProductStatusEnum.Showing.toString()));
		    	List<ProductModel> getAgentShopProductsList=new ArrayList<ProductModel>();
		    	for (PProducts product : prolist) {
		    		ProductModel pp=new ProductModel();
		    		pp.setProductId(product.getProductId());
		    		pp.setProductName(product.getProductTitle());
		    		pp.setProductPicture(ImgDomain.GetFullImgUrl_product(product.getDefaultImg(), 75));
		    		pp.setDisplayPrice(product.getOriginalPrice()==null?product.getDefaultPrice():product.getOriginalPrice());
		    		if(agentinfo!=null){
			    		if(agentinfo.getLevel()==1){
			    			pp.setRetailPrice(product.getDukePrice());
			    		}else if(agentinfo.getLevel()==2){
			    			pp.setRetailPrice(product.getDeputyPrice());
			    		}else if(agentinfo.getLevel()==3){
			    			pp.setRetailPrice(product.getAgentPrice());
			    		}else{
			    			pp.setRetailPrice(product.getDefaultPrice());
			    		}
		    		}else{
		    			pp.setRetailPrice(product.getDefaultPrice());
		    		}
		    		
		    		//pp.setRetailPrice(product.getDefaultPrice());
		    		pp.setCommission(product.getDefaultConmision());
		    		pp.setSellerWid(weiid);
		    		pp.setProviderWid(product.getSupplierWeiId());
		    		//add by @zlp at 20160628 添加是否全返活动商品
					ActProductInfo actpro=productSearchDao.get_ProductAct(product.getProductId());
					if(actpro!=null&&actpro.getActiveType()==1){
						pp.setIsActPro(1);
						AActShowProducts actshow=productDao.get(AActShowProducts.class, actpro.getProActId());
						pp.setStockCount(actshow.getStockCount()==null?0:actshow.getStockCount());
					}
					getAgentShopProductsList.add(pp);
		    		
				}
		    	return new PageResult<ProductModel>(result.getTotalCount(), limit, getAgentShopProductsList);
		    }
		    return new PageResult<ProductModel>(result.getTotalCount(),limit,null);
	    }
	
	 @Override
	 public PageResult<ProductModel>  findAgentShopProductlist(Long weiid,Long loginedWeiid,Integer classID,Integer sysClassID, int pageIndex,int pageSize){
	    	DBrandSupplier supplier=productDao.get(DBrandSupplier.class, weiid);
			Limit limit=Limit.buildLimit(pageIndex, pageSize);
			if(supplier!=null){
				StringBuilder sb=new StringBuilder();
				sb.append(" from PBrandShevle a where a.brandId=:brandid ");
				Map<String, Object> map=new HashMap<String, Object>();
				map.put("brandid", supplier.getBrandId());
				if(classID!=null&&classID!=-1&&classID!=0){
					sb.append(" and a.classId=:classid ");
					map.put("classid", classID);
				}
				if(sysClassID!=null&&sysClassID!=-1&&sysClassID!=0){
					List<Integer> list=new ArrayList<Integer>();
					list.add(sysClassID);
					List<PProductClass> a=findClassId(list);
					List<Integer> ls=new ArrayList<Integer>();

					for(PProductClass b:a){
						ls.add(b.getClassId());
					}
					sb.append(" and a.systemClassID in(:systemClassID) ");
					map.put("systemClassID", ls);
				}
				
				sb.append(" order by a.bid desc ");
				DAgentInfo agentinfo=null;
				if(loginedWeiid!=0L){
					//判断登录人的代理身份
					agentinfo=agentDao.getDAgentInfo(loginedWeiid, supplier.getBrandId());
				}
			    PageResult<PBrandShevle> result=productDao.findPageResultByMap(sb.toString(), limit, map);
			    if(result!=null&&result.getList()!=null&&result.getList().size()>0){
			    	List<Long> proidsList=new ArrayList<Long>();
			    	for (PBrandShevle p : result.getList()) {
						proidsList.add(p.getProductId());
					}
			    	List<PProducts> prolist=productDao.findProductlistByIds(proidsList, Short.parseShort(ProductStatusEnum.Showing.toString()));
			    	List<ProductModel> getAgentShopProductsList=new ArrayList<ProductModel>();
			    	for (PProducts product : prolist) {
			    		ProductModel pp=new ProductModel();
			    		pp.setProductId(product.getProductId());
			    		pp.setProductName(product.getProductTitle());
			    		pp.setProductPicture(ImgDomain.GetFullImgUrl_product(product.getDefaultImg(), 75));
			    		pp.setDisplayPrice(product.getOriginalPrice()==null?product.getDefaultPrice():product.getOriginalPrice());
			    		if(agentinfo!=null){
				    		if(agentinfo.getLevel()==1){
				    			pp.setRetailPrice(product.getDukePrice());
				    		}else if(agentinfo.getLevel()==2){
				    			pp.setRetailPrice(product.getDeputyPrice());
				    		}else if(agentinfo.getLevel()==3){
				    			pp.setRetailPrice(product.getAgentPrice());
				    		}else{
				    			pp.setRetailPrice(product.getDefaultPrice());
				    		}
			    		}else{
			    			pp.setRetailPrice(product.getDefaultPrice());
			    		}
			    		
			    		pp.setCommission(product.getDefaultConmision());
			    		pp.setSellerWid(weiid);
			    		//add by @zlp at 20160628 添加是否全返活动商品
						ActProductInfo actpro=productSearchDao.get_ProductAct(product.getProductId());
						if(actpro!=null&&actpro.getActiveType()==1){
							pp.setIsActPro(1);
							AActShowProducts actshow=productDao.get(AActShowProducts.class, actpro.getProActId());
							pp.setStockCount(actshow.getStockCount()==null?0:actshow.getStockCount());
						}
						getAgentShopProductsList.add(pp);
			    		
					}
			    	return new PageResult<ProductModel>(result.getTotalCount(), limit, getAgentShopProductsList);
			    }
			    return new PageResult<ProductModel>(result.getTotalCount(),limit,null);
			}
			return new PageResult<ProductModel>(0,limit,null);
	    }
	
	
	 public List<DAgentInfo> find_DAgents(Long weiid){
		 if(weiid==null||weiid<=0)
			 return null;
		 DBrandSupplier supplier=baseDAO.get(DBrandSupplier.class, weiid);
		 if(supplier!=null){
			 return agentDao.find_DAgentInfosByBrandId(supplier.getBrandId(),0);
		 }
		 return null;
	 }
	 
	 public PageResult<DAgentInfo> find_DAgents(Long weiid,Long agentweiid,int pageindex,int pagesize){
		 if(weiid==null||weiid<=0)
			 return null;
		 DBrandSupplier supplier=baseDAO.get(DBrandSupplier.class, weiid);
		 if(supplier!=null){
			 return agentDao.find_DAgentInfosByBrandId(supplier.getBrandId(),agentweiid, null, pageindex, pagesize);
		 }
		 return null;
	 }
	 
	 
	 public List<BrandAgentInfo> find_BrandAgents(Long weiid){
		 if(weiid==null||weiid<=0)
			 return null;
		 List<DAgentInfo> agents=agentDao.findDagentInfoByWeiid(weiid);
		 if(agents!=null&&agents.size()>0){
			 List<BrandAgentInfo> result=new ArrayList<BrandAgentInfo>();
			 for (DAgentInfo aa : agents) {
				DBrands brands= baseDAO.get(DBrands.class, aa.getBrandId());
				if(brands!=null){
					BrandAgentInfo model=new BrandAgentInfo();
					model.setBrandId(aa.getBrandId());
					model.setBrandName(brands.getBrandName()); 
					
					DAgentInfo 	superAgent= agentDao.getDAgentInfo(aa.getSuperWeiid(), brands.getBrandId());
					List<DAgentInfo> childs=agentDao.find_DAgentInfoBySuperWeiid(weiid, brands.getBrandId());
					
					List<AgentInfoExd> list=new ArrayList<AgentInfoExd>();
					if(superAgent!=null){
						AgentInfoExd mo=new AgentInfoExd();
						mo.setBrandId(superAgent.getBrandId());
						mo.setWeiId(superAgent.getWeiId());
						mo.setShopName(commonService.getShopNameByWeiId(superAgent.getWeiId())); 
						mo.setContactPhone(superAgent.getContactPhone());
						mo.setQq(superAgent.getQq());
						mo.setLevel(superAgent.getLevel());
						mo.setCreateTime(superAgent.getCreateTime());
						mo.setDateStr(DateUtils.format(superAgent.getCreateTime(), "yyyy-MM-dd")); 
						mo.setRelation("上级");
						list.add(mo);
					}
					if(childs!=null&&childs.size()>0){
						for (AgentInfoExd cc : list) {
							AgentInfoExd mo=new AgentInfoExd();
							mo.setBrandId(cc.getBrandId());
							mo.setWeiId(cc.getWeiId());
							mo.setShopName(commonService.getShopNameByWeiId(cc.getWeiId()));
							mo.setContactPhone(cc.getContactPhone());
							mo.setQq(cc.getQq());
							mo.setLevel(cc.getLevel());
							mo.setCreateTime(cc.getCreateTime());
							mo.setDateStr(DateUtils.format(cc.getCreateTime(), "yyyy-MM-dd")); 
							mo.setRelation("下级");
							mo.setCosts(cc.getCosts()); 
							list.add(mo);
						}
					}
					model.setAgentList(list);
					result.add(model);
				}
			}
			 return result;
		 }
		 return null;
	 }
 	/**
     * 得到产品的代理代价
     * @param agentinfo
     * @param product
     * @return
     */
    @Override
    public Double getBrandAgentPrice(DAgentInfo agentinfo,PProducts product){
    	Double price=0.0;
    	if(agentinfo!=null&&agentinfo.getLevel()==1){
    		price=(product.getDukePrice()==null)?0.0:product.getDukePrice();
		}else if(agentinfo!=null&&agentinfo.getLevel()==2){
			price=(product.getDeputyPrice()==null)?0.0:product.getDeputyPrice();
		}else if(agentinfo!=null&&agentinfo.getLevel()==3){
			price=(product.getAgentPrice()==null)?0.0:product.getAgentPrice();
		}else{
			price=(product.getDefaultPrice()==null)?0.0:product.getDefaultPrice();
		}
    	return price;
    }
    
    /**
     * 获取产品零售价（针对买家的零售价）
     * @param weiid
     * @param styleId
     * @return
     */
    public Double getProductPriceByWeiid(Long weiid,Long styleId){
    	Double price=0.0;
    	PProductStyles style=baseDAO.get(PProductStyles.class, styleId);
    	if(style!=null){
    		PProducts product=baseDAO.get(PProducts.class, style.getProductId());
    		if(product!=null){
    			if(product.getPublishType()!=null&&product.getPublishType()==1){
    				DBrandSupplier supplier=baseDAO.get(DBrandSupplier.class, product.getSupplierWeiId());
    				if(supplier!=null){
    					DAgentInfo agentInfo=agentDao.getDAgentInfo(weiid, supplier.getBrandId());
    					if(agentInfo!=null){
    						switch (agentInfo.getLevel()==null?3:agentInfo.getLevel()) {
							case 1:
								return style.getDukePrice();
							case 2:
								return style.getDeputyPrice();
							default:
								return style.getAgentPrice()==null?style.getPrice():style.getAgentPrice();
							}
    					}
    				}
    			}
    		}
    		return style.getPrice();
    	}
    	return price;
    }
    
    public String getDAgentInfoPayOrderId(Long weiid,Integer brandid){
    	if(weiid==null||brandid==null){
    		return "";
    	}
    	OPayOrder oPayOrder=baseDAO.getNotUniqueResultByHql(" from OPayOrder o where o.weiId=? and o.typeState=? and o.supplierOrder=? order by o.orderTime desc", weiid,Short.parseShort(OrderTypeEnum.AgentPayment.toString()),String.valueOf(brandid));
    	if(oPayOrder!=null){
    		return oPayOrder.getPayOrderId();
    	}
    	return "";
    }
	
}
