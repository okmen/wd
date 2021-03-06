package com.okwei.service.impl.share;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.okwei.bean.domain.PClassProducts;
import com.okwei.bean.domain.PProductClass;
import com.okwei.bean.domain.PProducts;
import com.okwei.bean.domain.PShopClass;
import com.okwei.bean.domain.SMainData;
import com.okwei.bean.domain.SProducts;
import com.okwei.bean.domain.SShareActive;
import com.okwei.bean.domain.SSingleDesc;
import com.okwei.bean.domain.SStatics;
import com.okwei.bean.domain.UShopInfo;
import com.okwei.bean.domain.USupplyer;
import com.okwei.bean.enums.MainDataUserType;
import com.okwei.bean.enums.ProductStatusEnum;
import com.okwei.bean.enums.ShareOnHomePage;
import com.okwei.bean.enums.ShareStatus;
import com.okwei.bean.enums.ShareTypeEnum;
import com.okwei.bean.enums.TerminalEnum;
import com.okwei.bean.enums.UserIdentityType;
import com.okwei.bean.vo.ReturnModel;
import com.okwei.bean.vo.ReturnStatus;
import com.okwei.bean.vo.product.ProductInfo;
import com.okwei.bean.vo.share.MainShare;
import com.okwei.bean.vo.share.ShareProduct;
import com.okwei.common.Limit;
import com.okwei.common.PageResult;
import com.okwei.dao.IBaseDAO;
import com.okwei.service.impl.BaseService;
import com.okwei.service.share.IShareAddService;
import com.okwei.util.AppSettingUtil;
import com.okwei.util.BitOperation;
import com.okwei.util.ImgDomain;
import com.okwei.util.ParseHelper;
import com.okwei.util.RedisUtil;
import com.okwei.util.TransferManager;

@Service
public class ShareAddService extends BaseService implements IShareAddService {
    @Autowired
    private IBaseDAO baseDAO;

    @Override
    public Long getShareProCount(Long weiid, String title, short type, Long[] ids, int classid) {
	Map<String, Object> params = new HashMap<String, Object>();
	params.put("weiid", weiid);
	params.put("state", Short.parseShort(ProductStatusEnum.Showing.toString()));
	String sql = "SELECT COUNT(1) FROM P_Products WHERE ProductID IN (SELECT ProductID FROM P_ClassProducts WHERE WeiID=:weiid AND State=1 ";
	if (classid > 0) {
	    List<Long> classids = new ArrayList<Long>();
	    classids.add(Long.valueOf(classid));
	    List<PShopClass> classList = baseDAO.find("from PShopClass where weiid=? and paretId=?", new Object[] { weiid, classid });
	    if (classList != null && classList.size() > 0) {
		for (PShopClass shop : classList) {
		    classids.add(shop.getSid().longValue());
		}
	    }
	    sql += " AND ClassID IN(:classids) ";
	    params.put("classids", classids.toArray());
	}
	if (type > 0) {
	    if (type == 1) {
		sql += " AND Type=1";
	    } else {
		sql += " AND Type!=1";
	    }
	}
	if (ids != null && ids.length > 0) {
	    sql += " AND ID NOT IN(:ids)";
	    params.put("ids", ids);
	}
	sql += " ) AND State=:state ";
	if (title != null && !"".equals(title)) {
	    sql += " AND ProductTitle LIKE '%" + title + "%' ";
	}
	return baseDAO.countBySqlMap(sql, params);
    }

