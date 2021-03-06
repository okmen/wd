package com.okwei.supplyportal.web;

import java.util.List;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.okwei.bean.domain.UCustomerAddr;
import com.okwei.common.AjaxUtil;
import com.okwei.supplyportal.bean.vo.AddressVO;
import com.okwei.supplyportal.bean.vo.BaseSSOController;
import com.okwei.supplyportal.bean.vo.LoginUser;
import com.okwei.supplyportal.service.IUserInfoService;

@Controller
@RequestMapping(value = "/userinfo")
public class UserInfoController extends BaseSSOController
{

    @Autowired
    private IUserInfoService userInfoService;

    @RequestMapping(value = "/address",method =
    {RequestMethod.GET})
    public String refundetail(Model model)
    {
        LoginUser user = super.getLoginUser();
        long weiid = user.getWeiID();
        List<AddressVO> list = userInfoService.getAddressList(weiid);
        // 收货地址列表
        model.addAttribute("list",list);
        return "userinfo/address";
    }

    /**
     * 删除地址
     */
    @ResponseBody
    @RequestMapping(value = "/deleteAddr",method =
    {RequestMethod.POST,RequestMethod.GET})
    public String deleteAddr(int caddrId)
    {
        LoginUser user = super.getLoginUser();
        long weiid = user.getWeiID();
        if(caddrId <= 0)
        {
            return AjaxUtil.ajaxSuccess("地址ID获取失败");
        }
        String result = "";
        try
        {
            if(userInfoService.deleteAddress(weiid,caddrId) > 0)
            {
                result = "1";
            }
            else
            {
                result = "删除失败";
            }
        }
        catch(Exception e)
        {
            result = "删除失败";
        }
        return AjaxUtil.ajaxSuccess(result);
    }

    /**
     * 设为默认
     */
    @ResponseBody
    @RequestMapping(value = "/setDefaultAddr",method =
    {RequestMethod.POST,RequestMethod.GET})
    public String setDefaultAddr(int caddrId)
    {
        LoginUser user = super.getLoginUser();
        long weiid = user.getWeiID();
        if(caddrId <= 0)
        {
            return AjaxUtil.ajaxSuccess("地址ID获取失败");
        }
        String result = "";
        try
        {
            if(userInfoService.setDefault(weiid,caddrId) > 0)
            {
                result = "1";
            }
            else
            {
                result = "设为地址操作失败";
            }
        }
        catch(Exception e)
        {
            result = "设为地址操作失败";
        }
        return AjaxUtil.ajaxSuccess(result);
    }

    /**
     * 保存收货地址
     */
    @ResponseBody
    @RequestMapping(value = "/saveAddress",method =
    {RequestMethod.POST,RequestMethod.GET})
    public String submitApply(UCustomerAddr entity)
    {
        LoginUser user = super.getLoginUser();
        long weiid = user.getWeiID();
        // 验证
        String check = checkAddrEntity(entity);
        if(check != "ok")
        {
            return AjaxUtil.ajaxSuccess(check);
        }
        entity.setWeiId(weiid);
        String result = "";
        try
        {
            if(userInfoService.saveOrUpdateAdd(entity) > 0)
            {
                result = "1";
            }
            else
            {
                result = "保存收货地址失败";
            }
        }
        catch(Exception e)
        {
            result = "保存收货地址失败";
        }
        return AjaxUtil.ajaxSuccess(result);
    }

    private String checkAddrEntity(UCustomerAddr entity)
    {
        if(isNullOrEmpty(entity.getMobilePhone()))
        {
            return "手机不能为空";
        }
        else
        {
            if(!Pattern.matches("^[1][3,4,5,7,8][0-9]{9}$",entity.getMobilePhone()))
            {
                return "手机格式错误";
            }
        }
        if(isNullOrEmpty(entity.getReceiverName()))
        {
            return "收货人不能为空";
        }
        if(entity.getProvince() == null || entity.getProvince().intValue() == 0)
        {
            return "请选择省";
        }
        if(entity.getCity() == null || entity.getCity().intValue() == 0)
        {
            return "请选择市";
        }
        if(entity.getDistrict() == null || entity.getDistrict().intValue() == 0)
        {
            return "请选择区";
        }
        if(isNullOrEmpty(entity.getDetailAddr()))
        {
            return "详细地址不能为空";
        }
        if(isNullOrEmpty(entity.getQq()))
        {
            return "QQ不能为空";
        }
        return "ok";
    }

    private boolean isNullOrEmpty(String str)
    {
        if(str == null || str == "")
            return true;
        return false;
    }

    @RequestMapping(value = "/ing")
    public String error(Model model) {
    	 LoginUser user = super.getLoginUser();
         model.addAttribute("userinfo",user);
	     return "error/ing";

    }
}
