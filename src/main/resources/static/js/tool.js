/*******************************
 * 本文件主要包含了系统所用的各种弹框公用组件
 * _Single结尾的是单选组件；
 * _Multi结尾的是多选组件。
 ******************************/

/**
 * grid列表新增记录后的回调，使grid自动刷新显示新增的记录。 注意：新打开的窗口要用window.open的方式，不然无法回调。
 * 此方法在子页面记录新增时候调用。
 * 
 * @param gridid
 *            父页面的grid的id
 */

$.jgrid.defaults.width = 780;
$.jgrid.defaults.responsive = true;
$.jgrid.defaults.styleUI = 'Bootstrap';

function gridAddCallback(gridid) {
	// 因为是新增，所以刷新前面的表格
	if (window.opener && !window.opener.closed) {
		var p = $('#' + gridid, window.opener.document).jqGrid('getGridParam',
				'page');// 获取当前页
		window.opener.$('#' + gridid).jqGrid('setGridParam', {
			page : p,
		}).trigger("reloadGrid");
	}
}

/**
 * 创建上传组件
 * 
 * @param id
 *            文件上传input的id，必填
 * @param callback
 *            回调函数，处理文件上传后的显示等，必填
 * @param pathType
 *            上传路径，参考枚举类：UploadFilePathEnum，可选
 * @param buttonName
 *            上传按钮显示的文字，可选
 * @param fileTypeExts
 *            可上传文件的后缀 可选
 * @param type
 *            默认上传多个 1上传单个
 * @param fileDataList
 *            已有文件的信息数组(id,name,url)
 */
function createUploadify(id, callback, pathType, buttonName, fileTypeExt,
		fileDataList, type) {
	$("#" + id).siblings('.input-group').remove();
	// 如果已有文件，则显示示文件名和链接
	var show = '<div class="input-group"><div class="maxheig143 drop-duan">'
			+ '<div class="add-lianjie"><div class="dropzone">'
			+ '<div class="fallback"></div></div></div><ul id="_fileshow" class="filebox"></ul>';
	$("#" + id).after(show);
	if (fileDataList) {
		for (var i = 0; i < fileDataList.length; i++) {
			var fileData = fileDataList[i];
			var url = fileData.url;
			var li = '<li value="'
					+ fileData.id
					+ '"><div class="filemin">'
					+ '<div class="filename"><a target="_blank" href="'
					+ url
					+ '">'
					+ fileData.name
					+ '</a></div>'
					+ '</div> <a class="fileremove" onclick="$(this).li_remove();"><i class="icon-remove-sign bigger-140 red"></i></a></li>';

			$("#" + id).siblings('.input-group').find('.filebox').append(li);
		}
	}

	var fileupload = $('#' + id);
	var thisbuttonName = '上传文件';
	var fileTypeExts = '*.pdf;*.jpg;*.png;*.PNG';
	if (buttonName)
		thisbuttonName = buttonName;
	if (fileTypeExt)
		fileTypeExts = fileTypeExt;

	var multi = true;
	if (type == 1)
		multi = false;

	fileupload
			.uploadify({
				'swf' : home_url + '/static/js/uploadify/uploadify.swf',
				'uploader' : home_url + '/fileupload/upload',
				// 按钮显示的文字
				'buttonText' : thisbuttonName,
				// 允许上传的文件后缀
				'fileTypeExts' : fileTypeExts,
				// 文件大小限制
				'fileSizeLimit' : '20MB',
				'multi' : multi,
				'method' : 'post',
				'debug' : false,
				// 响应时间 防止超时无法响应
				'successTimeout':60,
				'onUploadStart' : function(file) {
					var thispathType = 0;
					if (pathType)
						thispathType = pathType
					var param = {};
					param.pathType = thispathType;
					param.staffId = _staffId;
					param.cid = _cid;
					param.sessionid = jsessionid;
					fileupload.uploadify("settings", "formData", param);
				},
				'onUploadSuccess' : function(file, data, response) {
					var obj = eval('(' + data + ')');
					// 判断时候空间超出
					if(obj.code<0){
						alert(obj.message);
						return;
					}
					// 文件大小
					var file_size = obj.data.filesize;
					if (file_size > 1024 * 1024) {
						file_size = (file_size / 1024 / 1024).toFixed(2) + "M";
					} else if (file_size > 1024) {
						file_size = (file_size / 1024).toFixed(2) + "K";
					}
					// 上传成功显示文件名和链接
					var url = obj.data.ossurl;
					var li = '<li value="'
							+ obj.data.id
							+ '"><div class="filemin">'
							+ '<div class="filename"><a target="_blank" href="'
							+ url
							+ '">'
							+ obj.data.filename
							+ '</a></div>'
							+ '<div class="filesize">'
							+ file_size
							+ '</div>'
							+ '</div> <a class="fileremove" onclick="$(this).li_remove();"><i class="icon-remove-sign bigger-140 red"></i></a></li>';

					// $("#_fileshow").append(li);
					if (type == 1) {
						$("#" + id).siblings('.input-group').find('.filebox')
								.html(li);
					} else {
						$("#" + id).siblings('.input-group').find('.filebox')
								.append(li);
					}
					if (isFunction(callback))
						callback(obj.data);
				},
				// 检测FLASH失败调用
				'onFallback' : function() {
					alert("您未安装FLASH控件，无法上传！请安装FLASH控件后再试。");
				},
				'onUploadError' : function(file, errorCode, errorMsg,
						errorString) {
					alert('文件上传出错，文件名：' + file.name + ' 错误详情：' + errorString);
				}
			});
}

