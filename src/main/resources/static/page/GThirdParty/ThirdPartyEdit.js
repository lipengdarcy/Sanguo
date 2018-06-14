var otherCertificate = (function() {
	return {
		init : function() {
			otherCertificate.initjqGrid();
		},
		jqGridRedraw : function(data) {
			$("#otherCertificatetb").jqGrid('setGridParam', {
				postData : {
					othercid : $("input[name='id']").val()
				}
			}).trigger("reloadGrid");// 重新载入
		},
		search : function(data) {
			$("#otherCertificatetb").jqGrid('setGridParam', {
				postData : {}
			}).trigger("reloadGrid");// 重新载入
		},
		initjqGrid : function() {
			// table 和 页脚的id
			var grid_selector = "#otherCertificatetb";
			var pager_selector = "#jqGridPager";
			jQuery(grid_selector)
					.jqGrid(
							{
								url : home_url
										+ '/ThirdParty/queryotherCerListData',
								datatype : "json",
								mtype : "post",
								postData : {
									othercid : $("input[name='id']").val()
								},
								height : 'auto',
								viewrecords : true,// 显示总记录数
								autowidth : true,// 自动匹配宽度
								rowNum : 10,// 默认每页显示
								rowList : [ 5, 10, 15, 20, 25, 30 ],// 自定义每页显示下拉选
								pager : pager_selector,// 表格数据关联的分页条，html元素
								altRows : true,// 分页选项，可以下拉选择每页显示记录数
								rownumbers : true,// 显示序号
								/*multiselect : true,// 生成多选列
								multiboxonly : true,// 全选
*/								colModel : [
										{
											label : "名称",
											name : "certificatename",
											index : "certificatename",
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
											label : "编号",
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
											label : "有效期",
											name : "expiretime",
											index : "expiretime",
											editable : false,
											sortable : false,
											width : 10,
											formatter : function(cellvalue,
													options, rowObject) {
												if (cellvalue != null
														&& cellvalue != undefined) {
													return formatDate(new Date(
															cellvalue));
												}
												return "";
											}
										},
										{
											label : "详情",
											name : "file",
											index : "file",
											editable : false,
											sortable : false,
											width : 10,
											formatter : function(cellvalue,
													options, rowObject) {
												if (rowObject != null
														&& rowObject.attachmentid != null) {
													var info = '<a target="_blank" href="'
														+ getfileUrl(rowObject.attachmentid)
														+ '">'
														+ getfileName(rowObject.attachmentid)
														+ '</a>';
													return info;
												}
												return "";
											}
										},
										{
											label : "操作",
											name : "caozuo",
											index : "caozuo",
											editable : false,
											sortable : false,
											width : 10,
											formatter : function(cellvalue,
													options, rowObject) {
												var info = '<a href="javascript:otherCertificateEdit('
														+ rowObject.id
														+ ');" class="ui-pg-div" data-rel="tooltip" data-original-title="编辑" ><i class="ui-icon icon-pencil green"></i></a>';
											
												if (rowObject.issystemgeneration != 1) {
													info += ' <a href="javascript:otherCertificateDel('
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
function saveotherCertificate() {
	// 验证 验证失败则不进行下一步
	if (!$("#otherCompanyForm").valid()) {
		return ;
	}
	$.ajax({
		type : "post",
		url : home_url + "/ThirdParty/saveThirdParty",
		data : $("#otherCompanyForm").serialize(),
		dataType : "json",
		success : function(data) {
			if (data.code == 1) {
				$.jGrowl(data.message, {
					header : "提示"
				});
				window.location.reload();
			}
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			$.jGrowl("异常！请重新尝试或者联系管理员!", {
				header : "error"
			});
		}
	});
}

function otherCertificateDel(id) {
	delConfirmDiag(function() {
	$.ajax({
		type : "post",
		url : home_url + "/ThirdParty/delOthercertificate",
		data : {id:id},
		dataType : "json",
		success : function(data) {
			if (data.code == 1) {
				otherCertificate.jqGridRedraw();
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
function toBlank(){
	var myDialog = dialog({
		title : '黑名单',
		width:500,
		content : $('#hmd'),
		lock : true,
		okValue : '确认',
		ok : function() {
			$.ajax({
				type : "post",
				url : home_url + "/ThirdParty/toBlank",
				data : $("#hdmForm").serialize(),
				dataType : "json",
				success : function(data) {
					if (data.code == 1) {
						window.location.href=home_url+"/ThirdParty/blankList";
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
		},
		cancelValue : '取消',
		cancel : function() {
		}
	  });

	 myDialog.showModal();
}