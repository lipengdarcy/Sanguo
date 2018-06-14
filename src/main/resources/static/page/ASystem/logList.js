$(function() {
	var gridid = 'grid_table';
	var grid_selector = "#" + gridid;
	var pager_selector = "#grid_pager";

	$(grid_selector)
			.jqGrid(
					{
						url : 'logData',
						datatype : "json",
						colNames : [ '类型', '账号', '姓名', '操作时间', 'IP', '内容' ],
						colModel : [
								{
									sortable : false,
									name : 'type',
									formatter : function(data, options,
											rowObject) {
										var temp = '自定义';
										if (data == 0) {
											return "登录";
										} else if (data == 1) {
											return "新增";
										} else if (data == 2) {
											return "修改";
										} else if (data == 3) {
											return "删除(逻辑)";
										} else if (data == 4) {
											return "删除(物理)";
										}
										return temp;
									}

								},
								{
									sortable : false,
									name : "user.username"
								},
								{
									sortable : false,
									name : "user.fullname"
								},
								{
									name : "createtime",
									formatter : function(cellvalue, options,
											rowObject) {
										if (cellvalue == undefined)
											return "";
										return moment(new Date(cellvalue))
												.format("YYYY-MM-DD hh:mm:ss");
									}
								}, {
									name : "operateip"
								}, {
									sortable : false,
									name : "description"
								} ],

						viewrecords : true,
						autowidth : true, // 宽度自适应
						height : 'auto', // 高度自适应
						rownumbers : true, // 显示序号
						rownumWidth : 60,// 序号列宽度
						rowNum : 10,
						rowList : [ 10, 20, 30 ],
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