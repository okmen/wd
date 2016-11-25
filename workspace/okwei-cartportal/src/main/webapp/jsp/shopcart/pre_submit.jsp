<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>  
<%@ page import="java.util.ResourceBundle"%>
<%
	String productdomain = ResourceBundle.getBundle("domain").getString("productdomain");
String okweidomain = ResourceBundle.getBundle("domain").getString("okweidomain");
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
%>      
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="http://base3.okwei.com/js/jquery-1.7.1.min.js"></script>
<title>（购物车）确认订单</title>
</head>   
<style type="text/css">
.jbzl_dl dt {
	width: auto;
}
.jbzl_dl{display: block;}
.jbzl_dl dd.jbzl_dl_dd{width: 100px;}
.jbzl_shdz_yes_div b{width:48px;}
.jbzl_shdz_yes .mzh_span{margin:5px 5px 0 0px;padding: 3px 10px;float:left;}
</style> 
<body class="bg_f3">
<div class="outermost">
	<div class="head_two">
    <div class="mar_au">
      <div class="head_logo fl"><a href="http://www.<%=okweidomain%>"><img src="http://base3.okimgs.com/images/xh_logo.gif" /></a></div>
      <div class="head_gwc fl f24 ft_c6">购物车</div>
      <div class="jidong_tiao fr">
        <div class="dut_imgs_one"></div>
        <div class="font_imgsone f12 ft_red">购物车</div>
        <div class="font_imgstwo f12 ft_red">确认订单</div>
        <div class="font_imgsthr f12 ft_c9">支付</div>
        <div class="font_imgsfor f12 ft_c9">完成</div>
      </div>
    </div>
  </div>
  <div class="blank"></div>

