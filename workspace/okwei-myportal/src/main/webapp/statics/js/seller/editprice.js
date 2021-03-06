/**
 * 修改订单价格
 */
$(function() {

	// 优惠输入
	$(".youhui").keyup(function() {
		//alldecimal($(this));// 验证
		var editprice=$(this).val();
		var lowprice=$(this).parents(".xiugaiprc").find("input[name=lowprice]").val();
		
		calculate();
	}).blur(function() {
		validationdian($(this));// 验证
		calculate();
	});
	
});
// 点击确认
function submitajax(orderNo) {
	var proArr=new Array();
	$(".youhui").each(function(){
		var pro=new Object();
		pro.productOrderId=$(this).parents(".xiugaiprc").find("input[name=productorderid]").val();
		pro.price=$(this).val();
		proArr.push(pro);
	})
	
		$.ajax({
			url : "editPrice",
			type : "post",
			dataType : "json",
			data : {
				"supplyOrderid" : orderNo,
				"proArr" : JSON.stringify(proArr)
			},
			error : function() {
				alert("异常！");
			},
			success : function(data) {
				if (data.Statu == "Success") {
					alert("修改成功");
					location.href = "buylist";
				}
				else{
					alert(data.StatusReson);
					location.reload();
				}
			}

		})
	
}

// 点击取消
function cancel() {
	$("li input").val("");
}
/* 小数（正负都行）验证 */
function alldecimal($this) {
	var tmptxt = $this.val();
	tmptxt = tmptxt.replace(/[^\-\d.]/g, ""); // 清除“数字”和“.”以外的字符
	tmptxt = tmptxt.replace(/^\./g, ""); // 验证第一个字符是数字而不是.
	tmptxt = tmptxt.replace(/\.{2,}/g, "."); // 只保留第一个. 清除多余的.
	if (tmptxt.indexOf(".") > 0 && tmptxt.length - tmptxt.indexOf(".") > 3) {
		tmptxt = tmptxt.substr(0, tmptxt.indexOf(".") + 3);
	}
	if (tmptxt.indexOf("-") > 0) {
		tmptxt = tmptxt.replace(/\-/g, ""); // 只保留第一个. 清除多余的.
	}
	tmptxt = tmptxt.replace(".", "$#$").replace(/\./g, "").replace("$#$", ".");
	tmptxt = tmptxt.replace("-", "$#$").replace(/\-/g, "").replace("$#$", "-");
	$this.val(tmptxt);
}
// 正数，小数
function zhengdecimal($this) {
	var tmptxt = $this.val();
	tmptxt = tmptxt.replace(/[^\d.]/g, ""); // 清除“数字”和“.”以外的字符
	tmptxt = tmptxt.replace(/^\./g, ""); // 验证第一个字符是数字而不是.
	tmptxt = tmptxt.replace(/\.{2,}/g, "."); // 只保留第一个. 清除多余的.
	if (tmptxt.indexOf(".") > 0 && tmptxt.length - tmptxt.indexOf(".") > 3) {
		tmptxt = tmptxt.substr(0, tmptxt.indexOf(".") + 3);
	}
	tmptxt = tmptxt.replace(".", "$#$").replace(/\./g, "").replace("$#$", ".");
	$this.val(tmptxt);
}
// 验证最后一位
function validationdian($this) {
	var tmptxt = $this.val();
	if (tmptxt.indexOf(".") == (tmptxt.length - 1)) {
		tmptxt = tmptxt.replace(/\./g, ".0"); // 只保留第一个. 清除多余的.
	}
	$this.val(tmptxt);
}
// 计算
function calculate() {
	var original = $("#original").text();
	var freight = $("#freight").text();
	
	var temp = accAdd(accAdd(original, freight), -0.01);
	var allyouhui = 0;
	$(".youhui").each(function() {
		var count= $(this).parents(".xiugaiprc").find("input[name=productsCount]").val();
		allyouhui = accAdd(allyouhui, $(this).val()*count);
	});
	if (isNaN(allyouhui)) {
		return false;
	}
	$("#original").text(allyouhui)
	var totalprice = accAdd(freight, allyouhui);
	if (isNaN(totalprice)) {
		return false;
	}
	$("#totalprice").text(totalprice);
}
/** ***************** */
/**
 * * 加法函数，用来得到精确的加法结果 * 说明：javascript的加法结果会有误差，在两个浮点数相加的时候会比较明显。这个函数返回较为精确的加法结果。 * 调用：accAdd(arg1,arg2) * 返回值：arg1加上arg2的精确结果
 */
function accAdd(arg1, arg2) {
	var r1, r2, m, c;
	try {
		r1 = arg1.toString().split(".")[1].length;
	}
	catch (e) {
		r1 = 0;
	}
	try {
		r2 = arg2.toString().split(".")[1].length;
	}
	catch (e) {
		r2 = 0;
	}
	c = Math.abs(r1 - r2);
	m = Math.pow(10, Math.max(r1, r2));
	if (c > 0) {
		var cm = Math.pow(10, c);
		if (r1 > r2) {
			arg1 = Number(arg1.toString().replace(".", ""));
			arg2 = Number(arg2.toString().replace(".", "")) * cm;
		}
		else {
			arg1 = Number(arg1.toString().replace(".", "")) * cm;
			arg2 = Number(arg2.toString().replace(".", ""));
		}
	}
	else {
		arg1 = Number(arg1.toString().replace(".", ""));
		arg2 = Number(arg2.toString().replace(".", ""));
	}
	return (arg1 + arg2) / m;
}
//减法函数  
function Subtr(arg1, arg2) {  
    var r1, r2, m, n;  
    try {  
        r1 = arg1.toString().split(".")[1].length;  
    }  
    catch (e) {  
        r1 = 0;  
    }  
    try {  
        r2 = arg2.toString().split(".")[1].length;  
    }  
    catch (e) {  
        r2 = 0;  
    }  
    m = Math.pow(10, Math.max(r1, r2));  
     //last modify by deeka  
     //动态控制精度长度  
    n = (r1 >= r2) ? r1 : r2;  
    return ((arg1 * m - arg2 * m) / m).toFixed(n);  
}  

/**
 * * 乘法函数，用来得到精确的乘法结果 * 说明：javascript的乘法结果会有误差，在两个浮点数相乘的时候会比较明显。这个函数返回较为精确的乘法结果。 * 调用：accMul(arg1,arg2) * 返回值：arg1乘以 arg2的精确结果
 */
function accMul(arg1, arg2) {
	var m = 0, s1 = arg1.toString(), s2 = arg2.toString();
	try {
		m += s1.split(".")[1].length;
	}
	catch (e) {
	}
	try {
		m += s2.split(".")[1].length;
	}
	catch (e) {
	}
	return Number(s1.replace(".", "")) * Number(s2.replace(".", "")) / Math.pow(10, m);
}