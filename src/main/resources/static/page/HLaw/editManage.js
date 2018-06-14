var grid_selector = "#grid_table";
var pager_selector = "#grid_pager";

$(function() {

	$("#lawType").change(function() {
		var type = $(this).val();
		var data = {
			subtype : type
		};
		var postData = $("#grid_table").jqGrid("getGridParam", "postData");
		// 将查询参数融入postData选项对象
		$.extend(postData, data);
		$(grid_selector).jqGrid('setGridParam').trigger("reloadGrid");
	});

	$(grid_selector)
			.jqGrid(
					{
						url : home_url + '/law/listData?type=3',
						editurl : home_url + '/law/update',
						datatype : "json",
						colNames : [ 'ID', '名称', '类别', '文件号', '版本号', '实施日期',
								'附件' ],
						colModel : [
								{
									name : 'id',
									hidden : true,
									width : 100
								},
								{
									name : 'name',
									sortable : false,
									width : 170
								},
								{
									name : 'type',
									sortable : false,
									width : 163,
									formatter : function(cellvalue, options,
											rowObject) {
										return getType(cellvalue);
									}
								},
								{
									name : 'fileno',
									sortable : false,
									width : 170
								},
								{
									name : 'version',
									sortable : false,
									width : 170
								},
								{
									name : 'effecttime',
									formatter : function(cellvalue, options,
											rowObject) {
										if (cellvalue == undefined)
											return "";
										return moment(new Date(cellvalue))
												.format("YYYY-MM-DD");
									},
									hidden : true
								},
								{
									name : 'fileid',
									sortable : false,
									formatter : function(cellvalue, options,
											rowObject) {
										if (!rowObject.file)
											return "";
										var temp = '<a dataid="'
												+ rowObject.fileid
												+ '" target="_blank" href="'
												+ getfileUrl(rowObject.fileid)
												+ '">'+getfileName(rowObject.fileid)+'</a>';
										return temp;
									},
									hidden : true
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

	// 分页
	$(grid_selector).navGrid(pager_selector, {
		edit : false,
		add : false,
		del : false,
		search : false,
		refresh : false,
		view : false,
		position : "left",
		cloneToTop : false
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