    @Override
    public PageResult<ShareProduct> getShareProListForApp(Long weiid, String title, short type, Long[] ids, Limit limit, int classid) {
	Map<String, Object> params = new HashMap<String, Object>();
	params.put("weiid", weiid);
	params.put("state", Short.parseShort(ProductStatusEnum.Showing.toString()));
	String sql = "SELECT a.id,b.ProductID proID,b.DefaultImg proImage,b.ProductTitle proTitle,b.DefaultPrice price,a.Type type,a.ClassID shopid,b.SupplierWeiID supweiid,b.OriginalPrice yprice,b.DefaultConmision commission FROM (SELECT * FROM P_ClassProducts WHERE WeiID=:weiid AND State=1 ";
	if (classid > 0) {
	    List<Long> classids = new ArrayList<Long>();
	    classids.add(Long.valueOf(classid));
	    List<PShopClass> classList = baseDAO.find("from PShopClass where weiid=? and paretId=?", new Object[] { weiid, classid });
	    if (classList != null && classList.size() > 0) {
		for (PShopClass shop : classList) {
		    classids.add(shop.getSid().longValue());
		}
	    }
	    sql += " AND ClassID IN(:classids) ";
	    params.put("classids", classids.toArray());
	}
	if (type > 0) {
	    if (type == 1) {
		sql += " AND Type=1";
	    } else {
		sql += " AND Type!=1";
	    }
	}
	if (ids != null && ids.length > 0) {
	    sql += " AND ID NOT IN(:ids)";
	    params.put("ids", ids);
	}
	sql += " ) a INNER JOIN P_Products b WHERE a.ProductID=b.ProductID AND b.State=:state ";
	if (title != null && !"".equals(title)) {
	    sql += " AND b.ProductTitle LIKE '%" + title + "%'";
	}
	PageResult<ShareProduct> page = baseDAO.queryPageResultByMap(sql, ShareProduct.class, limit, params);
	if (page != null) {
	    List<ShareProduct> list = page.getList();
	    int count = list.size();
	    if (list != null && count > 0) {
		Long[] supids = new Long[count];
		for (int i = 0; i < count; i++) {
		    supids[i] = list.get(i).getSupweiid().longValue();
		}
		// 店铺分类
		List<PShopClass> shopList = baseDAO.find("from PShopClass where weiid=?", weiid);
		boolean flagShop = false;
		if (shopList != null && shopList.size() > 0)
		    flagShop = true;
		// 供应商名称
		Map<String, Object> paramSup = new HashMap<String, Object>();
		paramSup.put("supids", supids);
		List<USupplyer> supList = baseDAO.findByMap("from USupplyer where weiId in(:supids)", paramSup);
		boolean flagSup = false;
		if (supList != null && supList.size() > 0)
		    flagSup = true;
		for (ShareProduct pro : list) {
		    pro.setProImage(ImgDomain.GetFullImgUrl(pro.getProImage(), 24));
		    if (pro.getYprice() == null) {
			pro.setYprice(pro.getPrice());
		    }
		    if (flagShop) {
			for (PShopClass shop : shopList) {
			    if (pro.getShopid().longValue() == shop.getSid().longValue()) {
				if (shop.getLevel().intValue() == 1) {
				    pro.setShopClass(shop.getSname());
				} else {
				    String name = "";
				    for (PShopClass shop1 : shopList) {
					if (shop.getParetId().equals(shop1.getSid())) {
					    name = shop1.getSname() + "/";
					    break;
					}
				    }
				    name += shop.getSname();
				    pro.setShopClass(name);
				}
				break;
			    }
			}
		    }
		    if (flagSup) {
			for (USupplyer sup : supList) {
			    if (sup.getWeiId().longValue() == pro.getSupweiid().longValue()) {
				pro.setSupName(sup.getCompanyName());
				break;
			    }
			}
		    }
		}
	    }
	    page.setList(list);
	    return page;
	}
	return null;
    }

    @Override
    public List<ShareProduct> getShareProList(Long weiid, String title, short type, Long[] ids, Limit limit, int classid) {
	PageResult<ShareProduct> page = getShareProListForApp(weiid, title, type, ids, limit, classid);
	if (page != null) {
	    return page.getList();
	}
	return null;
    }

