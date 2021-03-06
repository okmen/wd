package com.okwei.myportal.dao;

import java.util.List;

import com.okwei.bean.domain.TBatchMarket;
import com.okwei.bean.domain.UBatchSupplyer;
import com.okwei.bean.domain.UOtherLogin;
import com.okwei.bean.domain.UShopInfo;
import com.okwei.bean.domain.USupplyer;
import com.okwei.bean.domain.UWallet;
import com.okwei.bean.domain.UWeiSeller;

public interface IUserInfoDAO {
	/**
	 * 获取订单数量
	 * @param weiid
	 * @return
	 */
    List<Object[]> getOrderCounts(Long weiid);
    /**
     * 获取今天的订单数量
     * @param weiid
     * @return
     */
    Integer getTodayOrderCount(Long weiid);
    /**
     * 获取产品数量
     * @param weiid
     * @return
     */
    List<Object[]> getProductCounts(Long weiid);
    /**
     * 获取昨天的分销商数
     * @param weiid
     * @return
     */
    Integer getYeserDayCount(Long weiid);
    /**
     * 获取分销商总数
     * @param weiid
     * @return
     */
    Integer getTotalCount(Long weiid); 
    /**
     * 获取展示信息
     * @param weiid
     * @return
     */
    UShopInfo getUserInfo(Long weiid);
    /**
     * 获取批发号供应商信息
     * @param weiid
     * @return
     */
    UBatchSupplyer getBatchSupplyerInfo(Long weiid);
    /**
     * 获取用户信息
     * @param weiid
     * @return
     */
    UWeiSeller getWeiSellerInfo(Long weiid);
    /**
     * 获取用户的钱包信息
     * @param weiid
     * @return
     */
    UWallet getWallet(Long weiid);
    /**
     * 获取第三方登录信息
     * @param weiid
     * @return
     */
    List<UOtherLogin> getOtherLogin(Long weiid);
    /**
     * 插入店铺信息
     * @param shopinfo
     */
    void saveShopInfo(UShopInfo shopinfo);

    /**
     * 判断手机号是否绑定
     * 
     * @param phone
     *            手机号码
     * @return
     */
    boolean getUserPhoneisBind(String phone);

    /**
     * 判断发送短信是否到达上限
     * 
     * @param weiid
     *            微店号
     * @param phone
     *            手机号
     * @param itype
     *            发送短信的类型
     * @return
     */
    boolean getPhoneSendSMS(long weiid,short itype);

    /**
     * 发送短信添加表
     * 
     * @param code
     *            验证码
     * @param weiid
     *            微店号
     * @param itype
     *            验证类型
     * @param phone
     *            手机号码
     * @param ip
     *            来源IP
     * @return
     */
    boolean insertTVerificationID(String code,long weiid,short itype,String phone,String ip);

    /**
     * 获取供应商信息
     * @param weiid
     * @return
     */
    USupplyer getSupplyerInfo(Long weiid);
    /**
     * 获取批发市场信息
     * @param bmid
     * @return
     */
    TBatchMarket getTBatchMarket(int bmid);
    /**
     * 更新店铺信息
     * @param shopinfo
     */
    void updateShopInfo(UShopInfo shopinfo);
    
    /**
     * 修改用户表
     * 
     * @param user
     */
    void updateUWeiSeller(UWeiSeller user);

    /**
     * 修改发送验证表
     */
    void updateTVerificationId(Long weiid,String code);
    /**
     * 修改钱包
     * @param wallet
     */
    void updateUWallet(UWallet wallet);
    /**
     * 根据微店号获取第三方登陆信息
     * @param weiid
     * @return
     */
    List<UOtherLogin> getuolListByWeiId(Long weiid);
    
    /**
     * 解除绑定
     * @param weiid 微店号
     * @param ltype 第三方类型
     */
    void updateUOtherLogin(Long weiid,short ltype);
    
    /**
     * 获取上游供应的数量
     * @param weiID
     * @return
     */
    int getAttentionCount(long weiID);
    
    /**
     * 获取下游分销的数量
     * @param weiID
     * @return
     */
    int getAtteneionedCount(long weiID);
    
    /**
     * 草稿箱数量
     * @param weiID
     * @return
     */
    int getProductCount(long weiID,short state);
    
    /**
     * 统计供应商 待付款 待发货 待收货 售后中
     * @param weiID
     * @return
     */
    List<Object[]> getSupplyOrderCount(long weiID);
}