/**
 * 拼接数据，在已选list里展现
 */
function dataNodeSelected(id, name) {
	var node = '<li value="'
			+ id
			+ '" class="info"><span>'
			+ name
			+ '</span><a href="javascript:;" onclick="$(this).li_remove();"><i class="icon-remove white delete"></i> </a></li>';
	return node;
}

/**
 * 拼接数据，在已选list里展现 没有删除按钮的
 */
function dataNodeSelected2(id, name) {
	var node = '<li value="' + id + '" class="info"><span>' + name
			+ '</span></li>';
	return node;
}

/**
 * 单选员工
 */

function SelectStaff_Single(callback,array) {
	var myDialog = dialog({
		title : '人员单选',
		okValue : '确定',
		width : 710,
		height : 'auto',
		ok : function(data) {
			var type = new Object();
			type.id = $("#rselcted_staffid").val();
			type.name = $("#rselcted_staffname").val();
			type.deptid = $("#selected_deptid").val();
			type.deptname = $("#selected_deptname").val();
			type.phone = $("#selected_phone").val();
			type.idcard = $("#selected_IdCard").val();
			if (type.id == null || type.id == "") {
				alert("请先选择一个人员！");
				return false;
			}
			setTimeout(function() {
				if (isFunction(callback)) {
					callback(type);
				}
			}, 10);

			return true;
		},
		cancelValue : '取消',
		cancel : function() {
		}
	});
	$.ajax({
		url : home_url + '/tool/SelectStaff_Single',
		data : {
			list : array
		},
		success : function(data) {
			myDialog.content(data);
		}
	});
	myDialog.showModal();

}

/**
 * 多选员工
 * 
 * @param unorderlistId
 *            多选人员所在的ul的id
 * 
 */
function SelectStaff_Multi(unorderlistId) {
	var myDialog = dialog({
		title : '人员多选',
		okValue : '确定',
		ok : function() {
			$('#_selectedList li').each(function() {
				$('#' + unorderlistId).append($(this));
			});
		},
		cancelValue : '取消',
		cancel : function() {
		}
	});
	$.ajax({
		url : home_url + '/tool/SelectStaff_Multi',
		success : function(data) {
			myDialog.content(data);
		}
	});
	myDialog.showModal();
}

/**
 * 单选区域
 */

function SelectArea_Single(callback) {
	showDialogModal("区域单选", home_url + "/tool/SelectArea_Single",
			function(data) {
				var type = new Object();
				type.id = $("#selected_areaid").val();
				type.name = $("#selected_areaname").val();
				type.pid = $("#p_areaid").val();
				type.pname = $("#p_areaname").val();
				type.pid2 = $("#p_areaid2").val();
				type.pname2 = $("#p_areaname2").val();
				if (type.id == null || type.id == "") {
					alert("请选择一条数据 ！");
					return false;
				}
				setTimeout(function() {
					if (isFunction(callback)) {
						callback(type);
					}
				}, 10);

				return true;
			}, 360, 360);

}

/**
 * 单选房间(只能选最后一级) *
 * 
 * @param list
 *            已选择的
 */
function SelectRoom_Single(callback, array) {
	var myDialog = dialog({
		title : '区域单选',
		okValue : '确定',
		width : 360,
		height : 360,
		ok : function(data) {
			var type = new Object();
			type.id = $("#selected_areaid").val();
			type.name = $("#selected_areaname").val();
			type.pid = $("#p_areaid").val();
			type.pname = $("#p_areaname").val();
			type.pid2 = $("#p_areaid2").val();
			type.pname2 = $("#p_areaname2").val();
			type.areaid = $("#areaId").val();
			type.areaName = $("#areaName").val();
			if (type.id == null || type.id == "") {
				alert("请选择一条数据 ！");
				return false;
			}
			setTimeout(function() {
				if (isFunction(callback)) {
					callback(type);
				}
			}, 10);
			return true;
		},
		cancelValue : '取消',
		cancel : function() {
		}
	});
	$.ajax({
		url : home_url + '/tool/SelectRoom_Single',
		data : {
			list : array
		},
		success : function(data) {
			myDialog.content(data);
		}
	});
	myDialog.showModal();

}
/**
 * 多选区域
 * 
 * @param unorderlistId
 *            多选区域所在的ul的id
 * 
 */
