package com.okwei.bean.vo.shoppingcart;
import java.util.List;
public class Style {
	//款式id
	private long styleId;
	//个数
	private int count;
	//价格
	private double price;
	//属性
	private String property;
	//图片
	private String image;
	//标题
	private String proTitle;
	//是否启用
	private short status;
	//主键
	private long scid;
	//成交微店号
	private long tradeWeiId;
	
	//是否参加活动
	private Short isActivity;
	//活动价
	private Double activityPrice;
	
	private Double displayPrice;
	
	
	/**
	 * 分享制作人
	 */
	private Long sharePageProducer;
	/**
	 * 分享人
	 */
	private Long shareOne;
	/**
	 * 分享Id
	 */
	private Long sharePageId;
	
	public Short getIsActivity() {
		return isActivity;
	}
	public void setIsActivity(Short isActivity) {
		this.isActivity = isActivity;
	}
	public Double getActivityPrice() {
		return activityPrice;
	}
	public void setActivityPrice(Double activityPrice) {
		this.activityPrice = activityPrice;
	}
	public Long getSharePageId() {
		return sharePageId;
	}
	public void setSharePageId(Long sharePageId) {
		this.sharePageId = sharePageId;
	}
	public Double getDisplayPrice() {
		return displayPrice;
	}
	public void setDisplayPrice(Double displayPrice) {
		this.displayPrice = displayPrice;
	}
	public long getSharePageProducer() {
		return sharePageProducer;
	}
	public void setSharePageProducer(Long sharePageProducer) {
		this.sharePageProducer = sharePageProducer;
	}
	public long getShareOne() {
		return shareOne;
	}
	public void setShareOne(Long shareOne) {
		this.shareOne = shareOne;
	}
	//属性值
	private List<PProductStyleKVVO> pProductStyleKVVOList;
	//区
	private short source;
	public long getStyleId() {
		return styleId;
	}
	public void setStyleId(long styleId) {
		this.styleId = styleId;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public String getProperty() {
		return property;
	}
	public void setProperty(String property) {
		this.property = property;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	 
	public long getTradeWeiId() {
		return tradeWeiId;
	}
	public void setTradeWeiId(long tradeWeiId) {
		this.tradeWeiId = tradeWeiId;
	}
	public String getProTitle() {
		return proTitle;
	}
	public void setProTitle(String proTitle) {
		this.proTitle = proTitle;
	}
	public short getStatus() {
		return status;
	}
	public void setStatus(short status) {
		this.status = status;
	}
	public long getScid() {
		return scid;
	}
	public void setScid(long scid) {
		this.scid = scid;
	}
	public List<PProductStyleKVVO> getpProductStyleKVVOList() {
		return pProductStyleKVVOList;
	}
	public void setpProductStyleKVVOList(List<PProductStyleKVVO> pProductStyleKVVOList) {
		this.pProductStyleKVVOList = pProductStyleKVVOList;
	}
	public short getSource() {
		return source;
	}
	public void setSource(short source) {
		this.source = source;
	}

}
