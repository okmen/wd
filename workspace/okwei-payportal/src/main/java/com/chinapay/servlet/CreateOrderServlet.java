/**
 * 
 */
package com.chinapay.servlet;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.chinapay.bean.PaymentBean;
import com.chinapay.util.Config;

import chinapay.PrivateKey;
import chinapay.SecureLink;

/**
 * @author Jackie.Gao
 * 
 */
public class CreateOrderServlet extends HttpServlet {

	private static final String KEY_CHINAPAY_MERID = "chinapay.merid";
	private static final String KEY_CHINAPAY_MERKEY_FILEPATH = "chinapay.merkey.filepath";
	private static final String PAYMENT_URL = "chinapay.payment.url";
	private static final String VERSION_INPUT_JSP = "/template/versionInput.jsp";
	private static final String CREATE_ORDER_INPUT_JSP = "/template/createOrderInput.jsp";
	private static final String CREATE_ORDER_COMMIT_JSP = "/template/createOrderCommit.jsp";

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String Version = req.getParameter("version");
		if (null != Version && !("").equals(Version)) {
			Version = req.getParameter("version");
		} else {
			req.getRequestDispatcher(VERSION_INPUT_JSP).forward(req, resp);
			return;
		}
		List errorList = new ArrayList();
		String MerId = null;
		try {
			Properties config = Config.getInstance().getProperties();
			MerId = config.getProperty(KEY_CHINAPAY_MERID);
			System.out.println(MerId);
		} catch (Exception e) {
			errorList.add("errors_properties_init_failed");
		}
		if (!errorList.isEmpty()) {
			req.setAttribute("errors", errorList);
			req.getRequestDispatcher(CREATE_ORDER_INPUT_JSP).forward(req, resp);
			return;
		}

		SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
		SimpleDateFormat sf2 = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat sf3 = new SimpleDateFormat("HHmmss");
		SimpleDateFormat sf4 = new SimpleDateFormat("SSSS");
		Date dt = new Date();
		String OrdId = null;
	
			OrdId = "00" + sf.format(dt);
		String TransAmt = "000000000001";
		String TransDate = sf2.format(dt);
		req.setAttribute("version", Version);
		req.setAttribute("merId", MerId);
		req.setAttribute("OrdId", OrdId);
		req.setAttribute("TransAmt", TransAmt);
		req.setAttribute("TransDate", TransDate);
		req.getRequestDispatcher(CREATE_ORDER_INPUT_JSP).forward(req, resp);
		return;
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		List errorList = new ArrayList();
		String MerKeyPath = null;
		String pay_url = null;
		String plain="";
		try {
			Properties config = Config.getInstance().getProperties();
			MerKeyPath = config.getProperty(KEY_CHINAPAY_MERKEY_FILEPATH);
			pay_url = config.getProperty(PAYMENT_URL);
		} catch (Exception e) {
			errorList.add("errors_properties_init_failed");
		}
		if (!errorList.isEmpty()) {
			req.setAttribute("errors", errorList);
			req.getRequestDispatcher(CREATE_ORDER_INPUT_JSP).forward(req, resp);
			return;
		}

