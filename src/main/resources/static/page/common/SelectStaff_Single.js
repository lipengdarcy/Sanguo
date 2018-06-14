$(document).ready(function() {
	// alert("树形组织架构加载成功!");
	var setting = {
		view : {
			selectedMulti : false
		},
		data : {
			simpleData : {
				enable : true
			}
		},
		callback : {
			onClick : onClick
		}
	};

	// 弹窗提示配置
	var hsArtDialog = dialog({
		title : '提示',
		id : "hs-dialog",
		fixed : true,
		width : 300,
		height : 50
	});
	// 查询机构
	var orgList = [];
	$.ajax({
		url : home_url + '/org?method=queryOrg',
		data : {},
		type : 'post',
		dataType : 'json',

		success : function(data) {
			var r = data.data;
			for (var i = 0; i < r.length; i++) {
				var item = {
					id : r[i].id,
					pId : r[i].pid,
					name : r[i].fullname
				};
				orgList[i] = item;
			}
			// 初始化树节点
			$.fn.zTree.init($("#orgTree"), setting, orgList);
			// 展开全部
			// expandZtree("orgTree");
			$.fn.zTree.getZTreeObj("orgTree").expandAll(true);
			$("#callbackTrigger").bind("change", {}, setTrigger);
		},
		error : function() {
			hsArtDialog.content("异常！请重新尝试或者联系管理员！").showModal();
		}
	});

	// 查询机构的成员
	// getAllOrgMembers();

});

function setTrigger() {
	var zTree = $.fn.zTree.getZTreeObj("#orgTree");
	zTree.setting.edit.drag.autoExpandTrigger = $("#callbackTrigger").attr(
			"checked");
}

// 点击节点，查询该组织的人员信息
function onClick(event, treeId, treeNode, clickFlag) {
	getOrgMembers(treeNode.id, treeNode.name);
	
}

// 获取组织的成员
function getOrgMembers(id, name) {

	$
			.ajax({
				url : home_url + '/org?method=getOrgMembers',
				data : {
					id : id
				},
				type : 'post',
				dataType : 'json',
				success : function(data) {
					$('#_staffList li').remove();
					$.each(data.data,function() {
										if (this.basic.realname != undefined) {
											var node = '<li><div class="checkbox"><label ><input type="radio" class="ace" name="form-field-radio" ';
													
											if(list!=null&&list.indexOf(this.id)!=-1){
												node+='<span class="">'+ this.basic.realname+'(已取证)';
											}else{
												node+='onclick="_selectStaff('
												+ this.id
												+ ',\''
												+ this.basic.realname
												+ '\','
												+ id
												+',\''+this.basic.mobile
												+ '\',\''
												+ name
												+ '\',\''
												+ this.basic.idcard
												+ '\')">'+'<span class="lbl">'+ this.basic.realname;
											}
											node+= '</span></label></div></li>';
											$("#_staffList").append(node);
										}
									});
				},
				error : function() {
					$.jGrowl("异常！请重新尝试或者联系管理员!", {
						header : "error"
					});
				}
			});
}

// 根据名字查询人员
function query() {
	// 弹窗提示配置
	var hsArtDialog = dialog({
		title : '提示',
		id : "hs-dialog",
		fixed : true,
		width : 300,
		height : 50
	});

	var name = $('#simplename').val();

	$
			.ajax({
				url : home_url + '/org?method=getStaffByName',
				data : {
					name : name
				},
				async: false,
				type : 'post',
				dataType : 'json',
				success : function(data) {
					$('#_staffList li').remove();
					$.each(data.data,function() {
						if (this.basic.realname != undefined) {
							var node = '<li><div class="checkbox"><label ><input type="radio" class="ace" name="form-field-radio" ';
							
							if(list!=null&&list.indexOf(this.id)!=-1){
								node+='<span class="">'+ this.basic.realname+'(已取证)';
							}else{
								node+='onclick="_selectStaff('
								+ this.id
								+ ',\''
								+ this.basic.realname
								+ '\','
								+ this.orgid
								+',\''+this.basic.mobile
								+ '\',\''
								+ name
								+ '\',\''
								+ this.basic.idcard
								+ '\')">'+'<span class="lbl">'+ this.basic.realname;
							}
									
							node+= '</span></label></div></li>';
							$("#_staffList").append(node);
						}
									});
				},
				error : function() {
					hsArtDialog.content("异常！请重新尝试或者联系管理员！").showModal();
				}
			});
	//气泡提示
	$('[data-rel=tooltip]').tooltip();
}

// 选取人员事件
function _selectStaff(uid, uname, did,phone, dname,IdCard) {
	$("#rselcted_staffid").val(uid);
	$("#rselcted_staffname").val(uname);
	$("#selected_deptid").val(did);
	$("#selected_deptname").val(dname);
	$("#selected_phone").val(phone);
	$("#selected_IdCard").val(IdCard);
	// 每次添加一个元素后，都会更改li绑定移除事件
	bindListener();
}

// 删除人员事件
function bindListener() {
	// 给所有的 ul li重新绑定移除事件
	$("#_selectedList li a").unbind().click(function() {
		$(this).parent().remove();
	});
}
