$(function() {
	var gridid = 'grid_table';
	var grid_selector = "#" + gridid;
	var pager_selector = "#grid_pager";

	$(grid_selector).jqGrid({
		url : 'area/getData',
		datatype : "json",
		colNames : [ '编号', '上级编号', '区域名称', '经度', '纬度', '级别' ],
		colModel : [ {
			name : 'id'
		}, {
			name : "pid"
		}, {
			name : "name"
		}, {
			name : "longitude"
		}, {
			name : "latitude"
		}, {
			name : "level"
		} ],

		viewrecords : true,
		autowidth : true, // 宽度自适应
		height : 'auto', // 高度自适应
		rownumbers : true, // 显示序号
		rownumWidth : 60,// 序号列宽度
		rowNum : 10,
		rowList : [ 10, 20, 30 ],
		//caption: "行政区域列表",
		pager : pager_selector
	});

});

// 查询
function query() {
	var data = {
		updateBegin : $("#form input[name='updateBegin'").val(),
		updateEnd : $("#form input[name='updateEnd'").val(),
		type : $("#type").val()
	};
	var postData = $("#grid_table").jqGrid("getGridParam", "postData");
	// 将查询参数融入postData选项对象
	$.extend(postData, data);
	$("#grid_table").jqGrid('setGridParam').trigger("reloadGrid");
}