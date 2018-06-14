//框架补充js

/*根据分辨率高度调整主体内容是否显示滚动条 start*/
$(window).load(function(){
	setSizeOnResize();	
});
function setSizeOnResize(){
	var height=$(window).height();
	var height2=height-160+"px";
	var height3=height-220+"px";
	var height4=height-180+"px";
	var height5=height-120+"px";
	var heightth=height-170+"px";
	var height6=height-260+"px";
	$(".heightbox").css({"height":height3,"max-height":height3});
	$(".heightbox2").css({"height":heightth});
	/*$(".heightbox2").css({"height":height2});*/
	$(".heightbox3").css({"height":height4});
	$(".height").css({"height":height});
	$(".conheight").css({"height":height5});
	$(".conheight2").css({"height":heightth,"overflow-x":"hidden","overflow-y":"auto"});
	$(".carheight").css({"height":height6});

}
$(window).resize(function() {//判断窗体调整是否显示滚动条
	setSizeOnResize();	
});
/*根据分辨率高度调整主体内容是否显示滚动条 end*/


// start
$(function(){
	//关闭IE升级提示
	$(".ietss").click(function(){
		$("#ie6-warning").hide(0);
		$(".lock-bg").hide(0);
	});

	$.fn.extend({'gjsearchshow':function(){/**高级搜索显示**/
		$(".jd-searchbox").hide();
		$(".jd-searchbox").next(".gj-searchbox").show(0);
		
		}
	});
	$.fn.extend({'jdsearchshow':function(){/**简单搜索显示**/
		$(".gj-searchbox").hide();
		$(".gj-searchbox").prev(".jd-searchbox").show(0);
		
		}
	});
		
	//组织架构-列表树 拖动
	$( ".strtWrap" ).draggable();
	$( ".ui-popup" ).draggable();
	/*$( "#strtWrap" ).draggable();*/
	
});	



// end

//dxy S

// 设备设施管理-射线装置信息-编辑 页面
// 下拉
$(".main-container").on("click",".slide-title",function(){
	$(this).siblings(".slide-neir").toggleClass("slide-toggle");
	$(this).find("i").toggleClass("icon-angle-down");
	$(this).find("i").toggleClass("icon-angle-right");
});

// 编辑查看切换
$(".switch-bg").click(function(){
	$(this).parents(".slide-view").addClass("dn");
	$(this).parents(".slide-view").siblings(".form-horizontal").removeClass("dn");
});

$(".switch-bc").click(function(){
	$(this).parents(".form-horizontal").addClass("dn");
	$(this).parents(".form-horizontal").siblings(".slide-view").removeClass("dn");
});

$(".switch-qx").click(function(){
	$(this).parents(".form-horizontal").addClass("dn");
	$(this).parents(".form-horizontal").siblings(".slide-view").removeClass("dn");
	$(this).parents(".form-horizontal").siblings(".slide-neir").children(".slide-view").removeClass("dn");
});
// 下拉编辑查看切换
$(".switch-bg").click(function(e){
	e.stopPropagation(); 
	$(this).parents(".slide-neir").addClass("dn");
	$(this).parents(".slide-neir").siblings(".form-horizontal").removeClass("dn");
});

$(".switch-bc").click(function(e){
	e.stopPropagation(); 
	$(this).parents(".form-horizontal").addClass("dn");
	$(this).parents(".form-horizontal").siblings(".slide-neir").removeClass("dn");
});

$(".switch-qx").click(function(e){
	e.stopPropagation(); 
	$(this).parents(".form-horizontal").addClass("dn");
	$(this).parents(".form-horizontal").siblings(".slide-neir").removeClass("dn");
});
//数字累加累减
/*$('#spinner1').ace_spinner({value:0,min:0,max:1000,step:1, btn_up_class:'btn-info border0' , btn_down_class:'btn-info border0'})
$('#spinner2').ace_spinner({value:0,min:0,max:1000,step:1, btn_up_class:'btn-info border0' , btn_down_class:'btn-info border0'})*/

