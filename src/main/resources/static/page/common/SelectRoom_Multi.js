$(document).ready(function() {
	// alert("多选区域数据加载成功!");
	var setting = {
		view : {
			selectedMulti : false
		},
		check : {
			enable : true,
			chkboxType : {
				"Y" : "s",
				"N" : "s"
			},
			nocheckInherit : false
		// ,chkboxType : { "Y" : "", "N" : "" }
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
			DynamicUpdateNodeCheck();
			// 展开全部
			$.fn.zTree.getZTreeObj("muitiSelectArea").expandAll(true);
			$("#callbackTrigger").bind("change", {}, setTrigger);
		},
		error : function() {
			hsArtDialog.content("异常！请重新尝试或者联系管理员！").showModal();
		}
	});


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

// 过滤节点的机制 直接return node表示不做任何过滤
function filter(treeNode) {
	if (eval(treeNode.level+1) == 4 || eval(treeNode.level+1) == 5) {
		//有子节点
		if (treeNode.children && treeNode.children.length>0) {
			return true;
		}
		//没有子节点
		else{
			if(list.indexOf(treeNode.id)!=-1){
				return true;
			}
			return false;
		}
	}
	return true;
}
// /动态设置zTree的所有节点有checkbox
function DynamicUpdateNodeCheck() {
	var zTree = $.fn.zTree.getZTreeObj("muitiSelectArea");
	// 根据过滤机制获得zTree的所有节点
	var nodes = zTree.getNodesByFilter(filter);
	// 遍历每一个节点然后动态更新nocheck属性值
	for (var i = 0; i < nodes.length; i++) {
		var node = nodes[i];
		 node.nocheck = true; //表示不显示checkbox
		zTree.updateNode(node);
	}
}