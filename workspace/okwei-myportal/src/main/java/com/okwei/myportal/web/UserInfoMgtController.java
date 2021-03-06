package com.okwei.myportal.web;

import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.okwei.bean.domain.UCustomerAddr;
import com.okwei.bean.domain.UShopInfo;
import com.okwei.bean.domain.UWallet;
import com.okwei.bean.domain.UWeiSeller;
import com.okwei.bean.domain.User;
import com.okwei.bean.vo.LoginUser;
import com.okwei.common.AjaxUtil;
import com.okwei.myportal.bean.vo.AccountVO;
import com.okwei.myportal.bean.vo.AddressVO; 
import com.okwei.myportal.bean.vo.SettingsVO;
import com.okwei.myportal.bean.vo.ShopInfoVO;
import com.okwei.myportal.service.IAdressService;
import com.okwei.myportal.service.IDistributorMgtService;
import com.okwei.myportal.service.IUserInfoService;
import com.okwei.service.IRegionService;
import com.okwei.util.ImgDomain;
import com.okwei.util.MD5Util;
import com.okwei.web.base.SSOController;

@Controller
@RequestMapping(value = "/userInfo")
public class UserInfoMgtController extends SSOController
{

    private final static Log logger = LogFactory.getLog(UserInfoMgtController.class);

    @Autowired
    private IDistributorMgtService distributorMgtService;
    @Autowired
    private IUserInfoService userInfoService;
    @Autowired
    private IRegionService tBatchMarket;
    @Autowired
    private IAdressService adressService;

    /**
     * 到达店铺资料修改页面
     * @param model
     * @return
     */
    @RequestMapping(value = "/index")
	public String index(Model model) {
		logger.info("UserInfoMgtController index starting.......");
		LoginUser user = getUserOrSub();
		ShopInfoVO shopinfo = userInfoService.getUShopInfo(user);
		if (shopinfo.getShowBatchSupplyer() == 1) {
			StringBuffer area = new StringBuffer();
			area.append(tBatchMarket.getNameByCode(shopinfo.getProvince())).append("省");
			area.append(tBatchMarket.getNameByCode(shopinfo.getCity()));
			area.append(tBatchMarket.getNameByCode(shopinfo.getDistrict()));
			shopinfo.setAddrArea(area.toString());
		}
		model.addAttribute("shopinfo",shopinfo);
		model.addAttribute("fullImgUrl",ImgDomain.GetFullImgUrl(shopinfo.getShopImg()));
		return "userinfo/index";
	}
    /**
     * 获取图片全路径
     * @param img
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/fullImgUrl",method =
    {RequestMethod.POST,RequestMethod.GET})
    public String getFullImgUrl(String img)
    {
    	String imgUrl = "";
    	if (StringUtils.isNotEmpty(img)) {
    		imgUrl = ImgDomain.GetFullImgUrl(img);
		}
        return AjaxUtil.ajaxSuccess(imgUrl);
    }
    /**
     * 保存店铺信息
     * @param shopinfo
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/saveShopInfo",method = RequestMethod.POST)
    public String saveShopInfo(UShopInfo shopinfo)
    {
    	LoginUser user = getUserOrSub();
        long weiId = user.getWeiID();
        shopinfo.setWeiId(weiId);
    	String msg = "-1";
    	int ret = userInfoService.updateShopInfo(shopinfo);
    	if (ret == 0) {
			msg = "0";
		} 
        return AjaxUtil.ajaxSuccess(msg);
    }

    /**
     * 删除地址
     */
    @ResponseBody
    @RequestMapping(value = "/deleteAddr", method = { RequestMethod.POST, RequestMethod.GET })
    public String deleteAddr(int caddrId) {
	LoginUser user = super.getUserOrSub();
	long weiid = user.getWeiID();
	if (caddrId <= 0) {
	    return AjaxUtil.ajaxSuccess("地址ID获取失败");
	}
	String result = "";
	try {
	    if (adressService.deleteAddress(weiid, caddrId) > 0) {
		result = "1";
	    } else {
		result = "删除失败";
	    }
	} catch (Exception e) {
	    result = "删除失败";
	}
	return AjaxUtil.ajaxSuccess(result);
    }

