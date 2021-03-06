package com.okwei.pay.bean.vo;

import java.util.ResourceBundle;

import com.okwei.util.AppSettingUtil;

public class ConfigSetting {
    /**
     * 批发号 本公司抽成 0.003 如果没有 代理商则 代理商部分 归公司所有
     */
    public static double batchOkWei = Double.parseDouble(AppSettingUtil.getSingleValue("BatchOkWei"));
    /**
     * 批发号供应商总交易额 货款获得比例 0.97
     */
    public static double batchHuoKuan = Double.parseDouble(AppSettingUtil.getSingleValue("BatchHuoKuan"));
    /**
     * 批发号整体入驻供应商 签约人抽成比例
     */
    public static double batchMarketWei = Double.parseDouble(AppSettingUtil.getSingleValue("BatchMarketWei"));
    /**
     * 批发号整体入驻供应商 牵线人抽成比例
     */
    public static double batchMarketVer = Double.parseDouble(AppSettingUtil.getSingleValue("BatchMarketVer"));
    /**
     * 批发号认证点总交易额 佣金抽成比例
     */
    public static double batchVerifiPort = Double.parseDouble(AppSettingUtil.getSingleValue("BatchVerifiPort"));
    /**
     * 批发号认证员总交易额 佣金抽成比例
     */
    public static double batchVerifier = Double.parseDouble(AppSettingUtil.getSingleValue("BatchVerifier"));
    /**
     * 批发号认证员代理商总交易额 佣金抽成比例
     */
    public static double batchVerifierAgent = Double.parseDouble(AppSettingUtil.getSingleValue("BatchVerifierAgent"));
    /**
     * 批发号 供应商成为认证点 700佣金,上级300
     */
    public static double batchGys = Double.parseDouble(AppSettingUtil.getSingleValue("BatchGys"));
    public static double batchUpGys = Double.parseDouble(AppSettingUtil.getSingleValue("BatchUpGys"));
    /**
     * 微店主 70%的佣金 0.7m;
     */
    public static double yunSeller = Double.parseDouble(AppSettingUtil.getSingleValue("YunSeller"));
    /**
     * 介绍人 30%的佣金 0.3
     */
    public static double yunUpSeller = Double.parseDouble(AppSettingUtil.getSingleValue("YunUpSeller"));
    /**
     * 系统设置的佣金比例 0.75
     */
    public static double yunSystem = Double.parseDouble(AppSettingUtil.getSingleValue("YunSystem"));
    /**
     * 批发号供应商保证金
     */
    public static double batchSupplyerBond = Double.parseDouble(AppSettingUtil.getSingleValue("BatchSupplyerBond"));
    /**
     * 批发号供应商服务费
     */
    public static double batchSupplyerServiceFee = Double.parseDouble(AppSettingUtil.getSingleValue("BatchSupplyerServiceFee"));
    /**
     * 升级认证点服务费
     */
    public static double batchPortServerFee = Double.parseDouble(AppSettingUtil.getSingleValue("BatchPortServerFee"));
    /**
     * 批发号供应商保证金 并 升级认证点 套餐
     */
    public static double gysAndVerifierServerFee = Double.parseDouble(AppSettingUtil.getSingleValue("GysAndVerifierServerFee"));
    /**
     * 消息推送接口地址
     */
    public static String pushMessageAddress = AppSettingUtil.getSingleValue("PushMessageAddress");
    /**
     * 工厂号认证员 保证金
     */
    public static double yunVerifierBond = Double.parseDouble(AppSettingUtil.getSingleValue("YunVerifierBond"));
    /**
     * 工厂号 保证金
     */
    public static double yunSupplyerBond = Double.parseDouble(AppSettingUtil.getSingleValue("YunSupplyerBond"));
    /**
     * 3年的工厂号 来源佣金
     */
    public static double yunSupplyerThreeYearSocure = Double.parseDouble(AppSettingUtil.getSingleValue("YunSupplyerThreeYearSocure"));
    /**
     * 3年工厂号 跟进佣金
     */
    public static double yunSupplyerThreeYearFollow = Double.parseDouble(AppSettingUtil.getSingleValue("YunSupplyerThreeYearFollow"));
    /**
     * 3年工厂号 服务费
     */
    public static double yunSupplyerThreeYearServiceFree = Double.parseDouble(AppSettingUtil.getSingleValue("YunSupplyerThreeYearServiceFree"));
    /**
     * 1年工厂号 来源佣金
     */
    public static double yunSupplyerOneYearSocure = Double.parseDouble(AppSettingUtil.getSingleValue("YunSupplyerOneYearSocure"));
    /**
     * 1年工厂号 跟进佣金
     */
    public static double yunSupplyerOneYearFollow = Double.parseDouble(AppSettingUtil.getSingleValue("YunSupplyerOneYearFollow"));
    /**
     * 1年工厂 服务费
     */
    public static double yunSupplyerOneYearServiceFree = Double.parseDouble(AppSettingUtil.getSingleValue("YunSupplyerOneYearServiceFree"));
}
