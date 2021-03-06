<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ page import="java.util.ResourceBundle"%>
<%
    String walletdomain = ResourceBundle.getBundle("domain").getString("walletdomain");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>IBS支付收银台</title>
<link rel="stylesheet" href="/statics/css/glbdy.css" />
<link rel="stylesheet" href="/statics/css/index.css" />
<link rel="stylesheet" href="/statics/css/mzh_dd.css" />
<link rel="stylesheet" href="/statics/css/mzh_ibs.css" />
<script type="text/javascript" src="/statics/js/layer/layer.min.js"></script>
<script language="javascript">
//防止刷新处理
var _s="close";
window.onunload = function(){
   if(_s=="fresh")
   {
	  var totalprice=$("#totalPrice").val();
      $("#zhifujine").val(totalprice)   
      $("#walletzhifu").val("0");
      $("#weicoinzhifu").val("0");
      $("#paytype").val("");
   }
}
window.onbeforeunload = function(){
   _s="fresh";
}
</script>
<style>
.ddxq_gg_ddzt_xx_time {
padding-left: 15px;
background: url("../pic/time_3_27.png") no-repeat;
height: 14px;
color: #da0000;
}
</style>
<script type="text/javascript" src="/statics/js/cashier/cashier.js"></script>

</head>
<body>
	<input type="hidden" id="totalPrice" value="${order.totalPrice }" />
	<input type="hidden" id="wallets" value="${balance }" />
	<input type="hidden" id="zhifujine" value="${order.totalPrice }" />
	<input type="hidden" id="weicoins" value="${weicoin }"/>
	<input type="hidden" id="walletzhifu" value="0" />
	<input type="hidden" id="weicoinzhifu" value="0" />
	<input type="hidden" id="balancePrice" value="${order.totalPrice }"/>
	<input type="hidden" id="orderNo" value="${order.payOrderId }" />
	<input type="hidden" value="${surplus}" id="surplus">
	<input type="hidden" value="${payOrderInfoVO.cashAmount}" id="cashAmount">
	<input type="hidden" id="paytype" value="" />
	<input type="hidden" value="false" id="lastone1">
	<input type="hidden" value="${expirationTime}" id="extimes">
	<div class="content mar_au">
		<!--<div class="weiz_iam f12 fm_song">当前位置：<a href="#">供应管理</a>><span>IBS支付收银台</span></div>-->
		<div class="fr conter_right mzh_width_100">
			<div class="bor_si fl bg_white mzh_width_100">
				<div class="fl mzh_width_100 mt_30">
					<img src="/statics/images/d-ico1.png" width="28" class="fl mr_20 ml40"><span class="fl f16 fw_b lh_30">您的订单生成成功!</span>
					<div class="fl f14 lh_30 ml_20">
						<span class="color_red">${order.totalPrice }</span>元
					</div>
				</div>
				<div class="fl ml40 mb_10">
					<ul>
						<li class=" f14 ml_48">订单号：${order.payOrderId }</li>
						
						 <c:if test="${payOrderInfoVO.useCash==1 }"> 
							<li class=" f14 ml_48" style="padding-top: 5px">
							<input type="checkbox" onclick="clicke()"  name="lastone" id="lastone"><label for="lastone">可使用微店现金券抵减  ${payOrderInfoVO.cashAmount}元</label>
							</li>
							<li class=" f14 ml_48" style="padding-top: 5px">应付：							 	

							 <label class="money" >${order.totalPrice}</label>
						      元</li> 
						</c:if>
					</ul>
					
				</div>
				<c:if test="${isActivity==1 }">
					<div class="fl mb_20" style="width: 1072px;margin-left:128px;">
					请在 <b style="padding-left: 15px;background: url('../statics/images/time_3_27.png') no-repeat;height: 14px;color: #da0000;" id="countdown"> <script>
									countdown("${expirationTime}")
								</script>
								</b> 内付款，否则系统将取消订单
					</div>
					</c:if>
				</div>
				
			<div class="blank2"></div>
			<div class="bor_si fl bg_white mzh_width_100">
				<div class="Binding f14 fl">
					<ul>
						<li><c:if test="${isyz==1 }">
								<input type="checkbox" id="walletck"  class="fl mt_14 mr_5" checked />
							</c:if> <label for="wallet" class="fl"> 
							<span class="color_red">推荐</span>：微店钱包支付（账户余额：<span class="color_red">${balance }</span>元）
						</label> <a href="#" class="fl color_999 ml_10">余额不足？</a>
						 <a href="recharge" class="mzh_qcz ml_10 mt_10" style="color: #fff">去充值</a></li>

					
					</ul> 
					<c:if test="${isyz==1 }">
						<dl class="jbzl_dl f14" style="display: none; padding: 0% 2% 2%;" id="bor_walletl">
							<ul>
								
									<li style="line-height: 40px;" >
										<h3>
											抵扣金额：<label class="walletmoney color_red" >${balance }</label>元
										</h3>
									</li>
									<li style="line-height: 40%;" class="mt_10"><span>订单剩余金额：<label class="walletmoney1  color_red">${surplus }</label>元 请使用其他方式支付
									</span></li>
							</ul>
						</dl>
						<dl class="jbzl_dl f14" style="display: none; padding: 0% 2% 2%;" id="bor_walletg">
							<ul>
									<li style="line-height: 40px;">
										<h3>
											全额支付： <label class="walletmoney color_red f14" >${order.totalPrice }</label>元
										</h3>
									</li>
							</ul>
						</dl>
						</c:if>
					<ul>
					<c:if test="${payOrderInfoVO.useCash!=1 && weicoin>0}"> 
						<li>
								<input type="checkbox" id="weicoinck"   class="fl mt_14 mr_5"  />
							 <label for="weicoin" class="fl"> 
							微金币支付（账户余额：<span class="color_red">${weicoin }</span>元）
						</label> </li>		
					</c:if>
					</ul> 
						<dl class="jbzl_dl f14" style="display: none; padding: 0% 2% 2%;" id="bor_weicoinl">
							<ul>
								
									<li style="line-height: 40px;">
										<h3>
											抵扣金币：<span class="color_red">${weicoin }</span>元
										</h3>
									</li>
									<li style="line-height: 40%;" class="mt_10"><span>订单剩余金额：<label class="weicoinmoney1  color_red"></label>元 请使用其他方式支付
									</span></li>
							</ul>
						</dl>
						<dl class="jbzl_dl f14" style="display: none; padding: 0% 2% 2%;" id="bor_weicoing">
							<ul>
									<li style="line-height: 40px;">
										<h3>
											全币支付： <label class="weicoinmoney color_red f14" ></label>元
										</h3>
									</li>
							</ul>
						</dl>
					<ul>
						<li><span class="fl w80 tc bgcolor_889ffe color_fff">其他方式</span></li>
					</ul>
					<ul class="bg_F0F0F0">
						<li><input class="ml_10" type="radio" name="mzh_zf" id="kjzf" /> <label for="kjzf">快捷支付</label></li>
					</ul>
					<dl class="jbzl_dl f14 bor_si" id="kuaijie" style="display: none;">
						<ul>
							<c:forEach var="back" items="${list }">
						   <li><input class="fl mt_14 mr_5" type="radio"  onclick="clicke()"  name="mzh_zf_zf" id="backid${back.id }" value="${back.id }"> <label class="fl" for="backid${back.id }">${back.banckName } 尾号: ${back.banckCard}</label> <span class="fl ml_20">储蓄卡</span>
									<div class="fl dis_n" style="display: none;">
										<img src="/statics/images/u60.png" class="fl mt_5 ml_10">
									 
										<div class="fl ml_20"> 
										 支付： <label class="money2 color_red f14" >${surplus }</label>元
										</div>
										 
										<%-- <div class="fl ml_20"> 
											 支付：<span class="color_red f14"> ${surplus } +"---"+<label class="money" ></label></span>元
										</div> --%>
									</div></li>  
							</c:forEach>
							<c:if test="${empty list  }">
								<li>您还没有绑定银行卡！ <a href="<%=walletdomain%>/bankCardMgt/bankCard">马上绑定</a></li>
							</c:if>
						</ul>
					</dl>
					<ul>
						<li><input class="ml_10" type="radio" name="mzh_zf" id="wxzf" /> <label for="wxzf" class="color_000">微信支付</label></li>
					</ul>
					<!--<div class="jbzl_dl f14 bor_si" id="jbz2" style="display:none"></div>-->

