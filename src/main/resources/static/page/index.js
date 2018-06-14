$(function() {

	//jquery file upload demo
	$('#fileupload_jq').fileupload({
		url :  home_url + '/fileupload/upload',
		dataType : 'json',
		done : function(e, data) {
			$.each(data.result.files, function(index, file) {
				$('<p/>').text(file.name).appendTo('#files');
			});
		},
		progressall : function(e, data) {
			var progress = parseInt(data.loaded / data.total * 100, 10);
			$('#progress .progress-bar').css('width', progress + '%');
		}
	}).prop('disabled', !$.support.fileInput).parent().addClass(
			$.support.fileInput ? undefined : 'disabled');

	//uploadify5
	$('#uploadify5').Huploadify({
		auto : true,
		fileTypeExts : '*.jpg;*.png;*.exe',
		multi : true,
		formData : {
			pathType : 2,
			staffId : _staffId
		},
		fileSizeLimit : 9999,
		showUploadedPercent : true,// 是否实时显示上传的百分比，如20%
		showUploadedSize : true,
		removeTimeout : 9999999,
		uploader : home_url + '/fileupload/upload',
		onUploadStart : function() {
			alert('开始上传');
		},
		onInit : function() {
			// alert('初始化');
		},
		onUploadComplete : function() {
			// alert('上传完成');
		},
		onDelete : function(file) {
			console.log('删除的文件：' + file);
			console.log(file);
		}
	});

	var fileupload = $('#file_upload');
	fileupload.uploadify({
		'swf' : home_url + '/static/js/uploadify/uploadify.swf',
		'uploader' : home_url + '/fileupload/upload;sessionid=' + jsessionid,
		// 按钮显示的文字
		'buttonText' : '资质证书',
		// 允许上传的文件后缀
		'fileTypeExts' : '*.pdf;*.jpg;*.png;*.PNG',
		'multi' : true,
		'method' : 'post',
		'debug' : false,
		'onUploadStart' : function(file) {
			var param = {};
			param.pathType = "2";// 企业信息路径
			param.staffId = _staffId;
			fileupload.uploadify("settings", "formData", param);
		},
		'onUploadSuccess' : function(file, data, response) {
			var obj = eval('(' + data + ')');
			$("#fileid").val(obj.data.id);
			$("#showimgs").attr("src", "${ctx }/upload/" + obj.data.ossurl);

		},
		'onUploadError' : function(file, errorCode, errorMsg, errorString) {
			alert('文件上传出错，文件名：' + file.name + ' 错误详情：' + errorString);
		}
	// Your options here
	});
});

// 人员单选
function selectStaff1() {
	SelectStaff_Single(function(data) {
		$("#staffList1 li").remove();
		$("#staffList1").append(dataNodeSelected(data.id, data.name));
	});
}

// 人员多选
function selectStaff2() {
	SelectStaff_Multi('staffList2');
}

// 区域单选
function selectArea1() {
	SelectArea_Single(function(data) {
		// 本级区域
		$("#areaList1").append(dataNodeSelected(data.id, data.name));
		// 上级区域
		$("#areaList1").append(dataNodeSelected(data.pid, data.pname));
	});
}
//区域单选
function selectRoom_Single() {
	SelectRoom_Single(function(data) {
		// 本级区域
		$("#areaList1").append(dataNodeSelected(data.id, data.name));
		// 上级区域
		$("#areaList1").append(dataNodeSelected(data.pid, data.pname));
	});
}

// 区域多选
function selectArea2() {
	SelectArea_Multi('areaList2');
}

//区域多选(房间)
function selectRoom_Multi() {
	SelectRoom_Multi(function(data) {
		console.log(data);
	},'areaList2');
}

//元素周期表 单选
function getElement(){
	element(function(data){
		$("#element").html("");
		$("#element").append(data);
	});
}

//设备多选
function getDevics(){
	EDevice_Multi(function(data){//返回的是一个数组  里面是对象 对象有两个属性 id  和  name
		var str = "";
		for(var i = 0; i < data.length; i++){
			str += '<li value="'+ data[i].id +'" name="'+ data[i].name +'" class="primary">'+
    					'<span>'+ data[i].name +'</span>'+
    					'<a href="javascript:;" onclick="$(this).li_remove();">'+
    					'<i class="icon-remove white"></i>'+
    					'</a>'+
    				'</li>'
		}
		$("#devics").append(str);;
	});//EDevice_Multi(function(){},[1,2,3,4] )   [1]只显示射线装置 [2]显示非密封放射 [3]显示密封放射源 [4]只显示含源装置   不给显示全部
}


/*//设备单选
function getDevic(){
	EDevice(function(data){//返回的是一个数组  里面是对象 对象有两个属性 id  和  name
		$("#devic").html("");//清空数据
		var str = "";
		if(data.id != undefined && data.id != ""){
			str += '<li value="'+ data.id +'" name="'+ data.name +'" class="primary">'+
						'<span>'+ data.name +'</span>'+
						'<a href="javascript:;" onclick="$(this).li_remove();">'+
						'<i class="icon-remove white"></i>'+
						'</a>'+
					'</li>'
		}
		$("#devic").append(str);;
	});//EDevice(function(){},[1,2,3,4] )   [1]只显示射线装置 [2]显示非密封放射 [3]显示密封放射源 [4]只显示含源装置   不给显示全部
}*/

//第三方企业单选
function getCompany(){
	Company_Single(function(data){
		$("#Company").html("");//清空数据
		$("#Company").append(data.str);;
	},1);//1.个人剂量监测单位；2.职业健康检查单位；3.评价与检测机构； 4.供货商；5.生产厂家；6.设计单位；7.施工单位；
}

//组织单选
function selectorganize() {
	SelectOrg_Single(function(data) {
		$("#Company").html("");//清空数据
	});
}


//所在房间或区域
function selectArea() {
	SelectAreaLevel5(function(data) {
		console.log(data);
		$("#areaulId li").remove();
		var str = ' <li class="warning"><span>'
				+ data.name
				+ '</span> <a href="javascript:;" onclick="$(this).li_remove();"> <i class="icon-remove white"></i> </a></li>';
		str += ' <li class="warning"><span>'
			+ data.parentName
			+ '</span> <a href="javascript:;" onclick="$(this).li_remove();"> <i class="icon-remove white"></i> </a></li>';
		str+= ' <li class="warning"><span>'
			+ data.grandparentName
			+ '</span> <a href="javascript:;" onclick="$(this).li_remove();"> <i class="icon-remove white"></i> </a></li>';
		$("#areaList1").append(str);
	});

}