    @Override
    public ReturnModel addShare(Long weiid, Long shareId, String title, String des, List<SProducts> list, MainDataUserType type, String ctsUser, int shareType, List<SSingleDesc> singlist) {
	ReturnModel result = new ReturnModel();
	result.setStatu(ReturnStatus.DataError);
	SMainData share = null;
	if (shareId > 0) {
	    share = baseDAO.get(SMainData.class, shareId);
	    if (share == null) {
		result.setStatusreson("获取分享专题错误");
		return result;
	    }
	    if (ctsUser == null) {
		if (!share.getWeiId().equals(weiid)) {
		    result.setStatusreson("非法操作");
		    return result;
		}
	    }
	    share.setUpdateTime(new Date());
	} else {
	    share = new SMainData();
	}
	share.setWeiId(weiid);
	share.setTitle(title);
	share.setDescrible(des);
	share.setPcount(list.size());
	share.setUserType(Integer.parseInt(type.toString()));
	share.setCtsUser(ctsUser);
	share.setShareType((short) shareType);
	if (shareId != null && shareId.longValue() > 0) {
	    share.setTopTime(null);
	} else {
	    share.setStatus(Short.parseShort(ShareStatus.Pass.toString()));
	}
	share.setCreateTime(new Date());
	share.setTerminateType(Short.parseShort(TerminalEnum.pc.toString()));
	share.setCtsState((short) 0);
	share.setOnHomePage((short) 2);
	share.setCtsFlag((short) 0);
	baseDAO.saveOrUpdate(share);
	// 先删除所有的
	baseDAO.executeHql("delete from SProducts where shareId=?", share.getShareId());
	// 添加分享产品
	int stype = Integer.parseInt(ShareTypeEnum.SingleQuality.toString());
	for (SProducts pro : list) {
	    pro.setShareId(share.getShareId());
	    pro.setWeiId(weiid);
	    baseDAO.save(pro);
	    if (shareType == stype) {
		if (singlist != null && singlist.size() > 0) {
		    for (SSingleDesc imgDesc : singlist) {
			imgDesc.setSpid(pro.getSpid());
			imgDesc.setImageUrl(ImgDomain.ReplitImgDoMain(imgDesc.getImageUrl()));
			baseDAO.save(imgDesc);
		    }
		}
	    }
	}
	SShareActive active = baseDAO.getUniqueResultByHql("from SShareActive where shareId=? and weiId=?", new Object[] { share.getShareId(), weiid });
	if (active == null) {
	    active = new SShareActive();
	    active.setShareId(share.getShareId());
	    active.setWeiId(weiid);
	    active.setMakeWeiId(weiid);
	    active.setCreateTime(new Date());
	    active.setStatus((short) 1);
	    baseDAO.save(active);
	}
	if (shareId != null && shareId.longValue() > 0) {
	    SStatics statics = baseDAO.get(SStatics.class, share.getShareId());
	    if (statics != null) {
		statics.setPcount(share.getPcount());
		baseDAO.update(statics);
	    }
	} else {
	    SStatics statics = new SStatics();
	    statics.setShareId(share.getShareId());
	    statics.setWeiId(weiid);
	    statics.setPcount(share.getPcount());
	    statics.setAppSv(0);
	    statics.setAppPv(0);
	    statics.setWapSv(0);
	    statics.setWapPv(0);
	    statics.setWebSv(0);
	    statics.setWebPv(0);
	    statics.setVol(0);
	    statics.setTurnover(0d);
	    baseDAO.save(statics);
	}
	result.setStatu(ReturnStatus.Success);
	result.setBasemodle(share.getShareId());
	return result;
    }

    @Override
    public List<PShopClass> getShopClasses(Long weiid) {
	return baseDAO.find("from PShopClass a where a.weiid=? and a.state=1 and a.level=1 order by a.sort asc", weiid);
    }

