package com.okwei.company.service.Impl;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.okwei.bean.domain.PProductClass;
import com.okwei.bean.domain.UShopInfo;
import com.okwei.bean.domain.USupplyer;
import com.okwei.bean.domain.UYunSupplier;
import com.okwei.bean.vo.agent.YunSupplierList;
import com.okwei.company.bean.vo.BusKeyValue;
import com.okwei.company.bean.vo.YHomePageNavagation;
import com.okwei.company.bean.vo.YHpageMainVO;
import com.okwei.company.bean.vo.YunSupMsgListVO;
import com.okwei.company.bean.vo.YunSupMsgVO;
import com.okwei.company.service.IYStateService;
import com.okwei.company.util.LocalRedisUtil;
import com.okwei.dao.agent.IYunSupDao;
import com.okwei.util.ImgDomain;



@Service
public class YStateService implements IYStateService{
	
	@Autowired
	private IYunSupDao yunsupDao;
	
    private Integer state;//0：全品类状态；1：一级分类；2：二级分类状态
    private Integer classid;
    private Integer pageindex;
    private Integer pagesize;
	public Integer getPageindex() {
		return pageindex;
	}
	public void setPageindex(Integer pageindex) {
		this.pageindex = pageindex;
	}
	public Integer getPagesize() {
		return pagesize;
	}
	public void setPagesize(Integer pagesize) {
		this.pagesize = pagesize;
	}
	public Integer getState() {
		return state;
	}
	public void setState(Integer state) {
		this.state = state;
	}
	public Integer getClassid() {
		return classid;
	}
	public void setClassid(Integer classid) {
		this.classid = classid;
	}
    
	//公共方法
	/**
	 * 全部的方法
	 * @return
	 */
	@Override
	public YHpageMainVO methodAllType(){
		YHpageMainVO mainvo=new YHpageMainVO();
		List<BusKeyValue> alltype = getAllProductClass(1);
		//YHomePageNavagation navagation = new YHomePageNavagation();
		YunSupMsgListVO supmsglist = getSupMsgListByID(1,0,this.pageindex,this.pagesize);
		String clumbs = "<a href='http://www.okwei.com'>微店网首页</a> &gt; "
				        +"<a href='http://company.okwei.com/yun/list'>工厂</a> &gt; "
				        +"<a class='tb' href='http://company.okwei.com/yun/list'>全部</a>";
		mainvo.setClassone(0);
		mainvo.setClasstwo(0);
		mainvo.setType(0);
		mainvo.setClassid(0);
		mainvo.setCurrentType(clumbs);
		mainvo.setAlltype(alltype);
		mainvo.setHpNavagation(null);
		mainvo.setSupmsgList(supmsglist);
		return mainvo;
	}
	/**
	 * 二级分类的方法
	 * @return
	 */
	@Override
	public YHpageMainVO methodTowLevel(){
		YHpageMainVO mainvo=new YHpageMainVO();
		List<BusKeyValue> alltype = getAllProductClass(1);
		PProductClass pc = yunsupDao.getProductClassByCode(this.classid);
		PProductClass uppc = yunsupDao.getProductClassByCode(pc==null?0:pc.getParentId());
		List<BusKeyValue> erjitype = getAllProductClass(uppc==null?0:uppc.getClassId());
		
		YHomePageNavagation navagation = new YHomePageNavagation();
		navagation.setHead(uppc==null?"":uppc.getClassName());
		navagation.setHeadcode(uppc==null?0:uppc.getClassId());
		navagation.setAll("全部");
		navagation.setAllcode(uppc==null?0:uppc.getClassId());
		navagation.setTypelist(erjitype);
		YunSupMsgListVO supmsglist = getSupMsgListByID(this.classid,pc==null?0:pc.getParentId(),this.pageindex,this.pagesize);
		String clumbs = "<a href='http://www.okwei.com'>微店网首页</a> &gt; "
		        +"<a href='http://company.okwei.com/yun/list'>工厂</a> &gt; "
		        +"<a href='http://company.okwei.com/yun/list?c="+(uppc==null?0:uppc.getClassId())+"&type=1'>"+(uppc==null?"":uppc.getClassName())+"</a> &gt; "
		        +"<a class='tb' href='http://company.okwei.com/yun/list?c="+(pc==null?0:pc.getClassId())+"&type=2'>"+(pc==null?"":pc.getClassName())+"</a>";
		mainvo.setType(2);
		mainvo.setClassid(this.classid);
		mainvo.setClassone(uppc==null?0:uppc.getClassId());
		mainvo.setClasstwo(pc==null?0:pc.getClassId());
		mainvo.setCurrentType(clumbs);
		mainvo.setAlltype(alltype);
		mainvo.setHpNavagation(navagation);
		mainvo.setSupmsgList(supmsglist);
		return mainvo;
	}
	/**
	 * 一级分类的方法
	 * @return
	 */
	@Override
	public YHpageMainVO methodOnetLevel(){
		YHpageMainVO mainvo=new YHpageMainVO();
		List<BusKeyValue> alltype = getAllProductClass(1);
		PProductClass pc = yunsupDao.getProductClassByCode(this.classid);
		List<BusKeyValue> erjitype = getAllProductClass(this.classid);
		YHomePageNavagation navagation = new YHomePageNavagation();
		navagation.setHead(pc==null?"":pc.getClassName());
		navagation.setHeadcode(this.classid);
		navagation.setAll("全部");
		navagation.setAllcode(this.classid);
		navagation.setTypelist(erjitype);
		YunSupMsgListVO supmsglist = getSupMsgListByID(this.classid,pc==null?0:pc.getParentId(),this.pageindex,this.pagesize);
		String clumbs = "<a href='http://www.okwei.com'>微店网首页</a> &gt; "
		        +"<a href='http://company.okwei.com/yun/list'>工厂</a> &gt; "
		        +"<a class='tb' href='http://company.okwei.net/yun/list?c="+(pc==null?0:pc.getClassId()==null?0:pc.getClassId())+"&type=1'>"+(pc==null?"":pc.getClassName()==null?"":pc.getClassName())+"</a>";
		mainvo.setType(1);
		mainvo.setClassid(this.classid);
		mainvo.setClassone(pc==null?0:pc.getClassId());
		mainvo.setClasstwo(0);
		mainvo.setCurrentType(clumbs);
		mainvo.setAlltype(alltype);
		mainvo.setHpNavagation(navagation);
		mainvo.setSupmsgList(supmsglist);
		return mainvo;
	}
	
