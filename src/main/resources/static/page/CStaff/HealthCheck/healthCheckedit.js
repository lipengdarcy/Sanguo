var healthCheckData = (function() {
	return {
		init : function(type) {
			healthCheckData.initjqGrid(type);
			getcount(type);
		},
		jqGridRedraw : function(type) {
			var grid_selector = "#healthchecktb" + type;
			$(grid_selector).jqGrid('setGridParam', {
				checkType : type,
				checkId : $("[name='id']").val()
			}).trigger("reloadGrid");// 重新载入
			getcount(type);
		},
		search : function(type) {
			var grid_selector = "#healthchecktb" + type;
			var searchId="#search"+type
			$(grid_selector).jqGrid('setGridParam', {
				postData : {
					checkType : type,
					checkId : $("[name='id']").val(),
					paramName : $(searchId).val()
				}
			}).trigger("reloadGrid");// 重新载入
		},
		initjqGrid : function(type) {
			// table 和 页脚的id
			var grid_selector = "#healthchecktb" + type;
			var pager_selector = "#jqGridPager" + type;
			var CheckResultTypeList = getCheckResultType(type);
			jQuery(grid_selector)
					.jqGrid(
							{
								url : home_url
										+ '/healthCheck/queryCheckDataList',
								datatype : "json",
								mtype : "post",
								postData : {
									checkType : type,
									checkId : $("[name='id']").val()
								},
								height : 'auto',
								viewrecords : true,// 显示总记录数
								autowidth : true,// 自动匹配宽度
								rowNum : 5,// 默认每页显示
								rowList : [ 5, 10, 15, 20, 25, 30 ],// 自定义每页显示下拉选
								pager : pager_selector,// 表格数据关联的分页条，html元素
								altRows : true,// 分页选项，可以下拉选择每页显示记录数
								rownumbers : true,// 显示序号
								colModel : [
										{
											label : "姓名",
											name : "name",
											index : "name",
											editable : false,
											sortable : false,
											width : 40,
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
											label : "年龄",
											name : "age",
											index : "age",
											editable : false,
											sortable : false,
											width : 20,
											formatter : function(cellvalue,
													options, rowObject) {
												if (rowObject != null
														&& rowObject.staff != null
														&& rowObject.staff.basic != null
														&& rowObject.staff.basic.age != null)
													return rowObject.staff.basic.age;
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
														&& rowObject.staff.basic.gender != null){
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
											label : "部门/科室 ",
											name : "orgName",
											index : "orgName",
											editable : false,
											sortable : false,
											width : 40,
											formatter : function(cellvalue,
													options, rowObject) {
												if (rowObject != null
														&& rowObject.staff != null
														&& rowObject.staff.organization != null
														&& rowObject.staff.organization.fullname != null)
													return rowObject.staff.organization.fullname;
												return "";
											}
										},
										{
											label : "岗位/工种 ",
											name : "workName",
											index : "workName",
											editable : false,
											sortable : false,
											width : 40,
											formatter : function(cellvalue,
													options, rowObject) {
												if (rowObject != null
														&& rowObject.staff != null
														&& rowObject.staff.staffworkrole != null
														&& rowObject.staff.staffworkrole.rname != null)
													return rowObject.staff.staffworkrole.rname;
												return "";
											}
										},
										{
											label : "体检结论 ",
											name : "checkresultName",
											index : "checkresultName",
											editable : false,
											sortable : false,
											width : 80,
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
											label : "放射工作适应性意见 ",
											name : "adaptabilitySuggestName",
											index : "adaptabilitySuggestName",
											editable : false,
											sortable : false,
											width : 80,
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
											label : "异常指标 ",
											name : "anomalyindex",
											index : "anomalyindex",
											editable : false,
											sortable : false,
											width : 80,
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
											label : "医学建议",
											name : "medicaladvice",
											index : "medicaladvice",
											editable : false,
											sortable : false,
											width : 80,
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
											label : "操作",
											name : null,
											index : null,
											editable : false,
											sortable : false,
											width : 40,
											formatter : function(cellvalue,
													options, rowObject) {
												
												var  info=' <a href="javascript:healthCheckEdit('
													+ rowObject.id
													+ ','
													+rowObject.staff.id
													+ ','
													+ type
													+ ');" class="ui-pg-div" data-rel="tooltip" data-original-title="编辑"><i class="ui-icon icon-pencil green"></i></a>';
												info += ' <a href="javascript:healthCheckDel('
														+ rowObject.id
														+ ','
														+ type
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

var healthCheckDataView = (function() {
	return {
		init : function(type) {
			healthCheckDataView.initjqGrid(type);
			getcount(type);
		},
		jqGridRedraw : function(type) {
			var grid_selector = "#healthchecktb" + type;
			$(grid_selector).jqGrid('setGridParam', {
				checkType : type,
				checkId : $("[name='id']").val()
			}).trigger("reloadGrid");// 重新载入
			getcount(type);
		},
		search : function(type) {
			var grid_selector = "#healthchecktb" + type;
			var searchId="#search"+type
			$(grid_selector).jqGrid('setGridParam', {
				postData : {
					checkType : type,
					checkId : $("[name='id']").val(),
					paramName : $(searchId).val()
				}
			}).trigger("reloadGrid");// 重新载入
		},
		initjqGrid : function(type) {
			// table 和 页脚的id
			var grid_selector = "#healthchecktb" + type;
			var pager_selector = "#jqGridPager" + type;
			var CheckResultTypeList = getCheckResultType(type);
			jQuery(grid_selector)
					.jqGrid(
							{
								url : home_url
										+ '/healthCheck/queryCheckDataList',
								datatype : "json",
								mtype : "post",
								postData : {
									checkType : type,
									checkId : $("[name='id']").val()
								},
								height : 'auto',
								viewrecords : true,// 显示总记录数
								autowidth : true,// 自动匹配宽度
								rowNum : 5,// 默认每页显示
								rowList : [ 5, 10, 15, 20, 25, 30 ],// 自定义每页显示下拉选
								pager : pager_selector,// 表格数据关联的分页条，html元素
								altRows : true,// 分页选项，可以下拉选择每页显示记录数
								rownumbers : true,// 显示序号
								colModel : [
											{
												label : "姓名",
												name : "name",
												index : "name",
												editable : false,
												sortable : false,
												width : 40,
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
												label : "年龄",
												name : "age",
												index : "age",
												editable : false,
												sortable : false,
												width : 20,
												formatter : function(cellvalue,
														options, rowObject) {
													if (rowObject != null
															&& rowObject.staff != null
															&& rowObject.staff.basic != null
															&& rowObject.staff.basic.age != null)
														return rowObject.staff.basic.age;
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
															&& rowObject.staff.basic.gender != null){
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
												label : "部门/科室 ",
												name : "orgName",
												index : "orgName",
												editable : false,
												sortable : false,
												width : 40,
												formatter : function(cellvalue,
														options, rowObject) {
													if (rowObject != null
															&& rowObject.staff != null
															&& rowObject.staff.organization != null
															&& rowObject.staff.organization.fullname != null)
														return rowObject.staff.organization.fullname;
													return "";
												}
											},
											{
												label : "岗位/工种 ",
												name : "workName",
												index : "workName",
												editable : false,
												sortable : false,
												width : 40,
												formatter : function(cellvalue,
														options, rowObject) {
													if (rowObject != null
															&& rowObject.staff != null
															&& rowObject.staff.staffworkrole != null
															&& rowObject.staff.staffworkrole.rname != null)
														return rowObject.staff.staffworkrole.rname;
													return "";
												}
											},
											{
												label : "体检结论 ",
												name : "checkresultName",
												index : "checkresultName",
												editable : false,
												sortable : false,
												width : 80,
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
												label : "放射工作适应性意见 ",
												name : "adaptabilitySuggestName",
												index : "adaptabilitySuggestName",
												editable : false,
												sortable : false,
												width : 80,
												formatter : function(cellvalue,
														options, rowObject) {
													console.log(rowObject);
													if (cellvalue != null
															&& cellvalue != undefined) {
														return cellvalue;
													}
													return "";
												}
											},
											{
												label : "异常指标 ",
												name : "anomalyindex",
												index : "anomalyindex",
												editable : false,
												sortable : false,
												width : 80,
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
												label : "医学建议",
												name : "medicaladvice",
												index : "medicaladvice",
												editable : false,
												sortable : false,
												width : 80,
												formatter : function(cellvalue,
														options, rowObject) {
													if (cellvalue != null
															&& cellvalue != undefined) {
														return cellvalue;
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
})();

var staffCheckView = (function() {
	return {
		init : function(staffid) {
			staffCheckView.initjqGrid(staffid);
		},
		jqGridRedraw : function(staffid) {
			var grid_selector = "#healthchecktb" + type;
			$(grid_selector).jqGrid('setGridParam', {
				staffid:staffid
			}).trigger("reloadGrid");// 重新载入
		},
		initjqGrid : function(staffid) {
			// table 和 页脚的id
			var grid_selector = "#staffChecktb";
			var pager_selector = "#jqGridPager" ;
			jQuery(grid_selector)
					.jqGrid(
							{
								url : home_url
										+ '/healthCheck/queryCheckDataListByStaffid',
								datatype : "json",
								mtype : "post",
								postData : {
									staffid:staffid
								},
								height : 240,
								viewrecords : true,// 显示总记录数
								autowidth : true,// 自动匹配宽度
								rowNum : 10,// 默认每页显示
								rowList : [ 5, 10, 15, 20, 25, 30 ],// 自定义每页显示下拉选
								pager : pager_selector,// 表格数据关联的分页条，html元素
								altRows : true,// 分页选项，可以下拉选择每页显示记录数
								rownumbers : true,// 显示序号
								colModel : [
										{
											label : "检查时间",
											name : "checkTime",
											index : "checkTime",
											editable : false,
											sortable : false,
											width : 10,
											formatter : function(cellvalue,
													options, rowObject) {
												if (cellvalue != null && cellvalue != undefined) {
													return cellvalue;
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
											width : 10,
											formatter : function(cellvalue,
													options, rowObject) {
												if (cellvalue != null && cellvalue != undefined) {
													for (var k = 0; k < HealthCheckTypeList.length; k++) {
														if (cellvalue == HealthCheckTypeList[k].value) {
															return HealthCheckTypeList[k].name;
														}
													}
													return "";
												}
												return "";
											}
										},
										{
											label : "职业健康检查机构",
											name : "othercompany",
											index : "othercompany",
											editable : false,
											sortable : false,
											width : 10,
											formatter : function(cellvalue,
													options, rowObject) {
												if (rowObject != null
														&& rowObject.check != null
														&& rowObject.check.othercompany != null
														&& rowObject.check.othercompany.companyname != null)
													return rowObject.check.othercompany.companyname;
												return "";
												return "";
											}
										},
										{
											label : "检查结论 ",
											name : "checkresult",
											index : "checkresult",
											editable : false,
											sortable : false,
											width : 10,
											formatter : function(cellvalue,
													options, rowObject) {
												if (cellvalue != null
														&& cellvalue != undefined) {
													var CheckResultTypeList = getCheckResultType(rowObject.checktype);
													for (var k = 0; k < CheckResultTypeList.length; k++) {
														if (cellvalue == CheckResultTypeList[k].value) {
															return CheckResultTypeList[k].name;
														}
													}
													return "";
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
})();

jQuery(function($) {



	// 日历
	$('input[name=date-range-picker]').daterangepicker().prev().on(
			ace.click_event, function() {
				$(this).next().focus();
			});
	$(".applyBtn").html("应用");
	$(".cancelBtn").html("取消");

	// 验证必填项 自定义
	$("#editFrom").validate({
		ignore : []
	});

});
/**
 * @param id
 *            传递id可以根据id删除
 * @param type
 *            根据type判断刷新哪个页面
 */
function healthCheckDel(id, type) {
	$.ajax({
		type : "post",
		url : home_url + "/healthCheck/delCheckData",
		data : {
			id : id,
		},
		dataType : "json",
		success : function(data) {
			if (data.code == 1) {
				healthCheckData.jqGridRedraw(type);
			}
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			$.jGrowl("异常！请重新尝试或者联系管理员!", {
				header : "error"
			});
		}
	});
}


/**
 * @param id
 *            传递id可以根据id查找页面
  @param staffid
 *            根据staffid查找人员信息
 * @param type
 *            根据type判断刷新哪个页面
 */
function healthCheckEdit(id,staffid, type) {
	
	var myDialog = dialog({
		title : '编辑检查结果',
		okValue : '确定',
		width : 710,
		height : 'auto',
		ok : function(data) {
			$.ajax({
				type : "post",
				url : home_url + "/healthCheck/saveCheckData",
				data : $("#checkDataEditTb").serialize(),
				dataType : "json",
				success : function(data) {
					if (data.code == 1) {
						healthCheckData.jqGridRedraw(type);
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
		url : home_url + '/healthCheck/healthCheckDataedit',
		data : {
			id : id,
			staffId:staffid
		},
		success : function(data) {
			myDialog.content(data);
		}
	});
	myDialog.showModal();
	
}

/**
 * @param id
 *            传递id可以根据id更新
 * @param that
 *            体检结论改变的值
 * @param type
 *            用来刷新type的汇总页面
 */
function changeCheckResult(id, that, type) {
	$.ajax({
		type : "post",
		url : home_url + "/healthCheck/saveCheckData",
		data : {
			id : id,
			checkresult : $(that).val(),
		},
		dataType : "json",
		success : function(data) {
			if (data.code == 1) {
				healthCheckData.jqGridRedraw(type);
			}
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			$.jGrowl("异常！请重新尝试或者联系管理员!", {
				header : "error"
			});
		}
	});
}
// 保存健康监护信息
function saveHealthCheck() {
	var time = $("#id-date-range-picker-1").val();
	var arr = time.split(' 至 ');
	$("[name='begintime']").val(arr[0]);
	$("[name='endtime']").val(arr[1]);
	var tempStr = "";
	var length = $("input[name='selecttype']:checked").length;
	var i = 0;
	$('input[name="selecttype"]:checked').each(function() {
		i++;
		tempStr += $(this).val();
		// 防止最后还是有，
		if (i != length) {
			tempStr += ",";
		}
	});
	$("[name='type']").val(tempStr);
	if (!$("#editFrom").valid()) {
		return ;
	}
	$.ajax({
		url : home_url + "/healthCheck/saveHealthCheck",
		type : "POST",
		data : $("#editFrom").serialize(),
		success : function(data) {
			if($("input[name='id']").val()==undefined){
				gridAddCallback("healthChecktb");
			}
			alert(data.message);
			if (data.code == 1) {
				 window.location.replace(home_url + "/healthCheck/view?id=" + data.data);
			}
		}
	})

}

// 第三方企业单选
function getCompany() {
	Company_Single_edit(function(data) {
		var str = "";
		str = '<li value="' + data.id + '" name="' + data.companyName
				+ '" class="warning">' + '<span>' + data.companyName
				+ '</span>'
				+ '<a href="javascript:;" onclick="$(this).li_remove();">'
				+ ' <i class="icon-remove white"></i>' + '</a>' + '</li>';
		$("#otherCampanyulid").html("");// 清空数据
		$("#otherCampanyulid").append(str);

		$("[name='othercompanyid']").val(data.id);// 第三方企业
		// 第三方删除
		$("#otherCampanyulid").click(function(e) {
			// a => li
			if(e.target.nodeName != "I"){//点击i的时候触发
				return;
			}
			
			$(this).parent("li").remove();
			$("[name='othercompanyid]").val("");
			$("#managertelId").val("");
			$("#managernameId").val("");
		});

		$("#managertelId").val(data.managertel);
		$("#managernameId").val(data.managername);
	}, 2);// 1.个人剂量监测单位；2.职业健康检查单位；3.评价与检测机构； 4.供货商；5.生产厂家；6.设计单位；7.施工单位；
}

var tempEditFrom=$("#editFrom").serialize();
// 检查汇总新增
function selectStaff(type) {
	//在新增检查结果汇总之前必须保存基础信息  不然会出现职业健康检查种类未保存可是已经新增了该类型数据的bug 这个判断由于1.2 新增是放在查看页面的  已经不用管了
	if(tempEditFrom!=$("#editFrom").serialize()){
		 var myDialog = dialog({
				title:"提示",
				lock : true,
				okValue : '保存',
				content:'<i class="jian-alertico jian-alert-xs jian-ico-alert"></i>基本信息已经被改动 请保存信息！',
				ok : function() {
					var time = $("#id-date-range-picker-1").val();
					var arr = time.split(' 至 ');
					$("[name='begintime']").val(arr[0]);
					$("[name='endtime']").val(arr[1]);
					var tempStr = "";
					var length = $("input[name='selecttype']:checked").length;
					var i = 0;
					$('input[name="selecttype"]:checked').each(function() {
						i++;
						tempStr += $(this).val();
						// 防止最后还是有，
						if (i != length) {
							tempStr += ",";
						}
					});
					$("[name='type']").val(tempStr);
					if (!$("#editFrom").valid()) {
						return false;
					}
					$.ajax({
						url : home_url + "/healthCheck/saveHealthCheck",
						type : "POST",
						data : $("#editFrom").serialize(),
						success : function(data) {
							alert(data.message);
							tempEditFrom=$("#editFrom").serialize();
							saveCheckStaff(type);
						}
					})
				},
				cancelValue : '取消',
				cancel : function() {
					
				}
			});
			myDialog.showModal();
	}else{
		saveCheckStaff(type);
	}
	
}
//选择人员并保存检查汇总
function saveCheckStaff(type){
	var checkid = $("[name='id']").val();
	var time = $("#id-date-range-picker-1").val();
	var arr = time.split(' 至 ');
	var begintime=arr[0];
	var endtime=arr[1];
	showDialogModal("人员多选", home_url
			+ "/tool/SelectStaff_MultiforCheckHealth?type=" + type
			+ "&checkId=" + checkid, function(data) {
		// 选好之后直接保存数据
		var ids =  new Array();
		if($("#_selectedList").find("li").length>0){
			$('#_selectedList li').each(function() {
			ids.push($(this).val());//id添加到数组
			});
		}else{
			alert("请选择一个人员！");
			return false;
		}
		$.ajax({
			type : "post",
			url : home_url + "/healthCheck/saveCheckData",
			data : {
				checktype : type,
				checkid : checkid,
				staffids : ids,
				begintime:begintime,
				endtime:endtime
			},
			dataType : "json",
			success : function(data) {
				if (data.code == 1) {
					healthCheckData.jqGridRedraw(type);
				}
			},
			error : function(XMLHttpRequest, textStatus, errorThrown) {
				$.jGrowl("异常！请重新尝试或者联系管理员!", {
					header : "error"
				});
			}
		});
	}, 710, 'auto');
}

/**
 * @param type
 *            根据type选择要的枚举体检结论类型
 * @returns {String}
 */
function getCheckResultType(type) {
	var CheckResultType = "";
	if (type == 1) {
		CheckResultType = CheckResultType1;
	}
	if (type == 2) {
		CheckResultType = CheckResultType2;
	}
	if (type == 3) {
		CheckResultType = CheckResultType3;
	}
	if (type == 4) {
		CheckResultType = CheckResultType4;
	}
	if (type == 5) {
		CheckResultType = CheckResultType5;
	}
	return CheckResultType;
}

function getcount(type){
	$.ajax({
		type : "post",
		url : home_url + "/healthCheck/getDataCount",
		data : {
			checkType : type,
			checkId : $("[name='id']").val(),
		},
		dataType : "json",
		success : function(data) {
			if (data.code == 1) {
				var staffCountId="#staffCount"+type;
				$(staffCountId).html(data.data);
			}
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			$.jGrowl("异常！请重新尝试或者联系管理员!", {
				header : "error"
			});
		}
	});
	
}