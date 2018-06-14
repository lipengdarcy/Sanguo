var certificatePractice = (function() {
	return {
		init : function() {
			certificatePractice.initjqGrid();
		},
		jqGridRedraw : function(data) {
			$("#keywords").val("");
			$("#certificatePracticetb").jqGrid('setGridParam', {

			}).trigger("reloadGrid");// 重新载入
		},
		search : function(data) {
			$("#certificatePracticetb").jqGrid('setGridParam', {
				postData : {
					realname : $("#keywords").val()
				}
			}).trigger("reloadGrid");// 重新载入
		},
		initjqGrid : function() {
			// table 和 页脚的id
			var grid_selector = "#certificatePracticetb";
			var pager_selector = "#grid-pager";
			jQuery(grid_selector)
					.jqGrid(
							{
								url : home_url
										+ '/Certificate/queryCerListData?fileType='
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
								/* 序号,姓名,证件号码,部门/科室,职称等级,职称,证书扫描件 */
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
											label : "部门/科室",
											name : 'orgName',
											index : 'orgName',
											editable : false,
											sortable : false,
											width : 10,
											formatter : function(cellvalue,
													options, rowObject) {
												if (rowObject != null
														&& rowObject.organization != null
														&& rowObject.organization.fullname != null)
													return rowObject.organization.fullname;
												return "";
											}
										},
										{
											label : "执业范围",
											name : 'practicescope',
											index : 'practicescope',
											editable : false,
											sortable : false,
											width : 10,
											formatter : function(cellvalue,
													options, rowObject) {
												if (rowObject != null
														&& rowObject.certificatetraining != null
														&& rowObject.certificatetraining.practicescope != null) {
													for (var j = 0; j < practisingScopeList.length; j++) {
														if (rowObject.certificatetraining.practicescope == practisingScopeList[j].paramcode) {
															return practisingScopeList[j].paramvalue;
														}
													}
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
												if (rowObject != null
														&& rowObject.certificatetraining != null
														&& rowObject.certificatetraining.ossid != null) {
													var link = '<a dataid="'
															+ rowObject.certificatetraining.ossid
															+ '" target="_blank" href="'
															+ getfileUrl(rowObject.certificatetraining.ossid)
															+ '">'
															+ getfileName(rowObject.certificatetraining.ossid)
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
												var info = '<a href="javascript:ceredit('
														+ rowObject.id
														+ ',\'CerPracticeTb\');" class="ui-pg-div" title="编辑"><i class="ui-icon icon-pencil green"></i></a>';
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

var certificateJobTitle = (function() {
	return {
		init : function() {
			certificateJobTitle.initjqGrid();
		},
		jqGridRedraw : function(data) {
			$("#keywords").val("");
			$("#certificateJobTitletb").jqGrid('setGridParam', {

			}).trigger("reloadGrid");// 重新载入
		},
		search : function(data) {
			$("#certificateJobTitletb").jqGrid('setGridParam', {
				postData : {
					realname : $("#keywords").val()
				}
			}).trigger("reloadGrid");// 重新载入
		},
		initjqGrid : function() {
			// table 和 页脚的id
			var grid_selector = "#certificateJobTitletb";
			var pager_selector = "#grid-pager";
			jQuery(grid_selector)
					.jqGrid(
							{
								url : home_url
										+ '/Certificate/queryCerListData?fileType='
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
											label : "部门/科室",
											name : 'orgName',
											index : 'orgName',
											editable : false,
											sortable : false,
											width : 10,
											formatter : function(cellvalue,
													options, rowObject) {
												if (rowObject != null
														&& rowObject.organization != null
														&& rowObject.organization.fullname != null)
													return rowObject.organization.fullname;
												return "";
											}
										},

										{
											label : "职称等级",
											name : 'jobtype',
											index : 'jobtype',
											editable : false,
											sortable : false,
											width : 10,
											formatter : function(cellvalue,
													options, rowObject) {
												if (cellvalue != null
														&& cellvalue != undefined) {

													for (var j = 0; j < jobLevelList.length; j++) {
														if (cellvalue == jobLevelList[j].value) {
															return jobLevelList[j].name;
														}

													}
													return "";
												}
												return "";
											}
										},

										{
											label : "职称",
											name : 'jobName',
											index : 'jobName',
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
												if (rowObject != null
														&& rowObject.certificatetraining != null
														&& rowObject.certificatetraining.ossid != null) {
													var link = '<a dataid="'
															+ rowObject.certificatetraining.ossid
															+ '" target="_blank" href="'
															+ getfileUrl(rowObject.certificatetraining.ossid)
															+ '">'
															+ getfileName(rowObject.certificatetraining.ossid)
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
												var info = '<a href="javascript:ceredit('
														+ rowObject.id
														+ ',\'cerJobTitletb\');" class="ui-pg-div" title="编辑"><i class="ui-icon icon-pencil green"></i></a>';
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




var certificateEducation = (function() {
	return {
		init : function() {
			certificateEducation.initjqGrid();
		},
		jqGridRedraw : function(data) {
			$("#keywords").val("");
			$("#certificateEducationtb").jqGrid('setGridParam', {

			}).trigger("reloadGrid");// 重新载入
		},
		search : function(data) {
			$("#certificateEducationtb").jqGrid('setGridParam', {
				postData : {
					realname : $("#keywords").val()
				}
			}).trigger("reloadGrid");// 重新载入
		},
		initjqGrid : function() {
			// table 和 页脚的id
			var grid_selector = "#certificateEducationtb";
			var pager_selector = "#grid-pager";
			jQuery(grid_selector)
					.jqGrid(
							{
								url : home_url
										+ '/Certificate/queryCerListData?fileType='
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
											label : "部门/科室",
											name : 'orgName',
											index : 'orgName',
											editable : false,
											sortable : false,
											width : 10,
											formatter : function(cellvalue,
													options, rowObject) {
												if (rowObject != null
														&& rowObject.organization != null
														&& rowObject.organization.fullname != null)
													return rowObject.organization.fullname;
												return "";
											}
										},

										{
											label : "学历",
											name : 'education',
											index : 'education',
											editable : false,
											sortable : false,
											width : 10,
											formatter : function(cellvalue,
													options, rowObject) {
												if (rowObject != null
														&& rowObject.basic != null
														&& rowObject.basic.education != null){
													for (var j = 0; j < educationList.length; j++) {
														if (rowObject.basic.education == educationList[j].paramcode) {
															return educationList[j].paramvalue;
														}
													}
												}
												return "";
											}
										},

										{
											label : "毕业院校",
											name : 'graduatedFrom',
											index : 'graduatedFrom',
											editable : false,
											sortable : false,
											width : 10,
											formatter : function(cellvalue,
													options, rowObject) {
												if (rowObject != null
														&& rowObject.basic != null
														&& rowObject.basic.graduatedFrom != null){
													return rowObject.basic.graduatedFrom;
												}
												return "";
											}
										},

										{
											label : "专业",
											name : 'major',
											index : 'major',
											editable : false,
											sortable : false,
											width : 10,
											formatter : function(cellvalue,
													options, rowObject) {
												if (rowObject != null
														&& rowObject.basic != null
														&& rowObject.basic.major != null){
													return rowObject.basic.major;
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
												if (rowObject != null
														&& rowObject.certificatetraining != null
														&& rowObject.certificatetraining.ossid != null) {
													var link = '<a dataid="'
															+ rowObject.certificatetraining.ossid
															+ '" target="_blank" href="'
															+ getfileUrl(rowObject.certificatetraining.ossid)
															+ '">'
															+ getfileName(rowObject.certificatetraining.ossid)
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
												var info = '<a href="javascript:ceredit('
														+ rowObject.id
														+ ',\'cerJobTitletb\');" class="ui-pg-div" title="编辑"><i class="ui-icon icon-pencil green"></i></a>';
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
// 证书编辑
/**
 * @param staffid
 *            人员id
 * @param idName
 *            id名称
 */
function ceredit(staffid, idName) {
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
			if (!$("#" + idName).valid()) {
				return false;
			}
			saveCer(idName);
		},
		cancelValue : '取消',
		cancel : function() {
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

// 保存证书
function saveCer(idName) {
	$.ajax({
		url : home_url + "/Certificate/saveTraing",
		type : "POST",
		data : $("#" + idName).serialize(),
		dataType : "json",
		success : function(data) {
			$.jGrowl(data.message);
		}
	});
	certificatePractice.jqGridRedraw();
	certificateJobTitle.jqGridRedraw();
	certificateEducation.jqGridRedraw();
}
