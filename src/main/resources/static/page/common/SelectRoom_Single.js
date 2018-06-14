$(document).ready(function() {
	// alert("区域数据加载成功!");
	var setting = {
		view : {
			selectedMulti : false
		},
		data : {
			simpleData : {
				enable : true
			}
		},
		callback : {
			beforeClick : beforeClick,
			onClick : onClick
		}
	};

	// 弹窗提示配置
	var hsArtDialog = dialog({
		title : '提示',
		id : "hs-dialog",
		fixed : true,
		width : 300,
		height : 50
	});
	// 查询机构
	var orgList = [];
	$.ajax({
		url : home_url + '/area?method=queryArea',
		data : {},
		type : 'post',
		dataType : 'json',

		success : function(data) {
			var r = data.data;
			for (var i = 0; i < r.length; i++) {
				var item = {
					id : r[i].id,
					pId : r[i].pid,
					name : r[i].name
				};
				if(list.indexOf(item.id)!=-1){
					item.name+="(已选择)";
				}
				orgList[i] = item;
			}
			// 初始化树节点
			$.fn.zTree.init($("#orgTree"), setting, orgList);
			// 展开全部
			$.fn.zTree.getZTreeObj("orgTree").expandAll(true);
			$("#callbackTrigger").bind("change", {}, setTrigger);
		},
		error : function() {
			hsArtDialog.content("异常！请重新尝试或者联系管理员！").showModal();
		}
	});

	// 查询机构的成员
	// getAllOrgMembers();

});

function setTrigger() {
	var zTree = $.fn.zTree.getZTreeObj("#orgTree");
	zTree.setting.edit.drag.autoExpandTrigger = $("#callbackTrigger").attr(
			"checked");
}
function beforeClick(treeId, treeNode, clickFlag) {
	//只有房间才让点击
	if (eval(treeNode.level+1) == 4 || eval(treeNode.level+1) == 5) {
		//有子节点 不让点击
		if (treeNode.children && treeNode.children.length>0) {
			return false;
		}
		//没有子节点
		else{
			//已选择的不让点击
			if(list.indexOf(treeNode.id)!=-1){
				return false;
			}
			return true;
		}
	}
	return false;
}
// 点击节点，查询该组织的人员信息
function onClick(event, treeId, treeNode, clickFlag) {
	$("#selected_areaid").val(treeNode.id);
	$("#selected_areaname").val(treeNode.name);
	var parent = treeNode.getParentNode(); 
	if(parent){
		$("#p_areaid").val(parent.id);
		$("#p_areaname").val(parent.name);
	}	
	var parent2 = treeNode.getParentNode().getParentNode();
	if(parent2){
		$("#p_areaid2").val(parent2.id);
		$("#p_areaname2").val(parent2.name);
	}	
	var treeObj = $.fn.zTree.getZTreeObj("orgTree");  
    //获取全部节点数据  
    var nodes = treeObj.getNodes(); 
    //获取院区
    var area;
   if(treeNode.level==3){
	    area=treeNode.getParentNode().getParentNode().getParentNode();
   }else if(treeNode.level==4){
	    area=treeNode.getParentNode().getParentNode().getParentNode().getParentNode();
   }
   $("#areaId").val(area.id);
   $("#areaName").val(area.name);
}