function SelectArea_Multi(unorderlistId) {
	var myDialog = dialog({
		title : '区域多选',
		okValue : '确定',
		ok : function() {
			var treeObj = $.fn.zTree.getZTreeObj("muitiSelectArea");
			var nodes = treeObj.getCheckedNodes(true);
			if (nodes == null || nodes.length == 0) {
				alert("请选择一条数据！");
				return false;
			}
			for (var i = 0; i < nodes.length; i++) {
				var listItem = dataNodeSelected(nodes[i].id, nodes[i].name);
				$('#' + unorderlistId).append(listItem);
			}
		},
		cancelValue : '取消',
		cancel : function() {
		}
	});
	$.ajax({
		url : home_url + '/tool/SelectArea_Multi',
		success : function(data) {
			myDialog.content(data);
		}
	});
	myDialog.showModal();
}

/**
 * 多选区域(只能选最后一级) *
 * 
 * @param array
 *            传递数组
 * @param unorderlistId
 *            多选区域所在的ul的id
 * 
 */
function SelectRoom_Multi(callback, array, unorderlistId) {
	var list = new Array();
	list = array;
	var myDialog = dialog({
		title : '区域多选',
		okValue : '确定',
		ok : function() {
			var treeObj = $.fn.zTree.getZTreeObj("muitiSelectArea");
			var nodes = treeObj.getCheckedNodes(true);
			if (nodes == null || nodes.length == 0) {
				alert("请选择一条数据！");
				return false;
			}
			// id不为空
			if (unorderlistId !== null || unorderlistId !== undefined
					|| unorderlistId !== '') {
				for (var i = 0; i < nodes.length; i++) {
					var listItem = dataNodeSelected(nodes[i].id, nodes[i].name);
					$('#' + unorderlistId).append(listItem);
				}
			}
			// 为空回调
			if (isFunction(callback)) {
				callback(nodes);
			}
		},
		cancelValue : '取消',
		cancel : function() {
		}
	});
	$.ajax({
		url : home_url + '/tool/SelectRoom_Multi',
		data : {
			list : list
		},
		success : function(data) {
			myDialog.content(data);
		}
	});
	myDialog.showModal();
}

// 元素周期表
/**
 * okcallback 确认的时候回调
 */
function element(okcallback) {
	var data = null;
	showDialogModal("元素周期表", home_url + '/tool/SelectElement', function() {
		var str = "";
		if (data == null || data.id == undefined || data.id == "") {
			alert("请先选择一个核素！");
			return false;
		}

		if (data != null) {
			str = '<li value="' + data.id + '" name="' + data.name
					+ '" class="inverse">' + '<span>' + data.name + '</span>'
					+ '<a href="javascript:;" onclick="$(this).li_remove();">'
					+ ' <i class="icon-remove white"></i>' + '</a>' + '</li>';
		}
		okcallback(str);

	}, 880, 600, function() {

		$("#element_e table td div a").click(
				function() {
					var name = $(this).attr("name");
					var reg = new RegExp("^11[2-8]*$");
					if (name == undefined || name == "" || reg.test(name))
						return;

					showDialogModal("元素", home_url + '/element/elementById?id='
							+ name, function() {
						// input -> label -> div -> li
						var $li = $("#isotope input:checked").parent().parent()
								.parent();
						data = {
							"name" : $li.attr("name"),
							"id" : $li.val()
						};

						$(".ui-dialog-grid button[i-id='ok']").eq(0).click();// 点击元素周期表的确认键
					}, 550, 300, function() {

					});
				});
	});

}

/**
 * 设备多选模块 okcallback 确认的时候回调 entrance 值为[1]只显示射线装置 [2]显示非密封放射 [3]显示含源装置
 * [4]显示密封放射源 默认显示全部 [1,2,3,4]
 */
function EDevice_Multi(okcallback, entrance) {
	if (entrance == undefined)
		entrance = [ 1, 2, 3, 4 ];
	var str = "";
	for (var i = 0; i < entrance.length; i++) {
		str += "entrance[]=" + entrance[i];
		if (entrance.length - 1 > i) {
			str += "&";
		}
	}
	showDialogModal("设备设施多选", home_url + '/tool/device?isFiltered=1&' + str,
			function() {
				okcallback(getEDeviceIds(true));
			}, 710, 300, function() {

			});
}

