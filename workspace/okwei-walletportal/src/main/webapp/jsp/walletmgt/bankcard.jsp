<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="pg" uri="http://www.okwei.com/pagination"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
%>
<head>
<title>我的钱包-银行卡</title>
<script type="text/javascript">

$(function(){
	
	//删除银行卡
	$("#bankdiv").on("click","div.yhk_yhk_2_close",function(){
		if(confirm("确定要删除该银行卡？")){
			var t = $(this);
			var id = t.attr("value");
			$.get("/bankCardMgt/remove/"+id,function(data){
	   			if(!data.error){
	   				t.closest(".yhk_yhk").remove();
	   	  			alert("删除成功！",true);
	   			}     
   			},"json");
		}
	});
	
	$("#addBtn").click(function(){
		$.get("/bankCardMgt/checkWallet",function(data){
   			if(!data.error){
   		        var url = $("#addBtn").attr("ahref");
   		     	var to = $("#" + $("#addBtn").attr("to"));
   		     	to.load(url+".ajax", function(data){});
   			}else{
   				var layerIndex = $.layer({
   					title:"实名认证",
   					shade: [0.2, '#000'],
   				    area: ['auto','auto'],
   				    border: [0],
   				    dialog: {
   				        msg: "微钱包已更新升级,实名认证后方可进行安全购物及提现!",
   				        btns: 2,                    
   				        type: 4,
   				        btn: ["马上认证","暂不认证"],
   				        yes: function(){
   				        	location.href=$("#txtSetUrl").val()+"/userInfo/settings?set=name";
   				        }	
   				    }
   				});	
   			}    
		},"json");
	});
	
    $("#mzh_rzfwd").click(function(){
    	if($(this).attr("class") == "gygl_xxk_no"){
	    	var html ='当前位置：<a href="/walletMgt/index">钱包管理</a>><span>银行卡</span>';
			$("#breadcrumb").empty().append(html);
			$("#leftMenu li.now").removeClass("now");
			$("#leftMenu li").eq(1).addClass("now")
			$("#navTab").load("/companyAccount/info.ajax", function(data){});
		}
    })
	
});
	
</script>
</head>
<body class="bg_f3">
	<div style="border: 0px; background: none;" class="fr conter_right">
		<div id="bankdiv" class="fl conter_right mt_20  bg_white bor_si">
			<div class="gygl_xxk_t f16 ndfxs_1-2_border">
                <div class="gygl_xxk_yes" id="mzh_pfh">个人账户<span style="width: 60px;"></span></div>
                <c:if test="${ userinfo.yunS ==1 || userinfo.batchS ==1 }">
                	<div class="gygl_xxk_no" id="mzh_rzfwd">对公账户<span style="width:60px;"></span></div>
                </c:if>
            </div>
			<c:forEach items="${cards}" var="card">
				<div class="yhk_yhk">
					<div class="yhk_yhk_1">
						<img class="yhk_yhk_1_img" src="/statics/pic/bank_icon_004.png"> 
						<span class="yhk_yhk_1_span">${card.banckName}</span>
					</div>
					<div class="yhk_yhk_2">
						<span class="yhk_yhk_2_span"><b>尾号${fn:substring(card.banckCard, card.banckCard.length()-4, card.banckCard.length())}</b></span> 
						<img class="yhk_yhk_1_cxk" 
						<c:choose>
							<c:when test="${card.cardType==2}">
								src="/statics/pic/cxk_3_24.png"
							</c:when>
							<c:when test="${card.cardType==3}">
								src="/statics/pic/xyk_3_24.png"
							</c:when>
						</c:choose>
						>
						<div class="yhk_yhk_2_close" value="${card.id}"></div>
					</div>
				</div>	
			</c:forEach>
			<div class="yhk_yhk_0">
				<a id="addBtn" href="javascript:;" ahref="<%=basePath%>bankCardMgt/add" to="navTab"><img src="/statics/pic/addyhk_3_24.png"></a>
			</div>
			<div class="blank"></div>
		</div>
	</div>
</body>
</html>
