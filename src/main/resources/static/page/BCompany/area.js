var setting = {
	view : {
		addHoverDom : addHoverDom,// 点击新增按钮触发事件
		removeHoverDom : removeHoverDom,// 鼠标离开节点触发
		selectedMulti : false
	},
	edit : {
		enable : true,
		removeTitle : '删除',
		renameTitle : '重命名',
		drag : {
			autoExpandTrigger : true,
			prev : dropPrev,
			inner : dropInner,
			next : dropNext
		},
		editNameSelectAll : true,
		showRemoveBtn : showRemoveBtn,
		showRenameBtn : showRenameBtn
	},
	data : {
		simpleData : {
			enable : true
		}
	},
	callback : {
		beforeClick : beforeClick,
		onClick : onClick,
		beforeDrag : beforeDrag,
		beforeEditName : beforeEditName,
		beforeRemove : beforeRemove,
		beforeRename : beforeRename,
		onRemove : onRemove,// 删除触发事件
	/* onRename : onRename */
	}
};
var areainfoList=new Array();
$(document).ready(function() {
	// 弹窗提示配置
	var hsArtDialog = dialog({
		title : '提示',
		id : "hs-dialog",
		fixed : true,
		width : 300,
		height : 50
	});
	// 查询区域
	var areaList = [];
	$.ajax({
		url : home_url + '/area/getArea',
		data : {},
		type : 'post',
		dataType : 'json',
		beforeSend: function(){
			$.ajax({
				url : home_url + '/area/getAreaInfo',
				type : 'post',
				dataType : 'json',
				async: false,
				success : function(data) {
					areainfoList=data.data;
				}
				});
		},
		success : function(data) {

			var r = data.data;
			var itemTop = {
				id : "0",
				pId : "0",
				name : companyName
			};
			areaList[0] = itemTop;
			for (var i = 0; i < r.length; i++) {
				if (i == 0) {
					var item = {
						id : r[i].id,
						pId : 0,
						name : r[i].name
					};
				} else if (!contains(areainfoList,r[i].id)) {
					var item = {
						id : r[i].id,
						pId : r[i].pid,
						name : r[i].name
					};
				} else {
					var item = {
						id : r[i].id,
						pId : r[i].pid,
						name : r[i].name,
						iconSkin : 'point'
					};
				}
				areaList[i + 1] = item;
			}
			// 初始化树节点
			treeObj = $.fn.zTree.init($("#treeDemo"), setting, areaList);
			// 全部展开
			$.fn.zTree.getZTreeObj("treeDemo").expandAll(true);

		},
		error : function() {
			hsArtDialog.content("异常！请重新尝试或者联系管理员！").showModal();
		}
	});
});
/*
 * 判断面积不符合要求的区域是否在
 * obj 当前区域id
 * arr 后台传来的面积不符合要求的数组
 * */
function contains(arr, obj) {  
    var i = arr.length;  
    while (i--) {  
        if (arr[i].areaId === obj) {  
            return true;  
        }  
    }  
    return false;  
}  



function dropPrev(treeId, nodes, targetNode) {
	var pNode = targetNode.getParentNode();
	if (pNode && pNode.dropInner === false) {
		return false;
	} else {
		for (var i = 0, l = curDragNodes.length; i < l; i++) {
			var curPNode = curDragNodes[i].getParentNode();
			if (curPNode && curPNode !== targetNode.getParentNode()
					&& curPNode.childOuter === false) {
				return false;
			}
		}
	}
	return true;
}
function dropInner(treeId, nodes, targetNode) {
	if (targetNode && targetNode.dropInner === false) {
		return false;
	} else {
		for (var i = 0, l = curDragNodes.length; i < l; i++) {
			if (!targetNode && curDragNodes[i].dropRoot === false) {
				return false;
			} else if (curDragNodes[i].parentTId
					&& curDragNodes[i].getParentNode() !== targetNode
					&& curDragNodes[i].getParentNode().childOuter === false) {
				return false;
			}
		}
	}
	return true;
}
function dropNext(treeId, nodes, targetNode) {
	var pNode = targetNode.getParentNode();
	if (pNode && pNode.dropInner === false) {
		return false;
	} else {
		for (var i = 0, l = curDragNodes.length; i < l; i++) {
			var curPNode = curDragNodes[i].getParentNode();
			if (curPNode && curPNode !== targetNode.getParentNode()
					&& curPNode.childOuter === false) {
				return false;
			}
		}
	}
	return true;
}