// 获取被选中的设备id sign 为true的时候所有数据放一个集合中反之 则放一个二维集合中
// [0]射线装置ID列表 [1]非密封放射源ID列表 [2]密封放射源ID列表 [3]含源装置ID列表
function getEDeviceIds(sign) {
	var arr = new Array();
	var rayList = new Array();// 射线
	var unsealedList = new Array();// 非密封
	var sealedList = new Array();// 密封
	var containerList = new Array();// 含源
	// +++++++++++++++++++++++++++++++++++++++++++++射线装置++++++++++++++++++++++++
	var ids = $('#RayDeviceTable').jqGrid('getGridParam', 'selarrrow');
	if (ids != undefined && ids.length > 0)
		for (var i = 0; i < ids.length; i++) {
			var d = $('#RayDeviceTable').jqGrid('getRowData', ids[i]);
			var data = {
				name : d.deviceName,
				id : ids[i]
			};
			if (sign) {
				arr.push(data);
			} else {
				rayList.push(data);
			}

		}
	if (!sign) {
		arr.push(rayList);
	}

	// +++++++++++++++++++++++++++++++++++++++++++++非密封放射++++++++++++++++++++++++
	var ids = $('#UnsealedNuclideTable').jqGrid('getGridParam', 'selarrrow');
	if (ids != undefined && ids.length > 0)
		for (var i = 0; i < ids.length; i++) {
			var d = $('#UnsealedNuclideTable').jqGrid('getRowData', ids[i]);
			var data = {
				name : d.nuclideName,
				id : ids[i]
			};
			if (sign) {
				arr.push(data);
			} else {
				unsealedList.push(data);
			}
		}
	if (!sign) {
		arr.push(unsealedList);
	}
	// +++++++++++++++++++++++++++++++++++++++++++++密封放射++++++++++++++++++++++++
	var ids = $('#SealedRadioactiveTable').jqGrid('getGridParam', 'selarrrow');
	if (ids != undefined && ids.length > 0)
		for (var i = 0; i < ids.length; i++) {
			var d = $('#SealedRadioactiveTable').jqGrid('getRowData', ids[i]);
			var data = {
				name : d.nuclideName,
				id : ids[i]
			};
			if (sign) {
				arr.push(data);
			} else {
				sealedList.push(data);
			}
		}
	if (!sign) {
		arr.push(sealedList);
	}
	// +++++++++++++++++++++++++++++++++++++++++++++含源++++++++++++++++++++++++
	var ids = $('#SourceDeviceTable').jqGrid('getGridParam', 'selarrrow');
	if (ids != undefined && ids.length > 0)
		for (var i = 0; i < ids.length; i++) {
			var d = $('#SourceDeviceTable').jqGrid('getRowData', ids[i]);
			var data = {
				name : d.deviceName,
				id : ids[i]
			};
			if (sign) {
				arr.push(data);
			} else {
				containerList.push(data);
			}
		}
	if (!sign) {
		arr.push(containerList);
	}
	return arr;
}

/**
 * 设备多选模块 三同时使用 (含有装置也调用啦此方法)
 * 
 * @param gridList
 *            数组分别对应jqGrid table的id【射线装置，非密封放射，含源装置，密封放射源】
 * @param rayList
 *            射线装置ID列表
 * @param unsealedList
 *            非密封放射源ID列表
 * @param sealedList
 *            密封放射源ID列表
 * @param containerList
 *            含源装置ID列表
 * @param entrance
 *            [1]只显示射线装置 [2]显示非密封放射 [3]显示密封放射源 [4]显示含源装置 默认显示全部 [1,2,3,4]
 * @param okcallback
 *            确认时候的回调函数
 * @param filtered
 *            0.展示4个id列表的中数据 1 显示全部数据 2.展示除4个id列表中的数据 默认值为0
 */
function EDevice_Project(gridList, rayList, unsealedList, sealedList,
		containerList, entrance, okcallback, filtered) {

	var isFiltered = 0; // 是否展示筛选后的数据
	if ((rayList == undefined && unsealedList == undefined
			&& sealedList == undefined && containerList == undefined)
			|| (rayList == null && unsealedList == null && sealedList == null && containerList == null))
		isFiltered = 1;

	if (filtered != undefined && filtered == 2) {
		isFiltered = 2;
	}
	if (entrance == undefined)
		entrance = [ 1, 2, 3, 4 ];

	var myDialog = dialog({
		title : '设备选择',
		width : 710,
		height : 'auto',
		okValue : '确定',
		ok : function() {
			if (gridList != null && gridList.length == 4) {
				initDeviceGrid(gridList);
			}
			// 为 2 的时候返回被选中的数据的id 以二维集合的形式
			// 顺序[0]射线装置ID列表 [1]非密封放射源ID列表 [2]密封放射源ID列表 [3]含源装置ID列表
			if (okcallback != undefined && okcallback != null) {
				okcallback(getEDeviceIds(false));
			}

		},
		cancelValue : '取消',
		cancel : function() {
		}
	});
	$.ajax({
		url : home_url + '/tool/device',
		data : {
			isFiltered : isFiltered,
			entrance : entrance,
			rayList : rayList,
			unsealedList : unsealedList,
			sealedList : sealedList,
			containerList : containerList
		},
		success : function(data) {
			myDialog.content(data);

		}
	});
	myDialog.showModal();

}

/**
 * 判断设备是否已存在
 * 
 * @param gridList
 *            4种类型的设备表格
 * @param index
 *            设备表格的序号，1,2,3,4
 * 
 * @param deviceid
 *            设备id
 */
