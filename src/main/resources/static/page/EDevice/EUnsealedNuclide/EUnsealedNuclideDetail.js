//非密封放射
var EUnsealedNuclideDetail = (function() {
	return {
		init : function(){//初始化
			
			// 区域   绑定删除事件  删除li的时候更新数据
			$("#areaList1").click(function(e){
				if(e.target.nodeName != "I"){//点击i的时候触发
					return;
				}
				//获取i-> a-> li
				$(e.target).parent().parent().remove();
				//所在院区
				$("#hospitalArea").html("");
				$("#Area").val("");
				//所在机房或区域 id
				$("#areaId").val("");
				//清空区域等级
				$("#siteGrade").html("");
			});
			// 人员   绑定删除事件  删除li的时候更新数据
			$("#staffList1").click(function(e){
				if(e.target.nodeName != "I"){//点击i的时候触发
					return;
				}
				//获取i-> a-> li
				$(e.target).parent().parent().remove();
				//人员id 清空
				$("#staffId").val("");
			});
			
			// 生产单位   绑定删除事件  删除li的时候更新数据
			$("#productionUnitList").click(function(e){
				if(e.target.nodeName != "I"){//点击i的时候触发
					return;
				}
				//获取i-> a-> li
				$(e.target).parent().parent().remove();
				//生产单位
				$("#productionUnitId").val("");
			});
			
			//计算等级
			$("#dayBqExponent").blur(function(){
				EUnsealedNuclideDetail.countRank($("#areaId").val(), $("#id").val());//计算等级
			});
			$("#dayBq").blur(function(){
				EUnsealedNuclideDetail.countRank($("#areaId").val(), $("#id").val());//计算等级
			});
			
			EUnsealedNuclideDetail.initValidate();
		},
		initValidate : function(){
			//提交
			$(".a-submit").click(function(){  
				if ($("#EUnsealedNuclideForm").valid()) {
					
					var id = $("#EUnsealedNuclideForm").find("#id").val();
					var data = {
							id : id,
						     productionUnitId : $("#EUnsealedNuclideForm").find("#productionUnitId").val(),//生产单位id
						     nuclideId : $("#EUnsealedNuclideForm").find("#nuclideId").val(),//核素表id
						     staffId : $("#EUnsealedNuclideForm").find("#staffId").val(),//存储用户人的id
						     areaId : $("#EUnsealedNuclideForm").find("#areaId").val(),//区域表的id
						     dayBqExponent : $("#EUnsealedNuclideForm").find("#dayBqExponent").val(),//最大等效日操作量 指数
						     dayBq : $("#EUnsealedNuclideForm").find("#dayBq").val(),//最大等效日操作量  正数
						     yearBqExponent : $("#EUnsealedNuclideForm").find("#yearBqExponent").val(),// 最大等效年操作量 指数
						     yearBq : $("#EUnsealedNuclideForm").find("#yearBq").val(),//最大等效年操作量  正数
						     purpose : $("#EUnsealedNuclideForm").find("#purpose").val(),//用途
						     physicalState : $("#EUnsealedNuclideForm").find("#physicalState").val(),//物理状态
						     nuclideName : $("#EUnsealedNuclideForm").find("#nuclideName").val(),//核素名称
						     siteGrade : $("#EUnsealedNuclideForm").find("#siteGrade").html(),//场所等级
						     activityType : $("#EUnsealedNuclideForm").find("#activityType").val(),// 活动种类
						};
					 $.post(home_url + "/EDevice/unsealedNuclide/submit",data, 
							function (response) {
								if (response.code == 0){//提示   确认后跳转到查看
									gridAddCallback("EUnsealedNuclideTable");//刷新父页面table
									location.href =  home_url + "/EDevice/unsealedNuclide/lookOver?id=" + response.data;
								}
							}, 'json');
				}
			});
			
			//所在最大等效日操作量正数不为空时指数 没有值时   指数默认为0
			$("#dayBq").change(function(){
				var str = $(this).val();
				var activityExponent = $("#dayBqExponent").val();//指数
				if(str != "" && activityExponent == ""){
					$("#dayBqExponent").val("0")
				}
				if(str == ""){
					$("#dayBqExponent").val("")
				}
			});
			//当最大等效日操作量正数有值时候  指数不能为 空
			$("#dayBqExponent").change(function(){
				var activityExponent = $(this).val();
				var str = $("#dayBq").val();
				if(str != "" && activityExponent == ""){
					$("#dayBqExponent").val("0")
				}
			});
			
			//所在最大等效年操作量正数不为空时指数 没有值时   指数默认为0
			$("#yearBq").change(function(){
				var str = $(this).val();
				var activityExponent = $("#yearBqExponent").val();//指数
				if(str != "" && activityExponent == ""){
					$("#yearBqExponent").val("0")
				}
				if(str == ""){
					$("#yearBqExponent").val("")
				}
			});
			//当最大等效年操作量正数有值时候  指数不能为 空
			$("#yearBqExponent").change(function(){
				var activityExponent = $(this).val();
				var str = $("#yearBq").val();
				if(str != "" && activityExponent == ""){
					$("#yearBqExponent").val("0")
				}
			});
			
			$("#EUnsealedNuclideForm").validate({
				rules: {
					nuclideId: {//核素名称
						required: true
				    },
				    purpose : {//用途
				    	required:true
				    },
				    areaId: {//场所
				    	required: true
				    },
				    dayBqExponent : {//最大等效日操作量 指数
				    	digits:true,
				    	range:[0,20]
				    },
				    dayBq : {//最大等效日操作量 正数
					    number : true,
					    verifyfigure : true
				    },
				    yearBqExponent : {//最大等效年操作量 指数
				    	digits:true,
				    	range:[0,20]
				    }, 
				    yearBq : {//最大等效年操作量 正数
				    	number : true,
				    	verifyfigure : true
				    }
				},
				messages: {	
				    dayBqExponent : {//最大等效日操作量 指数
				    	digits:"<div class='tisyz'><i class='tisbefore'></i>请输入0到20之间的整数</div>",
				    	range:"<div class='tisyz'><i class='tisbefore'></i>请输入0到20之间的整数</div>"
				    },
				    dayBq : {//最大等效日操作量 正数
					    number : "<div class='tisyz'><i class='tisbefore'></i>请输入正数且4位有效数字</div>",
					    verifyfigure : "<div class='tisyz'><i class='tisbefore'></i>请输入正数且4位有效数字</div>"
				    },
				    yearBqExponent : {//最大等效年操作量 指数
				    	digits:"<div class='tisyz'><i class='tisbefore'></i>请输入1到20之间的整数</div>",
				    	range:"<div class='tisyz'><i class='tisbefore'></i>请输入1到20之间的整数</div>"
				    },
				    yearBq : {//最大等效年操作量 正数
				    	number : "<div class='tisyz'><i class='tisbefore'></i>请输入正数且4位有效数字</div>",
				    	verifyfigure : "<div class='tisyz'><i class='tisbefore'></i>请输入正数且4位有效数字</div>"
				    }
				}
			});
		},
		countRank : function(areaId, id){//计算区域的等级
			if(areaId == undefined || $.trim(areaId) == "")
				return;
			
			 $.get(home_url + "/EDevice/unsealedNuclide/daySun",{"areaId" : areaId, "id" : id}, 
				function (data) {
					if (data.code == 0){
						var sun = data.data;//得到区域日操作量总数
						
						//获取页面上填的日操作量
						var dayBq = $("#dayBq").val();
						var dayBqExponent = $("#dayBqExponent").val();
						var reg = /^\d+(?=\.{0,1}\d+$|$)/
						if(dayBq != "" && dayBqExponent != "" && reg.test(dayBq) && reg.test(dayBqExponent)){//判断数据有效性
							sun += dayBq * Math.pow(10, dayBqExponent);
						}
						//区域等级判断  siteGrade
						if(sun < (2 * Math.pow(10, 7))){
							$("#siteGrade").html("丙级");
							return;
						}
							
						if(sun < (4 * Math.pow(10, 9)) && sun >= (2 * Math.pow(10, 7))){
							$("#siteGrade").html("乙级");
							return;
						}
							
						if(sun >= (4 * Math.pow(10, 9))){
							$("#siteGrade").html("甲级");
							return;
						}
					}
			}, 'json');
		}
	}
})();