function beforeDrag(treeId, treeNodes) {
	return false;
}

function beforeEditName(treeId, treeNode) {
	// 编辑院区
	if (treeNode.level == 1) {
		editArea(treeNode)
	}
	// 编辑建筑物
	if (treeNode.level == 2) {
		editBuilding(treeNode)
	}
	// 编辑楼层
	if (treeNode.level == 3) {
		editFloor(treeNode)
	}
	// 编辑房间
	if (treeNode.level == 4 || treeNode.level == 5) {
		editRoom(treeNode)
	}
	var zTree = $.fn.zTree.getZTreeObj("treeDemo");
	zTree.selectNode(treeNode);
	return true;
	// return confirm("进入节点 -- " + treeNode.name + " 的编辑状态吗？");
}
function beforeRemove(treeId, treeNode) {

	var zTree = $.fn.zTree.getZTreeObj("treeDemo");
	zTree.selectNode(treeNode);
	return confirm("确认删除 节点 -- " + treeNode.name + " 吗？");
}

// 点击删除触发事件
function onRemove(e, treeId, treeNode) {
	delarea(treeNode.id);
}
// 删除区域
function delarea(id) {
	$.ajax({
		url : home_url + '/area?method=delArea',
		data : {
			areaid : id
		},
		type : 'post',
		dataType : 'json',
		success : function(data) {
			showTipDialog(data.message);
		},
		error : function() {
			$.jGrowl("异常！请重新尝试或者联系管理员！", {
				header : "error"
			});
		}
	});
}

function beforeRename(treeId, treeNode, newName, isCancel) {
	if (isNull(newName)) {
		alert("节点名称不能为空和空格!");
		var zTree = $.fn.zTree.getZTreeObj("treeDemo");
		setTimeout(function() {
			zTree.editName(treeNode)
		}, 10);
		return false;
	}
	return true;
}
function isNull(str) {
	if (str == "")
		return true;
	var regu = "^[ ]+$";
	var re = new RegExp(regu);
	return re.test(str);
}
// 编辑组织名称
function onRename(e, treeId, treeNode, isCancel) {
	editOrg(treeNode.id, treeNode.name, treeNode.cid);
}

function showRemoveBtn(treeId, treeNode) {
	// 第一级公司不显示删除
	if (treeNode.id == 0) {
		return false;
	}
	// 不显示删除按钮
	return !treeNode.isParent;
}
function showRenameBtn(treeId, treeNode) {
	// 不显示编辑按钮 第一级医院名不允许修改
	if (treeNode.level == 0) {
		return false;
	}
	return true;
}

// 共用提醒框
var hsArtDialog = dialog({
	title : '提示',
	id : "hs-dialog",
	fixed : true,
	width : 300,
	height : 50
});
// 新增下级组织
function addHoverDom(treeId, treeNode) {
	// 第五级不可新增
	if (treeNode.level < 5) {
		var sObj = $("#" + treeNode.tId + "_span");
		if (treeNode.editNameFlag || $("#addBtn_" + treeNode.tId).length > 0)
			return;
		var addStr = "<span class='button add' id='addBtn_" + treeNode.tId
				+ "' title='新增下级组织' onfocus='this.blur();'></span>";
		sObj.after(addStr);
	}
	var btn = $("#addBtn_" + treeNode.tId);
	if (btn)
		btn.bind("click", function() {
			// 弹窗
			// 新增院区
			if (treeNode.level == 0) {
				addArea(treeNode);
			}// 新增建筑物
			else if (treeNode.level == 1) {
				addBuilding(treeNode);
			}// 新增楼层
			else if (treeNode.level == 2) {
				addFloor(treeNode);
			}
			// 新增房间
			else if (treeNode.level == 3 || treeNode.level == 4) {
				addRoom(treeNode);
			}

		});
}
function removeHoverDom(treeId, treeNode) {
	$("#addBtn_" + treeNode.tId).unbind().remove();
};

