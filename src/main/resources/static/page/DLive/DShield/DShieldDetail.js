//个人防护用品
var DShieldDetail = (function() {
	return {
		init : function(){//初始化
			// 生成areaList1域  绑定删除事件  删除li的时候更新数据
			$("#areaList1").click(function(e){
				if(e.target.nodeName != "I"){//点击i的时候触发
					return;
				}
				//获取i-> a-> li
				$(e.target).parent().parent().remove();
				//区域 id
				$("#areaid").val("");
				//清空放射诊疗数据
				$("#DShieldForm").find("#radiotype").val("0");
				$("#DShieldForm").find(".radiotype").html("");
			});
			
			DShieldDetail.initValidate();
		},
		initValidate : function(){

			//提交
			$(".a-submit").click(function(){  
				if ($("#DShieldForm").valid()) {
					var id = $("#DShieldForm").find("#id").val();
					var areaid = $("#DShieldForm").find("#areaid").val();
					var isoher = $("input[name='isoher']:checked").val();
					
					var is1 = false;//区域是否改变
					var is2 = false;//是否陪检者是否改变
					//判断数据是否改变
					if(id != undefined && id != ''){//是编辑的时候
						
						if(areaid != $("#DShieldForm").find("#areaid2").val()){//区域
							is1 = true;
						}
						if(isoher != $("#DShieldForm").find("#isoher2").val()){//是否陪检者
							is2 = true;
						}
					}
					
					var data = {
							id : $("#DShieldForm").find("#id").val(),
							areaid : areaid,//区域id
							is1 : is1,//区域是否改变
							is2 : is2,//是否陪检者是否改变
							isoher : isoher,//是否有陪检者
							radiotype : $("#DShieldForm").find("#radiotype").val()//放射检查类型
						};
					
					if(!is1 && !is2){
						
						 $.post(home_url + "/DLive/shield/submit" ,data, 
							function (response) {
								if (response.code == 0){//提示   确认后跳转到查看
									gridAddCallback("jqGrid");//刷新父页面table
									location.href =  home_url + "/DLive/shield/lookOver?id=" + response.data;
								}
							}, 'json');
					}else{
						//改变后的提示信息
						var myDialog = dialog({
							title : "提示",
							content: "数据修改后将可能<strong class='red'>清空或完全变更</strong>您之前录入的防护用品信息，确认要进行修改吗？",	
							lock : true,
							cancelValue : '取消',
							okValue : '确定',
							cancel : function() {
							},
							ok : function(){
								 $.post(home_url + "/DLive/shield/submit" ,data, 
									function (response) {
										if (response.code == 0){//提示   确认后跳转到查看
											gridAddCallback("jqGrid");//刷新父页面table
											location.href =  home_url + "/DLive/shield/lookOver?id=" + response.data;
										}
									}, 'json');
								
							}
						});				

						myDialog.showModal();
					}
					
				}
			});
			
			$("#DShieldForm").validate({
				rules: {
					areaid : {//检测服务机构
						required: true
					},
					isoher: {//检测类型
						required: true
				    }
				}
			});
		}		
	}
})();

jQuery(document).ready(function() {
	DShieldDetail.init();//初始化
	
});

//区域单选
function selectArea1() {
	//获取已选的机房id
	 $.get(home_url + "/DLive/shield/selectedAreaId" ,{}, 
		function (response) {
			if (response.code == 0){
				SelectRoom_Single(function(data) {
					$("#areaList1").children().remove();//单选   删除里面多余的数据
					$("#DShieldForm").find("#radiotype").val("0");
					$("#DShieldForm").find(".radiotype").html("");
					//本级区域
					if(data.id != ""){
						$("#areaList1").append(dataNodeSelected(data.id, data.name));
						//所在机房或区域 id
						$("#areaid").val(data.id);
						//生成 放射检查类型   通过区域查询 射线装置被绑定的设备
						 $.get(home_url + "/DLive/shield/getRadiotype" ,{areaid : data.id}, 
									function (response) {
										if (response.code == 0){
											$("#DShieldForm").find("#radiotype").val(response.data);
											if(response.data == 1){
												$("#DShieldForm").find(".radiotype").html("放射诊断学用X射线设备隔室透视、摄影");
											}else if(response.data == 2){
												$("#DShieldForm").find(".radiotype").html("放射诊断学用X射线设备同室透视、摄影");			
											}else if(response.data == 3){
												$("#DShieldForm").find(".radiotype").html("口内牙片摄影");
											}else if(response.data == 4){
												$("#DShieldForm").find(".radiotype").html("牙科全景体层摄影、口腔CT");
											}else if(response.data == 5){
												$("#DShieldForm").find(".radiotype").html("CT体层扫描");
											}else if(response.data == 6){
												$("#DShieldForm").find(".radiotype").html("床旁摄影");
											}else if(response.data == 7){
												$("#DShieldForm").find(".radiotype").html("骨科复位等设备旁操作");
											}else if(response.data == 8){
												$("#DShieldForm").find(".radiotype").html("介入放射学操作 ");
											}else{
												$("#DShieldForm").find(".radiotype").html("");
												$("#DShieldForm").find("#radiotype").val("0");
											} 
										}
										
									}, 'json');
						
						$("#areaid").focus().blur()//光标切入移除
					}else{
						$("#areaid").val("");
					}
					
				},response.data);
				
				
			}
	 	})
}



