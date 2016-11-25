package com.okwei.service.impl.user;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.okwei.bean.domain.TLevelWeiId;
import com.okwei.bean.domain.TVerificationId;
import com.okwei.bean.domain.TWeiFans;
import com.okwei.bean.domain.UBrandSupplyer;
import com.okwei.bean.domain.UChildrenUser;
import com.okwei.bean.domain.UOtherLogin;
import com.okwei.bean.domain.UPlatformSupplyer;
import com.okwei.bean.domain.UShopInfo;
import com.okwei.bean.domain.UUserAssist;
import com.okwei.bean.domain.UWeiSeller;
import com.okwei.bean.domain.UWeiSellerLoginLog;
import com.okwei.bean.enums.ChildrenSupplyerStatusEnum;
import com.okwei.bean.enums.OtherLoginType;
import com.okwei.bean.enums.LoginTypeEnum;
import com.okwei.bean.enums.MobileBindEnum;
import com.okwei.bean.enums.UserChildenType;
import com.okwei.bean.enums.UserIdentityType;
import com.okwei.bean.enums.VerifyCodeType;
import com.okwei.bean.vo.BaseImageMsg;
import com.okwei.bean.vo.ChildAccountInfo;
import com.okwei.bean.vo.LoginPublicResult;
import com.okwei.bean.vo.LoginResult;
import com.okwei.bean.vo.LoginReturnMsg;
import com.okwei.bean.vo.LoginUser;
import com.okwei.bean.vo.ReturnModel;
import com.okwei.bean.vo.ReturnStatus;
import com.okwei.bean.vo.user.LoginReturnAPP;
import com.okwei.bean.vo.user.LoginUserExtend;
import com.okwei.common.JsonUtil;
import com.okwei.dao.user.ILoginDAO;
import com.okwei.service.IBaseCommonService;
import com.okwei.service.IPowerService;
import com.okwei.service.impl.BaseService;
import com.okwei.service.user.ILoginService;
import com.okwei.service.user.IUserIdentityManage;
import com.okwei.util.AES;
import com.okwei.util.AppSettingUtil;
import com.okwei.util.DES;
import com.okwei.util.HttpRequestHelper;
import com.okwei.util.ImgDomain;
import com.okwei.util.MD5Encrypt;
import com.okwei.util.ObjectUtil;
import com.okwei.util.ParseHelper;
import com.okwei.util.RedisUtil;
import com.okwei.util.VerifyCode;

@Service
public class LoginService extends BaseService implements ILoginService
{
    @Autowired
    private IPowerService power;
    @Autowired
    IBaseCommonService commonService;
    @Autowired
    ILoginDAO loginDAO;
    @Autowired
    HttpServletRequest request;
    // @Autowired
    // IUUserAssistMgtDAO assistDAO;
    @Autowired
    IUserIdentityManage identityManage;

    /**
     * 判断back的链接是否合法
     */
    @Override
    public String getDomainByBack(String back)
    {
        if(ObjectUtil.isEmpty(back))
        {
            return "http://www.okwei.com";
        }
        try
        {
            back = URLDecoder.decode(back,"UTF-8");
        }
        catch(Exception e)
        {
            return "http://www.okwei.com";
        }
        String newback = back;
        back = back.replaceAll("http://","");
        back = back.replaceAll("https://","");
        int index = back.indexOf("/");// 查找第一个分割符
        if(index > 0)
        {
            back = back.substring(0,index);// 获取纯域名
        }
        // String newback = back;

        back = back.replaceAll("www\\.\\d*","");// 排除用户数据
        back = back.replaceAll("^\\d*\\.{1}","");// 排除用户数据
        List<Map<String,String>> list = AppSettingUtil.getMaplist("domains");// 获取所有可跳转的链接
        for(Map<String,String> m : list)
        {// 遍历查找
            String domain = m.get("domainUrl");
            if(domain.equals(back))
            {
                return newback;
            }
        }
        return "http://www.okwei.com";
    }

    @Override
    public UWeiSeller getWeiSeller(Long username,String md5Pwd)
    {
        UWeiSeller seller = loginDAO.getUWeiSeller(username.toString(),md5Pwd);
        return seller;
    }

    @Override
    public String setSubLoginUserByRedis(UChildrenUser childen)
    {
        if(childen == null || ObjectUtil.isEmpty(childen.getChildrenId()))
        {
            return null;
        }
        // 定义数据
        Long weiid = childen.getParentId();// 子帐号父亲微店号
        UWeiSeller seller = loginDAO.getUWeiSeller(weiid);
        if(seller == null)
        {
            return null;
        }
        LoginUser sub = new LoginUser();
        sub.setAccount(childen.getChildrenId());// 子帐号的账号
        sub.setAccountName(childen.getUserName());// 子帐号名称
        sub.setAccountType(childen.getType());// 子帐号类型
        sub.setPower(getPowerByUser(childen.getChildrenId()));// 子帐号权限
        sub.setPthSub((short) 0);// 子帐号 员工
        sub.setPthSupply((short) 0);// 子帐号 供应商
        sub.setWeiID(weiid);// 子帐号父亲微店号
        sub.setParentWeiId(weiid);// 子帐号父亲微店号
        UUserAssist assist = loginDAO.getUUserAssist(weiid);// 用户权限
        if(sub.getAccountType() == Integer.parseInt(UserChildenType.Employee.toString()))
        {
            sub.setPthSub((short) 1);// 子帐号 员工
        }
        else if(sub.getAccountType() == Integer.parseInt(UserChildenType.Supplier.toString()))
        {
            sub.setPthSupply((short) 1);// 子帐号 供应商
        }
        sub.setIsChildren(1);
        if(assist != null)
        {
            sub.setIdentity(assist.getIdentity() == null ? 0 : assist.getIdentity());// 权限标识
        }
        else
        {
            sub.setIdentity(0);// 权限标识
        }
        sub.setWeiType((short) sub.getIdentity().intValue());// 权限标识（旧）
        UShopInfo info = loginDAO.getUShopInfo(weiid);
        if(info != null)
        {
            sub.setWeiImg(ImgDomain.GetFullImgUrl(info.getShopImg()));
            sub.setWeiName(info.getShopName());
        }
        else
        {
            sub.setWeiImg(ImgDomain.GetFullImgUrl(seller.getImages()));// 头像
            sub.setWeiName(seller.getWeiName());// 昵称
        }
        String tiket = UUID.randomUUID().toString().toUpperCase();
        if(sub.getPthSub() == 1)
        {
            app4RedisByTiket(tiket,seller,assist);
        }
        RedisUtil.setObject(tiket + "_SUB",sub,86400);
        app4RedisByTiket(tiket,seller,assist);
        return tiket;
    }

    @Override
    public String setLoginUserByRedis(UWeiSeller seller)
    {
        if(seller == null || seller.getWeiId() < 1)
        {
            return null;
        }
        // 定义数据
        LoginUser user = getLoginUser(seller);
        Long weiid = seller.getWeiId();
        UUserAssist assist = loginDAO.getUUserAssist(weiid);// 用户权限
        if(assist != null)
        {
            user.setIdentity(assist.getIdentity() == null ? 0 : assist.getIdentity());// 权限标识
        }
        else
        {
            user.setIdentity(0);// 权限标识
            identityManage.insertUUserAssist(weiid);// 添加UUserAssist
        }
        user.setIsChildren(0);
        String tiket = UUID.randomUUID().toString().toUpperCase();
        RedisUtil.setObject(tiket + "_IBS",user,86400);
        app4RedisByTiket(tiket,seller,assist);
        return tiket;
    }

