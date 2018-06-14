//个人防护用品
var DShieldList = (function() {
	return {
		init : function(){//初始化
			
			//搜索 
			$('#seek').click(function(){
				DShieldList.jqGridRedraw();
			});
			
			$("#keyword").keypress(function(e){//回车搜索
				 if (e.which == 13) {
					 DShieldList.jqGridRedraw();
		         }
			});
			
			//新增
    		$("#jqGridPager").siblings(".btnblod").find(".a-add").click(function(){
    			var str =  home_url + "/DLive/shield/detail";
    			window.open(str);
    		});
    		
    		//导出
    		$("#jqGridPager").siblings(".btnblod").find(".a-excel").click(function(e){
    			e.preventDefault();
    			location.href = home_url + "/DLive/shield/export";
    		});
    		
			//多项删除
//			$("#jqGridPager").find(".a-remove").click(function() {
//				//获取选中id;
//				var ids=$('#jqGrid').jqGrid('getGridParam','selarrrow');
//				var str = "";
//				for(var i = 0; i < ids.length ; i++){//格式化参数
//					str += "ids="+ids[i];
//					if(ids.length - 1 > i){
//						str += "&";
//					}
//				}
//				if(ids != null  && $.trim(ids) != ""){
//					delConfirmDiag(function(){
//						$.post(home_url + "/DLive/shield/delete" , str,function(data,status,xhr){
//							if(status == "success"  && data.code =="0"){
//								//重绘
//								DShieldList.jqGridRedraw();
//							}else{
//								alertDiag(data.message);
//							}
//						});
//					},"删除","");
//					
//				}else{
//					alertDiag("请选择需要删除的数据! ");
//				}
//			});
			
			
			DShieldList.initjqGrid();
		},
    	initjqGrid : function(){
    		//table  和   页脚的id
    		var grid_selector = "#jqGrid";
    		var pager_selector = "#jqGridPager";
    		
    		jQuery(grid_selector).jqGrid({
    			url : home_url + '/DLive/shield/listData2',
    			datatype : "json",
    			height : 'auto',
    			viewrecords : true,//显示总记录数 
    			autowidth: true,//自动匹配宽度 
    			rowNum : 10,//默认每页显示
    			rowList:[5,10,15,20,25,30],//自定义每页显示下拉选
    			pager : pager_selector,//表格数据关联的分页条，html元素
    			altRows : true,//分页选项，可以下拉选择每页显示记录数 
    			multiselect : false,//生成多选列
    			multiboxonly : false,//全选
    			rownumbers: false,//生成序号列
    			colNames:[ '机房或区域','操作'],
    			colModel : [ 
					{name:"areaName",index:"", width:30,editable: false,sortable : false,
						formatter : function(cellvalue, options, rowObject){
							if(cellvalue != null && cellvalue != undefined)
								return cellvalue;
							return "";
						}
					},
    				{name: "" ,index:null, width:30,editable: false,sortable : false,
    						formatter : function(cellvalue, options, rowObject){
        						//查看
    							var info = '<a href="javascript:;" class="ui-pg-div a-lookOver" data-original-title="" dataId='+ rowObject.id +' title="查看"><i class="ui-icon icon-eye-open blue"></i></a>';
    							info += '<a href="javascript:;" class="ui-pg-div a-redact" data-original-title="" dataId='+ rowObject.id +' title="编辑"><i class="ui-icon icon-pencil green"></i></a>';
    							info += '<a href="javascript:;" class="ui-pg-div a-delete" data-original-title=""  dataId='+ rowObject.id +' title="删除"><i class="ui-icon icon-trash red"></i></a>';
    							return info;
        					}
    				}
    			],
    			subGrid : true,//开启子表格支持  
    			//展开子表格时，将触发此选项定义的事件方法
    			subGridRowExpanded: showThirdLevelChildGrid,
//    			subGridRowColapsed : function(subgrid_id, row_id){//当点击“-”收起子表格时，将触发此选项定义的事件方法；
//    			},
    			loadComplete : function() {//重绘回调函数
    				var table = this;
    				//删除
    	    		$(table).find(".a-delete").click(function() {
    					var data = {ids : $(this).attr('dataId')};
    						delConfirmDiag(function(){
    							$.post(home_url + "/DLive/shield/delete",data,function(data,status,xhr){
    								if(status == "success"  && data.code =="0"){
    									//重绘
    									DShieldList.jqGridRedraw();
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
						var str  = home_url + "/DLive/shield/lookOver" + str;
						location.href =str;
					});
					
					//编辑
    	    		$(table).find(".a-redact").click(function(){
						var id = $(this).attr('dataId');
						var str = "";
						if(id != undefined){
							str = "?id=" + id;
						}
						var str = home_url + "/DLive/shield/detail" + str;
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
    					//editfunc : false,
    					del : false,
    					view : false,
    					search : false,
    					refresh : false,
    					refreshicon : 'icon-refresh green',
    					beforeRefresh : function(){//刷新前触发事件
    						$("#keyword").val("");//清空关键字
    					}
    				})
    			
    	},
    	jqGridRedraw : function(){//重绘
    		var keyword = $("#keyword").val();       //获取输入框内容  
	        $("#jqGrid").jqGrid('setGridParam',{  
	            datatype:'json', 
	            postData:{'keyword':keyword} //发送数据  
	        }).trigger("reloadGrid"); //重新载入  
    	}
	}
})();


jQuery(document).ready(function() {
	DShieldList.init();//初始化
});



//扩展父行的事件处理程序接收两个参数，即网格的ID和行的主键
function showThirdLevelChildGrid(parentRowID, parentRowKey) {
	var childGridID = parentRowID + "_table";
	var childGridPagerID = parentRowID + "_pager";

	// 将父行主键发送到服务器，以便我们知道要显示哪个网格
	var childGridURL = home_url + '/DLive/shield/listData?pid=' + parentRowKey ;

	// 将表和pager HTML元素添加到父网格行——我们将在这里呈现子网格
	$('#' + parentRowID).append('<table id=' + childGridID + '></table><div id=' + childGridPagerID + ' class=scroll></div>');

	$("#" + childGridID).jqGrid({
		url: childGridURL,
		mtype: "GET",
		datatype: "json",
		colNames: ['防护类型','名称','铅当量','生产厂家','出厂日期','数量'],
		colModel: [
			{name:"shieldtypeName",index:"", width:30,editable: false,sortable : false,
				formatter : function(cellvalue, options, rowObject){
					if(cellvalue != null && cellvalue != undefined)
						return cellvalue;
					return "";
				},
				cellattr: function(rowId, tv, rawObject, cm, rdata) {
			        //合并单元格
			        return 'id=\'shieldtypeName' + rowId + "\'";
			    }
			},
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
				}}
		],
		loadonce: true,
		autowidth : true,
		height : 'auto',
		autoheight: true,
		rowNum: 999999,
		gridComplete: function() {
			Merger("#"+childGridID, 'shieldtypeName');
		}
//		pager: "#" + childGridPagerID
	});

}






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
            if (before[CellName] == end[CellName] ){//}&& before.warningmarkid == end.warningmarkid) {
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
 
