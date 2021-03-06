package com.okwei.sso.web;

import net.sf.json.JSONObject;

import com.okwei.common.JsonUtil;
import com.okwei.sso.bean.vo.WxLoginResultWap;
import com.okwei.sso.bean.vo.WxLoginTokenWap ;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.HttpURLConnection;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.struts2.ServletActionContext;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.okwei.sso.bean.util.HttpRequestWap;

@Controller
@RequestMapping(value = "/")
public class Wap_WxLogin {
	
	/**
     * 登录页
     * @param model
     * @return
     */
    @ResponseBody
	@RequestMapping(value = "/wapwxlogin", method = { RequestMethod.POST, RequestMethod.GET })
	public ModelAndView Page_Load(String code, String state, ModelMap model) {
    	
    	ModelAndView mav = new ModelAndView("login/wxLoginWap");
	    String headimg ;
	    String nickname ;
	    String openid ;
		String accesstoken ;
		String originalState =state;
		String originalOpenid; //原openid变量名到最后其实不是openid，而是unionid，被占用了。没办法只好重新命名一个。
        model.put("code", code) ;
        model.put("originalState", originalState) ;
        if (isNotEmpty(code)) {
            String[] arrs = state.split("AABB") ;
            String prefix = arrs[0]; //目前对于微信登陆，= getwxlogtok；对于获取微信地址= editwxaddress
            //生成表单提交的URL
            String actionUrl ="http://"+("0".equals(arrs[1]) ? "" : arrs[1] + ".")+"m.okwei.com/weixin" ;
            model.put("actionUrl", actionUrl);
            model.put("state", arrs[0]) ;
            //第一次请求
			String getToeknUrl   = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=wx37b4b9567e34aa1d&secret=ad41e67224c689ca6f826588d4608919&code=" + code + "&grant_type=authorization_code";
            String tokenJson = httpRequest(getToeknUrl);
            JSONObject jsonOne=JSONObject.fromObject(tokenJson);
            if(jsonOne==null || jsonOne.getString("access_token")==null)
            {
            	model.put("errorType", "..") ;
            	return new ModelAndView("login/wapWxLoginError");
            }
            accesstoken = jsonOne.getString("access_token") ;
            openid = jsonOne.getString("openid") ;
            model.put("accesstoken", accesstoken) ;
            if (isNotEmpty(accesstoken)&&isNotEmpty(openid)) {
            	if(!"getwxlogtok".equals(prefix)){
            		model.put("openid", "anything") ;
            		return mav ;
            	}
            	//第二次请求地址
                String getUrl = "https://api.weixin.qq.com/sns/userinfo?access_token=" + accesstoken + "&openid=" + openid;
                String result = httpRequest(getUrl);
                JSONObject jsonTwo=JSONObject.fromObject(result);
                openid = jsonTwo.getString("unionid") ;
                originalOpenid = jsonTwo.getString("openid");
                nickname = jsonTwo.getString("nickname") ;
                headimg = jsonTwo.getString("headimgurl") ;
                if(isNotEmpty(openid)&&isNotEmpty(nickname)&&isNotEmpty(headimg)){
                	model.put("headimg", headimg) ;
                    model.put("nickname", nickname) ;
                    model.put("openid", openid) ;
                    model.put("originalOpenid", originalOpenid) ;
                    return mav ;
                }else{
                	model.put("errorType", "...") ;
                	return new ModelAndView("login/wapWxLoginError");
                }
            } else {
            	model.put("errorType", "..") ;
            	return new ModelAndView("login/wapWxLoginError");
            }
        } else {
            model.put("errorType", ".") ;
            return new ModelAndView("login/wapWxLoginError");
        }
    }
    