		// ֧����������׼��
		String MerId = req.getParameter("MerId");
		String OrdId = req.getParameter("OrdId");
		String Version = req.getParameter("Version");
		String TransAmt = req.getParameter("TransAmt");// 12
		String CuryId = req.getParameter("CuryId");// 3
		String TransDate = req.getParameter("TransDate");// 8
		String TransType = "0001";// 4
		String BgRetUrl = req.getParameter("BgRetUrl");
		String PageRetUrl = req.getParameter("PageRetUrl");
		String GateId = req.getParameter("GateId");
		String Priv1 = req.getParameter("Priv1");
		/*
		 * try { Priv1 = Base64Util.base64Encoder(Priv1); } catch (Exception e1)
		 * { // TODO Auto-generated catch block e1.printStackTrace(); }
		 */
		String ChkValue = null;
		// 20140522������汾����ʹ������ClientIp����
		String ClientIp = null;
		if (req.getHeader("X-Forwarded-For") == null) {
			System.out.println("X-Forwarded-For is null");
			//ClientIp = req.getRemoteAddr();
			ClientIp = "131.252.85.88";
		} else {
			System.out.println("X-Forwarded-For is not null");
			//ClientIp = req.getHeader("X-Forwarded-For");
			ClientIp = "131.252.85.88";
		}
		// ����汾����ʹ������6�����ݣ����ڰ汾����Ҫ
		String CountryId = req.getParameter("CountryId");
		String TransTime = req.getParameter("TransTime");
		String TimeZone = req.getParameter("TimeZone");
		String DSTFlag = req.getParameter("DSTFlag");
		String Priv2 = req.getParameter("Priv2");
		String ExtFlag = "00";

		boolean buildOK = false;
		int KeyUsage = 0;
		PrivateKey key = new PrivateKey();
		try {
			buildOK = key.buildKey(MerId, KeyUsage, MerKeyPath);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		if (!buildOK) {
			System.out.println("build error!");
			errorList.add("build_key_error!");
		}
		if (!errorList.isEmpty()) {
			req.setAttribute("errors", errorList);
			req.getRequestDispatcher(CREATE_ORDER_INPUT_JSP).forward(req, resp);
			return;
		}

		try {
			SecureLink sl = new SecureLink(key);

			if (Version.equalsIgnoreCase("20141120")) {
				// 20141120�汾ǩ������
				ChkValue = sl.Sign(MerId + OrdId + TransAmt + CuryId
						+ TransDate + TransType + Version + BgRetUrl + PageRetUrl + GateId + Priv1);
				System.out.println(MerId + OrdId + TransAmt + CuryId
						+ TransDate + TransType + Version + BgRetUrl + PageRetUrl + GateId + Priv1);
			}else if (Version.equalsIgnoreCase("20140522")){
				//������20140522�汾
				plain = MerId + OrdId + TransAmt + CuryId + TransDate + TransType + Version + GateId + BgRetUrl+ PageRetUrl + Priv1+ ClientIp;
				ChkValue = sl.Sign(plain);
				System.out.println("The plain of the 20140522: " + plain);
			}
			System.out.println(ChkValue);
		} catch (Exception e) {
			// e.printStackTrace();
			System.out.println(e.getMessage());
			errorList.add(e.getMessage());
		}
		if (!errorList.isEmpty()) {
			req.setAttribute("errors", errorList);
			req.getRequestDispatcher(CREATE_ORDER_INPUT_JSP).forward(req, resp);
			return;
		}
		PaymentBean pay = new PaymentBean();
		pay.setMerId(MerId);
		pay.setOrdId(OrdId);
		pay.setTransAmt(TransAmt);
		pay.setTransDate(TransDate);
		pay.setTransType(TransType);
		pay.setVersion(Version);
		pay.setCuryId(CuryId);
		pay.setGateId(GateId);
		pay.setPageRetUrl(PageRetUrl);
		pay.setBgRetUrl(BgRetUrl);
		pay.setPriv1(Priv1);
		pay.setChkValue(ChkValue);

		// 20140522������汾����ʹ������ClientIp����
		pay.setUserIp(ClientIp);

		// ����汾����ʹ������6�����ݣ����ڰ汾����Ҫ
		pay.setTransTime(TransTime);
		pay.setCountryId(CountryId);
		pay.setDSTFlag(DSTFlag);
		pay.setExtFlag(ExtFlag);
		pay.setTimeZone(TimeZone);
		pay.setPriv2(Priv2);

		req.setAttribute("paybean", pay);
		req.setAttribute("pay_url", pay_url);
		req.getRequestDispatcher(CREATE_ORDER_COMMIT_JSP).forward(req, resp);
		return;
	}

}
