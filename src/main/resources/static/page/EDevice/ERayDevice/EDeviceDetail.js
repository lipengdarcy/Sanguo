//射线装置 功能js
var EDeviceDetail = (function() {
	return {
		init : function() {

			$("#parameter").change(function() {// 主要参数 电压单位 变化时 控制电流的显示隐藏
				if ($(this).val() == 2) {// 当为2时隐藏 电流
					$("#ma").val("");// 清空数据
					$("#ma").parent("div").hide();
					return;
				}
				$("#ma").parent("div").show();// 显示电流
			});

			// 人员 绑定事件 删除li的时候更新数据
			$("#staffList2").click(function(e) {
				if (e.target.nodeName != "I") {// 点击i的时候触发
					return;
				}
				// 获取i-> a-> li
				$(e.target).parent().parent().remove();
				var ids = new Array();
				var pname = new Array();
				// li
				var $li = $("#staffList2 li");
				if ($li.length == 0) {
					$("#RayDeviceForm").find("#staffId").val("");
					// 更新 所在部门/科室
					$("#RayDeviceForm").find("#department").html("");
					return;
				}
				$li.each(function() {
					ids.push([ $(this).val() ]);// id添加到数组
					pname.push([ $(this).attr('pname') ]);// pname添加到数组
				});
				// 去重
				ids = ids.unique3().join(",");
				pname = pname.unique3().join(",");
				// 更新 人员id
				$("#RayDeviceForm").find("#staffId").val(ids);
				// 更新 所在部门/科室
				$("#RayDeviceForm").find("#department").html(pname);
			});

			// 生成厂家 绑定删除事件 删除li的时候更新数据
			$("#producerList1").click(function(e) {
				if (e.target.nodeName != "I") {// 点击i的时候触发
					return;
				}
				// 获取i-> a-> li
				$(e.target).parent().parent().remove();
				// 生成厂家 id
				$("#producerId").val("");
			});

			// 安装单位 绑定删除事件 删除li的时候更新数据
			$("#installUnitList1").click(function(e) {
				if (e.target.nodeName != "I") {// 点击i的时候触发
					return;
				}
				// 获取i-> a-> li
				$(e.target).parent().parent().remove();
				// 安装单位 id
				$("#installUnitId").val("");
			});

			// 区域 绑定删除事件 删除li的时候更新数据
			$("#areaList1").click(function(e) {
				if (e.target.nodeName != "I") {// 点击i的时候触发
					return;
				}
				// 获取i-> a-> li
				$(e.target).parent().parent().remove();
				// 所在院区
				$("#hospitalArea").html("");
				// 所在机房或区域 id
				$("#areaId").val("");
			});
			// 验证电压
			jQuery.validator.addMethod("majorParameter1", function(value,
					element) {
				var str = $("#parameter").val();// 单位 kv mv
				var voltage = $("#voltage").val();// 电压
				if( voltage == ""){
					return  true;
					
				}else{
				if (str == 2) {// mv时不要验证
					return true;
				}
				
				if (str == 1) {// 为kv的时候 电压数值为10~500的整数

					if (/^[0-9]*[1-9][0-9]|.0*$/.test(voltage) && 10 <= voltage
							&& voltage <= 500) {// 电压数值为10~500的整数
						return true;
					}
				}

				}

				return false;
			}, "");

			// 验证电流
			jQuery.validator.addMethod("majorParameter2", function(value,
					element) {
				var str = $("#parameter").val();// 单位 kv mv
				var ma = $("#ma").val();// 电流
				var voltage = $("#voltage").val();// 电压
				if(ma == "" && str != 1){
					return true;
				}else{
				if (str == 1 ) {// 为kv的时候 电流为1~5000的整数
					if (ma >= 1 && ma <= 5000) {
						return true;
					}
					else{
						if(voltage == ""){
						return true;}
						else{
						return false;}
					}
				} else {
					return true;
				}
			
				}
				return false;
			}, "");

			$("#RayDeviceForm")
					.validate(
							{
								rules : {
									deviceName : {// 装置名称
										required : true
									},
									code : {// 装置编号
//										required : true
									},
									unitType : {// 设备类型
//										required : true
									},
									voltage : {// 主要参数 电压
//										required : true,
										majorParameter1 : true,
										range : [ 0.5, 1000 ]
									},
									ma : {
										digits : true,
										majorParameter2 : true
									},
									producerId : {// 生产厂家
//										required : true
									},
									diagnosticRadiologyType : {// 放射诊断类别
										required : true
									},
									activityType : {// 活动种类
										required : true
									},
									areaId : {// 所在机房或区域
										required : true
									},
									diagnosisDetail : {// 放射诊疗明细
										required : true
									},
									bulbCount : {// 球管个数
										digits : true,
										range : [ 1, 5 ]
									},
//									actinoscopyType : {// 放射检查类型
//										required : true
//									},
								},
								messages : {
									voltage : {// 主要参数 电压
										majorParameter1 : "<div class='tisyz'><i class='tisbefore'></i>单位为kv时电压为10~500的整数</div>"
									},
									ma : {
										digits : "<div class='tisyz'><i class='tisbefore'></i>只能输入整数</div>",
										majorParameter2 : "<div class='tisyz'><i class='tisbefore'></i>单位为kv时电流为1~5000的整数</div>"
									},
									bulbCount : {// 球管个数
										digits : "<div class='tisyz'><i class='tisbefore'></i>请输入1到5的整数</div>",
										range : "<div class='tisyz'><i class='tisbefore'></i>请输入1到5的整数</div>"
									}
								}
							});

			// 提交
			$("#RayDeviceForm")
					.find(".a-submit")
					.click(
							function() {
								var data = {
									id : $("#RayDeviceForm").find("#Rid").val(),
									areaId : $("#RayDeviceForm")
											.find("#areaId").val(),// 区域
									installUnitId : $("#RayDeviceForm").find(
											"#installUnitId").val(),// 安装单位id
									deviceName : $("#RayDeviceForm").find(
											"#deviceName").val(),// 装置名称
									code : $("#RayDeviceForm").find("#code")
											.val(),// 编号
									unitType : $("#RayDeviceForm").find(
											"#unitType").val(),// 设备型号
									ma : $("#RayDeviceForm").find("#ma").val(),// 电流
									voltage : $("#RayDeviceForm").find(
											"#voltage").val(),// 电压
									producerId : $("#RayDeviceForm").find(
											"#producerId").val(),// 生产厂家
									diagnosticRadiologyType : $(
											"#RayDeviceForm").find(
											"#diagnosticRadiologyType").val(),// 放射诊疗类别
									activityType : $(
											"#RayDeviceForm").find(
											"#activityType").val(),// 活动种类
									bulbCount : $("#RayDeviceForm").find(
											"#bulbCount").val(),// 球管个数
									actinoscopyType : $("#RayDeviceForm").find(
											"#actinoscopyType").val(),// 放射检查类型
									productionTime : $("#RayDeviceForm").find(
											"#productionTime").val(),// 出厂日期
									staffId : $("#RayDeviceForm").find(
											"#staffId").val(),// 人员id
									rayDeviceType : $.trim($("#RayDeviceForm")
											.find("#rayDeviceType").html()),// 射线装置类别
									hazardFactors : $.trim($("#RayDeviceForm")
											.find("#hazardFactors").html()),// 可能产生的职业病危害因素
									diagnosisDetail : $("#RayDeviceForm").find(
											"#diagnosisDetail").val()
								// 放射诊疗明细
								};

								if ($("#RayDeviceForm").valid()) {

									$
											.post(
													home_url
															+ "/EDevice/rayDevice/submit",
													data,
													function(response) {
														if (response.code == 0) {// 提示
															// 确认后跳转到查看
															if(response.message=='新增成功')
																gridAddCallback("ERayDeviceTible");// 刷新父页面table
															window.location.replace(home_url
																	+ "/EDevice/rayDevice/lookOver?id="
																	+ response.data);
														}
													}, 'json');
								}
							});

			// 主要参数内容改变时间
			$("#voltage").change(function() {
				EDeviceDetail.initHazardFactors();// 可能产生的职业病危害因素
				EDeviceDetail.initRayDeviceType();// 生成射线装置类别
			});
			// 单位发生改变时候 触发时间
			$("#parameter").change(function() {
				EDeviceDetail.initHazardFactors();// 可能产生的职业病危害因素
				EDeviceDetail.initRayDeviceType();// 生成射线装置类别
			});

			// 放射诊疗类别的选择触发的事件
			$("#RayDeviceForm")
					.find("#diagnosticRadiologyType")
					.change(
							function() {
								var str = $(this).val();
								var content = '<option value="">请选择</option>';
								var $bulbCount = $("#RayDeviceForm").find(
										"#bulbCount");// 球管
								var $actinoscopyType = $("#RayDeviceForm")
										.find("#actinoscopyType");// 放射检查类型
								$bulbCount.val("1");

								EDeviceDetail.initRayDeviceType();

								if (str == "") {// 没有数据的时候
									$("#diagnosisDetail").html(content)
								}

								if (str == 1 || str == 2) {// 放射治疗 或 核医学
									// 隐藏 放射检查类型 和球管
									$bulbCount.val("1");// 数据清空
									$actinoscopyType.val("");
									// select->td->tr 隐藏
									$actinoscopyType.parent().parent().hide();
									if (str == 1) {// 改变诊疗项目明细 diagnosisDetail

										content += '<option value="1" >立体定向（X刀）治疗</option>'
												+ '<option value="2">立体定向（γ刀）治疗</option>'
												+ '<option value="3">医用加速器治疗</option>'
												+ '<option value="4">质子治疗</option>'
												+ '<option value="5"  >中子治疗</option>'
												+ '<option value="6" >钴-60机治疗</option>'
												+ '<option value="7" >后装治疗</option>'
												+ '<option value="8"  >深部X射线机治疗</option>'
												+ '<option value="9" >敷贴治疗</option>'
												+ '<option value="10">重离子治疗</option>'
												+ '<option value="11" >其他放射治疗项目</option>';
										$("#diagnosisDetail").html(content)
										return;
									}
									if (str == 2) {
										content += '<option value="12">PET影像诊断</option>'
												+ '<option value="13" >CT-PET影像诊断</option>'
												+ '<option value="14"  >SPECT影像诊断</option>'
												+ '<option value="15" >γ相机影像诊断</option>'
												+ '<option value="16" >γ骨密度测量</option>'
												+ '<option value="17" >籽粒插植治疗</option>'
												+ '<option value="18">放射性药物治疗</option>'
												+ '<option value="19">其他核医学诊疗项目</option>';
										$("#diagnosisDetail").html(content);
										return;
									}

								}

								// select->td->tr 显示
								$actinoscopyType.parent().parent().show();
								if (str == "") {
									$actinoscopyType
											.html('<option>请选择</option>');
								}

								if (str == 3) {// 介入放射学
									$actinoscopyType
											.html('<option value="8">介入放射学操作</option>');
									$actinoscopyType.attr("disabled",
											"disabled");

									content += '<option value="20">DSA介入放射诊疗</option>'
											+ '<option value="21">其他影像设备介入放射诊疗</option>';
									$("#diagnosisDetail").html(content);
									return;
								}
								if (str == 4) {// X射线影像诊断
									$actinoscopyType.removeAttr("disabled");
									$actinoscopyType
											.html('<option value="1" >放射诊断学用X射线设备隔室透视、摄影</option>'
													+ '<option value="2" >放射诊断学用X射线设备同室透视、摄影</option>'
													+ '<option  value="3" >口内牙片摄影</option>'
													+ '<option  value="4" >牙科全景体层摄影、口腔CT</option>'
													+ '<option  value="5" >CT体层扫描</option>'
													+ '<option value="6" >床旁摄影</option>'
													+ '<option value="7" >骨科复位等设备旁操作</option>');

									content += '<option value="22">X射线CT影像诊断</option>'
											+ '<option value="23">CR影像诊断</option>'
											+ '<option value="24">DR影像诊断</option>'
											+ '<option value="25" >乳腺X射线影像诊断</option>'
											+ '<option value="26">普通X射线机影像诊断</option>'
											+ '<option value="27">牙科X射线影像诊断</option>'
											+ '<option value="28">其它X射线影像诊断</option>';
									$("#diagnosisDetail").html(content);
									return;
								}
							});

		},
		// 生成 可能产生的职业病危害因素
		initHazardFactors : function() {
			var voltage = $("#voltage").val();// 电压
			// 主要参数 根据主要参数自动生成，≥10MV的为：X射线、中子、感生放射性。＜10MV或者选择kV的为：X射线
			var str = $("#parameter").val();// 单位 kv mv
			if (str == 1 && voltage!="") {// 为kv的时候
				$("#hazardFactors").html("X射线");
				return;
			}
			//电压为空则置空
			if(voltage == ""){
				$("#hazardFactors").html("");
				return;
			}
			if (10 <= voltage) {
				$("#hazardFactors").html("X射线、中子、感生放射性");
			} else {
				$("#hazardFactors").html("X射线");
			}
		},
		// 生成射线装置类别
		initRayDeviceType : function() {
			var str = $("#parameter").val();// 单位 kv mv
			var voltage = $("#voltage").val();// 电压.
			var ma = $("#ma").val();// 电流
			var Type = $("#diagnosticRadiologyType").val();// 放射诊断类别
			if (str == 2 && voltage!="") {// 单位为mv
				if (voltage >= 100) {
					$("#rayDeviceType").html("I");
					return;
				}
				$("#rayDeviceType").html("II");
				return;
			}
			//电压为空则置空
			if(voltage == ""){
				$("#rayDeviceType").html("");
				return;
			}
			// 单位为kv的时候
			if (Type == 3 && voltage >= 60 && voltage < 140) {// 介入放射学
				// 60kV~140kV
				$("#rayDeviceType").html("III");
				return;
			}
			if (Type == 3 && voltage >= 140 && voltage < 200) {// 介入放射学
				// 140kV~200kV
				$("#rayDeviceType").html("II");
				return;
			}
			if (Type == 4 && voltage >= 10 && voltage < 200) {// X射线影像诊断
				// 10kV~200kV
				$("#rayDeviceType").html("III");
				return;
			}
			if (voltage < 100 * 1000 && voltage >= 200) {// 200kV~100MV
				$("#rayDeviceType").html("II");
				return;
			}
			$("#rayDeviceType").html("");// 数据错误的时候置空
		}
	};
})();

