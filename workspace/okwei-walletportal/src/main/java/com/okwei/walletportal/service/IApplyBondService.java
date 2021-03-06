package com.okwei.walletportal.service;

import java.util.List;

import com.okwei.service.IBaseService;
import com.okwei.walletportal.bean.vo.ApplyBondInfo;
import com.okwei.walletportal.bean.vo.BankCardVO;
import com.okwei.walletportal.bean.vo.BaseResultVO;
import com.okwei.walletportal.bean.vo.VerifierInfo;

public interface IApplyBondService extends IBaseService {
    /**
     * 验证是否能够提取保证金
     * 
     * @param type
     *            保证金类型
     * @param weiid
     *            微店号
     * @return
     */
    public BaseResultVO getVerificationBond(int type, long weiid);

    /**
     * 获取申请保证金的信息
     * 
     * @param type
     * @param weiid
     * @return
     */
    public ApplyBondInfo getApplyBondInfo(int type, long weiid);

    /**
     * 获取银行卡信息（含对公账号）
     * 
     * @param bankNo
     * @param weiid
     * @return
     */
    public List<BankCardVO> getBankList(String bankNo, long weiid);

    /**
     * 保存修改提交申请
     * 
     * @param tid
     * @param cardid
     * @param ispub
     * @param imageback
     * @param imagefront
     * @return
     */
    public BaseResultVO saveApplyBond(long weiid, int type, int tid, int cardid, int ispub, String imageback, String imagefront);

    /**
     * 取消申请
     * 
     * @param weiid
     * @param type
     * @param tid
     * @return
     */
    public int updateCancelApply(long weiid, int type, int tid);

    /**
     * 获取认证员 顾问信息
     * 
     * @param weiid
     * @return
     */
    public VerifierInfo getVerifierInfo(long weiid);

    /**
     * 获取供应商的认证员
     * @param weiid
     * @param type
     * @return
     */
    public long getVerifierWeiid(long weiid, int type);
}
