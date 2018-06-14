//检测装置
var EMonitorDeviceDetail = (function() {
	return {
		init : function(){//初始化
			
			// 人员  绑定事件   删除li的时候更新数据
			$("#staffList2").click(function(e){
				if(e.target.nodeName != "I"){//点击i的时候触发
					return;
				}
				//获取i-> a-> li
				$(e.target).parent().parent().remove();
				var ids =  new Array();
				//li
				var $li = $("#staffList2 li");
				if($li.length == 0){
					$("#EMonitorDeviceForm").find("#staffId").val("");
					return;
				}
				$li.each(function() {
					ids.push([$(this).val()]);//id添加到数组
				});
				//去重
				ids = ids.unique3().join(",");
				//更新  人员id
				$("#EMonitorDeviceForm").find("#staffId").val(ids);
				//更新   所在部门/科室
			});
			
			
			// 使用场所  绑定事件   删除li的时候更新数据
			$("#areaList1").click(function(e){
				if(e.target.nodeName != "I"){//点击i的时候触发
					return;
				}
				//获取i-> a-> li
				$(e.target).parent().parent().remove();
				var ids =  new Array();
				//li
				var $li = $("#areaList1 li");
				if($li.length == 0){
					$("#EMonitorDeviceForm").find("#areaId").val("");
					return;
				}
				$li.each(function() {
					ids.push([$(this).val()]);//id添加到数组
				});
				//去重
				ids = ids.unique3().join(",");
				//更新  区域
				$("#EMonitorDeviceForm").find("#areaId").val(ids);
			});
			
			// 装置生产单位   绑定删除事件  删除li的时候更新数据
			$("#manufacturersList").click(function(e){
				if(e.target.nodeName != "I"){//点击i的时候触发
					return;
				}
				//获取i-> a-> li
				$(e.target).parent().parent().remove();
				//生产单位
				$("#manufacturers").val("");
			});
			
			EMonitorDeviceDetail.initValidate();
		},
		initValidate : function(){
			//提交
			$(".a-submit").click(function(){  
				if ($("#EMonitorDeviceForm").valid()) {
					
					var id = $("#EMonitorDeviceForm").find("#id").val();
					var data = {
							id : id,
						    name : $("#EMonitorDeviceForm").find("#name").val(),//设备名称
						    manufacturers : $("#EMonitorDeviceForm").find("#manufacturers").val(),//装置生产厂家id
						    type : $("#EMonitorDeviceForm").find("#type").val(),// 型号
						    code : $("#EMonitorDeviceForm").find("#code").val(),//编号
						    areaId : $("#EMonitorDeviceForm").find("#areaId").val(),//使用场所  
						    staffId : $("#EMonitorDeviceForm").find("#staffId").val(),//使用人员id集合 用，分开
						    buyTime : $("#EMonitorDeviceForm").find("#buyTime").val(),//购置日期
						    instrumentState : $("#EMonitorDeviceForm").find("#instrumentState").val(),//仪器状态
						    remark : $("#EMonitorDeviceForm").find("#remark").val(),//备注
						    saveRoom : $("#EMonitorDeviceForm").find("#saveRoom").val()//存放场所
						};
					 $.post(home_url + "/EDevice/monitorDevice/submit",data, 
							function (response) {
								if (response.code == 0){//提示   确认后跳转到查看
									gridAddCallback("EMonitorDeviceTable");//刷新父页面table
									location.href =  home_url + "/EDevice/monitorDevice/lookOver?id=" + response.data;
								}
							}, 'json');
				}
			});
			
			$("#EMonitorDeviceForm").validate({
				rules: {
					name: {//名称
						required: true
				    },
				    code : {//编号
				    	required: true
				    },
				    type : {//序号
				    	required:true
				    },
				    areaId: {//场所
				    	required: true
				    }
				}
			});
		}
	}
})();


jQuery(document).ready(function() {
	EMonitorDeviceDetail.init();//初始化
});
//生产厂家 单选
function getManufacturers(){
	Company_Single(function(data){
		$("#manufacturersList").html("");//清空数据
		//保存生产厂家的id
		$("#manufacturers").val($(data).val());
		$("#manufacturersList").append(data);
		$("#manufacturers").focus().blur()//光标切入移除
	},5);//1.个人剂量监测单位；2.职业健康检查单位；3.评价与检测机构； 4.供货商；5.生产厂家；6.设计单位；7.施工单位；
}


//人员多选
function selectStaff2() {

	var myDialog = dialog({
		title : '人员多选',
		okValue : '确定',
		ok : function() {
			var ids =  new Array();
			$('#_selectedList li').each(function() {
				ids.push([$(this).val()]);//id添加到数组
				var str = true;
				var ztreeLi = $(this);
				$('#staffList2 li').each(function() {//遍历已有元素  和  新增 元素是否相同
					ids.push([$(this).val()]);//id添加到数组
					if(ztreeLi.val() == $(this).val()){
						str = false;
					}
				});
				if(str){//判断数据是否重复
					$('#staffList2').append($(this));
				}
				
			});
			if(ids.length == 0){
				return;
			}
			//去重
			ids = ids.unique3().join(",");
			//保存人员id
			$("#EMonitorDeviceForm").find("#staffId").val(ids);
			$("#EMonitorDeviceForm").find("#staffId").focus().blur()//光标切入移除
		},
		cancelValue : '取消',
		cancel : function() {
		}
	});
	$.ajax({
		url : home_url + '/tool/SelectStaff_Multi',
		success : function(data) {
			myDialog.content(data);
		}
	});
	myDialog.showModal();
}


//使用场所多选
function SelectArea_Multi() {
	
	SelectRoom_Multi(function(nodes) {
		
		var ids =  new Array();//id
		for (var i = 0; i < nodes.length; i++) {
			ids.push([nodes[i].id]);//id添加到数组
			
			var str = true;
			$('#areaList1 li').each(function() {//遍历已有元素  和  新增 元素是否相同
				ids.push([$(this).val()]);//id添加到数组
				if(nodes[i].id == $(this).val()){
					str = false;
				}
			});
			
			var listItem = dataNodeSelected(nodes[i].id, nodes[i].name);
			if(str){//判断数据是否重复
				$('#areaList1').append(listItem);
			}
		}
		if(ids.length == 0){
			return;	
		}
		//去重
		ids = ids.unique3().join(",");
		//保存使用场所id
		$("#EMonitorDeviceForm").find("#areaId").val(ids);
		$("#areaId").focus().blur()//光标切入移除
	
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