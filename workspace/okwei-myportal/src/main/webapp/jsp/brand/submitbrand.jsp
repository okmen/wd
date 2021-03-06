<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>品牌认证</title>
<link rel="stylesheet" type="text/css" href="http://base1.okwei.com/css/web/pc-base.css" />
<link rel="stylesheet" type="text/css" href="http://base2.okwei.com/css/web/home-page.css" />
<script type="text/javascript" src="/statics/js/uploadify/jquery.uploadify-3.1.js"></script>
<script type="text/javascript" src="/statics/js/brand/submitbrand.js"></script>
<style type="text/css">
	#btnUpLogo-queue,#btnUpAuto-queue {display:none;}
</style>
</head>
<body>
    	<div class="w fl mt30 bg-w">
        	<div class="tc f16"><input type="checkbox" checked="checked" id="ckbRead" />&nbsp;&nbsp;<label for="ckbRead">我已经详细知悉并同意上述《品牌认证规则》</label></div>
            <div class="w fl ulfonz mt20">
            	<ul>
                	<li>
                    	<span class="spaic1"><font>*</font>品牌名：</span>
                        <span class="spaic2"><input type="text" id="txtBrandName" maxlength="20" value="${brandInfo.brandName }" />
                        	<!-- <span class="f14 ft_red error_font" ><img src="images/c-ico1.png" width="20" height="20" />输入错误</span> -->
                        </span> 
                    </li>
                    <li>
                    	<span class="spaic1"><font>*</font>品牌LOGO：</span>
                        <span class="spaic2">
                        	<div class="scimgs" ${brandInfo.brandLogo !=null && brandInfo.brandLogo !="" ?'':'style="display:none;"'  } ><img id="imgBrandLogo" src="${brandInfo.brandLogo }" width="150" height="86" /></div>
                        	<div class="scunbot fl"><span id="btnUpLogo">上传</span></div>
                            <span class="c9 li40 ml15">logo的统一尺寸和排版标准为：100px*50px .jpg格式</span> 
                        </span> 
                    </li>
                    <li>
                    	<span class="spaic1"><font>*</font>品牌授权书：</span>
                        <span class="spaic2">
                       		<div class="scimgs" ${brandInfo.authorization !=null && brandInfo.authorization !="" ?'':'style="display:none;"'  }><img id="imgBrandAuot" src="${brandInfo.authorization }" width="150" height="86" /></div>
                        	<div class="scunbot fl"><span id="btnUpAuto">上传</span></div>
                            <span class="c9 li40 ml15">(代理商请提交代理授权书、原厂请提交商标证书)</span> 
                        </span> 
                    </li>
                    <li>
                    	<span class="spaic1"><font>*</font>品牌所属分类：</span>
                        <span class="spaic2">
                        	<c:if test="${classList !=null && classList.size()>0 }">
                        	<c:forEach items="${classList }" var="parent">
                        	<p class="f14 li40 tb">${parent.parentCName }</p>                 	
                            <div class="bel_chboc">
                            <c:if test ="${parent.classChildVOs !=null && parent.classChildVOs.size()>0 }">
							<c:forEach items="${parent.classChildVOs }" var="child">      
                            	<span>
                    			     <input value="${child.classID }" id="class${child.classID}"
                    			     ${child.checked==true ?'checked="checked"':'' } type="checkbox" />&nbsp;&nbsp;
                            		<label for="class${child.classID}">${child.className }</label>
                           		</span>
                            </c:forEach>
                            </c:if> 
                            </div>
   
                            </c:forEach>
                            </c:if>                           
                        </span> 
                    </li>
                    <li>
                    	<span class="spaic1"><font>*</font>品牌故事：</span>
                        <span class="spaic2">
                        	<textarea class="bor_si" placeholder="不少于140字" id="txtBrandStory">${brandInfo.brandStory }</textarea>                     	
                        </span> 
                    </li>
                    <li>
                    	<span class="spaic1"><font>*</font>联系方式：</span>
                        <span class="spaic2"><input type="text" id="txtPhone" maxlength="20"   value="${brandInfo.phone }"/>
                        	<span class="c9 li40 ml10">我们会通过电话进行验证，请务必正确填写</span> 
                        </span> 
                    </li>
                    <li>
                    	<span class="spaic1"><font>*</font>联系人：</span>
                        <span class="spaic2"><input type="text" id="txtLinkMan" maxlength="20" value="${brandInfo.linkMan }" />
                        </span> 
                    </li>
                    <li>
                    	<span class="spaic1">职务：</span>
                        <span class="spaic2"><input type="text" id="txtJob" value="${brandInfo.job }" />
                        </span> 
                    </li>
                    <li><input type="submit" id="btnSubmit" value="提交" /></li>                
                </ul>          
            </div>       
        </div>
<!-- heidden区 -->
<input type="hidden" id="txtBrandID" value="${brandInfo.brandID }" />
</body>
</html>