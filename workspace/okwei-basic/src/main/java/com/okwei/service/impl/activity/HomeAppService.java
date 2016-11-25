package com.okwei.service.impl.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.okwei.bean.domain.AHomeApp;
import com.okwei.bean.domain.AHomeMain;
import com.okwei.bean.dto.activity.AHomeAppDTO;
import com.okwei.bean.vo.ReturnModel;
import com.okwei.bean.vo.ReturnStatus;
import com.okwei.bean.vo.activity.AHomeAppVO;
import com.okwei.common.Limit;
import com.okwei.common.PageResult;
import com.okwei.dao.IBaseDAO;
import com.okwei.service.activity.IHomeAppService;
import com.okwei.service.impl.BaseService;
import com.okwei.util.ImgDomain;

@Service
public class HomeAppService extends BaseService implements IHomeAppService {

    @Autowired
    private IBaseDAO baseDAO;

    @Override
    public ReturnModel addAHomeApp(AHomeAppDTO appDTO) {
	ReturnModel result = new ReturnModel();
	Object hma = null;
	if (appDTO != null) {
	    hma = baseDAO.getUniqueResultByHql("select homeId from AHomeMain where position=? and className=?", appDTO.getClassId(), appDTO.getClassNames());
	} else {
	    result.setStatusreson("非法操作");
	}
	if (hma != null) {
		baseDAO.executeHql("delete from AHomeApp where position=?",appDTO.getHomeapp().getPosition());
//	    // 先删除所有的
//		if(appDTO.getHomeapp().getHomeId()!=null){
//			baseDAO.executeHql("delete from AHomeApp where homeId=?", appDTO.getHomeapp().getHomeId());
//		}else{
//			baseDAO.executeHql("delete from AHomeApp where homeId=?", (int) hma);
//		}    
	    appDTO.getHomeapp().setHomeId((int) hma);
	    baseDAO.save(appDTO.getHomeapp());
	    result.setStatu(ReturnStatus.Success);
	} else {
	    result.setStatusreson("提交的数据格式不正确");
	    result.setStatu(ReturnStatus.DataError);
	}
	return result;
    }

    @Override
    public PageResult<AHomeAppVO> findAHomeApp(Limit limit) {
	PageResult<AHomeApp> page = baseDAO.findPageResult("from AHomeApp ORDER BY position asc", limit);
	if (page != null) {
	    List<AHomeApp> applist = page.getList();
	    if (applist != null && applist.size() > 0) {
		List<Integer> ids = new ArrayList<Integer>();
		for (AHomeApp app : applist) {
		    if (app.getHomeId() != null) {
			ids.add(app.getHomeId());
		    }
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ids", ids);
		List<AHomeMain> amlist = baseDAO.findByMap("from AHomeMain where homeId in(:ids)", params);
		boolean flag = false;
		if (amlist != null && amlist.size() > 0)
		    flag = true;

		List<AHomeAppVO> listVO = new ArrayList<AHomeAppVO>();
		for (AHomeApp app : applist) {
		    AHomeAppVO appVO = new AHomeAppVO();
		    appVO.setId(app.getId());
		    appVO.setBannerImage(ImgDomain.GetFullImgUrl(app.getBannerImage(),24));
		    appVO.setHomeId(app.getHomeId());
		    appVO.setPosition(app.getPosition());
		    appVO.setTitle(app.getTitle());
		    appVO.setHomeImage(ImgDomain.GetFullImgUrl(app.getHomeImage(),24));
		    if (flag) {
			for (AHomeMain am : amlist) {
			    if (app.getHomeId().equals(am.getHomeId())) {
				appVO.setClassId(am.getPosition());
				appVO.setClassName(am.getClassName());
				break;
			    }
			}
		    }
		    listVO.add(appVO);
		}
		return new PageResult<AHomeAppVO>(page.getTotalCount(), limit, listVO);
	    }
	}
	return null;

    }
    @Override
	public AHomeApp findAHomeApp(int areaId) {
    	AHomeApp app = null;
    	if(areaId>0){
    		app =baseDAO.getUniqueResultByHql("from AHomeApp where position=?", areaId);
    	}
    	if(app!=null){
    		app.setBannerImage(ImgDomain.GetFullImgUrl(app.getBannerImage(),24));
    		app.setHomeImage(ImgDomain.GetFullImgUrl(app.getHomeImage(), 24));
    		return app;
    	}
    	return null;
    }

}
