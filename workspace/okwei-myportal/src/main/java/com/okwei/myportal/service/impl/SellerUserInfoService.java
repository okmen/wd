package com.okwei.myportal.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.okwei.bean.domain.UCustomerAddr;
import com.okwei.myportal.bean.vo.AddressVO;
import com.okwei.myportal.dao.ISellerUserInfoDAO;
import com.okwei.myportal.service.ISellerUserInfoService;
import com.okwei.service.IRegionService; 

@Service
public class SellerUserInfoService implements ISellerUserInfoService
{

    @Autowired
    private ISellerUserInfoDAO sellerUserInfoDAO;

    @Autowired
    private IRegionService regionService;

    @Override
    public List<AddressVO> getAddressList(Long weiid) {
        List<UCustomerAddr> list = sellerUserInfoDAO.getAddressList(weiid);
        List<AddressVO> result = new ArrayList<AddressVO>();
        if (list != null && list.size() > 0) {
            for (UCustomerAddr addr : list) {
                AddressVO temp = new AddressVO();
                temp.setCaddrId(addr.getCaddrId());
                temp.setReceiverName(addr.getReceiverName());
                temp.setProvince(addr.getProvince());
                temp.setCity(addr.getCity());
                temp.setDistrict(addr.getDistrict());
                temp.setDetailAddr(addr.getDetailAddr());
                temp.setMobilePhone(addr.getMobilePhone());
                temp.setQq(addr.getQq());
                temp.setIsDefault(addr.getIsDefault());
                // TODO 获取详细地址 基类缓存还没有配置好
                String address = "";
                int province = addr.getProvince() == null ? 0 : addr.getProvince();
                if (province > 0) {
                    address += regionService.getNameByCode(province);
                }
                int city = addr.getCity() == null ? 0 : addr.getCity();
                if (city > 0) {
                    address += regionService.getNameByCode(city);
                }
                int street = addr.getDistrict() == null ? 0 : addr.getDistrict();
                if (street > 0) {
                    address += regionService.getNameByCode(street);
                }
                temp.setAddress(address);
                result.add(temp);
            }
        }
        return result;
    }

    @Override
    @Transactional
    public int saveOrUpdateAdd(UCustomerAddr model) {
        // 如果修改的时候设为默认,修改其他的地址不为默认
        if (model.getIsDefault() != null && model.getIsDefault().shortValue() == 1) {
            if(model.getCaddrId()==null||model.getCaddrId()<=0) {
                sellerUserInfoDAO.cancelDefault(model.getWeiId(),null);
            }
            else {
                sellerUserInfoDAO.cancelDefault(model.getWeiId(),model.getCaddrId());
            }
        }
        if (model.getCaddrId() != null && model.getCaddrId().intValue() > 0) {
            // 修改
//            UCustomerAddr entity = sellerUserInfoDAO.getUCustomerAddr(model.getCaddrId());
//            if (entity != null) {
//                if (!model.getWeiId().equals(entity.getWeiId())) {
//                    return 0;
//                }
//                entity.setReceiverName(model.getReceiverName());
//                entity.setProvince(model.getProvince());
//                entity.setCity(model.getCity());
//                entity.setDistrict(model.getDistrict());
//                entity.setDetailAddr(model.getDetailAddr());
//                entity.setMobilePhone(model.getMobilePhone());
//                entity.setQq(model.getQq());
//                entity.setIsDefault(model.getIsDefault());
//                entity.setUpdateTime(new Date());
//                return 1;//sellerUserInfoDAO.updateCustomerAddr(entity);
//            }
            if (model != null && model.getWeiId()!=null && model.getWeiId().longValue()>0) { 
                model.setUpdateTime(new Date());
                return sellerUserInfoDAO.updateCustomerAddr(model);
            }
            return 0;
        } else {
            model.setRegisterTime(new Date());
            // 添加
            return sellerUserInfoDAO.addCustomerAddr(model);
        }
    }

    @Override
    public int deleteAddress(long weiid, int caddrID) {
        return sellerUserInfoDAO.deleteAddress(weiid, caddrID);
    }

    @Override
    public int setDefault(long weiid, int caddrID) {
        // 如果修改的时候设为默认,修改其他的地址不为默认
        sellerUserInfoDAO.cancelDefault(weiid,caddrID);
        return sellerUserInfoDAO.setDefault(caddrID);
    }
}
