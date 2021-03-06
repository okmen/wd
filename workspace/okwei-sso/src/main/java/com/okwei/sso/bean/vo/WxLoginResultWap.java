package com.okwei.sso.bean.vo;

public class WxLoginResultWap {
	/// <summary>
    /// 微信登录用户标示
    /// </summary>
    public String openid ;

    /// <summary>
    /// 昵称
    /// </summary>
    public String nickname ;

    /// <summary>
    /// 头像
    /// </summary>
    public String headimgurl ;

    /// <summary>
    /// 微信登录相关业务 用户唯一标识
    /// </summary>
    public String unionid ;
    
    public WxLoginResultWap(){}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getHeadimgurl() {
		return headimgurl;
	}

	public void setHeadimgurl(String headimgurl) {
		this.headimgurl = headimgurl;
	}

	public String getUnionid() {
		return unionid;
	}

	public void setUnionid(String unionid) {
		this.unionid = unionid;
	}
    
}
