$(function() {

	//法律法规标准，不分页
	var grid_selector = "#law_standard";

	$(grid_selector).jqGrid({
		url : home_url + '/law/listData?type=2',
		datatype : "json",
		multiselect : true,
		colNames : [ '名称', '类别', '文号', '颁布部门', '实施日期', '状态', '详细信息', ],
		colModel : [ {
			name : 'name',
		}, {
			name : 'type',
			formatter : function(cellvalue, options, rowObject) {
				return getType(cellvalue);
			}
		}, {
			name : 'fileno',
		}, {
			name : 'publishdept',
		}, {
			name : 'effecttime',
			formatter : function(cellvalue, options,
					rowObject) {
				if (cellvalue == undefined)
					return "";
				return moment(new Date(cellvalue))
						.format("YYYY-MM-DD");
			}
		}, {
			name : 'isvalid',
			formatter : function(cellvalue, options, rowObject) {
				var temp = '现行';
				if (cellvalue == undefined)
					temp = '现行';
				else if (cellvalue == -1)
					temp = '废止';
				else
					temp = '现行';
				return temp;
			}

		}, {
			name : 'link',
			formatter : function(cellvalue, options, rowObject) {
				var temp = '<a target="_blank" href="'+ home_url + cellvalue + '">附件</a>';				
				return temp;
			}			
		}

		],

		viewrecords : true,
		height : '500', // 高度自适应
		rownumbers : true, // 显示序号
		rowNum : 100
	});


});

//法律或者制度类别转义
function getType(typeId) {
	if (typeId === undefined)
		return '';

	var typeName = '';
	switch (typeId) {
	case 1:
		typeName = '管理制度';
		break;
	case 2:
		typeName = '应急预案';
		break;
	case 3:
		typeName = '操作规程';
		break;
	case 4:
		typeName = '表单';
		break;
	case 5:
		typeName = '法律';
		break;
	case 6:
		typeName = '行政法规';
		break;
	case 7:
		typeName = '部门规章';
		break;
	case 8:
		typeName = '规范性文件';
		break;
	case 9:
		typeName = '标准';
		break;
	default:
		typeName = '法律';
		break;
	}
	return typeName;
}

//查询
function query_standard() {
	var data = {
		name : $("#name_standard").val()
	};
	var postData = $("#law_standard").jqGrid("getGridParam", "postData");
	// 将查询参数融入postData选项对象
	$.extend(postData, data);
	$("#law_standard").jqGrid('setGridParam').trigger("reloadGrid");
}
