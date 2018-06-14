//辐射源信息信息
var rayDeviceList = (function(){//射线装置
	return {
		init : function(){
			//批量删除
			$( "#jqgn1" ).on('click','.a-remove', function(e) {
				e.preventDefault();
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
						$.post(home_url + "/DLive/livecheckdetail/delete",str,function(data,status,xhr){
							if(status == "success"  && data.code =="0"){
								//重绘
								rayDeviceList.initData();
							}else{
								alertDiag(data.message);
							}
						},"json");
					},"删除","");
					
				}else{
					alertDiag("请选择需要删除的射线装置信息!");
				}
			});
			
			//修改检测结果
			$("#jqGrid").on("change", ".result", function(){
				var id = $(this).attr("pid");
				var result = $(this).val();
				$.post(home_url + "/DLive/livecheckdetail/add" , {'id' : id, 'result' : result}, function(data,status,xhr){
					if(status == "success"  && data.code == 0){
						//rayDeviceList.initData();//重新加载表格
						$.jGrowl("修改成功");
					}else{
						alertDiag("操作失败! ");
					}
				},"json");
			});
			
			rayDeviceList.initjqGrid();
			rayDeviceList.initData();
		},//存取jqGrid的数据源
		grid_data : [],
		initData : function(){//初始化jqGrid 的数据
			var data = {checkId : $("#id").val(), isvalid : 2};//isvalid  1.区域    2.射线装置  3.含源装置
			$.get(home_url + "/DLive/livecheckdetail/listData" , data, function(data,status,xhr){
				if(status == "success"  && data.code == 0){
					rayDeviceList.grid_data = data.data;
					$("#jqGrid").jqGrid('clearGridData');  //清空表格
					$("#jqGrid").jqGrid('setGridParam',{  // 重新加载数据
					      datatype:'local',
					      data : rayDeviceList.grid_data,   // 符合格式要求的需要重新加载的数据 
					}).trigger("reloadGrid");
				}
			},"json");
			
		},
		initjqGrid : function(){

    		//table  和   页脚的id
    		var grid_selector = "#jqGrid";
    		var grid_jqGridPager = "#jqGridPager";
    		
    		jQuery(grid_selector).jqGrid({
    			data : rayDeviceList.grid_data,
    			editurl: 'clientArray',
    			datatype : "local",
    			height : 'auto',
    			multiselect : true,//生成多选列
    			multiboxonly : true,//全选
    			autowidth : true,//宽度自适应
    			altRows : true,//分页选项，可以下拉选择每页显示记录数 
    			viewrecords : true,//显示总记录数 
				autoheight: true,//高度自适应
				rownumbers : true,//显示序号
				rownumWidth: 40,//序号列宽度				
				//每页显示记录
				rowNum : 10,
				rowList : [ 10, 20, 30 ],
				pager: grid_jqGridPager,
    			colNames:[ '装置名称',"型号",'编号', '主要参数', '所在场所', '检测结果', '操作'],
    			colModel : [ 
    			    {name:"rayDevice",index:"", editable: true, sortable : false,
    					formatter : function(cellvalue, options, rowObject){
	    					if(cellvalue.deviceName != null && cellvalue.deviceName != undefined)
								return   cellvalue.deviceName;
							return "";
    					}
    				},
					{name:'rayDevice',index:'', editable: true,sortable : true,
    					formatter : function(cellvalue, options, rowObject){
    						if(cellvalue.unitType != null && cellvalue.unitType != undefined)
								return cellvalue.unitType;
							return "";
    				}},
    				{name:"rayDevice",index:"", editable: true,sortable : false,
						formatter : function(cellvalue, options, rowObject){
	    					if(cellvalue.code != null && cellvalue.code != undefined)
								return cellvalue.code;
							return "";
    				}},
    				{name:"rayDevice",index:"", editable: true,sortable : false,
						formatter : function(cellvalue, options, rowObject){
							if(cellvalue.ma == undefined && cellvalue.voltage != undefined){//电流为null是  电压单位为 MV
								return cellvalue.voltage + "MV"; 
							}
							if(cellvalue.voltage != undefined){
								return cellvalue.voltage + "KV/"+ cellvalue.ma +"mA"; 
							}
							return ""; 
							
    				}},
    				{name:"rayDevice",index:"", editable: true,sortable : false,
						formatter : function(cellvalue, options, rowObject){
	    					if(cellvalue.areaName != null && cellvalue.areaName != undefined)
								return cellvalue.areaName;
							return "";
    				}},
    				{name:"result",index:"result", editable: true,sortable : false,
						formatter : function(cellvalue, options, rowObject){
							if(cellvalue != undefined && cellvalue != ''){
								var str = '<select pid="' + rowObject.id + '" class="result">';
								if(cellvalue == '合格'){
									str += '<option selected value="合格">合格</option>'+
										'<option  value="有不符合项">有不符合项</option>';
								}
								if(cellvalue == '有不符合项'){
									str += '<option  value="合格">合格</option>'+
										'<option selected value="有不符合项">有不符合项</option>';
								}
									
								str += '</select>';
								return str;
							}
							return "";
						}
    				},
    				{name:null,index:null, editable: false,sortable : false,
						formatter : function(cellvalue, options, rowObject){
							var info = "";//'<a href="javascript:;" class="ui-pg-div a-redact" data-original-title="" dataId='+ rowObject.id +' title="编辑"><i class="ui-icon icon-pencil green"></i></a>';
							info += '<a href="javascript:;" class="ui-pg-div a-remove" data-original-title="" dataId='+ rowObject.id +' title="删除"><i class="ui-icon icon-trash red"></i></a>';
							return info;
    				}}
    			],
    			loadComplete : function() {//重绘回调函数
    				//删除
    				$("#jqGrid").find(".a-remove").click(function(){
    					var $td = $(this);
    					var data = {
    							ids : $(this).attr('dataId')
    							};
    					delConfirmDiag(function(){
    						var url = home_url + '/DLive/livecheckdetail/delete';
    						$.post(url, data, function(data, status){
    							if(status == "success"  && data.code == 0){
    								rayDeviceList.initData();//重新加载表格
    							}else{
    								alertDiag("操作失败! ");
    							}
    						},"json");
    					},"删除所选记录","");
    				});
    				
    			}
    		});
    		
		}
	};
})();