    /**
     * 4.0之前的缓存兼容
     * 
     * @param tiket
     */
    public void app4RedisByTiket(String tiket,UWeiSeller seller,UUserAssist ua)
    {
        if(ua == null)
        {
            ua = new UUserAssist();
        }
        LoginReturnMsg lrm = new LoginReturnMsg();
        lrm.setTiket(tiket);
        lrm.setUserID(seller.getWeiId());
        lrm.setWeiId(seller.getWeiId());
        lrm.setPhoto(commonService.getShopImageByWeiId(seller.getWeiId()));
        lrm.setUserName(commonService.getShopNameByWeiId(seller.getWeiId()));
        lrm.setQRCode(ImgDomain.GetFullImgUrl(seller.getQrCodeUrl()));
        lrm.setQQ(seller.getQq());
        lrm.setSignature(ua.getSignature());
        lrm.setPhone(seller.getMobilePhone());
        lrm.setEmail(seller.getEmail());
        lrm.setIdentityType((ua.getIdentity() == null ? 0 : ua.getIdentity().intValue()));
        lrm.setCompanyName(lrm.getUserName());
        RedisUtil.setObject(tiket,lrm,86400);
    }

    @Override
    public boolean insertUWeiSellerLoginLog(UWeiSellerLoginLog log)
    {
        if(log == null)
            return false;
        loginDAO.insetUWeiSellerLoginLog(log);
        return true;
    }

    @Override
    public Long getWeiSellerByPhone(String phone)
    {
        Long weiid = loginDAO.getUWeiSellerByPhone(phone);
        return weiid;
    }

    @Override
    public boolean updateUWeiSellerByPwd(Long weiid,String md5pwd)
    {
        return loginDAO.updateUWeiSellerByPwd(weiid,md5pwd);
    }

    @Override
    public boolean insertTVerificationId(TVerificationId tvid)
    {
        return loginDAO.insertTVerificationId(tvid);
    }

    @Override
    public LoginResult getUWeiSellerByLogin(String username,String md5Pwd,String desPwd,String ip,LoginTypeEnum loginType)
    {
        LoginResult result = new LoginResult();
        // 判断是否子帐号
        boolean subNum = username.indexOf("_") > 1;
        if(subNum)
        {
            UChildrenUser childern = loginDAO.getChildrenUser(username,md5Pwd);
            // 密码是否正确
            if(childern == null)
            {
                result.setStatus(2);
                result.setMessage("用户名密码错误");
                return result;
            }
            if(childern.getState() == Short.parseShort(ChildrenSupplyerStatusEnum.pass.toString()))
            {
                String tiket = setSubLoginUserByRedis(childern);
                if(tiket != null)
                {
                    String childenId = childern.getChildrenId().replaceAll("_","");
                    // 登陆数据写入缓存
                    UWeiSellerLoginLog log = new UWeiSellerLoginLog();
                    log.setLoginIp(ip);
                    log.setLoginTime(new Date());
                    log.setWeiId(Long.parseLong(childenId));
                    log.setTiket(tiket);
                    log.setLoginType(Short.parseShort(loginType.toString()));
                    log.setTerrminateType(Short.parseShort(loginType.toString()));
                    // log.setIdentityType(identityType);//用户权限标识
                    insertUWeiSellerLoginLog(log);
                    result.setStatus(1);
                    result.setMessage(tiket);
                    result.setWeiid(childern.getParentId());// 返回子帐号的上级微店号
                }
                else
                {
                    result.setStatus(-1);
                    result.setMessage("写入缓存不成功");
                }
            }
            else
            {
                result.setStatus(-3);
                result.setMessage("该微店号已被禁用");
            }
        }
        else
        {
            UWeiSeller seller = loginDAO.getUWeiSeller(username,md5Pwd,desPwd);
            // 密码是否正确
            if(seller == null)
            {
                result.setStatus(2);
                result.setMessage("用户名密码错误");
                return result;
            }
            // 是否被禁用
            if(seller.getStates() == 1)
            {
                String tiket = setLoginUserByRedis(seller);// 数据写入缓存
                if(tiket != null)
                {
                    // 登陆数据写入缓存
                    UWeiSellerLoginLog log = new UWeiSellerLoginLog();
                    log.setLoginIp(ip);
                    log.setLoginTime(new Date());
                    log.setWeiId(seller.getWeiId());
                    log.setTiket(tiket);
                    log.setLoginType(Short.parseShort(loginType.toString()));
                    log.setTerrminateType(Short.parseShort(loginType.toString()));
                    // log.setIdentityType(identityType);//用户权限标识
                    insertUWeiSellerLoginLog(log);
                    result.setStatus(1);
                    result.setMessage(tiket);
                    result.setWeiid(seller.getWeiId());
                }
                else
                {
                    result.setStatus(-1);
                    result.setMessage("写入缓存不成功");
                }
            }
            else
            {
                result.setStatus(-3);
                result.setMessage("该微店号已被禁用");
            }
        }
        return result;
    }

    @Override
    public LoginResult getUWeiSellerByLogin(String username,String md5Pwd,String ip,LoginTypeEnum loginType)
    {
        return getUWeiSellerByLogin(username,md5Pwd,"",ip,loginType);
    }

    /**
     * 获取用户权限
     * 
     * @param name
     *            微店号/微店号_子帐号
     * @return
     */
    public List<String> getPowerByUser(String name)
    {
        boolean subNum = name.indexOf("_") > 1;
        List<String> list = new ArrayList<String>();
        if(subNum)
        {
            list = power.getPowerByChildrenID(name);
        }
        else
        {
            list = power.getPowerByWeiID(Long.parseLong(name));
        }
        return list;
    }

    @Override
    public UWeiSeller getUWeiSellerByWeiId(Long weiid)
    {
        return loginDAO.getUWeiSellerByWeiId(weiid);
    }

    @Override
    public long getNewWeiNo()
    {
        long result = 0;
        String url = "http://app.okwei.com/API/V2/User/GetWeiidNew.ashx";
        String param = "itype=1001";
        String reString = HttpRequestHelper.sendGet(url,param);
        result = ParseHelper.toLong(reString);
        return result;
    }

