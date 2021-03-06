package com.okwei.myportal.bean.dto;

public class AddChildrenAccountDTO {
	private String password;  //密码
	private String employeeName;  //姓名
	private String supplierName; //供应商名
	private String department;  //部门
	private String phone;  //联系电话
	private String accountId; //子账号id
	private Short isOrderSend;  //是否负责发货
	private Short status;
	private String linkName;  //联系人
	private Integer location;  //所在地区code(最后一级)
	private String address;  //代理商地址
	
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getEmployeeName() {
		return employeeName;
	}
	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}
	public String getSupplierName() {
		return supplierName;
	}
	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getAccountId() {
		return accountId;
	}
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	public Short getIsOrderSend() {
		return isOrderSend;
	}
	public void setIsOrderSend(Short isOrderSend) {
		this.isOrderSend = isOrderSend;
	}
	public Short getStatus() {
		return status;
	}
	public void setStatus(Short status) {
		this.status = status;
	}
	public String getLinkName() {
		return linkName;
	}
	public void setLinkName(String linkName) {
		this.linkName = linkName;
	}
	public Integer getLocation() {
		return location;
	}
	public void setLocation(Integer location) {
		this.location = location;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
	
}
