<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>我的钱包-申请保证金退驻</title>
<style type="text/css">
.sel_wid {
	width: 700px;
	float: left;
	margin-left: 150px;
}

.wid_leftsl {
	width: 100px;
	height: 24px;
	line-height: 24px;
}

.wid_rightsl {
	width: 600px;
}

.wid_rightsl ul li {
	float: left;
	height: 24px;
	font-size: 14px;
	line-height: 24px;
	width: 600px;
	float: left;
}

.wid_rightsl ul li input {
	margin-right: 5px;
}

.wid_rightsl ul li a {
	margin-left: 16px;
}

.sc_botdnb {
	width: 110px;
	height: 24px;
	background: #f1f1f1;
	border: 1px solid #ddd;
}

.mr_10 {
	margin-right: 10px;
}

.uploadify {
	position: relative;
}

.uploadify object {
	left: 0px;
	top: 0px;
}
</style>
<script type="text/javascript" src="/statics/js/lib/uploadify/jquery.uploadify-3.1.min.js"></script>
<script type="text/javascript">
	var imgUrl = "http://img1.okimgs.com";
	$(function() {
		$("[name=bankcard]").change(function() {
			if ($(this).attr("isPublic") == "1") {
				$("#uploadiv").hide();
			} else {
				$("#uploadiv").show();
			}
		});
		$('#UpImageBack').uploadify({
			'buttonText' : '上传收款人正面照',
			'buttonClass' : 'browser',
			'dataType' : 'html',
			'removeCompleted' : false,
			'swf' : '/statics/js/lib/uploadify/uploadify.swf',
			'debug' : false,
			'width' : '130',
			'height' : '28',
			'auto' : true,
			'multi' : false,
			'fileTypeExts' : '*.jpg;*.gif;*.png;*.jpeg',
			'fileTypeDesc' : '图片类型(*.jpg;*.jpeg;*.gif;*.png)',
			'formData' : {
				'cate' : "1",
				'type' : "3"
			},
			'uploader' : 'http://fdfsservice.okwei.com/handle/UploadImg.ashx',
			'fileSizeLimit' : '2560',
			'progressData' : 'speed',
			'removeCompleted' : true,
			'removeTimeout' : 0,
			'requestErrors' : true,
			'onFallback' : function() {
				alert("您的浏览器没有安装Flash");
			},
			'onUploadStart' : function(file) {
				$(".uploadify-queue").css("height", "50px");
			},
			'onUploadSuccess' : function(file, data, response) {
				var json = JSON.parse(data);
				if (json.Status != 1) {
					alert(json.StatusReason);
				} else {
					$("#ImageBack").attr("src", imgUrl + json.ImgUrl);
					$("#ImageBack").show();
				}
			}
		});
		$('#UpImageFront').uploadify({
			'buttonText' : '上传授权证明',
			'buttonClass' : 'browser',
			'dataType' : 'html',
			'removeCompleted' : false,
			'swf' : '/statics/js/lib/uploadify/uploadify.swf',
			'debug' : false,
			'width' : '130',
			'height' : '28',
			'auto' : true,
			'multi' : false,
			'fileTypeExts' : '*.jpg;*.gif;*.png;*.jpeg',
			'fileTypeDesc' : '图片类型(*.jpg;*.jpeg;*.gif;*.png)',
			'formData' : {
				'cate' : "1",
				'type' : "3"
			},
			'uploader' : 'http://fdfsservice.okwei.com/handle/UploadImg.ashx',
			'fileSizeLimit' : '2560',
			'progressData' : 'speed',
			'removeCompleted' : true,
			'removeTimeout' : 0,
			'requestErrors' : true,
			'onFallback' : function() {
				alert("您的浏览器没有安装Flash");
			},
			'onUploadStart' : function(file) {
				$(".uploadify-queue").css("height", "50px");
			},
			'onUploadSuccess' : function(file, data, response) {
				var json = JSON.parse(data);
				if (json.Status != 1) {
					alert(json.StatusReason);
				} else {
					$("#ImageFront").attr("src", imgUrl + json.ImgUrl);
					$("#ImageFront").show();
				}
			}
		});
	});
	//提交申请
	function SubmitApplyBond() {
		var tid = $("#tid").val();
		var obj = $("[name=bankcard]:checked");
		var cardid = obj.attr("cardID");
		var ispub = obj.attr("isPublic");
		if (cardid == null) {
			alert("请选择银行卡");
			return;
		}
		var imageback = "";
		var imagefront = "";
		if (ispub != "1") {
			imageback = $("#ImageBack").attr("src");
			imagefront = $("#ImageFront").attr("src");
			if (imageback == "") {
				alert("请上传收款人正面照");
				return;
			}
			if (imagefront == "") {
				alert("请上传授权证明");
				return;
			}
			if (imageback != "") {
				imageback = imageback.replace(imgUrl, "");
			}
			if (imageback != "") {
				imagefront = imagefront.replace(imgUrl, "");
			}
		}
		if (!$("#Ican").is(":checked")) {
			alert("请勾选“我同意《退驻服务协议》”");
			return;
		}
		$.ajax({
			url : "/walletMgt/SubmitApplyBond",
			async : false,
			type : "post",
			data : {
				tid : tid,
				cardid : cardid,
				ispub : ispub,
				imageback : imageback,
				imagefront : imagefront,
				type : $("#htype").val()
			},
			success : function(data) {
				if (data.state == "Success") {
					alert("申请提交成功", true);
					location.reload();
				} else {
					alert(data.message);
				}
			}
		});
	}

	function resubmit() {
		$("#dhul").attr("class", "gchjz_bg1");
		$("#dhul li").removeClass("red");
		$("#dhul li").eq(0).addClass("red");
		$("#divbtg").hide();
		$("#divapply").show();
		var ispub = $("[name=bankcard]:checked").attr("isPublic");
		if (ispub == "0") {
			$("#uploadiv").show();
		}
	}

	function cancelapply() {
		$.ajax({
			url : "/walletMgt/cancelapply",
			async : false,
			type : "post",
			data : {
				tid : $("#tid").val(),
				type : $("#htype").val()
			},
			success : function(data) {
				if (data.state == "Success") {
					alert("取消成功", true);
					location.href = "/bondMgt/bondList";
				} else {
					alert(data.message);
				}
			}
		});
	}
	
	var selcard = $("[name=bankcard]:checked");
	if (selcard.attr("isPublic") != "1") {
		$("#uploadiv").show();
	}
