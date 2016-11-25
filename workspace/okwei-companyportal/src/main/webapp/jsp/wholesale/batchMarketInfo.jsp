<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>    
<%@ page import="java.util.ResourceBundle"%>
<%
	String companydomain = ResourceBundle.getBundle("domain").getString("companydomain");
	String mydomain = ResourceBundle.getBundle("domain").getString("mydomain");
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
%> 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="http://base2.okwei.com/js/compnay/cloudLibrary/coludList.js"></script>
<link rel="stylesheet" type="text/css" href="http://base2.okwei.com/css/compnay/pagination.css" />
<title>批发市场,全国批发市场最集中的地方,微店网(okwei.com)——免费开微店，认证原创微店官方平台</title>
<meta name="description" id="description" content="全国批发市场最集中的地方，批发市场商户免费开微店，全国批发市场商户都在这里开通了微店。" />
<meta name="keywords" id="keywords" content="微店网，免费开通微店，批发市场，批发市场微店。" />

<script>
$(function(){
	$('.leftscr a').click(function(){
		$('.leftscr a').each(function(){
			$('.leftscr a').attr('class','');
			$('#'+$(this).attr('showId')).hide();
		});
		$(this).attr('class','nows');
		$('#'+$(this).attr('showId')).show();
		//iframe
		if($(this).attr('showId') == 'shangpu'){
			if($('#shangpu_iframepage').attr("src") == ""){
				$('#shangpu_iframepage').attr("src",'/wholesale/batchMarketShopList?bmid=${batchMarket.bmid}');
				//初始化店铺分类
				getShopClassList();
			}
			//分页li
			$('#shangpu_li').show();
			$('#shangpin_li').hide();
			$('.pos_rabls').hide();
		} else {
			$('#shangpu_li').hide();
			$('#shangpin_li').show();
			$('.pos_rabls').show();
		}
	})
	
	//样式控制
	$(".cpimgs ul li:nth-child(5n)").css("margin-right","0px");
	$('.imgps a').height($('.imgps a').width()*0.75);
	//$("#shangpin_iframepage").attr("src","/wholesale/batchMarketProductList?bmid=${batchMarket.bmid}");
	//分页事件
	$('#shangpu_li a').click(function(){
		$("#shangpu_iframepage")[0].contentWindow.goPage1($(this).html());
	})
	$('#shangpin_li a').click(function(){
		$("#shangpin_iframepage")[0].contentWindow.goPage1($(this).html());
	})
	
	//初始化上架分类
	/* $("#refreshShopList").click(function(){
		getShopClassList();
	}); */
})

/* //获取用户店铺分类
 function getShopClassList(){
	var isLogin =$("#loginID").val();
	if(isLogin =="" || isLogin <1){
		return;
	}
	
	$.ajax({
	    url: "/wholesale/getShopClassList",
	    type: "post",
	    data: { },
	    dataType : 'json',
	    success: function (data) {
			var data = eval(data);
			if(data !=null && data.length>0){
				var html ="";
				$.each(data,function(index,item){
					html +="<li value='"+item.sid+"'>"+item.sname+"</li>";					
				});
				
				$(".zhuang_delssic ul").html(html);
			}
	    }
	});	 
	
} */

//iframe 自适应高度
function iFrameHeightAuto(fid) { 
	var ifm= document.getElementById(fid); 
	var subWeb = document.frames ? document.frames[fid].document : ifm.contentDocument; 
	if(ifm != null && subWeb != null) { 
		ifm.height = subWeb.body.scrollHeight; 
	} 
}
</script>
<style>
.scpf_market .cpimgs ul li{ height:260px;}
</style>
</head>

