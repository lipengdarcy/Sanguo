var workrole = (function() {
	return {
		init : function() {
			workrole.initjqGrid();
		},
		jqGridRedraw : function(data) {
			$("#workroleInfotb").jqGrid('setGridParam', {
				postData : {keyword:$("#workkeyword").val()}
			}).trigger("reloadGrid");// 重新载入
		},
		search : function(data) {
			$("#workroleInfotb").jqGrid('setGridParam', {
				postData : {keyword:$("#workkeyword").val()}
			}).trigger("reloadGrid");// 重新载入
		},
		initjqGrid : function(id) {
			// table 和 页脚的id
			var grid_selector = "#workroleInfotb";
			var pager_selector = "#grid-pager";
			jQuery(grid_selector).jqGrid(
				{
					url : home_url + '/workrole/queryWorkListData',
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
					 * multiselect : true,//生成多选列 multiboxonly :
					 * true,//全选
					 */
					colModel : [
						{
							label:'岗位/工种名称',
							name : "workname",
							index : "workname",
							editable : false,
							sortable : false,
							width:30,
							formatter : function(cellvalue,
									options, rowObject) {
								if (cellvalue != null
										&& cellvalue != undefined)
									return cellvalue;
								return "";
							}
						},
						/*{
							label:'关联区域名称',
							name : "areaname",
							index : "areaname",
							editable : false,
							sortable : false,
							width:60,
							title:false,
							formatter : function(cellvalue,
									options, rowObject) {
								if (rowObject != null
										&& rowObject.areaList != null){
									var info="";
									for(var i=0;rowObject.areaList.length>i;i++){
										info+="<span class='ui-pg-div' data-rel='tooltip' data-original-title='"+rowObject.areaList[i].parentName+"'>"+rowObject.areaList[i].name+"</span>";
										if(rowObject.areaList.length-1!=i){
											info+=",";
										}
									}
									return info;
								}
								return "";
							}
						},*/
						{
							label:'操作',
							name : null,
							index : null,
							editable : false,
							sortable : false,
							width:10,
							formatter : function(cellvalue,
									options, rowObject) {
								var info="";
								//暂时先注释 以防万一需求变动要改回来
								//info+='<a href="javascript:workroleview('+rowObject.id+');" class="ui-pg-div" data-rel="tooltip" data-original-title="查看" ><i class="ui-icon icon-eye-open blue"></i></a>';
								info+=' <a href="javascript:workroleEdit('+rowObject.id+');" class="ui-pg-div" data-rel="tooltip" data-original-title="编辑"><i class="ui-icon icon-pencil green"></i></a>';
								info+=' <a href="javascript:workroleDel('+rowObject.id+');" class="ui-pg-div" data-rel="tooltip" data-original-title="删除"><i class="ui-icon icon-trash red"></i></a>';
								return info;
							}
						}
					],
					loadComplete : function() {// 重绘回调函数
						var table = this;
					},
					loadComplete : function() {
						var table = this;
						setTimeout(function(){
							enableTooltips(table);
						}, 0);
					},
					
				}
			);
			

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
			})
			
			function enableTooltips(table) {
				$('.navtable .ui-pg-button').tooltip({container:'body'});
				$(table).find('.ui-pg-div').tooltip({container:'body'});
			}
		}
	}
})();

