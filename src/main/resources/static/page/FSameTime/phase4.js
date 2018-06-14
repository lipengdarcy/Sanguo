//4.验收阶段
$(function() {

	// 射线装置
	$("#rayList").jqGrid({
		data : rayListData,
		datatype : "local",
		editurl : 'clientArray',
		multiselect : true,
		colNames : [ '装置名称', '型号', '编号', '主要参数', '所在场所' ],
		colModel : [ {
			name : "deviceName",
			index : "deviceName",
			width: 'auto',
			editable : false,
			sortable : false,
			formatter : function(cellvalue, options, rowObject) {
				if (cellvalue != null && cellvalue != undefined)
					return cellvalue;
				return "";
			}
		}, {
			name : "unitType",
			index : "unitType",
			editable : false,
			sortable : false,
			formatter : function(cellvalue, options, rowObject) {
				if (cellvalue != null && cellvalue != undefined)
					return cellvalue;
				return "";
			}
		}, {
			name : "code",
			index : "code",
			editable : false,
			sortable : false,
			formatter : function(cellvalue, options, rowObject) {
				if (cellvalue != null && cellvalue != undefined)
					return cellvalue;
				return "";
			}
		}, {
			name : "exportKeyParameter",
			index : "exportKeyParameter",
			editable : false,
			sortable : false,
			formatter : function(cellvalue, options, rowObject) {
				if (cellvalue != null && cellvalue != undefined)
					return cellvalue;
				return "";
			}
		}, {
			name : "areaName",
			index : "areaName",
			editable : false,
			sortable : false,
			formatter : function(cellvalue, options, rowObject) {
				if (cellvalue != null && cellvalue != undefined)
					return cellvalue;
				return "";
			}
		} ],

		viewrecords : true,// 是否要显示总记录数
		autowidth : true,// 宽度自适应
		height : 'auto',// 高度自适应
		autoheight : true,// 高度自适应
		rownumbers : true,// 显示序号
		rownumWidth : 40,// 序号列宽度
		pgbuttons : false,//是否显示翻页按钮
		pginput : false,//是否显示跳转页面的输入框
		pager : "#rayPager"

	});

	// 分页1
	$('#rayList').navGrid('#rayPager', {
		edit : false,
		add : false,
		del : false,
		search : false,
		refresh : false,
		view : false,
		position : "left",
		cloneToTop : false
	}, {
		// 删除表单
		closeAfterEdit : true
	});
	//多项删除
	$("#rayPageron").find(".a-remove").click(function() {
		//获取选中id;
		var ids=$('#rayList').jqGrid('getGridParam','selarrrow');
		var str = "";
		var len = ids.length;//ids的长度
		for(var i = 0; i < ids.length ; i++){//格式化参数
			str += "ids="+ids[i];
			if(ids.length - 1 > i){
				str += "&";
			}
		}
		if(ids != null  && $.trim(ids) != ""){
			delConfirmDiag(function(){
				//删除
				for(var i=0;i<len;i++)
				{
				    $("#rayList").jqGrid('delRowData',ids[0]);
				}
			},"删除","");
			
		}else{
			alertDiag("请选择需要删除项!");
		}
	});

	// 非密封放射源
	$("#unsealedList").jqGrid({
		data : unsealedListData,
		datatype : "local",
		editurl : 'clientArray',
		// 复选框
		multiselect : true,

		colNames : [ '核素名称', '用途', '物理状态', '最大等效日操作量', '最大等效年操作量', '所在场所' ],
		colModel : [ {
			name : "nuclideName",
			index : "nuclideName",
			width: 'auto',
			editable : false,
			sortable : false,
			formatter : function(cellvalue, options, rowObject) {
				if (cellvalue != null && cellvalue != undefined)
					return cellvalue;
				return "";
			}
		}, {
			name : "purpose",
			index : "purpose",
			editable : false,
			sortable : false,
			formatter : function(cellvalue, options, rowObject) {
				if (cellvalue == 1)
					return "影像诊断";
				if (cellvalue == 2)
					return "核素治疗";
				if (cellvalue == 3)
					return "放射免疫分析";
				if (cellvalue == 4)
					return "核素检查 ";
				if (cellvalue == 5)
					return "同位素试验";
				return cellvalue;
			}
		},{
			name : "physicalState",
			index : "physicalState",
			editable : false,
			sortable : false,
			formatter : function(cellvalue, options, rowObject) {
				if (cellvalue == undefined)
					return "";
				if (cellvalue == 1)
					return "固态";
				if (cellvalue == 2)
					return "液态";
				if (cellvalue == 3)
					return "气态";
				return cellvalue;
			}
		},{
			name : "exportDayBq",
			index : "exportDayBq",
			editable : false,
			sortable : false,
			formatter : function(cellvalue, options, rowObject) {
				if (cellvalue != null && cellvalue != undefined)
					return cellvalue;
				return "";
			}
		}, {
			name : "exportyearBq",
			index : "exportyearBq",
			editable : false,
			sortable : false,
			formatter : function(cellvalue, options, rowObject) {
				if (cellvalue != null && cellvalue != undefined)
					return cellvalue;
				return "";
			}
		}, {
			name : "areaName",
			index : "areaName",
			editable : false,
			sortable : false,
			formatter : function(cellvalue, options, rowObject) {
				if (cellvalue != null && cellvalue != undefined)
					return cellvalue;
				return "";
			}
		} ],

		// sortable:true,//是否可排序
		sortname : 'CustomerID',// 默认的排序列。可以是列名称或者是一个数字，这个参数会被提交到后台
		sortorder : 'asc',// 排序顺序，升序或者降序（asc or desc）
		loadonce : true,// 如果为ture则数据只从服务器端抓取一次，之后所有操作都是在客户端执行，翻页功能会被禁用
		viewrecords : true,// 是否要显示总记录数
		// 表格标题
		// caption : "角色列表",
		autowidth : true,// 宽度自适应
		height : 'auto',// 高度自适应
		autoheight : true,// 高度自适应
		rownumbers : true,// 显示序号
		rownumWidth : 40,// 序号列宽度
		pgbuttons : false,//是否显示翻页按钮
		pginput : false,//是否显示跳转页面的输入框
		pager : '#unsealedPager'

	});

	// 分页2
	$('#unsealedList').navGrid('#unsealedPager', {
		edit : false,
		add : false,
		del : false,
		search : false,
		refresh : false,
		view : false,
		position : "left",
		cloneToTop : false
	}, {
		// 删除表单
		closeAfterEdit : true
	});
	//多项删除
	$("#unsealedPageron").find(".a-remove").click(function() {
		//获取选中id;
		var ids=$('#unsealedList').jqGrid('getGridParam','selarrrow');
		var str = "";
		var len = ids.length;//ids的长度
		for(var i = 0; i < ids.length ; i++){//格式化参数
			str += "ids="+ids[i];
			if(ids.length - 1 > i){
				str += "&";
			}
		}
		if(ids != null  && $.trim(ids) != ""){
			delConfirmDiag(function(){
				//删除
				for(var i=0;i<len;i++)
				{
				    $("#unsealedList").jqGrid('delRowData',ids[0]);
				}
			},"删除","");
			
		}else{
			alertDiag("请选择需要删除项!");
		}
	});

	// 密封放射源
	$("#sealedList").jqGrid({
		data : sealedListData,
		datatype : "local",
		editurl : 'clientArray',
		// 复选框
		multiselect : true,

		colNames : [ '核素名称', '活度', '测量日期', '所在场所' ],
		colModel : [ {
			name : "nuclideName",
			index : "nuclideName",
			width: 'auto',
			editable : false,
			sortable : false,
			formatter : function(cellvalue, options, rowObject) {
				if (cellvalue != null && cellvalue != undefined)
					return cellvalue;
				return "";
			}
		}, {
			name : "exportActivity",
			index : "exportActivity",
			editable : false,
			sortable : false,
			formatter : function(cellvalue, options, rowObject) {
				if (cellvalue != null && cellvalue != undefined)
					return cellvalue;
				return "";
			}
		}, {
			name : "measureTime",
			index : "measureTime",
			editable : false,
			sortable : false,
			formatter : function(cellvalue, options, rowObject) {
				var a = moment(new Date(cellvalue)).format("YYYY-MM-DD");
				if (cellvalue == undefined)
					a = cellvalue;
				if (cellvalue != null && cellvalue != undefined)
					return a;
				return "";
			}
		}, {
			name : "areaName",
			index : "areaName",
			editable : false,
			sortable : false,
			formatter : function(cellvalue, options, rowObject) {
				if (cellvalue != null && cellvalue != undefined)
					return cellvalue;
				return "";
			}
		} ],

		// sortable:true,//是否可排序
		sortname : 'CustomerID',// 默认的排序列。可以是列名称或者是一个数字，这个参数会被提交到后台
		sortorder : 'asc',// 排序顺序，升序或者降序（asc or desc）
		loadonce : true,// 如果为ture则数据只从服务器端抓取一次，之后所有操作都是在客户端执行，翻页功能会被禁用
		viewrecords : true,// 是否要显示总记录数
		// 表格标题
		// caption : "角色列表",
		autowidth : true,// 宽度自适应
		height : 'auto',// 高度自适应
		autoheight : true,// 高度自适应
		rownumbers : true,// 显示序号
		rownumWidth : 40,// 序号列宽度
		pgbuttons : false,//是否显示翻页按钮
		pginput : false,//是否显示跳转页面的输入框
		pager : '#sealedPager'

	});

	// 分页3
	$('#sealedList').navGrid('#sealedPager', {
		edit : false,
		add : false,
		del : false,
		search : false,
		refresh : false,
		view : false,
		position : "left",
		cloneToTop : false
	}, {
		// 删除表单
		closeAfterEdit : true
	});
	//多项删除
	$("#sealedPageron").find(".a-remove").click(function() {
		//获取选中id;
		var ids=$('#sealedList').jqGrid('getGridParam','selarrrow');
		var str = "";
		var len = ids.length;//ids的长度
		for(var i = 0; i < ids.length ; i++){//格式化参数
			str += "ids="+ids[i];
			if(ids.length - 1 > i){
				str += "&";
			}
		}
		if(ids != null  && $.trim(ids) != ""){
			delConfirmDiag(function(){
				//删除
				for(var i=0;i<len;i++)
				{
				    $("#sealedList").jqGrid('delRowData',ids[0]);
				}
			},"删除","");
			
		}else{
			alertDiag("请选择需要删除项!");
		}
	});

	// 含源装置
	$("#containerList").jqGrid({
		data : containerListData,
		datatype : "local",
		editurl : 'clientArray',
		// 复选框
		multiselect : true,
		colNames : [ '装置名称', '型号', '编号', '生产厂家' ],
		colModel : [ {
			name : "deviceName",
			index : "deviceName",
			width: 'auto',
			editable : false,
			sortable : false,
			formatter : function(cellvalue, options, rowObject) {
				if (cellvalue != null && cellvalue != undefined)
					return cellvalue;
				return "";
			}
		}, {
			name : "type",
			index : "type",
			editable : false,
			sortable : false,
			formatter : function(cellvalue, options, rowObject) {
				if (cellvalue != null && cellvalue != undefined)
					return cellvalue;
				return "";
			}
		}, {
			name : "code",
			index : "code",
			editable : false,
			sortable : false,
			formatter : function(cellvalue, options, rowObject) {
				if (cellvalue != null && cellvalue != undefined)
					return cellvalue;
				return "";
			}
		}, {
			name : "deviceManufacturersName",
			index : "deviceManufacturersName",
			editable : false,
			sortable : false,
			formatter : function(cellvalue, options, rowObject) {
				if (cellvalue != null && cellvalue != undefined)
					return cellvalue;
				return "";
			}
		} ],

		// sortable:true,//是否可排序
		sortname : 'CustomerID',// 默认的排序列。可以是列名称或者是一个数字，这个参数会被提交到后台
		sortorder : 'asc',// 排序顺序，升序或者降序（asc or desc）
		loadonce : true,// 如果为ture则数据只从服务器端抓取一次，之后所有操作都是在客户端执行，翻页功能会被禁用
		viewrecords : true,// 是否要显示总记录数
		// 表格标题
		// caption : "角色列表",
		autowidth : true,// 宽度自适应
		height : 'auto',// 高度自适应
		autoheight : true,// 高度自适应
		rownumbers : true,// 显示序号
		rownumWidth : 40,// 序号列宽度
		pgbuttons : false,//是否显示翻页按钮
		pginput : false,//是否显示跳转页面的输入框
		pager : "#containerPager"
	});

	// 分页4
	$('#containerList').navGrid('#containerPager', {
		edit : false,
		add : false,
		del : false,
		search : false,
		refresh : false,
		view : false,
		position : "left",
		cloneToTop : false
	}, {
		// 删除表单
		closeAfterEdit : true
	});
	//多项删除
	$("#containerPageron").find(".a-remove").click(function() {
		//获取选中id;
		var ids=$('#containerList').jqGrid('getGridParam','selarrrow');
		var str = "";
		var len = ids.length;//ids的长度
		for(var i = 0; i < ids.length ; i++){//格式化参数
			str += "ids="+ids[i];
			if(ids.length - 1 > i){
				str += "&";
			}
		}
		if(ids != null  && $.trim(ids) != ""){
			delConfirmDiag(function(){
				//删除
				for(var i=0;i<len;i++)
				{
				    $("#containerList").jqGrid('delRowData',ids[0]);
				}
			},"删除","");
			
		}else{
			alertDiag("请选择需要删除项!");
		}
	});

	// 相关附件
	$("#fileList")
			.jqGrid(
					{
						url : home_url
								+ '/project/fileData?type=1&patchid='+patchid+'&phase=4&projectId='
								+ proid,
						datatype : "json",
						editurl : home_url + '/project/updateFile',
						colModel : [
								{
									label : '文件名称',
									name : 'filename',
									editable : true,
									width: 'auto'
								},
								{
									label : '附件',
									name : 'fileid',
									formatter : function(cellvalue, options,
											rowObject) {
										if (cellvalue == undefined
												)
											return "";
										var link = '<a dataid="'+cellvalue+'" target="_blank" href="'
										+ getfileUrl(cellvalue)+ 
										'">'
										+ getfileName(cellvalue)
												+ '</a>';
										return link;
									}
								
								},{
									label : '操作',
									name : 'id',
									formatter : function(cellvalue, options,
											rowObject) {
										if(!edit)
											return "";
										var link = '<a href="javascript:editRow('
												+ rowObject.id
												+ ');" class="ui-pg-div a-redact" data-original-title="" title="编辑"><i class="ui-icon icon-pencil green"></i></a>';
										return link;
									}
								}  ],

						viewrecords : true,// 是否要显示总记录数
						autowidth : true,// 宽度自适应
						height : 'auto',// 高度自适应
						autoheight : true,// 高度自适应
						rownumbers : true,// 显示序号
						rownumWidth : 40// 序号列宽度
						
					});


	// 批复信息
	$("#approveList")
			.jqGrid(
					{
						url : home_url
								+ '/project/fileData?type=2&patchid='+patchid+'&phase=4&projectId='
								+ proid,
						datatype : "json",
						editurl : home_url + '/project/updateFile',
						// 复选框
						multiselect : true,
						colModel : [
								{
									label : '名称',
									name : 'filename',
									editable : true,
									width: 'auto'
								},
								{
									label : '文件号',
									name : 'fileno',
									editable : true
								},
								{
									label : '批复时间',
									name : 'approvetime',
									formatter : function(cellvalue, options,
											rowObject) {
										if (cellvalue == undefined)
											return "";
										return moment(new Date(cellvalue))
												.format("YYYY-MM-DD");
									},
									editable : true,
									editoptions : { // 单一时间
										dataInit : function(element) {
											$(element).datepicker({
												autoclose : true,
												format : 'yyyy-mm-dd',
												orientation : 'bottom'
											});
										}
									}
								},
								{
									label : '附件',
									name : 'fileid',
									formatter : function(cellvalue, options,
											rowObject) {

										if (cellvalue == undefined
												)
											return "";
										var link = '<a dataid="'+cellvalue+'" target="_blank" href="'
										+ getfileUrl(cellvalue)+ 
										'">'
										+ getfileName(cellvalue)
												+ '</a>';
										return link;
									},
									editable : true,
									edittype : "custom",
									editoptions : {
										custom_value : getElementValue,
										custom_element : createEditElement
									},
									width : 70,
								} ],
						viewrecords : true,// 是否要显示总记录数
						autowidth : true,// 宽度自适应
						height : 'auto',// 高度自适应
						autoheight : true,// 高度自适应
						rownumbers : true,// 显示序号
						rownumWidth : 40,// 序号列宽度
						// 每页显示记录
						rowNum : 10,
						rowList : [ 10, 20, 30 ],
						pager : "#approvePager"

					});

	// 批复分页
	$('#approveList').navGrid(
			'#approvePager',
			{
				edit : edit,
				add : edit,
				del : edit,
				search : false,
				refresh : false,
				view : false,
				position : "left",
				cloneToTop : false
			},
			{
				// 编辑
				top : 200,  //位置
				left: 400, //位置
				closeAfterEdit : true,
				recreateForm : true,
				viewPagerButtons : false,
				errorTextFormat : function(data) {
					return 'Error: ' + data.responseText
				},
				beforeShowForm : function(e) {
					var rowId = $("#approveList").jqGrid('getGridParam', 'selrow');
					var rowData = $('#approveList').jqGrid('getRowData', rowId);
					if(rowData.fileid){
						var d = {id: $(rowData.fileid).attr('dataid'), name: $(rowData.fileid).html(), url: $(rowData.fileid).attr('href')};
						var data = [];
						data[0] = d;
					}		
					$('#fileid').after(
							'<input id="file_upload1" type="file" multiple />')
							.after(fileShow);
					createUploadify('file_upload1', showFile, 6, '上传审批文件', '*.*',data,1);
					return true;
				},
				// 删除文件后，fileid设置为-1
				serializeEditData : function(data) {
					var fileid = $("#fileid").siblings('.input-group').find('.filebox li').val();
					if(fileid==undefined)
						fileid = -1;
					return $.param($.extend({}, data, {
						fileid : fileid,
						phase : '4',
						filetype : '2',
						patchid : patchid,
						proid : proid
					}));
				},
				afterSubmit : function(response, postdata) {
					var data = $.parseJSON(response.responseText);
					$.jGrowl(data.message, {
						header : "提醒"
					});
					return false;
				}
			},
			{
				// 新增表单
				 top : 200,  //位置
				 left: 400, //位置
				 //height:480, //大小
				 //width:750, //大小
				closeAfterAdd : true,
				recreateForm : true,
				viewPagerButtons : false,
				errorTextFormat : function(data) {
					return 'Error: ' + data.responseText
				},
				// 默认的id由_empty变为空
				serializeEditData : function(data) {
					return $.param($.extend({}, data, {
						id : '',
						phase : '4',
						filetype : '2',
						patchid : patchid,
						proid : proid
					}));
				},
				beforeShowForm : function(e) {
					$('#fileid').after(
							'<input id="file_upload1" type="file" multiple />')
							.after(fileShow);
					createUploadify('file_upload1', showFile, 6, '上传审批文件', '*.*',[],1);
					return true;
				}
			}, {
				// 删除表单
				closeAfterEdit : true,
				// 批量删除默认的多个参数由id变为idlist
				serializeDelData : function(data) {
					return $.param($.extend({}, data, {
						oper : 'batchdel',
						idlist : data.id,
						id : ''
					}));
				}
				

			});

});