    @Override
    public ReturnModel otherRegist(String token,long sjWeiNo,String mobile,String pwd,String code)
    {
        ReturnModel rm = new ReturnModel();
        // 如果获取不到token数据直接注册
        if(ObjectUtil.isEmpty(mobile))
        {
            rm.setStatu(ReturnStatus.ParamError);
            rm.setStatusreson("手机号码不能为空");
            return rm;
        }
        if(ObjectUtil.isEmpty(pwd))
        {
            rm.setStatu(ReturnStatus.ParamError);
            rm.setStatusreson("密码不能为空");
            return rm;
        }
        if(ObjectUtil.isEmpty(code))
        {
            rm.setStatu(ReturnStatus.ParamError);
            rm.setStatusreson("手机验证码不能为空");
            return rm;
        }
        // 先判断手机有没有注册
        Long weiid = getWeiSellerByPhone(mobile);
        if(weiid != null && weiid > 0)
        {
            rm.setStatu(ReturnStatus.ParamError);
            rm.setStatusreson("手机已注册微店号");
            return rm;
        }
//        long newweiid = getNewWeiNo();
//        if(newweiid < 1)
//        {
//            rm.setStatu(ReturnStatus.SystemError);
//            rm.setStatusreson("生成微店号失败，请联系客服");
//            return rm;
//        }
        Object objcount = RedisUtil.getObject("RegistPhoneCodeCount_" + mobile);
        String countStr = (objcount == null ? "0" : objcount.toString());
        Integer count = ObjectUtil.isEmpty(countStr) ? 0 : StringToInt(countStr);
        count++;
        RedisUtil.setObject("RegistPhoneCodeCount_" + mobile,count.toString(),3600);
        if(count > 5)
        {
            rm.setStatusreson("手机验证码验证次数超过五次，请重新获取验证码");
            rm.setStatu(ReturnStatus.DataError);
            return rm;
        }
        Object objcode = RedisUtil.getObject("RegistPhoneCode_" + mobile);
        String redisCode = (objcode == null ? "" : objcode.toString());
        if(!code.equals(redisCode))
        {
            rm.setStatusreson("手机验证码错误");
            rm.setStatu(ReturnStatus.DataError);
            return rm;
        }
        UWeiSeller sjseller = loginDAO.getUWeiSeller(sjWeiNo);
        if(sjseller == null)
        {
            sjseller = loginDAO.getUWeiSeller(1L);
            sjWeiNo = 1L;
        }
        UWeiSeller seller = new UWeiSeller();
//        seller.setWeiId(newweiid);
        seller.setSponsorWeiId(sjWeiNo);
        seller.setMobilePhone(mobile);
        seller.setMobileIsBind(Short.parseShort(MobileBindEnum.Bind.toString()));
        seller.setPassWord(MD5Encrypt.encrypt(pwd).toUpperCase());
        seller.setQq("");
        seller.setRegisterTime(new Date());
        seller.setIpAddress(getIpAddr());
        seller.setStates((short) 1);
        seller.setUptree(ObjectUtil.isEmpty(sjseller.getUptree()) ? String.valueOf(sjWeiNo) : sjseller.getUptree() + "-" + String.valueOf(sjWeiNo));
        seller.setWeiName("微店用户");
        if(!ObjectUtil.isEmpty(token))
        {
            LoginPublicResult lpr = (LoginPublicResult) RedisUtil.getObject(token);
            if(lpr != null)
            {
                seller.setWeiName(lpr.getNickName());
                if(!ObjectUtil.isEmpty(lpr.getHeadImg()))
                {
                    seller.setImages(lpr.getHeadImg());
                }
                else
                {
                    seller.setImages("/images/logo.jpg");
                }
               
            }
        }
        if(loginDAO.insertUWeiSeller(seller))
        {
            // 用户基础信息
            identityManage.addIdentity(seller.getWeiId(),UserIdentityType.commonUser);
            rm.setStatusreson("成功");
            rm.setStatu(ReturnStatus.Success);
            String tiket = setLoginUserByRedis(seller);
            rm.setBasemodle(tiket);
//          //完善等级表
//			completeLevelWeiID(seller);
//			//完善粉丝表
//			completeWeiFans(seller,seller.getWeiId());
//			//成为8888的粉丝
//			TWeiFans wf= new TWeiFans();
//			wf.setFanWeiId(seller.getWeiId());
//			wf.setFlag((short) 1);
//			wf.setWeiId(8888L);
//			loginDAO.save(wf);
        }
        else
        {
            rm.setStatusreson("注册失败");
            rm.setStatu(ReturnStatus.DataError);
        }
        if(!ObjectUtil.isEmpty(token))
        {
        	LoginPublicResult lpr = (LoginPublicResult) RedisUtil.getObject(token);
            if(lpr != null)
            {
	        	 UOtherLogin other = new UOtherLogin();
	             other.setCreateTime(new Date());
	             other.setOpenId(lpr.getOpenID());
	             other.setPortType(lpr.getLoginType());
	             other.setWeiId(seller.getWeiId());
	             loginDAO.insertOtherLogin(other);
            }
        }
        return rm;
    }

    @Override
    public LoginResult registWeiUser(String ip,String sjWeiNo,String weiName,String mobile,String pwd,String code)
    {
        LoginResult result = new LoginResult();// 返回实体
        if(ObjectUtil.isEmpty(weiName))
        {
            result.setMessage("微店名称不能为空");
            result.setStatus(-1);
            return result;
        }
        if(ObjectUtil.isEmpty(mobile))
        {
            result.setMessage("手机号码不能为空");
            result.setStatus(-1);
            return result;
        }
        if(ObjectUtil.isEmpty(pwd))
        {
            result.setMessage("密码不能为空");
            result.setStatus(-1);
            return result;
        }
        if(ObjectUtil.isEmpty(code))
        {
            result.setMessage("手机验证码不能为空");
            result.setStatus(-1);
            return result;
        }
        // 先判断手机有没有注册
        Long weiid = getWeiSellerByPhone(mobile);
        if(weiid != null && weiid > 0)
        {
            result.setMessage("手机已注册微店号");
            result.setStatus(-3);
            return result;
        }
//        long newweiid = getNewWeiNo();
//        if(newweiid < 1)
//        {
//            result.setMessage("生成微店号失败，请联系客服");
//            result.setStatus(-1);
//            return result;
//        }
        Object objcount = RedisUtil.getObject("RegistPhoneCodeCount_" + mobile);
        String countStr = (objcount == null ? "0" : objcount.toString());
        Integer count = ObjectUtil.isEmpty(countStr) ? 0 : StringToInt(countStr);
        count++;
        RedisUtil.setObject("RegistPhoneCodeCount_" + mobile,count.toString(),3600);
        if(count > 5)
        {
            result.setMessage("手机验证码验证次数超过五次，请重新获取验证码");
            result.setStatus(-1);
            return result;
        }
        Object objred = RedisUtil.getObject("RegistPhoneCode_" + mobile);
        String redisCode = (objred == null ? "" : objred.toString());
        if(!code.equals(redisCode))
        {
            result.setMessage("验证码错误");
            result.setStatus(-2);
            return result;
        }
        RedisUtil.delete("RegistPhoneCode_" + mobile);
        Long sjweiId = 1L;
        UWeiSeller sjseller = new UWeiSeller();
        try
        {
            sjweiId = Long.parseLong(sjWeiNo);
            sjseller = getUWeiSellerByWeiId(sjweiId);
            if(sjseller == null)
            {
                sjweiId = 1L;
                sjseller = getUWeiSellerByWeiId(sjweiId);
            }
        }
        catch(Exception e)
        {
            sjweiId = 1L;
        }
        UWeiSeller seller = new UWeiSeller();
//        seller.setWeiId(newweiid);
        seller.setSponsorWeiId(sjweiId);
        seller.setWeiName(weiName);
        seller.setMobilePhone(mobile);
        seller.setMobileIsBind(Short.parseShort(MobileBindEnum.Bind.toString()));
        seller.setPassWord(MD5Encrypt.encrypt(pwd).toUpperCase());
        seller.setQq("");
        seller.setRegisterTime(new Date());
        seller.setIpAddress(ip);
        seller.setStates((short) 1);
        seller.setUptree(ObjectUtil.isEmpty(sjseller.getUptree()) ? sjweiId.toString() : sjseller.getUptree() + "-" + sjweiId.toString());
        seller.setImages("/images/logo.jpg");
        if(loginDAO.insertUWeiSeller(seller))
        {
            UShopInfo info = new UShopInfo();
            info.setWeiId(seller.getWeiId());
            info.setShopImg("/images/logo.jpg");
            info.setShopName(weiName);
            info.setWeiImg("/images/logo.jpg");
            loginDAO.insertShopInfo(info);
            // 用户基础信息
            identityManage.addIdentity(seller.getWeiId(),UserIdentityType.commonUser);
            String tiket = setLoginUserByRedis(seller);
            result.setMessage(tiket);
            result.setStatus(1);
//          //完善等级表
//			completeLevelWeiID(seller);
//			//完善粉丝表
//			completeWeiFans(seller,seller.getWeiId());
//			//成为8888的粉丝
//			TWeiFans wf= new TWeiFans();
//			wf.setFanWeiId(seller.getWeiId());
//			wf.setFlag((short) 1);
//			wf.setWeiId(8888L);
//			loginDAO.save(wf);
        }
        else
        {
            result.setMessage("注册失败");
            result.setStatus(-1);
        }
        return result;
    }

