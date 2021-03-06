package com.okwei.myportal.web;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.okwei.bean.domain.TFansApply;
import com.okwei.bean.domain.UWeiSeller;
import com.okwei.bean.vo.LoginUser;
import com.okwei.bean.vo.ReturnModel;
import com.okwei.bean.vo.ReturnStatus;
import com.okwei.common.Limit;
import com.okwei.common.PageResult;
import com.okwei.myportal.bean.dto.FansDTO;
import com.okwei.myportal.bean.dto.RelationDTO;
import com.okwei.myportal.bean.dto.RelationDTO.Type; 
import com.okwei.myportal.bean.vo.RelationVO;
import com.okwei.myportal.service.IRelationMgtService;
import com.okwei.web.base.SSOController;

/**
 * 
 * @ClassName: RelationMgtController
 * @Description: 上下游关系管理
 * @author xiehz
 * @date 2015年7月14日 上午10:05:56
 *
 */
@RequestMapping(value = "/relationMgt")
@Controller
public class RelationMgtController extends SSOController {

	@Autowired
	private IRelationMgtService relationMgtService;

	private static int pageSize = 10;

	/**
	 * 获取铁钢粉丝
	 */
	@RequestMapping(value = "/fans")
	public String getFans(@ModelAttribute("dto") FansDTO dto,@RequestParam(required = false, defaultValue = "1") int pageId, Model model,@RequestParam(required = false, defaultValue = "-1") short statu) {
		LoginUser user = super.getUserOrSub();
		//user.setWeiID(1036799l);
		if(user.getWeiID()<=0&&user.getWeiID()!=8888){
			return "redirect:/maininfo/maininfo";// 没有权限
		}
		if(statu!=-1){
			dto.setStatu(statu);
		}
		PageResult<TFansApply> pageResult = relationMgtService.getFans(dto,Limit.buildLimit(pageId, pageSize));
		model.addAttribute("pageResult", pageResult);
		model.addAttribute("userinfo", user);
		return "relationmgt/tieFans";
	}
	
	/**
	 * 修改审核状态
	 */
	@ResponseBody
	@RequestMapping("/change")
	public ReturnModel change(Short state,Long weiId){
		ReturnModel rm=new ReturnModel();
		rm=relationMgtService.changeStatus(state,weiId);
		return rm;
	}
	
	/**
	 * 上游供应
	 */
	@RequestMapping(value = "/upstream")
	public String getUpStream(@ModelAttribute("dto") RelationDTO dto, @RequestParam(required = false, defaultValue = "1") int pageId, Model model) {
		LoginUser user = super.getUserOrSub();
		dto.setUserId(user.getWeiID());
		PageResult<RelationVO> pageResult = relationMgtService.getUpStream(dto, Limit.buildLimit(pageId, pageSize));
		model.addAttribute("pageResult", pageResult);
		model.addAttribute("userinfo", user);
		return "relationmgt/upstream";
	}

	/**
	 * 下游分销
	 */
	@RequestMapping(value = "/downstream/{opType}")
	public String getDownStream(@PathVariable String opType, @ModelAttribute("dto") RelationDTO dto,
			@RequestParam(required = false, defaultValue = "1") int pageId, Model model) {
		LoginUser user = super.getUserOrSub();
		dto.setUserId(user.getWeiID());
		dto.setType(Type.valueOf(opType));
		// 普通微店主仅显示直接分销商，无分类选择
		if (null != user.getyHrz() && user.getyHrz() == 0 && null != user.getYrz() && user.getYrz() == 0 && null != user.getYunS() && user.getYunS() == 0
				&& null != user.getBatchS() && user.getBatchS() == 0 && null != user.getbHrz() && user.getbHrz() == 0 && null != user.getBrz()
				&& user.getBrz() == 0) {
			dto.setType(Type.distributor);
			dto.setOnlyWeiSeller(1);
		}

		// 缺省选中"全部"
		if (null == dto.getType()) {
			dto.setType(Type.all);
		}
		PageResult<RelationVO> pageResult = relationMgtService.getDownStream(dto, Limit.buildLimit(pageId, pageSize));
		model.addAttribute("pageResult", pageResult);
		model.addAttribute("userinfo", user);
		return "relationmgt/downstream";
	}

}
