//警示标识
var DWarningMarkDetail = (function() {
	return {
		init : function(){//初始化
			
			// 人员   绑定删除事件  删除li的时候更新数据
			$("#staffList1").click(function(e){
				if(e.target.nodeName != "I"){//点击i的时候触发
					return;
				}
				//获取i-> a-> li
				$(e.target).parent().parent().remove();
				//人员id 清空
				$("#managerid").val("");
			});
			
			// 区域   绑定删除事件  删除li的时候更新数据
			$("#areaList1").click(function(e){
				if(e.target.nodeName != "I"){//点击i的时候触发
					return;
				}
				//获取i-> a-> li
				$(e.target).parent().parent().remove();
				//人员id 清空
				$("#areaid").val("");
			});
			
			//设置位置  绑定删除事件 删除li的时候更新数据
			$(".ull").click(function(e){
				if(e.target.nodeName != "I"){//点击i的时候触发
					return;
				}
				//获取i-> a-> li
				var $li = $(e.target).parent().parent();
				var val = $li.attr("name");
				$li.remove();
				//更改数据 a -> ul-> ->div  input
				var $input =  $(this).parent().parent().find("input");
				$input.val( replaceComma($input.val().replace(val, "")));
				
			});
			
			DWarningMarkDetail.initValidate();
		},
		initValidate : function(){
			//提交
			$(".a-submit").click(function(){  
				if ($("#DWarningmarkForm").valid()) {
					var id = $("#DWarningmarkForm").find("#id").val();
					var data = {
						   areaid : $("#DWarningmarkForm").find("#areaid").val(),//区域ID
						   managerid : $("#DWarningmarkForm").find("#managerid").val(),////负责人编号
						   remark : $("#DWarningmarkForm").find("#remark").val(),//备注说明
						   fileid : $("#DWarningmarkForm").find("#fileid").val(),//现场照片
						   signType1 : $("#DWarningmarkForm").find("#signType1").val(),//电离辐射警示标志个数
						   signType2 : $("#DWarningmarkForm").find("#signType2").val(),//灯箱警示语个数
						   signType3 : $("#DWarningmarkForm").find("#signType3").val(),//警戒线个数
						   signType4 : $("#DWarningmarkForm").find("#signType4").val(),//危害告知个数
						   signType5 : $("#DWarningmarkForm").find("#signType5").val(),//禁止入内个数
						   signType6 : $("#DWarningmarkForm").find("#signType6").val(),//禁止停留个数
						   signType7 : $("#DWarningmarkForm").find("#signType7").val(),//孕妇告知个数
						   positionAddr1 : $("#DWarningmarkForm").find("#positionAddr1").val(),//电离辐射警示位置
						   positionAddr2 : $("#DWarningmarkForm").find("#positionAddr2").val(),//灯箱警示语位置
						   positionAddr3 : $("#DWarningmarkForm").find("#positionAddr3").val(),//警戒线位置
						   positionAddr4 : $("#DWarningmarkForm").find("#positionAddr4").val(),//危害告知位置
						   positionAddr5 : $("#DWarningmarkForm").find("#positionAddr5").val(),//禁止入内位置
						   positionAddr6 : $("#DWarningmarkForm").find("#positionAddr6").val(),//禁止停留位置
						   positionAddr7 : $("#DWarningmarkForm").find("#positionAddr7").val(),//孕妇告知位置
						   id : id
						};
					
					 $.post(home_url + "/DLive/warningMark/submit",data, 
							function (response) {
								if (response.code == 0){//提示   确认后跳转到查看
									gridAddCallback("DWarningMarkTable");//刷新父页面table
									location.href =  home_url + "/DLive/warningMark/lookOver?id=" + response.data;
								}
							}, 'json');
				}
			});
			
			$("#DWarningmarkForm").validate({
				rules: {
					areaid: {//区域名称
						required: true
				    }
				}
			});
		}		
	}
})();


jQuery(document).ready(function() {
	DWarningMarkDetail.init();//初始化
	//计数器
	$('#signType1').ace_spinner({value:parseInt($("#signType1").val() != "" ? $("#signType1").val() : 0),min:0,max:1000,step:1, btn_up_class:'btn-info border0' , btn_down_class:'btn-info border0'});
	$('#signType2').ace_spinner({value:parseInt($("#signType2").val() != "" ? $("#signType2").val() : 0),min:0,max:1000,step:1, btn_up_class:'btn-info border0' , btn_down_class:'btn-info border0'});
	$('#signType3').ace_spinner({value:parseInt($("#signType3").val() != "" ? $("#signType3").val() : 0),min:0,max:1000,step:1, btn_up_class:'btn-info border0' , btn_down_class:'btn-info border0'});
	$('#signType4').ace_spinner({value:parseInt($("#signType4").val() != "" ? $("#signType4").val() : 0),min:0,max:1000,step:1, btn_up_class:'btn-info border0' , btn_down_class:'btn-info border0'});
	$('#signType5').ace_spinner({value:parseInt($("#signType5").val() != "" ? $("#signType5").val() : 0),min:0,max:1000,step:1, btn_up_class:'btn-info border0' , btn_down_class:'btn-info border0'});
	$('#signType6').ace_spinner({value:parseInt($("#signType6").val() != "" ? $("#signType6").val() : 0),min:0,max:1000,step:1, btn_up_class:'btn-info border0' , btn_down_class:'btn-info border0'});
	$('#signType7').ace_spinner({value:parseInt($("#signType7").val() != "" ? $("#signType7").val() : 0),min:0,max:1000,step:1, btn_up_class:'btn-info border0' , btn_down_class:'btn-info border0'});

	$(".inde").click(function(){
		//获取ul a->div ->ul
		var $ul = $(this).parent().find("ul");
		//获取数据存储的位置   a-> div -> td -> input
		var $_location = $(this).parent().parent().find("input");
		WarningMark_Multi($ul ,function(){
			//获取存储的数据
			var name =  new Array();
			$ul.find("li").each(function() {//遍历已有元素  和  新增 元素是否相同
				name.push([$(this).attr('name')]);
			});
			var _location = name.join(",");
			$_location.val(_location);//存储数据
		});
	});
	//警示标识位置

});


//区域单选
function selectArea1() {
	 $.get(home_url + "/DLive/warningMark/selectedAreaId" ,{}, 
		function (response) {
			 SelectRoom_Single(function(data) {
					$("#areaList1").children().remove();//单选   删除里面多余的数据
					//本级区域
					if(data.id != ""){
						$("#areaList1").append(dataNodeSelected(data.id, data.name));
						
						//所在机房或区域 id
						$("#areaid").val(data.id);
						$("#areaid").focus().blur()//光标切入移除
					}else{
						$("#areaid").val("");
					}
				},response.data);
	 });
	
	
	
}

//人员单选
function selectStaff1() {
	SelectStaff_Single(function(data) {
		$("#staffList1 li").remove();
		if(data.id != ""){
			$("#staffList1").append(dataNodeSelected(data.id, data.name));
			//保存 人员id
			$("#managerid").val(data.id);
			$("#managerid").focus().blur()//光标切入移除
		}else{
			
			$("#managerid").val("");
		}
		
	});
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