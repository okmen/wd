package com.okwei.company.bean.vo;

import java.io.Serializable;

public class BusKeyValue implements Serializable{
	
	private static final long serialVersionUID = 1L;
     private Integer typeid;
     private String typename;
     private String cssclass;
	public Integer getTypeid() {
		return typeid;
	}
	public void setTypeid(Integer typeid) {
		this.typeid = typeid;
	}
	public String getTypename() {
		return typename;
	}
	public void setTypename(String typename) {
		this.typename = typename;
	}
	public String getCssclass() {
		return cssclass;
	}
	public void setCssclass(String cssclass) {
		this.cssclass = cssclass;
	}
     
}
