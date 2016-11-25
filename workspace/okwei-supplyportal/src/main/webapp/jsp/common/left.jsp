<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page import="java.util.ResourceBundle"%>
<%
    String mydomain = ResourceBundle.getBundle("domain").getString("mydomain");
	String sellerdomain = ResourceBundle.getBundle("domain").getString("sellerdomain");
	String okweidomain = ResourceBundle.getBundle("domain").getString("okweidomain"); 
%>
<div id="leftMenu" class="fl conter_left">
	<div class="p10">
		<div name="menu_top">
			<h2>
				<img class="ico_5" src="/statics/images/space.gif"><span
					name="node">产品管理</span><i></i>
			</h2>
			<ul>
				<li name="leaf_node"><a href="/publishProduct/index">发布产品</a></li>
				<li name="leaf_node"><a href='/manage/list'>我的产品</a></li>
			</ul>
		</div>
		<div name="menu_top">
			<h2>
				<img class="ico_6" src="/statics/images/space.gif"><span
					name="node">订单管理</span><i></i>
			</h2>
			<ul>
				<li name="leaf_node" data="/order/reservelist"><a
					href='/order/buylist'>我的销售订单</a></li>
			</ul>
		</div>
		<div name="menu_top">
			<h2>
				<img class="ico_3" src="/statics/images/space.gif"><span
					name="node">商城管理</span><i></i>
			</h2>
			<ul>
				<li name="leaf_node"><a href='<%=sellerdomain%>/userinfo/ing'>微商城</a></li>
				<li name="leaf_node"><a href='<%=sellerdomain%>/userinfo/ing'>客服QQ</a></li>
				<li name="leaf_node"><a href='<%=sellerdomain%>/userinfo/ing'>品牌认证</a></li>
			</ul>
		</div>
	</div>
</div>