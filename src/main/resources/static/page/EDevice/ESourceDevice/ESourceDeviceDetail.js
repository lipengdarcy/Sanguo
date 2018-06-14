//含源装置
var ESourceDeviceDetail = (function() {
	return {
		init : function(){//初始化
			
			// 区域   绑定删除事件  删除li的时候更新数据
			$("#areaList1").click(function(e){
				if(e.target.nodeName != "I"){//点击i的时候触发
					return;
				}
				//获取i-> a-> li
				$(e.target).parent().parent().remove();
				//所在院区
				$("#hospitalArea").html("");
				//所在机房或区域 id
				$("#areaId").val("");
			});
			// 人员   绑定删除事件  删除li的时候更新数据
			$("#staffList1").click(function(e){
				if(e.target.nodeName != "I"){//点击i的时候触发
					return;
				}
				//获取i-> a-> li
				$(e.target).parent().parent().remove();
				//人员id 清空
				$("#staffId").val("");
			});
			
			// 装置生产单位   绑定删除事件  删除li的时候更新数据
			$("#deviceManufacturersList").click(function(e){
				if(e.target.nodeName != "I"){//点击i的时候触发
					return;
				}
				//获取i-> a-> li
				$(e.target).parent().parent().remove();
				//生产单位
				$("#deviceManufacturers").val("");
			});
			
			
			ESourceDeviceDetail.initValidate();
		},
		initValidate : function(){
			//提交
			$(".a-submit").click(function(){  
				if ($("#EsourceDeviceForm").valid()) {
					var id = $("#EsourceDeviceForm").find("#id").val();
					var data = {
							id : id,
						     deviceManufacturers : $("#EsourceDeviceForm").find("#deviceManufacturers").val(),//装置生产厂家id
						     staffId : $("#EsourceDeviceForm").find("#staffId").val(),//负责人表id
						     areaId : $("#EsourceDeviceForm").find("#areaId").val(),//区域id
						    deviceName : $("#EsourceDeviceForm").find("#deviceName").val(),//装置名称
						    type : $("#EsourceDeviceForm").find("#type").val(),//型号
						    code : $("#EsourceDeviceForm").find("#code").val(),//编号
						    measurementTime : $("#EsourceDeviceForm").find("#measurementTime").val(),//活性测量日期
						    purpose : $("#EsourceDeviceForm").find("#purpose").val()//用途
						};
					 $.post(home_url + "/EDevice/sourceDevice/submit",data, 
							function (response) {
								if (response.code == 0){//提示   确认后跳转到查看
									gridAddCallback("EsourceDeviceTable");//刷新父页面table
									location.href =  home_url + "/EDevice/sourceDevice/lookOver?id=" + response.data;
								}
							}, 'json');
				}
			});
			
			$("#EsourceDeviceForm").validate({
				rules: {
					deviceName : {//装置名称
						required: true
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
	ESourceDeviceDetail.init();//初始化
});
//装置生产厂家 单选
function getdeviceManufacturers(){
	Company_Single(function(data){
		$("#deviceManufacturersList").html("");//清空数据
		//保存生产厂家的id
		$("#deviceManufacturers").val($(data).val());
		$("#deviceManufacturersList").append(data);
		$("#deviceManufacturers").focus().blur()//光标切入移除
	},5);//1.个人剂量监测单位；2.职业健康检查单位；3.评价与检测机构； 4.供货商；5.生产厂家；6.设计单位；7.施工单位；
}

//区域单选
function selectArea1() {
	$.get(home_url + "/EDevice/rayDevice/selectedAreaId" ,{}, 
		function (response) {
			SelectRoom_Single(function(data) {
				$("#areaList1").children().remove();//单选   删除里面多余的数据
				$("#hospitalArea").html("");//清空 所在院区
				//所在机房或区域 id
				$("#areaId").val("");
				if(data.id == undefined || data.id == ""){
					return;
				}
				//本级区域
				$("#areaList1").append(dataNodeSelected(data.id, data.name));
				//生成所在院区
				$("#hospitalArea").html(data.areaName);
				//所在机房或区域 id
				$("#areaId").val(data.id);
				$("#areaId").focus().blur()//光标切入移除
			},response.data);
	},"json");

	
}

//人员单选
function selectStaff1() {
	SelectStaff_Single(function(data) {
		$("#staffList1 li").remove();
		$("#staffId").val("");
		if(data.id == undefined || data.id == ""){
			return;
		}
		$("#staffList1").append(dataNodeSelected(data.id, data.name));
		//保存 人员id
		$("#staffId").val(data.id);
		$("#staffId").focus().blur()//光标切入移除
	});
}
