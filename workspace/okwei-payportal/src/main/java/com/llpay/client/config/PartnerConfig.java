package com.llpay.client.config;

import java.util.ResourceBundle;

/**
* 商户配置信息
* @author guoyx e-mail:guoyx@lianlian.com
* @date:2013-6-25 下午01:45:40
* @version :1.0
*
*/
public interface PartnerConfig{
	String YT_PUB_KEY     = ResourceBundle.getBundle("paySettings").getString("llpay.YT_PUB_KEY");
			//"MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCSS/DiwdCf/aZsxxcacDnooGph3d2JOj5GXWi+q3gznZauZjkNP8SKl3J2liP0O6rU/Y/29+IUe+GTMhMOFJuZm1htAtKiu5ekW0GlBMWxf4FPkYlQkPE0FtaoMP3gYfh+OwI+fIRrpW3ySn3mScnc6Z700nU/VYrRkfcSCbSnRwIDAQAB";
	// 商户私钥
	String TRADER_PRI_KEY =  ResourceBundle.getBundle("paySettings").getString("llpay.TRADER_PRI_KEY");
			//"";
	// MD5 KEY
	String MD5_KEY        = ResourceBundle.getBundle("paySettings").getString("llpay.MD5_KEY");
			//"201306031000001013";
	// 接收异步通知地址
	String NOTIFY_URL     = ResourceBundle.getBundle("paySettings").getString("llpay.NOTIFY_URL");
	// 支付结束后返回地址
	String URL_RETURN     = ResourceBundle.getBundle("paySettings").getString("llpay.URL_RETURN");
	// 商户编号
	String OID_PARTNER    = ResourceBundle.getBundle("paySettings").getString("llpay.OID_PARTNER");
			//"201306031000001013";
	// 签名方式 RSA或MD5
	String SIGN_TYPE      = ResourceBundle.getBundle("paySettings").getString("llpay.SIGN_TYPE");
			//"MD5";
	// 接口版本号，固定1.0
	String VERSION        = ResourceBundle.getBundle("paySettings").getString("llpay.VERSION");
	
	// 业务类型，连连支付根据商户业务为商户开设的业务类型； （101001：虚拟商品销售、109001：实物商品销售、108001：外部账户充值）
	
	String BUSI_PARTNER   = ResourceBundle.getBundle("paySettings").getString("llpay.BUSI_PARTNER");
			//"101001";
	
	//返回修改订单信息地址
	String Back_url = ResourceBundle.getBundle("paySettings").getString("llpay.Back_url");
	
	String QUERYORDER_URL= ResourceBundle.getBundle("paySettings").getString("llpay.QUERYORDER_URL");
}
