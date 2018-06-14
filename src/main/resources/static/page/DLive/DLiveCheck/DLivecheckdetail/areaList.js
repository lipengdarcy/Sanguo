//机房区域信息
var areaList = (function(){
	return {
		init : function(){
			
			//新增
			$("#id-cerxz-dialog").click(function(){
				//获取已经被选中的区域的id
				var arr = new  Array();
				
				var data = areaList.grid_data;//表数据源
				for(i = 0; i < data.length; i++){
					arr.push(data[i].areaid);
				}
				SelectRoom_Multi(function(data){
					var checkid = $("#DLivecheckForm").find("#id").val(); //现场检测id
					if(data != null && data !=undefined && data.length != 0){
						var str = "";
						for(i = 0; i < data.length; i++){//获取选中数据的id并且序列化
							str += "ids="+data[i].id;
							if(data.length - 1 > i){
								str += "&";
							}
						}
						str += "&checkid=" + checkid;
						str += "&result=合格";//默认为合格
						str += "&isvalid=1";//状态 0.已删除 1.区域    2.射线装置  3.含源装置
						str += "&module=" + module;//区分入口
						str += "&detectiontime=" + $("#detectiontime").val();
						//ajax新增
						$.post(home_url + '/DLive/livecheckdetail/add', str, function(data, status){
							if(status == "success"  && data.code == 0){
								areaList.initData();//重新加载表格
							}else{
								alertDiag("操作失败! ");
							}
						},"json");
					}
				},arr);
			});
			
			//批量删除
			$( "#areaList" ).on('click','.a-delete', function(e) {
				e.preventDefault();
				//获取选中id;
				var ids=$('#areaTable').jqGrid('getGridParam','selarrrow');
				var str = "";
				for(var i = 0; i < ids.length ; i++){//格式化参数
					str += "ids="+ids[i];
					if(ids.length - 1 > i){
						str += "&";
					}
				}
				if(ids != null  && $.trim(ids) != ""){
					delConfirmDiag(function(){
						$.post(home_url + "/DLive/livecheckdetail/delete",str,function(data,status,xhr){
							if(status == "success"  && data.code =="0"){
								//重绘
								areaList.initData();
							}else{
								alertDiag(data.message);
							}
						},"json");
					},"删除","");
					
				}else{
					alertDiag("请选择需要删除的机房区域信息!");
				}
			});
			
			//修改检测结果
			$("#areaTable").on("change", ".result", function(){
				var id = $(this).attr("pid");
				var result = $(this).val();
				$.post(home_url + "/DLive/livecheckdetail/add", {'id' : id, 'result' : result}, function(data,status,xhr){
					if(status == "success"  && data.code == 0){
						$.jGrowl("修改成功");
						//rayDeviceList.initData();//重新加载表格
					}else{
						alertDiag("操作失败! ");
					}
				},"json");
			});
			
			
			areaList.initjqGrid();
			areaList.initData();
		},//存取jqGrid的数据源
		grid_data : [],
		initData : function(){//初始化jqGrid 的数据
			var data = {checkId : $("#id").val(), isvalid : 1};//isvalid  1.区域    2.射线装置  3.含源装置
			$.get(home_url + "/DLive/livecheckdetail/listData" , data, function(data,status,xhr){
				if(status == "success"  && data.code == 0){
					areaList.grid_data = data.data;
					$("#areaTable").jqGrid('clearGridData');  //清空表格
					$("#areaTable").jqGrid('setGridParam',{  // 重新加载数据
					      datatype:'local',
					      data : areaList.grid_data,   // 符合格式要求的需要重新加载的数据 
					}).trigger("reloadGrid");
				}
			},"json");
			
		},
		initjqGrid : function(){

    		//table  和   页脚的id
    		var grid_selector = "#areaTable";
    		var grid_jqGridPager = "#area-jqGridPager";
    		
    		jQuery(grid_selector).jqGrid({
    			data : areaList.grid_data,
    			editurl: 'clientArray',
    			datatype : "local",
    			height : 'auto',
    			multiselect : true,//生成多选列
    			multiboxonly : true,//全选
    			autowidth : true,//宽度自适应
    			altRows : true,//分页选项，可以下拉选择每页显示记录数 
    			viewrecords : true,//显示总记录数 
				autoheight: true,//高度自适应
				rownumbers : true,//显示序号
				rownumWidth: 40,//序号列宽度				
				//每页显示记录
				rowNum : 10,
				rowList : [ 10, 20, 30 ],
				pager: grid_jqGridPager,
    			colNames:[ '机房或区域',"楼层",'建筑物', '检测结果', '操作'],
    			colModel : [ 
    			    {name:"areaName",index:"areaName", editable: true, sortable : false,
    					formatter : function(cellvalue, options, rowObject){
	    					if(cellvalue != null && cellvalue != undefined)
								return   cellvalue;
							return "";
    					}
    				},
					{name:'floor',index:'floor', editable: true,editoptions:{readonly: 'readonly'},sortable : true,
    					formatter : function(cellvalue, options, rowObject){
    						if(cellvalue != null && cellvalue != undefined)
								return cellvalue;
							return "";
    				}},
    				{name:"construction",index:"construction", editable: true,editoptions:{readonly: 'readonly'},sortable : false,
						formatter : function(cellvalue, options, rowObject){
	    					if(cellvalue != null && cellvalue != undefined)
								return cellvalue;
							return "";
    				}},
    				{name:"result",index:"result", editable: true,sortable : false,
						formatter : function(cellvalue, options, rowObject){
							if(cellvalue != null && cellvalue != undefined){

								var str = '<select pid="' + rowObject.id + '" class="result">';
								if(cellvalue == '合格'){
									str += '<option selected value="合格">合格</option>'+
										'<option  value="有不符合项">有不符合项</option>';
								}
								if(cellvalue == '有不符合项'){
									str += '<option  value="合格">合格</option>'+
										'<option selected value="有不符合项">有不符合项</option>';
								}
									
								str += '</select>';
								return str;
							
							}
							return "";
						}
    				},
    				{name:null,index:null, editable: false,sortable : false,
						formatter : function(cellvalue, options, rowObject){
							var info = '<a href="javascript:;" class="ui-pg-div a-remove" data-original-title="" dataId='+ rowObject.id +' title="删除"><i class="ui-icon icon-trash red"></i></a>';
							return info;
    				}}
    			],
    			loadComplete : function() {//重绘回调函数
    				//删除
    				$("#areaTable").find(".a-remove").click(function(){
    					var $td = $(this);
    					var data = {
    							ids : $(this).attr('dataId')
    							};
    					delConfirmDiag(function(){
    						var url = home_url + '/DLive/livecheckdetail/delete';
    						$.post(url, data, function(data, status){
    							if(status == "success"  && data.code == 0){
    								areaList.initData();//重新加载表格
    							}else{
    								alertDiag("操作失败! ");
    							}
    						},"json");
    					},"删除所选记录","");
    				});
    				
    			}
    		});
    		
    		
		}
	};
})();

var module = "";
jQuery(document).ready(function() {
	module = $("#module").val(); //区分模块入口 1-放射现场监测，2，放射设备监测 3辐射安全监测
	areaList.init();
});



