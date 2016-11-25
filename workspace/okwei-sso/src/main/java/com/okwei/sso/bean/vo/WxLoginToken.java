package com.okwei.sso.bean.vo;

import java.io.Serializable;

public class WxLoginToken implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /**
     * 接口调用凭证
     */
    private String access_token;
    /**
     * 授权用户唯一标识
     */
    private String openid;
    /**
     * access_token接口调用凭证超时时间，单位（秒）
     */
    private Integer expires_in;
    /**
     * 用户刷新access_token
     */
    private String refresh_token;
    /**
     * 用户授权的作用域，使用逗号（,）分隔
     */
    private String scope;
    /**
     * 只有在用户将公众号绑定到微信开放平台帐号后，才会出现该字段。
     */
    private String unionid;

    
    public Integer getExpires_in()
    {
        return expires_in;
    }

    public void setExpires_in(Integer expires_in)
    {
        this.expires_in = expires_in;
    }

    public String getRefresh_token()
    {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token)
    {
        this.refresh_token = refresh_token;
    }

    public String getScope()
    {
        return scope;
    }

    public void setScope(String scope)
    {
        this.scope = scope;
    }

    public String getUnionid()
    {
        return unionid;
    }

    public void setUnionid(String unionid)
    {
        this.unionid = unionid;
    }

    public String getAccess_token()
    {
        return access_token;
    }

    public void setAccess_token(String access_token)
    {
        this.access_token = access_token;
    }

    public String getOpenid()
    {
        return openid;
    }

    public void setOpenid(String openid)
    {
        this.openid = openid;
    }

}