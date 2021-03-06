package com.okwei.myportal.bean.dto;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

/**
 * 
 * @ClassName: RelationDTO
 * @Description: 上下游管理页面DTO
 * @author xiehz
 * @date 2015年7月14日 上午10:20:38
 *
 */
public class RelationDTO implements Serializable {

	/**
	 * @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么)
	 */
	private static final long serialVersionUID = 1L;

	public enum Type {
		/** 全部 */
		all((short) 0),

		/** 直接分销商 */
		distributor((short) 1),

		/** 普通关注 */
		attention((short) 2);

		short value;

		Type(short value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return String.valueOf(this.value);
		}
	}

	private Long userId;
	private Long weiId;
	private String weiName;
	private Integer province;
	private Integer city;
	private Integer district;

	// 批发市场
	private Integer bmId;
	private String bmName;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date fromTime;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date toTime;
	private Type type;

	// 是否仅仅普通微店主身份: 1是，0否
	private Integer onlyWeiSeller;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getWeiId() {
		return weiId;
	}

	public void setWeiId(Long weiId) {
		this.weiId = weiId;
	}

	public String getWeiName() {
		return weiName;
	}

	public void setWeiName(String weiName) {
		this.weiName = weiName;
	}

	public Integer getProvince() {
		return province;
	}

	public void setProvince(Integer province) {
		this.province = province;
	}

	public Integer getCity() {
		return city;
	}

	public void setCity(Integer city) {
		this.city = city;
	}

	public Integer getDistrict() {
		return district;
	}

	public void setDistrict(Integer district) {
		this.district = district;
	}

	public Integer getBmId() {
		return bmId;
	}

	public void setBmId(Integer bmId) {
		this.bmId = bmId;
	}

	public Date getFromTime() {
		return fromTime;
	}

	public void setFromTime(Date fromTime) {
		this.fromTime = fromTime;
	}

	public Date getToTime() {
		return toTime;
	}

	public void setToTime(Date toTime) {
		this.toTime = toTime;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getBmName() {
		return bmName;
	}

	public void setBmName(String bmName) {
		this.bmName = bmName;
	}

	public Integer getOnlyWeiSeller() {
		return onlyWeiSeller;
	}

	public void setOnlyWeiSeller(Integer onlyWeiSeller) {
		this.onlyWeiSeller = onlyWeiSeller;
	}

}