<body style="background:#ebebeb;">
<article>
	<div class="w line-b fl">
    <div class="line4 c6 mar1200">当前位置&nbsp;：&nbsp;微店网 > <a target="_blank" href="<%=companydomain %>/wholesale/index" >批发市场</a> > ${batchMarket.name}</div>
  </div> 
  <div class="w line-b  pt20 pb20 bg-w fl">
    <div class="mar1200">
      	
      <div class="fl "><img src="${batchMarket.image}" width="224" height="164" /></div>
    <div class="fl  line2 f14 ml20" style="width:900px;">
      <h4 ><c:if test="${batchMarket.isAllIn != 1}">${batchMarket.name}</c:if><c:if test="${batchMarket.isAllIn == 1}"><a href="/wholesale/batchMarketDetail?m=${batchMarket.bmid}" target="_blank" class="tb cB">${batchMarket.name}</a><span class="bg_R p5 f12  cW">已签约</span></c:if></h4>
      <ul class="mt20 block fl" style="width:40%;"><li>主营：${batchMarket.busContent}</li>
       <%--  <li> 所属行业：${batchMarket.bussinessClass}          </li> --%>
        <li>商家：${batchMarket.bussinesCount}家         
       &nbsp; &nbsp; &nbsp; &nbsp;   服务点：${batchMarket.serviceCount}家          </li>
        <li>地址：${batchMarket.area}</li>
      </ul>
      <div class="mt20 fl" style="width:59%; border-left:1px solid #ebebeb;">
      <div class="ml20" style="height:120px; overflow:hidden;">市场简介：
      <c:if test="${batchMarket.marketDes == '' || batchMarket.marketDes == null}"></c:if>
      <p class="lh150 f12">${batchMarket.marketDec}</p>
      </div>
      </div>
      </div>
      </div>
  </div>
  <div class="mar1200 scpf_market"> 
    <!-- 筛选 --> 
    <!-- 商品排列  style=" visibility:visible;opacity:1"  list_li-->
    
    <div class="screen bg-w fl mt20 mb20">
      <ul>
        <li class="fl leftscr"><a href="javascript:;" class="nows" showId="shangpin">商品</a><a href="javascript:;" showId="shangpu">商铺</a></li>
        <li class="fr rigsuces" id="shangpin_li"><span><b></b>/<b></b></span> <a href="javascript:;">上一页</a> <a href="javascript:;">下一页</a></li>
        <li class="fr rigsuces" id="shangpu_li" style="display: none;"><span><b></b>/<b></b></span> <a href="javascript:;">上一页</a> <a href="javascript:;">下一页</a></li>
      </ul>
    </div>
  	<div id="shangpin" >
    	<iframe src="/wholesale/batchMarketProductList?&bmid=${batchMarket.bmid}" onload="iFrameHeightAuto(this.id)" id="shangpin_iframepage" frameBorder=0 scrolling=no width="100%" ></iframe>
    </div>
    <div id="shangpu" style="display: none;">
    	<iframe src="" onload="iFrameHeightAuto(this.id)" id="shangpu_iframepage" frameBorder=0 scrolling=no width="100%" ></iframe>
    </div> 
  </div>
  <div class=""> 
  </div>
  <!-- 上架产品弹出层 -->
<c:if test="${user.weiID>0}">
<div class="pos_rabls">
  <div class="clicimgs" id="TanLeft" icon="0"><img src="http://base2.okwei.com/images/ing_sjup.png" /></div>
  <div class="rabls_one">
    <div class="blank mt10"></div>
    <div class="absdel" id="ImgClose"><img src="http://base3.okwei.com/images/delete_imgs.png" /></div>
    <div id="shelvesDIV" style="overflow:auto; max-height:200px;">
    
    </div>
    <div class="blank"></div> 
  </div>
  <div class="rabls_two">
    <div class="twoinp_sele">
    <input type="hidden" id="loginID" value="${user.weiID}" />
    
      <div class="two_titles">你要把产品上架到哪个分类?<a href="#" class="cB" id="refreshShopList">刷新</a>&nbsp;<a target="_blank" href="<%=mydomain %>/shopClass/classList">新建/编辑分类</a></div>
      <div class="zhuang_delssic" style="max-height:120px; overflow:auto;">
        <ul>
          <!-- <li class="red_borup" value="8744">女装</li>
          <li value="11279">女鞋</li> -->
        </ul>
        <div class="blank"></div>
      </div>
     <div class="zhuang_shuru"></div> 
      
    </div>
  </div>
  <c:if test="${ user.yunS ==1 || user.batchS ==1 }">
  <div class="rabls_two" id="shelvesPriceDiv">
    <div class="twoinp_sele">
      <div class="two_titles">修改批发价出售：原批发价<span>0.0~￥0.0</span></div>
      <div class="news_input">
        <ul>
          <li class="news_title">新批发价</li>
          
        </ul>
        <div class="blank"></div>
      </div>
    </div>
  </div>
  <div class="rabls_two">
<!--     <div class="twoinp_sele">
      <div class="two_titles">由谁来发货？</div>
      <div class="radio_to">
        <ul>
          <li>
            <input type="radio"  checked="checked" name="identity1" id="rio_2">
            <label for="rio_2">我自己发货</label>
          </li>
        </ul>
      </div>
    </div> -->
  </div>
  </c:if>
  <div class="rabls_two" id="divReason">
    <div class="twoinp_sele">
     <!--  <div class="two_titles tb">评论</div>
      <div class="radio_to">
        <textarea id="txtReason" rows="6"  class="w"></textarea>
      </div> -->
    </div>
  </div>
  <div class="rabls_two"> <a href="javascript:void(0);" id="btnSubmitShelves" class="btn_bluese cW">确定</a> </div>
</div>
</c:if>
</article>
</body>
</html>