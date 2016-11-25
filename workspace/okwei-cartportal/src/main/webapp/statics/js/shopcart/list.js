﻿$(function(){
//	var type = $('#stype').val();

	//铺货单不能和其它单一起结算提示，满足落地店提示
	showPrompt();
	//绑定结算
	jisuanProd();
	/****** 12-1（购物车）购物车 ******/
	if ($("#jiesuan_div").attr("class") == "wind_two_no fl") {
		var $selProd = $('input[name=product_cb]:checked');
		if ($selProd.length <= 0) {
			$("#jiesuan_div").find("#jiesuan").attr("id","jiesuan1");
		} else {
			$("#jiesuan_div").attr("class","wind_two_yes fl")
		}
	}
	//鼠标放上
	$("[name=mzh_hover]").hover(function(){
		if($(this).attr("ishover") ==0){
			$(this).find("div[name=mzh_fangshang]").attr("class","comm_bianjno");
			$(this).attr("ishover","1");
		}
	},function(){
		if($(this).attr("ishover") ==1){
			$(this).find("div[name=mzh_fangshang]").attr("class","comm_bianjno_mr");
			$(this).attr("ishover","0");
		}
	})
	
	$("[name=mzh_click]").mouseover(function(){
		$(this).parent().attr("class","comm_bianjyes");
	})
	$(".img_bianji").mouseout(function(){
		if($(this).parent().parent().parent().attr("ishover") == 1){
			$(this).parent().attr("class","comm_bianjno")
		}
	});
	//点击编辑-出弹出层
	$(".img_bianji").live("click",function(){
		//productId
		var productId = $(this).attr("productId");
		//styleId
		var styleId = $(this).attr("styleId");
		var isActivity=$(this).attr("isactivity");
		var aprice=$(this).attr("activityprice");
		$("#sel_style_"+$(this).attr("scid")).val(styleId);
		getProductStyleParamList(productId,styleId,isActivity,aprice,"/shopCartMgt/getProductStyleParamAjax",$(this));
		if(isActivity ==1)
		{
			$("[name=mzh_ok]").attr("price",aprice);
		}
		else
		{
			$("[name=mzh_ok]").attr("price",$(this).attr("price"));
		}
	});
	//弹出层-颜色-点击
	$(".ones_color li").live("click",function(){
		var $this = $(this); 		
		if ($this.attr("class") == "bor_huisel" || $this.attr("class") == "bor_redsel") {
			return;
		}
		$("[name="+$this.attr('name')+"]").attr("class","bor_heisel");
		$this.attr("class","bor_redsel");
		//查询其他属性的值
		var data = {
				productId : $this.attr("productId"),
				attrId : $this.attr("attrId"),
				keyId : $this.attr("keyId")
			}
		$.ajax({
			url : "/shopCartAjax/checkProdStyles",
			type : "post",
			dataType:"json",
			async : false,
			data : data,
			contentType : "application/x-www-form-urlencoded; charset=utf-8",
			error : function(XMLHttpRequest, textStatus, errorThrown) {
				alert("服务器出现异常");
			},
			success : function(data) {
				if (data != '' && typeof(data) != 'undefined') {
					$.each(data, function (n, v) {  
			              $("[scid="+$this.attr("scid")+"]").each(function(){
			            	  var $ts = $(this);
			            	  if (v.attrId == $ts.attr("attrid")) {
			            		  if (v.keyIds.indexOf($ts.attr("keyid"))>=0) {
			            			  if ($ts.attr("class") != "bor_redsel") {
			            				  $ts.attr("class","bor_heisel");
		            				  }
								  } else {
									  $ts.attr("class","bor_huisel");
								  }
							  }
			              });
			        });
				}
			}
		});
		
		var redSx = $(".bor_redsel[scid="+$this.attr("scid")+"]");
		if (redSx.length == $this.parent("ul").attr("sxCount")) {
			var styleArr = new Array();
			redSx.each(function(){
				var $red = $(this);
				var style = new Object();
				style.productId = $red.attr("productId");
				style.attrId = $red.attr("attrId");
				style.keyId = $red.attr("keyId");
				style.source = $red.attr("source");
				styleArr.push(style);
			});
			
			var data = {
					styleJson : $.toJSON(styleArr)
				}
			
			$.ajax({
				url : "/shopCartAjax/getProdStylePrice",
				type : "post",
				dataType:"json",
				async : false,
				data : data,
				contentType : "application/x-www-form-urlencoded; charset=utf-8",
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					alert("服务器出现异常");
				},
				success : function(data) {
					$("#styleprice_"+$this.parent("ul").attr("scid")).html(data.price);
					$("#sel_style_"+$this.parent("ul").attr("scid")).val(data.styleId);
					if (data.img != '' && typeof(data.img) != 'undefined') {
						$("#sel_img_"+$this.attr("scid")).attr('src',data.img);
					}
					$("[name=mzh_ok]").attr("price",data.price);
					
				}
			});
		} else {
			$("#prod_nowprice_"+$this.parent("ul").attr("scid")).html("");
		}
	})
	//修改款式
	$("[name=mzh_ok]").live("click",function() {
		var $this = $(this);
		var isActivity=isNull($this.attr("isactivity")) == true ? 0 : $this.attr("isactivity");
		var aprice=$(this).attr("activityprice");
		if ($("#prod_style_"+$this.attr("scid")).val() != $("#sel_style_"+$this.attr("scid")).val() && $("#sel_style_"+$this.attr("scid")).val() != '0') {
			var styleId = $("#sel_style_"+$this.attr("scid")).val();
			var data = {
					styleId:styleId,
					scId:$this.attr("scid"),
					source:$this.attr("source")
				}
			$.ajax({
				url : "/shopCartAjax/modifyProdStyle",
				type : "post",
				dataType:"json",
				async : false,
				data : data,
				contentType : "application/x-www-form-urlencoded; charset=utf-8",
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					alert("服务器出现异常");
				},
				success : function(data) {
					location.reload() ;
					var type = isNull($this.attr("buyType") ) == true ? "-1" : $this.attr("buyType") ;
					if (data.styleId == 0) {
						$("#prodDiv_"+$this.attr("scid")).remove();
						jisuanProd();
						$this.parent().parent().parent().parent().parent().find(".img_bianji").attr("styleId",styleId);
						
					} 
					else 
					{
						if (data.img != '' && typeof(data.img) != 'undefined') {
							$("#prod_img_"+$this.attr("scid")).attr('src',data.img);
						}
						$("#prod_style_"+$this.attr("scid")).val(data.styleId);
						//属性html
						var propertyHtml = "";
						var propertyList = eval(data.property);
						$.each(propertyList, function (index)
						{   
							//keyName
							if(isNull(propertyList[index].attributeName) == true || propertyList[index].attributeName == "-1")
							{
								propertyHtml += "<span>默认</span>";
							}
							else{
								propertyHtml +="<span>"+propertyList[index].attributeName+"</span>";
							}
							//valueName
							if(isNull(propertyList[index].keyIdName) || propertyList[index].keyIdName == "-1")
							{
								propertyHtml += "<span>:默认</span>";
							}
							else
							{
								propertyHtml += "<span>:"+propertyList[index].keyIdName+"</span>";
							}
							propertyHtml +="</br>"
						});
						$("#property_"+$this.attr("scid")).html(propertyHtml);
//						if (type == 1) {
							//alert($this.attr("price"));
							var thisPrice =0.0;
							if(isActivity==1)
							{	
								thisPrice = isNull($this.attr("activityprice")) == true ? parseFloat(0.0) : parseFloat($this.attr("activityprice"));
								$("#nowPrice_"+$this.attr("scid")).find("lable").html($this.attr("price"));
							}
							else
							{
								thisPrice = isNull($this.attr("price")) == true ? parseFloat(0.0) : parseFloat($this.attr("price"));
							}
							$("#nowPrice_"+$this.attr("scid")).find("font").html(thisPrice);
							var totalPrice = parseFloat(thisPrice * parseInt($("#prod_count_"+$this.attr("scid")).val())).toFixed(2);
							$("#totalPrice_"+$this.attr("scid")).html(totalPrice);
//						}
						//给checkbox赋值price
						$this.parent().parent().parent().parent().parent().parent().prev().prev().prev().children().attr("price",totalPrice);
						//给编辑赋值价格
						$this.parent().parent().parent().parent().parent().find(".img_bianji").attr("price",thisPrice);
						jisuanProd();
						//提示
						showPrompt();
						//编辑框隐藏
						editStyleShowOrHide($this);
						//更新款式styleId
						$this.parent().parent().parent().parent().parent().find(".img_bianji").attr("styleId",styleId);
						//更新styleId
						$this.parent().parent().parent().parent().parent().parent().prev().prev().prev().children().attr("styleId",styleId);
					}
				}
			});
		}
		else //不修改任何东西直接关闭
		{
			//编辑框隐藏
			editStyleShowOrHide($this);
		}
	});
	
	//弹出层-尺码-点击
	$("[name=mzh_size]").live("click",function(){
		$("[name=mzh_size]").attr("class","bor_heisel");
		$(this).attr("class","bor_redsel");
	})
	//弹出层-取消
	$("[name=mzh_close]").live("click",function(){
		$(".elect_color").hide();
		$("[name=mzh_fangshang]").attr("class","comm_bianjno_mr");
		$("[name=mzh_hover]").attr("ishover","0");
	})
	//减
	$("[name=mzh_jian]").live("click",function(){
	    if($(this).attr("state") != "1")
	    {
	    	return false;
	    }
		var $jian = parseInt($(this).next().find('input').val());
		if($jian > 1){
			$jian = $jian - 1;
			$(this).next().find('input').val($jian);
			$(this).next().find('input').trigger('change');
		}
	})
	//加
	$("[name=mzh_jia]").live("click",function(){
	    if($(this).attr("state") != "1")
	    {
	    	return false;
	    }
		var $jia = parseInt($(this).prev().find('input').val());
		$jia = $jia + 1;
		$(this).prev().find('input').val($jia);
		$(this).prev().find('input').trigger('change');
	}) 
	//商品数量输入框控制非法输入
	$("input:[name=prodCountText]").bind("keyup",function(){    
		$(this).val($(this).val().replace(/\D|^0/g,''));  
	}).bind("paste",function(){  //CTR+V事件处理     
		$(this).val($(this).val().replace(/\D|^0/g,''));     
	}).bind("change",function(){  //
		$tcount = $(this);
		var type = isNull($(this).attr("buyType")) == true ? "-1" : $(this).attr("buyType");
		var result = modifyProdCount($tcount.val(),$tcount.attr("scid"),$tcount.attr("sellerWeiId"));
		if(result == true)
		{   
			$(this).attr("count",$(this).val());
			if (type == '3') {
				var productId = isNull($(this).attr("productId")) == true ? "-1" : $(this).attr("productId");
				var supplierWeiId = isNull($(this).attr("supplierWeiId")) == true ? "-2" : $(this).attr("supplierWeiId");
				var productCount = 0;
				//这产品的批发价
				//统计该供应商的这个产品的个数
				$("input[name=prodCountText]").each(function(){ 
					var secondProductId = $(this).attr("productId");
					var secondSupplierWeiId = $(this).attr("supplierWeiId");
					if(productId == secondProductId && supplierWeiId && secondSupplierWeiId)
					{	
						productCount += parseInt(isNull($(this).val()) == true ? 0 : $(this).val());
					}
				});
				var wholesalePriceList = eval($(this).attr("wholesalePriceListJson"));
				//批发价
				var price = getWholesalePrice(wholesalePriceList,productCount)
				if(parseFloat(price) <= parseFloat(0) )
				{
					price = isNull($(this).attr("price")) == true ? parseFloat(0) : parseFloat($(this).attr("price"));
				}
				//找到这个该供应商的所有款式
				$("input[name=prodCountText]").each(function(){ 
					var secondProductId = $(this).attr("productId");
					var secondSupplierWeiId = $(this).attr("supplierWeiId");
					if(productId == secondProductId && supplierWeiId && secondSupplierWeiId)
					{	
						var scid = $(this).attr("scid");
						var thisCount = isNull($(this).val()) == true ? 0 : $(this).val();
						$(this).parent().parent().parent().parent().parent().find("#nowPrice_"+scid).find("font").html(price);
						$(this).parent().parent().parent().parent().parent().find("#totalPrice_"+scid).html(price * thisCount);
					}
				});
//					var tcount = parseFloat($tcount.val());
//					var $selProd = $('input[name=product_cb]:checked');
//					$selProd.each(function(){
//						var a = $('[name=prodCountText][scid='+$(this).val()+']');
//						if (a.attr('productId') == $tcount.attr('productId') && $tcount.attr('scid') != a.attr('scid')) {
//							tcount = parseFloat(tcount) + parseFloat(a.val());
//						}
//					});
//					var proPrice = '0.0';
//					$('[name=pifajia_'+$tcount.attr("scid")+']').each(function(){
//						if (parseFloat(tcount) >= parseFloat($(this).val())) {
//							proPrice = $(this).attr('price');
//						} 
//					});
//					if (proPrice == '0.0') {
//						proPrice = $("#prod_batchprice_"+$tcount.attr('scid')).val();
//					}
//					if (proPrice != '0.0') {
//						$("#nowPrice_"+$tcount.attr('scid')).html(proPrice);
//						$("#totalPrice_"+$tcount.attr('scid')).html((parseFloat($("#nowPrice_"+$tcount.attr('scid')).html())*parseFloat($('[name=prodCountText][scid='+$tcount.attr('scid')+']').val())).toFixed(2));
//						var $selProd = $('input[name=product_cb]:checked');
//						$selProd.each(function(){
//							$("#nowPrice_"+$(this).val()).html(proPrice);
//							$("#totalPrice_"+$(this).val()).html((parseFloat($("#nowPrice_"+$(this).val()).html())*parseFloat($('[name=prodCountText][scid='+$(this).val()+']').val())).toFixed(2)); 
//						});
//					}
				//更新页面批发价
			} else
			{
				
				$("#totalPrice_"+$tcount.attr("scid")).html((parseFloat($("#nowPrice_"+$tcount.attr("scid")).find("font").html())*parseFloat($tcount.val())).toFixed(2)); 
				$(this).parent().parent().parent().parent().parent().find("[name=product_cb]").attr("price",(parseFloat($("#nowPrice_"+$tcount.attr("scid")).find("font").html())*parseFloat($tcount.val())).toFixed(2));
			} 
//			jisuanProd(type);
//			jsPrice();
			//绑定结算
			jisuanProd();
			//铺货单不能和其它单一起结算提示，满足落地店提示
			showPrompt();


			
		}
		else
		{
			$(this).val($(this).attr("count"));
		}
		
	}).css("ime-mode", "disabled"); //CSS设置输入法不可用
	//计算价格
	function jsPrice(){
//		var $selProd = $('input[name=product_cb]:checked');
//		$selProd.each(function(){
//			var $tcount = $(this);
//			var tcount = parseFloat(0);
//			$selProd.each(function(){
//				var a = $('[name=prodCountText][scid='+$(this).val()+']');
//				if (a.attr('productId') == $tcount.attr('productId')) {
//					tcount = parseFloat(tcount) + parseFloat(a.val());
//				}
//			});
//			var proPrice = '0.0';
//			$('[name=pifajia_'+$tcount.val()+']').each(function(){
//				if (parseFloat(tcount) >= parseFloat($(this).val())) {
//					proPrice = $(this).attr('price');
//				} 
//			});
//			if (proPrice != '0.0') {
//				$("#nowPrice_"+$tcount.val()).html(proPrice);
//				$("#totalPrice_"+$tcount.val()).html((parseFloat($("#nowPrice_"+$tcount.val()).html())*parseFloat($('[name=prodCountText][scid='+$tcount.val()+']').val())).toFixed(2)); 
//			}
//		});
//		var totalPrice = 0.0;
//		var productCount = 0;
//		$('input[name=product_cb]:checked').each(function(index){
//			totalPrice += parseFloat($(this).parent().parent().find("span[id^=totalPrice_]").html().trim());
//			productCount += parseInt($(this).parent().parent().find("[name=prodCountText]").val().trim());
//		});
//		$("#selProd").html(productCount);
//		$("#selProdPrice").html("￥ "+totalPrice);
	}
	//商品数量修改
	function modifyProdCount(prodcount,scid,sellerWeiId){
		var ret = false;
		if (isNull(sellerWeiId) == false && typeof(prodcount) != "undefined" && prodcount != '' && typeof(scid) != "undefined" && scid != '') {
			$.ajax({
				type:"post",
				async:false,
				dataType:"json",
				contentType : "application/x-www-form-urlencoded; charset=utf-8",
				url:"/shopCartAjax/updateCartPordCount",
				data:'prodCount='+prodcount+'&scId='+scid+'&sellerWeiId='+sellerWeiId,
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					alert("服务器出现异常");
				},
				success:function(result){
					var result = eval(result);
		         	if(result != null || result != "")
		         	{  
			    		if(result.Statu == "Success")
			    		{ 
			    			ret = true;
			    		}
			    		else{
			    			alert(result.StatusReson);
			    		}
			    	}
				}
			});
		} 
		return ret;
	}

	// 全选 不需要了
