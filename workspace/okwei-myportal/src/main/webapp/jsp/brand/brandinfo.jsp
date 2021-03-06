<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>品牌认证信息</title>
<link rel="stylesheet" type="text/css" href="http://base1.okwei.com/css/web/pc-base.css" />
<link rel="stylesheet" type="text/css" href="http://base2.okwei.com/css/web/home-page.css" />
</head>
<body>
		<c:choose>
			<c:when test="${brandInfo.status ==0 }">
			<div class="w tc h40 mt30 fl lin40 f30"><img src="/statics/images/d-ico1.png" width="36" height="36" />&nbsp;&nbsp;提交成功，请耐心等待审核</div>
			</c:when>
			<c:when  test="${brandInfo.status ==2 }">
	    	 <div class="wei_tguo w mt20 fl">
	         	<p class="f30 ml100">您的品牌认证申请未通过</p>
	            <p class="f18 ml100 c9 mt20">不通过理由：${brandInfo.reason }</p>
	            <a href="/brand/submitbrand?brandID=${brandInfo.brandID }" class="block ml100 submitbot">重新提交</a>	       
         	</div>
			</c:when>
		</c:choose>
         <div class="bgimg_news fl mt50">
         	<ul>
            	<li>
                	<span class="rzs_type1 f18">品牌名：</span>
                    <span class="rzs_type2 f18">${brandInfo.brandName }</span>
                </li>
                <li>
                	<span class="rzs_type1 f18">品牌LOGO：</span>
                    <span class="rzs_type2 f18"><img src="${brandInfo.brandLogo}" width="150" height="86" /></span>
                </li>
                <li>
                	<span class="rzs_type1 f18">品牌授权书：</span>
                    <span class="rzs_type2 f18"><img src="${brandInfo.authorization}" width="150" height="86" /></span>
                </li>
                <li>
                	<span class="rzs_type1 f18">品牌所属分类：</span>
                    <span class="rzs_type2 f18">
                    	<c:if test="${brandInfo.cfbVO !=null && brandInfo.cfbVO.size()>0 }">
                    	<c:forEach items="${brandInfo.cfbVO }" var="parent">
                    	<p>${parent.parentCName} -- 
                    	<c:if test="${parent.classChildVOs !=null &&  parent.classChildVOs.size()>0}">
                    	<c:forEach items="${parent.classChildVOs }" var="child">
                    		${child.className }
                    	</c:forEach>
                    	</c:if>                    		
                    	</p>
                    	</c:forEach>
                    	</c:if>
                    </span>
                </li>
                <li>
                	<span class="rzs_type1 f18">品牌故事：</span>
                    <span class="rzs_type2 f18">
                    	<div class="f12">
                        	${brandInfo.brandStory}
                        </div>
                    </span>
                </li>
                <li>
                	<span class="rzs_type1 f18">联系人：</span>
                    <span class="rzs_type2 f18">${brandInfo.linkMan}</span>
                </li>
                <li>
                	<span class="rzs_type1 f18">联系方式：</span>
                    <span class="rzs_type2 f18">${brandInfo.phone}</span>
                </li>
                <li>
                	<span class="rzs_type1 f18">职务：</span>
                    <span class="rzs_type2 f18">${brandInfo.job}</span>
                </li>
            </ul>       
         </div> 
</body>
</html>