var sourceDeviceList = (function(){//含源装置
	return {
		init : function(){
			//批量删除
			$( "#jqgn3" ).on('click','.a-remove', function(e) {
				e.preventDefault();
				//获取选中id;
				var ids=$('#jqGrid3').jqGrid('getGridParam','selarrrow');
				var str = "";
				for(var i = 0; i < ids.length ; i++){//格式化参数
					str += "ids="+ids[i];
					if(ids.length - 1 > i){
						str += "&";
					}
				}
				if(ids != null  && $.trim(ids) != ""){
					delConfirmDiag(function(){
						$.post(home_url + "/DLive/livecheckdetail/delete",str,function(data,status,xhr){
							if(status == "success"  && data.code =="0"){
								//重绘
								sourceDeviceList.initData();
							}else{
								alertDiag(data.message);
							}
						},"json");
					},"删除","");
					
				}else{
					alertDiag("请选择需要删除的含源装置信息!");
				}
			});
			
			
			//修改检测结果
			$("#jqGrid3").on("change", ".result", function(){
				var id = $(this).attr("pid");
				var result = $(this).val();
				$.post(home_url + "/DLive/livecheckdetail/add" , {'id' : id, 'result' : result}, function(data,status,xhr){
					if(status == "success"  && data.code == 0){
						$.jGrowl("修改成功");
						//rayDeviceList.initData();//重新加载表格
					}else{
						alertDiag("操作失败! ");
					}
				},"json");
			});
			
			
			sourceDeviceList.initjqGrid();
			sourceDeviceList.initData();
		},//存取jqGrid的数据源
		grid_data : [],
		initData : function(){//初始化jqGrid 的数据
			var data = {checkId : $("#id").val(), isvalid : 3};//isvalid  1.区域    2.射线装置  3.含源装置
			$.get(home_url + "/DLive/livecheckdetail/listData" , data, function(data,status,xhr){
				if(status == "success"  && data.code == 0){
					sourceDeviceList.grid_data = data.data;
					$("#jqGrid3").jqGrid('clearGridData');  //清空表格
					$("#jqGrid3").jqGrid('setGridParam',{  // 重新加载数据
					      datatype:'local',
					      data : sourceDeviceList.grid_data,   // 符合格式要求的需要重新加载的数据 
					}).trigger("reloadGrid");
				}
			},"json");
			
		},
		initjqGrid : function(){

    		//table  和   页脚的id
    		var grid_selector = "#jqGrid3";
    		var grid_jqGridPager = "#jqGridPager3";
    		
    		jQuery(grid_selector).jqGrid({
    			data : sourceDeviceList.grid_data,
    			editurl: 'clientArray',
    			datatype : "local",
    			height : 'auto',
    			multiselect : true,//生成多选列
    			multiboxonly : true,//全选
    			autowidth : true,//宽度自适应
    			altRows : true,//分页选项，可以下拉选择每页显示记录数 
    			viewrecords : true,//显示总记录数 
				autoheight: true,//高度自适应
				rownumbers : true,//显示序号
				rownumWidth: 40,//序号列宽度				
				//每页显示记录
				rowNum : 10,
				rowList : [ 10, 20, 30 ],
				pager: grid_jqGridPager,
    			colNames:[ '装置名称',"型号",'编号', '所在场所', '检测结果', '操作'],
    			colModel : [ 
    			    {name:"sourceDevice",index:"", editable: true, sortable : false,
    					formatter : function(cellvalue, options, rowObject){
	    					if(cellvalue.deviceName != null && cellvalue.deviceName != undefined)
								return   cellvalue.deviceName;
							return "";
    					}
    				},
					{name:'sourceDevice',index:'', editable: true,sortable : true,
    					formatter : function(cellvalue, options, rowObject){
    						if(cellvalue.type != null && cellvalue.type != undefined)
								return cellvalue.type;
							return "";
    				}},
    				{name:"sourceDevice",index:"", editable: true,sortable : false,
						formatter : function(cellvalue, options, rowObject){
	    					if(cellvalue.code != null && cellvalue.code != undefined)
								return cellvalue.code;
							return "";
    				}},
    				{name:"sourceDevice",index:"", editable: true,sortable : false,
						formatter : function(cellvalue, options, rowObject){
	    					if(cellvalue.areaName != null && cellvalue.areaName != undefined)
								return cellvalue.areaName;
							return "";
    				}},
    				{name:"result",index:"result", editable: true,sortable : false,
						formatter : function(cellvalue, options, rowObject){
							if(cellvalue != undefined && cellvalue != ''){
								var str = '<select pid="' + rowObject.id + '"  class="result" >';
								if(cellvalue == '合格'){
									str += '<option selected value="合格">合格</option>'+
										'<option  value="有不符合项">有不符合项</option>';
								}
								if(cellvalue == '有不符合项'){
									str += '<option  value="合格">合格</option>'+
										'<option selected value="有不符合项">有不符合项</option>';
								}
									
								str += '</select>';
								return str;
							}
							return "";
						}
    				},
    				{name:null,index:null, editable: false,sortable : false,
						formatter : function(cellvalue, options, rowObject){
							var info = "";//'<a href="javascript:;" class="ui-pg-div a-redact" data-original-title="" dataId='+ rowObject.id +' title="编辑"><i class="ui-icon icon-pencil green"></i></a>';
							info += '<a href="javascript:;" class="ui-pg-div a-remove" data-original-title="" dataId='+ rowObject.id +' title="删除"><i class="ui-icon icon-trash red"></i></a>';
							return info;
    				}}
    			],
    			loadComplete : function() {//重绘回调函数
    				//删除
    				$("#jqGrid3").find(".a-remove").click(function(){
    					var $td = $(this);
    					var data = {
    							ids : $(this).attr('dataId')
    							};
    					delConfirmDiag(function(){
    						var url = home_url + '/DLive/livecheckdetail/delete';
    						$.post(url, data, function(data, status){
    							if(status == "success"  && data.code == 0){
    								sourceDeviceList.initData();//重新加载表格
    							}else{
    								alertDiag("操作失败! ");
    							}
    						},"json");
    					},"删除所选记录","");
    				});
    			}
    		});
    		
		}
	};
})();