jQuery(document).ready(function() {
	EDeviceDetail.init();
});

// 安装单位单选
function getInstallUnit() {
	Company_Single(function(data) {
		$("#installUnitList1").html("");// 清空数据
		// 保存生产厂家的id
		$("#installUnitId").val($(data).val());
		$("#installUnitList1").append(data);
		;
	}, 7);// 1.个人剂量监测单位；2.职业健康检查单位；3.评价与检测机构； 4.供货商；5.生产厂家；6.设计单位；7.施工单位；
}

// 生成厂家 单选
function getProducer() {
	Company_Single(function(data) {
		$("#producerList1").html("");// 清空数据
		// 保存生产厂家的id
		$("#producerId").val($(data).val());
		$("#producerList1").append(data);
		;
	}, 5);// 1.个人剂量监测单位；2.职业健康检查单位；3.评价与检测机构； 4.供货商；5.生产厂家；6.设计单位；7.施工单位；
}

// 区域单选
function selectArea1() {
	$.get(home_url + "/EDevice/rayDevice/selectedAreaId", {},
			function(response) {
				SelectRoom_Single(function(data) {
					$("#areaList1").children().remove();// 单选 删除里面多余的数据
					$("#hospitalArea").html("");// 清空 所在院区
					// 使用场所或区域 id
					$("#areaId").val("");
					if (data.id == undefined || data.id == "") {
						return;
					}
					// 使用场所
					$("#areaList1")
							.append(dataNodeSelected(data.id, data.name));
					// 生成所在院区
					$("#hospitalArea").html(data.areaName);
					// 使用场所或区域 id
					$("#areaId").val(data.id);
					// $("#areaId").focus().blur()// 光标切入移除
				}, response.data);
			}, "json");
}

