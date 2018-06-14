$(document).ready(function() {
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
		url : home_url + '/org?method=queryOrg',
		data : {},
		type : 'post',
		dataType : 'json',

		success : function(data) {
			var r = data.data;
			for (var i = 0; i < r.length; i++) {
				var item = {
					id : r[i].id,
					pId : r[i].pid,
					name : r[i].fullname
				};
				orgList[i] = item;
			}
			// 初始化树节点
			$.fn.zTree.init($("#orgTree"), setting, orgList);
			// 全部展开
			$.fn.zTree.getZTreeObj("orgTree").expandAll(true);
		},
		error : function() {
			hsArtDialog.content("异常！请重新尝试或者联系管理员！").showModal();
		}
	});


});

function setTrigger() {
	var zTree = $.fn.zTree.getZTreeObj("#orgTree");
	zTree.setting.edit.drag.autoExpandTrigger = $("#callbackTrigger").attr(
			"checked");
}

// 点击节点 给orgid和orgname赋值
function onClick(event, treeId, treeNode, clickFlag) {
	$("#selected_orgid").val(treeNode.id);
	$("#selected_orgname").val(treeNode.name);
	console.log(treeNode);
}
