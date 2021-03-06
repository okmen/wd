<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>    
<%@ taglib prefix="pg" uri="http://www.okwei.com/pagination"%>
<%@ page import="java.util.ResourceBundle"%>
<%
	String productdomain = ResourceBundle.getBundle("domain").getString("productdomain");
	String companydomain = ResourceBundle.getBundle("domain").getString("companydomain");
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
%>  
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script src="http://base3.okwei.com/js/company/jquery-1.7.1.min.js"></script>
<script type="text/javascript" src="http://base3.okwei.com/js/company/common/extends_fn.js"></script>
<script type="text/javascript" src="http://base3.okwei.com/js/company/common/pagination.js"></script>
<script type="text/javascript" src="http://base3.okwei.com/js/company/jquery.lazyload.js"></script>
<script type="text/javascript" src="http://base3.okwei.com/js/company/layer/layer.min.js"></script>
<script type="text/javascript" src="http://base3.okwei.com/js/company/common/share.js"></script>

<link rel="stylesheet" type="text/css" href="http://base1.okwei.com/css/web/pc-base.css" />
<link rel="stylesheet" type="text/css" href="http://base2.okwei.com/css/web/home-page.css" />
<link rel="stylesheet" type="text/css" href="http://base3.okwei.com/css/company/pagination.css" />
<title>批发市场商铺列表</title>

<style>
.scpf_market .cpimgs ul li{ height:380px;}
.imgps img{ width:206px; height:206px;}
.mzh_fenxiang_no{ margin-left:-4px;}
.mzh_fenxiang_yes{ margin-left:-4px;}
.cpimgs .smwzs{ overflow:hidden; display:block; height:44px; line-height:22px; width:100%; font-size:14px;}
.mzh_fenxiang {
    background: #fff;
    border: 1px solid #ddd;
    display: none; 
    left: -4px;
    padding: 5px;
    position: absolute;
    top: 24px;
    width: 100px;
    z-index: 88;
    }
