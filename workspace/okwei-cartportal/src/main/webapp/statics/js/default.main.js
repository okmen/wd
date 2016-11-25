//jQuery.noConflict();

jQuery(document).ready(function(){
	
    mainwrapperHeight();
	responsive();
	
	
	// animation
/*	if(jQuery(window)) {
		var anicount = 4;	
		jQuery('.leftmenu .nav-tabs > li').each(function(){										   
			jQuery(this).addClass('animate'+anicount+' fadeInLeft');
			anicount++;
		});
		jQuery('.leftmenu .nav-tabs > li a').hover(function(){
			jQuery(this).find('span').addClass('animate0 swing');
		},function(){
			jQuery(this).find('span').removeClass('animate0 swing');
		});
		
		jQuery('.logopanel').addClass('animate0 fadeInUp');
		jQuery('.datewidget, .headerpanel').addClass('animate1 fadeInUp');
		jQuery('.navListwrapper, .breadcrumbwidget').addClass('animate2 fadeInDown'); 
//		jQuery('.rightpanel').addClass('animate3 fadeInUp');
		jQuery('.maincontent').addClass('animate4 fadeInUp');
		jQuery('.footer').addClass('animate4 fadeInUp');
	}*/
	

 
	
	function mainwrapperHeight() {
		var windowHeight = $(window).height();
//		var iCenterH = windowHeight - $('#header').height() - $('#footer').height();
		var iCenterH = windowHeight - $('#header').height();
		var mainWrapperHeight = $('.mainwrapper').height();
		var navList = iCenterH - $('.navListwrapper').height();
		if(mainWrapperHeight < iCenterH){
			$('.leftpanel, .rightpanel').css({height: iCenterH});	
		}
		if(mainWrapperHeight > windowHeight){
			$('.leftpanel,.rightpanel').css({height: iCenterH});
		}
		$('.leftmenu').css({height:navList});
	}

	function responsive() {
		
		var windowWidth = jQuery(window).width();
		
		// hiding and showing left menu
		if(!jQuery('.showmenu').hasClass('clicked')) {
			
			if(windowWidth < 960)
				hideLeftPanel();
			else
				showLeftPanel();
		}
		// rearranging widget icons in dashboard
		if(windowWidth < 768) {
			if(jQuery('.widgeticons .one_third').length == 0) {
				var count = 0;
				jQuery('.widgeticons li').each(function(){
					jQuery(this).removeClass('one_fifth last').addClass('one_third');
					if(count == 2) {
						jQuery(this).addClass('last');
						count = 0;
					} else { count++; }
				});	
			}
		} else {
			if(jQuery('.widgeticons .one_firth').length == 0) {
				var count = 0;
				jQuery('.widgeticons li').each(function(){
					jQuery(this).removeClass('one_third last').addClass('one_fifth');
					if(count == 4) {
						jQuery(this).addClass('last');
						count = 0;
					} else { count++; }
				});	
			}
		}
	}
	
	// when resize window event fired
	jQuery(window).resize(function(){
		mainwrapperHeight();
		responsive();
	});
	
	// dropdown in leftmenu
	jQuery('.leftmenu .dropdown > a').click(function(){
		if(!jQuery(this).next().is(':visible')){
			jQuery(this).next().slideDown('fast');
			jQuery(this).addClass('leftpanelActive');
		}else{
			jQuery(this).next().slideUp('fast');
			jQuery(this).removeClass('leftpanelActive');
}
		return false;
	});
	
	$(".leftmenu .sub-menu").click(function(){
         $(".sub-menu").removeClass("leftpanelActive");
         $(this).addClass("leftpanelActive");
   });

	
	// 隐藏左边栏目
	function hideLeftPanel() {
		jQuery('.leftpanel').css({marginLeft: '-200px'}).addClass('hide');
		jQuery('.rightpanel').css({marginLeft: 0});
		jQuery('.mainwrapper').css({backgroundPosition: '-200px 0'});
		jQuery('.headerpanel').css({marginLeft: '65px'});;
		jQuery('.logopanel').css({width:'65px'});
//		jQuery('.datewidget').hide();
//		jQuery('.footerright').css({marginLeft: 0});
	}
	
	// 显示左边栏目
	function showLeftPanel() {
		jQuery('.leftpanel').css({marginLeft: '0px'}).removeClass('hide');
		jQuery('.rightpanel').css({marginLeft: '200px'});
		jQuery('.mainwrapper').css({backgroundPosition: '0 0'});
		jQuery('.headerpanel').css({marginLeft: '200px'});
		jQuery('.logopanel').css({width:'200px'});
//		jQuery('.datewidget').show();
//		jQuery('.footerright').css({marginLeft: '200px'});
	}
	
	// 显示与隐藏导航栏
	jQuery('.showmenu').click(function() {
		jQuery(this).addClass('clicked');
		if(jQuery('.leftpanel').hasClass('hide'))
			showLeftPanel();
		else
			hideLeftPanel();
		return false;
	});
	
	
	
});



