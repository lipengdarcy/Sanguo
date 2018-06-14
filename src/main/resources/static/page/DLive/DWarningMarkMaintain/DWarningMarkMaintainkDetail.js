//警示标识维护
var DWarningMarkMaintainkDetail = (function() {
	return {
		init : function(){//初始化
			
			// 维护人   绑定删除事件  删除li的时候更新数据
			$("#staffList1").click(function(e){
				if(e.target.nodeName != "I"){//点击i的时候触发
					return;
				}
				//获取i-> a-> li
				$(e.target).parent().parent().remove();
				//清空数据
				$("#maintainby").val("");//维护人
			});
			//保存
			
			DWarningMarkMaintainkDetail.initValidate();
		},
		initValidate : function(){
			//提交
			$("#DWarningMarkMaintainkForm").find(".a-submit").click(function(){  
				if ($("#DWarningMarkMaintainkForm").valid()) {
					var id = $("#DWarningMarkMaintainkForm").find("#id").val();
					var data = {
						   maintaincode : $("#DWarningMarkMaintainkForm").find("#maintaincode").val(),//维护编号
						   maintainby : $("#DWarningMarkMaintainkForm").find("#maintainby").val(),//维护人
						   maintaintime : $("#DWarningMarkMaintainkForm").find("#maintaintime").val(),//维护时间
						   id : id
						};
					$.post(home_url + "/DLive/warningMarkMaintain/submit",data, 
							function (response) {
								if (response.code == 0){//提示   确认后跳转到查看
									gridAddCallback("jqGrid");//刷新父页面table
									location.href =  home_url + "/DLive/warningMarkMaintain/lookOver?id=" + response.data;
								}
							}, 'json');
				}
			});
			
			$("#DWarningMarkMaintainkForm").validate({
				rules: {
					maintainby : {
						required: true
				    },
					maintaintime : {
						required: true
					}
				}
			});
		}		
	}
})();


jQuery(document).ready(function() {
	DWarningMarkMaintainkDetail.init();//初始化

});

//维护人单选
function selectStaff1() {
	SelectStaff_Single(function(data) {
		$("#staffList1 li").remove();
		$("#maintainby").val("");
		if(data.id == undefined || data.id == ""){
			return;
		}
		$("#staffList1").append(dataNodeSelected(data.id, data.name));
		//保存 人员id
		$("#maintainby").val(data.id);
		$("#maintainby").focus().blur()//光标切入移除
	});
}
