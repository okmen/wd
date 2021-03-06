<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>微店网-注册 云商微店旗下产品</title>
<link rel="stylesheet" type="text/css" href="/statics/css/ysdd-reg.css?v=1" />
<script type="text/javascript" src="http://base1.okwei.com/js/jquery-1.7.1.min.js"></script>
<script type="text/javascript" src="/statics/js/common.js?v=1"></script>
<script type="text/javascript" src="/statics/js/xiangqing.js?v=1"></script>
<script type="text/javascript" src="/statics/js/OtherRegist.js?v=1"></script>

<style type="text/css">
.mzh_mfzc_tjr dd {
	width: 100px;
}

.mzh_mfzc_tjr {
	width: 750px;
}

.mzh_mfzc_tjr dt {
	width: auto;
	float: left;
}

.fang {
	padding: 5px 0px 0px 5px;
	display: none;
}

.mzh_dljm_left {
	width: 530px;
}

.mzh_dljm_right_dl {
	width: 530px;
}
</style>
</head>
<body style="background: #f2f2f2;">
	<div class="top">
		<div class="w-990">
			<div class="topLogo">
				<em><img src="http://base.okimgs.com/images/xh_logo.gif"></em><span>用户登录</span>
			</div>
		</div>
	</div>
	<div class="w-990">
		<div class="mzh_dljm">
			<div class="mzh_dljm_left" style="padding-bottom: 0px; padding-left: 270px;">
				<div class="mzh_dljm_right_dl">
					<ul>
						<li class="mzh_dljm_left_1" style=" border-bottom:5px; display: inline; width: 600px;">快速完成微店网账号创建，完成账号创建后，即可直接登录微店网哦！</li>
						<li style="color: red; padding-left: 100px; margin-bottom: 13px; height: 10px;">
							<div id="liTip" style="display: none">
								<span class="mzh_span" style="height: 10px;"></span><img src="/statics/images/m_jingzhi.png" style="margin-right: 5px;"><font>请输入微店昵称</font>
							</div>
						</li>
						<li>
							<dl class="mzh_mfzc_tjr">
								<dd>手机：</dd>
								<dt>
									<input type="text" id="phoneTxt" maxlength="11" placeholder="手机号">
								</dt>
								<dt>
									<img src="/statics/images/c-ico1.png" width="28" class="fang" />
								</dt>
							</dl>
						</li>
						
						<li class="yzm" style="display: none;">
							<dl class="mzh_mfzc_tjr">
								<dd>输入验证码：</dd>
								<dt>
									<input id="yzm" type="text" style="width: 120px;" placeholder="请输入验证码" value="" />
								</dt>
								<dt>
									<img id="yzmimg" src="/getVImageCode" style="float: left; width: 80px; height: 37px; padding-left: 10px;" />
								</dt>
								<dt>
									<img src="/statics/images/c-ico1.png" width="28" class="fang" />
								</dt>
							</dl>
						</li>
						<li class="sjyzm" style="display: none;">
							<dl class="mzh_mfzc_tjr">
								<dd>手机验证码：</dd>
								<dt>
									<input id="txtCheckCode" type="text" style="width: 120px;" value="" placeholder="验证码" />
								</dt>
								<dt>
									<span class="mzh_yzmdxfs" id="sendCheckCode">获取验证码</span>
								</dt>
								<dt>
									<img src="/statics/images/c-ico1.png" width="28" class="fang" />
								</dt>
							</dl>
						</li>
						<li>
							<dl class="mzh_mfzc_tjr">
								<dd>密码：</dd>
								<dt>
									<input id="passwordTxt1" type="password" placeholder="密码">
								</dt>
								<dt>
									<img src="/statics/images/c-ico1.png" width="28" class="fang" />
								</dt>
							</dl>
						</li>
						<li>
							<dl class="mzh_mfzc_tjr">
								<dd>重复密码：</dd>
								<dt>
									<input id="passwordTxt2" type="password" placeholder="重复密码">
								</dt>
								<dt>
									<img src="/statics/images/c-ico1.png" width="28" class="fang" />
								</dt>
							</dl>
						</li>
						<li>
							<dl class="mzh_mfzc_tjr">
								<dd></dd>
								<dt>
									<span class="mzh_dljm_right_dl_3" id="regist" style="width: 111px;">注册</span>
									<span style="float: left; width: 150px; line-height: 55px; margin-left: 16px; display: inline; color: #999;">已有账号，<a href="/oldlogin" class="mzh_zhmm" style="margin: 0px; float: none;">去绑定</a></span>
								</dt>
							</dl>
						</li>
					</ul>
				</div>
			</div>
		</div>
	</div>


	<!-- 底部 -->

	<div class="mzh-dingwei">
		<div class="mzh-dingwei_1">
			<span style="">深圳市云商微店网络技术有限公司版权所有 经营许可证：粤ICP备10203293号-3 - 增值电信业务经营许可证：粤B2-20130803</span>
		</div>
	</div>
</body>
<input id="sjWeiNo" type="hidden" value="${sjWeiNo }" />

<script type="text/javascript">
	function noNumbers(e) {
		var keynum;
		var keychar;
		var numcheck;
		if (window.event) // IE
		{
			keynum = e.keyCode;
		}
		else if (e.which) // Netscape/Firefox/Opera
		{
			keynum = e.which;
		}
		keychar = String.fromCharCode(keynum);
		numcheck = /^[0-9]*$/;
		return numcheck.test(keychar);
	}
</script>
</html>