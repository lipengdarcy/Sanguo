//含源装置 - 》密封放射源
var ESealedRadioactiveSource = (function() {
	return {
		init : function(){//初始化
			
			//多项删除
			$("#ers .a-delete").click(function() {
				//获取选中id;
				var ids=$('#ESealedRadioactiveSource').jqGrid('getGridParam','selarrrow');
				var str = "";
				for(var i = 0; i < ids.length ; i++){//格式化参数
					str += "ids="+ids[i];
					if(ids.length - 1 > i){
						str += "&";
					}
				}
				if(ids != null  && $.trim(ids) != ""){
					str += "&cid=" + $("#id").val();//用来维护
					str += "&sign=1";//区别新增删除 1删除  2新增
					delConfirmDiag(function(){
						operation(str);
					},"删除放射源信息","");
					
				}else{
					alertDiag("请选择需要删除的放射源信息! ");
				}
			});
			
			ESealedRadioactiveSource.initjqGrid();
			ESealedRadioactiveSource.initData();
		},//存取jqGrid的数据源
		grid_data : [],
		initData : function(){//初始化jqGrid 的数据
			var data = {cid : $("#id").val()};
			$.get(home_url + "/EDevice/sourceDevice/getData", data, function(data,status,xhr){
				if(status == "success"  && data.code == 0){
					
					ESealedRadioactiveSource.grid_data = data.data.list;//设置数据源
					//设置放射源总活度
					if(data.data.sun != undefined){
						$("#radiateTotalActivity").html(data.data.sun);
					}else{
						$("#radiateTotalActivity").html("");
					}
					
					$("#ESealedRadioactiveSource").jqGrid('clearGridData');  //清空表格
					$("#ESealedRadioactiveSource").jqGrid('setGridParam',{  // 重新加载数据
					      datatype:'local',
					      data : ESealedRadioactiveSource.grid_data,   // 符合格式要求的需要重新加载的数据 
					}).trigger("reloadGrid");
				}
			},"json");
			
		},
    	initjqGrid : function(){
    		//table  和   页脚的id
    		var grid_selector = "#ESealedRadioactiveSource";
    		
    		jQuery(grid_selector).jqGrid({
    			data : ESealedRadioactiveSource.grid_data,
    			datatype : "local",
    			height : 'auto',
    			multiselect : true,//生成多选列
    			multiboxonly : true,//全选
    			autowidth: true,//自动匹配宽度 
    			colNames:[ '核素名称',"放射源类别",'出厂活度', '出厂日期',"放射源编号" ,'操作'],
    			colModel : [ 
    				{name:"nuclideName",index:"nuclide_name", width:30,editable: false,sortable : false,
    					formatter : function(cellvalue, options, rowObject){
	    					if(cellvalue != null && cellvalue != undefined)
								return cellvalue;
							return "";
    					}},
    				{name:"sourceType",index:"sourceType", width:30,editable: false,sortable : false,
    					formatter : function(cellvalue, options, rowObject){//放射源类别
    						if(cellvalue == 1)
    							return "I";
    						if(cellvalue == 2)
    							return "II";
    						if(cellvalue == 3)
    							return "III";
    						if(cellvalue == 4)
    							return "IV";
    						if(cellvalue == 5)
    							return "V";
    						return "";
    					}},
					{name:null,index:null, width:30,editable: false,sortable : false,
    					formatter : function(cellvalue, options, rowObject){//活度
    						if(rowObject.activity != null  && rowObject.activity != undefined)
    							return parseFloat(rowObject.activity) + "E" + rowObject.activityExponent; 
    						return "";
    					}},
					{name:"measureTime",index:"measure_time", width:30,editable: false,sortable : false,
    					formatter : function(cellvalue, options, rowObject){//测量日期
    						if(cellvalue != null  && cellvalue != undefined)
								return moment(new Date(cellvalue)).format("YYYY-MM-DD");
    						return ""; 
    					}},
    				{name:"sourceCode",index:"source_code", width:30,editable: false,sortable : false,//放射源编号
    						formatter : function(cellvalue, options, rowObject){
    	    					if(cellvalue != null && cellvalue != undefined)
    								return cellvalue;
    							return "";
        					}},
    				{name:"a",index:null, width:30,editable: false,sortable : false,
    						formatter : function(cellvalue, options, rowObject){
        						//删除
    							var info ='<a href="javascript:;" class="ui-pg-div a-remove" data-original-title="" dataId='+ rowObject.id +' title="删除"><i class="ui-icon icon-trash red"></i></a>';
    							return info;
        					}},
    			],
    			loadComplete : function(){
    				var table = this;
    				
    				
    				$(table).find(".a-remove").click(function() {
    					var data = {
    							cid : $("#id").val(),//用来维护
    							ids : $(this).attr('dataId'),
    							sign : 1//区别新增删除 1删除  2新增
    							};
    						delConfirmDiag(function(){
    							operation(data);
    						},"删除放射源信息","");
    				});
    				
    			}
    		});
    	}		
	}
})();


jQuery(document).ready(function() {
	ESealedRadioactiveSource.init();//初始化
});
/**新增删除ajax操作相同  所以提取出来 str参数  （cid必须有）*/
function operation(str){//
	$.post(home_url + "/EDevice/sourceDevice/operation" , str,function(data,status,xhr){
		if(status == "success"  && data.code =="0"){
			gridAddCallback("EsourceDeviceTable");//刷新父页面table
			//重绘
			ESealedRadioactiveSource.initData();
		}else{
			alertDiag(data.message);
		}
	});
}

//设备多选
function getDevics(){
	var list = $("#ESealedRadioactiveSource").jqGrid('getDataIDs');
	EDevice_Project(null,[],[],list,[],[3],
			function(data){
			var str = "";
			for(var i = 0; i < data[2].length; i++){//格式化化参数
				str += "ids="+data[2][i].id;
				if(data[2].length - 1 > i){
					str += "&";
				}
				
			}
			if(str != ""){
				str += "&cid=" + $("#id").val();//用来维护
				str += "&sign=2";//区别新增删除 1删除  2新增
				operation(str);
			}
		},2);
	
}