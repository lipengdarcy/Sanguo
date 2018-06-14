//用作对比表单是否改变
var tempform;
var monitorData = (function() {
	return {
		init : function() {
			monitorData.initjqGrid();
			tempform=$("#editFrom").serialize();
		},
		jqGridRedraw : function() {
			var grid_selector = "#monitortb";
			$(grid_selector).jqGrid('setGridParam', {
				postData : {monitorId: $("[name='id']").val()}
			}).trigger("reloadGrid");// 重新载入
		},
		search : function() {
			var grid_selector = "#monitortb";
			$(grid_selector).jqGrid('setGridParam', {
				postData : {parameterName:$("#keywords").val(),
				monitorId: $("[name='id']").val()}
			}).trigger("reloadGrid");// 重新载入
		},
		initjqGrid : function() {
			// table 和 页脚的id
			var grid_selector = "#monitortb" ;
			var pager_selector = "#jqGridPager";
			jQuery(grid_selector).jqGrid(
							{
								url : home_url+ '/monitor/queryMonitorDataList',
								datatype : "json",
								mtype : "post",
								postData : {
									monitorId: $("[name='id']").val()
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
											label : "工号",
											name : "workno",
											index : "workno",
											editable : false,
											sortable : false,
											width : 10,
											formatter : function(cellvalue,
													options, rowObject) {
												if (rowObject != null
														&& rowObject.staff != null
														&& rowObject.staff.workno != null)
													return rowObject.staff.workno;
												return "";
											}
										},
										{
											label : "部门/科室 ",
											name : "orgName",
											index : "orgName",
											editable : false,
											sortable : false,
											width : 10,
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
											width : 10,
											formatter : function(cellvalue,
													options, rowObject) {
												if (rowObject != null
														&& rowObject.staff.staffworkrole != null
														&& rowObject.staff.staffworkrole.rname != null)
													return rowObject.staff.staffworkrole.rname;
												return "";
											}
										},
										{
											label : "职业照射种类及其代码	 ",
											name : "radiationtype",
											index : "radiationtype",
											editable : false,
											sortable : false,
											width : 10,
											formatter : function(cellvalue,
													options, rowObject) {
												if (rowObject != null
														&& rowObject.staff.staffworkrole != null
														&& rowObject.staff.staffworkrole.radiationtype != null) {
													for (var i = 0; i < OccupationaSysparamList.length; i++) {
														if (rowObject.staff.staffworkrole.radiationtype == OccupationaSysparamList[i].paramcode) {
															return OccupationaSysparamList[i].paramvalue;
														}
													}
												}
												return "";
											}
										},
										{
											label : "本次监测结果（mSv）",
											name : "data",
											index : "data",
											editable : false,
											sortable : false,
											width : 10,
											formatter : function(cellvalue,
													options, rowObject) {
												/*	var info='<div class="input-group" style="height:30px; line-height:30px;text-align:center;"><input type="text" style=" width: 150px; height: 25px;" name="data" onBlur="saveMonitorData('
														+ rowObject.id
														+ ',this'
														+ ')"' ;
														if (cellvalue != null
																&& cellvalue != undefined){
															info+='value="'+cellvalue+'"';
														}
													info+='placeholder="填写数字"/></div>';
													return info;*/
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
												var info = ' <a href="javascript:monitorDataEdit('
													+ rowObject.id+');" class="ui-pg-div" data-rel="tooltip" data-original-title="编辑"><i class="ui-icon icon-pencil green"></i></a>';
												 info += ' <a href="javascript:monitorDataDel('
														+ rowObject.id+');" class="ui-pg-div" data-rel="tooltip" data-original-title="删除"><i class="ui-icon icon-trash red"></i></a>';
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

var monitorDataView = (function() {
	return {
		init : function() {
			monitorDataView.initjqGrid();
		},
		jqGridRedraw : function() {
			var grid_selector = "#monitortb";
			$(grid_selector).jqGrid('setGridParam', {
				postData : {monitorId: $("[name='id']").val()}
			}).trigger("reloadGrid");// 重新载入
		},
		search : function() {
			var grid_selector = "#monitortb";
			$(grid_selector).jqGrid('setGridParam', {
				postData : {parameterName:$("#keywords").val(),
				monitorId: $("[name='id']").val()}
			}).trigger("reloadGrid");// 重新载入
		},
		initjqGrid : function() {
			// table 和 页脚的id
			var grid_selector = "#monitortb" ;
			var pager_selector = "#jqGridPager";
			jQuery(grid_selector).jqGrid(
							{
								url : home_url+ '/monitor/queryMonitorDataList',
								datatype : "json",
								mtype : "post",
								postData : {
									monitorId: $("[name='id']").val()
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
											label : "工号",
											name : "workno",
											index : "workno",
											editable : false,
											sortable : false,
											width : 10,
											formatter : function(cellvalue,
													options, rowObject) {
												if (rowObject != null
														&& rowObject.staff != null
														&& rowObject.staff.workno != null)
													return rowObject.staff.workno;
												return "";
											}
										},
										{
											label : "部门/科室 ",
											name : "orgName",
											index : "orgName",
											editable : false,
											sortable : false,
											width : 10,
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
											width : 10,
											formatter : function(cellvalue,
													options, rowObject) {
												if (rowObject != null
														&& rowObject.staff.staffworkrole != null
														&& rowObject.staff.staffworkrole.rname != null)
													return rowObject.staff.staffworkrole.rname;
												return "";
											}
										},
										{
											label : "职业照射种类及其代码	 ",
											name : "radiationtype",
											index : "radiationtype",
											editable : false,
											sortable : false,
											width : 10,
											formatter : function(cellvalue,
													options, rowObject) {
												if (rowObject != null
														&& rowObject.staff.staffworkrole != null
														&& rowObject.staff.staffworkrole.radiationtype != null) {
													for (var i = 0; i < OccupationaSysparamList.length; i++) {
														if (rowObject.staff.staffworkrole.radiationtype == OccupationaSysparamList[i].paramcode) {
															return OccupationaSysparamList[i].paramvalue;
														}
													}
												}
												return "";
											}
										},
										{
											label : "本次监测结果（mSv）",
											name : "data",
											index : "data",
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

var staffMonitorView = (function() {
	return {
		init : function(staffid) {
			staffMonitorView.initjqGrid(staffid);
		},
		jqGridRedraw : function(staffid) {
			var grid_selector = "#staffMonitortb";
			$(grid_selector).jqGrid('setGridParam', {
				staffid:staffid
			}).trigger("reloadGrid");// 重新载入
		},
		initjqGrid : function(staffid) {
			// table 和 页脚的id
			var grid_selector = "#staffMonitortb";
			var pager_selector = "#jqGridPager" ;
			jQuery(grid_selector)
					.jqGrid(
							{
								url : home_url+ '/monitor/queryMonitorDataListByStaffid',
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
											label : "剂量计佩带起止日期",
											name : "wearingTime",
											index : "wearingTime",
											editable : false,
											sortable : false,
											width : 10,
											formatter : function(cellvalue,
													options, rowObject) {
												if (rowObject != null
														&& rowObject.dosemonitor != null){
													var info="";
													if(rowObject.dosemonitor.begintime!=null){
														info+=formatDate(new Date(rowObject.dosemonitor.begintime));
													}
													if(rowObject.dosemonitor.endtime!=null){
														info+="至"+formatDate(new Date(rowObject.dosemonitor.endtime));
													}
													return info;
												}
												return "";
											}
										},
										{
											label : "监测周期（月）",
											name : "cycle",
											index : "cycle",
											editable : false,
											sortable : false,
											width : 10,
											formatter : function(cellvalue,
													options, rowObject) {
												//30±15天的算一个月 60±15算两个月  四舍五入
												if (rowObject != null
														&& rowObject.dosemonitor != null){
													var time="";
													if(rowObject.dosemonitor.begintime!=null&&rowObject.dosemonitor.endtime!=null){
													    var dateFrom = new Date(rowObject.dosemonitor.begintime);
													    var dateTo = new Date(rowObject.dosemonitor.endtime);
													    //计算天数
													    var diff = dateTo.valueOf() - dateFrom.valueOf();
													    var diff_day = parseInt(diff/(1000*60*60*24));
													    //天数/30
													    time= Math.round(diff_day/30);
													}
													return time;
												}
												return "";
											}
										},
										{
											label : "监测机构 ",
											name : "othercompany",
											index : "othercompany",
											editable : false,
											sortable : false,
											width : 10,
											formatter : function(cellvalue,
													options, rowObject) {
												if (rowObject != null
														&& rowObject.dosemonitor != null
														&& rowObject.dosemonitor.othercompany != null
														&& rowObject.dosemonitor.othercompany.companyname != null)
													return rowObject.dosemonitor.othercompany.companyname;
												return "";
											}
										},
									
										{
											label : "监测结果（mSv） ",
											name : "data",
											index : "data",
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
										}],
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

// 第三方企业单选
function getCompany() {
	Company_Single_edit(function(data) {
		var str = "";
		str = '<li value="' + data.id + '" name="' + data.companyName
				+ '" class="warning">' + '<span>' + data.companyName
				+ '</span>'
				+ '<a href="javascript:;" class="fileremove delete"  onclick="$(this).li_remove();">'
				+ ' <i class="icon-remove white"></i>' + '</a>' + '</li>';
		$("#otherCampanyulid").html("");// 清空数据
		$("#otherCampanyulid").append(str);

		$("[name='othercompanyid']").val(data.id);// 第三方企业

		$("#managertelId").val(data.managertel);
		$("#managernameId").val(data.managername);
	}, 1);// 1.个人剂量监测单位；2.职业健康检查单位；3.评价与检测机构； 4.供货商；5.生产厂家；6.设计单位；7.施工单位；
}
jQuery(function($) {
	//文件上传组件
	createUploadify('file_upload',function(data){
		
		//保存数据
		var ids =  new Array();//文件id组
		$("#_fileshow li").each(function() {//获取原有的数据
			ids.push([$(this).val()]);//id添加到数组
		});
		
		$("#reportfiles").val(ids.join(","));//文件id  保存
		$("#reportfiles").focus().blur()//光标切入移除
	},2,'上传文件',"*.*", dataList);//默认上传多个 1上传单个 
		// 文件删除
		$("#_fileshow").click(function(e){
			if(e.target.nodeName != "I"){//点击i的时候触发
				return;
			}
			
			//保存数据
			var ids =  new Array();//文件id组
			$("#_fileshow li").each(function() {//获取原有的数据
				ids.push([$(this).val()]);//id添加到数组
			});
			$("#reportfiles").val(ids.join(","));//文件id  保存
		});
	

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
	
	$("#otherCampanyulid").click(function(e) {
		if (e.target.nodeName != "I") {// 点击i的时候触发
			return;
		}
		// 获取i-> a-> li
		$(e.target).parent().parent().remove();
		//  清空id
		$('[name="othercompanyid"]').val("");
		$("#managertelId").val("");
		$("#managernameId").val("");
	});

});

//保存个人剂量监测信息
function saveMonitor() {
	var time = $("#id-date-range-picker-1").val();
	var arr = time.split(' 至 ');
	$("[name='begintime']").val(arr[0]);
	$("[name='endtime']").val(arr[1]);

	if (!$("#editFrom").valid()) {
		return false;
	}
	$.ajax({
		url : home_url + "/monitor/saveMonitor",
		type : "POST",
		data : $("#editFrom").serialize(),
		success : function(data) {
			if($("input[name='id']").val()==undefined){
				gridAddCallback("monitortb");
				alert(data.message);
				if (data.code == 1) {
					window.location.replace(home_url + "/monitor/edit?id=" + data.data);
				}
			}else{
				alert(data.message);
				if (data.code == 1) {
					window.location.replace(home_url + "/monitor/view?id=" + data.data);
				}
			}
			
		}
	})
}

/**
 * @param id
 *            传递id可以根据id更新
 * @param that
 *            监测结果的值
 */
function saveMonitorData(id, str) {
	$.ajax({
		type : "post",
		url : home_url + "/monitor/saveMonitorData",
		data : {
			id : id,
			monitorid: $("[name='id']").val(),
			data :str
		},
		dataType : "json",
		success : function(data) {
			if (data.code == 1) {
				monitorData.jqGridRedraw();
			}
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			$.jGrowl("异常！请重新尝试或者联系管理员!", {
				header : "error"
			});
		}
	});
}

//监测数据编辑
function monitorDataEdit(id) {
	var myDialog = dialog({
		title : '编辑',
		okValue : '确定',
		width : 710,
		height : 'auto',
		ok : function(data) {
			var str = $("#data").val();
			//验证输入的是否是数字
			if (str.length > 0 && !isNaN(str)) {
				saveMonitorData(id,str);
			} else {
				$.jGrowl("请输入数字!", {
					header : "提示"
				});
				$(that).val("");
				return false;
			} 
		},
		cancelValue : '取消',
		cancel : function() {
		}
	});
	var rowData = $('#monitortb').jqGrid('getRowData',id);
	var html='<div class="modal-body"><div class="personnel-list"><form id="monitorDataEditTb"><input type="hidden" value="'+rowData.id+'"  /><table class="table table-bordered center">';
		html+='	<tbody><tr><td class="active" width="160">姓名</td><td>'+rowData.name+'</td></tr><tr><td class="active" width="160">部门/科室</td><td>'+rowData.orgName+'</td></tr><tr><td class="active" width="160">本次监测结果（mSv）</td>';
		html+='<td><input name="data" id="data" value="'+rowData.data+'"/></td></tr></tbody></table></form></div></div>';

	myDialog.content(html);
	myDialog.showModal();
	
}

/**
 * @param id
 *            传递id可以根据id更新
 */
function monitorDataDel(id) {
	delConfirmDiag(function() {
	$.ajax({
		type : "post",
		url : home_url + "/monitor/delMonitorData",
		data : {
			id : id,
		},
		dataType : "json",
		success : function(data) {
			if (data.code == 1) {
				monitorData.jqGridRedraw();
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
//监测数据新增
function selectStaff() {
	if(tempform!=$("#editFrom").serialize()){
		
		var time = $("#id-date-range-picker-1").val();
		var arr = time.split(' 至 ');
		$("[name='begintime']").val(arr[0]);
		$("[name='endtime']").val(arr[1]);
		
		if (!$("#editFrom").valid()) {
			//跳转锚  防止用户不知道验证未通过
			location.href = "#firstAnchor";
			window.location.hash = firstAnchor; 
			return false;
		}
		$.ajax({
			url : home_url + "/monitor/saveMonitor",
			type : "POST",
			data : $("#editFrom").serialize(),
			success : function(data) {
				if (data.code == 1) {
					//替换form表单 以便下次验证
					tempform=$("#editFrom").serialize();
					$.jGrowl("表单改动！已保存数据！", {
						header : "提示"
					});
				}
			},
			error : function(XMLHttpRequest, textStatus, errorThrown) {
				$.jGrowl("异常！请重新尝试或者联系管理员!", {
					header : "error"
				});
			}
		})
	}
	var monitorId = $("[name='id']").val();
	showDialogModal("人员多选", home_url
			+ "/tool/SelectStaff_SingleforMonitor?monitorId=" + monitorId
			, function(data) {
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
		// 选好之后直接保存数据
		$.ajax({
			type : "post",
			url : home_url + "/monitor/saveMonitorData",
			data : {
				monitorid :monitorId,
				staffids : ids
			},
			dataType : "json",
			success : function(data) {
				if (data.code == 1) {
					$.jGrowl(data.message, {
						header : "提示"
					});
					monitorData.jqGridRedraw();
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


