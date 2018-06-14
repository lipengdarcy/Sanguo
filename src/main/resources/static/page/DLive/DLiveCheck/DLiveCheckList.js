//场所检测
var DLiveCheckList = (function() {
	return {
		init : function(){//初始化
			//搜索
			$("#DLiveCheckSeek").click(function(){
				//重绘
				DLiveCheckList.jqGridRedraw();
			});
			
			$("#keyword").keypress(function(e){//回车搜索
				 if (e.which == 13) {
					 DLiveCheckList.jqGridRedraw();
		         }
			});
			//新增
    		$("#DLiveCheck-pager").siblings(".btnblod").find(".a-add").click(function(){
    			var str = home_url + "/DLive/liveCheck/detail/" + module;
    			window.open(str);
    		});
    		
    		//导出
    		$("#DLiveCheck-pager").siblings(".btnblod").find(".a-excel").click(function(e){
    			e.preventDefault();
    			location.href = home_url + "/DLive/liveCheck/export/" + module;
    		});
    		
			//多项删除
			$("#DLiveCheck-pager").siblings(".btnblod").find(".a-remove").click(function() {
				//获取选中id;
				var ids=$('#DLiveCheckTable').jqGrid('getGridParam','selarrrow');
				var str = "";
				for(var i = 0; i < ids.length ; i++){//格式化参数
					str += "ids="+ids[i];
					if(ids.length - 1 > i){
						str += "&";
					}
				}
				if(ids != null  && $.trim(ids) != ""){
					delConfirmDiag(function(){
						$.post(home_url + "/DLive/liveCheck/delete" , str,function(data,status,xhr){
							if(status == "success"  && data.code =="0"){
								//重绘
								DLiveCheckList.jqGridRedraw();
							}else{
								alertDiag(data.message);
							}
						});
					},"删除","");
					
				}else{
					alertDiag("请选择需要删除信息! ");
				}
			});
			
			
			DLiveCheckList.initjqGrid();
		},
    	initjqGrid : function(){
    		//table  和   页脚的id
    		var grid_selector = "#DLiveCheckTable";
    		var pager_selector = "#DLiveCheck-pager";
    		//因为三个模块共用同一个jqGrid  模块中有些列是隐藏的
    		//控制   （'检测区域', "监测设备"） 列的隐藏 显示属性
    		var mark = true;//true  为  放射现场监测和 辐射安全监测   反之为放射设备监测模块
    		if(module == 2){//设备监测模块
    			mark = false;
    		}
    		jQuery(grid_selector).jqGrid({
    			url : home_url + '/DLive/liveCheck/listData',
    			datatype : "json",
    			height : 'auto',
    			viewrecords : true,//显示总记录数 
    			autowidth: true,//自动匹配宽度 
    			rowNum : 10,//默认每页显示
    			rowList:[5,10,15,20,25,30],//自定义每页显示下拉选
    			pager : pager_selector,//表格数据关联的分页条，html元素
    			altRows : true,//分页选项，可以下拉选择每页显示记录数 
    			multiselect : true,//生成多选列
    			multiboxonly : true,//全选
    			rownumbers: true,//生成序号列
    			postData : {"module" : module},// 区分模块入口 1-放射现场监测，2，放射设备监测 3辐射安全监测
    			colNames:[ '检测类型','检测区域', "检测设备", '检测时间', '检测机构','检测报告编号','操作'],
    			colModel : [ 
    				{name:"checktype",index:"checktype", width:30,editable: false,sortable : false,
    					formatter : function(cellvalue, options, rowObject){
    						if(cellvalue == 1)
    							return "控评检测";
    						if(cellvalue == 2)
    							return "年度检测";
    						if(cellvalue == 3)
    							return "验收检测";
    						if(cellvalue == 4)
    							return "年度检测";
    						if(cellvalue == 5)
    							return "验收检测";
    						if(cellvalue == 6)
    							return "状态检测";
    						if(cellvalue == 7)
    							return "稳定性检测";
							return "";
    					}},
    				{name: "areaName",index:'areaName', width:30,editable: false,sortable : false,hidden : !mark,
    					formatter : function(cellvalue, options, rowObject){
    						if(cellvalue != null && cellvalue != undefined)
								return cellvalue;
							return "";
    					}},
					{name: "deviceName",index:'deviceName', width:30,editable: false,sortable : false,hidden : mark,
    					formatter : function(cellvalue, options, rowObject){
    						if(cellvalue != null && cellvalue != undefined)
								return cellvalue;
							return "";
    					}},
    				{name: "detectiontime" ,index:'', width:30,editable: false,sortable : true,
    					formatter : function(cellvalue, options, rowObject){
    						if(cellvalue == undefined)return ""; 
    						return moment(new Date(cellvalue)).format("YYYY-MM-DD");
    					}},
    				{name: "checkorgName",index:"", width:30,editable: false,sortable : false,
    						formatter : function(cellvalue, options, rowObject){
    	    					if(cellvalue != null && cellvalue != undefined)
    								return cellvalue;
    							return "";
        					}},
					{name: "reportno",index:"", width:30,editable: false,sortable : false,
						formatter : function(cellvalue, options, rowObject){
	    					if(cellvalue != null && cellvalue != undefined)
								return cellvalue;
	    					return "";
    					}},
    				{name: null ,index:null, width:30,editable: false,sortable : false,
    						formatter : function(cellvalue, options, rowObject){
        						//查看
    							var info = '<a href="javascript:;" class="ui-pg-div a-lookOver" data-original-title="" dataId='+ rowObject.id +' title="查看"><i class="ui-icon icon-eye-open blue"></i></a>';
    							info += '<a href="javascript:;" class="ui-pg-div a-redact" data-original-title="" dataId='+ rowObject.id +' title="编辑"><i class="ui-icon icon-pencil green"></i></a>';
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
    							$.post(home_url + "/DLive/liveCheck/delete",data,function(data,status,xhr){
    								if(status == "success"  && data.code =="0"){
    									//重绘
    									DLiveCheckList.jqGridRedraw();
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
							str = "?id=" + id;  
						}
						var str = home_url + "/DLive/liveCheck/lookOver/" + module + str;
						location.href =str;
					});
					
					//编辑
    	    		$(table).find(".a-redact").click(function(){
						var id = $(this).attr('dataId');
						var str = "";
						if(id != undefined){
							str = "?id=" + id;
						}
						var str = home_url + "/DLive/liveCheck/detail/" + module + str;
						location.href =str;
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
    					}
    				})
    			
    	},
    	jqGridRedraw : function(){//重绘
    		var keyword = $("#keyword").val();       //获取输入框内容  
	        $("#DLiveCheckTable").jqGrid('setGridParam',{  
	            datatype:'json',
	            postData:{'keyword':keyword, 'module' : module} //发送数据  
	        }).trigger("reloadGrid"); //重新载入  
    	}
	}
})();

var module = "";
jQuery(document).ready(function() {
	module = $("#module").val(); //区分模块入口 1-放射现场监测，2，放射设备监测 3辐射安全监测
	DLiveCheckList.init();//初始化
});

