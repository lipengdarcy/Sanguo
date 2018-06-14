var certificateSafe = (function() {
	return {
		init : function() {
			certificateSafe.initjqGrid();
		},
		jqGridRedraw : function(data) {
			$("#keywords").val("");
			$("#certificateSafetb").jqGrid('setGridParam', {

			}).trigger("reloadGrid");// 重新载入
		},
		search : function(data) {
			$("#certificateSafetb").jqGrid('setGridParam', {
				postData : {
					realname : $("#keywords").val()
				}
			}).trigger("reloadGrid");// 重新载入
		},
		initjqGrid : function() {
			// table 和 页脚的id
			var grid_selector = "#certificateSafetb";
			var pager_selector = "#grid-pager";
			jQuery(grid_selector)
					.jqGrid(
							{
								url : home_url
										+ '/Certificate/queryCertificateListData?fileType='
										+ fileType,
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
								/*
								 * multiselect : true,// 生成多选列 multiboxonly :
								 * true,// 全选
								 */
								colModel : [
										{
											label : '姓名',
											name : "realname",
											index : "realname",
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
											label : '证件号码',
											name : "IdCard",
											index : "IdCard",
											editable : false,
											sortable : false,
											width : 10,
											formatter : function(cellvalue,
													options, rowObject) {
												if (rowObject != null
														&& rowObject.staff != null
														&& rowObject.staff.basic != null
														&& rowObject.staff.basic.idcard != null)
													return rowObject.staff.basic.idcard;
												return "";
											}
										},
										{
											label : "证书编号",
											name : 'certificateno',
											index : 'certificateno',
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
											label : "上次培训时间",
											name : 'begintime',
											index : 'begintime',
											editable : false,
											sortable : false,
											width : 10,
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
											label : "证书扫描件",
											name : 'ossid',
											index : 'ossid',
											editable : false,
											sortable : false,
											width : 10,
											formatter : function(cellvalue,
													options, rowObject) {
												if (cellvalue != null
														&& cellvalue != ""
														&& cellvalue != undefined) {
													console.log(cellvalue);
													var link = '<a dataid="'
															+ cellvalue
															+ '" target="_blank" href="'
															+ getfileUrl(cellvalue)
															+ '">'
															+ getfileName(cellvalue)
															+ '</a>';
													return link;
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
												var info = '<a href="javascript:cerview('
														+ rowObject.staffid
														+ ');" class="ui-pg-div" data-rel="tooltip" data-original-title="查看" ><i class="ui-icon icon-eye-open blue"></i></a>';
												info += '<a href="javascript:ceredit('
														+ rowObject.staffid
														+ ');" class="ui-pg-div" title="编辑"><i class="ui-icon icon-pencil green"></i></a>';
												info += ' <a href="javascript:cerDel('
														+ rowObject.staffid
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

var trainingSafe = (function() {
	return {
		init : function(staffid) {
			trainingSafe.initjqGrid(staffid);
		},
		jqGridRedraw : function(data) {
			$("#trainingSafetb").jqGrid('setGridParam', {
				postData : {
					fileType : fileType,
					id : staffid
				}
			}).trigger("reloadGrid");// 重新载入
		},
		initjqGrid : function(staffid) {
			// table 和 页脚的id
			var grid_selector = "#trainingSafetb";
			var pager_selector = "#grid-pager1";
			jQuery(grid_selector)
					.jqGrid(
							{
								url : home_url
										+ '/Certificate/queryTrainingListData',
								postData : {
									fileType : fileType,
									id : staffid
								},
								datatype : "json",
								mtype : "post",
								height : 'auto',
								viewrecords : true,// 显示总记录数
								autowidth : true,// 自动匹配宽度
								rowNum : 10,// 默认每页显示
								rowList : [ 5, 10, 15, 20, 25, 30 ],// 自定义每页显示下拉选
								pager : pager_selector,// 表格数据关联的分页条，html元素
								altRows : true,// 分页选项，可以下拉选择每页显示记录数
								rownumbers : true,
								// 显示序号
								/*
								 * multiselect : true,// 生成多选列 multiboxonly :
								 * true,// 全选
								 */

								/** 培训开始时间,*培训结束时间,*培训等级,*培训性质,*学时,*培训证号,*培训机构名称,*培训内容,证书扫描件 */
								colModel : [
										{
											label : '培训开始时间',
											name : "begintime",
											index : "begintime",
											editable : false,
											sortable : false,
											width : 10,
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
											label : '培训结束时间',
											name : "endtime",
											index : "endtime",
											editable : false,
											sortable : false,
											width : 10,
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
											label : '培训等级',
											name : "traininglevel",
											index : "traininglevel",
											editable : false,
											sortable : false,
											width : 10,
											formatter : function(cellvalue,
													options, rowObject) {
												if (cellvalue != null
														&& cellvalue != undefined) {
													if (cellvalue == 1) {
														return "初级";
													} else if (cellvalue == 2) {
														return "中级";
													} else if (cellvalue == 3) {
														return "高级";
													}
													return "";
												}
												return "";
											}
										},
										{
											label : '培训性质',
											name : "trainingtype",
											index : "trainingtype",
											editable : false,
											sortable : false,
											width : 10,
											formatter : function(cellvalue,
													options, rowObject) {
												if (cellvalue != null
														&& cellvalue != undefined) {
													if (cellvalue == 1) {
														return "初训";
													} else if (cellvalue == 2) {
														return "复训";
													}
													return "";
												}
												return "";
											}
										},
										{
											label : '学时',
											name : "classhour",
											index : "classhour",
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
											label : "培训证号",
											name : 'certificateno',
											index : 'certificateno',
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
											label : "培训机构名称",
											name : 'trainname',
											index : 'trainname',
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
											label : "培训内容",
											name : 'trainingcontent',
											index : 'trainingcontent',
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
											label : "证书扫描件",
											name : 'ossid',
											index : 'ossid',
											editable : false,
											sortable : false,
											width : 10,
											formatter : function(cellvalue,
													options, rowObject) {
												if (cellvalue != null
														&& cellvalue != ""
														&& cellvalue != undefined) {
													var link = '<a dataid="'
															+ cellvalue
															+ '" target="_blank" href="'
															+ getfileUrl(cellvalue)
															+ '">'
															+ getfileName(cellvalue)
															+ '</a>';
													return link;
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
												var info = '<a href="javascript:trainIngedit('
														+ rowObject.id
														+ ');" class="ui-pg-div" title="编辑"><i class="ui-icon icon-pencil green"></i></a>';
												info += ' <a href="javascript:trainDel('
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
// 证书查看
function cerview(staffid) {
	var myDialog = dialog({
		title : "查看",
		okValue : '确定',
		width : 900,
		ok : function() {

		},
		cancelValue : '取消',
		cancel : function() {
		}
	});
	$.ajax({
		url : home_url + '/Certificate/viewCerPage?fileType=' + fileType
				+ '&staffid=' + staffid,
		success : function(data) {
			myDialog.content(data);
		}
	});
	myDialog.showModal();
}
// 证书编辑
function ceredit(staffid) {
	var url = home_url + '/Certificate/editCerPage?fileType=' + fileType
			+ '&staffid=' + staffid;
	var title = "编辑";
	if (staffid == null) {
		title = "新增";
		url = home_url + '/Certificate/editCerPage?fileType=' + fileType;
	}
	var myDialog = dialog({
		title : title,
		okValue : '确定',
		width : 900,
		ok : function() {
			if (!$("#CerSafeTb").valid()) {
				return false;
			}
			
			var re_records = $("#trainingSafetb").jqGrid('getGridParam', 'records'); //获取数据总条数  
			if(re_records==0){  
			    alert("请先添加数据！"); 
			    return false;
			}
			
			saveCer();
		},
		cancelValue : '取消',
		cancel : function() {
			certificateSafe.jqGridRedraw();
		}
	});
	$.ajax({
		url : url,
		success : function(data) {
			myDialog.content(data);
		}
	});
	myDialog.showModal();
}
// 培训编辑
function trainIngedit(id) {
	if (!$("#CerSafeTb").valid()) {
		return;
	}
	// staffid从editcertificateSafe.jsp页面来的
	var url = home_url + '/Certificate/editTrainPage?fileType=' + fileType
			+ '&staffid=' + staffid + '&id=' + id;
	var title = "编辑";
	if (id == null) {
		title = "新增";
		url = home_url + '/Certificate/editTrainPage?fileType=' + fileType
				+ '&staffid=' + staffid;
	}
	var myDialog = dialog({
		title : title,
		okValue : '确定',
		width : 500,
		ok : function() {
			if($("#_fileshow li").val()==undefined){
				$("#reportfiles").val(" ");//文件id  保存
			}
			
			if (!$("#trainingSafeTb").valid()) {
				return false;
			}
			saveTraning();
		},
		cancelValue : '取消',
		cancel : function() {
		}
	});
	$.ajax({
		url : url,
		cache : false,
		success : function(data) {
			myDialog.content(data);
		}
	});
	myDialog.showModal();
}
// 保存证书
function saveCer() {
	var newstaffid = staffid;
	// 如果是新增则oldstaffid为空 编辑如果没有换人员 都不进行更新替换
	if (oldstaffid != "" && newstaffid != oldstaffid) {
		$.ajax({
			url : home_url + "/Certificate/saveCer",
			type : "POST",
			data : {
				newstaffid : newstaffid,
				oldstaffid : oldstaffid,
				fileType : fileType
			},
			dataType : "json",
			success : function(data) {
				$.jGrowl(data.message);
			}
		});
	}
	certificateSafe.jqGridRedraw();
}
// 保存培训
function saveTraning() {
	$.ajax({
		url : home_url + "/Certificate/saveTraing",
		type : "POST",
		data : $("#trainingSafeTb").serialize(),
		dataType : "json",
		async : false,
		success : function(data) {
			$.jGrowl(data.message);
			if (data.code > 0) {
				trainingSafe.jqGridRedraw();
			}

		}
	});
}
// 删除证书
function cerDel(id) {
	delConfirmDiag(function() {
		$.post(home_url + "/Certificate/delCer", {
			staffid : id,
			fileType : fileType
		}, function(data) {
			alert(data.message);
			if (data.code > 0) {
				certificateSafe.jqGridRedraw();
			}
		});
	});
}

// 删除培训
function trainDel(id) {
	$.post(home_url + "/Certificate/delTrain", {
		id : id
	}, function(data) {
		alert(data.message);
		if (data.code > 0) {
			trainingSafe.jqGridRedraw();
		}
	});
}
// 批量新增复训
function batchInsert() {
	var myDialog = dialog({
		title : "批量新增复训",
		okValue : '确定',
		width : 900,
		ok : function() {
			if (!$("#batchInsertFormId").valid()) {
				return false;
			}
			// 人员id列表
			var staffids = $('#batchInsertTb').jqGrid('getGridParam',
					'selarrrow');

			if (staffids != null && staffids.length == 0) {
				alert("请选择一个人员！");
				return false;
			}
			$("#staffids").val(staffids);

			$.ajax({
				url : home_url + "/Certificate/batchInsertSave",
				type : "POST",
				data : $("#batchInsertFormId").serialize(),
				dataType : "json",
				success : function(data) {
					$.jGrowl(data.message);
					certificateSafe.jqGridRedraw();
				}
			});

		},
		cancelValue : '取消',
		cancel : function() {
		}
	});
	$.ajax({
		url : home_url + "/Certificate/batchInsert?fileType=" + fileType,
		success : function(data) {
			myDialog.content(data);
		}
	});
	myDialog.showModal();
}
