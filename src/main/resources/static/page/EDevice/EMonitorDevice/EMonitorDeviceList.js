//监测设备
var EMonitorDeviceList = (function() {
	return {
		init : function(){//初始化
			//搜索
			$("#EMonitorDeviceSeek").click(function(){
				//重绘
				EMonitorDeviceList.jqGridRedraw();
			});
			
			$("#keyword").keypress(function(e){//回车搜索
				 if (e.which == 13) {
					 EMonitorDeviceList.jqGridRedraw();
		         }
			});
			//新增
    		$("#EMonitorDeviceGrid-pager").siblings(".btnblod").find(".a-add").click(function(){
    			var str = home_url + "/EDevice/monitorDevice/detail";
    			window.open(str);
    		});
    		
    		//导出
    		$("#EMonitorDeviceGrid-pager").siblings(".btnblod").find(".a-excel").click(function(e){
    			e.preventDefault();
    			location.href = home_url + "/EDevice/monitorDevice/export";
    		});
    		
			//多项删除
			$("#EMonitorDeviceGrid-pager").siblings(".btnblod").find(".a-remove").click(function() {
				//获取选中id;
				var ids=$('#EMonitorDeviceTable').jqGrid('getGridParam','selarrrow');
				var str = "";
				for(var i = 0; i < ids.length ; i++){//格式化参数
					str += "ids="+ids[i];
					if(ids.length - 1 > i){
						str += "&";
					}
				}
				if(ids != null  && $.trim(ids) != ""){
					delConfirmDiag(function(){
						$.post(home_url + "/EDevice/monitorDevice/delete" , str,function(data,status,xhr){
							if(status == "success"  && data.code =="0"){
								//重绘
								EMonitorDeviceList.jqGridRedraw();
							}else{
								alertDiag(data.message);
							}
						});
					},"删除监测设备","");
					
				}else{
					alertDiag("请选择需要删除的监测设备! ");
				}
			});
			
			
			EMonitorDeviceList.initjqGrid();
		},
    	initjqGrid : function(){
    		//table  和   页脚的id
    		var grid_selector = "#EMonitorDeviceTable";
    		var pager_selector = "#EMonitorDeviceGrid-pager";
    		
    		jQuery(grid_selector).jqGrid({
    			url : home_url + '/EDevice/monitorDevice/listData',
    			datatype : "json",
    			height : "aotu",
    			viewrecords : true,//显示总记录数 
    			autowidth: true,//自动匹配宽度 
    			rowNum : 10,//默认每页显示
    			rowList:[5,10,15,20,25,30],//自定义每页显示下拉选
    			pager : pager_selector,//表格数据关联的分页条，html元素
    			altRows : true,//分页选项，可以下拉选择每页显示记录数 
    			multiselect : true,//生成多选列
    			multiboxonly : true,//全选
    			rownumbers: true,//生成序号列
    			colNames:[ '监测设备名称','型号', '编号', '存放场所','上次检定时间','负责人','操作'],
    			colModel : [ 
    				{name:"name",index:"name", width:30,editable: false,sortable : false,
    					formatter : function(cellvalue, options, rowObject){
	    					if(cellvalue != null && cellvalue != undefined)
								return cellvalue;
							return "";
    					}},
    				{name:'type',index:'type', width:30,editable: false,sortable : false,
    					formatter : function(cellvalue, options, rowObject){
    						if(cellvalue != null && cellvalue != undefined)
								return cellvalue;
							return "";
    					}},
    				{name:'code',index:'code', width:30,editable: false,sortable : false,
    					formatter : function(cellvalue, options, rowObject){
    						if(cellvalue != null && cellvalue != undefined)
								return cellvalue;
							return "";
    					}},
    				{name:"saveRoom",index:"saveRoom", width:30,editable: false,sortable : false,//存放场所
    						formatter : function(cellvalue, options, rowObject){
    	    					if(cellvalue != null && cellvalue != undefined)
    								return cellvalue;
    							return "";
        					}},
					{name: "verificationTime" ,index: "verificationTime", width:30,editable: false,sortable : false,//上次检定时间
						formatter : function(cellvalue, options, rowObject){
							if(cellvalue != null)
								return moment(new Date(cellvalue)).format("YYYY-MM-DD");
    						return ""; 
    					}},
					{name:"realnames",index:"realnames", width:30,editable: false,sortable : false,//负责人
						formatter : function(cellvalue, options, rowObject){
	    					if(cellvalue != null && cellvalue != undefined)
								return cellvalue;
							return "";
    					}},
    				{name: null ,index:null, width:30,editable: false,sortable : false,
    						formatter : function(cellvalue, options, rowObject){
        						//查看
    							var info = '<a href="javascript:;"  class="ui-pg-div a-lookOver" data-original-title="" dataId='+ rowObject.id +' title="查看"><i class="ui-icon icon-eye-open blue"></i></a>';
    							info += '<a href="javascript:;"  class="ui-pg-div a-redact" data-original-title="" dataId='+ rowObject.id +' title="编辑"><i class="ui-icon icon-pencil green"></i></a>';
    							info += '<a href="javascript:;" class="ui-pg-div a-remove" data-original-title=""  dataId='+ rowObject.id +' title="删除"><i class="ui-icon icon-trash red"></i></a>';
    							return info;
        					}},
    			],
    			loadComplete : function() {//重绘回调函数
    				
    				var table = this;
    				//删除
    	    		$(table).find(".a-remove").click(function() {
    					var data = {ids : $(this).attr('dataId')};
    						delConfirmDiag(function(){
    							$.post(home_url + "/EDevice/monitorDevice/delete",data,function(data,status,xhr){
    								if(status == "success"  && data.code =="0"){
    									//重绘
    									EMonitorDeviceList.jqGridRedraw();
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
						var str = home_url + "/EDevice/monitorDevice/lookOver" + str;
						window.location.href=str;
					});
					
					//编辑
    	    		$(table).find(".a-redact").click(function(){
						var id = $(this).attr('dataId');
						var str = "";
						if(id != undefined){
							str = "?id=" + id;
						}
						var str = home_url + "/EDevice/monitorDevice/detail" + str;
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
    						$("#keyword").val("");//清空关键字
    						EMonitorDeviceList.jqGridRedraw();//刷新
    					}
    				})
    			
    	},
    	jqGridRedraw : function(){//重绘
    		var keyword = $("#keyword").val();       //获取输入框内容  
	        $("#EMonitorDeviceTable").jqGrid('setGridParam',{  
	            datatype:'json', 
	            postData:{'keyword':keyword} //发送数据  
	        }).trigger("reloadGrid"); //重新载入  
    	}
	}
})();


jQuery(document).ready(function() {
	EMonitorDeviceList.init();//初始化
});