    @Override
    public MainShare getMainShare(Long weiid, Long shareid) {
	MainShare result = new MainShare();
	result.setShareType(1);
	if (shareid <= 0) {
	    return result;
	}
	SMainData main = baseDAO.get(SMainData.class, shareid);
	if (main != null) {
	    result.setShareid(main.getShareId());
	    result.setTitle(main.getTitle());
	    result.setDes(main.getDescrible());
	    result.setShareType(main.getShareType() == null ? 1 : main.getShareType().intValue());
	    List<SProducts> list = baseDAO.find("from SProducts where shareId=? order by spid asc", main.getShareId());
	    if (list != null && list.size() > 0) {
		Long[] ids = new Long[list.size()];
		for (int i = 0; i < list.size(); i++) {
		    ids[i] = list.get(i).getShelveId();
		}
		String sql = "SELECT a.id,b.ProductID proID,b.DefaultImg proImage,b.ProductTitle proTitle,b.DefaultPrice price,a.Type type,a.ClassID shopid,b.SupplierWeiID supweiid,b.OriginalPrice yprice FROM (SELECT * FROM P_ClassProducts WHERE ID IN(:ids) AND State=1) a INNER JOIN P_Products b WHERE a.ProductID=b.ProductID AND b.State=:state";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ids", ids);
		params.put("state", Short.parseShort(ProductStatusEnum.Showing.toString()));
		PageResult<ShareProduct> page = baseDAO.queryPageResultByMap(sql, ShareProduct.class, Limit.buildLimit(1, 100), params);
		if (page != null) {
		    List<ShareProduct> tempList = new ArrayList<ShareProduct>();
		    List<ShareProduct> pros = page.getList();
		    for (SProducts spro : list) {
			for (ShareProduct pro : pros) {
			    if (spro.getProductId().longValue() == pro.getProID().longValue()) {
				pro.setProImage(ImgDomain.GetFullImgUrl(pro.getProImage(), 24));
				if (pro.getYprice() == null) {
				    pro.setYprice(pro.getPrice());
				}
				pro.setSupName(spro.getDescription());
				// 如果是单品获取子属性
				if (result.getShareType() == Integer.parseInt(ShareTypeEnum.SingleQuality.toString())) {
				    List<SSingleDesc> singlist = baseDAO.find("from SSingleDesc where spid=?", spro.getSpid());
				    if (singlist != null && singlist.size() > 0) {
					for (SSingleDesc sing : singlist) {
					    sing.setImageUrl(ImgDomain.GetFullImgUrl(sing.getImageUrl(), 24));
					}
				    }
				    pro.setSinglist(singlist);
				}
				tempList.add(pro);
				break;
			    }
			}
		    }
		    result.setList(tempList);
		}
	    }
	}
	return result;
    }

    @Override
    public boolean getIsSupper(int idtype) {
	if (BitOperation.isIdentity(idtype, UserIdentityType.yunSupplier))
	    return true;
	if (BitOperation.isIdentity(idtype, UserIdentityType.batchSupplier))
	    return true;
	if (BitOperation.isIdentity(idtype, UserIdentityType.PlatformSupplier))
	    return true;
	if (BitOperation.isIdentity(idtype, UserIdentityType.BrandSupplier))
	    return true;
	return false;
    }

    @Override
    public List<PProductClass> getProductClasses() {
	return baseDAO.find("from PProductClass order by sort asc");
    }

    @Override
    public List<PProductClass> getProductClasses(int classid1, int classid2, int step) {
	if (step == 1) {
	    Map<String, Object> params = new HashMap<String, Object>();
	    params.put("step", (short) 2);
	    String hql = "from PProductClass where step=:step ";
	    if (classid1 > 0) {
		hql += " and parentId=:classid";
		params.put("classid", classid1);
	    }
	    return baseDAO.findByMap(hql, params);
	} else if (step == 2) {
	    Map<String, Object> params = new HashMap<String, Object>();
	    params.put("step", (short) 3);
	    String hql = "from PProductClass where step=:step ";
	    if (classid2 > 0) {
		hql += " and parentId=:classid";
		params.put("classid", classid2);
	    } else if (classid1 > 0) {
		hql += " and parentId in(select classId from PProductClass where parentId=:classid and step=2)";
		params.put("classid", classid1);
	    }
	    return baseDAO.findByMap(hql, params);
	}
	return null;
    }

    @Override
    public int getShareCountCts(Map<String, String> kv) {
	int result = 0;
	String searchContent = TransferManager.SearchProduct(kv);
	if (!"".equals(searchContent)) {
	    JSONObject jo = new JSONObject().fromObject(searchContent);
	    result = jo.getInt("totalCount");
	}
	return result;
    }

