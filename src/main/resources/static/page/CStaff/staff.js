var isThirdParty = $("#isThirdParty").val();
var satff = (function() {
	return {
		init : function() {
			satff.initjqGrid(null);
			if (isThirdParty == 0) {
				// 记录下表格原始长度，然后再隐藏后设置长度为原始长度
				var len = $("#stafftb").getGridParam("width");
				// 隐藏外聘单位名称
				$("#stafftb").setGridParam().hideCol("othercompany");
				$("#stafftb").setGridWidth(len);
			}
		},
		jqGridRedraw : function(data) {

			$("#stafftb").jqGrid('setGridParam', {
				postData : {
					isThirdParty : $("#isThirdParty").val()
				}
			}).trigger("reloadGrid");// 重新载入
		},
		// 搜索
		search : function() {

			/*
			 * 先清空条件 jqgrid postData setGridParam 调用多次时查询条件会累加 解决postData参数值累加问题
			 * 导致参数不准确
			 */
			var postData = $("#stafftb").jqGrid("getGridParam", "postData");
			$.each(postData, function(k, v) {
				delete postData[k];
			});

			// 传参页面参数
			$("#stafftb").jqGrid('setGridParam', {
				postData : $("#searchForm").serializeObject()
			}).trigger("reloadGrid");// 重新载入

		},
		initjqGrid : function(id) {
			// table 和 页脚的id
			var grid_selector = "#stafftb";
			var pager_selector = "#grid-pager";
			jQuery(grid_selector)
					.jqGrid(
							{
								url : home_url + '/staff/querySatffListData',
								datatype : "json",
								mtype : "post",
								postData : {
									isThirdParty : $("#isThirdParty").val()
								},
								search : false,
								height : 'auto',
								viewrecords : true,// 显示总记录数
								autowidth : true,// 自动匹配宽度
								rowNum : 10,// 默认每页显示
								rowList : [ 5, 10, 15, 20, 25, 30 ],// 自定义每页显示下拉选
								pager : pager_selector,// 表格数据关联的分页条，html元素
								altRows : true,// 分页选项，可以下拉选择每页显示记录数
								rownumbers : true,// 显示序号
								gridComplete : function(data) {// 气泡
									$(".point").tooltip();
									$(".ui-pg-div").tooltip();
								},
								/*
								 * multiselect : true,//生成多选列 multiboxonly :
								 * true,//全选
								 */colNames : [ '工号', '姓名','年龄', "性别",'出生日期', '部门/科室',
										'岗位/工种', '职称等级','职称', '岗位状态', '外聘单位名称',
										'更新时间','籍贯','民族', '证件类型','证件号码','婚姻','学历','毕业院校','专业','手机号码','其他号码','邮箱','备注',
										'参加工作时间','本单位入职时间','职务','所在院区','工作类别','起始从事放射工作时间','职业照射种类及代码','操作' ],
								colModel : [
										{
											name : "workno",
											index : "workno",
											editable : false,
											sortable : false,
											width : 60,
											formatter : function(cellvalue,
													options, rowObject) {
												var value = cellvalue;
												if (cellvalue == null
														|| cellvalue == undefined)
													value = "";
												if (!rowObject.isInfoComplete)
													value = value
															+ "<a class='point' data-rel='tooltip' data-placement='right' data-original-title='该人员信息不完整！'><i class='icon-exclamation'></i></a>";

												return value;
											}
										},
										{
											name : "name",
											index : "name",
											editable : false,
											sortable : false,
											width : 100,
											formatter : function(cellvalue,
													options, rowObject) {
												if (rowObject != null
														&& rowObject.basic != null
														&& rowObject.basic.realname != null)
													return rowObject.basic.realname;
												return "";
											}
										},
										{
											name : "age",
											index : "age",
											editable : false,
											sortable : false,
											width : 100,
											formatter : function(cellvalue,
													options, rowObject) {
												if (rowObject != null
														&& rowObject.basic != null
														&& rowObject.basic.age != null)
													return rowObject.basic.age;
												return "";
											}
										},
										{
											name : "gender",
											index : "gender",
											editable : false,
											sortable : false,
											width : 80,
											formatter : function(cellvalue,
													options, rowObject) {
												if (rowObject != null
														&& rowObject.basic != null
														&& rowObject.basic.gender != null) {
													if (rowObject.basic.gender == 1) {
														return '男';
													} else if (rowObject.basic.gender == 0) {
														return '女';
													}

												}
												return "";
											}
										},
										{
											name : "birthday",
											index : "birthday",
											hidden:true,
											editable : false,
											sortable : false,
											width : 150,
											formatter : function(cellvalue,
													options, rowObject) {
												if(rowObject.basic.birthday == undefined)return ""; 
				        						return moment(new Date(rowObject.basic.birthday)).format("YYYY-MM-DD");
											
											}
										},
										{
											name : "orgName",
											index : "orgName",
											editable : false,
											sortable : false,
											width : 150,
											formatter : function(cellvalue,
													options, rowObject) {
												if (rowObject != null
														&& rowObject.organization != null
														&& rowObject.organization.fullname != null)
													return rowObject.organization.fullname;
												return "";
											}
										},
										{
											name : "workName",
											index : "workName",
											editable : false,
											sortable : false,
											width : 120,
											formatter : function(cellvalue,
													options, rowObject) {
												if (rowObject != null
														&& rowObject.staffworkrole != null
														&& rowObject.staffworkrole.workroleinfo != null
														&& rowObject.staffworkrole.workroleinfo.workname != null)
													return rowObject.staffworkrole.workroleinfo.workname;
												return "";
											}
										},
										{
											name : "jobtype",
											index : "jobtype",
											editable : false,
											sortable : false,
											hidden:true,
											width : 200,
											formatter : function(cellvalue,
													options, rowObject) {
												if (rowObject != null
														&& rowObject.jobtype != null){
													if (rowObject.jobtype == 0) {
														return "员级";
													} else if (rowObject.jobtype == 1) {
														return "初级";
													}else if (rowObject.jobtype == 2) {
														return "中级";
													}else if (rowObject.jobtype == 3) {
														return "副高级";
													}else if (rowObject.jobtype == 4) {
														return "正高级";
													}
												}
												return "";
											}
										},
										{
											name : "jobName",
											index : "jobName",
											editable : false,
											sortable : false,
											width : 200,
											formatter : function(cellvalue,
													options, rowObject) {
												if (rowObject != null
														&& rowObject.jobName != null
														&& rowObject.jobName != null)
													return rowObject.jobName;
												return "";
											}
										},
										{
											name : "status",
											index : "status",
											editable : false,
											sortable : false,
											width : 100,
											formatter : function(cellvalue,
													options, rowObject) {
												if (rowObject != null
														&& rowObject.staffworkrole != null
														&& rowObject.staffworkrole.status != null) {
													if (rowObject.staffworkrole.status == 1) {
														return "在职";
													} else if (rowObject.staffworkrole.status == 2) {
														return "离岗";
													}

												}
												return "";
											}
										},
										{
											name : "othercompany",
											index : "othercompany",
											editable : false,
											sortable : false,
											width : 80,
											formatter : function(cellvalue,
													options, rowObject) {
												if (rowObject != null
														&& rowObject.staffworkrole != null
														&& rowObject.staffworkrole.othercompany != null) {
													return rowObject.staffworkrole.othercompany;
												}
												return "";
											}
										},
										{
											name : "updatetime",
											index : "updatetime",
											editable : false,
											sortable : false,
											width : 100,
											formatter : function(cellvalue,
													options, rowObject) {
												if (cellvalue != null
														&& cellvalue != undefined) {
													return formatDate(new Date(
															cellvalue));
												}
												return "";
											}
										},
										{
											name : "nativeplace",
											index : "nativeplace",
											hidden:true,
											editable : false,
											sortable : false,
											width : 100,
											formatter : function(cellvalue,
													options, rowObject) {
												if (rowObject != null
														&& rowObject.basic.nativeplace != null
														&& rowObject.basic.nativeplace != null) {
													return rowObject.basic.nativeplace;
												}
												return "";
											}
										},
										{
											name : "nation",
											index : "nation",
											hidden:true,
											editable : false,
											sortable : false,
											width : 100,
											formatter : function(cellvalue,
													options, rowObject) {
												if (rowObject != null
														&& rowObject.basic.nation != null
														&& rowObject.basic.nation != null) {
													return rowObject.basic.nation;
												}
												return "";
											}
										},
										{
											name : "certificateType",
											index : "certificateType",
											hidden:true,
											editable : false,
											sortable : false,
											width : 100,
											formatter : function(cellvalue,
													options, rowObject) {
												if (rowObject != null
														&& rowObject.basic != null
														&& rowObject.basic.certificateType != null) {
													if (rowObject.basic.certificateType == 0) {
														return "身份证";
													} else if (rowObject.basic.certificateType == 1) {
														return "护照";
													}else if (rowObject.basic.certificateType == 2) {
														return "军官证";
													}else if (rowObject.basic.certificateType == 3) {
														return "士兵证";
													}else if (rowObject.basic.certificateType == 4) {
														return "警官证";
													}
												}
												return "";
											}
										},
										{
											name : "idcard",
											index : "idcard",
											hidden:true,
											editable : false,
											sortable : false,
											width : 180,
											formatter : function(cellvalue,
													options, rowObject) {
												if (rowObject != null
														&& rowObject.basic.idcard != null
														&& rowObject.basic.idcard != null) {
													return rowObject.basic.idcard;
												}
												return "";
											}
										},
										{
											name : "ismarried",
											index : "ismarried",
											hidden:true,
											editable : false,
											sortable : false,
											width : 100,
											formatter : function(cellvalue,
													options, rowObject) {
												if (rowObject != null
														&& rowObject.basic != null
														&& rowObject.basic.ismarried != null) {
													if (rowObject.basic.ismarried == 0) {
														return "未婚";
													} else if (rowObject.basic.ismarried == 1) {
														return "已婚";
													}else if (rowObject.basic.ismarried == 2) {
														return "离异";
													}else if (rowObject.basic.ismarried == 3) {
														return "丧偶";
													}else if (rowObject.basic.ismarried == 4) {
														return "分居";
													}
												}
												return "";
											}
										},
										{
											name : "education",
											index : "education",
											hidden:true,
											editable : false,
											sortable : false,
											width : 100,
											formatter : function(cellvalue,
													options, rowObject) {
												if (rowObject != null
														&& rowObject.basic != null
														&& rowObject.basic.education != null){
													for (var j = 0; j < educationList.length; j++) {
														if (rowObject.basic.education == educationList[j].paramcode) {
															return educationList[j].paramvalue;
														}
													}
												}
												return "";
											}
										},
										{
											name : "graduatedFrom",
											index : "graduatedFrom",
											hidden:true,
											editable : false,
											sortable : false,
											width : 100,
											formatter : function(cellvalue,
													options, rowObject) {
												if (rowObject != null
														&& rowObject.basic.graduatedFrom != null
														&& rowObject.basic.graduatedFrom!= null) {
													return rowObject.basic.graduatedFrom;
												}
												return "";
											}
										},
										{
											name : "major",
											index : "major",
											hidden:true,
											editable : false,
											sortable : false,
											width : 100,
											formatter : function(cellvalue,
													options, rowObject) {
												if (rowObject != null
														&& rowObject.basic.major != null
														&& rowObject.basic.major!= null) {
													return rowObject.basic.major;
												}
												return "";
											}
										},
										{
											name : "mobile",
											index : "mobile",
											hidden:true,
											editable : false,
											sortable : false,
											width : 100,
											formatter : function(cellvalue,
													options, rowObject) {
												if (rowObject != null
														&& rowObject.basic.mobile != null
														&& rowObject.basic.mobile!= null) {
													return rowObject.basic.mobile;
												}
												return "";
											}
										},
										{
											name : "phone",
											index : "phone",
											hidden:true,
											editable : false,
											sortable : false,
											width : 100,
											formatter : function(cellvalue,
													options, rowObject) {
												if (rowObject != null
														&& rowObject.basic.phone != null
														&& rowObject.basic.phone!= null) {
													return rowObject.basic.phone;
												}
												return "";
											}
										},
										{
											name : "email",
											index : "email",
											hidden:true,
											editable : false,
											sortable : false,
											width : 150,
											formatter : function(cellvalue,
													options, rowObject) {
												if (rowObject != null
														&& rowObject.basic != null
														&& rowObject.basic.email!= null) {
													return rowObject.basic.email;
												}
												return "";
											}
										},
										{
											name : "remark",
											index : "remark",
											hidden:true,
											editable : false,
											sortable : false,
											width : 100,
											formatter : function(cellvalue,
													options, rowObject) {
												if (rowObject != null
														&& rowObject.basic.remark != null
														&& rowObject.basic.remark!= null) {
													return rowObject.basic.remark;
												}
												return "";
											}
										},
										{
											name : "worktime",
											index : "worktime",
											hidden:true,
											editable : false,
											sortable : false,
											width : 100,
											formatter : function(cellvalue,
													options, rowObject) {
												if(rowObject.basic.worktime == undefined)return ""; 
				        						return moment(new Date(rowObject.basic.worktime)).format("YYYY-MM-DD");
											}
										},
										{
											name : "jointime",
											index : "jointime",
											hidden:true,
											editable : false,
											sortable : false,
											width : 100,
											formatter : function(cellvalue,
													options, rowObject) {
												if(rowObject.jointime == undefined)return ""; 
				        						return moment(new Date(rowObject.jointime)).format("YYYY-MM-DD");
											}
										},
										{
											name : "jobtitle",
											index : "jobtitle",
											hidden:true,
											editable : false,
											sortable : false,
											width : 100,
											formatter : function(cellvalue,
													options, rowObject) {
												if (rowObject != null
														&& rowObject.jobtitle != null
														&& rowObject.jobtitle!= null) {
													return rowObject.jobtitle;
												}
												return "";
											}
										},
										{
											name : "areaName",
											index : "areaName",
											hidden:true,
											editable : false,
											sortable : false,
											width : 100,
											formatter : function(cellvalue,
													options, rowObject) {
												if (rowObject != null
														&& rowObject.area != null
														&& rowObject.area.name!= null) {
													return rowObject.area.name;
												}
												return "";
											}
										},
										{
											name : "workType",
											index : "workType",
											hidden:true,
											editable : false,
											sortable : false,
											width : 100,
											formatter : function(cellvalue,
													options, rowObject) {
												if (rowObject != null
														&& rowObject.workType != null
													) {
													if (rowObject.workType == 0) {
														return "医学影像";
													} else if (rowObject.workType == 1) {
														return "放射治疗";
													}
												}
												return "";
											}
										},
										{
											name : "radiationbegindate",
											index : "radiationbegindate",
											hidden:true,
											editable : false,
											sortable : false,
											width : 150,
											formatter : function(cellvalue,
													options, rowObject) {
												if(rowObject.radiationbegindate == undefined || rowObject.isradiation==0 )return ""; 
				        						return moment(new Date(rowObject.radiationbegindate)).format("YYYY-MM-DD");
											}
										},
										{
											name : "radiationtype",
											index : "radiationtype",
											hidden:true,
											editable : false,
											sortable : false,
											width : 150,
											formatter : function(cellvalue,
													options, rowObject) {
												if (rowObject != null
														&& rowObject.staffworkrole != null
														&& rowObject.staffworkrole.radiationtype != null && rowObject.isradiation!=0){
													for (var j = 0; j < OccupationaSysparamList.length; j++) {
														if (rowObject.staffworkrole.radiationtype == OccupationaSysparamList[j].paramcode) {
															return OccupationaSysparamList[j].paramvalue;
														}
													}
												}
												return "";
											}
										},
										{
											name : "Rating",
											index : "Rating",
											editable : false,
											sortable : false,
											width : 80,
											formatter : function(cellvalue,
													options, rowObject) {
												var info = '<a href="javascript:staffview('
														+ rowObject.id
														+ ');" class="ui-pg-div" data-rel="tooltip" data-original-title="查看" ><i class="ui-icon icon-eye-open blue"></i></a>';
												if (adminid!=rowObject.id&&staffid != rowObject.id) {
													info += ' <a href="javascript:staffDel('
															+ rowObject.id
															+ ');" class="ui-pg-div" data-rel="tooltip" data-original-title="删除"><i class="ui-icon icon-trash red"></i></a>';
												}
												return info;
											}
										} ],
								loadComplete : function() {// 重绘回调函数
									var table = this;
									//当为本单位人员时 判断人员数据如果小于等于1  则显示提示 （有一条是管理员信息）    
									if(isThirdParty==0){
										$.ajax({
											type: "POST",
								               url: home_url+"/staff/othersCount",
								               success: function(data){
								            	   if(data.data==0){
								            		   ImDialog = dialog({
															title : '提示',
															content : $('#noOneTipDiv'),
															width:'300',
															lock : true,
															cancelValue : '取消',
															cancel : function() {
															}
														});
													ImDialog.showModal();
								            	   }
								              }
										});
										
										
									}
								}

							});

			// 表格下方操作
			jQuery(grid_selector).jqGrid('navGrid', pager_selector, {
				add : false,
				edit : false,
				editfunc : false,
				del : false,
				search : false,
				refresh : false,
				refreshicon : 'icon-refresh green',
				view : false
			});

		}
	}
})();