function isDeviceExist(gridList, index, deviceid) {
	var data = $("#" + gridList[index]).jqGrid("getRowData");
	for (var j = 0; j < data.length; j++) {
		if (data[j].id == deviceid)
			return true;
	}
	return false;
}

// 三同时设备操作
function initDeviceGrid(gridList) {
	// +++++++++++++++++++++++++++++++++++++++++++++射线装置++++++++++++++++++++++++
	var ids = $('#RayDeviceTable').jqGrid('getGridParam', 'selarrrow');
	if (ids != undefined && ids.length > 0) {
		for (var i = 0; i < ids.length; i++) {
			if ($.inArray("1", entrance) != -1) {
				var d = $('#RayDeviceTable').jqGrid('getRowData', ids[i]);
				if (!isDeviceExist(gridList, 0, ids[i]))
					$("#" + gridList[0]).addRowData(ids[i], d, "last");
			}
		}
	}

	// +++++++++++++++++++++++++++++++++++++++++++++非密封放射++++++++++++++++++++++++
	var ids = $('#UnsealedNuclideTable').jqGrid('getGridParam', 'selarrrow');
	if (ids != undefined && ids.length > 0)
		for (var i = 0; i < ids.length; i++) {
			if ($.inArray("2", entrance) != -1) {
				var d = $('#UnsealedNuclideTable').jqGrid('getRowData', ids[i]);
				if (!isDeviceExist(gridList, 1, ids[i]))
					$("#" + gridList[1]).addRowData(ids[i], d, "last");
			}
		}
	// +++++++++++++++++++++++++++++++++++++++++++++密封放射++++++++++++++++++++++++
	var ids = $('#SealedRadioactiveTable').jqGrid('getGridParam', 'selarrrow');
	if (ids != undefined && ids.length > 0)
		for (var i = 0; i < ids.length; i++) {
			if ($.inArray("3", entrance) != -1) {
				var d = $('#SealedRadioactiveTable').jqGrid('getRowData',
						ids[i]);
				if (!isDeviceExist(gridList, 2, ids[i]))
					$("#" + gridList[2]).addRowData(ids[i], d, "last");
			}
		}
	// +++++++++++++++++++++++++++++++++++++++++++++含源++++++++++++++++++++++++
	var ids = $('#SourceDeviceTable').jqGrid('getGridParam', 'selarrrow');
	if (ids != undefined && ids.length > 0)
		for (var i = 0; i < ids.length; i++) {
			if ($.inArray("4", entrance) != -1) {
				var d = $('#SourceDeviceTable').jqGrid('getRowData', ids[i]);
				if (!isDeviceExist(gridList, 3, ids[i]))
					$("#" + gridList[3]).addRowData(ids[i], d, "last");
			}
		}
}

// $("#RayDeviceTable").addRowData("99", c, "last"); id 数据 位置
/**
 * 警示标识位置多选 okcallback 确认的时候回调 $listId 警示位置ul的id
 */
function WarningMark_Multi($listId, okcallback) {

	var myDialog = dialog({
		title : '警示标识位置',
		width : 350,
		lock : true,
		okValue : '确认',
		ok : function() {
			var _location = "";// 存储所有的位置名称 中间，隔开

			var $input = $(".model-body input:checked");// 获取被选中的
			if ($input.length != 0) {
				$input
						.each(function() {
							var str = true;
							var ztreeLi = $(this);
							$listId.find("li").each(function() {// 遍历已有元素 和 新增
								// 元素是否相同
								if (ztreeLi.val() == $(this).attr("name")) {
									str = false;
								}
							});
							if (str) {// 判断数据是否重复
								$listId
										.append('<li class="warning" name="'
												+ $(this).val()
												+ '">'
												+ '<span>'
												+ $(this).val()
												+ '</span>'
												+ '<a href="javascript:;" onclick="$(this).li_remove();">'
												+ ' <i class="icon-remove white"></i>'
												+ '</a></li>');
							}

						});

			}
			okcallback();
		},
		cancelValue : '取消',
		cancel : function() {
		}
	});

	$.ajax({
		url : home_url + '/DLive/tool/SelectWarningMark',
		success : function(data) {
			myDialog.content(data);
		}
	});

	myDialog.showModal();

}

/**
 * 设备单选模块 okcallback 确认的时候回调 entrance 值为[1]只显示射线装置 [2]显示非密封放射 [3]显示含源装置
 * [4]显示密封放射源 默认显示全部 [1,2,3,4]
 */
function EDevice(okcallback, entrance) {
	if (entrance == undefined)
		entrance = [ 1, 2, 3, 4 ];
	var str = "";
	for (var i = 0; i < entrance.length; i++) {
		str += "entrance=" + entrance[i];
		if (entrance.length - 1 > i) {
			str += "&";
		}
	}
	str += "&judge=true";// 用来区分单选多选
	showDialogModal("设备设施单选", home_url + '/EDevice/device?' + str, function() {
		var data = {};
		$("#sheb input:radio:checked").each(function(index, element) {// 获取所有选中的框框
			if ($(this).val() != "") {
				data = {
					name : $(this).val(),
					id : $(this).attr("dataId")
				};
			}
		});
		okcallback(data);
	}, 710, 300, function() {

	});
}