jQuery(document).ready(function() {
	EUnsealedNuclideDetail.init();//初始化
	EUnsealedNuclideDetail.countRank($("#areaId").val(), $("#id").val());//计算等级
});

//生成单位  单选
function getProducer(){
	Company_Single(function(data){
		$("#productionUnitList").html("");//清空数据
		//保存生产厂家的id
		$("#productionUnitId").val($(data).val());
		$("#productionUnitList").append(data);
	},5);//1.个人剂量监测单位；2.职业健康检查单位；3.评价与检测机构； 4.供货商；5.生产厂家；6.设计单位；7.施工单位；
}

//区域单选
function selectArea1() {
	SelectRoom_Single(function(data) {
		$("#areaList1").children().remove();//单选   删除里面多余的数据
		$("#hospitalArea").html("");//清空 所在院区
		//所在机房或区域 id
		$("#areaId").val("");
		//清空区域等级
		$("#siteGrade").html("");
		if(data.id == undefined || data.id == ""){
			return;
		}
		//本级区域
		$("#areaList1").append(dataNodeSelected(data.id, data.name));
		//生成所在院区
		
		$("#hospitalArea").html(data.areaName);
		$("#Area").val(data.areaName);
		//所在机房或区域 id
		$("#areaId").val(data.id);
		EUnsealedNuclideDetail.countRank($("#areaId").val(), $("#id").val());//计算等级
		$("#areaId").focus().blur()//光标切入移除
	});
}

//人员单选
function selectStaff1() {
	SelectStaff_Single(function(data) {
		$("#staffList1 li").remove();
		$("#staffId").val("");
		if(data.id == undefined || data.id == ""){
			return;
		}
		$("#staffList1").append(dataNodeSelected(data.id, data.name));
		//保存 人员id
		$("#staffId").val(data.id);
		$("#staffId").focus().blur()//光标切入移除
	});
}

//元素周期表
function getElement(){
	element(function(data){
		$("#element").html("");
		
		if( $(data).attr("name") == 'undefined' ||  $(data).attr("name") == undefined){//判断数据是否为null
			$("#nuclideName").val("");//元素名称
			$("#nuclideId").val("");//id
			return;
		}
		$("#element").append(data);
		//保存数据
		$("#nuclideName").val($(data).attr("name"));//元素名称
		$("#nuclideId").val($(data).val());//id
		$("#nuclideId").focus().blur()//光标切入移除
	});
}

//数组去重
Array.prototype.unique3 = function(){
	 var res = [];
	 var json = {};
	 for(var i = 0; i < this.length; i++){
	  if(!json[this[i]]){
	   res.push(this[i]);
	   json[this[i]] = 1;
	  }
	 }
	 return res;
	}