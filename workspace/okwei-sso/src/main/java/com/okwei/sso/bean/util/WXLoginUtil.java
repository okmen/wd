package com.okwei.sso.bean.util;

import java.net.URLEncoder;

/**
 * 微信登陆
 * @author Administrator
 *
 */
public class WXLoginUtil
{

    /**
     * 微信登录Appid
     */
    public static final String WxLoginAppid = "wxf5852f941d9a25b9";
    /**
     * 微信登录秘钥
     */
    public static final String WxLoginSecret = "7d1ac3ebdeda4b1d31c09df091dc6d23";
    /**
     * 微信返回的URL
     */
    @SuppressWarnings("deprecation")
    public static final String WXRedirectURl= URLEncoder.encode("http://port.okwei.com/other/wxBack");
}
