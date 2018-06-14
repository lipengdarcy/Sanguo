$(function($) {
	var grid_selector = "#grid_table";
	var pager_selector = "#grid_pager";

	$(grid_selector).jqGrid(
			{
				url : home_url + '/user/listData',
				editurl : home_url + '/user/update',
				datatype : "json",
				width : 'auto',
				autowidth : true,// 宽度自适应
				height : 'auto',// 高度自适应
				autoheight : true,// 高度自适应
				colNames : [ '姓名', '账号', '是否可用', '操作' ],
				colModel : [ {
					name : 'realName',
				}, {
					name : 'userName'
				}, {
					name : 'isValid',
					formatter : function(cellvalue, options, rowObject) {
						if (cellvalue == '1')
							return '是';
						return '否';
					}
				}, {
					name : 'id',
					formatter : function(cellvalue, options, rowObject) {
						if (cellvalue == undefined)
							return "";
						return getOperateColumn(cellvalue);
					}
				} ],
				viewrecords : true,
				rowNum : 10,
				rowList : [ 10, 20, 30 ],
				pager : pager_selector,
				rownumbers : true,
				multiselect : true,
				gridComplete : function() {//加载完毕后获取所有的checkbox遍历
					var rowIds = $(grid_selector).jqGrid('getDataIDs');
					for (var k = 0; k < rowIds.length; k++) {
						var curRowData = $(grid_selector).jqGrid('getRowData',
								rowIds[k]);
						if (curRowData.isAdmin == 'true') {
							$('#' + rowIds[k]).find("input[type='checkbox']")
									.attr('disabled', 'disabled');
						}
					}
				},
				autowidth : true
			});

	// 表格下方操作
	$(grid_selector).jqGrid('navGrid', pager_selector, {
		edit : false,
		add : false,
		del : false,
		search : false,
		refresh : false,
		view : false
	});
});

// 新增、编辑用户
function edit(id) {
	var title = '';
	if (id > 0) {
		var rowId = $("#grid_table").jqGrid('getGridParam', 'selrow');
		if (!rowId) {
			$.jGrowl("请选择要编辑的用户");
			return;
		} else {
			title='编辑用户';
		}
	} else {
		title='新增用户';
	}
	
	var myDialog = dialog({
		title : title,
		height : 'auto',
		width : 500,
		okValue : '确定',
		ok : function() {
			$.ajax({
				url : home_url + '/security/user/update',
				data : {
					'idlist' : rowId,
					'oper' : 'batchdel'
				},
				type : 'post',
				dataType : 'json',
				success : function(data) {
					$.jGrowl(data.message);
					if (data.code < 0)
						return false;
					$("#grid_table").jqGrid().trigger("reloadGrid");
				}
			});
		},
		cancelValue : '取消',
		cancel : function() {
		}
	});
	$.ajax({
		url : home_url + '/user/edit/'+id,
		success : function(data) {
			myDialog.content(data);
			myDialog.showModal();
		}
	});
	
}

function validateForm() {
	var manager = $("#staffList li").val();
	$('#userEditDialog input[name=manager]').val(manager);
	return $('#userEditDialog').validate({
		rules : {
			manager : {
				required : true
			},
			username : {
				required : true
			},
			password : {
				required : true,
				minlength : 6
			},
			passwordConfirm : {
				equalTo : $('#userEditDialog input[name=password]')
			}
		}

	}).form();
}

// 查询用户
function query() {

	var data = {
		'name' : $('#name').val()
	};
	var postData = $("#grid_table").jqGrid("getGridParam", "postData");
	$.extend(postData, data);
	$("#grid_table").jqGrid('setGridParam').trigger("reloadGrid");
}

//操作栏转义
function getOperateColumn(id) {
	if (id === undefined)
		return '';
	var edit = '<a href="javascript:edit('
			+ id
			+ ');" class="ui-pg-div a-redact" data-original-title="编辑" title=""><i class="ui-icon icon-pencil green"></i></a>';
	var del = '<a href="javascript:del('
			+ id
			+ ');" class="ui-pg-div" data-rel="tooltip" data-original-title="删除"><i class="ui-icon icon-trash red"></i></a>';

	//var rowData = $("#grid_table").jqGrid('getRowData', id);
	return edit + del;
}

// 删除用户
function del() {
	var rowId = $("#grid_table").jqGrid('getGridParam', 'selarrrow');
	if (rowId.length == 0) {
		$.jGrowl("请选择要删除的用户");
		return;
	}
	var myDialog = dialog({
		title : '删除用户',
		content : '确定删除用户？',
		height : 'auto',
		width : 500,
		okValue : '确定',
		ok : function() {
			$.ajax({
				url : home_url + '/security/user/update',
				data : {
					'idlist' : rowId,
					'oper' : 'batchdel'
				},
				type : 'post',
				dataType : 'json',
				success : function(data) {
					$.jGrowl(data.message);
					if (data.code < 0)
						return false;
					$("#grid_table").jqGrid().trigger("reloadGrid");
				},
				error : function() {
					$.jGrowl("异常！请重新尝试或者联系管理员！");
				}
			});
		},
		cancelValue : '取消',
		cancel : function() {
		}
	});
	myDialog.showModal();
}
