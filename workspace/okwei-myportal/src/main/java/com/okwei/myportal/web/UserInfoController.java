package com.okwei.myportal.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.okwei.bean.domain.AActSupplier;
import com.okwei.bean.enums.ActProductVerState;
import com.okwei.bean.vo.LoginUser;
import com.okwei.myportal.bean.vo.UserInfoCountVO;
import com.okwei.myportal.service.IActivityService;
import com.okwei.myportal.service.IUserInfoService;
import com.okwei.service.activity.IBaseActivityService;
import com.okwei.web.base.SSOController;

@Controller
@RequestMapping(value = "/maininfo")
public class UserInfoController extends SSOController {
	// private final static Log logger =
	// LogFactory.getLog(UserInfoMgtController.class);
	@Autowired
	IUserInfoService userinfoService;
	@Autowired
	IBaseActivityService actService;
	@Autowired
	IActivityService active;

	@RequestMapping(value = "/maininfo")
	public String index(Model model) {
		LoginUser user = getUserOrSub();
		UserInfoCountVO uservo = userinfoService.getUserCounts(user);

		model.addAttribute("uservo", uservo);
		model.addAttribute("userinfo", user);
		int isGetIn = 0;
		if (user!=null && (user.getPph() != null && user.getPph() > 0) || (user.getBatchS() != null && user.getBatchS() > 0) || (user.getYunS() != null && user.getYunS() > 0)) {//
			model.addAttribute("issupplier", 1);
			Long actID = active.getgetActCelebration(); 
			if (actID!=null&& actID > 0) {
//				AActSupplier sup = actService.getAActSupplier(actID, user.getWeiID());
				long count= actService.count_AActivityProducts(actID, user.getWeiID(), Short.parseShort(ActProductVerState.Ok.toString()));
				if (count >0) {
					isGetIn = 1;
				}
			}
		} else {
			model.addAttribute("issupplier", 0);
		}
		model.addAttribute("isIn", isGetIn);
		return "userinfo/usercenter";
	}

	@RequestMapping(value = "/ing")
	public String error(Model model) {
		LoginUser user = getUserOrSub();
		model.addAttribute("userinfo", user);
		return "error/ing";
	}
}
