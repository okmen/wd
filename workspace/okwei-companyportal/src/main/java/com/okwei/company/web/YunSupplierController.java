package com.okwei.company.web;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.okwei.bean.domain.PProductClass;
import com.okwei.bean.vo.LoginUser;
import com.okwei.bean.vo.agent.HeadInfo;
import com.okwei.common.AjaxUtil;
import com.okwei.common.JsonUtil;
import com.okwei.common.Limit;
import com.okwei.common.PageResult;
import com.okwei.company.bean.vo.BusKeyValue;
import com.okwei.company.bean.vo.YunSupParam;
import com.okwei.company.bean.vo.YunSupVo;
import com.okwei.company.service.IYunsupService;
import com.okwei.service.IAgentCommonService;
import com.okwei.service.IRegionService;
import com.okwei.web.base.SSOController;

@Controller
@RequestMapping(value = "/yun")
public class YunSupplierController extends SSOController {
	private final static Log logger = LogFactory.getLog(YunSupplierController.class);
	@Autowired
	private IYunsupService yunsupService;
	@Autowired
	private IAgentCommonService commonService;
	@Autowired
	private IRegionService tBatchMarket;

	@RequestMapping(value = "/list", method = { RequestMethod.POST, RequestMethod.GET })
	public String index(String w, Model model, @RequestParam(required = false, defaultValue = "1") int pageId, @ModelAttribute("queryparam") YunSupParam queryparam) {
		LoginUser user = getUserOrSub();
		// common
		if (w != null && w != "") {
			user.setTgWeiID(w);
		} else {
			if (user.getWeiID() > 0) {
				user.setTgWeiID(String.valueOf(user.getWeiID()));
			}
		}
		user.setCartCount(commonService.getCartCount(user.getWeiID()));
		HeadInfo headinfo = commonService.getHeadInfo();
		model.addAttribute("user", user);
		model.addAttribute("mtype", "1");
		model.addAttribute("headinfo", headinfo);
		StringBuffer currentNav = new StringBuffer();
		// String areaName = "";
		PageResult<YunSupVo> pageRes = null;
		// 获取大类
		List<BusKeyValue> classoneList = null;
		// 二级分类
		List<PProductClass> classtwoList = new ArrayList<PProductClass>();
		// 三级分类
		List<PProductClass> classthreeList = new ArrayList<PProductClass>();
		// 品牌列表
		/*
		 * List<PBrand> brandList = new ArrayList<PBrand>(); List<AreaVo>
		 * areaList = null;
		 */
		try {
			classoneList = yunsupService.getProductClassOne(1);
			// areaList = yunsupService.getAreaListByCondition(queryparam);
			switch (queryparam.getType()) {
			case 0:// 查询所有 一级分类
				queryparam.setType(0);
				if (queryparam.getClassone() == 0) {
					classthreeList = yunsupService.getHotClasstwoList();// 热门类目
					// brandList = yunsupService.getHotBrandList();// 热门品牌
				} else {
					classtwoList = yunsupService.getRelevantClassList(queryparam);// 相关类目
					// brandList =
					// yunsupService.getRelevantBrandList(queryparam);// 相关品牌
				}
				break;
			case 1:// 二级分类
				queryparam.setType(1);
				if (queryparam.getClassone() == 0) {
					classthreeList = yunsupService.getHotClasstwoList();// 热门类目
				} else {
					classthreeList = yunsupService.getRelevantClassList(queryparam);// 相关类目
				}
				break;
			default:
				break;
			}
			if (queryparam.getClassone() != null && queryparam.getClassone() > 0) {
				for (BusKeyValue bv : classoneList) {
					if (bv.getTypeid().equals(queryparam.getClassone())) {
						currentNav.append("&nbsp;&gt;&nbsp;" + bv.getTypename());
					}
				}
			} else {
				// currentNav.append("&nbsp;&gt;&nbsp;全部");
			}
			if (queryparam.getClasstwo() != null && queryparam.getClasstwo() > 0) {
				for (PProductClass pc : classtwoList) {
					if (pc.getClassId().equals(queryparam.getClasstwo())) {
						currentNav.append("&nbsp;&gt;&nbsp;" + pc.getClassName());
					}
				}
			}
			if (queryparam.getClassthree() != null && queryparam.getClassthree() > 0) {
				for (PProductClass pc : classthreeList) {
					if (pc.getClassId().equals(queryparam.getClassthree())) {
						currentNav.append("&nbsp;&gt;&nbsp;" + pc.getClassName());
					}
				}
			}
			/*
			 * if (queryparam.getBrandid() != null && queryparam.getBrandid() >
			 * 0) { for (PBrand pb : brandList) { if
			 * (pb.getBrandId().equals(queryparam.getBrandid())) {
			 * currentNav.append("&nbsp;&gt;&nbsp;" + pb.getBrandName()); } } }
			 */
			pageRes = yunsupService.getUBatchSupplyerList(Limit.buildLimit(pageId, 20), queryparam, user.getWeiID());
		} catch (Exception e) {
			logger.error(e);
		}
		model.addAttribute("classoneList", classoneList);
		model.addAttribute("classtwoList", classtwoList);
		model.addAttribute("classthreeList", classthreeList);
		// model.addAttribute("brandList", brandList);
		model.addAttribute("currentNav", currentNav.toString());
		// model.addAttribute("areaList", areaList);
		// model.addAttribute("areaName", areaName);
		model.addAttribute("pageRes", pageRes);
		model.addAttribute("queryparam", queryparam);
		return "yun/yunsuplist";
	}

	@ResponseBody
	@RequestMapping(value = "/attentionSup", method = { RequestMethod.POST, RequestMethod.GET })
	public String attentionSup(long supID, int type) {
		LoginUser user = getUserOrSub();
		String msg = "-1";
		if (user == null || user.getWeiID() < 1) {
			return JsonUtil.objectToJson("2");// 登录已过期,请重新登录！
		}
		if (type == 0 || type == 1) {
			try {
				yunsupService.attentionSup(user.getWeiID(), type, supID);
			} catch (Exception e) {
				logger.error(e);
				return AjaxUtil.ajaxSuccess("-2");
			}
			msg = "0";// 0-关注成功
		}
		return AjaxUtil.ajaxSuccess(msg);
	}

}
