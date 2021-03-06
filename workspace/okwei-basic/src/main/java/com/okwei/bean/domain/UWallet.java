package com.okwei.bean.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * UWallet entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "U_Wallet")
public class UWallet extends BaseEntity {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long weiId;
	private Double balance;
	private Double accountNot;
	private Double accountIng;
	private String realName;
	private String idCard;
	private String idpositivePic;
	private String idbackPic;
	private String payPassword;
	private String questionOne;
	private String answerOne;
	private String questionTow;
	private String answerTow;
	private String questionThree;
	private String answerThree;
	private Date createTime;
	private Short status;
	private Double totalCoin;
	
	private Double weiDianCoin;
	
	private Double ableTicket;
	private Double unAbleTicket;
	private Double limitTicket;

	// Constructors

	/** default constructor */
	public UWallet() {
	}

	/** full constructor */
	public UWallet(Double balance, Double accountNot, Double accountIng,
			String realName, String idCard, String payPassword,
			String questionOne, String answerOne, String questionTow,
			String answerTow, String questionThree, String answerThree,
			Date createTime,Double totalCoin) {
		this.balance = balance;
		this.accountNot = accountNot;
		this.accountIng = accountIng;
		this.realName = realName;
		this.idCard = idCard;
		this.payPassword = payPassword;
		this.questionOne = questionOne;
		this.answerOne = answerOne;
		this.questionTow = questionTow;
		this.answerTow = answerTow;
		this.questionThree = questionThree;
		this.answerThree = answerThree;
		this.createTime = createTime;
		this.totalCoin=totalCoin;
	}

	// Property accessors
	@Id
	@Column(name = "WeiID", unique = true, nullable = false)
	public Long getWeiId() {
		return this.weiId;
	}

	public void setWeiId(Long weiId) {
		this.weiId = weiId;
	}

	@Column(name = "Balance", precision = 18)
	public Double getBalance() {
		if(this.balance==null)
			return 0d;
		return this.balance;
	}

	public void setBalance(Double balance) {
		this.balance = balance;
	}

	@Column(name = "AccountNot", precision = 18)
	public Double getAccountNot() {
		if(this.accountNot==null)
			return 0d;
		return this.accountNot;
	}

	public void setAccountNot(Double accountNot) {
		this.accountNot = accountNot;
	}

	@Column(name = "AccountIng", precision = 18)
	public Double getAccountIng() {
		if(this.accountIng==null)
			return 0d;
		return this.accountIng;
	}

	public void setAccountIng(Double accountIng) {
		this.accountIng = accountIng;
	}

	@Column(name = "RealName", length = 32)
	public String getRealName() {
		return this.realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	@Column(name = "IdCard")
	public String getIdCard() {
		return this.idCard;
	}

	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}

	@Column(name = "PayPassword", length = 128)
	public String getPayPassword() {
		return this.payPassword;
	}

	public void setPayPassword(String payPassword) {
		this.payPassword = payPassword;
	}

	@Column(name = "QuestionOne", length = 128)
	public String getQuestionOne() {
		return this.questionOne;
	}

	public void setQuestionOne(String questionOne) {
		this.questionOne = questionOne;
	}

	@Column(name = "AnswerOne", length = 128)
	public String getAnswerOne() {
		return this.answerOne;
	}

	public void setAnswerOne(String answerOne) {
		this.answerOne = answerOne;
	}

	@Column(name = "QuestionTow", length = 128)
	public String getQuestionTow() {
		return this.questionTow;
	}

	public void setQuestionTow(String questionTow) {
		this.questionTow = questionTow;
	}

	@Column(name = "AnswerTow", length = 128)
	public String getAnswerTow() {
		return this.answerTow;
	}

	public void setAnswerTow(String answerTow) {
		this.answerTow = answerTow;
	}

	@Column(name = "QuestionThree", length = 128)
	public String getQuestionThree() {
		return this.questionThree;
	}

	public void setQuestionThree(String questionThree) {
		this.questionThree = questionThree;
	}

	@Column(name = "AnswerThree", length = 128)
	public String getAnswerThree() {
		return this.answerThree;
	}

	public void setAnswerThree(String answerThree) {
		this.answerThree = answerThree;
	}

	@Column(name = "CreateTime", length = 0)
	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
    @Column(name="IDBackPic")
	public String getIdbackPic() {
		return idbackPic;
	}

	public void setIdbackPic(String idbackPic) {
		this.idbackPic = idbackPic;
	}
    @Column(name="IDPositivePic")
	public String getIdpositivePic() {
		return idpositivePic;
	}

	public void setIdpositivePic(String idpositivePic) {
		this.idpositivePic = idpositivePic;
	}
	@Column(name = "Status")
	public Short getStatus() {
		return this.status;
	}

	public void setStatus(Short status) {
		this.status = status;
	}
	
	@Column(name = "TotalCoin", precision = 18)
	public Double getTotalCoin() {
//		if(this.totalCoin==null)
//			return 0d;
		return this.totalCoin;
	}

	public void setTotalCoin(Double totalCoin) {
		this.totalCoin = totalCoin;
	}
	
	@Column(name = "LimitTicket", precision = 18)
	public Double getLimitTicket() {
		return this.limitTicket;
	}

	public void setLimitTicket(Double limit) {
		this.limitTicket = limit;
	}

	@Column(name = "WeiDianCoin", precision = 18)
	public Double getWeiDianCoin() {
		return weiDianCoin;
	}

	public void setWeiDianCoin(Double weiDianCoin) {
		this.weiDianCoin = weiDianCoin;
	}

	@Column(name = "AbleTicket", precision = 18)
	public Double getAbleTicket() {
		return ableTicket;
	}

	public void setAbleTicket(Double ableTicket) {
		this.ableTicket = ableTicket;
	}

	@Column(name = "UnAbleTicket", precision = 18)
	public Double getUnAbleTicket() {
		return unAbleTicket;
	}

	public void setUnAbleTicket(Double unAbleTicket) {
		this.unAbleTicket = unAbleTicket;
	}

	
}