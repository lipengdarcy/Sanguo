var organization = (function() {
	return {
		init : function() {
			
			organization.initjqGrid(0);
		},
    	jqGridRedraw : function(data){
	        $("#orgtb").jqGrid('setGridParam',{  
	            postData:{'id':data} //发送数据  
	        }).trigger("reloadGrid"); //重新载入  
	        
	        var list=$("#orgtb").jqGrid("getRowData");
	        //解决先点击组织机构下没有人员的组织机构，再点击组织机构下有人员的组织机构，第一次点击不会显示该架构下的人员，再次点击才会显示
	        if(list.length==0){
	        	setTimeout(function() {
	        		 $("#orgtb").jqGrid('setGridParam',{  
	     	            postData:{'id':data} //发送数据  
	     	        }).trigger("reloadGrid"); //重新载入  
	        	}, 100);
	        }
    	},
		initjqGrid : function(id) {
			// table 和 页脚的id
			var grid_selector = "#orgtb";
			var pager_selector = "#grid-pager";
			jQuery(grid_selector).jqGrid(
							{
								url : home_url + '/org/queryOrgListData',
								datatype : "json",
								mtype : "post",
								postData : {
									id : id
								},
								height : 'auto',
								viewrecords : true,// 显示总记录数
								autowidth: true,//自动匹配宽度 
								rowNum : 10,// 默认每页显示
								rowList : [ 5, 10, 15, 20, 25, 30 ],// 自定义每页显示下拉选
								pager : pager_selector,// 表格数据关联的分页条，html元素
								altRows : true,// 分页选项，可以下拉选择每页显示记录数
								rownumbers: true,//显示序号
								/*
								 * multiselect : true,//生成多选列 multiboxonly :
								 * true,//全选
								 */colNames : [  '姓名', '性别', "工号", '岗位/工种' ],
								colModel : [
										{
											name : "name",
											index : "name",
											editable : false,
											sortable : false,
											formatter : function(cellvalue,
													options, rowObject) {
												if (rowObject != null
														&& rowObject.basic != null
														&& rowObject.basic.realname != null)
													return rowObject.basic.realname;
												return "";
											}
										},
										{
											name : "gender",
											index : "gender",
											editable : false,
											sortable : false,
											formatter : function(cellvalue,
													options, rowObject) {
												if (rowObject != null
														&& rowObject.basic != null
														&& rowObject.basic.gender != null){
													if(rowObject.basic.gender==1){
														return '男';
													}else if(rowObject.basic.gender==0){
														return '女';
													}
												}
												
												return "";
											}
										},
										{
											name : "workno",
											index : "workno",
											editable : false,
											sortable : false,
											formatter : function(cellvalue,
													options, rowObject) {
												if (cellvalue != null
														&& cellvalue != undefined)
													return cellvalue;
												return "";
											}
										},
										{
											name : "rname",
											index : "rname",
											editable : false,
											sortable : false,
											formatter : function(cellvalue,
													options, rowObject) {
												if (rowObject != null
														&& rowObject.staffworkrole != null&& rowObject.staffworkrole.workroleinfo != null&& rowObject.staffworkrole.workroleinfo.workname != null)
													return rowObject.staffworkrole.workroleinfo.workname;
												return "";
											}
										} ],
								loadComplete : function() {// 重绘回调函数
									var table = this;
									
								}

							});

			// 表格下方操作
			jQuery(grid_selector).jqGrid('navGrid', pager_selector, {
				add : false,
				edit : false,
				editfunc : false,
				del : false,
				search : false,
				refresh : false,
				refreshicon : 'icon-refresh green',
				view : false
			});

		}
	}
})();
// 点击节点，查询该组织的人员信息


function onClick(event, treeId, treeNode, clickFlag) {
	organization.jqGridRedraw(treeNode.id);
	
}


$(document).ready(function() {
	// 查询所有资源
	var orgList = [];
	$.ajax({
		url : home_url + '/org?method=queryOrg',
		data : {},
		type : 'post',
		dataType : 'json',
		success : function(data) {
			var r = data.data;
			var itemTop = {
					id : "0",
					pId : "0",
					name : companyName
			};
			orgList[0] = itemTop;
			for (var i = 0; i < r.length; i++) {
					var item = {
						id : r[i].id,
						pId : r[i].pid,
						name : r[i].fullname
					};
				orgList[i+1] = item;
			}
			
			// 初始化树节点
			$.fn.zTree.init($("#treeDemo"), setting, orgList);
			// 全部展开
			$.fn.zTree.getZTreeObj("treeDemo").expandAll(true);
			organization.init();
		},
		error : function() {
			hsArtDialog.content("异常！请重新尝试或者联系管理员！").showModal();
		}
	});
});
var setting = {
	view : {
		addHoverDom : addHoverDom,
		removeHoverDom : removeHoverDom,
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
		beforeDrop : beforeDrop,

		beforeEditName : beforeEditName,
		beforeRemove : beforeRemove,
		beforeRename : beforeRename,
		onDrag : onDrag,
		onDrop : onDrop,
		onRemove : onRemove,//
		onRename : onRename
	}
};
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
	for (var i = 0, l = treeNodes.length; i < l; i++) {
		if (treeNodes[i].drag === false) {
			return false;
		}
	}
	return true;
}

