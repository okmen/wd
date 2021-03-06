<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:choose>
	<c:when test="${setting.bindPhone == 1 }">
		<!-- 已经绑定手机 --> 
		<div class="fl mzh_100">
				<div class="gygl_xxk_t f16 ndfxs_1-2_border">
					<div class="fl line_42">更换绑定手机</div>
				</div>
			<div id="unbind" class="fl mzh_100">
				<dl class="fl mzh_100 f14 mt_20">
					<dd>已绑定的手机号：</dd>
					<dt>
						${setting.phoneStr }<input name="button" type="submit" class="btn_wear42_pre ml_10" value="点击获取验证码" id="unbind_hqyzm" />
					</dt>
				</dl>
				<dl class="fl mzh_100 f14">
					<dd class=" mt_20">验证码：</dd>
					<dt class=" mt_20">
						<input type="text" class="fl btn_h42 w250 mr_20" id="unyzm" />
					</dt>
				</dl>
				<dl class="fl mzh_100 f14 mt_20">
					<dd></dd>
					<dt class="mt_20">
						<input name="button" type="submit" onclick="unphone()" class="jbzl_dl_bc b" value="下一步" />
					</dt>
				</dl>
			</div>
			<div  id="anewbind"  class="fl mzh_100" style="display: none;">
				<dl class="fl mzh_100 f14 mt_20">
					<dd>新手机号：</dd>
					<dt>
						<input type="text" class="fl btn_h42 w250 mr_20" id="bind_phone2" />
						<input name="button" type="submit" class="btn_wear42_pre ml_10" id="bind_hqyzm2" value="点击获取验证码"/>
					</dt>
				</dl>
				<dl class="fl mzh_100 f14">
					<dd class=" mt_20">验证码：</dd>
					<dt class=" mt_20">
						<input type="text" class="fl btn_h42 w250 mr_20" id="bind_yzm2" />
					</dt>
				</dl>
				<dl class="fl mzh_100 f14 mt_20">
					<dd></dd>
					<dt class="mt_20">
						<input name="button" type="submit" class="jbzl_dl_bc b" value="确定" onclick="bind_phone2()">
					</dt>
				</dl>
			</div>
		</div>
	</c:when>
	<c:otherwise>
		<!-- 未绑定手机 -->
		<div class="fl mzh_100">
			<div class="gygl_xxk_t f16 ndfxs_1-2_border">
				<div class="fl line_42">绑定手机</div>
			</div>
			<dl class="fl mzh_100 f14 mt_20">
				<dd>手机号：</dd>
				<dt>
					<input type="text" id="bind_phone" class="fl btn_h42 w250 mr_20" maxlength="11" /> 
					<input name="button" type="submit" class="btn_wear42_pre" id="bind_hqyzm" value="点击获取验证码" />
				</dt>
			</dl>
			<dl class="fl mzh_100 f14">
				<dd class=" mt_20">验证码：</dd>
				<dt class=" mt_20">
					<input type="text" class="fl btn_h42 w250 mr_20" id="bind_yzm" value="" />
				</dt>
			</dl>
			<dl class="fl mzh_100 f14 mt_20">
				<dd></dd>
				<dt class="mt_20">
					<input name="button" type="submit" class="jbzl_dl_bc b" value="确定" onclick="bindphone()">
				</dt>
			</dl>
		</div>
	</c:otherwise>
</c:choose> 