/**
 * 第三方企业单选 type 含义 1.个人剂量监测单位； 2.职业健康检查单位； 3.评价与检测机构； 4.供货商； 5.生产厂家； 6.设计单位；7
 * 施工单位 7.施工单位；
 */
function Company_Single(okcallback, type) {

	showDialogModal("选择第三方企业", home_url + '/tool/SelectCompany_Single?type='
			+ type, function() {
		var $radio = $("#_company").find('ul > li input:checked');
		var str = "";
		if ($radio.val() == undefined) {
			alert("请先选择选择一个企业！");
			return false;
		}
		if ($radio.val() != undefined) {
			var certName = $radio.attr('certName');//
			var certNo = $radio.attr('certNo');
			var managername = $radio.attr('managername');// 联系人
			var managertel = $radio.attr('managertel');// 联系电话
			str = '<li value="' + $radio.attr("dataId") + '" name="'
					+ $radio.val() + '" class="warning" managername="'
					+ managername + '" managertel="' + managertel + '">'
					+ '<span>' + $radio.val() + '</span>'
					+ '<a href="javascript:;" onclick="$(this).li_remove();">'
					+ ' <i class="icon-remove white"></i>' + '</a>'
					+ '<input type="hidden" name="certName" value=" '
					+ certName + '" />'
					+ ' <input type="hidden" name="certNo" value="' + certNo
					+ '">' + '</li>';
		}
		okcallback(str);
	}, 710, 300, function() {

	});
}

/**
 * 第三方企业单选 type 含义 1.个人剂量监测单位； 2.职业健康检查单位； 3.评价与检测机构； 4.供货商； 5.生产厂家； 6.设计单位；
 * 7.施工单位； edit by yanglei
 */
function Company_Single_edit(okcallback, type) {

	showDialogModal("选择第三方企业", home_url + '/tool/SelectCompany_Single?type='
			+ type, function() {
		var $radio = $("#_company").find('ul > li input:checked');
		var data = new Object();
		if ($radio.val() != undefined) {
			data.companyName = $radio.val();
			data.id = $radio.attr("dataId");
			data.managername = $radio.attr("managername");
			data.managertel = $radio.attr("managertel");
		} else {
			alert("请先选择一个数据！");
			return false;
		}
		okcallback(data);
	}, 710, 300, function() {

	});
}
// 编辑房间
function editRoomForThird(id) {
	showDialogModal("编辑房间", home_url + "/area/editarea2?&id=" + id, function() {
		var dialog_typename = $("#dialog_Roomname").val();
		// 验证 验证失败则不进行下一步
		if (!$("#RoomForm").valid()) {
			return false;
		}
		submitForm("#RoomForm", home_url + '/area?method=editArea', null,
				function(data) {
					_contentLoadTriggered = false;
					if (data.code == 0) {
						alert("编辑成功！");
					} else {
						// showTipDialog(data.message);
					}
				}, 'json');
	}, 400, 240);
}
/**
 * 单选组织
 */

function SelectOrg_Single(callback) {
	showDialogModal("组织单选", home_url + "/tool/SelectOrg_Single",
			function(data) {
				var type = new Object();
				type.id = $("#selected_orgid").val();
				type.name = $("#selected_orgname").val();
				setTimeout(function() {
					if (isFunction(callback)) {
						callback(type);
					}
				}, 10);
				return true;
			}, 360, 360);

}

/**
 * 选择院区
 */
function SelectAreaLevel1(callback) {
	showDialogModal("所在院区", home_url + "/area/queryAreaByLevel?level=1",
			function(data) {
				var type = new Object();
				type.id = $('#areaListId input[name="areaid"]:checked ').val();
				type.name = $('#areaListId input[name="areaid"]:checked ')
						.next().text();
				setTimeout(function() {
					if (isFunction(callback)) {
						callback(type);
					}
				}, 10);
				return true;
			}, 720, 240);

}
/**
 * 选择房间即没有下一级
 */
function SelectAreaLevel5(callback, list) {
	var myDialog = dialog({
		title : '所在房间',
		width : 750,
		lock : true,
		okValue : '确认',
		ok : function() {
			var type = new Object();
			var check = $('#areaListId input[name="areaid"]:checked ');
			type.id = check.val();
			type.name = check.next().text();
			type.parentName = check.attr("parentName");
			type.parentId = check.attr("parentId");
			type.grandparentName = check.attr("grandparentName");
			type.grandparentid = check.attr("grandparentid");
			setTimeout(function() {
				if (isFunction(callback)) {
					callback(type);
				}
			}, 10);
			return true;
		},
		cancelValue : '取消',
		cancel : function() {
		}
	});
	$.ajax({
		url : home_url + '/area/queryRoom',
		type : "post",
		data : {
			idList : list
		},
		success : function(data) {
			myDialog.content(data);
		}
	});

	myDialog.showModal();

}
/**
 * 选择岗位
 */
