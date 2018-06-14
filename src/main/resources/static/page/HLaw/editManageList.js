var grid_selector = "#grid_table";
var pager_selector = "#grid_pager";

$(function() {

	$(grid_selector)
			.jqGrid(
					{
						url : home_url + '/law/listData?type=4',
						editurl : home_url + '/law/update',
						datatype : "json",
						multiselect : true,
						colNames : [ 'ID', '修订编号', '类别', '名称（修订前）', '文件号（修订前）', '名称（修订后）','文件号（修订后）', 
								'负责部门', '修订时间'],
						colModel : [
								{
									name : 'id',
									hidden : true
								},
								{
									name : 'link'
								},
								{
									name : 'type',
									sortable : false,
									formatter : function(cellvalue, options,
											rowObject) {
										return getType(cellvalue);
									}
								},
								{
									name : 'parent.name',
									sortable : false
								},
								
								{
									name : 'parent.fileno',
									sortable : false
								},
								{
									name : 'name',
									sortable : false
								},
								
								{
									name : 'fileno',
									sortable : false
								},
								{
									name : 'org.fullname',
									sortable : false
								},																
								{
									name : 'editdate',
									sortable : false,
									formatter : function(cellvalue, options,
											rowObject) {
										if (cellvalue == undefined)
											return "";
										return moment(new Date(cellvalue))
												.format("YYYY-MM-DD");
									}
								}
								

						],

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

// 编辑
function edit() {
	var idlist = $(grid_selector).jqGrid('getGridParam', 'selarrrow'); // 参数是数组的形式
	if (idlist.length == 0) {
		var title = '提示', content = '请选择要编辑的制度', width='auto';
		var myDialog = dialog({
			title : title,
			content : content,
			//width : 800,
			okValue : '确定',
			ok : function() {			
			},
			cancelValue : '取消',
			cancel : function() {
			}
		});
		myDialog.showModal();
	}else{
		var rowId = $(grid_selector).jqGrid('getGridParam', 'selrow');
		location.href =  home_url + '/law/editManage/' + rowId;
	}		
}

// 删除
function del() {
	var idlist = $(grid_selector).jqGrid('getGridParam', 'selarrrow'); // 参数是数组的形式
	var title = '删除制度', content = '是否确认删除？';
	if (idlist.length == 0) {
		title = '提示', content = '请选择要删除的制度';
	}
	var myDialog = dialog({
		title : title,
		content : content,
		lock : true,
		okValue : '确定',
		ok : function() {
			if (idlist.length > 0) {
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