//场所检测
var DLiveDetetionDetail = (function() {
	return {
		init : function(){//初始化
		
			DLiveDetetionDetail.initjqGrid();
		},
    	initjqGrid : function(){
    		//table  和   页脚的id
    		var grid_selector = "#jqGrid1";
    		var pager_selector = "#jqGridPager1";
    		$(grid_selector).jqGrid({
				url: home_url + '/DLive/detection/detail?id='+id+'&type='+type,
				// we set the changes to be made at client side using predefined word clientArray
				editurl: 'clientArray',
				datatype: "json",
				mtype : "post",
				//复选框
				//multiselect : true,
				colNames:[ '类别',"防护用品名称", '检测结果'],
				colModel: [
						{name:"shielddtype",index:"", editable: true,sortable : false,
						formatter : function(cellvalue, options, rowObject){
							return getshieldtype(cellvalue);
								
						}},
						{name:"shielddname",index:"", editable: true,sortable : false,
						formatter : function(cellvalue, options, rowObject){
							if(rowObject.shielddname != null && rowObject.shielddname != undefined)
								return rowObject.shielddname;
							return "";
						}},
						{name:"result",index:"result", editable: true,sortable : false,
						formatter : function(cellvalue, options, rowObject){
							var str = '<select pid="' + rowObject.id + '" class="results">';
							if(cellvalue == '合格'){
								str += '<option selected value="合格">合格</option>'+
									'<option  value="不合格">不合格</option>'+
									'<option  value="本次未检测">本次未检测</option>';
							}
							if(cellvalue == '不合格'){
								str += '<option  value="合格">合格</option>'+
									'<option selected value="不合格">不合格</option>'+
									'<option  value="本次未检测">本次未检测</option>';
							}
							if(cellvalue == '本次未检测'){
								str += '<option  value="合格">合格</option>'+
									'<option value="不合格">不合格</option>'+
									'<option selected  value="本次未检测">本次未检测</option>';
							}
								
							str += '</select>';
							return str;
						
						return "";
							}
						},
											
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
				pager: pager_selector,
				loadComplete : function() {
					var table = this;
					setTimeout(function(){
						/*styleCheckbox(table);
						updateActionIcons(table);
						updatePagerIcons(table);*/
						enableTooltips(table);
					}, 0);
				},
			});

			//气泡
			function enableTooltips(table) {
				$('.navtable .ui-pg-button').tooltip({container:'body'});
				$(table).find('.ui-pg-div').tooltip({container:'body'});
			}
    		
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
	}
})();

//项目阶段转义
function getshieldtype(shieldtypeId) {
	if (shieldtypeId === undefined)
		return '';

	var typeName = '';
	switch (shieldtypeId) {
	case 1:
		typeName = '工作人员个人防护用品';
		break;
	case 2:
		typeName = '工作人员辅助防护设施';
		break;
	case 3:
		typeName = '患者和受检者个人防护用品';
		break;
	case 4:
		typeName = '患者和受检者辅助防护设施';
		break;
	case 5:
		typeName = '陪检者';
		break;
	}
	return typeName;
}

//修改检测结果
$("#jqGrid1").on("change", ".results", function(){
	var id = $(this).attr("pid");
	var result = $(this).val();
	$.post(home_url + "/DLive/detection/addchild" , {'id' : id, 'result' : result}, function(data,status,xhr){
		if(type!=0){
			$.jGrowl(data.message);
		}
	},"json");
});

//人员单选
function selectStaff1() {
	SelectStaff_Single(function(data) {
		$("#staffList1 li").remove();
		$("#inspector").val("");
		if(data.id == undefined || data.id == ""){
			return;
		}
		$("#staffList1").append(dataNodeSelected(data.id, data.name));
		//保存 人员id
		$("#inspector").val(data.id);
		$("#inspector").focus().blur()//光标切入移除
	});
}

jQuery(document).ready(function() {
	DLiveDetetionDetail.init();//初始化
});