    @Override
    public ReturnModel appLoginReturnMsg(String tiket)
    {
        ReturnModel rm = new ReturnModel();
        // 判断tiket是否为空
        if(ObjectUtil.isEmpty(tiket))
        {
            rm.setStatu(ReturnStatus.LoginError);
            rm.setStatusreson("生成Tiket为空");
            return rm;
        }
        // 获取缓存的用户信息
        LoginUser user = (LoginUser) RedisUtil.getObject(tiket + "_IBS");
        LoginReturnAPP app = new LoginReturnAPP();
        if(user == null)
        {
            user = (LoginUser) RedisUtil.getObject(tiket + "_SUB");
            if(user == null)
            {
                rm.setStatu(ReturnStatus.LoginError);
                rm.setStatusreson("缓存失效，请重新登录");
                rm.setBasemodle(app);
                return rm;
            }
        }
        app.setTiket(tiket);// 用户标识
        app.setWeiId(user.getWeiID());// 用户微店号
        app.setUserId(user.getWeiID());// 用户微店号
        app.setIsEnableAuth((short) 0);// 不知道不赋值
        UWeiSeller seller = loginDAO.getUWeiSeller(user.getWeiID()); // 获取用户基本信息
        app.setPhone(seller.getMobilePhone());// 手机号
        app.setIdentityType(user.getIdentity());// 用户权限标示
        if((user.getIdentity()&2)>0 || (user.getIdentity()&4)>0)
        {
        	app.setPublishDenyFlag((short) 0);
        }
        else
        {
        	app.setPublishDenyFlag((short) 1);
        }
        // 是否设置密码
        if(ObjectUtil.isEmpty(seller.getPassWord()))
        {
            app.setHasPassWord((short) 0);
        }
        else
        {
            app.setHasPassWord((short) 1);
        }
        // 是否绑定手机
        if(seller.getMobileIsBind() == null || seller.getMobileIsBind() == Short.parseShort(MobileBindEnum.Nobind.toString()))
        {
            app.setMobileIsBind((short) 0);
        }
        else
        {
            app.setMobileIsBind((short) 1);
        }
        BaseImageMsg img = new BaseImageMsg();// 头像
        img.setH(0);
        img.setW(0);
        UShopInfo shopInfo = loginDAO.getUShopInfo(user.getWeiID());// 查询店铺信息表
        if(shopInfo != null)
        {
            app.setShopBusContent(shopInfo.getShopBusContent());// 店铺主营
            app.setShopName(shopInfo.getShopName());// 店铺名称
            img.setUrl(ImgDomain.GetFullImgUrl(shopInfo.getShopImg()));
        }
        else
        {
            // ///////////////////////////////////////////////////同步UShopInfo表
            img.setUrl("");
        }
        app.setShopImg(img);// 图像信息
        // 查询是否绑定微信
        if(loginDAO.getUOtherLogin(user.getWeiID(),OtherLoginType.weixin))
        {
            app.setIsBindWx((short) 1);
        }
        else
        {
            app.setIsBindWx((short) 0);
        }
        app.setIsChildAccount((short) 0);// 是否子帐号
        if(user.getIsChildren() == 1)
        {
            ChildAccountInfo info = new ChildAccountInfo();
            UChildrenUser child = loginDAO.getChildrenUser(user.getAccount());
            info.setAccount(child.getChildrenId());
            info.setAccountName(child.getUserName());
            info.setAccountType(Short.parseShort(child.getType().toString()));
            info.setAuth(user.getPower());
            app.setChildAccount(info);
            app.setIsChildAccount((short) 1);// 是否子帐号
        }
        app.setHomePageUrl(AppSettingUtil.getSingleValue("homepageurl").replace("{tiket}",tiket));
        rm.setStatu(ReturnStatus.Success);
        rm.setStatusreson("成功");
        rm.setBasemodle(app);
        return rm;
    }

    @Override
    public boolean judgeUserType(long weiid,UserIdentityType type)
    {
        if(weiid < 1)
            return false;
        UUserAssist assist = loginDAO.getUUserAssist(weiid);
        if(assist == null)
        {
            return false;
        }
        Integer userType = assist.getIdentity();
        if(userType == null || userType < 1)
        {
            return false;
        }
        else
        {
            if((userType & Integer.parseInt(type.toString())) > 0)
            {
                return true;
            }
            else
            {
                return false;
            }
        }
    }

    @Override
    public boolean judgeSub(String id,UserChildenType type)
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public LoginUser getLoginUserByWeiId(long weiid)
    {
        if(weiid < 1)
        {
            return null;
        }
        UWeiSeller seller = loginDAO.getUWeiSeller(weiid);
        if(seller == null)
        {
            return null;
        }
        return getLoginUser(seller);
    }

    public LoginUser getLoginUser(UWeiSeller seller)
    {
        // 定义数据
        LoginUser user = new LoginUser();
        UUserAssist assist = loginDAO.getUUserAssist(seller.getWeiId());// 用户权限
        if(assist != null)
        {
            user.setIdentity(assist.getIdentity() == null ? 0 : assist.getIdentity());// 权限标识
        }
        else
        {
            user.setIdentity(0);// 权限标识
        }
        user.setWeiType((short) user.getIdentity().intValue());// 权限标识（旧）
        user.setPthSub((short) 0);// 子帐号
        user.setPthSupply((short) 0);// 子帐号 供应商
        user.setWeiID(seller.getWeiId());// 微店号
        user.setIsChildren(0);// 不是子帐号
        UShopInfo info = loginDAO.getUShopInfo(seller.getWeiId());
        if(info != null)
        {
            user.setWeiImg(ImgDomain.GetFullImgUrl(info.getShopImg()));
            user.setWeiName(info.getShopName());
        }
        else
        {
            user.setWeiImg(ImgDomain.GetFullImgUrl(seller.getImages()));// 头像
            user.setWeiName(seller.getWeiName());// 昵称
        }
        // 获取权限
        user.setPower(getPowerByUser(user.getWeiID().toString()));
        return user;
    }

    @Override
    public LoginReturnMsg getLoginReturnMsgByTiekt(String tiket)
    {
        LoginUser user = (LoginUser) RedisUtil.getObject(tiket + "_IBS");// 微店用户
        LoginUser sub = (LoginUser) RedisUtil.getObject(tiket + "_SUB");// 子帐号用户
        LoginReturnMsg msg = new LoginReturnMsg();
        if(user != null)
        {
            msg = getLoginReturnMsgs(user.getWeiID());
            msg.setIsChildAccount(0);
            return msg;
        }
        else if(sub != null)
        {
            msg = getLoginReturnMsgs(sub.getWeiID());
            msg.setIsChildAccount(1);
            ChildAccountInfo info = new ChildAccountInfo();
            info.setAccount(sub.getAccount());
            info.setAccountName(sub.getAccountName());
            info.setAccountType(sub.getAccountType().shortValue());
            info.setAuth(sub.getPower());
            msg.setChildAccount(info);
            return msg;
        }
        else
        {
            return null;
        }
    }