//	$("[name=quanChe]").click(function() {
//		if ($(this).attr("checked")) {
//			$("input[type='checkbox']").each(function() {
//				$(this).attr("checked", true);
//			});
//		} else {
//			$("input[type='checkbox']").each(function() {
//				$(this).attr("checked", false);
//			});
//		}
//		jisuanProd();
//		if (type == '3') {
//			jsBatchProductPrice();
//		}
//	});
	
	// 店铺全选  不要了
	$("[name=cart_cb]").click(function() {
		var $p = $(this).parent().nextAll().find("input[name=product_cb]");
		if ($(this).attr("checked")) {
			$p.each(function() {
				if($(this).attr("status") == "1")
				{
					$(this).attr("checked", true);
				}
	
			});
		} else {
			$p.each(function() {
				$(this).attr("checked", false);
			});
		}
//		jsPrice();

//		if (type == '3') {
//			jsBatchProductPrice();
//		}
		showPrompt();
		jisuanProd();
	});
	//选择需要结算的商品
	$("[name=product_cb]").click(function() {
		
//		var type = isNull($(this).attr("buyType")) == true ? "-1" : $(this).attr("buyType");
//		jsPrice();
//		if (type == '3') {
//			jsBatchProductPrice();
//		}
		//结算钱
		jisuanProd();
		//提示
		showPrompt();
		var thisClass = $(this).attr("class");
		//选中的个数
		var thisCheckCount = parseInt($("input[class="+thisClass+"]:checked").length);
		//所有的个数
		var thisCount = parseInt($("."+thisClass+"").length);
		//个数相同全选选中
		if(thisCheckCount == thisCount)
		{
			$(this).parent().parent().parent().find("[name=cart_cb]").prop("checked",true);
		}
		else //全选取消
		{
			$(this).parent().parent().parent().find("[name=cart_cb]").prop("checked",false);
		}
	});
	//结算
	$("#jiesuan").live("click",function() {
		var $selProd = $('input[name=product_cb]:checked');
		if ($selProd.length > 0) {
//			if (type == '3') {
//			    var flag = true;
//				$selProd.each(function() {
//					var $tcount = $(this);
//					var tcount = parseFloat(0);
//					$selProd.each(function(){
//						var a = $('[name=prodCountText][scid='+$(this).val()+']');
//						if (a.attr('productId') == $tcount.attr('productId')) {
//							tcount = parseFloat(tcount) + parseFloat(a.val());
//						}
//					});
//					if(tcount < $('[name=pifajia_'+$tcount.val()+']').eq(0).val()){
//						alert('批发数量未达到，请重新修改!');
//						flag = false;
//						return false;
//					}
//				})
//				if (flag == false) {
//					return;
//				}
//			}		
//			var scids = new Array();
//			$selProd.each(function() {
//				var scid = $(this).val();
//				var cartProd = new Object;  
//				cartProd.scid = scid; //购物车id
//				cartProd.companyLogid = $(this).attr("companyLogid"); //店铺标识
//				cartProd.companyName = $("#cart_companyname_"+$(this).attr("companyIndex")).val(); //店铺名称
//				cartProd.prodId = $("#prod_id_"+scid).val();//商品id
//				cartProd.styleId = $("#prod_style_"+scid).val();//商品款式id
//				cartProd.shopWeiId = $("#prod_sourceid_"+scid).val();//来源微店id
//				cartProd.shopWeiName = $("#prod_source_"+scid).val();//来源微店名称
//				cartProd.img = $("#prod_img_"+scid).attr("src");//商品图片
//				cartProd.prodTitle = $("#prod_title_"+scid).val();//商品名称
//				cartProd.prodCount = $("#prod_count_"+scid).val();//商品数量
//				cartProd.prodNowprice = $("#prod_nowprice_"+scid).val();//商品单价
//				var tempproperty = $("#property_"+scid).html();
//				cartProd.property = tempproperty;
//				scids.push(cartProd);
//			});
//			document.forms[0].action = "/shopCartMgt/preSubmit";
//			$("#scidJson").val($.toJSON(scids));
//			document.forms[0].submit();
		var shopCartList = new Array();
		//遍历所有单的类型
		$("[name='companybuytype']").each(function(){
			//遍历选中的款式
			var thisCompanyBuyType = $(this).attr("data");
			var productList = new Array();
			$("."+thisCompanyBuyType+":checked").each(function(){
				var style = new Object();
				style.proNum = $(this).attr("productid");
				style.scid = $(this).attr("scid");
				style.styleId = $(this).attr("styleId");
				style.buyShopId = $(this).attr("tradeWeiId");
				style.shareOne=$(this).attr("shareweiid");
				style.price = $(this).attr("price");
				style.count = $(this).parent().parent().find("#prod_count_"+$(this).attr("scid")+"").val();
				productList.push(style);
			});
//			alert(JSON.stringify(productList)); 
//			alert(parseInt(productList.length));
			if(parseInt(productList.length) > parseInt(0))
			{
				var shopCart = new Object();
				shopCart.supplierWeiId = $(this).attr("supplierweiid");
				shopCart.buyType = $(this).attr("buyType");
				shopCart.source = $(this).attr("source");
				shopCart.demandId = $(this).attr("demandid");
				shopCart.productList = productList;
				shopCart.source = $(this).attr("source");
				shopCartList.push(shopCart);
			}
		});
		//铺货单不能和别的订单一起结算的
		var distirbutionpromptCount = parseInt(0);
		$("[name=distirbutionprompt]").each(function(){
			if($(this).is(":visible") == true)
			{
				distirbutionpromptCount ++ ;
			}			
		});
		if(distirbutionpromptCount > parseInt(0))
		{
//			alert("铺货订单不能和其他类型的订单合并结算，请单独结算 ");
			windows("提示","win_div_2","520","300");
			return false;
		}
		//落地单提示
		var notallowedlandingpromptCount = parseInt(0);
		$("[name=notallowedlandingprompt]").each(function(){
			if($(this).is(":visible") == true)
			{
				notallowedlandingpromptCount ++;
			}
		});
		if(notallowedlandingpromptCount > parseInt(0))
		{
//			alert("您的进货订单不满足落地店的采购标准，请补货后再提交订单！")
			windows("提示","win_div_1","520","300");
			return false;
		}
		//alert($.toJSON(shopCartList)); 
//		return;
		document.forms[0].action = "/shopCartMgtVerFive/preSubmitNew";
		$("#scidJson").val($.toJSON(shopCartList));
		document.forms[0].submit();
		} 
		else {
			alert("请选择要购买的商品!");
		}
	});
	$("#jiesuan1").live("click",function() {
		alert("请选择要购买的商品!");
	});
	
	//其他购物车类型
	$(".qie_thr&.fl li").click(function() {
		window.location.href = "/shopCartMgt/list/"+$(this).attr('stype');
	});
});
//单个删除购物车
function signDel(scid,cid){
	if(confirm('您确定要删除吗？')){ 
		if (typeof(scid) != "undefined" && scid != '') {
			var $boxes = $('input[name=product_cb]'); 
			var scids = new Array();
			scids[0] = scid;
			$.ajax({
				type:"post",
				async:false,
				dataType:"json",
				traditional :true,
				contentType : "application/x-www-form-urlencoded; charset=utf-8",
				url:"/shopCartAjax/delCartPord",
				data:{'scids':scids},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					alert("服务器出现异常");
				},
				success:function(data){
					var data = eval(data);
				
		         	if(isNull(data) == false)
		         	{  
			    		if(data.Statu == "Success")
			    		{ 
			    			alert('删除成功!');
			    		
			    			var thisCompanyBuyType = $("#prodDiv_"+scid).parent().attr("data");
			    			
			    			if(parseInt($("."+thisCompanyBuyType+"").length) > parseInt(1))
			    			{
			    				$("#prodDiv_"+scid).remove();
			    			}
							else
			    			{
								$("#prodDiv_"+scid).parent().remove();
			    			}
//							$('input[name=cart_cb]').each(function(){
//								if ($('input[name=product_cb][companyIndex='+$(this).val()+']').length == 0) {
//									$(this).parent('div').remove();
//								}
//							});
							if ($('input[name=product_cb]').length == 0) {
								window.location.href = "/shopCartMgt/list/";
							}
//							$('#sell_'+$('#stype').val()).html(parseInt($('#sell_'+$('#stype').val()).html())-1);
							jisuanProd();
			    			var count = parseInt($("#catCount").html().trim()) - parseInt(1);
			    			$("#catCount").html(count);
							
			    		}
			    		else{
			    			alert(result.StatusReson);
			    		}
			    	}
		         	else
		         	{
		         		alert('服务器异常!');
		         	}
		         	
		         	
//					if (data.msg == '0') {
//						alert('删除成功!');
//						$("#prodDiv_"+scid).remove();
//						$('input[name=cart_cb]').each(function(){
//							if ($('input[name=product_cb][companyIndex='+$(this).val()+']').length == 0) {
//								$(this).parent('div').remove();
//							}
//						});
//						if ($('input[name=product_cb]').length == 0) {
//							window.location.href = "/shopCartMgt/list/"+$('#stype').val();
//						}
//						$('#sell_'+$('#stype').val()).html(parseInt($('#sell_'+$('#stype').val()).html())-1);
//					} else {
//						alert('服务器异常!');
//					}
					
					
				}
			});
		}
	}
}
//批量删除购物车
function batchDel(){
	var $check_boxes = $('input[name=product_cb]:checked');  
	if($check_boxes.length<=0){ alert('请选择要删除的商品!');return;}
	var sl = parseInt($check_boxes.length);
	if(confirm('您确定要删除选中的商品吗？')){ 
		var scids = new Array();
		 $check_boxes.each(function(){  
			 scids.push($(this).val());  
	     });
		$.ajax({
			type:"post",
			async:false,
			dataType:"json",
			traditional :true,
			contentType : "application/x-www-form-urlencoded; charset=utf-8",
			url:"/shopCartAjax/delCartPord",
			data:{'scids':scids},
			error : function(XMLHttpRequest, textStatus, errorThrown) {
				alert("服务器出现异常");
			},
			success:function(data){
				if (data.msg == '0') {
					alert('删除成功!');
					$check_boxes.each(function(){  
						$(this).parent().parent().remove(); 
					});
					$('input[name=cart_cb]').each(function(){
						if ($('input[name=product_cb][companyIndex='+$(this).val()+']').length == 0) {
							$(this).parent('div').remove();
						}
					});
					if ($('input[name=product_cb]').length == 0) {
						window.location.href = "/shopCartMgt/list/"+$('#stype').val();
					}
					$('#sell_'+$('#stype').val()).html(parseInt($('#sell_'+$('#stype').val()).html())-sl);
				} else {
					alert('服务器异常!');
				}
			}
		});
	}
}
function getProductStyleParamList(productId,styleId,isActivity,aprice,url,_this)
{
	isAct=isNull(isActivity) == true ? 0 : isActivity;
	$.ajax({
	    url: url,
	    type: "POST",
	    data:
	    {
	    	productId:productId,
	    	styleId:styleId
	    },
	    error: function (XMLHttpRequest, textStatus, errorThrown) 
	    { alert("系统异常"); },
	    success: function (result)
	    {  
         	var result = eval(result);
         	if(result != null || result != "")
         	{    
         		if(result.Statu == "Success")
         		{   
         			assignmentStyleParamHtml(result.BaseModle,_this,isAct,aprice);
         		}
         	}
	    }
	    }
	);
}
function assignmentStyleParamHtml(list,_this,isActivity,aprice)
{   
	var isAct=isNull(isActivity) == true ? 0 : isActivity;
//	var keyValue = _this.attr("keyvalue");
	//格式话一下数据
	var keyValueList = eval(isNull(list.jsonPProductStyleKVVOList) == true ? "[]" : list.jsonPProductStyleKVVOList);
	var html = "";
	html += "<div class='icon_lefsan'><img src='../../statics/pic/bianji_yangsimgs.png' /></div>";
	html += "<div class='elect_left fl'>";
    html += "<div class='zuo_ones fl'>";
    $.each(list.basicProductStyleParamList, function (index)
    {
    	html += "<div class='ones_color'>";
    	html += "<div class='color_font fl'>";
    		if(list.basicProductStyleParamList[index].proSellKey.attributeName == "-1")
    		{
    			html += "默认：";
    		}
    		else
    		{
    			html += list.basicProductStyleParamList[index].proSellKey.attributeName;
    		}
    		html +="</div>"
	    	html += "<div class='color_sel fl'>";
    			//款式的个数
    			var sxCount = JudgePositiveInteger(list.basicProductStyleParamList.length) == true ? list.basicProductStyleParamList.length : -1;
		    	html += "<ul sxCount='"+sxCount+"' scid='"+_this.attr("scid")+"'>"
		    		 		$.each(list.basicProductStyleParamList[index].proSellValue, function (index2)
		    				 {  
		    		 			//attributeId 是否为空
	    		 				var attributeId = isNull(list.basicProductStyleParamList[index].proSellValue[index2].attributeId) == true ? "-1" : list.basicProductStyleParamList[index].proSellValue[index2].attributeId;
	    		 				//productId 是否为空 
	    		 				var productId = isNull(list.basicProductStyleParamList[index].proSellValue[index2].productId) == true ? "-1" : list.basicProductStyleParamList[index].proSellValue[index2].productId;
		    		 			//keyId 是否为空
	    		 				var keyId = isNull(list.basicProductStyleParamList[index].proSellValue[index2].keyId) == true ? "-12" : list.basicProductStyleParamList[index].proSellValue[index2].keyId ;  
		    		 			if(equalKeyId(keyValueList,keyId) == true)
		    		 			{   
		    		 				
		    		 				html += "<li class='bor_redsel' source='"+_this.attr("source")+"' name='mzh_"+productId+"-"+attributeId+"' scid='sc_"+_this.attr("scid")+"' defaultSel='1' productid = '"+productId+"' keyid='"+keyId+"' attrid='"+attributeId+"' >";
		    		 			}
		    		 			else{
		    		 				html += "<li class='bor_heisel' source='"+_this.attr("source")+"' name='mzh_"+productId+"-"+attributeId+"' scid='sc_"+_this.attr("scid")+"' defaultSel='0' productid = '"+productId+"' keyid='"+keyId+"' attrid='"+attributeId+"' >";
		    		 			}
		           				if(list.basicProductStyleParamList[index].proSellValue[index2].value == "-1")
		           				{
		           					html += "默认";
		           				}
		           				else{
		           					html += list.basicProductStyleParamList[index].proSellValue[index2].value;	
		           				}
		           				html += "</li>"
		    				 });
	    				
		    	html += "</ul>";
	    	html += "</div>"
    	html += "</div>";
    });
    html += "<div class='ones_cprice'>";
    html +=     "<div class='color_font fl'>价格：</div>";  	
    html +=     "<div class='color_sel fl'>";      
    html +=         "<ul>";  	
    					//赋值价格id(scid是主键不可能为空)
    html +=          	"<li class='f12 ft_c6' id='styleprice_"+_this.attr("scid")+"'>"+_this.attr("price")+"</li>";
  
    html +=         "</ul>";     
    html += 	"</div>";     
    html += "</div>";
    html += "<div class='bot_redyes fl'>";   
    html +=    "<a href='javascript:void(0)' buyType='"+_this.attr("buyType")+"' class='ft_white tc btn_yes' source='"+_this.attr("source")+"' price='"+_this.attr("price")+"' isactivity='"+isAct+"' activityprice='"+aprice+"' name='mzh_ok' scid='"+_this.attr("scid")+"'>确定</a>";
    html +=    "<a href='javascript:void(0)' class='btn_qxia ft_lan tc' name='mzh_close'>取消</a>";
    html += "</div>"
    html += "</div>";
    html += "<div class='blank1'></div>"   
    html += "</div>"
    html += "<div class='elect_right fl'><img id='sel_img_sc_"+_this.attr("scid")+"' src='"+_this.attr("image")+"' /></div>";
    _this.parent().find(".elect_color").html(html);
	//默认选择属性值
	$("[scid=sc_"+_this.attr("scid")+"]").each(function(){
		if (_this.attr("defaultSel") == '1') {
			_this.attr("class","bor_redsel");
		} else if (_this.attr("class") == "bor_redsel") {
			_this.attr("class","bor_heisel");
		}
	});
	
	$("[name=mzh_hover]").attr("ishover","0");
	$("[name=mzh_fangshang]").attr("class","comm_bianjno_mr");
	$(".elect_color").hide();
	_this.parent().attr("class","comm_bianjyes");
	_this.parent().parent().parent().attr("ishover","2");
	_this.nextAll("div[class=elect_color]").show();
}
function equalKeyId(keyValueList,keyId)
{
	var result = false;
	$.each(keyValueList, function (i)
	{
		var secondKeyId = isNull(keyValueList[i].keyId) == true ? "-11" : keyValueList[i].keyId;
		if(secondKeyId == keyId)
		{
			result = true;
			return;
		}
	});
	return result;
}
function getWholesalePrice(wholesalePriceList,count)
{   
	var price = parseFloat(0.0);
	var resultList = sort(wholesalePriceList);
	//遍历批发价列表
	$.each(resultList, function (i)
	{    
		//判断批发个数
		if(parseInt(count) >= parseInt(resultList[i].count))
		{
			price = parseFloat(resultList[i].price);
		}
	});
	return price;
}
function sort(wholesalePriceList)
{
	for(var i=0;i<wholesalePriceList.length - 1;i++)
	{     
		
		  for(var j = i+1;j<wholesalePriceList.length;j++)
		  {    
			   if(parseInt(wholesalePriceList[i].count)  > parseInt(wholesalePriceList[j].count))
			   {   
				    var swap = wholesalePriceList[i];
				    wholesalePriceList[i] = wholesalePriceList[j];
				    wholesalePriceList[j] = swap;
			   }
		  }
	}
	return wholesalePriceList;
}
function showPrompt()
{   
	
	//没有铺货单
	var notHasDistirbution = false;
	//其它单
	var otherBuyType = false;
	//遍历每一个单
	$("[name='companybuytype']").each(function(){
		//取出class
		//首单金额
		var firstorderamount = parseFloat($(this).attr("firstorderamount"));
		//是否首单
		var isfirstorder = $(this).attr("isfirstorder");
		// 落地价
		var landingPrice = parseFloat(0.0);
		var thisCompanyBuyType = $(this).attr("data");
		//遍历每一个款式，产品下架
		var styleCount = parseInt($("."+thisCompanyBuyType+"").length);
		var statusCount = parseInt(0);
		$("."+thisCompanyBuyType+"").each(function(){
			if($(this).attr("status") == "0")
			{
				statusCount++;
			}
		});
		//全部都是下架的时候的状态
		var allUnShelve = false;
		if(styleCount == statusCount)
		{
			$(this).find("[name=cart_cb]").prop("disabled","disabled");
			//修改样式
			$(this).attr("class","shopp_count fl ft_e");
			allUnShelve = true;
		}
		//如果是全部下架，修改样式
		if(allUnShelve == true)
		{   
			//遍历每一个款式
			$("."+thisCompanyBuyType+"").each(function(){
				//在这里把设置为灰色的样式去掉
				$(this).parent().parent().attr("class","shop_comm bor_bo fl");
			});
		}
		//遍历每一个款式,落地单提示
		if(isNull(isfirstorder) == false && isfirstorder == "1")
		{

			var isCheck = false;
			$("."+thisCompanyBuyType+"").each(function(){
				//选中的是落地价
				if($(this).is(':checked') == true && $(this).attr("source") == "2" )
				{  
//					alert($(this).attr("price"));
					landingPrice += parseFloat($(this).attr("price"));
					isCheck = true;
				}
			});
			if(isCheck == false)
			{
				$(this).find("[name=notallowedlandingprompt]").hide();
				$(this).find("[name=allowedlandingprompt]").hide();
			}
		}
		//不满足落地店首单提示和满足落地店首单提示
		if(firstorderamount != parseFloat(0.0) && landingPrice != parseFloat(0.0) && landingPrice < firstorderamount)
		{
			$(this).find("[name=notallowedlandingpromptprice]").html(firstorderamount - landingPrice);
			$(this).find("[name=notallowedlandingprompt]").show();
			$(this).find("[name=allowedlandingprompt]").hide();
		}
		//不满足落地店首单提示和满足落地店首单提示
		if(firstorderamount != parseFloat(0.0) && landingPrice != parseFloat(0.0) && landingPrice >= firstorderamount)
		{
			$(this).find("[name=allowedlandingprompt]").show();
			$(this).find("[name=notallowedlandingprompt]").hide();
		}
	});
	//遍历已经选中的款式
	$("[name=product_cb]:checked").each(function(){ 
		//已选中
		if($(this).attr("buytype") == "5")
		{
			notHasDistirbution = true;
		}
		else
		{
			otherBuyType = true;
		}
	});
	//铺货单不能其它一起结算js
	if(notHasDistirbution == true && otherBuyType == true)
	{    
		$("[name=product_cb]:checked").each(function(){
			if($(this).attr("buytype") == "5")
			{
				$(this).parent().parent().parent().find("[name=distirbutionprompt]").show();
			}
		});
	}
	else{
		$("[name=product_cb]").each(function(){
			$(this).parent().parent().parent().find("[name=distirbutionprompt]").hide();
		});
	}
}
/** 弹窗调用函数 **/
function windows(title,win_id,width,height){ //title 标题 win_id 弹窗ID  width 弹窗宽度 height 弹窗高度
    var pagei = $.layer({
        type: 1,   //0-4的选择,0：信息框（默认），1：页面层，2：iframe层，3：加载层，4：tips层。
        btns: 1,
        btn: ['关闭'],
        title: title,
        border: [0],
        closeBtn: [0],
        closeBtn: [0, true],
        shadeClose: true,
        area: [width,height],
        page: {dom : '#'+ win_id},
        end: function(){ $("#AddCount").hide()}
    });
}
//
function editStyleShowOrHide(_this)
{
	$(".elect_color").hide();
	$("[name=mzh_fangshang]").attr("class","comm_bianjno_mr");
	$("[name=mzh_hover]").attr("ishover","0");
	//默认款式
	$("[scid=sc_"+_this.attr("scid")+"]").each(function(){
		if ($(this).attr("class") == "bor_redsel") {
			$(this).attr("defaultSel","1");
		} else {
			$(this).attr("defaultSel","0");
		}
	});
}
//价格计算
function jisuanProd(){
	var scids = new Array();
	$('input[name=product_cb]:checked').each(function() {
		scids.push($(this).val());  
	});
	if (scids.length > 0) {
		$("#jiesuan_div").attr("class","wind_two_yes fl");
		$("#jiesuan_div").find("#jiesuan1").attr("id","jiesuan");
		$.ajax({
			type:"post",
			async:false,
			dataType:"json",
			traditional :true,
			contentType : "application/x-www-form-urlencoded; charset=utf-8",
			url:"/shopCartAjax/getJiesuanInfo",
			data:{'scids':scids},
			error : function(XMLHttpRequest, textStatus, errorThrown) {
				alert("服务器出现异常");
			},
			success:function(data){
				if (data != null && typeof(data) != "undefined" && data != "null" && data != "") {
					$("#selProd").html(data.BaseModle.count);
					$("#selProdPrice").html("￥ " +  (data.BaseModle.totalPrice).toFixed(2));
					if( (data.BaseModle.totalPrice).toFixed(2) ==0.00)
					{
						$("#jiesuan_div").attr("class","wind_two_no fl");
						$("#jiesuan_div").find("#jiesuan").attr("id","jiesuan1");
					}
				}
			}
		});
	} else {
		$("#selProd").html(0);
		$("#selProdPrice").html("￥ 0.00");
		$("#jiesuan_div").attr("class","wind_two_no fl");
		$("#jiesuan_div").find("#jiesuan").attr("id","jiesuan1");
	}
}
