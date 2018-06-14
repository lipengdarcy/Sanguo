//场所检测
var DLiveDetetionList = (function() {
	return {
		init : function(){//初始化
		
			DLiveDetetionList.initjqGrid();
		},
    	initjqGrid : function(){
    		//table  和   页脚的id
    		var grid_selector = "#jqGrid";
    		var pager_selector = "#jqGridPager";
    		$(grid_selector).jqGrid({
				url: home_url + '/DLive/detection/listData?id='+id ,
				// we set the changes to be made at client side using predefined word clientArray
				datatype: "json",
				mtype : "post",
				//复选框
				//multiselect : true,
				
				colModel : [
					{
						label:'检测时间',
						name : "detectiontime",
						index : "detectiontime",
						editable : false,
						sortable : false,
						width:30,
						formatter : function(cellvalue,
								options, rowObject) {
							if(rowObject.detectiontime == undefined)return ""; 
    						return moment(new Date(rowObject.detectiontime)).format("YYYY-MM-DD");
						}
					},
					{
						label:'检测负责人',
						name : "inspectname",
						index : "inspectname",
						editable : false,
						sortable : false,
						width:30,
						formatter : function(cellvalue,
								options, rowObject) {
							if (cellvalue != null
									&& cellvalue != undefined)
								return cellvalue;
							return "";
						}
					},
					{
						label:'检测结果',
						name : "result",
						index : "result",
						editable : false,
						sortable : false,
						width:30,
						formatter : function(cellvalue,
								options, rowObject) {
							if (cellvalue != null
									&& cellvalue != undefined)
								return cellvalue;
							return "";
						}
					},
					{
						label:'检测记录',
						name : "files",
						index : "files",
						editable : false,
						sortable : false,
						width:30,
						formatter : function(cellvalue,
								options, rowObject) {
							var temp="";
							//console.log(rowObject);
							if (cellvalue != null && cellvalue != undefined){
							for(var i=0;i<rowObject.files.length;i++){
								temp+= '<a dataid="'
									+ rowObject.files[i].id
									+ '" target="_blank" href="'
									+ getfileUrl(rowObject.files[i].id)
									+ '">'
									+ getfileName(rowObject.files[i].id)
									+ '</a><br>';
							}
							}
						return temp;
						}
					},
					{
						label:'操作',
						name : null,
						index : null,
						editable : false,
						sortable : false,
						width:10,
						formatter : function(cellvalue,
								options, rowObject) {
							var info="";
							//暂时先注释 以防万一需求变动要改回来
							//info+='<a href="javascript:workroleview('+rowObject.id+');" class="ui-pg-div" data-rel="tooltip" data-original-title="查看" ><i class="ui-icon icon-eye-open blue"></i></a>';
							info+=' <a href="javascript:Modal('+rowObject.id+');" class="ui-pg-div" data-rel="tooltip" data-original-title="编辑"><i class="ui-icon icon-pencil green"></i></a>';
							info+=' <a href="javascript:del('+rowObject.id+');" class="ui-pg-div" data-rel="tooltip" data-original-title="删除"><i class="ui-icon icon-trash red"></i></a>';
							return info;
						}
					}
				],
				
				//sortable:'boolean',//是否可排序
				sortname: 'CustomerID',//默认的排序列。可以是列名称或者是一个数字，这个参数会被提交到后台
				sortorder : 'asc',//排序顺序，升序或者降序（asc or desc）
				loadonce: true,//如果为ture则数据只从服务器端抓取一次，之后所有操作都是在客户端执行，翻页功能会被禁用
				viewrecords: true,//是否要显示总记录数
				// 表格标题
				//caption : "角色列表",
				autowidth : true,//宽度自适应
				height : 'auto',//高度自适应
				autoheight: true,//高度自适应
				//rownumbers : true,//显示序号
				rownumWidth: 40,//序号列宽度				
				//每页显示记录
				rowNum : 10,
				rowList : [ 10, 20, 30, 40 ],
				pager: "#jqGridPager",
				loadComplete : function() {
					var table = this;
					setTimeout(function(){
						/*styleCheckbox(table);
						updateActionIcons(table);
						updatePagerIcons(table);*/
					}, 0);
				},
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
    						$("#keyword").val("");//清空关键字
    					}
    				})
    			
    	},
    	jqGridRedraw : function(){//重绘
    		var keyword = "";       //获取输入框内容  
	        $("#jqGrid").jqGrid('setGridParam',{  
	            datatype:'json',
	            postData:{'keyword':keyword} //发送数据  
	        }).trigger("reloadGrid"); //重新载入  
    	}
	}
})();

//删除
function del(id) {
	var title = '删除制度', content = '是否确认删除？';
	var myDialog = dialog({
		title : title,
		content : content,
		lock : true,
		okValue : '确定',
		ok : function() {
			$.ajax({
				url : home_url + '/DLive/detection/delete',
				data : {
					id: id ,
				},
				type : 'post',
				dataType : 'json',
				success : function(data) {
					$.jGrowl(data.message);
					DLiveDetetionList.jqGridRedraw();	
				},
				error : function() {
					$.jGrowl("异常！请重新尝试或者联系管理员！");
				}
			});
		},
		cancelValue : '取消',
		cancel : function() {
		}
	});
	myDialog.showModal();
}

//编辑
function edit(id) {
					Modal(id);
		
}

jQuery(document).ready(function() {
	DLiveDetetionList.init();//初始化
});

