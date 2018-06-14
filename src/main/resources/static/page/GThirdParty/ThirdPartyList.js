var thirdParty = (function() {
	return {
		init : function() {
			thirdParty.initjqGrid();
		},
		jqGridRedraw : function(data) {
			$("#thirdPartytb").jqGrid('setGridParam', {
				
			})
					.trigger("reloadGrid");// 重新载入
		},
		search : function(data) {
			$("#thirdPartytb").jqGrid('setGridParam', {
				postData:{paramName:$("#keywords").val()}
			}).trigger("reloadGrid");// 重新载入
		},
		initjqGrid : function() {
			// table 和 页脚的id
			var grid_selector = "#thirdPartytb";
			var pager_selector = "#jqGridPager";
			jQuery(grid_selector)
					.jqGrid(
							{
								url : home_url + '/ThirdParty/queryListData',
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
											label : "名称",
											name : "companyname",
											index : "companyname",
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
											label : "类型",
											name : "type",
											index : "type",
											editable : false,
											sortable : false,
											width : 10,
											formatter : function(cellvalue,
													options, rowObject) {
												if (cellvalue != null
														&& cellvalue != undefined) {
													for (var k = 0; k < thirdPartyType.length; k++) {
														if (cellvalue==thirdPartyType[k].value) {
															return thirdPartyType[k].name;
														}
													}
													return "";
												}
												return "";
											}
										},
										{
											label : "联系人",
											name : "managername",
											index : "managername",
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
											label : "联系方式",
											name : "managertel",
											index : "managertel",
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
											label : "更新时间",
											name : "updatetime",
											index : "updatetime",
											editable : false,
											sortable : false,
											width : 10,
											formatter : function(cellvalue,
													options, rowObject) {
												if (cellvalue != null
														&& cellvalue != undefined)
													return formatDate(new Date(cellvalue));
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
												var info = '<a href="javascript:thirdPartyview('
														+ rowObject.id
														+ ');" class="ui-pg-div" data-rel="tooltip" data-original-title="查看" ><i class="ui-icon icon-eye-open blue"></i></a>';
												info += ' <a href="javascript:thirdPartyDel('
														+ rowObject.id
														+ ');" class="ui-pg-div" data-rel="tooltip" data-original-title="删除"><i class="ui-icon icon-trash red"></i></a>';
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


var blank = (function() {
	return {
		init : function() {
			blank.initjqGrid();
		},
		jqGridRedraw : function(data) {
			$("#blanktb").jqGrid('setGridParam', {
				
			}).trigger("reloadGrid");// 重新载入
		},
		initjqGrid : function() {
			// table 和 页脚的id
			var grid_selector = "#blanktb";
			var pager_selector = "#jqGridPager";
			jQuery(grid_selector)
					.jqGrid(
							{
								url : home_url + '/ThirdParty/queryBlankListData',
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
								multiselect : true,// 生成多选列
								multiboxonly : true,// 全选
								colModel : [
										{
											label : "名称",
											name : "companyname",
											index : "companyname",
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
											label : "类型",
											name : "type",
											index : "type",
											editable : false,
											sortable : false,
											width : 10,
											formatter : function(cellvalue,
													options, rowObject) {
												if (cellvalue != null
														&& cellvalue != undefined) {
													for (var k = 0; k < thirdPartyType.length; k++) {
														if (cellvalue==thirdPartyType[k].value) {
															return thirdPartyType[k].name;
														}
													}
													return "";
												}
												return "";
											}
										},
										{
											label : "联系人",
											name : "managername",
											index : "managername",
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
											label : "联系方式",
											name : "managertel",
											index : "managertel",
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
											label : "更新时间",
											name : "updatetime",
											index : "updatetime",
											editable : false,
											sortable : false,
											width : 10,
											formatter : function(cellvalue,
													options, rowObject) {
												if (cellvalue != null
														&& cellvalue != undefined)
													return formatDate(new Date(cellvalue));
												return "";
											}
										},
										{
											label : "原因",
											name : "reason",
											index : "reason",
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

function thirdPartyview(id){
	window.location.href=home_url+"/ThirdParty/ThirdPartyEdit?id="+id;
}

function thirdPartyDel(id) {
	delConfirmDiag(function() {
	$.ajax({
		type : "post",
		url : home_url + "/ThirdParty/delThirdParty",
		data : {id:id},
		dataType : "json",
		success : function(data) {
			if (data.code == 1) {
				thirdParty.jqGridRedraw();
			}
			$.jGrowl(data.message, {
				header : "提示"
			});
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			$.jGrowl("异常！请重新尝试或者联系管理员!", {
				header : "error"
			});
		}
	});
	});
}

function exportThirdParty(){
	window.location.href=home_url+"/ThirdParty/exportThirdParty";
}
function exportBlank(){
	window.location.href=home_url+"/ThirdParty/exportBlank";
}
