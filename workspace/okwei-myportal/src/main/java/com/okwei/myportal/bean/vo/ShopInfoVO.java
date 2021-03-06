package com.okwei.myportal.bean.vo;

import com.okwei.bean.domain.UShopInfo;

public class ShopInfoVO extends UShopInfo
{

    /** 地区-省代码 */
    private int province;
    /** 地区-市代码 */
    private int city;
    /** 地区-区代码 */
    private int district;
    /** 地区 */
    private String addrArea;
    /** 市场名称 */
    private String marketName;
    /** 店铺位置 */
    private String shopPosition;
    /** 是否需要显示批发市场信息 0-不显示1-显示 */
    private int showBatchSupplyer;

    public int getProvince()
    {
        return province;
    }

    public void setProvince(int province)
    {
        this.province = province;
    }

    public int getCity()
    {
        return city;
    }

    public void setCity(int city)
    {
        this.city = city;
    }

    public int getDistrict()
    {
        return district;
    }

    public void setDistrict(int district)
    {
        this.district = district;
    }

    public String getAddrArea()
    {
        return addrArea;
    }

    public void setAddrArea(String addrArea)
    {
        this.addrArea = addrArea;
    }

    public String getMarketName()
    {
        return marketName;
    }

    public void setMarketName(String marketName)
    {
        this.marketName = marketName;
    }

    public int getShowBatchSupplyer()
    {
        return showBatchSupplyer;
    }

    public void setShowBatchSupplyer(int showBatchSupplyer)
    {
        this.showBatchSupplyer = showBatchSupplyer;
    }

    public String getShopPosition()
    {
        return shopPosition;
    }

    public void setShopPosition(String shopPosition)
    {
        this.shopPosition = shopPosition;
    }

}
