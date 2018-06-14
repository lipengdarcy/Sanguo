var certificateDevice = (function() {
	return {
		init : function() {
			certificateDevice.initjqGrid();
		},
		jqGridRedraw : function(data) {
			$("#keywords").val("");
			$("#certificateDevicetb").jqGrid('setGridParam', {

			}).trigger("reloadGrid");// 重新载入
		},
		search : function(data) {
			$("#certificateDevicetb").jqGrid('setGridParam', {
				postData : {
					realname : $("#keywords").val()
				}
			}).trigger("reloadGrid");// 重新载入
		},
		initjqGrid : function() {
			// table 和 页脚的id
			var grid_selector = "#certificateDevicetb";
			var pager_selector = "#grid-pager";
			jQuery(grid_selector)
					.jqGrid(
							{
								url : home_url
										+ '/Certificate/queryDeviceCertificateListData?fileType='
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
														&& rowObject.basic != null
														&& rowObject.basic.realname != null)
													return rowObject.basic.realname;
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
														&& rowObject.basic != null
														&& rowObject.basic.idcard != null)
													return rowObject.basic.idcard;
												return "";
											}
										},
										{
											label : "设备上岗操作证类别",
											name : 'fileType',
											index : 'fileType',
											editable : false,
											sortable : false,
											width : 10,
											formatter : function(cellvalue,
													options, rowObject) {
												if (rowObject != null
														&& rowObject.certificatetrainingList != null) {
													var info="";
													var list=rowObject.certificatetrainingList;
													for(var i=0;i<list.length;i++){
														for(var j=0;j<deviceTypeList.length;j++){
															if (list[i].certificatetype == deviceTypeList[j].paramcode) {
																info+=deviceTypeList[j].paramvalue;
																//防止最后一个设备上岗操作证类型名后面也有，
																if(i!=list.length-1){
																	info+=",";
																}
																continue ;
															}
														}
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
											width : 10,
											formatter : function(cellvalue,
													options, rowObject) {
												var info = '<a href="javascript:cerview('
														+ rowObject.id
														+ ');" class="ui-pg-div" data-rel="tooltip" data-original-title="查看" ><i class="ui-icon icon-eye-open blue"></i></a>';
												info += '<a href="javascript:ceredit('
														+ rowObject.id
														+ ');" class="ui-pg-div" title="编辑"><i class="ui-icon icon-pencil green"></i></a>';
												info += ' <a href="javascript:cerDel('
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

var cerDeviceEdit = (function() {
	return {
		init : function(staffid) {
			console.log(staffid);
			cerDeviceEdit.initjqGrid(staffid);
		},
		jqGridRedraw : function(data) {
			$("#cerDeviceEdittb").jqGrid('setGridParam', {
				postData : {
					fileType : fileType,
					id : staffid
				}
			}).trigger("reloadGrid");// 重新载入
		},
		initjqGrid : function(staffid) {
			console.log(staffid);
			// table 和 页脚的id
			var grid_selector = "#cerDeviceEdittb";
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

								/** ,*证书类别,*取证时间,*发证单位,*证书编号,证书扫描件,操作 */
								colModel : [
										{
											label : '证书类别',
											name : "certificatetype",
											index : "certificatetype",
											editable : false,
											sortable : false,
											width : 10,
											formatter : function(cellvalue,
													options, rowObject) {
												if (cellvalue != null
														&& cellvalue != undefined){
													for(var j=0;j<deviceTypeList.length;j++){
														if (cellvalue == deviceTypeList[j].paramcode) {
															return deviceTypeList[j].paramvalue;
														}
													}
												}
													
												return "";
											}
										},
										{
											label : '取证时间',
											name : "certificatetime",
											index : "certificatetime",
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
											label : '发证单位',
											name : "issueunit",
											index : "issueunit",
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
											label : '证书编号',
											name : "certificateno",
											index : "certificateno",
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
												var info = '<a href="javascript:CerSingleEdit('
														+ rowObject.id
														+ ');" class="ui-pg-div" title="编辑"><i class="ui-icon icon-pencil green"></i></a>';
												info += ' <a href="javascript:CerSingleDel('
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
// 人员所有证书编辑
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
			if (!$("#CerDeviceTb").valid()) {
				return false;
			}
			var re_records = $("#cerDeviceEdittb").jqGrid('getGridParam', 'records'); //获取数据总条数  
			if(re_records==0){  
			    alert("请先添加数据！");  
			    return false;
			}
			
			saveCer();
		},
		cancelValue : '取消',
		cancel : function() {
			certificateDevice.jqGridRedraw();
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

//保存证书
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
			async: false,
			success : function(data) {
				$.jGrowl(data.message);
			}
		});
	}
	certificateDevice.jqGridRedraw();
}

//单个证书编辑
function CerSingleEdit(id) {
	if (!$("#CerDeviceTb").valid()) {
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
			if (!$("#editCerSingleDeviceTb").valid()) {
				return false;
			}
			saveCerSingle();
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

//保存单个证书
function saveCerSingle() {
	$.ajax({
		url : home_url + "/Certificate/saveTraing",
		type : "POST",
		data : $("#editCerSingleDeviceTb").serialize(),
		dataType : "json",
		async:false,
		success : function(data) {
			$.jGrowl(data.message);
			if(data.code>0){
				cerDeviceEdit.jqGridRedraw();
			}
			
		}
	});
}

//删除人员的证书
function cerDel(id) {
	delConfirmDiag(function() {
	$.post(home_url + "/Certificate/delCer", {
		staffid : id,
		fileType : fileType
	}, function(data) {
		alert(data.message);
		if (data.code > 0) {
			certificateDevice.jqGridRedraw();
		}
	});
	});
}

// 删除单个证书
function CerSingleDel(id) {
	$.post(home_url + "/Certificate/delTrain", {
		id : id
	}, function(data) {
		alert(data.message);
		if (data.code > 0) {
			cerDeviceEdit.jqGridRedraw();
		}
	});
}