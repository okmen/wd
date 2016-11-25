package com.okwei.bean.domain;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * DBrandsInfo entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "D_BrandSupplierInfo")
public class DBrandsInfo implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2896175315340982889L;
	// Fields

	private Long weiID;
	private Integer brandId;
	private String industry;
	private String companyName;
	private String contact;
	private String contactPhone;
	private String landLine;
	private Integer province;
	private Integer city;
	private Integer district;
	private String addressDetail;
	private String main;
	private Double securityDeposit;
	private Double costs;
	private Integer payedType;
	private String companyProfile;
	private String characteristics;
	private String conditions;
	private Date createTime;
	private Double agency;
	private String bLicense;
	private String qq;

	// Constructors

	/** default constructor */
	public DBrandsInfo() {
	}

	/** minimal constructor */
	public DBrandsInfo(Integer brandId) {
		this.brandId = brandId;
	}

	/** full constructor */
	public DBrandsInfo(Long weiID, Integer brandId, String industry, String companyName, String contact, String contactPhone, String landLine, Integer province, Integer city, Integer district, String addressDetail, String main, Double securityDeposit, Double costs, Integer payedType, String companyProfile,
			String characteristics, String conditions, Date createTime) {
		this.weiID=weiID;
		this.brandId = brandId;
		this.industry = industry;
		this.companyName = companyName;
		this.contact = contact;
		this.contactPhone = contactPhone;
		this.landLine = landLine;
		this.province = province;
		this.city = city;
		this.district = district;
		this.addressDetail = addressDetail;
		this.main = main;
		this.securityDeposit = securityDeposit;
		this.costs = costs;
		this.payedType = payedType;
		this.companyProfile = companyProfile;
		this.characteristics = characteristics;
		this.conditions = conditions;
		this.createTime = createTime;
	}

	// Property accessors
	@Id
	@Column(name = "WeiID", unique = true, nullable = false)
	public Long getWeiID() {
		return this.weiID;
	}

	public void setWeiID(Long weiId) {
		this.weiID = weiId;
	}

	@Column(name = "BrandID", unique = true, nullable = false)
	public Integer getBrandId() {
		return this.brandId;
	}

	public void setBrandId(Integer brandId) {
		this.brandId = brandId;
	}

	@Column(name = "Industry", length = 128)
	public String getIndustry() {
		return this.industry;
	}

	public void setIndustry(String industry) {
		this.industry = industry;
	}

	@Column(name = "CompanyName", length = 128)
	public String getCompanyName() {
		return this.companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	@Column(name = "Contact", length = 64)
	public String getContact() {
		return this.contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	@Column(name = "ContactPhone", length = 64)
	public String getContactPhone() {
		return this.contactPhone;
	}

	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}

	@Column(name = "LandLine", length = 64)
	public String getLandLine() {
		return this.landLine;
	}

	public void setLandLine(String landLine) {
		this.landLine = landLine;
	}

	@Column(name = "Province")
	public Integer getProvince() {
		return this.province;
	}

	public void setProvince(Integer province) {
		this.province = province;
	}

	@Column(name = "City")
	public Integer getCity() {
		return this.city;
	}

	public void setCity(Integer city) {
		this.city = city;
	}

	@Column(name = "District")
	public Integer getDistrict() {
		return this.district;
	}

	public void setDistrict(Integer district) {
		this.district = district;
	}

	@Column(name = "AddressDetail", length = 128)
	public String getAddressDetail() {
		return this.addressDetail;
	}

	public void setAddressDetail(String addressDetail) {
		this.addressDetail = addressDetail;
	}

	@Column(name = "Main", length = 128)
	public String getMain() {
		return this.main;
	}

	public void setMain(String main) {
		this.main = main;
	}

	@Column(name = "SecurityDeposit", precision = 18)
	public Double getSecurityDeposit() {
		return this.securityDeposit;
	}

	public void setSecurityDeposit(Double securityDeposit) {
		this.securityDeposit = securityDeposit;
	}

	@Column(name = "Costs", precision = 18)
	public Double getCosts() {
		return this.costs;
	}

	public void setCosts(Double costs) {
		this.costs = costs;
	}

	@Column(name = "PayedType")
	public Integer getPayedType() {
		return this.payedType;
	}

	public void setPayedType(Integer payedType) {
		this.payedType = payedType;
	}

	@Column(name = "CompanyProfile", length = 65535)
	public String getCompanyProfile() {
		return this.companyProfile;
	}

	public void setCompanyProfile(String companyProfile) {
		this.companyProfile = companyProfile;
	}

	@Column(name = "Characteristics", length = 65535)
	public String getCharacteristics() {
		return this.characteristics;
	}

	public void setCharacteristics(String characteristics) {
		this.characteristics = characteristics;
	}

	@Column(name = "Conditions", length = 65535)
	public String getConditions() {
		return this.conditions;
	}

	public void setConditions(String conditions) {
		this.conditions = conditions;
	}

	@Column(name = "CreateTime", length = 0)
	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Column(name = "Agency", precision = 18)
	public Double getAgency() {
		return this.agency;
	}

	public void setAgency(Double agency) {
		this.agency = agency;
	}

	/**
	 * @return the bLicense
	 */
	@Column(name = "BLicense", length = 128)
	public String getbLicense() {
		return bLicense;
	}

	/**
	 * @param bLicense
	 *            the bLicense to set
	 */
	public void setbLicense(String bLicense) {
		this.bLicense = bLicense;
	}

	@Column(name = "QQ", length = 32)
	public String getQq() {
		return qq;
	}

	public void setQq(String qq) {
		this.qq = qq;
	}
	
	

}