function SelectWorkRole(callback) {
	showDialogModal("岗位/工种", home_url + "/workrole/queryworkrole", function(
			data) {
		var type = new Object();
		type.id = $('#workListId input[name="workid"]:checked ').val();
		type.name = $('#workListId input[name="workid"]:checked ').next()
				.text();
		setTimeout(function() {
			if (isFunction(callback)) {
				callback(type);
			}
		}, 10);
		return true;
	}, 720, 240);
}
/**
 * 选择证书类型
 */
function getCertificateType(callback) {
	showDialogModal("证书类型", home_url + "/tool/getCertificateType", function(
			data) {
		var type = new Object();
		type.cernameId = $("#selected_cernameId").val();
		type.cername = $("#selected_cername").val();
		type.typeCode = $("#selected_typeCode").val();
		type.typeName = $("#selected_typeName").val();

		if (type.typeName.length == 0) {
			alert("请先选择证书类型！");
			return false;
		}

		setTimeout(function() {
			if (isFunction(callback)) {
				callback(type);
			}
		}, 10);
		return true;
	}, 710, 'auto');

}

/**
 * 警示标识维护对象多选 返回一个集合其中存取的是对象 集合中的 对象存取id 和 name(标示位置) areaName三个属性(以单个警示标志为单位)
 * 
 * @param okcallback
 *            回调方法
 */
function warningMarkMaintain_Multi(okcallback) {

	showDialogModal("维护对象", home_url + '/tool/warningMarkMaintain', function() {
		var data = new Array();// 外层集合

		var $li = $("#myTab3 li");
		for (var i = 0; i < $li.length; i++) {// 遍历li来遍历其对应的Table中的多选

			$(
					"#sheb #tab" + i
							+ " tr input[name='form-field-checkbox']:checked")
					.each(function() {// 获取table中被选中的
						var arr = {};
						var name = $(this).val();// 标示位置
						var id = $(this).attr("pid");// 警示标识的id
						var areaName = $(this).attr("areaName");// 警示标识的区域名字
						arr.name = name;
						arr.id = id;
						arr.areaName = areaName;
						data.push(arr);
					});

		}
		if (isFunction(okcallback)) {
			okcallback(data);
		}
	}, 710, 350, function() {

	});
}

// 日期转换
var formatDate = function(date) {
	var y = date.getFullYear();
	var m = date.getMonth() + 1;
	m = m < 10 ? '0' + m : m;
	var d = date.getDate();
	d = d < 10 ? ('0' + d) : d;
	return y + '-' + m + '-' + d;
};
//对Date的扩展，将 Date 转化为指定格式的String   
//月(M)、日(d)、小时(h)、分(m)、秒(s)、季度(q) 可以用 1-2 个占位符，   
//年(y)可以用 1-4 个占位符，毫秒(S)只能用 1 个占位符(是 1-3 位的数字)   
//例子：   
//(new Date()).Format("yyyy-MM-dd hh:mm:ss.S") ==> 2006-07-02 08:09:04.423   
//(new Date()).Format("yyyy-M-d h:m:s.S")      ==> 2006-7-2 8:9:4.18  
Date.prototype.Format = function(fmt)   
{ //author: meizz   
  var o = {   
    "M+" : this.getMonth()+1,                 //月份   
    "d+" : this.getDate(),                    //日   
    "h+" : this.getHours(),                   //小时   
    "m+" : this.getMinutes(),                 //分   
    "s+" : this.getSeconds(),                 //秒   
    "q+" : Math.floor((this.getMonth()+3)/3), //季度   
    "S"  : this.getMilliseconds()             //毫秒   
  };   
  if(/(y+)/.test(fmt))   
    fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length));   
  for(var k in o)   
    if(new RegExp("("+ k +")").test(fmt))   
  fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));   
  return fmt;   
}  

// 根据文件id获取文件名字
function getfileName(fileid) {
	var filename = "";
	$.ajax({
		url : home_url + "/tool/getFile",
		dataType : "json",
		type : "post",
		async : false,
		data : {
			fileid : fileid
		},
		success : function(JsonData) {
			var data = JsonData.data;
			if(data!=null){
				filename = data.filename;
			}
		}
	});
	return filename;
}

// 根据文件id获取文件url
function getfileUrl(fileid) {
	var fileurl = "";
	$.ajax({
		type : "POST",
		async : false,
		url : home_url + "/tool/getFile",
		dataType : "json",
		data : {
			fileid : fileid
		},
		success : function(JsonData) {
			var data = JsonData.data;
			if(data!=null){
				fileurl = data.ossurl;
			}
		}
	});
	return fileurl;
}

