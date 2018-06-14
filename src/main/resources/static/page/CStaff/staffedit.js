var OccupationaSysparamList;
var radiationExperience = (function() {
	return {
		init : function() {
			getOccupationaSysparamList();
			radiationExperience.initjqGrid();
		},
		jqGridRedraw : function(data) {
			$("#radiationExperiencetb").jqGrid('setGridParam', {
				staffid : staffid
			}).trigger("reloadGrid");// 重新载入
			if (isradiation != '' && isradiation == 0) {
				setTimeout(function (){
				if($("#radiationExperiencetb").jqGrid('getGridParam', 'records')==0){
					$('#MonitorHrefId').addClass('dn');
					$('#MonitorSpanId').removeClass('dn');
				}else{
					$('#MonitorHrefId').removeClass('dn');
					$('#MonitorSpanId').addClass('dn');
				}
				},200); 
			}
				
		},
		initjqGrid : function() {
			// table 和 页脚的id
			var grid_selector = "#radiationExperiencetb";
			var pager_selector = "#grid-pager";
			jQuery(grid_selector)
					.jqGrid(
							{
								url : home_url
										+ '/staff/getradiationExperienceList',
								datatype : "json",
								mtype : "post",
								postData : {
									staffid : staffid
								},
								height : 'auto',
								viewrecords : true,// 显示总记录数
								autowidth : true,// 自动匹配宽度
								rowNum : 5,// 默认每页显示
								rowList : [ 5, 10, 15, 20, 25, 30 ],// 自定义每页显示下拉选
								pager : pager_selector,// 表格数据关联的分页条，html元素
								altRows : true,// 分页选项，可以下拉选择每页显示记录数
								rownumbers : true,// 显示序号
								/*
								 * multiselect : true,//生成多选列 multiboxonly :
								 * true,//全选
								 */colNames : [ '单位名称', '岗位名称', "职业照射种类及其代码",
										'上岗时间', '转/离岗时间', '操作' ],
								colModel : [
										{
											name : "othercompany",
											index : "othercompany",
											editable : false,
											sortable : false,
											formatter : function(cellvalue,
													options, rowObject) {
												if (rowObject != null){
													if (rowObject.isthirdparty == 0){
														return companyname;
													}
													return cellvalue;
												}
												return "";
											}
										},
										{
											name : "rname",
											index : "rname",
											editable : false,
											sortable : false,
											formatter : function(cellvalue,
													options, rowObject) {
												//不能为放射工作新添的岗位特地设立一个岗位  所以直接用rname
												if (cellvalue != null
														&& cellvalue != undefined)
													return cellvalue;
												return "";
											}
										},
										{
											name : "radiationtype",
											index : "radiationtype",
											editable : false,
											sortable : false,
											formatter : function(cellvalue,
													options, rowObject) {
												if (cellvalue != null
														&& cellvalue != undefined) {
													for (var i = 0; i < OccupationaSysparamList.length; i++) {
														if (cellvalue == OccupationaSysparamList[i].paramcode) {
															return OccupationaSysparamList[i].paramvalue;
														}
													}
												}
												return "";
											}
										},
										{
											name : "begintime",
											index : "begintime",
											editable : false,
											sortable : false,
											formatter : function(cellvalue,
													options, rowObject) {
												if (cellvalue != null
														&& cellvalue != undefined)
													return formatDateTime(cellvalue);
												return "";
											}
										},
										{
											name : "leavetime",
											index : "leavetime",
											editable : false,
											sortable : false,
											formatter : function(cellvalue,
													options, rowObject) {
												if (cellvalue != null
														&& cellvalue != undefined){
													return formatDateTime(cellvalue);
												}
												return "";
											}
										},
										{
											name : null,
											index : null,
											editable : false,
											sortable : false,
											formatter : function(cellvalue,
													options, rowObject) {
												var info ="";
												if (rowObject != null
														&& rowObject.type == 2){
												info= '<a href="javascript:editradiationExperience('
														+ rowObject.id
														+ ');" class="ui-pg-div" data-rel="tooltip" data-original-title="编辑" ><i class="ui-icon icon-pencil green"></i></a>';
												info += ' <a href="javascript:delradiationExperience('
														+ rowObject.id
														+ ');" class="ui-pg-div" data-rel="tooltip" data-original-title="删除"><i class="ui-icon icon-trash red"></i></a>';
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
/*// 人员附件
var staffFile = (function() {
	return {
		init : function() {
			staffFile.initjqGrid();
		},
		jqGridRedraw : function(data) {
			$("#filetb").jqGrid('setGridParam', {
				staffid : staffid
			}).trigger("reloadGrid");// 重新载入
		},
		initjqGrid : function() {
			// table 和 页脚的id
			var grid_selector = "#filetb";
			var pager_selector = "#grid-pager2";
			jQuery(grid_selector)
					.jqGrid(
							{
								url : home_url + '/staff/getFileList',
								datatype : "json",
								mtype : "post",
								postData : {
									staffid : staffid
								},
								height : 'auto',
								viewrecords : true,// 显示总记录数
								autowidth : true,// 自动匹配宽度
								rowNum : 5,// 默认每页显示
								rowList : [ 5, 10, 15, 20, 25, 30 ],// 自定义每页显示下拉选
								pager : pager_selector,// 表格数据关联的分页条，html元素
								altRows : true,// 分页选项，可以下拉选择每页显示记录数
								rownumbers : true,// 显示序号
								
								 * multiselect : true,//生成多选列 multiboxonly :
								 * true,//全选
								 colNames : [ '名称', "附件", '操作' ],
								colModel : [
										{
											name : "filename",
											index : "filename",
											editable : false,
											sortable : false,
											formatter : function(cellvalue,
													options, rowObject) {
												if (cellvalue != null
														&& cellvalue != undefined)
													return cellvalue;
												return "";
											}
										},
										{
											name : "file",
											index : "file",
											editable : false,
											sortable : false,
											formatter : function(cellvalue,
													options, rowObject) {
												if (rowObject != null
														&& rowObject.file != null
														&& rowObject.file.filename != null) {
													var info = '<a target="_blank" href="'
														+ getfileUrl(rowObject.fileid)
														+ '">'
														+ getfileName(rowObject.fileid)
														+ '</a>';
													return info;
												}
												return "";
											}
										},
										{
											name : null,
											index : null,
											editable : false,
											sortable : false,
											formatter : function(cellvalue,
													options, rowObject) {
												var info = '<a href="javascript:editFile('
														+ rowObject.id
														+ ');" class="ui-pg-div" data-rel="tooltip" data-original-title="编辑" ><i class="ui-icon icon-pencil green"></i></a>';
												info += ' <a href="javascript:delFile('
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
// 个人证书
var cerSysparamList;
var staffCer = (function() {
	return {
		init : function() {
			getcerSysparamList();
			getcerSysparamTypeList();
			staffCer.initjqGrid();
		},
		jqGridRedraw : function(data) {
			$("#staffcertb").jqGrid('setGridParam', {
				staffid : staffid
			}).trigger("reloadGrid");// 重新载入
		},
		initjqGrid : function() {
			// table 和 页脚的id
			var grid_selector = "#staffcertb";
			var pager_selector = "#grid-pager3";
			jQuery(grid_selector)
					.jqGrid(
							{
								url : home_url + '/Certificate/getStaffCerList',
								datatype : "json",
								mtype : "post",
								postData : {
									staffid : staffid
								},
								height : 'auto',
								viewrecords : true,// 显示总记录数
								autowidth : true,// 自动匹配宽度
								rowNum : 5,// 默认每页显示
								rowList : [ 5, 10, 15, 20, 25, 30 ],// 自定义每页显示下拉选
								pager : pager_selector,// 表格数据关联的分页条，html元素
								altRows : true,// 分页选项，可以下拉选择每页显示记录数
								rownumbers : true,// 显示序号
								
								 * multiselect : true,//生成多选列 multiboxonly :
								 * true,//全选
								 

								colModel : [
										{
											label : '证书名字',
											name : "certificatename",
											index : "certificatename",
											editable : false,
											sortable : false,
											formatter : function(cellvalue,
													options, rowObject) {
												if (cellvalue != null
														&& cellvalue != undefined) {
													for (var i = 0; i < cerSysparamList.length; i++) {
														if (cellvalue == cerSysparamList[i].id) {
															return cerSysparamList[i].typevalue;
														}
													}
												}
												return "";
											}
										},
										{
											label : '证书类型',
											name : "filetype",
											index : "filetype",
											editable : false,
											sortable : false,
											formatter : function(cellvalue,
													options, rowObject) {
												if (cellvalue != null
														&& cellvalue != undefined) {
													for (var i = 0; i < cerSysparamTypeList.length; i++) {
														if (cellvalue == cerSysparamTypeList[i].paramcode) {
															return cerSysparamTypeList[i].paramvalue;
														}
													}
												}
												return "";
											}
										},
										{
											label : '证书编号',
											name : "certificateno",
											index : "certificateno",
											editable : false,
											sortable : false,
											formatter : function(cellvalue,
													options, rowObject) {
												if (cellvalue != null
														&& cellvalue != undefined)
													return cellvalue;
												return "";
											}
										},
										{
											label : '取证时间',
											name : "begintime",
											index : "begintime",
											editable : false,
											sortable : false,
											formatter : function(cellvalue,
													options, rowObject) {
												if (cellvalue != null
														&& cellvalue != undefined)
													return formatDate(new Date(
															cellvalue));
												return "";
											}
										},
										{
											label : '到期时间',
											name : "expiretime",
											index : "expiretime",
											editable : false,
											sortable : false,
											formatter : function(cellvalue,
													options, rowObject) {
												if (cellvalue != null
														&& cellvalue != undefined)
													return formatDate(new Date(
															cellvalue));
												return "";
											}
										},
										{
											label : '复审日期',
											name : "repeatTime",
											index : "repeatTime",
											editable : false,
											sortable : false,
											formatter : function(cellvalue,
													options, rowObject) {
												if (cellvalue != null
														&& cellvalue != undefined)
													return cellvalue;
												return "";
											}
										},
										{
											label : '证书扫描件',
											name : "file",
											index : "file",
											editable : false,
											sortable : false,
											width : 100,
											formatter : function(cellvalue,
													options, rowObject) {
												if (rowObject != null
														&& rowObject.file != null
														&& rowObject.file.filename != null) {
													var info = '<a target="_blank" href="'
														+ getfileUrl(rowObject.ossid)
														+ '">'
														+ getfileName(rowObject.ossid)
														+ '</a>';
													return info;
												}
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
})();*/
// 日期格式化
function formatDateTime(inputTime) {
	var date = new Date(inputTime);
	var y = date.getFullYear();
	var m = date.getMonth() + 1;
	m = m < 10 ? ('0' + m) : m;
	var d = date.getDate();
	d = d < 10 ? ('0' + d) : d;
	return y + '-' + m + '-' + d;
};
// 获取职业照射种类及代码系统参数
function getOccupationaSysparamList() {
	$.ajax({
		type : "post",
		url : home_url + '/staff/getOccupationaSysparamList',
		async : false,
		success : function(data) {
			OccupationaSysparamList = data.data;
		}
	});
}
// 编辑新增放射经历
function editradiationExperience(id) {
	var title = "新增放射工作经历";
	var url = home_url + "/staff/editExperience?staffid=" + staffid;
	if (id != null) {
		title = "编辑放射工作经历";
		url = home_url + "/staff/editExperience?staffid=" + staffid + "&id="
				+ id;
	}
	showDialogModal(title, url, function() {
		// 验证 验证失败则不进行下一步
		if (!$("#experienceForm").valid()) {
			return false;
		}
		submitForm("#experienceForm", home_url + '/staff/updateExperience',
				null, function(data) {
					_contentLoadTriggered = false;
					alert(data.message);
					if (data.code == 1) {
						radiationExperience.jqGridRedraw();
					} else {
						// showTipDialog(data.message);
					}
				}, 'json');
	}, 600, 'auto');
}
// 删除放射经历
function delradiationExperience(id) {
	$.ajax({
		type : "post",
		url : home_url + '/staff/delExperience',
		data : {
			id : id
		},
		async : false,
		success : function(data) {
			alert(data.message);
			if (data.code == 1) {
				radiationExperience.jqGridRedraw();
			}
		}
	});
}
// 保存基础信息
function saveBasic() {
	if (!$("#basicForm").valid()) {
		return false;
	}
	$.ajax({
		type : 'POST',
		url : home_url + "/staff/saveStaff",
		data : $("#basicForm").serialize(),
		dataType : 'json',
		success : function(data) {
			alert(data.message);
			if(data.code>0){
				window.location.replace(home_url + "/staff/editStaff?id="+ data.data);
			}
		}
	});
}
// 保存职业卫生信息
function saveHealth() {
	if (!$("#healthForm").valid()) {
		return false;
	}
	$.ajax({
		type : 'POST',
		url : home_url + "/staff/updateStaff",
		data : $("#healthForm").serialize(),
		dataType : 'json',
		success : function(data) {
			if (data.code == 1) {
				window.location.replace(home_url + "/staff/editStaff?id="+ data.data);
			} else if (data.code == 0) {
				alert("编辑失败");
			}
		}
	});
}
// 编辑人员附件信息
function editFile(id) {
	var title = "新增附件";
	var url = home_url + "/staff/editFile?staffid=" + staffid;
	if (id != null) {
		title = "编辑附件";
		url = home_url + "/staff/editFile?staffid=" + staffid + "&id=" + id;
	}
	showDialogModal(title, url, function() {
		// 验证 验证失败则不进行下一步
		if (!$("#fileForm").valid()) {
			return false;
		}
		submitForm("#fileForm", home_url + '/staff/updateFile', null, function(
				data) {
			_contentLoadTriggered = false;
			alert(data.message);
			if (data.code == 1) {
				staffFile.jqGridRedraw();
			} else {
				// showTipDialog(data.message);
			}
		}, 'json');
	}, 600, 'auto');
}

// 删除人员附件
function delFile(id) {
	$.ajax({
		type : "post",
		url : home_url + '/staff/delFile',
		data : {
			id : id
		},
		async : false,
		success : function(data) {
			alert(data.message);
			if (data.code == 1) {
				staffFile.jqGridRedraw();
			}
		}
	});
}

// 组织单选组件
function selectorganize() {
	SelectOrg_Single(function(data) {
		$("#orgulId li").remove();
		var str = ' <li class="success"><span>'
				+ data.name
				+ '</span> <a href="javascript:;" onclick="$(this).li_remove();"> <i class="icon-remove white"></i> </a></li>';
		$("#orgulId").append(str);
		$("input[name=orgid]").val(data.id);
	});
}

// 所在院区
function selectArea() {
	SelectAreaLevel1(function(data) {
		$("#areaulId li").remove();
		var str = ' <li class="warning"><span>'
				+ data.name
				+ '</span> <a href="javascript:;" onclick="$(this).li_remove();"> <i class="icon-remove white"></i> </a></li>';
		$("#areaulId").append(str);
		$("input[name=areaid]").val(data.id);
	});

}

// 岗位/工种
function selectworkrole() {
	SelectWorkRole(function(data) {
		$("#workroleulId li").remove();
		var str = ' <li class="warning"><span>'
				+ data.name
				+ '</span> <a href="javascript:;" onclick="$(this).li_remove();"> <i class="icon-remove white"></i> </a></li>';
		$("#workroleulId").append(str);
		$("#workroleid").val(data.id);
		$("#workrolename").val(data.name);
	});

}

// 获取证书名字系统参数
function getcerSysparamList() {
	$.ajax({
		type : "post",
		url : home_url + '/Certificate/getSysparamtypeListData',
		async : false,
		success : function(data) {
			cerSysparamList = data.data;
		}
	});
}
// 获取证书类型系统参数
function getcerSysparamTypeList() {
	$.ajax({
		type : "post",
		url : home_url + '/Certificate/getcerSysparamTypeList',
		async : false,
		success : function(data) {
			cerSysparamTypeList = data.data;
		}
	});
}

//人员单选
function selectchargeStaff() {
	SelectStaff_Single(function(data) {
		$("#staffulId li").remove();
		$("#staffulId").append(dataNodeSelected(data.id, data.name));
		$("#chargeid").val(data.id);
	});
}
