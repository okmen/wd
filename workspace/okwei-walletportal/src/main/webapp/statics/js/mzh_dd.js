﻿$(function(){
    $("[name=mzh_fenye]").live("click",function(){
        $("[name=mzh_fenye]").attr("class","ndfxs_1-2_fenye_no");
        $(this).attr("class","ndfxs_1-2_fenye_yes");
    })

    //选项卡
//    var $this = $("[class=gygl_xxk_yes]").width();
//    $("[class=gygl_xxk_yes]").find("span").css({width:$this});
//    $("[name=mzh_xxk]").live("click",function(){
//        var $this = $(this).width();
//        $("[name=mzh_xxk]").attr("class","gygl_xxk_no");
//        $(this).attr("class","gygl_xxk_yes");
//        $(this).find("span").css({width:$this});
//        if($(this).attr("idclick") == 0){
//        	$("[id^=mzh_click_]").hide();
//        	$("#mzh_click_0").show();
//        } else if($(this).attr("idclick") == 1){
//        	$("[id^=mzh_click_]").hide();
//        	$("#mzh_click_1").show();
//        } else if($(this).attr("idclick") == 2){
//        	$("[id^=mzh_click_]").hide();
//        	$("#mzh_click_2").show();
//        } else if($(this).attr("idclick") == 3){
//        	$("[id^=mzh_click_]").hide();
//        	$("#mzh_click_3").show();
//        } else if($(this).attr("idclick") == 4){
//        	$("[id^=mzh_click_]").hide();
//        	$("#mzh_click_4").show();
//        }
//    })
    //选项卡改（2015-6-15）
    $("[name=mzh_xxk]").each(function(i){
        $(this).click(function(){
            $("[name=mzh_xxk]").attr("class","gygl_xxk_no");
            $(this).attr("class","gygl_xxk_yes");
            $("[id^=mzh_qb_]").hide();
            $("#mzh_qb_"+i).show();
        })
    })



    /* 4-3-3（供应商管理）发布产品(yes-no) */
	 $("[name=mzh_lmbt_one_ck_no]").click(function(){
		$("[name=mzh_lmbt_one_ck_no]").attr("class","mzh_lmbt_one_ck_no")
		$(this).attr("class","mzh_lmbt_one_ck_yes")
	 })

	
   /* $("[name=mzh_lmbt_one_ck_no]").each(function(){
		$(this).click(function(){
			$("[name=mzh_lmbt_one_ck_no]").attr("class","mzh_lmbt_one_ck_no")
			$(this).attr("class","mzh_lmbt_one_ck_yes")
		})
	})*/
	$("[name=mzh_lmbt_one_ck_notwo]").each(function(){
		$(this).click(function(){
			$("[name=mzh_lmbt_one_ck_notwo]").attr("class","mzh_lmbt_one_ck_no")
			$(this).attr("class","mzh_lmbt_one_ck_yes")
		})
	})
	$("[name=mzh_lmbt_one_ck_nothr]").each(function(){
		$(this).click(function(){
			$("[name=mzh_lmbt_one_ck_nothr]").attr("class","mzh_lmbt_one_ck_no")
			$(this).attr("class","mzh_lmbt_one_ck_yes")
		})
	})
	$("[name=mzh_lmbt_one_ck_nofor]").each(function(){
		$(this).click(function(){
			$("[name=mzh_lmbt_one_ck_nofor]").attr("class","mzh_lmbt_one_ck_no")
			$(this).attr("class","mzh_lmbt_one_ck_yes")
		})
	})
	
	

    /* 3-3（供应商管理）发布产品-编辑 */
    $("[name=mzh_lmbt_one_ck_no_img2]").live("click",function(){
        $(".mzh_lmbt_one_ck_xinjian").show();
        $(".mzh_lmbt_one_ck_no_img3").live("click",function(){
            $(".mzh_lmbt_one_ck_xinjian").hide();
        })
    })

    /* 3-3（供应商管理）发布产品-删除 */
    $("[name=mzh_lmbt_one_ck_no_img1]").click(function(){
        $(this).parent("div").remove();
    })


	/****** 左侧选项卡 ********/
	$(".p10 ul li").each(function(){
		$(this).click(function(){
			$(".p10 ul li").attr("class","");
			$(this).attr("class","now");
		})
	})
	
	/* 1-3（首页）我上架的产品-上架中---改 */
	//状态yes
	$("[name=mzh_4_7_yes]").live("click",function(){
		$("[name=mzh_4_7_yes]").attr("class","");
		$(this).attr("class","yes_bgs");
	})
	
	//分类no
	$("[name=mzh_4_7_no]").live("click",function(){
		$("[name=mzh_4_7_no]").attr("class","");
		$(this).attr("class","yes_bgs");
	})

	//分类no
	$("[name=mzh_4_7_no_1]").live("click",function(){
		$("[name=mzh_4_7_no_1]").attr("class","");
		$(this).attr("class","yes_bgs");
	})
	
	/****** 1-4（首页）店铺分类--新增 ******/
	//删除
	$("[name=mzh_remove]").live("click",function(){
		$(this).parent().parent().remove();
	})
	
	/****** 6-1（供应商管理）客服QQ ******/
	//增加QQ
	$("[name=mzh_add_qq]").live("click",function(){
		$("#mzh_add_qq").append("<li><input type='text' class='btn_h30 fl w150' maxlength='12' /><i class='jian' name='mzh_remove_qq'></i></li>");
	})
	
	//删除QQ
	$("[name=mzh_remove_qq]").live("click",function(){
		$(this).parent().remove();
	})
	
	
	/****** 11-1我的钱包 ******/
	//鼠标放上-详情
	$("[class=wdqb_div_1_span]").hover(function(){
		$(this).next().show();
	},function(){
		$(this).next().hide();
	})
	
	/***** 3-1（首页）供应管理--改 *****/
	//下拉和按钮
	$("[name=mzh_xl]").mouseover("click",function(){
		if($(this).attr("isclick") == 0){
			$(this).parent().attr("class","mzh_xl_yes");
			$(this).attr("isclick","1");
			$(this).attr("src","pic/mzh_4_8_xl.jpg")
		} else{
			$(this).parent().attr("class","mzh_xl");
			$(this).attr("isclick","0");
			$(this).attr("src","pic/mzh_4_8_xl_no.jpg")
		}
	})
	$("[name=mzh_span]").live("click",function(){
		$(this).parent().parent().attr("class","mzh_xl");
		$(this).parent().prev().attr("isclick","0");
		$(this).parent().prev().attr("src","pic/mzh_4_8_xl_no.jpg")
	})

    $(".mzh_xl_an").mouseleave(function(){

        $(this).find(".mzh_xl_yes").attr("class","mzh_xl");
        $(this).find("[name=mzh_xl]").attr("isclick","0");
        $(this).find("[name=mzh_xl]").attr("src","pic/mzh_4_8_xl_no.jpg");
    })
	
	
	/****** 1-3（首页）我上架的产品-上架中---改 ******/
	$(".mzh_xlk_text").live("click",function(){
		if($(this).attr("isclick") == 0){
			$("[name=mzh_xlk]").attr("class","mzh_xlk");
			$(this).parent().attr("class","mzh_xlk_yes");
			$(this).attr("isclick","1");
		} else{
			$(this).parent().attr("class","mzh_xlk");
			$(this).attr("isclick","0");
		}
	})
	$("[name=mzh_span_4_9]").click(function(){
		var $val = $(this).text();
		$(this).parent().parent().attr("class","mzh_xlk");
		$(this).parent().prev().attr("isclick","0");
		$(this).parent().prev().html($val);
	})

	/****** 12-1（购物车）购物车 ******/
	//鼠标放上
	$("[name=mzh_hover]").hover(function(){
		if($(this).attr("ishover") ==0){
			$(this).find("div[name=mzh_fangshang]").attr("class","comm_bianjno");
			$(this).attr("ishover","1");
		}
	},function(){
		if($(this).attr("ishover") ==1){
			$(this).find("div[name=mzh_fangshang]").attr("class","comm_bianjno_mr");
			$(this).attr("ishover","0");
		}
	})
	
	$("[name=mzh_click]").mouseover(function(){
		$(this).parent().attr("class","comm_bianjyes");
	})
	$(".img_bianji").mouseout(function(){
		if($(this).parent().parent().parent().attr("ishover") == 1){
			$(this).parent().attr("class","comm_bianjno")
		}
	})

	//点击编辑-出弹出层
	$(".img_bianji").live("click",function(){
		$("[name=mzh_hover]").attr("ishover","0");
		$("[name=mzh_fangshang]").attr("class","comm_bianjno_mr");
		$(".elect_color").hide();
		$(this).parent().attr("class","comm_bianjyes");
		$(this).parent().parent().parent().attr("ishover","2");
		$(this).nextAll("div[class=elect_color]").show();
	})
	//弹出层-颜色-点击
	$("[name=mzh_color]").live("click",function(){
		$("[name=mzh_color]").attr("class","bor_heisel");
		$(this).attr("class","bor_redsel");
	})
	//弹出层-尺码-点击
	$("[name=mzh_size]").live("click",function(){
		$("[name=mzh_size]").attr("class","bor_heisel");
		$(this).attr("class","bor_redsel");
	})
	//弹出层-取消
	$("[name=mzh_close]").live("click",function(){
		$(".elect_color").hide();
		$("[name=mzh_fangshang]").attr("class","comm_bianjno_mr");
		$("[name=mzh_hover]").attr("ishover","0");
	})
	//减
	$("[name=mzh_jian]").live("click",function(){
		var $jian = parseInt($(this).next().find('input').val());
		if($jian >= 1){
			$jian = $jian - 1;
			$(this).next().find('input').val($jian);
		}
	})
	//加
	$("[name=mzh_jia]").live("click",function(){
		var $jia = parseInt($(this).prev().find('input').val());
		$jia = $jia + 1;
		$(this).prev().find('input').val($jia);
	})
	//全选
    $("[id=quanChe]").change(function(){
        $("[name=intName]").attr("checked",true);
        wirteFun();
    },function(){
	//反选
        $("[name=intName]").each(function(){
            if($(this).attr("checked")){
                $(this).attr("checked",false);
            } else {
                $(this).attr("checked",true);
            }
        })
        wirteFun();
    })
	function wirteFun(){
        $ingType = $("#zysaa").find("input:checked");    //[1]变量$ingType，zysaaID下的所有子元素平且是input里面为checked的
        $("#zysId").html("");                            //[4]清除以勾选的内容叠加
        $($ingType).each(function(i,n){         //[2]用变量循环带2个参数(i,n)第一个的意思是intName的个数,第二个是input里的属性
            $("#zysId").append("<li class=\"mfx2\">"+$ingType.eq(i).parent().text() +"<span class=\"a\" name=\"spName\" id=pa_"+ n.value+"> X</span></li>")
            //[3]zysId追加它下面的一句话让循环，+变量下的索引，查找(i),查找父元素的文本
    	})
	}
    $("[name=intName]").each(function(){
        $(this).click(function(){
            wirteFun();
        })
    })
	
	/**  我的钱包 **/
	$("#Addinput").click(function(){
		$("#AddCount").show();
		//$("#Bdbanks").attr("onclick","win('绑定银行卡','win_div_4','574px','600px')");
	})
	
	
	/*** 2-4 ***/
	//绑定倒计时
	$("#mzh_tijiao").click(function() {
        var yzm = $("#mzh_yzm").val();
        if ($.trim(yzm) == "") {
        }
    });
    //获取验证码
    $("#mzh_hqyzm").click(function(){
    	$(this).attr("value","已发送");
    })

    /****** 1-5（订单详情）已完成 --评分 ******/
        //放上
    $("[id^=mzh_pingfen_]").hover(function(){
        var $val = $(this).attr("value");
        if($(this).parent().attr("idclick") ==0){
            if($val == 1){
                $("[id^=mzh_pingfen_]").attr("class","mzh_pjsd_pf_moren");
                $(this).attr("class","mzh_pjsd_pf_cha");
            } else if($val == 2){
                $("[id^=mzh_pingfen_]").attr("class","mzh_pjsd_pf_moren");
                $(this).attr("class","mzh_pjsd_pf_cha");
                $(this).prevAll().attr("class","mzh_pjsd_pf_cha");
            } else if($val == 3){
                $("[id^=mzh_pingfen_]").attr("class","mzh_pjsd_pf_moren");
                $(this).attr("class","mzh_pjsd_pf_hao");
                $(this).prevAll().attr("class","mzh_pjsd_pf_hao");
            } else if($val == 4){
                $("[id^=mzh_pingfen_]").attr("class","mzh_pjsd_pf_moren");
                $(this).attr("class","mzh_pjsd_pf_hao");
                $(this).prevAll().attr("class","mzh_pjsd_pf_hao");
            } else if($val == 5){
                $("[id^=mzh_pingfen_]").attr("class","mzh_pjsd_pf_moren");
                $(this).attr("class","mzh_pjsd_pf_henhao");
                $(this).prevAll().attr("class","mzh_pjsd_pf_henhao");
            }
        }
    },function(){
        if($("#mzh_dingyi").attr("idclick") == 0){
            $("[id^=mzh_pingfen_]").attr("class","mzh_pjsd_pf_moren");
        }
    })
    //点击
    $("[id^=mzh_pingfen_]").click(function(){
        var $vala = $(this).attr("value");
        if($vala == 1){
            $("[id^=mzh_pingfen_]").attr("class","mzh_pjsd_pf_moren");
            $(this).attr("class","mzh_pjsd_pf_cha");
        } else if($vala == 2){
            $("[id^=mzh_pingfen_]").attr("class","mzh_pjsd_pf_moren");
            $(this).attr("class","mzh_pjsd_pf_cha");
            $(this).prevAll().attr("class","mzh_pjsd_pf_cha");
        } else if($vala == 3){
            $("[id^=mzh_pingfen_]").attr("class","mzh_pjsd_pf_moren");
            $(this).attr("class","mzh_pjsd_pf_hao");
            $(this).prevAll().attr("class","mzh_pjsd_pf_hao");
        } else if($vala == 4){
            $("[id^=mzh_pingfen_]").attr("class","mzh_pjsd_pf_moren");
            $(this).attr("class","mzh_pjsd_pf_hao");
            $(this).prevAll().attr("class","mzh_pjsd_pf_hao");
        } else if($vala == 5){
            $("[id^=mzh_pingfen_]").attr("class","mzh_pjsd_pf_moren");
            $(this).attr("class","mzh_pjsd_pf_henhao");
            $(this).prevAll().attr("class","mzh_pjsd_pf_henhao");
        }
        $(this).parent().attr("idclick","1");
        if($vala<=2){
            $("#mzh_fenshu").attr("class","mzh_pf_fs_cha");
        } else if($vala<=4 || $vala >=3){
            $("#mzh_fenshu").attr("class","mzh_pf_fs_hao");
        } else{
            $("#mzh_fenshu").attr("class","mzh_pf_fs_henhao");
        }
        $(this).siblings("#mzh_fenshu").show();
        $(this).siblings("#mzh_fenshu").html($vala+"分");
    })

    /********  *******/
    $("[name=mzh_xgcp]").click(function(){
        $("[name=mzh_xgcp]").attr("class","mzh_radio_xxk_2_no");
        $(this).attr("class","mzh_radio_xxk_2_yes");
    })

    /* 2-3-1（订单详情-申请退款）已发货 */
    $("#mzh_no").click(function(){
        $("#mzh_no_hide").show();
    })
    $("#mzh_yes").click(function(){
        $("#mzh_no_hide").hide();
    })

    /* 3-1（预定订单详情）处理预定订单 */
    $("[name=mzh_cpdj]").hide();
    $("#mzh_bxgcpjg").click(function(){
        $("[name=mzh_cpdj]").hide();
        $("[name=mzh_cpdj_no]").show();
        $("[name=mzh_zjg]").hide();
    })
    $("#mzh_xgcpjg").click(function(){
        $("[name=mzh_cpdj]").show();
        $("[name=mzh_cpdj_no]").hide();
        $("[name=mzh_zjg]").hide();
        $("[name=mzh_zjg_no]").show();
    })
    $("#mzh_xgcpzg").click(function(){
        $("[name=mzh_cpdj]").hide();
        $("[name=mzh_zjg_no]").hide();
        $("[name=mzh_cpdj_no]").show();
        $("[name=mzh_zjg]").show();
    })

    /* 2-3 收货地址 */
    $("[name=jbzl_shdz_yes]").click(function(){
        $("[name=jbzl_shdz_yes]").attr("class","jbzl_shdz_no");
        $(this).attr("class","jbzl_shdz_yes");
        $(".jbzl_shdz_yes").find("[name=mzh_mrdz]").show();
        $(".jbzl_shdz_no").find("[name=mzh_mrdz]").hide();
    })
	
	$('.rlsiz').each(function(){
		$(this).mouseover(function(){
			$(this).children('div').removeClass('none');
			 
		})
		$(this).mouseout(function(){
			$(this).children('div').addClass('none');
		})
	})
	
	
	
	$('[name=Lis]').each(function(i){
		$(this).click(function(){
			if($("[name=box"+i+"]").css("display")=="none"){
			$("[name=box"+i+"]").show();
			$("[name=add"+i+"]").text("-");
			}
			else{
			$("[name=box"+i+"]").hide();
			$("[name=add"+i+"]").text("+");
				}
		})
	})
	
	/***** 查看大图 ******/
	$('[name=ImgNum]').click(function(){
		 var CounWidth = $(window).width()
		 var CounHeight = $(window).height();
		 var TopHei = (CounHeight - $('.dtuImg').height())/2;
		 var LeftHei = (CounWidth - $('.dtuImg').width())/2;  
		 $('.dtuImg').css({
			 "left":LeftHei,
  			 "top": TopHei
		 }).show(); 
		 imgsrc = $(this).parents().html();
		 alert(imgsrc);
 
		 $('#Imgnews').attr('src',imgsrc)
		 $('.bgnone_hui').css('height',$(document).height()).show();
		 $('.bgnone_hui').click(function(){
			$(this).hide();
			$('.dtuImg').hide();	
		 })
		 
		 $('.clear_imgs').click(function(){
			$('.dtuImg').hide();
			$('.bgnone_hui').hide();	
		 })
		 
	})
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/******* 7.16 1-2-3我的产品 *********/
	$('[name=BanJi]').each(function(){
		$(this).click(function(){
			var posTop = ($(window).height() -  $('.pos_rabls').height())/2;  
			if(posTop<=0){
				$('.pos_rabls').css('top','30px'); 
				$('.pos_rabls').show().animate({right:'0px'});
			}else{ 
				$('.pos_rabls').css('top',posTop+'px');
				$('.pos_rabls').show().animate({right:'0px'}); 
			} 
		}) 
    }); 
	 
 	$('#ImgClose').click(function(){ 
		$('.pos_rabls').animate({right:'-352px'}); 
	})
	
	
	
	
	
	/* 发布产品 -- 添加二类分级 */
	$("#id_tjejfl").live("click",function(){
        $("#add_tjelfj").append("" +
            "<div class='fl mzh_width_100 mb_10'>" +
            "<span class='fl mr_10'>二级分类名称1：</span>" +
            "<input type='text' class='xzgys_input' style='width: 150px;' value='手机'>" +
            "<font class='fl ft_red fw_b shou ml_10 f22' name='name_close'>×</font>" +
            "</div>");
    })
    /* 发布产品 -- 添加二类分级 -- 删除分类 */
    $("[name=name_close]").live("click",function(){
        $(this).parent("div").remove();
    })
	
	
	
	
	
	
	
	
	
	$('.zhuang_delssic ul li').each(function(){
		$(this).click(function(){
			$('.zhuang_delssic ul li').attr('class','');
			$(this).attr('class','red_borup');
		})
	})
	
	
	$('#Tpyeof').focus(function(){
		$('#Fcount').show();
	})
	$('#YesterBot').click(function(){
		$('#Fcount').hide();
	})


    /* 显示隐藏储蓄卡 */
    $("[name=mzh_zf_zf]").click(function(){
        $("[name=mzh_zf_zf]").next().next().next().hide();
        $(this).next().next().next().show();
    })
	
})







