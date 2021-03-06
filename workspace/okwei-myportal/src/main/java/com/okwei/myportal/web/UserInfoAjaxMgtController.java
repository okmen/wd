package com.okwei.myportal.web;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.okwei.bean.domain.UWeiSeller;
import com.okwei.bean.vo.LoginUser;
import com.okwei.common.JsonUtil;
import com.okwei.myportal.bean.enums.LoginType;
import com.okwei.myportal.bean.enums.VerifyCodeType; 
import com.okwei.myportal.bean.vo.MsgResult;
import com.okwei.myportal.service.IUserInfoService;
import com.okwei.util.ObjectUtil;
import com.okwei.web.base.SSOController;

@Controller
@RequestMapping(value = "/userInfo")
public class UserInfoAjaxMgtController extends SSOController
{
    @Autowired
    public IUserInfoService userInfoService;

    @ResponseBody
    @RequestMapping(value = "/ajaxuserinfo",method =
    {RequestMethod.POST,RequestMethod.GET})
    public String orderajax(String key,HttpServletRequest request)
    {
        LoginUser user = getUserOrSub();
        long weiid = user.getWeiID();
        String ip = request.getRemoteAddr();
        MsgResult mr = new MsgResult();
        String phone = request.getParameter("phone");// 手机号
        String yzm = request.getParameter("yzm");// 验证码
        switch(key)
        {
            case "getyzm":// 验证手机号码（绑定手机号码）
                mr = getYzm(weiid,phone,yzm);
                break;
            case "yzphone":// 发送手机验证码
                mr = yzPhone(weiid,phone,ip);
                break;
            case "unphone":// 解除绑定手机验证码
                mr = unBindPhone(weiid,ip);
                break;
            case "unbind":// 验证解绑
                mr = verifyUnBind(weiid,yzm);
                break;
            case "unqq"://解绑qq
                mr = unBindQQ(weiid);
                break;
            case "unwx"://解绑微信
                mr = unBindWX(weiid);
                break;
            default:
                break;
        }
        return JsonUtil.objectToJson(mr);
    }
    /**
     * 解绑qq
     * @param weiid
     * @return
     */
    public MsgResult unBindQQ(Long weiid)
    {
        MsgResult mr = new MsgResult();
        try
        {
            userInfoService.updateUnLoginBind(weiid,Short.parseShort(LoginType.qq.toString()));
            mr.setState(1);
            mr.setMsg("成功"); 
        }
        catch(Exception e)
        {
            mr.setState(-1);
            mr.setMsg("失败"); 
        }
        return mr;
    }
    /**
     * 解绑微信
     * @param weiid
     * @return
     */
    public MsgResult unBindWX(Long weiid)
    {
        MsgResult mr = new MsgResult();
        try
        {
            userInfoService.updateUnLoginBind(weiid,Short.parseShort(LoginType.weixin.toString()));
            mr.setState(1);
            mr.setMsg("成功"); 
        }
        catch(Exception e)
        {
            mr.setState(-1);
            mr.setMsg("失败"); 
        }
        return mr;
    }

    /**
     * 验证解绑手机验证码
     */
    public MsgResult verifyUnBind(Long weiid,String yzm)
    {
        MsgResult mr = new MsgResult();
        if(ObjectUtil.isEmpty(yzm))
        {
            mr.setState(-1);
            mr.setMsg("验证码不能为空");
            return mr;
        }
        mr = userInfoService.updateUnBind(weiid,yzm);
        return mr;
    }

    /**
     * 解除绑定验证码
     * 
     * @param weiid
     *            微店号
     * @return
     */
    public MsgResult unBindPhone(Long weiid,String ip)
    {
        MsgResult mr = new MsgResult();
        // 判断手机号今天发送的次数是否超过上限
        boolean surpass = userInfoService.getSendCeiling(weiid,Short.parseShort(VerifyCodeType.unbundl.toString()));
        if(surpass)
        {
            mr.setState(-1);
            mr.setMsg("验证码获取次数过多，请明日重试");
            return mr;
        }
        // 发送验证码
        mr = userInfoService.insertunBindPhone(weiid,ip);
        return mr;
    }

    /**
     * 验证验证码是否正确
     * 
     * @param weiid
     * @param phone
     * @param yzm
     * @return
     */
    public MsgResult getYzm(Long weiid,String phone,String yzm)
    {
        MsgResult mr = new MsgResult();
        // 验证数据是否合法
        if(phone == null || phone.length() != 11 || !isMobileNO(phone))
        {
            mr.setState(-1);
            mr.setMsg("手机格式不正确");
            return mr;
        }
        if(yzm == null || ObjectUtil.isEmpty(yzm))
        {
            mr.setState(-1);
            mr.setMsg("验证码不能为空");
            return mr;
        }
        // 验证次数是否过多
        mr = userInfoService.getPhoneVerify(weiid,phone,yzm);
        return mr;
    }