.mzh_fenxiang a{ width:23px; height:23px; float:left; margin-right:5px;}
.scpf_market .cpimgs ul li{ height:380px;}
.imgps img{ width:206px; height:206px;}
.mzh_fenxiang_no{ margin-left:-4px;}
.mzh_fenxiang_yes{ margin-left:-4px;}
	
	.mzh_fenxiang {
    background: #fff;
    border: 1px solid #ddd;
    display: none; 
    left: -4px;
    padding: 5px;
    position: absolute;
    top: 24px;
    width: 100px;
    z-index: 88;
}
.FX {
    background: rgba(0, 0, 0, 0) url("http://base.okimgs.com/images/xh_fx.gif") no-repeat scroll 0 0;
    color: #ffffff;
    float: left;
    font-family: "宋体";
    font-size: 12px;
    height: 20px;
    line-height: 20px;
    position: relative;
    text-indent: 24px;
    width: 19px;
    border-radius: 3px;
}
.fx_WKX {
    background: #ffffff none repeat scroll 0 0;
    border: 1px solid #1a9a5c;
    height: 40px;
    overflow: hidden;
    position: absolute;
    right: 0;
    top: 20px;
    width: 120px;
    z-index: 1;
}
.ZF div {
    float: left;
}
</style>
<script>
$(function(){
	$(".FX").mouseover(function() {
		$(this).find(".fx_WKX").show();
	}).mouseout(function() {
		$(this).find(".fx_WKX").hide();
	});	
});	
$(function(){
	//图片延迟加载
	$("img[name='productImg']").lazyload({
        placeholder: "<%=path%>/statics/images/206_206.png",
        event : "sporty"
    });
	
	$(window).bind("load", function() {    
		var timeout = setTimeout(function() {$("img[name='productImg']").trigger("sporty")}, 1000);    
	}); 
	var page = new Pagination( {
		formId: "searchForm",
		isAjax : false,
		targetId : "navTab",
		submitId:"searchBtn"
	});
	page.init();
	
	//样式
	$(".cpimgs ul li:nth-child(5n)").css("margin-right","0px");
	//分享样式
	$("[name=mzh_fenxiang]").mouseover(function(){
	    $(this).find("[id=aaa]").attr("class","mzh_fenxiang_yes");
	    $(this).find(".mzh_fenxiang").show();
	});
	$("[name=mzh_fenxiang]").mouseout(function(){
	    $(this).find("[id=aaa]").attr("class","mzh_fenxiang_no");
	    $(this).find(".mzh_fenxiang").hide();
	});
	//分页控制
	$('#shangpin_li',window.parent.document).find('b').eq(0).text($('.current').html());
	$('#shangpin_li',window.parent.document).find('b').eq(1).text($('.pageinfo em').html());
	
	//上架按钮点击事件
	$(".pro_tool .btn_small[name='shelves']").live("click",function(){
		var productID = $(this).attr("value");
		//判断是否登录
		var isLogin =$("#loginID").val();
		if(isLogin !="" && isLogin >0){
			var isSupply = $("#isSupply").val();
			if(isSupply =="true"){
				window.parent.SupplyShelves($(this));
			}
			else{
				window.parent.UserShelves($(this));
			}
			//展示上架层
			window.parent.ShowShelvesDIV();
		}else{//去登录
			window.parent.goLogin();
		}
	});
	
	//不知道干嘛的
	$(".cpimgs ul li:nth-child(5n)").css("margin-right","0px")
	$("#showtype1div .money").hover(function(){
		$(this).siblings(".ladder-price").css({"visibility":"visible","opacity":"1"})},
		function(){
		$(this).siblings(".ladder-price").css({"visibility":"hidden","opacity":"0"})
	});
});

  function shareto(ftype,prodId,w){
	var title="";
	if(w==null){
		w="";
	}
	var pageurl="<%=productdomain %>/product?pid="+prodId+"&w="+w;
	var source="普通网民可以在这里免费注册开微店，供应商可以从这里提交资料，把产品发到云端产品库，让像我一样的无数网民为他销售产品。";
	switch(ftype)
	{
		case 0:{
			ShareToQzone(title, pageurl, source);
			break;}
		case 1:{
			ShareToTencent(title, pageurl, source);
			break;}
		case 2:{
			ShareToSina(title, pageurl, source);
			break;}
		default:{
			alert("分享类型错误！");
			break;
		}
	}
}

  function shareto(ftype,prodId){
	var title="";
	var w=$("#weiid").val();
	if(w<=0){
		w="";
	}
	var pageurl="<%=productdomain %>/product?pid="+prodId+"&w="+w;
	var source="普通网民可以在这里免费注册开微店，供应商可以从这里提交资料，把产品发到云端产品库，让像我一样的无数网民为他销售产品。";
	switch(ftype)
	{
		case 0:{
			ShareToQzone(title, pageurl, source);
			break;}
		case 1:{
			ShareToTencent(title, pageurl, source);
			break;}
		case 2:{
			ShareToSina(title, pageurl, source);
			break;}
		default:{
			alert("分享类型错误！");
			break;
		}
	}
	// 修改分享数量
	$.ajax({
		url : "<%=productdomain %>/product/share",
		type : "post",
		data : {
			proID : prodId
		},
		success : function(data) {
			if (data.msg == "1") {
				var count = parseInt($("#shareCount").html());
				count += 1;
				$("#shareCount").html(count);
			}
		}
	});
}
function goPage1(h){
	$('.pagination a').each(function(){
		if ($(this).html()==h) {
			$(this).trigger('click');
		}
	});
}
</script>

</head>
<body style="background:#ebebeb;">
<div class="mar1200 scpf_market"> 
    <!-- 筛选 --> 
    <!-- 商品排列  style=" visibility:visible;opacity:1"  list_li-->
<form id="searchForm" name="searchForm" action="<%=basePath %>wholesale/batchMarketProductList">
<input type="hidden" name="bmid" value="${bmid}"/>

