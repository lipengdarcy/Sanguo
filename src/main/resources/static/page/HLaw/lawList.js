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
						url : home_url + '/law/listData',
						datatype : "json",
						//multiselect : true,
						colNames : [ '名称', '类别', '文号', '颁布部门', '实施日期', '状态',
								'详细信息' ],
						colModel : [
								{
									name : 'name',
									sortable : true
								},
								{
									name : 'type',
									sortable : true,
									formatter : function(cellvalue, options,
											rowObject) {
										return getType(cellvalue);
									}
								},
								{
									name : 'fileno',
									sortable : false
								},
								{
									name : 'publishdept',
									sortable : false
								},
								{
									name : 'effecttime',
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
									name : 'isvalid',
									sortable : false,
									formatter : function(cellvalue, options,
											rowObject) {
										var temp = '现行';
										if (cellvalue == undefined)
											temp = '现行';
										else if (cellvalue == -1)
											temp = '废止';
										else
											temp = '现行';
										return temp;
									}

								},
								{
									name : 'link',
									sortable : false,
									formatter : function(cellvalue, options,
											rowObject) {
										var temp = '<a target="_blank" href="'
												+ home_url + cellvalue
												+ '">附件</a>';
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
	var myDialog = dialog({
		title : '选择法律法规标准',
		width : 800,
		content : $('#addDiv'),
		lock : true,
		okValue : '确定',
		ok : function() {
			addCallback();
		},
		cancelValue : '取消',
		cancel : function() {
		}
	});

	myDialog.showModal();
}

function addCallback() {
	var idlist = $("#law_standard").jqGrid('getGridParam', 'selarrrow'); // 参数是数组的形式
	if (idlist.length == 0)
		return;
	$.ajax({
		url : home_url + '/law/update',
		data : {
			'oper' : 'batchadd',
			'idlist' : idlist
		},
		type : 'post',
		dataType : 'json',
		success : function(data) {
			$.jGrowl(data.message);
			$(grid_selector).jqGrid('setGridParam').trigger("reloadGrid");
		},
		error : function() {
			$.jGrowl("异常！请重新尝试或者联系管理员！", {
				header : "提醒"
			});
		}
	});
}

// 删除
function del() {
	var idlist = $(grid_selector).jqGrid('getGridParam', 'selarrrow'); // 参数是数组的形式
	var title = '删除法律法规', content = '是否确认删除？';
	if (idlist.length == 0) {
		title = '提示', content = '请选择要删除的法律法规';
	}

	var myDialog = dialog({
		title : title,
		content : content,
		lock : true,
		okValue : '确定',
		ok : function() {
			if (idlist.length > 0) {
				delCallback();
			}
		},
		cancelValue : '取消',
		cancel : function() {
		}
	});

	myDialog.showModal();
}

function delCallback() {
	var idlist = $(grid_selector).jqGrid('getGridParam', 'selarrrow'); // 参数是数组的形式
	if (idlist.length == 0)
		return;
	$.ajax({
		url : home_url + '/law/update',
		data : {
			'oper' : 'batchdel',
			'idlist' : idlist
		},
		type : 'post',
		dataType : 'json',
		success : function(data) {
			$.jGrowl(data.message);
			$(grid_selector).jqGrid('setGridParam').trigger("reloadGrid");
		},
		error : function() {
			$.jGrowl("异常！请重新尝试或者联系管理员！", {
				header : "提醒"
			});
		}
	});
}
