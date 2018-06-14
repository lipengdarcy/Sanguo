var grid_selector = "#grid_table";
var pager_selector = "#grid_pager";

$(function() {
	//绑定回车事件
	$('#name').keydown(function(e){
		if(e.keyCode==13){
		  query();
		}
	});

	$(grid_selector)
			.jqGrid(
					{
						url : home_url + '/law/listData?type=3',
						editurl : home_url + '/law/update',
						datatype : "json",
						multiselect : true,
						colNames : [ 'id', 'pid', '管理制度名称', '附件', '更新人',
								'更新日期', '模板下载' ],
						colModel : [
								{
									name : 'id',
									hidden : true
								},
								{
									name : 'pid',
									hidden : true
								},
								{
									name : 'name',
									sortable : false
								},
								{
									name : 'fileid',
									sortable : false,
									formatter : function(cellvalue, options,
											rowObject) {
										if (!rowObject.fileid)
											return "";
										var temp = '<a dataid="'
												+ rowObject.fileid
												+ '" target="_blank" href="'
												+ getfileUrl(rowObject.fileid)
												+ '">'
												+ getfileName(rowObject.fileid)
												+ '</a>';
										return temp;
									}
								},
								{
									name : 'updator',
									formatter : function(cellvalue, options,
											rowObject) {
										if (cellvalue == undefined)
											return "";
										return getStaffName(cellvalue);
									},
									sortable : false
								},
								{
									name : 'updatetime',
									sortable : false,
									formatter : function(cellvalue, options,
											rowObject) {
										if (cellvalue == undefined)
											return "";
										return moment(new Date(cellvalue))
												.format("YYYY-MM-DD");
									}
								},
								{
									name : '',
									sortable : false,
									formatter : function(cellvalue, options,
											rowObject) {
										var temp = '';
										if (rowObject.pid > 1000
												&& rowObject.pid < 1010) {
											temp = '<a target="_blank" href="'
													+ home_url + rowObject.link
													+ '">模板</a>';
										}
										return temp;
									}

								} ],

						viewrecords : true,
						autowidth : true, // 宽度自适应
						height : 'auto', // 高度自适应
						autoheight : true,
						rownumbers : true, // 显示序号
						rowNum : 10,
						rowList : [ 10, 20, 30 ],
						pager : pager_selector
					});

});

// 法律或者制度类别转义
function getType(typeId) {
	if (typeId === undefined)
		return '';

	var typeName = '';
	switch (typeId) {
	case 1:
		typeName = '管理制度';
		break;
	case 2:
		typeName = '应急预案';
		break;
	case 3:
		typeName = '操作规程';
		break;
	case 4:
		typeName = '表单';
		break;
	case 5:
		typeName = '法律';
		break;
	case 6:
		typeName = '行政法规';
		break;
	case 7:
		typeName = '部门规章';
		break;
	case 8:
		typeName = '规范性文件';
		break;
	case 9:
		typeName = '标准';
		break;
	default:
		typeName = '法律';
		break;
	}
	return typeName;
}

// 查询
function query() {
	var data = {
		name : $("#name").val()
	};
	var postData = $("#grid_table").jqGrid("getGridParam", "postData");
	// 将查询参数融入postData选项对象
	$.extend(postData, data);
	$("#grid_table").jqGrid('setGridParam').trigger("reloadGrid");
}

// 新增
function add() {
	$('#editForm input[name=id]').val('');
	$('#editForm input[name=name]').val('');
	$('#editForm input[name=name]').removeAttr('readonly');
	$('#editForm input[name=fileid]').val('');
	createUploadify('report_upload', showReport, 8, '上传附件', '*.*', [], 1);

	var myDialog = dialog({
		title : '新增管理制度',
		width : 800,
		content : $('#addDiv'),
		okValue : '确定',
		ok : function() {
			if (!validateForm()) {
				return false;
			}
			editCallback('add');
		},
		cancelValue : '取消',
		cancel : function() {
		}
	});

	myDialog.showModal();
}

// 编辑
function edit() {
	var idlist = $(grid_selector).jqGrid('getGridParam', 'selarrrow'); // 参数是数组的形式
	var title = '编辑管理制度', content = $('#addDiv'), width = 800;
	if (idlist.length == 0) {
		title = '提示', content = '请选择要编辑的制度', width = 'auto';
	}
	var rowId = $(grid_selector).jqGrid('getGridParam', 'selrow');
	var rowData = $(grid_selector).jqGrid('getRowData', rowId);
	if (rowData) {
		if (rowData.pid > 1000 && rowData.pid < 1010) {
			$('#editForm input[name=name]').attr('readonly', 'readonly');
		}
		$('#editForm input[name=id]').val(rowData.id);
		$('#editForm input[name=name]').val(rowData.name);
		$('#editForm input[name=fileid]').val($(rowData.fileid).attr('dataid'));
		if (rowData.fileid) {
			var data = {
				id : $(rowData.fileid).attr('dataid'),
				name : $(rowData.fileid).html(),
				url : $(rowData.fileid).attr('href')
			};
			var dataList = [];
			dataList[0] = data;
			$("#editForm .filebox").remove();
			createUploadify('report_upload', showReport, 8, '上传附件', '*.*',
					dataList, 1);
		}else{
			$("#editForm .filebox li").remove();
		}

	}

	var myDialog = dialog({
		title : title,
		content : content,
		width : width,
		okValue : '确定',
		ok : function() {
			if (idlist.length > 0 && !validateForm()) {
				return false;
			}
			if (idlist.length > 0)
				editCallback('edit');
		},
		cancelValue : '取消',
		cancel : function() {
		}
	});
	myDialog.showModal();
}

// 删除
function del() {
	var idlist = $(grid_selector).jqGrid('getGridParam', 'selarrrow'); // 参数是数组的形式
	var title = '删除制度', content = '是否确认删除？';
	if (idlist.length == 0) {
		title = '提示', content = '请选择要删除的制度';
	}
	var flag = false;
	for (var i = 0; i < idlist.length; i++) {
		var rowData = $(grid_selector).jqGrid('getRowData', idlist[i]);
		if (rowData.pid > 1000 && rowData.pid < 1010) {
			title = '提示', content = '前9条制度是系统默认数据，不能删除';
			break;
		}
	}

	var myDialog = dialog({
		title : title,
		content : content,
		lock : true,
		okValue : '确定',
		ok : function() {
			if (title != '提示' && idlist.length > 0) {
				editCallback('batchdel');
			}
		},
		cancelValue : '取消',
		cancel : function() {
		}
	});
	myDialog.showModal();
}

function editCallback(type) {
	var fileid = $("#editForm .filebox li").val();
	$('#editForm input[name=fileid]').val(fileid);
	var idlist = $(grid_selector).jqGrid('getGridParam', 'selarrrow'); // 参数是数组的形式
	$.ajax({
		url : home_url + '/law/update',
		data : $.param({
			'oper' : type,
			'idlist' : idlist
		}) + '&' + $('#editForm').serialize(),
		type : 'post',
		dataType : 'json',
		success : function(data) {
			$.jGrowl(data.message);
			$(grid_selector).jqGrid('setGridParam').trigger("reloadGrid");
		},
		error : function() {
			$.jGrowl("异常！请重新尝试或者联系管理员！");
		}
	});
}

// 表单验证
function validateForm() {
	var fileid = $("#editForm .filebox li").val();
	$('#editForm input[name=fileid]').val(fileid);
	return $('#editForm').validate({
		rules : {
			name : {
				required : true
			}			
		}
	}).form();
}