<div class="tab_qie">
    <div class="tab_marcen bg_white fl">
      <div class="fl nei_guis">
        <div class="fl guan_dao f14 fw_b ft_c3">选择收货地址</div>
        <div class="jbzl_ns">
          <div class="jbzl_shdz">
          
        	  <c:forEach var="addr" items="${list}" varStatus="index">
					  <div class='${addr.isDefault==1||addr.isShopAddress==1?"jbzl_shdz_yes":"jbzl_shdz_no" } mb_20' name="divaddress" caddrId="${addr.caddrId }">
						<div class="jbzl_shdz_yes_div"  >
							<b name="receiverName">${addr.receiverName }</b>
						</div>
						<div class="jbzl_shdz_yes_div_0">
							<span>${addr.address }</span> <span name="detailAddr">${addr.detailAddr }</span> <span name="mobilePhone">${addr.mobilePhone }</span>
							<div  class="mzh_span">
							<c:choose>
								 <c:when test="${addr.isShopAddress==1&&addr.isDefault==1  }">
									默认收货、落地店地址
								</c:when>
								<c:when test="${addr.isDefault==1 }">
									默认收货地址
								</c:when>
								<c:when test="${addr.isShopAddress==1 }">
									默认落地店地址
								</c:when> 
							 <%--  <c:otherwise>
									<span name="setasdefault" style="display: none;" onclick="setDefaultAddr('${addr.caddrId }')">设为默认收货地址</span>
									<span name="setasdefault" style="display: none;" onclick="setShopAddress('${addr.caddrId }')">设为默认落地店地址</span>
								</c:otherwise>   --%>
							</c:choose>   
							</div>   
						<input type="hidden" name="isDefault" value="${addr.isDefault}" />
						<input type="hidden" name="isShopAddress" value="${addr.isShopAddress}" />
						</div>
						<div class="jbzl_shdz_yes_div_1" name="${addr.isDefault==1||addr.isShopAddress==1?"mzh_mrdz1":"mzh_mrdz" }" >
								<a href="javascript:deleteAddr('${addr.caddrId }');" class="jbzl_close mr_20">删除</a> <a href="javascript:;" class="jbzl_bainji mr_20" onclick="openPopups('编辑地址',this)">编辑</a>
						</div>
						<input type="hidden" name="province" value="${addr.province }" /> <input type="hidden" name="city" value="${addr.city }" /> <input type="hidden" name="district" value="${addr.district }" /> <input type="hidden" name="qq" value="${addr.qq }" />
					</div>  
					 
					<input type="hidden" name="province" value="${addr.province }" /> 
					<input type="hidden" name="city" value="${addr.city }" /> 
					<input type="hidden" name="district" value="${addr.district }" /> 
					<input type="hidden" name="qq" value="${addr.qq }" />
					<input type="hidden" name="address" value="${addr.address }" />
					<input type="hidden" name="detail_addr" value="${addr.detailAddr }" />
					<input type="hidden" name="receiver_name" value="${addr.receiverName }" />
					<input type="hidden" name="mobile_phone" value="${addr.mobilePhone }" />
				</c:forEach> 
          
    <%--         <c:forEach var="addr" items="${list}" varStatus="index">
				<div class='${addr.isDefault==1||addr.isShopAddress==1?"jbzl_shdzsiz_yes":"jbzl_shdzsiz_no" }' name="divaddress" caddrId="${addr.caddrId }">
					<div class="jbzl_shdz_yes_div">
						<b name="receiverName">${addr.receiverName }</b>
						<c:choose>
							 <c:when test="${addr.isShopAddress==1&&addr.isDefault==1  }">
									默认收货、落地店地址
								</c:when>
								<c:when test="${addr.isDefault==1 }">
									默认收货地址
								</c:when>
								<c:when test="${addr.isShopAddress==1 }">
									默认落地店地址
								</c:when> 
							<c:otherwise>
								<span name="setasdefault" style="display: none;" onclick="setDefaultAddr('${addr.caddrId }')">设为默认</span>
							</c:otherwise>
						</c:choose>
						<input type="hidden" name="isDefault" value="${addr.isDefault}" />
					</div>
					<div class="jbzl_shdz_yes_div_0">
						<span>${addr.address }</span> <span name="detailAddr">${addr.detailAddr }</span> <span name="mobilePhone">${addr.mobilePhone }</span>
					</div>
					<div class="jbzl_shdz_yes_div_1" name="mzh_mrdz" style="display: block;">
						<a href="javascript:deleteAddr('${addr.caddrId }');" class="jbzl_close">删除</a> <a href="javascript:;" class="jbzl_bainji mr_20" onclick="openPopups('编辑地址',this)">编辑</a>
					</div>
					<input type="hidden" name="province" value="${addr.province }" /> 
					<input type="hidden" name="city" value="${addr.city }" /> 
					<input type="hidden" name="district" value="${addr.district }" /> 
					<input type="hidden" name="qq" value="${addr.qq }" />
					<input type="hidden" name="address" value="${addr.address }" />
					<input type="hidden" name="detail_addr" value="${addr.detailAddr }" />
					<input type="hidden" name="receiver_name" value="${addr.receiverName }" />
					<input type="hidden" name="mobile_phone" value="${addr.mobilePhone }" />
				</div>
			</c:forEach> --%>
          <div class="jbzl_shdz_add" onclick="openPopups('添加地址',null)"></div>
          </div>
          <div id="address_1" style="display: none;">
				<input type="hidden" id="txtcaddrId" value="" />
				<dl class="jbzl_dl f14">
					<dd class="jbzl_dl_dd">手机：</dd>
					<dt>
						<input id="txtPhone" type="text" class="fl btn_h28 w250" maxlength="11" onkeyup="this.value=this.value.replace(/\D/g,'')" />
					</dt>
				</dl>
				<dl class="jbzl_dl f14">
					<dd class="jbzl_dl_dd">收货人：</dd>
					<dt>
						<input id="txtLinkman" type="text" class="fl btn_h28 w250" />
					</dt>
				</dl>
				<dl class="jbzl_dl f14">
					<dd class="jbzl_dl_dd">地区：</dd>
					<dt class=" fl">
						<select id="selProvince" class="mzh_select">
							<option>省</option>
						</select> <select id="selCity" class="mzh_select">
							<option>市</option>
						</select> <select id="selDistrict" class="mzh_select">
							<option>区</option>
						</select>
					</dt>
				</dl>
				<dl class="jbzl_dl f14">
					<dd class="jbzl_dl_dd">详细地址：</dd>
					<dt>
						<input type="text" maxlength="65" id="txtDetailAdd" class="kud_boxs" value="" /> 
					</dt>
				</dl>
				<dl class="jbzl_dl f14">
					<dd class="jbzl_dl_dd">QQ号：</dd>
					<dt>
						<input id="txtQQ" type="text" class="fl btn_h28 w250" maxlength="20" onkeyup="this.value=this.value.replace(/\D/g,'')" />
					</dt>
				</dl>
				<dl class="jbzl_dl f14">
					<dd class="jbzl_dl_dd"></dd>
					<dt>
						<label><input type="checkbox" checked="checked" name="checkisDef" id="checkisDef"></input>设为默认收货地址</label>   </br>
					 	<c:if test="${user.pthldd == 1}">
					 	<label><input type="checkbox" name="checkisDef"   id="checkisDef1"></input>设为默认落地店地址</label>
						</c:if> 
					</dt>
				</dl>
			</div>
        </div>
        <div class="fl guan_dao f14 fw_b ft_c3">产品清单</div>
        <div class="shop_title fl">
          <div class="neiceng fl fw_b f12 ft_c3">
            <div class="ceng_cia1 fl">商品信息</div>
            <div class="ceng_cia2 fl">单价（元）</div>
            <div class="ceng_cia3 fl">数量</div>
            <div class="ceng_cia4 fl">优惠方式（元）</div>
            <div class="ceng_cia5 fl">来源</div>
            <div class="ceng_cia6 fl">小计（元）</div>
          </div>
        </div>
        <div class="blank2"></div>
        <c:if test="${fn:length(list)<1}">
        </c:if>
        <form action="/shopCartMgt/orderSubmit" method="post">
       	<input type="hidden" id="stype" name="stype" value="${stype}"/>
       	<input type="hidden" id="addrId" name="addrId"/>
       	<input type="hidden" id="orderJson" name="orderJson" />
       	</form>
        <form action="/shopCartMgt/preSubmit" method="post">
        	<input type="hidden" id="stype" name="stype" value="${stype}"/>
        	<input type="hidden" id="addr_Id" name="addrId" value="${addrId}"/>
        	<input type="hidden" id="scidJson" name="scidJson" value='${scidJson}'/>
        </form>
       	<input type="hidden" id="totalPrice" value="${totalPrice}" />
        <c:forEach items="${showList}" var="cart" varStatus="cartSta">
        <input type="hidden" id="totalShopPrice_${cart.companyLogid}" value="${cart.totalShopPrice}" />
        <input type="hidden" id="totalShopYoufei_${cart.companyLogid}" name="totalShopYoufei" value="${cart.totalShopYoufei}" />
        <input type="hidden" id="logisticsId_${cart.companyLogid}" value="${cart.logisticsId}" />
        <div class="fl widt_w1156 bor_si">
          <div class="h40_title f12 fl ft_c9">店铺：<span class="ft_c3">${cart.companyName}</span></div>
          <c:forEach items="${cart.productList}" var="product" varStatus="productSta">
          <div class="width_marl fl bor_bo">
          <input type="hidden" name="companyId" value="${cart.companyLogid}"/>
          <input type="hidden" name="scId" value="${product.scId}"/>
          <input type="hidden" name="prodId" value="${product.prodId}"/>
          <input type="hidden" name="styleId" value="${product.styleId}"/>
          <input type="hidden" name="prodCount" value="${product.prodCount}"/>
          <input type="hidden" name="shopWeiId" value="${product.shopWeiId}"/>
            <div class="fl widt_001">
              <div class="img_lef fl"><a  href="<%=productdomain %>/product/${product.shelvesId}?w=${user.weiID}" target="_blank" ><img src="${product.img}" /></a></div>
              <div class="img_fot fl"><a  href="<%=productdomain %>/product/${product.shelvesId}?w=${user.weiID}" target="_blank" class="ft_c3">${product.prodTitle}</a></div>
              <div class="img_cocima fl">
                <div class="color_yi mar_t20 ft_hui">${product.property}</div>
              </div>
            </div>
            <div class="fl widt_002 f12 ft_c3">${product.prodNowprice}</div>
            <div class="fl widt_003 f12 ft_c3">${product.prodCount}</div>
            <div class="fl widt_004 f12 ft_c3">无优惠</div>
            <div class="fl widt_005 f12 ft_c3">${product.shopWeiName}</div>
            <div class="fl widt_006 ft_red f14">${product.totalPrice}</div>
          </div>
		  </c:forEach>
          <div class="fl wid_66 bor_bo">
            <div class="comment_le fl f12 ft_c3">备注：
              <input type="text" class="btn_h24" placeholder="对本次交易的说明（建议填写已经和卖家达成一致的说明）" id="message_${cart.companyLogid}"/>
            </div>
            <c:if test="${stype != 2}">
            <div class="comment_rg fr f12 ft_c6"> <span style="float: left;line-height: 24px;">运送方式：</span>
              <div class="mzh_xlk" style="margin: 0px;width: 100px;" name="mzh_xlk">
              <select id="logistics_${cart.companyLogid}" name="yunfei" onchange="javascript:selYunfei(this,${cart.companyLogid});">
              	<c:forEach items="${cart.logistics}" var="logis" >
              		<option value="${logis.id}" <c:if test="${logis.id == cart.logisticsId}">selected="selected"</c:if> price="${logis.amount}">${logis.name}(¥ ${logis.amount})</option>
              	</c:forEach>
              </select>
               <!--  <div class="mzh_xlk_text" isclick="0" style="width: 100px;">包邮</div>
                <div class="mzh_xlk_dw" style="width: 100px;"> <span name="mzh_span_4_9">包邮</span> </div> -->
              </div>
            </div>
            </c:if>
          </div>
          <div class="fl count_price f12 tr">店铺合计（含运费）：<span class="f24 ft_red" id="showTotalShopPrice_${cart.companyLogid}">￥<fmt:formatNumber type="number" value="${cart.totalShopPrice+cart.totalShopYoufei}" maxFractionDigits="2"/></span></div>
        </div>
        </c:forEach>
        <div class="blank1"></div>
        <div class="fl submit_dd">
          <div class="sub_right fr">
            <div class="right_onefie fr">
              <div class="price_money tr f12 ft_c6 fw_b">实付款：<span class="f24 ft_red" id="showTotalPrice">¥ </span></div>
              <div class="price_site tr f12 ft_c6 fw_b">寄送至：<span class="fw_100">${address.address}${address.detailAddr}</span></div>
              <div class="price_site tr f12 ft_c6 fw_b">收货人：<span class="fw_100">${address.receiverName} ${address.mobilePhone}</span></div>
            </div>
            <div class="clear"></div>
            <div class="tijiao_sub fr">
              <div class="go_back fl"><a href="/shopCartMgt/list/${stype}">返回购物车</a></div>
              <div class="fl go_rensub">
                <input type="submit" value="提交订单" id="submitOrder"/>
              </div>
            </div>
          </div>
        </div>
        <div class="blank1"></div>
      </div>
    </div>
  </div>
