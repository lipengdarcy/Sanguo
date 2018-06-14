var InformMessage = (function() {
	return {
		init : function() {
			InformMessage.initjqGrid();
		},
		jqGridRedraw : function(data) {
			$("#InformMessagetb").jqGrid('setGridParam', {

			}).trigger("reloadGrid");// 重新载入
		},
		search : function(data) {
			$("#InformMessagetb").jqGrid('setGridParam', {
				postData:{keywords:$("#keywords").val()}
			}).trigger("reloadGrid");// 重新载入
		},
		initjqGrid : function() {
			// table 和 页脚的id
			var grid_selector = "#InformMessagetb";
			var pager_selector = "#jqGridPager";
			jQuery(grid_selector)
					.jqGrid(
							{
								url : home_url
										+ '/InformMessage/queryListunfinishData',
								datatype : "json",
								mtype : "post",
								postData : {

								},
								height : 'auto',
								viewrecords : true,// 显示总记录数
								autowidth : true,// 自动匹配宽度
								rowNum : 10,// 默认每页显示
								rowList : [ 5, 10, 15, 20, 25, 30 ],// 自定义每页显示下拉选
								pager : pager_selector,// 表格数据关联的分页条，html元素
								altRows : true,// 分页选项，可以下拉选择每页显示记录数
								rownumbers : true,// 显示序号
								colModel : [
										{
											label : '姓名',
											name : "name",
											index : "name",
											editable : false,
											sortable : false,
											width : 10,
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
											label : '工号',
											name : "workno",
											index : "workno",
											editable : false,
											sortable : false,
											width : 10,
											formatter : function(cellvalue,
													options, rowObject) {
												if (cellvalue != null
														&& cellvalue != undefined)
													return cellvalue;
												return "";
											}
										},
										{
											label : '预从事的岗位/工种',
											name : "workName",
											index : "workName",
											editable : false,
											sortable : false,
											width : 10,
											formatter : function(cellvalue,
													options, rowObject) {
												if (rowObject != null
														&& rowObject.staffworkrole != null&& rowObject.staffworkrole.workroleinfo != null&& rowObject.staffworkrole.workroleinfo.workname != null)
													return rowObject.staffworkrole.workroleinfo.workname;
												return "";
											}
										},
										{
											label : '更新时间',
											name : "updatetime",
											index : "updatetime",
											editable : false,
											sortable : false,
											width : 10,
											formatter : function(cellvalue,
													options, rowObject) {
												if (cellvalue != null
														&& cellvalue != undefined) {
													return formatDate(new Date(
															cellvalue));
												}
												return "";
											}
										},
										{
											label : "操作",
											name : null,
											index : null,
											editable : false,
											sortable : false,
											width : 10,
											formatter : function(cellvalue,
													options, rowObject) {
												var info = '<a href="javascript:view('
														+ rowObject.id
														+ ');" class="btn btn-primary btn-xs switch-bg" style="margin:5px;">查看详情</a>';
												info += '<a href="javascript:addmark('
														+ rowObject.id
														+ ');" class="btn btn-primary btn-xs switch-bg" style="margin:5px;">确认告知</a>'
												info += ' <a href="javascript:noneed('
														+ rowObject.id
														+ ');" class="btn btn-primary btn-xs switch-bg">无需告知</a>';
												return info;
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

var finished = (function() {
	return {
		init : function() {
			finished.initjqGrid();
		},
		jqGridRedraw : function(data) {
			$("#finishedInformMessagetb").jqGrid('setGridParam', {
				
			}).trigger("reloadGrid");// 重新载入
		},
		search : function(data) {
			$("#finishedInformMessagetb").jqGrid('setGridParam', {
				postData:{keywords:$("#keywords").val()}
			}).trigger("reloadGrid");// 重新载入
		},
		initjqGrid : function() {
			// table 和 页脚的id
			var grid_selector = "#finishedInformMessagetb";
			var pager_selector = "#jqGridPager";
			jQuery(grid_selector)
					.jqGrid(
							{
								url : home_url
										+ '/InformMessage/queryListfinishedData',
								datatype : "json",
								mtype : "post",
								height : 'auto',
								viewrecords : true,// 显示总记录数
								autowidth : true,// 自动匹配宽度
								rowNum : 10,// 默认每页显示
								rowList : [ 5, 10, 15, 20, 25, 30 ],// 自定义每页显示下拉选
								pager : pager_selector,// 表格数据关联的分页条，html元素
								altRows : true,// 分页选项，可以下拉选择每页显示记录数
								rownumbers : true,// 显示序号
								colModel : [
										{
											label : '姓名',
											name : "name",
											index : "name",
											editable : false,
											sortable : false,
											width : 10,
											formatter : function(cellvalue,
													options, rowObject) {
												if (rowObject != null
														&& rowObject.staff != null
														&& rowObject.staff.basic != null
														&& rowObject.staff.basic.realname != null)
													return rowObject.staff.basic.realname;
												return "";
											}
										},
										{
											label : '工号',
											name : "workno",
											index : "workno",
											editable : false,
											sortable : false,
											width : 10,
											formatter : function(cellvalue,
													options, rowObject) {
												if (rowObject != null
														&& rowObject.staff != null)
													return rowObject.staff.workno;
												return "";
											}
										},
										{
											label : '岗位/工种',
											name : "workName",
											index : "workName",
											editable : false,
											sortable : false,
											width : 10,
											formatter : function(cellvalue,
													options, rowObject) {
												if (rowObject != null
														&& rowObject.staff != null
														&& rowObject.staff.staffworkrole != null
														&& rowObject.staff.staffworkrole.workroleinfo!= null
														&& rowObject.staff.staffworkrole.workroleinfo.workname!=null )
													return rowObject.staff.staffworkrole.workroleinfo.workname;
												return "";
											}
										},
										{
											label : '告知时间',
											name : "infortime",
											index : "infortime",
											editable : false,
											sortable : false,
											width : 10,
											formatter : function(cellvalue,
													options, rowObject) {
												if (cellvalue != null
														&& cellvalue != undefined) {
													return formatDate(new Date(
															cellvalue));
												}
												return "";
											}
										},
										{
											label : '无需告知原因',
											name : "content",
											index : "content",
											editable : false,
											sortable : false,
											width : 10,
											formatter : function(cellvalue,
													options, rowObject) {
												if (cellvalue != null
														&& cellvalue != undefined) {
													return cellvalue;
												}
												return "";
											}
										},
										{
											label : '状态',
											name : "status",
											index : "status",
											editable : false,
											sortable : false,
											width : 10,
											formatter : function(cellvalue,
													options, rowObject) {
												if (cellvalue != null
														&& cellvalue != undefined) {
													if (cellvalue == 0) {
														return '未告知 ';
													} else if (cellvalue == 1) {
														return '已告知 ';
													} else if (cellvalue == -1) {
														return '无需告知 ';
													}
												}
												return "";
											}
										},
										{
											label : "操作",
											name : null,
											index : null,
											editable : false,
											sortable : false,
											width : 10,
											formatter : function(cellvalue,
													options, rowObject) {
												var info='<a href="javascript:view('+rowObject.id+','+rowObject.status+');" class="ui-pg-div" data-rel="tooltip" data-original-title="查看" ><i class="ui-icon icon-eye-open blue"></i></a>';
												return info;
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
// 查看详情
function view(id,status) {
	if(status==undefined||status==null||status==""){
		window.location.href = home_url + "/InformMessage/view?id=" + id;
	}else{
		window.location.href = home_url + "/InformMessage/view?id=" + id+"&status="+status;
	}
}

// 确认告知
function addmark(id) {
	var myDialog = dialog({
		title : '确认告知',
		lock : true,
		okValue : '确认',
		ok : function() {
			$.ajax({
				type : "post",
				url : home_url + '/InformMessage/saveInform',
				data : $("#addmarkForm").serialize(),
				dataType : "json",
				success : function(data) {
					if (data.code == 1) {
						InformMessage.jqGridRedraw();
					}
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					alert("请求失败！");
				}
			});
		},
		cancelValue : '取消',
		cancel : function() {
		}
	});
	$.ajax({
		url : home_url + '/InformMessage/addmark',
		data : {status:1,staffid:id},
		type : "post",
		success : function(data) {
			myDialog.content(data);
		}
	});
	myDialog.showModal();
}

// 无需告知
function noneed(id) {
	var myDialog = dialog({
		title : '无需告知的原因',
		lock : true,
		okValue : '确认',
		ok : function() {
			// 验证 验证失败则不进行下一步
			if (!$("#noneedForm").valid()) {
				return false;
			}
			$.ajax({
				type : "post",
				url : home_url + '/InformMessage/saveInform',
				data : $("#noneedForm").serialize(),
				dataType : "json",
				success : function(data) {
					if (data.code == 1) {
						InformMessage.jqGridRedraw();
					}
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					alert("请求失败！");
				}
			});
		},
		cancelValue : '取消',
		cancel : function() {
		}
	});
	$.ajax({
		url : home_url + '/InformMessage/addmark',
		data : {status:-1,staffid:id},
		type : "post",
		success : function(data) {
			myDialog.content(data);
		}
	});
	myDialog.showModal();

}
//导出已完成的
function exportfinish(){
	window.location.href=home_url+"/InformMessage/export";
}