function beforeClick(treeId, treeNode, clickFlag) {
	return (treeNode.click != false);
}

// 点击节点，如果是房间 则显示详情 level:4 5 为房间
function onClick(event, treeId, treeNode, clickFlag) {
	if (treeNode.level == 4 || treeNode.level == 5) {
		showdetail(treeNode.id);
	} else {
		$('#detail').empty(); // 清空detail里面的所有内容
	}
}
// 展示详情
function showdetail(id) {
	$.ajax({
		type : "post",
		url : home_url + "/area/getAreaByid",
		data : {
			id : id
		},
		success : function(data) {
			$('#detail').empty(); // 清空detail里面的所有内容
			var html = data;
			$('#detail').html(html);
		}
	});

}
/*
 * 弹出院区新增页面 treeNode 节点信息
 */
function addArea(treeNode) {
	// 新增
	showDialogModal("新增院区", home_url + "/area/editarea?level=1", function() {
		var dialog_typename = $("#dialog_areaname").val();
		// 验证 验证失败则不进行下一步
		if (!$("#areaForm").valid()) {
			return false;
		}
		function beforeSubmit() {
			$('#areaForm input[name="pid"').val(treeNode.id);
			$('#areaForm input[name="level"').val(eval(treeNode.level + 1));
		}
		submitForm("#areaForm", home_url + '/area?method=editArea',
				beforeSubmit(), function(data) {
					_contentLoadTriggered = false;
					if (data.code == 0) {
						var zTree = $.fn.zTree.getZTreeObj("treeDemo");
						zTree.addNodes(treeNode, {
							id : data.data.id,
							pid : treeNode.id,
							name : dialog_typename + "院区"
						});
						// 取消当前所有被选中节点的选中状态 防止出现新增后 上一节点删除等功能未消失
						zTree.cancelSelectedNode();
					} else {
						// showTipDialog(data.message);
					}
				}, 'json');
	}, 320, 'auto');
}
function editArea(treeNode) {
	showDialogModal("编辑院区", home_url + "/area/editarea?level=1&id="
			+ treeNode.id, function() {
		var dialog_typename = $("#dialog_areaname").val();
		// 验证 验证失败则不进行下一步
		if (!$("#areaForm").valid()) {
			return false;
		}
		submitForm("#areaForm", home_url + '/area?method=editArea', null,
				function(data) {
					_contentLoadTriggered = false;
					var zTree = $.fn.zTree.getZTreeObj("treeDemo");
					// 设置节点属性
					treeNode.name = dialog_typename + "院区";
					// 更新改节点
					zTree.updateNode(treeNode, false);
					// 刷新节点
					zTree.selectNode(treeNode);
				}, 'json');
	}, 320, 'auto');
}
/*
 * 弹出建筑新增编辑页面 treeNode 节点信息
 */
