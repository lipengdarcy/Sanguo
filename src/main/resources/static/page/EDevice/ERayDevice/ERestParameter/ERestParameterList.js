//其他参数 功能js
var ERestParameterList = (function(){
	return {
		init : function(){
			
			//其他参数新增弹出
			$( "#id-qtxz-dialog" ).on('click', function(e) {
				e.preventDefault();
				ERestParameterList.Detail(null);
			});
			
			//批量删除
			$( "#ERestParameter .a-delete" ).on('click', function(e) {
				e.preventDefault();
				//获取选中id;
				var ids=$('#ERestParameterTable').jqGrid('getGridParam','selarrrow');
				var str = "";
				for(var i = 0; i < ids.length ; i++){//格式化参数
					str += "ids="+ids[i];
					if(ids.length - 1 > i){
						str += "&";
					}
				}
				if(ids != null  && $.trim(ids) != ""){
					str += "&cid=" + $("#Rid").val();
					delConfirmDiag(function(){
						$.post(home_url + "/EDevice/restParameter/delete",str,function(data,status,xhr){
							if(status == "success"  && data.code =="0"){
								//重绘
								ERestParameterList.initData();
							}else{
								alertDiag(data.message);
							}
						},"json");
					},"删除其他参数","");
					
				}else{
					alertDiag("请选择需要删除的其他参数!");
				}
			});
			
			ERestParameterList.initjqGrid();
			ERestParameterList.initData();
		},
		//存取jqGrid的数据源
		grid_data : [],
		initData : function(){//初始化jqGrid 的数据
			var data = {cid : $("#Rid").val()};
			$.get(home_url + "/EDevice/restParameter/listData", data, function(data,status,xhr){
				if(status == "success"  && data.code == 0){
					ERestParameterList.grid_data = data.data;
					$("#ERestParameterTable").jqGrid('clearGridData');  //清空表格
					$("#ERestParameterTable").jqGrid('setGridParam',{  // 重新加载数据
					      datatype:'local',
					      data : ERestParameterList.grid_data,   // 符合格式要求的需要重新加载的数据 
					}).trigger("reloadGrid");
				}
			},"json");
			
		},
		initjqGrid : function(){

    		//table  和   页脚的id
    		var grid_selector = "#ERestParameterTable";
    		
    		jQuery(grid_selector).jqGrid({
    			data : ERestParameterList.grid_data,
    			datatype : "local",
    			height : 'auto',
    			multiselect : true,//生成多选列
    			multiboxonly : true,//全选
    			autowidth: true,//自动匹配宽度 
    			colNames:[ '参数名','参数值', '附件', '操作'],
    			colModel : [ 
    				{name:"parameterName",index:"parameterName", editable: false,sortable : false,
    					formatter : function(cellvalue, options, rowObject){
	    					if(cellvalue != null && cellvalue != undefined)
								return cellvalue;
							return "";
    					}},
					{name:"parameterValue",index:"parameterValue", editable: false,sortable : false,
						formatter : function(cellvalue, options, rowObject){
	    					if(cellvalue != null && cellvalue != undefined)
								return cellvalue;
							return "";
    					}},
    				{name:"files",index:"files", editable: false,sortable : false,
    						formatter : function(cellvalue, options, rowObject){
    	    					if(cellvalue == null || cellvalue == undefined)
    	    						return "";
    	    					var str = "";
    	    					for(var i = 0; i < cellvalue.length; i++){
    	    						str += '<a target="_blank" href="'+getfileUrl(cellvalue[i].id) +'" >'+ getfileName(cellvalue[i].id) +'</a></br>';
    	    					}
    	    					return str;
        					}},
    				{name:null,index:null, editable: false,sortable : false,
    						formatter : function(cellvalue, options, rowObject){
    							var info = '<a href="javascript:;" class="ui-pg-div a-redact" data-original-title="" dataId='+ rowObject.id +' title="编辑"><i class="ui-icon icon-pencil green"></i></a>';
    							info += '<a href="javascript:;" class="ui-pg-div a-remove" data-original-title="" dataId='+ rowObject.id +' title="删除"><i class="ui-icon icon-trash red"></i></a>';
    							return info;
        					}},
    			],
    			loadComplete : function() {//重绘回调函数
    				//其他参数编辑弹出
    				$("#ERestParameterTable").find(".a-redact").click(function(e){
    					e.preventDefault();
    					var id = $(this).attr('dataId');
    					ERestParameterList.Detail(id);
    				});
    				//删除
    				$("#ERestParameterTable").find(".a-remove").click(function(e){
    					e.preventDefault();
    					var $td = $(this);
    					var data = {
    							ids:$(this).attr('dataId'),
    							cid : $("#Rid").val()};//放射装置的id  用来维护关系用的
    					delConfirmDiag(function(){
    						var url = home_url + '/EDevice/restParameter/delete';
    						$.post(url, data, function(data, status){
    							if(status == "success"  && data.code == 0){
    								ERestParameterList.initData();//重新加载表格
    							}else{
    								alertDiag("操作失败! ");
    							}
    						},"json");
    					},"删除其他参数","");
    				});
    				
    				
    			}
    		});
    		
		},
    	//新增   详情   页面上有验证      url为页面地址     u为提交地址
    	Detail : function checkView(id){
    		var str = "";
    		var title = "新增其他参数";
    		if(id != null && id != undefined){
    			str = "?id="+id;
    			title = "编辑其他参数";
    		}
    		
    		showDialogModal(title, home_url + "/EDevice/restParameter/Detail"+ str, 
    				function(data){
    					var str = ERestParameterDetail.validate();//验证   写在ERestParameterDetail.jsp  中
    					
    					if(str != false){
    						$.post(home_url + "/EDevice/restParameter/add",str,function(data,status,xhr){
								if(status == "success"  && data.code =="0"){
									$(".ui-dialog-button [i-id = cancel]").click();
									ERestParameterList.initData();//重新加载表格
								}else{
									alertDiag(data.message);
								}
							},"json");
    					}
    					
    					
    					return false;
    				}, 480, 250, function(data){
    					
    				});
    	}
		
	};
})();



jQuery(document).ready(function() {
	ERestParameterList.init();
});