    /**
     * 登录页
     * @param model
     * @return
     */
    @ResponseBody             
	@RequestMapping(value = "/wapwxloginnet", method = { RequestMethod.POST, RequestMethod.GET })
	public ModelAndView Page_LoadNet(String code, String state, ModelMap model) {
    	
    	ModelAndView mav = new ModelAndView("login/wxLoginWapNet");
	    String headimg ;
	    String nickname ;
	    String openid ;
		String accesstoken ;
		String originalState =state;
		String originalOpenid; //原openid变量名到最后其实不是openid，而是unionid，被占用了。没办法只好重新命名一个。
        model.put("code", code) ;
        model.put("originalState", originalState) ;
        if (isNotEmpty(code)) {
            String[] arrs = state.split("AABB") ;
            String prefix = arrs[0]; //目前对于微信登陆，= getwxlogtok；对于获取微信地址= editwxaddress
            //生成表单提交的URL
            String actionUrl ="http://"+("0".equals(arrs[1])? "" : arrs[1] + ".")+"m.okwei.net/weixin" ;
            model.put("actionUrl", actionUrl);
            model.put("state", arrs[0]) ;
            //第一次请求
			String getToeknUrl   = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=wx37b4b9567e34aa1d&secret=ad41e67224c689ca6f826588d4608919&code=" + code + "&grant_type=authorization_code";
            String tokenJson = httpRequest(getToeknUrl);
            JSONObject jsonOne=JSONObject.fromObject(tokenJson);
            accesstoken = jsonOne.getString("access_token") ;
            openid = jsonOne.getString("openid") ;
            model.put("accesstoken", accesstoken) ;
            if (isNotEmpty(accesstoken)&&isNotEmpty(openid)) {
            	if(!"getwxlogtok".equals(prefix)){
            		model.put("openid", "anything") ;
            		return mav ;
            	}
            	//第二次请求地址
                String getUrl = "https://api.weixin.qq.com/sns/userinfo?access_token=" + accesstoken + "&openid=" + openid;
                String result = httpRequest(getUrl);
                JSONObject jsonTwo=JSONObject.fromObject(result);
                openid = jsonTwo.getString("unionid") ;
                originalOpenid = jsonTwo.getString("openid");
                nickname = jsonTwo.getString("nickname") ;
                headimg = jsonTwo.getString("headimgurl") ;
                if(isNotEmpty(openid)&&isNotEmpty(nickname)&&isNotEmpty(headimg)){
                	model.put("headimg", headimg) ;
                    model.put("nickname", nickname) ;
                    model.put("openid", openid) ;
                    model.put("originalOpenid", originalOpenid) ;
                    return mav ;
                }else{
                	model.put("errorType", "...") ;
                	return new ModelAndView("login/wapWxLoginError");
                }
            } else {
            	model.put("errorType", "..") ;
            	return new ModelAndView("login/wapWxLoginError");
            }
        } else {
            model.put("errorType", ".") ;
            return new ModelAndView("login/wapWxLoginError");
        }
    }
    
    private boolean isNotEmpty(Object obj){
    	if(obj!=null&&!"".equals(obj))return true ;
    	return false; 
    }
    
    
    public String requestGetData(String url,String param){
		String returnMsg = HttpRequestWap.sendPost(url, param);
		return returnMsg ;
    }
    
    public String httpRequest(String urlStr) {
    	try {
			URL url = new URL(urlStr) ;
			HttpURLConnection httpConn = (HttpURLConnection) url.openConnection() ;
			httpConn.setConnectTimeout(3000);
		    httpConn.setDoInput(true);
		    httpConn.setRequestMethod("GET");
		    int respCode = httpConn.getResponseCode();
		    if (respCode == 200)
		        return ConvertStream2Json(httpConn.getInputStream());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return null;
    }
    private static String ConvertStream2Json(InputStream inputStream) {
        String jsonStr = "";
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        try {
            while ((len = inputStream.read(buffer, 0, buffer.length)) != -1) {
                out.write(buffer, 0, len);
            }
            jsonStr = new String(out.toByteArray());
        }catch (IOException e) {
            e.printStackTrace();
        }
        return jsonStr;
      }
}
