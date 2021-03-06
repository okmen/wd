<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<script type="text/javascript">
	var bindPhone = '${setting.bindPhone}';
</script>
<!-- 设置登录密码 -->
<div class="mzh_100 fl" id="step1">
	<label id="loginTiper" style="azimuth: center;color: red;"></label>
	<c:choose>
		<c:when test="${setting.loginPwd ==1 }">
			<!-- 已经设置登陆密码 -->
			<div class="gygl_xxk_t f16 ndfxs_1-2_border">
				<div class="fl line_42">修改登录密码</div>
			</div>
			<dl class="fl mzh_100 f14 mt_20">
				<dd>旧登录密码：</dd>
				<dt>
					<input type="password" class="fl btn_h42 w250 mr_20" id="oldPwd"><a href="javascript:forgetLoginPwd();" class="ft_lan">忘记密码？</a>
				</dt>
			</dl>
			<dl class="fl mzh_100 f14 mt_20">
				<dd>新登录密码：</dd>
				<dt>
					<input type="password" class="fl btn_h42 w250 mr_20" id="newPwd">
				</dt>
			</dl>
			<dl class="fl mzh_100 f14 mt_20">
				<dd>确认新登录密码：</dd>
				<dt>
					<input type="password" class="fl btn_h42 w250 mr_20" id="reNewPwd">
				</dt>
			</dl>
			<dl class="fl mzh_100 f14 mt_20">
				<dd></dd>
				<dt>
					<input type="button" value="确认修改" class="jbzl_dl_bc" name="button" id="modifyPWDBtn">
				</dt>
			</dl>
		</c:when>
		<c:otherwise>
			<!-- 未设置登陆密码 -->
			<div class="gygl_xxk_t f16 ndfxs_1-2_border">
				<div class="fl line_42">设置登录密码</div>
			</div>
			<dl class="fl mzh_100 f14 mt_20">
				<dd>登录密码：</dd>
				<dt>
					<input type="password" class="fl btn_h42 w250 mr_20" id="loginPWD">
				</dt>
			</dl>
			<dl class="fl mzh_100 f14 mt_20">
				<dd>确认登录密码：</dd>
				<dt>
					<input type="password" class="fl btn_h42 w250 mr_20" id="reLoginPWD">
				</dt>
			</dl>
			<dl class="fl mzh_100 f14 mt_20">
				<dd></dd>
				<dt>
					<input type="button" value="确认" class="jbzl_dl_bc" name="button" id="setPWDBtn">
				</dt>
			</dl>
		</c:otherwise>
	</c:choose>
	
</div>

<!-- 登录密码设置成功 -->
<div class="mzh_100 fl" style="display: none;" id="step2">
	<div class="gygl_xxk_t f16 ndfxs_1-2_border">
		<div class="fl line_42">登录密码设置成功</div>
	</div>
	<dl class="fl mzh_100 f14 mt_20">
		<dd></dd>
		<dt>
			<input type="submit" value="返回" class="btn_wear42_pre" name="button" onclick="returnSetting();">
		</dt>
	</dl>
</div>

<!-- 登录密码修改成功 -->
<div class="mzh_100 fl" style="display: none;" id="step3">
	<div class="gygl_xxk_t f16 ndfxs_1-2_border">
		<div class="fl line_42">登录密码修改成功</div>
	</div>
	<div class="fl f14 mzh_100 mt_20 ft_c6">下次登录时请使用新密码登录</div>
	<dl class="fl mzh_100 f14 mt_20">
		<input type="submit" value="返回" class="btn_wear42_pre" name="button" onclick="returnSetting();">
	</dl>
</div>

<!-- 找回登录密码 -->
<div class="mzh_100 fl"  style="display: none;" id="step4">
	<div class="gygl_xxk_t f16 ndfxs_1-2_border">
		<div class="fl line_42">找回登录密码</div>
	</div>
	<div class="fl f14 mzh_100 mt_20 ft_c6">您还没有绑定手机，暂不能找回密码</div>
	<div class="fl f14 mzh_100 ft_c6">请先绑定手机</div>
	<dl class="fl mzh_100 f14 mt_20">
		<input type="submit" value="绑定手机" class="btn_wear42_pre fl" name="button" onclick="focusTo('ul_bindPhone');">
		<a href="javascript:void(0)" class="rztd_cx_f10 fl line_42 ml_10" onclick="focusTo('ul_bindPhone');">跳转至绑定手机页面</a>
	</dl>
</div>

<!-- 找回登录密码 -->
<div class="mzh_100 fl"  style="display: none;" id="step5">
<label id="loginFindTiper" style="azimuth: center;color: red;"></label>
	<div class="gygl_xxk_t f16 ndfxs_1-2_border">
		<div class="fl line_42">找回登录密码</div>
	</div>
	<dl class="fl mzh_100 f14 mt_20">
		<dd>已绑定手机号：</dd>
		<dt>
			${setting.phoneStr} <input type="submit" value="点击获取验证码" class="btn_wear42_pre" name="button" onclick="getVerifyCode(5)">
		</dt>
	</dl>
	<dl class="fl mzh_100 f14 mt_20">
		<dd>验证码：</dd>
		<dt>
			<input type="text" class="fl btn_h42 w250 mr_20" id="loginCode">
		</dt>
	</dl>
	<dl class="fl mzh_100 f14 mt_20">
		<dd>新登录密码：</dd>
		<dt>
			<input type="password" class="fl btn_h42 w250 mr_20" id="newFindPwd">
		</dt>
	</dl>
	<dl class="fl mzh_100 f14 mt_20">
		<dd>确认新登录密码：</dd>
		<dt>
			<input type="password" class="fl btn_h42 w250 mr_20" id="reNewFindPwd">
		</dt>
	</dl>
	<dl class="fl mzh_100 f14 mt_20">
		<dd></dd>
		<dt>
			<input type="submit" value="确认修改" class="jbzl_dl_bc" name="button" id="modifyNewFindPwdBtn">
		</dt>
	</dl>
</div>

<!-- 登录密码重置成功 -->
<div class="mzh_100 fl"  style="display: none;" id="step6">
	<div class="gygl_xxk_t f16 ndfxs_1-2_border">
		<div class="fl line_42">登录密码重置成功</div>
	</div>
	<div class="fl f14 mzh_100 mt_20 ft_c6">下次登录时请使用新密码登录</div>
	<dl class="fl mzh_100 f14 mt_20">
		<input type="submit" value="返回" class="btn_wear42_pre" name="button" onclick="returnSetting();">
	</dl>
</div>