<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<li class="dropdown">
	<a href='<c:out value="${module.funcUrl}" />'>
		<span class="${module.iconClass}"></span> 我的订单
	</a>
	<ul>
		<li class="sub-menu">
			<a href="javascript:void(0)" ahref="${module.funcUrl}" act="loadPage" to="navTab">
				<span class="${module.iconClass}"></span> 我的购买订单
			</a>
		</li>
	</ul>
</li>
<li class="dropdown">
	<a href='<c:out value="${module.funcUrl}" />'>
		<span class="${module.iconClass}"></span> 我的分销
	</a>
	<ul>
		<li class="sub-menu">
			<a href="javascript:void(0)" ahref="${module.funcUrl}" act="loadPage" to="navTab">
				<span class="${module.iconClass}"></span> 上架产品
			</a>
		</li>
		<li class="sub-menu">
			<a href="javascript:void(0)" ahref="${module.funcUrl}" act="loadPage" to="navTab">
				<span class="${module.iconClass}"></span> 我上架的产品
			</a>
		</li>
		<li class="sub-menu">
			<a href="javascript:void(0)" ahref="${module.funcUrl}" act="loadPage" to="navTab">
				<span class="${module.iconClass}"></span> 我的分销订单
			</a>
		</li>
		<li class="sub-menu">
			<a href="javascript:void(0)" ahref="${module.funcUrl}" act="loadPage" to="navTab">
				<span class="${module.iconClass}"></span> 我的分销商
			</a>
		</li>
	</ul>
</li>
<li class="dropdown">
	<a href='<c:out value="${module.funcUrl}" />'>
		<span class="${module.iconClass}"></span> 店铺管理
	</a>
	<ul>
		<li class="sub-menu">
			<a href="javascript:void(0)" ahref="${module.funcUrl}" act="loadPage" to="navTab">
				<span class="${module.iconClass}"></span> 我的店铺分类
			</a>
		</li>
		<li class="sub-menu">
			<a href="javascript:void(0)" ahref="${module.funcUrl}" act="loadPage" to="navTab">
				<span class="${module.iconClass}"></span> 进入我的微店
			</a>
		</li>
	</ul>
</li>