    public LoginReturnMsg getLoginReturnMsgs(Long weiid)
    {
        LoginReturnMsg msg = new LoginReturnMsg();
        UWeiSeller seller = loginDAO.getUWeiSellerByWeiId(weiid);
        if(seller == null || seller.getStates() == null || seller.getStates().shortValue() != 1)
        {
            return null;
        }
        UShopInfo info = loginDAO.getUShopInfo(weiid);
        if(info != null && info.getShopName() != null && !"".equals(info.getShopName()))
        {
            msg.setShopName(info.getShopName());
            msg.setUserName(info.getShopName());
        }
        else
        {
            msg.setShopName(commonService.getShopNameByWeiId(weiid));
            msg.setUserName(seller.getWeiName());
        }
        if(info != null && info.getShopBusContent() != null && !"".equals(info.getShopBusContent()))
        {
            msg.setShopBusContent(info.getShopBusContent());
        }
        else
        {
            msg.setShopBusContent(commonService.getBusContentByWeiId(weiid));
        }
        BaseImageMsg basrImg = new BaseImageMsg();
        basrImg.setH(0);
        basrImg.setW(0);
        if(info != null && info.getShopImg() != null && !"".equals(info.getShopImg()))
        {
            basrImg.setUrl(commonService.getShopImageByWeiId(weiid));
            msg.setShopImg(basrImg);
            msg.setPhoto(ImgDomain.GetFullImgUrl(info.getShopImg()));
        }
        else
        {
            basrImg.setUrl(commonService.getShopImageByWeiId(weiid));
            msg.setShopImg(basrImg);
            msg.setPhoto(ImgDomain.GetFullImgUrl(seller.getImages()));
        }
        msg.setQRCode("http://app.okwei.com/API/V1/UserInfo/GetQRCode.aspx?weino=" + seller.getWeiId());
        msg.setHasPassWord((short) 1);// 默认是有密码的
        // 判断木有密码时
        if(seller.getPassWord() == null || seller.getPassWord().trim().equals(""))
            msg.setHasPassWord((short) 0);
        msg.setCompanyName(commonService.getShopNameByWeiId(seller.getWeiId()));
        msg.setEmail(seller.getEmail());
        msg.setMobileIsBind((short) 0);
        if(seller.getMobileIsBind() != null && seller.getMobileIsBind().shortValue() == Short.parseShort(MobileBindEnum.Bind.toString()))
        {
            msg.setMobileIsBind((short) 1);
        }
        msg.setPhone(seller.getMobilePhone());
        msg.setQQ(seller.getQq());
        msg.setUserID(seller.getWeiId());
        UUserAssist assist = loginDAO.getUUserAssist(weiid);
        msg.setIdentityType(1);
        if(assist != null && assist.getIdentity() != null)
        {
            msg.setIdentityType(assist.getIdentity());
        }
        if(loginDAO.getUOtherLogin(weiid,OtherLoginType.weixin))
        {
            msg.setIsBindWx(1);
        }
        else
        {
            msg.setIsBindWx(0);
        }
        return msg;
    }

    @Override
    public String otherRegist(LoginPublicResult login,Long sjWeiID)
    {
        // 如果过来的第三方登陆数据是空的
        if(login == null || ObjectUtil.isEmpty(login.getOpenID()))
        {
            return null;
        }
        UOtherLogin other = loginDAO.getOtherLoginByOpenIDAndType(login.getOpenID(),login.getLoginType());
        if(other != null)
        {
            return "-1";// 已经注册
            // 登录
            // UWeiSeller seller = loginDAO.getUWeiSeller(other.getWeiId());
            // return setLoginUserByRedis(seller);
        }
        else
        {
            // 没有数据就注册**************************************************************
//            long regWeiID = getNewWeiNo();// 新注册的微店号
            long upWeiID = 1;
            if(sjWeiID != null && sjWeiID > 0)
            {
                upWeiID = sjWeiID;
            }
            UWeiSeller upSeller = getUWeiSellerByWeiId(upWeiID);
            if(upSeller == null)
            {
                upSeller = getUWeiSellerByWeiId(1L);
            }
            // 定义添加数据
            UWeiSeller weiSeller = new UWeiSeller();
//            weiSeller.setWeiId(regWeiID);
            weiSeller.setWeiName(login.getNickName());
            weiSeller.setImages(login.getHeadImg());
            weiSeller.setMobileIsBind(Short.parseShort(MobileBindEnum.Nobind.toString()));
            weiSeller.setRegisterTime(new Date());
            weiSeller.setStates((short) 1);
            weiSeller.setQq("");
            weiSeller.setSponsorWeiId(upWeiID);
            weiSeller.setUptree(ObjectUtil.isEmpty(upSeller.getUptree()) ? upSeller.toString() : upSeller.getUptree() + "-" + upSeller.toString());
            if(loginDAO.insertUWeiSeller(weiSeller))
            {
                // 用户基础信息
                // assistDAO.saveUserAssist(regWeiID,Integer.parseInt(UserIdentityType.commonUser.toString()));
                identityManage.addIdentity(weiSeller.getWeiId(),UserIdentityType.commonUser);
                // 用户添加成功
                UOtherLogin registother = new UOtherLogin();
                registother.setCreateTime(new Date());
                registother.setOpenId(login.getOpenID());
                registother.setPortType(login.getLoginType());
                registother.setWeiId(weiSeller.getWeiId());
                if(loginDAO.insertOtherLogin(registother))
                {
                    return setLoginUserByRedis(weiSeller);
                }
            }
        }
        return null;
    }

    @Override
    public String otherLogin(LoginPublicResult login)
    {
        // 如果过来的第三方登陆数据是空的
        if(login == null || ObjectUtil.isEmpty(login.getOpenID()))
        {
            return null;
        }
        UOtherLogin other = loginDAO.getOtherLoginByOpenIDAndType(login.getOpenID(),login.getLoginType());
        if(other != null)
        {
            // 登录
            UWeiSeller seller = loginDAO.getUWeiSeller(other.getWeiId());
            String tiket = setLoginUserByRedis(seller);
            UWeiSellerLoginLog log = new UWeiSellerLoginLog();
            log.setLoginIp(getIpAddr());
            log.setLoginTime(new Date());
            log.setWeiId(seller.getWeiId());
            log.setTiket(tiket);
            log.setLoginType(Short.parseShort(LoginTypeEnum.APPLogin.toString()));
            log.setTerrminateType(Short.parseShort(LoginTypeEnum.APPLogin.toString()));
            insertUWeiSellerLoginLog(log);
            return tiket;
        }
        else
        {
            return "-1";// 没有数据，没有注册
        }
    }

