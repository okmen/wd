<%@ page language="java" contentType="text/html; charset=GBK" pageEncoding="GBK"%>
<%@ page import="com.tenpay.TenpayUtil"%>
<%@ page import="com.tenpay.MD5Util"%>
<%@ page import="com.wxpay.WxRequestHandler"%>
<%@ page import="com.wxpay.WxResponseHandler"%>
<%@ page import="com.tenpay.TenpayHttpClient"%>
<%@ include file="config.jsp"%>
<%
	//=================================
//άȨ�ӿ�
//=================================
	//����֧��Ӧ�����
	WxRequestHandler queryReq = new WxRequestHandler(null, null);
	queryReq.init();
	//��ʼ��ҳ���ύ�����Ĳ���
	WxResponseHandler resHandler = new WxResponseHandler(request, response);
	resHandler.setKey(APP_KEY);
	
	//�ж�ǩ��
if(resHandler.isWXsign() == true) {
	//�ظ������������ɹ�
	System.out.print("ok");
	}
	else{
	//SHA1ǩ��ʧ��
	System.out.print("fail");
	}
%>