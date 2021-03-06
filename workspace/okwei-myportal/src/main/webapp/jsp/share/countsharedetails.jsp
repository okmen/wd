<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="pg" uri="http://www.okwei.com/pagination"%>
<%@ page import="java.util.ResourceBundle"%>
<%
    String mydomain = ResourceBundle.getBundle("domain").getString("mydomain");
	String sellerdomain = ResourceBundle.getBundle("domain").getString("sellerdomain");
	String okweidomain = ResourceBundle.getBundle("domain").getString("okweidomain");
	String jsdomain = ResourceBundle.getBundle("domain").getString("jsdomain");
	String cssdomain = ResourceBundle.getBundle("domain").getString("cssdomain");
	String imgdomain = ResourceBundle.getBundle("domain").getString("imgdomain");
	String detaildomain = ResourceBundle.getBundle("domain").getString("detaildomain");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>统计明细</title>
<link rel="stylesheet" href="<%=request.getContextPath()%>/statics/css/glbdy.css" />
<link rel="stylesheet" href="<%=request.getContextPath()%>/statics/css/index.css" />
<link rel="stylesheet" href="<%=request.getContextPath()%>/statics/css/mzh_dd.css" />
<link rel="stylesheet" href="<%=request.getContextPath()%>/statics/css/jumei_usercenter.min.css" />
<script type="text/javascript" src="<%=request.getContextPath()%>/statics/js/share/sharedetails.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/statics/js/common/share.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/statics/js/layer/layer.min.js"></script>
 <style>
    .conter_right tr{border-bottom: 1px solid #ddd;}
    .conter_right tr td{border-right: 1px solid #ddd;}
    .xh-shurk{height: auto;}
	.shen{float: left;width: 300px;text-align: center;height: 42px;display: block;white-space: nowrap;overflow: hidden;text-overflow: ellipsis;}
</style>
</head>
<body>	 
<input type="hidden" value="<%=mydomain%>" name="mydomain" id="mydomain">
	<form id="searcherForm" name="searcherForm"  action="countdetails">
	<input type="hidden" name="shareId" value="${shareId}"/> 
	  <div class="fr conter_right">
      <div class="zhuag_suv bor_si fl bg_white">
        <div class="xh-shurk" style="margin: 10px;">
            <a href="<%=mydomain%>/share/sharecount" class="ft_lan f14 fl">&lt;返回列表</a>
        </div>
      </div>
      <div class="zhuag_suv bor_si fl bg_white mt_20">
        <div class="xh-shurk">
          <ul>
              <li class="mzh_100 fw_b" style="color: #333;">【${countSharedetails.title }】数据统计：</li>
              <li class="mzh_100">
                  <span class="fl mr_20">标题： ${countSharedetails.title }</span>
                  <span class="fl mr_20">产品数量： ${countSharedetails.pcount }</span>
              </li>
              <li class="mzh_100">
                  <span class="fl mr_20">浏览量：${countSharedetails.pv }</span>
                  <span class="fl mr_20">分享次数： ${countSharedetails.sv }</span>
                  <span class="fl mr_20">成交笔数：${countSharedetails.vol }</span>
                  <span class="fl mr_20">成交金额： ￥${countSharedetails.turnover }</span>
                  <%-- <span class="fl mr_20">佣金： ￥${countSharedetails.commission }</span> --%>
              </li>
          </ul>
        </div>
      </div> 
      <table class="conter_right mt_20 fl bg_white bor_si f14 line_42 tc">
          <tr>
              <th colspan="8" class="tl pl20">【${countSharedetails.title }】数据详情：</th>
          </tr>
          <tr class="bg_f3">
              <th>产品标题</th>
              <th>产品图片</th>
              <!-- <th>浏览量</th>
              <th>分享次数</th> -->
              <th>成交笔数</th>
              <th>成交金额(￥)</th>
            <!--   <th>佣金(￥)</th> -->
              <th>操作</th>
          </tr>
         <c:forEach var="share" items="${pageResult.list}" varStatus="varStr">
	          <tr>
	              <td><div class="shen">${share.productTitle}</div></td>
	              <td><img src="${share.defaultImg}" width="80" class="mt_10 mb_10"></td>
	              <%-- <td>${share.pv}</td>
	              <td>${share.sv}</td> --%>
	              <td>${share.vol}</td>
	              <td>${share.vol*share.turnover}</td>
	             <%--  <td>${share.vol*share.commission*0.2}</td> --%>
	              <td>
	                  <a href="<%=mydomain%>/share/sharedetails?shareId=${countSharedetails.shareId}" class="ft_lan">分享页详情</a>
	              </td>
	          </tr>
          </c:forEach>
        
      </table>
    </div>
    <!-- 分页 -->
		<div class="pull-right">
			<pg:page pageResult="${pageResult}" />
		</div>
	</form> 
    	<script>
		$(function() {
			var page = new Pagination({
				formId : "searcherForm",
				isAjax : false,
				targetId : "navTab",
				submitId : "searchBtn",
				validateFn : false
			});
			page.init();
			 
		});
		</script>
</body>
</html>