//非密封放射
var EUnsealedNuclideList = (function() {
	return {
		init : function(){//初始化
			//搜索
			$("#EUnsealedNuclideSeek").click(function(){
				//重绘
				EUnsealedNuclideList.jqGridRedraw();
			});
			$("#EUnsealedNuclideKeyword").keypress(function(e){//回车搜索
				 if (e.which == 13) {
					 EUnsealedNuclideList.jqGridRedraw();
		         }
			});
			
			//新增
    		$("#EUnsealedNuclideGrid-pager").siblings(".btnblod").find(".a-add").click(function(){
    			var str = home_url + "/EDevice/unsealedNuclide/detail";
    			window.open(str);
    		});
    		
    		//导出
    		$("#EUnsealedNuclideGrid-pager").siblings(".btnblod").find(".a-excel").click(function(e){
    			e.preventDefault();
    			location.href = home_url + "/EDevice/unsealedNuclide/export";
    		});
    		
			//多项删除
			$("#EUnsealedNuclideGrid-pager").siblings(".btnblod").find(".a-remove").click(function() {
				//获取选中id;
				var ids=$('#EUnsealedNuclideTable').jqGrid('getGridParam','selarrrow');
				var str = "";
				for(var i = 0; i < ids.length ; i++){//格式化参数
					str += "ids="+ids[i];
					if(ids.length - 1 > i){
						str += "&";
					}
				}
				if(ids != null  && $.trim(ids) != ""){
					delConfirmDiag(function(){
						$.post(home_url + "/EDevice/unsealedNuclide/delete" , str,function(data,status,xhr){
							if(status == "success"  && data.code =="0"){
								//重绘
								EUnsealedNuclideList.jqGridRedraw();
							}else{
								alertDiag(data.message);
							}
						});
					},"删除射线装置","");
					
				}else{
					alertDiag("请选择需要删除的非密封放射核素信息! ");
				}
			});
			
			
			EUnsealedNuclideList.initjqGrid();
		},
    	initjqGrid : function(){
    		//table  和   页脚的id
    		var grid_selector = "#EUnsealedNuclideTable";
    		var pager_selector = "#EUnsealedNuclideGrid-pager";
    		
    		jQuery(grid_selector).jqGrid({
    			url : home_url + '/EDevice/unsealedNuclide/listData',
    			datatype : "json",
    			height : 'auto',
    			autoheight: true,
    			onSelectRow : function(id){// 选择某行时触发事件
					var rowData = $(grid_selector).jqGrid('getRowData', id);
					var state =  $(rowData.a).eq(2).attr('state');
					if (state == 1 || state == 2) {
						$(grid_selector).jqGrid("setSelection", id,false);
					}
				},
				onSelectAll : function(aRowids, status) {//全选事件
					if (status) {
						// uncheck "protected" rows		        	
						var cbs = $("tr.jqgrow > td > input.cbox:disabled",
								grid_selector);
						cbs.removeAttr("checked");
						//modify the selarrrow parameter
						$(grid_selector).selarrrow = $(grid_selector).find(
								"tr.jqgrow:has(td > input.cbox:checked)").map(
								function() {
									return this.id;
								}) // convert to set of ids
						.get(); // convert to instance of Array
					}
					var ids = $(grid_selector).jqGrid('getDataIDs');
					for (var i = 0; i < ids.length; i++) {
						var rowId = ids[i];
						var curCheck = $("#" + rowId).find(":checkbox");
						var rowData = $(grid_selector).jqGrid('getRowData',
								rowId);
						var state =  $(rowData.a).eq(2).attr('state');
						if (state == 1 || state == 2) {
							$('#' + rowId).removeClass('ui-state-highlight');
							$(grid_selector).jqGrid("setSelection", rowId,
									false);
						}
					}
				},
    			viewrecords : true,//显示总记录数 
    			autowidth: true,//自动匹配宽度 
    			rowNum : 10,//默认每页显示
    			rowList:[5,10,15,20,25,30],//自定义每页显示下拉选
    			pager : pager_selector,//表格数据关联的分页条，html元素
    			loadonce: false,//如果为ture则数据只从服务器端抓取一次，之后所有操作都是在客户端执行，翻页功能会被禁用
    			pgbuttons : true,//是否显示翻页按钮
				pginput : true,//是否显示跳转页面的输入框
    			altRows : true,//分页选项，可以下拉选择每页显示记录数 
    			multiselect : true,//生成多选列
    			multiboxonly : true,//全选
    			rownumbers: true,//生成序号列
    			gridComplete: function(data){//气泡 
				    $( ".point" ).tooltip();  
				    $( ".ui-pg-div" ).tooltip();
				    var ids = $(grid_selector).jqGrid('getDataIDs');
					for (var i = 0; i < ids.length; i++) {
						var rowId = ids[i];
						var curCheck = $("#" + rowId).find(":checkbox");
						var rowData = $(grid_selector).jqGrid('getRowData',
								rowId);
						var state =  $(rowData.a).eq(2).attr('state');
						if (state == 1 || state == 2) {
							$('#'+rowId).find("input[type='checkbox']").attr('disabled', 'disabled');
							
						}	
						
					}
				},
    			colNames:[ '核素名称','用途', '物理状态', '最大等效日操作量','最大等效年操作量','使用场所','放射诊疗许可状态',"辐射安全许可状态",'负责人','操作'],
    			colModel : [ 
    				{name:"nuclideName",index:"nuclide_name", width:30,editable: false,sortable : false,
    					formatter : function(cellvalue, options, rowObject){
    						var value;
							if(rowObject.isInfoComplete){
								value = cellvalue
							}else{
								value = cellvalue + "<a class='point' data-rel='tooltip' data-placement='right' data-original-title='该设备信息不完整！'><i class='icon-exclamation'></i></a>";
							}
								
							if (cellvalue != null
									&& cellvalue != undefined)
								return value;
							return "";
    					}},
    				{name:'purpose',index:'purpose', width:30,editable: false,sortable : false,
    					formatter : function(cellvalue, options, rowObject){
    						if(cellvalue == 1){
    							return "影像诊断";
    						}
    						if(cellvalue == 2){
    							return "核素治疗";
    						}
    						if(cellvalue == 3){
    							return "放射免疫分析";
    						}
    						if(cellvalue == 4){
    							return "核素检查";
    						}
    						if(cellvalue == 5){
    							return "同位素试验";
    						}
    						return "";
    					}},
    				{name:'physicalState',index:'physical_state', width:30,editable: false,sortable : false,
    					formatter : function(cellvalue, options, rowObject){
    						if(cellvalue == 1){
    							return "固态";
    						}
    						if(cellvalue == 2){
    							return "液态";
    						}
    						if(cellvalue == 3){
    							return "气态";
    						}
    						return "";
    					}},
    				{name:null,index:null, width:30,editable: false,sortable : false,
    					formatter : function(cellvalue, options, rowObject){//最大等效日操作量
    						if(rowObject.dayBq != null && rowObject.dayBq != undefined)
    							return parseFloat(rowObject.dayBq) + "E" + rowObject.dayBqExponent; 
    						return "";
    					}},
					{name:null,index:null, width:30,editable: false,sortable : false,
    					formatter : function(cellvalue, options, rowObject){//最大等效年操作量
    						if(rowObject.yearBq != null && rowObject.yearBq != undefined)
    							return parseFloat(rowObject.yearBq) + "E" + rowObject.yearBqExponent; 
    						return "";
    					}},
    				{name:"areaName",index:null, width:30,editable: false,sortable : false,//操作场所
    						formatter : function(cellvalue, options, rowObject){
    	    					if(cellvalue != null && cellvalue != undefined)
    								return cellvalue;
    							return "";
        					}},
					{name:"irradiationPermissionState",index:"irradiation_permission_state", width:30,editable: false,sortable : false,
    					formatter : function(cellvalue, options, rowObject){
    						if(cellvalue == 0)//input 删除的时候用
    							return "未许可"; 
    						if(cellvalue == 1)
    							return "已许可"; 
    						if(cellvalue == 2)//未许可  但在三同时
    							return "未许可";
    						if(cellvalue == 3)
    							return "预评已完成";
							return ""; 
    					}},
					{name:"radiatePermissionState2",index:"radiate_permission_state2", width:30,editable: false,sortable : false,
    					formatter : function(cellvalue, options, rowObject){
    						if(cellvalue == 0)
    							return "未许可"; 
    						if(cellvalue == 1)
    							return "已许可"; 
    						if(cellvalue == 2)//未许可  但在三同时
    							return "未许可";
    						if(cellvalue == 3)
    							return "预评已完成";
							return ""; 
    					}},
					{name:"staffName",index:null, width:30,editable: false,sortable : false,//负责人
						formatter : function(cellvalue, options, rowObject){
	    					if(cellvalue != null && cellvalue != undefined)
								return cellvalue;
							return "";
    					}},
    				{name:"a",index:null, width:30,editable: false,sortable : false,
    						formatter : function(cellvalue, options, rowObject){
    							var state = 0;//用来判断是否可以删除
    							if(rowObject.radiatePermissionState2 == 1)//辐射
    								state = 1;
    							if(rowObject.radiatePermissionState2 != 1 && rowObject.radiatePermissionState2 != 0)//辐射
    								state = 2;
    							if(rowObject.irradiationPermissionState == 1)//放射
    								state = 1;
    							if(rowObject.irradiationPermissionState != 1 && rowObject.irradiationPermissionState != 0)//放射
    								state = 2;
        						//查看
    							var info = '<a href="javascript:;" class="ui-pg-div a-lookOver" data-original-title="" dataId='+ rowObject.id +' title="查看"><i class="ui-icon icon-eye-open blue"></i></a>';
    							info += '<a href="javascript:;" class="ui-pg-div a-redact" data-original-title="" dataId='+ rowObject.id +' title="编辑"><i class="ui-icon icon-pencil green"></i></a>';
    							var whether = "";//当设备在未许可但不在三同时  显示删除
    							if(state != 0){
    								whether = "display:none";
    							}
    							
    							info += '<a href="javascript:;" style="' + whether + '" class="ui-pg-div a-remove" data-original-title=""  state='+ state +' dataId='+ rowObject.id +' title="删除"><i class="ui-icon icon-trash red"></i></a>';
    							return info;
        					}},
    			],
    			loadComplete : function() {//重绘回调函数
    				
    				var table = this;
    				//删除
    	    		$(table).find(".a-remove").click(function() {
    	    			var state = $(this).attr('state');
    	    			if(state == 1){
    	    				ConfirmDiag2("该设备已许可");
    	    				return ;
    	    			}
    	    			if(state == 2){
    	    				ConfirmDiag2("该设备已进入三同时工作流程");
    	    				return ;
    	    			}
    					var data = {ids : $(this).attr('dataId')};
    						delConfirmDiag(function(){
    							$.post(home_url + "/EDevice/unsealedNuclide/delete",data,function(data,status,xhr){
    								if(status == "success"  && data.code =="0"){
    									//重绘
    									EUnsealedNuclideList.jqGridRedraw();
    								}else{
    									alertDiag(data.message);
    								}
    							});
    						},"删除","");
    						
    				});
    				
    	    		//查看
    	    		$(table).find(".a-lookOver").click(function(){
						var id = $(this).attr('dataId');
						var str = "";
						if(id != undefined){
							str = "?id=" + id;  //tag一个标识符  用来标记跳转的页面
						}
						var str = home_url + "/EDevice/unsealedNuclide/lookOver" + str;
						window.location.href=str;
					});
					
					//编辑
    	    		$(table).find(".a-redact").click(function(){
						var id = $(this).attr('dataId');
						var str = "";
						if(id != undefined){
							str = "?id=" + id;
						}
						var str = home_url + "/EDevice/unsealedNuclide/detail" + str;
						window.location.href=str;
					});
    	    		
				}
    		});
    		
    		// 表格下方操作
    		jQuery(grid_selector).jqGrid(
    				'navGrid',
    				pager_selector,
    				{
    					add : false,
    					edit : false,
    					editfunc : false,
    					del : false,
    					search : false,
    					refresh : false,
    					refreshicon : 'icon-refresh green',
    					view : false,
    					beforeRefresh : function(){//刷新前触发事件
    						$("#EUnsealedNuclideKeyword").val("");//清空关键字
    						EUnsealedNuclideList.jqGridRedraw();
    					}
    				})
    			
    	},
    	jqGridRedraw : function(){//重绘
    		var keyword = $("#EUnsealedNuclideKeyword").val();       //获取输入框内容  
	        $("#EUnsealedNuclideTable").jqGrid('setGridParam',{  
	            datatype:'json',  
	            postData:{'keyword':keyword} //发送数据  
	        }).trigger("reloadGrid"); //重新载入  
    	}
	}
})();


jQuery(document).ready(function() {
	EUnsealedNuclideList.init();//初始化
});

