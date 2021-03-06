package com.okwei.walletportal.bean.enums;

public enum PublicBanksStatusEnum {
    /// 取消了
    Cancel(-1),
    /// 申请中
    Applying(0),
    /// 审核通过
    Pass(1),
    /// 审核不通过
    Fail(2);
	private final int step; 
    private PublicBanksStatusEnum(int step) { 

         this.step = step; 
    }
    @Override
    public String toString()
    {
    	return String.valueOf(this.step);         	
    }
}
