//密封放射
var ESealedRadioactiveSourceDetail = (function() {
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
			// 元素表   绑定删除事件  删除li的时候更新数据
			$("#element").click(function(e){
				if(e.target.nodeName != "I"){//点击i的时候触发
					return;
				}
				//获取i-> a-> li
				$(e.target).parent().parent().remove();
				//元素名称id 清空
				$("#nuclideId").val("");
				$("#nuclideName").val("");
			});
			
			
			// 生产单位   绑定删除事件  删除li的时候更新数据
			$("#nuclideManufacturersList1").click(function(e){
				if(e.target.nodeName != "I"){//点击i的时候触发
					return;
				}
				//获取i-> a-> li
				$(e.target).parent().parent().remove();
				//生产单位
				$("#nuclideManufacturers").val("");
			});
			
			//根据编号生成类别
			$("#sourceCode").change(function(){
				var sourceCode = $(this).val();
				if(!/^[A-Z\d]{2}[0-9N]{2}[A-Z0-9\d]{2}[\d]{5}[1-5N]$/.test(sourceCode)){
					$("#sourceType").val("0");
					$("#sourceType1").html("");
					var sourceType = sourceCode.substr(sourceCode.length-1,1);
					return;
				}
				//根据最后一个字符生成类别
				var sourceType = sourceCode.substr(sourceCode.length-1,1);
				if(sourceType == "N"){
					$("#sourceType").val("0");
					$("#sourceType1").html("");
				}
				if(sourceType == 1){
					$("#sourceType").val("1");
					$("#sourceType1").html("I");
				}
				if(sourceType == 2){
					$("#sourceType").val("2");
					$("#sourceType1").html("II");
				}
				if(sourceType == 3){
					$("#sourceType").val("3");
					$("#sourceType1").html("III");
				}
				if(sourceType == 4){
					$("#sourceType").val("4");
					$("#sourceType1").html("IV");
				}
				if(sourceType == 5){
					$("#sourceType").val("5");
					$("#sourceType1").html("V");
				}
				
			});
			
			ESealedRadioactiveSourceDetail.initValidate();
		},
		initValidate : function(){
			//提交
			$("#ESealedRadioactiveSourceForm").find(".a-submit").click(function(){  
				if ($("#ESealedRadioactiveSourceForm").valid()) {
					var data = {
							id : $("#ESealedRadioactiveSourceForm").find("#id").val(),
						    nuclideId : $("#ESealedRadioactiveSourceForm").find("#nuclideId").val(),// 核素表id
						    staffId : $("#ESealedRadioactiveSourceForm").find("#staffId").val(),// 存储用户人的id
						    areaId : $("#ESealedRadioactiveSourceForm").find("#areaId").val(),//区域表的id
						    nuclideManufacturers : $("#ESealedRadioactiveSourceForm").find("#nuclideManufacturers").val(),//核素生产厂家id
						    activityExponent : $("#ESealedRadioactiveSourceForm").find("#activityExponent").val(),//活度指数
						    activity : $("#ESealedRadioactiveSourceForm").find("#activity").val(),//活度正数
						    measureTime : $("#ESealedRadioactiveSourceForm").find("#measureTime").val(),//测量日期
						    sourceCode : $("#ESealedRadioactiveSourceForm").find("#sourceCode").val(),//放射编号
						    purpose : $("#ESealedRadioactiveSourceForm").find("#purpose").val(),//用途
						    sourceType : $("#ESealedRadioactiveSourceForm").find("#sourceType").val(),// 放射源类别
						    activityType : $("#ESealedRadioactiveSourceForm").find("#activityType").val(),// 活动种类
						    nuclideName : $("#ESealedRadioactiveSourceForm").find("#nuclideName").val()//核素名称
						    
						};
					
					 $.post(home_url + "/EDevice/sealRadiate/submit",data, 
							function (response) {
								if (response.code == 0){//提示   确认后跳转到查看
									gridAddCallback("ESealedRadioactiveSourceTable");//刷新父页面table
									location.href =  home_url + "/EDevice/sealRadiate/lookOver?id=" + response.data;
								}
							}, 'json');
				}
			});
			
			//验证编号一二位   生产单位代码
			jQuery.validator.addMethod("verifySourceCodeUnit", function(value, element) {
				var SourceCode = $.trim($("#ESealedRadioactiveSourceForm").find("#sourceCode").val());
				if(SourceCode == ''){
					return true;
				}
				var SourceCode = $.trim($("#ESealedRadioactiveSourceForm").find("#sourceCode").val());
				var state = new Array(
				         "AR", "GR", "PL", "PT", "RU" ,"SI", "SK" ,"RO" ,"ZA", "SE" ,"ZZ", "TR" ,"GB", 
				         "UA", "US" ,"UZ" ,"ES" ,"HU", "IN" ,"IE", "ID" ,"IS" ,"IL" ,"JP",
				         "NO","KZ","KG","KR","MX","NL","IT","AU","BE","BG","BR","BY","CA","CZ","DE","DK",
				         "EE","EG","FI","FR","HR"
				                  );//国家名称代码
				var str = SourceCode.substring(0,2);//获取1   2位编号
				var b = false;//判断1  2位编号是否合格   true为不合格
				if($.inArray(str, state) == -1){//不包含
					b = true;
				}
				if(str == "NN" || !b || !isNaN(str)){
					return true;
				}
				return false;
			});
			
			//验证编号三四位   出厂年份
			jQuery.validator.addMethod("verifySourceCodeYear", function(value, element) {
				var SourceCode = $.trim($("#ESealedRadioactiveSourceForm").find("#sourceCode").val());
				if(SourceCode == ''){
					return true;
				}
				var time = SourceCode.substring(2,4);//获取年份
				if(/^[0-9N]{2}$/.test(time) ){//不是数字或NN
					return true;
				}
				return false;
			});
			
			//验证编号五六位   核素代码
			jQuery.validator.addMethod("verifySourceCodeNuclide", function(value, element) {	
				var SourceCode = $.trim($("#ESealedRadioactiveSourceForm").find("#sourceCode").val());
				if(SourceCode == ''){
					return true;
				}
				var str = SourceCode.substring(4,6);//获取5   6位编号()核素代码
				if(str == "NN"){
					return true;
				}
				
				var nuclide =  new Array(//现在能用的核素 英文名
						"H3","FE","C7","CO","NI","GE","SE","KR","CD","CS","PM",
						"GD","IR","PO","RA","P8","AM","CM","CF"
					);
					if($.inArray(str, nuclide) == -1){//不包含
						return false;
					}
					var nuclide2 =  new Array(//现在能用的核素 中文名
							"氢-3","铁-55","钴-57","钴-60","镍-63","锗-68","硒-75","氪-85","镉-109","铯-137","钷-147",
							"钆-153","铱-192","钋-210","镭-226","钚-238","镅-241","锔-244","锎-252",
							"磷-32","钼-99","钯-103","碘-125","碘-131","金-198"
						);
					//获取选取的核素名称
					var element = $("#element span").html();
					if($.inArray(element, nuclide2) == -1){//不包含
						return false;
					}
					
					for(i = 0; i < nuclide.length; i++){//看元素是否对应  代码  和名字
						if(element == nuclide2[i]){
							if(str != nuclide[i]){//编号5   6位是否和核素英文代码相同
								return false;
							}
							break;
						}
					}
					
				if( /^[A-Z0-9\d]{2}$/.test(str) ){
					return true;
				}
				return false;
			});
			
			//验证编号是否正确
			jQuery.validator.addMethod("verifySourceCode", function(value, element) {
				var SourceCode = $.trim($("#ESealedRadioactiveSourceForm").find("#sourceCode").val());
				if(SourceCode == ''){
					return true;
				}
				if( /^[A-Z\d]{2}[0-9N]{2}[A-Z0-9\d]{2}[\d]{5}[1-5N]$/.test(SourceCode) ){
					return true;
				}
				return false;
			});
			
			//所在活度正数不为空时指数 没有值时   指数默认为1
			$("#activity").change(function(){
				var str = $(this).val();
				var activityExponent = $("#activityExponent").val();//指数
				if(str != "" && activityExponent == ""){
					$("#activityExponent").val("0")
				}
				if(str == ""){
					$("#activityExponent").val("")
				}
			});
			//当活度正数有值时候  指数不能为 空
			$("#activityExponent").change(function(){
				var activityExponent = $(this).val();
				var str = $("#activity").val();
				if(str != "" && activityExponent == ""){
					$("#activityExponent").val("0")
				}
			});
			
			$("#ESealedRadioactiveSourceForm").validate({
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
				    sourceCode : {//放射源编号
				    	rangelength : [12,12],
				    	verifySourceCodeUnit : true,
				    	verifySourceCodeYear : true,
				    	verifySourceCodeNuclide : true,
				    	verifySourceCode : 	true
				    },
				    activityExponent : {//活度指数
				    	digits:true,
				    	range:[0,20]
				    },
				    activity : {//活度 正数
					    number : true,
				    	verifyfigure : true
				    }
				},
				messages: {	
				    sourceCode : {//放射源编号
				    	rangelength :$.validator.format("<div class='tisyz'><i class='tisbefore'></i>请输入{0}位的放射源编号</div>"),
				    	verifySourceCodeUnit : "<div class='tisyz'><i class='tisbefore'></i>一二位是生产单位代码</div>",
				    	verifySourceCodeYear : "<div class='tisyz'><i class='tisbefore'></i>三四位是出厂年份后两位或NN</div>",
				    	verifySourceCodeNuclide : "<div class='tisyz'><i class='tisbefore'></i>五六位是核素代码编号或NN</div>",
				    	verifySourceCode : "<div class='tisyz'><i class='tisbefore'></i>放射编号后六位不正确</div>"
				    },
				    activityExponent : {
				    	digits:"<div class='tisyz'><i class='tisbefore'></i>请输入0到20之间的整数</div>",
				    	range:"<div class='tisyz'><i class='tisbefore'></i>请输入0到20之间的整数</div>"
				    },
				    activity : {
					    number : "<div class='tisyz'><i class='tisbefore'></i>请输入正数且4位有效数字</div>",
					    verifyfigure : "<div class='tisyz'><i class='tisbefore'></i>请输入正数且4位有效数字</div>"
				    }
				}
			});
		}
	}
})();


jQuery(document).ready(function() {
	ESealedRadioactiveSourceDetail.init();//初始化
});
//核素生产厂家 单选
function getNuclideManufacturers(){
	Company_Single(function(data){
		$("#nuclideManufacturersList1").html("");//清空数据
		//保存生产厂家的id
		$("#nuclideManufacturers").val($(data).val());
		$("#nuclideManufacturersList1").append(data);
		$("#nuclideManufacturers").focus().blur()//光标切入移除
	},5);//1.个人剂量监测单位；2.职业健康检查单位；3.评价与检测机构； 4.供货商；5.生产厂家；6.设计单位；7.施工单位；
}

//区域单选
function selectArea1() {
	SelectRoom_Single(function(data) {
		$("#areaList1").children().remove();//单选   删除里面多余的数据
		$("#hospitalArea").html("");//清空 所在院区
		//所在机房或区域 id
		$("#areaId").val("");
		if(data.id == undefined || data.id == ""){
			return;
		}
		//本级区域
		$("#areaList1").append(dataNodeSelected(data.id, data.name));
		//生成所在院区
		$("#hospitalArea").html(data.areaName);
		//所在机房或区域 id
		$("#areaId").val(data.id);
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