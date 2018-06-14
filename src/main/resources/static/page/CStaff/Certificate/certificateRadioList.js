var certificateRadio = (function() {
	return {
		init : function() {
			certificateRadio.initjqGrid();
		},
		jqGridRedraw : function(data) {
			$("#keywords").val("");
			$("#certificateRadiotb").jqGrid('setGridParam', {
				
			}).trigger("reloadGrid");// 重新载入
		},
		search : function(data) {
			$("#certificateRadiotb").jqGrid('setGridParam', {
				postData : {
					realname : $("#keywords").val()
				}
			}).trigger("reloadGrid");// 重新载入
		},
		initjqGrid : function() {
			// table 和 页脚的id
			var grid_selector = "#certificateRadiotb";
			var pager_selector = "#grid-pager";
			jQuery(grid_selector).jqGrid({
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


var trainingRadio = (function() {
	return {
		init : function(staffid) {
			trainingRadio.initjqGrid(staffid);
		},
		jqGridRedraw : function(data) {
			$("#trainingRadiotb").jqGrid('setGridParam', {
				postData : {
					fileType : fileType,
					id : staffid
				}
			}).trigger("reloadGrid");// 重新载入
		},
		initjqGrid : function(staffid) {
			// table 和 页脚的id
			var grid_selector = "#trainingRadiotb";
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
								colModel : [
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
											label : '培训时间',
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
											label : "培训及考核机构",
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
											label : "考核结果",
											name : 'checkresult',
											index : 'checkresult',
											editable : false,
											sortable : false,
											width : 10,
											formatter : function(cellvalue,
													options, rowObject) {
												if (cellvalue != null
														&& cellvalue != undefined) {
													if (cellvalue == 1) {
														return "合格";
													} else if (cellvalue == 0) {
														return "不合格";
													}
													return "";
												}
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
//证书查看
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
		url :home_url + '/Certificate/viewCerPage?fileType=' + fileType+ '&staffid=' + staffid,
		success : function(data) {
			myDialog.content(data);
		}
	});
	myDialog.showModal();
}
//证书编辑
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
			if (!$("#CerRadioTb").valid()) {
				return false;
			}
			
			var re_records = $("#trainingRadiotb").jqGrid('getGridParam', 'records'); //获取数据总条数  
			if(re_records==0){  
			    alert("请先添加数据！");  
			    return false;
			}
			
			saveCer();
		},
		cancelValue : '取消',
		cancel : function() {
			certificateRadio.jqGridRedraw();
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
//培训编辑
function trainIngedit(id) {
	if (!$("#CerRadioTb").valid()) {
		return;
	}
	//staffid从editcertificateRadio.jsp页面来的
	var url = home_url + '/Certificate/editTrainPage?fileType=' + fileType+'&staffid='+staffid
			+ '&id=' + id;
	var title = "编辑";
	if (id == null) {
		title = "新增";
		url = home_url + '/Certificate/editTrainPage?fileType=' + fileType+'&staffid='+staffid;
	}

	var myDialog = dialog({
		title : title,
		okValue : '确定',
		width : 500,
		ok : function() {
			if($("#_fileshow li").val()==undefined){
				$("#reportfiles").val(" ");//文件id  保存
			}
			
			if (!$("#trainingRadioTb").valid()) {
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
		cache:false,
		success : function(data) {
			myDialog.content(data);
		}
	});
	myDialog.showModal();
}
//保存证书
function saveCer() {
	var newstaffid=staffid;
	//如果是新增则oldstaffid为空  编辑如果没有换人员  都不进行更新替换
	if(oldstaffid!=""&&newstaffid!=oldstaffid){
	$.ajax({
		url : home_url + "/Certificate/saveCer",
		type : "POST",
		data : {newstaffid:newstaffid,oldstaffid:oldstaffid,fileType:fileType},
		dataType : "json",
		success : function(data) {
			$.jGrowl(data.message);
		}
	});
	}
	certificateRadio.jqGridRedraw();
}
//保存培训
function saveTraning() {
	$.ajax({
		url : home_url + "/Certificate/saveTraing",
		type : "POST",
		data : $("#trainingRadioTb").serialize(),
		dataType : "json",
		success : function(data) {
			$.jGrowl(data.message);
			if(data.code>0){
				trainingRadio.jqGridRedraw();
			}
		}
	});
}
//删除证书
function cerDel(id) {
	delConfirmDiag(function() {
	$.post(home_url + "/Certificate/delCer", {
		staffid : id,
		fileType:fileType
	}, function(data) {
		alert(data.message);
		if (data.code > 0) {
			certificateRadio.jqGridRedraw();
		}
	});
	});
}

//删除培训
function trainDel(id) {
	$.post(home_url + "/Certificate/delTrain", {
		id : id
	}, function(data) {
		alert(data.message);
		if (data.code > 0) {
			trainingRadio.jqGridRedraw();
		}
	});
}
//批量新增复训
function batch_Insert(){
	var myDialog = dialog({
		title : "批量新增复训",
		okValue : '确定',
		width : 900,
		ok : function() {
			if (!$("#batchInsertFormId").valid()) {
				return false;
			}
			//人员id列表
			var staffids=$('#batchInsertTb').jqGrid('getGridParam','selarrrow');
			
			if(staffids!=null&&staffids.length==0){
				alert("请选择一个人员！");
				return false;
			}
			$.ajax({
				url : home_url + "/Certificate/batchInsertSave",
				type : "POST",
				data :$.param({'staffids[]':staffids})+'&'+ $("#batchInsertFormId").serialize(),
				dataType : "json",
				success : function(data) {
					$.jGrowl(data.message);
					certificateRadio.jqGridRedraw();
				}
			});
			
		},
		cancelValue : '取消',
		cancel : function() {
		}
	});
	$.ajax({
		url :home_url +  "/Certificate/batchInsert?fileType="+fileType,
		success : function(data) {
			myDialog.content(data);
		}
	});
	myDialog.showModal();
}
