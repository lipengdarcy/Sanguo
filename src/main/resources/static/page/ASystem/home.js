//全局变量，记录是否已经加载了提示信息
var initFlag = [];
var DetailData = [];
// 设置全局变量 暂时用来存储职业健康检查的id
var SelectList = []// (用于记录被选中的行)
$(function() {

	// 先隐藏全部
	$('.qiehuan .slide-li').addClass('dn');
	// 再根据 用户设置显示 var settingList = ${settingList};

	for (var i = 0; i < settingList.length; i++) {
		$('#list' + settingList[i]).parent().parent().removeClass('dn');
		$('input[name=moduleList]').each(function() {
			if ($(this).val() == settingList[i]) {
				$(this).attr("checked", true);
			}
		});
	}

	$(".slide-neir").on('click', '.togglebottom', (function() {
		$(this).parents(".paddmin").removeClass("stoggheig");
	}));
	$(".slide-neir").on('click', '.toggletop', (function() {
		$(this).parents(".paddmin").addClass("stoggheig");
	}));

	$(".xinzr").click(function() {
		$(this).addClass("dn");
		$(this).siblings("a").removeClass("dn");
		$(".qiehuan").addClass("dn");
		$(".qiehuan2").removeClass("dn");
	});
	$(".wanc").click(function() {
		$(this).addClass("dn");
		$(this).siblings("a").removeClass("dn");
		$(".qiehuan2").addClass("dn");
		$(".qiehuan").removeClass("dn");
		// 显示选中的
		$('.qiehuan .slide-li').addClass('dn');
		var newList = [];
		$('input[name=moduleList]:checked').each(function() {
			newList.push($(this).val());
		});
		for (i = 0; i < newList.length; i++) {
			$('#list' + newList[i]).parent().parent().removeClass('dn');
		}
		// 没有变化则不更新
		if (settingList.sort().toString() != newList.sort().toString())
			updateUserSetting(newList);
	});

	// 判断windows的宽度
	var windows = $(window).width();
	// alert($(window).width());
	if (windows > 1200) {
		$(".width-duan").css({
			"width" : "1200px"
		});
	} else {
		$(".width-duan").css({
			"width" : "100%"
		});
	}

	// 4设备性能检测 设备类型， 1：射线；2：含源装置
	$("#dataListDevice").jqGrid({
		datatype : "local",
		// multiselect : true,
		colNames : [ 'id', '设备类型', '设备', '所在场所','设备id' ],
		colModel : [ {
			name : 'id',
			editable : false,
			hidden : true,
			sortable : false
		}, {
			name : "m1",
			editable : false,
			hidden : true,
			sortable : false
		}, 
		 {
			name : "m2",
			index : "m2",
			width : 'auto',
			editable : false,
			sortable : false
		}, {
			name : "m3",
			index : "m3",
			editable : false,
			sortable : false
		},{
			name : "m4",
			editable : false,
			hidden : true,
			sortable : false
		}, ],

		multiselect : true,
		viewrecords : true,// 是否要显示总记录数
		autowidth : true,// 宽度自适应
		height : 'auto',// 高度自适应
		autoheight : true,// 高度自适应
		rownumbers : true,// 显示序号
		rownumWidth : 40,// 序号列宽度
		// 每页显示记录
		rowNum : 10,
		rowList : [ 10, 20, 30 ],
		pgbuttons : true,// 是否显示翻页按钮
		pginput : true,// 是否显示跳转页面的输入框
		pager : "#dataPagerDevice"
	});

	// 分页0
	$('#dataListDevice').navGrid('#dataPagerDevice', {
		edit : false,
		add : false,
		del : false,
		search : false,
		refresh : false,
		view : false,
		position : "left",
		cloneToTop : false
	});

	// 1三同时
	$("#dataListSameTime").jqGrid({
		datatype : "local",
		// multiselect : true,
		colNames : [ 'id', '设备', '所在场所' ],
		colModel : [ {
			name : 'id',
			editable : false,
			hidden : true,
			sortable : false
		}, {
			name : "m1",
			index : "m1",
			width : 'auto',
			editable : false,
			sortable : false
		}, {
			name : "m2",
			index : "m2",
			editable : false,
			sortable : false
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
		pgbuttons : true,// 是否显示翻页按钮
		pginput : true,// 是否显示跳转页面的输入框
		pager : "#dataPagerSameTime"
	});

	// 分页1
	$('#dataListSameTime').navGrid('#dataPagerSameTime', {
		edit : false,
		add : false,
		del : false,
		search : false,
		refresh : false,
		view : false,
		position : "left",
		cloneToTop : false
	});

	// 2职业健康检查
	$("#dataListHealthCheck").jqGrid({
		datatype : "local",
		width : 420,
		multiselect : true,
		colModel : [ {
			name : 'id',
			editable : false,
			hidden : true,
			key : true,
			sortable : false
		}, {
			label : '姓名',
			name : "m1",
			index : "m1",
			width : 'auto',
			editable : false,
			sortable : false
		}, {
			label : '部门/科室',
			name : "m2",
			index : "m2",
			editable : false,
			sortable : false
		} ],
		onSelectAll : function(aRowids, status) {
			if (status) {
				for (var i = 0; i < aRowids.length; i++) {
					if (SelectList.indexOf(aRowids[i]) == -1) {// 每一次勾选时，检查该行是否在SelectList中，
						// 若在，则在SelectList中删除该记录
						// （翻页也会触发onselect事件，所以需检查该行是否在unselects中）
						SelectList.push(aRowids[i]);
					}
				}
			} else {
				for (var i = 0; i < aRowids.length; i++) {
					if (SelectList.indexOf(aRowids[i]) > -1) {// 每一次勾选时，检查该行是否在SelectList中，
						// 若在，则在SelectList中删除该记录
						// （翻页也会触发onselect事件，所以需检查该行是否在unselects中）
						SelectList.splice($.inArray(aRowids[i], SelectList), 1)
					}
				}
			}
		},
		gridComplete : function() {
			var ids = $("#dataListHealthCheck").jqGrid('getDataIDs');
			for (var i = 0; i < ids.length; i++) {
				if ((SelectList.indexOf(ids[i]) > -1)) {
					$("#dataListHealthCheck").jqGrid('setSelection', ids[i]);
				}
			}
		},
		onSelectRow : function(rowid, status) {
			if (status) { // 每一次取消选中时，将该行id添加到SelectList中
				if (SelectList.indexOf(rowid) == -1) {// 每一次勾选时，检查该行是否在SelectList中，
					// 若在，则在SelectList中删除该记录
					// （翻页也会触发onselect事件，所以需检查该行是否在unselects中）
					SelectList.push(rowid);
				}
			} else {
				if (SelectList.indexOf(rowid) > -1) {// 每一次勾选时，检查该行是否在SelectList中，
					// 若在，则在SelectList中删除该记录
					// （翻页也会触发onselect事件，所以需检查该行是否在unselects中）
					SelectList.splice($.inArray(rowid, SelectList), 1)
				}
			}
		},
		viewrecords : true,// 是否要显示总记录数
		autowidth : true,// 宽度自适应
		height : 'auto',// 高度自适应
		autoheight : true,// 高度自适应
		rownumbers : true,// 显示序号
		rownumWidth : 40,// 序号列宽度
		// 每页显示记录
		rowNum : 5,
		rowList : [ 5, 10, 20 ],
		pgbuttons : true,// 是否显示翻页按钮
		pginput : true,// 是否显示跳转页面的输入框
		pager : "#dataPagerHealthCheck"
	});

	// 分页2
	$('#dataListHealthCheck').navGrid('#dataPagerHealthCheck', {
		edit : false,
		add : false,
		del : false,
		search : false,
		refresh : false,
		view : false,
		position : "left",
		cloneToTop : false
	});

	$("#jqGrid1")
			.jqGrid(
					{
						datatype : "local",
						width : 420,
						colModel : [
								{
									name : 'id',
									editable : false,
									hidden : true,
									sortable : false,
									key : true
								},
								{
									label : '姓名',
									name : "m1",
									index : "m1",
									width : 40,
									editable : false,
									sortable : false
								},
								{
									label : '部门/科室',
									name : "m2",
									index : "m2",
									width : 40,
									editable : false,
									sortable : false
								},
								{
									label : "操作",
									name : "caozuo",
									index : "caozuo",
									width : 10,
									editable : false,
									sortable : false,
									formatter : function(cellvalue, options,
											rowObject) {
										var html = '<a href="javascript:delprework('
												+ rowObject.id
												+ ');" class="ui-pg-div" data-rel="tooltip" data-original-title="删除"><i class="ui-icon icon-trash red"></i></a>';
										return html;
									}
								} ],

						// sortable:'boolean',//是否可排序
						sortname : 'CustomerID',// 默认的排序列。可以是列名称或者是一个数字，这个参数会被提交到后台
						sortorder : 'asc',// 排序顺序，升序或者降序（asc or desc）
						loadonce : true,// 如果为ture则数据只从服务器端抓取一次，之后所有操作都是在客户端执行，翻页功能会被禁用
						viewrecords : true,// 是否要显示总记录数
						// 表格标题
						caption : "已选择需进行上岗前职业健康检查的人员",
						autowidth : true,// 宽度自适应
						height : 'auto',// 高度自适应
						autoheight : true,// 高度自适应
						rownumbers : true,// 显示序号
						rownumWidth : 10,// 序号列宽度
						// 每页显示记录
						rowNum : 5,
						rowList : [ 5, 10, 20, 30, 40 ],
						pager : "#jqGridPager1",
						loadComplete : function() {
							var table = this;
							setTimeout(function() {
								/*
								 * styleCheckbox(table);
								 * updateActionIcons(table);
								 * updatePagerIcons(table);
								 */
								enableTooltips(table);
							}, 0);
						},
					});

	$("#jqGrid2")
			.jqGrid(
					{
						datatype : "local",
						colModel : [
								{
									name : 'id',
									editable : false,
									hidden : true,
									sortable : false,
									key : true
								},
								{
									label : '姓名',
									name : "m1",
									index : "m1",
									width : 40,
									editable : false,
									sortable : false
								},
								{
									label : '部门/科室',
									name : "m2",
									index : "m2",
									width : 40,
									editable : false,
									sortable : false
								},
								{
									label : "操作",
									name : "caozuo",
									index : "caozuo",
									width : 10,
									editable : false,
									sortable : false,
									formatter : function(cellvalue, options,
											rowObject) {
										var html = '<a href="javascript:delworking('
												+ rowObject.id
												+ ');" class="ui-pg-div" data-rel="tooltip" data-original-title="删除"><i class="ui-icon icon-trash red"></i></a>';
										return html;
									}
								}

						],
						// sortable:'boolean',//是否可排序
						sortorder : 'asc',// 排序顺序，升序或者降序（asc or desc）
						loadonce : true,// 如果为ture则数据只从服务器端抓取一次，之后所有操作都是在客户端执行，翻页功能会被禁用
						viewrecords : true,// 是否要显示总记录数
						// 表格标题
						caption : "已选择需进行在岗期间职业健康检查的人员",
						autowidth : true,// 宽度自适应
						height : 'auto',// 高度自适应
						autoheight : true,// 高度自适应
						rownumbers : true,// 显示序号
						rownumWidth : 10,// 序号列宽度
						// 每页显示记录
						rowNum : 5,
						rowList : [ 5, 10, 20, 30, 40 ],
						pager : "#jqGridPager2",
						loadComplete : function() {
							var table = this;
							setTimeout(function() {
								/*
								 * styleCheckbox(table);
								 * updateActionIcons(table);
								 * updatePagerIcons(table);
								 */
								enableTooltips(table);
							}, 0);
						},
					});

	// 2职业健康检查 检查结果异常
	$("#dataListAbnormalCheck")
			.jqGrid(
					{
						datatype : "local",
						// multiselect : true,
						colModel : [
								{
									name : 'id',
									editable : false,
									key : true,
									hidden : true,
									sortable : false
								},
								{
									label : "姓名",
									name : "m1",
									index : "m1",
									width : 'auto',
									editable : false,
									sortable : false
								},
								{
									label : "部门/科室",
									name : "m2",
									index : "m2",
									width : 'auto',
									editable : false,
									sortable : false
								},
								{
									label : "岗位/工种",
									name : "m3",
									index : "m3",
									width : 'auto',
									editable : false,
									sortable : false
								},
								{
									label : "操作",
									name : "caozuo",
									index : "caozuo",
									width : 'auto',
									editable : false,
									sortable : false,
									formatter : function(cellvalue, options,
											rowObject) {
										var html = '<a href="javascript:abnormalCheckEdit('
												+ rowObject.id
												+ ');" class="btn btn-primary btn-sm">处理</a>';
										return html;
									}
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
						pgbuttons : true,// 是否显示翻页按钮
						pginput : true,// 是否显示跳转页面的输入框
						pager : "#dataPagerAbnormalCheck"
					});

	// 分页2 检查结果异常
	$('#dataListAbnormalCheck').navGrid('#dataPagerAbnormalCheck', {
		edit : false,
		add : false,
		del : false,
		search : false,
		refresh : false,
		view : false,
		position : "left",
		cloneToTop : false
	});

	// 8人员证书
	$("#dataListCertificate")
			.jqGrid(
					{
						datatype : "local",
						// multiselect : true,
						colModel : [
								{
									name : 'id',
									editable : false,
									hidden : true,
									key : true,
									sortable : false
								},
								{
									label : "姓名",
									name : "m1",
									index : "m1",
									width : 'auto',
									editable : false,
									sortable : false
								},
								{
									label : "证件号码",
									name : "m2",
									index : "m2",
									editable : false,
									sortable : false
								},
								{
									label : "操作",
									name : "caozuo",
									index : "caozuo",
									editable : false,
									sortable : false,
									formatter : function(cellvalue, options,
											rowObject) {
										var html = '<a href="javascript:ceredit('
												+ rowObject.id
												+ ','
												+ $("#fileType").val()
												+ ');" class="btn btn-primary btn-sm">录入证书信息</a>';
										return html;
									}
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
						pgbuttons : true,// 是否显示翻页按钮
						pginput : true,// 是否显示跳转页面的输入框
						pager : "#dataPagerCertificate"
					});

	// 分页2
	$('#dataListCertificate').navGrid('#dataPagerCertificate', {
		edit : false,
		add : false,
		del : false,
		search : false,
		refresh : false,
		view : false,
		position : "left",
		cloneToTop : false
	});

	// 10合同告知
	$("#dataListInformMessage")
			.jqGrid(
					{
						datatype : "local",
						// multiselect : true,
						colModel : [
								{
									name : 'id',
									editable : false,
									hidden : true,
									key : true,
									sortable : false
								},
								{
									label : "姓名",
									name : "m1",
									index : "m1",
									width : 'auto',
									editable : false,
									sortable : false
								},
								{
									label : "证件号码",
									name : "m2",
									index : "m2",
									width : 'auto',
									editable : false,
									sortable : false
								},
								{
									label : "操作",
									name : null,
									index : null,
									editable : false,
									sortable : false,
									width : 'auto',
									formatter : function(cellvalue, options,
											rowObject) {
										var html = '<a href="javascript:addmark('
												+ rowObject.id
												+ ');" class="btn btn-primary btn-xs switch-bg" style="margin:5px;">确认告知</a>';
										html += '<a href="javascript:noneed('
												+ rowObject.id
												+ ');" class="btn btn-primary btn-xs switch-bg" style="margin:5px;">无需告知</a>';
										return html;
									}
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
						pgbuttons : true,// 是否显示翻页按钮
						pginput : true,// 是否显示跳转页面的输入框
						pager : "#dataPagerInformMessage"
					});

	// 分页2
	$('#dataListInformMessage').navGrid('#dataPagerInformMessage', {
		edit : false,
		add : false,
		del : false,
		search : false,
		refresh : false,
		view : false,
		position : "left",
		cloneToTop : false
	});

	// 5场所检测、
	$("#dataListDlive").jqGrid({
		datatype : "local",
		// multiselect : true,
		colNames : [ 'id', '机房或区域', '所在楼层', '建筑物' ],
		colModel : [ {
			name : 'id',
			editable : false,
			hidden : true,
			sortable : false
		}, {
			name : "m1",
			index : "m1",
			width : 'auto',
			editable : false,
			sortable : false
		}, {
			name : "m2",
			index : "m2",
			editable : false,
			sortable : false
		}, {
			name : "m3",
			index : "m3",
			editable : false,
			sortable : false
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
		pgbuttons : true,// 是否显示翻页按钮
		pginput : true,// 是否显示跳转页面的输入框
		pager : "#dataPagerDlive"
	});

	// 分页3
	$('#dataListDlive').navGrid('#dataPagerDlive', {
		edit : false,
		add : false,
		del : false,
		search : false,
		refresh : false,
		view : false,
		position : "left",
		cloneToTop : false
	});

	// 防护用品种类
	$("#dataListShield").jqGrid({
		datatype : "local",
		// multiselect : true,
		colNames : [ 'id', '防护用品名称', '出厂日期', '数量', '机房或区域' ],
		colModel : [ {
			name : 'id',
			editable : false,
			hidden : true,
			sortable : false
		}, {
			name : "m1",
			index : "m1",
			width : 'auto',
			editable : false,
			sortable : false
		}, {
			name : "m2",
			index : "m2",
			editable : false,
			sortable : false
		}, {
			name : "m3",
			index : "m3",
			editable : false,
			sortable : false
		}, {
			name : "m4",
			index : "m4",
			editable : false,
			sortable : false
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
		pgbuttons : true,// 是否显示翻页按钮
		pginput : true,// 是否显示跳转页面的输入框
		pager : "#dataPagerShield"
	});

	// 分页1
	$('#dataListShield').navGrid('#dataPagerShield', {
		edit : false,
		add : false,
		del : false,
		search : false,
		refresh : false,
		view : false,
		position : "left",
		cloneToTop : false
	});

	// 绑定数字点击事件
	$(".slide-li ul").on('click', 'a', function(e) {
		var li = $(e.target).parent();
		var ul = li.parent();
		var id = ul.attr('id');
		var index = li.index();

		switch (id) {
		case 'list1':
			showHintDetail(1, index);
			break;
		case 'list2':
			showHintDetail(2, index);
			break;
		case 'list4':
			showHintDetail(4, index);
			break;
		case 'list5':
			showHintDetail(5, index);
			break;
		case 'list6':
			showHintDetail(6, index);
			break;
		case 'list7':
			showHintDetail(7, index);
			break;
		case 'list8':
			showHintDetail(8, index);
			break;
		case 'list9':
			showHintDetail(9, index);
			break;
		case 'list10':
			showHintDetail(10, index);
			break;
		default:
			break;
		}

	});

	// 三同时：绑定数字点击事件
	$("#project-ul").on('click', 'a', function(e) {
		if (certType == 1)
			title = '未获得辐射安全许可的设备';
		else
			title = '未获得放射诊疗许可的设备';
		content = $('#dataDivSameTime');
		$("#dataListSameTime").jqGrid("clearGridData");
		$("#dataListSameTime").setGridParam({
			data : messageDetail
		}).trigger('reloadGrid');
		var myDialog = dialog({
			title : title,
			content : content,
			width : 710,
			height : 'auto',
			okValue : '确定',
			ok : function() {
			},
		});
		myDialog.showModal();
	});

});

// 点击数字，展示弹框grid详情
function showHintDetail(type, i) {
	var title, content = $('#dataDiv');
	switch (type) {
	case 1:

		break;
	case 2:
		// 暂时有bug 可能会出现需进行职业健康检查的人员不存在 导致title显示第一个
		var idTip = $("#dataListHealthCheck");
		if (i == 1) {
			title = '体检结论异常需进行处理的人员';
			idTip = $("#dataListAbnormalCheck");
			content = $('#dataDivAbnormalCheck');
		} else {
			title = '需进行职业健康检查的人员';
			content = $('#dataDivHealthCheck');
			objList= DetailData[type][i];
		}

		idTip.jqGrid("clearGridData");
		idTip.setGridParam({
			data : DetailData[type][i]
		}).trigger('reloadGrid');
		break;
	case 3:
		break;
	case 4:
		if (i == 4) {
			title = '性能检测不合格的设备';
		} else {
			title = '需性能检测的设备';
		}
		content = $('#dataDivDevice');
		$("#dataListDevice").jqGrid("clearGridData");
		$("#dataListDevice").setGridParam({
			data : DetailData[type][i]
		}).trigger('reloadGrid');
		break;
	case 5:
		title = '需放射诊疗检测的场所';
		if (certType == 1)
			title = '需进行辐射安全检测的工作场所';
		else
			title = '需进行放射诊疗检测的工作场所';
		content = $('#dataDivDlive');
		$("#dataListDlive").jqGrid("clearGridData");
		$("#dataListDlive").setGridParam({
			data : DetailData[type][i]
		}).trigger('reloadGrid');
		break;
	case 6:
		if (i == 1) {
			title = '过期的防护用品';
			content = $('#dataDivShield');
			$("#dataListShield").jqGrid("clearGridData");
			$("#dataListShield").setGridParam({
				data : DetailData[type][i]
			}).trigger('reloadGrid');
		} else {
			if (DetailData[type][0].length > 0) {
				title = '防护用品配备不足的区域';
				content = $('#dataDivDlive');
				$("#dataListDlive").jqGrid("clearGridData");
				$("#dataListDlive").setGridParam({
					data : DetailData[type][0]
				}).trigger('reloadGrid');
			} else {
				title = '过期的防护用品';
				content = $('#dataDivShield');
				$("#dataListShield").jqGrid("clearGridData");
				$("#dataListShield").setGridParam({
					data : DetailData[type][1]
				}).trigger('reloadGrid');
			}
		}
		break;
	case 7:
		title = '需完善警示标识的区域';
		content = $('#dataDivDlive');
		$("#dataListDlive").jqGrid("clearGridData");
		$("#dataListDlive").setGridParam({
			data : DetailData[type][i]
		}).trigger('reloadGrid');
		break;
	case 8:
		var divTip = "";
		if (i == 0) {
			divTip = "您也可以进入放射工作人员证列表页面，批量导入证书信息，<a href='" + home_url
					+ "/Certificate?fileType=1 '>点击进入</a>。";
			title = '未取得放射工作人员证的人员';
			$("#fileType").val(1);
		} else {
			divTip = "您也可以进入辐射安全工作人员证列表页面，批量导入证书信息，<a href='" + home_url
					+ "/Certificate?fileType=2 '>点击进入</a>。";
			title = '未取得辐射安全与防护培训合格证书的人员';
			$("#fileType").val(2);
		}
		content = $('#dataDivCertificate');
		$("#divTip").empty();
		$("#divTip").append(divTip);
		$("#dataListCertificate").jqGrid("clearGridData");
		$("#dataListCertificate").setGridParam({
			data : DetailData[type][i]
		}).trigger('reloadGrid');
		break;
	case 9:
		title = '需检定的监测设备';
		content = $('#dataDivSameTime');
		$("#dataListSameTime").jqGrid("clearGridData");
		$("#dataListSameTime").setGridParam({
			data : DetailData[type][i]
		}).trigger('reloadGrid');
		break;
	case 10:
		title = '需合同告知的人员';
		content = $('#dataDivInformMessage');
		$("#dataListInformMessage").jqGrid("clearGridData");
		$("#dataListInformMessage").setGridParam({
			data : DetailData[type][i]
		}).trigger('reloadGrid');
		break;
	default:
		break;
	}
	if(type==2&&i==0){
		var myDialog = dialog({
			title : title,
			content : content,
			width : 710,
			height : 'auto',
		 okValue : '确定',
		 ok : function() {
			 
		 },
		 cancelValue:'取消',
		 cancel : function() {
		 }
		});
		myDialog.showModal();
	}else{
		var myDialog = dialog({
			title : title,
			content : content,
			width : 710,
			height : 'auto',
		// okValue : '确定',
		// ok : function() {
		// },
		});
		myDialog.showModal();
	}
	
}

// 新增项目
function add() {
	var myDialog1 = dialog({
		title : '新增项目',
		width : 710,
		height : 'auto',
		okValue : '确定',
	});
	$.ajax({
		url : home_url + '/project/add?type=' + type,
		success : function(data) {
			myDialog1.content(data);
		},
		error : function() {
			$.jGrowl("异常！请重新尝试或者联系管理员！");
		}
	});
	myDialog1.showModal();

}

// 新发证
function newcer() {
	var myDialog = dialog({
		title : '请填写下列信息',
		width : 420,
		content : $('#newcer'),
		lock : true,
		okValue : '确认',
		ok : function() {
			add();
		}
	});
	myDialog.showModal();
}

// 统计页面
function showStatistics() {
	var message = '统计功能正在完善，敬请期待~';
	$.jGrowl(message, {
		header : "提醒"
	});
}

/**
 * 获取首页提示信息
 * 
 * @param type
 *            提示信息类型
 * 
 */
function getHint(type) {
	if (initFlag[type])
		return;

	var loadDiv = '<div class="loading"><i class="icon-spinner icon-spin orange bigger-500"></i></div>';
	$('#list' + type).parent().append(loadDiv);
	$('#list' + type).addClass("dn");

	$
			.ajax({
				url : home_url + '/getHint',
				data : {
					certType : certType,
					type : type
				},
				type : 'post',
				dataType : 'json',
				success : function(data) {
					$(".loading").addClass("dn");
					$('#list' + type).removeClass("dn");
					initFlag[type] = true;
					DetailData[type] = [];
					for (var i = 0; i < data.data.length; i++) {
						if (data.data[i].message != "0") {
							var listItem = '<li><i class="icon-exclamation-sign bigger-120"></i>'
									+ data.data[i].message + '</li>';
							// if (data.data[i].detail &&
							// data.data[i].detail.length > 0)
							if (data.data[i].message != "密码输入错误") {
								$('#list' + type).append(listItem);
							}
							if (type == 1 || type == 2 ) {
								var array = [];
								for (var j = 0; j < data.data[i].detail.length; j++)
									array[j] = {
										id : data.data[i].detail[j].id,
										m1 : data.data[i].detail[j].m1,
										m2 : data.data[i].detail[j].m2,
										m3 : data.data[i].detail[j].m3,
									};
								DetailData[type][i] = array;
							}
							if (type == 4) {
								var array = [];
								for (var j = 0; j < data.data[i].detail.length; j++)
									array[j] = {
										m1 : data.data[i].detail[j].m1,
										m2 : data.data[i].detail[j].m2,
										m3 : data.data[i].detail[j].m3,
										m4 : data.data[i].detail[j].m4
									};
								DetailData[type][i] = array;
							}
							if (type == 5 || type == 7) {
								var array = [];
								for (var j = 0; j < data.data[i].detail.length; j++)
									array[j] = {
										m1 : data.data[i].detail[j].m1,
										m2 : data.data[i].detail[j].m2,
										m3 : data.data[i].detail[j].m3
									};
								DetailData[type][i] = array;
							}
							if (type == 6) {
								var array = [];
								if (i == 0)
									for (var j = 0; j < data.data[i].detail.length; j++)
										array[j] = {
											m1 : data.data[i].detail[j].m1,
											m2 : data.data[i].detail[j].m2,
											m3 : data.data[i].detail[j].m3
										};
								if (i == 1)
									for (var j = 0; j < data.data[i].detail.length; j++)
										array[j] = {
											m1 : data.data[i].detail[j].m1,
											m2 : data.data[i].detail[j].m2,
											m3 : data.data[i].detail[j].m3,
											m4 : data.data[i].detail[j].m4
										};
								DetailData[type][i] = array;
							}
							if (type == 8 || type == 10) {
								var array = [];
								for (var j = 0; j < data.data[i].detail.length; j++)
									array[j] = {
										id : data.data[i].detail[j].id,
										m1 : data.data[i].detail[j].m1,
										m2 : data.data[i].detail[j].m2
									};
								DetailData[type][i] = array;
							}
							if (type == 9 && data.data[i].detail) {
								var array = [];
								for (var j = 0; j < data.data[i].detail.length; j++)
									array[j] = {
										m1 : data.data[i].detail[j].m1,
										m2 : data.data[i].detail[j].m2
									};
								DetailData[type][i] = array;
							}

						} else {
							// 隐藏li 防止提取li的index错误 区分title出现bug
							var listItem = '<li class="dn"></li>';
							$('#list' + type).append(listItem);
						}
						if (data.data.length > 10) {
							var toggle = '<div class="toggleclass"><a class="togglebottom" href="javascript:;"> <i class="icon-angle-down"></i> 下拉</a> '
									+ '<a class="toggletop" href="javascript:;"> <i class="icon-angle-up"></i> 收起 </a> </div>';
							$('#list' + type).after(toggle);
						}
					}
				},
				error : function() {
					$.jGrowl("异常！请重新尝试或者联系管理员!");
				}
			});
}

/**
 * 更新用户设置
 * 
 */
function updateUserSetting(moduleList) {
	$.ajax({
		url : home_url + '/userSetting',
		data : {
			moduleList : moduleList
		},
		type : 'post',
		dataType : 'json',
		success : function(data) {
			$.jGrowl(data.message);
		},
		error : function() {
			$.jGrowl("异常！请重新尝试或者联系管理员!");
		}
	});
}

// 证书编辑
function ceredit(staffid, fileType) {

	var title = "录入证书信息";
	var url = home_url + '/Certificate/editCerPage?fileType=' + fileType
			+ '&staffid=' + staffid;
	var myDialogCer = dialog({
		title : title,
		okValue : '确定',
		width : 900,
		ok : function() {
			if (!$("#CerRadioTb").valid()) {
				return false;
			}

			var re_records = $("#trainingRadiotb").jqGrid('getGridParam',
					'records'); // 获取数据总条数
			if (re_records == 0) {
				alert("请先添加数据！");
				return false;
			}

			saveCer();
			// js动态删除
			$("#dataListCertificate").delRowData(staffid);
		},
		cancelValue : '取消',
		cancel : function() {
			// 取消则删除培训
			$.post(home_url + "/Certificate/delCer", {
				staffid : staffid,
				fileType : fileType
			});
		}
	});
	$.ajax({
		url : url,
		success : function(data) {
			myDialogCer.content(data);
		}
	});
	myDialogCer.showModal();
}
// 确认告知
function addmark(id) {
	var myDialog = dialog({
		title : '确认告知',
		lock : true,
		okValue : '确认',
		ok : function() {
			$.ajax({
				type : "post",
				url : home_url + '/InformMessage/saveInform',
				data : $("#addmarkForm").serialize(),
				dataType : "json",
				success : function(data) {
					if (data.code == 1) {
						$("#dataListInformMessage").delRowData(id);
					}
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					alert("请求失败！");
				}
			});
		},
		cancelValue : '取消',
		cancel : function() {
		}
	});
	$.ajax({
		url : home_url + '/InformMessage/addmark',
		data : {
			status : 1,
			staffid : id
		},
		type : "post",
		success : function(data) {
			myDialog.content(data);
		}
	});
	myDialog.showModal();
}

// 无需告知
function noneed(id) {
	var myDialog = dialog({
		title : '无需告知的原因',
		lock : true,
		okValue : '确认',
		ok : function() {
			// 验证 验证失败则不进行下一步
			if (!$("#noneedForm").valid()) {
				return false;
			}
			$.ajax({
				type : "post",
				url : home_url + '/InformMessage/saveInform',
				data : $("#noneedForm").serialize(),
				dataType : "json",
				success : function(data) {
					if (data.code == 1) {
						$("#dataListInformMessage").delRowData(id);
					}
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					alert("请求失败！");
				}
			});
		},
		cancelValue : '取消',
		cancel : function() {
		}
	});
	$.ajax({
		url : home_url + '/InformMessage/addmark',
		data : {
			status : -1,
			staffid : id
		},
		type : "post",
		success : function(data) {
			myDialog.content(data);
		}
	});
	myDialog.showModal();

}
// 体检结论异常需进行处理的人员 处理
function abnormalCheckEdit(id) {
	var myDialog = dialog({
		title : "编辑",
		okValue : '确定',
		width : 500,
		ok : function() {
			// 验证 验证失败则不进行下一步
			if (!$("#editAbnormalCheckTb").valid()) {
				return false;
			}
			$.ajax({
				type : "post",
				url : home_url + "/healthCheck/saveCheckData",
				data : $("#editAbnormalCheckTb").serialize(),
				dataType : "json",
				success : function(data) {
					if (data.code == 1) {
						$("#dataListAbnormalCheck").delRowData(id);
					}
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					$.jGrowl("异常！请重新尝试或者联系管理员!", {
						header : "error"
					});
				}
			});
		},
		cancelValue : '取消',
		cancel : function() {
		}
	});
	$.ajax({
		url : home_url + "/healthCheck/abnormalCheckEdit?id=" + id,
		success : function(data) {
			myDialog.content(data);
		}
	});
	myDialog.showModal();
}
//上岗前职业健康检查数组对象
var preWorkList = new Array();
//在岗期间职业健康检查数组对象
var workingList = new Array();
//职业健康检查的数组对象
var objList =new Array();
// 添加到上岗前职业健康检查
function addprework(type, i) {
	var ids = SelectList.concat();
	// 筛选数组对象
	for (var i = 0; i < ids.length; i++) {
		for (var j = 0; j < objList.length; j++) {
			if (ids[i] == objList[j].id) {
				preWorkList.push(objList[j]);
				objList.splice($.inArray(objList[j], objList), 1)
			}
		}
	}
	//清空已选id
	SelectList=[];
	// 先清空列表
	$("#dataListHealthCheck").jqGrid("clearGridData");
	//$("#jqGrid1").jqGrid("clearGridData");
	// 再刷新列表
	$('#dataListHealthCheck').setGridParam({
		data : objList
	}).trigger('reloadGrid');
	$('#jqGrid1').setGridParam({
		data : preWorkList
	}).trigger('reloadGrid');

}

// 添加到在岗期间职业健康检查
function addWorking(type, i) {
	var ids = SelectList.concat();
	
	// 筛选数组对象
	for (var i = 0; i < ids.length; i++) {
		for (var j = 0; j < objList.length; j++) {
			if (ids[i] == objList[j].id) {
				workingList.push(objList[j]);
				objList.splice($.inArray(objList[j], objList), 1)
			}
		}
	}
	//清空已选id
	SelectList=[];
	// 先清空列表
	$("#dataListHealthCheck").jqGrid("clearGridData");
	//$("#jqGrid2").jqGrid("clearGridData");
	// 再刷新列表
	$('#dataListHealthCheck').setGridParam({
		data : objList
	}).trigger('reloadGrid');
	$('#jqGrid2').setGridParam({
		data : workingList
	}).trigger('reloadGrid');
}
// 删除上岗前职业健康检查
function delprework(id) {

	for (var j = 0; j < preWorkList.length; j++) {
		if (id == preWorkList[j].id) {
			objList.push(preWorkList[j]);
			preWorkList.splice($.inArray(preWorkList[j], preWorkList), 1)
		}
	}
	// 先清空列表
	//$("#dataListHealthCheck").jqGrid("clearGridData");
	$("#jqGrid1").jqGrid("clearGridData");
	// 再刷新列表
	$('#dataListHealthCheck').setGridParam({
		data : objList
	}).trigger('reloadGrid');
	$('#jqGrid1').setGridParam({
		data : preWorkList
	}).trigger('reloadGrid');
}
// 在岗期间职业健康检查
function delworking(id) {

	for (var j = 0; j < workingList.length; j++) {
		if (id == workingList[j].id) {
			objList.push(workingList[j]);
			workingList.splice($.inArray(workingList[j], workingList), 1)
		}
	}
	// 先清空列表
	//$("#dataListHealthCheck").jqGrid("clearGridData");
	$("#jqGrid2").jqGrid("clearGridData");
	// 再刷新列表
	$('#dataListHealthCheck').setGridParam({
		data : objList
	}).trigger('reloadGrid');
	$('#jqGrid2').setGridParam({
		data : workingList
	}).trigger('reloadGrid');
}