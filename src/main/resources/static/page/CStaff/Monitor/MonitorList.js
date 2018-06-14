var monitor = (function() {
	return {
		init : function() {
			monitor.initjqGrid();
		},
		jqGridRedraw : function() {
			var grid_selector = "#monitortb";
			$(grid_selector).jqGrid('setGridParam', {
				postData : {}
			}).trigger("reloadGrid");// 重新载入
		},
		search : function(data) {
			$("#id-date-range-picker-1").val();
			var time = $("#id-date-range-picker-1").val();
			var arr = time.split(' 至 ');
			/*
			 * 先清空条件 jqgrid postData setGridParam 调用多次时查询条件会累加 解决postData参数值累加问题
			 * 导致参数不准确
			 */
			var postData = $("#monitortb").jqGrid("getGridParam", "postData");
			$.each(postData, function(k, v) {
				delete postData[k];
			});
			$("#monitortb").jqGrid('setGridParam', {
				postData : {
					begintime : arr[0],
					endtime : arr[1]
				}
			}).trigger("reloadGrid");// 重新载入
		},
		initjqGrid : function() {
			// table 和 页脚的id
			var grid_selector = "#monitortb";
			var pager_selector = "#jqGridPager";
			jQuery(grid_selector)
					.jqGrid(
							{
								url : home_url + '/monitor/queryMonitorList',
								datatype : "json",
								mtype : "post",
								postData : {

								},
								height : 'auto',
								viewrecords : true,// 显示总记录数
								autowidth : true,// 自动匹配宽度
								rowNum : 5,// 默认每页显示
								rowList : [ 5, 10, 15, 20, 25, 30 ],// 自定义每页显示下拉选
								pager : pager_selector,// 表格数据关联的分页条，html元素
								altRows : true,// 分页选项，可以下拉选择每页显示记录数
								rownumbers : true,// 显示序号
								multiselect : true,// 生成多选列
								multiboxonly : true,// 全选
								colModel : [
										{
											label : "监测时间",
											name : "monitorTime",
											index : "monitorTime",
											editable : false,
											sortable : false,
											width : 10,
											formatter : function(cellvalue,
													options, rowObject) {
												if (rowObject != null) {
													var info = "";
													if (rowObject.begintime != null) {
														info += formatDate(new Date(
																rowObject.begintime))
																+ "";
													}
													if (rowObject.endtime != null) {
														info += " 至 ";
														info += formatDate(new Date(
																rowObject.endtime))
																+ "";
													}
													return info;
												}
												return "";
											}
										},
										{
											label : "监测人员",
											name : "totalcount",
											index : "totalcount",
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
											label : "可疑人员 ",
											name : "abnormalcount",
											index : "abnormalcount",
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
											label : "操作",
											name : null,
											index : null,
											editable : false,
											sortable : false,
											width : 10,
											formatter : function(cellvalue,
													options, rowObject) {
												var info = '<a href="javascript:monitorview('
														+ rowObject.id
														+ ');" class="ui-pg-div" title="查看"><i class="ui-icon icon-eye-open blue"></i></a>';
												info += '<a href="javascript:monitoredit('
														+ rowObject.id
														+ ');" class="ui-pg-div" title="编辑"><i class="ui-icon icon-pencil green"></i></a>';
												info += ' <a href="javascript:monitorDel();" class="ui-pg-div" data-rel="tooltip" data-original-title="删除"><i class="ui-icon icon-trash red"></i></a>';
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

var LargeDose = (function() {
	return {
		init : function() {
			LargeDose.initjqGrid();
		},
		jqGridRedraw : function() {
			var grid_selector = "#LargeDosetb";
			$(grid_selector).jqGrid('setGridParam', {
				postData : {}
			}).trigger("reloadGrid");// 重新载入
		},
		search : function(data) {
			$("#LargeDosetb").jqGrid('setGridParam', {
				postData : {
					parameterName : $("#keyword").val()
				}
			}).trigger("reloadGrid");// 重新载入
		},
		initjqGrid : function() {
			// table 和 页脚的id
			var grid_selector = "#LargeDosetb";
			var pager_selector = "#jqGridPager";
			jQuery(grid_selector)
					.jqGrid(
							{
								url : home_url + '/monitor/queryLargeDoseList',
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
								multiselect : true,// 生成多选列
								multiboxonly : true,// 全选
								gridComplete: function(data){//气泡 
									var rowIds = jQuery(grid_selector).jqGrid('getDataIDs');// 获取jqgrid中所有数据行的id
									for (var k = 0; k < rowIds.length; k++) {
										var rowId = rowIds[k];
										var curRowData = jQuery(grid_selector)
												.jqGrid('getRowData', rowId);// 获取指定id所在行的所有数据.
										if (curRowData.status == '已终止') {
											$('#'+rowId).find("input[type='checkbox']").attr('disabled', 'disabled');
										 }
									}
								},
								onSelectAll : function(rowid, status) { // 点击全选时触发事件
									var rowIds = jQuery(grid_selector).jqGrid('getDataIDs');// 获取jqgrid中所有数据行的id
									for (var k = 0; k < rowIds.length; k++) {
										var curRowData = jQuery(grid_selector)
												.jqGrid('getRowData', rowIds[k]);// 获取指定id所在行的所有数据.
										if (curRowData.status == '已终止') {
											 $(grid_selector).jqGrid("setSelection", rowIds[k],false);//设置改行不能被选中。
										 }
									}
								},
								onSelectRow : function(id)// 选择某行时触发事件
								{
									var curRowData = jQuery(grid_selector)
											.jqGrid('getRowData', id);
									if (curRowData.status == '已终止') {
										$(grid_selector).jqGrid("setSelection",
												id, false);
									}
								},
								colModel : [
										{
											label : "佩戴人",
											name : "Wearer",
											index : "Wearer",
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
											label : "剂量计佩戴截止日期",
											name : "monitorTime",
											index : "monitorTime",
											editable : false,
											sortable : false,
											width : 10,
											formatter : function(cellvalue,
													options, rowObject) {
												if (rowObject != null
														&& rowObject.dosemonitor != null) {
													var info = "";
													if (rowObject.dosemonitor.begintime != null) {
														info += formatDate(new Date(
																rowObject.dosemonitor.begintime))
																+ "";
													}
													if (rowObject.dosemonitor.endtime != null) {
														info += " 至 ";
														info += formatDate(new Date(
																rowObject.dosemonitor.endtime))
																+ "";
													}
													return info;
												}
												return "";
											}
										},
										{
											label : "监测机构",
											name : "monitorOrg",
											index : "monitorOrg",
											editable : false,
											sortable : false,
											width : 10,
											formatter : function(cellvalue,
													options, rowObject) {
												if (rowObject != null
														&& rowObject.dosemonitor != null
														&& rowObject.dosemonitor.othercompany != null
														&& rowObject.dosemonitor.othercompany.companyname != null) {
													return rowObject.dosemonitor.othercompany.companyname;
												}
												return "";
											}
										},
										{
											label : "本次监测结果",
											name : "data",
											index : "data",
											editable : false,
											sortable : false,
											width : 10,
											formatter : function(cellvalue,
													options, rowObject) {
												if (cellvalue != null
														&& cellvalue != undefined) {
													return cellvalue + "mSv";
												}
												return "";
											}
										},
										{
											label : "调查人",
											name : "checker",
											index : "checker",
											editable : false,
											sortable : false,
											width : 10,
											formatter : function(cellvalue,
													options, rowObject) {
												if (rowObject != null
														&& rowObject.checkStaff != null
														&& rowObject.checkStaff.basic != null
														&& rowObject.checkStaff.basic.realname != null)
													return rowObject.checkStaff.basic.realname;
												return "";
											}
										},
										{
											label : "调查状态",
											name : "status",
											index : "status",
											editable : false,
											sortable : false,
											width : 10,
											formatter : function(cellvalue,
													options, rowObject) {
												if (cellvalue != null
														&& cellvalue != undefined) {
													if (cellvalue == 1) {
														return '未完成';
													}
													if (cellvalue == 2) {
														return '已完成';
													}
													if (cellvalue == 3) {
														return '已终止';
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
												var info = '<a href="javascript:LargeDoseView('
														+ rowObject.id
														+ ');" class="ui-pg-div" title="查看"><i class="ui-icon icon-eye-open blue"></i></a>';
												if (rowObject.status != 3) {
													info += '<a href="javascript:LargeDoseedit('
															+ rowObject.id
															+ ');" class="ui-pg-div" title="编辑"><i class="ui-icon icon-pencil green"></i></a>';
												}
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

function LargeDoseedit(id) {
	window.location.href = home_url + "/monitor/LargeDose/edit?id=" + id;
}
function LargeDoseView(id) {
	window.location.href = home_url + "/monitor/LargeDose/view?id=" + id;
}

function monitoredit(id) {
	if (id != null) {
		window.location.href = home_url + "/monitor/edit?id=" + id;
	} else {
		void (window.open(home_url + "/monitor/edit"));
	}
}
function monitorview(id) {
	window.location.href = home_url + "/monitor/view?id=" + id;
}
function monitorDel() {
	var ids = $('#monitortb').jqGrid('getGridParam', 'selarrrow');
	if (ids != null && ids.length > 0) {

	} else {
		alert("请至少选择一条数据！");
		return false;
	}
	delConfirmDiag(function() {
		$.ajax({
			type : "post",
			url : home_url + "/monitor/delMonitor",
			data : {
				idList : ids
			},
			dataType : "json",
			success : function(data) {
				if (data.code == 1) {
					monitor.jqGridRedraw();
				}
			},
			error : function(XMLHttpRequest, textStatus, errorThrown) {
				$.jGrowl("异常！请重新尝试或者联系管理员!", {
					header : "error"
				});
			}
		});
	});
}