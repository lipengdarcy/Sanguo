
var grid_selector = "#grid_table";
var pager_selector = "#grid_pager";
var id = 0 ;//用作删除主键ID


$(function() {
	

	$(grid_selector)
			.jqGrid(
					{
						url : home_url + '/project/report/filelistData?id=' + reportid + '&' + 'type=' + types,
						
						datatype : "json",
						mtype : "post",
						colNames : [  '附件名称', '操作' ],
						colModel : [
								{
									name : 'fileid',
									sortable : false,
									formatter : function(cellvalue, options,
											rowObject) {
									/*	if (!rowObject.file)
											return "";*/
										var temp = '<a dataid="'
												+ rowObject.fileid
												+ '" target="_blank" href="'
												+ getfileUrl(rowObject.fileid)
												+ '">'
												+ getfileName(rowObject.fileid)
												+ '</a>';
										return temp;
									}
								},
									
								{
									label:'操作',
									name : null,
									index : null,
									editable : false,
									sortable : false,
									width:20,
									formatter : function(cellvalue,
											options, rowObject) {
										var info="";
										//暂时先注释 以防万一需求变动要改回来
										//info+='<a href="javascript:workroleview('+rowObject.id+');" class="ui-pg-div" data-rel="tooltip" data-original-title="查看" ><i class="ui-icon icon-eye-open blue"></i></a>';
										//info+=' <a href="javascript:projectEdit('+rowObject.id+');" class="ui-pg-div" data-rel="tooltip" data-original-title="编辑"><i class="ui-icon icon-pencil green"></i></a>';
										info+=' <a href="javascript:del('+rowObject.id+');" class="ui-pg-div" data-rel="tooltip" data-original-title="删除"><i class="ui-icon icon-trash red"></i></a>';
										return info;
									}
								}
								 ],

						viewrecords : true,
						autowidth : true, // 宽度自适应
						height : 'auto', // 高度自适应
						autoheight : true,
						rownumbers : true, // 显示序号
						rowNum : 10,
						rowList : [ 10, 20, 30 ],
					
					});

});




//删除
function del(id) {
	
	var title = '删除文件', content = '是否确认删除？';
	id=id;

	var myDialog = dialog({
		title : title,
		content : content,
		lock : true,
		okValue : '确定',
		ok : function() {
				editCallback(2,id);
		
		},
		cancelValue : '取消',
		cancel : function() {
		}
	});
	myDialog.showModal();
}





// 表单验证
function validateForm() {
	var fileid = $("#editForm .filebox li").val();
	$('#editForm input[name=fileid]').val(fileid);
	return $('#editForm').validate({
		rules : {
			fileid : {
				required : true
			}
		}
	}).form();
}