//从array中删除removeItems，removeItems和array都是数组
function removeFromArray(array, removeItems){
	var result = [];
	var index = 0;
	for(var i=0; i<array.length; i++){
		var flag = true;
		for(var j=0; j<removeItems.length; j++){
			if(array[i]==removeItems[j])
				flag = false;
		}
		if(flag)
			result[index++]=array[i];
	}
	return result;
}


// 设备多选(不是全部设备)
function getDevice_filtered() {
	
	var rayList = $("#rayList").jqGrid('getDataIDs');
	var unsealedList = $("#unsealedList").jqGrid('getDataIDs');
	var sealedList = $("#sealedList").jqGrid('getDataIDs');
	var containerList = $("#containerList").jqGrid('getDataIDs');

	array1 = removeFromArray(array1, rayList);
	array2 = removeFromArray(array2, unsealedList);
	array3 = removeFromArray(array3, sealedList);
	array4 = removeFromArray(array4, containerList);
	


	EDevice_Project(
			[ 'rayList', 'unsealedList', 'sealedList', 'containerList' ],
			array1, array2, array3, array4);
}

function editRow(id){
	var rowData = $('#fileList').jqGrid('getRowData', id);
	$('#attach_name').html(rowData.filename);	
	if(rowData.fileid){
		var url = $(rowData.fileid).attr('href');
		//url = url.substr(home_url.length);
		var d = {id: $(rowData.fileid).attr('dataid'), name: $(rowData.fileid).html(), url: url};
		var data = [];
		data[0] = d;
	}
	$("#dialog-file").find('.filebox').remove();
	createUploadify('file_upload', showFile, 6, '上传相关附件', '*.*', data,1);
	//上传相关附件后的回调显示
	if(rowData.fileid !=''){
		$("#file_name").replaceWith(rowData.fileid);
		//$("#file_name").html(rowData.file.filename);
		//$("#file_name").attr("href", home_url + "/upload/" + rowData.file.ossurl);
	}
	var myDialog = dialog({
		title : '附件编辑',
		content: $('#dialog-file'),
		width : 710,
		height : 'auto',
		okValue : '确定',
		ok : function() {
			var fileid = $("#dialog-file").find('.filebox li').val();
			if(fileid==undefined)
				fileid = -1;
			$.ajax({
				url : home_url + '/project/updateFile',
				data : {
					'id' : id,
					'oper' : 'edit',
					'filename': rowData.filename,
					'fileid' : fileid
				},
				type : 'post',
				dataType : 'json',
				success : function(data) {
					$.jGrowl(data.message);					
					$("#fileList").jqGrid().trigger("reloadGrid");
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
