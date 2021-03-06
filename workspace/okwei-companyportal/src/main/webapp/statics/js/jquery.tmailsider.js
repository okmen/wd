// JavaScript Document
/*
	TMail Sider - jQuery TMail Sider Plugin
	Author: 		澶忋伄瀵掗
	Version:		1.0 (2012/12/28)
	QQ:				490720430
	Please use this development script if you intend to make changes to the plugin code.
	For production sites, please use jquery.tmailsider.js.
*/

(function($) {
	var Z_TMAIL_SIDE_DATA = new Array();  
	
	$.fn.Z_TMAIL_SIDER = function(options) {
		var opts = $.extend( {}, $.fn.Z_TMAIL_SIDER.defaults, options);
		var base = this;
		var target = opts.target;
		var Z_MenuList = $(base).find('.Z_MenuList');
		var Z_SubList = $(base).find('.Z_SubList');
		
		initPosition();

		var isIE = navigator.userAgent.indexOf('MSIE') != -1;
	    var isIE6 = isIE && ([/MSIE (\d)\.0/i.exec(navigator.userAgent)][0][1] == '6');

	 	 
		$(window).resize(function() {
			initPosition();
		});
		
	    //if(isIE6) return;
	    
		$(Z_MenuList).find('li').live('mouseover', function(e, index) {
			var thisLi = this;
			var timeOut = setTimeout(function() {
				showSubList(thisLi);
			},200);
			$(Z_SubList).hover(function() {
				clearTimeout(timeOut);
			},function() {
				hideSubList(thisLi);
			});
			e.stopPropagation();
		}).live('mouseout', function(e) {
			var thisLi = this;
			var timeOut = setTimeout(function(){
				hideSubList(thisLi);
			}, 200);
			$(Z_SubList).hover(function() {
				clearTimeout(timeOut);
			},function() {
				hideSubList(thisLi);
			});
			e.stopPropagation();
		});
		
		if(!isIE6) {
			$(base).find('.title').append('<s class="btn_group bright"><a class="bleft"></a><a class="bright"></a></s>');
			$(base).find('.title .btn_group a').click(function() {
				if($(this).attr('class') == 'bleft' && $(this).parent().attr('class') == 'btn_group bleft') {
					$(base).find('.title .btn_group').attr('class', 'btn_group bright');
				
					opts.autoExpan = true;
				}
				if($(this).attr('class') == 'bright' && $(this).parent().attr('class') == 'btn_group bright') {
					$(base).find('.title .btn_group').attr('class', 'btn_group bleft');
				
					opts.autoExpan = false;
				}
			});
		}
		
		/*if(!isIE6) {
			$(Z_MenuList).find('li').click(function() {
				$(this).find('p').slideToggle(500);
			});
		}
*/
		function showSubList(thisLi) {
			$("#winhide").width(1200)
			if(!isIE6) {
				$(thisLi).append('<s class="menuIcon"></s>');
				$(thisLi).addClass('curr');
			}
			var thisIndex = $(Z_MenuList).find('li').index($(thisLi));
			var subExList = $(Z_SubList).find('div');
			if(thisIndex > subExList.length - 1) return;
			
			var winHeight = $(window).height();
			var subTop = $(thisLi).offset().top - $(window).scrollTop();
			var subBottom = winHeight - subTop - $(Z_SubList).height();
			
			var absTop = $(thisLi).offset().top - opts.fTop;
			if(isIE6) {
				absTop = $(thisLi).offset().top - opts.fTop;
			}
			var absLeft = $(target).offset().left - $(base).offset().left;
			if(subBottom < 40) {
				absTop = absTop + subBottom - 40;
			}
			
			$(subExList).each(function(index) {
				if(index == thisIndex) {
					$(this).show();
				} else {
					$(this).hide();
				}
			});
			
			$(Z_SubList).show().stop().animate({
				top: absTop-107,
				left: 200
			}, 100);
		};

		function hideSubList(thisLi) {
			$("#winhide").width("auto")
			if(!isIE6) {
				$(thisLi).find('.menuIcon').remove();
				$(thisLi).removeClass('curr');
			}
			$(Z_SubList).hide();
		};
		
		 
		function initPosition() {
			if($(base).css('position') == 'absolute') {
				$(base).css({
					top: $(target).offset().top, 
					left: $(target).offset().left - $(base).width()
				}).show();

				$(Z_SubList).css({
					top: $(target).offset().top - 60,
					left: $(target).offset().left - $(base).offset().left
				});
			}
			if($(base).css('position') == 'fixed') {
				$(base).css({
					top: opts.fTop, 
					left: $(target).offset().left - $(base).width()
				}).show();

				$(Z_SubList).css({
					top: opts.cTop - 60,
					left: $(target).offset().left - $(base).offset().left
				});
			}
		};

		 
		function OpenOrCloseMenu(l) {
			var mList = $(Z_MenuList).find('ul li');
			for(var i = 0; i < l; i++) {
				if(i < mList.length) {
					var thisLi = $(mList[i]);
					$(thisLi).find('p').slideUp(500, function (){
						$(this).hide();
					});
				}
			}
			
			for(var i = mList.length - 1; i >= l; i--) {
				if(i >= 0) {
					var thisLi = $(mList[i]);
					$(thisLi).find('p').slideDown(500, function (){
						$(this).show();
					});
				}
			}
		};
		
		 
		var scrollTimeOut;
		$(window).scroll(function() {
			if(!opts.autoExpan) return;
			if(isIE6) return;
			clearTimeout(scrollTimeOut);
			var sTop = $(window).scrollTop();
		});
	};
	
	 
	
	$.fn.Z_TMAIL_SIDER.defaults = {
		target: $('#Z_RightSide'),
		fTop: 60, 
		cTop: 100, 
		unitHeight: 80, 
		autoExpan: true
	};
})(jQuery);