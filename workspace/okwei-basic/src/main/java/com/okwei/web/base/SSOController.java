package com.okwei.web.base;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.okwei.bean.domain.UUserAssist;
import com.okwei.bean.domain.UWeiSeller;
import com.okwei.bean.vo.LoginUser;
import com.okwei.bean.vo.ReturnModel;
import com.okwei.bean.vo.ReturnStatus;
import com.okwei.common.EnumSerializer;
import com.okwei.dao.user.ILoginDAO;
import com.okwei.util.ImgDomain;
import com.okwei.util.ObjectUtil;
import com.okwei.util.ParseHelper;
import com.okwei.util.RedisUtil;
import com.sdicons.json.mapper.MapperException;

/**
 * 单点登录获取
 */
public class SSOController
{
    private static Gson gson = null;
    static
    {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(ReturnStatus.class,new EnumSerializer());
        gson = gsonBuilder.registerTypeAdapter(Date.class,new JsonSerializer<Date>()
        {
            public JsonElement serialize(Date src,Type typeOfSrc,JsonSerializationContext context)
            {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                return new JsonPrimitive(format.format(src));
            }
        }).setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        gson = gsonBuilder.create();
    }

    private final Logger LOG = Logger.getLogger(this.getClass());
    /**
     * 请求信息
     */
    @Autowired
    HttpServletRequest request;
    @Autowired
    HttpServletResponse response;
    /**
     * 微店用户
     */
    private LoginUser user;
    /**
     * 子账号用户
     */
    private LoginUser subuser;

    /**
     * 直接获取tiekt，没有返回空
     * 
     * @return
     */
    public String getTiket()
    {
        String tiket = request.getParameter("tiket");
        // 判断tiekt是否为空
        if(ObjectUtil.isEmpty(tiket))
        {
            tiket = getCookieByName(request,"tiket");// 获取cookie的tiket的值
            return tiket;
        }
        else{
            return tiket;
        }
    }

    /**
     * 微店用户
     */
    public LoginUser getUser()
    {
        String tiket = request.getParameter("tiket");
        // 判断tiekt是否为空
        if(ObjectUtil.isEmpty(tiket))
        {
            tiket = getCookieByName(request,"tiket");// 获取cookie的tiket的值
            if(ObjectUtil.isEmpty(tiket))
            {
                return null;
            }
        }
        Object userObject = RedisUtil.getObject(tiket + "_IBS");
        if(userObject != null)// 如果存在
        {
            RedisUtil.setExpire(tiket + "_IBS",1800);// 延长时间
            user = (LoginUser) userObject;
            return user;
        }
        return null;// 用户过期
    }
    
    

    @Autowired ILoginDAO loginDAO;
    /**
     * 兼容以前，返回实例化后的LoginUser,不会因为对象为空报错
     * 
     * @return
     */
    public LoginUser getLoginUser()
    {
        LoginUser user = getUser();
        if(user != null)
        {
            return user;
        }
        user = new LoginUser();
        //TODO
        long weiid=ParseHelper.toLong(request.getParameter("weiid"));
        if(weiid>0){
        	 UWeiSeller seller=loginDAO.getUWeiSeller(weiid);
        	 UUserAssist assist=loginDAO.getUUserAssist(weiid);
             user.setWeiID(weiid);
             user.setIdentity(assist.getIdentity()==null?0:assist.getIdentity());//
             user.setPthSub((short) 0);
             user.setPthSupply((short) 0);
             user.setWeiName(seller.getWeiName());
             user.setWeiType((short) 0);
             user.setWeiImg(ImgDomain.GetFullImgUrl(seller.getImages())); 
             
             String tiket=UUID.randomUUID().toString();
             RedisUtil.setObject(tiket+"_IBS",user,3600);
             RedisUtil.setObject(tiket,user,3600);
             addCookie(response,"tiket",tiket,3600);
             
        }else {
        	user.setWeiID(1007781L);
            user.setIdentity(1);//
            user.setPthSub((short) 0);
            user.setPthSupply((short) 0);
            user.setWeiName("测试");
            user.setWeiType((short) 0);
            user.setWeiImg("");
		}
        return user;
    }

    public static void addCookie(HttpServletResponse response,String name,String value,int maxAge){
        Cookie cookie = new Cookie(name,value);
        cookie.setPath("/");
        if(maxAge>0)  cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }
    
    /**
     * 子账号用户
     */
    public LoginUser getSubuser()
    {
        String tiket = request.getParameter("tiket");
        // 判断tiekt是否为空
        if(ObjectUtil.isEmpty(tiket))
        {
            tiket = getCookieByName(request,"tiket");// 获取cookie的tiket的值
            if(ObjectUtil.isEmpty(tiket))
            {
                return null;
            }
        }
        Object userObject = RedisUtil.getObject(tiket + "_SUB");
        if(userObject != null)// 如果存在
        {
            RedisUtil.setExpire(tiket + "_SUB",1800);// 延长时间
            subuser = (LoginUser) userObject;
            return subuser;
        }
        return null;// 用户过期
    }

    public LoginUser getUserOrSub()
    {
        user = getUser();
        if(user != null)
        {
            return user;
        }
        else
        {
            user = getSubuser();
            if(user != null)
            {
                return user;
            }
            else
            {
                return getLoginUser();
            }
        }
    }

    /**
     * 登陆错误
     * 
     * @return
     */
    public String loginError()
    {
        ReturnModel rm = new ReturnModel();
        rm.setStatu(ReturnStatus.LoginError);
        rm.setStatusreson("登陆过期");
        return JsonStr(rm);
    }

    /**
     * 返回ReturnModel的Json字符串
     * 
     * @param rm
     *            返回实体
     * @return
     * @throws MapperException
     */
    public String JsonStr(ReturnModel rm)
    {
        if(rm == null)
        {
            rm = new ReturnModel();
            rm.setStatu(ReturnStatus.DataError);
            rm.setStatusreson("返回为空");
        }
        return gson.toJson(rm);
    }

    /**
     * 获取指定cookie值
     * 
     * @param request
     *            请求实体
     * @param name
     *            cookie的键
     * @return cookie的值
     */
    private String getCookieByName(HttpServletRequest request,String name)
    {
        Cookie[] cookies = request.getCookies();
        if(ObjectUtil.isEmpty(name))
        {
            return null;
        }
        if(cookies != null)
        {
            for(Cookie cookie : cookies)
            {
                if(name.equals(cookie.getName().trim()))
                {
                    return cookie.getValue();
                }
            }
            return null;
        }
        else
        {
            return null;
        }
    }

}
