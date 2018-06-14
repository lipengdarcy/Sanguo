//场所检测
var DliveCheckDetail = (function() {
	return {
		init : function(){//初始化
			
			
			// 生成检测服务机构  绑定删除事件  删除li的时候更新数据
			$("#producerList1").click(function(e){
				if(e.target.nodeName != "I"){//点击i的时候触发
					return;
				}
				//获取i-> a-> li
				$(e.target).parent().parent().remove();
				//检测服务机构 id
				$("#checkorg").val("");
				$("#managerName").html("");//联系人
				$("#managerTel").html("");//联系方式
			});
			
			
			DliveCheckDetail.initValidate();
		},
		initValidate : function(){
			//提交
			$(".a-submit").click(function(){  
				if ($("#DLivecheckForm").valid()) {
					var id = $("#DLivecheckForm").find("#id").val();
					var data = {
							id : id,
							module : module,//区分模块入口 1-放射现场监测，2，放射设备监测 3辐射安全监测
							checkorg : $("#DLivecheckForm").find("#checkorg").val(),//检测服务机构
							checktype : $("#DLivecheckForm").find("#checktype").val(),//检测类型
							report : $("#DLivecheckForm").find("#report").val(),//检测报告
							reportno : $("#DLivecheckForm").find("#reportno").val(),// 检测报告编号
							detectiontime : $("#DLivecheckForm").find("#detectiontime").val(),//检测时间
						};
					 $.post(home_url + "/DLive/liveCheck/submit/" + module,data, 
							function (response) {
								if (response.code == 0){//提示   确认后跳转到查看
									gridAddCallback("DLiveCheckTable");//刷新父页面table
									location.href =  home_url + "/DLive/liveCheck/lookOver/" + module + "?id=" + response.data;
								}
							}, 'json');
				}
			});
			
			$("#DLivecheckForm").validate({
				rules: {
					checkorg : {//检测服务机构
						required: true
					},
					checktype: {//检测类型
						required: true
				    },
				    detectiontime: {//检测时间
						required: true
				    },
				    report : {//检测报告
				    	required: true
				    },
				    reportno: {//检测报告编号
						required: true
				    }
				}
			});
		}		
	}
})();

var module = "";
jQuery(document).ready(function() {
	module = $("#module").val(); //区分模块入口 1-放射现场监测，2，放射设备监测 3辐射安全监测
	DliveCheckDetail.init();//初始化

});

//
//检测服务机构  单选
function getProducer(){
	Company_Single(function(data){
		$("#producerList1").html("");//清空数据
		//保存检测服务机构的id
		$("#checkorg").val($(data).val());
		$("#managerName").html( $(data).attr('managername') );//联系人
		$("#managerTel").html( $(data).attr('managerTel') );//联系方式
		$("#producerList1").append(data);;
		$("#checkorg").focus().blur()//光标切入移除
	},3);//1.个人剂量监测单位；2.职业健康检查单位；3.评价与检测机构； 4.供货商；5.生产厂家；6.设计单位；7.施工单位；
}






