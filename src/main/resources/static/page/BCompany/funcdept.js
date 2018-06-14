var funcdept = (function() {
	return {
		init : function() {
			funcdept.initjqGrid();
		},
		jqGridRedraw : function(data) {
			$("#functb").jqGrid('setGridParam', {

			}).trigger("reloadGrid");// 重新载入
		},
		initjqGrid : function() {
			// table 和 页脚的id
			var grid_selector = "#functb";
			var pager_selector = "#grid-pager";
			jQuery(grid_selector)
					.jqGrid(
							{
								url : home_url + '/funcdept/listData',
								datatype : "json",
								mtype : "post",
								postData : {
									id : deptid
								},
								height : 'auto',
								viewrecords : true,// 显示总记录数
								autowidth : true,// 自动匹配宽度
								rowNum : 5,// 默认每页显示
								rowList : [ 5, 10, 15, 20, 25, 30 ],// 自定义每页显示下拉选
								pager : pager_selector,// 表格数据关联的分页条，html元素
								altRows : true,// 分页选项，可以下拉选择每页显示记录数
								/*
								 * multiselect : true,//生成多选列 multiboxonly :
								 * true,//全选
								 */colNames : [ '本机构职务', '姓名', "部门/科室", '职务',
										'联系电话', '专/兼职', '操作' ],
								colModel : [
										{
											name : "duty",
											index : "duty",
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
											name : "name",
											index : "name",
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
											name : "dept",
											index : "dept",
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
											name : "work",
											index : "work",
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
											name : 'tel',
											index : 'tel',
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
											name : 'type',
											index : 'type',
											editable : false,
											sortable : false,
											formatter : function(cellvalue,
													options, rowObject) {
												if (rowObject != null) {
													if (rowObject.type == 1) {
														return "专职";
													} else if (rowObject.type == 2) {
														return "兼职";
													} else {
														return ""
													}
												}
												return "";

											}
										},
										{
											name : "caozuo",
											index : "caozuo",
											editable : false,
											sortable : false,
											formatter : function(cellvalue,
													options, rowObject) {
												var info = '<a href="javascript:;" class="ui-pg-div a-redact" data-original-title=""onclick="editMember('
														+ rowObject.id
														+ ')" title="编辑"><i class="ui-icon icon-pencil green"></i></a>';
												info += '<a href="javascript:;" class="ui-pg-div a-remove" data-original-title="" onclick="delmember('
														+ rowObject.id
														+ ')"  title="删除"><i class="ui-icon icon-trash red"></i></a>';
												return info;
											}
										}, ],
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

var meetting = (function() {
	return {
		init : function() {
			meetting.initjqGrid();
		},
		jqGridRedraw : function(data) {
			$("#meetingtb").jqGrid('setGridParam', {

			}).trigger("reloadGrid");// 重新载入
		},
		initjqGrid : function() {
			// table 和 页脚的id
			var grid_selector = "#meetingtb";
			var pager_selector = "#grid-pager2";
			jQuery(grid_selector)
					.jqGrid(
							{
								url : home_url + '/funcdept?method=queryMeetting',
								datatype : "json",
								mtype : "post",
								postData : {
									id : deptid
								},
								height : 'auto',
								viewrecords : true,// 显示总记录数
								autowidth : true,// 自动匹配宽度
								rowNum : 5,// 默认每页显示
								rowList : [ 5, 10, 15, 20, 25, 30 ],// 自定义每页显示下拉选
								pager : pager_selector,// 表格数据关联的分页条，html元素
								altRows : true,// 分页选项，可以下拉选择每页显示记录数
								/*
								 * multiselect : true,//生成多选列 multiboxonly :
								 * true,//全选
								 */colNames : [ '会议时间', '会议主题', "会议地点", '会议组织人',
										'会议记录',  '操作' ],
								colModel : [
										{
											name : "meettingdate",
											index : "meettingdate",
											editable : false,
											sortable : false,
											formatter : function(cellvalue,
													options, rowObject) {
												if (cellvalue != null
														&& cellvalue != undefined)
													return formatDate(new Date(cellvalue));
												return "";
											}
										},
										{
											name : "meettingtheme",
											index : "meettingtheme",
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
											name : "address",
											index : "address",
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
											name : "staffid",
											index : "staffid",
											editable : false,
											sortable : false,
											formatter : function(cellvalue,
													options, rowObject) {
												if (cellvalue != null
														&& cellvalue != undefined)
													return getStaffName(cellvalue);
												return "";
											}
										},
										{
											name : 'fileid',
											index : 'fileid',
											editable : false,
											sortable : false,
											formatter : function(cellvalue,
													options, rowObject) {
												if (cellvalue != null
														&& cellvalue != undefined){
													var info="";
													var result=cellvalue.split(",");
													for(var i=0;i<result.length;i++){
														if(result[i]!=undefined&&result[i]!=""){
															info+="<a href='"+getfileUrl(result[i])+"'>"+getfileName(result[i])+",</a>";
														}
													}
													return info;
												}
													
												return "";
											}
										},
										{
											name : "caozuo",
											index : "caozuo",
											editable : false,
											sortable : false,
											formatter : function(cellvalue,
													options, rowObject) {
												var info = '<a href="javascript:;" class="ui-pg-div a-redact" data-original-title=""onclick="editMeetting('
														+ rowObject.id
														+ ')" title="编辑"><i class="ui-icon icon-pencil green"></i></a>';
												info += '<a href="javascript:;" class="ui-pg-div a-remove" data-original-title="" onclick="delMeetting('
														+ rowObject.id
														+ ')"  title="删除"><i class="ui-icon icon-trash red"></i></a>';
												return info;
											}
										}, ],
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
// 删除职能机构成员
function delmember(id) {

	var myDialog = dialog({
		title : "删除职能机构成员",
		lock : true,
		okValue : '确定',
		ok : function() {
			$.ajax({
				url : home_url + '/funcdept/delMember',
				type : 'post',
				dataType : 'json',
				data : {
					id : id
				},
				success : function(data) {
					alert(data.message);
					window.location.reload();
				},
				error : function() {
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
	var myDialog1 = dialog({
		title : "删除职能机构成员",
		lock : true,
		content : '<i class="jian-alertico jian-alert-xs jian-ico-alert"></i>是否确定要删除?',
		okValue : '确定',
		ok : function() {
			$
					.ajax({
						url : home_url + '/funcdept/findChildByid',
						type : 'post',
						dataType : 'json',
						data : {
							id : id
						},
						success : function(data) {
							if (data.code == -1) {
								myDialog
										.content('<i class="jian-alertico jian-alert-xs jian-ico-alert"></i>'
												+ data.message);
								myDialog.showModal();
							} else {
								$.ajax({
									url : home_url + '/funcdept/delMember',
									type : 'post',
									dataType : 'json',
									data : {
										id : id
									},
									success : function(data) {
										alert(data.message);
										window.location.reload();
									},
									error : function() {
										$.jGrowl("异常！请重新尝试或者联系管理员!", {
											header : "error"
										});
									}
								});
							}

						}
					});
		},
		cancelValue : '取消',
		cancel : function() {
		}
	});
	myDialog1.showModal();

}
// 新增
function add() {
	$("#addDeptForm input[name='depttype']").val(parseInt(maxTypeid) + 1);
	var myDialog = dialog({
		title : '新增机构',
		width : 480,
		content : $('#adddeptDiv'),
		lock : true,
		okValue : '确认',
		ok : function() {
			// 验证 验证失败则不进行下一步
			if (!$("#addDeptForm").valid()) {
				return false;
			}
			$.ajax({
				url : home_url + '/funcdept/edit',
				data : $("#addDeptForm").serialize(),
				type : 'post',
				dataType : 'json',
				success : function(data) {
					alert(data.message);
					window.location.href = home_url + '/funcdept?type='
							+ data.data;
				}
			});
		},
		cancelValue : '取消',
		cancel : function() {
		}
	});

	myDialog.showModal();

}
// 编辑
function edit() {
	$.ajax({
		url : home_url + '/funcdept/edit',
		data : $("#editDeptForm").serialize(),
		type : 'post',
		dataType : 'json',
		success : function(data) {
			alert(data.message);
			window.location.reload();
		}
	});

}
function del(id) {
	$.ajax({
		url : home_url + '/funcdept/del',
		data : {
			id : id
		},
		type : 'post',
		dataType : 'json',
		success : function(data) {
			alert(data.message);
			window.location.href = "funcdept?type=1";
		}
	});
}

// 人员新增
function addMember() {

	var myDialog = dialog({
		title : '新增职能机构成员',
		lock : true,
		okValue : '确定',
		ok : function() {
			if ($("#duty").val() == "") {
				alert("本机构职务不能为空!");
				return false;
			}
			if ($("#name").val() == "") {
				alert("请选择姓名!");
				return false;
			}
			$.ajax({
				url : home_url + '/funcdept/editMember',
				data : $("#addForm").serialize(),
				type : 'post',
				dataType : 'json',
				success : function(data) {
					// 自动生成放射卫生和应急救援
					if (active != "" && active == 0) {
						$.ajax({
							url : home_url + '/funcdept/edit',
							data : $("#editDeptForm").serialize(),
							type : 'post',
							dataType : 'json',
							success : function(data) {
								alert(data.message);
								window.location.reload();
							}
						});
					} else {
						alert(data.message);
						window.location.reload();
					}

				},
				error : function() {
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
		url : home_url + '/funcdept/addMember?type=' + fundepttype
				+ '&funcdeptId=' + funcdeptId,
		success : function(data) {
			myDialog.content(data);
		}
	});
	myDialog.showModal();

}

//编辑会议
function editMeetting(id) {
	var title="新增会议纪要";
	if(id!=null){
		title="编辑会议纪要";
	}
	var myDialog = dialog({
		title : title,
		lock : true,
		okValue : '确定',
		ok : function() {
			// 验证 验证失败则不进行下一步
			if (!$("#editForm").valid()) {
				return false;
			}
			$.ajax({
				url : home_url + '/funcdept/editMeetting',
				data : $("#editForm").serialize(),
				type : 'post',
				dataType : 'json',
				success : function(data) {
					// 自动生成放射卫生和应急救援
					if (active != "" && active == 0) {
						$.ajax({
							url : home_url + '/funcdept/edit',
							data : $("#editDeptForm").serialize(),
							type : 'post',
							dataType : 'json',
							success : function(data) {
								alert(data.message);
								window.location.reload();
							}
						});
					} else {
						alert(data.message);
						meetting.jqGridRedraw();
					}

				},
				error : function() {
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
		url : home_url + '/funcdept/editMeetting',
		data : {funcdeptId:funcdeptId,id:id},
		success : function(data) {
			myDialog.content(data);
		}
	});
	myDialog.showModal();

}

function delMeetting(id) {
	delConfirmDiag(function(){
		$.ajax({
			url : home_url + '/funcdept/delMeetting',
			data : {id:id},
			type : 'post',
			dataType : 'json',
			success : function(data) {
				alert(data.message);
				meetting.jqGridRedraw();
			},
			error : function() {
				$.jGrowl("异常！请重新尝试或者联系管理员!", {
					header : "error"
				});
			}
		});
	},"删除","");
}
// 编辑职能机构成员
function editMember(id) {
	var myDialog = dialog({
		title : '编辑职能机构成员',
		lock : true,
		okValue : '确定',
		ok : function() {
			if ($("#duty").val() == "") {
				alert("本机构职务不能为空!");
				return false;
			}
			if ($("#name").val() == "") {
				alert("请选择姓名!");
				return false;
			}

			$.ajax({
				url : home_url + '/funcdept/editMember',
				data : $("#editForm").serialize(),
				type : 'post',
				dataType : 'json',
				success : function(data) {
					alert(data.message);
					window.location.reload();
				},
				error : function() {
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
		url : home_url + '/funcdept/addMember?id=' + id + "&type="
				+ fundepttype + "&funcdeptId=" + funcdeptId,
		success : function(data) {
			myDialog.content(data);
		}
	});
	myDialog.showModal();
}