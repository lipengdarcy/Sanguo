//场所检测
var basisList = (function() {
	return {
		init : function(){//初始化
			//新增
    		$("#basisList").find(".a-add").click(function(){
    			var myDialog = dialog({
                    title : '新增',
                    width:'310',
                    content : "<table class='table table-bordered center'>" +
                    		"<tbody>" +
                    		"<tr>" +
                    		"<td>" +
                    		"维护方式" +
                    		"</td>" +
                    		"<td>" +
                    		"<select id='sss'> " +
                    		    "<option value='1'>检查</option>" +
                    		    "<option value='2'>修整</option>" +
                    		    "<option value='3'>更换</option>" +
                    		 "</select>" +
                    		 "</td>" +
                    		 "</tr>" +
                    		 "</tbody>" +
                    		 "</table>",
                    lock : true,
                    okValue : '确认',
                    ok : function() {
                    	var type = $("#sss").val();
            			warningMarkMaintain_Multi(function(data){//位置选择组件
            				if(data.length == 0){
            					return;
            				}
            				var warningmarkid = [];
            				var maintaining = [];
            				for(i = 0; i < data.length; i++){//处理数据   得到需要的数据
            					warningmarkid[i] = data[i].id;
            					maintaining[i] = data[i].name;
            				}
            				var data = {
                					'maintaintype' : type,//维护方式
                					'warningmarkid' : warningmarkid,//维护
                					'pid' : $("#id").val(),
                					'maintaining' : maintaining//维护对象
    	            				};
            				$.post(home_url + "/DLive/warningMarkMaintain/batchAdd" ,data,function(data,status,xhr){
	    							if(status == "success"  && data.code =="0"){
	    								//重绘
	    								basisList.jqGridRedraw();
	    							}else{
	    								alertDiag(data.message);
	    							}
    						});
            			});
                    },
                    cancelValue : '取消',
                    cancel : function() {
                    }
                });
                    myDialog.showModal();

    		});
    		 
			//多项删除
			$("#basisList").find(".a-delete").click(function() {
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
								basisList.jqGridRedraw();
							}else{
								alertDiag(data.message);
							}
						});
					},"删除","");
					
				}else{
					alertDiag("请选择需要删除信息! ");
				}
			});
			
			
			basisList.initjqGrid();
		},
    	initjqGrid : function(){
    		//table  和   页脚的id
    		var grid_selector = "#jqGrid";
    		var pager_selector = "#jqGridPager";
    		//查看界面不显示操作列
    		var mark = true;
    		if($("#basisList").find(".a-add")[0] == undefined){
    			mark = false;
    		}
    		
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
    			multiboxonly : mark,//全选
    			rownumbers: true,//生成序号列
    			postData : {"pid" : $("#id").val()},
    			colNames:[ '机房区域','维护对象', "维护方式", '操作',"警示标识ID"],
    			colModel : [ 
    				{name: "areaName",index:'areaName', width:30,editable: false,sortable : false,
    					formatter : function(cellvalue, options, rowObject){
    						if(cellvalue != null && cellvalue != undefined)
								return cellvalue;
							return "";
    					},
                        cellattr: function(rowId, tv, rawObject, cm, rdata) {
                            //合并单元格
                            return 'id=\'areaName' + rowId + "\'";
                        }
                    },
					{name: "maintaining",index:'maintaining', width:30,editable: false,sortable : false,
    					formatter : function(cellvalue, options, rowObject){
    						if(cellvalue != null && cellvalue != undefined)
								return cellvalue;
							return "";
    					}},
    				{name: "maintaintype" ,index:'maintaintype', width:30,editable: false,sortable : false,
    					formatter : function(cellvalue, options, rowObject){
    						if(cellvalue == 1){
    							return "检查";
    						}
    						if(cellvalue == 2){
    							return "修整";
    						}
    						if(cellvalue == 3){
    							return "更换";
    						}
							return "";
    					},
    					cellattr: function(rowId, tv, rawObject, cm, rdata) {
                            //合并单元格
                            return 'id=\'maintaintype' + rowId + "\'";
                        }
    					},
    				{name: null ,index:null, width:30,editable: false,sortable : false, hidden : !mark,
    						formatter : function(cellvalue, options, rowObject){
        						//查看
    							var info = '<a href="javascript:;" class="ui-pg-div a-remove" data-original-title=""  dataId='+ rowObject.id +' title="删除"><i class="ui-icon icon-trash red"></i></a>';
    							return info;
        					}},
					{name: "warningmarkid",index:'warningmarkid', width:30,editable: false,sortable : false,hidden : true,
    					formatter : function(cellvalue, options, rowObject){
    						if(cellvalue != null && cellvalue != undefined)
								return cellvalue;
							return "";
    					}}
    			],
    			gridComplete: function() {
                    //②在gridComplete调用合并方法
                    Merger(grid_selector, 'areaName');
                    Merger(grid_selector, 'maintaintype');
                    
                },
    			loadComplete : function() {//重绘回调函数
    				
    				var table = this;
    				//删除
    	    		$(table).find(".a-remove").click(function() {
    					var data = {ids : $(this).attr('dataId')};
    						delConfirmDiag(function(){
    							$.post(home_url + "/DLive/warningMarkMaintain/delete",data,function(data,status,xhr){
    								if(status == "success"  && data.code =="0"){
    									//重绘
    									basisList.jqGridRedraw();
    								}else{
    									alertDiag(data.message);
    								}
    							});
    						},"删除","");
    						
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
    						basisList.jqGridRedraw();//刷新
    					}
    				})/*.navButtonAdd('#jqGridPager',{      //新增导出按钮
    					caption:"", 
    					title:"新增",
    					buttonicon:"icon-plus-sign purple",  
    					onClickButton: function(){ 
    						onclick=""
    						//location.href = 'btn.jsp';
    					}
    				})*/
    			
    	},
    	jqGridRedraw : function(){//重绘
    		var pid = $("#id").val();       //获取输入框内容  
	        $("#jqGrid").jqGrid('setGridParam',{  
	            datatype:'json',
	            postData:{'pid' : pid} //发送数据  
	        }).trigger("reloadGrid"); //重新载入  
    	}
	}
})();

jQuery(document).ready(function() {
	basisList.init();//初始化
});

//表格合并函数
function Merger(gridName, CellName) {
    //得到显示到界面的id集合
    var mya = $(gridName).getDataIDs();
    //当前显示多少条
    var length = mya.length;
    
    for (var i = 0; i < length; i++) {
        //从上到下获取一条信息
        var before = $( gridName ).jqGrid('getRowData', mya[i]);
        //定义合并行数
        var rowSpanTaxCount = 1;
        for (j = i + 1; j <= length; j++) {
            //和上边的信息对比 如果值一样就合并行数+1 然后设置rowspan 让当前单元格隐藏
            var end = $(gridName).jqGrid('getRowData', mya[j]);
            if (before[CellName] == end[CellName] && before.warningmarkid == end.warningmarkid) {
                rowSpanTaxCount++;
                $(gridName).setCell(mya[j], CellName, '', { display: 'none' });
            } else {
                rowSpanTaxCount = 1;
                break;
            }
            $("#" + CellName + "" + mya[i] + "").attr("rowspan", rowSpanTaxCount);
        }
    }
}

