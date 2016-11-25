package com.okwei.bean.enums;

/**
 * 发布编辑产品的用户身份 按身份身份发布产品 1-普通微店主、代理商、落地店；2-工厂号、批发号供应商；3-平台号；4-品牌号；5-子账号供应商
 * @author yangjunjun
 *
 */
public enum PubProductTypeEnum 
{
	/**
	 * 普通微店主、代理商、落地店
	 */
	Ordinary(1),
	/**
	 * 工厂号、批发号供应商
	 */
	Supplyer(2),
	/**
	 * 平台号
	 */
	Platform(3),
	/**
	 * 品牌号
	 */
	Brand(4),
	/**
	 * 子账号供应商
	 */
	SubAccount(5),
	/**
	 * 分销体系供应商
	 */
	BrandSupplyer(6);
	
	private final int step; 

    private PubProductTypeEnum(int step) { 

         this.step = step; 
    }
    @Override
    public String toString()
    {
    	return String.valueOf(this.step);         	
    }
}