    @ResponseBody
    @RequestMapping(value = "/changePwd",method = RequestMethod.POST)
    public String changePWD(String oldPwd,String reNewPwd)
    {
    	LoginUser user = getUserOrSub();
		UWeiSeller weiSeller = userInfoService.getUWeiSeller(user.getWeiID());
		if (!MD5Util.md5s(oldPwd).equals(weiSeller.getPassWord())) {
			return AjaxUtil.ajaxFail("1");
		}
		weiSeller.setPassWord(MD5Util.md5s(reNewPwd));
		int ret = userInfoService.modifyUWeiSeller(weiSeller);
		if (ret == 0 ) {
			return AjaxUtil.ajaxSuccess("0");
		} 
        return AjaxUtil.ajaxFail("2");
    }
    
    @ResponseBody
    @RequestMapping(value = "/setPWD",method = RequestMethod.POST)
    public String setPWD(String loginPWD)
    {
    	LoginUser user = getUserOrSub();
		UWeiSeller weiSeller = userInfoService.getUWeiSeller(user.getWeiID());
		weiSeller.setPassWord(MD5Util.md5s(loginPWD));
		int ret = userInfoService.modifyUWeiSeller(weiSeller);
		if (ret == 0 ) {
			return AjaxUtil.ajaxSuccess("0");
		} 
        return AjaxUtil.ajaxSuccess("-1");
    }

    @RequestMapping(value = "/bindAccount")
    public String account(Model model)
    {
        LoginUser user = super.getUserOrSub();
        long weiid = user.getWeiID();
        AccountVO account = userInfoService.getAccountVO(weiid);
        model.addAttribute("account",account);
        model.addAttribute("userinfo",user);
        return "userinfo/account";
    }

    @RequestMapping(value = "/address")
    public String address(Model model)
    {
    	LoginUser user = super.getUserOrSub();
    	long weiid = user.getWeiID();
    	List<AddressVO> list = adressService.getAddressList(weiid);
    	// 收货地址列表
    	model.addAttribute("list", list);
    	return "userinfo/address";
     }
    /**
     * 设为默认
     */
    @ResponseBody
    @RequestMapping(value = "/setDefaultAddr", method = { RequestMethod.POST, RequestMethod.GET })
    public String setDefaultAddr(int caddrId) {
	LoginUser user = super.getUserOrSub();
	long weiid = user.getWeiID();
	if (caddrId <= 0) {
	    return AjaxUtil.ajaxSuccess("地址ID获取失败");
	}
	String result = "";
	try {
	    if (adressService.setDefault(weiid, caddrId) > 0) {
		result = "1";
	    } else {
		result = "设为地址操作失败";
	    }
	} catch (Exception e) {
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
        LoginUser user = super.getUserOrSub();
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
            if(adressService.saveOrUpdateAdd(entity) > 0)
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
    
    @ResponseBody
    @RequestMapping(value = "/changePayPwd",method = RequestMethod.POST)
    public String changePayPWD(String oldPayPwd,String newPayPwd)
    {
    	LoginUser user = getUserOrSub();
		UWallet wallet = userInfoService.UWallet(user.getWeiID());
		if (!MD5Util.md5s(oldPayPwd).equals(wallet.getPayPassword())) {
			return AjaxUtil.ajaxFail("1");
		}
		wallet.setPayPassword(MD5Util.md5s(newPayPwd));
		int ret = userInfoService.modifyUWallet(wallet);
		if (ret == 0 ) {
			return AjaxUtil.ajaxSuccess("0");
		} 
        return AjaxUtil.ajaxFail("2");
    }
    
    @ResponseBody
    @RequestMapping(value = "/setPayPWD",method = RequestMethod.POST)
    public String setPayPWD(String payPWD)
    {
    	LoginUser user = getUserOrSub();
    	UWallet wallet = userInfoService.UWallet(user.getWeiID());
    	wallet.setPayPassword(MD5Util.md5s(payPWD));
		int ret = userInfoService.modifyUWallet(wallet);
		if (ret == 0 ) {
			return AjaxUtil.ajaxSuccess("0");
		} 
        return AjaxUtil.ajaxSuccess("-1");
    }

    /**
     * 安全设置
     */
    @RequestMapping(value = "/settings")
    public String settings(Model model)
    {
        LoginUser user = getUserOrSub();
        long weiId = user.getWeiID();
        SettingsVO setting = userInfoService.findSettings(weiId);
        model.addAttribute("setting",setting);
        return "userinfo/settings";
    }

}
