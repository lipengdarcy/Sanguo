//个人防护用品详情页
var DShield_1 = (function() {//工作人员个人防护用品
	return {
		init : function(){//初始化
			//新增
    		$("#jqGridpdn1").find(".add").click(function(){
    			add(null,  1 , 1);
    		});
    		DShield_1.initjqGrid();
		},
    	initjqGrid : function(){
    		//table  和   页脚的id
    		var grid_selector = "#jqGrid_1";
    		var pager_selector = "#jqGridPager_1";
    		
    		jQuery(grid_selector).jqGrid(
				getdata({
					pager : pager_selector,//表格数据关联的分页条，html元素
					postData : {"pid" : $("#id").val(), 'shieldType' : 1},//工作人员个人防护用品
					loadComplete : function() {//重绘回调函数
						var table = this;
						$(this).find(".a-redact").click(function(){//编辑
							add($(this).attr("dataid"),  1 , 1);
						});
					}
				})
    		);
    		
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
    					}
    				})
    			
    	},
    	jqGridRedraw : function(){//重绘
    		var pid = $("#id").val();   
	        $("#jqGrid_1").jqGrid('setGridParam',{  
	            datatype:'json',
	            postData:{'pid' : pid, 'shieldType' : 1} //发送数据  
	        }).trigger("reloadGrid"); //重新载入  
    	}
	}
})();

var DShield_2 = (function() {//工作人员辅助防护设施
	return {
		init : function(){//初始化
			//新增
			$("#jqGridpdn2").find(".add").click(function(){
    			add(null,  2, 2);
    		});
    		DShield_2.initjqGrid();
		},
    	initjqGrid : function(){
    		//table  和   页脚的id
    		var grid_selector = "#jqGrid_2";
    		var pager_selector = "#jqGridPager_2";
    		
    		jQuery(grid_selector).jqGrid(
    				getdata({
    						pager : pager_selector,//表格数据关联的分页条，html元素
    						postData : {"pid" : $("#id").val(), 'shieldType' : 2},//工作人员辅助防护设施
    						loadComplete : function() {//重绘回调函数
    							var table = this;
    							$(this).find(".a-redact").click(function(){//编辑
    								add($(this).attr("dataid"),  2 , 2);
    							});
    							
    						}
    				})
    		);
    		
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
    					}
    				})
    			
    	},
    	jqGridRedraw : function(){//重绘
    		var pid = $("#id").val();   
	        $("#jqGrid_2").jqGrid('setGridParam',{  
	            datatype:'json',
	            page:1,
	            postData:{'pid' : pid, 'shieldType' : 2} //发送数据  
	        }).trigger("reloadGrid"); //重新载入  
    	}
	}
})();


var DShield_3 = (function() {//患者和受检者个人防护用品
	return {
		init : function(){//初始化
			//新增
			$("#jqGridpdn3").find(".add").click(function(){
    			add(null, 3 , 3);
    		});
    		DShield_3.initjqGrid();
		},
    	initjqGrid : function(){
    		//table  和   页脚的id
    		var grid_selector = "#jqGrid_3";
    		var pager_selector = "#jqGridPager_3";
    		
    		jQuery(grid_selector).jqGrid(
    				getdata({
    						pager : pager_selector,//表格数据关联的分页条，html元素
    						postData : {"pid" : $("#id").val(), 'shieldType' : 3},//患者和受检者个人防护用品
    						loadComplete : function() {//重绘回调函数
    							var table = this;
    							$(this).find(".a-redact").click(function(){//编辑
    								add($(this).attr("dataid"),  3 , 3);
    							});
    							
    						}
    				})
    		);
    		
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
    					}
    				})
    			
    	},
    	jqGridRedraw : function(){//重绘
    		var pid = $("#id").val();   
	        $("#jqGrid_3").jqGrid('setGridParam',{  
	            datatype:'json',
	            page:1,
	            postData:{'pid' : pid, 'shieldType' : 3} //发送数据  
	        }).trigger("reloadGrid"); //重新载入  
    	}
	}
})();


var DShield_4= (function() {//患者和受检者辅助防护设施
	return {
		init : function(){//初始化
			//新增
			$("#jqGridpdn4").find(".add").click(function(){
    			add(null,  4 , 4);
    		});
    		DShield_4.initjqGrid();
		},
    	initjqGrid : function(){
    		//table  和   页脚的id
    		var grid_selector = "#jqGrid_4";
    		var pager_selector = "#jqGridPager_4";
    		
    		jQuery(grid_selector).jqGrid(
    				getdata({
    						pager : pager_selector,//表格数据关联的分页条，html元素
    						postData : {"pid" : $("#id").val(), 'shieldType' : 4},//患者和受检者辅助防护设施
    						loadComplete : function() {//重绘回调函数
    							var table = this;
    							$(this).find(".a-redact").click(function(){//编辑
    								add($(this).attr("dataid"),  4 , 4);
    							});
    						}
    				})
    		);
    		
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
    					}
    				})
    			
    	},
    	jqGridRedraw : function(){//重绘
    		var pid = $("#id").val();   
	        $("#jqGrid_4").jqGrid('setGridParam',{  
	            datatype:'json',
	            page:1,
	            postData:{'pid' : pid, 'shieldType' : 4} //发送数据  
	        }).trigger("reloadGrid"); //重新载入  
    	}
	}
})();