// 根据员工id获取员工姓名
function getStaffName(staffId) {
	var name = "";
	$.ajax({
		type : "POST",
		async : false,
		url : home_url + "/tool/getStaffName",
		dataType : "json",
		data : {
			staffId : staffId
		},
		success : function(JsonData) {
			var data = JsonData.data;
			if(data!=null){
				name = data;
			}
		}
	});
	return name;
}

// 身份证号码的验证规则
function isIdCardNo(num, type) {
	 num = num.toUpperCase();  
	 if (type == 0) {
	if (!(/(^\d{15}$)|(^\d{17}([0-9]|X)$)/.test(num))) { 
		
			return false;
		}
	 }
	 return true;
 }

// 出生日期验证
function isIdCardNo1(num, type) {
if (type == 0) {
		var len = num.length, re;
		if (len == 15)
			re = new RegExp(/^(\d{6})()?(\d{2})(\d{2})(\d{2})(\d{2})(\w)$/);
		else if (len == 18)
			re = new RegExp(/^(\d{6})()?(\d{4})(\d{2})(\d{2})(\d{3})(\w)$/);
	var a = num.match(re);
	if (a != null) {
		if (len == 15) {
			var D = new Date("19" + a[3] + "/" + a[4] + "/" + a[5]);
			var B = D.getYear() == a[3] && (D.getMonth() + 1) == a[4]
					&& D.getDate() == a[5];
		} else {
			var D = new Date(a[3] + "/" + a[4] + "/" + a[5]);
			var B = D.getFullYear() == a[3] && (D.getMonth() + 1) == a[4]
					&& D.getDate() == a[5];
		}
		if (!B) {
			// alert("输入的身份证号 "+ a[0] +" 里出生日期不对。");
			return false;
		}
	}
}
	return true;
}

// 地址验证
function isIdCardNo2(num, type) {
	num = num.toUpperCase();
	if (type == 0) {
		var aCity={11:"北京",12:"天津",13:"河北",14:"山西",15:"内蒙古",
				21:"辽宁",22:"吉林",23:"黑龙江 ",31:"上海",32:"江苏",
				33:"浙江",34:"安徽",35:"福建",36:"江西",37:"山东",
				41:"河南",42:"湖北",43:"湖南",44:"广东",45:"广西",
				46:"海南",50:"重庆",51:"四川",52:"贵州",53:"云南",
				54:"西藏",61:"陕西",62:"甘肃",63:"青海",64:"宁夏",
				65:"新疆",71:"台湾",81:"香港",82:"澳门",91:"国外"};  
	
	if(aCity[parseInt(num.substr(0,2))]==null){
     
        return false;
    }
	}
	return true;
}

// 性别验证
function isIdCardNo3(num, type ,sex) {
	if (type == 0) {
	idCard = num;        // 对身份证号码做处理。包括字符间有空格。
    if(idCard.length==15){   
        if(idCard.substring(14,15)%2==0){  
        	if(sex!=0)
            return false;   
        }else{   
        	if(sex!=1)
            return false;   
        }   
    }else if(idCard.length ==18){   
        if(idCard.substring(14,17)%2==0){   
        	if(sex!=0)
                return false;   
        }else{   
        	if(sex!=1)
                return false;   
        }   
    }else{   
        return false;   
    }   
    return true ;
	}
	return true;
}
		
// 护照不能中文
function isIdCardNo4(num, type) {
	if (type == 1) {
		var reg = new RegExp("[\\u4E00-\\u9FFF]+","g");
		if(reg.test(num)){
			return false;
		}
	}
	return true;
}

// 身份证与出生日期匹配
function isIdCardNo5(num, type , date) {
	if (type == 0) {
		var dateTemp= new Date(); 
	    if(date != ""){ 
	    var arr_date = date.split("/"); 
	    if(arr_date.length<3){
	    	arr_date = date.split("-");
	    }
	     // 15位身份证
	     if(num.length == 15){ 
	      // 从ID NO 中截取生日6位数字，前面加上19
	      var idBirthday = "19"+num.substr(6,6); 
	      // 日期字符串中的8位生日数字
	      var textBirthday = arr_date[0]+arr_date[1]+arr_date[2]; 
	      if(idBirthday == textBirthday){ 
	          return true; 
	      }else{
	       return false; 
	      }               
	    } 
	    // 18位身份证
	     if(num.length == 18){ 
	      // 从ID NO 中截取生日8位数字
	      var idBirthday = num.substr(6,8); 
	      // 日期字符串中的8位生日数字
	      var textBirthday = arr_date[0]+arr_date[1]+arr_date[2]; 
	      // alert(idBirthday);
	      // alert(textBirthday);
	      if(idBirthday == textBirthday){ 
	         return true; 
	      }else{  
	       return false; 
	      } 
	     } 
	    } 
	   return true;
	
	}
	return true;
}
/**
 * 现在radio通用隐藏域验证
 * 
 * @param radioValue
 *            radio的值
 * @param hiddenId
 *            隐藏域的id
 */
function changeRadio(radioValue,hiddenId){
	$("#"+hiddenId).val(radioValue);
}
