package com.okwei.myportal.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.okwei.bean.domain.PClassProducts;
import com.okwei.bean.domain.PProducts;
import com.okwei.bean.domain.PShopClass;
import com.okwei.bean.enums.ProductStatusEnum;
import com.okwei.common.Limit;
import com.okwei.common.PageResult;
import com.okwei.dao.impl.BaseDAO;
import com.okwei.myportal.dao.IShopClassDAO;

@Repository
public class ShopClassDAO extends BaseDAO implements IShopClassDAO
{

    @Override
    public int getCountByClassId(long weiid,int cid)
    {
        String hql = "select count(1) from PClassProducts p where p.weiId=? and p.classId=? and p.state=1";
        long longcid = cid;

        Long count = super.count(hql,weiid,longcid);
        return count.intValue();
    }

    @Override
    public List<PClassProducts> getPClassProductsList(int cid,Long[] childrenSc)
    {
        String hql = "from PClassProducts p where p.classId=:classId or p.classId in (:childrenSc)";
        long longcid = cid;
        Map<String,Object> param = new HashMap<String, Object>();
        param.put("classId", longcid);
        param.put("childrenSc", childrenSc);
        List<PClassProducts> list = findByMap(hql,param);
        return list;
    }

    @Override
    public int getClassCount(long weiid)
    {
        String hql = "select count(1) from PShopClass p where p.weiid=? and p.state=1";
        Long count = super.count(hql,weiid);
        return count.intValue();
    }

    @Override
    public boolean deleteClassByCid(long weiid,int cid)
    {
        PShopClass sc = get(PShopClass.class,cid);
        if(sc != null && sc.getWeiid() == weiid)
        {
        	if (Short.valueOf("1").equals(sc.getLevel())) {
				executeHql("delete from PShopClass WHERE paretId=?", new Object[]{cid});
			} 
        	delete(sc);
            return true;
        }
        return false;
    }

    @Override
    public boolean updatePShopClass(PShopClass sc)
    {
        if(sc == null)
        {
            return false;
        }
        update(sc);
        return true;
    }

    @Override
    public PageResult<PShopClass> getShopClassListByCid(long weiid,Limit limit)
    {
        String hql = "from PShopClass p where p.weiid=? and p.state=1 order by p.sort asc,p.sid asc";
        Object[] b = new Object[1];
        b[0] = weiid;
        PageResult<PShopClass> sc = findPageResult(hql,limit,b);
        return sc;
    }

    @Override
    public short getMaxClassSort(long weiid)
    {
        String hql = "select MAX(p.sort) from PShopClass p where p.weiid=?";
        Object[] b = new Object[1];
        b[0] = weiid;
        Short objs = getUniqueResultByHql(hql,b);
        if(objs != null)
        {
            return objs;
        }
        return 100;
    }

    @Override
    public PShopClass getPShopClass(long weiid,int cid)
    {
        String hql = "from PShopClass p where p.weiid=? and p.sid=? and p.state=1";
        Object[] b = new Object[2];
        b[0] = weiid;
        b[1] = cid;
        PShopClass sc = getUniqueResultByHql(hql,b);
        return sc;
    }

    @Override
    public boolean insertPShopClass(PShopClass sc)
    {
        if(sc != null)
        {
            save(sc);
            return true;
        }
        return false;
    }

    @Override
    public PShopClass getPShopClassByName(long weiid,String className)
    {
        String hql = "from PShopClass p where p.weiid=? and p.sname=? and p.state=1";
        Object[] b = new Object[2];
        b[0] = weiid;
        b[1] = className;
        List<PShopClass> list = find(hql,b);
        if(list != null && list.size() > 0)
        {
            return list.get(0);
        }
        return null;
    }

    @Override
    public boolean updatePClassProducts(PClassProducts pc)
    {
        update(pc);
        return true;
    }

    @Override
    public boolean deletePClassProducts(PClassProducts pc)
    {
        delete(pc);
        return true;
    }

    @Override
    public boolean updateProducts(PProducts pp)
    {
        update(pp);
        return false;
    }

    @Override
    public List<PProducts> getPProducts(long weiid,int cid)
    {
        String hql = "from PProducts p where p.supplierWeiId=? and (p.classId=? OR p.classId IN (SELECT sid FROM PShopClass WHERE paretId=?)) and p.state=?";
        List<PProducts> pplist = find(hql,weiid,cid,cid,Short.parseShort(ProductStatusEnum.Showing.toString()));
        return pplist;
    }

    @Override
    public List<PClassProducts> getClassProductsByProIds(List<Long> ids)
    {
        String hql = "from PClassProducts p where p.productId in (:ids)";
        Map<String,Object> p = new HashMap<String,Object>();
        p.put("ids",ids);
        List<PClassProducts> plist = findByMap(hql,p);
        return plist;
    }

    @Override
    public short getMinClassSort(Long weiid)
    {
        String hql = "select MIN(p.sort) from PShopClass p where p.weiid=?";
        Object[] b = new Object[1];
        b[0] = weiid;
        Short objs = getUniqueResultByHql(hql,b);
        if(objs != null)
        {
            return objs;
        }
        return 0;
    }

	@Override
	public List<PShopClass> getShopClassListById(int cid) {
        String hql = "from PShopClass p where p.paretId=?";
        List<PShopClass> list = find(hql,cid);
        return list;
    }

}