    @Override
    public List<ProductInfo> getShareProListCts(Map<String, String> kv) {
	String searchContent = TransferManager.SearchProduct(kv);
	if (!"".equals(searchContent)) {
	    JSONObject jo = new JSONObject().fromObject(searchContent);
	    JSONArray ja = new JSONArray().fromObject(jo.getString("list"));
	    if (ja != null && ja.size() > 0) {
		Long[] proids = new Long[ja.size()];
		Long[] supids = new Long[ja.size()];
		short verStatus = ParseHelper.toShort(kv.get("activity"));
		for (int i = 0; i < ja.size(); i++) {
		    JSONObject jsonObj = ja.getJSONObject(i);
		    proids[i] = jsonObj.getLong("productId"); // 产品id
		    supids[i] = jsonObj.getLong("supplierWeiId");// 供应商id
		}
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("proids", proids);
		// 首先查产品
		Map<String, Object> paramPro = new HashMap<String, Object>();
		String hql = "from PProducts where state=:state and productId in(:proids)";
		paramPro.put("state", Short.parseShort(ProductStatusEnum.Showing.toString()));
		paramPro.put("proids", proids);
		//收索查询828产品，条件是已审核并且去除已选择的产品
		List<Long> proIdList = baseDAO.find("select productId from AHomeProducts");
		if(verStatus==1){
			if(proIdList!=null){
				paramPro.put("proid", proIdList);
				hql+=" and productId not in(:proid)";
			}
/*			paramPro.put("verStatus", verStatus);
			hql+=" and verStatus=:verStatus ";*/
		}
		List<PProducts> prolist = baseDAO.findByMap(hql, paramPro);
		List<Integer> sids = new ArrayList<Integer>();
		if (prolist != null && prolist.size() > 0) {
		    for (int i = 0; i < prolist.size(); i++) {
			if (prolist.get(i).getSid() != null) {
			    sids.add(prolist.get(i).getSid());
			}
		    }
		}
		// 上架表
		String hqlString = "from PClassProducts where productId in(:proids) and weiId in(:weiids) and state=1";
		Map<String, Object> paramCla = new HashMap<String, Object>();
		paramCla.put("proids", proids);
		paramCla.put("weiids", supids);
		List<PClassProducts> clalist = baseDAO.findByMap(hqlString, paramCla);
		boolean flagcla = false;
		if (clalist != null && clalist.size() > 0)
		    flagcla = true;
		// 店铺分类
		List<PShopClass> shoplist = null;
		if (sids.size() > 0) {
		    Map<String, Object> paramShop = new HashMap<String, Object>();
		    paramShop.put("sids", sids);
		    shoplist = baseDAO.findByMap("from PShopClass where sid in(:sids)", paramShop);
		}
		boolean flagshop = false;
		if (shoplist != null && shoplist.size() > 0)
		    flagshop = true;
		// 供应商名称
		Map<String, Object> paramSup = new HashMap<String, Object>();
		paramSup.put("weiids", supids);
		List<USupplyer> suplist = baseDAO.findByMap("from USupplyer where weiId in(:weiids)", paramSup);
		boolean flagsup = false;
		if (suplist != null && suplist.size() > 0)
		    flagsup = true;
		List<ProductInfo> result = new ArrayList<ProductInfo>();
		if (prolist != null && prolist.size() > 0) {
		    for (PProducts pro : prolist) {
			ProductInfo temp = new ProductInfo();
			if (flagcla) {
			    for (PClassProducts cla : clalist) {
				if (cla.getProductId().equals(pro.getProductId()) && cla.getWeiId().equals(pro.getSupplierWeiId())) {
				    temp.setShelveId(cla.getId());
				    temp.setSaleCount(cla.getType().intValue());
				    break;
				}
			    }
			}
			temp.setProductId(pro.getProductId());
			temp.setProductName(pro.getProductTitle());
			temp.setProductPicture(ImgDomain.GetFullImgUrl(pro.getDefaultImg(), 24));
			temp.setRetailPrice(pro.getDefaultPrice());
			temp.setCommission(pro.getDefaultConmision());
			temp.setDisplayPrice(pro.getOriginalPrice() == null ? pro.getDefaultPrice() : pro.getOriginalPrice());
			if (flagshop) {
			    for (PShopClass shop : shoplist) {
				if (shop.getSid().equals(pro.getSid())) {
				    temp.setShopClass(shop.getSname());
				    break;
				}
			    }
			}
			if (flagsup) {
			    for (USupplyer sup : suplist) {
				if (sup.getWeiId().equals(pro.getSupplierWeiId())) {
				    temp.setCompanyName(sup.getCompanyName());
				    break;
				}
			    }
			}
			result.add(temp);
		    }
		    return result;
		}
	    }
	}
	return null;
    }