var workrolearea = (function() {
	return {
		init : function() {
			workrolearea.initjqGrid();
		},
		jqGridRedraw : function(data) {
			$("#workroleareatb").jqGrid('setGridParam', {
				postData : {keywords:$("#areakeyword").val()}
			}).trigger("reloadGrid");// 重新载入
		},
		initjqGrid : function(id) {
			// table 和 页脚的id
			var grid_selector = "#workroleareatb";
			var pager_selector = "#grid-pager";
			jQuery(grid_selector)
					.jqGrid(
							{
								url : home_url + '/workrole/queryAreaListData',
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
								 * multiselect : true,//生成多选列 multiboxonly :
								 * true,//全选
								 */
								
								colNames : [ '区域名称', '关联岗位/工种名称','操作' ],
								colModel : [
										{
											name : "name",
											index : "name",
											editable : false,
											sortable : false,
											width:30,
											formatter : function(cellvalue,
													options, rowObject) {
												var info="";
												if (rowObject.name != null
														&& rowObject.name != undefined){
													info="<span class='ui-pg-div' data-rel='tooltip' data-original-title='"+rowObject.parentName+"'>"+rowObject.name+"</span>";
													return info;
												}
												return "";
											}
										},
										{
											name : "workname",
											index : "workname",
											editable : false,
											sortable : false,
											width:60,
											formatter : function(cellvalue,
													options, rowObject) {
												if (rowObject != null
														&& rowObject.workroleList != null){
													var info="";
													for(var i=0;rowObject.workroleList.length>i;i++){
														info+=rowObject.workroleList[i].workname;
														if(rowObject.workroleList.length-1!=i){
															info+=",";
														}
													}
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
											width:10,
											formatter : function(cellvalue,
													options, rowObject) {
												var info='<a href="javascript:workroleAreaview('+rowObject.id+');" class="ui-pg-div" data-rel="tooltip" data-original-title="查看" ><i class="ui-icon icon-eye-open blue"></i></a>';
												return info;
											}
										}
										],
								loadComplete : function() {// 重绘回调函数
									var table = this;

								},
								loadComplete : function() {
									var table = this;
									setTimeout(function(){
										enableTooltips(table);
									}, 0);
								},

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
				view : false,
				beforeRefresh : function(){//刷新前触发事件
					$("#workkeyword").val("");//清空关键字
					workrolearea.jqGridRedraw();//刷新
				}
			})
			
			function enableTooltips(table) {
				$('.navtable .ui-pg-button').tooltip({container:'body'});
				$(table).find('.ui-pg-div').tooltip({container:'body'});
			}

		}
	}
})();
//编辑岗位/工种
function workroleEdit(id) {
	
	
	var myDialog = dialog({
		width : "406", //宽度
		title : '编辑岗位/工种',		
		okValue : '确定',
		ok : function() {
			var returnval=true;
			if (!$("#workroleinfoForm").valid()) {
				return false;
			}
			var areaIdList = [];
			var treeObj = $.fn.zTree.getZTreeObj("treeDemo");
			var name = $('#workname1').val();
			$.ajax({
				url :	home_url+'/workrole/edit', 
				data : { workname: name, id: id},
				type : 'post',
				async: false,
				dataType : 'json',
				success : function(data) {
					if(data.code<0){
						showTipDialog(data.message);
						returnval= false;
					}else{
						showTipDialog(data.message);
						workrole.jqGridRedraw();
					}
							
				},
				error : function() {
					$.jGrowl("异常！请重新尝试或者联系管理员!", {
						header : "error"
					});
				}
			});
			console.log(returnval);
			return returnval;
		},
		cancelValue : '取消',
		cancel : function() {
		}
	});
	if(id){			
		$.ajax({
			url :home_url+ '/workrole/edit?id=' +id,
			success : function(data) {
				myDialog.content(data);
			}
		});
	}else{
		myDialog.title('新增岗位/工种');
		$.ajax({
			url : home_url+'/workrole/edit',
			success : function(data) {
				myDialog.content(data);
			}
		});
	}

	myDialog.showModal();		

}
//编辑岗位/工种
function workroleDel(id) {
	$.ajax({
		url :home_url+ '/workrole/delete/'+id,
		type : 'post',
		dataType : 'json',
		success : function(data) {
			alert(data.message);
			workrole.jqGridRedraw();			
		},
		error : function() {
			$.jGrowl("异常！请重新尝试或者联系管理员!", {
				header : "error"
			});
		}
	});
}
//查看岗位/工种
function workroleview(id) {
	var myDialog = dialog({
		width : "500", //宽度
		title : '查看岗位/工种',		
		cancelValue : '取消',
		cancel : function() {
		}
	});
		$.ajax({
			url : home_url+'/workrole/view/' +id,
			success : function(data) {
				myDialog.content(data);
			}
		});
	

	myDialog.showModal();		

}

//查看区域
function workroleAreaview(id) {
	var myDialog = dialog({
		width : "500", //宽度
		title : '查看',		
		cancelValue : '取消',
		cancel : function() {
		}
	});
		$.ajax({
			url : home_url+'/workrole/areaview/' +id,
			success : function(data) {
				myDialog.content(data);
			}
		});
	

	myDialog.showModal();		

}

$('a[data-rel=tooltip]').tooltip();
$('a.ui-pg-div').tooltip();