</script>
</head>
<body>
	<div class="fl conter_right mt_20  bg_white bor_si">
		<div class="wdqb_xxk ndfxs_1-2_border">
			<div class="tab_pc f16">
				<li class="now"><a href="javascript:;" style="color: #333;">保证金</a><i></i></li>
			</div>
		</div>
		<!-- 申退内容 -->
		<div class="se_tuimon">
			<!-- 申退进度条 -->
			<c:choose>
				<c:when test="${bondinfo.state==-1 }">
					<div id="dhul" class="gchjz_bg1">
						<ul>
							<li class="red">确认账户</li>
							<li>等待审核</li>
							<li>审核结果</li>
							<li>已退驻</li>
						</ul>
					</div>
				</c:when>
				<c:when test="${bondinfo.state==0 }">
					<div id="dhul" class="gchjz_bg2">
						<ul>
							<li class="red">确认账户</li>
							<li class="red">等待审核</li>
							<li>审核结果</li>
							<li>已退驻</li>
						</ul>
					</div>
				</c:when>
				<c:when test="${bondinfo.state==1 || bondinfo.state==2 }">
					<div id="dhul" class="gchjz_bg3">
						<ul>
							<li class="red">确认账户</li>
							<li class="red">等待审核</li>
							<li class="red">审核结果</li>
							<li>已退驻</li>
						</ul>
					</div>
				</c:when>
				<c:when test="${bondinfo.state==3 }">
					<div id="dhul" class="gchjz_bg4">
						<ul>
							<li class="red">确认账户</li>
							<li class="red">等待审核</li>
							<li class="red">审核结果</li>
							<li class="red">已退驻</li>
						</ul>
					</div>
				</c:when>
			</c:choose>
			<input id="tid" type="hidden" value="${bondinfo.tid }" /> <input id="htype" type="hidden" value="${bondinfo.type }" />
			<div class="blank"></div>
			<c:choose>
				<c:when test="${bondinfo.state==-1 || bondinfo.state==2 }">
					<div id="divapply" style="<c:if test="${bondinfo.state==2 }">display: none;</c:if>">
						<div class="tix_title">
							为保障消费者权益，保证金提取后，<span>你的产品将无法继续销售</span>
						</div>
						<div class="blank"></div>
						<div class="sel_wid">
							<div class="fl wid_leftsl f14">选择银行卡：</div>
							<div class="fl wid_rightsl">
								<ul>
									<c:forEach var="bank" items="${banklist }" varStatus="vs">
										<li><input cardID="${bank.cardID }" isPublic="${bank.isPublic }" <c:if test="${bank.isSelect==1 }">checked="checked"</c:if> name="bankcard" id="bankno${vs.index }" type="radio" /><label for="bankno${vs.index }"><c:if test="${bank.isPublic==1 }">对公账户</c:if> ${bank.bankName } 尾号：${bank.cardNo }</label></li>
									</c:forEach>
									<li><a href="/bankCardMgt/bankCard" class="ft_lan">添加账户</a></li>
								</ul>
							</div>
						</div>
						<div class="clear"></div>
						<div id="uploadiv" style="display: none;">
							<div class="sel_wid ft_c9 f12 mt_20">选择个人账户，需上传银行卡持卡人的正面照，以及公司授权该人为收款人的授权证明</div>
							<div class="clear"></div>
							<div class="sel_wid mt_10">
								<div class="fl wid_leftsl f14">上传凭证：</div>
								<div class="fl wid_rightsl">
									<div class="fl mr_10">
										<img id="ImageBack" src="${bondinfo.imageBack }" width="100" height="120" style="<c:if test="${bondinfo.imageBack==null||bondinfo.imageBack=='' }">display: none;</c:if>" />
									</div>
									<div class="fl mr_10 btn_wear30_pre shou w130">
										<div id="UpImageBack">上传收款人正面照</div>
									</div>
									<div class="fl mr_10">
										<img src="http://base.okwei.com/images/xh_image0920004-1.jpg" />
									</div>
									<div class="fl w222 ft_c9">
										参考示例：负责人及身份证上的所有信息清晰可见，必须能看清证件号。<br /> 照片需免冠，建议未化妆，手持证件人的五官清晰可见。<br /> 照片内容真实有效，不得做任何修改。<br /> 支持.jpg .jpeg .bmp .gif格式照片，大小不超过2.5M。
									</div>
								</div>
							</div>
							<div class="clear"></div>
							<div class="sel_wid mt_10">
								<div class="fl wid_leftsl f14"></div>
								<div class="fl wid_rightsl">
									<div class="fl mr_10">
										<img id="ImageFront" src="${bondinfo.imageFront }" width="100" height="120" style="<c:if test="${bondinfo.imageFront==null||bondinfo.imageFront=='' }">display: none;</c:if>" />
									</div>
									<div class="fl mr_10 btn_wear30_pre shou w130">
										<div id="UpImageFront">上传授权证明</div>
									</div>
									<div class="fl mr_10">
										<a target="_blank" href="/statics/pic/img_slitus.jpg"><img src="/statics/pic/img_slitus.jpg" width="100" height="120" /></a>
									</div>
									<div class="fl w222 ft_c9">
										<a href="http://xue.okwei.com/forum.php?mod=viewthread&tid=72&extra=page%3D1" class="ft_lan">点击下载授权证明模板>></a><br /> 参考示例：授权书内容清晰可见，必须能看盖章。若无法提供公章，可按手印代替。<br /> 可上传扫描件或照片，内容真实有效，不得做任何修改。<br /> 支持.jpg .jpeg .bmp .gif格式照片，大小不超过2.5M。
									</div>
								</div>
							</div>
						</div>
						<div class="clear"></div>
						<div class="sel_wid mt_10">
							<div class="fl wid_leftsl f14"></div>
							<div class="fl wid_rightsl">
								<ul>
									<li><input type="checkbox" id="Ican" /><label for="Ican">我同意<a target="_blank" href="http://xue.okwei.com/forum.php?mod=viewthread&tid=20&extra=" class="ft_lan" style="margin-left: 0;">《退驻服务协议》</a></label></li>
									<li class="mt_30"><a href="javascript:SubmitApplyBond();" class="btn_wear30_pre dis_b fl" style="margin-left: 0;">提交申请</a></li>
								</ul>
							</div>
						</div>
					</div>
					<div id="divbtg" style="<c:if test="${bondinfo.state==-1 }">display: none;</c:if>">
						<div class="blank"></div>
						<div class="bank_idcard">
							<p class="tc f16">审核不通过</p>
							<p class="tc f14 mt_10">不通过原因：${bondinfo.reason }</p>
							<p class="tc f12 mt_20">申请时间：${bondinfo.time }</p>
							<p class="tc f12 mt_10">审核时间：${bondinfo.cltime }</p>
							<p class="tc f12 mt_10">提款账户：${bondinfo.account }</p>
							<div>

								<div class="btn_wear42_pre fl mt_20" style="margin-left: 100px;" onclick="cancelapply()">取消申请</div>
								<div class="btn_wear42_pre fl mt_20" style="margin-left: 60px;" onclick="resubmit()">重新提交</div>
							</div>

						</div>
					</div>
				</c:when>
				<c:when test="${bondinfo.state==0 }">
					<div class="blank"></div>
					<div class="bank_idcard">
						<p class="tc f16">提取保证金申请已提交</p>
						<p class="tc f14 mt_10">系统将在15天内处理该申请，请耐心等待</p>
						<div class="btn_wear42_pre mt_20" style="margin-left: 201px;" onclick="cancelapply()">取消申请</div>
						<p class="tc f12 mt_30">申请时间：${bondinfo.time }</p>
						<p class="tc f12 mt_10">提款账户：${bondinfo.account }</p>
					</div>
				</c:when>
				<c:when test="${bondinfo.state==1}">
					<div class="blank"></div>
					<div class="bank_idcard">
						<p class="tc f16">审核通过</p>
						<p class="tc f14 mt_10">提取保证金申请已通过，系统将在45天内为您打款</p>
						<p class="tc f14 mt_10">
							保证金打款金额：${bondinfo.amount }元
							<c:if test="${not empty bondinfo.detailpath && bondinfo.detailpath!='' }">
								<a href="${bondinfo.detailpath }" class="ml_30 ft_lan">下载明细</a>
							</c:if>
						</p>
						<p class="tc f12 mt_30">申请时间：${bondinfo.time }</p>
						<p class="tc f12 mt_10">审核时间：${bondinfo.cltime }</p>
						<p class="tc f12 mt_10">提款账户：${bondinfo.account }</p>
					</div>
				</c:when>
				<c:when test="${bondinfo.state==3}">
					<div class="blank"></div>
					<div class="bank_idcard">
						<p class="tc f16">
							<c:choose>
								<c:when test="${bondinfo.type==1}">工厂号供应商已退驻</c:when>
								<c:when test="${bondinfo.type==2}">批发号供应商已退驻</c:when>
							</c:choose>
						</p>
						<p class="tc f14 mt_30">
							保证金打款金额：${bondinfo.amount }元
							<c:if test="${not empty bondinfo.detailpath && bondinfo.detailpath!='' }">
								<a href="${bondinfo.detailpath }" class="ml_30 ft_lan">下载明细</a>
							</c:if>
						</p>
						<p class="tc f12 mt_30">退驻时间：${bondinfo.paytime }</p>
						<p class="tc f12 mt_10">提款账户：${bondinfo.account }</p>
						<a target="_blank" href="${bondinfo.url }"><div class="btn_wear42_pre mt_30 w189" style="margin-left: 150px;">重新申请为供应商</div></a>
					</div>
				</c:when>
			</c:choose>
			<div class="blank"></div>
			<div class="blank"></div>
			<div class="blank"></div>
			<div class="zmwl_gys_xh">
				<div class="left_rzy_xh">
					<div class="left_rzy_xh_yi">您的专属上级认证员，任何进驻问题可以及时联系TA</div>
					<div class="left_rzy_xh_er">
						<div class="left_rzy_xh_erasp">
							<img src="${verifier.photo }" />
						</div>
						<div class="left_rzy_xh_erjsp">
							<ul>
								<li>身份：${verifier.name }（${verifier.cwei }）</li>
								<li>微店号：${verifier.weiid }</li>
								<li>手机号：${verifier.phone }<span><a target="_blank" href="http://wpa.qq.com/msgrd?v=3&uin=${verifier.qq }&site=qq&menu=yes" title="QQ:${verifier.qq }"><img src="/statics/pic/xh_image09200013.jpg" /></a></span></li>
							</ul>

						</div>
					</div>
				</div>
				<div class="right_rzy_xh">
					<div class="left_rzy_xh_yi">您的辅导顾问，如有特殊情况，可以与她联系</div>
					<div class="left_rzy_xh_er">
						<div class="left_rzy_xh_erasp">
							<img src="${verifier.gwphoto }" />
						</div>
						<div class="left_rzy_xh_erjsp">
							<ul>
								<li>身份：${verifier.gwname }（${verifier.gwcwei }）</li>
								<li>微店号：${verifier.gwweiid }</li>
								<li>手机号：${verifier.gwphone }<span><a target="_blank" href="http://wpa.qq.com/msgrd?v=3&uin=${verifier.gwqq }&site=qq&menu=yes" title="QQ:${verifier.gwqq }"><img src="/statics/pic/xh_image09200013.jpg" /></a></span></li>
							</ul>

						</div>
					</div>
				</div>
			</div>
			<div class="blank"></div>
		</div>
	</div>
</body>
</html>