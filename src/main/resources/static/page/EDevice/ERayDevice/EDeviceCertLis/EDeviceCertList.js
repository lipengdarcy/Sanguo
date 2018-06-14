//设备证书 功能js
var EDeviceCertList = (function(){
	return {
		init : function(){
			
			//新增
			$("#id-cerxz-dialog").click(function(){
				EDeviceCertList.Detail(null);
			});
			
			//批量删除
			$( "#EDeviceCert .a-delete" ).on('click', function(e) {
				e.preventDefault();
				//获取选中id;
				var ids=$('#EDeviceCertTable').jqGrid('getGridParam','selarrrow');
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
						$.post(home_url + "/EDevice/deviceCert/delete",str,function(data,status,xhr){
							if(status == "success"  && data.code =="0"){
								//重绘
								EDeviceCertList.initData();
							}else{
								alertDiag(data.message);
							}
						},"json");
					},"删除设备证书","");
					
				}else{
					alertDiag("请选择需要删除的设备证书!");
				}
			});
			
			EDeviceCertList.initjqGrid();
			EDeviceCertList.initData();
		},//存取jqGrid的数据源
		grid_data : [],
		initData : function(){//初始化jqGrid 的数据
			var data = {cid : $("#Rid").val()};
			$.get(home_url + "/EDevice/deviceCert/listData", data, function(data,status,xhr){
				if(status == "success"  && data.code == 0){
					EDeviceCertList.grid_data = data.data;
					$("#EDeviceCertTable").jqGrid('clearGridData');  //清空表格
					$("#EDeviceCertTable").jqGrid('setGridParam',{  // 重新加载数据
					      datatype:'local',
					      data : EDeviceCertList.grid_data,   // 符合格式要求的需要重新加载的数据 
					}).trigger("reloadGrid");
				}
			},"json");
			
		},
		initjqGrid : function(){

    		//table  和   页脚的id
    		var grid_selector = "#EDeviceCertTable";
    		
    		jQuery(grid_selector).jqGrid({
    			data : ERestParameterList.grid_data,
    			datatype : "local",
    			height : 'auto',
    			multiselect : true,//生成多选列
    			multiboxonly : true,//全选
    			autowidth: true,//自动匹配宽度 
    			colNames:[ '证书名称','证书编号',"发证日期", '证书扫描件', '操作'],
    			colModel : [ 
    				{name:"name",index:"name", editable: false,sortable : false,
    					formatter : function(cellvalue, options, rowObject){
	    					if(cellvalue != null && cellvalue != undefined)
								return cellvalue;
							return "";
    					}},
					{name:"certCode",index:"certCode", editable: false,sortable : false,
						formatter : function(cellvalue, options, rowObject){
	    					if(cellvalue != null && cellvalue != undefined)
								return cellvalue;
							return "";
    					}},
					{name:'certificateTime',index:'certificateTime', editable: false,sortable : true,
    					formatter : function(cellvalue, options, rowObject){
    						if(cellvalue == undefined)return ""; 
    						return moment(new Date(cellvalue)).format("YYYY-MM-DD");
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
    				//删除
    				$("#EDeviceCertTable").find(".a-remove").click(function(){
    					var $td = $(this);
    					var data = {
    							ids : $(this).attr('dataId'),
    							cid : $("#Rid").val()};//放射装置的id  用来维护关系用的
    					delConfirmDiag(function(){
    						var url = home_url + '/EDevice/deviceCert/delete';
    						$.post(url, data, function(data, status){
    							if(status == "success"  && data.code == 0){
    								EDeviceCertList.initData();//重新加载表格
    							}else{
    								alertDiag("操作失败! ");
    							}
    						},"json");
    					},"删除设备证书","");
    				});
    				
    				//编辑
    				$("#EDeviceCertTable").find(".a-redact").click(function(){
    					id = $(this).attr('dataId');
    					EDeviceCertList.Detail(id);
    				});
    			}
    		});
    		
		},
    	//新增   详情   页面上有验证   
    	Detail : function checkView(id){
    		var str = "";
    		var title = "新增设备证书";
    		if(id != null && id != undefined){
    			str = "?id="+id;
    			title = "编辑设备证书";
    		}
    		showDialogModal(title, home_url + "/EDevice/deviceCert/Detail"+ str, 
    				function(data){
    					var str = EDeviceCertDetail.validate();//验证   写在EDeviceCertDetail.jsp  中
    					if(str != false){
    						$.post(home_url + "/EDevice/deviceCert/add",str,function(data,status,xhr){
								if(status == "success"  && data.code =="0"){
									$(".ui-dialog-button [i-id = cancel]").click();
									EDeviceCertList.initData();//重新加载表格
								}else{
									alertDiag(data.message);
								}
							},"json");
    					}
    					
    					return false;
    				}, 500, 300, function(data){
    					
    					
    				});
    	}
		
	};
})();



jQuery(document).ready(function() {
	EDeviceCertList.init();
});

