$(document).ready(function() {
	// alert("多选区域数据加载成功!");
	var setting = {
		view : {
			selectedMulti : false
		},
		check: {
			enable: true
			,chkboxType : { "Y" : "s", "N" : "s" }
			,nocheckInherit: true
			//,chkboxType : { "Y" : "", "N" : "" }
		},	
		data : {
			simpleData : {
				enable : true
			}
		},
		callback : {
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
				orgList[i] = item;
			}
			// 初始化树节点
			$.fn.zTree.init($("#muitiSelectArea"), setting, orgList);
			// 展开全部
			//expandZtree("muitiSelectArea");
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
	var zTree = $.fn.zTree.getZTreeObj("#muitiSelectArea");
	zTree.setting.edit.drag.autoExpandTrigger = $("#callbackTrigger").attr(
			"checked");
}

// 点击节点，查询该组织的人员信息
function onClick(event, treeId, treeNode, clickFlag) {
	$("#selected_areaid").val(treeNode.id);
	$("#selected_areaname").val(treeNode.name);
}