</div>    
<div class="blank"></div>
<script type="text/javascript" src="/statics/js/district.js"></script>
	<script type="text/javascript">
		$(function() {
			InitCity();
			//显示实付款
			if ('${stype}' == 2) {
				var p = parseFloat($("#totalPrice").val());
				if (p != '' && typeof(p) != 'undefined' && !isNaN(p)) {
					$("#showTotalPrice").html("￥"+parseFloat(p).toFixed(2));
				} else {
					$("#showTotalPrice").html("￥0.0");
				}
			} else {
				var totalprice = parseFloat(0);
				$("input[name=totalShopYoufei]").each(function(){
					totalprice += parseFloat($(this).val());
				});
				var p = parseFloat($("#totalPrice").val())+parseFloat(totalprice);
				if (p != '' && typeof(p) != 'undefined' && !isNaN(p)) {
					$("#showTotalPrice").html("￥"+parseFloat(p).toFixed(2));
				} else {
					$("#showTotalPrice").html("￥0.0");
				}
			}
			
			//无收货地址判断  
			if ("${fn:length(list)}" == 0) {
				openPopups('添加地址',null);
			}
			
			//更改收货地址
		/* 	$(".jbzl_shdz_yes_div_0").click(function() {
				if ($(this).parent().attr("caddrId") != $(".jbzl_shdz .jbzl_shdzsiz_yes").attr("caddrId")) {
					if (!confirm("确定要更改收货地址吗？")) {
						return;
					}
				} else {
					return;
				}
				$(".jbzl_shdz div").each(function(){
					if ($(this).attr("class") == "jbzl_shdzsiz_yes") {
						$(this).attr("class","jbzl_shdzsiz_no");
					}
				});
				var $selAddrDiv = $(this).parent("div");
				$selAddrDiv.attr("class","jbzl_shdzsiz_yes");

				$("#addr_Id").val($(this).parent().attr("caddrId"));
				document.forms[1].submit();
			}) */
			
			//提交订单
			$("#submitOrder").click(function() {
				var addrId = $(".jbzl_shdz .jbzl_shdzsiz_yes").attr("caddrId");
				if(addrId == "" || typeof(addrId) == "undefined"){
					alert("请选择收货地址!");
					return;
				}
				var $oj = $('input[name=scId]');
				if ($oj.length <= 0) {
					alert("商品已经下架，无法购买！");
					return;
				}
				if ($("select[name='yunfei']").find("option:selected").length <= 0 && '${stype}' != 2) {
					alert("未选择运送方式！");
					return;
				}
				$("#addrId").val(addrId);
				document.forms[0].action = "/shopCartMgt/orderSubmit";
				var orderArr = new Array();
				$oj.each(function(){
					var order = new Object;
					order.scId = $(this).val();
					order.companyId = $(this).parent().find("input[name=companyId]").val();
					order.productId = $(this).parent().find("input[name=prodId]").val();
					order.count = $(this).parent().find("input[name=prodCount]").val();
					order.shopWeiId = $(this).parent().find("input[name=shopWeiId]").val();
					order.stylesId = $(this).parent().find("input[name=styleId]").val();
					order.message = $("#message_"+$(this).parent().find("input[name=companyId]").val()).val();
					order.logisticsId = $("#logistics_"+$(this).parent().find("input[name=companyId]").val()+" option:selected").val();
					orderArr.push(order);
				});
				$("#orderJson").val($.toJSON(orderArr));
				document.forms[0].submit();
			});
			//收货地址鼠标特效
		  	$("[name=divaddress]").mouseover(function() {
				$(this).find("[name=setasdefault]").show();
				$(this).find("[name=mzh_mrdz]").show();

			}).mouseout(function() {
				$(this).find("[name=setasdefault]").hide(); 
				$(this).find("[name=mzh_mrdz]").hide();
				 
				
			}) 
		});

		/*========城市选择下拉（地址管理）===================*/
		function InitCity() {
			//初始化省市区列表
			var province = $("#selProvince option:selected").text();
			var city = $("#selCity option:selected").text();
			var area = $("#selDistrict option:selected").text();
			var dis = new district();
			dis.init('#selProvince', '#selCity', '#selDistrict');
			dis.bind(province, city, area);
		}
		//编辑、添加地址
		function openPopups(title, obj) {
			if (obj != null) {
				//修改
				var $divAddr = $(obj).parents("div[name=divaddress]");
				$("#txtcaddrId").val($divAddr.attr("caddrId"));
				$("#txtPhone").val($divAddr.find("[name=mobilePhone]").text());
				$("#txtLinkman").val($divAddr.find("[name=receiverName]").text());
				$("#selProvince option[value=" + $divAddr.find("[name=province]").val() + "]").attr("selected", true);
				$("#selProvince").change();
				$("#selCity option[value=" + $divAddr.find("[name=city]").val() + "]").attr("selected", true);
				$("#selCity").change();
				$("#selDistrict option[value=" + $divAddr.find("[name=district]").val() + "]").attr("selected", true);
				$("#txtDetailAdd").val($divAddr.find("[name=detailAddr]").text());
				$("#txtQQ").val($divAddr.find("[name=qq]").val());
				if ($divAddr.find("[name=isDefault]").val() == "1") {
					$("#checkisDef").attr("checked", true);
				} else {
					$("#checkisDef").attr("checked", false);
				}
				if ($divAddr.find("[name=isShopAddress]").val() == "1") {
					$("#checkisDef1").attr("checked", true);
				} else {
					$("#checkisDef1").attr("checked", false);
				}

			} else {
				$("#txtcaddrId").val("");
			}
			var pagei = $.layer({
				type : 1, //0-4的选择,0：信息框（默认），1：页面层，2：iframe层，3：加载层，4：tips层。
				btns : 2,
				btn : [ '确定', '取消' ],
				title : title,
				border : [ 0 ],
				closeBtn : [ 0 ],
				closeBtn : [ 0, true ],
				shadeClose : true,
				area : [ '700px', '520px' ],
				page : {
					dom : '#address_1'
				},
				yes : function(index) {
					//保存方法
					saveAddress();
				}
			});
		}

		function saveAddress() {
			var data = {
				caddrId : $("#txtcaddrId").val(),
				mobilePhone : $.trim($("#txtPhone").val()),
				receiverName : $.trim($("#txtLinkman").val()),
				province : $("#selProvince").val(),
				city : $("#selCity").val(),
				district : $("#selDistrict").val(),
				detailAddr : $.trim($("#txtDetailAdd").val()),
				qq : $.trim($("#txtQQ").val()),
				isDefault : $("#checkisDef").attr("checked") ? 1 : 0,
				isShopAddress : $("#checkisDef1").attr("checked") ? 1 : 0
			}
			if (!CheckData(data)) {
				return;
			}
			$.ajax({
				url : "/shopCartAjax/saveAddress",
				type : "post",
				async : false,
				data : data,
				contentType : "application/x-www-form-urlencoded; charset=utf-8",
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					alert("服务器出现异常");
				},
				success : function(data) {
					if (data.msg == "1") {
						addreload();
					} else {
						alert(data.msg);
					}

				}
			});
		}

		function CheckData(data) {
			if (data.mobilePhone == "") {
				alert("手机不能为空");
				return false;
			} else {
				var reg = /^1[34578]\d{9}$/;
				if (!reg.test(data.mobilePhone)) {
					alert("手机格式错误");
					return false;
				}
			}

			if (data.receiverName == "") {
				alert("收货人不能为空");
				return false;
			}
			if (data.province == "0") {
				alert("请选择省");
				return false;
			}
			if (data.city == "0") {
				alert("请选择市");
				return false;
			}
			if (data.detailAddr == "") {
				alert("详细地址不能为空");
				return false;
			}
			if (data.qq != "") { 
				var re = /[1-9][0-9]{4,}/;
				if (!re.test(data.qq)) {
					alert("QQ格式错误");
					return false;
				}
			}
			return true;
		}
		//设为默认
		function setDefaultAddr(caddrId) {
			$.ajax({
				url : "/shopCartAjax/setDefaultAddr",
				type : "post",
				contentType : "application/x-www-form-urlencoded; charset=utf-8",
				async : false,
				data : {
					caddrId : caddrId
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					alert("服务器出现异常");
				},
				success : function(data) {
					if (data.msg == "1") {
						addreload();
					} else {
						alert(data.msg);
					}

				}
			});
		}

		function deleteAddr(caddrId) {
			if (!confirm("确定要删除该地址吗？")) {
				return;
			}
			$.ajax({
				url : "/shopCartAjax/deleteAddr",
				type : "post",
				async : false,
				contentType : "application/x-www-form-urlencoded; charset=utf-8",
				data : {
					caddrId : caddrId
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					alert("服务器出现异常");
				},
				success : function(data) {
					if (data.msg == "1") {
						addreload();
					} else {
						alert(data.msg);
					}

				}
			});
		}

		function addreload() {
			document.forms[1].submit();
		}
		//选择运输方式
		function selYunfei(tis,cid){
			$("#showTotalShopPrice_"+cid).html("￥"+parseFloat(parseFloat($("#totalShopPrice_"+cid).val())+parseFloat($(tis).find("option:selected").attr("price"))).toFixed(2));
			var totalprice = parseFloat(0);
			$("select[name='yunfei']").each(function(){
				totalprice += parseFloat($(this).find("option:selected").attr("price"));
			});
			var p = parseFloat($("#totalPrice").val())+parseFloat(totalprice);
			if (p != '' && typeof(p) != 'undefined' && !isNaN(p)) {
				$("#showTotalPrice").html("￥"+parseFloat(p).toFixed(2));
			} else {
				$("#showTotalPrice").html("￥0.0");
			}
		}
		
	</script>
</body>
</html>
