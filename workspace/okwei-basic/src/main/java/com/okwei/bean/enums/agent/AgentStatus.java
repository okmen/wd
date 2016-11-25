package com.okwei.bean.enums.agent;
/**
 * 品牌代理 状态
 * @author Administrator
 *
 */
public enum AgentStatus {

	/**
     * 申请中
     */
	Applying(0),
	/**
	 * 申请不通过
	 */
	Fail(2),
	/**
     * 申请通过
     */
    Pass(3),
    /**
     * 成功（已缴费/已成功成为代理）
     */
    Ok(1);
    private final int Type;

    private AgentStatus(int step)
    {
        this.Type = step;
    }

    public String toString()
    {
        return String.valueOf(this.Type);
    }
}