// 人员查看
function staffview(id) {
	window.location.href = home_url + "/staff/editStaff?id=" + id;
}
// 人员删除
function staffDel(id) {
	delConfirmDiag(function() {
		$.post(home_url + "/staff/delStaff", {
			id : id
		}, function(data) {
			alert(data.message);
			if (data.code > 0) {
				satff.jqGridRedraw();
			}
		});
	})
}
// 人员新增
function addstaff() {
	void (window
			.open(home_url + "/staff/addStaff?isThirdParty=" + isThirdParty));
}
$(document).ready(
		function() {
			satff.init();
			// 日历
			$('input[name=date-range-picker]').daterangepicker().prev().on(
					ace.click_event, function() {
						$(this).next().focus();
					});
		});

// 组织单选
function selectorganize() {
	SelectOrg_Single(function(data) {
		$("#orgulId li").remove();
		var str = ' <li class="success"><input type="hidden"  name="org" value="'
				+ data.id
				+ '"/><span>'
				+ data.name
				+ '</span> <a href="javascript:;" onclick="$(this).li_remove();"> <i class="icon-remove white"></i> </a></li>';
		$("#orgulId").append(str);
	});
}
// 岗位/工种
function selectworkrole() {
	SelectWorkRole(function(data) {
		$("#workroleulId li").remove();
		var str = ' <li class="warning"> <input type="hidden" name="workrole"value="'
				+ data.id
				+ '" /> <span>'
				+ data.name
				+ '</span> <a href="javascript:;" onclick="$(this).li_remove();"> <i class="icon-remove white"></i> </a></li>';
		$("#workroleulId").append(str);
	});

}