var DShield_5= (function() {//陪检者
	return {
		init : function(){//初始化
		
		
			//新增
			$("#jqGridpdn5").find(".add").click(function(){
    			add(null,  5 , 5);
    		});
    		DShield_5.initjqGrid();
		},
    	initjqGrid : function(){
    		//table  和   页脚的id
    		var grid_selector = "#jqGrid_5";
    		var pager_selector = "#jqGridPager_5";
    		jQuery(grid_selector).jqGrid(
    				getdata({
    						pager : pager_selector,//表格数据关联的分页条，html元素
    						postData : {"pid" : $("#id").val(), 'shieldType' : 5},//陪检者
    						loadComplete : function() {//重绘回调函数
    							var table = this;
    							$(this).find(".a-redact").click(function(){//编辑
    								add($(this).attr("dataid"),5 , 5);
    							});
    						}
    				})
    		);
    		
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
    					}
    				})
    			
    	},
    	jqGridRedraw : function(){//重绘
    		var pid = $("#id").val();   
	        $("#jqGrid_5").jqGrid('setGridParam',{  
	            datatype:'json',
	            page:1,
	            postData:{"pid" : $("#id").val(), 'shieldType' : 5} //发送数据  
	        }).trigger("reloadGrid"); //重新载入  
    	}
	}
})();

jQuery(document).ready(function() {
	DShield_1.init();//初始化  //工作人员个人防护用品
	DShield_2.init();//初始化  //工作人员辅助防护设施
	DShield_3.init();//初始化  //患者和受检者个人防护用品
	DShield_4.init();//初始化  // 患者和受检者辅助防护设施
	DShield_5.init();//初始化// 陪检者
	
	$("#DShield").on("click", ".a-delete", function(){
		var id = $(this).attr("dataId");
		var tableId = $(this).attr("tableId");//table的id
//		console.log(tableId);
		var str = "";//多项删除参数
		if(id == undefined){//多项删除
			var ids=$('#' + tableId).jqGrid('getGridParam','selarrrow');
			for(var i = 0; i < ids.length ; i++){//格式化参数
				str += "ids="+ids[i];
				if(ids.length - 1 > i){
					str += "&";
				}
			}
		}
		
		if(id == undefined && $.trim(str) == ""){
			//多项删除  没参数
			alertDiag("请选择需要删除的数据!");
			return;
		}
		if((id != undefined && id != '') || str != ''){
			var data = "";
			if(id != undefined && id != ''){
				data = "ids=" + id;
			}
			if(str != ''){
				data = str;
			}
			console.log();
			delConfirmDiag(function(){
				$.post(home_url + "/DLive/shield/delete",data,function(data,status,xhr){
					if(status == "success"  && data.code =="0"){
						//重绘
						if(tableId == 'jqGrid_1'){
							DShield_1.jqGridRedraw();
							return;
						}
						if(tableId == 'jqGrid_2'){
							DShield_2.jqGridRedraw();
							return;
						}
						if(tableId == 'jqGrid_3'){
							DShield_3.jqGridRedraw();
							return;
						}
						if(tableId == 'jqGrid_4'){
							DShield_4.jqGridRedraw();
							return;
						}
						if(tableId == 'jqGrid_5'){
							DShield_5.jqGridRedraw();
							return;
						}
					}else{
						alertDiag(data.message);
					}
				},"json");
			},"删除","");
		}
		

		
	});
	
});

//新增共同方法
/**
 * id  需要查看的数据 id
 * index table的尾号、
 * shieldType 防护用品类型
 */
