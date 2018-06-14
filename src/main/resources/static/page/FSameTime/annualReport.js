var annualReport = (function() {
	return {
		init : function() {
			annualReport.initjqGrid();
		},
		jqGridRedraw : function(data) {
			$("#annualReporttb").jqGrid('setGridParam', {})
					.trigger("reloadGrid");// 重新载入
		},
		initjqGrid : function() {
			// table 和 页脚的id
			var grid_selector = "#annualReporttb";
			var pager_selector = "#jqGridPager";
			jQuery(grid_selector)
					.jqGrid(
							{
								url : home_url + '/annualReport/listData',
								datatype : "json",
								mtype : "post",
								height : 'auto',
								viewrecords : true,// 显示总记录数
								autowidth : true,// 自动匹配宽度
								rowNum : 10,// 默认每页显示
								rowList : [ 5, 10, 15, 20, 25, 30 ],// 自定义每页显示下拉选
								pager : pager_selector,// 表格数据关联的分页条，html元素
								altRows : true,// 分页选项，可以下拉选择每页显示记录数
								rownumbers : true,// 显示序号
								colModel : [
										{
											label : "年份",
											name : "year",
											index : "year",
											editable : false,
											sortable : false,
											width : 10,
											formatter : function(cellvalue,
													options, rowObject) {
												if (cellvalue != null
														&& cellvalue != undefined)
													return cellvalue;
												return "";
											}
										},
										{
											label : "生成时间",
											name : "createtime",
											index : "createtime",
											editable : false,
											sortable : false,
											width : 10,
											formatter : function(cellvalue,
													options, rowObject) {
												if (cellvalue != null
														&& cellvalue != undefined){
													return new Date(cellvalue).Format("yyyy-MM-dd hh:mm:ss");
												}
												return "";
											}
										},
										{
											label : "操作",
											name : null,
											index : null,
											editable : false,
											sortable : false,
											width : 10,
											formatter : function(cellvalue,
													options, rowObject) {
												var info ='<a href="javascript:download('
													+ rowObject.id
													+ ');" class="ui-pg-div" title="下载"><i class="ui-icon icon-download-alt green"></i></a>';
												info += ' <a href="javascript:del('
														+ rowObject.id
														+ ');" class="ui-pg-div" data-rel="tooltip" data-original-title="删除"><i class="ui-icon icon-trash red"></i></a>';
												return info;
											}
										} ],
								loadComplete : function() {// 重绘回调函数
									var table = this;
									setTimeout(function(){
										//气泡
										$('.navtable .ui-pg-button').tooltip({container:'body'});
										$(table).find('.ui-pg-div').tooltip({container:'body'});
									}, 0);
								}
							});
			// 表格下方操作
			jQuery(grid_selector).jqGrid('navGrid', pager_selector, {
				add : false,
				edit : false,
				editfunc : false,
				del : false,
				search : false,
				refresh : false,
				refreshicon : 'icon-refresh green',
				view : false
			});

		}
	}
})();

function download(id){
	$.ajax({
         type: "get",
         url: home_url+"/annualReport/downloadFile",
         data:{id:id},
         success: function(data){
        	 location.href=home_url+data.message;
         }
      });
}
function del(id){
	delConfirmDiag(function() {
	$.ajax({
        type: "post",
        url: home_url+"/annualReport/deleteFile",
        data:{id:id},
        success: function(data){
        	alert(data.message);
        	if(data.code>0){
        		annualReport.jqGridRedraw();
        	}else{
        		
        	}
       	 
        }
     });
	})
}