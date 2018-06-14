var projectreport = (function() {
	return {
		init : function(){//初始化
			projectreport.initjqGrid();
			
			//搜索
			$("#Seek").click(function(){
				//重绘
				projectreport.jqGridRedraw();
			});
			$("#projectreportkey").keypress(function(e){//回车搜索
				 if (e.which == 13) {
					 projectreport.jqGridRedraw();
					 return false;
		         }
			});	
	
		},
		jqGridRedraw : function(){//重绘
    		var keyword = $("#projectreportkey").val();       //获取输入框内容  
	        $("#jqGrid").jqGrid('setGridParam',{  
	            datatype:'json',
	            postData:{'keyword':keyword} //发送数据  
	        }).trigger("reloadGrid"); //重新载入  
    	},
		
				//jqGrid内容
		initjqGrid : function(){{
					//table  和   页脚的id
		    		var grid_selector = "#jqGrid";
		    		var pager_selector = "#jqGridPager";
		    	jQuery(grid_selector).jqGrid({
					url: home_url + '/project/report/listData',
					mtype : "post",
					editurl: 'clientArray',
					datatype: "json",
					//复选框
					//multiselect : true,
					
					colModel : [
						{
							label:'申报类型',
							name : "typename",
							index : "typename",
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
						{
							label:'项目名称',
							name : "gridprojectname",
							index : "gridprojectname",
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
						{
							label:'创建时间',
							name : "createtime",
							index : "createtime",
							editable : false,
							sortable : false,
							width:30,
							formatter : function(cellvalue,
									options, rowObject) {
								if(rowObject.createtime == undefined)return ""; 
        						return moment(new Date(rowObject.createtime)).format("YYYY-MM-DD");
							
							}
						},
						{
							label:'申报状态',
							name : "statusname",
							index : "statusname",
							editable : false,
							sortable : false,
							width:30,
							formatter : function(cellvalue,
									options, rowObject) {
								if (rowObject != null
										&& rowObject.statusname != null
										&& rowObject.statusname!= null) {
									return rowObject.statusname;
								}
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
								info+=' <a href="javascript:projectEdit('+rowObject.id+');" class="ui-pg-div" data-rel="tooltip" data-original-title="编辑"><i class="ui-icon icon-pencil green"></i></a>';
								info+=' <a href="javascript:projectreportDel('+rowObject.id+');" class="ui-pg-div" data-rel="tooltip" data-original-title="删除"><i class="ui-icon icon-trash red"></i></a>';
								return info;
							}
						}
					],
					viewrecords : true,
					width : 'auto',
					autowidth : true, // 宽度自适应
					height : 'auto', // 高度自适应
					autoheight : true,
					rownumbers : true, // 显示序号
					
					// rownumWidth: 40,//序号列宽度
					// 每页显示记录
					rowNum : 5,
					rowList : [5, 10, 20, 30 ],
					pager : pager_selector
				});
		
				
		}
		
		}
		
		}
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
})();	


//删除申请
function projectreportDel(id) {
	
	content = '确认删除该申报信息？';
	id=id;

	var myDialog = dialog({
		content : content,
		width : 240,
		lock : true,
		okValue : '确定',
		ok : function() {
			$.ajax({
				url :home_url+ '/project/report/delete/'+id,
				type : 'post',
				dataType : 'json',
				success : function(data) {
					alert(data.message);
					projectreport.jqGridRedraw();			
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
	myDialog.showModal();
	
}

//编辑
function projectEdit(id) {
	
	window.location.href=home_url + "/project/report/show?id=" + id;

}

var project = (function() {
	return {
		init : function(){//初始化
			project.initjqGrid();
			
			//搜索
			$("#Seekproject").click(function(){
				//重绘
				project.jqGridRedraw();
			});
			$("#projectkey").keypress(function(e){//回车搜索
				 if (e.which == 13) {
					 project.jqGridRedraw();
		         }
			});	
	
		},
		jqGridRedraw : function(){//重绘
    		var keyword = $("#projectkey").val();       //获取输入框内容  
	        $("#jqGrid1").jqGrid('setGridParam',{  
	            datatype:'json',
	            postData:{'keyword':keyword} //发送数据  
	        }).trigger("reloadGrid"); //重新载入  
    	},
		
				//jqGrid内容
		initjqGrid : function(){{

			var grid_selector = "#jqGrid1";
			var pager_selector = "#jqGridPager1";

			$(grid_selector).jqGrid(
							{

								url : home_url + '/project/report/projectlistData?type=' + type + '&id=' + id,
								mtype : "post",
								datatype : "json",
								
								colNames : [ '项目名称', '项目性质', '诊疗类别', '项目类别','项目所处阶段'],
								colModel : [
										
										{
											name : 'projectname',
											//width: 40,
										},
										{
											name : 'projecttype',
											//width:60,
											formatter : function(cellvalue, options,
													rowObject) {
												return getProjectType(cellvalue);
											}
										},
										{
											name : 'radiotype',
											hidden : true,
											//width: 80,
										},
										{
											name : 'safetype',
											hidden : true,
											//width: 120,
										},
										{
											name : 'phase',	
											//width: 60,
											formatter : function(cellvalue, options,
													rowObject) {
												return getPhase(cellvalue);
											}

										},
										
									],

								viewrecords : true,
								multiselect : true,
								multiboxonly:true, 
								width : 'auto',
								autowidth : true, // 宽度自适应
								height : 'auto', // 高度自适应
								autoheight : true,
								rownumbers : true, // 显示序号
								beforeSelectRow: beforeSelectRow ,
								gridComplete : function() {
									var records = $(grid_selector).jqGrid('getGridParam', 'records');//表格里没数据则隐藏表格显示提示信息
									var keyword = $("#projectkey").val();  
									if(keyword==null||keyword==""){//此处有搜索内容则不弹出提示信息
									 if(records==0){
										$("#warning").show();
										$("#gbox_jqGrid1").hide();
										$("#searchreport").hide();
										$(".ui-dialog-button").hide();
									} 
									}
									if(id==1||id==2||id==5||id==6||id==8)
									$("#cb_jqGrid1").attr('disabled', 'disabled');//规定那种申报类型不能全选
									if (type == 0)
										$(grid_selector).jqGrid().setGridParam()
												.showCol('radiotype');
									else
										$(grid_selector).jqGrid().setGridParam()
												.showCol('safetype');
									
								},
								

								// rownumWidth: 40,//序号列宽度
								// 每页显示记录
								rowNum : 5,
								rowList : [5,10, 20, 30 ],
								pager : pager_selector
							});

}
		
		}
		
		}
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
	
	function beforeSelectRow()  
	{   
		
		if(id==1||id==2||id==5||id==6||id==8){
		$("#jqGrid1").trigger("jqGridSelectAll",false); //规定那种申报类型不能多选
	    $("#jqGrid1").jqGrid('resetSelection');
	    return(true); 
	}
	} 
	
 
})();	

//项目性质转义
function getProjectType(typeList) {
	if (typeList === undefined)
		return '';

	var typeName = '';
	var array = new Array();
	array = typeList.split(",");
	for (var i = 0; i < array.length; i++) {
		switch (array[i]) {
		case "1":
			typeName += '新建,';
			break;
		case "2":
			typeName += '扩建,';
			break;
		case "3":
			typeName += '改建,';
			break;
		case "4":
			typeName += '技术改造,';
			break;
		case "5":
			typeName += '技术引进,';
			break;
		default:
			break;
		}
	}
	return typeName.substr(0, typeName.length - 1);
}

 


//项目阶段转义
function getPhase(phaseId) {
	if (phaseId === undefined)
		return '';

	var typeName = '';
	switch (phaseId) {
	case 1:
		typeName = '前期阶段';
		break;
	case 2:
		typeName = '评价阶段';
		break;
	case 3:
		typeName = '施工阶段';
		break;
	case 4:
		typeName = '验收阶段';
		break;
	case 5:
		typeName = '部分完成';
		break;
	case 6:
		typeName = '已完成';
		break;
	case -1:
		typeName = '已终止';
		break;
	default:
		typeName = '前期阶段';
		break;
	}
	return typeName;
}


jQuery(document).ready(function() {
	projectreport.init();//初始化
	project.init();
});
