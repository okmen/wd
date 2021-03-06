package com.okwei.sso.web;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.okwei.bean.vo.LoginUser;
import com.okwei.service.user.ILoginService; 
import com.okwei.util.ObjectUtil;
import com.okwei.util.RedisUtil;

@Controller
@RequestMapping(value = "/")
public class HomeController
{
    @Autowired
    ILoginService loginService;

    /**
     * 获取登陆Tiket
     * 
     * @return
     */
    @RequestMapping(value = "/GetTiket.aspx")
    public String getTiketAspNet(HttpServletRequest request,HttpServletResponse response,String back)
    {
        return getTiket(request,response,back);
    }

    @RequestMapping(value = "/getTiket")
    public String getTiketJava(HttpServletRequest request,HttpServletResponse response,String back)
    {
        return getTiket(request,response,back);
    }

    public String getTiket(HttpServletRequest request,HttpServletResponse response,String back)
    {
        back = loginService.getDomainByBack(back);
        boolean ask = back.indexOf("?") > 1;
        Cookie[] cookies = request.getCookies();
        String cookie_tiket = "";
        if(cookies != null)
        {
            for(Cookie c : cookies)
            {
                if("tiket".equals(c.getName().trim()))
                {
                    cookie_tiket = c.getValue();
                    break;
                }
            }
        }
        if(!ObjectUtil.isEmpty(cookie_tiket) && !"notiket".equals(cookie_tiket))
        {
            LoginUser user = (LoginUser) RedisUtil.getObject(cookie_tiket + "_IBS");
            LoginUser sub = (LoginUser) RedisUtil.getObject(cookie_tiket + "_SUB");
            if(user == null && sub == null)
            {
                if(user == null)
                {
                    RedisUtil.delete(cookie_tiket + "_IBS");
                }
                if(sub == null)
                {
                    RedisUtil.delete(cookie_tiket + "_SUB");
                }
                cookie_tiket = "notiket";
            }
        }
        else
        {
            cookie_tiket = "notiket";
        }
        if(ask)
        {
            return "redirect:" + back + "&tiket=" + cookie_tiket;
        }
        else
        {
            return "redirect:" + back + "?tiket=" + cookie_tiket;
        }
    }

    

    @RequestMapping(value = "/ThirdPartyBind.aspx")
    public String ThirdPartyBind(String type)
    {
        return "redirect:/other/bindLogin?type=" + type;
    }
}
