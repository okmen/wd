var onlineTimer;
$(function() {
	$.ajaxSetup({
		beforeSend : function() {
		},
		complete : function(info) {
			if (!info.status) {
				return;
			}
			switch (info.status)
			{
				case 200:
					if (this.dataType == "json" || (info.getResponseHeader("Content-Type") && info.getResponseHeader("Content-Type").indexOf("json")) > 0) {
						var _data = eval('(' + info.responseText + ')');
						if (_data.error) {

						}
						if (_data.status == "302") {
							alert("请重新登录！");
							location.href = "login";
						}
					}
					break;
				case 302:
					alert("请重新登录！");
					location.href = "login";
					break;
				case 500:
					alert("操作失败！");
					break;

				// plog(arguments);
			}
		},
		cache : true
	});

	part.undelegate("[act='loadPage']", "click").delegate("[act='loadPage']", "click", function() {
		var t = $(this);
		var to = $("#" + t.attr("to"));
		var url = t.attr("ahref");
		/* var loader = new Loading().start(); */
		to.load(url + ".ajax", function(data) {
			/*
			 * initDomEvent(); histy.addUrl(url); loader && loader.stop();
			 */
		});
	});

	navTab.undelegate("[act='delete']", "click").delegate("[act='delete']", "click", function() {
		var t = $(this);
		var url = t.attr("ahref");
		$.ajax({
			url : url,
			success : function(data) {
				if (!data.error) {
					alert("删除成功！");
					histy.back(1);

				}
				else {
					alert("删除失败！");
				}
			}
		});
	});
	navTab.undelegate("[act='back']", "click").delegate("[act='back']", "click", function() {
		histy.back(1);
	});
	navTab.undelegate("input[act='checkNumber']", "keyup").delegate("input[act='checkNumber']", "keyup", function() {
		this.value = this.value.replace(/[^\d]/g, '');
	});
	navTab.undelegate("[act='reset']", "click").delegate("[act='reset']", "click", function() {
		var t = $(this);
		var form = this.form;
		if (form) {
			$(form).find("input,select,textarea").val("");
		}
	});

	// 设置弹出框语言为中文
	/*
	 * bootbox.setDefaults({ locale: "zh_CN" });
	 */

	/*
	 * $.datepicker.regional['zh-CN'] = { closeText: '关闭', prevText: '<上月', nextText: '下月>', currentText: '今天', monthNames: ['一月', '二月', '三月', '四月', '五月', '六月', '七月', '八月', '九月', '十月', '十一月', '十二月' ],
	 * monthNamesShort: ['一', '二', '三', '四', '五', '六', '七', '八', '九', '十', '十一', '十二' ], dayNames: ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六'], dayNamesShort: ['周日', '周一', '周二', '周三', '周四',
	 * '周五', '周六'], dayNamesMin: ['日', '一', '二', '三', '四', '五', '六'], weekHeader: '周', dateFormat: 'yy/mm/dd', firstDay: 1, isRTL: false, showMonthAfterYear: true, yearSuffix: ' 年 ', monthSuffix: ' 月 ',
	 * changeYear: true, changeYear: true, changeMonth: true }; $.datepicker.setDefaults($.datepicker.regional['zh-CN']); $.datepicker.setDefaults({ dateFormat: "yy/mm/dd" });
	 */
	/*
	 * if (onlineTimer) { clearInterval(onlineTimer); delete onlineTimer; } onlineTimer = setInterval(function() { $.ajax({ url: "/setLoginStats", complete: function(info) { if (info.status == 500) {
	 * plog("服务重启中.....!"); } } }); }, 1000 * 60);
	 */

	// 左边菜单、面包屑的切换
	$("li[name=leaf_node]").each(function() {
		var settext = $("#settext").text();
		if ($(this).find("a:first").attr("href") == window.location.pathname || $(this).attr("data") == window.location.pathname || $(this).attr("data") == settext) {
			$(this).attr("class", "now");
			var url = $(this).parents("[name=menu_top]").find("a").first().attr("href");
			var html = '<a href=' + url + '>' + $(this).parents("[name=menu_top]").find("[name=node]").text() + '</a>' + ">" + "<span>" + $(this).text() + '</span>';
			$("#breadcrumb").empty().append(html);
		}
	});
});