<input type="hidden" id="loginID" value="${user.weiID}" />
<input type="hidden" id="isSupply" value="${user.yunS ==1 || user.batchS ==1  }" />
  <div class="mar1200">  
    <div class="w fl cpimgs mt20">
     <c:if test="${fn:length(pageResult.list)<1}">
         <!-- 批发市场详情 商铺空 -->
	     <div class="null_coues fl pb30">
	      	<p class="f18 tc">该市场暂无商品，逛逛其他批发市场</p>
	        <div class="w350 fl botlse">
	        	<a target="_blank" href="<%=companydomain %>/wholesale/list?w=${user.weiID}">去看看</a>
	        </div>
	     </div>
	  </c:if>
      <ul>
      <c:forEach items="${pageResult.list}" var="cp">
        <li class="pro_li" value="${cp.productId}" commision="${cp.commision}">
          <div class="pro_div">
            <div class="imgps"><a href="<%=productdomain %>/product?pid=${cp.productId}" target="_blank"><img name="productImg" original="${cp.image}"/></a></div>
            <div class="money" id="aaa"> <span class="fl cR f16">¥ <b class="tb" name="productPrice">${cp.price}</b> </span>
            <span class="fr c9">
            <%-- <a href="<%=productdomain %>/product?pid=${cp.productId}" class="cB" target="_blank">评论(${cp.evaluateCount })</a> --%>         
             <div style="cursor: pointer;" class="FX">
							<div style="display: none;" class="fx_WKX">
								<div class="ZF">
								<input type="hidden" id="weiid" value="${user.weiID}">
									<div style="width: 22px; height: 21px; margin: 10px 0 0 12px; text-indent: 0;">
										<a href="javascript:shareto(0,${cp.productId});"><img src="/statics/images/TX_kj.gif"></a>
									</div>
									<div style="width: 22px; height: 21px; margin: 10px 0 0 12px; text-indent: 0; background: red;">
										<a href="javascript:shareto(1,${cp.productId});"><img src="/statics/images/TX_wb.gif"></a>
									</div>
									<div style="float: left; width: 22px; height: 21px; margin: 10px 0 0 12px; text-indent: 0;">
										<a href="javascript:shareto(2,${cp.productId});"><img src="/statics/images/XL_wb.gif"></a>
									</div>
								</div>
					</div> 
			</div> 			
            </span>
                
            </div>
            <c:if test="${fn:length(cp.batchPriceList) > 0}">
            <div class="ladder-price line-count-3" >
              <div class="p5"> <i class="arrow-top"></i>
                <div class="fd-left" name="priceRegion" value="${cp.priceRegion }">
                 <c:forEach items="${cp.batchPriceList}" var="p">
                  <div class="ladder-price-item fd-clr"> <span class="price-number" name="batchPrice" value="${p.pirce}">¥ ${p.pirce}</span>
                    <div class="volume-block" name="tabchNum" value="${p.num}">${p.countRange }件</div>
                  </div>
                  </c:forEach>
                </div>
                <div class="price-other">
                  <div class="left-border"></div>
                  <div class="adjust-offset"></div>运费<br>
                  <em>${cp.post}</em>元起 </div>
                <div class="cb"></div>
              </div>
            </div>
            </c:if>
            
            <p class="smwzs"><a href="<%=productdomain %>/product?pid=${cp.productId}" target="_blank" name="productTitle" style="white-space: normal;">${cp.title} </a></p>
           <div  class="byou w h20">
          <%--  <c:if test="${cp.isPost==1}"><span class=" bg_K_1 p5 c6" title=""></span></c:if><c:if test="${cp.isBrand==1}"><span class=" bg_K_1 p5 c6"  title=""></span></c:if><i class="cb"></i> --%> 
           </div> 
            <div class="pro_contact"></div>
            <div class="pro_tool"><span class=" fl"> 
            <a href="javascript:void(0);"  name="shelves" class="${cp.isShevles==1?'btn_small_hui':'btn_small' } tc ">
       		 上架<c:if test="${cp.shelvesCount !=null && cp.shelvesCount >0 }">(${cp.shelvesCount})</c:if>
       		 </a>
            </span> 
            <span class="fr pr" name="mzh_fenxiang" >
	            <div class="mzh_fenxiang_no">
	            <a href="<%=productdomain %>/product?pid=${cp.productId}" target="_blank" class=" btn_small_3 pr tc pl10">立即购买</a>    
	            <%-- <div class="mzh_fenxiang" style="display: none;">
              		<a href="javascript:shareto(0,${cp.productId},${userinfo.weiID>0?userinfo.weiID:userinfo.tgWeiID});"><img src="http://base.okimgs.com/images/TX_kj.gif"></a>
              		<a href="javascript:shareto(1,${cp.productId},${userinfo.weiID>0?userinfo.weiID:userinfo.tgWeiID});"><img src="http://base.okimgs.com/images/TX_wb.gif"></a>
               	    <a href="javascript:shareto(2,${cp.productId},${userinfo.weiID>0?userinfo.weiID:userinfo.tgWeiID});"><img src="http://base.okimgs.com/images/XL_wb.gif"></a>
           	    </div>  --%>
           	    </div>       
            </span>
            </div>
          </div>
        </li>
      </c:forEach>
      </ul>  
    </div>
    <!-- 分页 -->
	<div class="pull-right">
		<pg:page pageResult="${pageResult}" />
	</div>
  </div>
  </form>
  </div>
  
</body>
</html>