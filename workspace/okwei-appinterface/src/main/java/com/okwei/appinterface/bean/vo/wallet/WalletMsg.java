package com.okwei.appinterface.bean.vo.wallet;

import java.util.List;

public class WalletMsg {

	//是否是新的月份
	private int isNew;
	//月份名称 （如 4月）
	private String monthName;
	//年份名称 （如 2014）
	private String yearName;
	//时间 （如 2015-10-11）
	private String dateStr;
	//收支列表
	private List<WalletVO> list;
	
	public String getYearName() {
		return yearName;
	}
	public void setYearName(String yearName) {
		this.yearName = yearName;
	}
	public int getIsNew() {
		return isNew;
	}
	public void setIsNew(int isNew) {
		this.isNew = isNew;
	}
	public String getMonthName() {
		return monthName;
	}
	public void setMonthName(String monthName) {
		this.monthName = monthName;
	}
	
	public String getDateStr() {
		return dateStr;
	}
	public void setDateStr(String dateStr) {
		this.dateStr = dateStr;
	}
	public List<WalletVO> getList() {
		return list;
	}
	public void setList(List<WalletVO> list) {
		this.list = list;
	}
	 
	
}
