	//删除单个记录
	function deleteShare(object,shareId,makeWeiID,shareWeiID){ 
		   var pagei = $.layer({
		        type: 1,   //0-4的选择,0：信息框（默认），1：页面层，2：iframe层，3：加载层，4：tips层。
		        btns: 2,
		        btn: ['确定','取消'],
		        title: "提示", 
		        border: [0],
		        closeBtn: [0],
		        closeBtn: [0, true],
		        shadeClose: true,
		        area: ['520px','250px',],
		        page: {dom : '#win_div_3'},
		        yes: function(){  
		        	var object1=$(object); 
		        	//调用删除的Ajax方法
		        	deleteShareAjax(object1,shareId,makeWeiID,shareWeiID);
			   //关闭弹窗
			   	layer.close(pagei);
			   }
			});
			}
		//shareId分享Id
		// shareWeiID 分享人
		function deleteShareAjax(object,shareId,makeWeiID,shareWeiID){
			 $.ajax({ 
				    url: "/share/deleteShare",
				    type: "post",
				    data:{shareId:shareId,
				    	  makeWeiID:makeWeiID,
				    	  shareOne:shareWeiID,
				    	},
				    dataType : 'json',
				    success: function (data) { 
				        if(data.state == 1){ 
				        	alert(data.msg,true);
							$(object).parents("tr").remove();  
						    }else{
						    	alert("删除数据失败！请稍后重试！");
						    };
				        }
				     ,
				    error: function () {
				        alert("数据提交失败！请稍后重试！");
				    }
				});   
		}
		 