    @Override
    public boolean bindLogin(LoginPublicResult lpr,Long weiID)
    {
        if(lpr == null || weiID == null || weiID < 1)
        {
            return false;
        }
        UWeiSeller seller = loginDAO.getUWeiSeller(weiID);
        if(seller != null)
        {
        	UOtherLogin reg=loginDAO.getUniqueResultByHql(" from UOtherLogin u where u.weiId=? and u.portType=?", weiID,lpr.getLoginType());
        	if(reg!=null)
        	{
        		reg.setOpenId(lpr.getOpenID());
        		reg.setCreateTime(new Date());
        		loginDAO.update(reg);
        	}
        	else
        	{
        		reg = new UOtherLogin();
//              reg.setCreateTime(new Date());
//              reg.setOpenId(lpr.getOpenID());
//              reg.setPortType(lpr.getLoginType());
//              reg.setWeiId(seller.getWeiId());
        		loginDAO.save(reg);
        	}
        	return true;
//            // 判断有没有这个类型的第三方登陆
//            if(loginDAO.getUOtherLogin(weiID,lpr.getLoginType()))
//            {
//                loginDAO.delUOtherLogin(weiID,lpr.getLoginType());
//            }
//            // 用户添加成功**
//            UOtherLogin registother = new UOtherLogin();
//            registother.setCreateTime(new Date());
//            registother.setOpenId(lpr.getOpenID());
//            registother.setPortType(lpr.getLoginType());
//            registother.setWeiId(seller.getWeiId());
//            if(loginDAO.insertOtherLogin(registother))
//            {
//                return true;
//            }
        }
        return false;
    }

    @Override
    public UPlatformSupplyer getU_PlatformSupplyer(Long weiId,Short state) throws Exception
    {
        return loginDAO.selectU_PlatformSupplyer(weiId,state);
    }

    @Override
    public UBrandSupplyer getU_BrandSupplyer(Long weiId,Short state) throws Exception
    {
        return loginDAO.selectU_BrandSupplyer(weiId,state);
    }

    @Override
    public boolean updateShopInfo(UShopInfo info)
    {
        UShopInfo shop = loginDAO.getUShopInfo(info.getWeiId());
        if(shop != null)
        {
            if(!ObjectUtil.isEmpty(info.getShopBusContent()))
            {
                shop.setShopBusContent(info.getShopBusContent());
            }
            if(!ObjectUtil.isEmpty(info.getShopImg()))
            {
                shop.setShopImg(info.getShopImg());
            }
            if(!ObjectUtil.isEmpty(info.getShopName()))
            {
                shop.setShopName(info.getShopName());
            }
            return true;
        }
        else
        {
            insertShopInfo(info.getWeiId());
        }
        return false;
    }

    public boolean insertUserAssist(long weiid)
    {
        if(weiid < 1)
            return false;
        UWeiSeller seller = loginDAO.getUWeiSeller(weiid);
        if(seller == null)
            return false;
        UUserAssist assist = loginDAO.getUUserAssist(weiid);
        if(assist != null)
            return false;
        // assistDAO.saveUserAssist(weiid,Integer.parseInt(UserIdentityType.commonUser.toString()));
        identityManage.addIdentity(weiid,UserIdentityType.commonUser);
        return true;
    }

    /**
     * 添加UshopInfo
     * 
     * @param info
     * @return
     */
    public boolean insertShopInfo(long weiID)
    {
        UWeiSeller seller = loginDAO.getUWeiSeller(weiID);
        if(seller == null)
        {
            return false;
        }
        UShopInfo info = new UShopInfo();
        info.setWeiId(weiID);
        info.setShopName(seller.getWeiName());
        info.setShopImg(seller.getImages());
        return loginDAO.insertShopInfo(info);
    }

    @Override
    public LoginUserExtend checkLoginByBind(String token,String tiket)
    {
        LoginUserExtend lue = new LoginUserExtend();
        if(ObjectUtil.isEmpty(token) || ObjectUtil.isEmpty(tiket))
        {
            lue.setMessage("参数有误");
            lue.setStatu((short) -1);
            return lue;
        }
        LoginUser user = (LoginUser) RedisUtil.getObject(tiket + "_IBS");
        LoginPublicResult lpr = (LoginPublicResult) RedisUtil.getObject(token);// 缓存获取数据
        if(user == null)
        {
            lue.setMessage("用户登陆过期");
            lue.setStatu((short) -1);
            return lue;
        }
        if(lpr == null)
        {
            lue.setMessage("第三方登陆过期，请重新登陆");
            lue.setStatu((short) -1);
            return lue;
        }
        // 绑定数据
        UOtherLogin login = new UOtherLogin();
        login.setCreateTime(new Date());
        login.setOpenId(lpr.getOpenID());
        login.setPortType(lpr.getLoginType());
        login.setWeiId(user.getWeiID());
        loginDAO.insertOtherLogin(login);
        lue.setStatu(Short.parseShort("1"));// 成功
        lue.setMessage("成功");
        return lue;
    }

    @Override
    public LoginUserExtend checkLoginByBind(LoginPublicResult lpr,String phone,String psw)
    {
        LoginUserExtend lue = new LoginUserExtend();
        if(ObjectUtil.isEmpty(psw) || ObjectUtil.isEmpty(phone))
        {
            lue.setMessage("参数有误");
            lue.setStatu((short) -1);
            return lue;
        }
        psw = DES.decrypt(psw,"");
        if(ObjectUtil.isEmpty(psw))
        {
            lue.setMessage("密码有误");
            lue.setStatu((short) -1);
            return lue;
        }
        String md5Pwd = MD5Encrypt.encrypt(psw);
        String aesPwd = AES.aesEncrypt(psw);
        UWeiSeller seller = loginDAO.getUWeiSeller(phone,md5Pwd,aesPwd);
        if(seller != null)
        {
            if(seller.getStates() != null && seller.getStates() == 1)
            {
                // 绑定数据
                UOtherLogin login = new UOtherLogin();
                login.setCreateTime(new Date());
                login.setOpenId(lpr.getOpenID());
                login.setPortType(lpr.getLoginType());
                login.setWeiId(seller.getWeiId());
                loginDAO.insertOtherLogin(login);
                // 登陆
                LoginReturnAPP lrapp = new LoginReturnAPP();
                String tiket = setLoginUserByRedis(seller);
                ReturnModel rm = appLoginReturnMsg(tiket);
                if(rm.getStatu() == ReturnStatus.Success)
                {
                    lrapp = (LoginReturnAPP) rm.getBasemodle();
                    lue.setStatu(Short.parseShort("1"));// 成功
                    lue.setMessage("成功");
                    lue.setLrm(lrapp);
                    // 缓存登陆信息
                    UWeiSellerLoginLog log = new UWeiSellerLoginLog();
                    log.setLoginIp(getIpAddr());
                    log.setLoginTime(new Date());
                    log.setWeiId(seller.getWeiId());
                    log.setTiket(lue.getLrm().getTiket());
                    log.setLoginType(Short.parseShort(LoginTypeEnum.APPLogin.toString()));
                    log.setTerrminateType(Short.parseShort(LoginTypeEnum.APPLogin.toString()));
                    insertUWeiSellerLoginLog(log);
                }
                else
                {
                    lue.setStatu(Short.parseShort("-6"));// 用户状态异常（非法用户）
                    lue.setMessage("系统数据有问题");
                }
            }
            else
            {
                lue.setStatu(Short.parseShort("-2"));// 用户状态异常（非法用户）
                lue.setMessage("非法用户");
            }
        }
        else
        {
            lue.setStatu(Short.parseShort("-5"));// 绑定的微店号不存在
            lue.setMessage("绑定的微店号不存在");
        }
        return lue;
    }

