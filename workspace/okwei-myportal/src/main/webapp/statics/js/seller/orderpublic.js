/**
 * 卖家公共js
 */
// 删除订单的弹窗
function deletewin(orderNo) {
	var pagei = $.layer({
		type : 1,
		btns : 2,
		btn : [ '是', '否' ],
		title : "删除订单",
		border : [ 0 ],
		closeBtn : [ 0 ],
		closeBtn : [ 0, true ],
		yes : function(index) {
			deleteByOrderNo(orderNo, index)
		},
		shadeClose : true,
		area : [ '500px', '280px' ],
		page : {
			dom : "#delete_ts"
		}
	});
}
function deleteByOrderNo(orderNo, index) {
	$.ajax({
		url : "orderajax",
		type : "post",
		dataType : "json",
		data : {
			"key" : "delete",
			"orderNo" : orderNo
		},
		error : function() {
			alert("异常！");
		},
		success : function(data) {
			if (data.msg == "1") {
				alert("删除成功");
			} else {
				alert(data.msg);
			}
			location.reload();
		}
	});
	layer.close(index);
}

// 拒绝订单
function refusedwin(orderNo) {
	var pagei = $.layer({
		type : 1,
		btns : 2,
		btn : [ '是', '否' ],
		title : "拒绝订单",
		border : [ 0 ],
		closeBtn : [ 0 ],
		closeBtn : [ 0, true ],
		yes : function(index) {
			refused(orderNo, index)
		},
		shadeClose : true,
		area : [ '500px', '280px' ],
		page : {
			dom : "#refused_ts"
		}
	});
}
function refused(orderNo, index) {
	$.ajax({
		url : "orderajax",
		type : "get",
		dataType : "json",
		data : {
			"key" : "refused",
			"orderNo" : orderNo
		},
		error : function() {
			alert("异常！");
		},
		success : function(data) {
			if (data.msg == "1") {
				alert("拒绝成功", true);
			} else {
				alert(data.msg);
			}
			location.reload();
		}
	});
	layer.close(index);
}

// 确认收货(退货收货)
function confirmcargowin(processId) {
	var pagei = $.layer({
		type : 1,
		btns : 2,
		btn : [ '我已确认收货', '点错了，取消' ],
		title : "确认收货",
		border : [ 0 ],
		closeBtn : [ 0 ],
		closeBtn : [ 0, true ],
		yes : function(index) {
			confirmByOrder(processId, index)
		},
		shadeClose : true,
		area : [ '500px', '280px' ],
		page : {
			dom : "#confirmcargo_ts"
		}
	});
}
function confirmByOrder(processId, index) {
	$("#refundsh").unbind("click");
	$.ajax({
		url : "orderajax",
		type : "get",
		dataType : "json",
		data : {
			"key" : "confirmcargo",
			"processId" : processId
		},
		error : function() {
			alert("异常！");
		},
		success : function(data) {
			if (data.msg == "1") {
				alert("确认成功");
			} else {
				alert(data.msg);
			}
			location.reload();
		}
	});
	layer.close(index);
}

// 支付尾款确认
function paymentwin(orderNo) {
	var pagei = $.layer({
		type : 1,
		btns : 2,
		btn : [ '是', '否' ],
		title : "确认支付尾款",
		border : [ 0 ],
		closeBtn : [ 0 ],
		closeBtn : [ 0, true ],
		yes : function(index) {
			payment(orderNo, index)
		},
		shadeClose : true,
		area : [ '500px', '280px' ],
		page : {
			dom : "#payment_ts"
		}
	});
}
function payment(orderNo, index) {
	$.ajax({
		url : "orderajax",
		type : "post",
		dataType : "json",
		data : {
			"key" : "finalpayment",
			"orderNo" : orderNo
		},
		error : function() {
			alert("异常！");
		},
		success : function(data) {
			if (data.msg == "1") {
				alert("确认成功");
			} else {
				alert(data.msg);
			}
			location.reload();
		}
	});
	layer.close(index);
}

// 同意退款
function agreedwin(processId) {
	var pagei = $.layer({
		type : 1,
		btns : 2,
		btn : [ '是', '否' ],
		title : "退款确认",
		border : [ 0 ],
		closeBtn : [ 0 ],
		closeBtn : [ 0, true ],
		yes : function(index) {
			agreed(processId, index)
		},
		shadeClose : true,
		area : [ '500px', '280px' ],
		page : {
			dom : "#agreed_ts"
		}
	});
}
function agreed(processId, index) {
	$("#agreerefundk").unbind("click");
	$.ajax({
		url : "orderajax",
		type : "get",
		dataType : "json",
		data : {
			"key" : "agreed",
			"processId" : processId
		},
		error : function() {
			alert("异常！");
		},
		success : function(data) {
			if (data.msg == "1") {
				alert("确认成功");
			} else {
				alert(data.msg);
			}
			location.reload();
		}
	});
	layer.close(index);
}

// ***不同意退款
function notagreedwin(processId) {
	var pagei = $.layer({
		type : 1,
		btns : 2,
		btn : [ '是', '否' ],
		title : "退款确认",
		border : [ 0 ],
		closeBtn : [ 0 ],
		closeBtn : [ 0, true ],
		yes : function(index) {
			notagreed(processId, index)
		},
		shadeClose : true,
		area : [ '500px', '280px' ],
		page : {
			dom : "#notagreed_ts"
		}
	});
}
function notagreed(processId, index) {
	var msg = $("#msg").val();// 不同意理由
	$.ajax({
		url : "orderajax",
		type : "post",
		dataType : "json",
		contentType : "application/x-www-form-urlencoded; charset=utf-8",
		data : {
			"key" : "notagreed",
			"processId" : processId,
			"msg" : msg
		},
		error : function() {
			alert("异常！");
		},
		success : function(data) {
			if (data.msg == "1") {
				alert("确认成功");
			} else {
				alert(data.msg);
			}
			location.reload();
		}
	});
	layer.close(index);
}

/* 确认收货地址 */
// -***- 确认收货地址
function okaddress(processId) {
	var cadid = $(".jbzl_shdz_yes").attr("caddrId");
	$.ajax({
		url : "orderajax",
		type : "get",
		dataType : "json",
		data : {
			"key" : "confirmsh",
			"processId" : processId,
			"cadid" : cadid
		},
		error : function() {
			alert("异常！");
		},
		success : function(data) {
			if (data.msg == "1") {
				alert("确认成功");
			} else {
				alert(data.msg);
			}
			location.reload();
		}
	});
}