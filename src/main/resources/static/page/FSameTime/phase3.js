//施工阶段

$(function() {

	// 施工图纸列表
	$("#mapList")
			.jqGrid(
					{
						url : home_url
								+ '/project/fileData?type=1&phase=3&projectId='
								+ proid,
						datatype : "json",
						editurl : home_url + '/project/updateFile',
						multiselect : true,
						colModel : [
								{
									label : '图纸名称',
									name : 'filename',
									editable : true,
									width: 'auto'
								},
								{
									label : '图纸编号',
									name : 'fileno',
									editable : true,
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
										var link = '<a dataid="'+cellvalue+'" target="_blank" href="'
												+ getfileUrl(rowObject.fileid)+ 
												'">'
												+ getfileName(rowObject.fileid)
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
						pager : "#mapPager"

					});

	// 施工图纸分页
	$('#mapList').navGrid(
			'#mapPager',
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
				closeAfterEdit : true,
				recreateForm : true,
				viewPagerButtons : false,
				errorTextFormat : function(data) {
					return 'Error: ' + data.responseText
				},
				beforeShowForm : function(e) {
					var rowId = $("#mapList").jqGrid('getGridParam', 'selrow');
					var rowData = $('#mapList').jqGrid('getRowData', rowId);
					if(rowData.fileid){
						var d = {id: $(rowData.fileid).attr('dataid'), name: $(rowData.fileid).html(), url: $(rowData.fileid).attr('href')};
						var data = [];
						data[0] = d;
					}					
					$('#fileid').after(
							'<input id="file_upload1" type="file" multiple />');							
					createUploadify('file_upload1', showFile, 6, '上传施工图纸', '*.*', data,1);
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
						filetype : '1',
						phase : '3',
						proid : proid
					}));
				},
				beforeShowForm : function(e) {
					$('#fileid').after(
							'<input id="file_upload1" type="file" multiple />');
					createUploadify('file_upload1', showFile, 6, '上传施工图纸', '*.*', [], 1);
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