var module = "";
jQuery(document).ready(function() {
	module = $("#module").val(); //区分模块入口 1-放射现场监测，2，放射设备监测 3辐射安全监测
	
	
	//新增
	$("#id-cerxz-dialog").click(addDevice);
	rayDeviceList.init();//放射
	sourceDeviceList.init();//含源
});

//新增设备
function addDevice(){
	//获取已经被选中的设备id
	var ray = rayDeviceList.grid_data;//射线装置数据
	var source = sourceDeviceList.grid_data;//含源装置数据
	var arr1 = Array();
	var arr2 = Array();
	for(i = 0; i < ray.length; i++){//射线装置
		var deviceid = ray[i].deviceid
		arr1.push(deviceid);
	}
	
	for(i = 0; i < source.length; i++){//含源装置
		var deviceid = source[i].deviceid
		arr2.push(deviceid);
	}
	EDevice_Project(null,arr1,null,null,arr2,[1,4],function(data){//设备组件
		var checkid = $("#DLivecheckForm").find("#id").val(); //现场检测id
		//射线装置新增
		var ids = data[0];
		if(ids.length != 0){//有数据的时候
			var str = "";
			for(var i = 0; i < ids.length ; i++){//格式化参数
				str += "ids="+ids[i].id;
				if(ids.length - 1 > i){
					str += "&";
				}
			}
			str += "&checkid=" + checkid;
			str += "&isvalid=2";//状态 0.已删除 1.区域    2.射线装置  3.含源装置
			str += "&result=合格";//默认为合格
			str += "&detectiontime=" + $("#detectiontime").val();
			$.post(home_url + '/DLive/livecheckdetail/batchAdd', str, function(data, status){
				if(status == "success"  && data.code == 0){
					rayDeviceList.initData();//重新加载表格
				}else{
					alertDiag("操作失败! ");
				}
			},"json");
		}
		
		//含源装置新增
		var ids = data[3];
		if(ids.length != 0){//有数据的时候
			var str = "";
			for(var i = 0; i < ids.length ; i++){//格式化参数
				str += "ids="+ids[i].id;
				if(ids.length - 1 > i){
					str += "&";
				}
			}
			str += "&checkid=" + checkid;
			str += "&isvalid=3";//状态 0.已删除 1.区域    2.射线装置  3.含源装置
			str += "&result=合格";//默认为合格
			str += "&detectiontime=" + $("#detectiontime").val();
			$.post(home_url + '/DLive/livecheckdetail/batchAdd', str, function(data, status){
				if(status == "success"  && data.code == 0){
					sourceDeviceList.initData();//重新加载表格
				}else{
					alertDiag("操作失败! ");
				}
			},"json");
		}
	},2);
}
