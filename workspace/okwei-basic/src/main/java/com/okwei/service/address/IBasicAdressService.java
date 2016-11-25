package com.okwei.service.address;

import java.util.List;

import com.okwei.bean.domain.UCustomerAddr;
import com.okwei.bean.vo.address.AddressVO;
import com.okwei.bean.vo.order.BAddressVO;

public interface IBasicAdressService {
    /**
     * 获取个人收货地址
     * 
     * @param weiid
     * @return
     */
    public List<AddressVO> getAddressList(Long weiid);
    /**
     * 测试接口
     * @author fuhao
     * @param weiid
     * @return
     */
    public List<BAddressVO> getAddressList1(Long weiid);
    /**
     *  获取个人收货地址
     * @param weiid
     * @return
     */
    public List<BAddressVO> getBAddressList(Long weiid);
    /**
     * 添加或者修改收货地址
     * 
     * @param model
     * @return
     */
    public int saveOrUpdateAdd(UCustomerAddr model);

    /**
     * 删除收货地址
     * 
     * @param caddrID
     * @return
     */
    public int deleteAddress(long weiid, int caddrID);

    /**
     * 设为默认收货地址
     * 
     * @param weiid
     * @param caddrID
     * @return
     */
    public int setDefault(long weiid, int caddrID);
    /**
     * 设为默认落地店地址
     * @param weiid
     * @param caddrID
     * @return
     */
    public int setShopAddress(long weiid, int caddrID);
    /**
     * 取消所有默认
     * @param weiid
     * @return
     */
    public void cancelAllDefault(long weiid);
    
	/**得到weiid及addressID得 的地址
	 * @param weiid
	 * @return
	 */
	public BAddressVO getCustomerAddressById(Long weiid,String addressId);
	
	/**根据weiId得到他默认的地址
	 * @param weiId
	 * @return
	 */
	BAddressVO getCustomerDefaultAddress(Long weiId);
    
    
}