    @Override
    public LoginUserExtend checkLoginByThird(LoginPublicResult lpr)
    {
        Log logger = LogFactory.getLog("OtherLogin");

        LoginUserExtend lue = new LoginUserExtend();
        if(lpr == null || ObjectUtil.isEmpty(lpr.getOpenID()) || lpr.getLoginType() == null || lpr.getLoginType() < 1)
        {
            lue.setStatu((short) -2);
            lue.setMessage("传入的参数有误");
            return lue;
        }
        // 获取第三方登陆的信息
        UOtherLogin uo = loginDAO.getOtherLoginByOpenIDAndType(lpr.getOpenID(),lpr.getLoginType());
        if(uo != null && uo.getWeiId() != null && uo.getWeiId().longValue() > 0)
        {
            // 第三方信息存在
            UWeiSeller seller = loginDAO.getUWeiSeller(uo.getWeiId());// 获取登陆信息
            if(seller != null)
            {
                if(seller.getStates() != null && seller.getStates() == 1)
                {
                    LoginReturnAPP lrapp = new LoginReturnAPP();
                    String tiket = setLoginUserByRedis(seller);
                    ReturnModel rm = appLoginReturnMsg(tiket);
                    if(rm.getStatu() == ReturnStatus.Success)
                    {
                        lrapp = (LoginReturnAPP) rm.getBasemodle();
                        lue.setStatu(Short.parseShort("1"));// 成功
                        lue.setMessage("成功");
                        lue.setLrm(lrapp);
                        // 缓存登陆信息
                        UWeiSellerLoginLog log = new UWeiSellerLoginLog();
                        log.setLoginIp(getIpAddr());
                        log.setLoginTime(new Date());
                        log.setWeiId(seller.getWeiId());
                        log.setTiket(lue.getLrm().getTiket());
                        log.setLoginType(Short.parseShort(LoginTypeEnum.APPLogin.toString()));
                        log.setTerrminateType(Short.parseShort(LoginTypeEnum.APPLogin.toString()));
                        insertUWeiSellerLoginLog(log);
                    }
                    else
                    {
                        lue.setStatu(Short.parseShort("-6"));// 用户状态异常（非法用户）
                        lue.setMessage("系统数据有问题");
                    }
                }
                else
                {
                    lue.setStatu(Short.parseShort("-2"));// 用户状态异常（非法用户）
                    lue.setMessage("非法用户");
                }
            }
            else
            {
                lue.setStatu(Short.parseShort("-5"));// 绑定的微店号不存在
                lue.setMessage("绑定的微店号不存在");
            }
        }
        else
        {
            // 第三方登陆成功，没有绑定微店号
            String tiket = UUID.randomUUID().toString().toUpperCase();// 生成一个tiket
            RedisUtil.setObject(tiket,lpr,300);// 存入Redis
            logger.equals("OtherLogintiket:" + tiket);
            // 第三方登陆成功，没有绑定微店号
            lue.setStatu(Short.parseShort("2"));
            lue.setMessage("第三方登陆成功，没有绑定微店号");
            lue.setTokent(tiket);

        }
        return lue;
    }

    @Override
    public ReturnModel appOtherRegist(String mobile,String vcode,String pwd,long upweiid,String tokent)
    {
        Log logger = LogFactory.getLog("OtherLogin");
        logger.error("OtherLogintokent:" + tokent);
        ReturnModel rm = new ReturnModel();
        if(ObjectUtil.isEmpty(mobile) || ObjectUtil.isEmpty(vcode) || ObjectUtil.isEmpty(pwd) || ObjectUtil.isEmpty(tokent))
        {
            rm.setStatu(ReturnStatus.ParamError);
            rm.setStatusreson("参数错误");
            return rm;
        }
        if(!VerifyCode.checkVerifyCode(mobile,VerifyCodeType.register,vcode))
        {
            rm.setStatu(ReturnStatus.SystemError);
            rm.setStatusreson("验证码有误");
            return rm;
        }
        LoginPublicResult lpr = (LoginPublicResult) RedisUtil.getObject(tokent);// 获取缓存的第三方登陆信息
        if(lpr == null)
        {
            rm.setStatu(ReturnStatus.SystemError);
            rm.setStatusreson("token不存在");
            return rm;
        }
        pwd = DES.decrypt(pwd,"");
        if(ObjectUtil.isEmpty(pwd))
        {
            rm.setStatu(ReturnStatus.ParamError);
            rm.setStatusreson("密码有误");
            return rm;
        }
        String md5Pwd = MD5Encrypt.encrypt(pwd);
        String aesPwd = AES.aesEncrypt(pwd);
        // 判断手机号码是否注册
        Long phoneWeiid = loginDAO.getUWeiSellerByPhone(mobile);
        if(phoneWeiid != null && phoneWeiid > 0)
        {
            rm.setStatu(ReturnStatus.DataError);
            rm.setStatusreson("手机号码已注册");
            return rm;
        }
//        long regWeiID = getNewWeiNo();// 新注册的微店号
        UWeiSeller upSeller = getUWeiSellerByWeiId(upweiid);
        if(upSeller == null)
        {
            upSeller = getUWeiSellerByWeiId(1L);
            upweiid = 1L;
        }
        // 定义添加数据

        UWeiSeller weiSeller = new UWeiSeller();
//        weiSeller.setWeiId(regWeiID);
        weiSeller.setWeiName(lpr.getNickName());
        weiSeller.setImages(lpr.getHeadImg());
        weiSeller.setMobileIsBind(Short.parseShort(MobileBindEnum.Bind.toString()));
        weiSeller.setMobilePhone(mobile);
        weiSeller.setRegisterTime(new Date());
        weiSeller.setStates((short) 1);
        weiSeller.setQq("");
        weiSeller.setPassWord(md5Pwd);
        weiSeller.setPwd(aesPwd);
        weiSeller.setSponsorWeiId(upweiid);
        weiSeller.setIpAddress(getIpAddr());
        weiSeller.setUptree(ObjectUtil.isEmpty(upSeller.getUptree()) ? upSeller.toString() : upSeller.getUptree() + "-" + upSeller.toString());
        if(loginDAO.insertUWeiSeller(weiSeller))
        {
            // 用户基础信息
            // assistDAO.saveUserAssist(regWeiID,Integer.parseInt(UserIdentityType.commonUser.toString()));
            identityManage.addIdentity(weiSeller.getWeiId(),UserIdentityType.commonUser);
            UShopInfo info = new UShopInfo();
            info.setShopImg(lpr.getHeadImg());
            info.setShopName(lpr.getNickName());
            info.setWeiId(weiSeller.getWeiId());
            info.setWeiImg(lpr.getHeadImg());
            info.setWeiName(lpr.getNickName());
            loginDAO.insertShopInfo(info);
            // 用户添加成功
            UOtherLogin registother = new UOtherLogin();
            registother.setCreateTime(new Date());
            registother.setOpenId(lpr.getOpenID());
            registother.setPortType(lpr.getLoginType());
            registother.setWeiId(weiSeller.getWeiId());
            if(loginDAO.insertOtherLogin(registother))
            {
                String tiket = setLoginUserByRedis(weiSeller);
                // 登陆记录
                UWeiSellerLoginLog log = new UWeiSellerLoginLog();
                log.setLoginIp(getIpAddr());
                log.setLoginTime(new Date());
                log.setWeiId(weiSeller.getWeiId());
                log.setTiket(tiket);
                log.setLoginType(Short.parseShort(LoginTypeEnum.APPLogin.toString()));
                log.setTerrminateType(Short.parseShort(LoginTypeEnum.APPLogin.toString()));
                insertUWeiSellerLoginLog(log);
                return appLoginReturnMsg(tiket);
            }
        }
        rm.setStatu(ReturnStatus.DataError);
        rm.setStatusreson("注册失败");
        return rm;
    }

