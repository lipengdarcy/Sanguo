
//var fileShow = '<a id="file_show" href="" target="_blank">';
var fileShow = '<ul id="file_show" class="filebox"></ul>';	

// 上传文件后的回调显示
function showFile(data) {										
	$("#fileid").val(data.id);
}

//点击编辑，显示文件。gridId为表格的id
function showEditFile(gridId){
	var rowId = $('#'+ gridId).jqGrid('getGridParam', 'selrow');
	var rowData = $('#'+ gridId).getRowData(rowId);
	if (rowData.fileid != ''){	
		var name = $(rowData.fileid).html();
		var url =  $(rowData.fileid).attr('href');						
		$("#file_show").html(name);
		$("#file_show").attr("href", url);
	}
}

// 创建文件上传
function createEditElement(value, editOptions) {
	var file_id = '<input type="hidden"/>';
	return file_id;
}

function getElementValue(elem, oper, value) {
	if (oper === "set") {
		$('#fileid').val(value);
	}
	if (oper === "get") {
		var v = $('#fileid').val();
		if (!v)
			v = '';
		return v;
	}
}

// 设备多选
function getDevice() {
	var rayList = $("#rayList").jqGrid('getDataIDs');
	var unsealedList = $("#unsealedList").jqGrid('getDataIDs');
	var sealedList = $("#sealedList").jqGrid('getDataIDs');
	var containerList = $("#containerList").jqGrid('getDataIDs');
	
	EDevice_Project(
			[ 'rayList', 'unsealedList', 'sealedList', 'containerList' ],
			rayList,unsealedList,sealedList,containerList,[1,2,3,4], function(){}, 2);
}

//更新设备放射诊疗许可状态(只有验收选择的设备是已许可，其他状态不变)
function updateStatus(proid) {
	$.ajax({
		url : home_url + '/project/device/updateStatus',
		data : {
			'proid' : proid,
		},
		type : 'post',
		dataType : 'json',
		success : function(data) {
			//
		},
		error : function() {
			$.jGrowl("异常！请重新尝试或者联系管理员！", {
				header : "提醒"
			});
		}
	});
}