function beforeDrop(treeId, treeNodes, targetNode, moveType) {
	return targetNode ? targetNode.drop !== false : true;
}

function beforeEditName(treeId, treeNode) {
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

// 删除组织
function onRemove(e, treeId, treeNode) {
	delOrg(treeNode.id);
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
// 编辑机构名称
function editOrg(id, name, cid) {

	// 弹窗提示配置
	var hsArtDialog = dialog({
		title : '提示',
		id : "hs-dialog",
		fixed : true,
		width : 300,
		height : 50
	});
	$.ajax({
		url : home_url + '/org?method=editOrg',
		data : {
			fullname : name,
			pinyin : 'ppqq',
			id : id,
			cid : cid
		},
		type : 'post',
		dataType : 'json',
		success : function(data) {
			// hsArtDialog.content(data.content).showModal();
			showTipDialog(data.message);
		},
		error : function() {
			hsArtDialog.content("异常！请重新尝试或者联系管理员！").showModal();
		}
	});
}

function showRemoveBtn(treeId, treeNode) {
	//第一级医院名不显示删除
	if(treeNode.level==0){
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
	// 不显示编辑按钮
	return true;
}

// 新增下级组织
function addHoverDom(treeId, treeNode) {
	var hsArtDialog = dialog({
		title : '提示',
		id : "hs-dialog",
		fixed : true,
		width : 300,
		height : 50
	});
	var sObj = $("#" + treeNode.tId + "_span");
	if (treeNode.editNameFlag || $("#addBtn_" + treeNode.tId).length > 0)
		return;
	var addStr = "<span class='button add' id='addBtn_" + treeNode.tId
			+ "' title='新增下级组织' onfocus='this.blur();'></span>";
	sObj.after(addStr);
	var btn = $("#addBtn_" + treeNode.tId);
	if (btn)
		btn.bind("click", function() {
			/*
			 * 
			 * addOrg(treeNode, treeNode.id, "新增下级组织"); return false;
			 */
			// 弹窗
			var abc = treeNode.id;
			showDialogModal("新增组织结构", home_url + "/org/addOrg", function() {
				var dialog_typename = $("#dialog_funcdeptname").val();
				if (checkNull(dialog_typename) == true) {
					hsArtDialog.content("请填写组织机构名称!").showModal();
					return;
				}
				function beforeSubmit() {
					$('#fnctionalDeptForm input[name="pid"').val(treeNode.id);
				}
				/*
				 * $.ajax({ url : home_url + '/org/findDuplication', type :
				 * 'post', dataType : 'json', data :
				 * {fullname:dialog_typename,pid:treeNode.id}, success :
				 * function(data) { if(data.result>0){
				 * if(confirm("同级目录下该组织架构名已经存在，确定保存数据吗")){ }else{ return false; } } }
				 * });
				 */
				submitForm("#fnctionalDeptForm", home_url
						+ '/org?method=addOrg&fullname='
						+ encodeURI(dialog_typename), beforeSubmit(), function(
						data) {
					_contentLoadTriggered = false;
					if (data.code == 0) {
						/*showTipDialog(data.message);*/
						var zTree = $.fn.zTree.getZTreeObj("treeDemo");
						zTree.addNodes(treeNode, {
							id : data.data,
							pid : treeNode.id,
							name : dialog_typename
						});
						//取消当前所有被选中节点的选中状态  防止出现新增后 上一节点删除等功能未消失
						zTree.cancelSelectedNode();
					} else {
						showTipDialog(data.message);
					}
				}, 'json');
			},320,'auto');
		});
}
function removeHoverDom(treeId, treeNode) {
	$("#addBtn_" + treeNode.tId).unbind().remove();
};

function beforeClick(treeId, treeNode, clickFlag) {
	return (treeNode.click != false);
}

function onDrag(event, treeId, treeNodes) {

}
// 拖拽，保存新的组织架构
function onDrop(event, treeId, treeNodes, targetNode, moveType, isCopy) {
	saveOrg(treeNodes[0].id, targetNode.id);
}

function saveOrg(id, pid) {
	// alert("保存组织架构成功！id:" + id + ", pid:" + pid);
	$.ajax({
		url : home_url + '/org?method=editOrg',
		data : {
			id : id,
			pid : pid
		},
		type : 'post',
		dataType : 'json',
		success : function(data) {
			// alert(data.content);
			showTipDialog(data.message);
		},
		error : function() {
			hsArtDialog.content("异常！请重新尝试或者联系管理员！").showModal();
		}
	});
}
// 删除机构
function delOrg(id) {
	$.ajax({
		url : home_url + '/org?method=delOrg',
		data : {
			orgid : id
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
