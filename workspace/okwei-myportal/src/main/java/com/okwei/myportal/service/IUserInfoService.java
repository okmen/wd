package com.okwei.myportal.service;

import com.okwei.bean.domain.UShopInfo;
import com.okwei.bean.domain.UWallet;
import com.okwei.bean.domain.UWeiSeller;
import com.okwei.bean.vo.LoginUser;
import com.okwei.myportal.bean.vo.AccountVO; 
import com.okwei.myportal.bean.vo.SettingsVO;
import com.okwei.myportal.bean.vo.MsgResult;
import com.okwei.myportal.bean.vo.ShopInfoVO;
import com.okwei.myportal.bean.vo.UserInfoCountVO;

public interface IUserInfoService
{
    /**
     * 获取用户信息数量
     * 
     * @param weiid
     * @return
     */
    UserInfoCountVO getUserCounts(LoginUser user);

    /**
     * 发送手机验证码
     * 
     * @param weiid
     *            微店号
     * @param phone
     *            手机号
     * @return
     */
    MsgResult insertVerification(long weiid,String phone);

    /**
     * 判读手机是否已经注册
     * 
     * @param phone
     *            手机号
     * @return
     */
    boolean getUserByPhone(String phone);

    /**
     * 是否到业务的上限
     * 
     * @param weiid
     * @param itype
     * @return
     */
    boolean getSendCeiling(long weiid,short itype);

    /**
     * 发送短信
     * 
     * @param weiid
     *            微店号
     * @param phone
     *            手机号码
     * @param ip
     *            IP地址
     * @param itype
     *            发送类型
     * @return
     */
    boolean insertSendSMS(long weiid,String phone,String ip,short itype);

    /**
     * 获得用户商店信息
     * 
     * @param user
     * @return
     */
    ShopInfoVO getUShopInfo(LoginUser user);

    /**
     * 保存店铺资料
     * 
     * @param shopInfo
     * @return
     */
    int updateShopInfo(UShopInfo shopInfo);

    /**
     * 获取验证码
     * 
     * @param tiket
     * @return
     */
    MsgResult getPhoneVerify(Long weiid,String phone,String yzm);

    /**
     * 解除绑定手机，发送验证码
     * 
     * @param weiid
     *            微店号
     * @return
     */
    MsgResult insertunBindPhone(Long weiid,String ip);

    /**
     * 解除绑定验证
     * 
     * @param weiid
     *            微店号
     * @param yzm
     *            验证码
     * @return
     */
    MsgResult updateUnBind(Long weiid,String yzm);
	/**
	 * 获得微店的安全设置状态
	 * @param weiId
	 * @return
	 */
	SettingsVO findSettings(Long weiId);
	/**
	 * 获得用户信息
	 * @param weiId
	 * @return
	 */
	UWeiSeller getUWeiSeller(Long weiId);
	/**
	 * 修改用户信息
	 * @param weiSeller
	 * @return
	 */
	int modifyUWeiSeller(UWeiSeller weiSeller);

	/**
	 * 获得用户钱包
	 * @param weiId
	 * @return
	 */
	UWallet UWallet(Long weiId);
	/**
	 * 修改钱包信息
	 * @param wallet
	 * @return
	 */
	int modifyUWallet(UWallet wallet);
	/**
	 * 验证验证码后修改登陆密码或者支付密码
	 * @param weiid 微店号
	 * @param phone 手机号
	 * @param yzm 验证码
	 * @param verifyCodeType 找回登陆密码/支付密码
	 * @param pwd 新密码
	 * @return
	 */
	MsgResult resetPwdByVerify(Long weiid,String phone,String yzm,String verifyCodeType,String pwd);
	/**
         * 是否
         * @param weiid
         * @return
         */
        AccountVO getAccountVO(Long weiid);
        
        /**
         * 解除绑定
         * @param weiid 微店号
         * @param ltype 登陆类型
         */
        void updateUnLoginBind(Long weiid,short ltype);
}
