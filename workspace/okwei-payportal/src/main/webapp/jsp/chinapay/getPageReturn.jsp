<%@ page language="java" contentType="text/html; charset=GBK"
    pageEncoding="GBK"%>
<%@ page import="java.util.*"%>
<jsp:useBean id="errors" scope="request" class="java.util.ArrayList" />
<jsp:useBean id="payResult" scope="request" class="com.chinapay.bean.PaymentBean" />
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<link rel="stylesheet" type="text/css" href="./css/style.css">
<meta http-equiv="Content-Type" content="text/html; charset=GBK">
<title>ChinaPay ��������֧��</title>
</head>
<%
if (errors.size() > 0) {
%>
<hr>
	<font color="red">
<%
	java.util.Iterator it1 = errors.iterator();
	while (it1.hasNext()) {
		out.println("<p>");
		Object obj = it1.next();
		if (obj instanceof java.util.ArrayList) {
			java.util.Iterator it2 = ((java.util.ArrayList)obj).iterator();
			while (it2.hasNext()) {
				Object obj2 = it2.next();
				out.println(obj2 + "<br>");
			}
		}
		else {
			out.println(obj);
		}
		out.println("</p>");
	}
%>
	</font>
	<hr>
<%
}
%>

<br>
<p class="menu">
	<font size="2">
			<a href="./" class="menu">��ҳ</a>
			&gt; <a href="./createOrder" class="menu">֧������</a>
			&gt; �汾ѡ��
			&gt; ֧������������װ
			&gt; ֧���������ɲ��ύ
			&gt; ����֧���ɹ�֪ͨ
	</font>
</p>
<%
	String  MerId = payResult.getMerId();
	String  OrdId = payResult.getOrdId();
	String  TransDate = payResult.getTransDate();
	String  TransType = payResult.getTransType();
	String  TransAmt = payResult.getTransAmt();
	String  CuryId = payResult.getCuryId();
	String  GateId = payResult.getGateId();
	String  Version	= payResult.getVersion();
	String  ChkValue = payResult.getChkValue();
	String  BgRetUrl = payResult.getBgRetUrl();
	String  PageRetUrl = payResult.getPageRetUrl();
	String  Priv1 = payResult.getPriv1();
	String  ClientIp = "";//payResult.getClientIP();
%>	
		<table border="1" cellpadding="2" cellspacing="0" style="border-collapse: collapse" bordercolor="#111111">
			<tr>
				<td>
					<font color=red>*</font>�̻���
				</td>

				<td>
                     <%= MerId %>
                </td>
			</tr>
			<tr>
				<td>
					<font color=red>*</font>������
				</td>

				<td>
                     <%= OrdId %>
                </td>
			</tr>
			<tr>
				<td>
					<font color=red>*</font>�̻�����
				</td>

				<td>
                     <%= TransDate %>
                </td>
			</tr>
			<tr>
				<td>
					<font color=red>*</font>��������
				</td>

				<td>
                     <%= TransType %>
                </td>
			</tr>
			<tr>
				<td>
					<font color=red>*</font>���ױ���
				</td>

				<td>
                     <%= CuryId %>
                </td>
			</tr>
			<tr>
				<td>
					<font color=red>*</font>�������
				</td>

				<td>
                     <%= TransAmt %>
                </td>
			</tr>
			<tr>
				<td>
					֧�����غ�
				</td>

				<td>
                     <%= GateId %>
                </td>
			</tr>
			<tr>
				<td>
					<font color=red>*</font>�汾��
				</td>

				<td>
                     <%= Version %>
                </td>
			</tr>
			<tr>
				<td>
					ҳ��Ӧ�����URL
				</td>

				<td>
                     <%= PageRetUrl %>
                </td>
			</tr>
			<tr>
				<td>
					<font color=red>*</font>��̨Ӧ�����URL
				</td>

				<td>
                     <%= BgRetUrl %>
                </td>
			</tr>
			<tr>
				<td>
					�̻�˽����
				</td>

				<td>
                     <%= Priv1 %>
                </td>
			</tr>
			<tr>
				<td>
					<font color=red>*</font>ChinaPay����ǩ��
				</td>

				<td width="800">
                     <pre><%= ChkValue %></pre>
                </td>
			</tr>

		</table>	

	<hr>

<script language=JavaScript>
	//document.payment.submit();
</script>	

<body>

</body>
</html>