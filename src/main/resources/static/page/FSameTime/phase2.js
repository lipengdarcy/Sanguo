//评价阶段

$(function() {

	// 相关附件
	$("#fileList")
			.jqGrid(
					{
						url : home_url
								+ '/project/fileData?type=1&phase=2&projectId='
								+ proid,
						datatype : "json",
						editurl : home_url + '/project/updateFile',
						colModel : [
								{
									label : '文件名称',
									name : 'filename',
									width : 'auto',
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
										+ getfileUrl(rowObject.fileid)+ 
										'">'
										+ getfileName(rowObject.fileid)
												+ '</a>';
										return link;
									},
									width : 'auto',
								},
								{
									label : '操作',
									name : 'id',
									formatter : function(cellvalue, options,
											rowObject) {
										var link = '<a href="javascript:editRow('
												+ rowObject.id
												+ ');" class="ui-pg-div a-redact" data-original-title="" title="编辑"><i class="ui-icon icon-pencil green"></i></a>';
										if(!edit)
											return "";
										return link;
									},
									width : 'auto',
								} ],

						viewrecords : true,// 是否要显示总记录数
						autowidth : true,// 宽度自适应
						height : 'auto',// 高度自适应
						autoheight : true,// 高度自适应
						rownumbers : true,// 显示序号
						rownumWidth : 40
					// 序号列宽度

					});

	// 核技术应用环境影响评价公示列表
	$("#showList")
			.jqGrid(
					{
						url : home_url
								+ '/project/fileData?type=3&phase=2&projectId='
								+ proid,
						datatype : "json",
						editurl : home_url + '/project/updateFile',
						multiselect : true,																		
						colModel : [
								{
									label : '公示内容',
									name : 'filename',
									editable : true
								},
								{
									label : '公示期',
									name : 'fileno',
									editable : true
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
										+ getfileUrl(cellvalue)+ '">'
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
						rowNum : 10,
						rowList : [ 10, 20, 30 ],
						pager : "#showPager"

					});

	// 核技术应用环境影响评价公示分页
	$('#showList').navGrid(
			'#showPager',
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
				top : 200,
				left : 400,
				closeAfterEdit : true,
				recreateForm : true,
				viewPagerButtons : false,
				errorTextFormat : function(data) {
					return 'Error: ' + data.responseText
				},
				beforeShowForm : function(e) {
					var rowId = $("#showList").jqGrid('getGridParam', 'selrow');
					var rowData = $('#showList').jqGrid('getRowData', rowId);
					if(rowData.fileid){
						var d = {id: $(rowData.fileid).attr('dataid'), name: $(rowData.fileid).html(), url: $(rowData.fileid).attr('href')};
						var data = [];
						data[0] = d;
					}				
					$('#fileid').after(
							'<input id="file_upload1" type="file" multiple />');
							
					createUploadify('file_upload1', showFile, 6, '上传评价公示文件',
							'*.*',data,1);
					showEditFile('showList');
					// 公示期的日期选择
					$('#fileno').daterangepicker({
						format : 'YYYY-MM-DD',
					});
					return true;
				},
				// 删除文件后，fileid设置为-1
				serializeEditData : function(data) {
					var fileid = $("#fileid").siblings('.input-group').find('.filebox li').val();
					if(fileid==undefined)
						fileid = -1;
					return $.param($.extend({}, data, {
						fileid : fileid,
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
				top : 200,
				left : 400,
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
						filetype : '3',
						phase : '2',
						proid : proid
					}));
				},
				beforeShowForm : function(e) {
					$('#fileid').after(
							'<input id="file_upload1" type="file" multiple />')
							.after(fileShow);
					createUploadify('file_upload1', showFile, 6, '上传评价公示文件',
							'*.*',[],1);
					// 公示期的日期选择
					$('#fileno').daterangepicker({
						format : 'YYYY-MM-DD',
					});
					return true;
				}
			}, {
				// 删除表单
				top : 200,
				left : 400,
				closeAfterEdit : true,
				beforeSubmit:function(postdata) {
					var array = postdata.split(',');
					for(var i=0; i<array.length; i++){
						var curRowData = $('#showList').jqGrid('getRowData', array[i]);
						if(curRowData.filename=='项目公示' || curRowData.filename=='环境影响评价报告评前公示' || 
								curRowData.filename=='环境影响评价报告评审公示' || curRowData.filename=='环境影响评价报告批复公示')
							return [false, '前4条数据是默认的附件，不允许删除' ];
					}
					return [true, '' ];					
				},
				// 批量删除默认的多个参数由id变为idlist
				serializeDelData : function(data) {
					return $.param($.extend({}, data, {
						oper : 'batchdel',
						idlist : data.id,
						id : ''
					}));
				}

			});
	
	// 预评价审批列表
	$("#preList")
			.jqGrid(
					{
						url : home_url
								+ '/project/fileData?type=2&phase=2&projectId='
								+ proid,
						datatype : "json",
						editurl : home_url + '/project/updateFile',
						multiselect : true,
						colModel : [
								{
									label : '批复名称',
									name : 'filename',
									editable : true,
									width : 60,
								},
								{
									label : '文件号',
									name : 'fileno',
									editable : true,
									width : 70,
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
									},
									width : 70,
								},
								{
									label : '附件',
									name : 'fileid',
									formatter : function(cellvalue, options,
											rowObject) {

										if (cellvalue == undefined
												)
											return "";
										var link = '<a target="_blank" dataid=' +rowObject.fileid + ' href="'
										+ getfileUrl(cellvalue)+ '">'
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
						rowNum : 10,
						rowList : [ 10, 20, 30 ],
						pager : "#prePager"

					});

	// 预评价审批分页
	$('#preList').navGrid(
			'#prePager',
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
				top : 200,
				left : 400,
				closeAfterEdit : true,
				recreateForm : true,
				viewPagerButtons : false,
				errorTextFormat : function(data) {
					return 'Error: ' + data.responseText
				},				
				beforeShowForm : function(e) {
					var rowId = $("#preList").jqGrid('getGridParam', 'selrow');
					var rowData = $('#preList').jqGrid('getRowData', rowId);
					if(rowData.fileid){
						var d = {id: $(rowData.fileid).attr('dataid'), name: $(rowData.fileid).html(), url: $(rowData.fileid).attr('href')};
						var data = [];
						data[0] = d;
					}					
					$('#fileid').after(
							'<input id="file_upload1" type="file" multiple />');							
					createUploadify('file_upload1', showFile, 6, '上传预评价审批文件',
							'*.*', data,1);
					showEditFile('preList');
					return true;
				},
				// 删除文件后，fileid设置为-1
				serializeEditData : function(data) {
					var fileid = $("#fileid").siblings('.input-group').find('.filebox li').val();
					if(fileid==undefined)
						fileid = -1;
					return $.param($.extend({}, data, {
						fileid : fileid,
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
				top : 200,
				left : 400,
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
						filetype : '2',
						phase : '2',
						proid : proid
					}));
				},
				beforeShowForm : function(e) {
					$('#fileid').after(
							'<input id="file_upload1" type="file" multiple />');
					createUploadify('file_upload1', showFile, 6, '上传预评价审批文件',
							'*.*',[],1);
					return true;
				}
			}, {
				// 删除表单
				top : 200,
				left : 400,
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
	createUploadify('file_upload', showFile, 6, '上传相关附件', '*.*', data, 1);
	var myDialog = dialog({
		title : '附件编辑',
		content : $('#dialog-file'),
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