function addBuilding(treeNode) {
	// 新增
	showDialogModal("新增建（构）筑物", home_url + "/area/editarea?level=2",
			function() {
				var dialog_typename = $("#dialog_Buildingname").val();
				// 验证 验证失败则不进行下一步
				if (!$("#BuildingForm").valid()) {
					return false;
				}
				function beforeSubmit() {
					$('#BuildingForm input[name="pid"').val(treeNode.id);
					$('#BuildingForm input[name="level"').val(
							eval(treeNode.level + 1));
				}
				submitForm("#BuildingForm", home_url + '/area?method=editArea',
						beforeSubmit(), function(data) {
							_contentLoadTriggered = false;
							if (data.code == 0) {
								var zTree = $.fn.zTree.getZTreeObj("treeDemo");
								zTree.addNodes(treeNode, {
									id : data.data.id,
									pid : treeNode.id,
									name : dialog_typename + "楼"
								});
								// 取消当前所有被选中节点的选中状态 防止出现新增后 上一节点删除等功能未消失
								zTree.cancelSelectedNode();
							} else {
								// showTipDialog(data.message);
							}
						}, 'json');
			}, 500, 'auto');
}
function editBuilding(treeNode) {
	showDialogModal("编辑建（构）筑物", home_url + "/area/editarea?level=2&id="
			+ treeNode.id, function() {
		var dialog_typename = $("#dialog_Buildingname").val();
		// 验证 验证失败则不进行下一步
		if (!$("#BuildingForm").valid()) {
			return false;
		}
		submitForm("#BuildingForm", home_url + '/area?method=editArea', null,
				function(data) {
					_contentLoadTriggered = false;
					if (data.code == 0) {
						var zTree = $.fn.zTree.getZTreeObj("treeDemo");
						// 设置节点属性
						treeNode.name = dialog_typename + "楼";
						// 更新改节点
						zTree.updateNode(treeNode, false);
						// 刷新节点
						zTree.selectNode(treeNode);
					} else {
						// showTipDialog(data.message);
					}
				}, 'json');
	}, 500, 'auto');
}
/*
 * 弹出建筑新增编辑页面 treeNode 节点信息
 */
function addFloor(treeNode) {
	// 新增
	// 需要建筑物id查询该建筑物有多少层
	showDialogModal("新增楼层", home_url + "/area/editarea?level=3&budingid="
			+ treeNode.id, function() {
		var dialog_typename = $(".child").val();
		// 验证 验证失败则不进行下一步
		if (!$("#FloorForm").valid()) {
			return false;
		}
		function beforeSubmit() {
			$('#FloorForm input[name="pid"').val(treeNode.id);
			$('#FloorForm input[name="level"').val(eval(treeNode.level + 1));
		}
		submitForm("#FloorForm", home_url + '/area?method=editArea',
				beforeSubmit(), function(data) {
					_contentLoadTriggered = false;
					if (data.code == 0) {
						var zTree = $.fn.zTree.getZTreeObj("treeDemo");
						if (dialog_typename > 0) {
							dialog_typename = "地上" + dialog_typename + "层";
						} else if (dialog_typename < 0) {
							dialog_typename = "地下" + eval(-1 * dialog_typename)
									+ "层";
						}
						zTree.addNodes(treeNode, {
							id : data.data.id,
							pid : treeNode.id,
							name : dialog_typename
						});
						// 取消当前所有被选中节点的选中状态 防止出现新增后 上一节点删除等功能未消失
						zTree.cancelSelectedNode();
					} else {
						// showTipDialog(data.message);
					}
				}, 'json');
	}, 420, 'auto');
}
function editFloor(treeNode) {
	// 编辑
	// 需要建筑物id查询该建筑物有多少层
	showDialogModal("新增楼层", home_url + "/area/editarea?level=3&budingid="
			+ treeNode.pId + "&id=" + treeNode.id, function() {
		var dialog_typename = $(".child").val();
		// 验证 验证失败则不进行下一步
		if (!$("#FloorForm").valid()) {
			return false;
		}
		submitForm("#FloorForm", home_url + '/area?method=editArea', null,
				function(data) {
					_contentLoadTriggered = false;
					if (data.code == 0) {
						var zTree = $.fn.zTree.getZTreeObj("treeDemo");
						// 设置节点属性
						if (dialog_typename > 0) {
							dialog_typename = "地上" + dialog_typename + "层";
						} else if (dialog_typename < 0) {
							dialog_typename = "地下" + eval(-1 * dialog_typename)
									+ "层";
						}
						treeNode.name = dialog_typename;
						// 更新改节点
						zTree.updateNode(treeNode, false);
						// 刷新节点
						zTree.selectNode(treeNode);
					} else {
						// showTipDialog(data.message);
					}
				}, 'json');
	}, 420, 'auto');
}