function add(id, index, shieldType){
	
	var str = "";
	var title = "新增";
	if(id != null && id != undefined){
		str = "?id="+id;
		title = "编辑";
	}
	showDialogModal(title, home_url + "/DLive/shield/addDate"+ str, 
			function(data){
				var str = addDate.validate();//验证   写在addDate.jsp  中
				if(str != false){
					str.shieldtype = shieldType;
					str.is1 = false;//区域是否改变
					str.is2 = false;//是否陪检者是否改变
					$.post(home_url + "/DLive/shield/submit",str,function(data,status,xhr){
						if(status == "success"  && data.code =="0"){
							$(".ui-dialog-button [i-id = cancel]").click();
							//重绘
							var pid = $("#id").val();   
					        $("#jqGrid_" + index).jqGrid('setGridParam',{  
					            datatype:'json',
					            page:1,
					            postData:{"pid" : $("#id").val(), 'shieldType' : shieldType} //发送数据  
					        }).trigger("reloadGrid"); //重新载入  
					        
						}else{
							alertDiag(data.message);
						}
					},"json");
				}
				
				return false;
			}, 710, 300, function(data){});

}


//把jqGrid表格的共同参数提取出来
function getdata(aaa){
	var tag = $("#tag").val();//只有是编辑 和 新增 的时候这个才有值
	if(tag != ''){
		tag = true;
	}else{
		tag = false;
	}
	var data = {
			url : home_url + '/DLive/shield/listData',
			datatype : "json",
			height : 'auto',
			autowidth: true,//自动匹配宽度 
			rowNum : 9999999,//默认每页显示
			loadonce: true,//如果为ture则数据只从服务器端抓取一次，之后所有操作都是在客户端执行，翻页功能会被禁用
//			multiselect : !tag,//生成多选列
//			multiboxonly : !tag,//全选
			rownumbers: !tag,//生成序号列
			pgbuttons : false,//是否显示翻页按钮
			pginput : false,//是否显示跳转页面的输入框
			onSelectRow : function(id){// 选择某行时触发事件
				var grid_selector = "#" + $(this).attr("id");
				var rowData = $(grid_selector).jqGrid('getRowData', id);
				if ($(rowData.operate)[1] == undefined) {
					$(grid_selector).jqGrid("setSelection", id,
							false);
				}
			},
			onSelectAll : function(aRowids, status) {//全选事件
				if (status) {
					// uncheck "protected" rows		
					var grid_selector = "#" + $(this).attr("id");
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
					if ($(rowData.operate)[1] == undefined) {//没有删除超链接的时候  不让勾选
						$('#' + rowId).removeClass('ui-state-highlight');
						$(grid_selector).jqGrid("setSelection", rowId,
								false);
					}
				}
			},
			colNames:[ '<span class="red">*</span>名称','<span class="red">*</span>铅当量', "生产厂家","规格尺寸","<span class='red'>*</span>出厂日期","<span class='red'>*</span>数量", '操作'],
			colModel : [ 
					{name:"shieldname",index:"", width:30,editable: false,sortable : false,
					formatter : function(cellvalue, options, rowObject){
						if(cellvalue != null && cellvalue != undefined)
							return cellvalue;
						return "";
						}},
					{name:"leadEquivalent",index:"", width:30,editable: false,sortable : false,
					formatter : function(cellvalue, options, rowObject){
						if(cellvalue != null && cellvalue != undefined)
							return cellvalue;
						return "";
						}},
					{name:"producer",index:"", width:30,editable: false,sortable : false,
					formatter : function(cellvalue, options, rowObject){
						if(cellvalue != null && cellvalue != undefined)
							return cellvalue;
						return "";
						}},
					{name:"size",index:"", width:30,editable: false,sortable : false,
						formatter : function(cellvalue, options, rowObject){
							if(cellvalue != null && cellvalue != undefined)
								return cellvalue;
							return "";
							}},
					{name:"producetime",index:"producetime", width:30,editable: false,sortable : false,
					formatter : function(cellvalue, options, rowObject){
						if(cellvalue != null && cellvalue != undefined)
							return moment(new Date(cellvalue)).format("YYYY-MM-DD");
						return "";
						}},
					{name:"count",index:"", width:30,editable: false,sortable : false,
					formatter : function(cellvalue, options, rowObject){
						if(cellvalue != null && cellvalue != undefined)
							return cellvalue;
						return "";
					}},
					{name: "operate" ,index:null, width:30,editable: false,sortable : false,hidden : tag,
						formatter : function(cellvalue, options, rowObject){
							var info = '<a href="javascript:;" class="ui-pg-div a-redact" data-original-title="" dataId='+ rowObject.id +' title="编辑"><i class="ui-icon icon-pencil green"></i></a>';
							if(rowObject.isValid == 2){//系统生成的不能删除
								return info;
							}
							var tableId = $(this).attr("id");//table的id  用来删除区分表单刷新
							info += '<a href="javascript:;" class="ui-pg-div a-delete" tableId='+ tableId +' data-original-title=""  dataId='+ rowObject.id +' title="删除"><i class="ui-icon icon-trash red"></i></a>';
							return info;
						}}
			]
		};
	
	return $.extend(data, aaa);
}


