$(function() {

	var gridid = 'grid_table';
	var grid_selector = "#" + gridid;
	var pager_selector = "#grid_pager";

	$(grid_selector)
			.jqGrid(
					{

						url : home_url + '/project/listData?type=' + type,
						editurl : home_url + '/project/update',
						datatype : "json",
						colNames : [ '终止原因', '项目名称', '项目性质', '诊疗类别', '项目类别',
								'项目开始时间', '完成时间', '项目所处阶段', '更新时间', '操作' ],
						colModel : [
								{
									name : 'closereason',
									hidden : true,
									width: 10,
								},
								{
									name : 'projectname',
									width: 10,
								},
								{
									name : 'projecttype',
									width: 10,
									formatter : function(cellvalue, options,
											rowObject) {
										return getProjectType(cellvalue);
									}
								},
								{
									name : 'radiotype',
									hidden : true,
									width: 10,
								},
								{
									name : 'safetype',
									hidden : true,
									width: 10,
								},
								{
									name : 'begintime',
									width : 10,
									formatter : function(cellvalue, options,
											rowObject) {
										if (cellvalue == undefined)
											return "";
										return moment(new Date(cellvalue))
												.format("YYYY-MM-DD");
									}
								},
								{
									name : 'endtime',
									width : 10,
									formatter : function(cellvalue, options,
											rowObject) {
										if (cellvalue == undefined)
											return "";
										return moment(new Date(cellvalue))
												.format("YYYY-MM-DD");
									}
								},
								{
									name : 'phase',	
									width: 10,
									formatter : function(cellvalue, options,
											rowObject) {
										return getPhase(cellvalue);
									}

								},
								{
									name : 'updatetime',
									width: 10,
									formatter : function(cellvalue, options,
											rowObject) {
										if (cellvalue == undefined)
											return "";
										return moment(new Date(cellvalue))
												.format("YYYY-MM-DD hh:mm:ss");
									},
									editable : true
								},
								{
									name : 'id',
									width: 10,
									formatter : function(cellvalue, options,
											rowObject) {
										if (cellvalue == undefined)
											return "";
										return getOperateColumn(cellvalue,
												rowObject.phase);
									}
								} ],

						viewrecords : true,
						width : 'auto',
						autowidth : true, // 宽度自适应
						height : 'auto', // 高度自适应
						autoheight : true,
						rownumbers : true, // 显示序号
						gridComplete : function() {

							if (type == 0)
								$(grid_selector).jqGrid().setGridParam()
										.showCol('radiotype');
							else
								$(grid_selector).jqGrid().setGridParam()
										.showCol('safetype');

						},

						// rownumWidth: 40,//序号列宽度
						// 每页显示记录
						rowNum : 10,
						rowList : [ 10, 20, 30 ],
						pager : pager_selector
					});

	// 表格下方操作
	$(grid_selector).navGrid(pager_selector, {
		edit : false,
		add : false,
		del : false,
		search : false,
		refresh : false,
		view : false,
		position : "left", // 定义导航按钮在分页容器中的位置，可用值有： left, center, right
		cloneToTop : false
	});

});

// 项目性质转义
function getProjectType(typeList) {
	if (typeList === undefined)
		return '';

	var typeName = '';
	var array = new Array();
	array = typeList.split(",");
	for (var i = 0; i < array.length; i++) {
		switch (array[i]) {
		case "1":
			typeName += '新建,';
			break;
		case "2":
			typeName += '扩建,';
			break;
		case "3":
			typeName += '改建,';
			break;
		case "4":
			typeName += '技术改造,';
			break;
		case "5":
			typeName += '技术引进,';
			break;
		default:
			break;
		}
	}
	return typeName.substr(0, typeName.length - 1);
}

// 项目阶段转义
function getPhase(phaseId) {
	if (phaseId === undefined)
		return '';

	var typeName = '';
	switch (phaseId) {
	case 1:
		typeName = '前期阶段';
		break;
	case 2:
		typeName = '评价阶段';
		break;
	case 3:
		typeName = '施工阶段';
		break;
	case 4:
		typeName = '验收阶段';
		break;
	case 5:
		typeName = '部分完成';
		break;
	case 6:
		typeName = '已完成';
		break;
	case -1:
		typeName = '已终止';
		break;
	default:
		typeName = '前期阶段';
		break;
	}
	return typeName;
}

// 操作栏转义
function getOperateColumn(id, phase) {
	if (id === undefined || phase === undefined)
		return '';

	var url = "";
	switch (phase) {
	case 1:
		url = home_url + "/project/phase1/" + id;
		break;
	case 2:
		url = home_url + "/project/phase2/" + id;
		break;
	case 3:
		url = home_url + "/project/phase3/" + id;
		break;
	case 4:
		url = home_url + "/project/phase4/" + id;
		break;
	case 5:
		url = home_url + "/project/phase5/" + id;
		break;
	default:
		url = home_url + "/project/phase1/" + id;
		break;
	}
	var link = "<a href='"
			+ url
			+ "' class='ui-pg-div' data-rel='tooltip' data-original-title='查看' title='查看三同时项目详情'><i class='ui-icon icon-eye-open blue'></i></a>"
			+ "<a href='javascript:endcheck();' class='ui-pg-div' data-rel='tooltip' data-original-title='终止' title='终止三同时项目'><i class='ui-icon icon-ban-circle red'></i></a>"
	return link;
}
