/**
 * 充值
 */
$(function() {
	$("#price").keyup(function() {
		var tmptxt = $(this).val();
		tmptxt = tmptxt.replace(/[^\d.]/g, ""); // 清除“数字”和“.”以外的字符
		tmptxt = tmptxt.replace(/^\./g, ""); // 验证第一个字符是数字而不是.
		tmptxt = tmptxt.replace(/\.{2,}/g, "."); // 只保留第一个. 清除多余的.
		if (tmptxt.indexOf(".") > 0 && tmptxt.length - tmptxt.indexOf(".") > 3) {
			tmptxt = tmptxt.substr(0, tmptxt.indexOf(".") + 3);
		}
		tmptxt = tmptxt.replace(".", "$#$").replace(/\./g, "").replace("$#$", ".");
		$(this).val(tmptxt);
		$("span.color_red").text(tmptxt);
	});

	$("#kuaijie [type=radio]").click(function() {
		$(this).siblings("div").show(300).parent().siblings().find("div.dis_n").hide();
		if ($("#price").val() != "") {
			// 选中，并且微钱包余额大于支付金额的时候
			$(this).siblings("div").find("div>span").text($("#price").val());
		} else {
			$(this).siblings("div").find("div>span").text(0);
		}
	});

	$("#butzfwc").click(function() {
		var orderNo = $("[name=orderNo]").val();
		// 检测是否支付完成
		$.ajax({
			url : "yzPayState",
			type : "post",
			async : false,
			data : {
				orderNo : orderNo
			},
			dataType : "json",
			success : function(data) {
				if (data.status == 1) {
					window.location.href = "success";
				} else {
					alert("支付失败，请重新支付");
					window.location.reload();
				}
			}
		});
	});
	$(".btn_hui28_pre").click(function() {
		window.location.reload();
	});
});

function next() {
	var price = $("#price").val();
	var cardid = $("[type=radio]:checked").val();
	if (cardid == null || cardid == "") {
		alert("请选择支付的卡号");
		return false;
	}
	if (price == null || price == "") {
		alert("请输入要充值的金额");
		return false;
	} else if (parseInt(price) < 1) {
		alert("充值金额最少1元");
		return false;
	}
	var flag = false;
	$.ajax({
		url : "rechargeajax",
		type : "post",
		async : false,
		data : {
			price : price
		},
		dataType : "json",
		success : function(data) {
			if (data.status == 1) {
				$("[name=orderNo]").val(data.msg);
				$("[name=cardID]").val(cardid);
				flag = true;
				// window.location.href = "/llpay/payrequest?orderNo=" +
				// data.msg + "&cardID=" + cardid;
			} else {
				alert(data.msg);
			}
		}
	});
	if (flag) {
		document.getElementById("llpay").submit();
		win3('支付', 'win_div_3', '514px', '260px');
	}
}

function win3(title, win_id, width, height) { // title 标题 win_id 弹窗ID width
	// 弹窗宽度 height 弹窗高度
	var pagei = $.layer({
		type : 1, // 0-4的选择,0：信息框（默认），1：页面层，2：iframe层，3：加载层，4：tips层。
		btns : 0,
		btn : [ '确定', '取消' ],
		title : title,
		border : [ 0 ],
		closeBtn : [ 0 ],
		closeBtn : [ 0, false ],
		shadeClose : false,
		area : [ width, height ],
		page : {
			dom : '#' + win_id
		},
		end : function() {
			$("#AddCount").hide()
		}
	});
}