    @Override
    public ReturnModel addShare(Long weiid, Long shareId, String title, String des, ShareTypeEnum shareType, List<SSingleDesc> singList, List<SProducts> list, MainDataUserType type, String ctsUser) {
	ReturnModel result = new ReturnModel();
	result.setStatu(ReturnStatus.DataError);
	SMainData share = null;
	if (shareId > 0) {
	    share = baseDAO.get(SMainData.class, shareId);
	    if (share == null) {
		result.setStatusreson("获取分享专题错误");
		return result;
	    }
	    if (ctsUser == null) {
		if (!share.getWeiId().equals(weiid)) {
		    result.setStatusreson("非法操作");
		    return result;
		}
	    }
	    share.setUpdateTime(new Date());
	} else {
	    share = new SMainData();
	}
	share.setWeiId(weiid);
	share.setTitle(title);
	share.setDescrible(des);
	share.setPcount(list.size());
	share.setUserType(Integer.parseInt(type.toString()));
	share.setCtsUser(ctsUser);

	if (shareId != null && shareId.longValue() > 0) {
	    share.setTopTime(null);
	} else {
	    share.setStatus(Short.parseShort(ShareStatus.Pass.toString()));
	}
	share.setCreateTime(new Date());
	share.setTerminateType(Short.parseShort(TerminalEnum.pc.toString()));
	share.setCtsState((short) 0);
	share.setOnHomePage((short) 2);
	share.setCtsFlag((short) 0);
	baseDAO.saveOrUpdate(share);
	// 先删除所有的
	baseDAO.executeHql("delete from SProducts where shareId=?", share.getShareId());
	// 添加分享产品
	int i = 0;
	for (SProducts pro : list) {
	    pro.setShareId(share.getShareId());
	    pro.setWeiId(weiid);
	    baseDAO.save(pro);
	    if (shareType.equals(ShareTypeEnum.SingleQuality)) {
		baseDAO.executeHql("delete from SSingleDesc where spid=?", pro.getSpid());
		if (singList.size() > 0 && singList != null)
		    for (SSingleDesc sin : singList) {
			sin.setSpid(pro.getSpid());
			baseDAO.save(sin);
		    }
	    }
	}
	SShareActive active = baseDAO.getUniqueResultByHql("from SShareActive where shareId=? and weiId=?", new Object[] { share.getShareId(), weiid });
	if (active == null) {
	    active = new SShareActive();
	    active.setShareId(share.getShareId());
	    active.setWeiId(weiid);
	    active.setMakeWeiId(weiid);
	    active.setCreateTime(new Date());
	    active.setStatus((short) 1);
	    baseDAO.save(active);
	}
	if (shareId != null && shareId.longValue() > 0) {
	    SStatics statics = baseDAO.get(SStatics.class, share.getShareId());
	    if (statics != null) {
		statics.setPcount(share.getPcount());
		baseDAO.update(statics);
	    }
	} else {
	    SStatics statics = new SStatics();
	    statics.setShareId(share.getShareId());
	    statics.setWeiId(weiid);
	    statics.setPcount(share.getPcount());
	    statics.setAppSv(0);
	    statics.setAppPv(0);
	    statics.setWapSv(0);
	    statics.setWapPv(0);
	    statics.setWebSv(0);
	    statics.setWebPv(0);
	    statics.setVol(0);
	    statics.setTurnover(0d);
	    baseDAO.save(statics);
	}
	result.setStatu(ReturnStatus.Success);
	result.setBasemodle(share.getShareId());
	return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public PageResult<MainShare> getMainShares(Limit limit, int count) {
	String keyName = "HotShareProducts_" + limit.getPageId() + "_" + limit.getSize() + "_" + count;
	PageResult<MainShare> result = null;
	try {
	    result = (PageResult<MainShare>) RedisUtil.getObject(keyName);
	} catch (Exception e) {
	}
	if (result == null) {
	    Map<String, Object> paramsMain = new HashMap<String, Object>();
	    paramsMain.put("onHomePage", ParseHelper.toShort(ShareOnHomePage.Yes.toString()));
	    PageResult<SMainData> page = baseDAO.findPageResultByMap("from SMainData s where s.onHomePage=:onHomePage and s.status<>-1 order by s.topTime desc,s.updateTime desc", limit, paramsMain);
	    if (page != null) {
		List<SMainData> list = page.getList();
		if (list != null && list.size() > 0) {
		    List<Long> wids = new ArrayList<Long>();
		    for (SMainData main : list) {
			wids.add(main.getWeiId());
		    }
		    Map<String, Object> pMap = new HashMap<String, Object>();
		    pMap.put("wids", wids);
		    List<UShopInfo> shops = baseDAO.findByMap("from UShopInfo where weiId in(:wids)", pMap);
		    boolean flag = false;
		    if (shops != null && shops.size() > 0)
			flag = true;
		    List<MainShare> sharelist = new ArrayList<MainShare>();
		    for (SMainData main : list) {
			MainShare temp = new MainShare();
			temp.setShareid(main.getShareId());
			temp.setTitle(main.getTitle());
			temp.setDes(main.getDescrible());
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("shareid", main.getShareId());
			params.put("state", Short.parseShort(ProductStatusEnum.Showing.toString()));
			List<PProducts> proList = baseDAO.findPageByMap("from PProducts where productId in(select productId from SProducts where shareId=:shareid) and state=:state", 1, count, params);
			if (proList != null && proList.size() > 0) {
			    List<ProductInfo> resultlist = new ArrayList<ProductInfo>();
			    for (PProducts pro : proList) {
				ProductInfo tempInfo = new ProductInfo();
				tempInfo.setProductId(pro.getProductId());
				tempInfo.setProductPicture(ImgDomain.GetFullImgUrl(pro.getDefaultImg(), 24));
				tempInfo.setProductName(pro.getProductTitle());
				tempInfo.setStorePrice(pro.getDefaultPrice());
				double percent = 1.5;
				double displayPrice = pro.getDefaultPrice() * percent;
				DecimalFormat df = new DecimalFormat("#.00");
	    		displayPrice = Double.parseDouble(df.format(displayPrice));
				tempInfo.setDisplayPrice(pro.getOriginalPrice() == null ? displayPrice : pro.getOriginalPrice());
				resultlist.add(tempInfo);
			    }
			    temp.setProList(resultlist);
			}
			if (main.getUserType().intValue() == ParseHelper.toInt(MainDataUserType.operate.toString())) {
			    Map<String, String> getyunYingInfos = AppSettingUtil.getyunYingInfos();
			    for (Entry<String, String> info : getyunYingInfos.entrySet()) {
				if (info.getKey().equals(main.getCtsUser())) {
				    temp.setPhoto(info.getValue());
				    break;
				}
			    }
			} else {
			    if (flag) {
				for (UShopInfo shop : shops) {
				    if (main.getWeiId().equals(shop.getWeiId())) {
					temp.setPhoto(ImgDomain.GetFullImgUrl(shop.getShopImg()));
					break;
				    }
				}
			    }
			}
			sharelist.add(temp);
		    }
		    result = new PageResult<MainShare>(page.getTotalCount(), limit, sharelist);
		    RedisUtil.setObject(keyName, result, 1800);
		}
	    }
	}
	return result;
    }
}
