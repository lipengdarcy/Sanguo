//设备证书 功能js
var EVerificationMessageList = (function(){
	return {
		init : function(){
			
			//新增
			$("#id-cerxz-dialog").click(function(){
				EVerificationMessageList.Detail(null);
			});
			
			//批量删除
			$( "#EVerification .a-delete" ).on('click', function(e) {
				e.preventDefault();
				//获取选中id;
				var ids=$('#EVerificationTable').jqGrid('getGridParam','selarrrow');
				var str = "";
				for(var i = 0; i < ids.length ; i++){//格式化参数
					str += "ids="+ids[i];
					if(ids.length - 1 > i){
						str += "&";
					}
				}
				if(ids != null  && $.trim(ids) != ""){
					str += "&cid=" + $("#id").val();
					delConfirmDiag(function(){
						$.post(home_url + "/EDevice/Verification/delete",str,function(data,status,xhr){
							if(status == "success"  && data.code =="0"){
								//重绘
								EVerificationMessageList.initData();
							}else{
								alertDiag(data.message);
							}
						},"json");
					},"删除检定信息","");
					
				}else{
					alertDiag("请选择需要删除的检定信息!");
				}
			});
			
			EVerificationMessageList.initjqGrid();
			EVerificationMessageList.initData();
		},//存取jqGrid的数据源
		grid_data : [],
		initData : function(){//初始化jqGrid 的数据
			var data = {cid : $("#id").val()};
			$.get(home_url + "/EDevice/Verification/listData", data, function(data,status,xhr){
				if(status == "success"  && data.code == 0){
					
					EVerificationMessageList.grid_data = data.data;
					$("#EVerificationTable").jqGrid('clearGridData');  //清空表格
					$("#EVerificationTable").jqGrid('setGridParam',{  // 重新加载数据
					      datatype:'local',
					      data : EVerificationMessageList.grid_data,   // 符合格式要求的需要重新加载的数据 
					}).trigger("reloadGrid");
				}
			},"json");
			
		},
		initjqGrid : function(){

    		//table  和   页脚的id
    		var grid_selector = "#EVerificationTable";
    		
    		jQuery(grid_selector).jqGrid({
    			data : EVerificationMessageList.grid_data,
    			datatype : "local",
    			height : 'auto',
    			rownumbers: true,//生成序号列
    			multiselect : true,//生成多选列
    			multiboxonly : true,//全选
    			autowidth: true,//自动匹配宽度 
    			colNames:[ '检定单位','检定证书编号',"检定结果","检定时间", '附件', '操作'],
    			colModel : [ 
    				{name:"verificationUnit",index:"verificationUnit", editable: false,sortable : false,
    					formatter : function(cellvalue, options, rowObject){
	    					if(cellvalue != null && cellvalue != undefined)
								return cellvalue;
							return "";
    					}},
					{name:"code",index:"code", editable: false,sortable : false,
						formatter : function(cellvalue, options, rowObject){
							if(cellvalue != null && cellvalue != undefined)
								return cellvalue;
							return "";
    					}},
					{name:"result",index:"result", editable: false,sortable : false,
						formatter : function(cellvalue, options, rowObject){
							if(cellvalue == 1)
	    						return "合格";
	    					if(cellvalue == 2)
	    						return "不合格";
							return "";
    					}},
					{name:'verificationTime',index:'verificationTime', editable: false,sortable : true,
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
    	    						str += '<a target="_blank" href="'+getfileUrl(cellvalue[i].id)+'" >'+ getfileName(cellvalue[i].id) +'</a></br>';
    	    					}
    	    					return str;
        					}},
    				{name:null,index:null, editable: false,sortable : false,
    						formatter : function(cellvalue, options, rowObject){
    							var info = '<a href="javascript:;" class="ui-pg-div a-redact" data-original-title="" dataId='+ rowObject.id +' title="编辑"><i class="ui-icon icon-pencil green"></i></a>';
    							info += '<a href="javascript:;" class="ui-pg-div a-remove" data-original-title="" dataId='+ rowObject.id +' title="删除"><i class="ui-icon icon-trash red"></i></a>';
    							return info;
        					}}
    			],
    			loadComplete : function() {//重绘回调函数
    				//删除
    				$("#EVerificationTable").find(".a-remove").click(function(){
    					var $td = $(this);
    					var data = {
    							ids : $(this).attr('dataId'),
    							cid : $("#id").val()};//放射装置的id  用来维护关系用的
    					delConfirmDiag(function(){
    						var url = home_url + '/EDevice/Verification/delete';
    						$.post(url, data, function(data, status){
    							if(status == "success"  && data.code == 0){
    								EVerificationMessageList.initData();//重新加载表格
    							}else{
    								alertDiag("操作失败! ");
    							}
    						},"json");
    					},"删除检定信息","");
    				});
    				
    				//编辑
    				$("#EVerificationTable").find(".a-redact").click(function(){
    					id = $(this).attr('dataId');
    					EVerificationMessageList.Detail(id);
    				});
    			}
    		});
    		
		},
    	//新增   详情   页面上有验证   
    	Detail : function checkView(id){
    		var str = "";
    		var title = "新增检定信息";
    		if(id != null && id != undefined){
    			str = "?id="+id;
    			title = "编辑检定信息";
    		}
    		showDialogModal(title, home_url + "/EDevice/Verification/Detail"+ str, 
    				function(data){
    					var str = EVerificationMessageDetail.validate();//验证   写在EVerificationMessageDetail.jsp  中
    					if(str != false){
    						$.post(home_url + "/EDevice/Verification/add",str,function(data,status,xhr){
								if(status == "success"  && data.code =="0"){
									$(".ui-dialog-button [i-id = cancel]").click();
									EVerificationMessageList.initData();//重新加载表格
								}else{
									alertDiag(data.message);
								}
							},"json");
    					}
    					
    					return false;
    				}, 500, 'auto', function(data){
    				});
    	}
		
	};
})();



jQuery(document).ready(function() {
	EVerificationMessageList.init();
});

