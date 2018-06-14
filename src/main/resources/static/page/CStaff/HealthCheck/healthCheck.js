var healthCheck = (function() {
	return {
		init : function() {
			healthCheck.initjqGrid();
		},
		jqGridRedraw : function(data) {
			$("#healthChecktb").jqGrid('setGridParam', {})
					.trigger("reloadGrid");// 重新载入
		},
		search : function(data) {
			$("#id-date-range-picker-1").val();
			var time = $("#id-date-range-picker-1").val();
			var arr = time.split(' 至 ');
			/*
			 * 先清空条件 jqgrid postData setGridParam 调用多次时查询条件会累加
			 * 解决postData参数值累加问题  导致参数不准确
			 */
			var postData = $("#healthChecktb").jqGrid("getGridParam", "postData");
			$.each(postData, function(k, v) {
				delete postData[k];
			});
			
			$("#healthChecktb").jqGrid('setGridParam', {
				postData :{begintime:arr[0], endtime:arr[1]}
			}).trigger("reloadGrid");// 重新载入
		},
		initjqGrid : function() {
			// table 和 页脚的id
			var grid_selector = "#healthChecktb";
			var pager_selector = "#jqGridPager";
			jQuery(grid_selector)
					.jqGrid(
							{
								url : home_url + '/healthCheck/queryListData',
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
											label : "检测时间",
											name : "checkTime",
											index : "checkTime",
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
											label : "职业健康检查种类",
											name : "type",
											index : "type",
											editable : false,
											sortable : false,
											width : 10,
											formatter : function(cellvalue,
													options, rowObject) {
												if (cellvalue != null
														&& cellvalue != undefined) {
													var info = "";
													for (var k = 0; k < HealthCheckType.length; k++) {
														if (cellvalue.indexOf(HealthCheckType[k].value) != -1) {
															info += HealthCheckType[k].name
																	+ ",";
														}
													}
													// 去除最后一个逗号
													var reg = /,$/gi;
													info = info
															.replace(reg, "");
													return info;
												}
												return "";
											}
										},
										{
											label : "总检查人数",
											name : "staffcount",
											index : "staffcount",
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
											label : "服务机构名称",
											name : "othercompanyName",
											index : "othercompanyName",
											editable : false,
											sortable : false,
											width : 10,
											formatter : function(cellvalue,
													options, rowObject) {
												if (rowObject != null
														&& rowObject.othercompany != null
														&& rowObject.othercompany.companyname != null) {
													return rowObject.othercompany.companyname;
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
												var info ='<a href="javascript:healthCheckview('
													+ rowObject.id
													+ ');" class="ui-pg-div" title="查看"><i class="ui-icon icon-eye-open blue"></i></a>';
												info+= '<a href="javascript:healthCheckedit('
														+ rowObject.id
														+ ');" class="ui-pg-div" title="编辑"><i class="ui-icon icon-pencil green"></i></a>';
												info += ' <a href="javascript:healthCheckDel('
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


var abnormalCheck = (function() {
	return {
		init : function() {
			abnormalCheck.initjqGrid();
		},
		jqGridRedraw : function(data) {
			$("#abnormaltb").jqGrid('setGridParam', {})
					.trigger("reloadGrid");// 重新载入
		},
		initjqGrid : function() {
			// table 和 页脚的id
			var grid_selector = "#abnormaltb";
			var pager_selector = "#jqGridPager";
			jQuery(grid_selector)
					.jqGrid(
							{
								url : home_url + '/healthCheck/queryAbnormalListData',
								datatype : "json",
								mtype : "post",
								height : 'auto',
								postData : {
									isprocessed : isprocessed
								},
								viewrecords : true,// 显示总记录数
								autowidth : true,// 自动匹配宽度
								rowNum : 10,// 默认每页显示
								rowList : [ 5, 10, 15, 20, 25, 30 ],// 自定义每页显示下拉选
								pager : pager_selector,// 表格数据关联的分页条，html元素
								altRows : true,// 分页选项，可以下拉选择每页显示记录数
								rownumbers : true,// 显示序号
								colModel : [
										{
											//姓名,性别,部门/科室,岗位/工种,检查时间,职业健康检查种类,体检结论,复查/诊断时间,复查/诊断结论,人员处理情况,附件,操作

											label : "姓名",
											name : "realname",
											index : "realname",
											editable : false,
											sortable : false,
											width : 30,
											formatter : function(cellvalue,
													options, rowObject) {
												if (rowObject != null
														&& rowObject.staff != null
														&& rowObject.staff.basic != null
														&& rowObject.staff.basic.realname != null) {
													return rowObject.staff.basic.realname;
												}
												return "";
											}
										},
										{
											label : "性别",
											name : "gender",
											index : "gender",
											editable : false,
											sortable : false,
											width : 20,
											formatter : function(cellvalue,
													options, rowObject) {
												if (rowObject != null
														&& rowObject.staff != null
														&& rowObject.staff.basic != null
														&& rowObject.staff.basic.gender != null) {
													if(rowObject.staff.basic.gender==0){
														return '女';
													}else if(rowObject.staff.basic.gender==1){
														return '男';
													}
												}
												return "";
											}
										},
										
										{
											label : "部门/科室",
											name : "fullname",
											index : "fullname",
											editable : false,
											sortable : false,
											width : 40,
											formatter : function(cellvalue,
													options, rowObject) {
												if (rowObject != null
														&& rowObject.staff != null
														&& rowObject.staff.organization != null
														&& rowObject.staff.organization.fullname != null) {
														return rowObject.staff.organization.fullname;
												}
												return "";
											}
										},
										{
											label : "岗位/工种",
											name : "workname",
											index : "workname",
											editable : false,
											sortable : false,
											width : 40,
											formatter : function(cellvalue,
													options, rowObject) {
												if (rowObject != null
														&& rowObject.staff != null
														&& rowObject.staff.staffworkrole != null
														&& rowObject.staff.staffworkrole.workroleinfo != null
														&& rowObject.staff.staffworkrole.workroleinfo.workname != null) {
														return rowObject.staff.staffworkrole.workroleinfo.workname;
												}
												return "";
											}
										},
										{
											label : "检查时间",
											name : "checkTime",
											index : "checkTime",
											editable : false,
											sortable : false,
											width : 80,
											formatter : function(cellvalue,
													options, rowObject) {
												if (rowObject != null
														&&rowObject.check!=null) {
													var info = "";
													if (rowObject.check.begintime != null) {
														info += formatDate(new Date(
																rowObject.check.begintime))
																+ "";
													}
													if (rowObject.check.endtime != null) {
														info += " 至 ";
														info += formatDate(new Date(
																rowObject.check.endtime))
																+ "";
													}
													return info;
												}
												return "";
											}
										},
										
										{
											label : "职业健康检查种类",
											name : "checktype",
											index : "checktype",
											editable : false,
											sortable : false,
											width : 30,
											formatter : function(cellvalue,
													options, rowObject) {
												if (cellvalue != null
														&& cellvalue != undefined) {
													for (var k = 0; k < HealthCheckType.length; k++) {
														if (cellvalue==HealthCheckType[k].value) {
															return HealthCheckType[k].name;
														}
													}
													return "";
												}
												return "";
											}
										},
										{
											label : "体检结论",
											name : "checkresultName",
											index : "checkresultName",
											editable : false,
											sortable : false,
											width : 30,
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
											label : "复查/诊断时间",
											name : "diagnosistime",
											index : "diagnosistime",
											editable : false,
											sortable : false,
											width : 40,
											formatter : function(cellvalue,
													options, rowObject) {
												if (cellvalue != null
														&& cellvalue != undefined) {
													return  formatDate(new Date(cellvalue));
												}
												return "";
											}
										},
										
										{
											label : "复查/诊断结论",
											name : "diagnosis",
											index : "diagnosis",
											editable : false,
											sortable : false,
											width : 30,
											formatter : function(cellvalue,
													options, rowObject) {
												if (cellvalue != null
														&& cellvalue != undefined) {
													return  cellvalue;
												}
												return "";
											}
										},
										{
											label : "人员处理情况",
											name : "dealsituationtypeName",
											index : "dealsituationtypeName",
											editable : false,
											sortable : false,
											width : 30,
											formatter : function(cellvalue,
													options, rowObject) {
												if (cellvalue != null
														&& cellvalue != undefined) {
													return  cellvalue;
												}
												return "";
											}
										},
										{
											label : "附件",
											name : "fileid",
											index : "fileid",
											editable : false,
											sortable : false,
											width : 40,
											formatter : function(cellvalue,
													options, rowObject) {
												if (cellvalue != null
														&& cellvalue != undefined) {
													var info ="";
													
													var strs= new Array(); //定义一数组 
													strs=cellvalue.split(","); //字符分割 
													for (i=0;i<strs.length ;i++ ) 
													{ 
														info+= '<a target="_blank" href="'
															+ getfileUrl(strs[i])
															+ '">'
															+ getfileName(strs[i])
															+ '</a><br/>';
													} 
													
													return info;
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
											width : 20,
											formatter : function(cellvalue,
													options, rowObject) {
												var info = '<a href="javascript:abnormalCheckEdit('
														+ rowObject.id
														+ ');" class="ui-pg-div" title="编辑"><i class="ui-icon icon-pencil green"></i></a>';
												
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
function healthCheckview(id) {
	window.location.href = home_url + "/healthCheck/view?id=" + id;
}
function healthCheckedit(id) {
	window.location.href = home_url + "/healthCheck/edit?id=" + id;
}
function abnormalCheckEdit(id) {
	var myDialog = dialog({
		title : "编辑",
		okValue : '确定',
		width : 500,
		ok : function() {
			// 验证 验证失败则不进行下一步
			if (!$("#editAbnormalCheckTb").valid()) {
				return false;
			}
			$.ajax({
				type : "post",
				url : home_url + "/healthCheck/saveCheckData",
				data : $("#editAbnormalCheckTb").serialize(),
				dataType : "json",
				success : function(data) {
					if (data.code == 1) {
						abnormalCheck.jqGridRedraw();
					}
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					$.jGrowl("异常！请重新尝试或者联系管理员!", {
						header : "error"
					});
				}
			});
		},
		cancelValue : '取消',
		cancel : function() {
		}
	});
	$.ajax({
		url : home_url+"/healthCheck/abnormalCheckEdit?id="+id,
		success : function(data) {
			myDialog.content(data);
		}
	});
	myDialog.showModal();
}
function healthCheckDel(id) {
	delConfirmDiag(function() {
		$.ajax({
			type : "post",
			url : home_url + "/healthCheck/delCheck",
			data : {
				id : id,
			},
			dataType : "json",
			success : function(data) {
				if (data.code == 1) {
					healthCheck.jqGridRedraw();
				}
			},
			error : function(XMLHttpRequest, textStatus, errorThrown) {
				$.jGrowl("异常！请重新尝试或者联系管理员!", {
					header : "error"
				});
			}
		});
	})
}