<!-- 					<ul class="bg_F0F0F0">
						<li><input class="ml_10" type="radio" name="mzh_zf" id="bbzf" /> <label for="bbzf">百付宝支付</label></li>
					</ul> -->
					<ul>
						<li><input class="ml_10" type="radio" name="mzh_zf" id="cftzf" /> <label for="cftzf" class="color_000">财付通</label></li>
					</ul>		
				</div>
				<div class="fl mzh_width_100">
					<input name="button" type="submit" class="mzh_xiayibu ml_40 mb_30" value="下一步" onclick="next()" />
				</div>
			</div>
		</div>
	</div>

	<div class="updata_tixian layer_pageContent" style="display: none;" id="win_div_2">
		<div class="updata_tixian">
			<div class="fl mzh_width_100">
				<ul class="p10">
					<li>请输入您的微店钱包支付密码</li>
					<li class="fl mt_20">
						<div class="moile_ones fl tr fw_b" style="width: auto;">微店钱包支付密码：</div>
						<div class="fl">
							<input type="password" class="btn_h28 w82 dis_b fl" id="paypass" />
						</div>
					</li>
					<c:if test="${order.totalPrice>=200 && balance>=200 }">
						<li>
							<div class="fl mzh_width_100 bor_bo mt_20"></div>
						</li>
						<li class="fl mt_20">验证码已发至您的手机，请在下方填写验证码后完成支付</li>
						<li class="fl mt_20">
							<div class="moile_ones fl tr fw_b" style="width: auto;">手机验证码：</div>
							<div class="fl">
								<input type="password" class="btn_h28 dis_b fl" id="phonecode" style="width: 140px;">
							</div> <input name="button" type="button" id="sendsms" class="mzh_xiayibu ml_20" style="padding: 0px 20px;" value="发送验证码" onclick="sendSMS()" />
						</li>
					</c:if>
					<li class="fl"><input id="yzzf" name="button" type="submit" class="mzh_xiayibu mt_20" value="确认支付" onclick="verifySMS()" /> <span class="fl f14 color_red mt_20 ml_20" style="line-height: 26px; height: 26px; width: 150px;" id="tips"> </span></li>
				</ul>
			</div>
		</div>
	</div>

	<div class="updata_tixian layer_pageContent" style="display: none;" id="win_div_3">
		<div class="updata_tixian">
			<div class="fl mzh_width_100">
				<ul class="p10">
					<li class="f14">支付完成前，请不要关闭该窗口。</li>
					<li class="f14 mt_10">支付完成后，请根据您的情况点击下面的按钮：</li>
					<li class="fl mt_47"><input id="butzfwc" name="button" type="submit" class="mzh_xiayibu " style="padding: 0px 20px;" value="支付完成">
						<div class="dis_b ml_20 fl  btn_hui28_pre shou">支付遇到问题</div></li>
				</ul>
			</div>
		</div>
	</div>
	<form id="llpay" action="/llpay/payrequest" target="_blank">
		<input type="hidden" name="orderNo" value="${order.payOrderId }">
		<input type="hidden" name="cardID" value="">
	</form>
	<form id="wxpay" action="/wxpay/payrequest" target="_blank">
		<input type="hidden" name="orderNo" value="${order.payOrderId }">
	</form>
	<form id="bfbpay" action="/bfbpay/payrequest" target="_blank">
		<input type="hidden" name="orderNo" value="${order.payOrderId }">
	</form>
	<form id="tenpay" action="/tenpay/payrequest" target="_blank">
		<input type="hidden" name="orderNo" value="${order.payOrderId }">
	</form>
</body>
</html>