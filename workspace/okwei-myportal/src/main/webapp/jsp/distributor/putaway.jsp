<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="pg" uri="http://www.okwei.com/pagination"%>

<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>我的分销商</title>
</head>

<body class="bg_f3">
	<div class="fr conter_right">
      <div class="zhuag_suv bor_si fl bg_white">
      	<div class="oneci_ztai fl">
        	<div class="left_font tr fl f12 ft_c9">状态：</div>
            <div class="left_xuanzs fl f12">
            	<ul>
                	<li name="mzh_4_7_yes">上架中</li> 
                    <li name="mzh_4_7_yes" class="yes_bgs">已下架</li>
                </ul>
            </div>
        </div>
        <div class="oneci_ztai fl">
        	<div class="left_font tr fl f12 ft_c9">分类：</div>
            <div class="left_xuanzs fl f12">
            	<ul>
                	<li name="mzh_4_7_no">全部</li>
                    <li name="mzh_4_7_no">男装</li>
                    <li class="yes_bgs" name="mzh_4_7_no">女装</li>
                </ul>
            </div>
        </div>
        <div class="blank1"></div>
      
      
      </div>
      <div class="blank1"></div>
      <div class="gygl_xxk mt_20 bor_le bor_ri">  
        <table style="" class="gygl_xxk_table bor_cls">
          <tbody><tr class="ndfxs_1-2_color ndfxs_1-2_border lh_40 fw_b td_no">
            <td>图片</td>
            <td>标题</td>
            <td>分类</td>
            <td>价格</td>
            <td>供应商</td>
            <td>操作</td>
          </tr>
          <tr class="ndfxs_1-2_border td_no">
            <td colspan="6">
            	<div class="fiex_sel">
                	<input type="checkbox" class="dis_b fl ml_10 mt13">
                    <label class="dis_b fl ml_5 mt13 ft_c6">全选</label>
					<div class="profile">
						<span class="select_ui"><div class="select_arrow"></div><div class="select_text_ui" style="min-width: 7.5em;">更改到别的分类</div><select style="width: 127px;" name="birthday_year" required="true">
				            <option>更改到别的分类</option>
				            <option>男装</option>
				            <option>女装</option>
				            <option>童装</option>
				            <option>新建分类</option>
				        </select></span>
			        </div>
					<input type="text" placeholder="分类名称" class="dis_b ml_20 fl mt_5 btn_h28 w104">  
                    <input type="submit" onclick="win('请输入验证码','mzh_tsk')" class="dis_b ml_20 fl mt_5 btn_sel28_pre bor_cls shou" value="确认">
                    <a class="dis_b ml_20 fl mt_5 btn_hui28_pre shou">删除</a>
                    <a class="dis_b ml_20 fl mt_5 btn_hui28_pre shou">下架</a> 
                </div> 
            </td>
          </tr>
          <tr class="ndfxs_1-2_border">
            <td class="gygl_xxk_table_cz_td"><div class="gygl_xxk_table_cz_qx">
                <input type="checkbox" class="gygl_xxk_table_cz_qx_text">
                <img src="/statics/pic/mail_ico.jpg"> </div></td>
            <td class="gygl_xxk_table_cz_td"><div class="gygl_xxk_table_cz_bt"> <span class="gygl_xxk_table_cz_bt_span mb_10">步步高x3手机套vivo x3t手机壳x3sw保护套x手机套x3手机壳卡通</span>
                <div style="text-indent:0;" class="gygl_xxk_table_cz_bt_ytj mr_20"><span class="gygl_top_l_xx_hong mr_5 fw_b">批</span></div>
                 
              </div></td>
            <td class="gygl_xxk_table_cz_td"><div class="gygl_xxk_table_cz_sj lin_28">男装</div></td>
            <td class="gygl_xxk_table_cz_td">
            	<div class="gygl_xxk_table_cz_sj_fx lin_28">价格：<span class="fm_aria ft_c3 fw_b">￥12.50</span></div>
                <div class="gygl_xxk_table_cz_sj_fx lin_28">佣金：<span class="fm_aria ft_c3 fw_b">￥12.50</span></div>
            </td>
            <td class="gygl_xxk_table_cz_td">
            	<div class="gygl_xxk_table_cz_sj_fx w104 lin_20">深圳市云商微店网络技术有限公司</div> 
            
            </td>
            <td class="gygl_xxk_table_cz_td">
            	<div class="mzh_xl_an">
					<div class="mzh_an">编辑</div>
					<div class="mzh_xl">
						<img isclick="0" name="mzh_xl" src="/statics/pic/mzh_4_8_xl_no.jpg">
						<div class="mzh_xl_dw">
							<a class="mzh_xlk_dw_a" name="mzh_span" href="#">下架</a>
							<a class="mzh_xlk_dw_a" name="mzh_span" href="#">删除</a>
						</div>
					</div>
				</div>
            </td>
          </tr>
          <tr class="ndfxs_1-2_border">
            <td class="gygl_xxk_table_cz_td"><div class="gygl_xxk_table_cz_qx">
                <input type="checkbox" class="gygl_xxk_table_cz_qx_text">
                <img src="/statics/pic/mail_ico.jpg"> </div></td>
            <td class="gygl_xxk_table_cz_td"><div class="gygl_xxk_table_cz_bt"> <span class="gygl_xxk_table_cz_bt_span mb_10">步步高x3手机套vivo x3t手机壳x3sw保护套x手机套x3手机壳卡通</span>
                <div style="text-indent:0;" class="gygl_xxk_table_cz_bt_ytj mr_20"><span class="gygl_top_l_xx_hong mr_5 fw_b">批</span></div>
                 
              </div></td>
            <td class="gygl_xxk_table_cz_td"><div class="gygl_xxk_table_cz_sj lin_28">男装</div></td>
            <td class="gygl_xxk_table_cz_td">
            	<div class="gygl_xxk_table_cz_sj_fx lin_28">价格：<span class="fm_aria ft_c3 fw_b">￥12.50</span></div>
                <div class="gygl_xxk_table_cz_sj_fx lin_28">佣金：<span class="fm_aria ft_c3 fw_b">￥12.50</span></div>
            </td>
            <td class="gygl_xxk_table_cz_td">
            	<div class="gygl_xxk_table_cz_sj_fx w104 lin_20">深圳市云商微店网络技术有限公司</div> 
            
            </td>
            <td class="gygl_xxk_table_cz_td">
            	<div class="mzh_xl_an">
					<div class="mzh_an">编辑</div>
					<div class="mzh_xl">
						<img isclick="0" name="mzh_xl" src="/statics/pic/mzh_4_8_xl_no.jpg">
						<div class="mzh_xl_dw">
							<a class="mzh_xlk_dw_a" name="mzh_span" href="#">下架</a>
							<a class="mzh_xlk_dw_a" name="mzh_span" href="#">删除</a>
						</div>
					</div>
				</div>
            </td>
          </tr>
          <tr>
            <td class="gygl_xxk_table_cz_td"><div class="gygl_xxk_table_cz_qx">
                <input type="checkbox" class="gygl_xxk_table_cz_qx_text">
                <img src="/statics/pic/mail_ico.jpg"> </div></td>
            <td class="gygl_xxk_table_cz_td"><div class="gygl_xxk_table_cz_bt"> <span class="gygl_xxk_table_cz_bt_span mb_10">步步高x3手机套vivo x3t手机壳x3sw保护套x手机套x3手机壳卡通</span>
                <div style="text-indent:0;" class="gygl_xxk_table_cz_bt_ytj mr_20"><span class="gygl_top_l_xx_hong mr_5 fw_b">批</span></div>
                 
              </div></td>
            <td class="gygl_xxk_table_cz_td"><div class="gygl_xxk_table_cz_sj lin_28">男装</div></td>
            <td class="gygl_xxk_table_cz_td">
            	<div class="gygl_xxk_table_cz_sj_fx lin_28">价格：<span class="fm_aria ft_c3 fw_b">￥12.50</span></div>
                <div class="gygl_xxk_table_cz_sj_fx lin_28">佣金：<span class="fm_aria ft_c3 fw_b">￥12.50</span></div>
            </td>
            <td class="gygl_xxk_table_cz_td">
            	<div class="gygl_xxk_table_cz_sj_fx w104 lin_20">深圳市云商微店网络技术有限公司</div> 
            
            </td>
            <td class="gygl_xxk_table_cz_td">
            	<div class="mzh_xl_an">
					<div class="mzh_an">编辑</div>
					<div class="mzh_xl">
						<img isclick="0" name="mzh_xl" src="/statics/pic/mzh_4_8_xl_no.jpg">
						<div class="mzh_xl_dw">
							<a class="mzh_xlk_dw_a" name="mzh_span" href="#">下架</a>
							<a class="mzh_xlk_dw_a" name="mzh_span" href="#">删除</a>
						</div>
					</div>
				</div>
            </td>
          </tr>
          
        </tbody></table>
        
      </div>
      <div class="blank1"></div>
      <div class="gygl_xxk mt_20 bor_le bor_ri">  
        <table style="" class="gygl_xxk_table bor_cls">
          <tbody><tr class="ndfxs_1-2_color ndfxs_1-2_border lh_40 fw_b td_no">
            <td>图片</td>
            <td>标题</td>
            <td>分类</td>
            <td>价格</td>
            <td>供应商</td>
            <td>操作</td>
          </tr>
          <tr class="ndfxs_1-2_border td_no">
            <td colspan="6">
            	<div class="fiex_sel">
                	<input type="checkbox" class="dis_b fl ml_10 mt13">
                    <label class="dis_b fl ml_5 mt13 ft_c6">全选</label> 
					<div class="profile">
						<span class="select_ui"><div class="select_arrow"></div><div class="select_text_ui" style="min-width: 4.5em;">选择分类</div><select style="width: 50px;" name="birthday_year" required="true">
				            <option>选择分类</option>
				            <option>男装</option>
				            <option>女装</option>
				            <option>童装</option>
				            <option>新建分类</option>
				        </select></span>
			        </div>
                    <input type="text" placeholder="分类名称" class="dis_b ml_20 fl mt_5 btn_h28 w104">  
                    <input type="submit" class="dis_b ml_20 fl mt_5 btn_sel28_pre bor_cls shou" value="应用">
                    <div class="dis_b ml_20 fl mt_5 btn_hui28_pre shou">删除</div>
                    <div class="dis_b ml_20 fl mt_5 btn_hui28_pre shou">下架</div> 
                </div>
            	
            
            
            </td>
          </tr>
          <tr class="ndfxs_1-2_border">
            <td class="gygl_xxk_table_cz_td"><div class="gygl_xxk_table_cz_qx">
                <input type="checkbox" class="gygl_xxk_table_cz_qx_text">
                <img src="/statics/pic/mail_ico.jpg"> </div></td>
            <td class="gygl_xxk_table_cz_td"><div class="gygl_xxk_table_cz_bt"> <span class="gygl_xxk_table_cz_bt_span mb_10">步步高x3手机套vivo x3t手机壳x3sw保护套x手机套x3手机壳卡通</span>
                <div style="text-indent:0;" class="gygl_xxk_table_cz_bt_ytj mr_20"><span class="gygl_top_l_xx_hong mr_5 fw_b">批</span></div>
                 
              </div></td>
            <td class="gygl_xxk_table_cz_td"><div class="gygl_xxk_table_cz_sj lin_28">男装</div></td>
            <td class="gygl_xxk_table_cz_td">
            	<div class="gygl_xxk_table_cz_sj_fx lin_28">价格：<span class="fm_aria ft_c3 fw_b">￥12.50</span></div>
                <div class="gygl_xxk_table_cz_sj_fx lin_28">佣金：<span class="fm_aria ft_c3 fw_b">￥12.50</span></div>
            </td>
            <td class="gygl_xxk_table_cz_td">
            	<div class="gygl_xxk_table_cz_sj_fx w104 lin_20">深圳市云商微店网络技术有限公司</div> 
            
            </td>
            <td class="gygl_xxk_table_cz_td">
            	<div class="mzh_xl_an">
					<div class="mzh_an">编辑</div>
					<div class="mzh_xl">
						<img isclick="0" name="mzh_xl" src="/statics/pic/mzh_4_8_xl_no.jpg">
						<div class="mzh_xl_dw">
							<a class="mzh_xlk_dw_a" name="mzh_span" href="#">下架</a>
							<a class="mzh_xlk_dw_a" name="mzh_span" href="#">删除</a>
						</div>
					</div>
				</div>
            </td>
          </tr>
          <tr class="ndfxs_1-2_border">
            <td class="gygl_xxk_table_cz_td"><div class="gygl_xxk_table_cz_qx">
                <input type="checkbox" class="gygl_xxk_table_cz_qx_text">
                <img src="/statics/pic/mail_ico.jpg"> </div></td>
            <td class="gygl_xxk_table_cz_td"><div class="gygl_xxk_table_cz_bt"> <span class="gygl_xxk_table_cz_bt_span mb_10">步步高x3手机套vivo x3t手机壳x3sw保护套x手机套x3手机壳卡通</span>
                <div style="text-indent:0;" class="gygl_xxk_table_cz_bt_ytj mr_20"><span class="gygl_top_l_xx_hong mr_5 fw_b">批</span></div>
                 
              </div></td>
            <td class="gygl_xxk_table_cz_td"><div class="gygl_xxk_table_cz_sj lin_28">男装</div></td>
            <td class="gygl_xxk_table_cz_td">
            	<div class="gygl_xxk_table_cz_sj_fx lin_28">价格：<span class="fm_aria ft_c3 fw_b">￥12.50</span></div>
                <div class="gygl_xxk_table_cz_sj_fx lin_28">佣金：<span class="fm_aria ft_c3 fw_b">￥12.50</span></div>
            </td>
            <td class="gygl_xxk_table_cz_td">
            	<div class="gygl_xxk_table_cz_sj_fx w104 lin_20">深圳市云商微店网络技术有限公司</div> 
            
            </td>
            <td class="gygl_xxk_table_cz_td">
            	<div class="mzh_xl_an">
					<div class="mzh_an">编辑</div>
					<div class="mzh_xl">
						<img isclick="0" name="mzh_xl" src="/statics/pic/mzh_4_8_xl_no.jpg">
						<div class="mzh_xl_dw">
							<a class="mzh_xlk_dw_a" name="mzh_span" href="#">下架</a>
							<a class="mzh_xlk_dw_a" name="mzh_span" href="#">删除</a>
						</div>
					</div>
				</div>
            </td>
          </tr>
          <tr>
            <td class="gygl_xxk_table_cz_td"><div class="gygl_xxk_table_cz_qx">
                <input type="checkbox" class="gygl_xxk_table_cz_qx_text">
                <img src="/statics/pic/mail_ico.jpg"> </div></td>
            <td class="gygl_xxk_table_cz_td"><div class="gygl_xxk_table_cz_bt"> <span class="gygl_xxk_table_cz_bt_span mb_10">步步高x3手机套vivo x3t手机壳x3sw保护套x手机套x3手机壳卡通</span>
                <div style="text-indent:0;" class="gygl_xxk_table_cz_bt_ytj mr_20"><span class="gygl_top_l_xx_hong mr_5 fw_b">批</span></div>
                 
              </div></td>
            <td class="gygl_xxk_table_cz_td"><div class="gygl_xxk_table_cz_sj lin_28">男装</div></td>
            <td class="gygl_xxk_table_cz_td">
            	<div class="gygl_xxk_table_cz_sj_fx lin_28">价格：<span class="fm_aria ft_c3 fw_b">￥12.50</span></div>
                <div class="gygl_xxk_table_cz_sj_fx lin_28">佣金：<span class="fm_aria ft_c3 fw_b">￥12.50</span></div>
            </td>
            <td class="gygl_xxk_table_cz_td">
            	<div class="gygl_xxk_table_cz_sj_fx w104 lin_20">深圳市云商微店网络技术有限公司</div> 
            
            </td>
            <td class="gygl_xxk_table_cz_td">
            	<div class="mzh_xl_an">
					<div class="mzh_an">编辑</div>
					<div class="mzh_xl">
						<img isclick="0" name="mzh_xl" src="/statics/pic/mzh_4_8_xl_no.jpg">
						<div class="mzh_xl_dw">
							<a class="mzh_xlk_dw_a" name="mzh_span" href="#">下架</a>
							<a class="mzh_xlk_dw_a" name="mzh_span" href="#">删除</a>
						</div>
					</div>
				</div>
            </td>
          </tr>
          
        </tbody></table>
        
      </div>
    </div>	
</body>