var str =  $("#bulbCount").val();
if(str == undefined || str =='' ){
	str = 0;
}
str = parseInt(str);//转换为数字
$('.spinner3').ace_spinner({value:str,min:0,max:1000,step:1, btn_up_class:'btn-info border0' , btn_down_class:'btn-info border0'})

//单个日历
/*$('.date-picker').datepicker({autoclose:true}).next().on(ace.click_event, function(){
	$(this).prev().focus();
});*/

$('.date-picker').datepicker({
	autoclose:true,
	//endDate : new Date(),
},function(start, end, label) {
	$scope.start_time = start.format('YYYY-MM-DD')
});



//日历双选（范围）
/*$('input[name=date-range-picker]').daterangepicker().prev().on(ace.click_event, function(){
	$(this).next().focus();
});*/

$('input[name=date-range-picker]').daterangepicker({
	showDropdowns: true,//是否出现年月可选
	minDate: '1870-01-01',//可选最早时间
	maxDate: '2100-12-30',//可选最迟时间
	endDate : new Date(),
	applyClass: 'btn-small btn-primary',//应用按钮的样式
	opens:'right',
	separator:' 至 ',
	//applyClass: 'btn-small btn-success',//应用按钮的样式
},function(start, end, label) {
	//console.log(start.toISOString(), end.toISOString(), label);
	$('input[name=date-range-picker] span').html(start.format('MMMM D, YYYY') + ' 至 ' + end.format('MMMM D, YYYY'));
});


//人员弹出全选
$.fn.extend({'select_all':function(){
	$(this).closest('.personnel').find('.all input')
	.each(function(){
		if(!this.checked){
			var that=this;
			$(that).trigger("click");
		}
	});
}});

//
/*$.fn.extend({'delete':function(){//删除本身li
	$(this).parents("li").remove();
	alert(111);
	}
});*/
/*$(".delete").on("click",function(){
	$(this).parents("li").remove();
	alert(111);
})*/

//人员单个删除 & 单个li删除
$.fn.extend({'li_remove':function(){//删除本身li
	$(this).parents("li").remove();
	}
});

//人员弹出清空
$.fn.extend({'ulcon_empty':function(){//清空ul内容
	$(this).parents(".title-box").siblings("ul").empty();
	}
});

// 设备设施管理-射线装置信息 页面
//气泡
$('[data-rel=tooltip]').tooltip();
//$(".export").click(function(){
//	alert("导出的数据！");	
//});

//公共页面
//全选
$('table th input:checkbox').on('click' , function(){
	var that = this;
	$(this).closest('table').find('tr > td:first-child input:checkbox')
	.each(function(){
		this.checked = that.checked;
		$(this).closest('tr').toggleClass('selected');
	});
		
});

/*//生成机构树图
$(".orgs").jOrgChart({//生成机构树图
	chartElement : '.orgChart'
	//dragAndDrop  :false
});*/

//
/* var flminduan = $(".perul-duan").height();
$(".flduan").css({"min-height":flminduan});*/


//dxy E

/**
 * 替换多余的， 号
 */
function replaceComma(rpIds){
	if(rpIds == null || $.trim(rpIds) == ""){
		return "";
	}
	rpIds = rpIds.replaceAll("[,]{2,}", ",");//置空id有多余的，号替换掉
	if(rpIds.endsWith(",")){//最后一个是字符，号
		rpIds = rpIds.substring(0 , rpIds.length - 1);//去掉最后一个字符
	}
	if(rpIds.startsWith(",")){//第一个是字符，号
		rpIds = rpIds.substring(1);//去掉第一个字符
	}
	return rpIds;
}

String.prototype.replaceAll = function(s1,s2){ 
	return this.replace(new RegExp(s1,"gm"),s2); 
}


//数组去重
Array.prototype.unique3 = function(){
	 var res = [];
	 var json = {};
	 for(var i = 0; i < this.length; i++){
	  if(!json[this[i]]){
	   res.push(this[i]);
	   json[this[i]] = 1;
	  }
	 }
	 return res;
	}







