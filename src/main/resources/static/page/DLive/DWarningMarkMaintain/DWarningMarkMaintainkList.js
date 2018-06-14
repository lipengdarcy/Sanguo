//警示标识维护
var DWarningMarkMaintainList = (function() {
	return {
		init : function(){//初始化
			//搜索
			$("#DWarningMarkSeek").click(function(){
				//重绘
				DWarningMarkMaintainList.jqGridRedraw();
			});
			
			//搜索 
			$('#seek').click(function(){
				DWarningMarkMaintainList.jqGridRedraw();
			});
			
			//新增
    		$("#jqGridPager").siblings(".btnblod").find(".a-add").click(function(){
    			showDialogModal("新增", home_url + "/DLive/warningMarkMaintain/add", function(data) {
    				var str = add.validate();//获取数据   在add.jsp中
    				if(str != false){
    					$.post(home_url + "/DLive/warningMarkMaintain/submit",str, 
							function (response) {
								if (response.code == 0){//提示   确认后跳转到查看
									location.href =  home_url + "/DLive/warningMarkMaintain/detail?id=" + response.data;
								}
							}, 'json');
    				}
    				return false;
    			}, 710, 80);
    			
    		});
    		
    		//导出
    		$("#jqGridPager").siblings(".btnblod").find(".a-excel").click(function(e){
    			e.preventDefault();
    			location.href = home_url + "/DLive/warningMarkMaintain/export";
    		});
    		
			//多项删除
			$("#jqGridPager").siblings(".btnblod").find(".a-remove").click(function() {
				//获取选中id;
				var ids=$('#jqGrid').jqGrid('getGridParam','selarrrow');
				var str = "";
				for(var i = 0; i < ids.length ; i++){//格式化参数
					str += "ids="+ids[i];
					if(ids.length - 1 > i){
						str += "&";
					}
				}
				if(ids != null  && $.trim(ids) != ""){
					delConfirmDiag(function(){
						$.post(home_url + "/DLive/warningMarkMaintain/delete" , str,function(data,status,xhr){
							if(status == "success"  && data.code =="0"){
								//重绘
								DWarningMarkMaintainList.jqGridRedraw();
							}else{
								alertDiag(data.message);
							}
						});
					},"删除","");
					
				}else{
					alertDiag("请选择需要删除的数据! ");
				}
			});
			
			
			DWarningMarkMaintainList.initjqGrid();
		},
    	initjqGrid : function(){
    		//table  和   页脚的id
    		var grid_selector = "#jqGrid";
    		var pager_selector = "#jqGridPager";
    		
    		jQuery(grid_selector).jqGrid({
    			url : home_url + '/DLive/warningMarkMaintain/listData',
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
    			colNames:[ '维护时间','维护人', '操作'],
    			colModel : [ 
    				{name:"maintaintime",index:"maintainTime", width:30,editable: false,sortable : true,
    					formatter : function(cellvalue, options, rowObject){
	    					if(cellvalue != null && cellvalue != undefined)
								return moment(new Date(cellvalue)).format("YYYY-MM-DD");
							return "";
    					}},
					{name:"staffName",index:"", width:30,editable: false,sortable : false,
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
    							info += '<a href="javascript:;" class="ui-pg-div a-delete" data-original-title=""  dataId='+ rowObject.id +' title="删除"><i class="ui-icon icon-trash red"></i></a>';
    							return info;
        					}},
    			],
    			loadComplete : function() {//重绘回调函数
    				
    				var table = this;
    				//删除
    	    		$(table).find(".a-delete").click(function() {
    					var data = {ids : $(this).attr('dataId')};
    						delConfirmDiag(function(){
    							$.post(home_url + "/DLive/warningMarkMaintain/delete",data,function(data,status,xhr){
    								if(status == "success"  && data.code =="0"){
    									//重绘
    									DWarningMarkMaintainList.jqGridRedraw();
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
						var str = home_url + "/DLive/warningMarkMaintain/lookOver" + str;
						window.open(str);
					});
					
					//编辑
    	    		$(table).find(".a-redact").click(function(){
						var id = $(this).attr('dataId');
						var str = "";
						if(id != undefined){
							str = "?id=" + id;
						}
						var str = home_url + "/DLive/warningMarkMaintain/detail" + str;
						window.open(str);
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
    					//editfunc : false,
    					del : false,
    					view : false,
    					search : false,
    					refresh : false,
    					refreshicon : 'icon-refresh green',
    					beforeRefresh : function(){//刷新前触发事件
    						$("#id-date-range-picker").val(""); 
    						$("#state").val(""); 
    			    		$("#end").val("");
    						DWarningMarkMaintainList.jqGridRedraw();//刷新
    					}
    				})
    			
    	},
    	jqGridRedraw : function(){//重绘
    		var state = "";       //获取输入框内容  
    		var end = "";
    		
    		var time = $("#id-date-range-picker").val();
			var arr = time.split(' 至 ');
    		if(time != ""){//让用户看得时间表数据被清空  搜索条件失效
    			state = arr[0];
				end = arr[1];
    		}
	        $("#jqGrid").jqGrid('setGridParam',{  
	            datatype:'json',  
	            postData:{'state':state, "end" : end} //发送数据  
	        }).trigger("reloadGrid"); //重新载入  
    	}
	}
})();


jQuery(document).ready(function() {
	//日期插件
	$('#id-date-range-picker').daterangepicker(
			{
				format : 'YYYY-MM-DD'
			//startDate : begin,
			//endDate : end
			},
			function(start, end, label) {
				$('#state').val(start.format('YYYY-MM-DD'));//开始时间
				$('#end').val(end.format('YYYY-MM-DD'));//结束时间
			});

	
	
	DWarningMarkMaintainList.init();//初始化
});

