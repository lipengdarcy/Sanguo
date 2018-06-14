//射线装置信息汇总
var ERayDeviceList = (function() {
	return {
		init : function(){//初始化
			//搜索
			$("#ERayDevicessuoSeek").click(function(){
				//重绘
				ERayDeviceList.jqGridRedraw();
			});
			$("#ERayDeviceKeyword").keypress(function(e){//回车搜索
				 if (e.which == 13) {
					 ERayDeviceList.jqGridRedraw();
		         }
			});
			//新增
    		$("#ERayDeviceGrid-pager").siblings(".btnblod").find(".a-add").click(function(){
    			var str = home_url + "/EDevice/rayDevice/detail";
    			window.open(str);
    		});
    		
    		//导出
    		$("#ERayDeviceGrid-pager").siblings(".btnblod").find(".a-excel").click(function(e){
    			e.preventDefault();
    			location.href = home_url + "/EDevice/rayDevice/export";
    		});
    		
			//多项删除
			$("#ERayDeviceGrid-pager").siblings(".btnblod").find(".a-remove").click(function() {
				//获取选中id;
				var ids=$('#ERayDeviceTible').jqGrid('getGridParam','selarrrow');
				var str = "";
				for(var i = 0; i < ids.length ; i++){//格式化参数
					str += "ids="+ids[i];
					if(ids.length - 1 > i){
						str += "&";
					}
				}
				if(ids != null  && $.trim(ids) != ""){
					delConfirmDiag(function(){
						$.post(home_url + "/EDevice/rayDevice/delete",str,function(data,status,xhr){
							if(status == "success"  && data.code =="0"){
								//重绘
								ERayDeviceList.jqGridRedraw();
							}else{
								alertDiag(data.message);
							}
						},"json");
					},"删除射线装置","");
					
				}else{
					alertDiag("请选择需要删除的射线装置!");
				}
			});
			
			
			ERayDeviceList.initjqGrid();
		},
    	initjqGrid : function(){
    		//table  和   页脚的id
    		var grid_selector = "#ERayDeviceTible";
    		var pager_selector = "#ERayDeviceGrid-pager";
    		
    		
    		
    		jQuery(grid_selector).jqGrid({
    			url : home_url + '/EDevice/rayDevice/listData',
    			datatype : "json",
    			height : 'auto',
    			autoheight: true,
				onSelectRow : function(id){// 选择某行时触发事件
					var rowData = $(grid_selector).jqGrid('getRowData', id);
					var state =  $(rowData.a).eq(2).attr('state');
					if (state == 1 || state == 2) {
						$(grid_selector).jqGrid("setSelection", id,
								false);
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
    			altRows : true,//分页选项，可以下拉选择每页显示记录数 
    			multiselect : true,//生成多选列
    			multiboxonly : true,//全选
    			rownumbers: true,//生成序号列
    			autowidth: true,
    	        shrinkToFit: true,
    	        scrollrows: false,
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
						//checkbox置灰
						if (state == 1 || state == 2) {
							$('#'+rowId).find("input[type='checkbox']").attr('disabled', 'disabled');
							
						}	
						
					}
				},
    			//postData : {keyword:  $("#ERayDeviceKeyword").val()},//附加参数  //关键字	
    			colNames:[ '装置名称','型号', '编号', '主要参数','生产厂家','放射诊疗类别','球管个数','放射检查类型',
    			           '射线装置类别','所在机房或区域','所在院区','使用人','所在部门/科室','安装单位','出厂日期','诊疗项目明细',
    			           '可能产生的职业病危害因素','活动种类','放射诊疗许可状态','放射诊疗上次年检时间','辐射安全许可状态',
    			           '辐射安全上次年检时间','操作'],
    			colModel : [ 
    				{name:'deviceName',index:'device_name', width:60,editable: false,sortable : false,
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
    				{name:'unitType',index:'unit_type', width:40,editable: false,sortable : false,
    					formatter : function(cellvalue, options, rowObject){
    						if(cellvalue == undefined)return ""; 
    						return cellvalue; 
    					}},
    				{name:'code',index:'code', width:40,editable: false,sortable : false,
    					formatter : function(cellvalue, options, rowObject){
    						if(cellvalue == undefined)return ""; 
    						return cellvalue; 
    					}},
    				{name:null,index:null, width:50,editable: false,sortable : false,
    					formatter : function(cellvalue, options, rowObject){
    						if(rowObject.ma == undefined && rowObject.voltage != undefined){//电流为null是  电压单位为 MV
								return rowObject.voltage + "MV"; 
							}
							if(rowObject.voltage != undefined){
								return rowObject.voltage + "KV/"+ rowObject.ma +"mA"; 
							}
							return ""; 
    					}},
					{name:'producerName',index:'producerName', width:80,editable: false,sortable : false,hidden:true,
    					formatter : function(cellvalue, options, rowObject){
    						if(rowObject.producerName == undefined && rowObject.producerName == null)return "";
    						return rowObject.producerName; 
    					}},	
					{name:'diagnosticRadiologyType',index:'diagnosticRadiologyType', width:80,editable: false,sortable : false,hidden:true,
    					formatter : function(cellvalue, options, rowObject){
    						if(cellvalue == 1)//放射诊疗类别
    							return "放射治疗"; 
    						if(cellvalue == 2)
    							return "核医学"; 
    						if(cellvalue == 3)
    							return "介入放射学";
    						if(cellvalue == 4)
    							return "X射线影像诊断";
							return ""; 
    					}},	
					{name:'bulbCount',index:'bulbCount', width:50,editable: false,sortable : false,hidden:true,
						formatter : function(cellvalue, options, rowObject){
							if(rowObject.bulbCount == undefined && rowObject.bulbCount == null)return "";
							return rowObject.bulbCount; 
						}},	
					{name:"actinoscopyName",index:"actinoscopyName", width:100,editable: false,sortable : false,hidden:true,
						formatter : function(cellvalue, options, rowObject){
							if(rowObject.actinoscopyName == undefined && rowObject.actinoscopyName == null)return "";
							return rowObject.actinoscopyName; 
    					}},
					{name:"rayDeviceType",index:"rayDeviceType", width:80,editable: false,sortable : false,hidden:true,
						formatter : function(cellvalue, options, rowObject){
							if(rowObject.rayDeviceType == undefined && rowObject.rayDeviceType == null)return "";
							return rowObject.rayDeviceType; 
    					}},
    				{name:"areaName",index:"area_name", width:100,editable: false,sortable : false,
    						formatter : function(cellvalue, options, rowObject){
        						if(cellvalue == undefined)return ""; 
        						return cellvalue; 
        					}},
					{name:"hospitalArea",index:"hospitalArea", width:70,editable: false,sortable : false,hidden:true,
						formatter : function(cellvalue, options, rowObject){
							if(rowObject.hospitalArea == undefined && rowObject.hospitalArea == null)return "";
							return rowObject.hospitalArea; 
    					}},
    				{name:"realnames",index:"realnames", width:50,editable: false,sortable : false,
    					formatter : function(cellvalue, options, rowObject){
    						if(cellvalue == undefined)return ""; 
    						return cellvalue; 
    					}},
					{name:"departments",index:"departments", width:90,editable: false,sortable : false,hidden:true,
						formatter : function(cellvalue, options, rowObject){
							if(rowObject.departments == undefined && rowObject.departments == null)return "";
							return rowObject.departments; 
    					}},
					{name:"installUnitName",index:"installUnitName", width:70,editable: false,sortable : false,hidden:true,
						formatter : function(cellvalue, options, rowObject){
							if(rowObject.installUnitName == undefined && rowObject.installUnitName == null)return "";
							return rowObject.installUnitName; 
						}},
					{name:"productionTime",index:"productionTime", width:80,editable: false,sortable : false,sortable : false,
						formatter : function(cellvalue, options, rowObject){
							if(cellvalue == undefined)return ""; 
    						return moment(new Date(cellvalue)).format("YYYY-MM-DD");
						}},
					{name:"diagnosisDetailName",index:"diagnosisDetailName", width:100,editable: false,sortable : false,hidden:true,
						formatter : function(cellvalue, options, rowObject){
							if(rowObject.diagnosisDetailName == undefined && rowObject.diagnosisDetailName == null)return "";
							return rowObject.diagnosisDetailName; 
						}},
					{name:"hazardFactors",index:"hazardFactors", width:140,editable: false,sortable : false,hidden:true,
						formatter : function(cellvalue, options, rowObject){
							if(rowObject.hazardFactors == undefined && rowObject.hazardFactors == null)return "";
							return rowObject.hazardFactors; 
						}},
					{name:"activityType",index:"activityType", width:60,editable: false,sortable : false,hidden:true,
						formatter : function(cellvalue, options, rowObject){
							if(cellvalue == 0)
    							return "使用"; 
    						if(cellvalue == 1)
    							return "生产"; 
    						if(cellvalue == 2)
    							return "销售";
							return ""; 
						}},
    				{name:"irradiationPermissionState",index:"irradiation_permission_state", width:100,editable: false,sortable : false,
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
    				{name:'irradiationExamineTime',index:'irradiation_examine_time', width:120,editable: false,sortable : false,
    					formatter : function(cellvalue, options, rowObject){
    						if(cellvalue == undefined)return ""; 
    						return moment(new Date(cellvalue)).format("YYYY-MM-DD");
    					}},
					{name:"radiatePermissionState2",index:"radiate_permission_state2", width:100,editable: false,sortable : false,
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
    				{name:'radiateExamineTime2',index:'radiate_examine_time2', width:120,editable: false,sortable : false,
    					formatter : function(cellvalue, options, rowObject){
    						if(cellvalue == undefined)return ""; 
    						return moment(new Date(cellvalue)).format("YYYY-MM-DD");
    					}},
    				{name:"a",index:null, width:40,editable: false,sortable : false,
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
    								//$(whether).partents("#gbox_EUnsealedNuclideTable").find(".jqgrow").addClass("mmmm");
    								
    							}
    							
    							info += '<a href="javascript:;" style="' + whether + '" class="ui-pg-div a-remove" data-original-title=""  state='+ state +' dataId='+ rowObject.id +' title="删除"><i class="ui-icon icon-trash red"></i></a>';
    							return info;
    							
        					}},
    			],
    			loadComplete : function() {//重绘回调函数
    				var table = this;
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
							$.post(home_url + "/EDevice/rayDevice/delete" , data,function(data,status,xhr){
								if(status == "success"  && data.code =="0"){
									//重绘
									ERayDeviceList.jqGridRedraw();
								}else{
									alertDiag(data.message);
								}
							},"json");
						},"删除射线装置","");
    						
    				});
    				
    	    		
    	    		//查看
    	    		$(table).find(".a-lookOver").click(function(){
						var id = $(this).attr('dataId');
						var str = "";
						if(id != undefined){
							str = "?id=" + id;
						}
						var str = home_url + "/EDevice/rayDevice/lookOver" + str;
						window.location.href=str;
					});
					
					//编辑
    	    		$(table).find(".a-redact").click(function(){
						var id = $(this).attr('dataId');
						var str = "";
						if(id != undefined){
							str = "?id=" + id;
						}
						var str = home_url + "/EDevice/rayDevice/detail" + str;
						window.location.href=str;
					});
    	    		
				}
				
    		});
    		
    		// 表格下方操作
    		jQuery(grid_selector).jqGrid('navGrid',pager_selector,
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
						$("#ERayDeviceKeyword").val("");//清空关键字
						ERayDeviceList.jqGridRedraw();
					}
    			});

    		
    	},
    	jqGridRedraw : function(){
    		var keyword = $("#ERayDeviceKeyword").val();       //获取输入框内容  
	        $("#ERayDeviceTible").jqGrid('setGridParam',{  
	            datatype:'json',  
	            postData:{'keyword':keyword} //发送数据  
	        }).trigger("reloadGrid"); //重新载入  
    	}
	}
})();

jQuery(document).ready(function() {
	ERayDeviceList.init();//初始化
	
});