    /**
     * 验证手机号码，发送验证码
     * 
     * @param weiid
     *            微店号
     * @param phone
     *            手机号
     * @return
     */
    public MsgResult yzPhone(long weiid,String phone,String ip)
    {
        MsgResult mr = new MsgResult();
        if(phone == null || phone.length() != 11 || !isMobileNO(phone))
        {
            mr.setState(-1);
            mr.setMsg("手机格式不正确");
            return mr;
        }
        // 判断手机号是否已经绑定
        boolean isbind = userInfoService.getUserByPhone(phone);
        if(isbind)
        {
            mr.setState(-1);
            mr.setMsg("手机号码已注册");
            return mr;
        }
        // 判断手机号今天发送的次数是否超过上限
        boolean surpass = userInfoService.getSendCeiling(weiid,Short.parseShort(VerifyCodeType.bind.toString()));
        if(surpass)
        {
            mr.setState(-1);
            mr.setMsg("验证码获取次数过多，请明日重试");
            return mr;
        }
        // 发送验证码
        boolean issend = userInfoService.insertSendSMS(weiid,phone,ip,Short.parseShort(VerifyCodeType.bind.toString()));
        if(issend)
        {
            mr.setState(1);
            mr.setMsg("发送成功");
        }
        else
        {
            mr.setState(1);
            mr.setMsg("发送验证码失败");
        }
        return mr;
    }
    
    /**
     * 发送验证码
     * @param weiid
     * @param phone
     * @param ip
     * @param verifyCodeType
     * @return
     */
    public MsgResult sendMsgToPhone(long weiid,String phone,String ip,String verifyCodeType)
    {
        MsgResult mr = new MsgResult();
        if(phone == null || phone.length() != 11 || !isMobileNO(phone))
        {
            mr.setState(-1);
            mr.setMsg("手机格式不正确");
            return mr;
        }
        // 判断手机号今天发送的次数是否超过上限
        boolean surpass = userInfoService.getSendCeiling(weiid,Short.parseShort(verifyCodeType));
        if(surpass)
        {
            mr.setState(-1);
            mr.setMsg("验证码获取次数过多，请明日重试");
            return mr;
        }
        // 发送验证码
        boolean issend = userInfoService.insertSendSMS(weiid,phone,ip,Short.parseShort(verifyCodeType));
        if(issend)
        {
            mr.setState(1);
            mr.setMsg("发送成功");
        }
        else
        {
            mr.setState(-1);
            mr.setMsg("发送验证码失败");
        }
        return mr;
    }
    
    /**
     * 验证码校验
     * @param weiid
     * @param phone
     * @param yzm
     * @param verifyCodeType
     * @return
     */
    public MsgResult checkVerifyCodeAndResetPwd(Long weiid,String phone,String yzm,String verifyCodeType,String pwd)
    {
        MsgResult mr = new MsgResult();
        // 验证数据是否合法
        if(phone == null || phone.length() != 11 || !isMobileNO(phone))
        {
            mr.setState(-1);
            mr.setMsg("手机格式不正确");
            return mr;
        }
        if(yzm == null || ObjectUtil.isEmpty(yzm))
        {
            mr.setState(-1);
            mr.setMsg("验证码不能为空");
            return mr;
        }
        // 验证并修改密码
        mr = userInfoService.resetPwdByVerify(weiid, phone, yzm, verifyCodeType, pwd);
        return mr;
    }

    /**
     * 手机号码正则验证
     * 
     * @param phone
     *            手机号码
     * @return
     */
    public boolean isMobileNO(String phone)
    {
        Pattern p = Pattern.compile("^[1][3,4,5,7,8][0-9]{9}$");
        Matcher m = p.matcher(phone);
        return m.matches();
    }

    /**
     * 邮箱正则验证
     * 
     * @param email
     *            邮箱号码
     * @return
     */
    public static boolean isEmail(String email)
    {
        String str = "^([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)*@([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)+[\\.][A-Za-z]{2,3}([\\.][A-Za-z]{2})?$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);
        return m.matches();
    }
    
    @ResponseBody
    @RequestMapping(value = "/sendVerifyCode",method =
    {RequestMethod.POST,RequestMethod.GET})
    public String sendVerifyCode(String verifyCodeType,HttpServletRequest request)
    {
        LoginUser user = getUserOrSub();
        long weiId = user.getWeiID();
        String ip = request.getRemoteAddr();
        MsgResult mr = new MsgResult();
        UWeiSeller weiSeller = userInfoService.getUWeiSeller(weiId);
        String phone = weiSeller.getMobilePhone();// 手机号
    	if (StringUtils.isNotEmpty(phone)) {
			if (VerifyCodeType.updatepassword.toString().equals(verifyCodeType)
					|| VerifyCodeType.updatePayPassword.toString().equals(verifyCodeType)) {
				mr = sendMsgToPhone(weiId, phone, ip,verifyCodeType);// 发送验证码
			}
    	}
        return JsonUtil.objectToJson(mr);
    }
    
    @ResponseBody
    @RequestMapping(value = "/resetPwd",method =
    {RequestMethod.POST,RequestMethod.GET})
    public String resetPwd(String verifyCodeType,String pwd,String yzm)
    {
        LoginUser user = getUserOrSub();
        long weiId = user.getWeiID();
        MsgResult mr = new MsgResult();
        UWeiSeller weiSeller = userInfoService.getUWeiSeller(weiId);
        String phone = weiSeller.getMobilePhone();// 手机号
    	if (StringUtils.isNotEmpty(phone)) {
			if (VerifyCodeType.updatepassword.toString().equals(verifyCodeType)
					|| VerifyCodeType.updatePayPassword.toString().equals(verifyCodeType)) {
				mr = checkVerifyCodeAndResetPwd(weiId, phone, yzm,verifyCodeType,pwd);// 验证码校验后修改密码
			}
    	}
        return JsonUtil.objectToJson(mr);
    }
}