    /**
     * 注册
     */
    @Override
    public ReturnModel appRegist(String mobile,String vcode,String pwd,long upweiid,String weiname)
    {
        ReturnModel rm = new ReturnModel();
        if(ObjectUtil.isEmpty(mobile) || ObjectUtil.isEmpty(vcode) || ObjectUtil.isEmpty(pwd) || ObjectUtil.isEmpty(weiname))
        {
            rm.setStatu(ReturnStatus.ParamError);
            rm.setStatusreson("参数错误");
            return rm;
        }
        if(!VerifyCode.checkVerifyCode(mobile,VerifyCodeType.register,vcode))
        {
            rm.setStatu(ReturnStatus.SystemError);
            rm.setStatusreson("验证码有误");
            return rm;
        }
        pwd = DES.decrypt(pwd,"");
        if(ObjectUtil.isEmpty(pwd))
        {
            rm.setStatu(ReturnStatus.ParamError);
            rm.setStatusreson("密码有误");
            return rm;
        }
        String md5Pwd = MD5Encrypt.encrypt(pwd);
        String aesPwd = AES.aesEncrypt(pwd);
        // 判断手机号码是否注册
        Long phoneWeiid = loginDAO.getUWeiSellerByPhone(mobile);        
        if(phoneWeiid != null && phoneWeiid > 0)
        {
            rm.setStatu(ReturnStatus.DataError);
            rm.setStatusreson("手机号码已注册");
            return rm;
        }
//        long regWeiID = getNewWeiNo();// 新注册的微店号
        UWeiSeller upSeller = getUWeiSellerByWeiId(upweiid);
        if(upSeller == null)
        {
            upSeller = getUWeiSellerByWeiId(1L);
            upweiid = 1L;
        }
        // 定义添加数据

        UWeiSeller weiSeller = new UWeiSeller();
//        weiSeller.setWeiId(regWeiID);
        weiSeller.setWeiName(weiname);
        weiSeller.setImages("http://base3.okimgs.com/images/logo.jpg");
        weiSeller.setMobileIsBind(Short.parseShort(MobileBindEnum.Bind.toString()));
        weiSeller.setMobilePhone(mobile);
        weiSeller.setRegisterTime(new Date());
        weiSeller.setStates((short) 1);
        weiSeller.setQq("");
        weiSeller.setPassWord(md5Pwd);
        weiSeller.setPwd(aesPwd);
        weiSeller.setSponsorWeiId(upweiid);
        weiSeller.setIpAddress(getIpAddr());
        weiSeller.setUptree(ObjectUtil.isEmpty(upSeller.getUptree()) ? upSeller.toString() : upSeller.getUptree() + "-" + upSeller.toString());
        if(loginDAO.insertUWeiSeller(weiSeller))
        {
            // 用户基础信息
            // assistDAO.saveUserAssist(regWeiID,Integer.parseInt(UserIdentityType.commonUser.toString()));
            identityManage.addIdentity(weiSeller.getWeiId(),UserIdentityType.commonUser);
            UShopInfo info = new UShopInfo();
            info.setShopImg("http://base3.okimgs.com/images/logo.jpg");
            info.setShopName(weiname);
            info.setWeiId(weiSeller.getWeiId());
            info.setWeiImg("http://base3.okimgs.com/images/logo.jpg");
            info.setWeiName(weiname);
            loginDAO.insertShopInfo(info);
            String tiket = setLoginUserByRedis(weiSeller);
            // 登陆记录
            UWeiSellerLoginLog log = new UWeiSellerLoginLog();
            log.setLoginIp(getIpAddr());
            log.setLoginTime(new Date());
            log.setWeiId(weiSeller.getWeiId());
            log.setTiket(tiket);
            log.setLoginType(Short.parseShort(LoginTypeEnum.APPLogin.toString()));
            log.setTerrminateType(Short.parseShort(LoginTypeEnum.APPLogin.toString()));
            insertUWeiSellerLoginLog(log);
//            //完善等级表
//			completeLevelWeiID(weiSeller);
//			//完善粉丝表
//			completeWeiFans(weiSeller,weiSeller.getWeiId());
//			//成为8888的粉丝
//			TWeiFans wf= new TWeiFans();
//			wf.setFanWeiId(weiSeller.getWeiId());
//			wf.setFlag((short) 1);
//			wf.setWeiId(8888L);
//			loginDAO.save(wf);
            return appLoginReturnMsg(tiket);
        }
        rm.setStatu(ReturnStatus.DataError);
        rm.setStatusreson("注册失败");
        return rm;
    }
  //完善粉丝表
  	void completeWeiFans(UWeiSeller seller,Long weiid)
  	{
  		try
  		{
  		TWeiFans wf= new TWeiFans();
  		wf.setFanWeiId(weiid);
  		wf.setFlag((short) 1);
  		wf.setWeiId(seller.getSponsorWeiId()==null?-1:seller.getSponsorWeiId());
  		loginDAO.save(wf);
  		UWeiSeller user=loginDAO.get(UWeiSeller.class, seller.getSponsorWeiId());
  		if(user !=null && seller.getSponsorWeiId()>1)
  		{
  			completeWeiFans(user,weiid);
  		}
  		}
  		catch(Exception e)
  		{
  			return ;
  		}
  	}
  	
  	//完善等级表
  	void completeLevelWeiID(UWeiSeller seller)
  	{
  		try
  		{
  		TLevelWeiId tw= loginDAO.get(TLevelWeiId.class, seller.getSponsorWeiId());
  		if(tw == null)
  		{
  			UWeiSeller user= loginDAO.get(UWeiSeller.class, seller.getSponsorWeiId());
  			if(user!=null && seller.getSponsorWeiId()>1)
  				completeLevelWeiID(user);			
  		}
  		else
  		{
  			TLevelWeiId lw= new TLevelWeiId();
  			lw.setCreateTime(new Date());
  			lw.setLevel((tw.getLevel()==null?0:tw.getLevel())+1);
  			lw.setParentId(seller.getSponsorWeiId()==null?-1:seller.getSponsorWeiId());
  			lw.setWeiId(seller.getWeiId());
  			loginDAO.save(lw);
  		}}
  		catch(Exception e)
  		{
  			return;
  		}
  	}
    /**
     * 获取访问用户的客户端IP（适用于公网与局域网）.
     */
    public String getIpAddr()
    {
        if(request == null)
        {
            return "";
            // throw (new Exception("getIpAddr method HttpServletRequest Object is null"));
        }
        String ipString = request.getHeader("x-forwarded-for");
        if(StringUtils.isBlank(ipString) || "unknown".equalsIgnoreCase(ipString))
        {
            ipString = request.getHeader("Proxy-Client-IP");
        }
        if(StringUtils.isBlank(ipString) || "unknown".equalsIgnoreCase(ipString))
        {
            ipString = request.getHeader("WL-Proxy-Client-IP");
        }
        if(StringUtils.isBlank(ipString) || "unknown".equalsIgnoreCase(ipString))
        {
            ipString = request.getRemoteAddr();
        }

        // 多个路由时，取第一个非unknown的ip
        final String[] arr = ipString.split(",");
        for(final String str : arr)
        {
            if(!"unknown".equalsIgnoreCase(str))
            {
                ipString = str;
                break;
            }
        }
        return ipString;
    }

    private int StringToInt(String str)
    {
        if(str == null)
        {
            return 0;
        }

        try
        {
            return Integer.parseInt(str);
        }
        catch(Exception e)
        {
            return 0;
        }
    }

    /**
     * 父类转子类
     */
    @Override
    public <T> Object getSubByLoginUser(LoginUser user,Class<T> subClass)
    {
        return null;
    }

}