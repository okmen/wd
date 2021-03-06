<%@ page language="java" import="java.util.*"
	contentType="text/html; charset=UTF-8"%>
<%@ page import="java.util.ResourceBundle"%>

<% 
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";

	Date curDate = new Date();
	pageContext.setAttribute("VERSION", curDate.getTime(), PageContext.APPLICATION_SCOPE);
	pageContext.setAttribute("JSESSIONID", session.getId(), PageContext.SESSION_SCOPE);
%>
<script>
	window.basePath = "<%=basePath %>";
</script>
<title>首页</title>
<!-- 前端css库 start-->
<link rel="stylesheet" type="text/css" href="<%=path %>/statics/css/pc-base.css" />
<link rel="stylesheet" type="text/css" href="<%=path %>/statics/css/header.css" />
<link rel="stylesheet" type="text/css" href="<%=path %>/statics/css/footer.css" />
<link rel="stylesheet" type="text/css" href="<%=path %>/statics/css/pagination.css" />
<link rel="stylesheet" type="text/css" href="<%=path %>/statics/css/ysdd-reg.css" />
<link rel="shortcut icon" href="/statics/images/favicon.ico">
<!-- 前端css库 end-->

<!-- 前端js库 start-->
<script type="text/javascript" src="<%=path %>/statics/js/jquery-1.7.1.min.js"></script>
<%-- <script type="text/javascript" src="<%=path %>/statics/js/common/extends_fn.js?_=${VERSION}"></script>
<script type="text/javascript" src="<%=path %>/statics/js/common/pagination.js?_=${VERSION}"></script>
<script type="text/javascript" src="<%=path%>/statics/js/jquery.lazyload.js"></script> --%>
<script type="text/javascript" src="<%=request.getContextPath() %>/statics/js/layer/layer.min.js"></script>
<!-- 前端js库 end-->
<script type="text/javascript">
function alert(msg,bool){
	if(bool){
		layer.msg(msg, 2, 1);//绿色的钩钩
	}else{
		layer.msg(msg, 2, 8);//不高兴的脸
	}	
}
</script>