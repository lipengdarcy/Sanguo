$(function() {
	

	var gridid = 'grid_table';
	var grid_selector = "#" + gridid;
	var pager_selector = "#grid_pager";

	$(grid_selector).jqGrid(
			{
				// 加载数据url
				url : home_url + '/security/role/listData',
				// 编辑url
				editurl : home_url + '/security/role/update',
				datatype : "json",
				// 复选框
				multiselect : true,
				beforeSelectRow : function(rowid) {
					//系统默认的角色不能编辑删除
					var rowData = $(grid_selector).jqGrid('getRowData', rowid);
					if (rowData.cid == '系统默认') {
						$("#edit_" + gridid).addClass('ui-state-disabled');
						$("#del_" + gridid).addClass('ui-state-disabled');
					} else {
						$("#edit_" + gridid).removeClass('ui-state-disabled');
						$("#del_" + gridid).removeClass('ui-state-disabled');
					}
					return true;
				},

				onSelectRow : function(id)// 选择某行时触发事件
				{
					var curRowData = $(grid_selector).jqGrid('getRowData', id);
					//if(curRowData.cid=='系统默认')
					//$(grid_selector).jqGrid("setSelection", id, false);
				},

				//全选事件
				onSelectAll : function(aRowids, status) {
					if (status) {
						// uncheck "protected" rows		        	
						var cbs = $("tr.jqgrow > td > input.cbox:disabled",
								grid_selector);
						cbs.removeAttr("checked");
						//modify the selarrrow parameter
						$(grid_selector).selarrrow = $(grid_selector).find(
								"tr.jqgrow:has(td > input.cbox:checked)").map(
								function() {
									return this.id;
								}) // convert to set of ids
						.get(); // convert to instance of Array
					}
					var ids = $(grid_selector).jqGrid('getDataIDs');
					for (var i = 0; i < ids.length; i++) {
						var rowId = ids[i];
						var curCheck = $("#" + rowId).find(":checkbox");
						var rowData = $(grid_selector).jqGrid('getRowData',
								rowId);
						if (rowData.cid == '系统默认') {
							$('#' + rowId).removeClass('ui-state-highlight');
							$(grid_selector).jqGrid("setSelection", rowId,
									false);
						}
					}
				},

				colNames : [ '名称', '类型', '描述' ],
				colModel : [ {
					name : 'rolename',
					editable : true
				}, {
					name : 'cid',
					// formatter用来转义后台返回的数据
					formatter : function(cellvalue, options, rowObject) {
						var temp = '自定义';
						if (cellvalue == undefined) {
							temp = '系统默认';
						}
						return temp;
					}
				}, {
					name : 'description',
					sortable : false,
					editable : true,
					edittype : "textarea",
					editoptions : {
						//rows : "20",
						//cols : "30"
					}
				}

				],

				viewrecords : true,
				// 表格标题
				caption : "角色列表",
				autowidth : true, // 宽度自适应
				height : 'auto', // 高度自适应
				autoheight : true,
				rownumbers : true, // 显示序号
				// rownumWidth: 40,//序号列宽度
				// 每页显示记录
				rowNum : 10,
				rowList : [ 10, 20, 30 ],
				pager : pager_selector
			});

	// 表格下方操作
	$(grid_selector).navGrid(pager_selector, {
		edit : true,
		//editicon : 'icon-pencil blue',
		add : true,
		//addicon : 'icon-plus-sign purple',
		del : true,
		//delicon : 'icon-trash red',

		search : true,
		refresh : true,
		view : true,
		//viewicon : 'icon-zoom-in grey',
		position : "left", // 定义导航按钮在分页容器中的位置，可用值有： left, center, right
		cloneToTop : false
	},
	// options for the Edit Dialog
	{
		recreateForm : true,
		checkOnUpdate : true,
		checkOnSubmit : true,
		closeAfterEdit : true,
		beforeShowForm : function(e) {
			selRowId = $(grid_selector).jqGrid('getGridParam', 'selrow');
			var rowData = $(grid_selector).jqGrid('getRowData', selRowId);
			if (rowData.cid == '系统默认') {
				return false;
			}
			var form = $(e[0]); 
			form.append('<input id="file_upload" name="file" type="file" multiple />');
			createUploadify('file_upload');
			style_edit_form(form);
			return true;
		},
		afterSubmit : function(response, postdata) {
			var data = $.parseJSON(response.responseText);
			alert(data.message);
			return false;
		},
		errorTextFormat : function(data) {
			return 'Error: ' + data.responseText
		}
	},
	// options for the Add Dialog
	{
		closeAfterAdd : true,
		recreateForm : true,
		// 默认的id由_empty变为空
		serializeEditData : function(data) {
			return $.param($.extend({}, data, {
				id : ''
			}));
		},
		beforeShowForm : function(e) {
			var form = $(e[0]); 
			form.append('<input id="file_upload" name="file" type="file" multiple />');
			createUploadify('file_upload');
			style_edit_form(form);
			return true;
		},
		afterSubmit : function(response, postdata) {
			var data = $.parseJSON(response.responseText);
			alert(data.message);
			return false;
		},
		errorTextFormat : function(data) {
			return 'Error: ' + data.responseText
		}
	},
	// options for the Delete Dailog
	{
		// 批量删除默认的多个参数由id变为idlist
		serializeDelData : function(data) {
			var oper = 'del';
			var id = data.id;
			if (data.id.indexOf(',') > 0) {
				oper = 'batchdel';
				id = '';
			}
			return $.param($.extend({}, data, {
				oper : oper,
				idlist : data.id,
				id : id
			}));
		},
		afterSubmit : function(response, postdata) {
			var data = $.parseJSON(response.responseText);
			alert(data.message);
			// return true;
		},
		errorTextFormat : function(data) {
			return 'Error: ' + data.responseText
		}
	});

});

function style_edit_form(form) {
	//enable datepicker on "sdate" field and switches for "stock" field
	form.append()
	form.find('input[name=sdate]').datepicker({
		format : 'yyyy-mm-dd',
		autoclose : true
	}).end().find('input[name=stock]').addClass('ace ace-switch ace-switch-5')
			.wrap('<label class="inline" />')
			.after('<span class="lbl"></span>');

}