	//以下辅助的逻辑方法
    /**
     * 根据上级id查找下级分类
     * @param parentid 上级分类ID
     * @return
     */
	@Transactional
	private List<BusKeyValue> getAllProductClass(Integer parentid)
    {
    	@SuppressWarnings("unchecked")
		List<BusKeyValue> allkv = (List<BusKeyValue>) LocalRedisUtil.getObject("web_productclass_"+parentid);
    	if(allkv==null)
    	{
    		allkv = new ArrayList<BusKeyValue>();
    		List<PProductClass> pcList = yunsupDao.getProductClassByUpID(parentid);
        	if(pcList!=null && pcList.size()>0)
        	{
        		for(PProductClass pc:pcList)
        		{
        			BusKeyValue bkv = new BusKeyValue();
        			bkv.setTypeid(pc.getClassId());
        			bkv.setTypename(pc.getClassName());
        			if(parentid==1)
        			{
        				switch(pc.getClassName().trim())
        				{
        				case "衣":bkv.setCssclass("spn_red1");break;
        				case "食":bkv.setCssclass("spn_red2");break;
        				case "住":bkv.setCssclass("spn_red3");break;
        				case "行":bkv.setCssclass("spn_red4");break;
        				case "用":bkv.setCssclass("spn_red5");break;
        				case "玩":bkv.setCssclass("spn_red6");break;
        				case "学":bkv.setCssclass("spn_red7");break;
        				default:break;
        				}
        			}
        			allkv.add(bkv);
        		}
        		
        		LocalRedisUtil.setObject("web_productclass_"+parentid, allkv, 1800);
        	}
    	}
    	
    	return allkv;
    }
    /**
     * 查找供应商列表，用到了redis缓存
     * @param classid 分类ID
     * @param parentid 上级分类ID
     * @param pageindex 当前页码
     * @param pagesize 每页数据量
     * @return
     */
	private YunSupMsgListVO getSupMsgListByID(Integer classid,Integer parentid,Integer pageindex,Integer pagesize)
    {
    	YunSupMsgListVO supListVo = (YunSupMsgListVO) LocalRedisUtil.getObject("web_yunsuplist_"+classid.toString()+"+"+pageindex.toString());
    	if(supListVo==null)
    	{
    		supListVo = new YunSupMsgListVO();
    		
    		YunSupplierList AllsupList = yunsupDao.getYunSuplierById(classid, parentid, pageindex, pagesize);
    		supListVo.setPagecount(AllsupList.getPagecount());
    		supListVo.setItemcount(AllsupList.getItemcount());
    		supListVo.setPageindex(AllsupList.getPageindex());
    		supListVo.setPagesize(AllsupList.getPagesize());
    		
    		List<UYunSupplier> yunsupplierlist = AllsupList.getSupplierlist();
    		
    		if(yunsupplierlist!=null && yunsupplierlist.size()>0){
    		    List<YunSupMsgVO> supList = new ArrayList<YunSupMsgVO>();
    		    
    			Long[] weiids = new Long[yunsupplierlist.size()];
    		    for(int i=0;i<yunsupplierlist.size();i++)
    		    	weiids[i]=yunsupplierlist.get(i).getWeiId();

    		    List<USupplyer> baseSuplist = yunsupDao.getSupBaseMsgByIds(weiids);
    		    List<Object[]> categoryList = yunsupDao.getCategoryByIds(weiids);
    		    List<UShopInfo> infoList = yunsupDao.getShopInfoByIds(weiids);
    		    List<Object[]> defaultimgList = yunsupDao.getSupplierProImg(weiids);
    		    
    		    for(UYunSupplier uysup:yunsupplierlist)
    		    {
    		    	YunSupMsgVO yunsup = new YunSupMsgVO();
    		    	USupplyer basetemp=new USupplyer();
    		    	UShopInfo infotemp = new UShopInfo();
    		    	if(baseSuplist!=null && baseSuplist.size()>0)
    		    	{
    		    		for(USupplyer basesup:baseSuplist)
        		    	{
        		    		if(basesup.getWeiId().equals(uysup.getWeiId()))
        		    		{
        		    			basetemp=basesup;
        		    			break;
        		    		}
        		    	}
    		    	}
    		    	
    		    	
    		    	String bustemp = "";
    		    	if(categoryList!=null && categoryList.size()>0)
    		    	{
    		    		for(Object[] category:categoryList)
        		    	{
        		    		Long weiid = Long.parseLong(category[0].toString());
        		    		String catestr = category[1].toString();
        		    		if(uysup.getWeiId().equals(weiid))
        		    		{
        		    			bustemp+=catestr+"、";
        		    		}
        		    	}
    		    	}
    		    	
    		    	if(infoList!=null&&infoList.size()>0)
    		    	{
    		    		for(UShopInfo info:infoList)
    		    		{
    		    			if(uysup.getWeiId().equals(info.getWeiId()))
    		    			{
    		    				infotemp = info;
    		    				break;
    		    			}
    		    		}
    		    	}
    		    	
    		    	String defaultimg = "";
    		    	if(defaultimgList!=null&& defaultimgList.size()>0)
    		    	{
    		    		for(Object[] defau:defaultimgList)
    		    		{
    		    			Long weiid = Long.parseLong(defau[0].toString());
    		    			if(uysup.getWeiId().equals(weiid))
    		    			{
    		    				defaultimg = defau[1].toString();
    		    				break;
    		    			}
    		    		}
    		    	}
    		    	
    		    	if(bustemp.length()>0)
    		    		bustemp = bustemp.substring(0, bustemp.length()-1);
    		    	yunsup.setWeiid(uysup.getWeiId()==null?0:uysup.getWeiId());
    		    	yunsup.setSupname(basetemp.getCompanyName());
    		    	yunsup.setSupimg(ImgDomain.GetFullImgUrl(defaultimg, 24));
    		    	yunsup.setMainbus(bustemp);
    		    	yunsup.setArea(yunsupDao.getAreaNameByCode(uysup.getProvince()==null?0:uysup.getProvince())
    		    			+" "+yunsupDao.getAreaNameByCode(uysup.getCity()==null?0:uysup.getCity()));
    		    	yunsup.setProductcount(infotemp.getProductCount()==null?0:infotemp.getProductCount());
    		    	yunsup.setShelvecount(infotemp.getShelveCount()==null?0:infotemp.getShelveCount());
    		    	
    		    	supList.add(yunsup);
    		    }
    		    supListVo.setSupmsgList(supList);
    		    LocalRedisUtil.setObject("web_yunsuplist_"+classid.toString()+"+"+pageindex.toString(), supListVo, 1800);
    		}
    	}
    	return supListVo;
    }
}