/*
 * 弹出房间新增编辑页面 treeNode 节点信息
 */
function addRoom(treeNode) {
	var title = "新增区域或房间";
	if (treeNode.level + 1 == 5) {
		title = "新增房间";
	}

	// 新增
	showDialogModal(title, home_url + "/area/editarea?level="
			+ eval(treeNode.level + 1) + "&pid=" + treeNode.id, function() {
		var dialog_typename = $("#dialog_Roomname").val();

		var parentName = $("#parentName").val();
		function beforeSubmit() {
			$('#RoomForm input[name="pid"').val(treeNode.id);
			$('#RoomForm input[name="level"').val(eval(treeNode.level + 1));
		}
		// 验证 验证失败则不进行下一步
		if (!$("#RoomForm").valid()) {
			return false;
		}
		submitForm("#RoomForm", home_url + '/area?method=editArea',
				beforeSubmit(), function(data) {
					_contentLoadTriggered = false;
					if (data.code == 0) {
						var zTree = $.fn.zTree.getZTreeObj("treeDemo");
						if (contains(areainfoList,data.data.id)) {
							treeNode.iconSkin = 'point';
						}else{
							treeNode.iconSkin = '';
						}
						
						// 如果有更改及时更新上级节点
						if (parentName == undefined || parentName == ""
								|| parentName == null) {
						} else {
							treeNode.name = parentName;
							// 更新改节点
							zTree.updateNode(treeNode, false);
							// 刷新节点
							zTree.selectNode(treeNode);
						}
						var nodes;
						if (contains(areainfoList,data.data.id)) {
							nodes = {
								id : data.data.id,
								pid : treeNode.id,
								name : dialog_typename,
								iconSkin : 'point'
							};
						}else{
							 nodes = {
										id : data.data.id,
										pid : treeNode.id,
										name : dialog_typename
									};
						}
						zTree.addNodes(treeNode, nodes);
						// 取消当前所有被选中节点的选中状态 防止出现新增后 上一节点删除等功能未消失
						zTree.cancelSelectedNode();

					} else {
						// showTipDialog(data.message);
					}
				}, 'json');
	}, 400, 'auto');
}
function editRoom(treeNode) {
	var title = "编辑区域或房间";
	if (treeNode.level == 5) {
		title = "编辑房间";
	}

	// 第四级和第五级页面一样 直接用同一个方法 用level区分
	showDialogModal(title, home_url + "/area/editarea?level=" + treeNode.level
			+ "&id=" + treeNode.id, function() {
		var dialog_typename = $("#dialog_Roomname").val();
		// 验证 验证失败则不进行下一步
		if (!$("#RoomForm").valid()) {
			return false;
		}
		submitForm("#RoomForm", home_url + '/area?method=editArea', null,
				function(data) {
					_contentLoadTriggered = false;
					if (data.code == 0) {
						var zTree = $.fn.zTree.getZTreeObj("treeDemo");
						if (contains(areainfoList,data.data.id)) {
							treeNode.iconSkin = 'point';
						}else{
							treeNode.iconSkin = '';
						}
						
						treeNode.name = dialog_typename;
						// 更新改节点
						zTree.updateNode(treeNode, false);
						// 刷新节点
						zTree.selectNode(treeNode);
						// 刷新详情
						showdetail(treeNode.id);
					} else {
						// showTipDialog(data.message);
					}
				}, 'json');
	}, 400, 'auto');
}

// 判断区域房间信息是否完整
function IsInfoComplete(record) {
	if (record.level < 4) {
		return true;
	}
	if (record.acreage > 0
			|| (record.width > 0 && record.height > 0 && record.length > 0)) {
		return true;
	}
	return false;
}