// 人员多选
function selectStaff2() {

	var myDialog = dialog({
		title : '人员多选',
		okValue : '确定',
		ok : function() {
			var ids = new Array();
			var pname = new Array();
			$('#_selectedList li').each(function() {
				ids.push([ $(this).val() ]);// id添加到数组
				pname.push([ $(this).attr('pname') ]);// pname添加到数组

				var str = true;
				var ztreeLi = $(this);
				$('#staffList2 li').each(function() {// 遍历已有元素 和 新增 元素是否相同
					ids.push([ $(this).val() ]);// id添加到数组
					pname.push([ $(this).attr('pname') ]);// pname添加到数组
					if (ztreeLi.val() == $(this).val()) {
						str = false;
					}
				});
				if (str) {// 判断数据是否重复
					$('#staffList2').append($(this));
				}

			});
			// 去重
			ids = ids.unique3().join(",");
			pname = pname.unique3().join(",");
			if (ids.length == 0) {
				return;
			}
			// 保存人员id
			$("#RayDeviceForm").find("#staffId").val(ids);
			$("#RayDeviceForm").find("#staffId").focus().blur()// 光标切入移除
			// 生成所在部门/科室
			$("#RayDeviceForm").find("#department").html(pname);

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

// 数组去重
Array.prototype.unique3 = function() {
	var res = [];
	var json = {};
	for (var i = 0; i < this.length; i++) {
		if (!json[this[i]]) {
			res.push(this[i]);
			json[this[i]] = 1;
		}
	}
	return res;
}

// 表单校验
function validateForm() {
	return $("#RayDeviceForm")
			.validate(
					{
						rules : {
							deviceName : {// 装置名称
								required : true
							},
							code : {// 装置编号
//								required : true
							},
							unitType : {// 设备类型
//								required : true
							},
							voltage : {// 主要参数 电压
//								required : true,
								majorParameter1 : true,
								range : [ 0.5, 1000 ]
							},
							ma : {
								digits : true,
								majorParameter2 : true
							},
							producerId : {// 生产厂家
//								required : true
							},
							diagnosticRadiologyType : {// 放射诊断类别
								required : true
							},
							activityType : {// 活动种类
								required : true
							},
							areaId : {// 所在机房或区域
								required : true
							},
							diagnosisDetail : {// 放射诊疗明细
								required : true
							},
							bulbCount : {// 球管个数
								digits : true,
								range : [ 1, 5 ]
							},
//							actinoscopyType : {// 放射检查类型
//								required : true
//							},
						},
						messages : {
							voltage : {// 主要参数 电压
								majorParameter1 : "<div class='tisyz'><i class='tisbefore'></i>单位为kv时电压为10~500的整数</div>"
							},
							ma : {
								digits : "<div class='tisyz'><i class='tisbefore'></i>只能输入整数</div>",
								majorParameter2 : "<div class='tisyz'><i class='tisbefore'></i>单位为kv时电流为1~5000的整数</div>"
							},
							bulbCount : {// 球管个数
								digits : "<div class='tisyz'><i class='tisbefore'></i>请输入1到5的整数</div>",
								range : "<div class='tisyz'><i class='tisbefore'></i>请输入1到5的整数</div>"
							}
						}
					}).form();
}